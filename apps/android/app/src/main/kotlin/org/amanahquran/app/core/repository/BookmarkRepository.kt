package org.amanahquran.app.core.repository

import kotlinx.coroutines.flow.Flow
import org.amanahquran.app.core.database.dao.BookmarkDao
import org.amanahquran.app.core.database.entity.BookmarkEntity
import org.amanahquran.app.core.model.BookmarkType
import org.amanahquran.app.core.model.ValidationHelpers

interface BookmarkRepository {
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>
    suspend fun addAyahBookmark(ayahKey: String): Long
    suspend fun addPageBookmark(pageNumber: Int): Long
    suspend fun removeAyahBookmark(ayahKey: String)
    suspend fun removePageBookmark(pageNumber: Int)
    suspend fun removeBookmarkById(id: Long)
    suspend fun isAyahBookmarked(ayahKey: String): Boolean
    suspend fun isPageBookmarked(pageNumber: Int): Boolean
}

class BookmarkRepositoryImpl(
    private val bookmarkDao: BookmarkDao
) : BookmarkRepository {
    override fun getAllBookmarks(): Flow<List<BookmarkEntity>> = bookmarkDao.getAllBookmarks()

    override suspend fun addAyahBookmark(ayahKey: String): Long {
        if (!ValidationHelpers.isValidAyahKey(ayahKey)) return -1
        val now = System.currentTimeMillis()
        return bookmarkDao.insertBookmark(
            BookmarkEntity(
                bookmarkType = BookmarkType.AYAH,
                ayahKey = ayahKey,
                pageNumber = null,
                createdAt = now,
                updatedAt = now
            )
        )
    }

    override suspend fun addPageBookmark(pageNumber: Int): Long {
        if (pageNumber <= 0) return -1
        val now = System.currentTimeMillis()
        return bookmarkDao.insertBookmark(
            BookmarkEntity(
                bookmarkType = BookmarkType.PAGE,
                ayahKey = null,
                pageNumber = pageNumber,
                createdAt = now,
                updatedAt = now
            )
        )
    }

    override suspend fun removeAyahBookmark(ayahKey: String) = bookmarkDao.deleteAyahBookmark(ayahKey)

    override suspend fun removePageBookmark(pageNumber: Int) = bookmarkDao.deletePageBookmark(pageNumber)

    override suspend fun removeBookmarkById(id: Long) = bookmarkDao.deleteBookmarkById(id)

    override suspend fun isAyahBookmarked(ayahKey: String): Boolean = bookmarkDao.getAyahBookmark(ayahKey) != null

    override suspend fun isPageBookmarked(pageNumber: Int): Boolean = bookmarkDao.getPageBookmark(pageNumber) != null
}
