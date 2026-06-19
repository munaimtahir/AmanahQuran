package org.amanahquran.app.feature.reader

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.amanahquran.app.core.model.ReaderOpenMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JuzListScreen(
    onNavigateBack: () -> Unit,
    onOpenJuz: (Int) -> Unit,
    viewModel: JuzListViewModel = viewModel(factory = JuzListViewModel.factory(LocalContext.current)),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Juz List") },
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) { Text("Back") }
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
                    Text("Unable to load Juz list", style = MaterialTheme.typography.titleMedium)
                    Text(uiState.errorMessage.orEmpty(), style = MaterialTheme.typography.bodyMedium)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(uiState.items, key = { it.juzNumber }) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onOpenJuz(item.juzNumber) },
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Text("Juz ${item.juzNumber}", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "${item.startSurahName} ${item.startAyahKey} → ${item.endSurahName ?: item.startSurahName} ${item.endAyahKey ?: item.startAyahKey}",
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                                Text("${item.ayahCount} ayahs", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
