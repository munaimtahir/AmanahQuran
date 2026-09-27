package org.amanahquran.app.core.daily

import java.time.LocalDate

enum class DailyAyahSelectionMode {
    /** Picked from the scholar-reviewed eligibility pool. */
    CURATED,
    SEQUENTIAL,

    /**
     * Legacy stored value only. Up to v2.2.0 it was written for full-corpus picks even though no
     * review had happened, so it is read back and labelled as [FULL_CORPUS_RANDOM].
     */
    REVIEWED_RANDOM,

    /** Deterministic pick from all 6,236 ayahs; no reviewed pool was involved. */
    FULL_CORPUS_RANDOM,
    ;

    /** User-facing label. Never claims review for picks that were not reviewed. */
    val displayLabel: String
        get() = when (this) {
            CURATED -> "curated"
            SEQUENTIAL -> "sequential"
            REVIEWED_RANDOM, FULL_CORPUS_RANDOM -> "random"
        }
}

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

    /**
     * Chooses the mode and key for [date]: the reviewed pool when one exists, otherwise the full
     * corpus. The mode reflects the pool actually used, so history never overstates review status.
     */
    fun select(
        date: LocalDate,
        allAyahKeys: List<String>,
        reviewedKeys: List<String>,
        recentKeys: Set<String>,
    ): Pair<DailyAyahSelectionMode, String>? = if (reviewedKeys.isNotEmpty()) {
        reviewedRandomKey(date, reviewedKeys, recentKeys)?.let { DailyAyahSelectionMode.CURATED to it }
    } else {
        randomDailyKey(date, allAyahKeys, recentKeys)?.let { DailyAyahSelectionMode.FULL_CORPUS_RANDOM to it }
    }

    fun reviewedRandomKey(
        date: LocalDate,
        eligibleKeys: List<String>,
        recentKeys: Set<String>,
    ): String? {
        return randomDailyKey(date, eligibleKeys, recentKeys)
    }
}
