package org.amanahquran.app.feature.reader.mushaf

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.repository.MushafLineUi
import org.amanahquran.app.core.repository.MushafPageUi
import org.amanahquran.app.core.theme.AmanahSpacing
import org.amanahquran.app.core.theme.LocalElderMode
import org.amanahquran.app.core.theme.QuranFonts

val PaperBackground = Color(0xFFFFFDF7)
val MutedGoldBorder = Color(0xFFC7A24A)
val SoftInkColor = Color(0xFF1A1A17)
val SoftWarmDivider = Color(0xFFEFECE5)

@Composable
fun MushafPageFrame(
    page: MushafPageUi,
    lines: List<MushafLineUi>,
    scriptType: ScriptType,
    isBookmarked: Boolean,
    onBookmarkClick: () -> Unit,
    fontScale: Float,
    showFullHeader: Boolean,
    modifier: Modifier = Modifier,
) {
    val elder = LocalElderMode.current
    val baseFontSize = if (elder) 37.sp else 32.sp
    val scaledFontSize = baseFontSize * fontScale
    val fontFamily = QuranFonts.getFontFamily(scriptType)
    val borderShape = RoundedCornerShape(if (elder) 14.dp else 16.dp)
    val innerShape = RoundedCornerShape(if (elder) 13.dp else 15.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = if (elder) 4.dp else 6.dp, vertical = if (elder) 4.dp else 4.dp)
            .background(PaperBackground, borderShape)
            .border(
                border = BorderStroke(if (elder) 1.25.dp else 1.5.dp, MutedGoldBorder.copy(alpha = 0.78f)),
                shape = borderShape,
            )
            .padding(1.dp)
            .border(
                border = BorderStroke(0.5.dp, MutedGoldBorder.copy(alpha = 0.35f)),
                shape = innerShape,
            )
            .padding(horizontal = AmanahSpacing.xs, vertical = if (elder) 6.dp else 4.dp),
    ) {
        MushafBookmarkRibbon(
            isBookmarked = isBookmarked,
            onClick = onBookmarkClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 2.dp, top = 2.dp),
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            MushafPageHeader(
                page = page,
                scriptType = scriptType,
                showFullHeader = showFullHeader,
            )

            if (!showFullHeader) {
                HorizontalDivider(
                    color = MutedGoldBorder.copy(alpha = 0.18f),
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(bottom = AmanahSpacing.xs),
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                lines.forEachIndexed { index, line ->
                    MushafLineText(
                        line = line,
                        fontSize = scaledFontSize,
                        fontFamily = fontFamily,
                        modifier = Modifier.padding(vertical = if (elder) 2.dp else 1.dp),
                    )

                    if (index < lines.size - 1) {
                        HorizontalDivider(
                            color = SoftWarmDivider,
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(vertical = 1.dp),
                        )
                    }
                }
            }

            HorizontalDivider(
                color = MutedGoldBorder.copy(alpha = 0.18f),
                thickness = 0.5.dp,
                modifier = Modifier.padding(top = AmanahSpacing.xs),
            )

            MushafPageFooter()
        }
    }
}
