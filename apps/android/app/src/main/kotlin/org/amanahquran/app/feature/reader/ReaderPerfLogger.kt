package org.amanahquran.app.feature.reader

import android.os.SystemClock
import android.util.Log
import org.amanahquran.app.BuildConfig

internal object ReaderPerfLogger {
    private const val TAG = "AMANAH_PERF_READER"

    fun log(stage: String, startedAtMs: Long? = null, detail: String? = null) {
        if (!BuildConfig.DEBUG) return
        val now = SystemClock.elapsedRealtime()
        val elapsed = startedAtMs?.let { " +${now - it}ms" }.orEmpty()
        val suffix = detail?.takeIf { it.isNotBlank() }?.let { " $it" }.orEmpty()
        Log.i(TAG, "$stage$elapsed$suffix")
    }
}
