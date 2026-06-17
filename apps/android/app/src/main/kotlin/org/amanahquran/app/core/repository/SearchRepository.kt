package org.amanahquran.app.core.repository

import org.amanahquran.app.core.database.dao.QuranTextDao
import org.amanahquran.app.core.database.dao.SearchIndexDao
import org.amanahquran.app.core.database.entity.SearchIndexEntity

data class SearchResultDisplay(
    val ayahKey: String,
    val scriptType: String,
    val displayText: String,
)

interface SearchRepository {
    suspend fun searchNormalizedArabic(query: String, scriptType: String): List<SearchResultDisplay>
    suspend fun getSearchRow(ayahKey: String): SearchIndexEntity?
}

class SearchRepositoryImpl(
    private val searchIndexDao: SearchIndexDao,
    private val quranTextDao: QuranTextDao,
) : SearchRepository {
    override suspend fun searchNormalizedArabic(query: String, scriptType: String): List<SearchResultDisplay> {
        return searchIndexDao.searchNormalizedArabic(query).mapNotNull { row ->
            val displayText = quranTextDao.getTextByAyahAndScript(row.ayahKey, scriptType) ?: return@mapNotNull null
            SearchResultDisplay(
                ayahKey = row.ayahKey,
                scriptType = displayText.scriptType,
                displayText = displayText.displayText,
            )
        }
    }

    override suspend fun getSearchRow(ayahKey: String): SearchIndexEntity? = searchIndexDao.getSearchRow(ayahKey)
}
