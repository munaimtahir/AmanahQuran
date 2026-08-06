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
import kotlinx.coroutines.flow.flowOf
import org.amanahquran.app.core.model.BookmarkType
import org.amanahquran.app.core.model.PageReferenceType
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.repository.BookmarkRecord
import org.amanahquran.app.core.repository.BookmarkRepository
import org.amanahquran.app.core.repository.BookmarkCollection
import org.amanahquran.app.core.repository.BookmarkCollectionRepository
import org.amanahquran.app.core.repository.bookmarkCollectionRepository
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
    val collections: List<BookmarkCollection> = emptyList(),
    val selectedCollectionId: String = "default",
)

class BookmarksViewModel(
    private val bookmarkRepository: BookmarkRepository,
    private val settingsRepository: ReaderSettingsRepository,
    private val quranContentRepository: QuranContentRepository,
    private val collectionRepository: BookmarkCollectionRepository = EmptyBookmarkCollectionRepository,
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

    fun selectCollection(id: String) = _uiState.update { it.copy(selectedCollectionId = id) }

    fun createCollection(name: String) {
        viewModelScope.launch(dispatcher) { collectionRepository.create(name) }
    }

    fun setBookmarkInCollection(bookmarkId: Long, collectionId: String, shouldBeInCollection: Boolean) {
        viewModelScope.launch(dispatcher) {
            if (shouldBeInCollection) {
                collectionRepository.addBookmark(collectionId, bookmarkId)
            } else {
                collectionRepository.removeBookmark(collectionId, bookmarkId)
            }
        }
    }

    private fun observeState() {
        viewModelScope.launch(dispatcher) {
            combine(
                bookmarkRepository.getAllBookmarks(),
                settingsRepository.settings,
                collectionRepository.observeCollections(),
            ) { bookmarks, settings, collections -> Triple(bookmarks, settings, collections) }.collectLatest { (bookmarks, settings, collections) ->
                val selectedId = _uiState.value.selectedCollectionId
                val selected = collections.firstOrNull { it.id == selectedId } ?: collections.firstOrNull()
                val visibleBookmarks = if (selected == null || selected.isDefault) bookmarks
                else bookmarks.filter { selected.bookmarkIds.contains(it.id) }
                _uiState.update { it.copy(isLoading = true, selectedScript = settings.selectedScript) }
                runCatching {
                    visibleBookmarks.mapNotNull { record ->
                        buildBookmarkItem(record, settings.selectedScript)
                    }
                }.onSuccess { items ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            items = items,
                            errorMessage = null,
                            selectedScript = settings.selectedScript,
                            collections = collections,
                            selectedCollectionId = selected?.id ?: "default",
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
                    collectionRepository = bookmarkCollectionRepository(context),
                ) as T
            }
        }
    }
}

private object EmptyBookmarkCollectionRepository : BookmarkCollectionRepository {
    override fun observeCollections() = flowOf(listOf(BookmarkCollection("default", "Default", emptySet(), 0L, 0L, true)))
    override suspend fun create(name: String): String? = null
    override suspend fun rename(id: String, name: String) = false
    override suspend fun delete(id: String) = false
    override suspend fun addBookmark(collectionId: String, bookmarkId: Long) = Unit
    override suspend fun removeBookmark(collectionId: String, bookmarkId: Long) = Unit
}

private fun PageReferenceType.displayLabel(): String = when (this) {
    PageReferenceType.INDOPAK -> "IndoPak"
    PageReferenceType.UTHMANI -> "Uthmani"
}
