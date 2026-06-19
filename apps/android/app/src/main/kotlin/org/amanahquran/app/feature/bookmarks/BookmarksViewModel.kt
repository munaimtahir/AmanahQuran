package org.amanahquran.app.feature.bookmarks

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.amanahquran.app.core.model.BookmarkType
import org.amanahquran.app.core.model.PageReferenceType
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.repository.BookmarkRecord
import org.amanahquran.app.core.repository.BookmarkRepository
import org.amanahquran.app.core.repository.QuranContentRepository
import org.amanahquran.app.core.repository.ReaderSettingsRepository
import org.amanahquran.app.core.repository.bookmarkRepository
import org.amanahquran.app.core.repository.readerSettingsRepository
import org.amanahquran.app.feature.reader.quranContentRepository

data class BookmarkUiItem(
    val record: BookmarkRecord,
    val title: String,
    val subtitle: String,
    val previewText: String?,
    val createdLabel: String,
)

data class BookmarksUiState(
    val isLoading: Boolean = true,
    val items: List<BookmarkUiItem> = emptyList(),
    val errorMessage: String? = null,
    val selectedScript: ScriptType = ScriptType.INDOPAK,
)

class BookmarksViewModel(
    private val bookmarkRepository: BookmarkRepository,
    private val settingsRepository: ReaderSettingsRepository,
    private val quranContentRepository: QuranContentRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
) : ViewModel() {
    private val _uiState = MutableStateFlow(BookmarksUiState())
    val uiState: StateFlow<BookmarksUiState> = _uiState.asStateFlow()

    init {
        observeState()
    }

    fun removeBookmark(record: BookmarkRecord) {
        viewModelScope.launch(dispatcher) {
            when (record.bookmarkType) {
                BookmarkType.AYAH -> record.ayahKey?.let { bookmarkRepository.removeAyahBookmark(it) }
                BookmarkType.PAGE -> {
                    val pageNumber = record.pageNumber ?: return@launch
                    val pageReferenceType = record.pageReferenceType ?: PageReferenceType.INDOPAK
                    bookmarkRepository.removePageBookmark(pageNumber, pageReferenceType)
                }
            }
        }
    }

    private fun observeState() {
        viewModelScope.launch(dispatcher) {
            combine(
                bookmarkRepository.getAllBookmarks(),
                settingsRepository.settings,
            ) { bookmarks, settings -> bookmarks to settings }.collectLatest { (bookmarks, settings) ->
                _uiState.update { it.copy(isLoading = true, selectedScript = settings.selectedScript) }
                runCatching {
                    bookmarks.mapNotNull { record ->
                        buildBookmarkItem(record, settings.selectedScript)
                    }
                }.onSuccess { items ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            items = items,
                            errorMessage = null,
                            selectedScript = settings.selectedScript,
                        )
                    }
                }.onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            items = emptyList(),
                            errorMessage = throwable.message ?: "Unable to load bookmarks.",
                        )
                    }
                }
            }
        }
    }

    private suspend fun buildBookmarkItem(
        record: BookmarkRecord,
        scriptType: ScriptType,
    ): BookmarkUiItem? {
        val createdLabel = java.text.DateFormat.getDateInstance(java.text.DateFormat.MEDIUM)
            .format(java.util.Date(record.createdAt))
        return when (record.bookmarkType) {
            BookmarkType.AYAH -> {
                val ayahKey = record.ayahKey ?: return null
                val display = quranContentRepository.getAyahDisplay(ayahKey, scriptType.name) ?: return null
                val surah = quranContentRepository.getSurahByNumber(display.surahNumber)
                BookmarkUiItem(
                    record = record,
                    title = surah?.nameSimple?.takeIf { it.isNotBlank() } ?: "Surah ${display.surahNumber}",
                    subtitle = "${display.surahNumber}:${display.ayahNumber}",
                    previewText = display.displayText,
                    createdLabel = createdLabel,
                )
            }
            BookmarkType.PAGE -> {
                val pageNumber = record.pageNumber ?: return null
                val pageReferenceType = record.pageReferenceType ?: PageReferenceType.INDOPAK
                val firstAyahKey = quranContentRepository.getFirstAyahForPage(pageNumber, pageReferenceType) ?: return null
                val display = quranContentRepository.getAyahDisplay(firstAyahKey, scriptType.name) ?: return null
                val surah = quranContentRepository.getSurahByNumber(display.surahNumber)
                BookmarkUiItem(
                    record = record,
                    title = "Page $pageNumber",
                    subtitle = "${pageReferenceType.displayLabel()} · ${surah?.nameSimple?.takeIf { it.isNotBlank() } ?: "Surah ${display.surahNumber}"}",
                    previewText = display.displayText,
                    createdLabel = createdLabel,
                )
            }
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return BookmarksViewModel(
                    bookmarkRepository = bookmarkRepository(context),
                    settingsRepository = readerSettingsRepository(context),
                    quranContentRepository = quranContentRepository(context),
                ) as T
            }
        }
    }
}

private fun PageReferenceType.displayLabel(): String = when (this) {
    PageReferenceType.INDOPAK -> "IndoPak"
    PageReferenceType.UTHMANI -> "Uthmani"
}
