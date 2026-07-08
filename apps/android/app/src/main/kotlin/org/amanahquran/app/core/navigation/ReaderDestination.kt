package org.amanahquran.app.core.navigation

import org.amanahquran.app.core.model.BookmarkType
import org.amanahquran.app.core.model.PageReferenceType
import org.amanahquran.app.core.model.ReaderAnchor
import org.amanahquran.app.core.repository.BookmarkRecord
import org.amanahquran.app.core.repository.SearchResultItem
import org.amanahquran.app.core.repository.SearchResultType

fun SearchResultItem.toReaderAnchor(): ReaderAnchor? = when (resultType) {
    SearchResultType.SURAH -> surahNumber?.let(ReaderAnchor::SurahStart)
    SearchResultType.AYAH -> ayahKey?.let(ReaderAnchor::ExactAyah)
    SearchResultType.JUZ -> juzNumber?.let(ReaderAnchor::JuzStart)
    SearchResultType.PAGE -> pageNumber?.let { page ->
        ReaderAnchor.PageStart(page, pageReferenceType ?: PageReferenceType.INDOPAK)
    }
}

fun BookmarkRecord.toReaderAnchor(): ReaderAnchor? = when (bookmarkType) {
    BookmarkType.AYAH -> ayahKey?.let(ReaderAnchor::ExactAyah)
    BookmarkType.PAGE -> pageNumber?.let { page ->
        ReaderAnchor.PageStart(page, pageReferenceType ?: PageReferenceType.INDOPAK)
    }
}
