package org.amanahquran.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.amanahquran.app.core.model.ScriptType

@Entity(tableName = "last_read")
data class LastReadEntity(
    @PrimaryKey val id: Int = 1, // Single row
    val ayahKey: String,
    val surahNumber: Int,
    val ayahNumber: Int,
    val juzNumber: Int,
    val pageNumber: Int,
    val scriptType: ScriptType,
    val scrollOffset: Int?,
    val updatedAt: Long
)
