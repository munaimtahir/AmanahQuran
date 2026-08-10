package org.amanahquran.app.feature.stats

import java.io.File
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.amanahquran.app.MainDispatcherRule
import org.amanahquran.app.core.datastore.amanahPreferencesDataSourceForFile
import org.amanahquran.app.core.repository.ReadingActivityRepositoryImpl
import org.junit.Assert.assertEquals
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
class ReadingActivityDashboardViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: ReadingActivityRepositoryImpl
    private val zone = ZoneId.of("UTC")

    @Before
    fun setUp() {
        val tempFile = File(
            RuntimeEnvironment.getApplication().filesDir,
            "amanah-dashboard-${System.nanoTime()}.preferences_pb",
        )
        repository = ReadingActivityRepositoryImpl(amanahPreferencesDataSourceForFile(tempFile))
    }

    @Test
    fun weekIsDefaultRangeAndReflectsRecentActivity() = runTest {
        val today = LocalDate.now(zone)
        repository.recordSession(today, 150, setOf("1:1", "1:2"), setOf(1), timestamp = 1L)

        val viewModel = ReadingActivityDashboardViewModel(repository, zone, UnconfinedTestDispatcher(testScheduler))
        val state = viewModel.uiState.first { !it.isLoading }

        assertEquals(StatsRange.WEEK, state.selectedRange)
        assertEquals(150L, state.stats.totalReadingSeconds)
        assertEquals(2, state.stats.ayahsRead)
        assertEquals(7, state.weeklySeries.size)
    }

    @Test
    fun switchingToAllTimeIncludesOlderActivity() = runTest {
        val today = LocalDate.now(zone)
        repository.recordSession(today.minusYears(1), 150, emptySet(), emptySet(), timestamp = 1L)

        val viewModel = ReadingActivityDashboardViewModel(repository, zone, UnconfinedTestDispatcher(testScheduler))
        viewModel.uiState.first { !it.isLoading }

        viewModel.selectRange(StatsRange.ALL_TIME)
        val state = viewModel.uiState.first { it.selectedRange == StatsRange.ALL_TIME }

        assertEquals(150L, state.stats.totalReadingSeconds)
    }

    @Test
    fun monthRangeExcludesActivityFromPreviousMonth() = runTest {
        val today = LocalDate.now(zone)
        repository.recordSession(today.withDayOfMonth(1).minusDays(1), 150, emptySet(), emptySet(), timestamp = 1L)
        repository.recordSession(today, 150, emptySet(), emptySet(), timestamp = 1L)

        val viewModel = ReadingActivityDashboardViewModel(repository, zone, UnconfinedTestDispatcher(testScheduler))
        viewModel.uiState.first { !it.isLoading }
        viewModel.selectRange(StatsRange.MONTH)
        val state = viewModel.uiState.first { it.selectedRange == StatsRange.MONTH }

        assertEquals(150L, state.stats.totalReadingSeconds)
    }
}
