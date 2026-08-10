package org.amanahquran.app.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material.icons.automirrored.rounded.ViewList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.amanahquran.app.R
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.model.StreakSummary
import org.amanahquran.app.core.theme.AmanahAccentGoldBg
import org.amanahquran.app.core.theme.AmanahAccentGoldBgDark
import org.amanahquran.app.core.theme.AmanahAccentGoldIcon
import org.amanahquran.app.core.theme.AmanahAccentGoldIconDark
import org.amanahquran.app.core.theme.AmanahAccentPlumBg
import org.amanahquran.app.core.theme.AmanahAccentPlumBgDark
import org.amanahquran.app.core.theme.AmanahAccentPlumIcon
import org.amanahquran.app.core.theme.AmanahAccentPlumIconDark
import org.amanahquran.app.core.theme.AmanahAccentRoseBg
import org.amanahquran.app.core.theme.AmanahAccentRoseBgDark
import org.amanahquran.app.core.theme.AmanahAccentRoseIcon
import org.amanahquran.app.core.theme.AmanahAccentRoseIconDark
import org.amanahquran.app.core.theme.AmanahAccentTealBg
import org.amanahquran.app.core.theme.AmanahAccentTealBgDark
import org.amanahquran.app.core.theme.AmanahAccentTealIcon
import org.amanahquran.app.core.theme.AmanahAccentTealIconDark
import org.amanahquran.app.core.theme.AmanahAccentTerracottaBg
import org.amanahquran.app.core.theme.AmanahAccentTerracottaBgDark
import org.amanahquran.app.core.theme.AmanahAccentTerracottaIcon
import org.amanahquran.app.core.theme.AmanahAccentTerracottaIconDark
import org.amanahquran.app.core.theme.AmanahGoldMuted
import org.amanahquran.app.core.theme.AmanahGoldSoftSurface
import org.amanahquran.app.core.theme.AmanahGreenDarker
import org.amanahquran.app.core.theme.AmanahGreenDeep
import org.amanahquran.app.core.theme.AmanahSageDeep
import org.amanahquran.app.core.theme.AmanahSageOnDark
import org.amanahquran.app.core.theme.AmanahSageSoft
import org.amanahquran.app.core.theme.AmanahSageSoftOnDark
import org.amanahquran.app.core.theme.AmanahShapes
import org.amanahquran.app.core.theme.AmanahSpacing
import org.amanahquran.app.core.theme.DarkAccentGold
import org.amanahquran.app.core.theme.DarkOnSurface
import org.amanahquran.app.core.theme.DarkOnSurfaceVariant
import org.amanahquran.app.core.theme.DarkPrimaryGreen
import org.amanahquran.app.core.theme.DarkReaderSurface
import org.amanahquran.app.core.theme.LocalElderMode
import org.amanahquran.app.core.theme.LocalIsDarkTheme
import org.amanahquran.app.core.ui.AmanahCard
import org.amanahquran.app.core.ui.AmanahPrimaryButton
import org.amanahquran.app.core.ui.AmanahSectionCard
import org.amanahquran.app.core.ui.AmanahSectionHeader
import org.amanahquran.app.core.ui.AmanahTonalButton

