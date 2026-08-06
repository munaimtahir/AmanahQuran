package org.amanahquran.app.feature.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.amanahquran.app.core.model.ReaderOpenMode
import org.amanahquran.app.core.model.PageReferenceType
import org.amanahquran.app.core.theme.AmanahShapes
import org.amanahquran.app.core.theme.AmanahSpacing
import org.amanahquran.app.core.theme.LocalElderMode
import org.amanahquran.app.core.ui.AmanahDivider
import org.amanahquran.app.core.ui.AmanahScriptChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JuzListScreen(
    onNavigateBack: () -> Unit,
    onOpenJuz: (Int) -> Unit,
    viewModel: JuzListViewModel = viewModel(factory = JuzListViewModel.factory(LocalContext.current)),
) {
    val uiState by viewModel.uiState.collectAsState()
    val horizontalPadding = if (LocalElderMode.current) AmanahSpacing.screenHorizontalPaddingElder else AmanahSpacing.screenHorizontalPadding

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Juz", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Go back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                ),
            )
        },
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
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
                    Text("Unable to load Juz list", style = MaterialTheme.typography.titleMedium)
                    Text(uiState.errorMessage.orEmpty(), style = MaterialTheme.typography.bodyMedium)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = AmanahSpacing.sm),
                ) {
                    items(uiState.items, key = { it.juzNumber }) { item ->
                        JuzListRow(
                            juzNumber = item.juzNumber,
                            startInfo = "${item.startSurahName} ${item.startAyahKey}",
                            endInfo = if (item.endSurahName != null) "${item.endSurahName} ${item.endAyahKey ?: ""}" else "",
                            ayahCount = item.ayahCount,
                            onClick = { onOpenJuz(item.juzNumber) },
                        )
                        AmanahDivider(modifier = Modifier.padding(horizontal = AmanahSpacing.sm))
                    }
                }
            }
        }
    }
}

@Composable
private fun JuzListRow(
    juzNumber: Int,
    startInfo: String,
    endInfo: String,
    ayahCount: Int,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = AmanahSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.md),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = AmanahShapes.numberBadge,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = juzNumber.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = "Juz $juzNumber",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = if (endInfo.isNotBlank()) "$startInfo – $endInfo" else startInfo,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "$ayahCount ayahs",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageListScreen(
    onNavigateBack: () -> Unit,
    onOpenPage: (Int, PageReferenceType) -> Unit,
    viewModel: PageListViewModel = viewModel(factory = PageListViewModel.factory(LocalContext.current)),
) {
    val uiState by viewModel.uiState.collectAsState()
    val elder = LocalElderMode.current
    val horizontalPadding = if (elder) AmanahSpacing.screenHorizontalPaddingElder else AmanahSpacing.screenHorizontalPadding

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Pages", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Go back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                ),
            )
        },
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
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
                    Text("Unable to load pages", style = MaterialTheme.typography.titleMedium)
                    Text(uiState.errorMessage.orEmpty(), style = MaterialTheme.typography.bodyMedium)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = AmanahSpacing.sm),
                ) {
                    item {
                        Column(
                            modifier = Modifier.padding(vertical = AmanahSpacing.md),
                            verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm),
                        ) {
                            Text(
                                text = "Page reference layout",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
                                AmanahScriptChip(
                                    label = "IndoPak",
                                    selected = uiState.pageReferenceType == PageReferenceType.INDOPAK,
                                    onClick = { viewModel.selectPageReferenceType(PageReferenceType.INDOPAK) },
                                )
                                AmanahScriptChip(
                                    label = "Uthmani",
                                    selected = uiState.pageReferenceType == PageReferenceType.UTHMANI,
                                    onClick = { viewModel.selectPageReferenceType(PageReferenceType.UTHMANI) },
                                )
                            }
                        }
                        AmanahDivider()
                    }
                    items(uiState.items, key = { it.pageNumber }) { item ->
                        PageListRow(
                            pageNumber = item.pageNumber,
                            startInfo = "${item.startSurahName} ${item.startAyahKey}",
                            endInfo = if (item.endSurahName != null) "${item.endSurahName} ${item.endAyahKey ?: ""}" else "",
                            ayahCount = item.ayahCount,
                            onClick = { onOpenPage(item.pageNumber, item.pageReferenceType) },
                        )
                        AmanahDivider(modifier = Modifier.padding(horizontal = AmanahSpacing.sm))
                    }
                }
            }
        }
    }
}

@Composable
private fun PageListRow(
    pageNumber: Int,
    startInfo: String,
    endInfo: String,
    ayahCount: Int,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = AmanahSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.md),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = AmanahShapes.numberBadge,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = pageNumber.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = "Page $pageNumber",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = if (endInfo.isNotBlank()) "$startInfo – $endInfo" else startInfo,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "$ayahCount ayahs",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
