package org.amanahquran.app.core.repository

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.amanahquran.app.core.datastore.AmanahPreferencesDataSource
import org.amanahquran.app.core.datastore.amanahPreferencesDataSource
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.theme.ThemeMode

data class ReaderSettings(
    val selectedScript: ScriptType = ScriptType.INDOPAK,
    val selectedTheme: ThemeMode = ThemeMode.SYSTEM,
    val arabicFontSizeSp: Float = 24f,
    val elderModeEnabled: Boolean = false,
    val firstLaunchMessageDismissed: Boolean = false,
)

interface ReaderSettingsRepository {
    val settings: Flow<ReaderSettings>

    suspend fun setSelectedScript(scriptType: ScriptType)
    suspend fun setSelectedTheme(themeMode: ThemeMode)
    suspend fun setArabicFontSize(arabicFontSizeSp: Float)
    suspend fun setElderModeEnabled(enabled: Boolean)
    suspend fun setFirstLaunchMessageDismissed(dismissed: Boolean)
}

class ReaderSettingsRepositoryImpl(
    private val dataSource: AmanahPreferencesDataSource,
) : ReaderSettingsRepository {
    override val settings: Flow<ReaderSettings> = dataSource.dataStore.data.map { preferences ->
        preferences.toReaderSettings()
    }

    override suspend fun setSelectedScript(scriptType: ScriptType) {
        dataSource.dataStore.edit { preferences ->
            preferences[Keys.selectedScript] = scriptType.name
        }
    }

    override suspend fun setSelectedTheme(themeMode: ThemeMode) {
        dataSource.dataStore.edit { preferences ->
            preferences[Keys.selectedTheme] = themeMode.name
        }
    }

    override suspend fun setArabicFontSize(arabicFontSizeSp: Float) {
        dataSource.dataStore.edit { preferences ->
            preferences[Keys.arabicFontSizeSp] = arabicFontSizeSp.coerceIn(16f, 42f)
        }
    }

    override suspend fun setElderModeEnabled(enabled: Boolean) {
        dataSource.dataStore.edit { preferences ->
            preferences[Keys.elderModeEnabled] = enabled
        }
    }

    override suspend fun setFirstLaunchMessageDismissed(dismissed: Boolean) {
        dataSource.dataStore.edit { preferences ->
            preferences[Keys.firstLaunchMessageDismissed] = dismissed
        }
    }

    private fun Preferences.toReaderSettings(): ReaderSettings {
        val script = runCatching {
            ScriptType.valueOf(this[Keys.selectedScript].orEmpty())
        }.getOrDefault(ScriptType.INDOPAK)
        val theme = runCatching {
            ThemeMode.valueOf(this[Keys.selectedTheme].orEmpty())
        }.getOrDefault(ThemeMode.SYSTEM)
        return ReaderSettings(
            selectedScript = script,
            selectedTheme = theme,
            arabicFontSizeSp = this[Keys.arabicFontSizeSp] ?: DEFAULT_ARABIC_FONT_SIZE_SP,
            elderModeEnabled = this[Keys.elderModeEnabled] ?: false,
            firstLaunchMessageDismissed = this[Keys.firstLaunchMessageDismissed] ?: false,
        )
    }

    private object Keys {
        val selectedScript = stringPreferencesKey("selected_script")
        val selectedTheme = stringPreferencesKey("selected_theme")
        val arabicFontSizeSp = floatPreferencesKey("arabic_font_size_sp")
        val elderModeEnabled = booleanPreferencesKey("elder_mode_enabled")
        val firstLaunchMessageDismissed = booleanPreferencesKey("first_launch_message_dismissed")
    }

    companion object {
        const val DEFAULT_ARABIC_FONT_SIZE_SP = 24f
    }
}

fun readerSettingsRepository(context: Context): ReaderSettingsRepository {
    return ReaderSettingsRepositoryImpl(amanahPreferencesDataSource(context))
}
