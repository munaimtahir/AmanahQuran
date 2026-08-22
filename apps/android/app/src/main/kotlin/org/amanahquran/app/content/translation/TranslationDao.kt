package org.amanahquran.app.content.translation

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TranslationDao {
    @Query("SELECT * FROM translation_metadata WHERE translationId = :translationId LIMIT 1")
    suspend fun getMetadata(translationId: String): TranslationMetadataEntity?

    @Query("SELECT * FROM translation_ayahs WHERE translationId = :translationId AND ayahKey = :ayahKey LIMIT 1")
    suspend fun getAyah(translationId: String, ayahKey: String): TranslationAyahEntity?

    @Query("SELECT * FROM translation_ayahs WHERE translationId = :translationId ORDER BY surahNumber, ayahNumber")
    fun observeAyahs(translationId: String): Flow<List<TranslationAyahEntity>>

    @Query(
        "SELECT * FROM translation_ayahs WHERE translationId = :translationId AND normalizedSearchText LIKE '%' || :query || '%' ORDER BY surahNumber, ayahNumber LIMIT :limit",
    )
    suspend fun search(translationId: String, query: String, limit: Int): List<TranslationAyahEntity>

    @Query("SELECT * FROM translation_footnotes WHERE translationId = :translationId ORDER BY ayahKey, footnoteIndex")
    suspend fun getAllFootnotes(translationId: String): List<TranslationFootnoteEntity>

    @Query("SELECT * FROM translation_footnotes WHERE translationId = :translationId AND ayahKey = :ayahKey ORDER BY footnoteIndex")
    suspend fun getFootnotes(translationId: String, ayahKey: String): List<TranslationFootnoteEntity>

    @Query("SELECT COUNT(*) FROM translation_ayahs WHERE translationId = :translationId")
    suspend fun countAyahs(translationId: String): Int

    @Query("SELECT COUNT(*) FROM translation_ayahs WHERE translationId = :translationId AND availabilityStatus = 'SOURCE_MISSING'")
    suspend fun countSourceMissing(translationId: String): Int

    @Query("SELECT COUNT(*) FROM translation_footnotes WHERE translationId = :translationId")
    suspend fun countFootnotes(translationId: String): Int
}
