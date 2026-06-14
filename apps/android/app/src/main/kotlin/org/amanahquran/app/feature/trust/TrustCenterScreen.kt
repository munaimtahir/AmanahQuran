package org.amanahquran.app.feature.trust

import androidx.compose.runtime.Composable
import org.amanahquran.app.core.ui.PlaceholderScreen

@Composable
fun TrustCenterScreen(
    onNavigateBack: () -> Unit,
) {
    PlaceholderScreen(
        title = "Trust Center",
        description = "Offline source transparency placeholder. Verification metadata will be shown here in later sprints.",
        bullets = listOf(
            "Must disclose source, license, version, checksum, and validation status.",
            "Must state the no-modification and privacy pledge.",
        ),
        onNavigateBack = onNavigateBack,
    )
}

