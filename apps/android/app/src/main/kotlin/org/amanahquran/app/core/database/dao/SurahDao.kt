package org.amanahquran.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.amanahquran.app.core.database.entity.SurahEntity

@Dao
interface SurahDao {
    @Query("SELECT * FROM surahs ORDER BY surahNumber ASC")
    fun getAllSurahs(): Flow<List<SurahEntity>>

    @Query("SELECT * FROM surahs WHERE surahNumber = :number")
    suspend fun getSurahByNumber(number: Int): SurahEntity?

    @Query("SELECT COUNT(*) FROM surahs")
    suspend fun getSurahCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSurahs(surahs: List<SurahEntity>)
}
