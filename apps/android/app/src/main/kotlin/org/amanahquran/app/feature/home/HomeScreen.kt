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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material.icons.automirrored.rounded.ViewList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.amanahquran.app.R
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.theme.AmanahShapes
import org.amanahquran.app.core.theme.AmanahSpacing
import org.amanahquran.app.core.theme.LocalElderMode
import org.amanahquran.app.core.ui.AmanahCard
import org.amanahquran.app.core.ui.AmanahDivider
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
            HomeHeader(selectedScript = uiState.selectedScript)

            val cr = uiState.continueReading
            if (cr != null) {
                ContinueReadingCard(continueReading = cr, onContinue = { onContinueReading(cr) })
            }

            OpenMushafCard(
                onOpenMushaf = {
                    onOpenMushafReader(cr?.pageNumber ?: 1, uiState.selectedScript)
                }
            )

            if (elder) {
                ElderQuickActions(
                    onOpenSurahList = onOpenSurahList,
                    onOpenJuzList = onOpenJuzList,
                    onOpenPageList = onOpenPageList,
                    onOpenSearch = onOpenSearch,
                    onOpenBookmarks = onOpenBookmarks,
                    onOpenSettings = onOpenSettings,
                    onOpenTrustCenter = onOpenTrustCenter,
                )
            } else {
                QuickActionsGrid(
                    onOpenSurahList = onOpenSurahList,
                    onOpenJuzList = onOpenJuzList,
                    onOpenPageList = onOpenPageList,
                    onOpenSearch = onOpenSearch,
                    onOpenBookmarks = onOpenBookmarks,
                    onOpenSettings = onOpenSettings,
                    onOpenTrustCenter = onOpenTrustCenter,
                )
            }

            TrustSummaryStrip(onOpenTrustCenter = onOpenTrustCenter)

            // Safe bottom padding
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun HomeHeader(selectedScript: ScriptType) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.sm),
        modifier = Modifier.padding(horizontal = AmanahSpacing.xs)
    ) {
        Image(
            painter = painterResource(id = R.drawable.brand_mark),
            contentDescription = null,
            modifier = Modifier.size(36.dp),
        )
        Column {
            Text(
                text = "Amanah Quran",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "Offline  ·  Source-attributed  ·  No Ads  ·  ${selectedScript.displayLabel()}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ContinueReadingCard(
    continueReading: HomeContinueReadingUiModel,
    onContinue: () -> Unit,
) {
    AmanahCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onContinue,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.md),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = AmanahShapes.numberBadge,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.MenuBook,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp),
                )
            }
            Column {
                Text(
                    text = "Continue Reading",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = continueReading.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.height(AmanahSpacing.md))
        AmanahPrimaryButton(
            text = "Continue Reading",
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.AutoMirrored.Rounded.MenuBook,
        )
    }
}

@Composable
private fun OpenMushafCard(onOpenMushaf: () -> Unit) {
    AmanahCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onOpenMushaf,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.md),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = AmanahShapes.numberBadge,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.MenuBook,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp),
                )
            }
            Column {
                Text(
                    text = "Open Mushaf Page",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "Read by Surah, Juz, or Page using offline source-attributed text",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.height(AmanahSpacing.md))
        AmanahPrimaryButton(
            text = "Open Mushaf Page",
            onClick = onOpenMushaf,
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.AutoMirrored.Rounded.MenuBook,
        )
    }
}

private fun ScriptType.displayLabel(): String = when (this) {
    ScriptType.INDOPAK -> "IndoPak + Uthmani"
    ScriptType.UTHMANI -> "Uthmani + IndoPak"
}

@Composable
private fun QuickActionsGrid(
    onOpenSurahList: () -> Unit,
    onOpenJuzList: () -> Unit,
    onOpenPageList: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenTrustCenter: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
        AmanahSectionHeader(title = "Quick Actions")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.sm),
        ) {
            QuickActionCard(
                icon = Icons.AutoMirrored.Rounded.List,
                label = "Surah Index",
                onClick = onOpenSurahList,
                modifier = Modifier.weight(1f),
            )
            QuickActionCard(
                icon = Icons.AutoMirrored.Rounded.ViewList,
                label = "Juz/Para Index",
                onClick = onOpenJuzList,
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.sm),
        ) {
            QuickActionCard(
                icon = Icons.Rounded.Book,
                label = "Page Index",
                onClick = onOpenPageList,
                modifier = Modifier.weight(1f),
            )
            QuickActionCard(
                icon = Icons.Rounded.Search,
                label = "Search",
                onClick = onOpenSearch,
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.sm),
        ) {
            QuickActionCard(
                icon = Icons.Rounded.Bookmark,
                label = "Bookmarks",
                onClick = onOpenBookmarks,
                modifier = Modifier.weight(1f),
            )
            QuickActionCard(
                icon = Icons.Rounded.VerifiedUser,
                label = "Trust Center",
                onClick = onOpenTrustCenter,
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.sm),
        ) {
            QuickActionCard(
                icon = Icons.Rounded.Settings,
                label = "Settings",
                onClick = onOpenSettings,
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.weight(1f))
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
    onOpenSettings: () -> Unit,
    onOpenTrustCenter: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
        AmanahSectionHeader(title = "Quick Actions")
        AmanahTonalButton(
            text = "Surah Index",
            onClick = onOpenSurahList,
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.AutoMirrored.Rounded.List,
        )
        AmanahTonalButton(
            text = "Juz/Para Index",
            onClick = onOpenJuzList,
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.AutoMirrored.Rounded.ViewList,
        )
        AmanahTonalButton(
            text = "Page Index",
            onClick = onOpenPageList,
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Rounded.Book,
        )
        AmanahTonalButton(
            text = "Search",
            onClick = onOpenSearch,
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Rounded.Search,
        )
        AmanahTonalButton(
            text = "Bookmarks",
            onClick = onOpenBookmarks,
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Rounded.Bookmark,
        )
        AmanahTonalButton(
            text = "Settings",
            onClick = onOpenSettings,
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Rounded.Settings,
        )
        AmanahTonalButton(
            text = "Trust Center",
            onClick = onOpenTrustCenter,
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Rounded.VerifiedUser,
        )
    }
}

@Composable
private fun QuickActionCard(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .heightIn(min = 80.dp),
        shape = AmanahShapes.card,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AmanahSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(26.dp),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun TrustSummaryStrip(onOpenTrustCenter: () -> Unit) {
    AmanahSectionCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenTrustCenter),
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
