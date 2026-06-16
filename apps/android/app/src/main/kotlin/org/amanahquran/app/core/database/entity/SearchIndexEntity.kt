package org.amanahquran.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_index")
data class SearchIndexEntity(
    @PrimaryKey val ayahKey: String,
    val normalizedArabic: String, // Search only, never display
    val normalizedTransliteration: String?,
    val surahSearchTerms: String?
)
