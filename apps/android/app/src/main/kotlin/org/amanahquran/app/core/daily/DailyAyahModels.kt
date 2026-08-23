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

    /**
     * Deterministic pseudo-random selection for a given date.
     * Uses SplitMix64 hashing on the epoch day to achieve uniform, non-linear
     * distribution across the whole Quran, avoiding recent keys from the 30-day window.
     */
    fun randomDailyKey(
        date: LocalDate,
        allAyahKeys: List<String>,
        recentKeys: Set<String> = emptySet(),
    ): String? {
        if (allAyahKeys.isEmpty()) return null
        val candidates = allAyahKeys.filterNot(recentKeys::contains).ifEmpty { allAyahKeys }
        
        val epochDay = date.toEpochDay()
        var hash = epochDay xor 0x5bf03635e293c021L
        hash = (hash xor (hash ushr 30)) * 0xbf58476d1ce4e5b9UL.toLong()
        hash = (hash xor (hash ushr 27)) * 0x94d049bb133111ebUL.toLong()
        hash = hash xor (hash ushr 31)
        
        val index = Math.floorMod(hash, candidates.size.toLong()).toInt()
        return candidates[index]
    }

    fun reviewedRandomKey(
        date: LocalDate,
        eligibleKeys: List<String>,
        recentKeys: Set<String>,
    ): String? {
        return randomDailyKey(date, eligibleKeys, recentKeys)
    }
}
