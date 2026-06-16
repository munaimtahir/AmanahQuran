package org.amanahquran.app.content.validation

import org.amanahquran.app.core.model.ValidationSeverity

data class ContentValidationResult(
    val validationName: String,
    val expectedValue: String,
    val actualValue: String,
    val passed: Boolean,
    val severity: ValidationSeverity,
    val checkedAt: Long = System.currentTimeMillis()
)