@Composable
fun HomeScreen(
    onOpenMushafReader: (Int, ScriptType) -> Unit,
    onContinueReading: (HomeContinueReadingUiModel) -> Unit,
    onOpenSurahList: () -> Unit,
    onOpenJuzList: () -> Unit,
    onOpenPageList: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenTrustCenter: () -> Unit,
    onOpenReadingStreak: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.factory(LocalContext.current)),
) {
    val uiState by viewModel.uiState.collectAsState()
    val elder = LocalElderMode.current
    val horizontalPadding = if (elder) AmanahSpacing.screenHorizontalPaddingElder else AmanahSpacing.screenHorizontalPadding

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = horizontalPadding)
                .padding(top = AmanahSpacing.xl, bottom = AmanahSpacing.section),
            verticalArrangement = Arrangement.spacedBy(AmanahSpacing.xl),
        ) {
            HomeHeader(
                onOpenSettings = onOpenSettings,
                onOpenTrustCenter = onOpenTrustCenter,
            )

            ReadingHeroCard(
                continueReading = uiState.continueReading,
                onContinue = onContinueReading,
                onStartReading = { onOpenMushafReader(1, uiState.selectedScript) },
            )

            StreakLine(
                summary = uiState.streakSummary,
                onClick = onOpenReadingStreak,
            )

            // Extra breathing room beyond the standard xl gap so the hero and the
            // browse section below read as two distinct groups, not one continuous list.
            Spacer(modifier = Modifier.height(AmanahSpacing.md))

            if (elder) {
                ElderQuickActions(
                    onOpenSurahList = onOpenSurahList,
                    onOpenJuzList = onOpenJuzList,
                    onOpenPageList = onOpenPageList,
                    onOpenSearch = onOpenSearch,
                    onOpenBookmarks = onOpenBookmarks,
                )
            } else {
                QuickActionsGrid(
                    onOpenSurahList = onOpenSurahList,
                    onOpenJuzList = onOpenJuzList,
                    onOpenPageList = onOpenPageList,
                    onOpenSearch = onOpenSearch,
                    onOpenBookmarks = onOpenBookmarks,
                )
            }

            TrustSummaryStrip(onOpenTrustCenter = onOpenTrustCenter)

            // Safe bottom padding
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun HomeHeader(
    onOpenSettings: () -> Unit,
    onOpenTrustCenter: () -> Unit,
) {
    val elder = LocalElderMode.current
    val isDark = LocalIsDarkTheme.current
    val iconTouchTarget = if (elder) AmanahSpacing.minTouchTargetElder else AmanahSpacing.minTouchTarget

    // Brand mark sits in the same gold "jewel" tone as the hero card's reading badge below,
    // tying the two together instead of floating as a bare, colorless logo.
    val brandBadgeBackground = if (isDark) DarkAccentGold else AmanahGoldSoftSurface
    val titleColor = if (isDark) DarkPrimaryGreen else AmanahGreenDeep

    // Trust Center reuses the app's existing "calm, not promotional" sage accent (see
    // AmanahSageMuted/Deep/Soft) -- a verification action should feel reassuring, not loud.
    val trustIconTint = if (isDark) AmanahSageOnDark else AmanahSageDeep
    val trustBadgeBackground = if (isDark) AmanahSageSoftOnDark else AmanahSageSoft

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.sm),
        modifier = Modifier.padding(horizontal = AmanahSpacing.xs)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(color = brandBadgeBackground, shape = AmanahShapes.quickActionBadge),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.brand_mark),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Amanah Quran",
                style = MaterialTheme.typography.titleLarge,
                color = titleColor,
            )
            Text(
                text = "Ad-Free · Forever · Offline",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Box(
            modifier = Modifier
                .size(iconTouchTarget)
                .clip(CircleShape)
                .background(trustBadgeBackground),
            contentAlignment = Alignment.Center,
        ) {
            IconButton(onClick = onOpenTrustCenter, modifier = Modifier.size(iconTouchTarget)) {
                Icon(
                    imageVector = Icons.Rounded.VerifiedUser,
                    contentDescription = "Trust Center",
                    tint = trustIconTint,
                )
            }
        }
        Box(
            modifier = Modifier
                .size(iconTouchTarget)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            IconButton(onClick = onOpenSettings, modifier = Modifier.size(iconTouchTarget)) {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/**
 * Single state-driven primary action: "Continue Reading" once there's a last-read
 * position, otherwise "Start Reading". Deliberately styled as a filled, gold-bordered
 * "jewel panel" -- the one rich element on the screen -- rather than a plain outlined
 * card like the browse tiles below it, so it reads as the obvious primary action.
 */
@Composable
private fun ReadingHeroCard(
    continueReading: HomeContinueReadingUiModel?,
    onContinue: (HomeContinueReadingUiModel) -> Unit,
    onStartReading: () -> Unit,
) {
    val isDark = LocalIsDarkTheme.current
    val fill = if (isDark) DarkReaderSurface else AmanahGreenDeep
    val titleColor = if (isDark) DarkOnSurface else Color.White
    val subtitleColor = if (isDark) DarkOnSurfaceVariant else Color.White.copy(alpha = 0.78f)
    val goldTone = if (isDark) DarkAccentGold else AmanahGoldMuted
    val badgeBackground = if (isDark) DarkAccentGold else AmanahGoldSoftSurface

    val title = if (continueReading != null) "Resume Reading" else "Start Reading"
    val subtitle = continueReading?.title
        ?: "Read by Surah, Juz, or Page using offline source-attributed text"
    val buttonLabel = if (continueReading != null) "Continue Reading" else "Open Mushaf Page"
    val onPrimaryAction = { if (continueReading != null) onContinue(continueReading) else onStartReading() }

    AmanahCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onPrimaryAction,
        onClickLabel = buttonLabel,
        containerColor = fill,
        borderColor = goldTone,
        borderWidth = 1.2.dp,
        elevation = 6.dp,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.md),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = badgeBackground,
                        shape = AmanahShapes.numberBadge,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.MenuBook,
                    contentDescription = null,
                    tint = AmanahGreenDarker,
                    modifier = Modifier.size(24.dp),
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = titleColor,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = subtitleColor,
                )
            }
        }
        Spacer(Modifier.height(AmanahSpacing.md))
        AmanahPrimaryButton(
            text = buttonLabel,
            onClick = onPrimaryAction,
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.AutoMirrored.Rounded.MenuBook,
            containerColor = goldTone,
            contentColor = AmanahGreenDarker,
        )
    }
}

