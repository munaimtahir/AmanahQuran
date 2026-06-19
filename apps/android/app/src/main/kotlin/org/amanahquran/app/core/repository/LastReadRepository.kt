package org.amanahquran.app.core.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import org.amanahquran.app.core.datastore.AmanahPreferencesDataSource
import org.amanahquran.app.core.datastore.amanahPreferencesDataSource
import org.amanahquran.app.core.model.ScriptType

data class LastReadState(
    val ayahKey: String,
    val surahNumber: Int,
    val ayahNumber: Int,
    val pageNumber: Int?,
    val juzNumber: Int?,
    val scriptType: ScriptType,
    val updatedAt: Long,
)

interface LastReadRepository {
    fun getLastRead(): Flow<LastReadState?>
    suspend fun saveLastRead(lastRead: LastReadState)
    suspend fun saveLastRead(
        ayahKey: String,
        surahNumber: Int,
        ayahNumber: Int,
        juzNumber: Int,
        pageNumber: Int,
        scriptType: ScriptType,
        scrollOffset: Int? = null,
    )
}

class LastReadRepositoryImpl(
    private val dataSource: AmanahPreferencesDataSource,
) : LastReadRepository {
    override fun getLastRead(): Flow<LastReadState?> {
        return dataSource.dataStore.data.map { preferences ->
            preferences[Keys.lastReadJson]?.takeIf { it.isNotBlank() }?.toLastReadState()
        }
    }

    override suspend fun saveLastRead(lastRead: LastReadState) {
        dataSource.dataStore.edit { preferences ->
            preferences[Keys.lastReadJson] = lastRead.toJson().toString()
        }
    }

    override suspend fun saveLastRead(
        ayahKey: String,
        surahNumber: Int,
        ayahNumber: Int,
        juzNumber: Int,
        pageNumber: Int,
        scriptType: ScriptType,
        scrollOffset: Int?,
    ) {
        saveLastRead(
            LastReadState(
                ayahKey = ayahKey,
                surahNumber = surahNumber,
                ayahNumber = ayahNumber,
                pageNumber = pageNumber,
                juzNumber = juzNumber,
                scriptType = scriptType,
                updatedAt = System.currentTimeMillis(),
            ),
        )
    }

    private fun LastReadState.toJson(): JSONObject {
        return JSONObject()
            .put("ayahKey", ayahKey)
            .put("surahNumber", surahNumber)
            .put("ayahNumber", ayahNumber)
            .put("pageNumber", pageNumber)
            .put("juzNumber", juzNumber)
            .put("scriptType", scriptType.name)
            .put("updatedAt", updatedAt)
    }

    private fun String.toLastReadState(): LastReadState? {
        return runCatching {
            val json = JSONObject(this)
            LastReadState(
                ayahKey = json.getString("ayahKey"),
                surahNumber = json.getInt("surahNumber"),
                ayahNumber = json.getInt("ayahNumber"),
                pageNumber = if (json.isNull("pageNumber")) null else json.getInt("pageNumber"),
                juzNumber = if (json.isNull("juzNumber")) null else json.getInt("juzNumber"),
                scriptType = ScriptType.valueOf(json.getString("scriptType")),
                updatedAt = json.getLong("updatedAt"),
            )
        }.getOrNull()
    }

    private object Keys {
        val lastReadJson = stringPreferencesKey("last_read_json")
    }
}

fun lastReadRepository(context: Context): LastReadRepository {
    return LastReadRepositoryImpl(amanahPreferencesDataSource(context))
}
