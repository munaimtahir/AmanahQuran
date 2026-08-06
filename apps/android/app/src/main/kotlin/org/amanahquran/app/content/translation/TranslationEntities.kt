package org.amanahquran.app.content.translation

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "translation_metadata",
    primaryKeys = ["translationId"],
)
data class TranslationMetadataEntity(
    val translationId: String,
    val languageCode: String,
    val languageName: String,
    val translatorName: String,
    val editionName: String,
    val sourceName: String,
    val sourceUrl: String,
    val version: String,
    val licenseStatus: String,
    val checksum: String,
    val importDate: String,
    val validationStatus: String,
    val reviewerStatus: String,
)

@Entity(
    tableName = "translation_ayahs",
    primaryKeys = ["translationId", "ayahKey"],
    indices = [Index(value = ["ayahKey"]), Index(value = ["normalizedSearchText"])],
)
data class TranslationAyahEntity(
    val translationId: String,
    val ayahKey: String,
    val displayText: String,
    val normalizedSearchText: String,
)
