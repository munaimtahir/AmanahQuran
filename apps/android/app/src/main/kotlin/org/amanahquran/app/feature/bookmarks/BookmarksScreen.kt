package org.amanahquran.app.feature.bookmarks

import androidx.compose.runtime.Composable
import org.amanahquran.app.core.ui.PlaceholderScreen

@Composable
fun BookmarksScreen(
    onNavigateBack: () -> Unit,
) {
    PlaceholderScreen(
        title = "Bookmarks",
        description = "Local bookmark placeholder. Canonical ayah keys and page numbers will be added later.",
        bullets = listOf(
            "Bookmark identity must use ayah keys or page numbers.",
            "No bookmark storage is implemented in this sprint.",
        ),
        onNavigateBack = onNavigateBack,
    )
}

