package org.amanahquran.app.feature.reader

import androidx.compose.runtime.Composable
import org.amanahquran.app.core.ui.PlaceholderScreen

@Composable
fun ReaderScreen(
    onNavigateBack: () -> Unit,
) {
    PlaceholderScreen(
        title = "Reader",
        description = "Offline reader shell for verified Quran text. No Quran database is shipped in this sprint.",
        bullets = listOf(
            "Planned support: IndoPak and Uthmani scripts.",
            "Planned navigation: Surah, Juz, and Page modes.",
            "Last-read and bookmarks will remain canonical.",
        ),
        onNavigateBack = onNavigateBack,
    )
}

