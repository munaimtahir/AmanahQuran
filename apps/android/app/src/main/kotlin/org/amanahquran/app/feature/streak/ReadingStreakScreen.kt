package org.amanahquran.app.feature.streak

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.format.TextStyle
import org.amanahquran.app.core.theme.AmanahSpacing
import org.amanahquran.app.core.theme.LocalElderMode
import org.amanahquran.app.core.ui.AmanahSectionCard
import org.amanahquran.app.core.ui.AmanahSectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingStreakScreen(
    onNavigateBack: () -> Unit,
    onOpenCalendar: () -> Unit,
    viewModel: ReadingStreakViewModel = viewModel(factory = ReadingStreakViewModel.factory(LocalContext.current)),
) {
    val uiState by viewModel.uiState.collectAsState()
    val elder = LocalElderMode.current
    val horizontalPadding = if (elder) AmanahSpacing.screenHorizontalPaddingElder else AmanahSpacing.screenHorizontalPadding

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Reading Streak", style = MaterialTheme.typography.titleLarge) },
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
                StreakHeadlineCard(
                    currentStreak = uiState.summary.currentStreak,
                    readToday = uiState.summary.readToday,
                )
            }
            item {
                WeekHistoryCard(days = uiState.last7Days)
            }
            item {
                StatsRow(
                    longestStreak = uiState.summary.longestStreak,
                    totalDays = uiState.summary.totalQualifyingDays,
                )
            }
            item {
                AmanahSectionCard(modifier = Modifier.fillMaxWidth()) {
                    org.amanahquran.app.core.ui.AmanahSettingsRow(
                        title = "Reading History Calendar",
                        subtitle = "See past reading days on a calendar",
                        onClick = onOpenCalendar,
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
            item {
                QualificationExplainerCard()
            }
            item {
                PrivacyNoteCard()
            }
        }
    }
}

@Composable
private fun StreakHeadlineCard(currentStreak: Int, readToday: Boolean) {
    AmanahSectionCard(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.xs)) {
                Icon(
                    imageVector = Icons.Rounded.LocalFireDepartment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = if (currentStreak > 0) {
                        "$currentStreak-day reading streak"
                    } else {
                        "Start your reading streak today"
                    },
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(Modifier.height(AmanahSpacing.xs))
            Text(
                text = if (readToday) "Read today ✓" else "Continue today",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun WeekHistoryCard(days: List<DayHistoryUiModel>) {
    Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
        AmanahSectionHeader(title = "This week")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.xs),
        ) {
            days.forEach { day ->
                DayDot(day = day, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun DayDot(day: DayHistoryUiModel, modifier: Modifier = Modifier) {
    val fill = when {
        day.qualified -> MaterialTheme.colorScheme.primary
        day.isToday -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }
    val border = if (day.isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    val locale = LocalConfiguration.current.locales[0]
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AmanahSpacing.xs),
    ) {
        Text(
            text = day.date.dayOfWeek.getDisplayName(TextStyle.NARROW, locale),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxWidth()
                .padding(2.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = fill, shape = CircleShape)
                    .border(
                        width = if (day.isToday) 2.dp else 1.dp,
                        color = border,
                        shape = CircleShape,
                    ),
            )
        }
    }
}

@Composable
private fun StatsRow(longestStreak: Int, totalDays: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.sm),
    ) {
        StatTile(label = "Longest streak", value = "$longestStreak days", modifier = Modifier.weight(1f))
        StatTile(label = "Total reading days", value = "$totalDays days", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    AmanahSectionCard(modifier = modifier) {
        Column {
            Text(text = value, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun QualificationExplainerCard() {
    AmanahSectionCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "A reading day counts when you read at least 3 ayahs or spend 2 minutes reading.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun PrivacyNoteCard() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.sm),
    ) {
        Icon(
            imageVector = Icons.Rounded.Lock,
            contentDescription = null,
            modifier = Modifier.padding(top = 2.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = "Reading activity is stored only on this device.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
