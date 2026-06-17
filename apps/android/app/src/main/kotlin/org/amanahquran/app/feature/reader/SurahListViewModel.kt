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
import org.amanahquran.app.core.repository.QuranContentRepository

class SurahListViewModel(
    private val repository: QuranContentRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SurahListUiState())
    val uiState: StateFlow<SurahListUiState> = _uiState.asStateFlow()

    init {
        loadSurahs()
    }

    fun loadSurahs() {
        viewModelScope.launch(dispatcher) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                repository.getAllSurahs().map { surah ->
                    SurahListItem(
                        surahNumber = surah.number,
                        arabicName = surah.nameArabic,
                        simpleName = surah.nameSimple.ifBlank { "Surah ${surah.number}" },
                        ayahCount = surah.ayahCount,
                        revelationType = surah.revelationType,
                    )
                }
            }.onSuccess { surahs ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        surahs = surahs,
                        errorMessage = null,
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Unable to load Surahs.",
                    )
                }
            }
        }
    }

    fun openSurah(surahNumber: Int, navigateToSurah: (Int) -> Unit) {
        navigateToSurah(surahNumber)
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SurahListViewModel(quranContentRepository(context)) as T
            }
        }
    }
}
