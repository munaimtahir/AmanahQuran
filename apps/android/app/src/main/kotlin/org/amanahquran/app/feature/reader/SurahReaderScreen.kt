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
import kotlin.math.roundToInt
import org.amanahquran.app.R

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
    onNavigateBack: () -> Unit,
    viewModel: ReaderViewModel = viewModel(
        factory = ReaderViewModel.factory(LocalContext.current, ReaderOpenMode.Surah(surahNumber))
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
        onOpenModeChanged = viewModel::loadOpenMode,
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

@Composable
fun QuranReaderScreen(
    openMode: ReaderOpenMode = ReaderOpenMode.Surah(1),
    anchor: ReaderAnchor? = null,
    onNavigateBack: () -> Unit,
    viewModel: ReaderViewModel = viewModel(
        factory = if (anchor != null) {
            ReaderViewModel.factory(LocalContext.current, anchor)
        } else {
            ReaderViewModel.factory(LocalContext.current, openMode)
        }
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
        onOpenModeChanged = viewModel::loadOpenMode,
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

@Composable
fun JuzReaderScreen(
    juzNumber: Int,
    onNavigateBack: () -> Unit,
    viewModel: ReaderViewModel = viewModel(
        factory = ReaderViewModel.factory(LocalContext.current, ReaderOpenMode.Juz(juzNumber))
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
        onOpenModeChanged = viewModel::loadOpenMode,
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

@Composable
fun PageReaderScreen(
    pageNumber: Int,
    pageReferenceType: PageReferenceType,
    onNavigateBack: () -> Unit,
    viewModel: ReaderViewModel = viewModel(
        factory = ReaderViewModel.factory(LocalContext.current, ReaderOpenMode.Page(pageNumber, pageReferenceType))
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
        onOpenModeChanged = viewModel::loadOpenMode,
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

private fun headerItemCountFor(uiState: ReaderUiState): Int {
    return 1 + if (uiState.openMode is ReaderOpenMode.Page) 1 else 0
}

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
    val firstContentLogged = remember(uiState.readerOpenStartedAtMs, uiState.openMode, uiState.selectedScript) {
        mutableStateOf(false)
    }

    val displayBlocks = remember(uiState.readerBlocks, uiState.contentMode) {
        if (uiState.contentMode == ReaderContentMode.CONTINUOUS) {
            collapseIntoContinuousBlocks(uiState.readerBlocks)
        } else {
            uiState.readerBlocks
        }
    }

    val ayahsByKey = remember(uiState.ayahs) {
        uiState.ayahs.associateBy { it.ayahKey }
    }

    val listState = rememberLazyListState()

    // Dynamic single source of truth for current reading viewport
    val activeReadingPosition by remember(uiState.ayahs, displayBlocks) {
        derivedStateOf {
            val visibleItems = listState.layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty() || uiState.ayahs.isEmpty()) {
                val first = uiState.ayahs.firstOrNull()
                return@derivedStateOf first?.let {
                    ActiveReadingPosition(
                        ayahKey = it.ayahKey,
                        surahNumber = it.surahNumber,
                        surahNameSimple = it.surahNameSimple.ifBlank { "Surah ${it.surahNumber}" },
                        surahNameArabic = it.surahNameArabic,
                        ayahNumber = it.ayahNumber,
                        juzNumber = it.juzNumber,
                        canonicalPageNumber = it.pageNumber,
                    )
                }
            }

            val headerOffset = headerItemCountFor(uiState)
            val activeItem = visibleItems.firstOrNull { it.index >= headerOffset && (it.offset + it.size) > 20 }
                ?: visibleItems.firstOrNull { it.index >= headerOffset }
                ?: visibleItems.first()

            val blockIndex = activeItem.index - headerOffset
            val block = displayBlocks.getOrNull(blockIndex)

            val targetAyahKey: String? = when (block) {
                is ReaderStructuralItem.Ayah -> block.ayah.ayahKey
                is ReaderStructuralItem.ContinuousBlock -> {
                    val ranges = block.block.ayahRanges
                    if (ranges.size > 1 && activeItem.offset < 0 && activeItem.size > 0) {
                        val progress = (-activeItem.offset.toFloat() / activeItem.size.toFloat()).coerceIn(0f, 1f)
                        val rangeIdx = (progress * (ranges.size - 1)).toInt().coerceIn(0, ranges.lastIndex)
                        ranges[rangeIdx].ayahKey
                    } else {
                        ranges.firstOrNull()?.ayahKey
                    }
                }
                is ReaderStructuralItem.SurahHeader -> {
                    uiState.ayahs.firstOrNull { it.surahNumber == block.surahNumber }?.ayahKey
                }
                is ReaderStructuralItem.JuzHeader -> {
                    uiState.ayahs.firstOrNull { it.juzNumber == block.juzNumber }?.ayahKey
                }
                is ReaderStructuralItem.Bismillah -> {
                    uiState.ayahs.firstOrNull { it.surahNumber == block.surahNumber }?.ayahKey
                }
                is ReaderStructuralItem.PageDivider -> {
                    uiState.ayahs.firstOrNull { it.pageNumber == block.pageNumber }?.ayahKey
                }
                null -> uiState.ayahs.firstOrNull()?.ayahKey
            }

            val targetAyah = (targetAyahKey?.let { ayahsByKey[it] } ?: uiState.ayahs.firstOrNull())
            targetAyah?.let {
                ActiveReadingPosition(
                    ayahKey = it.ayahKey,
                    surahNumber = it.surahNumber,
                    surahNameSimple = it.surahNameSimple.ifBlank { "Surah ${it.surahNumber}" },
                    surahNameArabic = it.surahNameArabic,
                    ayahNumber = it.ayahNumber,
                    juzNumber = it.juzNumber,
                    canonicalPageNumber = it.pageNumber,
                )
            }
        }
    }

    val autoScroll = rememberAutoScrollController(
        listState = listState,
        pace = uiState.autoScrollPace,
        onSettled = {
            activeReadingPosition?.ayahKey?.let(onUpdateReadingPosition)
        },
    )

    org.amanahquran.app.core.ui.KeepScreenOnEffect(enabled = uiState.keepScreenAwakeEnabled)

    ReadingActivitySession(
        currentAyahKey = { activeReadingPosition?.ayahKey ?: uiState.selectedAyahKey },
        currentPageNumber = { activeReadingPosition?.canonicalPageNumber ?: uiState.ayahs.firstOrNull()?.pageNumber },
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

    LaunchedEffect(uiState.selectedScript, uiState.elderModeEnabled) {
        autoScroll.pause()
    }
    LaunchedEffect(uiState.contentMode, translationEnabled) {
        autoScroll.pause()
    }

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
            TopAppBar(
                title = {
                    val activeSurahName = activeReadingPosition?.surahNameSimple ?: uiState.surahName.ifBlank { uiState.pageSurahName }
                    val activeJuzNumber = activeReadingPosition?.juzNumber ?: uiState.juzNumber
                    val activePageNumber = activeReadingPosition?.canonicalPageNumber ?: uiState.ayahs.firstOrNull()?.pageNumber

                    val headerParts = remember(
                        uiState.readerHeaderFormat,
                        activeSurahName,
                        activeJuzNumber,
                        activePageNumber,
                    ) {
                        org.amanahquran.app.core.util.ReaderHeaderTextBuilder.build(
                            format = uiState.readerHeaderFormat,
                            surahName = activeSurahName,
                            juzNumber = activeJuzNumber,
                            pageNumber = activePageNumber,
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
                                    contentDescription = if (uiState.contentMode == ReaderContentMode.AYAH) "Switch to Continuous View" else "Switch to Ayah View",
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
                            linked = uiState.linkedZoomEnabled,
                            onToggleLinked = onSetLinkedZoomEnabled,
                            hasTranslation = translationEnabled,
                            translationLevel = uiState.translationZoomLevel,
                            onIncreaseTranslation = { beginZoomAnchorCapture(); onIncreaseTranslationZoom() },
                            onDecreaseTranslation = { beginZoomAnchorCapture(); onDecreaseTranslationZoom() },
                            onSelectTranslationLevel = { level -> beginZoomAnchorCapture(); onSelectTranslationZoom(level) },
                            onResetTranslation = { beginZoomAnchorCapture(); onResetTranslationZoom() },
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
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground,
                ),
            )
        },
    ) { padding ->
        when {
            uiState.isLoading -> {
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

            else -> {
                val readerPadding = if (elder) AmanahSpacing.readerPaddingElder else readerHorizontalPaddingDp.dp
                var jumpDialogVisible by remember { mutableStateOf(false) }
                val typographyTokens = remember(uiState.selectedScript, uiState.zoomLevel, uiState.elderModeEnabled) {
                    resolveQuranTypographyTokens(uiState.selectedScript, uiState.zoomLevel, uiState.elderModeEnabled)
                }
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
                                    Modifier.readerPinchPagingGesture(
                                        onPinchZoom = { zoomDelta, centroidX, centroidY, totalWidth ->
                                            if (pendingZoomAnchor == null) {
                                                beginZoomAnchorCapture(centroidY)
                                            }
                                            zoomPreviewScale = (zoomPreviewScale * zoomDelta).coerceIn(0.75f, 1.4f)
                                            val threshold = 1.15f
                                            val invThreshold = 1f / threshold
                                            val isTranslationSide = translationEnabled && totalWidth > 0f && (centroidX / totalWidth) < 0.5f

                                            if (zoomPreviewScale >= threshold) {
                                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                                if (uiState.linkedZoomEnabled || !translationEnabled) {
                                                    onIncreaseZoom()
                                                    if (translationEnabled) onIncreaseTranslationZoom()
                                                } else if (isTranslationSide) {
                                                    onIncreaseTranslationZoom()
                                                } else {
                                                    onIncreaseZoom()
                                                }
                                                zoomPreviewScale = 1f
                                            } else if (zoomPreviewScale <= invThreshold) {
                                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                                if (uiState.linkedZoomEnabled || !translationEnabled) {
                                                    onDecreaseZoom()
                                                    if (translationEnabled) onDecreaseTranslationZoom()
                                                } else if (isTranslationSide) {
                                                    onDecreaseTranslationZoom()
                                                } else {
                                                    onDecreaseZoom()
                                                }
                                                zoomPreviewScale = 1f
                                            }
                                        },
                                        onGestureEnd = {
                                            zoomPreviewScale = 1f
                                            pendingZoomAnchor = null
                                        },
                                    )
                                } else {
                                    Modifier
                                }
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
                                activeReadingPosition?.ayahKey?.let(onUpdateReadingPosition)
                            },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = if (uiState.selectedAyahKey != null) 96.dp else 24.dp),
                        )
                    }

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
private fun PageBookmarkRow(
    isBookmarked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = AmanahSpacing.sm, horizontal = AmanahSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = if (isBookmarked) "Page bookmarked" else "Bookmark this page",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Icon(
            imageVector = if (isBookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
            contentDescription = if (isBookmarked) "Remove page bookmark" else "Bookmark page",
            tint = if (isBookmarked) AmanahGoldMuted else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ReaderTypographyPanel(
    zoomLevel: ReaderZoomLevel,
    firstZoomHintShown: Boolean,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onSelectLevel: (ReaderZoomLevel) -> Unit,
    onReset: () -> Unit,
    linked: Boolean = true,
    onToggleLinked: (Boolean) -> Unit = {},
    hasTranslation: Boolean = false,
    translationLevel: ReaderZoomLevel = zoomLevel,
    onIncreaseTranslation: () -> Unit = onIncrease,
    onDecreaseTranslation: () -> Unit = onDecrease,
    onSelectTranslationLevel: (ReaderZoomLevel) -> Unit = onSelectLevel,
    onResetTranslation: () -> Unit = onReset,
) {
    var expanded by remember { mutableStateOf(false) }
    val elder = LocalElderMode.current
    val palette = LocalReaderPalette.current
    val touchTarget = if (elder) AmanahSpacing.minTouchTargetElder else AmanahSpacing.minTouchTarget
    Box {
        IconButton(
            onClick = { expanded = true },
            modifier = Modifier.size(touchTarget),
        ) {
            Icon(Icons.Rounded.TextFields, contentDescription = "Adjust text size")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(palette.controlSurface)
                .widthIn(min = 240.dp),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = AmanahSpacing.md, vertical = AmanahSpacing.sm),
                verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm),
            ) {
                Text("Arabic text size", style = MaterialTheme.typography.labelLarge, color = palette.text)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    IconButton(
                        onClick = onDecrease,
                        enabled = !zoomLevel.isMinimum,
                        modifier = Modifier.size(touchTarget),
                    ) {
                        Icon(Icons.Rounded.Remove, contentDescription = "Decrease Arabic text size")
                    }
                    Text(
                        text = "${(zoomLevel.multiplier * 100).roundToInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = palette.text,
                    )
                    IconButton(
                        onClick = onIncrease,
                        enabled = !zoomLevel.isMaximum,
                        modifier = Modifier.size(touchTarget),
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = "Increase Arabic text size")
                    }
                }

                if (hasTranslation) {
                    AmanahDivider()
                    Text("Translation size", style = MaterialTheme.typography.labelLarge, color = palette.text)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        IconButton(
                            onClick = onDecreaseTranslation,
                            enabled = !translationLevel.isMinimum,
                            modifier = Modifier.size(touchTarget),
                        ) {
                            Icon(Icons.Rounded.Remove, contentDescription = "Decrease translation size")
                        }
                        Text(
                            text = "${(translationLevel.multiplier * 100).roundToInt()}%",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = palette.text,
                        )
                        IconButton(
                            onClick = onIncreaseTranslation,
                            enabled = !translationLevel.isMaximum,
                            modifier = Modifier.size(touchTarget),
                        ) {
                            Icon(Icons.Rounded.Add, contentDescription = "Increase translation size")
                        }
                    }
                }

                AmanahDivider()
                TextButton(
                    onClick = {
                        onReset()
                        if (hasTranslation) onResetTranslation()
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Reset to default")
                }
            }
        }
    }
}

@Composable
private fun ReaderAutoScrollTrigger(
    state: AutoScrollState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val elder = LocalElderMode.current
    val palette = LocalReaderPalette.current
    val touchTarget = if (elder) AmanahSpacing.minTouchTargetElder else AmanahSpacing.minTouchTarget
    val running = state == AutoScrollState.RUNNING || state == AutoScrollState.STARTING
    IconButton(
        onClick = onClick,
        modifier = modifier.size(touchTarget),
    ) {
        val description = when (state) {
            AutoScrollState.RUNNING, AutoScrollState.STARTING -> "Pause auto-scroll"
            AutoScrollState.PAUSED -> "Resume auto-scroll"
            AutoScrollState.INACTIVE, AutoScrollState.COMPLETED -> "Start hands-free auto-scroll"
        }
        Icon(
            imageVector = if (running) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
            contentDescription = description,
            tint = if (state != AutoScrollState.INACTIVE) palette.activeControl else LocalContentColor.current,
        )
    }
}

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
        modifier = modifier.widthIn(max = 420.dp),
        shape = AmanahShapes.card,
        colors = CardDefaults.cardColors(containerColor = palette.controlSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = AmanahSpacing.md, vertical = AmanahSpacing.sm),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.sm),
                modifier = Modifier.fillMaxWidth(),
            ) {
                IconButton(
                    onClick = { if (running) controller.pause() else controller.resume() },
                    enabled = !completed,
                    modifier = Modifier.size(touchTarget),
                ) {
                    Icon(
                        imageVector = if (running) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = if (running) "Pause auto-scroll" else "Resume auto-scroll",
                        tint = if (completed) palette.inactiveControl else palette.activeControl,
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Slow",
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.secondaryText,
                        )
                        Text(
                            text = if (completed) "Completed" else "%02d:%02d".format(elapsedSeconds / 60, elapsedSeconds % 60),
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.secondaryText,
                        )
                        Text(
                            text = "Fast",
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.secondaryText,
                        )
                    }
                    Slider(
                        value = pace.ordinal.toFloat(),
                        onValueChange = { value ->
                            val index = value.roundToInt().coerceIn(0, AutoScrollPace.entries.lastIndex)
                            onPaceChange(AutoScrollPace.entries[index])
                        },
                        valueRange = 0f..AutoScrollPace.entries.lastIndex.toFloat(),
                        steps = AutoScrollPace.entries.size - 2,
                        enabled = !completed,
                        colors = SliderDefaults.colors(
                            thumbColor = palette.activeControl,
                            activeTrackColor = palette.activeControl,
                            inactiveTrackColor = palette.inactiveControl.copy(alpha = 0.3f),
                        ),
                    )
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
                    .clipToBounds()
                    .background(palette.controlSurface, shape = AmanahShapes.card)
                    .clickable { onSelectAyah(ayah.ayahKey) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "${ayah.ayahNumber}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = ((if (elder) 15f else 12f) * markerScale).sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = palette.secondaryText,
                )
            }
        }
        val fontFamily = remember(ayah.scriptType) { QuranFonts.getFontFamily(ayah.scriptType) }
        val effectiveLineHeightSp = (arabicFontSizeSp * arabicLineSpacingMultiplier).sp
        val letterSpacingSp = if (ayah.scriptType == ScriptType.INDOPAK) (-0.4).sp else 0.sp
        val isSelected = ayah.isSelected
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (isSelected) {
                            Modifier.background(palette.activeControl.copy(alpha = 0.12f), shape = AmanahShapes.card)
                        } else {
                            Modifier
                        }
                    )
                    .clickable { onSelectAyah(ayah.ayahKey) }
                    .padding(vertical = AmanahSpacing.xs, horizontal = AmanahSpacing.xs),
            ) {
                Text(
                    text = ayah.displayText,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = arabicFontSizeSp.sp,
                        lineHeight = effectiveLineHeightSp,
                        letterSpacing = letterSpacingSp,
                        fontFamily = fontFamily,
                    ),
                    color = palette.text,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        if (!translationText.isNullOrBlank()) {
            Text(
                text = translationText,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = translationFontSizeSp.sp,
                    lineHeight = (translationFontSizeSp * 1.65f).sp,
                ),
                color = palette.secondaryText,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectAyah(ayah.ayahKey) }
                    .padding(vertical = 2.dp, horizontal = AmanahSpacing.xs),
            )
        }
    }
}

