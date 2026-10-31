package com.example.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.FloatingHeartsBackground
import com.example.ui.theme.CaveatFontFamily
import com.example.ui.theme.DancingScriptFontFamily
import com.example.ui.theme.LightPinkPaper
import com.example.ui.theme.NunitoFontFamily
import com.example.ui.theme.RomanticPink
import com.example.ui.theme.RoseGold
import com.example.ui.theme.StarGold
import com.example.ui.theme.WarmRose
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun SurveyScreen(
    onYesProposalChosen: () -> Unit,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(1) } // 1, 2, or 3
    var playfulNotice by remember { mutableStateOf<String?>(null) }

    // Playful dodger offset for the "No" button on question 3
    var dodgeOffsetX by remember { mutableFloatStateOf(0f) }
    var dodgeOffsetY by remember { mutableFloatStateOf(0f) }
    var noClickCount by remember { mutableIntStateOf(0) }
    var maybeClicked by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_hearts")
    val heartBeatScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heart_pulse"
    )

    val animatedDodgeX by animateFloatAsState(
        targetValue = dodgeOffsetX,
        animationSpec = spring(stiffness = 500f),
        label = "dodgeX"
    )
    val animatedDodgeY by animateFloatAsState(
        targetValue = dodgeOffsetY,
        animationSpec = spring(stiffness = 500f),
        label = "dodgeY"
    )

    Box(modifier = modifier.fillMaxSize()) {
        FloatingHeartsBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        if (step > 1) {
                            playfulNotice = null
                            step--
                        } else {
                            onBackToHome()
                        }
                    },
                    modifier = Modifier.testTag("survey_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Atrás",
                        tint = Color.White
                    )
                }

                // Step progress dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..3) {
                        val isActive = i <= step
                        Box(
                            modifier = Modifier
                                .size(if (i == step) 14.dp else 10.dp)
                                .clip(CircleShape)
                                .background(if (isActive) RomanticPink else Color.White.copy(alpha = 0.3f))
                        )
                    }
                }

                Text(
                    text = "💘",
                    fontSize = 24.sp
                )
            }

            // Playful feedback notice banner if triggered
            if (playfulNotice != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 6.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    color = RomanticPink.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.SentimentVerySatisfied,
                            contentDescription = null,
                            tint = StarGold,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(
                            text = playfulNotice ?: "",
                            fontFamily = NunitoFontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Animated question view
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = step,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> -width } + fadeOut()
                            )
                        } else {
                            (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> width } + fadeOut()
                            )
                        }
                    },
                    label = "survey_step_transition"
                ) { currentStep ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        when (currentStep) {
                            1 -> QuestionOneContent(
                                onYes = {
                                    playfulNotice = null
                                    step = 2
                                },
                                onNo = {
                                    playfulNotice = "¡Oye Hill! Tienes que prepararte ya mismo jajaja 💘 ¡Vamos, dale a Sí!"
                                }
                            )

                            2 -> QuestionTwoContent(
                                onYes = {
                                    playfulNotice = null
                                    step = 3
                                },
                                onDoubt = {
                                    playfulNotice = "¡No puedes dudar con esto tan lindo! 😉💘 ¡Tú sabes que estás segurísima!"
                                }
                            )

                            3 -> QuestionThreeContent(
                                onYes = onYesProposalChosen,
                                onNoAttempt = {
                                    noClickCount++
                                    val randX = Random.nextInt(-160, 160).toFloat()
                                    val randY = Random.nextInt(-80, 80).toFloat()
                                    dodgeOffsetX = randX
                                    dodgeOffsetY = randY

                                    val wittyPhrases = listOf(
                                        "¡Esa opción no es válida para nosotros! 🤭💘",
                                        "¡El botón se escapó porque tu destino es el SÍ! 💘",
                                        "¡Uy casi! Pero aquí solo aceptamos amor del bueno 💘",
                                        "¡No te resistas a ser feliz a mi lado! 💘",
                                        "¡Diosito ya escribió que seremos novios! 💘💍"
                                    )
                                    playfulNotice = wittyPhrases[noClickCount % wittyPhrases.size]
                                },
                                onMaybeAttempt = {
                                    maybeClicked = true
                                    playfulNotice = "¡'Tal vez' significa que te mueres de ganas de decir que SÍ! 💘💍 ¡Dale al botón grande!"
                                },
                                heartBeatScale = heartBeatScale,
                                dodgeOffsetX = animatedDodgeX,
                                dodgeOffsetY = animatedDodgeY,
                                maybeClicked = maybeClicked
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionOneContent(
    onYes: () -> Unit,
    onNo: () -> Unit
) {
    QuestionCard(
        badgeText = "Pregunta 1 de 3 💘",
        questionText = "¿Estás preparada para la pregunta que te haré?",
        iconEmoji = "🫣"
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Button(
                onClick = onYes,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("q1_yes_button"),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RomanticPink)
            ) {
                Text(
                    text = "¡Sí, estoy lista! 💘",
                    fontFamily = NunitoFontFamily,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            OutlinedButton(
                onClick = onNo,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("q1_no_button"),
                shape = RoundedCornerShape(26.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, RoseGold.copy(alpha = 0.6f))
            ) {
                Text(
                    text = "No... todavía no 🙈",
                    fontFamily = NunitoFontFamily,
                    fontSize = 15.sp,
                    color = LightPinkPaper
                )
            }
        }
    }
}

@Composable
private fun QuestionTwoContent(
    onYes: () -> Unit,
    onDoubt: () -> Unit
) {
    QuestionCard(
        badgeText = "Pregunta 2 de 3 💘",
        questionText = "¿Segura segurísima??",
        iconEmoji = "👀"
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Button(
                onClick = onYes,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("q2_yes_button"),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WarmRose)
            ) {
                Text(
                    text = "¡Sip, 100% segurísima! 💘",
                    fontFamily = NunitoFontFamily,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            OutlinedButton(
                onClick = onDoubt,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("q2_doubt_button"),
                shape = RoundedCornerShape(26.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, RoseGold.copy(alpha = 0.6f))
            ) {
                Text(
                    text = "Mmm... déjame pensarlo 🤔",
                    fontFamily = NunitoFontFamily,
                    fontSize = 15.sp,
                    color = LightPinkPaper
                )
            }
        }
    }
}

@Composable
private fun QuestionThreeContent(
    onYes: () -> Unit,
    onNoAttempt: () -> Unit,
    onMaybeAttempt: () -> Unit,
    heartBeatScale: Float,
    dodgeOffsetX: Float,
    dodgeOffsetY: Float,
    maybeClicked: Boolean
) {
    QuestionCard(
        badgeText = "✨ El Momento Especial 💘",
        questionText = "¿Quieres ser mi novia y la persona que se quede a mi lado durante todo el tiempo que Dios decida y que sea eterno?",
        iconEmoji = "💍",
        isFinalQuestion = true
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val scaleModifier = if (maybeClicked) Modifier.scale(1.12f) else Modifier.scale(heartBeatScale)

            Button(
                onClick = onYes,
                modifier = scaleModifier
                    .fillMaxWidth(0.92f)
                    .height(64.dp)
                    .shadow(20.dp, RoundedCornerShape(32.dp), spotColor = RomanticPink)
                    .testTag("q3_yes_button"),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "💘",
                            fontSize = 22.sp
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(
                            text = "¡¡SÍ, ACEPTO!! 💘💍",
                            fontFamily = NunitoFontFamily,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = StarGold,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onMaybeAttempt,
                    shape = RoundedCornerShape(22.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, RoseGold.copy(alpha = 0.5f)),
                    modifier = Modifier.testTag("q3_maybe_button")
                ) {
                    Text(
                        text = "Tal vez... 🤭",
                        fontFamily = NunitoFontFamily,
                        fontSize = 14.sp,
                        color = LightPinkPaper
                    )
                }

                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(dodgeOffsetX.roundToInt(), dodgeOffsetY.roundToInt())
                        }
                ) {
                    OutlinedButton(
                        onClick = onNoAttempt,
                        shape = RoundedCornerShape(22.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f)),
                        modifier = Modifier.testTag("q3_no_button")
                    ) {
                        Text(
                            text = "No 🙈",
                            fontFamily = NunitoFontFamily,
                            fontSize = 13.sp,
                            color = Color.LightGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionCard(
    badgeText: String,
    questionText: String,
    iconEmoji: String,
    isFinalQuestion: Boolean = false,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 520.dp)
            .shadow(
                elevation = 24.dp,
                shape = RoundedCornerShape(28.dp),
                spotColor = RomanticPink.copy(alpha = 0.4f)
            )
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    listOf(RomanticPink.copy(alpha = 0.8f), StarGold.copy(alpha = 0.6f))
                ),
                shape = RoundedCornerShape(28.dp)
            ),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = RomanticPink.copy(alpha = 0.18f),
                border = androidx.compose.foundation.BorderStroke(1.dp, RomanticPink.copy(alpha = 0.4f)),
                modifier = Modifier.padding(bottom = 14.dp)
            ) {
                Text(
                    text = badgeText,
                    fontFamily = NunitoFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = RoseGold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Text(
                text = iconEmoji,
                fontSize = if (isFinalQuestion) 44.sp else 36.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = questionText,
                fontFamily = if (isFinalQuestion) DancingScriptFontFamily else CaveatFontFamily,
                fontSize = if (isFinalQuestion) 28.sp else 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = if (isFinalQuestion) 36.sp else 32.sp
            )

            Spacer(modifier = Modifier.height(26.dp))

            content()
        }
    }
}
