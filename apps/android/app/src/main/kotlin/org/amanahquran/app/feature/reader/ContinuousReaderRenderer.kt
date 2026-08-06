package org.amanahquran.app.feature.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.theme.AmanahShapes
import org.amanahquran.app.core.theme.AmanahSpacing
import org.amanahquran.app.core.theme.LocalReaderPalette
import org.amanahquran.app.core.theme.QuranFonts

/**
 * Builds the styled [AnnotatedString] for one [ContinuousQuranBlock]: the canonical ayah text is
 * left completely untouched (only [SpanStyle]s are layered on top for colour/highlight), inline
 * markers get a smaller, distinctly-coloured span so they read as "not Quran text", and the
 * selected ayah gets the same restrained highlight tint Ayah Mode uses.
 */
private fun buildContinuousAnnotatedText(
    block: ContinuousQuranBlock,
    selectedAyahKey: String?,
    textColor: Color,
    markerColor: Color,
    highlightColor: Color,
    markerFontSizeSp: Float,
): AnnotatedString = buildAnnotatedString {
    append(block.plainText)
    addStyle(SpanStyle(color = textColor), 0, block.plainText.length)
    block.ayahRanges.forEach { range ->
        if (range.ayahKey == selectedAyahKey) {
            addStyle(SpanStyle(background = highlightColor), range.textStart, range.markerEnd)
        }
        addStyle(SpanStyle(color = markerColor, fontSize = markerFontSizeSp.sp), range.markerStart, range.markerEnd)
    }
}

/**
 * Single-pane Continuous Mode rendering of one page block: one flowing [Text] with inline ayah
 * markers. Tapping anywhere resolves the tapped character back to its canonical ayah via
 * [TextLayoutResult.getOffsetForPosition] + [offsetToAyahKey], then calls [onSelectAyah] -- the
 * exact same callback Ayah Mode's per-row tap already uses, so the existing selected-ayah action
 * card (bookmark/share/report) works unmodified in Continuous Mode too.
 */
@Composable
fun ContinuousQuranBlockText(
    block: ContinuousQuranBlock,
    scriptType: ScriptType,
    arabicFontSizeSp: Float,
    arabicLineSpacingMultiplier: Float,
    selectedAyahKey: String?,
    onSelectAyah: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalReaderPalette.current
    val fontFamily = QuranFonts.getFontFamily(scriptType)
    val markerFontSizeSp = arabicFontSizeSp * 0.55f
    val annotatedText = remember(block, selectedAyahKey, palette, markerFontSizeSp) {
        buildContinuousAnnotatedText(
            block = block,
            selectedAyahKey = selectedAyahKey,
            textColor = palette.text,
            markerColor = palette.activeControl,
            highlightColor = palette.currentAyahHighlight,
            markerFontSizeSp = markerFontSizeSp,
        )
    }
    var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    Text(
        text = annotatedText,
        fontFamily = fontFamily,
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(block) {
                detectTapGestures { offset ->
                    val result = layoutResult ?: return@detectTapGestures
                    val charOffset = result.getOffsetForPosition(offset)
                    offsetToAyahKey(block, charOffset)?.let(onSelectAyah)
                }
            },
        style = MaterialTheme.typography.bodyLarge.copy(
            fontSize = arabicFontSizeSp.sp,
            lineHeight = (arabicFontSizeSp * arabicLineSpacingMultiplier).sp,
            letterSpacing = 0.sp,
        ),
        textAlign = TextAlign.Justify,
        onTextLayout = { layoutResult = it },
    )
}

/**
 * Split-screen row for one page block: translation (left) and Quran Arabic (right), sharing this
 * single `LazyColumn` item's scroll position by construction -- there is no second scroll state
 * to drift out of sync with. Translation renders as one short paragraph per ayah (rather than
 * fully run-on prose like the Arabic side) because sentence-length translation text reads far
 * better with a line break between ayahs; both sides still scroll and virtualize as one unit and
 * carry no per-ayah card/border, so the "continuous" feel is preserved. `IntrinsicSize.Min` +
 * `fillMaxHeight` on the divider is the standard Compose way to make a divider span exactly the
 * row's own (intrinsically measured) height inside a `LazyColumn` item, where height is otherwise
 * unbounded.
 */
@Composable
fun ParallelTranslationBlockRow(
    block: ContinuousQuranBlock,
    scriptType: ScriptType,
    arabicFontSizeSp: Float,
    arabicLineSpacingMultiplier: Float,
    translations: Map<String, String>,
    translationFontSizeSp: Float,
    selectedAyahKey: String?,
    onSelectAyah: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalReaderPalette.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = AmanahSpacing.sm),
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                block.ayahRanges.forEach { range ->
                    val translationText = translations[range.ayahKey]
                    if (!translationText.isNullOrBlank()) {
                        Text(
                            text = translationText,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectAyah(range.ayahKey) }
                                .then(
                                    if (range.ayahKey == selectedAyahKey) {
                                        Modifier.background(palette.currentAyahHighlight, AmanahShapes.ayahCard)
                                    } else {
                                        Modifier
                                    },
                                )
                                .padding(horizontal = AmanahSpacing.xs, vertical = 2.dp),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = translationFontSizeSp.sp,
                                lineHeight = (translationFontSizeSp * 1.65f).sp,
                            ),
                            textAlign = TextAlign.Right,
                            color = palette.secondaryText,
                        )
                    }
                }
            }
        }

        VerticalDivider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp),
            color = palette.divider,
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(start = AmanahSpacing.sm),
        ) {
            ContinuousQuranBlockText(
                block = block,
                scriptType = scriptType,
                arabicFontSizeSp = arabicFontSizeSp,
                arabicLineSpacingMultiplier = arabicLineSpacingMultiplier,
                selectedAyahKey = selectedAyahKey,
                onSelectAyah = onSelectAyah,
            )
        }
    }
}
