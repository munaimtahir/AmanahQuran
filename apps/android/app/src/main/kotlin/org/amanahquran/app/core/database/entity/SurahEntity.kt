package org.amanahquran.app.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(
    tableName = "surahs",
    indices = [Index(value = ["number"], unique = true)]
)
data class SurahEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int?,
    @ColumnInfo(name = "number")
    val number: Int,
    @ColumnInfo(name = "name_arabic")
    val nameArabic: String,
    @ColumnInfo(name = "name_simple")
    val nameSimple: String,
    @ColumnInfo(name = "revelation_type")
    val revelationType: String?,
    @ColumnInfo(name = "ayah_count")
    val ayahCount: Int
)
