package org.amanahquran.app.core.repository

import android.content.Context
import org.amanahquran.app.core.model.ScriptType

data class MushafPageUi(
    val pageNumber: Int,
    val juzNumber: Int?,
    val paraNumber: Int?,
    val surahNumber: Int?,
    val surahLabel: String?,
    val leftHeader: String?,
    val centerHeader: String?,
    val rightHeader: String?,
    val firstAyahKey: String? = null,
    val surahAyahCount: Int? = null,
    val startsAtSurahBoundary: Boolean = false,
)

data class MushafLineUi(
    val lineNumber: Int,
    val lineText: String,
    val containsSajdahMarker: Boolean,
    val containsRukuMarker: Boolean
)


interface MushafRepository {
    suspend fun getMushafPage(pageNumber: Int, scriptType: ScriptType): Pair<MushafPageUi, List<MushafLineUi>>
    suspend fun getPageCount(scriptType: ScriptType): Int
    suspend fun isPageBookmarked(pageNumber: Int, scriptType: ScriptType): Boolean
    suspend fun togglePageBookmark(pageNumber: Int, scriptType: ScriptType): Boolean
    suspend fun initializePrototypeDataIfNeeded(progressCallback: ((Float) -> Unit)? = null)
}

fun mushafRepository(context: Context): MushafRepository {
    val contentDb = org.amanahquran.app.core.database.AmanahContentDatabaseProvider.getDatabase(context.applicationContext)
    val userDb = org.amanahquran.app.core.database.DatabaseProvider.getDatabase(context.applicationContext)
    return MushafRepositoryImpl(
        contentDatabase = contentDb,
        quranDatabase = userDb,
        bookmarkRepository = bookmarkRepository(context)
    )
}
