package org.amanahquran.app.feature.home

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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.repository.LastReadRepository
import org.amanahquran.app.core.repository.QuranContentRepository
import org.amanahquran.app.core.repository.ReaderSettingsRepository

data class HomeContinueReadingUiModel(
    val title: String,
    val subtitle: String,
    val previewText: String?,
    val surahNumber: Int,
    val ayahKey: String,
)

data class HomeUiState(
    val continueReading: HomeContinueReadingUiModel? = null,
    val showFirstLaunchMessage: Boolean = false,
)

class HomeViewModel(
    private val repository: QuranContentRepository,
    private val lastReadRepository: LastReadRepository,
    private val settingsRepository: ReaderSettingsRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeState()
    }

    fun dismissFirstLaunchMessage() {
        viewModelScope.launch(dispatcher) {
            settingsRepository.setFirstLaunchMessageDismissed(true)
        }
    }

    private fun observeState() {
        viewModelScope.launch(dispatcher) {
            combine(
                lastReadRepository.getLastRead(),
                settingsRepository.settings,
            ) { lastRead, settings ->
                lastRead to settings
            }.collectLatest { (lastRead, settings) ->
                val continueReading = lastRead?.let {
                    buildContinueReading(it.ayahKey, it.surahNumber, it.ayahNumber, settings.selectedScript)
                }
                _uiState.update {
                    it.copy(
                        continueReading = continueReading,
                        showFirstLaunchMessage = !settings.firstLaunchMessageDismissed,
                    )
                }
            }
        }
    }

    private suspend fun buildContinueReading(
        ayahKey: String,
        surahNumber: Int,
        ayahNumber: Int,
        scriptType: ScriptType,
    ): HomeContinueReadingUiModel? {
        val surah = repository.getSurahByNumber(surahNumber) ?: return null
        val display = repository.getAyahDisplay(ayahKey, scriptType.name)
        return HomeContinueReadingUiModel(
            title = "Continue: ${surah.nameSimple.ifBlank { "Surah $surahNumber" }} $surahNumber:$ayahNumber",
            subtitle = if (display?.displayText.isNullOrBlank()) {
                "Open last-read position"
            } else {
                "Resume from ${display?.displayText?.take(36)}"
            },
            previewText = display?.displayText,
            surahNumber = surahNumber,
            ayahKey = ayahKey,
        )
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(
                    repository = org.amanahquran.app.feature.reader.quranContentRepository(context),
                    lastReadRepository = org.amanahquran.app.core.repository.lastReadRepository(context),
                    settingsRepository = org.amanahquran.app.core.repository.readerSettingsRepository(context),
                ) as T
            }
        }
    }
}
