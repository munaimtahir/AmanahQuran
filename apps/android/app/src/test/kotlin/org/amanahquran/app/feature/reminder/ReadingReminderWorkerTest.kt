package org.amanahquran.app.feature.reminder

import android.app.NotificationManager
import android.content.Context
import java.time.DayOfWeek
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.testing.WorkManagerTestInitHelper
import org.amanahquran.app.core.repository.readingActivityRepository
import org.amanahquran.app.core.repository.reminderSettingsRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ReadingReminderWorkerTest {
    private lateinit var context: Context

    @Before
    fun setUp() = runTest {
        context = RuntimeEnvironment.getApplication()
        shadowOf(RuntimeEnvironment.getApplication()).grantPermissions(android.Manifest.permission.POST_NOTIFICATIONS)
        // Neither Robolectric's shadow NotificationManager nor the "amanah_quran_user_state"
        // DataStore file (filesDir isn't wiped between @Test methods here) reset automatically,
        // so explicitly clear both before every test.
        androidx.core.app.NotificationManagerCompat.from(context).cancelAll()
        reminderSettingsRepository(context).update { org.amanahquran.app.core.model.ReminderSettings() }
        readingActivityRepository(context).replaceAllActivity(emptyList())
    }

    private fun postedNotificationCount(): Int =
        shadowOf(context.getSystemService(NotificationManager::class.java)).allNotifications.size

    // -- Notification decision logic (ReadingReminderEvaluator), independent of WorkManager --

    @Test
    fun disabledReminderPostsNothing() = runTest {
        reminderSettingsRepository(context).update { it.copy(enabled = false) }

        ReadingReminderEvaluator.maybeShowNotification(context)

        assertEquals(0, postedNotificationCount())
    }

    @Test
    fun nonQualifiedDayReceivesNotification() = runTest {
        reminderSettingsRepository(context).update {
            it.copy(enabled = true, smartReminderEnabled = true, repeatDays = DayOfWeek.entries.toSet())
        }

        ReadingReminderEvaluator.maybeShowNotification(context)

        assertEquals(1, postedNotificationCount())
    }

    @Test
    fun smartReminderSuppressesNotificationAfterQualifyingReading() = runTest {
        val today = LocalDate.now()
        readingActivityRepository(context).recordSession(today, 150, emptySet(), emptySet(), timestamp = 1L)
        reminderSettingsRepository(context).update {
            it.copy(enabled = true, smartReminderEnabled = true, repeatDays = DayOfWeek.entries.toSet())
        }

        ReadingReminderEvaluator.maybeShowNotification(context)

        assertEquals(0, postedNotificationCount())
    }

    @Test
    fun qualifyingReadingStillNotifiesWhenSmartReminderDisabled() = runTest {
        val today = LocalDate.now()
        readingActivityRepository(context).recordSession(today, 150, emptySet(), emptySet(), timestamp = 1L)
        reminderSettingsRepository(context).update {
            it.copy(enabled = true, smartReminderEnabled = false, repeatDays = DayOfWeek.entries.toSet())
        }

        ReadingReminderEvaluator.maybeShowNotification(context)

        assertEquals(1, postedNotificationCount())
    }

    @Test
    fun dayNotInRepeatDaysPostsNothing() = runTest {
        val today = LocalDate.now()
        reminderSettingsRepository(context).update {
            it.copy(enabled = true, repeatDays = DayOfWeek.entries.toSet() - today.dayOfWeek)
        }

        ReadingReminderEvaluator.maybeShowNotification(context)

        assertEquals(0, postedNotificationCount())
    }

    // -- doWork() smoke test: exercises the full CoroutineWorker + WorkManager reschedule path --

    @Test
    fun doWorkSucceedsAndReschedulesWithoutCrashing() = runTest {
        val config = Configuration.Builder()
            .setExecutor(SynchronousExecutor())
            .setTaskExecutor(SynchronousExecutor())
            .build()
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
        reminderSettingsRepository(context).update {
            it.copy(enabled = true, repeatDays = DayOfWeek.entries.toSet())
        }

        val result = TestListenableWorkerBuilder<ReadingReminderWorker>(context).build().doWork()

        assertTrue(result is ListenableWorker.Result.Success)
    }
}
