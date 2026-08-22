package org.amanahquran.app.core.daily

import java.time.LocalDate

enum class DailyAyahSelectionMode { CURATED, SEQUENTIAL, REVIEWED_RANDOM }

data class DailyAyahRecord(
    val date: LocalDate,
    val ayahKey: String,
    val selectionMode: DailyAyahSelectionMode,
    val translationId: String?,
)

data class DailyAyahEligibility(
    val ayahKey: String,
    val eligible: Boolean,
    val category: String? = null,
    val contextSensitive: Boolean = false,
    val reviewStatus: String = "UNREVIEWED",
)

data class DailyAyahContent(
    val record: DailyAyahRecord,
    val arabicText: String,
    val translationText: String?,
    val surahName: String,
    val ayahNumber: Int,
)

/** Pure, deterministic selector. It never changes Quran content and only returns canonical keys. */
object DailyAyahSelector {
    fun sequentialKey(date: LocalDate, totalAyahs: Int, orderedAyahKeys: List<String>): String? {
        if (totalAyahs <= 0 || orderedAyahKeys.isEmpty()) return null
        val index = Math.floorMod(date.toEpochDay(), orderedAyahKeys.size.toLong()).toInt()
        return orderedAyahKeys[index]
    }

    fun reviewedRandomKey(
        date: LocalDate,
        eligibleKeys: List<String>,
        recentKeys: Set<String>,
    ): String? {
        val candidates = eligibleKeys.filterNot(recentKeys::contains)
        if (candidates.isEmpty()) return eligibleKeys.firstOrNull()
        val index = Math.floorMod(date.toEpochDay(), candidates.size.toLong()).toInt()
        return candidates[index]
    }
}
