package org.amanahquran.app.core.backup

import org.amanahquran.app.core.model.BookmarkType
import org.amanahquran.app.core.model.PageReferenceType
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.repository.BookmarkRecord
import org.amanahquran.app.core.repository.ReaderSettings
import org.amanahquran.app.core.theme.ThemeMode
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], manifest = Config.NONE)
class UserBackupCodecTest {
    @Test
    fun roundTripPreservesCanonicalBookmark() {
        val payload = UserBackupPayload(
            bookmarks = listOf(
                BookmarkRecord(1L, BookmarkType.AYAH, "2:255", 2, 255, null, null, 10L, 10L),
                BookmarkRecord(2L, BookmarkType.PAGE, null, null, null, 42, PageReferenceType.UTHMANI, 11L, 11L),
            ),
            collectionsJson = "[]",
            settings = ReaderSettings(selectedScript = ScriptType.UTHMANI, selectedTheme = ThemeMode.SEPIA, translationEnabled = true, translationFontSizeSp = 22f),
        )
        val restored = UserBackupCodec.validateAndParse(UserBackupCodec.encode(payload))
        assertEquals("2:255", restored.bookmarks.first().ayahKey)
        assertEquals(BookmarkType.AYAH, restored.bookmarks.first().bookmarkType)
        assertEquals(PageReferenceType.UTHMANI, restored.bookmarks[1].pageReferenceType)
        assertEquals(ScriptType.UTHMANI, restored.settings.selectedScript)
        assertEquals(ThemeMode.SEPIA, restored.settings.selectedTheme)
        assertEquals(true, restored.settings.translationEnabled)
        assertEquals(22f, restored.settings.translationFontSizeSp)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsUnsupportedBackupVersion() {
        UserBackupCodec.validateAndParse("{\"format\":\"amanah-quran-user-backup\",\"version\":99}")
    }
}
