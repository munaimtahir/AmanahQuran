package org.amanahquran.app.core.backup

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.flow.first
import org.amanahquran.app.core.repository.BookmarkCollectionRepository
import org.amanahquran.app.core.repository.BookmarkRepository
import org.amanahquran.app.core.repository.LastReadRepository
import org.amanahquran.app.core.repository.ReaderSettingsRepository

class UserBackupService(
    private val context: Context,
    private val bookmarks: BookmarkRepository,
    private val collections: BookmarkCollectionRepository,
    private val settings: ReaderSettingsRepository,
    private val lastRead: LastReadRepository,
) {
    suspend fun encodeCurrent(): String = UserBackupCodec.encode(
        UserBackupPayload(
            bookmarks = bookmarks.getAllBookmarks().first(),
            collectionsJson = collections.snapshotJson(),
            settings = settings.settings.first(),
            lastRead = lastRead.getLastRead().first(),
        ),
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

    suspend fun restore(payload: UserBackupPayload) {
        bookmarks.replaceAllBookmarks(payload.bookmarks)
        collections.replaceFromJson(payload.collectionsJson)
        settings.setSelectedScript(payload.settings.selectedScript)
        settings.setSelectedTheme(payload.settings.selectedTheme)
        settings.setArabicFontSize(payload.settings.arabicFontSizeSp)
        settings.setElderModeEnabled(payload.settings.elderModeEnabled)
        settings.setBookModeEnabled(payload.settings.bookModeEnabled)
        settings.setTranslationEnabled(payload.settings.translationEnabled)
        settings.setTranslationFontSize(payload.settings.translationFontSizeSp)
        settings.setArabicLineSpacing(payload.settings.arabicLineSpacingMultiplier)
        settings.setReaderHorizontalPadding(payload.settings.readerHorizontalPaddingDp)
        payload.lastRead?.let { lastRead.saveLastRead(it) }
    }
}
