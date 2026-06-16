package org.amanahquran.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.amanahquran.app.core.database.entity.QuranTextEntity
import org.amanahquran.app.core.model.ScriptType

@Dao
interface QuranTextDao {
    @Query("SELECT * FROM quran_text WHERE ayahKey = :ayahKey AND scriptType = :scriptType")
    suspend fun getTextByAyahAndScript(ayahKey: String, scriptType: ScriptType): QuranTextEntity?

    @Query("""
        SELECT qt.* FROM quran_text qt 
        INNER JOIN ayahs a ON qt.ayahKey = a.ayahKey 
        WHERE a.surahNumber = :surahNumber AND qt.scriptType = :scriptType 
        ORDER BY a.ayahNumber ASC
    """)
    fun getTextsBySurahAndScript(surahNumber: Int, scriptType: ScriptType): Flow<List<QuranTextEntity>>

    @Query("""
        SELECT qt.* FROM quran_text qt 
        INNER JOIN ayahs a ON qt.ayahKey = a.ayahKey 
        WHERE a.juzNumber = :juzNumber AND qt.scriptType = :scriptType 
        ORDER BY a.surahNumber ASC, a.ayahNumber ASC
    """)
    fun getTextsByJuzAndScript(juzNumber: Int, scriptType: ScriptType): Flow<List<QuranTextEntity>>

    @Query("""
        SELECT qt.* FROM quran_text qt 
        INNER JOIN ayahs a ON qt.ayahKey = a.ayahKey 
        WHERE a.pageNumber = :pageNumber AND qt.scriptType = :scriptType 
        ORDER BY a.surahNumber ASC, a.ayahNumber ASC
    """)
    fun getTextsByPageAndScript(pageNumber: Int, scriptType: ScriptType): Flow<List<QuranTextEntity>>

    @Query("SELECT COUNT(*) FROM quran_text WHERE scriptType = :scriptType")
    suspend fun getTextCountByScript(scriptType: ScriptType): Int

    @Query("SELECT COUNT(*) FROM quran_text WHERE scriptType = :scriptType AND (displayText IS NULL OR displayText = '')")
    suspend fun getBlankDisplayTextCount(scriptType: ScriptType): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllQuranTexts(texts: List<QuranTextEntity>)
}
