package com.example.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.ui.theme.CelestialCyan
import com.example.ui.theme.DeepSpace
import com.example.ui.theme.GalaxyViolet
import com.example.ui.theme.MidnightPurple
import com.example.ui.theme.RomanticPink
import com.example.ui.theme.SoftBlush
import com.example.ui.theme.StarGold
import com.example.ui.theme.WarmRose
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// Floating heart particle with arrow (💘)
private data class AmbientArrowHeart(
    val initialX: Float,
    val speed: Float,
    val size: Float,
    val alpha: Float,
    val swingFreq: Float,
    val swingAmp: Float,
    val color: Color
)

@Composable
fun FloatingHeartsBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_hearts")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ambient_hearts_progress"
    )

    val hearts = remember {
        val rand = Random(42)
        val colors = listOf(RomanticPink, SoftBlush, WarmRose, StarGold)
        List(24) {
            AmbientArrowHeart(
                initialX = rand.nextFloat(),
                speed = 0.35f + rand.nextFloat() * 0.65f,
                size = 18f + rand.nextFloat() * 26f,
                alpha = 0.25f + rand.nextFloat() * 0.45f,
                swingFreq = 1.5f + rand.nextFloat() * 2f,
                swingAmp = 25f + rand.nextFloat() * 35f,
                color = colors[rand.nextInt(colors.size)]
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(MidnightPurple, DeepSpace, GalaxyViolet)
            )
        )

        val w = size.width
        val h = size.height

        hearts.forEachIndexed { index, heart ->
            val effectiveProgress = (progress * heart.speed + index * 0.05f) % 1f
            val y = h - (effectiveProgress * (h + 100f))
            val xOffset = sin(effectiveProgress * PI * 2 * heart.swingFreq).toFloat() * heart.swingAmp
            val x = (heart.initialX * w + xOffset).coerceIn(10f, w - 10f)

            drawHeartWithArrow(
                center = Offset(x, y),
                size = heart.size,
                color = heart.color.copy(alpha = heart.alpha)
            )
        }
    }
}

/**
 * Draws a heart pierced by an arrow (representing 💘) on Canvas.
 */
fun DrawScope.drawHeartWithArrow(center: Offset, size: Float, color: Color) {
    val s = size / 2f

    // 1. Draw Heart
    val heartPath = Path().apply {
        moveTo(center.x, center.y + s * 0.6f)
        cubicTo(
            center.x - s * 1.3f, center.y - s * 0.4f,
            center.x - s * 0.9f, center.y - s * 1.2f,
            center.x, center.y - s * 0.4f
        )
        cubicTo(
            center.x + s * 0.9f, center.y - s * 1.2f,
            center.x + s * 1.3f, center.y - s * 0.4f,
            center.x, center.y + s * 0.6f
        )
        close()
    }
    drawPath(path = heartPath, color = color)

    // 2. Draw Arrow passing diagonally through the heart (bottom-left to top-right)
    val arrowLen = s * 1.7f
    val startX = center.x - arrowLen * 0.8f
    val startY = center.y + arrowLen * 0.65f
    val endX = center.x + arrowLen * 0.95f
    val endY = center.y - arrowLen * 0.75f

    // Arrow shaft
    drawLine(
        color = StarGold.copy(alpha = color.alpha),
        start = Offset(startX, startY),
        end = Offset(endX, endY),
        strokeWidth = (size * 0.08f).coerceAtLeast(1.5f),
        cap = StrokeCap.Round
    )

    // Arrow head (top-right)
    val headSize = s * 0.5f
    val headPath = Path().apply {
        moveTo(endX, endY)
        lineTo(endX - headSize, endY + headSize * 0.2f)
        lineTo(endX - headSize * 0.2f, endY + headSize)
        close()
    }
    drawPath(path = headPath, color = StarGold.copy(alpha = color.alpha))

    // Arrow fletching/feathers (bottom-left)
    val featherLen = s * 0.35f
    drawLine(
        color = StarGold.copy(alpha = color.alpha),
        start = Offset(startX, startY),
        end = Offset(startX - featherLen * 0.5f, startY - featherLen),
        strokeWidth = 1.5f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = StarGold.copy(alpha = color.alpha),
        start = Offset(startX, startY),
        end = Offset(startX + featherLen, startY + featherLen * 0.5f),
        strokeWidth = 1.5f,
        cap = StrokeCap.Round
    )
}

// Warp tunnel star for the slow warp animation
data class WarpStar(
    val angle: Float,
    val initialDist: Float,
    val speed: Float,
    val length: Float,
    val color: Color,
    val strokeWidth: Float
)

