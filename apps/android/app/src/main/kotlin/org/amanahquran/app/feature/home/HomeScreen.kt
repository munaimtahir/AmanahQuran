package org.amanahquran.app.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreen(
    onOpenReader: () -> Unit,
    onContinueReading: (HomeContinueReadingUiModel) -> Unit,
    onOpenSearch: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenTrustCenter: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.factory(LocalContext.current)),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text("Amanah Quran", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Android V1 Sacred Reader for offline Quran reading from verified packaged content.",
                style = MaterialTheme.typography.bodyLarge,
            )

            if (uiState.showFirstLaunchMessage) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            "Amanah Quran is free, offline, ad-free, and built without tracking. Quran text sources and verification details are available in the Trust Center.",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        OutlinedButton(onClick = viewModel::dismissFirstLaunchMessage) {
                            Text("Dismiss")
                        }
                    }
                }
            }

            uiState.continueReading?.let { continueReading ->
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(continueReading.title, style = MaterialTheme.typography.titleMedium)
                        Text(continueReading.subtitle, style = MaterialTheme.typography.bodyMedium)
                        continueReading.previewText?.takeIf { it.isNotBlank() }?.let { preview ->
                            Text(preview, style = MaterialTheme.typography.bodySmall)
                        }
                        Button(
                            onClick = { onContinueReading(continueReading) },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Continue Reading")
                        }
                    }
                }
            } ?: Button(onClick = onOpenReader, modifier = Modifier.fillMaxWidth()) {
                Text("Start Reading Quran")
            }

            Button(onClick = onOpenSearch, modifier = Modifier.fillMaxWidth()) {
                Text("Open Search")
            }
            Button(onClick = onOpenBookmarks, modifier = Modifier.fillMaxWidth()) {
                Text("Open Bookmarks")
            }
            Button(onClick = onOpenSettings, modifier = Modifier.fillMaxWidth()) {
                Text("Open Settings")
            }
            Button(onClick = onOpenTrustCenter, modifier = Modifier.fillMaxWidth()) {
                Text("Open Trust Center")
            }
        }
    }
}
