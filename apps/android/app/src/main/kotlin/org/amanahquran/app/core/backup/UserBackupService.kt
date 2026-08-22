package org.amanahquran.app.core.backup

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.flow.first
import org.amanahquran.app.core.repository.BookmarkCollectionRepository
import org.amanahquran.app.core.repository.BookmarkRepository
import org.amanahquran.app.core.repository.LastReadRepository
import org.amanahquran.app.core.repository.ReaderSettingsRepository
import org.amanahquran.app.core.model.ReminderSettings
import org.amanahquran.app.core.repository.ReadingActivityRepository
import org.amanahquran.app.core.repository.ReminderSettingsRepository

class UserBackupService(
    private val context: Context,
    private val bookmarks: BookmarkRepository,
    private val collections: BookmarkCollectionRepository,
    private val settings: ReaderSettingsRepository,
    private val lastRead: LastReadRepository,
    private val readingActivity: ReadingActivityRepository,
    private val reminderSettings: ReminderSettingsRepository,
    /** Re-arms WorkManager scheduling to match restored reminder settings; a no-op by default
     *  so core/backup doesn't need to depend on the feature/reminder package directly. */
    private val onReminderSettingsRestored: (ReminderSettings) -> Unit = {},
) {
    suspend fun encodeCurrent(): String = UserBackupCodec.encode(buildCurrentPayload())

    private suspend fun buildCurrentPayload(): UserBackupPayload = UserBackupPayload(
        bookmarks = bookmarks.getAllBookmarks().first(),
        collectionsJson = collections.snapshotJson(),
        settings = settings.settings.first(),
        lastRead = lastRead.getLastRead().first(),
        readingActivity = readingActivity.observeAllActivity().first(),
        reminderSettings = reminderSettings.getSettings(),
    )

    fun write(uri: Uri, json: String) {
        context.contentResolver.openOutputStream(uri, "wt").use { output ->
            requireNotNull(output) { "Unable to open backup destination" }
            output.writer(Charsets.UTF_8).use { it.write(json) }
        }
    }

    fun read(uri: Uri): UserBackupPayload {
        val json = context.contentResolver.openInputStream(uri).use { input ->
            requireNotNull(input) { "Unable to open backup file" }
            input.reader(Charsets.UTF_8).readText()
        }
        return UserBackupCodec.validateAndParse(json)
    }

    /**
     * Applies [payload] transactionally at the service level: a snapshot of current state is
     * taken first, and if any step throws mid-restore, every repository is rolled back to that
     * snapshot rather than left half-applied.
     */
    suspend fun restore(payload: UserBackupPayload) {
        val preRestoreSnapshot = buildCurrentPayload()
        runCatching { applyPayload(payload) }
            .onFailure { error ->
                runCatching { applyPayload(preRestoreSnapshot) }
                throw error
            }
    }

    private suspend fun applyPayload(payload: UserBackupPayload) {
        bookmarks.replaceAllBookmarks(payload.bookmarks)
        collections.replaceFromJson(payload.collectionsJson)
        settings.setSelectedScript(payload.settings.selectedScript)
        settings.setSelectedTheme(payload.settings.selectedTheme)
        settings.setArabicFontSize(payload.settings.arabicFontSizeSp)
        settings.setElderModeEnabled(payload.settings.elderModeEnabled)
        settings.setBookModeEnabled(payload.settings.bookModeEnabled)
        settings.setTranslationSelection(payload.settings.translationSelection)
        settings.setTranslationFontSize(payload.settings.translationFontSizeSp)
        settings.setArabicLineSpacing(payload.settings.arabicLineSpacingMultiplier)
        settings.setReaderHorizontalPadding(payload.settings.readerHorizontalPaddingDp)
        settings.setZoomLevel(org.amanahquran.app.core.model.ScriptType.INDOPAK, elderMode = false, payload.settings.indoPakZoomLevel)
        settings.setZoomLevel(org.amanahquran.app.core.model.ScriptType.UTHMANI, elderMode = false, payload.settings.uthmaniZoomLevel)
        settings.setZoomLevel(org.amanahquran.app.core.model.ScriptType.INDOPAK, elderMode = true, payload.settings.indoPakElderZoomLevel)
        settings.setZoomLevel(org.amanahquran.app.core.model.ScriptType.UTHMANI, elderMode = true, payload.settings.uthmaniElderZoomLevel)
        settings.setAutoScrollPace(payload.settings.autoScrollPace)
        settings.setPinchToResizeEnabled(payload.settings.pinchToResizeEnabled)
        settings.setReaderContentMode(payload.settings.readerContentMode)
        settings.setTranslationZoomLevel(payload.settings.translationZoomLevel)
        settings.setLinkedZoomEnabled(payload.settings.linkedZoomEnabled)
        settings.setReaderHeaderFormat(payload.settings.readerHeaderFormat)
        settings.setKeepScreenAwakeEnabled(payload.settings.keepScreenAwakeEnabled)
        settings.setFullScreenReadingDefault(payload.settings.fullScreenReadingDefault)
        payload.lastRead?.let { lastRead.saveLastRead(it) }
        // Reading activity/streak history is derived entirely from this list, so a full replace
        // (not a merge) both restores past activity and guarantees no duplicate day entries.
        readingActivity.replaceAllActivity(payload.readingActivity)
        reminderSettings.update { payload.reminderSettings }
        onReminderSettingsRestored(payload.reminderSettings)
    }
}
