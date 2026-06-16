package org.amanahquran.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.amanahquran.app.core.database.entity.ContentSourceEntity
import org.amanahquran.app.core.model.ContentType
import org.amanahquran.app.core.model.ScriptType

@Dao
interface ContentSourceDao {
    @Query("SELECT * FROM content_sources")
    fun getAllContentSources(): Flow<List<ContentSourceEntity>>

    @Query("SELECT * FROM content_sources WHERE id = :id")
    suspend fun getContentSourceById(id: Long): ContentSourceEntity?

    @Query("SELECT * FROM content_sources WHERE contentType = :contentType")
    fun getSourcesByContentType(contentType: ContentType): Flow<List<ContentSourceEntity>>

    @Query("SELECT * FROM content_sources WHERE scriptType = :scriptType")
    suspend fun getSourceByScript(scriptType: ScriptType): ContentSourceEntity?

    @Query("SELECT COUNT(*) FROM content_sources WHERE checksum IS NULL OR checksum = ''")
    suspend fun getBlankChecksumCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllContentSources(sources: List<ContentSourceEntity>)
}
