package org.amanahquran.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.amanahquran.app.core.database.entity.ContentValidationEntity

@Dao
interface ContentValidationDao {
    @Query("SELECT * FROM content_validations ORDER BY checkedAt DESC")
    fun getAllValidationResults(): Flow<List<ContentValidationEntity>>

    @Query("SELECT * FROM content_validations WHERE passed = 0")
    fun getFailedValidationResults(): Flow<List<ContentValidationEntity>>

    @Query("SELECT * FROM content_validations WHERE passed = 0 AND severity = 'CRITICAL'")
    fun getCriticalFailedValidationResults(): Flow<List<ContentValidationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertValidationResult(result: ContentValidationEntity)

    @Query("DELETE FROM content_validations")
    suspend fun clearValidationResults()
}
