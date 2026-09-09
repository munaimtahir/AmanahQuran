package org.amanahquran.app.feature.reader

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.amanahquran.app.core.model.ReaderContentMode
import org.amanahquran.app.core.model.ReaderZoomLevel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderSettingsBottomSheet(
    onDismiss: () -> Unit,
    zoomLevel: ReaderZoomLevel,
    onSelectZoomLevel: (ReaderZoomLevel) -> Unit,
    translationZoomLevel: ReaderZoomLevel,
    onSelectTranslationZoomLevel: (ReaderZoomLevel) -> Unit,
    contentMode: ReaderContentMode,
    onSetContentMode: (ReaderContentMode) -> Unit,
    hasTranslation: Boolean,
    selectedScript: org.amanahquran.app.core.model.ScriptType,
    onSelectScript: (org.amanahquran.app.core.model.ScriptType) -> Unit,
    translationSelection: org.amanahquran.app.core.model.TranslationSelection,
    onSelectTranslation: (org.amanahquran.app.core.model.TranslationSelection) -> Unit,
    selectedTheme: org.amanahquran.app.core.theme.ThemeMode,
    onSelectTheme: (org.amanahquran.app.core.theme.ThemeMode) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Reader Settings", style = MaterialTheme.typography.titleLarge)

            Text("Arabic Script", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                org.amanahquran.app.core.model.ScriptType.entries.forEach { script ->
                    FilterChip(
                        selected = selectedScript == script,
                        onClick = { onSelectScript(script) },
                        label = { Text(script.name) }
                    )
                }
            }

            Text("Translation", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                org.amanahquran.app.core.model.TranslationSelection.entries.forEach { translation ->
                    FilterChip(
                        selected = translationSelection == translation,
                        onClick = { onSelectTranslation(translation) },
                        label = { Text(translation.name.replace("_", " ")) }
                    )
                }
            }

            Text("Arabic Text Size", style = MaterialTheme.typography.labelLarge)
            Slider(
                value = zoomLevel.ordinal.toFloat(),
                onValueChange = { value ->
                    onSelectZoomLevel(ReaderZoomLevel.entries[value.toInt().coerceIn(0, ReaderZoomLevel.entries.lastIndex)])
                },
                valueRange = 0f..ReaderZoomLevel.entries.lastIndex.toFloat(),
                steps = ReaderZoomLevel.entries.size - 2
            )

            if (hasTranslation) {
                Text("Translation Size", style = MaterialTheme.typography.labelLarge)
                Slider(
                    value = translationZoomLevel.ordinal.toFloat(),
                    onValueChange = { value ->
                        onSelectTranslationZoomLevel(ReaderZoomLevel.entries[value.toInt().coerceIn(0, ReaderZoomLevel.entries.lastIndex)])
                    },
                    valueRange = 0f..ReaderZoomLevel.entries.lastIndex.toFloat(),
                    steps = ReaderZoomLevel.entries.size - 2
                )
            }

            Text("Reading Mode", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = contentMode == ReaderContentMode.CONTINUOUS,
                    onClick = { onSetContentMode(ReaderContentMode.CONTINUOUS) },
                    label = { Text("Continuous") }
                )
                FilterChip(
                    selected = contentMode == ReaderContentMode.AYAH,
                    onClick = { onSetContentMode(ReaderContentMode.AYAH) },
                    label = { Text("Ayah") }
                )
            }

            Text("Theme", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                org.amanahquran.app.core.theme.ThemeMode.entries.forEach { theme ->
                    FilterChip(
                        selected = selectedTheme == theme,
                        onClick = { onSelectTheme(theme) },
                        label = { Text(theme.name) }
                    )
                }
            }
        }
    }
}
