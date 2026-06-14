package org.amanahquran.app.feature.settings

import androidx.compose.runtime.Composable
import org.amanahquran.app.core.ui.PlaceholderScreen

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
) {
    PlaceholderScreen(
        title = "Settings",
        description = "Theme, script, and Elder Mode controls will live here.",
        bullets = listOf(
            "Planned themes: System, Light, Dark, Sepia.",
            "Planned settings storage: local only.",
        ),
        onNavigateBack = onNavigateBack,
    )
}