@Composable
fun WarpTunnelCanvas(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val stars = remember {
        val rand = Random(1234)
        val colors = listOf(Color.White, CelestialCyan, SoftBlush, StarGold, RomanticPink)
        List(140) {
            WarpStar(
                angle = rand.nextFloat() * (2f * PI.toFloat()),
                initialDist = rand.nextFloat(),
                speed = 0.6f + rand.nextFloat() * 1.2f,
                length = 20f + rand.nextFloat() * 60f,
                color = colors[rand.nextInt(colors.size)],
                strokeWidth = 2f + rand.nextFloat() * 4f
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val maxRadius = kotlin.math.hypot(cx, cy) * 1.2f

        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(GalaxyViolet, MidnightPurple, DeepSpace),
                center = Offset(cx, cy),
                radius = maxRadius * (0.6f + progress * 0.6f)
            )
        )

        // Concentric warp rings expanding outwards
        val ringCount = 6
        for (r in 0 until ringCount) {
            val ringProgress = (progress * 1.5f + r * (1f / ringCount)) % 1f
            val radius = ringProgress * maxRadius
            val ringAlpha = (1f - ringProgress).coerceIn(0f, 0.8f)
            drawCircle(
                color = CelestialCyan.copy(alpha = ringAlpha * 0.4f),
                radius = radius,
                center = Offset(cx, cy),
                style = Stroke(width = (2f + ringProgress * 6f))
            )
        }

        // Star streaks shooting out from the center (tunnel effect)
        stars.forEach { star ->
            val starP = (star.initialDist + progress * star.speed * 2.2f) % 1f
            val dist = starP * maxRadius
            val tailDist = (dist - star.length * starP * 1.8f).coerceAtLeast(0f)

            val x1 = cx + cos(star.angle) * tailDist
            val y1 = cy + sin(star.angle) * tailDist
            val x2 = cx + cos(star.angle) * dist
            val y2 = cy + sin(star.angle) * dist

            val starAlpha = (starP * 1.2f).coerceIn(0f, 1f)

            drawLine(
                color = star.color.copy(alpha = starAlpha),
                start = Offset(x1, y1),
                end = Offset(x2, y2),
                strokeWidth = star.strokeWidth * (0.5f + starP * 1.5f)
            )
        }

        // Center wormhole / portal glow
        val coreGlowRadius = 60f + progress * 140f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White, RomanticPink.copy(alpha = 0.8f), Color.Transparent),
                center = Offset(cx, cy),
                radius = coreGlowRadius
            ),
            center = Offset(cx, cy),
            radius = coreGlowRadius
        )
    }
}

// Galaxy background star representation
private data class GalaxyStar(
    val x: Float,
    val y: Float,
    val radius: Float,
    val twinkleOffset: Float,
    val twinkleSpeed: Float,
    val color: Color
)

@Composable
fun StarfieldGalaxyCanvas(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "twinkle")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "twinkle_time"
    )

    val stars = remember {
        val rand = Random(999)
        val colors = listOf(Color.White, Color(0xFFFFF0F5), CelestialCyan, StarGold, SoftBlush)
        List(280) {
            GalaxyStar(
                x = rand.nextFloat(),
                y = rand.nextFloat(),
                radius = 1f + rand.nextFloat() * 2.6f,
                twinkleOffset = rand.nextFloat() * 2f * PI.toFloat(),
                twinkleSpeed = 0.8f + rand.nextFloat() * 1.8f,
                color = colors[rand.nextInt(colors.size)]
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Cosmic nebula background
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    DeepSpace,
                    GalaxyViolet.copy(alpha = 0.85f),
                    DeepSpace,
                    MidnightPurple
                )
            )
        )

        // Nebula cloud glow blobs
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(RomanticPink.copy(alpha = 0.16f), Color.Transparent),
                center = Offset(w * 0.25f, h * 0.25f),
                radius = w * 0.6f
            ),
            center = Offset(w * 0.25f, h * 0.25f),
            radius = w * 0.6f
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(CelestialCyan.copy(alpha = 0.12f), Color.Transparent),
                center = Offset(w * 0.75f, h * 0.65f),
                radius = w * 0.55f
            ),
            center = Offset(w * 0.75f, h * 0.65f),
            radius = w * 0.55f
        )

        // Draw twinkling stars
        stars.forEach { star ->
            val sinVal = sin(time * star.twinkleSpeed + star.twinkleOffset)
            val brightness = 0.35f + 0.65f * ((sinVal + 1f) / 2f)
            val px = star.x * w
            val py = star.y * h

            drawCircle(
                color = star.color.copy(alpha = brightness),
                radius = star.radius * (0.8f + brightness * 0.4f),
                center = Offset(px, py)
            )

            // Cross sparkle for larger stars
            if (star.radius > 2.8f && brightness > 0.75f) {
                val flareLen = star.radius * 2.5f
                drawLine(
                    color = star.color.copy(alpha = brightness * 0.8f),
                    start = Offset(px - flareLen, py),
                    end = Offset(px + flareLen, py),
                    strokeWidth = 1f
                )
                drawLine(
                    color = star.color.copy(alpha = brightness * 0.8f),
                    start = Offset(px, py - flareLen),
                    end = Offset(px, py + flareLen),
                    strokeWidth = 1f
                )
            }
        }
    }
}

// Particle for the galaxy heart formed by stars
private data class ConstellationNode(
    val t: Float, // Parametric angle 0..2PI
    val jitterX: Float,
    val jitterY: Float,
    val size: Float,
    val color: Color,
    val twinkleSpeed: Float
)

