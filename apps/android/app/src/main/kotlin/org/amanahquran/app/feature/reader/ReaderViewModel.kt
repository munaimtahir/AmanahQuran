package org.amanahquran.app.feature.reader

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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.amanahquran.app.core.model.ReaderOpenMode
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.repository.BookmarkRepository
import org.amanahquran.app.core.repository.LastReadRepository
import org.amanahquran.app.core.repository.LastReadState
import org.amanahquran.app.core.repository.QuranContentRepository
import org.amanahquran.app.core.repository.ReaderSettingsRepository
import org.amanahquran.app.core.model.BookmarkType
import org.amanahquran.app.core.repository.bookmarkRepository
import org.amanahquran.app.core.repository.lastReadRepository
import org.amanahquran.app.core.repository.readerSettingsRepository

class ReaderViewModel(
    private val repository: QuranContentRepository,
    private val settingsRepository: ReaderSettingsRepository,
    private val lastReadRepository: LastReadRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val initialOpenMode: ReaderOpenMode,
    private val initialAyahKey: String? = null,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        ReaderUiState(
            openMode = initialOpenMode,
            modeTitle = initialOpenMode.displayTitle(),
        ),
    )
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(dispatcher) {
            val initialSettings = settingsRepository.settings.first()
            _uiState.update {
                it.copy(
                    selectedScript = initialSettings.selectedScript,
                    arabicFontSizeSp = initialSettings.arabicFontSizeSp,
                    elderModeEnabled = initialSettings.elderModeEnabled,
                )
            }
            observeSettings()
            observeBookmarks()
            loadOpenMode(initialOpenMode, initialSettings.selectedScript, initialAyahKey)
        }
    }

    fun selectScript(scriptType: ScriptType) {
        viewModelScope.launch(dispatcher) {
            settingsRepository.setSelectedScript(scriptType)
        }
    }

    fun selectAyah(ayahKey: String) {
        val current = _uiState.value
        val selectedAyah = current.ayahs.firstOrNull { it.ayahKey == ayahKey } ?: return
        _uiState.update {
            it.copy(selectedAyahKey = ayahKey, ayahs = updateAyahSelection(it.ayahs, ayahKey))
        }
        viewModelScope.launch(dispatcher) {
            val display = repository.getAyahDisplay(selectedAyah.ayahKey, current.selectedScript.name) ?: return@launch
            lastReadRepository.saveLastRead(
                LastReadState(
                    ayahKey = display.ayahKey,
                    surahNumber = display.surahNumber,
                    ayahNumber = display.ayahNumber,
                    pageNumber = display.pageNumber,
                    juzNumber = display.juzNumber,
                    scriptType = current.selectedScript,
                    updatedAt = System.currentTimeMillis(),
                ),
            )
        }
    }

    fun toggleBookmark(ayahKey: String) {
        viewModelScope.launch(dispatcher) {
            bookmarkRepository.toggleAyahBookmark(ayahKey)
        }
    }

    fun toggleCurrentPageBookmark() {
        val openMode = _uiState.value.openMode
        if (openMode !is ReaderOpenMode.Page) return
        viewModelScope.launch(dispatcher) {
            bookmarkRepository.togglePageBookmark(openMode.pageNumber, openMode.pageReferenceType)
        }
    }

    fun loadOpenMode(
        openMode: ReaderOpenMode = _uiState.value.openMode,
        scriptType: ScriptType = _uiState.value.selectedScript,
        selectedAyahKey: String? = _uiState.value.selectedAyahKey,
    ) {
        viewModelScope.launch(dispatcher) {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    selectedScript = scriptType,
                    openMode = openMode,
                    modeTitle = openMode.displayTitle(),
                    errorMessage = null,
                )
            }

            runCatching {
                val ayahs = repository.getReaderAyahs(openMode, scriptType.name).map { ayah ->
                    ReaderAyahUiModel(
                        ayahKey = ayah.ayahKey,
                        surahNumber = ayah.surahNumber,
                        ayahNumber = ayah.ayahNumber,
                        displayText = ayah.displayText,
                        scriptType = scriptType,
                    )
                }
                val displaySurahNumber = when (openMode) {
                    is ReaderOpenMode.Surah -> openMode.surahNumber
                    is ReaderOpenMode.Page -> ayahs.firstOrNull()?.surahNumber ?: 1
                    is ReaderOpenMode.Juz -> ayahs.firstOrNull()?.surahNumber ?: 1
                    is ReaderOpenMode.AyahTarget -> openMode.surahNumber
                }
                val surah = repository.getSurahByNumber(displaySurahNumber)
                surah to ayahs
            }.onSuccess { (surah, ayahs) ->
                val selectedKey = selectedAyahKey ?: ayahs.firstOrNull()?.ayahKey
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        surahNumber = surah?.number ?: it.surahNumber,
                        surahName = when (val mode = it.openMode) {
                            is ReaderOpenMode.Surah -> surah?.nameSimple?.ifBlank { "Surah ${mode.surahNumber}" } ?: "Surah ${mode.surahNumber}"
                            is ReaderOpenMode.Page -> "Page ${mode.pageNumber}"
                            is ReaderOpenMode.Juz -> "Juz ${mode.juzNumber}"
                            is ReaderOpenMode.AyahTarget -> surah?.nameSimple?.ifBlank { "Surah ${mode.surahNumber}" } ?: "Surah ${mode.surahNumber}"
                        },
                        ayahs = updateAyahs(ayahs, selectedKey),
                        selectedAyahKey = selectedKey,
                        errorMessage = null,
                    )
                }
                persistLastRead(selectedKey, scriptType)
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        ayahs = emptyList(),
                        errorMessage = throwable.message ?: "Unable to load reader content.",
                    )
                }
            }
        }
    }

    private fun observeSettings() {
        viewModelScope.launch(dispatcher) {
            settingsRepository.settings.collectLatest { settings ->
                val current = _uiState.value
                val scriptChanged = current.selectedScript != settings.selectedScript
                _uiState.update {
                    it.copy(
                        selectedScript = settings.selectedScript,
                        arabicFontSizeSp = settings.arabicFontSizeSp,
                        elderModeEnabled = settings.elderModeEnabled,
                    )
                }
                if (scriptChanged) {
                    loadOpenMode(current.openMode, settings.selectedScript, current.selectedAyahKey)
                }
            }
        }
    }

    private fun observeBookmarks() {
        viewModelScope.launch(dispatcher) {
            bookmarkRepository.getAllBookmarks().collectLatest { bookmarks ->
                val bookmarkedAyahKeys = bookmarks.mapNotNull { it.ayahKey }.toSet()
                val currentPage = _uiState.value.openMode as? ReaderOpenMode.Page
                _uiState.update { state ->
                    state.copy(
                        ayahs = state.ayahs.map { ayah ->
                            ayah.copy(
                                isBookmarked = ayah.ayahKey in bookmarkedAyahKeys,
                                isSelected = state.selectedAyahKey == ayah.ayahKey,
                            )
                        },
                        isPageBookmarked = currentPage?.let {
                            bookmarks.any { record ->
                                record.bookmarkType == BookmarkType.PAGE &&
                                    record.pageNumber == it.pageNumber &&
                                    record.pageReferenceType == it.pageReferenceType
                            }
                        } ?: false,
                    )
                }
            }
        }
    }

    private fun persistLastRead(
        selectedAyahKey: String?,
        scriptType: ScriptType,
    ) {
        if (selectedAyahKey.isNullOrBlank()) return
        viewModelScope.launch(dispatcher) {
            val ayah = repository.getAyahDisplay(selectedAyahKey, scriptType.name) ?: return@launch
            lastReadRepository.saveLastRead(
                LastReadState(
                    ayahKey = ayah.ayahKey,
                    surahNumber = ayah.surahNumber,
                    ayahNumber = ayah.ayahNumber,
                    pageNumber = ayah.pageNumber,
                    juzNumber = ayah.juzNumber,
                    scriptType = scriptType,
                    updatedAt = System.currentTimeMillis(),
                ),
            )
        }
    }

    private fun updateAyahs(
        ayahs: List<ReaderAyahUiModel>,
        selectedAyahKey: String?,
    ): List<ReaderAyahUiModel> {
        return ayahs.map { ayah ->
            ayah.copy(
                isSelected = selectedAyahKey == ayah.ayahKey,
                isBookmarked = _uiState.value.ayahs.firstOrNull { it.ayahKey == ayah.ayahKey }?.isBookmarked == true,
            )
        }
    }

    private fun updateAyahSelection(
        ayahs: List<ReaderAyahUiModel>,
        selectedAyahKey: String?,
    ): List<ReaderAyahUiModel> {
        return ayahs.map { ayah ->
            ayah.copy(isSelected = selectedAyahKey == ayah.ayahKey)
        }
    }

    private fun ReaderOpenMode.displayTitle(): String {
        return when (this) {
            is ReaderOpenMode.Surah -> "Surah $surahNumber"
            is ReaderOpenMode.Page -> "Page $pageNumber"
            is ReaderOpenMode.Juz -> "Juz $juzNumber"
            is ReaderOpenMode.AyahTarget -> "Surah $surahNumber"
        }
    }

    companion object {
        fun factory(
            context: Context,
            surahNumber: Int,
            initialAyahKey: String? = null,
        ): ViewModelProvider.Factory = factory(
            context = context,
            openMode = ReaderOpenMode.Surah(surahNumber),
            initialAyahKey = initialAyahKey,
        )

        fun factory(
            context: Context,
            openMode: ReaderOpenMode,
            initialAyahKey: String? = null,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ReaderViewModel(
                    repository = quranContentRepository(context),
                    settingsRepository = readerSettingsRepository(context),
                    lastReadRepository = lastReadRepository(context),
                    bookmarkRepository = bookmarkRepository(context),
                    initialOpenMode = openMode,
                    initialAyahKey = initialAyahKey,
                ) as T
            }
        }
    }
}
