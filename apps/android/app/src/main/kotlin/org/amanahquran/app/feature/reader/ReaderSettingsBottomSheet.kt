package org.amanahquran.app.feature.reader

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import kotlin.math.roundToInt
import org.amanahquran.app.core.model.ReaderContentMode
import org.amanahquran.app.core.model.ReaderZoomLevel
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.model.TranslationSelection
import org.amanahquran.app.core.theme.AmanahSpacing
import org.amanahquran.app.core.theme.LocalElderMode
import org.amanahquran.app.core.theme.ThemeMode
import org.amanahquran.app.core.ui.AmanahDivider
import org.amanahquran.app.core.ui.AmanahScriptChip

/** Which size slider in the sheet the user moved. */
internal enum class ReaderSizeSlider { ARABIC, TRANSLATION }

/** Zoom levels to apply after a sheet size change; null means "leave unchanged". */
internal data class ReaderZoomChange(
    val arabic: ReaderZoomLevel?,
    val translation: ReaderZoomLevel?,
)

/**
 * Applies the "link Arabic and translation size" preference to a sheet slider change, matching
 * the pinch gesture: when linked (or no translation is shown) both sizes move together.
 */
internal fun resolveReaderZoomChange(
    slider: ReaderSizeSlider,
    target: ReaderZoomLevel,
    linked: Boolean,
    hasTranslation: Boolean,
): ReaderZoomChange = when {
    !hasTranslation -> ReaderZoomChange(arabic = target, translation = null)
    linked -> ReaderZoomChange(arabic = target, translation = target)
    slider == ReaderSizeSlider.ARABIC -> ReaderZoomChange(arabic = target, translation = null)
    else -> ReaderZoomChange(arabic = null, translation = target)
}

internal fun zoomLevelForSliderValue(value: Float): ReaderZoomLevel =
    ReaderZoomLevel.entries[value.roundToInt().coerceIn(0, ReaderZoomLevel.entries.lastIndex)]

internal fun ReaderZoomLevel.percentLabel(): String = "${(multiplier * 100).roundToInt()}%"

internal val ScriptType.displayLabel: String
    get() = when (this) {
        ScriptType.INDOPAK -> "IndoPak"
        ScriptType.UTHMANI -> "Uthmani"
    }

