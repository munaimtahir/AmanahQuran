package org.amanahquran.app.feature.reader

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.amanahquran.app.core.model.ReaderOpenMode
import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.theme.AmanahGoldMuted
import org.amanahquran.app.core.theme.AmanahSpacing
import org.amanahquran.app.core.theme.LocalElderMode
import org.amanahquran.app.core.theme.QuranFonts

sealed interface ReaderStructuralItem {
    data class JuzHeader(
        val juzNumber: Int,
        val startingReference: String,
    ) : ReaderStructuralItem

    data class SurahHeader(
        val surahNumber: Int,
        val arabicName: String,
        val simpleName: String,
        val ayahCount: Int?,
        val scriptType: ScriptType,
    ) : ReaderStructuralItem

    data class Bismillah(
        val surahNumber: Int,
        val scriptType: ScriptType,
    ) : ReaderStructuralItem

    data class Ayah(
        val ayah: ReaderAyahUiModel,
    ) : ReaderStructuralItem
}

fun buildReaderStructuralItems(
    ayahs: List<ReaderAyahUiModel>,
    openMode: ReaderOpenMode,
    showLeadingJuzHeader: Boolean = false,
): List<ReaderStructuralItem> {
    if (ayahs.isEmpty()) return emptyList()

    val items = mutableListOf<ReaderStructuralItem>()
    var previousJuzNumber: Int? = null

    ayahs.forEachIndexed { index, ayah ->
        val isFirstItem = index == 0
        val juzChanged = previousJuzNumber != null && ayah.juzNumber != previousJuzNumber

        if (isFirstItem && (openMode is ReaderOpenMode.Juz || showLeadingJuzHeader)) {
            items += ReaderStructuralItem.JuzHeader(
                juzNumber = ayah.juzNumber,
                startingReference = ayah.startingReferenceLabel(),
            )
        } else if (!isFirstItem && juzChanged) {
            items += ReaderStructuralItem.JuzHeader(
                juzNumber = ayah.juzNumber,
                startingReference = ayah.startingReferenceLabel(),
            )
        }

        if (ayah.ayahNumber == 1) {
            items += ReaderStructuralItem.SurahHeader(
                surahNumber = ayah.surahNumber,
                arabicName = ayah.surahNameArabic,
                simpleName = ayah.surahNameSimple,
                ayahCount = ayah.surahAyahCount,
                scriptType = ayah.scriptType,
            )
            if (shouldRenderBismillah(ayah)) {
                items += ReaderStructuralItem.Bismillah(
                    surahNumber = ayah.surahNumber,
                    scriptType = ayah.scriptType,
                )
            }
        }

        items += ReaderStructuralItem.Ayah(ayah)

        previousJuzNumber = ayah.juzNumber
    }

    return items
}

fun ReaderStructuralItem.key(): String = when (this) {
    is ReaderStructuralItem.JuzHeader -> "juz-header-$juzNumber-$startingReference"
    is ReaderStructuralItem.SurahHeader -> "surah-header-$surahNumber"
    is ReaderStructuralItem.Bismillah -> "bismillah-$surahNumber"
    is ReaderStructuralItem.Ayah -> "ayah-${ayah.ayahKey}-${ayah.scriptType.name}"
}

@Composable
fun ReaderStructuralContent(item: ReaderStructuralItem) {
    when (item) {
        is ReaderStructuralItem.JuzHeader -> ReaderJuzHeader(item)
        is ReaderStructuralItem.SurahHeader -> ReaderSurahHeader(item)
        is ReaderStructuralItem.Bismillah -> ReaderBismillah(item)
        is ReaderStructuralItem.Ayah -> Unit
    }
}

