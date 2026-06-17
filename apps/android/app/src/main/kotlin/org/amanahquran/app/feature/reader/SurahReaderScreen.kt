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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.amanahquran.app.core.model.ScriptType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahReaderScreen(
    surahNumber: Int,
    onNavigateBack: () -> Unit,
    viewModel: ReaderViewModel = viewModel(
        key = "reader-surah-$surahNumber",
        factory = ReaderViewModel.factory(LocalContext.current, surahNumber),
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.surahName.ifBlank { "Surah ${uiState.surahNumber}" }) },
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
                    Text("Unable to load Surah", style = MaterialTheme.typography.titleMedium)
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
                            onScriptSelected = viewModel::selectScript,
                        )
                    }
                    items(uiState.ayahs, key = { it.ayahKey }) { ayah ->
                        ReaderAyahRow(ayah)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReaderHeader(
    selectedScript: ScriptType,
    onScriptSelected: (ScriptType) -> Unit,
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
    }
}

@Composable
private fun ReaderAyahRow(ayah: ReaderAyahUiModel) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = ayah.displayText,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Right,
            lineHeight = MaterialTheme.typography.headlineSmall.lineHeight,
        )
        Text(
            text = ayah.ayahNumber.toString(),
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Right,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

private fun ScriptType.displayLabel(): String = when (this) {
    ScriptType.INDOPAK -> "IndoPak"
    ScriptType.UTHMANI -> "Uthmani"
}
