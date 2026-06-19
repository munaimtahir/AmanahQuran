package org.amanahquran.app.core.database.dao

data class AyahDisplayRow(
    val ayahKey: String,
    val surahNumber: Int,
    val ayahNumber: Int,
    val juzNumber: Int,
    val pageNumber: Int,
    val scriptType: String,
    val displayText: String,
)
