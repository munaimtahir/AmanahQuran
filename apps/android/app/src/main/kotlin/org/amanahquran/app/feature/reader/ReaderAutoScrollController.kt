package org.amanahquran.app.feature.reader

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.amanahquran.app.core.model.AutoScrollPace
import org.amanahquran.app.core.model.AutoScrollState

/**
 * Hands-free auto-scroll engine (Feature B). Moves [listState] upward at a calm, constant
 * velocity calibrated from the currently visible rows' own average height -- not a raw
 * ayah-count or line-count heuristic -- so the perceived pace stays comfortable regardless of
 * zoom level, script, or Elder Mode. A short start ramp avoids a jarring first-frame jump; after
 * that the motion is linear with no per-ayah snapping, no bounce, and no page-boundary pause.
 *
 * [AutoScrollState.PAUSED] is only ever left by an explicit [resume] call -- nothing in this
 * class silently resumes on its own.
 */
@Stable
class AutoScrollController internal constructor(
    private val listState: LazyListState,
    private val scope: CoroutineScope,
    private val paceProvider: () -> AutoScrollPace,
    private val onSettled: () -> Unit,
) {
    var state: AutoScrollState by mutableStateOf(AutoScrollState.INACTIVE)
        private set
    var elapsedMs: Long by mutableLongStateOf(0L)
        private set

    private var job: Job? = null

    fun start() {
        if (state == AutoScrollState.RUNNING || state == AutoScrollState.STARTING) return
        elapsedMs = 0L
        runLoop()
    }

    fun resume() {
        if (state != AutoScrollState.PAUSED) return
        runLoop()
    }

    /** Pauses immediately. Direct interaction (drag, pinch, opening a panel, script switch, a
     * lifecycle interruption) all funnel through this -- see the callers in [SurahReaderScreen]. */
    fun pause() {
        if (state != AutoScrollState.RUNNING && state != AutoScrollState.STARTING) return
        job?.cancel()
        job = null
        state = AutoScrollState.PAUSED
        onSettled()
    }

    /** Stops completely and returns to normal reading chrome without navigating away or
     * resetting the pace/zoom preferences. */
    fun stop() {
        job?.cancel()
        job = null
        state = AutoScrollState.INACTIVE
        elapsedMs = 0L
        onSettled()
    }

    private fun runLoop() {
        job?.cancel()
        state = AutoScrollState.STARTING
        job = scope.launch {
            val frameDelayMs = 16L
            val startRampMs = 400L
            var rampElapsedMs = 0L
            while (true) {
                val visible = listState.layoutInfo.visibleItemsInfo
                if (visible.isEmpty()) {
                    delay(frameDelayMs)
                    continue
                }
                val avgRowHeightPx = visible.sumOf { it.size }.toFloat() / visible.size
                val pace = paceProvider()
                // 6236 ayahs over 30 Juz is an approximation of average Juz length in rows;
                // combined with the *actual* on-screen row height this yields a px/ms rate that
                // tracks the real, current typography (zoom level, script, Elder Mode) rather
                // than a fixed items-per-second constant.
                val ayahsPerJuzEstimate = 6236f / 30f
                val totalDistancePx = avgRowHeightPx * ayahsPerJuzEstimate
                val totalMillis = pace.approximateMinutesPerJuz * 60_000f
                val targetPxPerFrame = totalDistancePx / totalMillis * frameDelayMs

                rampElapsedMs += frameDelayMs
                val rampFactor = (rampElapsedMs / startRampMs.toFloat()).coerceIn(0f, 1f)
                state = if (rampFactor < 1f) AutoScrollState.STARTING else AutoScrollState.RUNNING

                val scrolled = listState.scrollBy(targetPxPerFrame * rampFactor)
                elapsedMs += frameDelayMs

                if (!listState.canScrollForward && scrolled < 0.5f) {
                    job = null
                    state = AutoScrollState.COMPLETED
                    onSettled()
                    return@launch
                }
                delay(frameDelayMs)
            }
        }
    }
}

/**
 * Builds an [AutoScrollController] wired to [listState] and automatically pauses it on a real
 * user drag (via [listState]'s own interaction source, which only reports genuine pointer drags
 * -- never the controller's own programmatic [LazyListState.scrollBy] calls) or a lifecycle
 * interruption (background, screen lock, losing focus). The controller's coroutine is scoped to
 * this composable's lifetime, so leaving the reader route cancels it automatically in addition
 * to the explicit [AutoScrollController.stop]/[AutoScrollController.pause] calls.
 */
@Composable
fun rememberAutoScrollController(
    listState: LazyListState,
    pace: AutoScrollPace,
    onSettled: () -> Unit = {},
): AutoScrollController {
    val scope = rememberCoroutineScope()
    var latestPace by remember { mutableStateOf(pace) }
    latestPace = pace
    // The controller instance itself must stay stable across recompositions (it owns an
    // in-flight coroutine job), but `onSettled` closes over UI state that changes on every
    // recomposition -- capturing it once via a plain constructor parameter would silently keep
    // calling back into a stale snapshot forever, the same class of bug already fixed elsewhere
    // in this reader (see the Page Mode LaunchedEffect note in SurahReaderScreen.kt).
    val latestOnSettled by rememberUpdatedState(onSettled)

    val controller = remember(listState) {
        AutoScrollController(
            listState = listState,
            scope = scope,
            paceProvider = { latestPace },
            onSettled = { latestOnSettled() },
        )
    }

    val isDragged by listState.interactionSource.collectIsDraggedAsState()
    LaunchedEffect(isDragged) {
        if (isDragged) controller.pause()
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, controller) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP || event == Lifecycle.Event.ON_PAUSE) {
                controller.pause()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    return controller
}
