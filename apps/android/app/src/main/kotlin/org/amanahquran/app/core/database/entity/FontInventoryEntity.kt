package org.amanahquran.app.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "font_inventory")
data class FontInventoryEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int?,
    @ColumnInfo(name = "raw_file_path")
    val rawFilePath: String,
    @ColumnInfo(name = "file_name")
    val fileName: String,
    @ColumnInfo(name = "likely_use")
    val likelyUse: String,
    @ColumnInfo(name = "license_status")
    val licenseStatus: String,
    @ColumnInfo(name = "v1_bundling_status")
    val v1BundlingStatus: String
)
