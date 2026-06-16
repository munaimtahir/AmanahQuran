package org.amanahquran.app.core.repository

import kotlinx.coroutines.flow.Flow
import org.amanahquran.app.core.database.dao.AyahDao
import org.amanahquran.app.core.database.dao.QuranTextDao
import org.amanahquran.app.core.database.dao.SurahDao
import org.amanahquran.app.core.database.entity.AyahEntity
import org.amanahquran.app.core.database.entity.QuranTextEntity
import org.amanahquran.app.core.database.entity.SurahEntity
import org.amanahquran.app.core.model.ScriptType

interface QuranRepository {
    fun getAllSurahs(): Flow<List<SurahEntity>>
    suspend fun getSurahByNumber(number: Int): SurahEntity?
    fun getAyahsBySurah(surahNumber: Int): Flow<List<AyahEntity>>
    fun getTextsBySurah(surahNumber: Int, scriptType: ScriptType): Flow<List<QuranTextEntity>>
    fun getTextsByJuz(juzNumber: Int, scriptType: ScriptType): Flow<List<QuranTextEntity>>
    fun getTextsByPage(pageNumber: Int, scriptType: ScriptType): Flow<List<QuranTextEntity>>
    suspend fun getAyahText(ayahKey: String, scriptType: ScriptType): QuranTextEntity?
}

class QuranRepositoryImpl(
    private val surahDao: SurahDao,
    private val ayahDao: AyahDao,
    private val quranTextDao: QuranTextDao
) : QuranRepository {
    override fun getAllSurahs(): Flow<List<SurahEntity>> = surahDao.getAllSurahs()

    override suspend fun getSurahByNumber(number: Int): SurahEntity? = surahDao.getSurahByNumber(number)

    override fun getAyahsBySurah(surahNumber: Int): Flow<List<AyahEntity>> = ayahDao.getAyahsBySurah(surahNumber)

    override fun getTextsBySurah(surahNumber: Int, scriptType: ScriptType): Flow<List<QuranTextEntity>> =
        quranTextDao.getTextsBySurahAndScript(surahNumber, scriptType)

    override fun getTextsByJuz(juzNumber: Int, scriptType: ScriptType): Flow<List<QuranTextEntity>> =
        quranTextDao.getTextsByJuzAndScript(juzNumber, scriptType)

    override fun getTextsByPage(pageNumber: Int, scriptType: ScriptType): Flow<List<QuranTextEntity>> =
        quranTextDao.getTextsByPageAndScript(pageNumber, scriptType)

    override suspend fun getAyahText(ayahKey: String, scriptType: ScriptType): QuranTextEntity? =
        quranTextDao.getTextByAyahAndScript(ayahKey, scriptType)
}
