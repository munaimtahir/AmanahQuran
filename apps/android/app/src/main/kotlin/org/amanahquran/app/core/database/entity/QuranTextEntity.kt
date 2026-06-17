package org.amanahquran.app.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "quran_texts",
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
    indices = [
        Index(value = ["ayah_key"])
    ]
)
data class QuranTextEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int?,
    @ColumnInfo(name = "ayah_key")
    val ayahKey: String,
    @ColumnInfo(name = "script_type")
    val scriptType: String,
    @ColumnInfo(name = "display_text")
    val displayText: String, // Immutable verified Quran text
    @ColumnInfo(name = "source_id")
    val sourceId: Int,
    @ColumnInfo(name = "checksum")
    val checksum: String?
)
