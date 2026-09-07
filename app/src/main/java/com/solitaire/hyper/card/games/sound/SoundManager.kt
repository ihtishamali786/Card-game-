package com.solitaire.hyper.card.games.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * Self-contained audio synthesizer and tactile haptics engine.
 * Generates crisp card physics audio in real-time with zero external audio assets.
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

    var soundEnabled: Boolean = true
    var vibrationEnabled: Boolean = true

    fun playCardFlip() {
        if (!soundEnabled) return
        scope.launch {
            synthesizeTone(frequency = 780.0, durationMs = 35, volume = 0.4)
        }
        triggerHaptic(durationMs = 15, amplitude = 40)
    }

    fun playCardMove() {
        if (!soundEnabled) return
        scope.launch {
            synthesizeTone(frequency = 520.0, durationMs = 50, volume = 0.35)
        }
        triggerHaptic(durationMs = 12, amplitude = 30)
    }

    fun playFoundationSnap() {
        if (!soundEnabled) return
        scope.launch {
            synthesizeArpeggio(frequencies = doubleArrayOf(880.0, 1174.66), durationMs = 70, volume = 0.5)
        }
        triggerHaptic(durationMs = 25, amplitude = 70)
    }

    fun playVictory() {
        if (!soundEnabled) return
        scope.launch {
            // Ascending celebratory fanfare: C5, E5, G5, C6
            val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50)
            synthesizeArpeggio(frequencies = notes, durationMs = 120, volume = 0.6)
        }
        triggerHaptic(durationMs = 80, amplitude = 120)
    }

    fun playInvalidMove() {
        if (!soundEnabled) return
        scope.launch {
            synthesizeTone(frequency = 180.0, durationMs = 60, volume = 0.25)
        }
        triggerHaptic(durationMs = 30, amplitude = 50)
    }

    fun playMagicWand() {
        if (!soundEnabled) return
        scope.launch {
            // Magical shimmering chime: G5, B5, D6, G6
            val chimeNotes = doubleArrayOf(783.99, 987.77, 1174.66, 1567.98)
            synthesizeArpeggio(frequencies = chimeNotes, durationMs = 90, volume = 0.55)
        }
        triggerHaptic(durationMs = 45, amplitude = 90)
    }

    fun playShuffle() {
        if (!soundEnabled) return
        scope.launch {
            synthesizeTone(frequency = 440.0, durationMs = 40, volume = 0.3)
            synthesizeTone(frequency = 660.0, durationMs = 40, volume = 0.35)
        }
        triggerHaptic(durationMs = 25, amplitude = 40)
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

    private fun synthesizeTone(frequency: Double, durationMs: Int, volume: Double) {
        try {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val time = i.toDouble() / sampleRate
                // Exponential decay envelope for snappy acoustic card feel
                val decay = 1.0 - (i.toDouble() / numSamples)
                val sample = (sin(2.0 * Math.PI * frequency * time) * decay * volume * Short.MAX_VALUE).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            Thread.sleep(durationMs.toLong() + 20)
            audioTrack.stop()
            audioTrack.release()
        } catch (e: Exception) {
            // Audio output gracefully ignored if system audio server is busy
        }
    }

    private fun synthesizeArpeggio(frequencies: DoubleArray, durationMs: Int, volume: Double) {
        try {
            val sampleRate = 22050
            val samplesPerNote = (sampleRate * (durationMs / 1000.0) / frequencies.size).toInt()
            val totalSamples = samplesPerNote * frequencies.size
            val buffer = ShortArray(totalSamples)

            var offset = 0
            for (freq in frequencies) {
                for (i in 0 until samplesPerNote) {
                    val time = i.toDouble() / sampleRate
                    val decay = 1.0 - (i.toDouble() / samplesPerNote)
                    val sample = (sin(2.0 * Math.PI * freq * time) * decay * volume * Short.MAX_VALUE).toInt()
                    buffer[offset + i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }
                offset += samplesPerNote
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            Thread.sleep(durationMs.toLong() + 30)
            audioTrack.stop()
            audioTrack.release()
        } catch (e: Exception) {
            // Ignore
        }
    }
}
