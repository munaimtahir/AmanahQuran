package org.amanahquran.app.core.theme

import androidx.compose.runtime.staticCompositionLocalOf
import org.amanahquran.app.core.model.ReaderZoomLevel
import org.amanahquran.app.core.model.ScriptType

/**
 * Script-specific safe typography bounds. IndoPak's Standard baseline (24sp / 1.88 line-height)
 * matches this app's pre-existing single global font setting exactly, so migrating an existing
 * user onto zoom levels changes nothing visually until they actually move a level. Uthmani gets
 * its own, deliberately different baseline rather than reusing the same absolute sp value --
 * the KFGQPC-style Uthmani glyphs read smaller than the IndoPak/Nastaliq font at an identical sp.
 */
data class QuranTypographyProfile(
    val scriptType: ScriptType,
    val baseFontSize: Float,
    val minimumFontSize: Float,
    val maximumFontSize: Float,
    val baseLineHeightRatio: Float,
    val ayahMarkerScale: Float,
    val baseAyahSpacingDp: Float,
    val baseSectionSpacingDp: Float,
    val horizontalPaddingDp: Float,
)

object QuranTypography {
    val IndoPak = QuranTypographyProfile(
        scriptType = ScriptType.INDOPAK,
        baseFontSize = 24f,
        minimumFontSize = 16f,
        maximumFontSize = 46f,
        baseLineHeightRatio = 1.88f,
        ayahMarkerScale = 1f,
        baseAyahSpacingDp = 20f,
        baseSectionSpacingDp = 18f,
        horizontalPaddingDp = 16f,
    )

    val Uthmani = QuranTypographyProfile(
        scriptType = ScriptType.UTHMANI,
        baseFontSize = 26f,
        minimumFontSize = 17f,
        maximumFontSize = 48f,
        baseLineHeightRatio = 1.72f,
        ayahMarkerScale = 1f,
        baseAyahSpacingDp = 20f,
        baseSectionSpacingDp = 18f,
        horizontalPaddingDp = 16f,
    )

    fun profileFor(scriptType: ScriptType): QuranTypographyProfile = when (scriptType) {
        ScriptType.INDOPAK -> IndoPak
        ScriptType.UTHMANI -> Uthmani
    }
}

/** Fully-resolved presentation values for one (script, zoom level, Elder Mode) combination. */
data class QuranTypographyTokens(
    val arabicFontSizeSp: Float,
    val lineHeightSp: Float,
    val ayahMarkerScale: Float,
    val ayahSpacingDp: Float,
    val sectionSpacingDp: Float,
    val horizontalPaddingDp: Float,
)

/**
 * Larger levels need proportionally more breathing room or ascenders/diacritics feel cramped;
 * this nudges each profile's own base line-height ratio rather than jumping to a fixed constant,
 * so IndoPak and Uthmani each keep their own character. Bounded so Maximum never runs away.
 */
private fun lineHeightBoost(zoomLevel: ReaderZoomLevel): Float = when (zoomLevel) {
    ReaderZoomLevel.COMPACT, ReaderZoomLevel.SMALL -> -0.05f
    ReaderZoomLevel.STANDARD, ReaderZoomLevel.LARGE -> 0f
    ReaderZoomLevel.ELDER, ReaderZoomLevel.EXTRA_LARGE -> 0.06f
    ReaderZoomLevel.MAXIMUM -> 0.10f
}

fun resolveQuranTypographyTokens(
    scriptType: ScriptType,
    zoomLevel: ReaderZoomLevel,
    elderMode: Boolean,
): QuranTypographyTokens {
    val profile = QuranTypography.profileFor(scriptType)
    val fontSize = (profile.baseFontSize * zoomLevel.multiplier)
        .coerceIn(profile.minimumFontSize, profile.maximumFontSize)
    val elderExtraLineHeight = if (elderMode) 0.03f else 0f
    val lineRatio = (profile.baseLineHeightRatio + lineHeightBoost(zoomLevel) + elderExtraLineHeight)
        .coerceIn(1.45f, 1.98f)
    // Spacing/marker scale track the zoom level but on a gentler curve than font size itself,
    // so Maximum doesn't balloon into excessive whitespace between ayahs.
    val spacingMultiplier = zoomLevel.multiplier.coerceIn(0.85f, 1.35f)
    return QuranTypographyTokens(
        arabicFontSizeSp = fontSize,
        lineHeightSp = fontSize * lineRatio,
        ayahMarkerScale = profile.ayahMarkerScale * spacingMultiplier,
        ayahSpacingDp = profile.baseAyahSpacingDp * spacingMultiplier,
        sectionSpacingDp = profile.baseSectionSpacingDp * spacingMultiplier,
        horizontalPaddingDp = profile.horizontalPaddingDp,
    )
}

/**
 * Lets structural content (Surah/Juz headers, Bismillah) scale its own spacing/sizing alongside
 * the active zoom level without threading an extra parameter through every call site -- the same
 * pattern this codebase already uses for [LocalElderMode]. The default is Standard IndoPak,
 * non-Elder, matching the app's overall default before any reader screen provides a real value.
 */
val LocalQuranTypographyTokens = staticCompositionLocalOf {
    resolveQuranTypographyTokens(ScriptType.INDOPAK, ReaderZoomLevel.default, elderMode = false)
}
