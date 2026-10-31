package com.example.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.VoiceOverOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.WelcomeVoicePlayer
import com.example.components.FloatingHeartsBackground
import com.example.ui.theme.CaveatFontFamily
import com.example.ui.theme.CelestialCyan
import com.example.ui.theme.DancingScriptFontFamily
import com.example.ui.theme.LightPinkPaper
import com.example.ui.theme.NunitoFontFamily
import com.example.ui.theme.RomanticPink
import com.example.ui.theme.RoseGold
import com.example.ui.theme.StarGold
import com.example.ui.theme.WarmRose
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun WelcomeScreen(
    onEnterClicked: () -> Unit,
    isMusicPlaying: Boolean,
    onToggleMusic: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val voicePlayer = remember { WelcomeVoicePlayer() }
    var isVoicePlaying by remember { mutableStateOf(false) }

    // Auto-play the AI voice narration when she opens the app
    LaunchedEffect(Unit) {
        delay(400)
        voicePlayer.play(context) {
            isVoicePlaying = false
        }
        isVoicePlaying = voicePlayer.isSpeaking
    }

    DisposableEffect(Unit) {
        onDispose {
            voicePlayer.stop()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "welcome_animations")
    val buttonScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "btn_pulse"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Box(modifier = modifier.fillMaxSize()) {
        FloatingHeartsBackground()

        // Top control buttons (AI Voice mute/unmute + music toggle)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 18.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Voice of AI button to mute or play the narration
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isVoicePlaying) StarGold else RoseGold.copy(alpha = 0.6f)
                ),
                modifier = Modifier.clickable {
                    voicePlayer.toggle(context) { playing ->
                        isVoicePlaying = playing
                    }
                }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isVoicePlaying) Icons.Default.RecordVoiceOver else Icons.Default.VoiceOverOff,
                        contentDescription = "Voz de IA",
                        tint = if (isVoicePlaying) StarGold else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isVoicePlaying) "Voz IA: Hablando" else "Escuchar Voz IA",
                        fontFamily = NunitoFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isVoicePlaying) StarGold else LightPinkPaper
                    )
                }
            }

            // Music note toggle
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f),
                border = androidx.compose.foundation.BorderStroke(1.dp, RoseGold.copy(alpha = 0.5f))
            ) {
                IconButton(
                    onClick = onToggleMusic,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("music_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isMusicPlaying) Icons.Default.MusicNote else Icons.Default.MusicOff,
                        contentDescription = if (isMusicPlaying) "Silenciar música" else "Reproducir música",
                        tint = if (isMusicPlaying) StarGold else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Center content inside scrollable column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Romantic Card container
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 500.dp)
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(28.dp),
                        spotColor = RomanticPink.copy(alpha = glowAlpha),
                        ambientColor = RomanticPink.copy(alpha = glowAlpha)
                    )
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            listOf(
                                RomanticPink.copy(alpha = 0.8f),
                                RoseGold.copy(alpha = 0.4f),
                                StarGold.copy(alpha = 0.7f)
                            )
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 26.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // As requested: The GIF from imgur placed right at the beginning!
                    WelcomeGifPlayer(
                        modifier = Modifier
                            .size(width = 175.dp, height = 140.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, StarGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Title
                    Text(
                        text = "Bienvenida Hill",
                        fontFamily = DancingScriptFontFamily,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // The exact required message:
                    Text(
                        text = "Bienvenida Hill, tengo algo muy especial que decirte hoy, no se si sea necesario, pero bueno dale a ese botón de ahi abajo",
                        fontFamily = CaveatFontFamily,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = LightPinkPaper,
                        textAlign = TextAlign.Center,
                        lineHeight = 30.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Audio reading indicator banner
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isVoicePlaying) RomanticPink.copy(alpha = 0.25f) else Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isVoicePlaying) RomanticPink.copy(alpha = 0.6f) else Color.Transparent
                        )
                    ) {
                        Text(
                            text = if (isVoicePlaying) "🎙️ Narrando mensaje para ti..." else "🔊 Toca 'Escuchar Voz IA' para oírlo",
                            fontFamily = NunitoFontFamily,
                            fontSize = 11.sp,
                            color = if (isVoicePlaying) StarGold else RoseGold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // The main "Entra aqui" button
            Button(
                onClick = {
                    voicePlayer.stop()
                    onEnterClicked()
                },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .widthIn(max = 360.dp)
                    .height(58.dp)
                    .scale(buttonScale)
                    .shadow(16.dp, RoundedCornerShape(30.dp), spotColor = RomanticPink)
                    .testTag("enter_button"),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(RomanticPink, WarmRose, StarGold)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Entra aqui",
                        fontFamily = NunitoFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

/**
 * Animated GIF player displaying the 20 extracted frames of the user's imgur GIF.
 */
@Composable
fun WelcomeGifPlayer(modifier: Modifier = Modifier) {
    val frames = remember {
        listOf(
            R.drawable.welcome_anim_00,
            R.drawable.welcome_anim_01,
            R.drawable.welcome_anim_02,
            R.drawable.welcome_anim_03,
            R.drawable.welcome_anim_04,
            R.drawable.welcome_anim_05,
            R.drawable.welcome_anim_06,
            R.drawable.welcome_anim_07,
            R.drawable.welcome_anim_08,
            R.drawable.welcome_anim_09,
            R.drawable.welcome_anim_10,
            R.drawable.welcome_anim_11,
            R.drawable.welcome_anim_12,
            R.drawable.welcome_anim_13,
            R.drawable.welcome_anim_14,
            R.drawable.welcome_anim_15,
            R.drawable.welcome_anim_16,
            R.drawable.welcome_anim_17,
            R.drawable.welcome_anim_18,
            R.drawable.welcome_anim_19
        )
    }

    var currentFrameIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (isActive) {
            delay(90) // Frame rate ~11 fps for GIF animation
            currentFrameIndex = (currentFrameIndex + 1) % frames.size
        }
    }

    Box(
        modifier = modifier.background(Color.Black.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = frames[currentFrameIndex]),
            contentDescription = "Animación de bienvenida",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
