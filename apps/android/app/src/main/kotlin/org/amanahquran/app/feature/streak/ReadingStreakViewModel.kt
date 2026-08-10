package org.amanahquran.app.feature.streak

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.amanahquran.app.core.model.StreakSummary
import org.amanahquran.app.core.repository.ReadingActivityRepository
import org.amanahquran.app.core.repository.readingActivityRepository

data class DayHistoryUiModel(
    val date: LocalDate,
    val isToday: Boolean,
    val qualified: Boolean,
)

data class ReadingStreakUiState(
    val isLoading: Boolean = true,
    val summary: StreakSummary = StreakSummary.EMPTY,
    val last7Days: List<DayHistoryUiModel> = emptyList(),
)

class ReadingStreakViewModel(
    private val repository: ReadingActivityRepository,
    private val zoneId: ZoneId = ZoneId.systemDefault(),
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReadingStreakUiState())
    val uiState: StateFlow<ReadingStreakUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(dispatcher) {
            repository.observeAllActivity().collectLatest { records ->
                val today = LocalDate.now(zoneId)
                val qualifyingDates = records.filter { it.qualifiedForReadingDay }.map { it.date }.toSet()
                val summary = org.amanahquran.app.core.util.StreakCalculator.calculate(qualifyingDates, today)
                val last7Days = (6 downTo 0).map { offset ->
                    val date = today.minusDays(offset.toLong())
                    DayHistoryUiModel(
                        date = date,
                        isToday = date == today,
                        qualified = date in qualifyingDates,
                    )
                }
                _uiState.value = ReadingStreakUiState(
                    isLoading = false,
                    summary = summary,
                    last7Days = last7Days,
                )
            }
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ReadingStreakViewModel(readingActivityRepository(context)) as T
            }
        }
    }
}
