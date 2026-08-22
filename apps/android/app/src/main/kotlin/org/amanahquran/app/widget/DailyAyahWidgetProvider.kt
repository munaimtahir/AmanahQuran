package org.amanahquran.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.amanahquran.app.MainActivity
import org.amanahquran.app.R
import org.amanahquran.app.core.daily.dailyAyahRepository

class DailyAyahWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val daily = dailyAyahRepository(context).getToday()
                ids.forEach { id -> updateOne(context, manager, id, daily) }
            } finally {
                pending.finish()
            }
        }
    }

    override fun onEnabled(context: Context) {
        onUpdate(context, AppWidgetManager.getInstance(context), intArrayOf())
    }

    companion object {
        fun refresh(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val component = ComponentName(context, DailyAyahWidgetProvider::class.java)
            val ids = manager.getAppWidgetIds(component)
            if (ids.isNotEmpty()) manager.notifyAppWidgetViewDataChanged(ids, R.id.daily_ayah_widget_root)
            CoroutineScope(Dispatchers.IO).launch {
                val daily = dailyAyahRepository(context).getToday()
                ids.forEach { updateOne(context, manager, it, daily) }
            }
        }

        private fun updateOne(
            context: Context,
            manager: AppWidgetManager,
            id: Int,
            daily: org.amanahquran.app.core.daily.DailyAyahContent?,
        ) {
            val views = RemoteViews(context.packageName, R.layout.daily_ayah_widget)
            views.setTextViewText(R.id.daily_ayah_arabic, daily?.arabicText ?: "Open Amanah Quran")
            views.setTextViewText(R.id.daily_ayah_translation, daily?.translationText.orEmpty())
            views.setTextViewText(R.id.daily_ayah_reference, daily?.let { "${it.surahName} · ${it.ayahNumber}" } ?: "Daily Ayah")
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                daily?.let { putExtra(MainActivity.EXTRA_OPEN_AYAH_KEY, it.record.ayahKey) }
            }
            views.setOnClickPendingIntent(
                R.id.daily_ayah_widget_root,
                PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE),
            )
            manager.updateAppWidget(id, views)
        }
    }
}
