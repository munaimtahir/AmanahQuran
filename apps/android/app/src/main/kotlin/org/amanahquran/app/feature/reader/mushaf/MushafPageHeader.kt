package org.amanahquran.app.feature.reader.mushaf

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.repository.MushafPageUi
import org.amanahquran.app.core.theme.AmanahGoldMuted
import org.amanahquran.app.core.theme.AmanahSpacing
import org.amanahquran.app.core.theme.LocalElderMode
import org.amanahquran.app.core.theme.QuranFonts

@Composable
fun MushafPageHeader(
    page: MushafPageUi,
    scriptType: ScriptType,
    showFullHeader: Boolean,
    modifier: Modifier = Modifier,
) {
    if (showFullHeader) {
        MushafFullHeader(page = page, scriptType = scriptType, modifier = modifier)
    } else {
        MushafCompactHeader(page = page, scriptType = scriptType, modifier = modifier)
    }
}

@Composable
private fun MushafFullHeader(
    page: MushafPageUi,
    scriptType: ScriptType,
    modifier: Modifier = Modifier,
) {
    val elder = LocalElderMode.current
    val fontFamily = QuranFonts.getFontFamily(scriptType)
    val titleSize = if (elder) 15.sp else 14.sp
    val arabicSize = if (elder) 27.sp else 25.sp
    val metaSize = if (elder) 12.sp else 11.sp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = if (elder) 6.dp else 4.dp, bottom = if (elder) 8.dp else 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(0.68f),
            color = AmanahGoldMuted.copy(alpha = 0.42f),
            thickness = 0.75.dp,
        )
        Text(
            text = page.surahLabel.orEmpty().ifBlank { "Surah ${page.surahNumber ?: page.pageNumber}" },
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = titleSize,
                fontWeight = FontWeight.Medium,
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = page.leftHeader.orEmpty().ifBlank { page.surahLabel.orEmpty() },
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = arabicSize,
                fontWeight = FontWeight.SemiBold,
            ),
            fontFamily = fontFamily,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Surah ${page.surahNumber ?: page.pageNumber}",
                style = MaterialTheme.typography.labelMedium.copy(fontSize = metaSize),
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            page.surahAyahCount?.let { ayahCount ->
                Text(
                    text = " · ${ayahCount} ayahs",
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = metaSize),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            page.juzNumber?.let { juz ->
                Text(
                    text = " · Juz $juz",
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = metaSize),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(0.68f),
            color = AmanahGoldMuted.copy(alpha = 0.32f),
            thickness = 0.75.dp,
        )
    }
}

@Composable
private fun MushafCompactHeader(
    page: MushafPageUi,
    scriptType: ScriptType,
    modifier: Modifier = Modifier,
) {
    val elder = LocalElderMode.current
    val fontFamily = QuranFonts.getFontFamily(scriptType)
    val arabicSize = if (elder) 15.sp else 14.sp
    val pageSize = if (elder) 16.sp else 15.sp
    val juzaSize = if (elder) 14.sp else 13.sp

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AmanahSpacing.xs, vertical = if (elder) 4.dp else 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = page.leftHeader.orEmpty().ifBlank { page.surahLabel.orEmpty() },
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = arabicSize,
                fontWeight = FontWeight.Medium,
            ),
            fontFamily = fontFamily,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Text(
            text = page.centerHeader.orEmpty().ifBlank { page.pageNumber.toString() },
            style = MaterialTheme.typography.titleSmall.copy(
                fontSize = pageSize,
                fontWeight = FontWeight.SemiBold,
            ),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Text(
            text = page.rightHeader.orEmpty().ifBlank { page.juzNumber?.let { "الجزء $it" } ?: "" },
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = juzaSize,
                fontWeight = FontWeight.Medium,
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
