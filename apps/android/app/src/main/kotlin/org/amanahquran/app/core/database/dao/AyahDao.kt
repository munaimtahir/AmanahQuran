package org.amanahquran.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.amanahquran.app.core.database.entity.AyahEntity

@Dao
interface AyahDao {
    @Query("SELECT * FROM ayahs WHERE ayahKey = :ayahKey")
    suspend fun getAyahByKey(ayahKey: String): AyahEntity?

    @Query("SELECT * FROM ayahs WHERE surahNumber = :surahNumber ORDER BY ayahNumber ASC")
    fun getAyahsBySurah(surahNumber: Int): Flow<List<AyahEntity>>

    @Query("SELECT * FROM ayahs WHERE juzNumber = :juzNumber ORDER BY surahNumber ASC, ayahNumber ASC")
    fun getAyahsByJuz(juzNumber: Int): Flow<List<AyahEntity>>

    @Query("SELECT * FROM ayahs WHERE pageNumber = :pageNumber ORDER BY surahNumber ASC, ayahNumber ASC")
    fun getAyahsByPage(pageNumber: Int): Flow<List<AyahEntity>>

    @Query("SELECT COUNT(*) FROM ayahs")
    suspend fun getAyahCount(): Int

    @Query("SELECT COUNT(*) FROM (SELECT ayahKey FROM ayahs GROUP BY ayahKey HAVING COUNT(*) > 1)")
    suspend fun getDuplicateAyahKeyCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAyahs(ayahs: List<AyahEntity>)
}
