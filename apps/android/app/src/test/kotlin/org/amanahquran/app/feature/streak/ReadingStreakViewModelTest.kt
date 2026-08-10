package org.amanahquran.app.feature.streak

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
class ReadingStreakViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: ReadingActivityRepositoryImpl
    private val zone = ZoneId.of("UTC")

    @Before
    fun setUp() {
        val tempFile = File(
            RuntimeEnvironment.getApplication().filesDir,
            "amanah-streak-vm-${System.nanoTime()}.preferences_pb",
        )
        repository = ReadingActivityRepositoryImpl(amanahPreferencesDataSourceForFile(tempFile))
    }

    @Test
    fun last7DaysMarksOnlyQualifyingDaysAndTodayFlag() = runTest {
        val today = LocalDate.now(zone)
        repository.recordSession(today, 150, emptySet(), emptySet(), timestamp = 1L)
        repository.recordSession(today.minusDays(2), 10, emptySet(), emptySet(), timestamp = 1L) // non-qualifying

        val viewModel = ReadingStreakViewModel(repository, zone, UnconfinedTestDispatcher(testScheduler))
        val state = viewModel.uiState.first { !it.isLoading }

        assertEquals(7, state.last7Days.size)
        val todayEntry = state.last7Days.last()
        assertTrue(todayEntry.isToday)
        assertTrue(todayEntry.qualified)

        val nonQualifyingEntry = state.last7Days[4] // today - 2
        assertEquals(today.minusDays(2), nonQualifyingEntry.date)
        assertEquals(false, nonQualifyingEntry.qualified)
    }

    @Test
    fun summaryReflectsCurrentAndLongestStreak() = runTest {
        val today = LocalDate.now(zone)
        repository.recordSession(today.minusDays(1), 150, emptySet(), emptySet(), timestamp = 1L)
        repository.recordSession(today, 150, emptySet(), emptySet(), timestamp = 1L)

        val viewModel = ReadingStreakViewModel(repository, zone, UnconfinedTestDispatcher(testScheduler))
        val state = viewModel.uiState.first { !it.isLoading }

        assertEquals(2, state.summary.currentStreak)
        assertEquals(2, state.summary.longestStreak)
        assertTrue(state.summary.readToday)
    }
}
