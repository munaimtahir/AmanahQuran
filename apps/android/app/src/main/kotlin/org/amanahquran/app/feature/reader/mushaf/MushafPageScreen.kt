package org.amanahquran.app.feature.reader.mushaf

import android.util.Log
import org.amanahquran.app.BuildConfig
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.res.painterResource
import org.amanahquran.app.R
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.FullscreenExit
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material.icons.rounded.ZoomOut
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.repository.MushafRepository
import org.amanahquran.app.core.repository.MushafPageUi
import org.amanahquran.app.core.repository.MushafLineUi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MushafPageScreen(
    initialPageNumber: Int,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MushafReaderViewModel = viewModel(
        key = "mushaf-reader-$initialPageNumber",
        factory = MushafReaderViewModel.factory(LocalContext.current, initialPageNumber)
    )
) {
    val state by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    val pageCount = state.pageCount.coerceAtLeast(1)
    val pagerState = key(initialPageNumber) {
        rememberPagerState(
            initialPage = (initialPageNumber - 1).coerceIn(0, pageCount - 1),
            pageCount = { pageCount }
        )
    }

    LaunchedEffect(initialPageNumber) {
        Log.d("AmanahFontAudit", "MushafPageScreen initialized with initialPageNumber: $initialPageNumber")
    }

    // Sync external navigation changes
    LaunchedEffect(state.pageNumber, pageCount) {
        val targetIndex = state.pageNumber - 1
        if (pagerState.currentPage != targetIndex && targetIndex in 0 until pageCount) {
            Log.d("AmanahFontAudit", "Syncing page to target index: $targetIndex (page ${state.pageNumber})")
            pagerState.scrollToPage(targetIndex)
        }
    }

    // Handle pager swipe to load next/previous
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { index ->
            val pageNum = index + 1
            if (pageNum != state.pageNumber) {
                Log.d("AmanahFontAudit", "Pager swiped to page: $pageNum")
                viewModel.loadPage(pageNum)
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (!state.isFullScreen) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Page ${state.pageNumber}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            val scriptLabel = if (state.scriptType == ScriptType.INDOPAK) "IndoPak" else "Uthmani"
                            Text(
                                text = " ($scriptLabel)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                modifier = Modifier.padding(start = 6.dp)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Go back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.setFontScale(state.fontScale - 0.1f) }) {
                            Icon(Icons.Rounded.ZoomOut, contentDescription = "Decrease Font Size")
                        }
                        IconButton(onClick = { viewModel.setFontScale(state.fontScale + 0.1f) }) {
                            Icon(Icons.Rounded.ZoomIn, contentDescription = "Increase Font Size")
                        }
                        IconButton(onClick = { viewModel.toggleFullScreen() }) {
                            Icon(Icons.Rounded.Fullscreen, contentDescription = "Full Screen Mode")
                        }
                        IconButton(onClick = onOpenSettings) {
                            Icon(Icons.Rounded.Settings, contentDescription = "Settings")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        }
    ) { paddingValues ->
        val topPadding = if (state.isFullScreen) 8.dp else paddingValues.calculateTopPadding()
        val bottomPadding = paddingValues.calculateBottomPadding()

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = topPadding, bottom = bottomPadding)
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        if (state.bookModeEnabled) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClickLabel = if (state.isFullScreen) {
                                            "Show reading controls"
                                        } else {
                                            "Hide reading controls"
                                        },
                                    ) {
                                        viewModel.toggleFullScreen()
                                    }
                            ) { pageIndex ->
                                val pageNum = pageIndex + 1
                                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                    MushafPageItem(
                                        pageNumber = pageNum,
                                        scriptType = state.scriptType,
                                        fontScale = state.fontScale,
                                        repository = remember { viewModel.mushafRepositoryInstance() },
                                        isBookmarked = if (pageNum == state.pageNumber) state.isPageBookmarked else null,
                                        onBookmarkClick = { viewModel.togglePageBookmark() },
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        } else {
                            VerticalPager(
                                state = pagerState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClickLabel = if (state.isFullScreen) {
                                            "Show reading controls"
                                        } else {
                                            "Hide reading controls"
                                        },
                                    ) {
                                        viewModel.toggleFullScreen()
                                    }
                            ) { pageIndex ->
                                val pageNum = pageIndex + 1
                                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                    MushafPageItem(
                                        pageNumber = pageNum,
                                        scriptType = state.scriptType,
                                        fontScale = state.fontScale,
                                        repository = remember { viewModel.mushafRepositoryInstance() },
                                        isBookmarked = if (pageNum == state.pageNumber) state.isPageBookmarked else null,
                                        onBookmarkClick = { viewModel.togglePageBookmark() },
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }

                    // Tapping LEFT margin (50.dp width) turns to NEXT page
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(50.dp)
                            .align(Alignment.CenterStart)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClickLabel = "Next page",
                            ) {
                                scope.launch {
                                    if (pagerState.currentPage < pageCount - 1) {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                }
                            }
                    )

                    // Tapping RIGHT margin (50.dp width) turns to PREVIOUS page
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(50.dp)
                            .align(Alignment.CenterEnd)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClickLabel = "Previous page",
                            ) {
                                scope.launch {
                                    if (pagerState.currentPage > 0) {
                                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                    }
                                }
                            }
                    )

                    // Compact fullscreen exit floating action
                    if (state.isFullScreen) {
                        IconButton(
                            onClick = { viewModel.toggleFullScreen() },
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                                .size(48.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                                    shape = MaterialTheme.shapes.medium
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.FullscreenExit,
                                contentDescription = "Exit Fullscreen",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    // Developer-only layout overlay visible only for debug builds
                    if (BuildConfig.DEBUG) {
                        val fontName = if (state.scriptType == ScriptType.INDOPAK) {
                            "indopak_nastaleeq.ttf"
                        } else {
                            "digital_khatt_v2.otf"
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                                .background(
                                    color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.6f),
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "DEBUG | ${state.scriptType} | $fontName | Page ${state.pageNumber}",
                                color = androidx.compose.ui.graphics.Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Bottom controls (only visible if not full screen)
            if (!state.isFullScreen) {
                MushafPageControls(
                    pageNumber = state.pageNumber,
                    pageCount = state.pageCount,
                    onPreviousPage = viewModel::goToPreviousPage,
                    onNextPage = viewModel::goToNextPage,
                    elderModeEnabled = state.elderModeEnabled,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun MushafPageItem(
    pageNumber: Int,
    scriptType: ScriptType,
    fontScale: Float,
    repository: MushafRepository,
    isBookmarked: Boolean?,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pageData by remember(pageNumber, scriptType) { mutableStateOf<Pair<MushafPageUi, List<MushafLineUi>>?>(null) }
    var isLoading by remember(pageNumber, scriptType) { mutableStateOf(true) }
    var errorMsg by remember(pageNumber, scriptType) { mutableStateOf<String?>(null) }
    var itemBookmarked by remember(pageNumber, scriptType) { mutableStateOf(false) }

    LaunchedEffect(pageNumber, scriptType) {
        isLoading = true
        errorMsg = null
        val startNano = System.nanoTime()
        Log.d("AmanahFontAudit", "Loading page $pageNumber with scriptType $scriptType")
        runCatching {
            repository.getMushafPage(pageNumber, scriptType)
        }.onSuccess { data ->
            val durationMs = (System.nanoTime() - startNano) / 1_000_000
            val fontName = if (scriptType == ScriptType.INDOPAK) "indopak_nastaleeq.ttf" else "digital_khatt_v2.otf"
            Log.d("AmanahFontAudit", "Successfully loaded page $pageNumber ($scriptType) using font $fontName in ${durationMs}ms. Lines count: ${data.second.size}")
            pageData = data
            isLoading = false
        }.onFailure { throwable ->
            val durationMs = (System.nanoTime() - startNano) / 1_000_000
            Log.e("AmanahFontAudit", "Failed to load page $pageNumber ($scriptType) after ${durationMs}ms", throwable)
            errorMsg = throwable.message ?: "Failed to load page $pageNumber"
            isLoading = false
        }
    }

    LaunchedEffect(pageNumber, scriptType) {
        itemBookmarked = repository.isPageBookmarked(pageNumber, scriptType)
    }

    Box(modifier = modifier) {
        when {
            isLoading -> {
                MushafLoadingView(modifier = Modifier.fillMaxSize())
            }
            errorMsg != null -> {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text(errorMsg ?: "Error loading page", style = MaterialTheme.typography.bodyLarge)
                }
            }
            pageData != null -> {
                val (page, lines) = pageData!!
                val bookmarkedState = isBookmarked ?: itemBookmarked
                MushafPageFrame(
                    page = page,
                    lines = lines,
                    scriptType = scriptType,
                    isBookmarked = bookmarkedState,
                    onBookmarkClick = onBookmarkClick,
                    fontScale = fontScale,
                    showFullHeader = page.startsAtSurahBoundary,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun MushafLoadingView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.brand_mark),
                contentDescription = "Amanah Quran Logo",
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = "Loading Holy Quran...",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
            CircularProgressIndicator(
                modifier = Modifier.size(28.dp),
                strokeWidth = 3.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
            )
        }
    }
}
