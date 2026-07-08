package org.amanahquran.app.feature.trust

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.amanahquran.app.R
import org.amanahquran.app.core.theme.AmanahSpacing
import org.amanahquran.app.core.theme.LocalElderMode
import org.amanahquran.app.core.ui.AmanahCard
import org.amanahquran.app.core.ui.AmanahDivider
import org.amanahquran.app.core.ui.AmanahSectionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrustCenterScreen(
    onNavigateBack: () -> Unit,
    viewModel: TrustCenterViewModel = viewModel(factory = TrustCenterViewModel.factory(LocalContext.current)),
) {
    val uiState by viewModel.uiState.collectAsState()
    val elder = LocalElderMode.current
    val horizontalPadding = if (elder) AmanahSpacing.screenHorizontalPaddingElder else AmanahSpacing.screenHorizontalPadding

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Trust Center", style = MaterialTheme.typography.titleLarge) },
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
        when {
            uiState.generatedAt == null && uiState.noModificationStatement == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = AmanahSpacing.lg),
                    verticalArrangement = Arrangement.spacedBy(AmanahSpacing.xl),
                ) {
                    // Header badge
                    item {
                        AmanahSectionCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.md),
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.trust_graphic),
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                )
                                Column {
                                    Text(
                                        text = if (uiState.publicReleaseAllowed) {
                                            "Verified Quran text"
                                        } else {
                                            "Internal test Quran content"
                                        },
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                    Text(
                                        text = if (uiState.publicReleaseAllowed) {
                                            "Offline · Source-attributed · No tracking"
                                        } else {
                                            "Not approved for public release · Manual review pending"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }

                    // Quran text sources
                    item {
                            TrustSectionHeader(title = "Quran Text Sources", icon = Icons.Rounded.FolderOpen)
                        Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
                            uiState.quranTextSourcesActuallyUsed.forEach { source ->
                                AmanahCard(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = source.sourceName,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                    val detail = listOfNotNull(
                                        source.referenceType?.let { "Type: $it" },
                                        source.scriptType?.let { "Script: $it" },
                                        source.rawSource?.let { "Raw source: $it" },
                                        source.sourceUrl?.let { "URL: $it" },
                                        source.licenseName?.let { "License: $it" },
                                        source.licenseUrl?.let { "License URL: $it" },
                                        source.notes?.let { "Notes: $it" },
                                        source.validationStatus?.let { "Validation: $it" },
                                    ).joinToString(" · ")
                                    if (detail.isNotBlank()) {
                                        Text(
                                            text = detail,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (uiState.sourceReferences.isNotEmpty()) {
                        item {
                            TrustSectionHeader(title = "Reference Sources", icon = Icons.Rounded.FolderOpen)
                            Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
                                uiState.sourceReferences.forEach { reference ->
                                    AmanahCard(modifier = Modifier.fillMaxWidth()) {
                                        Text(
                                            text = reference.sourceName,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        )
                                        val detail = listOfNotNull(
                                            reference.referenceType?.let { "Type: $it" },
                                            reference.sourceUrl?.let { "URL: $it" },
                                            reference.licenseName?.let { "License: $it" },
                                            reference.licenseUrl?.let { "License URL: $it" },
                                            reference.notes?.let { "Notes: $it" },
                                        ).joinToString(" · ")
                                        if (detail.isNotBlank()) {
                                            Text(
                                                text = detail,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // No-modification statement
                    item {
                        TrustSectionHeader(title = "No-Modification Statement", icon = Icons.Rounded.CheckCircle)
                        Text(
                            text = uiState.noModificationStatement.orEmpty(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }

                    // Validation status
                    item {
                        TrustSectionHeader(title = "Validation Status", icon = Icons.Rounded.Security)
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(AmanahSpacing.xs),
                        ) {
                            ValidationStat("Validation rows", uiState.validationRowCount.toString())
                            AmanahDivider()
                            ValidationStat("Failed rows", uiState.failedValidationRowCount.toString())
                            AmanahDivider()
                            ValidationStat("Content source rows", uiState.contentSourceCount.toString())
                        }
                    }

                    // Mushaf Page Layout Section
                    uiState.mushafLayoutInfo?.let { info ->
                        item {
                            TrustSectionHeader(title = "Mushaf Page Layout", icon = Icons.Rounded.VerifiedUser)
                            AmanahCard(modifier = Modifier.fillMaxWidth()) {
                                Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.xs)) {
                                    ValidationStat("Page Layout Source", info.pageLayoutSource)
                                    AmanahDivider()
                                    ValidationStat("Script Type", info.scriptType)
                                    AmanahDivider()
                                    ValidationStat("Page Count", info.pageCount)
                                    AmanahDivider()
                                    ValidationStat("Line Mapping", info.lineMappingStatus)
                                    AmanahDivider()
                                    ValidationStat("Import Date", info.importDate)
                                    AmanahDivider()
                                    ValidationStat("Checksum", info.checksum)
                                    AmanahDivider()
                                    ValidationStat("Validation Status", info.validationStatus)
                                    AmanahDivider()
                                    ValidationStat("Manual Review Status", info.manualReviewStatus)
                                }
                            }
                        }
                    }

                    // Privacy pledge
                    item {
                        TrustSectionHeader(title = "Privacy Pledge", icon = Icons.Rounded.Lock)
                        Text(
                            text = uiState.privacyPledge.orEmpty(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }

                    if (uiState.releaseApprovalStatus != null || uiState.appVersionName != null) {
                        item {
                            TrustSectionHeader(title = "Release Information", icon = Icons.Rounded.VerifiedUser)
                            AmanahCard(modifier = Modifier.fillMaxWidth()) {
                                Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.xs)) {
                                    uiState.releaseApprovalStatus?.let { ValidationStat("Status", it) }
                                    uiState.releaseApprovalBy?.let {
                                        AmanahDivider()
                                        ValidationStat("Approved By", it)
                                    }
                                    uiState.releaseApprovalAt?.let {
                                        AmanahDivider()
                                        ValidationStat("Approved At", it)
                                    }
                                    uiState.appVersionName?.let {
                                        AmanahDivider()
                                        ValidationStat("App Version", it)
                                    }
                                    uiState.appVersionCode?.let {
                                        AmanahDivider()
                                        ValidationStat("Version Code", it.toString())
                                    }
                                }
                            }
                        }
                    }

                    // Claims not made
                    if (uiState.claimsNotMade.isNotEmpty()) {
                        item {
                            TrustSectionHeader(title = "Claims Not Made", icon = Icons.Rounded.Info)
                            Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.xs)) {
                                uiState.claimsNotMade.forEach { claim ->
                                    Text(
                                        text = "· $claim",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }

                    // Integrity placeholders
                    if (uiState.appContentIntegrityPlaceholders.isNotEmpty()) {
                        item {
                            TrustSectionHeader(title = "Content Integrity", icon = Icons.Rounded.VerifiedUser)
                            Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.xs)) {
                                uiState.appContentIntegrityPlaceholders.forEach { placeholder ->
                                    Text(
                                        text = "· $placeholder",
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
    }
}

@Composable
private fun TrustSectionHeader(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.sm),
        modifier = Modifier.padding(bottom = AmanahSpacing.sm),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun ValidationStat(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}
