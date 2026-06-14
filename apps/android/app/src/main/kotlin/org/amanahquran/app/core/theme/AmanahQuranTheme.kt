package org.amanahquran.app.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AmanahLightColors = lightColorScheme(
    primary = Color(0xFF3F5A36),
    onPrimary = Color.White,
    secondary = Color(0xFF5D6A55),
    onSecondary = Color.White,
    background = Color(0xFFF7F5EF),
    onBackground = Color(0xFF1F1B16),
    surface = Color(0xFFFDFCF8),
    onSurface = Color(0xFF1F1B16),
)

private val AmanahDarkColors = darkColorScheme(
    primary = Color(0xFFB9D5AD),
    onPrimary = Color(0xFF102110),
    secondary = Color(0xFFB9C5B3),
    onSecondary = Color(0xFF101410),
    background = Color(0xFF11140F),
    onBackground = Color(0xFFE3E0D7),
    surface = Color(0xFF171A14),
    onSurface = Color(0xFFE3E0D7),
)

private val AmanahSepiaColors = lightColorScheme(
    primary = Color(0xFF6B4E16),
    onPrimary = Color.White,
    secondary = Color(0xFF8B6E3D),
    onSecondary = Color.White,
    background = Color(0xFFF6ECD8),
    onBackground = Color(0xFF2E2214),
    surface = Color(0xFFFCF4E6),
    onSurface = Color(0xFF2E2214),
)

@Composable
fun AmanahQuranTheme(
    themeMode: ThemeMode,
    elderMode: Boolean,
    content: @Composable () -> Unit,
) {
    val useDarkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SEPIA -> false
    }

    val colors = when (themeMode) {
        ThemeMode.SYSTEM -> if (useDarkTheme) AmanahDarkColors else AmanahLightColors
        ThemeMode.LIGHT -> AmanahLightColors
        ThemeMode.DARK -> AmanahDarkColors
        ThemeMode.SEPIA -> AmanahSepiaColors
    }

    MaterialTheme(
        colorScheme = colors,
        typography = if (elderMode) AmanahTypographyLarge else AmanahTypographyDefault,
        content = content,
    )
}

private val AmanahTypographyDefault = androidx.compose.material3.Typography()

private val AmanahTypographyLarge = androidx.compose.material3.Typography()

