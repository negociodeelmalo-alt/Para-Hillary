package com.example.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.LoveLetter
import com.example.model.LoveLettersRepository
import com.example.ui.theme.CaveatFontFamily
import com.example.ui.theme.CelestialCyan
import com.example.ui.theme.DancingScriptFontFamily
import com.example.ui.theme.DeepSpace
import com.example.ui.theme.GalaxyViolet
import com.example.ui.theme.LightPinkPaper
import com.example.ui.theme.MidnightPurple
import com.example.ui.theme.NunitoFontFamily
import com.example.ui.theme.Parchment
import com.example.ui.theme.RomanticPink
import com.example.ui.theme.RoseGold
import com.example.ui.theme.SoftBlush
import com.example.ui.theme.StarGold
import com.example.ui.theme.WarmRose
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

// 3D Star in Galaxy
private data class SpiralStar(
    val x: Float,
    val y: Float,
    val z: Float,
    val baseSize: Float,
    val color: Color,
    val isCoreHeart: Boolean = false,
    val isArrow: Boolean = false
)

// Clean Floating Word on the outer spiral
private data class CleanWord3D(
    val text: String,
    val emoji: String,
    val x: Float,
    val y: Float,
    val z: Float
)

@Composable
fun Galaxy3DScreen(
    isMusicPlaying: Boolean,
    onToggleMusic: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 3D Camera Controls
    var rotX by remember { mutableFloatStateOf(-0.48f) } // Slightly tilted top-down angle
    var rotY by remember { mutableFloatStateOf(0.15f) }
    var zoomScale by remember { mutableFloatStateOf(1.0f) }
    var isUserInteracting by remember { mutableStateOf(false) }

    // Active modals
    var activeLetter by remember { mutableStateOf<LoveLetter?>(null) }
    var fullPhotoLetter by remember { mutableStateOf<LoveLetter?>(null) }

    // Bouncing arrow animation for "Mira tus cartas mi pulga"
    val infiniteTransition = rememberInfiniteTransition(label = "bouncing_arrow")
    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 7f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    // Widely spaced floating words with emojis (Clean, without clutter, spaced far out on arms)
    val wordsList = remember {
        listOf(
            "Eres mi paz" to "🕊️",
            "Mi universo" to "🌌",
            "Te amo" to "💖",
            "Siempre a tu lado" to "💍",
            "Mi mayor suerte" to "🍀",
            "Mi razón de ser" to "✨",
            "Contigo por siempre" to "♾️",
            "Mi cielo" to "🌟"
        )
    }

    val cleanFloatingWords = remember {
        val rand = Random(99)
        wordsList.mapIndexed { index, (word, emoji) ->
            // Distribute across 360 degrees, wide radius (340 to 480), far away from core heart
            val angle = (index.toFloat() / wordsList.size) * (2f * PI.toFloat()) + (index * 0.35f)
            val radius = 340f + (index % 3) * 55f
            val elevation = (if (index % 2 == 0) 1f else -1f) * (20f + rand.nextFloat() * 25f)
            CleanWord3D(
                text = word,
                emoji = emoji,
                x = radius * cos(angle),
                y = elevation,
                z = radius * sin(angle)
            )
        }
    }

    // Generate Big Central Heart with Arrow (💘) + Wide Spiral Arms
    val galaxyStars = remember {
        val stars = mutableListOf<SpiralStar>()
        val rand = Random(444)
        val armColors = listOf(
            Color(0xFFFFB6C1), // Soft pink
            Color(0xFFFF69B4), // Radiant pink
            Color(0xFFFFD700), // Star gold
            Color(0xFFFFFFFF), // Pure white
            Color(0xFFE2B6FF), // Soft purple
            Color(0xFF8AE8FF)  // Soft cyan
        )

        // 1. BIG Central Heart formed by stars (approx 550 stars)
        // hScale = 8.0f makes it prominent, tall and bold in the core
        for (i in 0 until 500) {
            val t = rand.nextFloat() * (2f * PI.toFloat())
            val sinT = sin(t)
            val hx = 16f * sinT * sinT * sinT
            val hy = -(13f * cos(t) - 5f * cos(2f * t) - 2f * cos(3f * t) - cos(4f * t))
            val hScale = 7.5f + rand.nextFloat() * 1.2f
            val thickness = (36f * (1f - (kotlin.math.abs(hy) / 20f))).coerceAtLeast(8f)
            val hz = (rand.nextFloat() - 0.5f) * 2f * thickness

            stars.add(
                SpiralStar(
                    x = hx * hScale + (rand.nextFloat() - 0.5f) * 8f,
                    y = hy * hScale + (rand.nextFloat() - 0.5f) * 8f,
                    z = hz,
                    baseSize = 2.4f + rand.nextFloat() * 2.6f,
                    color = if (rand.nextFloat() > 0.3f) Color(0xFFFF2A7A) else Color(0xFFFFD166),
                    isCoreHeart = true
                )
            )
        }

        // 2. BIG Central Star Arrow piercing through the heart diagonally
        // From (-160, 135, -45) through center (0,0,0) to (+180, -150, +45)
        val arrowSteps = 95
        for (i in 0..arrowSteps) {
            val f = i / arrowSteps.toFloat()
            val ax = -160f + f * 340f
            val ay = 135f - f * 285f
            val az = -45f + f * 90f
            stars.add(
                SpiralStar(
                    x = ax + (rand.nextFloat() - 0.5f) * 5f,
                    y = ay + (rand.nextFloat() - 0.5f) * 5f,
                    z = az + (rand.nextFloat() - 0.5f) * 5f,
                    baseSize = 3.0f + rand.nextFloat() * 2.2f,
                    color = Color.White,
                    isCoreHeart = true,
                    isArrow = true
                )
            )
        }

        // Arrow head constellation cluster at top-right
        for (i in 0 until 40) {
            val spread = rand.nextFloat() * 32f
            stars.add(
                SpiralStar(
                    x = 180f - spread + (rand.nextFloat() - 0.5f) * 12f,
                    y = -150f + spread + (rand.nextFloat() - 0.5f) * 12f,
                    z = 45f + (rand.nextFloat() - 0.5f) * 12f,
                    baseSize = 3.2f + rand.nextFloat() * 2.8f,
                    color = Color.White,
                    isCoreHeart = true,
                    isArrow = true
                )
            )
        }

        // Arrow fletching feathers cluster at bottom-left
        for (i in 0 until 35) {
            val spread = rand.nextFloat() * 28f
            stars.add(
                SpiralStar(
                    x = -160f + spread + (rand.nextFloat() - 0.5f) * 10f,
                    y = 135f - spread + (rand.nextFloat() - 0.5f) * 10f,
                    z = -45f + (rand.nextFloat() - 0.5f) * 10f,
                    baseSize = 2.4f + rand.nextFloat() * 2.2f,
                    color = StarGold,
                    isCoreHeart = true,
                    isArrow = true
                )
            )
        }

        // 3. Wide Expansive Spiral Arms (3 arms) winding outward cleanly
        // Radii start at r = 180 to leave a clear boundary around the central heart
        val numArms = 3
        val starsPerArm = 520
        val maxRadius = 600f

        for (arm in 0 until numArms) {
            val armPhase = arm * (2f * PI.toFloat() / numArms)
            for (i in 0 until starsPerArm) {
                val f = rand.nextFloat()
                val r = 180f + f * (maxRadius - 180f)

                // Logarithmic spiral formula
                val theta = armPhase + (2.1f * kotlin.math.ln(r / 110f))
                val armWidth = 16f + (f * 42f)
                val perpAngle = theta + (PI.toFloat() / 2f)

                val jitterDist = (rand.nextFloat() - 0.5f) * armWidth
                val px = r * cos(theta) + jitterDist * cos(perpAngle)
                val pz = r * sin(theta) + jitterDist * sin(perpAngle)

                val diskHeight = (1 - f) * 30f + 10f
                val py = (rand.nextFloat() - 0.5f) * diskHeight

                stars.add(
                    SpiralStar(
                        x = px,
                        y = py,
                        z = pz,
                        baseSize = 1.0f + rand.nextFloat() * 2.0f,
                        color = armColors[rand.nextInt(armColors.size)]
                    )
                )
            }
        }

        // Ambient background stars
        for (i in 0 until 140) {
            val r = 260f + rand.nextFloat() * 450f
            val theta = rand.nextFloat() * (2f * PI.toFloat())
            val phi = (rand.nextFloat() - 0.5f) * (PI.toFloat() * 0.7f)
            stars.add(
                SpiralStar(
                    x = r * cos(phi) * cos(theta),
                    y = r * sin(phi),
                    z = r * cos(phi) * sin(theta),
                    baseSize = 0.8f + rand.nextFloat() * 1.5f,
                    color = Color.White.copy(alpha = 0.6f)
                )
            )
        }

        stars
    }

    // Gentle auto-rotation when user is not touching
    LaunchedEffect(isUserInteracting) {
        while (isActive) {
            if (!isUserInteracting) {
                rotY += 0.0025f
            }
            delay(16)
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(DeepSpace, MidnightPurple, DeepSpace, GalaxyViolet)
                )
            )
            .pointerInput(Unit) {
                // Unified gesture detection: 1-finger drag for 3D rotation, 2-finger pinch for zoom
                detectTransformGestures(
                    panZoomLock = false
                ) { _, pan, zoom, _ ->
                    isUserInteracting = true
                    zoomScale = (zoomScale * zoom).coerceIn(0.45f, 2.6f)
                    rotY += pan.x * 0.0065f
                    rotX = (rotX - pan.y * 0.0065f).coerceIn(-1.5f, 1.5f)
                }
            }
    ) {
        val w = constraints.maxWidth.toFloat()
        val h = constraints.maxHeight.toFloat()
        val cx = w / 2f
        val cy = h / 2f - 30f
        val cameraDist = 720f

        // 3D Perspective Projection
        fun project3D(x: Float, y: Float, z: Float): Triple<Float, Float, Float> {
            val cosY = cos(rotY)
            val sinY = sin(rotY)
            val x1 = x * cosY + z * sinY
            val z1 = -x * sinY + z * cosY

            val cosX = cos(rotX)
            val sinX = sin(rotX)
            val y1 = y * cosX - z1 * sinX
            val z2 = y * sinX + z1 * cosX

            val scale = ((cameraDist / (cameraDist + z2)) * zoomScale).coerceIn(0.2f, 3.8f)
            val px = cx + x1 * scale
            val py = cy + y1 * scale
            return Triple(px, py, z2)
        }

        // 1. Render Galaxy Stars on Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Heart core nebula glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        RomanticPink.copy(alpha = 0.45f),
                        WarmRose.copy(alpha = 0.22f),
                        CelestialCyan.copy(alpha = 0.1f),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = (190f * zoomScale).coerceAtLeast(80f)
                ),
                center = Offset(cx, cy),
                radius = (190f * zoomScale).coerceAtLeast(80f)
            )

            // Depth sort and render stars
            val projectedStars = galaxyStars.map { star ->
                val (px, py, pz) = project3D(star.x, star.y, star.z)
                val scale = ((cameraDist / (cameraDist + pz)) * zoomScale).coerceIn(0.2f, 3.8f)
                val alpha = ((pz + 500f) / 1000f).coerceIn(0.25f, 1f)
                Triple(star, Offset(px, py), scale to alpha)
            }.sortedBy { it.third.first }

            projectedStars.forEach { (star, offset, scaleAlpha) ->
                val (scale, alpha) = scaleAlpha
                val starRadius = (star.baseSize * scale).coerceAtLeast(0.8f)

                drawCircle(
                    color = star.color.copy(alpha = alpha),
                    radius = starRadius,
                    center = offset
                )

                // Extra glow for arrow and heart core stars
                if (star.isArrow && scale > 0.85f) {
                    drawCircle(
                        color = Color.White.copy(alpha = alpha * 0.5f),
                        radius = starRadius * 1.7f,
                        center = offset
                    )
                }
            }
        }

        // 2. Clean, Spaced-Out Floating Words on the Outer Spiral (No bulky boxes!)
        cleanFloatingWords.forEach { item ->
            val (px, py, pz) = project3D(item.x, item.y, item.z)
            val scale = ((cameraDist / (cameraDist + pz)) * zoomScale).coerceIn(0.5f, 1.7f)
            val alpha = ((pz + 450f) / 900f).coerceIn(0.28f, 0.95f)

            if (pz > -500f) {
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                (px - 50f).roundToInt(),
                                (py - 12f).roundToInt()
                            )
                        }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(2.dp)
                    ) {
                        Text(
                            text = item.text,
                            fontFamily = CaveatFontFamily,
                            fontSize = (18.sp.value * scale).sp,
                            fontWeight = FontWeight.Bold,
                            color = LightPinkPaper.copy(alpha = alpha),
                            style = TextStyle(
                                shadow = Shadow(
                                    color = RomanticPink.copy(alpha = 0.8f * alpha),
                                    blurRadius = 8f
                                )
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = item.emoji,
                            fontSize = (15.sp.value * scale).sp
                        )
                    }
                }
            }
        }

        // 3. Top Clean Bar with requested dedication:
        // "Para ti Hillary, amor que me enamora cada dia mas, Te amo un monton"
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(listOf(RomanticPink.copy(alpha = 0.5f), StarGold.copy(alpha = 0.5f)))
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Para ti Hillary, amor que me enamora cada dia mas, Te amo un monton",
                        fontFamily = DancingScriptFontFamily,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 2,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onToggleMusic,
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("galaxy_music_toggle")
                    ) {
                        Icon(
                            imageVector = if (isMusicPlaying) Icons.Default.MusicNote else Icons.Default.MusicOff,
                            contentDescription = "Música",
                            tint = if (isMusicPlaying) StarGold else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // 4. Right Side Quick Zoom Buttons (+, -, Reset)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                border = androidx.compose.foundation.BorderStroke(1.dp, RoseGold.copy(alpha = 0.6f)),
                shadowElevation = 6.dp
            ) {
                IconButton(
                    onClick = {
                        zoomScale = (zoomScale + 0.25f).coerceAtMost(2.6f)
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("zoom_in_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Acercar",
                        tint = Color.White
                    )
                }
            }

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                border = androidx.compose.foundation.BorderStroke(1.dp, RoseGold.copy(alpha = 0.6f)),
                shadowElevation = 6.dp
            ) {
                IconButton(
                    onClick = {
                        zoomScale = (zoomScale - 0.25f).coerceAtLeast(0.45f)
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("zoom_out_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Alejar",
                        tint = Color.White
                    )
                }
            }

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                border = androidx.compose.foundation.BorderStroke(1.dp, CelestialCyan.copy(alpha = 0.6f)),
                shadowElevation = 6.dp
            ) {
                IconButton(
                    onClick = {
                        rotX = -0.48f
                        rotY = 0.15f
                        zoomScale = 1.0f
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("reset_view_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = "Restablecer vista",
                        tint = CelestialCyan
                    )
                }
            }
        }

        // 5. Bottom Section: Pointer Arrow + "Mira tus cartas mi pulga" + The 6 Love Letters Dock
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // As requested: "que alla como una flecha que ella sepa y diga 'Mira tus cartas mi pulga'"
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                border = androidx.compose.foundation.BorderStroke(
                    1.2.dp,
                    Brush.horizontalGradient(listOf(RomanticPink, StarGold))
                ),
                shadowElevation = 10.dp,
                modifier = Modifier
                    .offset(y = bounceOffset.dp)
                    .padding(bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mira tus cartas mi pulga",
                        fontFamily = DancingScriptFontFamily,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = StarGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Clean, elegant Dock with all 6 letters (No cluttered labels in the 3D space!)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.90f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(listOf(RomanticPink.copy(alpha = 0.5f), StarGold.copy(alpha = 0.5f)))
                )
            ) {
                LazyRow(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(LoveLettersRepository.letters) { letter ->
                        Button(
                            onClick = { activeLetter = letter },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RomanticPink.copy(alpha = 0.28f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                horizontal = 14.dp,
                                vertical = 8.dp
                            ),
                            modifier = Modifier.testTag("dock_card_${letter.id}")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = letter.title,
                                    fontFamily = NunitoFontFamily,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // 6. Letter Modal (Clean, romantic, with photos and dedications)
        activeLetter?.let { letter ->
            Dialog(onDismissRequest = { activeLetter = null }) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 500.dp)
                        .shadow(24.dp, RoundedCornerShape(26.dp), spotColor = RomanticPink)
                        .border(
                            1.5.dp,
                            Brush.linearGradient(listOf(RomanticPink, StarGold, CelestialCyan)),
                            RoundedCornerShape(26.dp)
                        ),
                    shape = RoundedCornerShape(26.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = letter.title,
                                    fontFamily = DancingScriptFontFamily,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = letter.dateTag,
                                    fontFamily = NunitoFontFamily,
                                    fontSize = 12.sp,
                                    color = RoseGold
                                )
                            }
                            IconButton(onClick = { activeLetter = null }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cerrar",
                                    tint = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Polaroid photo
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Parchment,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    fullPhotoLetter = letter
                                }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(240.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                ) {
                                    Image(
                                        painter = painterResource(id = letter.photoRes),
                                        contentDescription = letter.title,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color.Black.copy(alpha = 0.65f),
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Visibility,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Ampliar",
                                                fontFamily = NunitoFontFamily,
                                                fontSize = 11.sp,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "✨ ${letter.decorativePhrase} ✨",
                                    fontFamily = DancingScriptFontFamily,
                                    fontSize = 15.sp,
                                    color = Color(0xFF6B4559),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Dedicated romantic handwritten message
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.Black.copy(alpha = 0.4f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                StarGold.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "“ ${letter.message} ”",
                                    fontFamily = CaveatFontFamily,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = LightPinkPaper,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 32.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "Siempre juntos 💘",
                                    fontFamily = DancingScriptFontFamily,
                                    fontSize = 17.sp,
                                    color = StarGold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 7. Full Screen Photo Modal
        fullPhotoLetter?.let { letter ->
            Dialog(onDismissRequest = { fullPhotoLetter = null }) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .shadow(24.dp, RoundedCornerShape(22.dp)),
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = letter.title,
                                fontFamily = DancingScriptFontFamily,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            IconButton(onClick = { fullPhotoLetter = null }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cerrar",
                                    tint = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Image(
                            painter = painterResource(id = letter.photoRes),
                            contentDescription = letter.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(380.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = letter.message,
                            fontFamily = CaveatFontFamily,
                            fontSize = 22.sp,
                            color = LightPinkPaper,
                            textAlign = TextAlign.Center,
                            lineHeight = 28.sp
                        )
                    }
                }
            }
        }
    }
}
