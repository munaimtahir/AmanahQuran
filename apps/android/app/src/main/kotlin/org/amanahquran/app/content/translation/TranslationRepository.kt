package org.amanahquran.app.content.translation

import android.content.Context
import kotlinx.coroutines.flow.Flow

data class UrduTranslation(
    val ayahKey: String,
    val text: String,
)

class TranslationRepository(context: Context) {
    private val dao = TranslationDatabaseProvider.getDatabase(context).translationDao()

    suspend fun getAyah(ayahKey: String): UrduTranslation? {
        return dao.getAyah(TRANSLATION_ID, ayahKey)?.let { UrduTranslation(it.ayahKey, it.displayText) }
    }

    suspend fun search(query: String): List<UrduTranslation> {
        val normalized = normalize(query)
        if (normalized.isBlank()) return emptyList()
        return dao.search(TRANSLATION_ID, normalized, 50).map { UrduTranslation(it.ayahKey, it.displayText) }
    }

    suspend fun metadata(): TranslationMetadataEntity? = dao.getMetadata(TRANSLATION_ID)

    fun observeAll(): Flow<List<TranslationAyahEntity>> = dao.observeAyahs(TRANSLATION_ID)

    private fun normalize(value: String): String = value
        .replace('\u0640'.toString(), "")
        .replace(Regex("[\\u064B-\\u065F\\u0670]"), "")
        .replace(Regex("\\s+"), " ")
        .trim()

    companion object {
        const val TRANSLATION_ID = "urdu_junagarhi"
    }
}
