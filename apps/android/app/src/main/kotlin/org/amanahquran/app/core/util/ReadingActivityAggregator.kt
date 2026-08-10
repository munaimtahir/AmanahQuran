package org.amanahquran.app.core.util

import java.time.LocalDate
import org.amanahquran.app.core.model.DailyReadingActivity

data class ReadingActivityStats(
    val totalReadingSeconds: Long,
    val readingDays: Int,
    val ayahsRead: Int,
    val pagesRead: Int,
    val currentStreak: Int,
    val longestStreak: Int,
) {
    companion object {
        val EMPTY = ReadingActivityStats(0, 0, 0, 0, 0, 0)
    }
}

/** Range-based aggregation over stored [DailyReadingActivity] records -- pure, no I/O. */
object ReadingActivityAggregator {
    /** [start] inclusive, null means unbounded (All Time). [end] inclusive. */
    fun aggregate(
        records: List<DailyReadingActivity>,
        start: LocalDate?,
        end: LocalDate,
        today: LocalDate,
    ): ReadingActivityStats {
        val inRange = records.filter { (start == null || !it.date.isBefore(start)) && !it.date.isAfter(end) }
        if (records.isEmpty()) return ReadingActivityStats.EMPTY

        val qualifyingDates = records.filter { it.qualifiedForReadingDay }.map { it.date }.toSet()
        val streak = StreakCalculator.calculate(qualifyingDates, today)

        return ReadingActivityStats(
            totalReadingSeconds = inRange.sumOf { it.readingDurationSeconds },
            readingDays = inRange.count { it.qualifiedForReadingDay },
            ayahsRead = inRange.sumOf { it.uniqueAyahsRead },
            pagesRead = inRange.flatMap { it.pagesRead }.toSet().size,
            currentStreak = streak.currentStreak,
            longestStreak = streak.longestStreak,
        )
    }

    /** One entry per day in [start]..[end] inclusive, zero-filled for days with no activity. */
    fun dailySeries(records: List<DailyReadingActivity>, start: LocalDate, end: LocalDate): List<Pair<LocalDate, Long>> {
        val byDate = records.associateBy { it.date }
        val days = mutableListOf<Pair<LocalDate, Long>>()
        var cursor = start
        while (!cursor.isAfter(end)) {
            days += cursor to (byDate[cursor]?.readingDurationSeconds ?: 0L)
            cursor = cursor.plusDays(1)
        }
        return days
    }
}
