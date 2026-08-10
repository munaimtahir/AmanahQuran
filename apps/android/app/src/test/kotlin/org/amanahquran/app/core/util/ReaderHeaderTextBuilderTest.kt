package org.amanahquran.app.core.util

import org.amanahquran.app.core.model.ReaderHeaderFormat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ReaderHeaderTextBuilderTest {
    @Test
    fun pageOnlyOmitsPrimaryEvenWhenSurahAndJuzKnown() {
        val parts = ReaderHeaderTextBuilder.build(ReaderHeaderFormat.PAGE_ONLY, "Al-Baqarah", 1, 8)
        assertNull(parts.primary)
        assertEquals("Page 8", parts.page)
    }

    @Test
    fun surahPageShowsSurahNameAndPage() {
        val parts = ReaderHeaderTextBuilder.build(ReaderHeaderFormat.SURAH_PAGE, "Al-Baqarah", 1, 8)
        assertEquals("Al-Baqarah", parts.primary)
        assertEquals("Page 8", parts.page)
    }

    @Test
    fun juzPageShowsJuzAndPageNotSurah() {
        val parts = ReaderHeaderTextBuilder.build(ReaderHeaderFormat.JUZ_PAGE, "Al-Baqarah", 1, 8)
        assertEquals("Juz 1", parts.primary)
        assertEquals("Page 8", parts.page)
    }

    @Test
    fun surahJuzPageCombinesAllThree() {
        val parts = ReaderHeaderTextBuilder.build(ReaderHeaderFormat.SURAH_JUZ_PAGE, "Al-Baqarah", 1, 8)
        assertEquals("Al-Baqarah · Juz 1", parts.primary)
        assertEquals("Page 8", parts.page)
    }

    @Test
    fun blankSurahNameOmittedFromPrimary() {
        val parts = ReaderHeaderTextBuilder.build(ReaderHeaderFormat.SURAH_PAGE, "", 1, 8)
        assertNull(parts.primary)
    }

    @Test
    fun nullPageNumberOmitsPagePart() {
        val parts = ReaderHeaderTextBuilder.build(ReaderHeaderFormat.SURAH_PAGE, "Al-Baqarah", 1, null)
        assertEquals("Al-Baqarah", parts.primary)
        assertNull(parts.page)
    }

    @Test
    fun nullJuzNumberOmitsJuzFromCombinedFormat() {
        val parts = ReaderHeaderTextBuilder.build(ReaderHeaderFormat.SURAH_JUZ_PAGE, "Al-Baqarah", null, 8)
        assertEquals("Al-Baqarah", parts.primary)
    }
}
