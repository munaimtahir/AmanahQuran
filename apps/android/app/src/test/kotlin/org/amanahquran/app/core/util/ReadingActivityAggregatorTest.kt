package org.amanahquran.app.core.util

import java.time.LocalDate
import org.amanahquran.app.core.model.DailyReadingActivity
import org.junit.Assert.assertEquals
import org.junit.Test

class ReadingActivityAggregatorTest {
    private val today = LocalDate.of(2026, 8, 11) // Tuesday

    private fun activity(date: LocalDate, seconds: Long, ayahs: Set<String> = emptySet(), pages: Set<Int> = emptySet()) =
        DailyReadingActivity.startingSession(date, seconds, ayahs, pages, timestamp = 1L)

    @Test
    fun zeroStateProducesEmptyStats() {
        val stats = ReadingActivityAggregator.aggregate(emptyList(), start = null, end = today, today = today)
        assertEquals(ReadingActivityStats.EMPTY, stats)
    }

    @Test
    fun oneDayActivityAggregatesCorrectly() {
        val records = listOf(activity(today, 150, setOf("1:1", "1:2"), setOf(1)))
        val stats = ReadingActivityAggregator.aggregate(records, start = today, end = today, today = today)
        assertEquals(150L, stats.totalReadingSeconds)
        assertEquals(1, stats.readingDays)
        assertEquals(2, stats.ayahsRead)
        assertEquals(1, stats.pagesRead)
    }

    @Test
    fun multiDayAggregationSumsAcrossDays() {
        val records = listOf(
            activity(today, 150, setOf("1:1"), setOf(1)),
            activity(today.minusDays(1), 200, setOf("1:2"), setOf(2)),
        )
        val stats = ReadingActivityAggregator.aggregate(records, start = today.minusDays(6), end = today, today = today)
        assertEquals(350L, stats.totalReadingSeconds)
        assertEquals(2, stats.readingDays)
        assertEquals(2, stats.ayahsRead)
        assertEquals(2, stats.pagesRead)
    }

    @Test
    fun weekBoundaryExcludesDaysOutsideRange() {
        val records = listOf(
            activity(today, 100),
            activity(today.minusDays(6), 50), // inside 7-day window
            activity(today.minusDays(7), 999), // just outside
        )
        val stats = ReadingActivityAggregator.aggregate(records, start = today.minusDays(6), end = today, today = today)
        assertEquals(150L, stats.totalReadingSeconds)
    }

    @Test
    fun monthBoundaryExcludesPriorMonth() {
        val monthStart = LocalDate.of(2026, 8, 1)
        val records = listOf(
            activity(LocalDate.of(2026, 8, 5), 100),
            activity(LocalDate.of(2026, 7, 31), 500), // previous month, excluded
        )
        val stats = ReadingActivityAggregator.aggregate(records, start = monthStart, end = today, today = today)
        assertEquals(100L, stats.totalReadingSeconds)
    }

    @Test
    fun allTimeAggregationIncludesEveryRecord() {
        val records = listOf(
            activity(LocalDate.of(2020, 1, 1), 100),
            activity(today, 200),
        )
        val stats = ReadingActivityAggregator.aggregate(records, start = null, end = today, today = today)
        assertEquals(300L, stats.totalReadingSeconds)
    }

    @Test
    fun readingTimeIsNeverDoubleCountedAcrossOverlappingQueries() {
        val records = listOf(activity(today, 100), activity(today.minusDays(1), 100))
        val week = ReadingActivityAggregator.aggregate(records, today.minusDays(6), today, today)
        val allTime = ReadingActivityAggregator.aggregate(records, null, today, today)
        assertEquals(200L, week.totalReadingSeconds)
        assertEquals(200L, allTime.totalReadingSeconds)
    }

    @Test
    fun dailySeriesZeroFillsMissingDays() {
        val records = listOf(activity(today, 60))
        val series = ReadingActivityAggregator.dailySeries(records, today.minusDays(2), today)
        assertEquals(3, series.size)
        assertEquals(0L, series[0].second)
        assertEquals(0L, series[1].second)
        assertEquals(60L, series[2].second)
    }

    @Test
    fun streaksReflectFullHistoryRegardlessOfRangeFilter() {
        val records = listOf(
            activity(today, 150),
            activity(today.minusDays(1), 150),
            activity(today.minusDays(30), 150), // outside the "week" range but still counted in streak calc
        )
        val stats = ReadingActivityAggregator.aggregate(records, start = today.minusDays(6), end = today, today = today)
        assertEquals(2, stats.currentStreak)
    }
}
