package org.amanahquran.app.feature.reader

/**
 * Character offsets for one ayah inside a [ContinuousQuranBlock.plainText]. [textStart]/[textEnd]
 * cover the ayah's own display text (unmodified, canonical); [markerStart]/[markerEnd] cover the
 * inline ayah-number marker appended right after it. [isJuzStart] indicates if this ayah begins
 * a new Juz transition, receiving subtle, respectful visual emphasis without altering Quran text.
 */
data class AyahTextRange(
    val ayahKey: String,
    val textStart: Int,
    val textEnd: Int,
    val markerStart: Int,
    val markerEnd: Int,
    val isJuzStart: Boolean = false,
)

/**
 * One virtualized unit of Continuous Mode text -- bounded by canonical page and Juz transitions.
 * [plainText] is the concatenation of canonical ayah text plus inline markers; no Quran word is
 * altered.
 */
data class ContinuousQuranBlock(
    val pageNumber: Int,
    val plainText: String,
    val ayahRanges: List<AyahTextRange>,
    val isJuzStart: Boolean = false,
)

/** Arabic end-of-ayah glyph (U+06DD) followed by the ayah number in Arabic-Indic digits. */
fun ayahMarkerText(ayahNumber: Int): String = "۝${toArabicIndicDigits(ayahNumber)}"

fun toArabicIndicDigits(number: Int): String {
    val digits = "٠١٢٣٤٥٦٧٨٩"
    return number.toString().map { ch -> digits[ch - '0'] }.joinToString(separator = "")
}

/**
 * Assembles consecutive ayahs (already ordered) into virtualized, page-and-juz-bounded continuous blocks.
 * Splitting on [ReaderAyahUiModel.pageNumber] and [ReaderAyahUiModel.juzNumber] ensures:
 * 1. A new Juz never begins inline with previous Juz text.
 * 2. Each block stays roughly one printed page for LazyColumn virtualization.
 * 3. Canonical Quran metadata is strictly preserved.
 */
fun buildContinuousQuranBlocks(ayahs: List<ReaderAyahUiModel>): List<ContinuousQuranBlock> {
    if (ayahs.isEmpty()) return emptyList()

    val blocks = mutableListOf<ContinuousQuranBlock>()
    var currentPage = ayahs.first().pageNumber
    var currentJuz = ayahs.first().juzNumber
    var blockIsJuzStart = false
    var text = StringBuilder()
    var ranges = mutableListOf<AyahTextRange>()

    fun flush() {
        if (ranges.isNotEmpty()) {
            blocks += ContinuousQuranBlock(
                pageNumber = currentPage,
                plainText = text.toString(),
                ayahRanges = ranges.toList(),
                isJuzStart = blockIsJuzStart,
            )
        }
        text = StringBuilder()
        ranges = mutableListOf()
        blockIsJuzStart = false
    }

    var previousJuzNumber: Int? = null

    for ((index, ayah) in ayahs.withIndex()) {
        val juzChanged = previousJuzNumber != null && ayah.juzNumber != previousJuzNumber
        val pageChanged = ayah.pageNumber != currentPage

        if (pageChanged || juzChanged) {
            flush()
            currentPage = ayah.pageNumber
            currentJuz = ayah.juzNumber
            if (juzChanged) {
                blockIsJuzStart = true
            }
        }

        val isFirstAyahOfJuz = (index == 0 && ayah.ayahNumber == 1 && (ayah.juzNumber == 1 || juzChanged)) || juzChanged

        val textStart = text.length
        text.append(ayah.displayText)
        val textEnd = text.length
        text.append(' ')
        val markerStart = text.length
        text.append(ayahMarkerText(ayah.ayahNumber))
        val markerEnd = text.length
        text.append(' ')
        ranges += AyahTextRange(
            ayahKey = ayah.ayahKey,
            textStart = textStart,
            textEnd = textEnd,
            markerStart = markerStart,
            markerEnd = markerEnd,
            isJuzStart = isFirstAyahOfJuz,
        )

        previousJuzNumber = ayah.juzNumber
    }
    flush()

    return blocks
}

/**
 * Replaces every consecutive run of [ReaderStructuralItem.Ayah] items (i.e. a run with no
 * Juz/Surah/Bismillah/PageDivider header inside it) with one or more [ReaderStructuralItem.ContinuousBlock]
 * items, leaving every existing header/divider item exactly where [buildReaderStructuralItems] placed it.
 */
fun collapseIntoContinuousBlocks(structuralItems: List<ReaderStructuralItem>): List<ReaderStructuralItem> {
    val result = mutableListOf<ReaderStructuralItem>()
    var run = mutableListOf<ReaderAyahUiModel>()

    fun flushRun() {
        if (run.isNotEmpty()) {
            result += buildContinuousQuranBlocks(run).map { ReaderStructuralItem.ContinuousBlock(it) }
            run = mutableListOf()
        }
    }

    for (item in structuralItems) {
        when (item) {
            is ReaderStructuralItem.Ayah -> run += item.ayah
            else -> {
                flushRun()
                result += item
            }
        }
    }
    flushRun()

    return result
}

/** Maps a raw character offset inside [ContinuousQuranBlock.plainText] back to its canonical ayah. */
fun offsetToAyahKey(block: ContinuousQuranBlock, offset: Int): String? {
    return block.ayahRanges.lastOrNull { it.textStart <= offset }?.ayahKey
}
