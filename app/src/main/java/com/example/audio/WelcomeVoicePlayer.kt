package com.example.audio

import android.content.Context
import android.media.MediaPlayer
import com.example.R

/**
 * Controller for playing the AI voice narration on the Welcome screen.
 */
class WelcomeVoicePlayer {
    private var mediaPlayer: MediaPlayer? = null
    var isSpeaking: Boolean = false
        private set

    fun play(context: Context, onComplete: () -> Unit = {}) {
        stop()
        try {
            mediaPlayer = MediaPlayer.create(context.applicationContext, R.raw.welcome_voice)?.apply {
                setOnCompletionListener {
                    isSpeaking = false
                    onComplete()
                }
                start()
            }
            isSpeaking = true
        } catch (_: Exception) {
            isSpeaking = false
        }
    }

    fun stop() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
        } catch (_: Exception) {}
        mediaPlayer = null
        isSpeaking = false
    }

    fun toggle(context: Context, onStateChanged: (Boolean) -> Unit) {
        if (isSpeaking) {
            stop()
            onStateChanged(false)
        } else {
            play(context) {
                onStateChanged(false)
            }
            onStateChanged(true)
        }
    }
}
