package org.amanahquran.app.core.repository

import org.amanahquran.app.core.database.dao.AyahDao
import org.amanahquran.app.core.database.dao.QuranTextDao
import org.amanahquran.app.core.database.dao.SearchIndexDao
import org.amanahquran.app.core.database.dao.SurahDao
import org.amanahquran.app.core.database.entity.SearchIndexEntity
import org.amanahquran.app.core.model.PageReferenceType
import org.amanahquran.app.core.model.ScriptType

data class SearchResultDisplay(
    val ayahKey: String,
    val scriptType: String,
    val displayText: String,
)

enum class SearchResultType {
    SURAH,
    AYAH,
    JUZ,
    PAGE,
}

data class SearchResultItem(
    val resultType: SearchResultType,
    val title: String,
    val subtitle: String,
    val ayahKey: String?,
    val surahNumber: Int?,
    val ayahNumber: Int?,
    val pageNumber: Int?,
    val pageReferenceType: PageReferenceType?,
    val juzNumber: Int?,
    val previewText: String?,
)

interface SearchRepository {
    suspend fun search(query: String, scriptType: ScriptType): List<SearchResultItem>
    suspend fun searchNormalizedArabic(query: String, scriptType: String): List<SearchResultDisplay>
    suspend fun getSearchRow(ayahKey: String): SearchIndexEntity?
}

