package com.solitaire.hyper.card.games.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * High-performance, zero-codec, zero-tone audio & haptics engine for Solitaire King.
 * Uses gentle in-memory PCM synthesis directly to AudioTrack (MODE_STATIC).
 * NEVER plays unrequested background tones, phone beeps, or auto-start sounds on launch.
 */
class SoundManager(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Default)

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        manager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    @Volatile private var trackCardMove: AudioTrack? = null
    @Volatile private var trackFoundationSnap: AudioTrack? = null
    @Volatile private var trackCardFlip: AudioTrack? = null
    @Volatile private var trackVictory: AudioTrack? = null
    @Volatile private var trackSpinTick: AudioTrack? = null
    @Volatile private var trackSpinReward: AudioTrack? = null
    @Volatile private var trackInvalidMove: AudioTrack? = null
    @Volatile private var trackCardShuffle: AudioTrack? = null
    @Volatile private var trackCardDeal: AudioTrack? = null

    var soundEnabled: Boolean = true
    var vibrationEnabled: Boolean = true

    init {
        // Pre-warm static audio buffers in background thread without emitting any audible sound
        scope.launch {
            initAudioTracks()
        }
    }

    private fun initAudioTracks() {
        try {
            val sampleRate = 22050
            trackCardMove = createStaticTrack(generateCardMoveSamples(sampleRate), sampleRate)
            trackFoundationSnap = createStaticTrack(generateFoundationSnapSamples(sampleRate), sampleRate)
            trackCardFlip = createStaticTrack(generateCardFlipSamples(sampleRate), sampleRate)
            trackVictory = createStaticTrack(generateVictorySamples(sampleRate), sampleRate)
            trackSpinTick = createStaticTrack(generateSpinTickSamples(sampleRate), sampleRate)
            trackSpinReward = createStaticTrack(generateSpinRewardSamples(sampleRate), sampleRate)
            trackInvalidMove = createStaticTrack(generateInvalidMoveSamples(sampleRate), sampleRate)
            trackCardShuffle = createStaticTrack(generateShuffleRiffleSamples(sampleRate), sampleRate)
            trackCardDeal = createStaticTrack(generateCardDealSamples(sampleRate), sampleRate)
        } catch (e: Exception) {
            Log.w("SoundManager", "AudioTrack init note: ${e.message}")
        }
    }

    private fun createStaticTrack(samples: ShortArray, sampleRate: Int): AudioTrack? {
        return try {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val audioFormat = AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()

            val track = AudioTrack(
                audioAttributes,
                audioFormat,
                samples.size * 2,
                AudioTrack.MODE_STATIC,
                AudioManager.AUDIO_SESSION_ID_GENERATE
            )
            track.write(samples, 0, samples.size)
            track
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Safely plays a pre-synthesized PCM track.
     * Silent fallback: if track is not ready or sound is muted, no harsh tones or beeps are ever produced.
     */
    private fun playTrack(track: AudioTrack?) {
        if (!soundEnabled || track == null) return
        try {
            if (track.state == AudioTrack.STATE_INITIALIZED) {
                track.stop()
                track.reloadStaticData()
                track.play()
            }
        } catch (_: Exception) {
            // Silently ignore to prevent disruption
        }
    }

    private fun generateCardMoveSamples(sampleRate: Int = 22050): ShortArray {
        val durationMs = 45
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val samples = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val decay = exp(-t / 0.014)
            val freq = 420.0 - 220.0 * (i.toDouble() / numSamples)
            val click = exp(-t / 0.003) * sin(2.0 * PI * 1300.0 * t) * 0.35
            val body = sin(2.0 * PI * freq * t) * 0.55
            val sample = ((body + click) * decay * 22000.0).toInt()
            samples[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return samples
    }

    private fun generateFoundationSnapSamples(sampleRate: Int = 22050): ShortArray {
        val durationMs = 180
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val samples = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val sample = if (t < 0.04) {
                val decay = exp(-t / 0.035)
                (sin(2.0 * PI * 880.0 * t) * decay * 22000.0).toInt()
            } else {
                val t2 = t - 0.04
                val decay = exp(-t2 / 0.07)
                val bell = sin(2.0 * PI * 1318.51 * t2) * 0.6 + sin(2.0 * PI * 2637.02 * t2) * 0.25
                (bell * decay * 23000.0).toInt()
            }
            samples[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return samples
    }

    private fun generateVictorySamples(sampleRate: Int = 22050): ShortArray {
        val durationMs = 900
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val samples = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val sample = when {
                t < 0.18 -> {
                    val decay = exp(-t / 0.14)
                    (sin(2.0 * PI * 523.25 * t) * decay * 18000.0).toInt()
                }
                t < 0.36 -> {
                    val t2 = t - 0.18
                    val decay = exp(-t2 / 0.14)
                    (sin(2.0 * PI * 659.25 * t2) * decay * 18000.0).toInt()
                }
                t < 0.54 -> {
                    val t3 = t - 0.36
                    val decay = exp(-t3 / 0.14)
                    (sin(2.0 * PI * 783.99 * t3) * decay * 20000.0).toInt()
                }
                else -> {
                    val t4 = t - 0.54
                    val decay = exp(-t4 / 0.30)
                    val chord = (sin(2.0 * PI * 523.25 * t4) * 0.28 +
                                 sin(2.0 * PI * 783.99 * t4) * 0.28 +
                                 sin(2.0 * PI * 1046.50 * t4) * 0.35)
                    (chord * decay * 22000.0).toInt()
                }
            }
            samples[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return samples
    }

    private fun generateSpinTickSamples(sampleRate: Int = 22050): ShortArray {
        val durationMs = 24
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val samples = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val decay = exp(-t / 0.006)
            val click = (sin(2.0 * PI * 1750.0 * t) * 0.65 + sin(2.0 * PI * 3200.0 * t) * 0.25)
            val sample = (click * decay * 22000.0).toInt()
            samples[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return samples
    }

    private fun generateSpinRewardSamples(sampleRate: Int = 22050): ShortArray {
        val durationMs = 500
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val samples = ShortArray(numSamples)
        val freqs = doubleArrayOf(587.33, 739.99, 880.0, 1174.66)
        val noteDuration = 0.12
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val noteIdx = (t / noteDuration).toInt().coerceIn(0, freqs.size - 1)
            val noteT = t - (noteIdx * noteDuration)
            val decay = exp(-noteT / 0.10)
            val sample = (sin(2.0 * PI * freqs[noteIdx] * noteT) * decay * 20000.0).toInt()
            samples[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return samples
    }

    private fun generateCardFlipSamples(sampleRate: Int = 22050): ShortArray {
        val durationMs = 35
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val samples = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val decay = exp(-t / 0.010)
            val sample = (sin(2.0 * PI * 720.0 * t) * decay * 20000.0).toInt()
            samples[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return samples
    }

    private fun generateShuffleRiffleSamples(sampleRate: Int = 22050): ShortArray {
        val durationMs = 320
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val samples = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val decay = exp(-t / 0.14)
            val flutter = sin(2.0 * PI * 85.0 * t)
            val cardSnap = sin(2.0 * PI * (440.0 + flutter * 120.0) * t) * 0.5
            val rustle = ((i * 1103515245 + 12345) and 0x7FFF) / 32768.0 - 0.5
            val sample = ((cardSnap + rustle * 0.35) * decay * 18000.0).toInt()
            samples[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return samples
    }

    private fun generateCardDealSamples(sampleRate: Int = 22050): ShortArray {
        val durationMs = 28
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val samples = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val decay = exp(-t / 0.007)
            val sample = (sin(2.0 * PI * 920.0 * t) * decay * 19000.0).toInt()
            samples[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return samples
    }

    private fun generateInvalidMoveSamples(sampleRate: Int = 22050): ShortArray {
        val durationMs = 70
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val samples = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val decay = exp(-t / 0.025)
            val sample = (sin(2.0 * PI * 180.0 * t) * decay * 18000.0).toInt()
            samples[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return samples
    }

    // --- Interactive Game Audio Actions ---

    fun playCardMove() {
        if (!soundEnabled) return
        playTrack(trackCardMove)
        triggerHaptic(durationMs = 12, amplitude = 30)
    }

    fun playFoundationSnap() {
        if (!soundEnabled) return
        playTrack(trackFoundationSnap)
        triggerHaptic(durationMs = 22, amplitude = 70)
    }

    fun playCardFlip() {
        if (!soundEnabled) return
        playTrack(trackCardFlip)
        triggerHaptic(durationMs = 10, amplitude = 25)
    }

    fun playVictory() {
        if (!soundEnabled) return
        playTrack(trackVictory)
        triggerHaptic(durationMs = 70, amplitude = 120)
    }

    fun playSpinTick() {
        if (!soundEnabled) return
        playTrack(trackSpinTick)
        triggerHaptic(durationMs = 6, amplitude = 18)
    }

    fun playSpinReward() {
        if (!soundEnabled) return
        playTrack(trackSpinReward)
        triggerHaptic(durationMs = 45, amplitude = 90)
    }

    fun playInvalidMove() {
        if (!soundEnabled) return
        playTrack(trackInvalidMove)
        triggerHaptic(durationMs = 20, amplitude = 40)
    }

    fun playMagicWand() {
        if (!soundEnabled) return
        playFoundationSnap()
    }

    fun playShuffle() {
        if (!soundEnabled) return
        playTrack(trackCardShuffle)
        triggerHaptic(durationMs = 25, amplitude = 45)
    }

    fun playCardDeal() {
        if (!soundEnabled) return
        playTrack(trackCardDeal)
        triggerHaptic(durationMs = 8, amplitude = 20)
    }

    private fun triggerHaptic(durationMs: Long, amplitude: Int = 50) {
        if (!vibrationEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val clampedAmp = amplitude.coerceIn(1, 255)
                vibrator.vibrate(VibrationEffect.createOneShot(durationMs, clampedAmp))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(durationMs)
            }
        } catch (_: Exception) {
            // Ignored
        }
    }

    fun release() {
        val tracks = listOf(
            trackCardMove, trackFoundationSnap, trackCardFlip, trackVictory,
            trackSpinTick, trackSpinReward, trackInvalidMove, trackCardShuffle, trackCardDeal
        )
        tracks.forEach {
            try {
                it?.stop()
                it?.release()
            } catch (_: Exception) {}
        }
    }
}