private enum class ReportCategory(val label: String, val emailSubject: String) {
    TYPO("Typo in Arabic Text", "Typo Report: Arabic Text"),
    SCRIPT("Script / Rendering Issue", "Rendering Report: Script Issue"),
    NUMBERING("Ayah Numbering Error", "Numbering Report: Ayah Number Error"),
    OTHER("Other Issue", "Content Report: General Issue"),
}

private fun reportAyah(context: Context, ayah: ReaderAyahUiModel, category: ReportCategory) {
    val body = buildString {
        appendLine("--- Amanah Quran Issue Report ---")
        appendLine("Category: ${category.label}")
        appendLine("Surah: ${ayah.surahNameSimple} (${ayah.surahNumber})")
        appendLine("Ayah: ${ayah.ayahNumber} (Key: ${ayah.ayahKey})")
        appendLine("Juz: ${ayah.juzNumber} | Page: ${ayah.pageNumber}")
        appendLine("Script: ${ayah.scriptType.name}")
        appendLine("Display Text: ${ayah.displayText}")
        appendLine()
        appendLine("Please describe the issue below:")
        appendLine()
    }
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "message/rfc822"
        putExtra(Intent.EXTRA_EMAIL, arrayOf("feedback@amanahquran.app"))
        putExtra(Intent.EXTRA_SUBJECT, "[Amanah Quran] ${category.emailSubject} (${ayah.ayahKey})")
        putExtra(Intent.EXTRA_TEXT, body)
    }
    try {
        context.startActivity(Intent.createChooser(intent, "Send issue report via..."))
    } catch (_: Exception) {
    }
}

