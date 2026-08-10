package org.amanahquran.app.core.repository

import java.io.File
import java.time.DayOfWeek
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.amanahquran.app.core.datastore.amanahPreferencesDataSourceForFile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ReminderSettingsRepositoryTest {
    private lateinit var repository: ReminderSettingsRepository

    @Before
    fun setUp() {
        val tempFile = File(
            RuntimeEnvironment.getApplication().filesDir,
            "amanah-reminder-${System.nanoTime()}.preferences_pb",
        )
        repository = ReminderSettingsRepositoryImpl(amanahPreferencesDataSourceForFile(tempFile))
    }

    @Test
    fun defaultsAreDisabledWithAllDaysSelected() = runTest {
        val settings = repository.getSettings()
        assertFalse(settings.enabled)
        assertEquals(7, settings.repeatDays.size)
        assertTrue(settings.smartReminderEnabled)
    }

    @Test
    fun updatePersistsAcrossReads() = runTest {
        repository.update {
            it.copy(enabled = true, hour = 6, minute = 45, repeatDays = setOf(DayOfWeek.MONDAY, DayOfWeek.FRIDAY))
        }

        val reloaded = repository.settings.first()
        assertTrue(reloaded.enabled)
        assertEquals(6, reloaded.hour)
        assertEquals(45, reloaded.minute)
        assertEquals(setOf(DayOfWeek.MONDAY, DayOfWeek.FRIDAY), reloaded.repeatDays)
    }

    @Test
    fun smartReminderTogglePersists() = runTest {
        repository.update { it.copy(smartReminderEnabled = false) }
        assertFalse(repository.getSettings().smartReminderEnabled)
    }
}
