package org.amanahquran.app.core.repository

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.amanahquran.app.core.datastore.AmanahPreferencesDataSource
import org.amanahquran.app.core.datastore.amanahPreferencesDataSource
import org.amanahquran.app.core.model.AutoScrollPace
import org.amanahquran.app.core.model.ReaderContentMode
import org.amanahquran.app.core.model.ReaderHeaderFormat
import org.amanahquran.app.core.model.ReaderZoomLevel
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.theme.QuranTypography
import org.amanahquran.app.core.theme.ThemeMode

data class ReaderSettings(
    val selectedScript: ScriptType = ScriptType.INDOPAK,
    val selectedTheme: ThemeMode = ThemeMode.SYSTEM,
    val arabicFontSizeSp: Float = 24f,
    val elderModeEnabled: Boolean = false,
    val bookModeEnabled: Boolean = false,
    val firstLaunchMessageDismissed: Boolean = false,
    val translationEnabled: Boolean = false,
    val translationFontSizeSp: Float = 18f,
    val arabicLineSpacingMultiplier: Float = 1.88f,
    val readerHorizontalPaddingDp: Float = 16f,
    // Unified Adaptive Reader Experience: four independently-remembered zoom levels (one per
    // script x Elder Mode combination) plus auto-scroll/onboarding state. These are additive --
    // arabicFontSizeSp above stays the single legacy raw value every older consumer (Search
    // results, the legacy Mushaf page renderer, and backup/restore) already reads, and every
    // zoom-level change also writes through to it so those consumers keep working unmodified.
    val indoPakZoomLevel: ReaderZoomLevel = ReaderZoomLevel.default,
    val uthmaniZoomLevel: ReaderZoomLevel = ReaderZoomLevel.default,
    val indoPakElderZoomLevel: ReaderZoomLevel = ReaderZoomLevel.elderDefault,
    val uthmaniElderZoomLevel: ReaderZoomLevel = ReaderZoomLevel.elderDefault,
    val autoScrollPace: AutoScrollPace = AutoScrollPace.default,
    val firstZoomHintShown: Boolean = false,
    val pinchToResizeEnabled: Boolean = true,
    // READER-UX-02: Continuous Mode + parallel split translation. Content mode is purely a
    // rendering choice (the ViewModel loads the same ayahs either way). Translation zoom reuses
    // the same seven-level scale as Arabic zoom but with its own base size and its own persisted
    // slot, since translationFontSizeSp already existed as a flat legacy value every pre-existing
    // consumer (the below-ayah translation text in Ayah Mode) reads -- exactly like arabicFontSizeSp,
    // every translationZoomLevel write also syncs translationFontSizeSp so nothing else needs to change.
    val readerContentMode: ReaderContentMode = ReaderContentMode.default,
    val translationZoomLevel: ReaderZoomLevel = ReaderZoomLevel.default,
    val linkedZoomEnabled: Boolean = true,
    // Advanced Reader Settings additions: header context format, keep-screen-awake while
    // reading, and the default full-screen state a Mushaf page session opens in.
    val readerHeaderFormat: ReaderHeaderFormat = ReaderHeaderFormat.SURAH_PAGE,
    val keepScreenAwakeEnabled: Boolean = false,
    // Matches MushafReaderUiState's pre-existing default (true) so introducing this setting
    // doesn't silently change current behavior for users who never touch it.
    val fullScreenReadingDefault: Boolean = true,
) {
    /** The zoom level that applies right now, given the currently selected script and Elder Mode. */
    fun effectiveZoomLevel(scriptType: ScriptType = selectedScript, elderMode: Boolean = elderModeEnabled): ReaderZoomLevel {
        return when (scriptType to elderMode) {
            ScriptType.INDOPAK to false -> indoPakZoomLevel
            ScriptType.INDOPAK to true -> indoPakElderZoomLevel
            ScriptType.UTHMANI to false -> uthmaniZoomLevel
            else -> uthmaniElderZoomLevel
        }
    }
}

interface ReaderSettingsRepository {
    val settings: Flow<ReaderSettings>