private fun shareAyah(context: Context, ayah: ReaderAyahUiModel, translationText: String? = null) {
    val shareText = buildString {
        appendLine(ayah.displayText)
        if (!translationText.isNullOrBlank()) {
            appendLine()
            appendLine(translationText)
        }
        appendLine()
        append("— ${ayah.surahNameSimple} ${ayah.surahNumber}:${ayah.ayahNumber}")
    }
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Share Ayah")
    context.startActivity(shareIntent)
}

private fun shareAyahImage(context: Context, ayah: ReaderAyahUiModel, translationText: String? = null) {
    val bitmap = createAyahCardBitmap(context, ayah, translationText)
    val uri = saveBitmapToCache(context, bitmap, "ayah_${ayah.surahNumber}_${ayah.ayahNumber}.png")
    if (uri != null) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/png"
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Ayah as Image")
        context.startActivity(shareIntent)
    }
}

private fun createAyahCardBitmap(
    context: Context,
    ayah: ReaderAyahUiModel,
    translationText: String? = null,
): Bitmap {
    val width = 1080
    val padding = 80
    val contentWidth = width - (padding * 2)

    val fontResId = if (ayah.scriptType == ScriptType.INDOPAK) {
        R.font.digital_khatt_indopak
    } else {
        R.font.digital_khatt_v2
    }
    val arabicTypeface = ResourcesCompat.getFont(context, fontResId) ?: Typeface.DEFAULT_BOLD

    val arabicPaint = TextPaint().apply {
        isAntiAlias = true
        color = AndroidColor.parseColor("#1C1B1F")
        textSize = 52f
        typeface = arabicTypeface
    }

    val arabicLayout = StaticLayout.Builder.obtain(ayah.displayText, 0, ayah.displayText.length, arabicPaint, contentWidth)
        .setAlignment(Layout.Alignment.ALIGN_CENTER)
        .setLineSpacing(0f, 1.8f)
        .setIncludePad(true)
        .build()

    val translationLayout = if (!translationText.isNullOrBlank()) {
        val transPaint = TextPaint().apply {
            isAntiAlias = true
            color = AndroidColor.parseColor("#49454F")
            textSize = 36f
            typeface = Typeface.DEFAULT
        }
        StaticLayout.Builder.obtain(translationText, 0, translationText.length, transPaint, contentWidth)
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .setLineSpacing(0f, 1.4f)
            .setIncludePad(true)
            .build()
    } else null

    val refText = "— ${ayah.surahNameSimple} ${ayah.surahNumber}:${ayah.ayahNumber} (Juz ${ayah.juzNumber})"
    val refPaint = TextPaint().apply {
        isAntiAlias = true
        color = AndroidColor.parseColor("#997B28")
        textSize = 32f
        typeface = Typeface.DEFAULT_BOLD
    }
    val refLayout = StaticLayout.Builder.obtain(refText, 0, refText.length, refPaint, contentWidth)
        .setAlignment(Layout.Alignment.ALIGN_CENTER)
        .setLineSpacing(0f, 1.0f)
        .setIncludePad(true)
        .build()

    val appNameText = "Amanah Quran"
    val appPaint = TextPaint().apply {
        isAntiAlias = true
        color = AndroidColor.parseColor("#79747E")
        textSize = 28f
        typeface = Typeface.DEFAULT
    }
    val appLayout = StaticLayout.Builder.obtain(appNameText, 0, appNameText.length, appPaint, contentWidth)
        .setAlignment(Layout.Alignment.ALIGN_CENTER)
        .setLineSpacing(0f, 1.0f)
        .setIncludePad(true)
        .build()

    val spacing = 40
    val totalHeight = (padding * 2) + arabicLayout.height +
            (if (translationLayout != null) spacing + translationLayout.height else 0) +
            spacing + refLayout.height + spacing + appLayout.height

    val height = totalHeight.coerceAtLeast(600)

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.drawColor(AndroidColor.parseColor("#FAF8F5"))

    val borderPaint = android.graphics.Paint().apply {
        isAntiAlias = true
        color = AndroidColor.parseColor("#E0D6C8")
        style = android.graphics.Paint.Style.STROKE
        strokeWidth = 4f
    }
    canvas.drawRoundRect(20f, 20f, width - 20f, height - 20f, 32f, 32f, borderPaint)

    canvas.save()
    var currentY = padding.toFloat()

    canvas.translate(padding.toFloat(), currentY)
    arabicLayout.draw(canvas)
    canvas.restore()
    currentY += arabicLayout.height + spacing

    if (translationLayout != null) {
        canvas.save()
        canvas.translate(padding.toFloat(), currentY)
        translationLayout.draw(canvas)
        canvas.restore()
        currentY += translationLayout.height + spacing
    }

    canvas.save()
    canvas.translate(padding.toFloat(), currentY)
    refLayout.draw(canvas)
    canvas.restore()
    currentY += refLayout.height + spacing

    canvas.save()
    canvas.translate(padding.toFloat(), currentY)
    appLayout.draw(canvas)
    canvas.restore()

    return bitmap
}

