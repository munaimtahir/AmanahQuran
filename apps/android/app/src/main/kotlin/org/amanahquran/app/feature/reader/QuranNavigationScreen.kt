package org.amanahquran.app.feature.reader

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranNavigationScreen(
    onNavigateBack: () -> Unit,
    onOpenSurahList: () -> Unit,
    onOpenJuzList: () -> Unit,
    onOpenPageList: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quran Navigation") },
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) {
                        Text("Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            NavigationCard(
                title = "Browse by Surah",
                subtitle = "Open the full Surah list.",
                buttonLabel = "Open Surah List",
                onClick = onOpenSurahList,
            )
            NavigationCard(
                title = "Browse by Juz",
                subtitle = "Open Juz 1 through Juz 30 offline.",
                buttonLabel = "Open Juz List",
                onClick = onOpenJuzList,
            )
            NavigationCard(
                title = "Browse by Page",
                subtitle = "Open page navigation by the selected reference layout.",
                buttonLabel = "Open Page List",
                onClick = onOpenPageList,
            )
        }
    }
}

@Composable
private fun NavigationCard(
    title: String,
    subtitle: String,
    buttonLabel: String,
    onClick: () -> Unit,
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium)
            Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
                Text(buttonLabel)
            }
        }
    }
}
