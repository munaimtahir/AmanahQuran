package org.amanahquran.app.feature.reminder

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import org.amanahquran.app.core.theme.AmanahSpacing
import org.amanahquran.app.core.theme.LocalElderMode
import org.amanahquran.app.core.ui.AmanahCard
import org.amanahquran.app.core.ui.AmanahDivider
import org.amanahquran.app.core.ui.AmanahScriptChip
import org.amanahquran.app.core.ui.AmanahSectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingReminderScreen(
    onNavigateBack: () -> Unit,
    viewModel: ReadingReminderViewModel = viewModel(factory = ReadingReminderViewModel.factory(LocalContext.current)),
) {
    val uiState by viewModel.uiState.collectAsState()
    val elder = LocalElderMode.current
    val horizontalPadding = if (elder) AmanahSpacing.screenHorizontalPaddingElder else AmanahSpacing.screenHorizontalPadding
    val settings = uiState.settings
    val context = LocalContext.current

    var showTimePicker by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted -> viewModel.setEnabled(enabled = true, permissionGranted = granted) }

    val requestEnable: () -> Unit = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !ReminderNotifications.hasPermission(context)) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            viewModel.setEnabled(enabled = true, permissionGranted = true)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Reading Reminder", style = MaterialTheme.typography.titleLarge) },
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
                AmanahCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.md)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Enable reminder", style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    "A gentle daily notification to read Quran",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Switch(
                                checked = settings.enabled,
                                onCheckedChange = { enabled ->
                                    if (enabled) requestEnable() else viewModel.setEnabled(false, true)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                                ),
                            )
                        }
                        if (uiState.notificationPermissionDenied) {
                            Text(
                                text = "Notification permission was not granted, so the reminder can't be shown. You can allow it from system settings.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }
            }

            if (settings.enabled) {
                item {
                    AmanahCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { showTimePicker = true },
                        onClickLabel = "Change reminder time",
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("Reminder time", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                text = LocalTime.of(settings.hour, settings.minute)
                                    .format(DateTimeFormatter.ofPattern("h:mm a")),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }

                item {
                    val locale = androidx.compose.ui.platform.LocalConfiguration.current.locales[0]
                    Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
                        AmanahSectionHeader(title = "Repeat on")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.xs),
                        ) {
                            DayOfWeek.entries.forEach { day ->
                                AmanahScriptChip(
                                    label = day.getDisplayName(TextStyle.NARROW, locale),
                                    selected = day in settings.repeatDays,
                                    onClick = {
                                        val updated = if (day in settings.repeatDays) {
                                            settings.repeatDays - day
                                        } else {
                                            settings.repeatDays + day
                                        }
                                        viewModel.setRepeatDays(updated)
                                    },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                }

                item {
                    AmanahCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Smart Reminder", style = MaterialTheme.typography.bodyLarge)
                                    Text(
                                        "Skip the reminder on days you've already read",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                Switch(
                                    checked = settings.smartReminderEnabled,
                                    onCheckedChange = viewModel::setSmartReminderEnabled,
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                                    ),
                                )
                            }
                            AmanahDivider()
                            Column {
                                Text(
                                    "Preview",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    org.amanahquran.app.core.model.ReminderSettings.NOTIFICATION_TITLE,
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                                Text(
                                    org.amanahquran.app.core.model.ReminderSettings.NOTIFICATION_BODY,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = settings.hour,
            initialMinute = settings.minute,
            is24Hour = false,
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.setTime(timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) { Text("Set") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            },
            text = { TimePicker(state = timePickerState) },
        )
    }
}
