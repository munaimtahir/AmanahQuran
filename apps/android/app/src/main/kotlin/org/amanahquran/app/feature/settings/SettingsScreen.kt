package org.amanahquran.app.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.repository.ReaderSettings
import org.amanahquran.app.core.repository.readerSettingsRepository
import org.amanahquran.app.core.theme.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onOpenTrustCenter: () -> Unit,
) {
    val context = LocalContext.current
    val settingsRepository = remember(context) { readerSettingsRepository(context) }
    val settings by settingsRepository.settings.collectAsStateWithLifecycle(initialValue = ReaderSettings())
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            SettingSection(title = "Script") {
                ChipRow(
                    selected = settings.selectedScript,
                    options = listOf(ScriptType.INDOPAK, ScriptType.UTHMANI),
                    label = { it.displayName() },
                    onSelect = { script ->
                        scope.launch { settingsRepository.setSelectedScript(script) }
                    },
                )
            }

            SettingSection(title = "Theme") {
                ChipRow(
                    selected = settings.selectedTheme,
                    options = listOf(ThemeMode.SYSTEM, ThemeMode.LIGHT, ThemeMode.DARK, ThemeMode.SEPIA),
                    label = { it.displayName },
                    onSelect = { theme ->
                        scope.launch { settingsRepository.setSelectedTheme(theme) }
                    },
                )
            }

            SettingSection(title = "Arabic font size") {
                Text(
                    text = "${settings.arabicFontSizeSp.toInt()} sp",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Slider(
                    value = settings.arabicFontSizeSp,
                    onValueChange = { value ->
                        scope.launch { settingsRepository.setArabicFontSize(value) }
                    },
                    valueRange = 18f..36f,
                )
            }

            SettingSection(title = "Elder Mode") {
                RowToggle(
                    title = "Enable Elder Mode",
                    checked = settings.elderModeEnabled,
                    onCheckedChange = { enabled ->
                        scope.launch { settingsRepository.setElderModeEnabled(enabled) }
                    },
                )
            }

            HorizontalDivider()

            SettingSection(title = "Trust Center") {
                Text(
                    text = "Source transparency, privacy pledge, and verification details are available offline.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                TextButton(onClick = onOpenTrustCenter) {
                    Text("Open Trust Center")
                }
            }
        }
    }
}

@Composable
private fun SettingSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        content()
    }
}

@Composable
private fun <T> ChipRow(
    selected: T,
    options: List<T>,
    label: (T) -> String,
    onSelect: (T) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { option ->
            FilterChip(
                selected = selected == option,
                onClick = { onSelect(option) },
                label = { Text(label(option)) },
            )
        }
    }
}

@Composable
private fun RowToggle(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(title)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

private fun ScriptType.displayName(): String = when (this) {
    ScriptType.INDOPAK -> "IndoPak"
    ScriptType.UTHMANI -> "Uthmani"
}

private fun ThemeMode.displayName(): String = when (this) {
    ThemeMode.SYSTEM -> "System"
    ThemeMode.LIGHT -> "Light"
    ThemeMode.DARK -> "Dark"
    ThemeMode.SEPIA -> "Sepia"
}
