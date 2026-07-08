package org.amanahquran.app.feature.reader.mushaf

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
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.repository.MushafRepository
import org.amanahquran.app.core.repository.mushafRepository
import org.amanahquran.app.core.repository.LastReadRepository
import org.amanahquran.app.core.repository.LastReadState
import org.amanahquran.app.core.repository.lastReadRepository
import org.amanahquran.app.core.repository.ReaderSettingsRepository
import org.amanahquran.app.core.repository.readerSettingsRepository
import org.amanahquran.app.core.repository.MushafPageUi

class MushafReaderViewModel(
    private val repository: MushafRepository,
    private val settingsRepository: ReaderSettingsRepository,
    private val lastReadRepository: LastReadRepository,
    private val initialPageNumber: Int,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MushafReaderUiState(
            pageNumber = initialPageNumber
        )
    )
    val uiState: StateFlow<MushafReaderUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(dispatcher) {
            // First run data-import initialize if needed
            repository.initializePrototypeDataIfNeeded()

            val settings = settingsRepository.settings.first()
            _uiState.update {
                it.copy(
                    scriptType = settings.selectedScript,
                    fontScale = settings.arabicFontSizeSp / 24f,
                    elderModeEnabled = settings.elderModeEnabled,
                    bookModeEnabled = settings.bookModeEnabled
                )
            }
            observeSettings()
            loadPage(_uiState.value.pageNumber)
        }
    }

    private fun observeSettings() {
        viewModelScope.launch(dispatcher) {
            settingsRepository.settings.collectLatest { settings ->
                val current = _uiState.value
                val scriptChanged = current.scriptType != settings.selectedScript
                _uiState.update {
                    it.copy(
                        scriptType = settings.selectedScript,
                        fontScale = settings.arabicFontSizeSp / 24f,
                        elderModeEnabled = settings.elderModeEnabled,
                        bookModeEnabled = settings.bookModeEnabled
                    )
                }
                if (scriptChanged) {
                    loadPage(current.pageNumber)
                }
            }
        }
    }

    fun loadPage(pageNumber: Int) {
        val script = _uiState.value.scriptType
        val maxPage = if (script == ScriptType.UTHMANI) 604 else 559
        val validatedPage = pageNumber.coerceIn(1, maxPage)

        _uiState.update {
            it.copy(
                isLoading = true,
                pageNumber = validatedPage,
                pageCount = maxPage,
                errorMessage = null
            )
        }

        viewModelScope.launch(dispatcher) {
            runCatching {
                repository.getMushafPage(validatedPage, script)
            }.onSuccess { (pageUi, linesUi) ->
                val bookmarked = repository.isPageBookmarked(validatedPage, script)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        page = pageUi,
                        lines = linesUi,
                        isPageBookmarked = bookmarked
                    )
                }
                saveLastRead(pageUi, script)
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Failed to load page $validatedPage"
                    )
                }
            }
        }
    }

    fun goToPreviousPage() {
        val currentPage = _uiState.value.pageNumber
        if (currentPage > 1) {
            loadPage(currentPage - 1)
        }
    }

    fun goToNextPage() {
        val currentPage = _uiState.value.pageNumber
        val maxPage = _uiState.value.pageCount
        if (currentPage < maxPage) {
            loadPage(currentPage + 1)
        }
    }

    fun togglePageBookmark() {
        val pageNum = _uiState.value.pageNumber
        val script = _uiState.value.scriptType
        viewModelScope.launch(dispatcher) {
            val isBookmarked = repository.togglePageBookmark(pageNum, script)
            _uiState.update {
                it.copy(isPageBookmarked = isBookmarked)
            }
        }
    }

    fun setFontScale(scale: Float) {
        _uiState.update {
            it.copy(fontScale = scale.coerceIn(0.7f, 2.5f))
        }
        viewModelScope.launch(dispatcher) {
            settingsRepository.setArabicFontSize(scale * 24f)
        }
    }

    fun toggleFullScreen() {
        _uiState.update {
            it.copy(isFullScreen = !it.isFullScreen)
        }
    }

    private fun saveLastRead(page: MushafPageUi, scriptType: ScriptType) {
        val firstKey = page.firstAyahKey ?: "1:1"
        val surahNum = firstKey.substringBefore(":").toIntOrNull() ?: 1
        val ayahNum = firstKey.substringAfter(":").toIntOrNull() ?: 1
        viewModelScope.launch(dispatcher) {
            lastReadRepository.saveLastRead(
                ayahKey = firstKey,
                surahNumber = surahNum,
                ayahNumber = ayahNum,
                juzNumber = page.juzNumber ?: 1,
                pageNumber = page.pageNumber,
                scriptType = scriptType
            )
        }
    }

    fun mushafRepositoryInstance(): MushafRepository = repository


    companion object {
        fun factory(
            context: Context,
            initialPageNumber: Int
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val appContext = context.applicationContext
                return MushafReaderViewModel(
                    repository = mushafRepository(appContext),
                    settingsRepository = readerSettingsRepository(appContext),
                    lastReadRepository = lastReadRepository(appContext),
                    initialPageNumber = initialPageNumber
                ) as T
            }
        }
    }
}