    suspend fun setSelectedScript(scriptType: ScriptType)
    suspend fun setSelectedTheme(themeMode: ThemeMode)
    suspend fun setArabicFontSize(arabicFontSizeSp: Float)
    suspend fun setElderModeEnabled(enabled: Boolean)
    suspend fun setBookModeEnabled(enabled: Boolean)
    suspend fun setFirstLaunchMessageDismissed(dismissed: Boolean)
    suspend fun setTranslationEnabled(enabled: Boolean)
    suspend fun setTranslationFontSize(fontSizeSp: Float)
    suspend fun setArabicLineSpacing(multiplier: Float)
    suspend fun setReaderHorizontalPadding(paddingDp: Float)

    /** Persists the zoom level for one (script, Elder Mode) slot and syncs [ReaderSettings.arabicFontSizeSp]. */
    suspend fun setZoomLevel(scriptType: ScriptType, elderMode: Boolean, level: ReaderZoomLevel)
    suspend fun increaseZoomLevel(scriptType: ScriptType, elderMode: Boolean)
    suspend fun decreaseZoomLevel(scriptType: ScriptType, elderMode: Boolean)
    suspend fun resetZoomLevel(scriptType: ScriptType, elderMode: Boolean)
    suspend fun setAutoScrollPace(pace: AutoScrollPace)
    suspend fun setFirstZoomHintShown(shown: Boolean)
    suspend fun setPinchToResizeEnabled(enabled: Boolean)

    suspend fun setReaderContentMode(mode: ReaderContentMode)
    suspend fun setLinkedZoomEnabled(enabled: Boolean)
    suspend fun setTranslationZoomLevel(level: ReaderZoomLevel)
    suspend fun increaseTranslationZoomLevel()
    suspend fun decreaseTranslationZoomLevel()
    suspend fun resetTranslationZoomLevel()

    suspend fun setReaderHeaderFormat(format: ReaderHeaderFormat)
    suspend fun setKeepScreenAwakeEnabled(enabled: Boolean)
    suspend fun setFullScreenReadingDefault(enabled: Boolean)

    /**
     * Resets reader display/behavior preferences to their defaults. Does not touch script/theme/
     * Elder Mode (accessibility, not a "reading preference"), nor anything outside this
     * preferences file -- bookmarks, last-read, reading history, and streak data live in their
     * own keys and are untouched by construction.
     */
    suspend fun resetReaderPreferences()
}

