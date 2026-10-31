package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Procedural romantic music box synthesizer using Android AudioTrack.
 * Plays a delicate, soothing romantic melody without requiring external audio files.
 */
class RomanticAudioPlayer {
    private var audioTrack: AudioTrack? = null
    private var playbackJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    var isPlaying: Boolean = false
        private set

    // Romantic music-box note frequencies in Hz (Canon in D / Romantic cadence)
    private val melodyNotes = listOf(
        587.33, 739.99, 880.00, 1174.66, // D5, F#5, A5, D6
        554.37, 739.99, 880.00, 1108.73, // C#5, F#5, A5, C#6
        493.88, 587.33, 739.99, 987.77,  // B4, D5, F#5, B5
        440.00, 554.37, 659.25, 880.00,  // A4, C#5, E5, A5
        392.00, 493.88, 587.33, 783.99,  // G4, B4, D5, G5
        369.99, 440.00, 587.33, 739.99,  // F#4, A4, D5, F#5
        392.00, 493.88, 587.33, 783.99,  // G4, B4, D5, G5
        440.00, 554.37, 659.25, 880.00   // A4, C#5, E5, A5
    )

    fun start() {
        if (isPlaying) return
        isPlaying = true

        playbackJob = scope.launch {
            val sampleRate = 44100
            val bufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(sampleRate / 4)

            try {
                audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack?.play()

                var noteIndex = 0
                val noteDurationSec = 0.52
                val noteSamples = (sampleRate * noteDurationSec).toInt()
                val buffer = ShortArray(noteSamples)

                while (isActive && isPlaying) {
                    val freq = melodyNotes[noteIndex % melodyNotes.size]
                    noteIndex++

                    // Generate a bell-like chime with harmonics and exponential decay
                    for (i in 0 until noteSamples) {
                        val t = i.toDouble() / sampleRate
                        val envelope = exp(-4.2 * t) // Soft chime envelope
                        // Fundamental + gentle warm octaves
                        val s1 = sin(2.0 * PI * freq * t)
                        val s2 = 0.45 * sin(2.0 * PI * (freq * 2.0) * t)
                        val s3 = 0.20 * sin(2.0 * PI * (freq * 3.0) * t)
                        val sampleVal = (s1 + s2 + s3) * envelope * 0.28
                        val clamped = (sampleVal * Short.MAX_VALUE).toInt().coerceIn(
                            Short.MIN_VALUE.toInt(),
                            Short.MAX_VALUE.toInt()
                        )
                        buffer[i] = clamped.toShort()
                    }

                    audioTrack?.write(buffer, 0, noteSamples)
                }
            } catch (_: Exception) {
                // AudioTrack fallback
            } finally {
                stopInternal()
            }
        }
    }

    fun stop() {
        isPlaying = false
        playbackJob?.cancel()
        playbackJob = null
        stopInternal()
    }

    private fun stopInternal() {
        try {
            audioTrack?.apply {
                if (playState == AudioTrack.PLAYSTATE_PLAYING) {
                    stop()
                }
                release()
            }
        } catch (_: Exception) {}
        audioTrack = null
    }
}
