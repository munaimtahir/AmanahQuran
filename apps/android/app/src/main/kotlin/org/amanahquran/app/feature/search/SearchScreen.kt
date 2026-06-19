package org.amanahquran.app.feature.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import org.amanahquran.app.core.repository.SearchResultItem
import org.amanahquran.app.core.repository.SearchResultType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onOpenResult: (SearchResultItem) -> Unit,
    viewModel: SearchViewModel = viewModel(
        factory = SearchViewModel.factory(LocalContext.current),
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search") },
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) {
                        Text("Back")
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = uiState.query,
                        onValueChange = viewModel::onQueryChanged,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Search Quran") },
                        placeholder = { Text("Surah, 2:255, Juz 30, Page 1, or Arabic text") },
                        singleLine = true,
                    )
                    Text(
                        text = "Script previews follow the selected reader setting. Arabic search is local and offline.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            if (uiState.isLoading) {
                item {
                    CircularProgressIndicator()
                }
            }

            if (uiState.errorMessage != null) {
                item {
                    Text(uiState.errorMessage.orEmpty(), style = MaterialTheme.typography.bodyMedium)
                }
            }

            if (!uiState.isLoading && uiState.results.isEmpty() && uiState.query.isNotBlank()) {
                item {
                    Text("No results found", style = MaterialTheme.typography.bodyMedium)
                }
            }

            val grouped = uiState.results.groupBy { it.resultType }
            SearchResultType.entries.forEach { type ->
                val results = grouped[type].orEmpty()
                if (results.isNotEmpty()) {
                    item {
                        Text(
                            text = type.name.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                    items(results, key = { it.subtitle + (it.ayahKey ?: "") }) { result ->
                        SearchResultRow(
                            item = result,
                            arabicFontSizeSp = uiState.arabicFontSizeSp,
                            onClick = { onOpenResult(result) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultRow(
    item: SearchResultItem,
    arabicFontSizeSp: Float,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        TextButton(onClick = onClick) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Start,
                )
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                item.previewText?.takeIf { it.isNotBlank() }?.let { preview ->
                    Text(
                        text = preview,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = arabicFontSizeSp.sp),
                        textAlign = TextAlign.Right,
                    )
                }
            }
        }
    }
}
