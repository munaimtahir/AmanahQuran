package org.amanahquran.app.core.repository

import kotlinx.coroutines.flow.Flow
import org.amanahquran.app.core.database.dao.SearchIndexDao
import org.amanahquran.app.core.database.entity.SearchIndexEntity

interface SearchRepository {
    fun searchArabic(query: String): Flow<List<SearchIndexEntity>>
    suspend fun getSearchIndex(ayahKey: String): SearchIndexEntity?
}

class SearchRepositoryImpl(
    private val searchIndexDao: SearchIndexDao
) : SearchRepository {
    override fun searchArabic(query: String): Flow<List<SearchIndexEntity>> =
        searchIndexDao.searchNormalizedArabic(query)

    override suspend fun getSearchIndex(ayahKey: String): SearchIndexEntity? =
        searchIndexDao.getSearchIndexByAyahKey(ayahKey)
}
