package org.amanahquran.app.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import org.amanahquran.app.core.database.entity.ContentSourceEntity

@Dao
interface ContentSourceDao {
    @Query("SELECT * FROM content_sources")
    suspend fun getContentSources(): List<ContentSourceEntity>

    @Query("SELECT * FROM content_sources WHERE id = :id")
    suspend fun getContentSourceById(id: Int): ContentSourceEntity?

    @Query("SELECT COUNT(*) FROM content_sources")
    suspend fun getContentSourceCount(): Int

    @Query("SELECT COUNT(*) FROM content_sources WHERE sha256 = ''")
    suspend fun getBlankChecksumCount(): Int
}