class ReaderSettingsRepositoryImpl(
    private val dataSource: AmanahPreferencesDataSource,
) : ReaderSettingsRepository {
    override val settings: Flow<ReaderSettings> = dataSource.dataStore.data.map { preferences ->
        preferences.toReaderSettings()
    }

    override suspend fun setSelectedScript(scriptType: ScriptType): Unit = withContext(NonCancellable) {
        dataSource.dataStore.edit { preferences ->
            preferences[Keys.selectedScript] = scriptType.name
        }
    }

    override suspend fun setSelectedTheme(themeMode: ThemeMode): Unit = withContext(NonCancellable) {
        dataSource.dataStore.edit { preferences ->
            preferences[Keys.selectedTheme] = themeMode.name
        }
    }

    override suspend fun setArabicFontSize(arabicFontSizeSp: Float): Unit = withContext(NonCancellable) {
        dataSource.dataStore.edit { preferences ->
            preferences[Keys.arabicFontSizeSp] = arabicFontSizeSp.coerceIn(16f, 42f)
        }
    }

    override suspend fun setElderModeEnabled(enabled: Boolean): Unit = withContext(NonCancellable) {
        dataSource.dataStore.edit { preferences ->
            preferences[Keys.elderModeEnabled] = enabled
        }
    }

    override suspend fun setBookModeEnabled(enabled: Boolean): Unit = withContext(NonCancellable) {
        dataSource.dataStore.edit { preferences ->
            preferences[Keys.bookModeEnabled] = enabled
        }
    }

    override suspend fun setFirstLaunchMessageDismissed(dismissed: Boolean): Unit = withContext(NonCancellable) {
        dataSource.dataStore.edit { preferences ->
            preferences[Keys.firstLaunchMessageDismissed] = dismissed
        }
    }

    override suspend fun setTranslationEnabled(enabled: Boolean): Unit = withContext(NonCancellable) {
        dataSource.dataStore.edit { preferences -> preferences[Keys.translationEnabled] = enabled }
    }

    override suspend fun setTranslationFontSize(fontSizeSp: Float): Unit = withContext(NonCancellable) {
        dataSource.dataStore.edit { preferences ->
            preferences[Keys.translationFontSizeSp] = fontSizeSp.coerceIn(14f, 32f)
        }
    }

    override suspend fun setArabicLineSpacing(multiplier: Float): Unit = withContext(NonCancellable) {
        dataSource.dataStore.edit { preferences -> preferences[Keys.arabicLineSpacingMultiplier] = multiplier.coerceIn(1.5f, 2.4f) }
    }

    override suspend fun setReaderHorizontalPadding(paddingDp: Float): Unit = withContext(NonCancellable) {
        dataSource.dataStore.edit { preferences -> preferences[Keys.readerHorizontalPaddingDp] = paddingDp.coerceIn(8f, 32f) }
    }

    override suspend fun setZoomLevel(scriptType: ScriptType, elderMode: Boolean, level: ReaderZoomLevel): Unit = withContext(NonCancellable) {
        dataSource.dataStore.edit { preferences ->
            preferences[zoomKeyFor(scriptType, elderMode)] = level.name
            val profile = QuranTypography.profileFor(scriptType)
            preferences[Keys.arabicFontSizeSp] = (profile.baseFontSize * level.multiplier)
                .coerceIn(profile.minimumFontSize, profile.maximumFontSize)
        }
    }

    override suspend fun increaseZoomLevel(scriptType: ScriptType, elderMode: Boolean) {
        val current = settings.currentEffectiveLevel(scriptType, elderMode)
        setZoomLevel(scriptType, elderMode, current.increased())
    }

    override suspend fun decreaseZoomLevel(scriptType: ScriptType, elderMode: Boolean) {
        val current = settings.currentEffectiveLevel(scriptType, elderMode)
        setZoomLevel(scriptType, elderMode, current.decreased())
    }

    override suspend fun resetZoomLevel(scriptType: ScriptType, elderMode: Boolean) {
        setZoomLevel(scriptType, elderMode, if (elderMode) ReaderZoomLevel.elderDefault else ReaderZoomLevel.default)
    }

    override suspend fun setAutoScrollPace(pace: AutoScrollPace): Unit = withContext(NonCancellable) {
        dataSource.dataStore.edit { preferences -> preferences[Keys.autoScrollPace] = pace.name }
    }

    override suspend fun setFirstZoomHintShown(shown: Boolean): Unit = withContext(NonCancellable) {
        dataSource.dataStore.edit { preferences -> preferences[Keys.firstZoomHintShown] = shown }
    }

    override suspend fun setPinchToResizeEnabled(enabled: Boolean): Unit = withContext(NonCancellable) {
        dataSource.dataStore.edit { preferences -> preferences[Keys.pinchToResizeEnabled] = enabled }
    }

    override suspend fun setReaderContentMode(mode: ReaderContentMode): Unit = withContext(NonCancellable) {
        dataSource.dataStore.edit { preferences -> preferences[Keys.readerContentMode] = mode.name }
    }

    override suspend fun setLinkedZoomEnabled(enabled: Boolean): Unit = withContext(NonCancellable) {
        dataSource.dataStore.edit { preferences -> preferences[Keys.linkedZoomEnabled] = enabled }
    }

    override suspend fun setTranslationZoomLevel(level: ReaderZoomLevel): Unit = withContext(NonCancellable) {
        dataSource.dataStore.edit { preferences ->
            preferences[Keys.translationZoomLevel] = level.name
            preferences[Keys.translationFontSizeSp] = (DEFAULT_TRANSLATION_FONT_SIZE_SP * level.multiplier)
                .coerceIn(14f, 32f)
        }
    }

    override suspend fun increaseTranslationZoomLevel() {
        val current = settings.first().translationZoomLevel
        setTranslationZoomLevel(current.increased())
    }

    override suspend fun decreaseTranslationZoomLevel() {
        val current = settings.first().translationZoomLevel
        setTranslationZoomLevel(current.decreased())
    }

    override suspend fun resetTranslationZoomLevel() {
        setTranslationZoomLevel(ReaderZoomLevel.default)
    }

    override suspend fun setReaderHeaderFormat(format: ReaderHeaderFormat): Unit = withContext(NonCancellable) {
        dataSource.dataStore.edit { preferences -> preferences[Keys.readerHeaderFormat] = format.name }
    }

    override suspend fun setKeepScreenAwakeEnabled(enabled: Boolean): Unit = withContext(NonCancellable) {
        dataSource.dataStore.edit { preferences -> preferences[Keys.keepScreenAwakeEnabled] = enabled }
    }

    override suspend fun setFullScreenReadingDefault(enabled: Boolean): Unit = withContext(NonCancellable) {
        dataSource.dataStore.edit { preferences -> preferences[Keys.fullScreenReadingDefault] = enabled }
    }

    override suspend fun resetReaderPreferences(): Unit = withContext(NonCancellable) {
        dataSource.dataStore.edit { preferences ->
            preferences.remove(Keys.arabicFontSizeSp)
            preferences.remove(Keys.translationFontSizeSp)
            preferences.remove(Keys.arabicLineSpacingMultiplier)
            preferences.remove(Keys.readerHorizontalPaddingDp)
            preferences.remove(Keys.zoomIndoPakNormal)
            preferences.remove(Keys.zoomUthmaniNormal)
            preferences.remove(Keys.zoomIndoPakElder)
            preferences.remove(Keys.zoomUthmaniElder)
            preferences.remove(Keys.autoScrollPace)
            preferences.remove(Keys.pinchToResizeEnabled)
            preferences.remove(Keys.readerContentMode)
            preferences.remove(Keys.translationZoomLevel)
            preferences.remove(Keys.linkedZoomEnabled)
            preferences.remove(Keys.readerHeaderFormat)
            preferences.remove(Keys.keepScreenAwakeEnabled)
            preferences.remove(Keys.fullScreenReadingDefault)
            preferences.remove(Keys.bookModeEnabled)
        }
    }

    private suspend fun Flow<ReaderSettings>.currentEffectiveLevel(scriptType: ScriptType, elderMode: Boolean): ReaderZoomLevel {
        return first().effectiveZoomLevel(scriptType, elderMode)
    }

    private fun zoomKeyFor(scriptType: ScriptType, elderMode: Boolean) = when (scriptType to elderMode) {
        ScriptType.INDOPAK to false -> Keys.zoomIndoPakNormal
        ScriptType.INDOPAK to true -> Keys.zoomIndoPakElder
        ScriptType.UTHMANI to false -> Keys.zoomUthmaniNormal
        else -> Keys.zoomUthmaniElder
    }

    private fun Preferences.toReaderSettings(): ReaderSettings {
        val script = runCatching {
            ScriptType.valueOf(this[Keys.selectedScript].orEmpty())
        }.getOrDefault(ScriptType.INDOPAK)
        val theme = runCatching {
            ThemeMode.valueOf(this[Keys.selectedTheme].orEmpty())
        }.getOrDefault(ThemeMode.SYSTEM)
        // Migration: a user who has never touched adaptive zoom has no stored per-slot level yet.
        // Normal IndoPak derives from whatever raw sp they already had (IndoPak's Standard base
        // is exactly the legacy default of 24sp, so an untouched install maps to STANDARD with
        // zero visual change); every other slot has no comparable legacy value, so it starts at
        // the documented default for that slot instead of guessing from an unrelated script's size.
        val migratedIndoPakNormal = ReaderZoomLevel.nearestTo(
            (this[Keys.arabicFontSizeSp] ?: DEFAULT_ARABIC_FONT_SIZE_SP) / QuranTypography.IndoPak.baseFontSize,
        )
        return ReaderSettings(
            selectedScript = script,
            selectedTheme = theme,
            arabicFontSizeSp = this[Keys.arabicFontSizeSp] ?: DEFAULT_ARABIC_FONT_SIZE_SP,
            elderModeEnabled = this[Keys.elderModeEnabled] ?: false,
            bookModeEnabled = this[Keys.bookModeEnabled] ?: false,
            firstLaunchMessageDismissed = this[Keys.firstLaunchMessageDismissed] ?: false,
            translationEnabled = this[Keys.translationEnabled] ?: false,
            translationFontSizeSp = this[Keys.translationFontSizeSp] ?: DEFAULT_TRANSLATION_FONT_SIZE_SP,
            arabicLineSpacingMultiplier = this[Keys.arabicLineSpacingMultiplier] ?: DEFAULT_ARABIC_LINE_SPACING,
            readerHorizontalPaddingDp = this[Keys.readerHorizontalPaddingDp] ?: DEFAULT_READER_PADDING_DP,
            indoPakZoomLevel = ReaderZoomLevel.fromStoredName(this[Keys.zoomIndoPakNormal]) ?: migratedIndoPakNormal,
            uthmaniZoomLevel = ReaderZoomLevel.fromStoredName(this[Keys.zoomUthmaniNormal]) ?: ReaderZoomLevel.default,
            indoPakElderZoomLevel = ReaderZoomLevel.fromStoredName(this[Keys.zoomIndoPakElder]) ?: ReaderZoomLevel.elderDefault,
            uthmaniElderZoomLevel = ReaderZoomLevel.fromStoredName(this[Keys.zoomUthmaniElder]) ?: ReaderZoomLevel.elderDefault,
            autoScrollPace = AutoScrollPace.fromStoredName(this[Keys.autoScrollPace]) ?: AutoScrollPace.default,
            firstZoomHintShown = this[Keys.firstZoomHintShown] ?: false,
            pinchToResizeEnabled = this[Keys.pinchToResizeEnabled] ?: true,
            readerContentMode = ReaderContentMode.fromStoredName(this[Keys.readerContentMode]) ?: ReaderContentMode.default,
            translationZoomLevel = ReaderZoomLevel.fromStoredName(this[Keys.translationZoomLevel]) ?: ReaderZoomLevel.default,
            linkedZoomEnabled = this[Keys.linkedZoomEnabled] ?: true,
            readerHeaderFormat = runCatching {
                ReaderHeaderFormat.valueOf(this[Keys.readerHeaderFormat].orEmpty())
            }.getOrDefault(ReaderHeaderFormat.SURAH_PAGE),
            keepScreenAwakeEnabled = this[Keys.keepScreenAwakeEnabled] ?: false,
            fullScreenReadingDefault = this[Keys.fullScreenReadingDefault] ?: true,
        )
    }

    private object Keys {
        val selectedScript = stringPreferencesKey("selected_script")
        val selectedTheme = stringPreferencesKey("selected_theme")
        val arabicFontSizeSp = floatPreferencesKey("arabic_font_size_sp")
        val elderModeEnabled = booleanPreferencesKey("elder_mode_enabled")
        val bookModeEnabled = booleanPreferencesKey("book_mode_enabled")
        val firstLaunchMessageDismissed = booleanPreferencesKey("first_launch_message_dismissed")
        val translationEnabled = booleanPreferencesKey("translation_enabled")
        val translationFontSizeSp = floatPreferencesKey("translation_font_size_sp")
        val arabicLineSpacingMultiplier = floatPreferencesKey("arabic_line_spacing_multiplier")
        val readerHorizontalPaddingDp = floatPreferencesKey("reader_horizontal_padding_dp")
        val zoomIndoPakNormal = stringPreferencesKey("zoom_level_indopak_normal")
        val zoomUthmaniNormal = stringPreferencesKey("zoom_level_uthmani_normal")
        val zoomIndoPakElder = stringPreferencesKey("zoom_level_indopak_elder")
        val zoomUthmaniElder = stringPreferencesKey("zoom_level_uthmani_elder")
        val autoScrollPace = stringPreferencesKey("auto_scroll_pace")
        val firstZoomHintShown = booleanPreferencesKey("first_zoom_hint_shown")
        val pinchToResizeEnabled = booleanPreferencesKey("pinch_to_resize_enabled")
        val readerContentMode = stringPreferencesKey("reader_content_mode")
        val translationZoomLevel = stringPreferencesKey("translation_zoom_level")
        val linkedZoomEnabled = booleanPreferencesKey("linked_zoom_enabled")
        val readerHeaderFormat = stringPreferencesKey("reader_header_format")
        val keepScreenAwakeEnabled = booleanPreferencesKey("keep_screen_awake_enabled")
        val fullScreenReadingDefault = booleanPreferencesKey("full_screen_reading_default")
    }

    companion object {
        const val DEFAULT_ARABIC_FONT_SIZE_SP = 24f
        const val DEFAULT_TRANSLATION_FONT_SIZE_SP = 18f
        const val DEFAULT_ARABIC_LINE_SPACING = 1.88f
        const val DEFAULT_READER_PADDING_DP = 16f
    }
}

fun readerSettingsRepository(context: Context): ReaderSettingsRepository {
    return ReaderSettingsRepositoryImpl(amanahPreferencesDataSource(context))
}
