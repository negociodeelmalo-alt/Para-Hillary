package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val RomanticColorScheme = darkColorScheme(
    primary = RomanticPink,
    onPrimary = Color.White,
    primaryContainer = CosmicPlum,
    onPrimaryContainer = SoftBlush,
    secondary = WarmRose,
    onSecondary = Color.White,
    secondaryContainer = GalaxyViolet,
    onSecondaryContainer = RoseGold,
    tertiary = StarGold,
    onTertiary = DeepSpace,
    background = DeepSpace,
    onBackground = Color.White,
    surface = CardBackground,
    onSurface = Color.White,
    surfaceVariant = GalaxyViolet,
    onSurfaceVariant = MutedPurpleText
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = RomanticColorScheme,
        typography = Typography,
        content = content
    )
}
