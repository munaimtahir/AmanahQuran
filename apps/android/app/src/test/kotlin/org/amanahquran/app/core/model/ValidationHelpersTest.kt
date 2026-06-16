package org.amanahquran.app.core.model

import org.junit.Assert.*
import org.junit.Test

class ValidationHelpersTest {

    @Test
    fun `isValidAyahKey accepts valid keys`() {
        assertTrue(ValidationHelpers.isValidAyahKey("2:255"))
        assertTrue(ValidationHelpers.isValidAyahKey("114:6"))
        assertTrue(ValidationHelpers.isValidAyahKey("1:1"))
    }

    @Test
    fun `isValidAyahKey rejects invalid keys`() {
        assertFalse(ValidationHelpers.isValidAyahKey("2"))
        assertFalse(ValidationHelpers.isValidAyahKey("2:abc"))
        assertFalse(ValidationHelpers.isValidAyahKey(":255"))
        assertFalse(ValidationHelpers.isValidAyahKey("2:"))
        assertFalse(ValidationHelpers.isValidAyahKey(""))
        assertFalse(ValidationHelpers.isValidAyahKey("0:1"))
    }

    @Test
    fun `isValidChecksum detects blank checksums`() {
        assertTrue(ValidationHelpers.isValidChecksum("some_hash"))
        assertFalse(ValidationHelpers.isValidChecksum(""))
        assertFalse(ValidationHelpers.isValidChecksum(null))
    }

    @Test
    fun `isValidBookmarkTarget validates correctly`() {
        // Ayah bookmark
        assertTrue(ValidationHelpers.isValidBookmarkTarget(BookmarkType.AYAH, "2:255", null))
        assertFalse(ValidationHelpers.isValidBookmarkTarget(BookmarkType.AYAH, "invalid", null))
        assertFalse(ValidationHelpers.isValidBookmarkTarget(BookmarkType.AYAH, null, null))

        // Page bookmark
        assertTrue(ValidationHelpers.isValidBookmarkTarget(BookmarkType.PAGE, null, 1))
        assertTrue(ValidationHelpers.isValidBookmarkTarget(BookmarkType.PAGE, null, 604))
        assertFalse(ValidationHelpers.isValidBookmarkTarget(BookmarkType.PAGE, null, 0))
        assertFalse(ValidationHelpers.isValidBookmarkTarget(BookmarkType.PAGE, null, -1))
        assertFalse(ValidationHelpers.isValidBookmarkTarget(BookmarkType.PAGE, null, null))
    }
}
