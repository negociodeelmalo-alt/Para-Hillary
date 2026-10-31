package com.example.audio

import android.content.Context
import android.media.MediaPlayer
import com.example.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Audio controller to play "Todo a su tiempo" (Neton Vega)
 * precisely from 2:23 (143,500 ms) to 3:00 (180,000 ms) in a continuous loop,
 * with volume fade-in capability starting from the tunnel.
 */
class ProposalAudioController {
    private var mediaPlayer: MediaPlayer? = null
    private var loopMonitorJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    var isPlaying: Boolean = false
        private set

    companion object {
        // 2:23 in milliseconds = (2 * 60 + 23.5) * 1000 = 143500 ms
        const val START_LOOP_MS = 143_500
        // 3:00 in milliseconds = (3 * 60) * 1000 = 180000 ms
        const val END_LOOP_MS = 180_000
    }

    fun startLoveSong(context: Context, initialVolume: Float = 0.2f) {
        if (mediaPlayer != null && isPlaying) {
            setVolume(initialVolume)
            return
        }

        stop()

        try {
            mediaPlayer = MediaPlayer.create(context.applicationContext, R.raw.love_song)?.apply {
                isLooping = false
                setVolume(initialVolume, initialVolume)
                seekTo(START_LOOP_MS)
                start()
            }
            isPlaying = true

            // Monitor position to enforce exact loop from 2:23 to 3:00
            loopMonitorJob = scope.launch {
                while (isActive && isPlaying) {
                    try {
                        mediaPlayer?.let { player ->
                            if (player.isPlaying) {
                                val currentPos = player.currentPosition
                                if (currentPos >= END_LOOP_MS || currentPos < START_LOOP_MS - 2000) {
                                    player.seekTo(START_LOOP_MS)
                                }
                            }
                        }
                    } catch (_: Exception) {}
                    delay(100)
                }
            }
        } catch (_: Exception) {
            isPlaying = false
        }
    }

    fun setVolume(volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        try {
            mediaPlayer?.setVolume(clamped, clamped)
        } catch (_: Exception) {}
    }

    fun togglePlayback() {
        mediaPlayer?.let { player ->
            try {
                if (player.isPlaying) {
                    player.pause()
                    isPlaying = false
                } else {
                    player.start()
                    isPlaying = true
                }
            } catch (_: Exception) {}
        }
    }

    fun stop() {
        isPlaying = false
        loopMonitorJob?.cancel()
        loopMonitorJob = null
        try {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
        } catch (_: Exception) {}
        mediaPlayer = null
    }
}
