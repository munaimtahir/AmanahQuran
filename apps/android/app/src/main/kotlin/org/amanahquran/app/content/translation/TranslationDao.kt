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

    @Query("SELECT * FROM translation_ayahs WHERE translationId = :translationId ORDER BY ayahKey")
    fun observeAyahs(translationId: String): Flow<List<TranslationAyahEntity>>

    @Query("SELECT * FROM translation_ayahs WHERE translationId = :translationId AND normalizedSearchText LIKE '%' || :query || '%' ORDER BY ayahKey LIMIT :limit")
    suspend fun search(translationId: String, query: String, limit: Int): List<TranslationAyahEntity>
}
