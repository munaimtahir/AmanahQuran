package org.amanahquran.app.core.model

import java.time.LocalDate

/**
 * One device-local calendar day of reading activity. Ayah/page identity sets (not raw counters)
 * are stored so repeated sessions can be merged without double-counting the same ayah or page.
 */
data class DailyReadingActivity(
    val date: LocalDate,
    val readingDurationSeconds: Long,
    val ayahKeysRead: Set<String>,
    val pagesRead: Set<Int>,
    val firstReadingTimestamp: Long,
    val lastReadingTimestamp: Long,
) {
    val uniqueAyahsRead: Int get() = ayahKeysRead.size
    val pagesReadCount: Int get() = pagesRead.size

    val qualifiedForReadingDay: Boolean
        get() = readingDurationSeconds >= QUALIFYING_DURATION_SECONDS ||
            uniqueAyahsRead >= QUALIFYING_AYAH_COUNT

    fun mergedWith(
        additionalDurationSeconds: Long,
        additionalAyahKeys: Set<String>,
        additionalPages: Set<Int>,
        timestamp: Long,
    ): DailyReadingActivity = copy(
        readingDurationSeconds = readingDurationSeconds + additionalDurationSeconds.coerceAtLeast(0),
        ayahKeysRead = ayahKeysRead + additionalAyahKeys,
        pagesRead = pagesRead + additionalPages,
        firstReadingTimestamp = minOf(firstReadingTimestamp, timestamp),
        lastReadingTimestamp = maxOf(lastReadingTimestamp, timestamp),
    )

    companion object {
        const val QUALIFYING_DURATION_SECONDS = 120L
        const val QUALIFYING_AYAH_COUNT = 3

        fun startingSession(
            date: LocalDate,
            durationSeconds: Long,
            ayahKeysRead: Set<String>,
            pagesRead: Set<Int>,
            timestamp: Long,
        ): DailyReadingActivity = DailyReadingActivity(
            date = date,
            readingDurationSeconds = durationSeconds.coerceAtLeast(0),
            ayahKeysRead = ayahKeysRead,
            pagesRead = pagesRead,
            firstReadingTimestamp = timestamp,
            lastReadingTimestamp = timestamp,
        )
    }
}

data class StreakSummary(
    val currentStreak: Int,
    val longestStreak: Int,
    val lastQualifyingDate: LocalDate?,
    val totalQualifyingDays: Int,
    val readToday: Boolean,
) {
    companion object {
        val EMPTY = StreakSummary(
            currentStreak = 0,
            longestStreak = 0,
            lastQualifyingDate = null,
            totalQualifyingDays = 0,
            readToday = false,
        )
    }
}
