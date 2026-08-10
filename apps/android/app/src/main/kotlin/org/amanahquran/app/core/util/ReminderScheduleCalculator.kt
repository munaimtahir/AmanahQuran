package org.amanahquran.app.core.util

import java.time.Duration
import java.time.DayOfWeek
import java.time.ZonedDateTime

/** Pure next-occurrence math for the daily reading reminder -- no WorkManager/Android involved. */
object ReminderScheduleCalculator {
    /**
     * Returns the next [ZonedDateTime] at [hour]:[minute] that falls on one of [allowedDays],
     * strictly after [now] (today only counts if the time hasn't passed yet). Null if
     * [allowedDays] is empty -- there is nothing to schedule.
     */
    fun nextOccurrence(
        now: ZonedDateTime,
        hour: Int,
        minute: Int,
        allowedDays: Set<DayOfWeek>,
    ): ZonedDateTime? {
        if (allowedDays.isEmpty()) return null
        val todayAtTime = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)
        for (offset in 0..7) {
            val candidate = todayAtTime.plusDays(offset.toLong())
            if (candidate.dayOfWeek in allowedDays && candidate.isAfter(now)) {
                return candidate
            }
        }
        return null // unreachable when allowedDays is non-empty (a full week is covered)
    }

    fun delayUntil(now: ZonedDateTime, target: ZonedDateTime): Duration =
        Duration.between(now, target).let { if (it.isNegative) Duration.ZERO else it }
}
