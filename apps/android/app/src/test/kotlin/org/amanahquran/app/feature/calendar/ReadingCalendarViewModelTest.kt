package org.amanahquran.app.feature.calendar

import java.io.File
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.amanahquran.app.MainDispatcherRule
import org.amanahquran.app.core.datastore.amanahPreferencesDataSourceForFile
import org.amanahquran.app.core.repository.ReadingActivityRepositoryImpl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ReadingCalendarViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: ReadingActivityRepositoryImpl
    private val zone = ZoneId.of("UTC")

    @Before
    fun setUp() {
        val tempFile = File(
            RuntimeEnvironment.getApplication().filesDir,
            "amanah-calendar-${System.nanoTime()}.preferences_pb",
        )
        repository = ReadingActivityRepositoryImpl(amanahPreferencesDataSourceForFile(tempFile))
    }

    @Test
    fun correctDatesAreMarkedQualifying() = runTest {
        val today = LocalDate.now(zone)
        repository.recordSession(today, 150, emptySet(), emptySet(), timestamp = 1L)

        val viewModel = ReadingCalendarViewModel(repository, zone, UnconfinedTestDispatcher(testScheduler))
        val state = viewModel.uiState.first { !it.isLoading }

        assertTrue(today in state.qualifyingDates)
    }

    @Test
    fun monthNavigationChangesDisplayedMonth() = runTest {
        val viewModel = ReadingCalendarViewModel(repository, zone, UnconfinedTestDispatcher(testScheduler))
        val initial = viewModel.uiState.first { !it.isLoading }.yearMonth

        viewModel.nextMonth()
        val afterNext = viewModel.uiState.first { it.yearMonth == initial.plusMonths(1) }
        assertEquals(initial.plusMonths(1), afterNext.yearMonth)

        viewModel.previousMonth()
        viewModel.previousMonth()
        val afterPrev = viewModel.uiState.first { it.yearMonth == initial.minusMonths(1) }
        assertEquals(initial.minusMonths(1), afterPrev.yearMonth)
    }

    @Test
    fun todayStateReflectsActualCurrentDate() = runTest {
        val viewModel = ReadingCalendarViewModel(repository, zone, UnconfinedTestDispatcher(testScheduler))
        val state = viewModel.uiState.first { !it.isLoading }
        assertEquals(LocalDate.now(zone), state.today)
    }

    @Test
    fun selectingDateShowsCorrectActivityData() = runTest {
        val today = LocalDate.now(zone)
        repository.recordSession(today, 150, setOf("1:1", "1:2", "1:3"), setOf(1), timestamp = 1L)

        val viewModel = ReadingCalendarViewModel(repository, zone, UnconfinedTestDispatcher(testScheduler))
        viewModel.uiState.first { !it.isLoading }

        viewModel.selectDate(today)
        val state = viewModel.uiState.first { it.selectedDate == today }

        assertEquals(150L, state.selectedDayActivity?.readingDurationSeconds)
        assertEquals(3, state.selectedDayActivity?.uniqueAyahsRead)
    }

    @Test
    fun selectingSameDateTwiceDeselects() = runTest {
        val today = LocalDate.now(zone)
        val viewModel = ReadingCalendarViewModel(repository, zone, UnconfinedTestDispatcher(testScheduler))
        viewModel.uiState.first { !it.isLoading }

        viewModel.selectDate(today)
        viewModel.uiState.first { it.selectedDate == today }
        viewModel.selectDate(today)
        val state = viewModel.uiState.first { it.selectedDate == null }

        assertNull(state.selectedDate)
    }

    @Test
    fun emptyMonthStillProducesFullGrid() = runTest {
        val viewModel = ReadingCalendarViewModel(repository, zone, UnconfinedTestDispatcher(testScheduler))
        val state = viewModel.uiState.first { !it.isLoading }
        assertEquals(0, state.days.size % 7)
        assertTrue(state.days.count { it.inCurrentMonth } == YearMonth.now(zone).lengthOfMonth())
    }

    @Test
    fun historicalStreakDataRemainsAccurateAfterMonthNavigation() = runTest {
        val today = LocalDate.now(zone)
        repository.recordSession(today.minusMonths(2), 150, emptySet(), emptySet(), timestamp = 1L)

        val viewModel = ReadingCalendarViewModel(repository, zone, UnconfinedTestDispatcher(testScheduler))
        viewModel.uiState.first { !it.isLoading }
        viewModel.previousMonth()
        viewModel.previousMonth()
        val state = viewModel.uiState.first { it.yearMonth == YearMonth.from(today).minusMonths(2) }

        assertTrue(today.minusMonths(2) in state.qualifyingDates)
    }
}
