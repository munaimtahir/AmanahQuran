package org.amanahquran.app.core.repository

import java.io.File
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.amanahquran.app.core.datastore.AmanahPreferencesDataSource
import org.amanahquran.app.core.datastore.amanahPreferencesDataSourceForFile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ReadingActivityRepositoryTest {
    private lateinit var dataSource: AmanahPreferencesDataSource
    private lateinit var repository: ReadingActivityRepository

    @Before
    fun setUp() {
        val tempFile = File(
            RuntimeEnvironment.getApplication().filesDir,
            "amanah-reading-activity-${System.nanoTime()}.preferences_pb",
        )
        dataSource = amanahPreferencesDataSourceForFile(tempFile)
        repository = ReadingActivityRepositoryImpl(dataSource)
    }

    @Test
    fun newDayCreatesActivityEntryWithGivenValues() = runTest {
        val today = LocalDate.of(2026, 8, 11)
        repository.recordSession(today, 30, setOf("2:1", "2:2"), setOf(5), timestamp = 1000L)

        val activity = repository.getActivityForDate(today)
        assertTrue(activity != null)
        assertEquals(30L, activity!!.readingDurationSeconds)
        assertEquals(2, activity.uniqueAyahsRead)
        assertEquals(setOf(5), activity.pagesRead)
        assertEquals(1000L, activity.firstReadingTimestamp)
        assertEquals(1000L, activity.lastReadingTimestamp)
    }

    @Test
    fun repeatedSessionsOnSameDayAccumulateWithoutDuplicatingAyahs() = runTest {
        val today = LocalDate.of(2026, 8, 11)
        repository.recordSession(today, 30, setOf("2:1", "2:2"), emptySet(), timestamp = 1000L)
        repository.recordSession(today, 40, setOf("2:2", "2:3"), emptySet(), timestamp = 5000L)

        val activity = repository.getActivityForDate(today)!!
        assertEquals(70L, activity.readingDurationSeconds)
        assertEquals(3, activity.uniqueAyahsRead) // 2:2 not double-counted
        assertEquals(1000L, activity.firstReadingTimestamp)
        assertEquals(5000L, activity.lastReadingTimestamp)
    }

    @Test
    fun underQualifyingDurationAndAyahsDoesNotQualify() = runTest {
        val today = LocalDate.of(2026, 8, 11)
        repository.recordSession(today, 119, setOf("2:1", "2:2"), emptySet(), timestamp = 1L)

        assertFalse(repository.getActivityForDate(today)!!.qualifiedForReadingDay)
    }

    @Test
    fun durationAtOrAboveTwoMinutesQualifies() = runTest {
        val today = LocalDate.of(2026, 8, 11)
        repository.recordSession(today, 120, emptySet(), emptySet(), timestamp = 1L)

        assertTrue(repository.getActivityForDate(today)!!.qualifiedForReadingDay)
    }

    @Test
    fun threeUniqueAyahsQualifiesRegardlessOfDuration() = runTest {
        val today = LocalDate.of(2026, 8, 11)
        repository.recordSession(today, 5, setOf("2:1", "2:2", "2:3"), emptySet(), timestamp = 1L)

        assertTrue(repository.getActivityForDate(today)!!.qualifiedForReadingDay)
    }

    @Test
    fun previousDaysRemainUnchangedWhenNewDayIsRecorded() = runTest {
        val day1 = LocalDate.of(2026, 8, 10)
        val day2 = LocalDate.of(2026, 8, 11)
        repository.recordSession(day1, 200, setOf("1:1"), emptySet(), timestamp = 1L)
        repository.recordSession(day2, 50, setOf("1:2"), emptySet(), timestamp = 2L)

        val day1Activity = repository.getActivityForDate(day1)!!
        assertEquals(200L, day1Activity.readingDurationSeconds)
        assertEquals(setOf("1:1"), day1Activity.ayahKeysRead)
    }

    @Test
    fun streakSummaryReflectsOnlyQualifyingDays() = runTest {
        val today = LocalDate.of(2026, 8, 11)
        repository.recordSession(today.minusDays(1), 150, emptySet(), emptySet(), timestamp = 1L) // qualifies
        repository.recordSession(today.minusDays(2), 10, emptySet(), emptySet(), timestamp = 1L) // does not qualify
        repository.recordSession(today, 150, emptySet(), emptySet(), timestamp = 1L) // qualifies

        val summary = repository.getStreakSummary(today)
        assertEquals(2, summary.currentStreak) // today + yesterday only; day-before-yesterday broke it
        assertTrue(summary.readToday)
    }

    @Test
    fun replaceAllActivityOverwritesStoredHistory() = runTest {
        val today = LocalDate.of(2026, 8, 11)
        repository.recordSession(today, 200, setOf("1:1"), emptySet(), timestamp = 1L)

        repository.replaceAllActivity(emptyList())

        assertNull(repository.getActivityForDate(today))
        assertTrue(repository.observeAllActivity().first().isEmpty())
    }

    @Test
    fun sessionWithNoDurationOrContentDoesNotCreateEntry() = runTest {
        val today = LocalDate.of(2026, 8, 11)
        repository.recordSession(today, 0, emptySet(), emptySet(), timestamp = 1L)

        assertNull(repository.getActivityForDate(today))
    }
}
