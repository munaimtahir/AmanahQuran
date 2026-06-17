package org.amanahquran.app.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "content_validation")
data class ContentValidationEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int?,
    @ColumnInfo(name = "validation_name")
    val validationName: String,
    @ColumnInfo(name = "expected_value")
    val expectedValue: String,
    @ColumnInfo(name = "actual_value")
    val actualValue: String,
    @ColumnInfo(name = "passed")
    val passed: Int,
    @ColumnInfo(name = "checked_at")
    val checkedAt: String
)
