package org.amanahquran.app.core.model

import org.junit.Assert.assertEquals
import org.junit.Test

class ReaderContentModeTest {

    @Test
    fun fromStoredName_migratesLegacyPageAndScrollModesToContinuous() {
        assertEquals(ReaderContentMode.CONTINUOUS, ReaderContentMode.fromStoredName("PAGE"))
        assertEquals(ReaderContentMode.CONTINUOUS, ReaderContentMode.fromStoredName("SCROLL"))
        assertEquals(ReaderContentMode.CONTINUOUS, ReaderContentMode.fromStoredName("BOOK"))
        assertEquals(ReaderContentMode.CONTINUOUS, ReaderContentMode.fromStoredName("BOOK_MODE"))
        assertEquals(ReaderContentMode.CONTINUOUS, ReaderContentMode.fromStoredName("CONTINUOUS"))
        assertEquals(ReaderContentMode.CONTINUOUS, ReaderContentMode.fromStoredName("continuous_view"))
    }

    @Test
    fun fromStoredName_resolvesAyahMode() {
        assertEquals(ReaderContentMode.AYAH, ReaderContentMode.fromStoredName("AYAH"))
        assertEquals(ReaderContentMode.AYAH, ReaderContentMode.fromStoredName("ayah_view"))
        assertEquals(ReaderContentMode.AYAH, ReaderContentMode.fromStoredName("ayah_by_ayah"))
    }

    @Test
    fun fromStoredName_nullOrUnknownFallsBackToDefault() {
        assertEquals(ReaderContentMode.default, ReaderContentMode.fromStoredName(null))
        assertEquals(ReaderContentMode.default, ReaderContentMode.fromStoredName(""))
        assertEquals(ReaderContentMode.default, ReaderContentMode.fromStoredName("unknown_mode"))
    }
}
