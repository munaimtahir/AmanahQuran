package org.amanahquran.app.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.repository.ReaderSettings
import org.amanahquran.app.core.repository.readerSettingsRepository
import org.amanahquran.app.core.theme.AmanahSpacing
import org.amanahquran.app.core.theme.LocalElderMode
import org.amanahquran.app.core.theme.ThemeMode
import org.amanahquran.app.core.ui.AmanahCard
import org.amanahquran.app.core.ui.AmanahDivider
import org.amanahquran.app.core.ui.AmanahSectionCard
import org.amanahquran.app.core.ui.AmanahSectionHeader
import org.amanahquran.app.core.ui.AmanahScriptChip
import org.amanahquran.app.core.ui.AmanahSettingsRow

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onOpenTrustCenter: () -> Unit,
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
                title = { Text("Settings", style = MaterialTheme.typography.titleLarge) },
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = horizontalPadding)
                .padding(vertical = AmanahSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(AmanahSpacing.xl),
        ) {

            // Script
            Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
                AmanahSectionHeader(title = "Script")
                AmanahCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
                        Text(
                            text = "Arabic display script",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.sm),
                            verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm),
                            maxItemsInEachRow = if (elder) 2 else 4,
                        ) {
                            AmanahScriptChip(
                                label = "IndoPak",
                                selected = settings.selectedScript == ScriptType.INDOPAK,
                                onClick = {
                                    scope.launch { settingsRepository.setSelectedScript(ScriptType.INDOPAK) }
                                },
                            )
                            AmanahScriptChip(
                                label = "Uthmani",
                                selected = settings.selectedScript == ScriptType.UTHMANI,
                                onClick = {
                                    scope.launch { settingsRepository.setSelectedScript(ScriptType.UTHMANI) }
                                },
                            )
                        }
                    }
                }
            }

            // Theme
            Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
                AmanahSectionHeader(title = "Theme")
                AmanahCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
                        Text(
                            text = "App colour scheme",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
                            listOf(ThemeMode.SYSTEM, ThemeMode.LIGHT, ThemeMode.DARK, ThemeMode.SEPIA).forEach { mode ->
                                AmanahScriptChip(
                                    label = mode.displayName,
                                    selected = settings.selectedTheme == mode,
                                    onClick = {
                                        scope.launch { settingsRepository.setSelectedTheme(mode) }
                                    },
                                )
                            }
                        }
                    }
                }
            }

            // Font size
            Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
                AmanahSectionHeader(title = "Arabic Font Size")
                AmanahCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Size",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "${settings.arabicFontSizeSp.toInt()} sp",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    Slider(
                        value = settings.arabicFontSizeSp,
                        onValueChange = { value ->
                            scope.launch { settingsRepository.setArabicFontSize(value) }
                        },
                        valueRange = 18f..36f,
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                    )
                }
            }

            // Accessibility & Reading Options
            Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
                AmanahSectionHeader(title = "Accessibility & Reading Options")
                AmanahCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.md)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Elder Mode",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Text(
                                    text = "Larger text and touch targets",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Switch(
                                checked = settings.elderModeEnabled,
                                onCheckedChange = { enabled ->
                                    scope.launch { settingsRepository.setElderModeEnabled(enabled) }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                                ),
                            )
                        }

                        AmanahDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Book Reading Mode",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Text(
                                    text = "Swipe left/right to turn pages",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Switch(
                                checked = settings.bookModeEnabled,
                                onCheckedChange = { enabled ->
                                    scope.launch { settingsRepository.setBookModeEnabled(enabled) }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                                ),
                            )
                        }
                    }
                }
            }

            AmanahDivider()

            // Trust Center
            AmanahSectionCard(
                modifier = Modifier.fillMaxWidth(),
            ) {
                AmanahSettingsRow(
                    title = "Trust Center",
                    subtitle = "Source transparency, privacy pledge, and verification details",
                    icon = Icons.Rounded.VerifiedUser,
                    onClick = onOpenTrustCenter,
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
