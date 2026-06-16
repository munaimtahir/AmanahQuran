package org.amanahquran.app.core.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.amanahquran.app.core.database.entity.LastReadEntity

@Dao
interface LastReadDao {
    @Query("SELECT * FROM last_read WHERE id = 1")
    fun getLastRead(): Flow<LastReadEntity?>

    @Upsert
    suspend fun upsertLastRead(lastRead: LastReadEntity)
}
