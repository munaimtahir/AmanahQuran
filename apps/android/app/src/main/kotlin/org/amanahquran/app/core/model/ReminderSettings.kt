package org.amanahquran.app.core.model

import java.time.DayOfWeek

data class ReminderSettings(
    val enabled: Boolean = false,
    val hour: Int = 20,
    val minute: Int = 0,
    val repeatDays: Set<DayOfWeek> = DayOfWeek.entries.toSet(),
    val smartReminderEnabled: Boolean = true,
) {
    companion object {
        const val NOTIFICATION_TITLE = "Amanah Quran"
        const val NOTIFICATION_BODY = "Time for your Quran reading"
    }
}
