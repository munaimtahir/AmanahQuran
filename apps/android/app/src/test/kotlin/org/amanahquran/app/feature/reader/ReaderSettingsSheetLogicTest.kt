package org.amanahquran.app.feature.reader

import org.amanahquran.app.core.model.ReaderZoomLevel
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.model.TranslationSelection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ReaderSettingsSheetLogicTest {
    @Test
    fun linkedZoom_movesArabicAndTranslationTogether_fromEitherSlider() {
        val fromArabic = resolveReaderZoomChange(ReaderSizeSlider.ARABIC, ReaderZoomLevel.LARGE, linked = true, hasTranslation = true)
        assertEquals(ReaderZoomChange(ReaderZoomLevel.LARGE, ReaderZoomLevel.LARGE), fromArabic)
        val fromTranslation = resolveReaderZoomChange(ReaderSizeSlider.TRANSLATION, ReaderZoomLevel.SMALL, linked = true, hasTranslation = true)
        assertEquals(ReaderZoomChange(ReaderZoomLevel.SMALL, ReaderZoomLevel.SMALL), fromTranslation)
    }

    @Test
    fun unlinkedZoom_onlyChangesTheSliderThatMoved() {
        assertEquals(
            ReaderZoomChange(arabic = ReaderZoomLevel.MAXIMUM, translation = null),
            resolveReaderZoomChange(ReaderSizeSlider.ARABIC, ReaderZoomLevel.MAXIMUM, linked = false, hasTranslation = true),
        )
        assertEquals(
            ReaderZoomChange(arabic = null, translation = ReaderZoomLevel.COMPACT),
            resolveReaderZoomChange(ReaderSizeSlider.TRANSLATION, ReaderZoomLevel.COMPACT, linked = false, hasTranslation = true),
        )
    }

    @Test
    fun withoutTranslation_onlyArabicChanges_evenWhenLinked() {
        val change = resolveReaderZoomChange(ReaderSizeSlider.ARABIC, ReaderZoomLevel.ELDER, linked = true, hasTranslation = false)
        assertEquals(ReaderZoomLevel.ELDER, change.arabic)
        assertNull(change.translation)
    }

    @Test
    fun sliderValues_snapToZoomLevels_andClamp() {
        assertEquals(ReaderZoomLevel.COMPACT, zoomLevelForSliderValue(-3f))
        assertEquals(ReaderZoomLevel.STANDARD, zoomLevelForSliderValue(1.6f))
        assertEquals(ReaderZoomLevel.MAXIMUM, zoomLevelForSliderValue(99f))
        assertEquals("100%", ReaderZoomLevel.STANDARD.percentLabel())
        assertEquals("180%", ReaderZoomLevel.MAXIMUM.percentLabel())
    }

    @Test
    fun sheetLabels_matchSettingsWording() {
        assertEquals(listOf("Off", "English", "Urdu"), TranslationSelection.entries.map { it.shortLabel })
        assertEquals(listOf("IndoPak", "Uthmani"), ScriptType.entries.map { it.displayLabel })
    }

    @Test
    fun chromeVisibility_hidesWhenReadingForward_showsWhenScrollingBack_ignoresJitter() {
        assertEquals(false, readerChromeVisibilityForScroll(-40f))
        assertEquals(true, readerChromeVisibilityForScroll(40f))
        assertNull(readerChromeVisibilityForScroll(2f))
        assertNull(readerChromeVisibilityForScroll(-2f))
    }
}