private fun saveBitmapToCache(context: Context, bitmap: Bitmap, fileName: String): android.net.Uri? {
    val cachePath = File(context.cacheDir, "images")
    cachePath.mkdirs()
    val file = File(cachePath, fileName)
    try {
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    } catch (_: IOException) {
        return null
    }
}

private fun Modifier.readerPinchPagingGesture(
    onPinchZoom: (zoomDelta: Float, centroidX: Float, centroidY: Float, totalWidth: Float) -> Unit,
    onGestureEnd: () -> Unit,
): Modifier = pointerInput(Unit) {
    detectPinchZoomGesturesWithCentroid(
        onGesture = onPinchZoom,
        onEnd = onGestureEnd,
    )
}

private suspend fun PointerInputScope.detectPinchZoomGesturesWithCentroid(
    onGesture: (zoomDelta: Float, centroidX: Float, centroidY: Float, totalWidth: Float) -> Unit,
    onEnd: () -> Unit,
) {
    val totalWidth = size.width.toFloat()
    awaitEachGesture {
        var zoom = 1f
        var pastTouchSlop = false
        val touchSlop = viewConfiguration.touchSlop

        awaitFirstDown(requireUnconsumed = false)
        do {
            val event = awaitPointerEvent()
            val canceled = event.changes.any { it.isConsumed }
            if (!canceled) {
                val zoomChange = event.calculateZoom()
                val panChange = event.calculatePan()

                if (!pastTouchSlop) {
                    zoom *= zoomChange
                    val centroid = event.calculateCentroidSize(useCurrent = false)
                    val panMotion = panChange.getDistance()

                    if (kotlin.math.abs(zoom - 1f) > (touchSlop / 100f) || panMotion > touchSlop) {
                        pastTouchSlop = true
                    }
                }

                if (pastTouchSlop) {
                    val centroid = event.calculateCentroid(useCurrent = false)
                    if (zoomChange != 1f) {
                        onGesture(zoomChange, centroid.x, centroid.y, totalWidth)
                    }
                    event.changes.forEach {
                        if (it.positionChanged()) {
                            it.consume()
                        }
                    }
                }
            }
        } while (!canceled && event.changes.any { it.pressed })
        onEnd()
    }
}

