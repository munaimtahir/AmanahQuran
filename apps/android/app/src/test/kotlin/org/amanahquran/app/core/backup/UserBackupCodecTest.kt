package org.amanahquran.app.core.backup

import java.time.DayOfWeek
import java.time.LocalDate
import org.amanahquran.app.core.model.BookmarkType
import org.amanahquran.app.core.model.DailyReadingActivity
import org.amanahquran.app.core.model.PageReferenceType
import org.amanahquran.app.core.model.ReaderHeaderFormat
import org.amanahquran.app.core.model.ReminderSettings
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.repository.BookmarkRecord
import org.amanahquran.app.core.repository.ReaderSettings
import org.amanahquran.app.core.theme.ThemeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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

    @Test(expected = org.json.JSONException::class)
    fun rejectsCorruptedJson() {
        UserBackupCodec.validateAndParse("{ this is not valid json ")
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsWrongFormatIdentifier() {
        UserBackupCodec.validateAndParse("{\"format\":\"some-other-app-backup\",\"version\":1}")
    }

    @Test
    fun roundTripPreservesReadingActivityAndReminderSettings() {
        val payload = UserBackupPayload(
            bookmarks = emptyList(),
            collectionsJson = "[]",
            settings = ReaderSettings(),
            readingActivity = listOf(
                DailyReadingActivity(
                    date = LocalDate.of(2026, 8, 10),
                    readingDurationSeconds = 150,
                    ayahKeysRead = setOf("2:1", "2:2"),
                    pagesRead = setOf(5),
                    firstReadingTimestamp = 1000L,
                    lastReadingTimestamp = 2000L,
                ),
            ),
            reminderSettings = ReminderSettings(
                enabled = true,
                hour = 6,
                minute = 30,
                repeatDays = setOf(DayOfWeek.MONDAY, DayOfWeek.FRIDAY),
                smartReminderEnabled = false,
            ),
        )

        val restored = UserBackupCodec.validateAndParse(UserBackupCodec.encode(payload))

        assertEquals(1, restored.readingActivity.size)
        assertEquals(LocalDate.of(2026, 8, 10), restored.readingActivity.first().date)
        assertEquals(2, restored.readingActivity.first().uniqueAyahsRead)
        assertTrue(restored.reminderSettings.enabled)
        assertEquals(6, restored.reminderSettings.hour)
        assertEquals(setOf(DayOfWeek.MONDAY, DayOfWeek.FRIDAY), restored.reminderSettings.repeatDays)
        assertEquals(false, restored.reminderSettings.smartReminderEnabled)
    }

    @Test
    fun roundTripPreservesFullReaderSettingsIncludingHeaderFormat() {
        val payload = UserBackupPayload(
            bookmarks = emptyList(),
            collectionsJson = "[]",
            settings = ReaderSettings(readerHeaderFormat = ReaderHeaderFormat.SURAH_JUZ_PAGE, keepScreenAwakeEnabled = true, fullScreenReadingDefault = false),
        )

        val restored = UserBackupCodec.validateAndParse(UserBackupCodec.encode(payload))

        assertEquals(ReaderHeaderFormat.SURAH_JUZ_PAGE, restored.settings.readerHeaderFormat)
        assertTrue(restored.settings.keepScreenAwakeEnabled)
        assertEquals(false, restored.settings.fullScreenReadingDefault)
    }

    @Test
    fun version1BackupWithoutNewFieldsStillParsesWithSafeDefaults() {
        // Simulates a backup exported before reading activity/reminder/header-format existed.
        val v1Json = JSONObjectOf(
            "format" to "amanah-quran-user-backup",
            "version" to 1,
            "bookmarks" to org.json.JSONArray(),
            "collections" to org.json.JSONArray(),
            "settings" to JSONObjectOf("script" to "UTHMANI", "theme" to "SEPIA"),
        )

        val restored = UserBackupCodec.validateAndParse(v1Json.toString())

        assertEquals(ScriptType.UTHMANI, restored.settings.selectedScript)
        assertEquals(ReaderHeaderFormat.SURAH_PAGE, restored.settings.readerHeaderFormat) // safe default
        assertTrue(restored.readingActivity.isEmpty())
        assertEquals(false, restored.reminderSettings.enabled)
    }

    private fun JSONObjectOf(vararg pairs: Pair<String, Any>): org.json.JSONObject =
        org.json.JSONObject().apply { pairs.forEach { (key, value) -> put(key, value) } }
}
