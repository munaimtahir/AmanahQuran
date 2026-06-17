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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.repository.QuranContentRepository

class ReaderViewModel(
    private val repository: QuranContentRepository,
    private val initialSurahNumber: Int,
    initialScript: ScriptType = ScriptType.INDOPAK,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        ReaderUiState(
            selectedScript = initialScript,
            surahNumber = initialSurahNumber,
        ),
    )
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    init {
        loadSurah(initialSurahNumber, initialScript)
    }

    fun selectScript(scriptType: ScriptType) {
        val current = _uiState.value
        if (current.selectedScript == scriptType && current.ayahs.isNotEmpty()) return
        loadSurah(current.surahNumber, scriptType)
    }

    fun loadSurah(surahNumber: Int, scriptType: ScriptType = _uiState.value.selectedScript) {
        viewModelScope.launch(dispatcher) {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    selectedScript = scriptType,
                    surahNumber = surahNumber,
                    errorMessage = null,
                )
            }

            runCatching {
                val surah = repository.getSurahByNumber(surahNumber)
                    ?: error("Surah $surahNumber was not found.")
                val ayahs = repository.getAyahsForSurah(surahNumber, scriptType.name)
                    .map { ayah ->
                        ReaderAyahUiModel(
                            ayahKey = ayah.ayahKey,
                            surahNumber = ayah.surahNumber,
                            ayahNumber = ayah.ayahNumber,
                            displayText = ayah.displayText,
                            scriptType = scriptType,
                        )
                    }
                surah to ayahs
            }.onSuccess { (surah, ayahs) ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        surahName = surah.nameSimple.ifBlank { "Surah ${surah.number}" },
                        ayahs = ayahs,
                        errorMessage = null,
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        ayahs = emptyList(),
                        errorMessage = throwable.message ?: "Unable to load Surah.",
                    )
                }
            }
        }
    }

    companion object {
        fun factory(
            context: Context,
            surahNumber: Int,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ReaderViewModel(
                    repository = quranContentRepository(context),
                    initialSurahNumber = surahNumber,
                ) as T
            }
        }
    }
}
