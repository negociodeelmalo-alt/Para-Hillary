package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.audio.ProposalAudioController
import com.example.audio.RomanticAudioPlayer
import com.example.screens.Galaxy3DScreen
import com.example.screens.SurveyScreen
import com.example.screens.WarpTunnelScreen
import com.example.screens.WelcomeScreen
import com.example.ui.theme.DeepSpace
import com.example.ui.theme.MyApplicationTheme

enum class ProposalScreenState {
    WELCOME,
    SURVEY,
    WARP_TUNNEL,
    GALAXY_GALLERY
}

class MainActivity : ComponentActivity() {
    private val introAudioPlayer = RomanticAudioPlayer()
    private val loveSongController = ProposalAudioController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                var currentScreen by remember { mutableStateOf(ProposalScreenState.WELCOME) }
                var isIntroMusicPlaying by remember { mutableStateOf(false) }
                var isSongPlaying by remember { mutableStateOf(false) }

                fun toggleIntroMusic() {
                    if (introAudioPlayer.isPlaying) {
                        introAudioPlayer.stop()
                        isIntroMusicPlaying = false
                    } else {
                        introAudioPlayer.start()
                        isIntroMusicPlaying = true
                    }
                }

                fun toggleSongMusic() {
                    loveSongController.togglePlayback()
                    isSongPlaying = loveSongController.isPlaying
                }

                DisposableEffect(Unit) {
                    onDispose {
                        introAudioPlayer.stop()
                        loveSongController.stop()
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = DeepSpace
                ) { _ ->
                    // System Back navigation handling
                    BackHandler(enabled = currentScreen != ProposalScreenState.WELCOME) {
                        when (currentScreen) {
                            ProposalScreenState.SURVEY -> currentScreen = ProposalScreenState.WELCOME
                            ProposalScreenState.WARP_TUNNEL -> {}
                            ProposalScreenState.GALAXY_GALLERY -> {
                                loveSongController.stop()
                                isSongPlaying = false
                                currentScreen = ProposalScreenState.WELCOME
                            }
                            ProposalScreenState.WELCOME -> {}
                        }
                    }

                    Crossfade(
                        targetState = currentScreen,
                        animationSpec = tween(700),
                        label = "screen_crossfade"
                    ) { screen ->
                        when (screen) {
                            ProposalScreenState.WELCOME -> {
                                WelcomeScreen(
                                    onEnterClicked = {
                                        if (!introAudioPlayer.isPlaying) {
                                            introAudioPlayer.start()
                                            isIntroMusicPlaying = true
                                        }
                                        currentScreen = ProposalScreenState.SURVEY
                                    },
                                    isMusicPlaying = isIntroMusicPlaying,
                                    onToggleMusic = { toggleIntroMusic() }
                                )
                            }

                            ProposalScreenState.SURVEY -> {
                                SurveyScreen(
                                    onYesProposalChosen = {
                                        // Stop soft intro music before entering tunnel
                                        introAudioPlayer.stop()
                                        isIntroMusicPlaying = false
                                        currentScreen = ProposalScreenState.WARP_TUNNEL
                                    },
                                    onBackToHome = {
                                        currentScreen = ProposalScreenState.WELCOME
                                    }
                                )
                            }

                            ProposalScreenState.WARP_TUNNEL -> {
                                WarpTunnelScreen(
                                    onStartTunnelMusic = {
                                        // Start song softly from minute 2:23 right in the tunnel!
                                        loveSongController.startLoveSong(context, initialVolume = 0.15f)
                                        isSongPlaying = true
                                    },
                                    onUpdateMusicVolume = { vol ->
                                        // Ramp up volume during tunnel travel
                                        loveSongController.setVolume(vol)
                                    },
                                    onTunnelFinished = {
                                        // Arrive at the 3D Galaxy with music at full volume (looping 2:23 - 3:00)
                                        loveSongController.setVolume(1.0f)
                                        isSongPlaying = true
                                        currentScreen = ProposalScreenState.GALAXY_GALLERY
                                    }
                                )
                            }

                            ProposalScreenState.GALAXY_GALLERY -> {
                                Galaxy3DScreen(
                                    isMusicPlaying = isSongPlaying,
                                    onToggleMusic = { toggleSongMusic() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        introAudioPlayer.stop()
        loveSongController.stop()
    }
}
