package org.amanahquran.app.feature.reader

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.amanahquran.app.content.translation.TranslationAvailability
import org.amanahquran.app.content.translation.TranslationAyahDisplay
import org.amanahquran.app.content.translation.TranslationFootnote
import org.amanahquran.app.core.model.TranslationDirection
import org.amanahquran.app.core.theme.AmanahSpacing
import org.amanahquran.app.core.theme.LocalReaderPalette

/** Text neither Quran content nor translation content -- application UI, never persisted as translation text. */
private const val SOURCE_MISSING_MESSAGE = "Translation not provided in this source"

fun TranslationDirection.toLayoutDirection(): LayoutDirection =
    if (this == TranslationDirection.RTL) LayoutDirection.Rtl else LayoutDirection.Ltr

fun TranslationDirection.toTextAlign(): TextAlign =
    if (this == TranslationDirection.RTL) TextAlign.Right else TextAlign.Left

/**
 * Renders one ayah's translation paragraph, direction-aware (Urdu right-aligned/RTL, English
 * left-aligned/LTR), with a distinct neutral placeholder for [TranslationAvailability.SOURCE_MISSING]
 * rather than any fabricated text, and a tappable footnote-marker row when footnotes exist for
 * this ayah. Renders nothing when there is no entry for this ayah at all.
 */
@Composable
fun TranslationAyahText(
    display: TranslationAyahDisplay?,
    direction: TranslationDirection,
    footnotes: List<TranslationFootnote>,
    translationFontSizeSp: Float,
    onClick: () -> Unit,
    onFootnotesRequested: (List<TranslationFootnote>) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (display == null) return
    val palette = LocalReaderPalette.current
    CompositionLocalProvider(LocalLayoutDirection provides direction.toLayoutDirection()) {
        Column(modifier = modifier) {
            when (display.availability) {
                TranslationAvailability.SOURCE_MISSING -> Text(
                    text = SOURCE_MISSING_MESSAGE,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = translationFontSizeSp.sp,
                        fontStyle = FontStyle.Italic,
                    ),
                    color = palette.secondaryText.copy(alpha = 0.7f),
                    textAlign = direction.toTextAlign(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onClick)
                        .padding(vertical = 2.dp, horizontal = AmanahSpacing.xs),
                )
                TranslationAvailability.TRANSLATED -> {
                    val text = display.displayText
                    if (!text.isNullOrBlank()) {
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = translationFontSizeSp.sp,
                                lineHeight = (translationFontSizeSp * 1.65f).sp,
                            ),
                            color = palette.secondaryText,
                            textAlign = direction.toTextAlign(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onClick)
                                .padding(vertical = 2.dp, horizontal = AmanahSpacing.xs),
                        )
                    }
                }
            }
            if (footnotes.isNotEmpty()) {
                Text(
                    text = footnotes.joinToString(" ") { it.marker.ifBlank { "*" } },
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.activeControl,
                    textAlign = direction.toTextAlign(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onFootnotesRequested(footnotes) }
                        .padding(horizontal = AmanahSpacing.xs),
                )
            }
        }
    }
}

/**
 * Bottom sheet listing every footnote passed in -- opened from the marker row [TranslationAyahText]
 * renders inline, never mixed into the translation text itself since the source data keeps
 * footnotes as a separate, ayah-associated list, not offsets into `display_text`.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslationFootnoteSheet(
    footnotes: List<TranslationFootnote>,
    direction: TranslationDirection,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        CompositionLocalProvider(LocalLayoutDirection provides direction.toLayoutDirection()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = AmanahSpacing.md, vertical = AmanahSpacing.sm),
            ) {
                Text(
                    text = "Footnotes",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = direction.toTextAlign(),
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(AmanahSpacing.sm))
                LazyColumn(
                    contentPadding = PaddingValues(bottom = AmanahSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm),
                ) {
                    items(footnotes) { footnote ->
                        Row(horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.xs)) {
                            if (footnote.marker.isNotBlank()) {
                                Text(
                                    text = footnote.marker,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                )
                            }
                            Text(
                                text = footnote.text,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = direction.toTextAlign(),
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }
        }
    }
}
