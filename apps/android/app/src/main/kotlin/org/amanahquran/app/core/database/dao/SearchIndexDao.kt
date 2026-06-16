package org.amanahquran.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.amanahquran.app.core.database.entity.SearchIndexEntity

@Dao
interface SearchIndexDao {
    @Query("SELECT * FROM search_index WHERE ayahKey = :ayahKey")
    suspend fun getSearchIndexByAyahKey(ayahKey: String): SearchIndexEntity?

    @Query("SELECT * FROM search_index WHERE normalizedArabic LIKE '%' || :query || '%'")
    fun searchNormalizedArabic(query: String): Flow<List<SearchIndexEntity>>

    @Query("SELECT COUNT(*) FROM search_index")
    suspend fun getSearchIndexCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSearchIndex(items: List<SearchIndexEntity>)
}
