package org.amanahquran.app.feature.settings

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import org.amanahquran.app.core.model.AutoScrollPace
import org.amanahquran.app.core.model.ReaderContentMode
import org.amanahquran.app.core.model.ReaderHeaderFormat
import org.amanahquran.app.core.repository.ReaderSettings
import org.amanahquran.app.core.repository.readerSettingsRepository
import org.amanahquran.app.core.theme.AmanahSpacing
import org.amanahquran.app.core.theme.LocalElderMode
import org.amanahquran.app.core.ui.AmanahCard
import org.amanahquran.app.core.ui.AmanahDivider
import org.amanahquran.app.core.ui.AmanahScriptChip
import org.amanahquran.app.core.ui.AmanahSectionCard
import org.amanahquran.app.core.ui.AmanahSectionHeader
import org.amanahquran.app.core.ui.AmanahSettingsRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedReaderSettingsScreen(
    onNavigateBack: () -> Unit,
    onOpenResetReadingSettings: () -> Unit,
) {
    val context = LocalContext.current
    val settingsRepository = remember(context) { readerSettingsRepository(context) }
    val settings by settingsRepository.settings.collectAsState(initial = ReaderSettings())
    val scope = rememberCoroutineScope()
    val elder = LocalElderMode.current
    val horizontalPadding = if (elder) AmanahSpacing.screenHorizontalPaddingElder else AmanahSpacing.screenHorizontalPadding

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Advanced Reader Settings", style = MaterialTheme.typography.titleLarge) },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = horizontalPadding),
            contentPadding = PaddingValues(vertical = AmanahSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(AmanahSpacing.lg),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
                    AmanahSectionHeader(title = "Reading")
                    AmanahCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.md)) {
                            Text("Default reader mode", style = MaterialTheme.typography.bodyLarge)
                            Row(horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
                                AmanahScriptChip(
                                    label = "Ayah by Ayah",
                                    selected = settings.readerContentMode == ReaderContentMode.AYAH,
                                    onClick = { scope.launch { settingsRepository.setReaderContentMode(ReaderContentMode.AYAH) } },
                                    modifier = Modifier.weight(1f),
                                )
                                AmanahScriptChip(
                                    label = "Continuous",
                                    selected = settings.readerContentMode == ReaderContentMode.CONTINUOUS,
                                    onClick = { scope.launch { settingsRepository.setReaderContentMode(ReaderContentMode.CONTINUOUS) } },
                                    modifier = Modifier.weight(1f),
                                )
                            }

                            AmanahDivider()

                            Text("Auto-scroll default speed", style = MaterialTheme.typography.bodyLarge)
                            AutoScrollPaceRow(
                                selected = settings.autoScrollPace,
                                onSelect = { pace -> scope.launch { settingsRepository.setAutoScrollPace(pace) } },
                            )

                            AmanahDivider()

                            Text("Reader header", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                text = "What shows in the reader's top bar while you read",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            HeaderFormatOptions(
                                selected = settings.readerHeaderFormat,
                                onSelect = { format -> scope.launch { settingsRepository.setReaderHeaderFormat(format) } },
                            )
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
                    AmanahSectionHeader(title = "Display")
                    AmanahCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.md)) {
                            SwitchRow(
                                title = "Keep screen awake",
                                subtitle = "Prevent the screen from sleeping while reading",
                                checked = settings.keepScreenAwakeEnabled,
                                onCheckedChange = { enabled -> scope.launch { settingsRepository.setKeepScreenAwakeEnabled(enabled) } },
                            )
                            AmanahDivider()
                            SwitchRow(
                                title = "Full-screen reading",
                                subtitle = "Open the Mushaf page reader in full-screen by default",
                                checked = settings.fullScreenReadingDefault,
                                onCheckedChange = { enabled -> scope.launch { settingsRepository.setFullScreenReadingDefault(enabled) } },
                            )
                        }
                    }
                }
            }

            item {
                AmanahSectionCard(modifier = Modifier.fillMaxWidth()) {
                    AmanahSettingsRow(
                        title = "Reset Reading Settings",
                        subtitle = "Restore these preferences to their defaults",
                        onClick = onOpenResetReadingSettings,
                        trailing = {
                            Icon(
                                imageVector = Icons.Rounded.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun SwitchRow(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
            ),
        )
    }
}

@Composable
private fun AutoScrollPaceRow(selected: AutoScrollPace, onSelect: (AutoScrollPace) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(androidx.compose.foundation.rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.xs),
    ) {
        AutoScrollPace.entries.forEach { pace ->
            AmanahScriptChip(
                label = pace.label,
                selected = pace == selected,
                onClick = { onSelect(pace) },
            )
        }
    }
}

@Composable
private fun HeaderFormatOptions(selected: ReaderHeaderFormat, onSelect: (ReaderHeaderFormat) -> Unit) {
    val options = listOf(
        ReaderHeaderFormat.PAGE_ONLY to "Page only",
        ReaderHeaderFormat.SURAH_PAGE to "Surah + Page",
        ReaderHeaderFormat.JUZ_PAGE to "Juz + Page",
        ReaderHeaderFormat.SURAH_JUZ_PAGE to "Surah + Juz + Page",
    )
    Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.xs)) {
        options.forEach { (format, label) ->
            AmanahScriptChip(
                label = label,
                selected = format == selected,
                onClick = { onSelect(format) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
