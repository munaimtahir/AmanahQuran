package org.amanahquran.app.content.translation

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class TranslationAvailability { TRANSLATED, SOURCE_MISSING }

data class TranslationFootnote(
    val marker: String,
    val text: String,
)

data class TranslationAyahDisplay(
    val ayahKey: String,
    /** Verbatim source text, or null when [availability] is [TranslationAvailability.SOURCE_MISSING]. Never a UI placeholder string. */
    val displayText: String?,
    val availability: TranslationAvailability,
)

/**
 * Reads bundled, offline translation content. Every method takes an explicit [translationId] --
 * see [org.amanahquran.app.core.model.TranslationSelection] for the stable ids -- so this class
 * carries no hardcoded single-translation assumption.
 */
class TranslationRepository(private val context: Context) {
    private val dao get() = TranslationDatabaseProvider.getDatabase(context).translationDao()

    suspend fun getAyah(translationId: String, ayahKey: String): TranslationAyahDisplay? {
        return dao.getAyah(translationId, ayahKey)?.toDisplay()
    }

    suspend fun search(translationId: String, query: String): List<TranslationAyahDisplay> {
        val normalized = normalize(query)
        if (normalized.isBlank()) return emptyList()
        return dao.search(translationId, normalized, 50).map { it.toDisplay() }
    }

    suspend fun metadata(translationId: String): TranslationMetadataEntity? = dao.getMetadata(translationId)

    suspend fun footnotesByAyah(translationId: String): Map<String, List<TranslationFootnote>> {
        return dao.getAllFootnotes(translationId)
            .groupBy({ it.ayahKey }, { TranslationFootnote(it.marker, it.footnoteText) })
    }

    fun observeAll(translationId: String): Flow<List<TranslationAyahDisplay>> =
        dao.observeAyahs(translationId).map { list -> list.map { it.toDisplay() } }

    private fun TranslationAyahEntity.toDisplay() = TranslationAyahDisplay(
        ayahKey = ayahKey,
        displayText = displayText,
        availability = if (availabilityStatus == TranslationAvailabilityStatus.SOURCE_MISSING) {
            TranslationAvailability.SOURCE_MISSING
        } else {
            TranslationAvailability.TRANSLATED
        },
    )

    private fun normalize(value: String): String = value
        .replace('ـ'.toString(), "")
        .replace(Regex("[\\u064B-\\u065F\\u0670]"), "")
        .replace(Regex("\\s+"), " ")
        .trim()
}
