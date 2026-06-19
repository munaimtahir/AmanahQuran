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

    @Query("""
        SELECT
            a.ayah_key AS ayahKey,
            a.surah_number AS surahNumber,
            a.ayah_number AS ayahNumber,
            a.juz_number AS juzNumber,
            a.page_number AS pageNumber,
            qt.script_type AS scriptType,
            qt.display_text AS displayText
        FROM ayahs a
        INNER JOIN quran_texts qt ON qt.ayah_key = a.ayah_key
        WHERE a.page_number = :pageNumber AND qt.script_type = :scriptType
        ORDER BY a.surah_number ASC, a.ayah_number ASC
    """)
    suspend fun getAyahsByPage(pageNumber: Int, scriptType: String): List<AyahDisplayRow>

    @Query("""
        SELECT
            a.ayah_key AS ayahKey,
            a.surah_number AS surahNumber,
            a.ayah_number AS ayahNumber,
            a.juz_number AS juzNumber,
            a.page_number AS pageNumber,
            qt.script_type AS scriptType,
            qt.display_text AS displayText
        FROM ayahs a
        INNER JOIN quran_texts qt ON qt.ayah_key = a.ayah_key
        WHERE a.juz_number = :juzNumber AND qt.script_type = :scriptType
        ORDER BY a.surah_number ASC, a.ayah_number ASC
    """)
    suspend fun getAyahsByJuz(juzNumber: Int, scriptType: String): List<AyahDisplayRow>

    @Query("SELECT ayah_key FROM ayahs WHERE page_number = :pageNumber ORDER BY surah_number ASC, ayah_number ASC LIMIT 1")
    suspend fun getFirstAyahKeyForPage(pageNumber: Int): String?

    @Query("SELECT ayah_key FROM ayahs WHERE juz_number = :juzNumber ORDER BY surah_number ASC, ayah_number ASC LIMIT 1")
    suspend fun getFirstAyahKeyForJuz(juzNumber: Int): String?

    @Query("SELECT page_number FROM ayahs WHERE ayah_key = :ayahKey LIMIT 1")
    suspend fun getPageNumberForAyah(ayahKey: String): Int?

    @Query("SELECT juz_number FROM ayahs WHERE ayah_key = :ayahKey LIMIT 1")
    suspend fun getJuzNumberForAyah(ayahKey: String): Int?

    @Query("SELECT COUNT(*) FROM ayahs WHERE page_number = :pageNumber")
    suspend fun getAyahCountForPage(pageNumber: Int): Int

    @Query("SELECT COUNT(*) FROM ayahs WHERE juz_number = :juzNumber")
    suspend fun getAyahCountForJuz(juzNumber: Int): Int

    @Query("SELECT COUNT(*) FROM (SELECT ayah_key FROM ayahs GROUP BY ayah_key HAVING COUNT(*) > 1)")
    suspend fun getDuplicateAyahKeyCount(): Int
}
