package org.amanahquran.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.amanahquran.app.core.database.entity.ContentManifestEntity

@Dao
interface ContentManifestDao {
    @Query("SELECT * FROM content_manifests ORDER BY generatedAt DESC LIMIT 1")
    fun getLatestManifest(): Flow<ContentManifestEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManifest(manifest: ContentManifestEntity)
}
