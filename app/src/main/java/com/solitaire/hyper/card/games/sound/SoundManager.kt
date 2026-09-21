package com.solitaire.hyper.card.games.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Collections
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * High-performance, zero-latency Sound & Haptics Engine for Solitaire.
 * Uses Android's native SoundPool for instant audio feedback without allocation lag,
 * synthesizing realistic physical card snaps, crystal foundation chimes, victory fanfare,
 * and spinning wheel ratchets.
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

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(10)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val loadedSounds = Collections.synchronizedSet(mutableSetOf<Int>())

    @Volatile private var soundCardMove: Int = 0
    @Volatile private var soundCardFlip: Int = 0
    @Volatile private var soundFoundationSnap: Int = 0
    @Volatile private var soundVictory: Int = 0
    @Volatile private var soundSpinTick: Int = 0
    @Volatile private var soundSpinReward: Int = 0
    @Volatile private var soundInvalidMove: Int = 0

    private val toneGenerator: ToneGenerator? = try {
        ToneGenerator(AudioManager.STREAM_MUSIC, 75)
    } catch (e: Exception) {
        null
    }

    var soundEnabled: Boolean = true
    var vibrationEnabled: Boolean = true

    init {
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) {
                loadedSounds.add(sampleId)
            }
        }
        scope.launch {
            initAudioAssets()
        }
    }

    private fun initAudioAssets() {
        try {
            val cacheDir = context.cacheDir.resolve("solitaire_audio")
            if (!cacheDir.exists()) cacheDir.mkdirs()

            val moveFile = cacheDir.resolve("card_move.wav")
            if (!moveFile.exists() || moveFile.length() < 44) {
                writeWav(moveFile, generateCardMoveSamples())
            }
            soundCardMove = soundPool.load(moveFile.absolutePath, 1)

            val foundationFile = cacheDir.resolve("foundation_snap.wav")
            if (!foundationFile.exists() || foundationFile.length() < 44) {
                writeWav(foundationFile, generateFoundationSnapSamples())
            }
            soundFoundationSnap = soundPool.load(foundationFile.absolutePath, 1)

            val flipFile = cacheDir.resolve("card_flip.wav")
            if (!flipFile.exists() || flipFile.length() < 44) {
                writeWav(flipFile, generateCardFlipSamples())
            }
            soundCardFlip = soundPool.load(flipFile.absolutePath, 1)

            val victoryFile = cacheDir.resolve("victory_fanfare.wav")
            if (!victoryFile.exists() || victoryFile.length() < 44) {
                writeWav(victoryFile, generateVictorySamples())
            }
            soundVictory = soundPool.load(victoryFile.absolutePath, 1)

            val spinTickFile = cacheDir.resolve("spin_tick.wav")
            if (!spinTickFile.exists() || spinTickFile.length() < 44) {
                writeWav(spinTickFile, generateSpinTickSamples())
            }
            soundSpinTick = soundPool.load(spinTickFile.absolutePath, 1)

            val spinRewardFile = cacheDir.resolve("spin_reward.wav")
            if (!spinRewardFile.exists() || spinRewardFile.length() < 44) {
                writeWav(spinRewardFile, generateSpinRewardSamples())
            }
            soundSpinReward = soundPool.load(spinRewardFile.absolutePath, 1)

            val invalidFile = cacheDir.resolve("invalid_move.wav")
            if (!invalidFile.exists() || invalidFile.length() < 44) {
                writeWav(invalidFile, generateInvalidMoveSamples())
            }
            soundInvalidMove = soundPool.load(invalidFile.absolutePath, 1)
        } catch (e: Exception) {
            Log.w("SoundManager", "Error initializing audio assets: ${e.message}")
        }
    }

    private fun writeWav(file: File, samples: ShortArray, sampleRate: Int = 22050) {
        val dataSize = samples.size * 2
        val totalSize = 36 + dataSize
        val buffer = ByteBuffer.allocate(44 + dataSize).order(ByteOrder.LITTLE_ENDIAN)

        buffer.put("RIFF".toByteArray())
        buffer.putInt(totalSize)
        buffer.put("WAVE".toByteArray())

        buffer.put("fmt ".toByteArray())
        buffer.putInt(16) // Subchunk1Size
        buffer.putShort(1) // PCM
        buffer.putShort(1) // Mono
        buffer.putInt(sampleRate)
        buffer.putInt(sampleRate * 2) // ByteRate
        buffer.putShort(2) // BlockAlign
        buffer.putShort(16) // BitsPerSample

        buffer.put("data".toByteArray())
        buffer.putInt(dataSize)

        for (s in samples) {
            buffer.putShort(s)
        }

        FileOutputStream(file).use { fos ->
            fos.write(buffer.array())
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
        playOrFallback(soundCardMove, ToneGenerator.TONE_PROP_BEEP, 30, volume = 0.85f)
        triggerHaptic(durationMs = 12, amplitude = 35)
    }

    fun playFoundationSnap() {
        if (!soundEnabled) return
        playOrFallback(soundFoundationSnap, ToneGenerator.TONE_PROP_ACK, 70, volume = 0.95f)
        triggerHaptic(durationMs = 25, amplitude = 70)
    }

    fun playCardFlip() {
        if (!soundEnabled) return
        playOrFallback(soundCardFlip, ToneGenerator.TONE_PROP_BEEP, 25, volume = 0.75f)
        triggerHaptic(durationMs = 15, amplitude = 40)
    }

    fun playVictory() {
        if (!soundEnabled) return
        playOrFallback(soundVictory, ToneGenerator.TONE_PROP_PROMPT, 500, volume = 1.0f)
        triggerHaptic(durationMs = 80, amplitude = 120)
    }

    fun playSpinTick() {
        if (!soundEnabled) return
        val randomRate = (0.95f + (Math.random() * 0.1f).toFloat())
        playOrFallback(soundSpinTick, ToneGenerator.TONE_PROP_BEEP, 20, volume = 0.7f, rate = randomRate)
        triggerHaptic(durationMs = 8, amplitude = 25)
    }

    fun playSpinReward() {
        if (!soundEnabled) return
        playOrFallback(soundSpinReward, ToneGenerator.TONE_PROP_ACK, 250, volume = 1.0f)
        triggerHaptic(durationMs = 60, amplitude = 100)
    }

    fun playInvalidMove() {
        if (!soundEnabled) return
        playOrFallback(soundInvalidMove, ToneGenerator.TONE_PROP_NACK, 50, volume = 0.6f)
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

    private fun playOrFallback(soundId: Int, fallbackTone: Int, toneDurationMs: Int, volume: Float = 1.0f, rate: Float = 1.0f) {
        if (!soundEnabled) return
        var played = false
        if (soundId != 0 && loadedSounds.contains(soundId)) {
            try {
                val streamId = soundPool.play(soundId, volume, volume, 1, 0, rate)
                played = (streamId != 0)
            } catch (e: Exception) {
                played = false
            }
        }
        if (!played) {
            try {
                toneGenerator?.startTone(fallbackTone, toneDurationMs)
            } catch (e: Exception) {
                // Graceful
            }
        }
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
        } catch (e: Exception) {
            Log.w("SoundManager", "Haptic feedback unavailable: ${e.message}")
        }
    }
}
