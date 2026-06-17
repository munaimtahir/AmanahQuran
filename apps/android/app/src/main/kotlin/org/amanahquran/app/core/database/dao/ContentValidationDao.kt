package org.amanahquran.app.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import org.amanahquran.app.core.database.entity.ContentValidationEntity

@Dao
interface ContentValidationDao {
    @Query("SELECT * FROM content_validation ORDER BY id ASC")
    suspend fun getContentValidationRows(): List<ContentValidationEntity>

    @Query("SELECT COUNT(*) FROM content_validation")
    suspend fun getContentValidationCount(): Int

    @Query("SELECT COUNT(*) FROM content_validation WHERE passed = 0")
    suspend fun getFailedValidationCount(): Int
}