/**
 * In-reader settings: script, translation, sizes, reading mode and theme, without leaving the
 * reader. Size sliders update a local draft while dragging and only persist on release
 * ([onApplyZoom]), so DataStore isn't written on every drag tick and the caller can capture the
 * reading anchor once per change.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ReaderSettingsBottomSheet(
    onDismiss: () -> Unit,
    zoomLevel: ReaderZoomLevel,
    translationZoomLevel: ReaderZoomLevel,
    linkedZoomEnabled: Boolean,
    onSetLinkedZoomEnabled: (Boolean) -> Unit,
    onApplyZoom: (arabic: ReaderZoomLevel?, translation: ReaderZoomLevel?) -> Unit,
    contentMode: ReaderContentMode,
    onSetContentMode: (ReaderContentMode) -> Unit,
    hasTranslation: Boolean,
    selectedScript: ScriptType,
    onSelectScript: (ScriptType) -> Unit,
    translationSelection: TranslationSelection,
    onSelectTranslation: (TranslationSelection) -> Unit,
    selectedTheme: ThemeMode,
    onSelectTheme: (ThemeMode) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val elder = LocalElderMode.current
    val touchTarget = if (elder) AmanahSpacing.minTouchTargetElder else AmanahSpacing.minTouchTarget
    val chipsPerRow = if (elder) 2 else 4

    var arabicDraft by remember(zoomLevel) { mutableFloatStateOf(zoomLevel.ordinal.toFloat()) }
    var translationDraft by remember(translationZoomLevel) { mutableFloatStateOf(translationZoomLevel.ordinal.toFloat()) }

    fun commit(slider: ReaderSizeSlider, value: Float) {
        val change = resolveReaderZoomChange(slider, zoomLevelForSliderValue(value), linkedZoomEnabled, hasTranslation)
        val arabic = change.arabic?.takeIf { it != zoomLevel }
        val translation = change.translation?.takeIf { it != translationZoomLevel }
        if (arabic != null || translation != null) onApplyZoom(arabic, translation)
    }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = AmanahSpacing.md)
                .padding(bottom = AmanahSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(AmanahSpacing.md),
        ) {
            Text(
                "Reader Settings",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.semantics { heading() },
            )

            SheetSection("Arabic Script") {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.sm),
                    verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm),
                    maxItemsInEachRow = chipsPerRow,
                ) {
                    ScriptType.entries.forEach { script ->
                        AmanahScriptChip(
                            label = script.displayLabel,
                            selected = selectedScript == script,
                            onClick = { onSelectScript(script) },
                        )
                    }
                }
            }

            SheetSection("Translation") {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.sm),
                    verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm),
                    maxItemsInEachRow = chipsPerRow,
                ) {
                    TranslationSelection.entries.forEach { translation ->
                        AmanahScriptChip(
                            label = translation.shortLabel,
                            selected = translationSelection == translation,
                            onClick = { onSelectTranslation(translation) },
                        )
                    }
                }
            }

            val arabicLevel = zoomLevelForSliderValue(arabicDraft)
            SizeSlider(
                title = if (hasTranslation && linkedZoomEnabled) "Text Size" else "Arabic Text Size",
                valueLabel = arabicLevel.percentLabel(),
                value = arabicDraft,
                onValueChange = { value ->
                    arabicDraft = value
                    if (hasTranslation && linkedZoomEnabled) translationDraft = value
                },
                onValueChangeFinished = { commit(ReaderSizeSlider.ARABIC, arabicDraft) },
                touchTarget = touchTarget,
            )

            if (hasTranslation) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = touchTarget)
                        .toggleable(
                            value = linkedZoomEnabled,
                            role = Role.Switch,
                            onValueChange = onSetLinkedZoomEnabled,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        "Link Arabic and translation size",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f),
                    )
                    Switch(checked = linkedZoomEnabled, onCheckedChange = null)
                }
                if (!linkedZoomEnabled) {
                    SizeSlider(
                        title = "Translation Size",
                        valueLabel = zoomLevelForSliderValue(translationDraft).percentLabel(),
                        value = translationDraft,
                        onValueChange = { translationDraft = it },
                        onValueChangeFinished = { commit(ReaderSizeSlider.TRANSLATION, translationDraft) },
                        touchTarget = touchTarget,
                    )
                }
            }

            TextButton(
                onClick = {
                    arabicDraft = ReaderZoomLevel.default.ordinal.toFloat()
                    translationDraft = ReaderZoomLevel.default.ordinal.toFloat()
                    val arabic = ReaderZoomLevel.default.takeIf { it != zoomLevel }
                    val translation = ReaderZoomLevel.default.takeIf { hasTranslation && it != translationZoomLevel }
                    if (arabic != null || translation != null) onApplyZoom(arabic, translation)
                },
                modifier = Modifier.heightIn(min = touchTarget),
            ) { Text("Reset text size") }

            AmanahDivider()

            SheetSection("Reading Mode") {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.sm),
                    verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm),
                    maxItemsInEachRow = chipsPerRow,
                ) {
                    AmanahScriptChip(
                        label = "Continuous",
                        selected = contentMode == ReaderContentMode.CONTINUOUS,
                        onClick = { onSetContentMode(ReaderContentMode.CONTINUOUS) },
                    )
                    AmanahScriptChip(
                        label = "Ayah by Ayah",
                        selected = contentMode == ReaderContentMode.AYAH,
                        onClick = { onSetContentMode(ReaderContentMode.AYAH) },
                    )
                }
            }

            SheetSection("Theme") {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(AmanahSpacing.sm),
                    verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm),
                    maxItemsInEachRow = if (elder) 2 else 3,
                ) {
                    ThemeMode.entries.forEach { theme ->
                        AmanahScriptChip(
                            label = theme.displayName,
                            selected = selectedTheme == theme,
                            onClick = { onSelectTheme(theme) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SheetSection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(AmanahSpacing.sm)) {
        Text(
            title,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.semantics { heading() },
        )
        content()
    }
}

@Composable
private fun SizeSlider(
    title: String,
    valueLabel: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    touchTarget: androidx.compose.ui.unit.Dp,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Text(valueLabel, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            valueRange = 0f..ReaderZoomLevel.entries.lastIndex.toFloat(),
            steps = ReaderZoomLevel.entries.size - 2,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = touchTarget)
                .semantics {
                    contentDescription = title
                    stateDescription = valueLabel
                },
        )
    }
}
