package org.amanahquran.app.content.manifest

import org.amanahquran.app.core.model.ReviewerStatus
import org.amanahquran.app.core.model.ValidationStatus

data class ContentManifest(
    val manifestVersion: String,
    val databaseVersion: String,
    val generatedAt: String,
    val surahCount: Int,
    val ayahCount: Int,
    val overallChecksum: String,
    val validationStatus: ValidationStatus,
    val reviewerStatus: ReviewerStatus,
    val sources: List<ContentPackManifest>
)

data class ContentPackManifest(
    val sourceName: String,
    val sourceUrl: String,
    val license: String,
    val version: String,
    val checksum: String,
    val importDate: String,
    val contentType: String,
    val scriptType: String? = null,
    val validationStatus: ValidationStatus,
    val reviewerStatus: ReviewerStatus
)

data class ContentChecksum(
    val fileName: String,
    val checksum: String,
    val algorithm: String = "SHA-256"
)

data class ContentReviewStatus(
    val reviewerName: String,
    val reviewDate: String,
    val status: ReviewerStatus,
    val comments: String? = null
)
