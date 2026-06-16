package org.amanahquran.app.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.amanahquran.app.core.model.ScriptType

@Entity(
    tableName = "quran_text",
    indices = [
        Index(value = ["ayahKey"]),
        Index(value = ["scriptType"]),
        Index(value = ["ayahKey", "scriptType"], unique = true)
    ]
)
data class QuranTextEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ayahKey: String,
    val scriptType: ScriptType,
    val displayText: String, // Immutable verified Quran text
    val sourceId: Long,
    val checksum: String?
)
