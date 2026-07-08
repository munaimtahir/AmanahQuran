package org.amanahquran.app.core.navigation

import org.amanahquran.app.core.model.BookmarkType
import org.amanahquran.app.core.model.PageReferenceType
import org.amanahquran.app.core.model.ReaderAnchor
import org.amanahquran.app.core.repository.BookmarkRecord
import org.amanahquran.app.core.repository.SearchResultItem
import org.amanahquran.app.core.repository.SearchResultType
import org.junit.Assert.assertEquals
import org.junit.Test

class ReaderDestinationTest {
    @Test
    fun searchAyahCreatesExactAyahAnchor() {
        assertEquals(
            ReaderAnchor.ExactAyah("2:255"),
            searchResult(SearchResultType.AYAH, ayahKey = "2:255").toReaderAnchor(),
        )
    }

    @Test
    fun bookmarkAyahCreatesExactAyahAnchor() {
        assertEquals(
            ReaderAnchor.ExactAyah("2:255"),
            bookmark(BookmarkType.AYAH, ayahKey = "2:255").toReaderAnchor(),
        )
    }

    @Test
    fun surahAndPageEntriesRetainTheirStartAnchors() {
        assertEquals(
            ReaderAnchor.SurahStart(2),
            searchResult(SearchResultType.SURAH, surahNumber = 2).toReaderAnchor(),
        )
        assertEquals(
            ReaderAnchor.PageStart(540, PageReferenceType.UTHMANI),
            bookmark(
                type = BookmarkType.PAGE,
                pageNumber = 540,
                pageReferenceType = PageReferenceType.UTHMANI,
            ).toReaderAnchor(),
        )
    }

    private fun searchResult(
        type: SearchResultType,
        ayahKey: String? = null,
        surahNumber: Int? = null,
    ) = SearchResultItem(
        resultType = type,
        title = "",
        subtitle = "",
        ayahKey = ayahKey,
        surahNumber = surahNumber,
        ayahNumber = null,
        pageNumber = null,
        pageReferenceType = null,
        juzNumber = null,
        previewText = null,
    )

    private fun bookmark(
        type: BookmarkType,
        ayahKey: String? = null,
        pageNumber: Int? = null,
        pageReferenceType: PageReferenceType? = null,
    ) = BookmarkRecord(
        id = 1,
        bookmarkType = type,
        ayahKey = ayahKey,
        surahNumber = ayahKey?.substringBefore(':')?.toIntOrNull(),
        ayahNumber = ayahKey?.substringAfter(':')?.toIntOrNull(),
        pageNumber = pageNumber,
        pageReferenceType = pageReferenceType,
        createdAt = 1,
        updatedAt = 1,
    )
}
