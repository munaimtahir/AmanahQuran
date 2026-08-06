package org.amanahquran.app.core.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.amanahquran.app.core.datastore.AmanahPreferencesDataSource
import org.amanahquran.app.core.datastore.amanahPreferencesDataSource
import org.json.JSONArray
import org.json.JSONObject

data class BookmarkCollection(
    val id: String,
    val name: String,
    val bookmarkIds: Set<Long>,
    val createdAt: Long,
    val updatedAt: Long,
    val isDefault: Boolean,
)

interface BookmarkCollectionRepository {
    fun observeCollections(): Flow<List<BookmarkCollection>>
    suspend fun snapshotJson(): String = "[]"
    suspend fun create(name: String): String?
    suspend fun rename(id: String, name: String): Boolean
    suspend fun delete(id: String): Boolean
    suspend fun addBookmark(collectionId: String, bookmarkId: Long)
    suspend fun removeBookmark(collectionId: String, bookmarkId: Long)
    suspend fun replaceFromJson(json: String) = Unit
}

class BookmarkCollectionRepositoryImpl(
    private val dataSource: AmanahPreferencesDataSource,
) : BookmarkCollectionRepository {
    override fun observeCollections(): Flow<List<BookmarkCollection>> = dataSource.dataStore.data.map { preferences ->
        decode(preferences[KEY].orEmpty()).ifEmpty { listOf(defaultCollection()) }
    }

    override suspend fun snapshotJson(): String = encode(observeCollections().first())

    override suspend fun create(name: String): String? {
        val cleanName = name.trim().take(80)
        if (cleanName.isBlank()) return null
        val id = "collection-${System.currentTimeMillis()}"
        update { it + BookmarkCollection(id, cleanName, emptySet(), System.currentTimeMillis(), System.currentTimeMillis(), false) }
        return id
    }

    override suspend fun rename(id: String, name: String): Boolean {
        val cleanName = name.trim().take(80)
        if (cleanName.isBlank()) return false
        var changed = false
        update { collections ->
            collections.map { collection ->
                if (collection.id == id && !collection.isDefault) {
                    changed = true
                    collection.copy(name = cleanName, updatedAt = System.currentTimeMillis())
                } else collection
            }
        }
        return changed
    }

    override suspend fun delete(id: String): Boolean {
        var changed = false
        update { collections ->
            collections.filterNot { collection ->
                if (collection.id == id && !collection.isDefault) {
                    changed = true
                    true
                } else false
            }
        }
        return changed
    }

    override suspend fun addBookmark(collectionId: String, bookmarkId: Long) {
        update { collections ->
            collections.map { collection ->
                if (collection.id == collectionId) {
                    collection.copy(bookmarkIds = collection.bookmarkIds + bookmarkId, updatedAt = System.currentTimeMillis())
                } else collection
            }
        }
    }

    override suspend fun removeBookmark(collectionId: String, bookmarkId: Long) {
        update { collections ->
            collections.map { collection ->
                if (collection.id == collectionId) {
                    collection.copy(bookmarkIds = collection.bookmarkIds - bookmarkId, updatedAt = System.currentTimeMillis())
                } else collection
            }
        }
    }

    override suspend fun replaceFromJson(json: String): Unit {
        val decoded = decode(json)
        if (decoded.isEmpty()) return
        update { decoded }
    }

    private suspend fun update(transform: (List<BookmarkCollection>) -> List<BookmarkCollection>) {
        dataSource.dataStore.edit { preferences ->
            val current = decode(preferences[KEY].orEmpty()).ifEmpty { listOf(defaultCollection()) }
            preferences[KEY] = encode(transform(current))
        }
    }

    private fun defaultCollection() = BookmarkCollection("default", "Default", emptySet(), 0L, 0L, true)

    private fun encode(collections: List<BookmarkCollection>): String {
        val result = JSONArray()
        collections.forEach { collection ->
            result.put(JSONObject().apply {
                put("id", collection.id)
                put("name", collection.name)
                put("createdAt", collection.createdAt)
                put("updatedAt", collection.updatedAt)
                put("isDefault", collection.isDefault)
                put("bookmarkIds", JSONArray(collection.bookmarkIds.toList()))
            })
        }
        return result.toString()
    }

    private fun decode(value: String): List<BookmarkCollection> = runCatching {
        val array = JSONArray(value)
        buildList {
            for (index in 0 until array.length()) {
                val item = array.optJSONObject(index) ?: continue
                val ids = item.optJSONArray("bookmarkIds") ?: JSONArray()
                add(
                    BookmarkCollection(
                        id = item.optString("id"),
                        name = item.optString("name").trim(),
                        bookmarkIds = buildSet { for (i in 0 until ids.length()) add(ids.optLong(i)) },
                        createdAt = item.optLong("createdAt"),
                        updatedAt = item.optLong("updatedAt"),
                        isDefault = item.optBoolean("isDefault"),
                    ),
                )
            }
        }.filter { it.id.isNotBlank() && it.name.isNotBlank() }
    }.getOrDefault(emptyList())

    companion object {
        private val KEY = stringPreferencesKey("bookmark_collections_json")
    }
}

fun bookmarkCollectionRepository(context: Context): BookmarkCollectionRepository =
    BookmarkCollectionRepositoryImpl(amanahPreferencesDataSource(context))
