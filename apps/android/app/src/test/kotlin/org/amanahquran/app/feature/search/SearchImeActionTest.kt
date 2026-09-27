package org.amanahquran.app.feature.search

import org.amanahquran.app.core.repository.SearchResultItem
import org.amanahquran.app.core.repository.SearchResultType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SearchImeActionTest {
    private fun result(key: String) = SearchResultItem(
        resultType = SearchResultType.AYAH,
        title = "t",
        subtitle = key,
        ayahKey = key,
        surahNumber = null,
        ayahNumber = null,
        pageNumber = null,
        pageReferenceType = null,
        juzNumber = null,
        previewText = null,
    )

    @Test
    fun opensDirectly_onlyForASingleResult() {
        val only = result("2:255")
        assertEquals(only, shouldOpenSingleResult(listOf(only)))
        assertNull(shouldOpenSingleResult(emptyList()))
        assertNull(shouldOpenSingleResult(listOf(result("1:1"), result("1:2"))))
    }
}
