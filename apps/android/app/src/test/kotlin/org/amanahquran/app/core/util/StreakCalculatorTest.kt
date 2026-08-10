package org.amanahquran.app.core.util

import java.time.LocalDate
import org.amanahquran.app.core.model.StreakSummary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StreakCalculatorTest {
    private val today = LocalDate.of(2026, 8, 11)

    @Test
    fun emptyHistoryProducesEmptySummary() {
        val summary = StreakCalculator.calculate(emptySet(), today)
        assertEquals(StreakSummary.EMPTY, summary)
    }

    @Test
    fun consecutiveDaysEndingTodayProduceMatchingCurrentStreak() {
        val dates = setOf(today.minusDays(2), today.minusDays(1), today)
        val summary = StreakCalculator.calculate(dates, today)
        assertEquals(3, summary.currentStreak)
        assertEquals(3, summary.longestStreak)
        assertTrue(summary.readToday)
        assertEquals(today, summary.lastQualifyingDate)
    }

    @Test
    fun streakStillAliveWhenYesterdayQualifiedButTodayHasNotYet() {
        val dates = setOf(today.minusDays(2), today.minusDays(1))
        val summary = StreakCalculator.calculate(dates, today)
        assertEquals(2, summary.currentStreak)
        assertFalse(summary.readToday)
    }

    @Test
    fun brokenStreakResetsCurrentButPreservesLongest() {
        // A 5-day streak two weeks ago, then a gap, then nothing today or yesterday.
        val oldStreak = (10..14).map { today.minusDays(it.toLong()) }.toSet()
        val summary = StreakCalculator.calculate(oldStreak, today)
        assertEquals(0, summary.currentStreak)
        assertEquals(5, summary.longestStreak)
        assertFalse(summary.readToday)
    }

    @Test
    fun longestStreakSurvivesAfterMoreRecentShorterStreak() {
        val longAgoStreak = (20..24).map { today.minusDays(it.toLong()) }.toSet() // 5-day streak
        val recentStreak = setOf(today.minusDays(1), today) // 2-day current streak
        val summary = StreakCalculator.calculate(longAgoStreak + recentStreak, today)
        assertEquals(2, summary.currentStreak)
        assertEquals(5, summary.longestStreak)
    }

    @Test
    fun singleGapDayBreaksConsecutiveRun() {
        val dates = setOf(today.minusDays(3), today.minusDays(2), today) // missing today-1
        val summary = StreakCalculator.calculate(dates, today)
        assertEquals(1, summary.currentStreak)
        assertEquals(2, summary.longestStreak)
    }

    @Test
    fun totalQualifyingDaysCountsAllEntriesRegardlessOfGaps() {
        val dates = setOf(today, today.minusDays(5), today.minusDays(10))
        val summary = StreakCalculator.calculate(dates, today)
        assertEquals(3, summary.totalQualifyingDays)
    }

    @Test
    fun lastQualifyingDateIsNullWhenHistoryEmpty() {
        assertNull(StreakCalculator.calculate(emptySet(), today).lastQualifyingDate)
    }

    @Test
    fun monthAndYearBoundariesDoNotBreakConsecutiveRun() {
        val dates = setOf(
            LocalDate.of(2025, 12, 30),
            LocalDate.of(2025, 12, 31),
            LocalDate.of(2026, 1, 1),
            LocalDate.of(2026, 1, 2),
        )
        val summary = StreakCalculator.calculate(dates, LocalDate.of(2026, 1, 2))
        assertEquals(4, summary.currentStreak)
        assertEquals(4, summary.longestStreak)
    }
}
