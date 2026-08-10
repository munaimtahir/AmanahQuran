package org.amanahquran.app.feature.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.format.TextStyle
import org.amanahquran.app.core.theme.AmanahSpacing
import org.amanahquran.app.core.theme.LocalElderMode
import org.amanahquran.app.core.ui.AmanahSectionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingActivityDashboardScreen(
    onNavigateBack: () -> Unit,
    viewModel: ReadingActivityDashboardViewModel = viewModel(
        factory = ReadingActivityDashboardViewModel.factory(LocalContext.current),
    ),
) {
    val uiState by viewModel.uiState.collectAsState()
    val elder = LocalElderMode.current
    val horizontalPadding = if (elder) AmanahSpacing.screenHorizontalPaddingElder else AmanahSpacing.screenHorizontalPadding

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Reading Activity", style = MaterialTheme.typography.titleLarge) },
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
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            val selectedIndex = StatsRange.entries.indexOf(uiState.selectedRange)
            TabRow(selectedTabIndex = selectedIndex) {
                StatsRange.entries.forEach { range ->
                    Tab(
                        selected = range == uiState.selectedRange,
                        onClick = { viewModel.selectRange(range) },
                        text = { Text(range.label()) },
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = horizontalPadding),
                contentPadding = PaddingValues(vertical = AmanahSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(AmanahSpacing.lg),
            ) {
                item {
                    StatsGrid(uiState.stats)
                }
                item {
                    WeeklyBarChart(series = uiState.weeklySeries)
                }
            }
        }
    }
}

private fun StatsRange.label(): String = when (this) {
    StatsRange.WEEK -> "Week"
    StatsRange.MONTH -> "Month"
    StatsRange.ALL_TIME -> "All Time"
}

@Composable
private fun StatsGrid(stats: org.amanahquran.app.core.util.ReadingActivityStats) {
    val totalMinutes = stats.totalReadingSeconds / 60
    Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
            StatTile(label = "Reading time", value = "$totalMinutes min", modifier = Modifier.weight(1f))
            StatTile(label = "Reading days", value = "${stats.readingDays}", modifier = Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
            StatTile(label = "Ayahs read", value = "${stats.ayahsRead}", modifier = Modifier.weight(1f))
            StatTile(label = "Pages read", value = "${stats.pagesRead}", modifier = Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
            StatTile(label = "Current streak", value = "${stats.currentStreak} days", modifier = Modifier.weight(1f))
            StatTile(label = "Longest streak", value = "${stats.longestStreak} days", modifier = Modifier.weight(1f))
        }
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

/** A restrained, single-color bar chart -- no gradients, comparisons, or celebratory motion. */
@Composable
private fun WeeklyBarChart(series: List<Pair<java.time.LocalDate, Long>>) {
    val barColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val locale = LocalConfiguration.current.locales[0]
    val maxSeconds = (series.maxOfOrNull { it.second } ?: 0L).coerceAtLeast(60L)

    Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
        Text(
            text = "Daily reading time this week",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Row(
            modifier = Modifier.fillMaxWidth().height(120.dp),
            horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.xs),
        ) {
            series.forEach { (date, seconds) ->
                Column(
                    modifier = Modifier.weight(1f).fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                ) {
                    val fraction = (seconds.toFloat() / maxSeconds.toFloat()).coerceIn(0f, 1f)
                    Canvas(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        val barHeight = size.height * fraction
                        drawRoundRect(
                            color = trackColor,
                            size = Size(size.width, size.height),
                            cornerRadius = CornerRadius(6f, 6f),
                            style = Fill,
                        )
                        if (barHeight > 0f) {
                            drawRoundRect(
                                color = barColor,
                                topLeft = androidx.compose.ui.geometry.Offset(0f, size.height - barHeight),
                                size = Size(size.width, barHeight),
                                cornerRadius = CornerRadius(6f, 6f),
                                style = Fill,
                            )
                        }
                    }
                    Text(
                        text = date.dayOfWeek.getDisplayName(TextStyle.NARROW, locale),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
