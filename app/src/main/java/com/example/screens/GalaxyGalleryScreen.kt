package com.example.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.components.GalaxyConstellationHeartCanvas
import com.example.components.StarfieldGalaxyCanvas
import com.example.model.LoveLetter
import com.example.model.LoveLettersRepository
import com.example.ui.theme.CaveatFontFamily
import com.example.ui.theme.CelestialCyan
import com.example.ui.theme.DancingScriptFontFamily
import com.example.ui.theme.LightPinkPaper
import com.example.ui.theme.NunitoFontFamily
import com.example.ui.theme.Parchment
import com.example.ui.theme.RomanticPink
import com.example.ui.theme.RoseGold
import com.example.ui.theme.SoftBlush
import com.example.ui.theme.StarGold
import com.example.ui.theme.WarmRose

@Composable
fun GalaxyGalleryScreen(
    isMusicPlaying: Boolean,
    onToggleMusic: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Initially first card is unfolded
    var unfoldedCardIds by remember { mutableStateOf(setOf(1)) }
    var photoInModal by remember { mutableStateOf<LoveLetter?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        // Deep cosmic starfield with thousands of twinkling stars & glowing nebulae
        StarfieldGalaxyCanvas()

        // Main content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar with Music Toggle and Galaxy Badge
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CelestialCyan.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "💘 Galaxia de Amor 💘",
                                fontFamily = NunitoFontFamily,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = CelestialCyan
                            )
                        }
                    }

                    // Music badge & toggle
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StarGold.copy(alpha = 0.6f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 12.dp, end = 4.dp, top = 2.dp, bottom = 2.dp)
                        ) {
                            Text(
                                text = "Todo a su tiempo 💘",
                                fontFamily = NunitoFontFamily,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = StarGold
                            )
                            IconButton(
                                onClick = onToggleMusic,
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("gallery_music_toggle")
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
            }

            // Hero Section: Heart formed by stars/galaxy with arrow in the center (💘)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Central Heart formed by Galaxy Stars with the arrow in the middle
                    Box(
                        modifier = Modifier
                            .size(280.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Custom Canvas: Star constellation & nebula forming the heart and star arrow
                        GalaxyConstellationHeartCanvas(
                            modifier = Modifier.fillMaxSize()
                        )

                        // Central glowing 💘 in the heart of the galaxy
                        Text(
                            text = "💘",
                            fontSize = 72.sp,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "¡Dijiste que SÍ, Hillary! 💘",
                        fontFamily = DancingScriptFontFamily,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Desde este instante y por toda la eternidad que Dios decida 💘💍",
                        fontFamily = NunitoFontFamily,
                        fontSize = 15.sp,
                        color = RoseGold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Floating romantic words banner with 💘
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RomanticPink.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth(0.92f)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "💘",
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.size(6.dp))
                                Text(
                                    text = "Palabras grabadas en el cosmos",
                                    fontFamily = NunitoFontFamily,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SoftBlush
                                )
                                Spacer(modifier = Modifier.size(6.dp))
                                Text(
                                    text = "💘",
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Te amo 💘 Mi calma y mi paz 💘 Mi negla hermosa 💘 Amor eterno 💘 Juntos tú y yo 💘 Mi bendición 💘 Eres mi vida 💘 Dios con nosotros",
                                fontFamily = CaveatFontFamily,
                                fontSize = 18.sp,
                                color = LightPinkPaper,
                                textAlign = TextAlign.Center,
                                lineHeight = 24.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Toggle expand all / collapse all buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Nuestras 6 Cartas 💘",
                            fontFamily = DancingScriptFontFamily,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = StarGold
                        )

                        val allOpen = unfoldedCardIds.size == LoveLettersRepository.letters.size
                        Button(
                            onClick = {
                                unfoldedCardIds = if (allOpen) {
                                    emptySet()
                                } else {
                                    LoveLettersRepository.letters.map { it.id }.toSet()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RomanticPink.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (allOpen) "Plegar 💘" else "Abrir todas 💘",
                                fontFamily = NunitoFontFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // The 6 unfoldable love letter cards
            itemsIndexed(LoveLettersRepository.letters) { _, letter ->
                val isUnfolded = unfoldedCardIds.contains(letter.id)
                UnfoldableLoveLetterCard(
                    letter = letter,
                    isUnfolded = isUnfolded,
                    onToggle = {
                        unfoldedCardIds = if (isUnfolded) {
                            unfoldedCardIds - letter.id
                        } else {
                            unfoldedCardIds + letter.id
                        }
                    },
                    onPhotoClicked = {
                        photoInModal = letter
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .widthIn(max = 520.dp)
                        .padding(vertical = 10.dp)
                        .testTag("love_letter_card_${letter.id}")
                )
            }

            // Bottom Romantic Certificate / Promise with 💘
            item {
                Spacer(modifier = Modifier.height(24.dp))
                EternalPromiseCard()
                Spacer(modifier = Modifier.height(36.dp))
                Spacer(modifier = Modifier.navigationBarsPadding())
            }
        }

        // Full Screen Photo Modal
        photoInModal?.let { letter ->
            FullScreenPhotoDialog(
                letter = letter,
                onDismiss = { photoInModal = null }
            )
        }
    }
}

@Composable
private fun UnfoldableLoveLetterCard(
    letter: LoveLetter,
    isUnfolded: Boolean,
    onToggle: () -> Unit,
    onPhotoClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(16.dp, RoundedCornerShape(24.dp), spotColor = RomanticPink.copy(alpha = 0.35f))
            .border(
                width = 1.2.dp,
                brush = Brush.linearGradient(
                    if (isUnfolded) {
                        listOf(RomanticPink, StarGold, CelestialCyan)
                    } else {
                        listOf(RoseGold.copy(alpha = 0.6f), RomanticPink.copy(alpha = 0.3f))
                    }
                ),
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header: Envelope fold preview (Always clickable to toggle unfold)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Wax Seal Stamp with 💘
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(RomanticPink, WarmRose)
                                )
                            )
                            .border(1.5.dp, StarGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = letter.stampIcon,
                            fontSize = 22.sp
                        )
                    }

                    Column {
                        Text(
                            text = letter.title,
                            fontFamily = DancingScriptFontFamily,
                            fontSize = 24.sp,
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
                }

                // Unfold indicator icon
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isUnfolded) "Cerrar" else "Desplegar",
                        fontFamily = NunitoFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUnfolded) SoftBlush else CelestialCyan,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Icon(
                        imageVector = if (isUnfolded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isUnfolded) "Plegar carta" else "Desplegar carta",
                        tint = if (isUnfolded) SoftBlush else CelestialCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Unfolded Letter Content
            AnimatedVisibility(
                visible = isUnfolded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                        .padding(bottom = 22.dp)
                ) {
                    HorizontalDivider(
                        color = RomanticPink.copy(alpha = 0.25f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(bottom = 18.dp)
                    )

                    // Romantic Polaroid framed photo
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPhotoClicked() }
                            .shadow(12.dp, RoundedCornerShape(18.dp)),
                        shape = RoundedCornerShape(18.dp),
                        color = Parchment
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(260.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            ) {
                                Image(
                                    painter = painterResource(id = letter.photoRes),
                                    contentDescription = "Foto con Hillary - ${letter.title}",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
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
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.size(4.dp))
                                        Text(
                                            text = "Ampliar",
                                            fontFamily = NunitoFontFamily,
                                            fontSize = 11.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "✨ ${letter.decorativePhrase} ✨",
                                fontFamily = DancingScriptFontFamily,
                                fontSize = 16.sp,
                                color = Color(0xFF6B4559),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Love Letter Written Text (Handwriting script)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.Black.copy(alpha = 0.35f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StarGold.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "“ ${letter.message} ”",
                                fontFamily = CaveatFontFamily,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LightPinkPaper,
                                textAlign = TextAlign.Center,
                                lineHeight = 34.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "💘",
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.size(6.dp))
                                Text(
                                    text = "Siempre juntos",
                                    fontFamily = DancingScriptFontFamily,
                                    fontSize = 16.sp,
                                    color = StarGold
                                )
                                Spacer(modifier = Modifier.size(6.dp))
                                Text(
                                    text = "💘",
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EternalPromiseCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .widthIn(max = 520.dp)
            .shadow(20.dp, RoundedCornerShape(26.dp), spotColor = StarGold.copy(alpha = 0.4f))
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    listOf(StarGold, RomanticPink, StarGold)
                ),
                shape = RoundedCornerShape(26.dp)
            ),
        shape = RoundedCornerShape(26.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "💘 Promesa de Amor Eterno 💘",
                fontFamily = DancingScriptFontFamily,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = StarGold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Que Dios guíe siempre nuestros pasos, bendiga nuestro amor y nos mantenga unidos en cada latido, cada risa y cada amanecer.",
                fontFamily = NunitoFontFamily,
                fontSize = 15.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Te amo infinitamente, Hillary 💘",
                fontFamily = CaveatFontFamily,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = SoftBlush,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun FullScreenPhotoDialog(
    letter: LoveLetter,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
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
                // Top close button
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
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // High res photo
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
