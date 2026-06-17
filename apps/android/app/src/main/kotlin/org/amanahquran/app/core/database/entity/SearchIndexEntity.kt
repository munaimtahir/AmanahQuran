package org.amanahquran.app.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "search_index",
    foreignKeys = [
        ForeignKey(
            entity = AyahEntity::class,
            parentColumns = ["ayah_key"],
            childColumns = ["ayah_key"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION,
            deferred = false
        )
    ],
    indices = [Index(value = ["ayah_key"], unique = true)]
)
data class SearchIndexEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int?,
    @ColumnInfo(name = "ayah_key")
    val ayahKey: String,
    @ColumnInfo(name = "normalized_arabic")
    val normalizedArabic: String, // Search only, never display
    @ColumnInfo(name = "normalization_source")
    val normalizationSource: String,
    @ColumnInfo(name = "display_safe", defaultValue = "0")
    val displaySafe: Int
)
