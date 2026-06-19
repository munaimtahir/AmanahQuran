package org.amanahquran.app.core.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import org.amanahquran.app.core.datastore.AmanahPreferencesDataSource
import org.amanahquran.app.core.datastore.amanahPreferencesDataSource
import org.amanahquran.app.core.model.BookmarkType
import org.amanahquran.app.core.model.PageReferenceType
import org.amanahquran.app.core.model.ValidationHelpers

data class BookmarkRecord(
    val id: Long,
    val bookmarkType: BookmarkType,
    val ayahKey: String?,
    val surahNumber: Int?,
    val ayahNumber: Int?,
    val pageNumber: Int?,
    val pageReferenceType: PageReferenceType?,
    val createdAt: Long,
    val updatedAt: Long,
)

interface BookmarkRepository {
    fun getAllBookmarks(): Flow<List<BookmarkRecord>>
    suspend fun addAyahBookmark(ayahKey: String): Long
    suspend fun addPageBookmark(
        pageNumber: Int,
        pageReferenceType: PageReferenceType = PageReferenceType.INDOPAK,
        firstAyahKey: String? = null,
    ): Long
    suspend fun toggleAyahBookmark(ayahKey: String): Boolean
    suspend fun togglePageBookmark(
        pageNumber: Int,
        pageReferenceType: PageReferenceType = PageReferenceType.INDOPAK,
    ): Boolean
    suspend fun removeAyahBookmark(ayahKey: String)
    suspend fun removePageBookmark(
        pageNumber: Int,
        pageReferenceType: PageReferenceType = PageReferenceType.INDOPAK,
    )
    suspend fun removeBookmarkById(id: Long)
    suspend fun isAyahBookmarked(ayahKey: String): Boolean
    suspend fun isPageBookmarked(
        pageNumber: Int,
        pageReferenceType: PageReferenceType = PageReferenceType.INDOPAK,
    ): Boolean
    suspend fun getBookmarkCount(): Int
}

