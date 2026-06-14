package org.amanahquran.app.core.theme

import org.junit.Assert.assertEquals
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
            ),
            ThemeMode.entries.toList(),
        )
    }
}

