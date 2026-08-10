package org.amanahquran.app.feature.reminder

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.time.LocalDate
import java.time.ZoneId
import org.amanahquran.app.core.repository.reminderSettingsRepository
import org.amanahquran.app.core.repository.readingActivityRepository

/**
 * Decides whether "now" warrants showing the reading-reminder notification: the reminder must
 * be enabled, today must be one of the selected repeat days, and (when Smart Reminder is on)
 * today must not already have qualified for a reading day. Kept independent of WorkManager/
 * CoroutineWorker so the decision itself is trivially unit-testable.
 */
object ReadingReminderEvaluator {
    suspend fun maybeShowNotification(context: Context, zoneId: ZoneId = ZoneId.systemDefault()) {
        val settings = reminderSettingsRepository(context).getSettings()
        if (!settings.enabled) return

        val today = LocalDate.now(zoneId)
        if (today.dayOfWeek !in settings.repeatDays) return

        val alreadyQualified = settings.smartReminderEnabled &&
            readingActivityRepository(context).getActivityForDate(today)?.qualifiedForReadingDay == true
        if (!alreadyQualified) {
            ReminderNotifications.show(context)
        }
    }
}

/**
 * Fires once for the reminder's next scheduled time, then reschedules the following occurrence
 * regardless of whether a notification was actually shown. Runs entirely on-device; no network.
 */
class ReadingReminderWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val zoneId = ZoneId.systemDefault()
        ReadingReminderEvaluator.maybeShowNotification(applicationContext, zoneId)
        val settings = reminderSettingsRepository(applicationContext).getSettings()
        ReminderScheduler.reschedule(applicationContext, settings, zoneId)
        return Result.success()
    }
}