private fun androidx.compose.ui.input.pointer.PointerEvent.calculateCentroid(useCurrent: Boolean = true): Offset {
    var centroid = Offset.Zero
    var count = 0
    changes.forEach { change ->
        val position = if (useCurrent) change.position else change.previousPosition
        centroid += position
        count++
    }
    return if (count > 0) centroid / count.toFloat() else Offset.Zero
}

private fun androidx.compose.ui.input.pointer.PointerEvent.calculateCentroidSize(useCurrent: Boolean = true): Float {
    val centroid = calculateCentroid(useCurrent)
    var size = 0f
    var count = 0
    changes.forEach { change ->
        val position = if (useCurrent) change.position else change.previousPosition
        size += (position - centroid).getDistance()
        count++
    }
    return if (count > 0) size / count.toFloat() else 0f
}

private suspend fun androidx.compose.ui.input.pointer.AwaitPointerEventScope.awaitFirstDown(
    requireUnconsumed: Boolean = true,
): androidx.compose.ui.input.pointer.PointerInputChange {
    var event: androidx.compose.ui.input.pointer.PointerInputChange
    do {
        val pointerEvent = awaitPointerEvent()
        event = pointerEvent.changes.first()
    } while (!event.pressed || (requireUnconsumed && event.isConsumed))
    return event
}
