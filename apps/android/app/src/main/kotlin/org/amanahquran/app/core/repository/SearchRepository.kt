package org.amanahquran.app.core.repository

import org.amanahquran.app.content.translation.TranslationDao
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
    val translationText: String? = null,
)

interface SearchRepository {
    /** [translationId] is the currently selected translation (see [org.amanahquran.app.core.model.TranslationSelection]), or null when translation is Off -- search then covers Arabic text only. */
    suspend fun search(query: String, scriptType: ScriptType, translationId: String? = null): List<SearchResultItem>
    suspend fun searchNormalizedArabic(query: String, scriptType: String): List<SearchResultDisplay>
    suspend fun getSearchRow(ayahKey: String): SearchIndexEntity?
}

class SearchRepositoryImpl(
    private val searchIndexDao: SearchIndexDao,
    private val quranTextDao: QuranTextDao,
    private val surahDao: SurahDao? = null,
    private val ayahDao: AyahDao? = null,
    private val translationDao: TranslationDao? = null,
) : SearchRepository {
    override suspend fun search(query: String, scriptType: ScriptType, translationId: String?): List<SearchResultItem> {
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

        findSurahAlias(trimmed)?.let { surahNumber ->
            return searchSurahByNumberOrName(surahNumber, scriptType)
        }

        val normalizedNameQuery = trimmed.normalizeSurahName()
        val surahMatches = surahDao?.getAllSurahs().orEmpty()
            .filter { surah ->
                surah.number.toString() == trimmed ||
                    surah.nameSimple.contains(trimmed, ignoreCase = true) ||
                    surah.nameArabic.contains(trimmed, ignoreCase = true) ||
                    surah.nameSimple.normalizeSurahName().contains(normalizedNameQuery)
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

        val translationMatches = searchTranslation(trimmed, scriptType, translationId)

        return (surahMatches + mergeAyahMatches(arabicMatches, translationMatches))
            .distinctBy { it.resultType to it.ayahKey to it.surahNumber to it.pageNumber to it.juzNumber }
    }

    private suspend fun searchTranslation(query: String, scriptType: ScriptType, translationId: String?): List<SearchResultItem> {
        val dao = translationDao ?: return emptyList()
        if (translationId == null) return emptyList()
        val normalized = query.normalizeUrduForSearch()
        if (normalized.isBlank()) return emptyList()
        return dao.search(translationId, normalized, 50).mapNotNull { translation ->
            if (translation.displayText.isNullOrBlank()) return@mapNotNull null
            val ayah = ayahDao?.getAyahByKey(translation.ayahKey)
            val quran = quranTextDao.getTextByAyahAndScript(translation.ayahKey, scriptType.name)
            SearchResultItem(
                resultType = SearchResultType.AYAH,
                title = ayah?.let { "Surah ${it.surahNumber}" } ?: translation.ayahKey,
                subtitle = translation.ayahKey,
                ayahKey = translation.ayahKey,
                surahNumber = ayah?.surahNumber,
                ayahNumber = ayah?.ayahNumber,
                pageNumber = ayah?.pageNumber,
                pageReferenceType = ayah?.let { scriptType.toPageReferenceType() },
                juzNumber = ayah?.juzNumber,
                previewText = quran?.displayText,
                translationText = translation.displayText,
            )
        }
    }

    private fun mergeAyahMatches(
        primary: List<SearchResultItem>,
        secondary: List<SearchResultItem>,
    ): List<SearchResultItem> {
        val byKey = linkedMapOf<String, SearchResultItem>()
        primary.forEach { item -> item.ayahKey?.let { byKey[it] = item } }
        secondary.forEach { item ->
            val key = item.ayahKey ?: return@forEach
            val existing = byKey[key]
            byKey[key] = if (existing != null) existing.copy(translationText = item.translationText) else item
        }
        return byKey.values.toList()
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

    private fun String.normalizeUrduForSearch(): String {
        return replace('ـ'.toString(), "")
            .replace(Regex("[\\u064B-\\u065F\\u0670]"), "")
            .replace(Regex("\\s+"), " ")
            .trim()
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

    private fun findSurahAlias(query: String): Int? {
        return SURAH_ALIASES[query.normalizeSurahName()]
    }

    private fun String.normalizeSurahName(): String {
        return lowercase()
            .replace(Regex("[^\\p{L}\\p{N}]"), "")
    }

    private fun ScriptType.toPageReferenceType(): PageReferenceType = when (this) {
        ScriptType.INDOPAK -> PageReferenceType.INDOPAK
        ScriptType.UTHMANI -> PageReferenceType.UTHMANI
    }

    private companion object {
        val SURAH_ALIASES = mapOf(
            "yaseen" to 36,
            "yasin" to 36,
            "يس" to 36,
            "ikhlas" to 112,
            "alikhlas" to 112,
            "fatiha" to 1,
            "fatihah" to 1,
            "alfatihah" to 1,
            "baqarah" to 2,
            "albaqarah" to 2,
            "mulk" to 67,
            "almulk" to 67,
            "nas" to 114,
            "annas" to 114,
            "falaq" to 113,
            "alfalaq" to 113,
            "tawbah" to 9,
            "attawbah" to 9,
            "aalimran" to 3,
            "alimran" to 3,
            "aliimran" to 3,
            "aaleimran" to 3,
        )
    }
}
