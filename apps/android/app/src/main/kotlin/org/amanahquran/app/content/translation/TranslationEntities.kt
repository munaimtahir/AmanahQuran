package org.amanahquran.app.content.translation

import androidx.room.Entity
import androidx.room.Index

/** [availabilityStatus] values. */
object TranslationAvailabilityStatus {
    const val TRANSLATED = "TRANSLATED"
    const val SOURCE_MISSING = "SOURCE_MISSING"
}

@Entity(
    tableName = "translation_metadata",
    primaryKeys = ["translationId"],
)
data class TranslationMetadataEntity(
    val translationId: String,
    val languageCode: String,
    val languageName: String,
    val displayName: String,
    val translatorName: String,
    val sourceName: String,
    val sourceUrl: String,
    /** "LTR" or "RTL" -- kept as a plain string column (not the [org.amanahquran.app.core.model.TranslationDirection] enum) so this module has no Compose/UI dependency. */
    val direction: String,
    val contentVersion: String,
    val permissionStatus: String,
    val attributionText: String,
    val availableCount: Int,
    val sourceMissingCount: Int,
    val footnoteCount: Int,
    val checksum: String,
    val importDate: String,
)

@Entity(
    tableName = "translation_ayahs",
    primaryKeys = ["translationId", "ayahKey"],
    indices = [Index(value = ["ayahKey"]), Index(value = ["normalizedSearchText"])],
)
data class TranslationAyahEntity(
    val translationId: String,
    val ayahKey: String,
    val surahNumber: Int,
    val ayahNumber: Int,
    /** Verbatim translation text, unmodified from the approved source. Null exactly when [availabilityStatus] is SOURCE_MISSING. */
    val displayText: String?,
    /** One of [TranslationAvailabilityStatus]. */
    val availabilityStatus: String,
    val normalizedSearchText: String?,
)

@Entity(
    tableName = "translation_footnotes",
    primaryKeys = ["translationId", "ayahKey", "footnoteIndex"],
    indices = [Index(value = ["translationId", "ayahKey"])],
)
data class TranslationFootnoteEntity(
    val translationId: String,
    val ayahKey: String,
    val footnoteIndex: Int,
    val marker: String,
    val footnoteText: String,
)
