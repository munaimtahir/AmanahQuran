package org.amanahquran.app.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onOpenReader: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenTrustCenter: () -> Unit,
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text("Amanah Quran", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Android V1 Sacred Reader for offline Quran reading from verified packaged content.",
                style = MaterialTheme.typography.bodyLarge,
            )

            Button(onClick = onOpenReader, modifier = Modifier.fillMaxWidth()) {
                Text("Open Reader")
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
