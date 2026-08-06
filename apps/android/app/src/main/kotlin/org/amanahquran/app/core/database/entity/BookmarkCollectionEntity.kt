package org.amanahquran.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmark_collections")
data class BookmarkCollectionEntity(
    @PrimaryKey(autoGenerate = true) val collectionId: Long = 0,
    val name: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isDefault: Boolean,
)

@Entity(
    tableName = "bookmark_collection_membership",
    primaryKeys = ["collectionId", "bookmarkId"],
)
data class BookmarkCollectionMembershipEntity(
    val collectionId: Long,
    val bookmarkId: Long,
)
