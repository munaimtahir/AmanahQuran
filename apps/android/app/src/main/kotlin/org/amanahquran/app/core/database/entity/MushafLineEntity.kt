package org.amanahquran.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "mushaf_lines",
    indices = [
        Index("pageNumber"),
        Index("scriptType"),
        Index(value = ["pageNumber", "lineNumber", "scriptType"], unique = true)
    ]
)
data class MushafLineEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val pageNumber: Int,
    val lineNumber: Int,
    val scriptType: String,
    val lineText: String,
    val startAyahKey: String?,
    val endAyahKey: String?,
    val surahNumber: Int?,
    val containsSajdahMarker: Boolean = false,
    val containsRukuMarker: Boolean = false
)
