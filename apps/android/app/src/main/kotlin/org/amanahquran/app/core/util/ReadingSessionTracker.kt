package org.amanahquran.app.core.util

/**
 * Tracks active reader time only. Callers must explicitly [resume]/[pause] around the periods
 * the user is actually looking at the reader (lifecycle onResume/onPause, screen leaving the
 * reader route, etc.) — this class does no lifecycle observation itself, so background time,
 * paused app time, and time on non-reader screens are never counted unless a caller mistakenly
 * calls [resume] during them.
 */
class ReadingSessionTracker(private val nowMillis: () -> Long = System::currentTimeMillis) {
    private var activeSinceMillis: Long? = null
    private var accumulatedSeconds: Long = 0

    val isTracking: Boolean get() = activeSinceMillis != null

    fun resume() {
        if (activeSinceMillis == null) activeSinceMillis = nowMillis()
    }

    /** Stops the active interval and folds its elapsed time into the accumulated total. */
    fun pause() {
        val since = activeSinceMillis ?: return
        accumulatedSeconds += ((nowMillis() - since) / 1000L).coerceAtLeast(0)
        activeSinceMillis = null
    }

    /**
     * Returns accumulated seconds since the last call (including the current active interval,
     * without ending it) and resets the accumulator — intended for periodic checkpoint persists.
     */
    fun drainAccumulatedSeconds(): Long {
        val since = activeSinceMillis
        if (since != null) {
            val now = nowMillis()
            accumulatedSeconds += ((now - since) / 1000L).coerceAtLeast(0)
            activeSinceMillis = now
        }
        val total = accumulatedSeconds
        accumulatedSeconds = 0
        return total
    }
}
