package org.amanahquran.app.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "content_sources")
data class ContentSourceEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int?,
    @ColumnInfo(name = "source_folder_number")
    val sourceFolderNumber: Int,
    @ColumnInfo(name = "original_file_path")
    val originalFilePath: String,
    @ColumnInfo(name = "original_file_name")
    val originalFileName: String,
    @ColumnInfo(name = "detected_format")
    val detectedFormat: String,
    @ColumnInfo(name = "sha256")
    val sha256: String,
    @ColumnInfo(name = "source_name")
    val sourceName: String,
    @ColumnInfo(name = "source_url")
    val sourceUrl: String?,
    @ColumnInfo(name = "content_category")
    val contentCategory: String,
    @ColumnInfo(name = "script_type")
    val scriptType: String?,
    @ColumnInfo(name = "license_status")
    val licenseStatus: String,
    @ColumnInfo(name = "v1_candidate_status")
    val v1CandidateStatus: String,
    @ColumnInfo(name = "requires_manual_review")
    val requiresManualReview: Int,
    @ColumnInfo(name = "raw_file_immutable")
    val rawFileImmutable: Int,
    @ColumnInfo(name = "notes")
    val notes: String?
)
