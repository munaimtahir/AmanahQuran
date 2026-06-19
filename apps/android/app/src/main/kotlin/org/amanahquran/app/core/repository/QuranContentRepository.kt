package org.amanahquran.app.core.repository

import org.amanahquran.app.core.database.dao.AyahDao
import org.amanahquran.app.core.database.dao.AyahDisplayRow
import org.amanahquran.app.core.database.dao.MushafLayoutReferenceDao
import org.amanahquran.app.core.database.dao.QuranTextDao
import org.amanahquran.app.core.database.dao.SurahDao
import org.amanahquran.app.core.model.JuzListItem
import org.amanahquran.app.core.model.PageListItem
import org.amanahquran.app.core.model.PageReferenceType
import org.amanahquran.app.core.model.ReaderOpenMode

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
    suspend fun getJuzList(): List<JuzListItem>
    suspend fun getAyahsForJuz(juzNumber: Int, scriptType: String): List<AyahDisplay>
    suspend fun getPageList(pageReferenceType: PageReferenceType): List<PageListItem>
    suspend fun getAyahsForPage(pageNumber: Int, pageReferenceType: PageReferenceType, scriptType: String): List<AyahDisplay>
    suspend fun getPageForAyah(ayahKey: String, pageReferenceType: PageReferenceType): Int?
    suspend fun getJuzForAyah(ayahKey: String): Int?
    suspend fun getFirstAyahForPage(pageNumber: Int, pageReferenceType: PageReferenceType): String?
    suspend fun getFirstAyahForJuz(juzNumber: Int): String?
    suspend fun getReaderAyahs(openMode: ReaderOpenMode, scriptType: String): List<AyahDisplay>
}

class QuranContentRepositoryImpl(
    private val surahDao: SurahDao,
    private val ayahDao: AyahDao,
    private val quranTextDao: QuranTextDao,
    private val mushafLayoutReferenceDao: MushafLayoutReferenceDao,
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

    override suspend fun getJuzList(): List<JuzListItem> {
        return (1..30).mapNotNull { juzNumber ->
            val ayahs = ayahDao.getAyahsByJuz(juzNumber, "INDOPAK")
            val firstAyahKey = ayahs.firstOrNull()?.ayahKey ?: return@mapNotNull null
            val lastAyahKey = ayahs.lastOrNull()?.ayahKey
            val startAyah = ayahDao.getAyahByKey(firstAyahKey) ?: return@mapNotNull null
            val endAyah = lastAyahKey?.let { ayahDao.getAyahByKey(it) }
            val startSurah = surahDao.getSurahByNumber(startAyah.surahNumber)
            val endSurah = endAyah?.let { surahDao.getSurahByNumber(it.surahNumber) }
            JuzListItem(
                juzNumber = juzNumber,
                startAyahKey = firstAyahKey,
                endAyahKey = lastAyahKey,
                startSurahNumber = startAyah.surahNumber,
                startSurahName = startSurah?.nameSimple?.ifBlank { "Surah ${startAyah.surahNumber}" } ?: "Surah ${startAyah.surahNumber}",
                endSurahNumber = endAyah?.surahNumber,
                endSurahName = endSurah?.nameSimple?.ifBlank { "Surah ${endAyah.surahNumber}" },
                ayahCount = ayahDao.getAyahCountForJuz(juzNumber),
            )
        }
    }

    override suspend fun getAyahsForJuz(juzNumber: Int, scriptType: String): List<AyahDisplay> {
        return ayahDao.getAyahsByJuz(juzNumber, scriptType).map { row ->
            AyahDisplay(
                ayahKey = row.ayahKey,
                surahNumber = row.surahNumber,
                ayahNumber = row.ayahNumber,
                juzNumber = row.juzNumber,
                pageNumber = row.pageNumber,
                scriptType = row.scriptType,
                displayText = row.displayText,
            )
        }
    }

    override suspend fun getPageList(pageReferenceType: PageReferenceType): List<PageListItem> {
        return mushafLayoutReferenceDao.getReferencesForLayout(pageReferenceType.layoutName).mapNotNull { reference ->
            val startAyah = reference.firstAyahKey?.let { ayahDao.getAyahByKey(it) } ?: return@mapNotNull null
            val endAyah = reference.lastAyahKey?.let { ayahDao.getAyahByKey(it) }
            val startSurah = surahDao.getSurahByNumber(startAyah.surahNumber)
            val endSurah = endAyah?.let { surahDao.getSurahByNumber(it.surahNumber) }
            PageListItem(
                pageNumber = reference.pageNumber,
                pageReferenceType = pageReferenceType,
                startAyahKey = startAyah.ayahKey,
                endAyahKey = endAyah?.ayahKey,
                startSurahNumber = startAyah.surahNumber,
                startSurahName = startSurah?.nameSimple?.ifBlank { "Surah ${startAyah.surahNumber}" } ?: "Surah ${startAyah.surahNumber}",
                endSurahNumber = endAyah?.surahNumber,
                endSurahName = endSurah?.nameSimple?.ifBlank { "Surah ${endAyah.surahNumber}" },
                ayahCount = ayahDao.getAyahCountForPage(reference.pageNumber),
            )
        }
    }

    override suspend fun getAyahsForPage(pageNumber: Int, pageReferenceType: PageReferenceType, scriptType: String): List<AyahDisplay> {
        return ayahDao.getAyahsByPage(pageNumber, scriptType).map { row ->
            AyahDisplay(
                ayahKey = row.ayahKey,
                surahNumber = row.surahNumber,
                ayahNumber = row.ayahNumber,
                juzNumber = row.juzNumber,
                pageNumber = row.pageNumber,
                scriptType = row.scriptType,
                displayText = row.displayText,
            )
        }
    }

    override suspend fun getPageForAyah(ayahKey: String, pageReferenceType: PageReferenceType): Int? {
        return ayahDao.getPageNumberForAyah(ayahKey)
    }

    override suspend fun getJuzForAyah(ayahKey: String): Int? = ayahDao.getJuzNumberForAyah(ayahKey)

    override suspend fun getFirstAyahForPage(pageNumber: Int, pageReferenceType: PageReferenceType): String? {
        return mushafLayoutReferenceDao.getFirstAyahKeyForPage(pageNumber, pageReferenceType.layoutName)
            ?: ayahDao.getFirstAyahKeyForPage(pageNumber)
    }

    override suspend fun getFirstAyahForJuz(juzNumber: Int): String? = ayahDao.getFirstAyahKeyForJuz(juzNumber)

    override suspend fun getReaderAyahs(openMode: ReaderOpenMode, scriptType: String): List<AyahDisplay> {
        return when (openMode) {
            is ReaderOpenMode.Surah -> getAyahsForSurah(openMode.surahNumber, scriptType)
            is ReaderOpenMode.Page -> getAyahsForPage(openMode.pageNumber, openMode.pageReferenceType, scriptType)
            is ReaderOpenMode.Juz -> getAyahsForJuz(openMode.juzNumber, scriptType)
            is ReaderOpenMode.AyahTarget -> {
                val display = getAyahDisplay(openMode.ayahKey, scriptType) ?: return emptyList()
                listOf(display)
            }
        }
    }
}
