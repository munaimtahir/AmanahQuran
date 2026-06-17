package org.amanahquran.app.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import org.amanahquran.app.core.database.entity.QuranTextEntity

@Dao
interface QuranTextDao {
    @Query("SELECT COUNT(*) FROM quran_texts WHERE script_type = :scriptType")
    suspend fun getTextCountByScript(scriptType: String): Int

    @Query("SELECT * FROM quran_texts WHERE ayah_key = :ayahKey AND script_type = :scriptType")
    suspend fun getTextByAyahAndScript(ayahKey: String, scriptType: String): QuranTextEntity?

    @Query("""
        SELECT qt.* FROM quran_texts qt
        INNER JOIN ayahs a ON qt.ayah_key = a.ayah_key
        WHERE a.surah_number = :surahNumber AND qt.script_type = :scriptType
        ORDER BY a.ayah_number ASC
    """)
    suspend fun getTextsForSurah(surahNumber: Int, scriptType: String): List<QuranTextEntity>

    @Query("SELECT * FROM quran_texts WHERE script_type = :scriptType ORDER BY id ASC LIMIT :limit")
    suspend fun getSampleTexts(scriptType: String, limit: Int = 5): List<QuranTextEntity>

    @Query("SELECT COUNT(*) FROM quran_texts WHERE script_type = :scriptType AND display_text = ''")
    suspend fun getBlankDisplayTextCount(scriptType: String): Int
}
