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
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import org.amanahquran.app.core.repository.PackagedAssetVerification
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
                            uiState.productionApprovalStatement?.let { statement ->
                                Text(
                                    text = statement,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(top = AmanahSpacing.sm),
                                )
                            }
                        }
                    }

                    // Quran text sources
                    item {
                        TrustSectionHeader(title = "Quran Text Sources", icon = Icons.Rounded.FolderOpen)
                        Text(
                            text = "Displayed exactly as sourced. Nothing has been changed.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = AmanahSpacing.sm),
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
                            uiState.quranTextSourcesActuallyUsed.forEach { source ->
                                AmanahCard(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = source.sourceName,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                            }
                        }
                    }

                    if (uiState.optionalContentPacks.isNotEmpty()) {
                        item {
                            TrustSectionHeader(title = "Installed Content", icon = Icons.Rounded.VerifiedUser)
                            Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
                                uiState.optionalContentPacks.forEach { pack ->
                                    AmanahCard(modifier = Modifier.fillMaxWidth()) {
                                        Text(pack.displayName, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                                        Text(
                                            text = "Displayed exactly as published. Nothing has been changed.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // On-device content verification
                    item {
                        TrustSectionHeader(title = "Verify Your Content", icon = Icons.Rounded.Security)
                        AmanahCard(modifier = Modifier.fillMaxWidth()) {
                            Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
                                Text(
                                    text = "Check that the Quran text on this device still matches the original source.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Button(
                                    onClick = { viewModel.verifyNow() },
                                    enabled = !uiState.isVerifying,
                                ) {
                                    if (uiState.isVerifying) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            strokeWidth = 2.dp,
                                        )
                                    } else {
                                        Text("Verify now")
                                    }
                                }
                                uiState.verificationError?.let { error ->
                                    Text(
                                        text = error,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error,
                                    )
                                }
                                uiState.verificationResults.forEach { result ->
                                    VerificationResultRow(result)
                                }
                            }
                        }
                    }

                    if (uiState.sourceReferences.isNotEmpty()) {
                        item {
                            TrustSectionHeader(title = "Fonts & Attribution", icon = Icons.Rounded.FolderOpen)
                            Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
                                uiState.sourceReferences.forEach { reference ->
                                    AmanahCard(modifier = Modifier.fillMaxWidth()) {
                                        Text(
                                            text = reference.sourceName,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        )
                                        reference.licenseName?.let { license ->
                                            Text(
                                                text = "License: $license",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                    }
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
private fun VerificationResultRow(result: PackagedAssetVerification) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = result.assetName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = if (result.matches) "Matches original" else "Mismatch detected",
            style = MaterialTheme.typography.labelLarge,
            color = if (result.matches) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
        )
    }
}
