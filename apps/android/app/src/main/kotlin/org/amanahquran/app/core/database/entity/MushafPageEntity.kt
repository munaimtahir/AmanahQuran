package org.amanahquran.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mushaf_pages")
data class MushafPageEntity(
    @PrimaryKey val pageNumber: Int,
    val juzNumber: Int?,
    val paraNumber: Int?,
    val surahLabel: String?,
    val leftHeader: String?,
    val centerHeader: String?,
    val rightHeader: String?,
    val firstAyahKey: String?,
    val lastAyahKey: String?
)
