package org.amanahquran.app.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val LocalElderMode = staticCompositionLocalOf { false }
val LocalThemeMode = staticCompositionLocalOf { ThemeMode.SYSTEM }

private val AmanahLightColorScheme = lightColorScheme(
    primary = AmanahGreenDeep,
    onPrimary = LightSurface,
    primaryContainer = AmanahGreenSoft,
    onPrimaryContainer = AmanahGreenDeep,
    secondary = AmanahGreenMuted,
    onSecondary = LightSurface,
    secondaryContainer = AmanahGreenSoft,
    onSecondaryContainer = AmanahGreenDeep,
    tertiary = AmanahGoldMuted,
    onTertiary = LightSurface,
    tertiaryContainer = AmanahGoldSoftSurface,
    onTertiaryContainer = AmanahGreenDarker,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightBorder,
    outlineVariant = LightDivider,
    error = LightError,
    onError = LightSurface,
)

private val AmanahDarkColorScheme = darkColorScheme(
    primary = DarkPrimaryGreen,
    onPrimary = AmanahGreenDarker,
    primaryContainer = DarkReaderSurface,
    onPrimaryContainer = DarkPrimaryGreen,
    secondary = DarkPrimaryGreen,
    onSecondary = AmanahGreenDarker,
    secondaryContainer = DarkReaderSurface,
    onSecondaryContainer = DarkPrimaryGreen,
    tertiary = DarkAccentGold,
    onTertiary = AmanahGreenDarker,
    tertiaryContainer = DarkCardSurface,
    onTertiaryContainer = DarkAccentGold,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkCardSurface,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkBorder,
    outlineVariant = DarkDivider,
)

private val AmanahSepiaColorScheme = lightColorScheme(
    primary = AmanahGreenDeep,
    onPrimary = SepiaSurface,
    primaryContainer = SepiaSurfaceVariant,
    onPrimaryContainer = AmanahGreenDeep,
    secondary = AmanahGreenMuted,
    onSecondary = SepiaSurface,
    secondaryContainer = SepiaSurfaceVariant,
    onSecondaryContainer = SepiaOnBackground,
    tertiary = AmanahGoldMuted,
    onTertiary = SepiaSurface,
    tertiaryContainer = AmanahGoldSoftSurface,
    onTertiaryContainer = SepiaOnBackground,
    background = SepiaBackground,
    onBackground = SepiaOnBackground,
    surface = SepiaSurface,
    onSurface = SepiaOnSurface,
    surfaceVariant = SepiaSurfaceVariant,
    onSurfaceVariant = SepiaOnSurfaceVariant,
    outline = SepiaBorder,
    outlineVariant = SepiaDivider,
    error = LightError,
    onError = SepiaSurface,
)

private fun buildTypography(elderMode: Boolean): Typography {
    val scale = if (elderMode) 1.125f else 1.0f
    fun sp(base: Float) = (base * scale).sp
    fun lh(base: Float) = (base * scale).sp
    return Typography(
        displayLarge = TextStyle(fontSize = sp(57f), lineHeight = lh(64f), fontWeight = FontWeight.Normal),
        displayMedium = TextStyle(fontSize = sp(45f), lineHeight = lh(52f), fontWeight = FontWeight.Normal),
        displaySmall = TextStyle(fontSize = sp(36f), lineHeight = lh(44f), fontWeight = FontWeight.Normal),
        headlineLarge = TextStyle(fontSize = sp(32f), lineHeight = lh(40f), fontWeight = FontWeight.SemiBold),
        headlineMedium = TextStyle(fontSize = sp(28f), lineHeight = lh(36f), fontWeight = FontWeight.SemiBold),
        headlineSmall = TextStyle(fontSize = sp(24f), lineHeight = lh(32f), fontWeight = FontWeight.SemiBold),
        titleLarge = TextStyle(fontSize = sp(22f), lineHeight = lh(28f), fontWeight = FontWeight.SemiBold),
        titleMedium = TextStyle(fontSize = sp(18f), lineHeight = lh(24f), fontWeight = FontWeight.SemiBold),
        titleSmall = TextStyle(fontSize = sp(14f), lineHeight = lh(20f), fontWeight = FontWeight.Medium),
        bodyLarge = TextStyle(fontSize = sp(16f), lineHeight = lh(24f), fontWeight = FontWeight.Normal),
        bodyMedium = TextStyle(fontSize = sp(14f), lineHeight = lh(20f), fontWeight = FontWeight.Normal),
        bodySmall = TextStyle(fontSize = sp(12f), lineHeight = lh(16f), fontWeight = FontWeight.Normal),
        labelLarge = TextStyle(fontSize = sp(16f), lineHeight = lh(24f), fontWeight = FontWeight.SemiBold),
        labelMedium = TextStyle(fontSize = sp(12f), lineHeight = lh(16f), fontWeight = FontWeight.Medium),
        labelSmall = TextStyle(fontSize = sp(11f), lineHeight = lh(16f), fontWeight = FontWeight.Medium),
    )
}

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

    val colorScheme = when (themeMode) {
        ThemeMode.SYSTEM -> if (useDarkTheme) AmanahDarkColorScheme else AmanahLightColorScheme
        ThemeMode.LIGHT -> AmanahLightColorScheme
        ThemeMode.DARK -> AmanahDarkColorScheme
        ThemeMode.SEPIA -> AmanahSepiaColorScheme
    }

    CompositionLocalProvider(
        LocalElderMode provides elderMode,
        LocalThemeMode provides themeMode,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = buildTypography(elderMode),
            shapes = androidx.compose.material3.Shapes(
                extraSmall = AmanahShapes.badge,
                small = AmanahShapes.numberBadge,
                medium = AmanahShapes.card,
                large = AmanahShapes.card,
                extraLarge = AmanahShapes.ayahCard,
            ),
            content = content,
        )
    }
}
