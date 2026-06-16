package org.amanahquran.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.amanahquran.app.core.model.BookmarkType

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookmarkType: BookmarkType,
    val ayahKey: String?, // Required if type is AYAH
    val pageNumber: Int?, // Required if type is PAGE
    val createdAt: Long,
    val updatedAt: Long
)
