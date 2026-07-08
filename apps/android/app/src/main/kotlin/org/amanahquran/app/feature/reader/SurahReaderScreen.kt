package org.amanahquran.app.feature.reader

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material.icons.rounded.Close
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch
import org.amanahquran.app.core.model.PageReferenceType
import org.amanahquran.app.core.model.ReaderAnchor
import org.amanahquran.app.core.model.ReaderOpenMode
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.theme.AmanahGoldMuted
import org.amanahquran.app.core.theme.AmanahShapes
import org.amanahquran.app.core.theme.AmanahSpacing
import org.amanahquran.app.core.theme.LocalElderMode
import org.amanahquran.app.core.theme.QuranFonts
import org.amanahquran.app.core.ui.AmanahDivider
import org.amanahquran.app.core.ui.AmanahScriptChip

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
    ReaderScreen(
        uiState = viewModel.uiState.collectAsState().value,
        onNavigateBack = onNavigateBack,
        onSelectAyah = viewModel::selectAyah,
        onToggleBookmark = viewModel::toggleBookmark,
        onTogglePageBookmark = viewModel::toggleCurrentPageBookmark,
        onOpenModeChanged = { newMode -> viewModel.loadOpenMode(newMode) },
        onSelectScript = viewModel::selectScript,
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
    ReaderScreen(
        uiState = viewModel.uiState.collectAsState().value,
        onNavigateBack = onNavigateBack,
        onSelectAyah = viewModel::selectAyah,
        onToggleBookmark = viewModel::toggleBookmark,
        onTogglePageBookmark = viewModel::toggleCurrentPageBookmark,
        onOpenModeChanged = { newMode -> viewModel.loadOpenMode(newMode) },
        onSelectScript = viewModel::selectScript,
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
    ReaderScreen(
        uiState = viewModel.uiState.collectAsState().value,
        onNavigateBack = onNavigateBack,
        onSelectAyah = viewModel::selectAyah,
        onToggleBookmark = viewModel::toggleBookmark,
        onTogglePageBookmark = viewModel::toggleCurrentPageBookmark,
        onOpenModeChanged = { newMode -> viewModel.loadOpenMode(newMode) },
        onSelectScript = viewModel::selectScript,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun ReaderScreen(
    uiState: ReaderUiState,
    onNavigateBack: () -> Unit,
    onSelectAyah: (String) -> Unit,
    onToggleBookmark: (String) -> Unit,
    onTogglePageBookmark: (() -> Unit)? = null,
    onOpenModeChanged: (ReaderOpenMode) -> Unit,
    onSelectScript: (ScriptType) -> Unit,
) {
    val elder = LocalElderMode.current
    val readerBg = MaterialTheme.colorScheme.background
    val firstContentLogged = remember(uiState.readerOpenStartedAtMs, uiState.openMode, uiState.selectedScript) {
        mutableStateOf(false)
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
            if (!uiState.bookModeEnabled) {
                TopAppBar(
                    title = {
                        Text(
                            text = uiState.surahName.ifBlank { uiState.modeTitle },
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Go back")
                        }
                    },
                    actions = {
                        ReaderScriptSwitch(
                            selectedScript = uiState.selectedScript,
                            onSelectScript = onSelectScript,
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = readerBg,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                        actionIconContentColor = MaterialTheme.colorScheme.onBackground,
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

            uiState.bookModeEnabled -> {
                val pageReferenceType = (uiState.openMode as? ReaderOpenMode.Page)?.pageReferenceType
                    ?: if (uiState.selectedScript == ScriptType.UTHMANI) PageReferenceType.UTHMANI else PageReferenceType.INDOPAK
                val pageCount = if (pageReferenceType == PageReferenceType.UTHMANI) 604 else 559
                val initialIndex = when (val mode = uiState.openMode) {
                    is ReaderOpenMode.Page -> mode.pageNumber - 1
                    else -> 0
                }

                val pagerState = rememberPagerState(
                    initialPage = initialIndex,
                    pageCount = { pageCount },
                )
                val scope = rememberCoroutineScope()

                LaunchedEffect(initialIndex) {
                    if (pagerState.currentPage != initialIndex) {
                        pagerState.scrollToPage(initialIndex)
                    }
                }

                LaunchedEffect(pagerState) {
                    snapshotFlow { pagerState.currentPage }.collect { index ->
                        val targetPageRefType = (uiState.openMode as? ReaderOpenMode.Page)?.pageReferenceType
                            ?: if (uiState.selectedScript == ScriptType.UTHMANI) PageReferenceType.UTHMANI else PageReferenceType.INDOPAK
                        val newMode = ReaderOpenMode.Page(index + 1, targetPageRefType)
                        if (newMode != uiState.openMode) {
                            onOpenModeChanged(newMode)
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding),
                        ) { page ->
                            val isCurrentLoaded = when (val mode = uiState.openMode) {
                                is ReaderOpenMode.Surah -> mode.surahNumber - 1 == page
                                is ReaderOpenMode.Page -> mode.pageNumber - 1 == page
                                is ReaderOpenMode.Juz -> mode.juzNumber - 1 == page
                                is ReaderOpenMode.AyahTarget -> mode.surahNumber - 1 == page
                            }

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
                                        pageNumber = when (val mode = uiState.openMode) {
                                            is ReaderOpenMode.Page -> mode.pageNumber
                                            else -> 1
                                        },
                                        onNavigateBack = onNavigateBack,
                                        onTogglePageBookmark = onTogglePageBookmark,
                                    )
                                    ReaderScriptSwitch(
                                        selectedScript = uiState.selectedScript,
                                        onSelectScript = onSelectScript,
                                    )
                                    AmanahDivider(modifier = Modifier.padding(bottom = AmanahSpacing.sm))

                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm),
                                    ) {
                                        val groupedBlocks = groupReaderBlocks(uiState.readerBlocks)
                                        items(
                                            items = groupedBlocks,
                                            key = { item ->
                                                when (item) {
                                                    is BookModeBlock.Header -> item.item.key()
                                                    is BookModeBlock.Paragraph -> "para-${item.ayahs.firstOrNull()?.ayahKey}"
                                                }
                                            }
                                        ) { groupedItem ->
                                            when (groupedItem) {
                                                is BookModeBlock.Header -> {
                                                    ReaderStructuralContent(groupedItem.item)
                                                }
                                                is BookModeBlock.Paragraph -> {
                                                    ReaderBookModeParagraph(
                                                        ayahs = groupedItem.ayahs,
                                                        arabicFontSizeSp = uiState.arabicFontSizeSp,
                                                        onSelectAyah = onSelectAyah,
                                                    )
                                                }
                                            }
                                        }
                                    }
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

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(60.dp)
                            .align(Alignment.CenterStart)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) {
                                scope.launch {
                                    if (pagerState.currentPage < pagerState.pageCount - 1) {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                }
                            },
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(60.dp)
                            .align(Alignment.CenterEnd)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) {
                                scope.launch {
                                    if (pagerState.currentPage > 0) {
                                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                    }
                                }
                            },
                    )

                    // Floating card for selected ayah in book mode
                    if (uiState.bookModeEnabled && uiState.selectedAyahKey != null) {
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
                                        IconButton(onClick = { onSelectAyah("") }) {
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
                val readerPadding = if (elder) AmanahSpacing.readerPaddingElder else AmanahSpacing.readerPadding
                val listState = rememberLazyListState()
                LaunchedEffect(
                    uiState.anchorScrollRequestId,
                    uiState.anchorScrollIndex,
                    uiState.readerBlocks,
                ) {
                    val targetIndex = uiState.anchorScrollIndex ?: return@LaunchedEffect
                    if (targetIndex in uiState.readerBlocks.indices) {
                        listState.scrollToItem(targetIndex)
                        ReaderPerfLogger.log(
                            "anchor_visible",
                            uiState.readerOpenStartedAtMs,
                            "ayah=${uiState.selectedAyahKey} index=$targetIndex",
                        )
                    }
                }
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(readerPadding),
                    verticalArrangement = Arrangement.spacedBy(AmanahSpacing.xl),
                ) {
                    if (uiState.openMode is ReaderOpenMode.Page && onTogglePageBookmark != null) {
                        item {
                            PageBookmarkRow(
                                isBookmarked = uiState.isPageBookmarked,
                                onToggle = onTogglePageBookmark,
                            )
                            AmanahDivider(modifier = Modifier.padding(top = AmanahSpacing.sm))
                        }
                    }

                    items(uiState.readerBlocks, key = { it.key() }) { item ->
                        when (item) {
                            is ReaderStructuralItem.Ayah -> ReaderAyahRow(
                                ayah = item.ayah,
                                arabicFontSizeSp = uiState.arabicFontSizeSp,
                                onToggleBookmark = onToggleBookmark,
                                onSelectAyah = onSelectAyah,
                            )

                            else -> ReaderStructuralContent(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReaderScriptSwitch(
    selectedScript: ScriptType,
    onSelectScript: (ScriptType) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        AmanahScriptChip(
            label = "IndoPak",
            selected = selectedScript == ScriptType.INDOPAK,
            onClick = { onSelectScript(ScriptType.INDOPAK) },
        )
        AmanahScriptChip(
            label = "Uthmani",
            selected = selectedScript == ScriptType.UTHMANI,
            onClick = { onSelectScript(ScriptType.UTHMANI) },
        )
    }
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
                modifier = Modifier.size(30.dp),
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
                    modifier = Modifier.size(30.dp),
                ) {
                    Icon(
                        imageVector = if (uiState.isPageBookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                        contentDescription = "Bookmark page",
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

@Composable
private fun ReaderAyahRow(
    ayah: ReaderAyahUiModel,
    arabicFontSizeSp: Float,
    onToggleBookmark: (String) -> Unit,
    onSelectAyah: (String) -> Unit,
) {
    val elder = LocalElderMode.current
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
                    .size(if (elder) 36.dp else 30.dp)
                    .background(
                        color = if (ayah.isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        shape = AmanahShapes.chip,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "${ayah.surahNumber}:${ayah.ayahNumber}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    color = if (ayah.isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.xs),
            ) {
                if (ayah.isSelected) {
                    Text(
                        text = "Current",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                IconButton(
                    onClick = { onToggleBookmark(ayah.ayahKey) },
                    modifier = Modifier.size(AmanahSpacing.minTouchTarget),
                ) {
                    Icon(
                        imageVector = if (ayah.isBookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                        contentDescription = if (ayah.isBookmarked) "Remove bookmark" else "Bookmark ayah",
                        tint = if (ayah.isBookmarked) AmanahGoldMuted else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }

        Text(
            text = ayah.displayText,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelectAyah(ayah.ayahKey) }
                .then(
                    if (!ayah.isSelected) Modifier else Modifier.background(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        shape = AmanahShapes.ayahCard,
                    )
                )
                .padding(horizontal = AmanahSpacing.sm, vertical = if (ayah.isSelected) AmanahSpacing.xs else 0.dp),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = arabicFontSizeSp.sp,
                lineHeight = (arabicFontSizeSp * 1.88f).sp,
                letterSpacing = 0.sp,
                fontFamily = QuranFonts.getFontFamily(ayah.scriptType),
            ),
            textAlign = TextAlign.Center,
            color = if (ayah.isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        )

        AmanahDivider()
    }
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

@Composable
private fun ReaderBookModeParagraph(
    ayahs: List<ReaderAyahUiModel>,
    arabicFontSizeSp: Float,
    onSelectAyah: (String) -> Unit,
    modifier: Modifier = Modifier,
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
    }
}