/**
 * One calm, compact line -- never a dashboard -- so the streak supports the reading habit
 * without competing with Continue Reading for visual weight.
 */
@Composable
private fun StreakLine(summary: StreakSummary, onClick: () -> Unit) {
    val label = when {
        summary.currentStreak > 0 -> "${summary.currentStreak}-day reading streak"
        else -> "Start your reading streak today"
    }
    val status = when {
        summary.currentStreak <= 0 -> null
        summary.readToday -> "Read today ✓"
        else -> "Continue today"
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClickLabel = "Open Reading Streak", onClick = onClick)
            .padding(horizontal = AmanahSpacing.xs, vertical = AmanahSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.xs),
    ) {
        Icon(
            imageVector = Icons.Rounded.LocalFireDepartment,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (status != null) {
            Text(
                text = status,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * One muted jewel tone per browse category (icon + soft badge background), resolved for
 * light vs. dark so the accent stays legible either way. Color is confined to the small
 * icon badge -- never the card body -- so five distinct hues still read as one calm,
 * elegant family rather than a scattered rainbow.
 */
private enum class QuickActionKind { SURAH, JUZ, PAGE, SEARCH, BOOKMARKS }

private data class TileAccent(val icon: Color, val badgeBackground: Color)

@Composable
private fun tileAccent(kind: QuickActionKind): TileAccent {
    val isDark = LocalIsDarkTheme.current
    return when (kind) {
        QuickActionKind.SURAH -> if (isDark) {
            TileAccent(AmanahAccentGoldIconDark, AmanahAccentGoldBgDark)
        } else {
            TileAccent(AmanahAccentGoldIcon, AmanahAccentGoldBg)
        }
        QuickActionKind.JUZ -> if (isDark) {
            TileAccent(AmanahAccentTealIconDark, AmanahAccentTealBgDark)
        } else {
            TileAccent(AmanahAccentTealIcon, AmanahAccentTealBg)
        }
        QuickActionKind.PAGE -> if (isDark) {
            TileAccent(AmanahAccentTerracottaIconDark, AmanahAccentTerracottaBgDark)
        } else {
            TileAccent(AmanahAccentTerracottaIcon, AmanahAccentTerracottaBg)
        }
        QuickActionKind.SEARCH -> if (isDark) {
            TileAccent(AmanahAccentPlumIconDark, AmanahAccentPlumBgDark)
        } else {
            TileAccent(AmanahAccentPlumIcon, AmanahAccentPlumBg)
        }
        QuickActionKind.BOOKMARKS -> if (isDark) {
            TileAccent(AmanahAccentRoseIconDark, AmanahAccentRoseBgDark)
        } else {
            TileAccent(AmanahAccentRoseIcon, AmanahAccentRoseBg)
        }
    }
}

@Composable
private fun QuickActionsGrid(
    onOpenSurahList: () -> Unit,
    onOpenJuzList: () -> Unit,
    onOpenPageList: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenBookmarks: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
        AmanahSectionHeader(title = "Browse the Quran")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.sm),
        ) {
            QuickActionCard(
                icon = Icons.AutoMirrored.Rounded.List,
                label = "Surah Index",
                accent = tileAccent(QuickActionKind.SURAH),
                onClick = onOpenSurahList,
                modifier = Modifier.weight(1f),
            )
            QuickActionCard(
                icon = Icons.AutoMirrored.Rounded.ViewList,
                label = "Juz Index",
                accent = tileAccent(QuickActionKind.JUZ),
                onClick = onOpenJuzList,
                modifier = Modifier.weight(1f),
            )
            QuickActionCard(
                icon = Icons.Rounded.Book,
                label = "Page Index",
                accent = tileAccent(QuickActionKind.PAGE),
                onClick = onOpenPageList,
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.sm),
        ) {
            QuickActionCard(
                icon = Icons.Rounded.Search,
                label = "Search",
                accent = tileAccent(QuickActionKind.SEARCH),
                onClick = onOpenSearch,
                modifier = Modifier.weight(1f),
            )
            QuickActionCard(
                icon = Icons.Rounded.Bookmark,
                label = "Bookmarks",
                accent = tileAccent(QuickActionKind.BOOKMARKS),
                onClick = onOpenBookmarks,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ElderQuickActions(
    onOpenSurahList: () -> Unit,
    onOpenJuzList: () -> Unit,
    onOpenPageList: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenBookmarks: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
        AmanahSectionHeader(title = "Browse the Quran")
        AmanahTonalButton(
            text = "Surah Index",
            onClick = onOpenSurahList,
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.AutoMirrored.Rounded.List,
            iconTint = tileAccent(QuickActionKind.SURAH).icon,
        )
        AmanahTonalButton(
            text = "Juz/Para Index",
            onClick = onOpenJuzList,
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.AutoMirrored.Rounded.ViewList,
            iconTint = tileAccent(QuickActionKind.JUZ).icon,
        )
        AmanahTonalButton(
            text = "Page Index",
            onClick = onOpenPageList,
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Rounded.Book,
            iconTint = tileAccent(QuickActionKind.PAGE).icon,
        )
        AmanahTonalButton(
            text = "Search",
            onClick = onOpenSearch,
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Rounded.Search,
            iconTint = tileAccent(QuickActionKind.SEARCH).icon,
        )
        AmanahTonalButton(
            text = "Bookmarks",
            onClick = onOpenBookmarks,
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Rounded.Bookmark,
            iconTint = tileAccent(QuickActionKind.BOOKMARKS).icon,
        )
    }
}

@Composable
private fun QuickActionCard(
    icon: ImageVector,
    label: String,
    accent: TileAccent,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .clickable(onClickLabel = "Open $label", onClick = onClick)
            .heightIn(min = 88.dp),
        shape = AmanahShapes.card,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AmanahSpacing.sm, vertical = AmanahSpacing.sm),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AmanahSpacing.xs),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color = accent.badgeBackground, shape = AmanahShapes.quickActionBadge),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent.icon,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun TrustSummaryStrip(onOpenTrustCenter: () -> Unit) {
    AmanahSectionCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClickLabel = "Open Trust Center", onClick = onOpenTrustCenter),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.md),
        ) {
            Image(
                painter = painterResource(id = R.drawable.brand_mark),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Quran Content Sources",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "Offline · Source-attributed · No tracking",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = "View Trust Center",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
