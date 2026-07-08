package org.amanahquran.app.feature.reader.mushaf

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.amanahquran.app.core.theme.AmanahSpacing

@Composable
fun MushafPageControls(
    pageNumber: Int,
    pageCount: Int,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit,
    elderModeEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val buttonHeight = if (elderModeEnabled) 56.dp else 44.dp
    val fontSize = if (elderModeEnabled) 18.sp else 14.sp
    val labelPadding = if (elderModeEnabled) AmanahSpacing.md else AmanahSpacing.sm

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AmanahSpacing.md, vertical = AmanahSpacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Prev Page on left (in Ltr swipe standard: prev is on left click or swipe right)
        ElevatedButton(
            onClick = onPreviousPage,
            enabled = pageNumber > 1,
            modifier = Modifier.height(buttonHeight),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = labelPadding),
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Icon(Icons.Rounded.ChevronLeft, contentDescription = "Previous Page", modifier = Modifier.size(24.dp))
            if (!elderModeEnabled) {
                Text("Previous", fontSize = fontSize)
            }
        }

        // Current page label
        Text(
            text = "Page $pageNumber of $pageCount",
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = if (elderModeEnabled) 20.sp else 16.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        // Next Page on right
        ElevatedButton(
            onClick = onNextPage,
            enabled = pageNumber < pageCount,
            modifier = Modifier.height(buttonHeight),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = labelPadding),
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            if (!elderModeEnabled) {
                Text("Next", fontSize = fontSize)
            }
            Icon(Icons.Rounded.ChevronRight, contentDescription = "Next Page", modifier = Modifier.size(24.dp))
        }
    }
}
