package org.amanahquran.app.feature.reader

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.amanahquran.app.core.model.ReaderOpenMode
import org.amanahquran.app.core.model.ScriptType

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
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onNavigateBack = onNavigateBack,
        onScriptSelected = viewModel::selectScript,
        onSelectAyah = viewModel::selectAyah,
        onToggleBookmark = viewModel::toggleBookmark,
        onTogglePageBookmark = viewModel::toggleCurrentPageBookmark,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranReaderScreen(
    openMode: ReaderOpenMode,
    initialAyahKey: String? = null,
    onNavigateBack: () -> Unit,
    viewModel: ReaderViewModel = viewModel(
        key = "reader-${openMode.identityKey()}-${initialAyahKey.orEmpty()}",
        factory = ReaderViewModel.factory(LocalContext.current, openMode, initialAyahKey),
    ),
) {
    ReaderScreen(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onNavigateBack = onNavigateBack,
        onScriptSelected = viewModel::selectScript,
        onSelectAyah = viewModel::selectAyah,
        onToggleBookmark = viewModel::toggleBookmark,
        onTogglePageBookmark = viewModel::toggleCurrentPageBookmark,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReaderScreen(
    uiState: ReaderUiState,
    onNavigateBack: () -> Unit,
    onScriptSelected: (ScriptType) -> Unit,
    onSelectAyah: (String) -> Unit,
    onToggleBookmark: (String) -> Unit,
    onTogglePageBookmark: (() -> Unit)? = null,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.surahName.ifBlank { uiState.modeTitle }) },
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) {
                        Text("Back")
                    }
                },
            )
        },
    ) { padding ->
        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text("Unable to load reader content", style = MaterialTheme.typography.titleMedium)
                    Text(uiState.errorMessage.orEmpty(), style = MaterialTheme.typography.bodyMedium)
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    item {
                        ReaderHeader(
                            selectedScript = uiState.selectedScript,
                            isPageBookmarked = uiState.isPageBookmarked,
                            showPageBookmark = uiState.openMode is ReaderOpenMode.Page,
                            onScriptSelected = onScriptSelected,
                            onTogglePageBookmark = onTogglePageBookmark,
                        )
                    }
                    items(uiState.ayahs, key = { it.ayahKey }) { ayah ->
                        ReaderAyahRow(
                            ayah = ayah,
                            arabicFontSizeSp = uiState.arabicFontSizeSp,
                            onSelect = onSelectAyah,
                            onToggleBookmark = onToggleBookmark,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReaderHeader(
    selectedScript: ScriptType,
    isPageBookmarked: Boolean,
    showPageBookmark: Boolean,
    onScriptSelected: (ScriptType) -> Unit,
    onTogglePageBookmark: (() -> Unit)?,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Script: ${selectedScript.displayLabel()}",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = selectedScript == ScriptType.INDOPAK,
                onClick = { onScriptSelected(ScriptType.INDOPAK) },
                label = { Text("IndoPak") },
            )
            FilterChip(
                selected = selectedScript == ScriptType.UTHMANI,
                onClick = { onScriptSelected(ScriptType.UTHMANI) },
                label = { Text("Uthmani") },
            )
        }
        if (showPageBookmark) {
            Button(onClick = { onTogglePageBookmark?.invoke() }) {
                Text(if (isPageBookmarked) "Page Bookmarked" else "Bookmark Page")
            }
        }
    }
}

@Composable
private fun ReaderAyahRow(
    ayah: ReaderAyahUiModel,
    arabicFontSizeSp: Float,
    onSelect: (String) -> Unit,
    onToggleBookmark: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(onClick = { onToggleBookmark(ayah.ayahKey) }) {
                Text(if (ayah.isBookmarked) "Bookmarked" else "Bookmark")
            }
            Text(
                text = if (ayah.isSelected) "Current reading position" else "",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Text(
            text = ayah.displayText,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = arabicFontSizeSp.sp,
                lineHeight = (arabicFontSizeSp * 1.45f).sp,
            ),
            textAlign = TextAlign.Right,
            color = if (ayah.isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "${ayah.surahNumber}:${ayah.ayahNumber}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            TextButton(onClick = { onSelect(ayah.ayahKey) }) {
                Text(if (ayah.isSelected) "Selected" else "Mark current")
            }
        }
    }
}

private fun ScriptType.displayLabel(): String = when (this) {
    ScriptType.INDOPAK -> "IndoPak"
    ScriptType.UTHMANI -> "Uthmani"
}

private fun ReaderOpenMode.identityKey(): String = when (this) {
    is ReaderOpenMode.Surah -> "surah-$surahNumber"
    is ReaderOpenMode.Page -> "page-$pageNumber-${pageReferenceType.name}"
    is ReaderOpenMode.Juz -> "juz-$juzNumber"
    is ReaderOpenMode.AyahTarget -> "ayah-$surahNumber-$ayahKey"
}
