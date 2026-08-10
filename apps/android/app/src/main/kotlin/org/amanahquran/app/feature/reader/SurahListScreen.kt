package org.amanahquran.app.feature.reader

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.amanahquran.app.core.theme.AmanahSpacing
import org.amanahquran.app.core.theme.LocalElderMode
import org.amanahquran.app.core.ui.AmanahDivider
import org.amanahquran.app.core.ui.AmanahNumberBadge

/**
 * The database stores revelation place as a raw lowercase enum ("makkah"/"madinah").
 * Map it to the standard English terms rather than leaking the raw value to the UI.
 */
private fun String.toRevelationPlaceLabel(): String = when (lowercase()) {
    "makkah", "makkan", "meccan", "mecca" -> "Makkan"
    "madinah", "madinan", "medinan", "medina" -> "Madinan"
    else -> replaceFirstChar { it.uppercase() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahListScreen(
    onOpenSurah: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: SurahListViewModel = viewModel(
        factory = SurahListViewModel.factory(LocalContext.current),
    ),
) {
    val uiState by viewModel.uiState.collectAsState()
    val elder = LocalElderMode.current
    val horizontalPadding = if (elder) AmanahSpacing.screenHorizontalPaddingElder else AmanahSpacing.screenHorizontalPadding

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Surahs", style = MaterialTheme.typography.titleLarge) },
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
                    Text("Unable to load Quran", style = MaterialTheme.typography.titleMedium)
                    Text(uiState.errorMessage.orEmpty(), style = MaterialTheme.typography.bodyMedium)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = AmanahSpacing.sm),
                ) {
                    items(uiState.surahs, key = { it.surahNumber }) { surah ->
                        SurahListRow(
                            surah = surah,
                            onClick = { viewModel.openSurah(surah.surahNumber, onOpenSurah) },
                        )
                        AmanahDivider(modifier = Modifier.padding(horizontal = AmanahSpacing.sm))
                    }
                }
            }
        }
    }
}

@Composable
private fun SurahListRow(
    surah: SurahListItem,
    onClick: () -> Unit,
) {
    val elder = LocalElderMode.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = if (elder) 76.dp else 64.dp)
            .clickable(onClickLabel = "Open ${surah.simpleName}", onClick = onClick)
            .padding(vertical = AmanahSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AmanahNumberBadge(number = surah.surahNumber)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = surah.simpleName,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = buildString {
                    append("${surah.ayahCount} ayahs")
                    surah.revelationType?.takeIf { it.isNotBlank() }
                        ?.let { append(" · ${it.toRevelationPlaceLabel()}") }
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = surah.arabicName,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
        )
    }
}
