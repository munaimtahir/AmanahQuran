package org.amanahquran.app.core.repository

import org.amanahquran.app.core.database.dao.AyahDao
import org.amanahquran.app.core.database.dao.QuranTextDao
import org.amanahquran.app.core.database.dao.SurahDao

data class AyahDisplay(
    val ayahKey: String,
    val surahNumber: Int,
    val ayahNumber: Int,
    val juzNumber: Int,
    val pageNumber: Int,
    val scriptType: String,
    val displayText: String,
)

data class SurahInfo(
    val number: Int,
    val nameArabic: String,
    val nameSimple: String,
    val revelationType: String?,
    val ayahCount: Int,
)

interface QuranContentRepository {
    suspend fun getSurahCount(): Int
    suspend fun getAyahCount(): Int
    suspend fun getAllSurahs(): List<SurahInfo>
    suspend fun getSurahByNumber(number: Int): SurahInfo?
    suspend fun getAyahDisplay(ayahKey: String, scriptType: String): AyahDisplay?
    suspend fun getAyahsForSurah(surahNumber: Int, scriptType: String): List<AyahDisplay>
}

class QuranContentRepositoryImpl(
    private val surahDao: SurahDao,
    private val ayahDao: AyahDao,
    private val quranTextDao: QuranTextDao,
) : QuranContentRepository {
    override suspend fun getSurahCount(): Int = surahDao.getSurahCount()

    override suspend fun getAyahCount(): Int = ayahDao.getAyahCount()

    override suspend fun getAllSurahs(): List<SurahInfo> = surahDao.getAllSurahs().map { surah ->
        SurahInfo(
            number = surah.number,
            nameArabic = surah.nameArabic,
            nameSimple = surah.nameSimple,
            revelationType = surah.revelationType,
            ayahCount = surah.ayahCount,
        )
    }

    override suspend fun getSurahByNumber(number: Int): SurahInfo? = surahDao.getSurahByNumber(number)?.let { surah ->
        SurahInfo(
            number = surah.number,
            nameArabic = surah.nameArabic,
            nameSimple = surah.nameSimple,
            revelationType = surah.revelationType,
            ayahCount = surah.ayahCount,
        )
    }

    override suspend fun getAyahDisplay(ayahKey: String, scriptType: String): AyahDisplay? {
        val ayah = ayahDao.getAyahByKey(ayahKey) ?: return null
        val text = quranTextDao.getTextByAyahAndScript(ayahKey, scriptType) ?: return null
        return AyahDisplay(
            ayahKey = ayah.ayahKey,
            surahNumber = ayah.surahNumber,
            ayahNumber = ayah.ayahNumber,
            juzNumber = ayah.juzNumber,
            pageNumber = ayah.pageNumber,
            scriptType = text.scriptType,
            displayText = text.displayText,
        )
    }

    override suspend fun getAyahsForSurah(surahNumber: Int, scriptType: String): List<AyahDisplay> {
        val ayahsByKey = ayahDao.getAyahsBySurah(surahNumber).associateBy { it.ayahKey }
        return quranTextDao.getTextsForSurah(surahNumber, scriptType).mapNotNull { text ->
            val ayah = ayahsByKey[text.ayahKey] ?: return@mapNotNull null
            AyahDisplay(
                ayahKey = ayah.ayahKey,
                surahNumber = ayah.surahNumber,
                ayahNumber = ayah.ayahNumber,
                juzNumber = ayah.juzNumber,
                pageNumber = ayah.pageNumber,
                scriptType = text.scriptType,
                displayText = text.displayText,
            )
        }
    }
}
