package org.amanahquran.app.core.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.amanahquran.app.core.datastore.amanahPreferencesDataSourceForFile
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.theme.ThemeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
    private lateinit var repository: ReaderSettingsRepository

    @Before
    fun setUp() {
        tempFile = File(RuntimeEnvironment.getApplication().filesDir, "amanah-settings-${System.nanoTime()}.preferences_pb")
        repository = ReaderSettingsRepositoryImpl(amanahPreferencesDataSourceForFile(tempFile))
    }

    @Test
    fun defaultsToIndoPakSystemThemeAndElderModeOff() = runTest {
        val settings = repository.settings.first()

        assertEquals(ScriptType.INDOPAK, settings.selectedScript)
        assertEquals(ThemeMode.SYSTEM, settings.selectedTheme)
        assertEquals(24f, settings.arabicFontSizeSp, 0.01f)
        assertFalse(settings.elderModeEnabled)
        assertFalse(settings.firstLaunchMessageDismissed)
    }

    @Test
    fun selectedScriptThemeFontAndElderModePersist() = runTest {
        repository.setSelectedScript(ScriptType.UTHMANI)
        repository.setSelectedTheme(ThemeMode.SEPIA)
        repository.setArabicFontSize(28f)
        repository.setElderModeEnabled(true)

        val persisted = repository.settings.first()

        assertEquals(ScriptType.UTHMANI, persisted.selectedScript)
        assertEquals(ThemeMode.SEPIA, persisted.selectedTheme)
        assertEquals(28f, persisted.arabicFontSizeSp, 0.01f)
        assertEquals(true, persisted.elderModeEnabled)
    }

    @Test
    fun firstLaunchDismissedPersists() = runTest {
        repository.setFirstLaunchMessageDismissed(true)

        val persisted = repository.settings.first()

        assertEquals(true, persisted.firstLaunchMessageDismissed)
    }
}
