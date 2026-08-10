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
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import kotlinx.coroutines.launch
import org.amanahquran.app.core.model.ReaderContentMode
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.repository.ReaderSettings
import org.amanahquran.app.core.repository.readerSettingsRepository
import org.amanahquran.app.core.repository.bookmarkRepository
import org.amanahquran.app.core.repository.bookmarkCollectionRepository
import org.amanahquran.app.core.repository.lastReadRepository
import org.amanahquran.app.core.backup.UserBackupPayload
import org.amanahquran.app.core.backup.UserBackupService
import org.amanahquran.app.core.theme.AmanahSpacing
import org.amanahquran.app.core.theme.LocalElderMode
import org.amanahquran.app.core.theme.ThemeMode
import org.amanahquran.app.core.ui.AmanahCard
import org.amanahquran.app.core.ui.AmanahDivider
import org.amanahquran.app.core.ui.AmanahSectionCard
import org.amanahquran.app.core.ui.AmanahSectionHeader
import org.amanahquran.app.core.ui.AmanahScriptChip
import org.amanahquran.app.core.ui.AmanahSettingsRow
import org.amanahquran.app.core.ui.AmanahSlider

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
    var pendingRestore by remember { mutableStateOf<UserBackupPayload?>(null) }
    var backupMessage by remember { mutableStateOf<String?>(null) }
    val backupService = remember(context) {
        UserBackupService(context, bookmarkRepository(context), bookmarkCollectionRepository(context), settingsRepository, lastReadRepository(context))
    }
    val createBackup = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) scope.launch { runCatching { backupService.write(uri, backupService.encodeCurrent()) }.onSuccess { backupMessage = "Backup saved" }.onFailure { backupMessage = "Backup failed: ${it.message}" } }
    }
    val openBackup = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) runCatching { backupService.read(uri) }.onSuccess { pendingRestore = it }.onFailure { backupMessage = "Backup rejected: ${it.message}" }
    }
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

            // READER-UX-02: Reading mode -- Ayah Mode's existing one-verse-per-row layout, or the
            // new book-style Continuous Mode. Purely a rendering choice, so switching it never
            // reloads or re-fetches Quran content.
            Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
                AmanahSectionHeader(title = "Reading Mode")
                AmanahCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
                        Text(
                            text = "How the Quran text is laid out",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
                            AmanahScriptChip(
                                label = "Ayah-by-Ayah",
                                selected = settings.readerContentMode == ReaderContentMode.AYAH,
                                onClick = {
                                    scope.launch { settingsRepository.setReaderContentMode(ReaderContentMode.AYAH) }
                                },
                            )
                            AmanahScriptChip(
                                label = "Continuous Reading",
                                selected = settings.readerContentMode == ReaderContentMode.CONTINUOUS,
                                onClick = {
                                    scope.launch { settingsRepository.setReaderContentMode(ReaderContentMode.CONTINUOUS) }
                                },
                            )
                        }
                        if (settings.readerContentMode == ReaderContentMode.CONTINUOUS && settings.translationEnabled) {
                            Text(
                                text = "Translation shows side-by-side with the Quran in Continuous Mode.",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                        val themeChip: @Composable (ThemeMode) -> Unit = { mode ->
                            AmanahScriptChip(
                                label = mode.displayName,
                                selected = settings.selectedTheme == mode,
                                onClick = { scope.launch { settingsRepository.setSelectedTheme(mode) } },
                                modifier = Modifier.weight(1f),
                            )
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.sm),
                            ) {
                                themeChip(ThemeMode.SYSTEM)
                                themeChip(ThemeMode.LIGHT)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.sm),
                            ) {
                                themeChip(ThemeMode.DARK)
                                themeChip(ThemeMode.SEPIA)
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
                    AmanahSlider(
                        value = settings.arabicFontSizeSp,
                        onValueChange = { value ->
                            scope.launch { settingsRepository.setArabicFontSize(value) }
                        },
                        valueRange = 18f..36f,
                        modifier = Modifier.fillMaxWidth(),
                        accessibilityLabel = "Arabic font size",
                        accessibilityValue = "${settings.arabicFontSizeSp.toInt()} sp",
                    )
                }
            }

            // Accessibility & Reading Options
            Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
                AmanahSectionHeader(title = "Reading Comfort")
                AmanahCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.md)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text("Arabic line spacing", style = MaterialTheme.typography.bodyMedium)
                            Text("${settings.arabicLineSpacingMultiplier}×", style = MaterialTheme.typography.labelLarge)
                        }
                        AmanahSlider(
                            value = settings.arabicLineSpacingMultiplier,
                            onValueChange = { value -> scope.launch { settingsRepository.setArabicLineSpacing(value) } },
                            valueRange = 1.5f..2.4f,
                            modifier = Modifier.fillMaxWidth(),
                            accessibilityLabel = "Arabic line spacing",
                            accessibilityValue = "${settings.arabicLineSpacingMultiplier} times",
                        )
                        AmanahDivider()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text("Reading margins", style = MaterialTheme.typography.bodyMedium)
                            Text("${settings.readerHorizontalPaddingDp.toInt()} dp", style = MaterialTheme.typography.labelLarge)
                        }
                        AmanahSlider(
                            value = settings.readerHorizontalPaddingDp,
                            onValueChange = { value -> scope.launch { settingsRepository.setReaderHorizontalPadding(value) } },
                            valueRange = 8f..32f,
                            modifier = Modifier.fillMaxWidth(),
                            accessibilityLabel = "Reading margins",
                            accessibilityValue = "${settings.readerHorizontalPaddingDp.toInt()} dp",
                        )
                    }
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
                                modifier = Modifier.semantics {
                                    contentDescription = "Elder Mode"
                                },
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
                                    text = "Page Mode",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Text(
                                    text = "Fits a full page on screen, swipe left/right to turn pages, pinch to zoom. Off: scroll continuously instead.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Switch(
                                checked = settings.bookModeEnabled,
                                modifier = Modifier.semantics {
                                    contentDescription = "Page Mode"
                                },
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

            Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
                AmanahSectionHeader(title = "Urdu Translation")
                AmanahCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.md)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Show Urdu translation", style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    "Muhammad Junagarhi · QuranEnc · offline",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Switch(
                                checked = settings.translationEnabled,
                                onCheckedChange = { enabled ->
                                    scope.launch { settingsRepository.setTranslationEnabled(enabled) }
                                },
                            )
                        }
                        if (settings.translationEnabled) {
                            AmanahDivider()
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text("Urdu font size", style = MaterialTheme.typography.bodyMedium)
                                Text("${settings.translationFontSizeSp.toInt()} sp", style = MaterialTheme.typography.labelLarge)
                            }
                            AmanahSlider(
                                value = settings.translationFontSizeSp,
                                onValueChange = { value ->
                                    scope.launch { settingsRepository.setTranslationFontSize(value) }
                                },
                                valueRange = 14f..30f,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }

            AmanahDivider()

            Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
                AmanahSectionHeader(title = "Local Backup")
                AmanahCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
                        Text("Bookmarks, collections, settings and last-read position stay on this device.", style = MaterialTheme.typography.bodyMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
                            TextButton(onClick = { createBackup.launch("amanah-quran-backup.json") }) { Text("Export") }
                            TextButton(onClick = { openBackup.launch(arrayOf("application/json", "text/*")) }) { Text("Import") }
                        }
                        backupMessage?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
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

    pendingRestore?.let { payload ->
        AlertDialog(
            onDismissRequest = { pendingRestore = null },
            title = { Text("Restore local backup?") },
            text = { Text("This backup contains ${payload.bookmarks.size} bookmarks and ${if (payload.lastRead != null) "a last-read position" else "no last-read position"}. Current local data will be replaced.") },
            confirmButton = { TextButton(onClick = { scope.launch { runCatching { backupService.restore(payload) }.onSuccess { backupMessage = "Backup restored" }.onFailure { backupMessage = "Restore failed: ${it.message}" }; pendingRestore = null } }) { Text("Restore") } },
            dismissButton = { TextButton(onClick = { pendingRestore = null }) { Text("Cancel") } },
        )
    }
}
