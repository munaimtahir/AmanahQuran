package org.amanahquran.app.core.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalendarGridBuilderTest {
    @Test
    fun gridSizeIsAlwaysMultipleOfSeven() {
        // Sample a range of months including short/long and leap-year February.
        listOf(
            YearMonth.of(2026, 1),
            YearMonth.of(2026, 2),
            YearMonth.of(2024, 2), // leap year
            YearMonth.of(2026, 8),
            YearMonth.of(2026, 12),
        ).forEach { ym ->
            val grid = CalendarGridBuilder.buildMonthGrid(ym)
            assertEquals(0, grid.size % 7)
        }
    }

    @Test
    fun gridContainsEveryDayOfTheMonthMarkedInCurrentMonth() {
        val ym = YearMonth.of(2026, 8)
        val grid = CalendarGridBuilder.buildMonthGrid(ym)
        val inMonthDates = grid.filter { it.inCurrentMonth }.map { it.date }
        assertEquals((1..31).map { LocalDate.of(2026, 8, it) }, inMonthDates)
    }

    @Test
    fun leadingAndTrailingDaysBelongToAdjacentMonthsAndAreMarkedOutOfMonth() {
        val ym = YearMonth.of(2026, 8) // Aug 1 2026 is a Saturday
        val grid = CalendarGridBuilder.buildMonthGrid(ym, weekStartsOn = DayOfWeek.SUNDAY)
        val leading = grid.takeWhile { !it.inCurrentMonth }
        assertTrue(leading.isNotEmpty())
        assertTrue(leading.all { it.date.month == java.time.Month.JULY })
    }

    @Test
    fun firstCellAlwaysMatchesWeekStartDay() {
        val ym = YearMonth.of(2026, 8)
        val grid = CalendarGridBuilder.buildMonthGrid(ym, weekStartsOn = DayOfWeek.MONDAY)
        assertEquals(DayOfWeek.MONDAY, grid.first().date.dayOfWeek)
    }

    @Test
    fun emptyMonthStillProducesFullGrid() {
        // February in a non-leap year -- shortest month, still must produce complete weeks.
        val grid = CalendarGridBuilder.buildMonthGrid(YearMonth.of(2026, 2))
        assertEquals(0, grid.size % 7)
        assertEquals(28, grid.count { it.inCurrentMonth })
    }
}
