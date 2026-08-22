package org.amanahquran.app.content.translation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TranslationImportContractTest {
    @Test
    fun translationIdentityUsesCanonicalAyahKey() {
        val row = TranslationAyahEntity(
            translationId = "TAHIR_QADRI_IRFAN_UR",
            ayahKey = "2:255",
            surahNumber = 2,
            ayahNumber = 255,
            displayText = "ترجمہ",
            availabilityStatus = TranslationAvailabilityStatus.TRANSLATED,
            normalizedSearchText = "ترجمہ",
        )
        assertEquals("TAHIR_QADRI_IRFAN_UR", row.translationId)
        assertEquals("2:255", row.ayahKey)
    }

    @Test
    fun translationDisplayAndSearchFieldsAreSeparate() {
        val row = TranslationAyahEntity(
            translationId = "TAHIR_QADRI_IRFAN_UR",
            ayahKey = "1:2",
            surahNumber = 1,
            ayahNumber = 2,
            displayText = "اصل متن",
            availabilityStatus = TranslationAvailabilityStatus.TRANSLATED,
            normalizedSearchText = "normalized",
        )
        assertNotEquals(row.displayText, row.normalizedSearchText)
    }

    @Test
    fun sourceMissingRowsCarryNoDisplayText() {
        val row = TranslationAyahEntity(
            translationId = "TAHIR_QADRI_MANIFEST_EN",
            ayahKey = "1:1",
            surahNumber = 1,
            ayahNumber = 1,
            displayText = null,
            availabilityStatus = TranslationAvailabilityStatus.SOURCE_MISSING,
            normalizedSearchText = null,
        )
        assertNull(row.displayText)
        assertEquals(TranslationAvailabilityStatus.SOURCE_MISSING, row.availabilityStatus)
    }

    @Test
    fun footnoteAssociationIsScopedToOneTranslationAndAyah() {
        val footnote = TranslationFootnoteEntity(
            translationId = "TAHIR_QADRI_MANIFEST_EN",
            ayahKey = "2:255",
            footnoteIndex = 0,
            marker = "1",
            footnoteText = "Explanatory note",
        )
        assertTrue(footnote.footnoteText.isNotBlank())
        assertEquals("2:255", footnote.ayahKey)
    }
}
