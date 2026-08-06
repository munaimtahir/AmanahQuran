package org.amanahquran.app.feature.reader

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.positionChanged

/**
 * Only responds to two-or-more-finger transform gestures, leaving single-finger drags completely
 * unconsumed so they fall through to whatever scrollable/pager content sits underneath (the
 * Page Mode [HorizontalPager] for swipe-to-turn-page, or the scroll-mode `LazyColumn` for normal
 * one-finger scrolling). The stock `detectTransformGestures` reports -- and consumes -- a
 * single-finger drag as a pan, which would silently break both of those.
 */
internal suspend fun PointerInputScope.detectPinchZoomGestures(
    onGesture: (pan: Offset, zoom: Float) -> Unit,
) {
    awaitEachGesture {
        do {
            val event = awaitPointerEvent()
            if (event.changes.size > 1) {
                val zoomChange = event.calculateZoom()
                val panChange = event.calculatePan()
                if (zoomChange != 1f || panChange != Offset.Zero) {
                    onGesture(panChange, zoomChange)
                }
                event.changes.forEach { change ->
                    if (change.positionChanged()) change.consume()
                }
            }
        } while (event.changes.any { it.pressed })
    }
}

/**
 * Two-finger-only pinch detection for adaptive Quran text zoom (Feature A), reporting a
 * cumulative scale factor from gesture start rather than raw per-frame deltas so the caller can
 * apply its own threshold/hysteresis (see `ReaderTypographyState` in `SurahReaderScreen.kt`)
 * instead of stepping a zoom level on every tiny frame-to-frame jitter. [onGestureStart] fires
 * once, with the gesture's centroid Y (in the pointer input area's local coordinates -- i.e.
 * viewport-relative when attached to the same Box that hosts the reader's `LazyColumn`), the
 * first time two fingers actually produce a zoom delta -- not on mere touch-down -- so a
 * one-finger scroll that happens to pick up an incidental second finger never fires it. The
 * centroid X (added for READER-UX-02) lets a split-translation caller tell which pane -- Arabic
 * or translation -- the gesture actually started in, for unlinked independent-pane zoom.
 */
internal suspend fun PointerInputScope.detectAdaptiveZoomGestures(
    onGestureStart: (centroidXPx: Float, centroidYPx: Float) -> Unit,
    onZoomChange: (cumulativeScale: Float) -> Unit,
    onGestureEnd: () -> Unit,
) {
    awaitEachGesture {
        var cumulativeScale = 1f
        var started = false
        do {
            val event = awaitPointerEvent()
            if (event.changes.size > 1) {
                val zoomChange = event.calculateZoom()
                if (zoomChange != 1f) {
                    if (!started) {
                        started = true
                        val centroid = event.calculateCentroid(useCurrent = true)
                        onGestureStart(centroid.x, centroid.y)
                    }
                    cumulativeScale *= zoomChange
                    onZoomChange(cumulativeScale)
                }
                event.changes.forEach { change ->
                    if (change.positionChanged()) change.consume()
                }
            }
        } while (event.changes.any { it.pressed })
        if (started) onGestureEnd()
    }
}
