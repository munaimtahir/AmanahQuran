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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.amanahquran.app.core.model.PageListItem
import org.amanahquran.app.core.model.PageReferenceType
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.repository.QuranContentRepository
import org.amanahquran.app.core.repository.ReaderSettingsRepository
import org.amanahquran.app.core.repository.readerSettingsRepository

data class PageListUiState(
    val isLoading: Boolean = true,
    val pageReferenceType: PageReferenceType = PageReferenceType.INDOPAK,
    val items: List<PageListItem> = emptyList(),
    val errorMessage: String? = null,
    val selectedScript: ScriptType = ScriptType.INDOPAK,
)

class PageListViewModel(
    private val repository: QuranContentRepository,
    private val settingsRepository: ReaderSettingsRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PageListUiState())
    val uiState: StateFlow<PageListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(dispatcher) {
            settingsRepository.settings.collectLatest { settings ->
                val defaultReferenceType = settings.selectedScript.toPageReferenceType()
                _uiState.update { state ->
                    state.copy(
                        selectedScript = settings.selectedScript,
                        pageReferenceType = state.pageReferenceType.takeIf { state.items.isNotEmpty() } ?: defaultReferenceType,
                    )
                }
                load(_uiState.value.pageReferenceType.takeIf { _uiState.value.items.isNotEmpty() } ?: defaultReferenceType)
            }
        }
    }

    fun selectPageReferenceType(pageReferenceType: PageReferenceType) {
        load(pageReferenceType)
    }

    private fun load(pageReferenceType: PageReferenceType) {
        viewModelScope.launch(dispatcher) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, pageReferenceType = pageReferenceType) }
            runCatching { repository.getPageList(pageReferenceType) }
                .onSuccess { items ->
                    _uiState.update {
                        it.copy(isLoading = false, items = items, errorMessage = null, pageReferenceType = pageReferenceType)
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(isLoading = false, items = emptyList(), errorMessage = throwable.message ?: "Unable to load page list.")
                    }
                }
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PageListViewModel(
                    repository = quranContentRepository(context),
                    settingsRepository = readerSettingsRepository(context),
                ) as T
            }
        }
    }
}

private fun ScriptType.toPageReferenceType(): PageReferenceType = when (this) {
    ScriptType.INDOPAK -> PageReferenceType.INDOPAK
    ScriptType.UTHMANI -> PageReferenceType.UTHMANI
}
