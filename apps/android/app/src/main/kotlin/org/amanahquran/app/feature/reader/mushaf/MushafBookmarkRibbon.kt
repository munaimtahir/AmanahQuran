package org.amanahquran.app.feature.reader.mushaf

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.amanahquran.app.core.theme.AmanahGoldMuted
import org.amanahquran.app.core.theme.AmanahGreenDeep
import androidx.compose.material3.MaterialTheme

@Composable
fun MushafBookmarkRibbon(
    isBookmarked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(48.dp),
    ) {
        Icon(
            imageVector = if (isBookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
            contentDescription = if (isBookmarked) "Remove page bookmark" else "Bookmark this page",
            tint = if (isBookmarked) {
                MaterialTheme.colorScheme.primary
            } else {
                AmanahGoldMuted.copy(alpha = 0.5f)
            },
            modifier = Modifier.size(32.dp),
        )
    }
}
