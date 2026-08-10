package org.amanahquran.app.feature.reminder

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import org.amanahquran.app.core.model.ReminderSettings
import org.amanahquran.app.core.util.ReminderScheduleCalculator

/**
 * Schedules the reminder as a chain of one-time WorkManager jobs (each run reschedules the
 * next) rather than a fixed-interval PeriodicWorkRequest, so it can land at a specific
 * user-chosen clock time instead of only an approximate interval. No network is ever required.
 */
object ReminderScheduler {
    private const val UNIQUE_WORK_NAME = "reading_reminder"

    fun reschedule(context: Context, settings: ReminderSettings, zoneId: ZoneId = ZoneId.systemDefault()) {
        val workManager = WorkManager.getInstance(context)
        if (!settings.enabled || settings.repeatDays.isEmpty()) {
            workManager.cancelUniqueWork(UNIQUE_WORK_NAME)
            return
        }

        val now = ZonedDateTime.now(zoneId)
        val next = ReminderScheduleCalculator.nextOccurrence(now, settings.hour, settings.minute, settings.repeatDays)
            ?: return
        val delay = ReminderScheduleCalculator.delayUntil(now, next)

        val request = OneTimeWorkRequestBuilder<ReadingReminderWorker>()
            .setInitialDelay(delay.toMillis(), TimeUnit.MILLISECONDS)
            .build()

        // REPLACE: settings changes (time/days/enable toggle) always supersede whatever was
        // previously queued rather than stacking duplicate future reminders.
        workManager.enqueueUniqueWork(UNIQUE_WORK_NAME, ExistingWorkPolicy.REPLACE, request)
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_WORK_NAME)
    }
}
