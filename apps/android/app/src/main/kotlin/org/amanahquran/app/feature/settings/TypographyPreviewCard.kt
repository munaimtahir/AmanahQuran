package org.amanahquran.app.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
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

/** Ayahs shown in the preview - Al-Fatiha (1:1-4) for comprehensive preview. */
private val PREVIEW_AYAH_KEYS = listOf("1:1", "1:2", "1:3", "1:4")

/** Legacy constant for test compatibility - now refers to first ayah in preview. */
internal const val TYPOGRAPHY_PREVIEW_AYAH_KEY = "1:1"

/**
 * Live preview of the current reader typography (script, Arabic size, line spacing, translation
 * size) on the reader's own palette, so size changes are visible before returning to the reader.
 * Text comes from the bundled verified source; if it can't be loaded the card shows nothing.
 * Enhanced to show multiple verses and optional script comparison.
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
    var arabicTexts by remember { mutableStateOf<Map<String, String?>>(emptyMap()) }
    var translationTexts by remember { mutableStateOf<Map<String, String?>>(emptyMap()) }
    var showScriptComparison by remember { mutableStateOf(false) }

    LaunchedEffect(scriptType) {
        arabicTexts = PREVIEW_AYAH_KEYS.associateWith { ayahKey ->
            runCatching {
                quranContentRepository(context).getAyahDisplay(ayahKey, scriptType.name)?.displayText
            }.getOrNull()
        }
    }
    LaunchedEffect(translationSelection) {
        val id = translationSelection.translationId
        translationTexts = if (id == null) {
            emptyMap()
        } else {
            PREVIEW_AYAH_KEYS.associateWith { ayahKey ->
                runCatching {
                    TranslationRepository(context).getAyah(id, ayahKey)
                        ?.takeIf { it.availability == TranslationAvailability.TRANSLATED }
                        ?.displayText
                }.getOrNull()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(palette.background, AmanahShapes.card)
            .padding(AmanahSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Preview · Al-Fatiha 1:1-4",
                style = MaterialTheme.typography.labelMedium,
                color = palette.secondaryText,
                modifier = Modifier.semantics { heading() },
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.xs),
                modifier = Modifier.clickable { showScriptComparison = !showScriptComparison },
            ) {
                Text(
                    text = "Compare scripts",
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.secondaryText,
                )
                Switch(
                    checked = showScriptComparison,
                    onCheckedChange = { showScriptComparison = it },
                )
            }
        }

        // Primary script preview
        PREVIEW_AYAH_KEYS.forEach { ayahKey ->
            val arabic = arabicTexts[ayahKey]
            val translation = translationTexts[ayahKey]
            
            if (arabic != null) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
                    translation?.let { trans ->
                        val direction = translationSelection.direction ?: TranslationDirection.LTR
                        CompositionLocalProvider(LocalLayoutDirection provides direction.toLayoutDirection()) {
                            Text(
                                text = trans,
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
        }

        // Script comparison (when enabled)
        if (showScriptComparison) {
            var comparisonTexts by remember { mutableStateOf<Map<String, String?>>(emptyMap()) }
            val otherScript = if (scriptType == ScriptType.INDOPAK) ScriptType.UTHMANI else ScriptType.INDOPAK
            
            LaunchedEffect(otherScript) {
                comparisonTexts = PREVIEW_AYAH_KEYS.associateWith { ayahKey ->
                    runCatching {
                        quranContentRepository(context).getAyahDisplay(ayahKey, otherScript.name)?.displayText
                    }.getOrNull()
                }
            }

            androidx.compose.material3.HorizontalDivider(
                modifier = Modifier.padding(vertical = AmanahSpacing.sm),
                color = palette.divider,
            )
            
            Text(
                text = "${otherScript.displayLabel} Script",
                style = MaterialTheme.typography.labelSmall,
                color = palette.secondaryText,
            )
            
            PREVIEW_AYAH_KEYS.forEach { ayahKey ->
                val arabic = comparisonTexts[ayahKey]
                if (arabic != null) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        Text(
                            text = arabic,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = arabicFontSizeSp.sp,
                                lineHeight = (arabicFontSizeSp * arabicLineSpacingMultiplier).sp,
                                letterSpacing = if (otherScript == ScriptType.INDOPAK) (-0.4).sp else 0.sp,
                                fontFamily = QuranFonts.getFontFamily(otherScript),
                            ),
                            color = palette.text,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

private val ScriptType.displayLabel: String
    get() = when (this) {
        ScriptType.INDOPAK -> "IndoPak"
        ScriptType.UTHMANI -> "Uthmani"
    }
