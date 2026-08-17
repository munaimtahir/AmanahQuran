package org.amanahquran.app.core.repository

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.amanahquran.app.core.datastore.amanahPreferencesDataSourceForFile
import org.amanahquran.app.core.model.AutoScrollPace
import org.amanahquran.app.core.model.ReaderContentMode
import org.amanahquran.app.core.model.ReaderHeaderFormat
import org.amanahquran.app.core.model.ReaderZoomLevel
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.theme.ThemeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import org.robolectric.RuntimeEnvironment
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ReaderSettingsRepositoryTest {
    private lateinit var tempFile: File
    private lateinit var dataSource: org.amanahquran.app.core.datastore.AmanahPreferencesDataSource
    private lateinit var repository: ReaderSettingsRepository

    @Before
    fun setUp() {
        tempFile = File(RuntimeEnvironment.getApplication().filesDir, "amanah-settings-${System.nanoTime()}.preferences_pb")
        dataSource = amanahPreferencesDataSourceForFile(tempFile)
        repository = ReaderSettingsRepositoryImpl(dataSource)
    }

    @Test
    fun defaultsToIndoPakSystemThemeAndElderModeOff() = runTest {
        val settings = repository.settings.first()

        assertEquals(ScriptType.INDOPAK, settings.selectedScript)
        assertEquals(ThemeMode.SYSTEM, settings.selectedTheme)
        assertEquals(24f, settings.arabicFontSizeSp, 0.01f)
        assertFalse(settings.elderModeEnabled)
        assertFalse(settings.firstLaunchMessageDismissed)
        assertFalse(settings.translationEnabled)
        assertEquals(18f, settings.translationFontSizeSp, 0.01f)
    }

    @Test
    fun selectedScriptThemeFontAndElderModePersist() = runTest {
        repository.setSelectedScript(ScriptType.UTHMANI)
        repository.setSelectedTheme(ThemeMode.SEPIA)
        repository.setArabicFontSize(28f)
        repository.setElderModeEnabled(true)
        repository.setTranslationEnabled(true)
        repository.setTranslationFontSize(22f)

        val persisted = repository.settings.first()

        assertEquals(ScriptType.UTHMANI, persisted.selectedScript)
        assertEquals(ThemeMode.SEPIA, persisted.selectedTheme)
        assertEquals(28f, persisted.arabicFontSizeSp, 0.01f)
        assertEquals(true, persisted.elderModeEnabled)
        assertEquals(true, persisted.translationEnabled)
        assertEquals(22f, persisted.translationFontSizeSp, 0.01f)
    }

    @Test
    fun firstLaunchDismissedPersists() = runTest {
        repository.setFirstLaunchMessageDismissed(true)

        val persisted = repository.settings.first()

        assertEquals(true, persisted.firstLaunchMessageDismissed)
    }

    @Test
    fun freshInstallMigratesIndoPakNormalToStandardAndElderSlotsToExtraLarge() = runTest {
        val settings = repository.settings.first()

        assertEquals(ReaderZoomLevel.STANDARD, settings.indoPakZoomLevel)
        assertEquals(ReaderZoomLevel.STANDARD, settings.uthmaniZoomLevel)
        assertEquals(ReaderZoomLevel.EXTRA_LARGE, settings.indoPakElderZoomLevel)
        assertEquals(ReaderZoomLevel.EXTRA_LARGE, settings.uthmaniElderZoomLevel)
        assertEquals(AutoScrollPace.COMFORTABLE, settings.autoScrollPace)
        assertFalse(settings.firstZoomHintShown)
        assertTrue(settings.pinchToResizeEnabled)
    }

    @Test
    fun theFourScriptElderSlotsPersistIndependently() = runTest {
        repository.setZoomLevel(ScriptType.INDOPAK, elderMode = false, ReaderZoomLevel.LARGE)
        repository.setZoomLevel(ScriptType.UTHMANI, elderMode = false, ReaderZoomLevel.SMALL)
        repository.setZoomLevel(ScriptType.INDOPAK, elderMode = true, ReaderZoomLevel.MAXIMUM)
        repository.setZoomLevel(ScriptType.UTHMANI, elderMode = true, ReaderZoomLevel.ELDER)

        val persisted = repository.settings.first()

        assertEquals(ReaderZoomLevel.LARGE, persisted.indoPakZoomLevel)
        assertEquals(ReaderZoomLevel.SMALL, persisted.uthmaniZoomLevel)
        assertEquals(ReaderZoomLevel.MAXIMUM, persisted.indoPakElderZoomLevel)
        assertEquals(ReaderZoomLevel.ELDER, persisted.uthmaniElderZoomLevel)
    }

    @Test
    fun effectiveZoomLevelSwitchesBetweenNormalAndElderSlotsPerScript() = runTest {
        repository.setZoomLevel(ScriptType.INDOPAK, elderMode = false, ReaderZoomLevel.STANDARD)
        repository.setZoomLevel(ScriptType.INDOPAK, elderMode = true, ReaderZoomLevel.EXTRA_LARGE)

        val settings = repository.settings.first()

        assertEquals(ReaderZoomLevel.STANDARD, settings.effectiveZoomLevel(ScriptType.INDOPAK, elderMode = false))
        assertEquals(ReaderZoomLevel.EXTRA_LARGE, settings.effectiveZoomLevel(ScriptType.INDOPAK, elderMode = true))
    }

    @Test
    fun increaseAndDecreaseZoomLevelStepOneLevelForTheGivenSlotOnly() = runTest {
        repository.increaseZoomLevel(ScriptType.INDOPAK, elderMode = false)
        var settings = repository.settings.first()
        assertEquals(ReaderZoomLevel.LARGE, settings.indoPakZoomLevel)
        assertEquals(ReaderZoomLevel.STANDARD, settings.uthmaniZoomLevel)

        repository.decreaseZoomLevel(ScriptType.INDOPAK, elderMode = false)
        repository.decreaseZoomLevel(ScriptType.INDOPAK, elderMode = false)
        settings = repository.settings.first()
        assertEquals(ReaderZoomLevel.SMALL, settings.indoPakZoomLevel)
    }

    @Test
    fun resetZoomLevelReturnsToTheDocumentedDefaultForNormalOrElder() = runTest {
        repository.setZoomLevel(ScriptType.INDOPAK, elderMode = false, ReaderZoomLevel.MAXIMUM)
        repository.setZoomLevel(ScriptType.INDOPAK, elderMode = true, ReaderZoomLevel.COMPACT)

        repository.resetZoomLevel(ScriptType.INDOPAK, elderMode = false)
        repository.resetZoomLevel(ScriptType.INDOPAK, elderMode = true)

        val settings = repository.settings.first()
        assertEquals(ReaderZoomLevel.STANDARD, settings.indoPakZoomLevel)
        assertEquals(ReaderZoomLevel.EXTRA_LARGE, settings.indoPakElderZoomLevel)
    }

    @Test
    fun settingAZoomLevelSyncsTheLegacyArabicFontSizeField() = runTest {
        // Search results, the legacy Mushaf page renderer, and backup/restore all still read
        // arabicFontSizeSp directly -- a zoom-level change must keep it in sync.
        repository.setZoomLevel(ScriptType.INDOPAK, elderMode = false, ReaderZoomLevel.EXTRA_LARGE)

        val settings = repository.settings.first()
        assertEquals(24f * ReaderZoomLevel.EXTRA_LARGE.multiplier, settings.arabicFontSizeSp, 0.01f)
    }

    @Test
    fun autoScrollPacePersists() = runTest {
        repository.setAutoScrollPace(AutoScrollPace.VERY_FAST)

        assertEquals(AutoScrollPace.VERY_FAST, repository.settings.first().autoScrollPace)
    }

    @Test
    fun firstZoomHintAndPinchToResizePreferencesPersist() = runTest {
        repository.setFirstZoomHintShown(true)
        repository.setPinchToResizeEnabled(false)

        val settings = repository.settings.first()
        assertTrue(settings.firstZoomHintShown)
        assertFalse(settings.pinchToResizeEnabled)
    }

    @Test
    fun invalidStoredZoomLevelAndPaceNamesFallBackToDefaultsSafely() = runTest {
        dataSource.dataStore.edit { preferences ->
            preferences[stringPreferencesKey("zoom_level_indopak_normal")] = "NOT_A_REAL_LEVEL"
            preferences[stringPreferencesKey("auto_scroll_pace")] = "WARP_SPEED"
        }

        val settings = repository.settings.first()

        assertEquals(ReaderZoomLevel.STANDARD, settings.indoPakZoomLevel)
        assertEquals(AutoScrollPace.COMFORTABLE, settings.autoScrollPace)
    }

    @Test
    fun anExistingUserWithACustomFontSizeMigratesToTheNearestIndoPakLevel() = runTest {
        // Simulates an install from before adaptive zoom existed, where only the raw sp value
        // was ever persisted (no zoom-level key present yet).
        repository.setArabicFontSize(28f)

        val settings = repository.settings.first()

        assertEquals(ReaderZoomLevel.nearestTo(28f / 24f), settings.indoPakZoomLevel)
    }

    @Test
    fun freshInstallDefaultsToContinuousModeAndLinkedTranslationZoom() = runTest {
        val settings = repository.settings.first()

        assertEquals(ReaderContentMode.CONTINUOUS, settings.readerContentMode)
        assertEquals(ReaderZoomLevel.STANDARD, settings.translationZoomLevel)
        assertTrue(settings.linkedZoomEnabled)
    }

    @Test
    fun readerContentModeAndLinkedZoomPersist() = runTest {
        repository.setReaderContentMode(ReaderContentMode.AYAH)
        repository.setLinkedZoomEnabled(false)

        val settings = repository.settings.first()

        assertEquals(ReaderContentMode.AYAH, settings.readerContentMode)
        assertFalse(settings.linkedZoomEnabled)
    }

    @Test
    fun translationZoomLevelPersistsIndependentlyOfArabicZoom() = runTest {
        repository.setZoomLevel(ScriptType.INDOPAK, elderMode = false, ReaderZoomLevel.MAXIMUM)
        repository.setTranslationZoomLevel(ReaderZoomLevel.SMALL)

        val settings = repository.settings.first()

        assertEquals(ReaderZoomLevel.MAXIMUM, settings.indoPakZoomLevel)
        assertEquals(ReaderZoomLevel.SMALL, settings.translationZoomLevel)
    }

    @Test
    fun translationZoomLevelSyncsTheLegacyTranslationFontSizeField() = runTest {
        repository.setTranslationZoomLevel(ReaderZoomLevel.EXTRA_LARGE)

        val settings = repository.settings.first()
        assertEquals(18f * ReaderZoomLevel.EXTRA_LARGE.multiplier, settings.translationFontSizeSp, 0.01f)
    }

    @Test
    fun increaseDecreaseAndResetTranslationZoomLevel() = runTest {
        repository.increaseTranslationZoomLevel()
        assertEquals(ReaderZoomLevel.LARGE, repository.settings.first().translationZoomLevel)

        repository.decreaseTranslationZoomLevel()
        repository.decreaseTranslationZoomLevel()
        assertEquals(ReaderZoomLevel.SMALL, repository.settings.first().translationZoomLevel)

        repository.resetTranslationZoomLevel()
        assertEquals(ReaderZoomLevel.STANDARD, repository.settings.first().translationZoomLevel)
    }

    @Test
    fun invalidStoredContentModeAndTranslationZoomFallBackToDefaultsSafely() = runTest {
        dataSource.dataStore.edit { preferences ->
            preferences[stringPreferencesKey("reader_content_mode")] = "SCROLL_FEED"
            preferences[stringPreferencesKey("translation_zoom_level")] = "NOT_A_REAL_LEVEL"
        }

        val settings = repository.settings.first()

        assertEquals(ReaderContentMode.CONTINUOUS, settings.readerContentMode)
        assertEquals(ReaderZoomLevel.STANDARD, settings.translationZoomLevel)
    }

    @Test
    fun freshInstallDefaultsHeaderFormatAndDisplayPreferences() = runTest {
        val settings = repository.settings.first()

        assertEquals(ReaderHeaderFormat.SURAH_PAGE, settings.readerHeaderFormat)
        assertFalse(settings.keepScreenAwakeEnabled)
        assertTrue(settings.fullScreenReadingDefault) // matches MushafReaderUiState's pre-existing default
    }

    @Test
    fun headerFormatAndDisplayPreferencesPersist() = runTest {
        repository.setReaderHeaderFormat(ReaderHeaderFormat.SURAH_JUZ_PAGE)
        repository.setKeepScreenAwakeEnabled(true)
        repository.setFullScreenReadingDefault(false)

        val settings = repository.settings.first()
        assertEquals(ReaderHeaderFormat.SURAH_JUZ_PAGE, settings.readerHeaderFormat)
        assertTrue(settings.keepScreenAwakeEnabled)
        assertFalse(settings.fullScreenReadingDefault)
    }

    @Test
    fun invalidStoredHeaderFormatFallsBackToDefaultSafely() = runTest {
        dataSource.dataStore.edit { preferences ->
            preferences[stringPreferencesKey("reader_header_format")] = "NOT_A_REAL_FORMAT"
        }

        assertEquals(ReaderHeaderFormat.SURAH_PAGE, repository.settings.first().readerHeaderFormat)
    }

    @Test
    fun resetReaderPreferencesRestoresOnlyIntendedFieldsToDefaults() = runTest {
        repository.setArabicFontSize(32f)
        repository.setTranslationFontSize(26f)
        repository.setArabicLineSpacing(2.2f)
        repository.setReaderHorizontalPadding(28f)
        repository.setAutoScrollPace(AutoScrollPace.VERY_FAST)
        repository.setReaderContentMode(ReaderContentMode.CONTINUOUS)
        repository.setLinkedZoomEnabled(false)
        repository.setReaderHeaderFormat(ReaderHeaderFormat.PAGE_ONLY)
        repository.setKeepScreenAwakeEnabled(true)
        repository.setFullScreenReadingDefault(false)
        repository.setBookModeEnabled(true)

        repository.resetReaderPreferences()

        val settings = repository.settings.first()
        assertEquals(24f, settings.arabicFontSizeSp, 0.01f)
        assertEquals(18f, settings.translationFontSizeSp, 0.01f)
        assertEquals(1.88f, settings.arabicLineSpacingMultiplier, 0.01f)
        assertEquals(16f, settings.readerHorizontalPaddingDp, 0.01f)
        assertEquals(AutoScrollPace.COMFORTABLE, settings.autoScrollPace)
        assertEquals(ReaderContentMode.CONTINUOUS, settings.readerContentMode)
        assertTrue(settings.linkedZoomEnabled)
        assertEquals(ReaderHeaderFormat.SURAH_PAGE, settings.readerHeaderFormat)
        assertFalse(settings.keepScreenAwakeEnabled)
        assertTrue(settings.fullScreenReadingDefault)
        assertFalse(settings.bookModeEnabled)
    }

    @Test
    fun resetReaderPreferencesDoesNotTouchScriptThemeOrElderMode() = runTest {
        repository.setSelectedScript(ScriptType.UTHMANI)
        repository.setSelectedTheme(ThemeMode.SEPIA)
        repository.setElderModeEnabled(true)
        repository.setFirstLaunchMessageDismissed(true)

        repository.resetReaderPreferences()

        val settings = repository.settings.first()
        assertEquals(ScriptType.UTHMANI, settings.selectedScript)
        assertEquals(ThemeMode.SEPIA, settings.selectedTheme)
        assertTrue(settings.elderModeEnabled)
        assertTrue(settings.firstLaunchMessageDismissed)
    }
}
