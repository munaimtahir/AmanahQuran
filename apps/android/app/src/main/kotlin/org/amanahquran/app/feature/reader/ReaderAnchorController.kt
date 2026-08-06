package org.amanahquran.app.feature.reader

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListState
import kotlin.math.abs

/** A canonical, stable reading position: an ayah key plus where it sat in the viewport. */
data class ReaderAnchorSnapshot(
    val ayahKey: String,
    val viewportOffsetPx: Int,
)

/**
 * Finds the ayah nearest [targetYPx] among the given visible rows -- a pinch gesture's centroid,
 * or (when the caller passes the viewport centre) an A-/A+ tap. Pure and Compose-free so it is
 * directly unit-testable; [captureReaderAnchor] adapts real [LazyListState] layout info into the
 * `(ayahKey, offsetPx, sizePx)` triples this expects.
 */
internal fun nearestAyahRow(
    rows: List<Triple<String, Int, Int>>,
    targetYPx: Float,
): Pair<String, Int>? {
    return rows.minByOrNull { (_, offset, size) ->
        abs((offset + size / 2f) - targetYPx)
    }?.let { (ayahKey, offset, _) -> ayahKey to offset }
}

/**
 * The reader-block index of the given canonical ayah key, or null if it isn't currently loaded.
 * Recognizes both Ayah Mode's one-row-per-ayah [ReaderStructuralItem.Ayah] blocks and Continuous
 * Mode's [ReaderStructuralItem.ContinuousBlock] (a block covering many ayahs) -- callers pass
 * whichever block list is actually displayed, so this needs to match either shape.
 */
internal fun blockIndexForAyah(readerBlocks: List<ReaderStructuralItem>, ayahKey: String): Int? {
    val index = readerBlocks.indexOfFirst { item ->
        when (item) {
            is ReaderStructuralItem.Ayah -> item.ayah.ayahKey == ayahKey
            is ReaderStructuralItem.ContinuousBlock -> item.block.ayahRanges.any { it.ayahKey == ayahKey }
            else -> false
        }
    }
    return index.takeIf { it >= 0 }
}

/**
 * Captures the current reading anchor: the ayah nearest [centroidYPx] (a pinch gesture's
 * centroid, in px from the top of the viewport), or -- when no centroid is supplied, e.g. an
 * A-/A+ tap or a script/theme switch -- the ayah nearest the viewport centre. Headers (context
 * bar, page-bookmark row, Surah/Juz headers, Bismillah) have no canonical ayah key to restore
 * against, so a centroid landing on one falls through to the nearest actual ayah/block row
 * instead. In Continuous Mode a [ReaderStructuralItem.ContinuousBlock] covers many ayahs at once
 * (a whole page), so its *first* ayah stands in as that row's representative ayah -- anchor
 * precision there is page-level, not sub-block, matching how coarse a pinch centroid already is
 * relative to a full page of flowing text.
 */
fun captureReaderAnchor(
    listState: LazyListState,
    readerBlocks: List<ReaderStructuralItem>,
    headerItemCount: Int,
    centroidYPx: Float? = null,
): ReaderAnchorSnapshot? {
    val layoutInfo = listState.layoutInfo
    val visible = layoutInfo.visibleItemsInfo
    if (visible.isEmpty()) return null
    val targetY = centroidYPx ?: (layoutInfo.viewportSize.height / 2f)

    val ayahRows = visible.mapNotNull { item ->
        val blockIndex = item.index - headerItemCount
        when (val block = readerBlocks.getOrNull(blockIndex)) {
            is ReaderStructuralItem.Ayah -> Triple(block.ayah.ayahKey, item.offset, item.size)
            is ReaderStructuralItem.ContinuousBlock -> block.block.ayahRanges.firstOrNull()?.let {
                Triple(it.ayahKey, item.offset, item.size)
            }
            else -> null
        }
    }
    if (ayahRows.isEmpty()) return null

    val (ayahKey, offset) = nearestAyahRow(ayahRows, targetY) ?: return null
    return ReaderAnchorSnapshot(ayahKey = ayahKey, viewportOffsetPx = offset)
}

/**
 * Restores [snapshot] after a reflow (zoom level, script, or typography change) by placing the
 * same ayah back at the same pixel offset from the top of the viewport it had before the change,
 * instead of resetting to the top of the list or the start of the Surah/Page/Juz. If the exact
 * ayah is no longer present (should not normally happen -- the underlying ayah list doesn't
 * change across a zoom/typography reflow), this is a no-op rather than falling back to index 0.
 */
suspend fun restoreReaderAnchor(
    listState: LazyListState,
    readerBlocks: List<ReaderStructuralItem>,
    headerItemCount: Int,
    snapshot: ReaderAnchorSnapshot,
) {
    val blockIndex = blockIndexForAyah(readerBlocks, snapshot.ayahKey) ?: return
    val targetIndex = blockIndex + headerItemCount
    listState.scrollToItem(targetIndex, scrollOffset = 0)
    if (snapshot.viewportOffsetPx > 0) {
        listState.scrollBy(-snapshot.viewportOffsetPx.toFloat())
    }
}