@Composable
private fun ReaderSurahHeader(item: ReaderStructuralItem.SurahHeader) {
    val elder = LocalElderMode.current
    val topPadding = if (elder) 22.dp else 18.dp
    val bottomPadding = if (elder) 12.dp else 10.dp
    val arabicSize = if (elder) 29.sp else 26.sp
    val simpleSize = if (elder) 16.sp else 14.sp
    val metaSize = if (elder) 13.sp else 11.sp
    val fontFamily = QuranFonts.getFontFamily(item.scriptType)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPadding, bottom = bottomPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AmanahSpacing.xs),
    ) {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(0.72f),
            color = AmanahGoldMuted.copy(alpha = 0.48f),
            thickness = 0.75.dp,
        )
        Text(
            text = item.simpleName,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = simpleSize,
                fontWeight = FontWeight.Medium,
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Text(
            text = item.arabicName.ifBlank { item.simpleName },
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = arabicSize,
                fontWeight = FontWeight.SemiBold,
            ),
            fontFamily = fontFamily,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Surah ${item.surahNumber}",
                style = MaterialTheme.typography.labelMedium.copy(fontSize = metaSize),
                color = MaterialTheme.colorScheme.primary,
            )
            if (item.ayahCount != null) {
                Text(
                    text = " · ${item.ayahCount} ayahs",
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = metaSize),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(0.72f),
            color = AmanahGoldMuted.copy(alpha = 0.32f),
            thickness = 0.75.dp,
        )
    }
}

@Composable
private fun ReaderJuzHeader(item: ReaderStructuralItem.JuzHeader) {
    val elder = LocalElderMode.current
    val topPadding = if (elder) 20.dp else 16.dp
    val bottomPadding = if (elder) 10.dp else 8.dp
    val titleSize = if (elder) 17.sp else 15.sp
    val referenceSize = if (elder) 13.sp else 11.sp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPadding, bottom = bottomPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = "Para ${item.juzNumber} · Juz ${item.juzNumber}",
            style = MaterialTheme.typography.titleSmall.copy(
                fontSize = titleSize,
                fontWeight = FontWeight.SemiBold,
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Text(
            text = item.startingReference,
            style = MaterialTheme.typography.labelMedium.copy(fontSize = referenceSize),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(0.58f),
            color = AmanahGoldMuted.copy(alpha = 0.32f),
            thickness = 0.75.dp,
        )
    }
}

@Composable
private fun ReaderBismillah(item: ReaderStructuralItem.Bismillah) {
    val elder = LocalElderMode.current
    val fontFamily = QuranFonts.getFontFamily(item.scriptType)
    val fontSize = if (elder) 24.sp else 20.sp
    val topPadding = if (elder) 2.dp else 0.dp
    val bottomPadding = if (elder) 18.dp else 16.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPadding, bottom = bottomPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        // Structural presentation element only; Quran display text stays untouched.
        Text(
            text = bismillahText(item.scriptType),
            modifier = Modifier.sizeIn(minHeight = 24.dp),
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = fontSize,
                fontWeight = FontWeight.Medium,
            ),
            fontFamily = fontFamily,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
    }
}

private fun ReaderAyahUiModel.startingReferenceLabel(): String {
    return "${surahNameSimple.ifBlank { "Surah $surahNumber" }} $surahNumber:$ayahNumber"
}

private fun shouldRenderBismillah(ayah: ReaderAyahUiModel): Boolean {
    if (ayah.ayahNumber != 1) return false
    if (ayah.surahNumber == 9) return false
    return !looksLikeBismillah(ayah.displayText)
}

private fun looksLikeBismillah(displayText: String): Boolean {
    val normalized = displayText
        .replace(Regex("[\\u064B-\\u065F\\u0670\\u0640]"), "")
        .replace(Regex("\\s+"), " ")
        .trim()
    return normalized.startsWith("بسم الله") ||
        normalized.startsWith("بسم ٱلله") ||
        normalized.startsWith("بِسْمِ ٱللَّهِ")
}

private fun bismillahText(scriptType: ScriptType): String {
    return when (scriptType) {
        ScriptType.INDOPAK -> "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّحِيْمِ"
        ScriptType.UTHMANI -> "بِسْمِ ٱللَّهِ ٱلرَّحْمَـٰنِ ٱلرَّحِيمِ"
    }
}
