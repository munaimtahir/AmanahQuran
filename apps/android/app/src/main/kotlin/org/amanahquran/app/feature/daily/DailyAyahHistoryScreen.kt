package org.amanahquran.app.feature.daily

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import org.amanahquran.app.core.daily.DailyAyahContent
import org.amanahquran.app.core.daily.dailyAyahRepository
import org.amanahquran.app.core.theme.AmanahSpacing
import org.amanahquran.app.core.theme.LocalElderMode
import org.amanahquran.app.core.ui.AmanahSectionCard
import org.amanahquran.app.core.ui.AmanahEmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyAyahHistoryScreen(onNavigateBack: () -> Unit, onOpenAyah: (String) -> Unit) {
    val context = LocalContext.current
    var historyContents by remember { mutableStateOf<List<DailyAyahContent>?>(null) }
    
    LaunchedEffect(Unit) { 
        historyContents = dailyAyahRepository(context).historyContent()
    }
    
    val elder = LocalElderMode.current
    val horizontalPadding = if (elder) AmanahSpacing.screenHorizontalPaddingElder else AmanahSpacing.screenHorizontalPadding

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { 
            TopAppBar(
                title = { Text("Daily Ayah History", style = MaterialTheme.typography.titleLarge) }, 
                navigationIcon = { 
                    IconButton(onClick = onNavigateBack) { 
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Go back") 
                    } 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                )
            ) 
        }
    ) { padding ->
        val items = historyContents
        if (items == null) {
            // Loading state
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                androidx.compose.material3.CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (items.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                AmanahEmptyState(
                    icon = androidx.compose.material.icons.Icons.Rounded.History,
                    title = "No History Yet",
                    message = "Your daily ayahs will appear here as they are opened."
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = horizontalPadding),
                contentPadding = PaddingValues(vertical = AmanahSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(AmanahSpacing.md),
            ) {
                items(items, key = { it.record.date.toString() }) { content ->
                    DailyAyahHistoryCard(content = content, onClick = { onOpenAyah(content.record.ayahKey) })
                }
            }
        }
    }
}

@Composable
private fun DailyAyahHistoryCard(content: DailyAyahContent, onClick: () -> Unit) {
    val locale = LocalConfiguration.current.locales[0]
    val dateFormatter = remember(locale) { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale) }
    
    AmanahSectionCard(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${content.surahName} ${content.ayahNumber}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = content.record.date.format(dateFormatter),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = content.arabicText,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )
            
            if (content.translationText != null) {
                Text(
                    text = content.translationText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
