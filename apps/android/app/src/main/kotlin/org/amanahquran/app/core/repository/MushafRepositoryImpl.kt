package org.amanahquran.app.core.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.amanahquran.app.core.database.AmanahContentDatabase
import org.amanahquran.app.core.database.AmanahQuranDatabase
import org.amanahquran.app.core.database.entity.MushafPageEntity
import org.amanahquran.app.core.database.entity.MushafLineEntity
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.model.PageReferenceType
import org.amanahquran.app.core.database.dao.AyahDisplayRow

class MushafRepositoryImpl(
    private val contentDatabase: AmanahContentDatabase,
    private val quranDatabase: AmanahQuranDatabase,
    private val bookmarkRepository: BookmarkRepository
) : MushafRepository {

    private val mushafDao = quranDatabase.mushafDao()
    private val layoutRefDao = contentDatabase.mushafLayoutReferenceDao()
    private val ayahDao = contentDatabase.ayahDao()
    private val surahDao = contentDatabase.surahDao()

    override suspend fun getMushafPage(pageNumber: Int, scriptType: ScriptType): Pair<MushafPageUi, List<MushafLineUi>> {
        initializePrototypeDataIfNeeded()
        return withContext(Dispatchers.IO) {
            val pageEntity = mushafDao.getPage(pageNumber)
            val lineEntities = mushafDao.getPageLines(pageNumber, scriptType.name)

            val linesUi = lineEntities.map {
                MushafLineUi(
                    lineNumber = it.lineNumber,
                    lineText = it.lineText,
                    containsSajdahMarker = it.containsSajdahMarker,
                    containsRukuMarker = it.containsRukuMarker
                )
            }

            fun buildPageUi(
                pageNumberValue: Int,
                juzNumber: Int?,
                paraNumber: Int?,
                surahNumberValue: Int?,
                surahLabelValue: String?,
                leftHeaderValue: String?,
                centerHeaderValue: String?,
                rightHeaderValue: String?,
                firstAyahKeyValue: String?,
                surahAyahCountValue: Int?,
                startsAtSurahBoundaryValue: Boolean,
            ) = MushafPageUi(
                pageNumber = pageNumberValue,
                juzNumber = juzNumber,
                paraNumber = paraNumber,
                surahNumber = surahNumberValue,
                surahLabel = surahLabelValue,
                leftHeader = leftHeaderValue,
                centerHeader = centerHeaderValue,
                rightHeader = rightHeaderValue,
                firstAyahKey = firstAyahKeyValue,
                surahAyahCount = surahAyahCountValue,
                startsAtSurahBoundary = startsAtSurahBoundaryValue,
            )

            val pageUi = if (lineEntities.isNotEmpty()) {
                val firstLine = lineEntities.first()
                val surahNum = firstLine.surahNumber ?: 1
                val surahEntity = surahDao.getSurahByNumber(surahNum)
                val surahSimple = surahEntity?.nameSimple ?: "Surah $surahNum"
                val surahArabic = surahEntity?.nameArabic ?: ""

                val firstAyahKey = firstLine.startAyahKey ?: "${surahNum}:1"
                val juzNumber = ayahDao.getJuzNumberForAyah(firstAyahKey) ?: 1
                val firstAyahNumber = firstLine.startAyahKey?.substringAfter(":")?.toIntOrNull()

                buildPageUi(
                    pageNumberValue = pageNumber,
                    juzNumber = juzNumber,
                    paraNumber = juzNumber,
                    surahNumberValue = surahNum,
                    surahLabelValue = surahSimple,
                    leftHeaderValue = surahArabic,
                    centerHeaderValue = "Page $pageNumber",
                    rightHeaderValue = "الجزء ${convertToArabicNumber(juzNumber)}",
                    firstAyahKeyValue = firstAyahKey,
                    surahAyahCountValue = surahEntity?.ayahCount,
                    startsAtSurahBoundaryValue = firstAyahNumber == 1,
                )
            } else if (pageEntity != null) {
                val firstAyahKey = pageEntity.firstAyahKey
                val surahNum = firstAyahKey?.substringBefore(":")?.toIntOrNull()
                val firstAyahNumber = firstAyahKey?.substringAfter(":")?.toIntOrNull()
                val surahEntity = surahNum?.let { surahDao.getSurahByNumber(it) }
                MushafPageUi(
                    pageNumber = pageEntity.pageNumber,
                    juzNumber = pageEntity.juzNumber,
                    paraNumber = pageEntity.paraNumber,
                    surahNumber = surahNum,
                    surahLabel = pageEntity.surahLabel,
                    leftHeader = pageEntity.leftHeader,
                    centerHeader = pageEntity.centerHeader,
                    rightHeader = pageEntity.rightHeader,
                    firstAyahKey = pageEntity.firstAyahKey,
                    surahAyahCount = surahEntity?.ayahCount,
                    startsAtSurahBoundary = firstAyahNumber == 1,
                )
            } else {
                buildPageUi(
                    pageNumberValue = pageNumber,
                    juzNumber = 1,
                    paraNumber = 1,
                    surahNumberValue = null,
                    surahLabelValue = "Surah",
                    leftHeaderValue = "",
                    centerHeaderValue = "Page $pageNumber",
                    rightHeaderValue = "الجزء ١",
                    firstAyahKeyValue = null,
                    surahAyahCountValue = null,
                    startsAtSurahBoundaryValue = false,
                )
            }

            Pair(pageUi, linesUi)
        }
    }

    override suspend fun getPageCount(scriptType: ScriptType): Int {
        return if (scriptType == ScriptType.UTHMANI) 604 else 559
    }

    override suspend fun isPageBookmarked(pageNumber: Int, scriptType: ScriptType): Boolean {
        val refType = if (scriptType == ScriptType.UTHMANI) PageReferenceType.UTHMANI else PageReferenceType.INDOPAK
        return bookmarkRepository.isPageBookmarked(pageNumber, refType)
    }

    override suspend fun togglePageBookmark(pageNumber: Int, scriptType: ScriptType): Boolean {
        val refType = if (scriptType == ScriptType.UTHMANI) PageReferenceType.UTHMANI else PageReferenceType.INDOPAK
        return bookmarkRepository.togglePageBookmark(pageNumber, refType)
    }

    override suspend fun initializePrototypeDataIfNeeded(progressCallback: ((Float) -> Unit)?) {
        withContext(Dispatchers.IO) {
            val count = mushafDao.getPageCount()
            if (count == 604) return@withContext

            // Incomplete or empty data - clear tables for a clean rebuild
            mushafDao.clearPages()
            mushafDao.clearLines()

            val pagesToInsert = mutableListOf<MushafPageEntity>()
            val linesToInsert = mutableListOf<MushafLineEntity>()

            val scripts = listOf(ScriptType.INDOPAK, ScriptType.UTHMANI)
            var processed = 0
            val totalSteps = 604 + 559

            for (script in scripts) {
                val layoutName = if (script == ScriptType.UTHMANI) "KFGQPC V2 1421H" else "IndoPak 15-line Qudratullah"
                val maxPage = if (script == ScriptType.UTHMANI) 604 else 559

                for (page in 1..maxPage) {
                    val refs = layoutRefDao.getReferencesForPage(page, layoutName)
                    val combinedTextBuilder = StringBuilder()

                    var firstAyahKey: String? = null
                    var lastAyahKey: String? = null
                    var firstJuz = 1
                    var firstSurahNum = 1
                    var isFirstPageAyahSet = false

                    if (refs.isNotEmpty()) {
                        firstAyahKey = refs.first().firstAyahKey
                        lastAyahKey = refs.last().lastAyahKey

                        for (ref in refs) {
                            val firstKey = ref.firstAyahKey ?: continue
                            val lastKey = ref.lastAyahKey ?: continue

                            val startSurah = firstKey.substringBefore(":").toIntOrNull() ?: 1
                            val startAyah = firstKey.substringAfter(":").toIntOrNull() ?: 1
                            val endSurah = lastKey.substringBefore(":").toIntOrNull() ?: 1
                            val endAyah = lastKey.substringAfter(":").toIntOrNull() ?: 1

                            val ayahs = mutableListOf<AyahDisplayRow>()
                            for (s in startSurah..endSurah) {
                                val currentStartAyah = if (s == startSurah) startAyah else 1
                                val currentEndAyah = if (s == endSurah) endAyah else 999
                                val segment = ayahDao.getAyahsByRange(s, currentStartAyah, currentEndAyah, script.name)
                                ayahs.addAll(segment)
                            }

                            if (ayahs.isNotEmpty()) {
                                if (!isFirstPageAyahSet) {
                                    firstJuz = ayahs.first().juzNumber
                                    firstSurahNum = ayahs.first().surahNumber
                                    isFirstPageAyahSet = true
                                }
                                for (ayah in ayahs) {
                                    val cleaned = ayah.displayText.trim()
                                    val hasMarker = cleaned.contains('\u06DD') || cleaned.contains('\u06FF')
                                    if (combinedTextBuilder.isNotEmpty()) {
                                        combinedTextBuilder.append(" ")
                                    }
                                    combinedTextBuilder.append(cleaned)
                                    if (!hasMarker) {
                                        combinedTextBuilder.append(" \u06DD${convertToArabicNumber(ayah.ayahNumber)} ")
                                    }
                                }
                            }
                        }
                    } else {
                        // Fallback fallback to raw page matching if no layout refs found
                        val ayahs = ayahDao.getAyahsByPage(page, script.name)
                        if (ayahs.isNotEmpty()) {
                            firstJuz = ayahs.first().juzNumber
                            firstSurahNum = ayahs.first().surahNumber
                            firstAyahKey = ayahs.first().ayahKey
                            lastAyahKey = ayahs.last().ayahKey

                            for (ayah in ayahs) {
                                val cleaned = ayah.displayText.trim()
                                val hasMarker = cleaned.contains('\u06DD') || cleaned.contains('\u06FF')
                                if (combinedTextBuilder.isNotEmpty()) {
                                    combinedTextBuilder.append(" ")
                                }
                                combinedTextBuilder.append(cleaned)
                                if (!hasMarker) {
                                    combinedTextBuilder.append(" \u06DD${convertToArabicNumber(ayah.ayahNumber)} ")
                                }
                            }
                        }
                    }

                    val surahEntity = surahDao.getSurahByNumber(firstSurahNum)
                    val surahSimple = surahEntity?.nameSimple ?: "Surah $firstSurahNum"
                    val surahArabic = surahEntity?.nameArabic ?: ""

                    // Split into exactly 15 lines
                    val textToSplit = combinedTextBuilder.toString()
                    val lines = splitTextIntoLines(textToSplit, 15)

                    // Save page metadata
                    if (script == ScriptType.INDOPAK || (script == ScriptType.UTHMANI && page > 559)) {
                        val mushafPage = MushafPageEntity(
                            pageNumber = page,
                            juzNumber = firstJuz,
                            paraNumber = firstJuz,
                            surahLabel = surahSimple,
                            leftHeader = surahArabic,
                            centerHeader = "Page $page",
                            rightHeader = "الجزء ${convertToArabicNumber(firstJuz)}",
                            firstAyahKey = firstAyahKey,
                            lastAyahKey = lastAyahKey
                        )
                        pagesToInsert.add(mushafPage)
                    }

                    // Save lines
                    val lineEntities = lines.mapIndexed { index, lineText ->
                        MushafLineEntity(
                            pageNumber = page,
                            lineNumber = index + 1,
                            scriptType = script.name,
                            lineText = lineText,
                            startAyahKey = firstAyahKey,
                            endAyahKey = lastAyahKey,
                            surahNumber = firstSurahNum
                        )
                    }
                    linesToInsert.addAll(lineEntities)

                    processed++
                    progressCallback?.invoke(processed.toFloat() / totalSteps)
                }
            }

            mushafDao.insertPages(pagesToInsert)
            mushafDao.insertLines(linesToInsert)
        }
    }

    private fun splitTextIntoLines(text: String, lineCount: Int): List<String> {
        val words = text.split("\\s+".toRegex()).filter { it.isNotBlank() }
        if (words.isEmpty()) return List(lineCount) { "" }

        val totalChars = text.length
        val targetCharsPerLine = totalChars / lineCount

        val lines = mutableListOf<String>()
        var currentLineWords = mutableListOf<String>()
        var currentLineLength = 0

        for (word in words) {
            if (lines.size == lineCount - 1) {
                currentLineWords.add(word)
            } else {
                val wordLength = word.length + (if (currentLineWords.isEmpty()) 0 else 1)
                if (currentLineLength + wordLength > targetCharsPerLine && currentLineWords.isNotEmpty()) {
                    lines.add(currentLineWords.joinToString(" "))
                    currentLineWords = mutableListOf(word)
                    currentLineLength = word.length
                } else {
                    currentLineWords.add(word)
                    currentLineLength += wordLength
                }
            }
        }
        if (currentLineWords.isNotEmpty() && lines.size < lineCount) {
            lines.add(currentLineWords.joinToString(" "))
        }
        while (lines.size < lineCount) {
            lines.add("")
        }
        return lines
    }

    private fun convertToArabicNumber(number: Int): String {
        val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        return number.toString().map { char ->
            if (char.isDigit()) arabicDigits[char.code - '0'.code] else char
        }.joinToString("")
    }
}
