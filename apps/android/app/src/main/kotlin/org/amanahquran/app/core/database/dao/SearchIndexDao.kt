package org.amanahquran.app.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import org.amanahquran.app.core.database.entity.SearchIndexEntity

@Dao
interface SearchIndexDao {
    @Query("SELECT COUNT(*) FROM search_index")
    suspend fun getSearchIndexCount(): Int

    @Query("SELECT * FROM search_index WHERE normalized_arabic LIKE '%' || :query || '%' ORDER BY id ASC LIMIT :limit")
    suspend fun searchNormalizedArabic(query: String, limit: Int = 50): List<SearchIndexEntity>

    @Query("SELECT * FROM search_index WHERE ayah_key = :ayahKey")
    suspend fun getSearchRow(ayahKey: String): SearchIndexEntity?
}
