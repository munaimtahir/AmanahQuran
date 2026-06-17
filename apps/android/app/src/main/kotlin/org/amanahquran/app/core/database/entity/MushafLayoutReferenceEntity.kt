package org.amanahquran.app.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mushaf_layout_references")
data class MushafLayoutReferenceEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int?,
    @ColumnInfo(name = "layout_name")
    val layoutName: String,
    @ColumnInfo(name = "page_number")
    val pageNumber: Int,
    @ColumnInfo(name = "first_ayah_key")
    val firstAyahKey: String?,
    @ColumnInfo(name = "last_ayah_key")
    val lastAyahKey: String?,
    @ColumnInfo(name = "first_word_id")
    val firstWordId: Int?,
    @ColumnInfo(name = "last_word_id")
    val lastWordId: Int?,
    @ColumnInfo(name = "mapping_basis")
    val mappingBasis: String?
)
