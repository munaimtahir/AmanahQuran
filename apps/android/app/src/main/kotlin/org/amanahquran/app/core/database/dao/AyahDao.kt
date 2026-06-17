package org.amanahquran.app.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import org.amanahquran.app.core.database.entity.AyahEntity

@Dao
interface AyahDao {
    @Query("SELECT COUNT(*) FROM ayahs")
    suspend fun getAyahCount(): Int

    @Query("SELECT * FROM ayahs WHERE ayah_key = :ayahKey")
    suspend fun getAyahByKey(ayahKey: String): AyahEntity?

    @Query("SELECT * FROM ayahs WHERE surah_number = :surahNumber ORDER BY ayah_number ASC")
    suspend fun getAyahsBySurah(surahNumber: Int): List<AyahEntity>

    @Query("SELECT * FROM ayahs WHERE page_number = :pageNumber ORDER BY surah_number ASC, ayah_number ASC")
    suspend fun getAyahsByPageIndopak(pageNumber: Int): List<AyahEntity>

    @Query("SELECT * FROM ayahs WHERE page_number = :pageNumber ORDER BY surah_number ASC, ayah_number ASC")
    suspend fun getAyahsByPageUthmani(pageNumber: Int): List<AyahEntity>

    @Query("SELECT * FROM ayahs WHERE juz_number = :juzNumber ORDER BY surah_number ASC, ayah_number ASC")
    suspend fun getAyahsByJuz(juzNumber: Int): List<AyahEntity>

    @Query("SELECT COUNT(*) FROM (SELECT ayah_key FROM ayahs GROUP BY ayah_key HAVING COUNT(*) > 1)")
    suspend fun getDuplicateAyahKeyCount(): Int
}
