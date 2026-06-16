package org.amanahquran.app.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ayahs",
    indices = [
        Index(value = ["surahNumber"]),
        Index(value = ["juzNumber"]),
        Index(value = ["pageNumber"]),
        Index(value = ["surahNumber", "ayahNumber"], unique = true)
    ]
)
data class AyahEntity(
    @PrimaryKey val ayahKey: String, // Format "surah:ayah"
    val surahNumber: Int,
    val ayahNumber: Int,
    val juzNumber: Int,
    val pageNumber: Int,
    val hizbNumber: Int?,
    val rukuNumber: Int?,
    val sajdahType: String?
)
