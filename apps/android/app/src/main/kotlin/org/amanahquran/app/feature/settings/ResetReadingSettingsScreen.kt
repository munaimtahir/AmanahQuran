package org.amanahquran.app.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import org.amanahquran.app.core.repository.readerSettingsRepository
import org.amanahquran.app.core.theme.AmanahSpacing
import org.amanahquran.app.core.theme.LocalElderMode
import org.amanahquran.app.core.ui.AmanahOutlinedButton
import org.amanahquran.app.core.ui.AmanahPrimaryButton
import org.amanahquran.app.core.ui.AmanahSectionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetReadingSettingsScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val settingsRepository = remember(context) { readerSettingsRepository(context) }
    val scope = rememberCoroutineScope()
    val elder = LocalElderMode.current
    val horizontalPadding = if (elder) AmanahSpacing.screenHorizontalPaddingElder else AmanahSpacing.screenHorizontalPadding
    var didReset by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Reset Reading Settings", style = MaterialTheme.typography.titleLarge) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = horizontalPadding, vertical = AmanahSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(AmanahSpacing.lg),
        ) {
            if (didReset) {
                AmanahSectionCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.xs)) {
                        Icon(Icons.Rounded.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            "Reading settings have been reset",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
                AmanahOutlinedButton(text = "Done", onClick = onNavigateBack, modifier = Modifier.fillMaxWidth())
                return@Column
            }

            Icon(
                imageVector = Icons.Rounded.RestartAlt,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "This will reset your reading display preferences to their defaults:",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            AmanahSectionCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.xs)) {
                    listOf(
                        "Quran and translation text size",
                        "Line spacing and reading margins",
                        "Default reader mode and auto-scroll speed",
                        "Reader header format",
                        "Keep-screen-awake and full-screen defaults",
                    ).forEach { line ->
                        Text(
                            text = "• $line",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            Text(
                text = "Your bookmarks, last-read position, reading history, streak, and the Quran text itself are never affected.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))

            AmanahPrimaryButton(
                text = "Reset Reading Settings",
                onClick = {
                    scope.launch {
                        settingsRepository.resetReaderPreferences()
                        didReset = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
            AmanahOutlinedButton(text = "Cancel", onClick = onNavigateBack, modifier = Modifier.fillMaxWidth())
        }
    }
}
