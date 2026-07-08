package org.amanahquran.app.core.model

sealed interface ReaderAnchor {
    data class SurahStart(val surahNumber: Int) : ReaderAnchor
    data class ExactAyah(val ayahKey: String) : ReaderAnchor
    data class PageStart(
        val pageNumber: Int,
        val pageReferenceType: PageReferenceType,
    ) : ReaderAnchor
    data class JuzStart(val juzNumber: Int) : ReaderAnchor
}

fun lastReadAnchor(
    ayahKey: String?,
    pageNumber: Int?,
    scriptType: ScriptType,
): ReaderAnchor? {
    if (!ayahKey.isNullOrBlank()) {
        return ReaderAnchor.ExactAyah(ayahKey)
    }
    if (pageNumber != null) {
        val pageReferenceType = when (scriptType) {
            ScriptType.INDOPAK -> PageReferenceType.INDOPAK
            ScriptType.UTHMANI -> PageReferenceType.UTHMANI
        }
        return ReaderAnchor.PageStart(pageNumber, pageReferenceType)
    }
    return null
}