class SearchRepositoryImpl(
    private val searchIndexDao: SearchIndexDao,
    private val quranTextDao: QuranTextDao,
    private val surahDao: SurahDao? = null,
    private val ayahDao: AyahDao? = null,
) : SearchRepository {
    override suspend fun search(query: String, scriptType: ScriptType): List<SearchResultItem> {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return emptyList()

        parseAyahReference(trimmed)?.let { (surahNumber, ayahNumber) ->
            val ayahKey = "$surahNumber:$ayahNumber"
            val display = quranTextDao.getTextByAyahAndScript(ayahKey, scriptType.name)
            val surah = surahDao?.getSurahByNumber(surahNumber)
            return listOf(
                SearchResultItem(
                    resultType = SearchResultType.AYAH,
                    title = surah?.nameSimple?.takeIf { it.isNotBlank() } ?: "Surah $surahNumber",
                    subtitle = "$surahNumber:$ayahNumber",
                    ayahKey = ayahKey,
                    surahNumber = surahNumber,
                    ayahNumber = ayahNumber,
                    pageNumber = ayahDao?.getAyahByKey(ayahKey)?.pageNumber,
                    pageReferenceType = scriptType.toPageReferenceType(),
                    juzNumber = ayahDao?.getAyahByKey(ayahKey)?.juzNumber,
                    previewText = display?.displayText,
                ),
            )
        }

        parseNumberedPrefix(trimmed, "surah")?.let { surahNumber ->
            return searchSurahByNumberOrName(surahNumber, scriptType)
        }

        parseNumberedPrefix(trimmed, "juz")?.let { juzNumber ->
            return searchJuz(juzNumber, scriptType)
        }

        parseNumberedPrefix(trimmed, "page")?.let { pageNumber ->
            return searchPage(pageNumber, scriptType)
        }

        trimmed.toIntOrNull()?.let { number ->
            if (number in 1..114) {
                return searchSurahByNumberOrName(number, scriptType)
            }
            if (number in 1..30) {
                return searchJuz(number, scriptType)
            }
            if (number in 1..604) {
                return searchPage(number, scriptType)
            }
        }

        val surahMatches = surahDao?.getAllSurahs().orEmpty()
            .filter { surah ->
                surah.number.toString() == trimmed ||
                    surah.nameSimple.contains(trimmed, ignoreCase = true) ||
                    surah.nameArabic.contains(trimmed, ignoreCase = true)
            }
            .map { surah ->
                SearchResultItem(
                    resultType = SearchResultType.SURAH,
                    title = surah.nameSimple,
                    subtitle = "Surah ${surah.number}",
                    ayahKey = null,
                    surahNumber = surah.number,
                    ayahNumber = null,
                    pageNumber = null,
                    pageReferenceType = null,
                    juzNumber = null,
                    previewText = quranTextDao.getTextsForSurah(surah.number, scriptType.name).firstOrNull()?.displayText,
                )
            }

        val arabicMatches = searchNormalizedArabic(trimmed.normalizeForSearch(), scriptType.name)
            .map { display ->
                val ayah = ayahDao?.getAyahByKey(display.ayahKey)
                val quran = quranTextDao.getTextByAyahAndScript(display.ayahKey, scriptType.name)
                SearchResultItem(
                    resultType = SearchResultType.AYAH,
                    title = ayah?.let { "Surah ${it.surahNumber}" } ?: display.ayahKey,
                    subtitle = display.ayahKey,
                ayahKey = display.ayahKey,
                surahNumber = ayah?.surahNumber,
                ayahNumber = ayah?.ayahNumber,
                pageNumber = ayah?.pageNumber,
                pageReferenceType = null,
                juzNumber = ayah?.juzNumber,
                previewText = quran?.displayText,
            )
        }

        return (surahMatches + arabicMatches).distinctBy { it.resultType to it.ayahKey to it.surahNumber to it.pageNumber to it.juzNumber }
    }

    override suspend fun searchNormalizedArabic(query: String, scriptType: String): List<SearchResultDisplay> {
        return searchIndexDao.searchNormalizedArabic(query.normalizeForSearch()).mapNotNull { row ->
            val displayText = quranTextDao.getTextByAyahAndScript(row.ayahKey, scriptType) ?: return@mapNotNull null
            SearchResultDisplay(
                ayahKey = row.ayahKey,
                scriptType = displayText.scriptType,
                displayText = displayText.displayText,
            )
        }
    }

    override suspend fun getSearchRow(ayahKey: String): SearchIndexEntity? = searchIndexDao.getSearchRow(ayahKey)

    private suspend fun searchSurahByNumberOrName(
        surahNumber: Int,
        scriptType: ScriptType,
    ): List<SearchResultItem> {
        val surah = surahDao?.getSurahByNumber(surahNumber) ?: return emptyList()
        return listOf(
            SearchResultItem(
                resultType = SearchResultType.SURAH,
                title = surah.nameSimple,
                subtitle = "Surah ${surah.number}",
                ayahKey = null,
                surahNumber = surah.number,
                ayahNumber = null,
                pageNumber = null,
                pageReferenceType = null,
                juzNumber = null,
                previewText = quranTextDao.getTextsForSurah(surah.number, scriptType.name).firstOrNull()?.displayText,
            ),
        )
    }

    private suspend fun searchJuz(
        juzNumber: Int,
        scriptType: ScriptType,
    ): List<SearchResultItem> {
        val ayahs = ayahDao?.getAyahsByJuz(juzNumber).orEmpty()
        return ayahs.take(10).mapNotNull { ayah ->
            val display = quranTextDao.getTextByAyahAndScript(ayah.ayahKey, scriptType.name) ?: return@mapNotNull null
            SearchResultItem(
                resultType = SearchResultType.JUZ,
                title = "Juz $juzNumber",
                subtitle = ayah.ayahKey,
                ayahKey = ayah.ayahKey,
                surahNumber = ayah.surahNumber,
                ayahNumber = ayah.ayahNumber,
                pageNumber = ayah.pageNumber,
                pageReferenceType = scriptType.toPageReferenceType(),
                juzNumber = ayah.juzNumber,
                previewText = display.displayText,
            )
        }
    }

    private suspend fun searchPage(
        pageNumber: Int,
        scriptType: ScriptType,
    ): List<SearchResultItem> {
        val ayahs = ayahDao?.getAyahsByPageIndopak(pageNumber).orEmpty()
        return ayahs.take(10).mapNotNull { ayah ->
            val display = quranTextDao.getTextByAyahAndScript(ayah.ayahKey, scriptType.name) ?: return@mapNotNull null
            SearchResultItem(
                resultType = SearchResultType.PAGE,
                title = "Page $pageNumber",
                subtitle = ayah.ayahKey,
                ayahKey = ayah.ayahKey,
                surahNumber = ayah.surahNumber,
                ayahNumber = ayah.ayahNumber,
                pageNumber = ayah.pageNumber,
                pageReferenceType = scriptType.toPageReferenceType(),
                juzNumber = ayah.juzNumber,
                previewText = display.displayText,
            )
        }
    }

    private fun parseAyahReference(query: String): Pair<Int, Int>? {
        val match = Regex("""^(\d+)\s*:\s*(\d+)$""").find(query) ?: return null
        val surahNumber = match.groupValues[1].toIntOrNull() ?: return null
        val ayahNumber = match.groupValues[2].toIntOrNull() ?: return null
        return surahNumber to ayahNumber
    }

    private fun parseNumberedPrefix(query: String, label: String): Int? {
        val regexes = listOf(
            Regex("""^$label\s+(\d+)$""", RegexOption.IGNORE_CASE),
            Regex("""^(\d+)\s+$label$""", RegexOption.IGNORE_CASE),
        )
        for (regex in regexes) {
            val match = regex.find(query) ?: continue
            return match.groupValues[1].toIntOrNull()
        }
        return null
    }

    private fun String.normalizeForSearch(): String {
        return lowercase()
            .replace(Regex("[\\u064B-\\u065F\\u0670\\u0640]"), "")
            .replace("أ", "ا")
            .replace("إ", "ا")
            .replace("آ", "ا")
            .replace("ى", "ي")
            .replace("ؤ", "و")
            .replace("ئ", "ي")
            .trim()
    }

    private fun ScriptType.toPageReferenceType(): PageReferenceType = when (this) {
        ScriptType.INDOPAK -> PageReferenceType.INDOPAK
        ScriptType.UTHMANI -> PageReferenceType.UTHMANI
    }
}
