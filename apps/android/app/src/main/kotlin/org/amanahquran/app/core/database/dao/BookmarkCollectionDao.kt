package org.amanahquran.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.amanahquran.app.core.database.entity.BookmarkCollectionEntity
import org.amanahquran.app.core.database.entity.BookmarkCollectionMembershipEntity

@Dao
interface BookmarkCollectionDao {
    @Query("SELECT * FROM bookmark_collections ORDER BY isDefault DESC, name COLLATE NOCASE")
    fun observeCollections(): Flow<List<BookmarkCollectionEntity>>

    @Insert
    suspend fun insertCollection(collection: BookmarkCollectionEntity): Long

    @Query("UPDATE bookmark_collections SET name = :name, updatedAt = :updatedAt WHERE collectionId = :collectionId")
    suspend fun renameCollection(collectionId: Long, name: String, updatedAt: Long): Int

    @Query("DELETE FROM bookmark_collections WHERE collectionId = :collectionId AND isDefault = 0")
    suspend fun deleteCollection(collectionId: Long): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addMembership(membership: BookmarkCollectionMembershipEntity): Long

    @Query("DELETE FROM bookmark_collection_membership WHERE collectionId = :collectionId AND bookmarkId = :bookmarkId")
    suspend fun removeMembership(collectionId: Long, bookmarkId: Long): Int

    @Query("SELECT bookmarkId FROM bookmark_collection_membership WHERE collectionId = :collectionId")
    suspend fun getBookmarkIds(collectionId: Long): List<Long>
}
