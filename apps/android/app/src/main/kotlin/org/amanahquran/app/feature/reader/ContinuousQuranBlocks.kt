package org.amanahquran.app.feature.reader

/**
 * Character offsets for one ayah inside a [ContinuousQuranBlock.plainText]. [textStart]/[textEnd]
 * cover the ayah's own display text (unmodified, canonical); [markerStart]/[markerEnd] cover the
 * inline ayah-number marker appended right after it. Tap hit-testing and current-ayah highlighting
 * both key off these ranges instead of re-deriving them from raw character positions each time.
 */
data class AyahTextRange(
    val ayahKey: String,
    val textStart: Int,
    val textEnd: Int,
    val markerStart: Int,
    val markerEnd: Int,
)

/**
 * One virtualized unit of Continuous Mode text -- in practice always the ayahs of a single Mushaf
 * page, since [buildContinuousQuranBlocks] splits on every page-number change. [plainText] is the
 * raw concatenation of canonical ayah text plus inline markers; no Quran word is altered, only
 * assembled. Styling (marker colour, current-ayah highlight) is applied at render time from this
 * plain text, not baked in here, so the same block never needs rebuilding when only the selected
 * ayah or theme changes.
 */
data class ContinuousQuranBlock(
    val pageNumber: Int,
    val plainText: String,
    val ayahRanges: List<AyahTextRange>,
)

/** Arabic end-of-ayah glyph (U+06DD) followed by the ayah number in Arabic-Indic digits. */
fun ayahMarkerText(ayahNumber: Int): String = "۝${toArabicIndicDigits(ayahNumber)}"

fun toArabicIndicDigits(number: Int): String {
    val digits = "٠١٢٣٤٥٦٧٨٩"
    return number.toString().map { ch -> digits[ch - '0'] }.joinToString(separator = "")
}

/**
 * Assembles consecutive ayahs (already ordered) into virtualized, page-bounded continuous blocks.
 * Splitting on [ReaderAyahUiModel.pageNumber] keeps each block roughly one printed page, which
 * both bounds block size for `LazyColumn` virtualization and gives a natural, already-canonical
 * seam for a page divider -- no ayah is duplicated or omitted across block boundaries.
 */
fun buildContinuousQuranBlocks(ayahs: List<ReaderAyahUiModel>): List<ContinuousQuranBlock> {
    if (ayahs.isEmpty()) return emptyList()

    val blocks = mutableListOf<ContinuousQuranBlock>()
    var currentPage = ayahs.first().pageNumber
    var text = StringBuilder()
    var ranges = mutableListOf<AyahTextRange>()

    fun flush() {
        if (ranges.isNotEmpty()) {
            blocks += ContinuousQuranBlock(currentPage, text.toString(), ranges.toList())
        }
        text = StringBuilder()
        ranges = mutableListOf()
    }

    for (ayah in ayahs) {
        if (ayah.pageNumber != currentPage) {
            flush()
            currentPage = ayah.pageNumber
        }
        val textStart = text.length
        text.append(ayah.displayText)
        val textEnd = text.length
        text.append(' ')
        val markerStart = text.length
        text.append(ayahMarkerText(ayah.ayahNumber))
        val markerEnd = text.length
        text.append(' ')
        ranges += AyahTextRange(ayah.ayahKey, textStart, textEnd, markerStart, markerEnd)
    }
    flush()

    return blocks
}

/**
 * Replaces every consecutive run of [ReaderStructuralItem.Ayah] items (i.e. a run with no
 * Juz/Surah/Bismillah/PageDivider header inside it) with one or more [ReaderStructuralItem.ContinuousBlock]
 * items, leaving every existing header/divider item exactly where the already-tested
 * [buildReaderStructuralItems] placed it. Because every page transition already gets some
 * non-Ayah marker there (a header or a page divider -- see [buildReaderStructuralItems]), a run
 * of Ayah items is already page-bounded before it even reaches [buildContinuousQuranBlocks].
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
