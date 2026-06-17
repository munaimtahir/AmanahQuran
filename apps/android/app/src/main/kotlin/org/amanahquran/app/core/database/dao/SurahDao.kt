package org.amanahquran.app.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import org.amanahquran.app.core.database.entity.SurahEntity

@Dao
interface SurahDao {
    @Query("SELECT COUNT(*) FROM surahs")
    suspend fun getSurahCount(): Int

    @Query("SELECT * FROM surahs ORDER BY number ASC")
    suspend fun getAllSurahs(): List<SurahEntity>

    @Query("SELECT * FROM surahs WHERE number = :number")
    suspend fun getSurahByNumber(number: Int): SurahEntity?
}
