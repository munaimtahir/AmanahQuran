package org.amanahquran.app.core.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ThemeModeTest {
    @Test
    fun themeModes_includeAllPlannedOptions() {
        assertEquals(
            listOf(
                ThemeMode.SYSTEM,
                ThemeMode.LIGHT,
                ThemeMode.DARK,
                ThemeMode.SEPIA,
                ThemeMode.BLACK,
            ),
            ThemeMode.entries.toList(),
        )
    }

    @Test
    fun blackTheme_isAnOptionalDarkVariant() {
        assertEquals(true, ThemeMode.BLACK.isDark)
        assertEquals(true, ThemeMode.DARK.isDark)
        assertEquals(false, ThemeMode.LIGHT.isDark)
        assertEquals(false, ThemeMode.SEPIA.isDark)
        assertNull(ThemeMode.SYSTEM.isDark)
        assertEquals("Black (OLED)", ThemeMode.BLACK.displayName)
    }

    @Test
    fun blackTheme_usesTrueBlackBackgroundWithSoftText() {
        assertEquals(0f, BlackBackground.red)
        assertEquals(0f, BlackBackground.green)
        assertEquals(0f, BlackBackground.blue)
        assertEquals(1f, BlackBackground.alpha)
        // Soft off-white text, not pure white, to limit glare on OLED in the dark.
        assertEquals(false, BlackOnSurface == androidx.compose.ui.graphics.Color.White)
    }

    @Test
    fun blackTheme_textContrastMeetsWcagAaa() {
        assertEquals(true, contrastRatio(BlackOnSurface, BlackBackground) >= 7.0)
        assertEquals(true, contrastRatio(BlackOnSurfaceVariant, BlackBackground) >= 7.0)
    }

    private fun contrastRatio(a: androidx.compose.ui.graphics.Color, b: androidx.compose.ui.graphics.Color): Double {
        fun luminance(c: androidx.compose.ui.graphics.Color): Double {
            fun channel(v: Float): Double = if (v <= 0.03928f) v / 12.92 else Math.pow((v + 0.055) / 1.055, 2.4)
            return 0.2126 * channel(c.red) + 0.7152 * channel(c.green) + 0.0722 * channel(c.blue)
        }
        val la = luminance(a)
        val lb = luminance(b)
        return (maxOf(la, lb) + 0.05) / (minOf(la, lb) + 0.05)
    }
}
