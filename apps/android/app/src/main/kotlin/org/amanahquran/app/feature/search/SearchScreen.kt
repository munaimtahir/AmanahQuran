package org.amanahquran.app.feature.search

import androidx.compose.runtime.Composable
import org.amanahquran.app.core.ui.PlaceholderScreen

@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
) {
    PlaceholderScreen(
        title = "Search",
        description = "Offline search placeholder. Search normalization and indexing will be implemented in a later sprint.",
        bullets = listOf(
            "Display text will stay separate from normalized search text.",
            "Search must remain local and offline.",
        ),
        onNavigateBack = onNavigateBack,
    )
}

