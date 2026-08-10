package org.amanahquran.app.core.util

import java.time.LocalDate
import org.amanahquran.app.core.model.StreakSummary

/**
 * Pure, timezone-agnostic streak math over a set of qualifying local dates. Longest streak is
 * deliberately derived from full history rather than persisted separately, so a broken current
 * streak can never lose a previously achieved longest streak.
 */
object StreakCalculator {
    fun calculate(qualifyingDates: Set<LocalDate>, today: LocalDate): StreakSummary {
        if (qualifyingDates.isEmpty()) return StreakSummary.EMPTY

        val sorted = qualifyingDates.sorted()
        var longestStreak = 1
        var runLength = 1
        for (i in 1 until sorted.size) {
            runLength = if (sorted[i] == sorted[i - 1].plusDays(1)) runLength + 1 else 1
            if (runLength > longestStreak) longestStreak = runLength
        }

        val readToday = today in qualifyingDates
        val currentStreakAnchor = when {
            readToday -> today
            today.minusDays(1) in qualifyingDates -> today.minusDays(1)
            else -> null
        }
        var currentStreak = 0
        if (currentStreakAnchor != null) {
            var cursor: LocalDate = currentStreakAnchor
            while (cursor in qualifyingDates) {
                currentStreak++
                cursor = cursor.minusDays(1)
            }
        }

        return StreakSummary(
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            lastQualifyingDate = sorted.last(),
            totalQualifyingDays = sorted.size,
            readToday = readToday,
        )
    }
}
