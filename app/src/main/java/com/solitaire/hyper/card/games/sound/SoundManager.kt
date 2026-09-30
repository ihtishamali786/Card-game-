package com.solitaire.hyper.card.games.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.ToneGenerator
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
 * High-performance, zero-codec audio and haptics engine for Solitaire.
 * Uses direct in-memory PCM AudioTrack (MODE_STATIC) with ToneGenerator fallback.
 * Bypasses MediaCodec and Codec2 bufferpools to eliminate system resource errors on emulators.
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

    private val toneGenerator: ToneGenerator? = try {
        ToneGenerator(AudioManager.STREAM_MUSIC, 75)
    } catch (_: Exception) {
        null
    }

    @Volatile private var trackCardMove: AudioTrack? = null
    @Volatile private var trackFoundationSnap: AudioTrack? = null
    @Volatile private var trackCardFlip: AudioTrack? = null
    @Volatile private var trackVictory: AudioTrack? = null
    @Volatile private var trackSpinTick: AudioTrack? = null
    @Volatile private var trackSpinReward: AudioTrack? = null
    @Volatile private var trackInvalidMove: AudioTrack? = null

    var soundEnabled: Boolean = true
    var vibrationEnabled: Boolean = true

    init {
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
        } catch (e: Exception) {
            Log.w("SoundManager", "Direct AudioTrack init fallback: ${e.message}")
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
        } catch (e: Exception) {
            null
        }
    }

    private fun playTrackOrTone(track: AudioTrack?, fallbackTone: Int, toneDurationMs: Int) {
        if (!soundEnabled) return
        var played = false
        if (track != null) {
            try {
                if (track.state == AudioTrack.STATE_INITIALIZED) {
                    track.stop()
                    track.reloadStaticData()
                    track.play()
                    played = true
                }
            } catch (_: Exception) {
                played = false
            }
        }
        if (!played) {
            try {
                toneGenerator?.startTone(fallbackTone, toneDurationMs)
            } catch (_: Exception) {
                // Gracefully handled
            }
        }
    }

    private fun generateCardMoveSamples(sampleRate: Int = 22050): ShortArray {
        val durationMs = 60
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val samples = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val decay = exp(-t / 0.016)
            val freq = 420.0 - 220.0 * (i.toDouble() / numSamples)
            val click = exp(-t / 0.003) * sin(2.0 * PI * 1300.0 * t) * 0.4
            val body = sin(2.0 * PI * freq * t) * 0.65
            val sample = ((body + click) * decay * 30000.0).toInt()
            samples[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return samples
    }

    private fun generateFoundationSnapSamples(sampleRate: Int = 22050): ShortArray {
        val durationMs = 240
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val samples = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val sample = if (t < 0.06) {
                val decay = exp(-t / 0.05)
                (sin(2.0 * PI * 880.0 * t) * decay * 28000.0).toInt()
            } else {
                val t2 = t - 0.06
                val decay = exp(-t2 / 0.09)
                val bell = sin(2.0 * PI * 1318.51 * t2) * 0.7 + sin(2.0 * PI * 2637.02 * t2) * 0.3
                (bell * decay * 29000.0).toInt()
            }
            samples[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return samples
    }

    private fun generateVictorySamples(sampleRate: Int = 22050): ShortArray {
        val durationMs = 1200
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val samples = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val sample = when {
                t < 0.20 -> {
                    val decay = exp(-t / 0.16)
                    (sin(2.0 * PI * 523.25 * t) * decay * 22000.0).toInt()
                }
                t < 0.40 -> {
                    val t2 = t - 0.20
                    val decay = exp(-t2 / 0.16)
                    (sin(2.0 * PI * 659.25 * t2) * decay * 22000.0).toInt()
                }
                t < 0.60 -> {
                    val t3 = t - 0.40
                    val decay = exp(-t3 / 0.16)
                    (sin(2.0 * PI * 783.99 * t3) * decay * 24000.0).toInt()
                }
                else -> {
                    val t4 = t - 0.60
                    val decay = exp(-t4 / 0.38)
                    val chord = (sin(2.0 * PI * 523.25 * t4) * 0.3 +
                                 sin(2.0 * PI * 783.99 * t4) * 0.3 +
                                 sin(2.0 * PI * 1046.50 * t4) * 0.4)
                    (chord * decay * 29000.0).toInt()
                }
            }
            samples[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return samples
    }

    private fun generateSpinTickSamples(sampleRate: Int = 22050): ShortArray {
        val durationMs = 28
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val samples = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val decay = exp(-t / 0.007)
            val click = (sin(2.0 * PI * 1750.0 * t) * 0.7 + sin(2.0 * PI * 3200.0 * t) * 0.3)
            val sample = (click * decay * 28000.0).toInt()
            samples[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return samples
    }

    private fun generateSpinRewardSamples(sampleRate: Int = 22050): ShortArray {
        val durationMs = 700
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val samples = ShortArray(numSamples)
        val freqs = doubleArrayOf(587.33, 739.99, 880.0, 1174.66)
        val noteDuration = 0.14
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val noteIdx = (t / noteDuration).toInt().coerceIn(0, freqs.size - 1)
            val noteT = t - (noteIdx * noteDuration)
            val decay = exp(-noteT / 0.12)
            val sample = (sin(2.0 * PI * freqs[noteIdx] * noteT) * decay * 26000.0).toInt()
            samples[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return samples
    }

    private fun generateCardFlipSamples(sampleRate: Int = 22050): ShortArray {
        val durationMs = 40
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val samples = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val decay = exp(-t / 0.012)
            val sample = (sin(2.0 * PI * 720.0 * t) * decay * 26000.0).toInt()
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
            val sample = (sin(2.0 * PI * 160.0 * t) * decay * 22000.0).toInt()
            samples[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return samples
    }

    fun playCardMove() {
        if (!soundEnabled) return
        playTrackOrTone(trackCardMove, ToneGenerator.TONE_PROP_BEEP, 30)
        triggerHaptic(durationMs = 12, amplitude = 35)
    }

    fun playFoundationSnap() {
        if (!soundEnabled) return
        playTrackOrTone(trackFoundationSnap, ToneGenerator.TONE_PROP_ACK, 70)
        triggerHaptic(durationMs = 25, amplitude = 70)
    }

    fun playCardFlip() {
        if (!soundEnabled) return
        playTrackOrTone(trackCardFlip, ToneGenerator.TONE_PROP_BEEP, 25)
        triggerHaptic(durationMs = 15, amplitude = 40)
    }

    fun playVictory() {
        if (!soundEnabled) return
        playTrackOrTone(trackVictory, ToneGenerator.TONE_PROP_PROMPT, 500)
        triggerHaptic(durationMs = 80, amplitude = 120)
    }

    fun playSpinTick() {
        if (!soundEnabled) return
        playTrackOrTone(trackSpinTick, ToneGenerator.TONE_PROP_BEEP, 20)
        triggerHaptic(durationMs = 8, amplitude = 25)
    }

    fun playSpinReward() {
        if (!soundEnabled) return
        playTrackOrTone(trackSpinReward, ToneGenerator.TONE_PROP_ACK, 250)
        triggerHaptic(durationMs = 60, amplitude = 100)
    }

    fun playInvalidMove() {
        if (!soundEnabled) return
        playTrackOrTone(trackInvalidMove, ToneGenerator.TONE_PROP_NACK, 50)
        triggerHaptic(durationMs = 30, amplitude = 50)
    }

    fun playMagicWand() {
        if (!soundEnabled) return
        playFoundationSnap()
    }

    fun playShuffle() {
        if (!soundEnabled) return
        playCardFlip()
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
}
