package org.amanahquran.app.core.database.dao

data class AyahReferenceRow(
    val ayahKey: String,
    val surahNumber: Int,
    val ayahNumber: Int,
    val juzNumber: Int,
    val pageNumber: Int,
)
