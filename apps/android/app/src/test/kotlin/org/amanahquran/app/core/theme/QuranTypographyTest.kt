package org.amanahquran.app.core.theme

import org.amanahquran.app.core.model.ReaderZoomLevel
import org.amanahquran.app.core.model.ScriptType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuranTypographyTest {
    @Test
    fun indoPakStandardMatchesTheAppsPreExistingDefaultExactly() {
        // Migration safety net: an install that has never touched adaptive zoom must render
        // identically to before -- 24sp is the historical single global default.
        val tokens = resolveQuranTypographyTokens(ScriptType.INDOPAK, ReaderZoomLevel.STANDARD, elderMode = false)
        assertEquals(24f, tokens.arabicFontSizeSp, 0.01f)
    }

    @Test
    fun everyLevelStaysWithinTheScriptsSafeFontSizeBounds() {
        for (script in ScriptType.entries) {
            val profile = QuranTypography.profileFor(script)
            for (level in ReaderZoomLevel.entries) {
                val tokens = resolveQuranTypographyTokens(script, level, elderMode = false)
                assertTrue(
                    "$script $level produced ${tokens.arabicFontSizeSp}sp outside [${profile.minimumFontSize}, ${profile.maximumFontSize}]",
                    tokens.arabicFontSizeSp >= profile.minimumFontSize && tokens.arabicFontSizeSp <= profile.maximumFontSize,
                )
                val elderTokens = resolveQuranTypographyTokens(script, level, elderMode = true)
                assertTrue(elderTokens.arabicFontSizeSp >= profile.minimumFontSize && elderTokens.arabicFontSizeSp <= profile.maximumFontSize)
            }
        }
    }

    @Test
    fun fontSizeIncreasesMonotonicallyWithZoomLevel() {
        val sizes = ReaderZoomLevel.entries.map { resolveQuranTypographyTokens(ScriptType.UTHMANI, it, elderMode = false).arabicFontSizeSp }
        assertEquals(sizes.sorted(), sizes)
    }

    @Test
    fun lineHeightIsAlwaysGreaterThanFontSize() {
        for (script in ScriptType.entries) {
            for (level in ReaderZoomLevel.entries) {
                val tokens = resolveQuranTypographyTokens(script, level, elderMode = false)
                assertTrue(tokens.lineHeightSp > tokens.arabicFontSizeSp)
            }
        }
    }

    @Test
    fun elderModeNeverReducesLineHeightRatioComparedToNonElder() {
        for (script in ScriptType.entries) {
            for (level in ReaderZoomLevel.entries) {
                val normal = resolveQuranTypographyTokens(script, level, elderMode = false)
                val elder = resolveQuranTypographyTokens(script, level, elderMode = true)
                val normalRatio = normal.lineHeightSp / normal.arabicFontSizeSp
                val elderRatio = elder.lineHeightSp / elder.arabicFontSizeSp
                assertTrue("$script $level: elder ratio $elderRatio should be >= normal ratio $normalRatio", elderRatio >= normalRatio)
            }
        }
    }

    @Test
    fun indoPakAndUthmaniDoNotShareIdenticalAbsoluteBaseSize() {
        val indoPak = resolveQuranTypographyTokens(ScriptType.INDOPAK, ReaderZoomLevel.STANDARD, elderMode = false)
        val uthmani = resolveQuranTypographyTokens(ScriptType.UTHMANI, ReaderZoomLevel.STANDARD, elderMode = false)
        assertTrue(indoPak.arabicFontSizeSp != uthmani.arabicFontSizeSp)
    }

    @Test
    fun ayahSpacingAndMarkerScaleGrowWithZoomLevelButStayBounded() {
        val compact = resolveQuranTypographyTokens(ScriptType.INDOPAK, ReaderZoomLevel.COMPACT, elderMode = false)
        val maximum = resolveQuranTypographyTokens(ScriptType.INDOPAK, ReaderZoomLevel.MAXIMUM, elderMode = false)
        assertTrue(maximum.ayahSpacingDp > compact.ayahSpacingDp)
        assertTrue(maximum.ayahMarkerScale > compact.ayahMarkerScale)
        // Bounded gentler curve (section 10): spacing must not blow up as aggressively as font size.
        val fontSizeGrowth = maximum.arabicFontSizeSp / compact.arabicFontSizeSp
        val spacingGrowth = maximum.ayahSpacingDp / compact.ayahSpacingDp
        assertTrue(spacingGrowth < fontSizeGrowth)
    }
}
