package org.amanahquran.app.feature.daily

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.amanahquran.app.core.daily.DailyAyahRecord
import org.amanahquran.app.core.daily.dailyAyahRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyAyahHistoryScreen(onNavigateBack: () -> Unit, onOpenAyah: (String) -> Unit) {
    val context = LocalContext.current
    var records by remember { mutableStateOf<List<DailyAyahRecord>>(emptyList()) }
    LaunchedEffect(Unit) { records = dailyAyahRepository(context).history() }
    Scaffold(topBar = { TopAppBar(title = { Text("Daily Ayah History") }, navigationIcon = { androidx.compose.material3.IconButton(onClick = onNavigateBack) { Text("‹") } }) }) { padding ->
        if (records.isEmpty()) {
            Text("Daily Ayahs will appear here as they are opened.", Modifier.padding(padding).padding(24.dp))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                verticalArrangement = Arrangement.spacedBy(1.dp),
            ) {
                items(records, key = { it.date.toString() }) { record ->
                    ListItem(
                        modifier = Modifier.fillMaxWidth().clickable { onOpenAyah(record.ayahKey) },
                        headlineContent = { Text(record.ayahKey) },
                        supportingContent = { Text("${record.date} · ${record.selectionMode.name.lowercase()}") },
                    )
                }
            }
        }
    }
}
