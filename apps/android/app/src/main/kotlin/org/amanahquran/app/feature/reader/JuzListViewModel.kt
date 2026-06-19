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
import org.amanahquran.app.core.model.JuzListItem
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.repository.QuranContentRepository
import org.amanahquran.app.core.repository.ReaderSettingsRepository
import org.amanahquran.app.core.repository.readerSettingsRepository

data class JuzListUiState(
    val isLoading: Boolean = true,
    val items: List<JuzListItem> = emptyList(),
    val errorMessage: String? = null,
    val selectedScript: ScriptType = ScriptType.INDOPAK,
)

class JuzListViewModel(
    private val repository: QuranContentRepository,
    private val settingsRepository: ReaderSettingsRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
) : ViewModel() {
    private val _uiState = MutableStateFlow(JuzListUiState())
    val uiState: StateFlow<JuzListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(dispatcher) {
            settingsRepository.settings.collectLatest { settings ->
                _uiState.update {
                    it.copy(selectedScript = settings.selectedScript)
                }
            }
        }
        load()
    }

    private fun load() {
        viewModelScope.launch(dispatcher) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { repository.getJuzList() }
                .onSuccess { items ->
                    _uiState.update {
                        it.copy(isLoading = false, items = items, errorMessage = null)
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(isLoading = false, items = emptyList(), errorMessage = throwable.message ?: "Unable to load Juz list.")
                    }
                }
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return JuzListViewModel(
                    repository = quranContentRepository(context),
                    settingsRepository = readerSettingsRepository(context),
                ) as T
            }
        }
    }
}
