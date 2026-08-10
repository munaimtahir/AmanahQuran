package org.amanahquran.app.core.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import java.time.DayOfWeek
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.amanahquran.app.core.datastore.AmanahPreferencesDataSource
import org.amanahquran.app.core.datastore.amanahPreferencesDataSource
import org.amanahquran.app.core.model.ReminderSettings
import org.json.JSONArray
import org.json.JSONObject

interface ReminderSettingsRepository {
    val settings: Flow<ReminderSettings>
    suspend fun getSettings(): ReminderSettings = settings.first()
    suspend fun update(transform: (ReminderSettings) -> ReminderSettings)
}

class ReminderSettingsRepositoryImpl(
    private val dataSource: AmanahPreferencesDataSource,
) : ReminderSettingsRepository {
    override val settings: Flow<ReminderSettings> = dataSource.dataStore.data.map { preferences ->
        preferences[Keys.reminderJson].orEmpty().toReminderSettings()
    }

    override suspend fun update(transform: (ReminderSettings) -> ReminderSettings): Unit = withContext(NonCancellable) {
        dataSource.dataStore.edit { preferences ->
            val current = preferences[Keys.reminderJson].orEmpty().toReminderSettings()
            preferences[Keys.reminderJson] = transform(current).toJson().toString()
        }
    }

    private fun String.toReminderSettings(): ReminderSettings {
        if (isBlank()) return ReminderSettings()
        return runCatching {
            val json = JSONObject(this)
            val daysArray = json.optJSONArray("repeatDays")
            val days = if (daysArray != null) {
                buildSet {
                    for (i in 0 until daysArray.length()) {
                        runCatching { DayOfWeek.valueOf(daysArray.getString(i)) }.getOrNull()?.let(::add)
                    }
                }
            } else {
                DayOfWeek.entries.toSet()
            }
            ReminderSettings(
                enabled = json.optBoolean("enabled", false),
                hour = json.optInt("hour", 20),
                minute = json.optInt("minute", 0),
                repeatDays = days,
                smartReminderEnabled = json.optBoolean("smartReminderEnabled", true),
            )
        }.getOrDefault(ReminderSettings())
    }

    private fun ReminderSettings.toJson(): JSONObject = JSONObject()
        .put("enabled", enabled)
        .put("hour", hour)
        .put("minute", minute)
        .put("repeatDays", JSONArray(repeatDays.map { it.name }))
        .put("smartReminderEnabled", smartReminderEnabled)

    private object Keys {
        val reminderJson = stringPreferencesKey("reminder_settings_json")
    }
}

fun reminderSettingsRepository(context: Context): ReminderSettingsRepository =
    ReminderSettingsRepositoryImpl(amanahPreferencesDataSource(context))
