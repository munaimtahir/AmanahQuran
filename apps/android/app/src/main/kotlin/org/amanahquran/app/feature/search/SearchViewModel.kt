package org.amanahquran.app.feature.search

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.repository.ReaderSettingsRepository
import org.amanahquran.app.core.repository.SearchRepository
import org.amanahquran.app.core.repository.SearchResultItem
import org.amanahquran.app.core.repository.SearchResultType
import org.amanahquran.app.core.repository.readerSettingsRepository
import org.amanahquran.app.feature.reader.quranContentRepository

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: List<SearchResultItem> = emptyList(),
    val errorMessage: String? = null,
    val selectedScript: ScriptType = ScriptType.INDOPAK,
    val arabicFontSizeSp: Float = 24f,
)

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val searchRepository: SearchRepository,
    private val settingsRepository: ReaderSettingsRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
) : ViewModel() {
    private val queryFlow = MutableStateFlow("")
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        observeSearch()
    }

    fun onQueryChanged(query: String) {
        queryFlow.value = query
        _uiState.update { it.copy(query = query) }
    }

    private fun observeSearch() {
        viewModelScope.launch(dispatcher) {
            combine(
                queryFlow.debounce(250).distinctUntilChanged(),
                settingsRepository.settings,
            ) { query, settings ->
                query to settings
            }.collectLatest { (query, settings) ->
                _uiState.update {
                    it.copy(
                        selectedScript = settings.selectedScript,
                        arabicFontSizeSp = settings.arabicFontSizeSp,
                    )
                }
                if (query.isBlank()) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            results = emptyList(),
                            errorMessage = null,
                        )
                    }
                    return@collectLatest
                }
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                runCatching {
                    searchRepository.search(query, settings.selectedScript)
                }.onSuccess { results ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            results = results,
                            errorMessage = null,
                        )
                    }
                }.onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            results = emptyList(),
                            errorMessage = throwable.message ?: "Unable to search.",
                        )
                    }
                }
            }
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SearchViewModel(
                    searchRepository = org.amanahquran.app.core.repository.SearchRepositoryImpl(
                        searchIndexDao = org.amanahquran.app.core.database.AmanahContentDatabaseProvider.getDatabase(context).searchIndexDao(),
                        quranTextDao = org.amanahquran.app.core.database.AmanahContentDatabaseProvider.getDatabase(context).quranTextDao(),
                        surahDao = org.amanahquran.app.core.database.AmanahContentDatabaseProvider.getDatabase(context).surahDao(),
                        ayahDao = org.amanahquran.app.core.database.AmanahContentDatabaseProvider.getDatabase(context).ayahDao(),
                    ),
                    settingsRepository = readerSettingsRepository(context),
                ) as T
            }
        }
    }
}
