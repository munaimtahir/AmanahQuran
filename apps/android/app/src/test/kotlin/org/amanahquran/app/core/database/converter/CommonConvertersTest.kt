package org.amanahquran.app.core.database.converter

import org.amanahquran.app.core.model.*
import org.junit.Assert.assertEquals
import org.junit.Test

class CommonConvertersTest {
    private val converters = CommonConverters()

    @Test
    fun `ScriptType round trip`() {
        val original = ScriptType.INDOPAK
        val converted = converters.fromScriptType(original)
        assertEquals(original, converters.toScriptType(converted))
    }

    @Test
    fun `BookmarkType round trip`() {
        val original = BookmarkType.PAGE
        val converted = converters.fromBookmarkType(original)
        assertEquals(original, converters.toBookmarkType(converted))
    }

    @Test
    fun `ValidationStatus round trip`() {
        val original = ValidationStatus.PASSED
        val converted = converters.fromValidationStatus(original)
        assertEquals(original, converters.toValidationStatus(converted))
    }

    @Test
    fun `ReviewerStatus round trip`() {
        val original = ReviewerStatus.REVIEWED
        val converted = converters.fromReviewerStatus(original)
        assertEquals(original, converters.toReviewerStatus(converted))
    }

    @Test
    fun `ContentType round trip`() {
        val original = ContentType.QURAN_TEXT
        val converted = converters.fromContentType(original)
        assertEquals(original, converters.toContentType(converted))
    }

    @Test
    fun `ValidationSeverity round trip`() {
        val original = ValidationSeverity.CRITICAL
        val converted = converters.fromValidationSeverity(original)
        assertEquals(original, converters.toValidationSeverity(converted))
    }
}