/**
 * Custom Canvas drawing a magnificent heart formed entirely by stars,
 * galactic nebula dust, and a star-arrow cutting through the middle (representing 💘).
 */
@Composable
fun GalaxyConstellationHeartCanvas(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "galaxy_heart_anim")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "twinkle_time"
    )

    val starNodes = remember {
        val rand = Random(555)
        val colors = listOf(Color.White, StarGold, RomanticPink, CelestialCyan, SoftBlush)
        List(110) {
            val t = rand.nextFloat() * 2f * PI.toFloat()
            ConstellationNode(
                t = t,
                jitterX = (rand.nextFloat() - 0.5f) * 16f,
                jitterY = (rand.nextFloat() - 0.5f) * 16f,
                size = 2f + rand.nextFloat() * 3.5f,
                color = colors[rand.nextInt(colors.size)],
                twinkleSpeed = 0.7f + rand.nextFloat() * 1.5f
            )
        }
    }

    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val scale = (size.width * 0.026f) * pulse

        // 1. Nebula gas glow in heart shape
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    RomanticPink.copy(alpha = 0.45f),
                    CelestialCyan.copy(alpha = 0.22f),
                    Color.Transparent
                ),
                center = Offset(cx, cy),
                radius = size.width * 0.45f
            ),
            center = Offset(cx, cy),
            radius = size.width * 0.45f
        )

        // 2. Compute heart star points:
        // x(t) = 16 * sin(t)^3
        // y(t) = -(13 * cos(t) - 5 * cos(2t) - 2 * cos(3t) - cos(4t))
        val computedPoints = starNodes.map { node ->
            val sinT = sin(node.t)
            val hx = 16f * sinT * sinT * sinT
            val hy = -(13f * cos(node.t) - 5f * cos(2f * node.t) - 2f * cos(3f * node.t) - cos(4f * node.t))
            val px = cx + (hx * scale) + node.jitterX
            val py = cy + (hy * scale) + node.jitterY
            node to Offset(px, py)
        }

        // 3. Draw subtle glowing constellation lines connecting neighboring stars
        for (i in 0 until computedPoints.size - 1) {
            val p1 = computedPoints[i].second
            val p2 = computedPoints[i + 1].second
            val dist = (p1 - p2).getDistance()
            if (dist < size.width * 0.22f) {
                drawLine(
                    color = SoftBlush.copy(alpha = 0.35f),
                    start = p1,
                    end = p2,
                    strokeWidth = 1.2f
                )
            }
        }

        // 4. Draw Star Arrow cutting diagonally through the heart (bottom-left to top-right)
        val arrowSpan = size.width * 0.52f
        val aStartX = cx - arrowSpan * 0.85f
        val aStartY = cy + arrowSpan * 0.75f
        val aEndX = cx + arrowSpan * 0.95f
        val aEndY = cy - arrowSpan * 0.85f

        // Arrow cosmic beam
        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(StarGold.copy(alpha = 0.4f), Color.White, CelestialCyan)
            ),
            start = Offset(aStartX, aStartY),
            end = Offset(aEndX, aEndY),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )

        // Arrowhead formed by constellation of shining stars at top right
        val headLength = size.width * 0.12f
        drawLine(
            color = StarGold,
            start = Offset(aEndX, aEndY),
            end = Offset(aEndX - headLength, aEndY + headLength * 0.2f),
            strokeWidth = 2.5f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = StarGold,
            start = Offset(aEndX, aEndY),
            end = Offset(aEndX - headLength * 0.2f, aEndY + headLength),
            strokeWidth = 2.5f,
            cap = StrokeCap.Round
        )

        // Arrow feathers at bottom left
        val featherSpan = size.width * 0.08f
        drawLine(
            color = StarGold,
            start = Offset(aStartX, aStartY),
            end = Offset(aStartX - featherSpan * 0.6f, aStartY - featherSpan),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = StarGold,
            start = Offset(aStartX, aStartY),
            end = Offset(aStartX + featherSpan, aStartY + featherSpan * 0.6f),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )

        // 5. Draw the star nodes (twinkling star cluster forming the heart)
        computedPoints.forEach { (node, pos) ->
            val brightness = 0.4f + 0.6f * ((sin(time * node.twinkleSpeed + node.t) + 1f) / 2f)
            drawCircle(
                color = node.color.copy(alpha = brightness),
                radius = node.size * (0.8f + brightness * 0.5f),
                center = pos
            )

            // Star flare for bright nodes
            if (node.size > 4f && brightness > 0.75f) {
                val fLen = node.size * 2.2f
                drawLine(
                    color = Color.White.copy(alpha = brightness * 0.7f),
                    start = Offset(pos.x - fLen, pos.y),
                    end = Offset(pos.x + fLen, pos.y),
                    strokeWidth = 1f
                )
                drawLine(
                    color = Color.White.copy(alpha = brightness * 0.7f),
                    start = Offset(pos.x, pos.y - fLen),
                    end = Offset(pos.x, pos.y + fLen),
                    strokeWidth = 1f
                )
            }
        }
    }
}
