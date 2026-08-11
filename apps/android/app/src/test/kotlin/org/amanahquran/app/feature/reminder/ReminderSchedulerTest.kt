package org.amanahquran.app.feature.reminder

import android.content.Context
import androidx.work.Configuration
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import java.time.DayOfWeek
import org.amanahquran.app.core.model.ReminderSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * Lifecycle-hardening coverage: repeated settings changes (time edits, toggling on/off, app
 * reopen after force-stop) must never leave more than one reminder job queued, and disabling
 * must actually clear any pending job rather than leaving a stale one to fire later.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ReminderSchedulerTest {
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        val config = Configuration.Builder()
            .setExecutor(SynchronousExecutor())
            .setTaskExecutor(SynchronousExecutor())
            .build()
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
    }

    private fun queuedWorkCount(): Int {
        val infos = WorkManager.getInstance(context).getWorkInfosForUniqueWork("reading_reminder").get()
        return infos.count { it.state == WorkInfo.State.ENQUEUED }
    }

    @Test
    fun reschedulingRepeatedlyNeverStacksDuplicateJobs() {
        val settings = ReminderSettings(enabled = true, hour = 20, minute = 0, repeatDays = DayOfWeek.entries.toSet())

        // Simulates the user changing the reminder time several times in a row.
        ReminderScheduler.reschedule(context, settings.copy(hour = 6))
        ReminderScheduler.reschedule(context, settings.copy(hour = 7))
        ReminderScheduler.reschedule(context, settings.copy(hour = 8))

        assertEquals(1, queuedWorkCount())
    }

    @Test
    fun disablingReminderClearsAnyPendingJob() {
        ReminderScheduler.reschedule(context, ReminderSettings(enabled = true, repeatDays = DayOfWeek.entries.toSet()))
        assertTrue(queuedWorkCount() >= 1)

        ReminderScheduler.reschedule(context, ReminderSettings(enabled = false))

        assertEquals(0, queuedWorkCount())
    }

    @Test
    fun reEnablingAfterDisableQueuesExactlyOneJob() {
        ReminderScheduler.reschedule(context, ReminderSettings(enabled = true, repeatDays = DayOfWeek.entries.toSet()))
        ReminderScheduler.reschedule(context, ReminderSettings(enabled = false))
        ReminderScheduler.reschedule(context, ReminderSettings(enabled = true, repeatDays = DayOfWeek.entries.toSet()))

        assertEquals(1, queuedWorkCount())
    }

    @Test
    fun emptyRepeatDaysWithEnabledTrueQueuesNothing() {
        // Defensive: enabled but no days selected should behave like "nothing to schedule",
        // not crash or leave a stray job.
        ReminderScheduler.reschedule(context, ReminderSettings(enabled = true, repeatDays = emptySet()))

        assertEquals(0, queuedWorkCount())
    }

    @Test
    fun cancelRemovesQueuedWork() {
        ReminderScheduler.reschedule(context, ReminderSettings(enabled = true, repeatDays = DayOfWeek.entries.toSet()))
        ReminderScheduler.cancel(context)

        assertEquals(0, queuedWorkCount())
    }
}
