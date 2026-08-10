package org.amanahquran.app.core.util

import java.time.DayOfWeek
import java.time.ZoneOffset
import java.time.ZonedDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReminderScheduleCalculatorTest {
    private val utc = ZoneOffset.UTC

    @Test
    fun emptyAllowedDaysReturnsNull() {
        val now = ZonedDateTime.of(2026, 8, 11, 10, 0, 0, 0, utc)
        assertNull(ReminderScheduleCalculator.nextOccurrence(now, 20, 0, emptySet()))
    }

    @Test
    fun sameDayLaterTimeIsPickedWhenAllowed() {
        // Tuesday 2026-08-11
        val now = ZonedDateTime.of(2026, 8, 11, 10, 0, 0, 0, utc)
        val next = ReminderScheduleCalculator.nextOccurrence(now, 20, 0, setOf(DayOfWeek.TUESDAY))
        assertEquals(ZonedDateTime.of(2026, 8, 11, 20, 0, 0, 0, utc), next)
    }

    @Test
    fun sameDayPassedTimeRollsToNextAllowedDay() {
        // Tuesday 2026-08-11, reminder time already passed today
        val now = ZonedDateTime.of(2026, 8, 11, 21, 0, 0, 0, utc)
        val next = ReminderScheduleCalculator.nextOccurrence(now, 20, 0, setOf(DayOfWeek.TUESDAY))
        assertEquals(ZonedDateTime.of(2026, 8, 18, 20, 0, 0, 0, utc), next) // next Tuesday
    }

    @Test
    fun singleAllowedDayNotTodaySkipsToThatDay() {
        // Tuesday -> next Friday
        val now = ZonedDateTime.of(2026, 8, 11, 10, 0, 0, 0, utc)
        val next = ReminderScheduleCalculator.nextOccurrence(now, 8, 0, setOf(DayOfWeek.FRIDAY))
        assertEquals(DayOfWeek.FRIDAY, next?.dayOfWeek)
        assertEquals(ZonedDateTime.of(2026, 8, 14, 8, 0, 0, 0, utc), next)
    }

    @Test
    fun delayUntilNeverNegative() {
        val now = ZonedDateTime.of(2026, 8, 11, 21, 0, 0, 0, utc)
        val past = now.minusHours(1)
        assertEquals(java.time.Duration.ZERO, ReminderScheduleCalculator.delayUntil(now, past))
    }

    @Test
    fun delayUntilMatchesGapBetweenTimes() {
        val now = ZonedDateTime.of(2026, 8, 11, 10, 0, 0, 0, utc)
        val target = ZonedDateTime.of(2026, 8, 11, 12, 30, 0, 0, utc)
        assertEquals(java.time.Duration.ofMinutes(150), ReminderScheduleCalculator.delayUntil(now, target))
    }

    @Test
    fun allDaysAllowedAlwaysPicksTodayOrTomorrow() {
        val now = ZonedDateTime.of(2026, 8, 11, 23, 59, 0, 0, utc)
        val next = ReminderScheduleCalculator.nextOccurrence(now, 6, 0, DayOfWeek.entries.toSet())
        assertTrue(next!!.isAfter(now))
        assertEquals(ZonedDateTime.of(2026, 8, 12, 6, 0, 0, 0, utc), next)
    }
}
