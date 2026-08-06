package org.amanahquran.app.content.translation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TranslationImportContractTest {
    @Test
    fun translationIdentityUsesCanonicalAyahKey() {
        val row = TranslationAyahEntity(
            translationId = TranslationRepository.TRANSLATION_ID,
            ayahKey = "2:255",
            displayText = "ترجمہ",
            normalizedSearchText = "ترجمہ",
        )
        assertEquals("urdu_junagarhi", row.translationId)
        assertEquals("2:255", row.ayahKey)
    }

    @Test
    fun translationDisplayAndSearchFieldsAreSeparate() {
        val row = TranslationAyahEntity("urdu_junagarhi", "1:1", "اصل متن", "normalized")
        assertTrue(row.displayText != row.normalizedSearchText)
    }
}
