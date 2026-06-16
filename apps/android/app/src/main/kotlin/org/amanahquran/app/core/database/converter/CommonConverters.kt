package org.amanahquran.app.core.database.converter

import androidx.room.TypeConverter
import org.amanahquran.app.core.model.*

class CommonConverters {
    @TypeConverter
    fun fromScriptType(value: ScriptType): String = value.name

    @TypeConverter
    fun toScriptType(value: String): ScriptType = ScriptType.valueOf(value)

    @TypeConverter
    fun fromBookmarkType(value: BookmarkType): String = value.name

    @TypeConverter
    fun toBookmarkType(value: String): BookmarkType = BookmarkType.valueOf(value)

    @TypeConverter
    fun fromValidationStatus(value: ValidationStatus): String = value.name

    @TypeConverter
    fun toValidationStatus(value: String): ValidationStatus = ValidationStatus.valueOf(value)

    @TypeConverter
    fun fromReviewerStatus(value: ReviewerStatus): String = value.name

    @TypeConverter
    fun toReviewerStatus(value: String): ReviewerStatus = ReviewerStatus.valueOf(value)

    @TypeConverter
    fun fromContentType(value: ContentType): String = value.name

    @TypeConverter
    fun toContentType(value: String): ContentType = ContentType.valueOf(value)

    @TypeConverter
    fun fromValidationSeverity(value: ValidationSeverity): String = value.name

    @TypeConverter
    fun toValidationSeverity(value: String): ValidationSeverity = ValidationSeverity.valueOf(value)
}
