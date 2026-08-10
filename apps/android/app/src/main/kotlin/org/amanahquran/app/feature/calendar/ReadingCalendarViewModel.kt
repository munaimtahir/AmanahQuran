package org.amanahquran.app.feature.calendar

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.amanahquran.app.core.model.DailyReadingActivity
import org.amanahquran.app.core.repository.ReadingActivityRepository
import org.amanahquran.app.core.repository.readingActivityRepository
import org.amanahquran.app.core.util.CalendarDay
import org.amanahquran.app.core.util.CalendarGridBuilder

data class ReadingCalendarUiState(
    val isLoading: Boolean = true,
    val yearMonth: YearMonth = YearMonth.now(),
    val today: LocalDate = LocalDate.now(),
    val days: List<CalendarDay> = emptyList(),
    val qualifyingDates: Set<LocalDate> = emptySet(),
    val selectedDate: LocalDate? = null,
    val selectedDayActivity: DailyReadingActivity? = null,
)

class ReadingCalendarViewModel(
    private val repository: ReadingActivityRepository,
    private val zoneId: ZoneId = ZoneId.systemDefault(),
    dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
) : ViewModel() {
    private val yearMonthFlow = MutableStateFlow(YearMonth.now(zoneId))
    private val selectedDateFlow = MutableStateFlow<LocalDate?>(null)

    private val _uiState = MutableStateFlow(ReadingCalendarUiState(yearMonth = yearMonthFlow.value, today = LocalDate.now(zoneId)))
    val uiState: StateFlow<ReadingCalendarUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(dispatcher) {
            combine(
                repository.observeAllActivity(),
                yearMonthFlow,
                selectedDateFlow,
            ) { records, yearMonth, selectedDate ->
                val today = LocalDate.now(zoneId)
                val byDate = records.associateBy { it.date }
                ReadingCalendarUiState(
                    isLoading = false,
                    yearMonth = yearMonth,
                    today = today,
                    days = CalendarGridBuilder.buildMonthGrid(yearMonth),
                    qualifyingDates = records.filter { it.qualifiedForReadingDay }.map { it.date }.toSet(),
                    selectedDate = selectedDate,
                    selectedDayActivity = selectedDate?.let { byDate[it] },
                )
            }.collect { state -> _uiState.update { state } }
        }
    }

    fun previousMonth() {
        yearMonthFlow.update { it.minusMonths(1) }
        selectedDateFlow.update { null }
    }

    fun nextMonth() {
        yearMonthFlow.update { it.plusMonths(1) }
        selectedDateFlow.update { null }
    }

    fun selectDate(date: LocalDate) {
        selectedDateFlow.update { current -> if (current == date) null else date }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ReadingCalendarViewModel(readingActivityRepository(context)) as T
            }
        }
    }
}