class BookmarkRepositoryImpl(
    private val dataSource: AmanahPreferencesDataSource,
) : BookmarkRepository {
    override fun getAllBookmarks(): Flow<List<BookmarkRecord>> {
        return dataSource.dataStore.data.map { preferences ->
            preferences[Keys.bookmarksJson]
                .orEmpty()
                .toBookmarkRecords()
                .sortedByDescending { it.createdAt }
        }
    }

    override suspend fun addAyahBookmark(ayahKey: String): Long {
        if (!ValidationHelpers.isValidAyahKey(ayahKey)) return -1
        val (surahNumber, ayahNumber) = ayahKey.split(":").mapNotNull { it.toIntOrNull() }
            .let { if (it.size == 2) it[0] to it[1] else return -1 }
        val now = System.currentTimeMillis()
        var createdId = -1L
        dataSource.dataStore.edit { preferences ->
            val records = preferences[Keys.bookmarksJson].orEmpty().toBookmarkRecords().toMutableList()
            val existing = records.indexOfFirst { it.bookmarkType == BookmarkType.AYAH && it.ayahKey == ayahKey }
            if (existing >= 0) {
                val current = records[existing]
                createdId = current.id
                records[existing] = current.copy(
                    updatedAt = now,
                    surahNumber = surahNumber,
                    ayahNumber = ayahNumber,
                )
            } else {
                val record = BookmarkRecord(
                    id = now,
                    bookmarkType = BookmarkType.AYAH,
                    ayahKey = ayahKey,
                    surahNumber = surahNumber,
                    ayahNumber = ayahNumber,
                    pageNumber = null,
                    pageReferenceType = null,
                    createdAt = now,
                    updatedAt = now,
                )
                createdId = record.id
                records.add(record)
            }
            preferences[Keys.bookmarksJson] = records.toJsonArray().toString()
        }
        return createdId
    }

    override suspend fun addPageBookmark(
        pageNumber: Int,
        pageReferenceType: PageReferenceType,
        firstAyahKey: String?,
    ): Long {
        if (pageNumber <= 0) return -1
        val now = System.currentTimeMillis()
        var createdId = -1L
        dataSource.dataStore.edit { preferences ->
            val records = preferences[Keys.bookmarksJson].orEmpty().toBookmarkRecords().toMutableList()
            val existing = records.indexOfFirst {
                it.bookmarkType == BookmarkType.PAGE &&
                    it.pageNumber == pageNumber &&
                    it.pageReferenceType == pageReferenceType
            }
            if (existing >= 0) {
                val current = records[existing]
                createdId = current.id
                records[existing] = current.copy(
                    updatedAt = now,
                    pageReferenceType = pageReferenceType,
                )
            } else {
                val record = BookmarkRecord(
                    id = now,
                    bookmarkType = BookmarkType.PAGE,
                    ayahKey = null,
                    surahNumber = null,
                    ayahNumber = null,
                    pageNumber = pageNumber,
                    pageReferenceType = pageReferenceType,
                    createdAt = now,
                    updatedAt = now,
                )
                createdId = record.id
                records.add(record)
            }
            preferences[Keys.bookmarksJson] = records.toJsonArray().toString()
        }
        return createdId
    }

    override suspend fun toggleAyahBookmark(ayahKey: String): Boolean {
        return if (isAyahBookmarked(ayahKey)) {
            removeAyahBookmark(ayahKey)
            false
        } else {
            addAyahBookmark(ayahKey)
            true
        }
    }

    override suspend fun togglePageBookmark(pageNumber: Int, pageReferenceType: PageReferenceType): Boolean {
        return if (isPageBookmarked(pageNumber, pageReferenceType)) {
            removePageBookmark(pageNumber, pageReferenceType)
            false
        } else {
            addPageBookmark(pageNumber, pageReferenceType, null)
            true
        }
    }

    override suspend fun removeAyahBookmark(ayahKey: String) {
        if (!ValidationHelpers.isValidAyahKey(ayahKey)) return
        dataSource.dataStore.edit { preferences ->
            val records = preferences[Keys.bookmarksJson].orEmpty().toBookmarkRecords()
                .filterNot { it.bookmarkType == BookmarkType.AYAH && it.ayahKey == ayahKey }
            preferences[Keys.bookmarksJson] = records.toJsonArray().toString()
        }
    }

    override suspend fun removePageBookmark(pageNumber: Int, pageReferenceType: PageReferenceType) {
        dataSource.dataStore.edit { preferences ->
            val records = preferences[Keys.bookmarksJson].orEmpty().toBookmarkRecords()
                .filterNot {
                    it.bookmarkType == BookmarkType.PAGE &&
                        it.pageNumber == pageNumber &&
                        it.pageReferenceType == pageReferenceType
                }
            preferences[Keys.bookmarksJson] = records.toJsonArray().toString()
        }
    }

    override suspend fun removeBookmarkById(id: Long) {
        dataSource.dataStore.edit { preferences ->
            val records = preferences[Keys.bookmarksJson].orEmpty().toBookmarkRecords()
                .filterNot { it.id == id }
            preferences[Keys.bookmarksJson] = records.toJsonArray().toString()
        }
    }

    override suspend fun isAyahBookmarked(ayahKey: String): Boolean {
        return getAllBookmarks().map { bookmarks ->
            bookmarks.any { it.bookmarkType == BookmarkType.AYAH && it.ayahKey == ayahKey }
        }.first()
    }

    override suspend fun isPageBookmarked(pageNumber: Int, pageReferenceType: PageReferenceType): Boolean {
        return getAllBookmarks().map { bookmarks ->
            bookmarks.any {
                it.bookmarkType == BookmarkType.PAGE &&
                    it.pageNumber == pageNumber &&
                    it.pageReferenceType == pageReferenceType
            }
        }.first()
    }

    override suspend fun getBookmarkCount(): Int = getAllBookmarks().map { it.size }.first()

    private fun String.toBookmarkRecords(): List<BookmarkRecord> {
        if (isBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(this)
            buildList {
                for (index in 0 until array.length()) {
                    array.optJSONObject(index)?.toBookmarkRecord()?.let(::add)
                }
            }
        }.getOrDefault(emptyList())
    }

    private fun List<BookmarkRecord>.toJsonArray(): JSONArray {
        val array = JSONArray()
        forEach { record ->
            array.put(record.toJson())
        }
        return array
    }

    private fun BookmarkRecord.toJson(): JSONObject {
        return JSONObject()
            .put("id", id)
            .put("bookmarkType", bookmarkType.name)
            .put("ayahKey", ayahKey)
            .put("surahNumber", surahNumber)
            .put("ayahNumber", ayahNumber)
            .put("pageNumber", pageNumber)
            .put("pageReferenceType", pageReferenceType?.name)
            .put("createdAt", createdAt)
            .put("updatedAt", updatedAt)
    }

    private fun JSONObject.toBookmarkRecord(): BookmarkRecord? {
        val type = runCatching {
            BookmarkType.valueOf(optString("bookmarkType"))
        }.getOrNull() ?: return null
        return BookmarkRecord(
            id = optLong("id"),
            bookmarkType = type,
            ayahKey = optString("ayahKey").takeIf { it.isNotBlank() },
            surahNumber = if (has("surahNumber") && !isNull("surahNumber")) optInt("surahNumber") else null,
            ayahNumber = if (has("ayahNumber") && !isNull("ayahNumber")) optInt("ayahNumber") else null,
            pageNumber = if (has("pageNumber") && !isNull("pageNumber")) optInt("pageNumber") else null,
            pageReferenceType = resolvePageReferenceType(),
            createdAt = optLong("createdAt"),
            updatedAt = optLong("updatedAt"),
        )
    }

    private fun JSONObject.resolvePageReferenceType(): PageReferenceType? {
        val stored = optString("pageReferenceType").takeIf { it.isNotBlank() }
        if (stored != null) {
            return runCatching { PageReferenceType.valueOf(stored) }.getOrNull()
        }
        return if (optString("bookmarkType") == BookmarkType.PAGE.name) {
            PageReferenceType.INDOPAK
        } else {
            null
        }
    }

    private object Keys {
        val bookmarksJson = stringPreferencesKey("bookmarks_json")
    }
}

fun bookmarkRepository(context: Context): BookmarkRepository {
    return BookmarkRepositoryImpl(amanahPreferencesDataSource(context))
}
