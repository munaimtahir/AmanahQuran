package org.amanahquran.app.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import org.amanahquran.app.content.translation.TranslationAvailability
import org.amanahquran.app.content.translation.TranslationRepository
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.model.TranslationDirection
import org.amanahquran.app.core.model.TranslationSelection
import org.amanahquran.app.core.theme.AmanahShapes
import org.amanahquran.app.core.theme.AmanahSpacing
import org.amanahquran.app.core.theme.LocalReaderPalette
import org.amanahquran.app.core.theme.QuranFonts
import org.amanahquran.app.feature.reader.quranContentRepository
import org.amanahquran.app.feature.reader.toLayoutDirection
import org.amanahquran.app.feature.reader.toTextAlign
import org.amanahquran.app.feature.reader.translationLineHeightSp

/** Ayah shown in the preview. Loaded from the verified content database, never hard-coded. */
internal const val TYPOGRAPHY_PREVIEW_AYAH_KEY = "1:2"

/**
 * Live preview of the current reader typography (script, Arabic size, line spacing, translation
 * size) on the reader's own palette, so size changes are visible before returning to the reader.
 * Text comes from the bundled verified source; if it can't be loaded the card shows nothing.
 */
@Composable
internal fun TypographyPreviewCard(
    scriptType: ScriptType,
    arabicFontSizeSp: Float,
    arabicLineSpacingMultiplier: Float,
    translationSelection: TranslationSelection,
    translationFontSizeSp: Float,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val palette = LocalReaderPalette.current
    var arabicText by remember { mutableStateOf<String?>(null) }
    var translationText by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(scriptType) {
        arabicText = runCatching {
            quranContentRepository(context).getAyahDisplay(TYPOGRAPHY_PREVIEW_AYAH_KEY, scriptType.name)?.displayText
        }.getOrNull()
    }
    LaunchedEffect(translationSelection) {
        val id = translationSelection.translationId
        translationText = if (id == null) {
            null
        } else {
            runCatching {
                TranslationRepository(context).getAyah(id, TYPOGRAPHY_PREVIEW_AYAH_KEY)
                    ?.takeIf { it.availability == TranslationAvailability.TRANSLATED }
                    ?.displayText
            }.getOrNull()
        }
    }

    val arabic = arabicText ?: return
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(palette.background, AmanahShapes.card)
            .padding(AmanahSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm),
    ) {
        Text(
            text = "Preview · $TYPOGRAPHY_PREVIEW_AYAH_KEY",
            style = MaterialTheme.typography.labelMedium,
            color = palette.secondaryText,
            modifier = Modifier.semantics { heading() },
        )
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Text(
                text = arabic,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = arabicFontSizeSp.sp,
                    lineHeight = (arabicFontSizeSp * arabicLineSpacingMultiplier).sp,
                    letterSpacing = if (scriptType == ScriptType.INDOPAK) (-0.4).sp else 0.sp,
                    fontFamily = QuranFonts.getFontFamily(scriptType),
                ),
                color = palette.text,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        translationText?.let { translation ->
            val direction = translationSelection.direction ?: TranslationDirection.LTR
            CompositionLocalProvider(LocalLayoutDirection provides direction.toLayoutDirection()) {
                Text(
                    text = translation,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = translationFontSizeSp.sp,
                        lineHeight = translationLineHeightSp(translationFontSizeSp, direction).sp,
                    ),
                    color = palette.secondaryText,
                    textAlign = direction.toTextAlign(),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
