package com.example.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.WarpTunnelCanvas
import com.example.components.drawHeartWithArrow
import com.example.ui.theme.CelestialCyan
import com.example.ui.theme.DancingScriptFontFamily
import com.example.ui.theme.NunitoFontFamily
import com.example.ui.theme.RomanticPink
import com.example.ui.theme.RoseGold
import com.example.ui.theme.SoftBlush
import com.example.ui.theme.StarGold
import com.example.ui.theme.WarmRose
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// Heart with arrow particle for explosion
private data class ExplosionHeart(
    val angle: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val rotation: Float
)

@Composable
fun WarpTunnelScreen(
    onStartTunnelMusic: () -> Unit,
    onUpdateMusicVolume: (Float) -> Unit,
    onTunnelFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val heartExplosionProgress = remember { Animatable(0f) }
    val tunnelWarpProgress = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }

    val explosionHearts = remember {
        val rand = Random(777)
        val colors = listOf(RomanticPink, SoftBlush, WarmRose, StarGold, Color.White, CelestialCyan)
        List(85) {
            ExplosionHeart(
                angle = rand.nextFloat() * (2f * PI.toFloat()),
                speed = 0.5f + rand.nextFloat() * 1.5f,
                size = 18f + rand.nextFloat() * 30f,
                color = colors[rand.nextInt(colors.size)],
                rotation = rand.nextFloat() * 360f
            )
        }
    }

    LaunchedEffect(Unit) {
        // Start the song softly right at the start of the tunnel!
        onStartTunnelMusic()
        onUpdateMusicVolume(0.15f)

        // Step 1: Heart explosion
        textAlpha.animateTo(1f, animationSpec = tween(500))
        heartExplosionProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1800, easing = FastOutSlowInEasing)
        )

        // Step 2: Slow warp tunnel through the cosmos with volume ramp up to 1.0f
        launch {
            // Monitor volume ramp during the tunnel
            while (tunnelWarpProgress.value < 0.99f) {
                val vol = 0.15f + (tunnelWarpProgress.value * 0.85f)
                onUpdateMusicVolume(vol)
                delay(60)
            }
            onUpdateMusicVolume(1.0f)
        }

        tunnelWarpProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 3800, easing = LinearEasing)
        )

        onUpdateMusicVolume(1.0f)
        delay(250)
        onTunnelFinished()
    }

    val isWarpActive = tunnelWarpProgress.value > 0.05f

    Box(modifier = modifier.fillMaxSize()) {
        if (isWarpActive) {
            // Cosmic Hyperspace Warp Tunnel
            WarpTunnelCanvas(
                progress = tunnelWarpProgress.value,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Hearts with Arrow (💘) Explosion Canvas
        if (heartExplosionProgress.value < 0.98f || !isWarpActive) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val maxRadius = kotlin.math.hypot(cx, cy)
                val p = heartExplosionProgress.value

                explosionHearts.forEach { heart ->
                    val dist = p * maxRadius * heart.speed * 0.95f
                    val x = cx + cos(heart.angle) * dist
                    val y = cy + sin(heart.angle) * dist
                    val alpha = (1f - (p * 0.85f)).coerceIn(0f, 1f)

                    drawHeartWithArrow(
                        center = Offset(x, y),
                        size = heart.size * (1f + p * 0.6f),
                        color = heart.color.copy(alpha = alpha)
                    )
                }
            }
        }

        // Atmospheric romantic text
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (!isWarpActive) "¡Dijiste que SÍ! 💘" else "Entrando a nuestra galaxia 3D... 💘",
                fontFamily = DancingScriptFontFamily,
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = textAlpha.value),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (!isWarpActive) "Mi corazón late a mil por hora 💘" else "Sube el volumen y siente nuestro amor ✨",
                fontFamily = NunitoFontFamily,
                fontSize = 16.sp,
                color = RoseGold.copy(alpha = textAlpha.value),
                textAlign = TextAlign.Center
            )
        }
    }
}
