package org.amanahquran.app.feature.stats

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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.amanahquran.app.core.model.DailyReadingActivity
import org.amanahquran.app.core.repository.ReadingActivityRepository
import org.amanahquran.app.core.repository.readingActivityRepository
import org.amanahquran.app.core.util.ReadingActivityAggregator
import org.amanahquran.app.core.util.ReadingActivityStats

enum class StatsRange { WEEK, MONTH, ALL_TIME }

data class ReadingActivityDashboardUiState(
    val isLoading: Boolean = true,
    val selectedRange: StatsRange = StatsRange.WEEK,
    val stats: ReadingActivityStats = ReadingActivityStats.EMPTY,
    val weeklySeries: List<Pair<LocalDate, Long>> = emptyList(),
)

class ReadingActivityDashboardViewModel(
    private val repository: ReadingActivityRepository,
    private val zoneId: ZoneId = ZoneId.systemDefault(),
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReadingActivityDashboardUiState())
    val uiState: StateFlow<ReadingActivityDashboardUiState> = _uiState.asStateFlow()

    private var allRecords: List<DailyReadingActivity> = emptyList()

    init {
        viewModelScope.launch(dispatcher) {
            repository.observeAllActivity().collectLatest { records ->
                allRecords = records
                recompute()
            }
        }
    }

    fun selectRange(range: StatsRange) {
        _uiState.update { it.copy(selectedRange = range) }
        recompute()
    }

    private fun recompute() {
        val today = LocalDate.now(zoneId)
        val range = _uiState.value.selectedRange
        val start = when (range) {
            StatsRange.WEEK -> today.minusDays(6)
            StatsRange.MONTH -> today.withDayOfMonth(1)
            StatsRange.ALL_TIME -> null
        }
        val stats = ReadingActivityAggregator.aggregate(allRecords, start, today, today)
        val series = ReadingActivityAggregator.dailySeries(allRecords, today.minusDays(6), today)
        _uiState.update { it.copy(isLoading = false, stats = stats, weeklySeries = series) }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ReadingActivityDashboardViewModel(readingActivityRepository(context)) as T
            }
        }
    }
}
