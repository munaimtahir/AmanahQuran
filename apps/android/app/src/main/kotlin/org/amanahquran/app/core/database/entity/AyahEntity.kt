package org.amanahquran.app.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ayahs",
    indices = [
        Index(value = ["surah_number", "ayah_number"], name = "idx_ayahs_surah_ayah"),
        Index(value = ["juz_number", "surah_number", "ayah_number"], name = "idx_ayahs_juz_order"),
        Index(value = ["page_number", "surah_number", "ayah_number"], name = "idx_ayahs_page_order"),
        Index(value = ["ayah_key"], unique = true, name = "idx_ayahs_key")
    ]
)
data class AyahEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int?,
    @ColumnInfo(name = "ayah_key")
    val ayahKey: String, // Format "surah:ayah"
    @ColumnInfo(name = "surah_number")
    val surahNumber: Int,
    @ColumnInfo(name = "ayah_number")
    val ayahNumber: Int,
    @ColumnInfo(name = "juz_number")
    val juzNumber: Int,
    @ColumnInfo(name = "page_number")
    val pageNumber: Int,
    @ColumnInfo(name = "hizb_number")
    val hizbNumber: Int?,
    @ColumnInfo(name = "rub_number")
    val rubNumber: Int?,
    @ColumnInfo(name = "manzil_number")
    val manzilNumber: Int?,
    @ColumnInfo(name = "ruku_number")
    val rukuNumber: Int?,
    @ColumnInfo(name = "sajdah_type")
    val sajdahType: String?
)
