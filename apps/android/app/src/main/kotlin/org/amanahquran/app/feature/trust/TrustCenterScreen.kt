package org.amanahquran.app.feature.trust

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrustCenterScreen(
    onNavigateBack: () -> Unit,
    viewModel: TrustCenterViewModel = viewModel(factory = TrustCenterViewModel.factory(LocalContext.current)),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trust Center") },
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) {
                        Text("Back")
                    }
                },
            )
        },
    ) { padding ->
        when {
            uiState.generatedAt == null && uiState.noModificationStatement == null -> {
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

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    section("Quran text sources") {
                        uiState.quranTextSourcesActuallyUsed.forEach { source ->
                            item {
                                Text(source.sourceName, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    text = listOfNotNull(
                                        source.scriptType?.let { "Script: $it" },
                                        source.rawSource?.let { "Raw source: $it" },
                                        source.validationStatus?.let { "Validation: $it" },
                                    ).joinToString(" • "),
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    }
                    section("No-modification statement") {
                        item {
                            Text(uiState.noModificationStatement.orEmpty(), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    section("Validation status") {
                        item {
                            Text("Validation rows: ${uiState.validationRowCount}")
                            Text("Failed validation rows: ${uiState.failedValidationRowCount}")
                            Text("Content source rows: ${uiState.contentSourceCount}")
                        }
                    }
                    section("Privacy pledge") {
                        item {
                            Text(
                                uiState.privacyPledge.orEmpty(),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                    section("Integrity placeholders") {
                        items(uiState.appContentIntegrityPlaceholders) { placeholder ->
                            Text("• $placeholder", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    section("Claims not made") {
                        items(uiState.claimsNotMade) { claim ->
                            Text("• $claim", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

private inline fun LazyListScope.section(
    title: String,
    content: LazyListScope.() -> Unit,
) {
    item {
        Text(title, style = MaterialTheme.typography.titleLarge)
    }
    content()
}
