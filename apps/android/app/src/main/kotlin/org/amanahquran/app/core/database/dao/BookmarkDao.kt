package org.amanahquran.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.amanahquran.app.core.database.entity.BookmarkEntity

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE id = :id")
    suspend fun getBookmarkById(id: Long): BookmarkEntity?

    @Query("SELECT * FROM bookmarks WHERE bookmarkType = 'AYAH' AND ayahKey = :ayahKey")
    suspend fun getAyahBookmark(ayahKey: String): BookmarkEntity?

    @Query("SELECT * FROM bookmarks WHERE bookmarkType = 'PAGE' AND pageNumber = :pageNumber")
    suspend fun getPageBookmark(pageNumber: Int): BookmarkEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity): Long

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteBookmarkById(id: Long)

    @Query("DELETE FROM bookmarks WHERE bookmarkType = 'AYAH' AND ayahKey = :ayahKey")
    suspend fun deleteAyahBookmark(ayahKey: String)

    @Query("DELETE FROM bookmarks WHERE bookmarkType = 'PAGE' AND pageNumber = :pageNumber")
    suspend fun deletePageBookmark(pageNumber: Int)
}
