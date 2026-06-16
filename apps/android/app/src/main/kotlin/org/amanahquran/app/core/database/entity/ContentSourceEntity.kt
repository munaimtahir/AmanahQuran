package org.amanahquran.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.amanahquran.app.core.model.ContentType
import org.amanahquran.app.core.model.ReviewerStatus
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.model.ValidationStatus

@Entity(tableName = "content_sources")
data class ContentSourceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val contentType: ContentType,
    val scriptType: ScriptType?,
    val sourceName: String,
    val sourceUrl: String,
    val license: String,
    val version: String,
    val checksum: String,
    val importDate: String,
    val validationStatus: ValidationStatus,
    val reviewerStatus: ReviewerStatus
)
