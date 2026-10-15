package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = TaskFlowIndigo,
    onPrimary = Color.White,
    primaryContainer = TaskFlowIndigoLight,
    onPrimaryContainer = TaskFlowIndigoDark,
    secondary = AccentCoralHigh,
    onSecondary = Color.White,
    secondaryContainer = AccentCoralLight,
    onSecondaryContainer = Color(0xFF9F1239),
    tertiary = SuccessGreen,
    onTertiary = Color.White,
    tertiaryContainer = SuccessGreenLight,
    onTertiaryContainer = Color(0xFF14532D),
    background = OffWhiteBackground,
    onBackground = TextPrimaryDark,
    surface = CardSurfaceWhite,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceSubtle,
    onSurfaceVariant = TextSecondaryMuted,
    outline = SurfaceBorderLight,
    outlineVariant = Color(0xFFE2E8F0)
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF312E81),
    onPrimaryContainer = Color(0xFFC7D2FE),
    secondary = AccentCoralHigh,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF881337),
    onSecondaryContainer = Color(0xFFFFE4E6),
    tertiary = SuccessGreen,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF064E3B),
    onTertiaryContainer = Color(0xFFA7F3D0),
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceBorder,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkSurfaceBorder,
    outlineVariant = Color(0xFF1E293B)
)

@Composable
fun TaskFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TaskFlowTypography,
        content = content
    )
}
