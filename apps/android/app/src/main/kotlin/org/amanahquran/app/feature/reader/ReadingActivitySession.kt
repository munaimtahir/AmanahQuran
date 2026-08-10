package org.amanahquran.app.feature.reader

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.amanahquran.app.core.repository.ReadingActivityRepository
import org.amanahquran.app.core.repository.readingActivityRepository
import org.amanahquran.app.core.util.ReadingSessionTracker

private const val CHECKPOINT_INTERVAL_MS = 20_000L

/**
 * Tracks active reader time for the reading-streak feature and periodically checkpoints it (plus
 * whatever ayah/page the reader is currently centered on) to on-device storage. Only counts time
 * while this composable is actually in composition and the host lifecycle is resumed -- app
 * backgrounding, screen-off, and navigating away all stop the clock via the same lifecycle
 * signal, so no separate "is the screen really visible" plumbing is needed.
 */
@Composable
fun ReadingActivitySession(
    currentAyahKey: () -> String?,
    currentPageNumber: () -> Int?,
    repository: ReadingActivityRepository = readingActivityRepository(LocalContext.current),
) {
    val tracker = remember { ReadingSessionTracker() }
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    suspend fun checkpoint() {
        val seconds = tracker.drainAccumulatedSeconds()
        val ayahKeys = setOfNotNull(currentAyahKey())
        val pages = setOfNotNull(currentPageNumber())
        if (seconds <= 0L && ayahKeys.isEmpty()) return
        repository.recordSession(
            date = LocalDate.now(ZoneId.systemDefault()),
            additionalDurationSeconds = seconds,
            ayahKeysRead = ayahKeys,
            pagesRead = pages,
        )
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> tracker.resume()
                Lifecycle.Event.ON_PAUSE -> {
                    tracker.pause()
                    scope.launch { checkpoint() }
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            tracker.pause()
            scope.launch { checkpoint() }
        }
    }

    LaunchedEffect(Unit) {
        while (isActive) {
            delay(CHECKPOINT_INTERVAL_MS)
            if (tracker.isTracking) checkpoint()
        }
    }
}
