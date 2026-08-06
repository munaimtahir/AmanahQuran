package org.amanahquran.app.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val LocalElderMode = staticCompositionLocalOf { false }
val LocalThemeMode = staticCompositionLocalOf { ThemeMode.SYSTEM }
val LocalIsDarkTheme = staticCompositionLocalOf { false }

/**
 * Reader-specific semantic colour tokens (Feature C), kept separate from the Material
 * [androidx.compose.material3.ColorScheme] roles above because "primary"/"tertiary" are shared
 * by the whole app's chrome, while the reading surface needs its own calm, low-glare identity --
 * a warm paper background, near-black (not pure-black) text, and muted sage accents instead of
 * the brand green used for navigation/buttons elsewhere.
 */
data class ReaderPalette(
    val background: Color,
    val text: Color,
    val secondaryText: Color,
    val chromeBackground: Color,
    val chromeContent: Color,
    val divider: Color,
    val activeControl: Color,
    val onActiveControl: Color,
    val inactiveControl: Color,
    val pageMarker: Color,
    val currentAyahHighlight: Color,
    val controlSurface: Color,
)

private val LightReaderPalette = ReaderPalette(
    background = LightBackground,
    text = LightOnSurface,
    secondaryText = LightOnSurfaceVariant,
    chromeBackground = LightBackground,
    chromeContent = LightOnBackground,
    divider = LightDivider,
    activeControl = AmanahSageDeep,
    onActiveControl = LightSurface,
    inactiveControl = LightOnSurfaceVariant.copy(alpha = 0.55f),
    pageMarker = AmanahSageMuted,
    currentAyahHighlight = AmanahSageSoft.copy(alpha = 0.55f),
    controlSurface = LightCardSurface,
)

private val DarkReaderPalette = ReaderPalette(
    background = DarkBackground,
    text = DarkOnSurface,
    secondaryText = DarkOnSurfaceVariant,
    chromeBackground = DarkBackground,
    chromeContent = DarkOnBackground,
    divider = DarkDivider,
    activeControl = AmanahSageOnDark,
    onActiveControl = AmanahGreenDarker,
    inactiveControl = DarkOnSurfaceVariant.copy(alpha = 0.55f),
    pageMarker = AmanahSageOnDark,
    currentAyahHighlight = AmanahSageSoftOnDark.copy(alpha = 0.75f),
    controlSurface = DarkCardSurface,
)

private val SepiaReaderPalette = ReaderPalette(
    background = SepiaBackground,
    text = SepiaOnSurface,
    secondaryText = SepiaOnSurfaceVariant,
    chromeBackground = SepiaBackground,
    chromeContent = SepiaOnBackground,
    divider = SepiaDivider,
    activeControl = AmanahSageDeep,
    onActiveControl = SepiaSurface,
    inactiveControl = SepiaOnSurfaceVariant.copy(alpha = 0.55f),
    pageMarker = AmanahSageMuted,
    currentAyahHighlight = AmanahSageSoft.copy(alpha = 0.6f),
    controlSurface = SepiaCardSurface,
)

val LocalReaderPalette = staticCompositionLocalOf { LightReaderPalette }

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

    val readerPalette = when (themeMode) {
        ThemeMode.SYSTEM -> if (useDarkTheme) DarkReaderPalette else LightReaderPalette
        ThemeMode.LIGHT -> LightReaderPalette
        ThemeMode.DARK -> DarkReaderPalette
        ThemeMode.SEPIA -> SepiaReaderPalette
    }

    CompositionLocalProvider(
        LocalElderMode provides elderMode,
        LocalThemeMode provides themeMode,
        LocalIsDarkTheme provides useDarkTheme,
        LocalReaderPalette provides readerPalette,
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
