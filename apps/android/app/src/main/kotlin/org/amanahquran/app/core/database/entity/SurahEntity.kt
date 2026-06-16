package org.amanahquran.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "surahs")
data class SurahEntity(
    @PrimaryKey val surahNumber: Int,
    val nameArabic: String,
    val nameSimple: String,
    val nameUrdu: String?,
    val revelationType: String?,
    val ayahCount: Int
)
