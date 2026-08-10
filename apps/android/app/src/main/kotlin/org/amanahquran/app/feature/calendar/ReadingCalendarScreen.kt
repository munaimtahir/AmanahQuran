package org.amanahquran.app.feature.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
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
import org.amanahquran.app.core.model.DailyReadingActivity
import org.amanahquran.app.core.theme.AmanahSpacing
import org.amanahquran.app.core.theme.LocalElderMode
import org.amanahquran.app.core.ui.AmanahSectionCard
import org.amanahquran.app.core.util.CalendarDay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingCalendarScreen(
    onNavigateBack: () -> Unit,
    viewModel: ReadingCalendarViewModel = viewModel(factory = ReadingCalendarViewModel.factory(LocalContext.current)),
) {
    val uiState by viewModel.uiState.collectAsState()
    val elder = LocalElderMode.current
    val horizontalPadding = if (elder) AmanahSpacing.screenHorizontalPaddingElder else AmanahSpacing.screenHorizontalPadding
    val locale = LocalConfiguration.current.locales[0]

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Reading History", style = MaterialTheme.typography.titleLarge) },
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
                MonthHeader(
                    label = uiState.yearMonth.month.getDisplayName(TextStyle.FULL, locale) + " " + uiState.yearMonth.year,
                    onPrevious = viewModel::previousMonth,
                    onNext = viewModel::nextMonth,
                )
            }
            item {
                CalendarGrid(
                    days = uiState.days,
                    today = uiState.today,
                    qualifyingDates = uiState.qualifyingDates,
                    selectedDate = uiState.selectedDate,
                    onSelectDate = viewModel::selectDate,
                    locale = locale,
                )
            }
            uiState.selectedDate?.let { date ->
                item {
                    DayDetailCard(
                        date = date,
                        activity = uiState.selectedDayActivity,
                        qualified = date in uiState.qualifyingDates,
                        locale = locale,
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthHeader(label: String, onPrevious: () -> Unit, onNext: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.Rounded.ChevronLeft, contentDescription = "Previous month")
        }
        Text(text = label, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
        IconButton(onClick = onNext) {
            Icon(Icons.Rounded.ChevronRight, contentDescription = "Next month")
        }
    }
}

@Composable
private fun CalendarGrid(
    days: List<CalendarDay>,
    today: java.time.LocalDate,
    qualifyingDates: Set<java.time.LocalDate>,
    selectedDate: java.time.LocalDate?,
    onSelectDate: (java.time.LocalDate) -> Unit,
    locale: java.util.Locale,
) {
    Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.xs)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            java.time.DayOfWeek.entries.let { all ->
                // Sunday-first order to match CalendarGridBuilder's default week start.
                listOf(
                    java.time.DayOfWeek.SUNDAY, java.time.DayOfWeek.MONDAY, java.time.DayOfWeek.TUESDAY,
                    java.time.DayOfWeek.WEDNESDAY, java.time.DayOfWeek.THURSDAY, java.time.DayOfWeek.FRIDAY,
                    java.time.DayOfWeek.SATURDAY,
                )
            }.forEach { day ->
                Text(
                    text = day.getDisplayName(TextStyle.NARROW, locale),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        days.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { day ->
                    CalendarDayCell(
                        day = day,
                        isToday = day.date == today,
                        isQualified = day.date in qualifyingDates,
                        isSelected = day.date == selectedDate,
                        onClick = { onSelectDate(day.date) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    day: CalendarDay,
    isToday: Boolean,
    isQualified: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentAlpha = if (day.inCurrentMonth) 1f else 0.35f
    val fill = when {
        isQualified -> MaterialTheme.colorScheme.primary.copy(alpha = if (day.inCurrentMonth) 1f else 0.4f)
        else -> androidx.compose.ui.graphics.Color.Transparent
    }
    val textColor = if (isQualified) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val borderColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isToday -> MaterialTheme.colorScheme.outline
        else -> androidx.compose.ui.graphics.Color.Transparent
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clickable(onClickLabel = day.date.toString(), onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
                .background(color = fill, shape = CircleShape)
                .border(width = if (isSelected || isToday) 2.dp else 0.dp, color = borderColor, shape = CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = day.date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = textColor.copy(alpha = contentAlpha),
            )
        }
    }
}

@Composable
private fun DayDetailCard(
    date: java.time.LocalDate,
    activity: DailyReadingActivity?,
    qualified: Boolean,
    locale: java.util.Locale,
) {
    AmanahSectionCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.xs)) {
            Text(
                text = date.month.getDisplayName(TextStyle.FULL, locale) + " ${date.dayOfMonth}, ${date.year}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (activity == null) {
                Text(
                    text = "No reading activity",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Text(
                    text = "${activity.readingDurationSeconds / 60} min · ${activity.uniqueAyahsRead} ayahs · ${activity.pagesReadCount} pages",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = if (qualified) "Counted toward your reading streak" else "Did not qualify as a reading day",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
