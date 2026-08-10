package org.amanahquran.app.feature.reader

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import org.amanahquran.app.R

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.rounded.Close
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.ReportProblem
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material.icons.rounded.ViewAgenda
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.amanahquran.app.core.model.AutoScrollPace
import org.amanahquran.app.core.model.AutoScrollState
import org.amanahquran.app.core.model.PageReferenceType
import org.amanahquran.app.core.model.ReaderAnchor
import org.amanahquran.app.core.model.ReaderContentMode
import org.amanahquran.app.core.model.ReaderOpenMode
import org.amanahquran.app.core.model.ReaderZoomLevel
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.theme.AmanahGoldMuted
import org.amanahquran.app.core.theme.AmanahShapes
import org.amanahquran.app.core.theme.AmanahSpacing
import org.amanahquran.app.core.theme.LocalElderMode
import org.amanahquran.app.core.theme.LocalQuranTypographyTokens
import org.amanahquran.app.core.theme.LocalReaderPalette
import org.amanahquran.app.core.theme.QuranFonts
import org.amanahquran.app.core.theme.resolveQuranTypographyTokens
import org.amanahquran.app.core.ui.AmanahDivider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahReaderScreen(
    surahNumber: Int,
    initialAyahKey: String? = null,
    onNavigateBack: () -> Unit,
    viewModel: ReaderViewModel = viewModel(
        key = "reader-surah-$surahNumber",
        factory = ReaderViewModel.factory(LocalContext.current, surahNumber, initialAyahKey),
    ),
) {
    val state by viewModel.uiState.collectAsState()
    ReaderScreen(
        uiState = state,
        onNavigateBack = onNavigateBack,
        onSelectAyah = viewModel::selectAyah,
        onSelectAdjacentAyah = viewModel::selectAdjacentAyah,
        onClearSelectedAyah = viewModel::clearSelectedAyah,
        onToggleBookmark = viewModel::toggleBookmark,
        onTogglePageBookmark = viewModel::toggleCurrentPageBookmark,
        onOpenModeChanged = { newMode -> viewModel.loadOpenMode(newMode) },
        onSetArabicFontSize = viewModel::setArabicFontSize,
        onIncreaseZoom = viewModel::increaseZoom,
        onDecreaseZoom = viewModel::decreaseZoom,
        onSelectZoomLevel = viewModel::selectZoomLevel,
        onResetZoom = viewModel::resetZoom,
        onSetAutoScrollPace = viewModel::setAutoScrollPace,
        onFirstZoomHintShown = { viewModel.setFirstZoomHintShown(true) },
        onUpdateReadingPosition = viewModel::updateReadingPosition,
        onSetContentMode = viewModel::setContentMode,
        onSetLinkedZoomEnabled = viewModel::setLinkedZoomEnabled,
        onIncreaseTranslationZoom = viewModel::increaseTranslationZoom,
        onDecreaseTranslationZoom = viewModel::decreaseTranslationZoom,
        onResetTranslationZoom = viewModel::resetTranslationZoom,
        onSelectTranslationZoom = viewModel::setTranslationZoomLevel,
        translationEnabled = state.translationEnabled,
        translationFontSizeSp = state.translationFontSizeSp,
        translations = state.translations,
        arabicLineSpacingMultiplier = state.arabicLineSpacingMultiplier,
        readerHorizontalPaddingDp = state.readerHorizontalPaddingDp,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranReaderScreen(
    openMode: ReaderOpenMode,
    initialAyahKey: String? = null,
    onNavigateBack: () -> Unit,
    viewModel: ReaderViewModel = viewModel(
        key = "reader-quran-${openMode.hashCode()}",
        factory = ReaderViewModel.factory(LocalContext.current, openMode, initialAyahKey),
    ),
) {
    val state by viewModel.uiState.collectAsState()
    ReaderScreen(
        uiState = state,
        onNavigateBack = onNavigateBack,
        onSelectAyah = viewModel::selectAyah,
        onSelectAdjacentAyah = viewModel::selectAdjacentAyah,
        onClearSelectedAyah = viewModel::clearSelectedAyah,
        onToggleBookmark = viewModel::toggleBookmark,
        onTogglePageBookmark = viewModel::toggleCurrentPageBookmark,
        onOpenModeChanged = { newMode -> viewModel.loadOpenMode(newMode) },
        onSetArabicFontSize = viewModel::setArabicFontSize,
        onIncreaseZoom = viewModel::increaseZoom,
        onDecreaseZoom = viewModel::decreaseZoom,
        onSelectZoomLevel = viewModel::selectZoomLevel,
        onResetZoom = viewModel::resetZoom,
        onSetAutoScrollPace = viewModel::setAutoScrollPace,
        onFirstZoomHintShown = { viewModel.setFirstZoomHintShown(true) },
        onUpdateReadingPosition = viewModel::updateReadingPosition,
        onSetContentMode = viewModel::setContentMode,
        onSetLinkedZoomEnabled = viewModel::setLinkedZoomEnabled,
        onIncreaseTranslationZoom = viewModel::increaseTranslationZoom,
        onDecreaseTranslationZoom = viewModel::decreaseTranslationZoom,
        onResetTranslationZoom = viewModel::resetTranslationZoom,
        onSelectTranslationZoom = viewModel::setTranslationZoomLevel,
        translationEnabled = state.translationEnabled,
        translationFontSizeSp = state.translationFontSizeSp,
        translations = state.translations,
        arabicLineSpacingMultiplier = state.arabicLineSpacingMultiplier,
        readerHorizontalPaddingDp = state.readerHorizontalPaddingDp,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranReaderScreen(
    anchor: ReaderAnchor,
    onNavigateBack: () -> Unit,
    viewModel: ReaderViewModel = viewModel(
        key = "reader-anchor-${anchor.hashCode()}",
        factory = ReaderViewModel.factory(LocalContext.current, anchor),
    ),
) {
    val state by viewModel.uiState.collectAsState()
    ReaderScreen(
        uiState = state,
        onNavigateBack = onNavigateBack,
        onSelectAyah = viewModel::selectAyah,
        onSelectAdjacentAyah = viewModel::selectAdjacentAyah,
        onClearSelectedAyah = viewModel::clearSelectedAyah,
        onToggleBookmark = viewModel::toggleBookmark,
        onTogglePageBookmark = viewModel::toggleCurrentPageBookmark,
        onOpenModeChanged = { newMode -> viewModel.loadOpenMode(newMode) },
        onSetArabicFontSize = viewModel::setArabicFontSize,
        onIncreaseZoom = viewModel::increaseZoom,
        onDecreaseZoom = viewModel::decreaseZoom,
        onSelectZoomLevel = viewModel::selectZoomLevel,
        onResetZoom = viewModel::resetZoom,
        onSetAutoScrollPace = viewModel::setAutoScrollPace,
        onFirstZoomHintShown = { viewModel.setFirstZoomHintShown(true) },
        onUpdateReadingPosition = viewModel::updateReadingPosition,
        onSetContentMode = viewModel::setContentMode,
        onSetLinkedZoomEnabled = viewModel::setLinkedZoomEnabled,
        onIncreaseTranslationZoom = viewModel::increaseTranslationZoom,
        onDecreaseTranslationZoom = viewModel::decreaseTranslationZoom,
        onResetTranslationZoom = viewModel::resetTranslationZoom,
        onSelectTranslationZoom = viewModel::setTranslationZoomLevel,
        translationEnabled = state.translationEnabled,
        translationFontSizeSp = state.translationFontSizeSp,
        translations = state.translations,
        arabicLineSpacingMultiplier = state.arabicLineSpacingMultiplier,
        readerHorizontalPaddingDp = state.readerHorizontalPaddingDp,
    )
}

/** The scroll-mode LazyColumn always prepends a ReaderContextBar, and in Page mode also a
 * PageBookmarkRow, before the actual readerBlocks items -- every raw block index (anchor
 * restoration, auto-scroll's "what ayah is centred now" lookup) must be shifted by this count. */
private fun headerItemCountFor(uiState: ReaderUiState): Int {
    return 1 + if (uiState.openMode is ReaderOpenMode.Page) 1 else 0
}

/** The canonical ayah a display-list row stands in for: itself in Ayah Mode, or its first ayah
 * in Continuous Mode (a [ReaderStructuralItem.ContinuousBlock] covers a whole page at once). */
private fun ReaderStructuralItem.representativeAyahKey(): String? = when (this) {
    is ReaderStructuralItem.Ayah -> ayah.ayahKey
    is ReaderStructuralItem.ContinuousBlock -> block.ayahRanges.firstOrNull()?.ayahKey
    else -> null
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun ReaderScreen(
    uiState: ReaderUiState,
    onNavigateBack: () -> Unit,
    onSelectAyah: (String) -> Unit,
    onSelectAdjacentAyah: (Int) -> Unit,
    onClearSelectedAyah: () -> Unit,
    onToggleBookmark: (String) -> Unit,
    onTogglePageBookmark: (() -> Unit)? = null,
    onOpenModeChanged: (ReaderOpenMode) -> Unit,
    onSetArabicFontSize: (Float) -> Unit,
    onIncreaseZoom: () -> Unit = {},
    onDecreaseZoom: () -> Unit = {},
    onSelectZoomLevel: (ReaderZoomLevel) -> Unit = {},
    onResetZoom: () -> Unit = {},
    onSetAutoScrollPace: (AutoScrollPace) -> Unit = {},
    onFirstZoomHintShown: () -> Unit = {},
    onUpdateReadingPosition: (String) -> Unit = {},
    onSetContentMode: (ReaderContentMode) -> Unit = {},
    onSetLinkedZoomEnabled: (Boolean) -> Unit = {},
    onIncreaseTranslationZoom: () -> Unit = {},
    onDecreaseTranslationZoom: () -> Unit = {},
    onResetTranslationZoom: () -> Unit = {},
    onSelectTranslationZoom: (ReaderZoomLevel) -> Unit = {},
    translationEnabled: Boolean,
    translationFontSizeSp: Float,
    translations: Map<String, String>,
    arabicLineSpacingMultiplier: Float,
    readerHorizontalPaddingDp: Float,
) {
    val elder = LocalElderMode.current
    val readerPalette = LocalReaderPalette.current
    val readerBg = readerPalette.background
    // READER-UX-02: Page Mode's fit-to-screen pager only actually renders when the loaded
    // content is page-scoped. Surah/Juz opens no longer auto-redirect into a page (see
    // ReaderViewModel.loadOpenMode), so bookModeEnabled alone is no longer sufficient here --
    // without this, a Surah/Juz open with Page Mode still globally enabled would try to render
    // the HorizontalPager against a Surah/Juz-shaped (now potentially book-length) ayah list.
    val isPageModeActive = uiState.bookModeEnabled && uiState.openMode is ReaderOpenMode.Page
    val firstContentLogged = remember(uiState.readerOpenStartedAtMs, uiState.openMode, uiState.selectedScript) {
        mutableStateOf(false)
    }

    // READER-UX-02: Continuous Mode is purely a rendering choice over the same uiState.ayahs/
    // readerBlocks the ViewModel already loads -- collapseIntoContinuousBlocks() only regroups
    // the already-built (and already-tested) header/Ayah item list, never re-queries the DB.
    // Page Mode (uiState.bookModeEnabled) is unaffected by this and always uses the flat blocks,
    // matching how its own pre-existing pinch-to-view-closer zoom was left untouched by READER-UX-01.
    val displayBlocks = remember(uiState.readerBlocks, uiState.contentMode) {
        if (uiState.contentMode == ReaderContentMode.CONTINUOUS) {
            collapseIntoContinuousBlocks(uiState.readerBlocks)
        } else {
            uiState.readerBlocks
        }
    }

    // Unified Adaptive Reader Experience: auto-scroll is a scroll-mode-only playback control,
    // not a persisted setting -- like a media player's play/pause state, it resets each time the
    // reader opens (only its *pace* is a remembered preference, via uiState.autoScrollPace).
    val listState = rememberLazyListState()
    val autoScroll = rememberAutoScrollController(
        listState = listState,
        pace = uiState.autoScrollPace,
        onSettled = {
            val centered = listState.layoutInfo.visibleItemsInfo.firstOrNull()?.let { first ->
                val blockIndex = first.index - headerItemCountFor(uiState)
                displayBlocks.getOrNull(blockIndex)?.representativeAyahKey()
            }
            centered?.let(onUpdateReadingPosition)
        },
    )

    org.amanahquran.app.core.ui.KeepScreenOnEffect(enabled = uiState.keepScreenAwakeEnabled)

    // Reading-streak time tracking reuses the same "centered item" signal the reader already
    // computes for auto-scroll's last-read bookkeeping above, rather than adding new
    // scroll-visibility instrumentation.
    ReadingActivitySession(
        currentAyahKey = {
            listState.layoutInfo.visibleItemsInfo.firstOrNull()?.let { first ->
                val blockIndex = first.index - headerItemCountFor(uiState)
                displayBlocks.getOrNull(blockIndex)?.representativeAyahKey()
            } ?: uiState.selectedAyahKey
        },
        currentPageNumber = {
            val key = listState.layoutInfo.visibleItemsInfo.firstOrNull()?.let { first ->
                val blockIndex = first.index - headerItemCountFor(uiState)
                displayBlocks.getOrNull(blockIndex)?.representativeAyahKey()
            } ?: uiState.selectedAyahKey
            uiState.ayahs.firstOrNull { it.ayahKey == key }?.pageNumber
        },
    )

    var controlsVisible by remember { mutableStateOf(true) }
    LaunchedEffect(autoScroll.state) {
        when (autoScroll.state) {
            AutoScrollState.RUNNING -> {
                delay(1500)
                if (autoScroll.state == AutoScrollState.RUNNING) controlsVisible = false
            }
            AutoScrollState.PAUSED, AutoScrollState.INACTIVE, AutoScrollState.COMPLETED -> controlsVisible = true
            AutoScrollState.STARTING -> Unit
        }
    }
    LaunchedEffect(isPageModeActive) {
        if (isPageModeActive) autoScroll.stop()
    }
    // Any deliberate interaction with reader chrome pauses hands-free scrolling immediately and
    // requires an explicit Resume afterward -- see section 12.6/29/30 of the sprint spec.
    LaunchedEffect(uiState.selectedScript, uiState.elderModeEnabled) {
        autoScroll.pause()
    }
    // READER-UX-02 section 21: switching Ayah <-> Continuous, or toggling translation, is also a
    // direct interaction with reader layout and must pause hands-free scrolling immediately.
    LaunchedEffect(uiState.contentMode, translationEnabled) {
        autoScroll.pause()
    }

    // Reader Anchor Preservation (Feature A, section 11): captured once when a pinch begins or
    // an A-/A+ press fires, held for the rest of that interaction, and consumed by the effect
    // below every time uiState.zoomLevel actually changes -- so a continued pinch that steps
    // through several levels keeps re-anchoring against the *same* originally-captured ayah
    // rather than drifting. `zoomPreviewScale` gives immediate (<50ms) visual feedback via a
    // graphicsLayer transform while the gesture is in progress, snapping back to 1x once the
    // real text has reflowed to match -- this is deliberately a *transient* preview, not a
    // lingering scale left in place instead of a real reflow (see section 42, prohibition #2).
    var pendingZoomAnchor by remember { mutableStateOf<ReaderAnchorSnapshot?>(null) }
    var zoomPreviewScale by remember { mutableFloatStateOf(1f) }
    val hapticFeedback = LocalHapticFeedback.current

    fun beginZoomAnchorCapture(centroidYPx: Float? = null) {
        autoScroll.pause()
        if (!uiState.firstZoomHintShown) onFirstZoomHintShown()
        pendingZoomAnchor = captureReaderAnchor(listState, displayBlocks, headerItemCountFor(uiState), centroidYPx)
    }

    LaunchedEffect(uiState.zoomLevel, uiState.translationZoomLevel) {
        pendingZoomAnchor?.let { snapshot ->
            restoreReaderAnchor(listState, displayBlocks, headerItemCountFor(uiState), snapshot)
        }
        zoomPreviewScale = 1f
    }

    LaunchedEffect(uiState.readerOpenStartedAtMs, uiState.isLoading, uiState.readerBlocks) {
        if (!uiState.isLoading && uiState.readerBlocks.isNotEmpty() && !firstContentLogged.value) {
            ReaderPerfLogger.log(
                "first_reader_content_composed",
                uiState.readerOpenStartedAtMs,
                "mode=${uiState.openMode} blocks=${uiState.readerBlocks.size}",
            )
            firstContentLogged.value = true
        }
    }

    Scaffold(
        containerColor = readerBg,
        topBar = {
            if (!isPageModeActive) {
                TopAppBar(
                    title = {
                        val headerPageNumber = remember(uiState.selectedAyahKey, uiState.ayahs) {
                            val key = uiState.selectedAyahKey ?: uiState.ayahs.firstOrNull()?.ayahKey
                            uiState.ayahs.firstOrNull { it.ayahKey == key }?.pageNumber
                        }
                        val headerParts = remember(uiState.readerHeaderFormat, uiState.surahName, uiState.modeTitle, uiState.juzNumber, headerPageNumber) {
                            org.amanahquran.app.core.util.ReaderHeaderTextBuilder.build(
                                format = uiState.readerHeaderFormat,
                                surahName = uiState.surahName.ifBlank { uiState.modeTitle },
                                juzNumber = uiState.juzNumber,
                                pageNumber = headerPageNumber,
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            headerParts.primary?.let { primary ->
                                Text(
                                    text = primary,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false),
                                )
                            }
                            headerParts.page?.let { page ->
                                if (headerParts.primary != null) {
                                    Text(
                                        text = " · ",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onBackground,
                                    )
                                }
                                Text(
                                    text = page,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    maxLines = 1,
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Go back")
                        }
                    },
                    actions = {
                        val chromeAlpha by animateFloatAsState(
                            targetValue = if (controlsVisible) 1f else 0.4f,
                            label = "reader-chrome-alpha",
                        )
                        Row(modifier = Modifier.alpha(chromeAlpha)) {
                            run {
                                val nextMode = if (uiState.contentMode == ReaderContentMode.AYAH) ReaderContentMode.CONTINUOUS else ReaderContentMode.AYAH
                                IconButton(onClick = { autoScroll.pause(); onSetContentMode(nextMode) }) {
                                    Icon(
                                        imageVector = if (uiState.contentMode == ReaderContentMode.AYAH) Icons.AutoMirrored.Rounded.MenuBook else Icons.Rounded.ViewAgenda,
                                        contentDescription = if (uiState.contentMode == ReaderContentMode.AYAH) "Switch to Continuous Reading" else "Switch to Ayah-by-Ayah",
                                    )
                                }
                            }
                            ReaderTypographyPanel(
                                zoomLevel = uiState.zoomLevel,
                                firstZoomHintShown = uiState.firstZoomHintShown,
                                onIncrease = {
                                    beginZoomAnchorCapture(); onIncreaseZoom()
                                    if (uiState.linkedZoomEnabled) onIncreaseTranslationZoom()
                                },
                                onDecrease = {
                                    beginZoomAnchorCapture(); onDecreaseZoom()
                                    if (uiState.linkedZoomEnabled) onDecreaseTranslationZoom()
                                },
                                onSelectLevel = { level ->
                                    beginZoomAnchorCapture(); onSelectZoomLevel(level)
                                    if (uiState.linkedZoomEnabled) onSelectTranslationZoom(level)
                                },
                                onReset = {
                                    beginZoomAnchorCapture(); onResetZoom()
                                    if (uiState.linkedZoomEnabled) onResetTranslationZoom()
                                },
                                onHintShown = onFirstZoomHintShown,
                                translationEnabled = translationEnabled && uiState.contentMode == ReaderContentMode.CONTINUOUS,
                                translationZoomLevel = uiState.translationZoomLevel,
                                linkedZoomEnabled = uiState.linkedZoomEnabled,
                                onIncreaseTranslation = { beginZoomAnchorCapture(); onIncreaseTranslationZoom() },
                                onDecreaseTranslation = { beginZoomAnchorCapture(); onDecreaseTranslationZoom() },
                                onResetTranslation = { beginZoomAnchorCapture(); onResetTranslationZoom() },
                                onSetLinkedZoomEnabled = onSetLinkedZoomEnabled,
                            )
                            ReaderAutoScrollTrigger(
                                state = autoScroll.state,
                                onClick = {
                                    when (autoScroll.state) {
                                        AutoScrollState.INACTIVE, AutoScrollState.COMPLETED -> autoScroll.start()
                                        AutoScrollState.PAUSED -> autoScroll.resume()
                                        AutoScrollState.RUNNING, AutoScrollState.STARTING -> autoScroll.pause()
                                    }
                                },
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = readerBg,
                        titleContentColor = readerPalette.chromeContent,
                        navigationIconContentColor = readerPalette.chromeContent,
                        actionIconContentColor = readerPalette.chromeContent,
                    ),
                )
            }
        },
    ) { padding ->
        when {
            uiState.isLoading && uiState.ayahs.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            uiState.errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(AmanahSpacing.xxl),
                    verticalArrangement = Arrangement.spacedBy(AmanahSpacing.md),
                ) {
                    Text("Unable to load reader content", style = MaterialTheme.typography.titleMedium)
                    Text(uiState.errorMessage.orEmpty(), style = MaterialTheme.typography.bodyMedium)
                }
            }

            isPageModeActive -> {
                // isPageModeActive already guarantees uiState.openMode is ReaderOpenMode.Page.
                val currentPageMode = uiState.openMode as ReaderOpenMode.Page
                val pageReferenceType = currentPageMode.pageReferenceType
                val pageCount = if (pageReferenceType == PageReferenceType.UTHMANI) 604 else 559
                val initialIndex = currentPageMode.pageNumber - 1

                val pagerState = rememberPagerState(
                    initialPage = initialIndex,
                    pageCount = { pageCount },
                )

                LaunchedEffect(initialIndex) {
                    if (pagerState.currentPage != initialIndex) {
                        pagerState.scrollToPage(initialIndex)
                    }
                }

                // LaunchedEffect(pagerState) never restarts once launched (pagerState is a
                // stable identity for the whole book-mode session), so a plain `uiState` read
                // inside `collect` would capture a stale snapshot from the first launch and
                // never see later updates -- causing spurious "already loaded" matches that
                // silently skip onOpenModeChanged, leaving a page stuck on its spinner forever.
                // rememberUpdatedState keeps this effect reading the current value instead.
                val latestUiState by rememberUpdatedState(uiState)
                LaunchedEffect(pagerState) {
                    snapshotFlow { pagerState.currentPage }.collect { index ->
                        val currentOpenMode = latestUiState.openMode
                        val targetPageRefType = (currentOpenMode as? ReaderOpenMode.Page)?.pageReferenceType
                            ?: if (latestUiState.selectedScript == ScriptType.UTHMANI) PageReferenceType.UTHMANI else PageReferenceType.INDOPAK
                        val newMode = ReaderOpenMode.Page(index + 1, targetPageRefType)
                        if (newMode != currentOpenMode) {
                            onOpenModeChanged(newMode)
                        }
                    }
                }

                var pagerZoomed by remember { mutableStateOf(false) }

                Box(modifier = Modifier.fillMaxSize()) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        HorizontalPager(
                            state = pagerState,
                            userScrollEnabled = !pagerZoomed,
                            // Mushaf pages read right-to-left: swiping right reveals the next
                            // page, swiping left goes back, matching a physical Urdu/Arabic
                            // Mushaf. Page chrome (header, borders, padding) stays LTR above.
                            reverseLayout = true,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding),
                        ) { page ->
                            val isCurrentLoaded = (uiState.openMode as? ReaderOpenMode.Page)?.let { it.pageNumber - 1 == page } == true

                            if (isCurrentLoaded && !uiState.isLoading) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp)
                                        .border(
                                            border = BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary),
                                            shape = MaterialTheme.shapes.medium,
                                        )
                                        .padding(3.dp)
                                        .border(
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                            shape = MaterialTheme.shapes.medium,
                                        )
                                        .padding(AmanahSpacing.lg),
                                ) {
                                    ReaderPageHeader(
                                        uiState = uiState,
                                        pageNumber = (uiState.openMode as? ReaderOpenMode.Page)?.pageNumber ?: 1,
                                        onNavigateBack = onNavigateBack,
                                        onTogglePageBookmark = onTogglePageBookmark,
                                    )
                                    AmanahDivider(modifier = Modifier.padding(bottom = AmanahSpacing.sm))

                                    PageFitZoomableContent(
                                        pageKey = page,
                                        groupedBlocks = groupReaderBlocks(uiState.readerBlocks),
                                        arabicFontSizeSp = uiState.arabicFontSizeSp,
                                        translationEnabled = translationEnabled,
                                        translations = translations,
                                        translationFontSizeSp = translationFontSizeSp,
                                        onSelectAyah = onSelectAyah,
                                        onZoomedChanged = { pagerZoomed = it },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f),
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }

                    // Floating card for selected ayah in book mode
                    if (uiState.selectedAyahKey != null) {
                        val selectedAyah = uiState.ayahs.firstOrNull { it.ayahKey == uiState.selectedAyahKey }
                        if (selectedAyah != null) {
                            Card(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(horizontal = 16.dp, vertical = 24.dp)
                                    .fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(horizontal = AmanahSpacing.md, vertical = AmanahSpacing.sm)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${selectedAyah.surahNameSimple} ${selectedAyah.surahNumber}:${selectedAyah.ayahNumber}",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                        Text(
                                            text = "Juz ${selectedAyah.juzNumber} · Page ${selectedAyah.pageNumber}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                        )
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.xs),
                                    ) {
                                        IconButton(onClick = { onToggleBookmark(selectedAyah.ayahKey) }) {
                                            Icon(
                                                imageVector = if (selectedAyah.isBookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                                                contentDescription = "Toggle bookmark",
                                                tint = if (selectedAyah.isBookmarked) AmanahGoldMuted else MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                        IconButton(onClick = onClearSelectedAyah) {
                                            Icon(
                                                imageVector = Icons.Rounded.Close,
                                                contentDescription = "Close description",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            else -> {
                val readerPadding = if (elder) AmanahSpacing.readerPaddingElder else readerHorizontalPaddingDp.dp
                var jumpDialogVisible by remember { mutableStateOf(false) }
                val typographyTokens = remember(uiState.selectedScript, uiState.zoomLevel, uiState.elderModeEnabled) {
                    resolveQuranTypographyTokens(uiState.selectedScript, uiState.zoomLevel, uiState.elderModeEnabled)
                }
                // Section 12.6: selecting an ayah, stepping to an adjacent one, or opening its
                // bookmark action is direct interaction with reader content and must pause
                // hands-free scrolling immediately, same as a manual drag or a pinch.
                val selectAyahAndPause: (String) -> Unit = { key -> autoScroll.pause(); onSelectAyah(key) }
                val selectAdjacentAyahAndPause: (Int) -> Unit = { direction -> autoScroll.pause(); onSelectAdjacentAyah(direction) }
                val toggleBookmarkAndPause: (String) -> Unit = { key -> autoScroll.pause(); onToggleBookmark(key) }

                LaunchedEffect(
                    uiState.anchorScrollRequestId,
                    displayBlocks,
                ) {
                    val targetIndex = uiState.selectedAyahKey?.let { blockIndexForAyah(displayBlocks, it) }
                        ?: return@LaunchedEffect
                    listState.scrollToItem(targetIndex + headerItemCountFor(uiState))
                    ReaderPerfLogger.log(
                        "anchor_visible",
                        uiState.readerOpenStartedAtMs,
                        "ayah=${uiState.selectedAyahKey} index=$targetIndex",
                    )
                }
                CompositionLocalProvider(LocalQuranTypographyTokens provides typographyTokens) {
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .graphicsLayer(scaleX = zoomPreviewScale, scaleY = zoomPreviewScale)
                            .then(
                                if (uiState.pinchToResizeEnabled) {
                                    // READER-UX-02 section 16.3: in split translation, an unlinked
                                    // pinch zooms only the pane it started in (by centroid X, since
                                    // the row is Translation-left/Arabic-right under this LTR Row);
                                    // linked (the default) always drives both together, same as a
                                    // single-pane Continuous/Ayah Mode pinch always did.
                                    Modifier.pointerInput(
                                        uiState.selectedScript,
                                        uiState.elderModeEnabled,
                                        uiState.contentMode,
                                        translationEnabled,
                                        uiState.linkedZoomEnabled,
                                    ) {
                                        var lastSteppedScale = 1f
                                        val isSplitTranslation = translationEnabled && uiState.contentMode == ReaderContentMode.CONTINUOUS
                                        var zoomingTranslationPane = false
                                        detectAdaptiveZoomGestures(
                                            onGestureStart = { centroidXPx, centroidYPx ->
                                                lastSteppedScale = 1f
                                                zoomingTranslationPane = isSplitTranslation &&
                                                    !uiState.linkedZoomEnabled &&
                                                    centroidXPx < size.width / 2f
                                                beginZoomAnchorCapture(centroidYPx)
                                            },
                                            onZoomChange = { cumulativeScale ->
                                                zoomPreviewScale = (cumulativeScale / lastSteppedScale).coerceIn(0.85f, 1.2f)
                                                val delta = cumulativeScale / lastSteppedScale
                                                val atMax = if (zoomingTranslationPane) uiState.translationZoomLevel.isMaximum else uiState.zoomLevel.isMaximum
                                                val atMin = if (zoomingTranslationPane) uiState.translationZoomLevel.isMinimum else uiState.zoomLevel.isMinimum
                                                when {
                                                    delta >= 1.06f && !atMax -> {
                                                        lastSteppedScale = cumulativeScale
                                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                                        if (zoomingTranslationPane) {
                                                            onIncreaseTranslationZoom()
                                                        } else {
                                                            onIncreaseZoom()
                                                            if (isSplitTranslation && uiState.linkedZoomEnabled) onIncreaseTranslationZoom()
                                                        }
                                                    }
                                                    delta <= 0.94f && !atMin -> {
                                                        lastSteppedScale = cumulativeScale
                                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                                        if (zoomingTranslationPane) {
                                                            onDecreaseTranslationZoom()
                                                        } else {
                                                            onDecreaseZoom()
                                                            if (isSplitTranslation && uiState.linkedZoomEnabled) onDecreaseTranslationZoom()
                                                        }
                                                    }
                                                }
                                            },
                                            onGestureEnd = {
                                                zoomPreviewScale = 1f
                                                pendingZoomAnchor = null
                                            },
                                        )
                                    }
                                } else {
                                    Modifier
                                },
                            ),
                        contentPadding = PaddingValues(readerPadding),
                        verticalArrangement = Arrangement.spacedBy(typographyTokens.ayahSpacingDp.dp),
                    ) {
                        item {
                            ReaderContextBar(
                                selectedAyahKey = uiState.selectedAyahKey,
                                currentIndex = uiState.ayahs.indexOfFirst { it.ayahKey == uiState.selectedAyahKey }.takeIf { it >= 0 } ?: 0,
                                totalAyahs = uiState.ayahs.size,
                                onPrevious = { selectAdjacentAyahAndPause(-1) },
                                onNext = { selectAdjacentAyahAndPause(1) },
                                onJump = { jumpDialogVisible = true },
                            )
                        }
                        if (uiState.openMode is ReaderOpenMode.Page && onTogglePageBookmark != null) {
                            item {
                                PageBookmarkRow(
                                    isBookmarked = uiState.isPageBookmarked,
                                    onToggle = { autoScroll.pause(); onTogglePageBookmark() },
                                )
                                AmanahDivider(modifier = Modifier.padding(top = AmanahSpacing.sm))
                            }
                        }

                        items(displayBlocks, key = { it.key() }) { item ->
                            when (item) {
                                is ReaderStructuralItem.Ayah -> ReaderAyahRow(
                                    ayah = item.ayah,
                                    arabicFontSizeSp = uiState.arabicFontSizeSp,
                                    arabicLineSpacingMultiplier = arabicLineSpacingMultiplier,
                                    translationText = if (translationEnabled) translations[item.ayah.ayahKey] else null,
                                    translationFontSizeSp = translationFontSizeSp,
                                    onSelectAyah = selectAyahAndPause,
                                )

                                is ReaderStructuralItem.ContinuousBlock -> if (translationEnabled) {
                                    ParallelTranslationBlockRow(
                                        block = item.block,
                                        scriptType = uiState.selectedScript,
                                        arabicFontSizeSp = uiState.arabicFontSizeSp,
                                        arabicLineSpacingMultiplier = arabicLineSpacingMultiplier,
                                        translations = translations,
                                        translationFontSizeSp = translationFontSizeSp,
                                        selectedAyahKey = uiState.selectedAyahKey,
                                        onSelectAyah = selectAyahAndPause,
                                    )
                                } else {
                                    ContinuousQuranBlockText(
                                        block = item.block,
                                        scriptType = uiState.selectedScript,
                                        arabicFontSizeSp = uiState.arabicFontSizeSp,
                                        arabicLineSpacingMultiplier = arabicLineSpacingMultiplier,
                                        selectedAyahKey = uiState.selectedAyahKey,
                                        onSelectAyah = selectAyahAndPause,
                                    )
                                }

                                else -> ReaderStructuralContent(item)
                            }
                        }
                    }

                    if (!uiState.firstZoomHintShown) {
                        ReaderFirstZoomHint(
                            modifier = Modifier.align(Alignment.TopCenter),
                            onDismiss = onFirstZoomHintShown,
                        )
                    }

                    if (autoScroll.state != AutoScrollState.INACTIVE) {
                        ReaderAutoScrollPanel(
                            controller = autoScroll,
                            pace = uiState.autoScrollPace,
                            onPaceChange = onSetAutoScrollPace,
                            onClose = {
                                autoScroll.stop()
                                val centered = listState.layoutInfo.visibleItemsInfo.firstOrNull()?.let { first ->
                                    val blockIndex = first.index - headerItemCountFor(uiState)
                                    displayBlocks.getOrNull(blockIndex)?.representativeAyahKey()
                                }
                                centered?.let(onUpdateReadingPosition)
                            },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = if (uiState.selectedAyahKey != null) 96.dp else 24.dp),
                        )
                    }

                    // Bookmark/share/report actions live here for the selected ayah instead
                    // of as an icon row on every single ayah, which crowded the list and made
                    // it uncomfortable to read. Tap an ayah to select it and reveal this card.
                    val selectedAyah = uiState.ayahs.firstOrNull { it.ayahKey == uiState.selectedAyahKey }
                    if (selectedAyah != null) {
                        ReaderSelectedAyahActionCard(
                            ayah = selectedAyah,
                            translationText = if (translationEnabled) translations[selectedAyah.ayahKey] else null,
                            onToggleBookmark = toggleBookmarkAndPause,
                            onDismiss = onClearSelectedAyah,
                            modifier = Modifier.align(Alignment.BottomCenter),
                        )
                    }
                }
                }
                if (jumpDialogVisible) {
                    AyahJumpDialog(
                        surahNumber = uiState.surahNumber,
                        maxAyah = uiState.ayahs.maxOfOrNull { it.ayahNumber } ?: 1,
                        onDismiss = { jumpDialogVisible = false },
                        onSelect = { ayahNumber ->
                            uiState.ayahs.firstOrNull { it.ayahNumber == ayahNumber }?.let { ayah -> selectAyahAndPause(ayah.ayahKey) }
                            jumpDialogVisible = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun ReaderContextBar(
    selectedAyahKey: String?,
    currentIndex: Int,
    totalAyahs: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onJump: () -> Unit,
) {
    val current = (currentIndex + 1).coerceAtMost(totalAyahs.coerceAtLeast(1))
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = AmanahSpacing.md, vertical = AmanahSpacing.sm),
            verticalArrangement = Arrangement.spacedBy(AmanahSpacing.xs),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TextButton(onClick = onPrevious, enabled = currentIndex > 0) { Text("Previous") }
                TextButton(onClick = onJump, enabled = totalAyahs > 0) { Text("Ayah $current / $totalAyahs") }
                TextButton(onClick = onNext, enabled = currentIndex < totalAyahs - 1) { Text("Next") }
            }
            Text(
                text = if (selectedAyahKey != null) "Reading position saved at $selectedAyahKey" else "Select an ayah to save your reading position",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun AyahJumpDialog(
    surahNumber: Int,
    maxAyah: Int,
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit,
) {
    var value by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Jump to ayah") },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = { value = it.filter(Char::isDigit).take(3) },
                label = { Text("Ayah number (1–$maxAyah)") },
                singleLine = true,
            )
        },
        confirmButton = {
            TextButton(
                onClick = { value.toIntOrNull()?.takeIf { it in 1..maxAyah }?.let(onSelect) },
                enabled = value.toIntOrNull()?.let { it in 1..maxAyah } == true,
            ) { Text("Open") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
private fun ReaderPageHeader(
    uiState: ReaderUiState,
    pageNumber: Int,
    onNavigateBack: () -> Unit,
    onTogglePageBookmark: (() -> Unit)?,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.size(AmanahSpacing.minTouchTarget),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Go back",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (uiState.openMode is ReaderOpenMode.Page && onTogglePageBookmark != null) {
                IconButton(
                    onClick = onTogglePageBookmark,
                    modifier = Modifier.size(AmanahSpacing.minTouchTarget),
                ) {
                    Icon(
                        imageVector = if (uiState.isPageBookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                        contentDescription = if (uiState.isPageBookmarked) {
                            "Remove page bookmark"
                        } else {
                            "Bookmark this page"
                        },
                        modifier = Modifier.size(18.dp),
                        tint = if (uiState.isPageBookmarked) AmanahGoldMuted else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Text(
                text = uiState.surahNameArabic,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Text(
            text = pageNumber.toString(),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary,
        )

        Text(
            text = "الجزء ${convertToArabicNumber(uiState.juzNumber)}",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun PageBookmarkRow(
    isBookmarked: Boolean,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
    ) {
        IconButton(onClick = onToggle) {
            Icon(
                imageVector = if (isBookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                contentDescription = if (isBookmarked) "Page bookmarked" else "Bookmark this page",
                tint = if (isBookmarked) AmanahGoldMuted else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = if (isBookmarked) "Page bookmarked" else "Bookmark page",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun ReaderZoomLevel.displayName(): String = when (this) {
    ReaderZoomLevel.COMPACT -> "Compact"
    ReaderZoomLevel.SMALL -> "Small"
    ReaderZoomLevel.STANDARD -> "Standard"
    ReaderZoomLevel.LARGE -> "Large"
    ReaderZoomLevel.ELDER -> "Elder"
    ReaderZoomLevel.EXTRA_LARGE -> "Extra Large"
    ReaderZoomLevel.MAXIMUM -> "Maximum"
}

/** Section 8.4: A-, a seven-level selector, A+, current level label, and Reset -- reachable
 * from the reader toolbar without permanently occupying screen space. */
@Composable
private fun ReaderTypographyPanel(
    zoomLevel: ReaderZoomLevel,
    firstZoomHintShown: Boolean,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onSelectLevel: (ReaderZoomLevel) -> Unit,
    onReset: () -> Unit,
    onHintShown: () -> Unit,
    translationEnabled: Boolean = false,
    translationZoomLevel: ReaderZoomLevel = ReaderZoomLevel.default,
    linkedZoomEnabled: Boolean = true,
    onIncreaseTranslation: () -> Unit = {},
    onDecreaseTranslation: () -> Unit = {},
    onResetTranslation: () -> Unit = {},
    onSetLinkedZoomEnabled: (Boolean) -> Unit = {},
) {
    val elder = LocalElderMode.current
    val palette = LocalReaderPalette.current
    var menuExpanded by remember { mutableStateOf(false) }
    val touchTarget = if (elder) AmanahSpacing.minTouchTargetElder else AmanahSpacing.minTouchTarget
    Box {
        IconButton(onClick = { menuExpanded = true }) {
            Icon(Icons.Rounded.TextFields, contentDescription = "Quran text size, currently ${zoomLevel.displayName()}")
        }
        DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false; onHintShown() }) {
            Column(
                modifier = Modifier
                    .padding(horizontal = AmanahSpacing.md, vertical = AmanahSpacing.sm)
                    .width(260.dp),
                verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm),
            ) {
                Text(
                    text = "Quran text size",
                    style = MaterialTheme.typography.labelLarge,
                    color = palette.text,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.sm),
                ) {
                    IconButton(
                        onClick = onDecrease,
                        enabled = !zoomLevel.isMinimum,
                        modifier = Modifier.size(touchTarget),
                    ) {
                        Icon(Icons.Rounded.Remove, contentDescription = "Decrease Quran text size")
                    }
                    Text(
                        text = zoomLevel.displayName(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.text,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(
                        onClick = onIncrease,
                        enabled = !zoomLevel.isMaximum,
                        modifier = Modifier.size(touchTarget),
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = "Increase Quran text size")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    ReaderZoomLevel.entries.forEach { level ->
                        val selected = level == zoomLevel
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(touchTarget)
                                .clickable(onClickLabel = level.displayName()) { onSelectLevel(level) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .background(
                                        color = if (selected) palette.activeControl else palette.inactiveControl.copy(alpha = 0.35f),
                                        shape = AmanahShapes.badge,
                                    ),
                            )
                        }
                    }
                }
                TextButton(onClick = onReset) { Text("Reset to Standard") }

                // READER-UX-02 section 16.2: shown only when split translation is actually on
                // screen (progressive disclosure) -- Ayah Mode's below-ayah translation already
                // has its own font-size slider elsewhere in Settings.
                if (translationEnabled) {
                    AmanahDivider()
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "Link Arabic and translation sizes",
                            style = MaterialTheme.typography.labelLarge,
                            color = palette.text,
                            modifier = Modifier.weight(1f),
                        )
                        Checkbox(checked = linkedZoomEnabled, onCheckedChange = onSetLinkedZoomEnabled)
                    }
                    if (!linkedZoomEnabled) {
                        Text(
                            text = "Translation text size",
                            style = MaterialTheme.typography.labelLarge,
                            color = palette.text,
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.sm),
                        ) {
                            IconButton(
                                onClick = onDecreaseTranslation,
                                enabled = !translationZoomLevel.isMinimum,
                                modifier = Modifier.size(touchTarget),
                            ) {
                                Icon(Icons.Rounded.Remove, contentDescription = "Decrease translation text size")
                            }
                            Text(
                                text = translationZoomLevel.displayName(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = palette.text,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f),
                            )
                            IconButton(
                                onClick = onIncreaseTranslation,
                                enabled = !translationZoomLevel.isMaximum,
                                modifier = Modifier.size(touchTarget),
                            ) {
                                Icon(Icons.Rounded.Add, contentDescription = "Increase translation text size")
                            }
                        }
                        TextButton(onClick = onResetTranslation) { Text("Reset translation size") }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReaderAutoScrollTrigger(
    state: AutoScrollState,
    onClick: () -> Unit,
) {
    val palette = LocalReaderPalette.current
    val running = state == AutoScrollState.RUNNING || state == AutoScrollState.STARTING
    IconButton(onClick = onClick) {
        Icon(
            imageVector = if (running) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
            contentDescription = when (state) {
                AutoScrollState.RUNNING, AutoScrollState.STARTING -> "Pause auto-scroll"
                AutoScrollState.PAUSED -> "Resume auto-scroll"
                else -> "Start auto-scroll"
            },
            tint = if (state != AutoScrollState.INACTIVE) palette.activeControl else LocalContentColor.current,
        )
    }
}

/** Section 12.3/24: a compact floating surface (not a heavy full-width toolbar, and never a
 * central overlay over the Quran text itself) with direct Slower/Faster controls, plus the
 * pace label paired with its indicative minutes-per-Juz estimate. */
@Composable
private fun ReaderAutoScrollPanel(
    controller: AutoScrollController,
    pace: AutoScrollPace,
    onPaceChange: (AutoScrollPace) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val elder = LocalElderMode.current
    val palette = LocalReaderPalette.current
    val touchTarget = if (elder) AmanahSpacing.minTouchTargetElder else AmanahSpacing.minTouchTarget
    val running = controller.state == AutoScrollState.RUNNING || controller.state == AutoScrollState.STARTING
    val completed = controller.state == AutoScrollState.COMPLETED
    val elapsedSeconds = controller.elapsedMs / 1000
    Card(
        modifier = modifier.widthIn(max = 380.dp),
        shape = AmanahShapes.card,
        colors = CardDefaults.cardColors(containerColor = palette.controlSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = AmanahSpacing.lg, vertical = AmanahSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AmanahSpacing.xs),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.sm),
            ) {
                // Reaching the end (section 12.9) is a terminal state: there is nothing left in
                // the current range to scroll to, so Play/Slower/Faster are disabled here rather
                // than doing nothing (Resume) or immediately re-completing (Start). Close, or
                // navigating to new content and starting again from the top-bar trigger, are the
                // only meaningful next actions.
                IconButton(
                    onClick = { if (running) controller.pause() else controller.resume() },
                    enabled = !completed,
                    modifier = Modifier.size(touchTarget),
                ) {
                    Icon(
                        imageVector = if (running) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = if (running) "Pause" else "Resume",
                        tint = if (completed) palette.inactiveControl else palette.activeControl,
                    )
                }
                IconButton(
                    onClick = { onPaceChange(pace.slower()) },
                    enabled = !pace.isSlowest && !completed,
                    modifier = Modifier.size(touchTarget),
                ) {
                    Icon(Icons.Rounded.Remove, contentDescription = "Slower")
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = if (completed) {
                            "Reached the end"
                        } else {
                            "${pace.label} · ~${pace.approximateMinutesPerJuz} min/Juz"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.text,
                    )
                    Text(
                        text = "%02d:%02d elapsed".format(elapsedSeconds / 60, elapsedSeconds % 60),
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.secondaryText,
                    )
                }
                IconButton(
                    onClick = { onPaceChange(pace.faster()) },
                    enabled = !pace.isFastest && !completed,
                    modifier = Modifier.size(touchTarget),
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "Faster")
                }
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(touchTarget),
                ) {
                    Icon(Icons.Rounded.Close, contentDescription = "Close auto-scroll", tint = palette.secondaryText)
                }
            }
        }
    }
}

/** Section 8.5: shown once, non-blocking, never covering the central Quran text, and dismissed
 * either by a timeout or by opening the typography panel (whichever comes first). */
@Composable
private fun ReaderFirstZoomHint(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
) {
    val palette = LocalReaderPalette.current
    LaunchedEffect(Unit) {
        delay(4500)
        onDismiss()
    }
    Card(
        modifier = modifier
            .padding(top = AmanahSpacing.sm, start = AmanahSpacing.xxl, end = AmanahSpacing.xxl)
            .clickable { onDismiss() },
        shape = AmanahShapes.card,
        colors = CardDefaults.cardColors(containerColor = palette.controlSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Text(
            text = "Pinch with two fingers to adjust Quran text.",
            modifier = Modifier.padding(horizontal = AmanahSpacing.md, vertical = AmanahSpacing.sm),
            style = MaterialTheme.typography.labelMedium,
            color = palette.text,
        )
    }
}

@Composable
private fun ReaderSelectedAyahActionCard(
    ayah: ReaderAyahUiModel,
    translationText: String?,
    onToggleBookmark: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Card(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = AmanahSpacing.md, vertical = AmanahSpacing.sm)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "${ayah.surahNameSimple} ${ayah.surahNumber}:${ayah.ayahNumber}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onToggleBookmark(ayah.ayahKey) }) {
                    Icon(
                        imageVector = if (ayah.isBookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                        contentDescription = if (ayah.isBookmarked) "Remove bookmark" else "Bookmark ayah",
                        tint = if (ayah.isBookmarked) AmanahGoldMuted else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                var actionsMenuExpanded by remember { mutableStateOf(false) }
                Box {
                    IconButton(onClick = { actionsMenuExpanded = true }) {
                        Icon(Icons.Rounded.MoreVert, contentDescription = "More ayah actions", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    DropdownMenu(expanded = actionsMenuExpanded, onDismissRequest = { actionsMenuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Share as text") },
                            leadingIcon = { Icon(Icons.Rounded.Share, contentDescription = null) },
                            onClick = {
                                actionsMenuExpanded = false
                                shareAyah(context, ayah, translationText)
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Share as image") },
                            leadingIcon = { Icon(Icons.Rounded.Share, contentDescription = null) },
                            onClick = {
                                actionsMenuExpanded = false
                                shareAyahImage(context, ayah, translationText)
                            },
                        )
                        AmanahDivider()
                        ReportCategory.entries.forEach { category ->
                            DropdownMenuItem(
                                text = { Text("Report: ${category.label}") },
                                leadingIcon = { Icon(Icons.Rounded.ReportProblem, contentDescription = null) },
                                onClick = {
                                    actionsMenuExpanded = false
                                    reportAyah(context, ayah, category)
                                },
                            )
                        }
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun ReaderAyahRow(
    ayah: ReaderAyahUiModel,
    arabicFontSizeSp: Float,
    arabicLineSpacingMultiplier: Float = 1.88f,
    translationText: String? = null,
    translationFontSizeSp: Float = 18f,
    onSelectAyah: (String) -> Unit,
) {
    val elder = LocalElderMode.current
    val palette = LocalReaderPalette.current
    // Ayah-marker size (section 10.5) tracks the active zoom level instead of a fixed
    // elder/non-elder binary, via the same tokens the surrounding structural headers use.
    val markerScale = LocalQuranTypographyTokens.current.ayahMarkerScale
    val markerSize = ((if (elder) 36f else 30f) * markerScale).dp
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(markerSize)
                    .background(
                        color = if (ayah.isSelected) palette.currentAyahHighlight else MaterialTheme.colorScheme.surfaceVariant,
                        shape = AmanahShapes.chip,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "${ayah.surahNumber}:${ayah.ayahNumber}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    color = if (ayah.isSelected) palette.activeControl else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (ayah.isSelected) {
                Text(
                    text = "Current",
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.activeControl,
                )
            }
        }

        // Section 23: a subtle sage-tinted background rather than a strong colour change --
        // must not alter the Quran text itself, must not obscure diacritics, and must remain
        // legible in dark mode (ReaderPalette.currentAyahHighlight is tuned per-theme for that).
        Text(
            text = ayah.displayText,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClickLabel = "Select ayah ${ayah.ayahKey}") {
                    onSelectAyah(ayah.ayahKey)
                }
                .then(
                    if (!ayah.isSelected) Modifier else Modifier.background(
                        color = palette.currentAyahHighlight,
                        shape = AmanahShapes.ayahCard,
                    )
                )
                .padding(horizontal = AmanahSpacing.sm, vertical = if (ayah.isSelected) AmanahSpacing.xs else 0.dp),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = arabicFontSizeSp.sp,
                lineHeight = (arabicFontSizeSp * arabicLineSpacingMultiplier).sp,
                letterSpacing = 0.sp,
                fontFamily = QuranFonts.getFontFamily(ayah.scriptType),
            ),
            textAlign = TextAlign.Center,
            color = palette.text,
        )

        if (!translationText.isNullOrBlank()) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Text(
                    text = translationText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AmanahSpacing.sm),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = translationFontSizeSp.sp,
                        lineHeight = (translationFontSizeSp * 1.65f).sp,
                    ),
                    textAlign = TextAlign.Right,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        AmanahDivider()
    }
}

private fun shareAyah(context: Context, ayah: ReaderAyahUiModel, translation: String?) {
    val body = buildString {
        append("${ayah.surahNameSimple} ${ayah.surahNumber}:${ayah.ayahNumber}\n\n")
        append(ayah.displayText)
        if (!translation.isNullOrBlank()) append("\n\nUrdu translation (Muhammad Junagarhi, QuranEnc):\n$translation")
        append("\n\nAmanah Quran")
    }
    context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, body)
    }, "Share ayah"))
}

enum class ReportCategory(val label: String) {
    RENDERING("Quran rendering issue"),
    TRANSLATION("Urdu translation issue"),
    APP_BUG("App bug"),
    OTHER("Other"),
}

private fun reportAyah(context: Context, ayah: ReaderAyahUiModel, category: ReportCategory) {
    val body = "Please describe the issue below.\n\nCategory: ${category.label}\nAyah: ${ayah.ayahKey}\nScript: ${ayah.scriptType.name}\nApp: Amanah Quran\n\nDescription:\n"
    context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SENDTO).apply {
        data = android.net.Uri.parse("mailto:")
        putExtra(Intent.EXTRA_SUBJECT, "Amanah Quran issue — ${ayah.ayahKey}")
        putExtra(Intent.EXTRA_TEXT, body)
    }, "Report an issue"))
}

private fun shareAyahImage(context: Context, ayah: ReaderAyahUiModel, translation: String?) {
    val bitmap = try {
        renderAyahBitmap(context, ayah, translation)
    } catch (e: Exception) {
        return
    }
    val file = try {
        val dir = File(context.cacheDir, "shared_images").apply { mkdirs() }
        val target = File(dir, "ayah_${ayah.surahNumber}_${ayah.ayahNumber}.png")
        FileOutputStream(target).use { out -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) }
        target
    } catch (e: IOException) {
        return
    }
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share ayah image"))
}

private fun renderAyahBitmap(context: Context, ayah: ReaderAyahUiModel, translation: String?): Bitmap {
    val width = 1080
    val padding = 64
    val contentWidth = width - padding * 2
    val spacing = 32

    val fontRes = if (ayah.scriptType == ScriptType.INDOPAK) R.font.digital_khatt_indopak else R.font.digital_khatt_v2
    val arabicTypeface = ResourcesCompat.getFont(context, fontRes) ?: Typeface.DEFAULT

    val referencePaint = TextPaint().apply {
        isAntiAlias = true
        color = AndroidColor.parseColor("#6B6B6B")
        textSize = 30f
    }
    val arabicPaint = TextPaint().apply {
        isAntiAlias = true
        color = AndroidColor.parseColor("#1B1B1B")
        textSize = 50f
        typeface = arabicTypeface
    }
    val translationPaint = TextPaint().apply {
        isAntiAlias = true
        color = AndroidColor.parseColor("#3A3A3A")
        textSize = 32f
    }
    val footerPaint = TextPaint().apply {
        isAntiAlias = true
        color = AndroidColor.parseColor("#9A8352")
        textSize = 26f
        textAlign = android.graphics.Paint.Align.CENTER
    }

    fun layoutFor(text: String, paint: TextPaint, alignment: Layout.Alignment): StaticLayout {
        return StaticLayout.Builder.obtain(text, 0, text.length, paint, contentWidth)
            .setAlignment(alignment)
            .setLineSpacing(0f, 1.3f)
            .build()
    }

    val referenceLayout = layoutFor(
        "${ayah.surahNameSimple} ${ayah.surahNumber}:${ayah.ayahNumber}",
        referencePaint,
        Layout.Alignment.ALIGN_CENTER,
    )
    val arabicLayout = layoutFor(ayah.displayText, arabicPaint, Layout.Alignment.ALIGN_CENTER)
    val translationLayout = translation?.takeIf { it.isNotBlank() }
        ?.let { layoutFor(it, translationPaint, Layout.Alignment.ALIGN_CENTER) }

    var height = padding * 2 + referenceLayout.height + spacing + arabicLayout.height
    if (translationLayout != null) height += spacing + translationLayout.height
    height += spacing + 40

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.drawColor(AndroidColor.parseColor("#FBF8F1"))

    var y = padding.toFloat()
    canvas.save()
    canvas.translate(padding.toFloat(), y)
    referenceLayout.draw(canvas)
    canvas.restore()
    y += referenceLayout.height + spacing

    canvas.save()
    canvas.translate(padding.toFloat(), y)
    arabicLayout.draw(canvas)
    canvas.restore()
    y += arabicLayout.height + spacing

    if (translationLayout != null) {
        canvas.save()
        canvas.translate(padding.toFloat(), y)
        translationLayout.draw(canvas)
        canvas.restore()
        y += translationLayout.height + spacing
    }

    canvas.drawText("Amanah Quran — verified offline text", width / 2f, y + 28f, footerPaint)

    return bitmap
}

private fun convertToArabicNumber(number: Int): String {
    val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    return number.toString().map { char ->
        if (char.isDigit()) arabicDigits[char.code - '0'.code] else char
    }.joinToString("")
}

private sealed interface BookModeBlock {
    data class Header(val item: ReaderStructuralItem) : BookModeBlock
    data class Paragraph(val ayahs: List<ReaderAyahUiModel>) : BookModeBlock
}

private fun groupReaderBlocks(blocks: List<ReaderStructuralItem>): List<BookModeBlock> {
    val result = mutableListOf<BookModeBlock>()
    val currentParagraph = mutableListOf<ReaderAyahUiModel>()
    
    for (block in blocks) {
        when (block) {
            is ReaderStructuralItem.Ayah -> {
                currentParagraph.add(block.ayah)
            }
            else -> {
                if (currentParagraph.isNotEmpty()) {
                    result.add(BookModeBlock.Paragraph(currentParagraph.toList()))
                    currentParagraph.clear()
                }
                result.add(BookModeBlock.Header(block))
            }
        }
    }
    if (currentParagraph.isNotEmpty()) {
        result.add(BookModeBlock.Paragraph(currentParagraph.toList()))
    }
    return result
}

/**
 * Renders a full mushaf page's blocks shrunk to fit the available height with no
 * scrolling, then layers a pinch-zoom/pan transform on top so a reader can inspect
 * the page closely without that gesture changing the persisted Arabic font size
 * setting. The fit search only ever shrinks the ayah paragraphs (via [scale]);
 * [ReaderStructuralContent] headers keep their own fixed sizing.
 */
@Composable
private fun PageFitZoomableContent(
    pageKey: Any,
    groupedBlocks: List<BookModeBlock>,
    arabicFontSizeSp: Float,
    translationEnabled: Boolean,
    translations: Map<String, String>,
    translationFontSizeSp: Float,
    onSelectAyah: (String) -> Unit,
    onZoomedChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var zoom by remember(pageKey) { mutableFloatStateOf(1f) }
    var pan by remember(pageKey) { mutableStateOf(Offset.Zero) }

    LaunchedEffect(zoom) { onZoomedChanged(zoom > 1.02f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .pointerInput(pageKey) {
                detectPinchZoomGestures { panDelta, zoomDelta ->
                    val newZoom = (zoom * zoomDelta).coerceIn(1f, 3f)
                    pan = if (newZoom <= 1f) Offset.Zero else pan + panDelta
                    zoom = newZoom
                }
            },
    ) {
        SubcomposeLayout(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = zoom,
                    scaleY = zoom,
                    translationX = pan.x,
                    translationY = pan.y,
                ),
        ) { constraints ->
            val maxW = constraints.maxWidth
            val maxH = constraints.maxHeight

            fun measureAt(scale: Float): Placeable {
                val measurables = subcompose(scale) {
                    Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm * scale)) {
                        groupedBlocks.forEach { groupedItem ->
                            when (groupedItem) {
                                is BookModeBlock.Header -> ReaderStructuralContent(groupedItem.item)
                                is BookModeBlock.Paragraph -> ReaderBookModeParagraph(
                                    ayahs = groupedItem.ayahs,
                                    arabicFontSizeSp = arabicFontSizeSp * scale,
                                    translations = if (translationEnabled) translations else emptyMap(),
                                    translationFontSizeSp = translationFontSizeSp * scale,
                                    onSelectAyah = onSelectAyah,
                                )
                            }
                        }
                    }
                }
                return measurables.first().measure(Constraints.fixedWidth(maxW))
            }

            val minScale = 0.4f
            var lo = minScale
            var hi = 1f
            var best = measureAt(minScale)
            if (best.height <= maxH) {
                repeat(7) {
                    val mid = (lo + hi) / 2f
                    val candidate = measureAt(mid)
                    if (candidate.height <= maxH) {
                        best = candidate
                        lo = mid
                    } else {
                        hi = mid
                    }
                }
            }

            layout(maxW, maxH) {
                best.place(0, ((maxH - best.height) / 2).coerceAtLeast(0))
            }
        }
    }
}

@Composable
private fun ReaderBookModeParagraph(
    ayahs: List<ReaderAyahUiModel>,
    arabicFontSizeSp: Float,
    onSelectAyah: (String) -> Unit,
    modifier: Modifier = Modifier,
    translations: Map<String, String> = emptyMap(),
    translationFontSizeSp: Float = 18f,
) {
    val scriptType = ayahs.firstOrNull()?.scriptType ?: ScriptType.INDOPAK
    val fontFamily = remember(scriptType) { QuranFonts.getFontFamily(scriptType) }
    
    val primaryContainerColor = MaterialTheme.colorScheme.primaryContainer
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    val annotatedString = remember(ayahs) {
        buildAnnotatedString {
            ayahs.forEach { ayah ->
                val start = length
                pushStringAnnotation(tag = "ayahKey", annotation = ayah.ayahKey)
                append(ayah.displayText)
                append(" \u06DD${convertToArabicNumber(ayah.ayahNumber)} ")
                pop()
                val end = length

                if (ayah.isSelected) {
                    addStyle(
                        style = SpanStyle(
                            background = primaryContainerColor.copy(alpha = 0.4f),
                            color = primaryColor
                        ),
                        start = start,
                        end = end
                    )
                } else {
                    addStyle(
                        style = SpanStyle(
                            color = onSurfaceColor
                        ),
                        start = start,
                        end = end
                    )
                }
            }
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
    ClickableText(
        text = annotatedString,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = AmanahSpacing.xs),
        style = MaterialTheme.typography.bodyLarge.copy(
            fontSize = arabicFontSizeSp.sp,
            lineHeight = (arabicFontSizeSp * 1.88f).sp,
            fontFamily = fontFamily,
            textAlign = TextAlign.Center,
            letterSpacing = 0.sp,
        ),
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "ayahKey", start = offset, end = offset)
                    .firstOrNull()?.let { annotation ->
                        onSelectAyah(annotation.item)
                    }
            }
    )
    if (translations.isNotEmpty()) {
        Text(
            text = ayahs.mapNotNull { translations[it.ayahKey] }.joinToString("\n"),
            modifier = modifier
                .fillMaxWidth()
                .padding(top = AmanahSpacing.sm),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = translationFontSizeSp.sp,
                lineHeight = (translationFontSizeSp * 1.65f).sp,
            ),
            textAlign = TextAlign.Right,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
}
