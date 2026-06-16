package org.amanahquran.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.amanahquran.app.core.model.ReviewerStatus
import org.amanahquran.app.core.model.ValidationStatus

@Entity(tableName = "content_manifests")
data class ContentManifestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val manifestVersion: String,
    val databaseVersion: String,
    val generatedAt: String,
    val surahCount: Int,
    val ayahCount: Int,
    val indopakSourceId: Long?,
    val uthmaniSourceId: Long?,
    val overallChecksum: String,
    val validationStatus: ValidationStatus,
    val reviewerStatus: ReviewerStatus
)
