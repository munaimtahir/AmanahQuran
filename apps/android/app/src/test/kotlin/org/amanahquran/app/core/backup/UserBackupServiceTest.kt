package org.amanahquran.app.core.backup

import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.amanahquran.app.core.datastore.AmanahPreferencesDataSource
import org.amanahquran.app.core.datastore.amanahPreferencesDataSourceForFile
import org.amanahquran.app.core.model.BookmarkType
import org.amanahquran.app.core.model.ReminderSettings
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.repository.BookmarkCollectionRepositoryImpl
import org.amanahquran.app.core.repository.BookmarkRepositoryImpl
import org.amanahquran.app.core.repository.LastReadRepositoryImpl
import org.amanahquran.app.core.repository.LastReadState
import org.amanahquran.app.core.repository.ReaderSettingsRepositoryImpl
import org.amanahquran.app.core.repository.ReadingActivityRepositoryImpl
import org.amanahquran.app.core.repository.ReminderSettingsRepositoryImpl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class UserBackupServiceTest {
    private lateinit var dataSource: AmanahPreferencesDataSource
    private lateinit var bookmarks: BookmarkRepositoryImpl
    private lateinit var collections: BookmarkCollectionRepositoryImpl
    private lateinit var settings: ReaderSettingsRepositoryImpl
    private lateinit var lastRead: LastReadRepositoryImpl
    private lateinit var readingActivity: ReadingActivityRepositoryImpl
    private lateinit var reminderSettings: ReminderSettingsRepositoryImpl
    private lateinit var service: UserBackupService

    @Before
    fun setUp() {
        val tempFile = File(RuntimeEnvironment.getApplication().filesDir, "amanah-backup-svc-${System.nanoTime()}.preferences_pb")
        dataSource = amanahPreferencesDataSourceForFile(tempFile)
        bookmarks = BookmarkRepositoryImpl(dataSource)
        collections = BookmarkCollectionRepositoryImpl(dataSource)
        settings = ReaderSettingsRepositoryImpl(dataSource)
        lastRead = LastReadRepositoryImpl(dataSource)
        readingActivity = ReadingActivityRepositoryImpl(dataSource)
        reminderSettings = ReminderSettingsRepositoryImpl(dataSource)
        service = UserBackupService(
            context = RuntimeEnvironment.getApplication(),
            bookmarks = bookmarks,
            collections = collections,
            settings = settings,
            lastRead = lastRead,
            readingActivity = readingActivity,
            reminderSettings = reminderSettings,
        )
    }

    @Test
    fun exportSucceedsAndProducesParseableJson() = runTest {
        val json = service.encodeCurrent()
        val parsed = UserBackupCodec.validateAndParse(json)
        assertEquals(UserBackupCodec.CURRENT_VERSION, org.json.JSONObject(json).getInt("version"))
        assertTrue(parsed.bookmarks.isEmpty())
    }

    @Test
    fun validRestoreSucceedsAndBringsBackAllData() = runTest {
        val payload = UserBackupPayload(
            bookmarks = listOf(
                org.amanahquran.app.core.repository.BookmarkRecord(
                    1L, BookmarkType.AYAH, "2:255", 2, 255, null, null, 10L, 10L,
                ),
            ),
            collectionsJson = "[]",
            settings = org.amanahquran.app.core.repository.ReaderSettings(selectedScript = ScriptType.UTHMANI),
            lastRead = LastReadState("2:255", 2, 255, 42, 3, ScriptType.UTHMANI, 999L),
            readingActivity = listOf(
                org.amanahquran.app.core.model.DailyReadingActivity.startingSession(
                    LocalDate.of(2026, 8, 10), 150, setOf("2:1"), setOf(5), 1L,
                ),
            ),
            reminderSettings = ReminderSettings(enabled = true, hour = 7, minute = 15, repeatDays = setOf(DayOfWeek.MONDAY)),
        )

        service.restore(payload)

        assertEquals(1, bookmarks.getAllBookmarks().first().size)
        assertTrue(bookmarks.isAyahBookmarked("2:255"))
        assertEquals("2:255", lastRead.getLastRead().first()?.ayahKey)
        assertEquals(ScriptType.UTHMANI, settings.settings.first().selectedScript)
        assertEquals(1, readingActivity.observeAllActivity().first().size)
        assertTrue(reminderSettings.getSettings().enabled)
        assertEquals(7, reminderSettings.getSettings().hour)
    }

    @Test
    fun restoringReadingActivityReplacesRatherThanDuplicates() = runTest {
        readingActivity.recordSession(LocalDate.of(2026, 1, 1), 150, emptySet(), emptySet(), timestamp = 1L)
        val payload = UserBackupPayload(
            bookmarks = emptyList(),
            collectionsJson = "[]",
            settings = org.amanahquran.app.core.repository.ReaderSettings(),
            readingActivity = listOf(
                org.amanahquran.app.core.model.DailyReadingActivity.startingSession(
                    LocalDate.of(2026, 8, 10), 150, emptySet(), emptySet(), 1L,
                ),
            ),
        )

        service.restore(payload)

        val restored = readingActivity.observeAllActivity().first()
        assertEquals(1, restored.size) // old Jan 1 entry is gone, not merged alongside the restored one
        assertEquals(LocalDate.of(2026, 8, 10), restored.first().date)
    }

    @Test
    fun corruptedBackupFileIsRejectedBeforeAnyRestoreHappens() = runTest {
        bookmarks.addAyahBookmark("2:255")

        assertThrowsAny { UserBackupCodec.validateAndParse("{ not valid json") }

        // Existing state must be completely untouched since validation happens before restore().
        assertTrue(bookmarks.isAyahBookmarked("2:255"))
    }

    @Test
    fun incompatibleSchemaVersionIsRejected() {
        assertThrowsAny {
            UserBackupCodec.validateAndParse("{\"format\":\"amanah-quran-user-backup\",\"version\":999}")
        }
    }

    @Test
    fun failedRestoreRollsBackToPreRestoreSnapshotAndRearmsReminder() = runTest {
        bookmarks.addAyahBookmark("1:1")
        settings.setSelectedScript(ScriptType.INDOPAK)
        var reschedules = 0
        // A bookmark repository that fails partway through restore, to exercise the rollback path.
        val serviceThatFails = UserBackupService(
            context = RuntimeEnvironment.getApplication(),
            bookmarks = FailingBookmarkRepository(bookmarks),
            collections = collections,
            settings = settings,
            lastRead = lastRead,
            readingActivity = readingActivity,
            reminderSettings = reminderSettings,
            onReminderSettingsRestored = { reschedules++ },
        )

        val newPayload = UserBackupPayload(
            bookmarks = emptyList(),
            collectionsJson = "[]",
            settings = org.amanahquran.app.core.repository.ReaderSettings(selectedScript = ScriptType.UTHMANI),
        )

        var threw = false
        try {
            serviceThatFails.restore(newPayload)
        } catch (_: Exception) {
            threw = true
        }
        assertTrue("restore() should propagate the underlying failure", threw)

        // Rolled back: original script preference is restored, not left mid-way at UTHMANI.
        assertEquals(ScriptType.INDOPAK, settings.settings.first().selectedScript)
        assertTrue(reschedules >= 1) // reminder re-armed as part of the rollback
    }

    private fun assertThrowsAny(block: () -> Unit) {
        var threw = false
        try {
            block()
        } catch (_: Throwable) {
            threw = true
        }
        assertTrue("Expected an exception to be thrown", threw)
    }

    /**
     * Wraps a real BookmarkRepository but fails on its first write only -- simulating a
     * transient failure so the forward restore pass fails while the rollback pass (which
     * replays the same call) can still succeed and complete.
     */
    private class FailingBookmarkRepository(
        private val delegate: org.amanahquran.app.core.repository.BookmarkRepository,
    ) : org.amanahquran.app.core.repository.BookmarkRepository by delegate {
        private var callCount = 0
        override suspend fun replaceAllBookmarks(records: List<org.amanahquran.app.core.repository.BookmarkRecord>) {
            callCount++
            if (callCount == 1) throw IllegalStateException("Simulated write failure")
            delegate.replaceAllBookmarks(records)
        }
    }
}
