package org.amanahquran.app.core.util

import org.amanahquran.app.core.model.ReaderHeaderFormat

/**
 * [primary] is the surah/juz context (may be null for PAGE_ONLY), [page] is the page label.
 * Kept separate rather than one joined string so the UI can ellipsize only [primary] under
 * width pressure while [page] -- the most load-bearing part -- always stays fully visible.
 */
data class ReaderHeaderParts(val primary: String?, val page: String?)

object ReaderHeaderTextBuilder {
    fun build(
        format: ReaderHeaderFormat,
        surahName: String,
        juzNumber: Int?,
        pageNumber: Int?,
    ): ReaderHeaderParts {
        val pagePart = pageNumber?.let { "Page $it" }
        val surahPart = surahName.takeIf { it.isNotBlank() }
        val juzPart = juzNumber?.let { "Juz $it" }

        val primary = when (format) {
            ReaderHeaderFormat.PAGE_ONLY -> null
            ReaderHeaderFormat.SURAH_PAGE -> surahPart
            ReaderHeaderFormat.JUZ_PAGE -> juzPart
            ReaderHeaderFormat.SURAH_JUZ_PAGE -> listOfNotNull(surahPart, juzPart).joinToString(" · ").ifBlank { null }
        }
        return ReaderHeaderParts(primary = primary, page = pagePart)
    }
}
