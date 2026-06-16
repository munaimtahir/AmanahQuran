package org.amanahquran.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.amanahquran.app.core.model.ValidationSeverity

@Entity(tableName = "content_validations")
data class ContentValidationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val validationName: String,
    val expectedValue: String,
    val actualValue: String,
    val passed: Boolean,
    val severity: ValidationSeverity,
    val checkedAt: Long
)
