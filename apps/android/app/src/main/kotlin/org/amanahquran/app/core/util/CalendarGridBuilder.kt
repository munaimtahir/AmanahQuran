package org.amanahquran.app.core.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

data class CalendarDay(val date: LocalDate, val inCurrentMonth: Boolean)

/** Pure month-grid math: full weeks (multiples of 7), padded with adjacent-month days. */
object CalendarGridBuilder {
    fun buildMonthGrid(yearMonth: YearMonth, weekStartsOn: DayOfWeek = DayOfWeek.SUNDAY): List<CalendarDay> {
        val firstOfMonth = yearMonth.atDay(1)
        val leadingDays = (firstOfMonth.dayOfWeek.value - weekStartsOn.value + 7) % 7
        val gridStart = firstOfMonth.minusDays(leadingDays.toLong())
        val rawTotalCells = leadingDays + yearMonth.lengthOfMonth()
        val totalCells = ((rawTotalCells + 6) / 7) * 7

        return (0 until totalCells).map { offset ->
            val date = gridStart.plusDays(offset.toLong())
            CalendarDay(date = date, inCurrentMonth = YearMonth.from(date) == yearMonth)
        }
    }
}
