package org.amanahquran.app.feature.reader

import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.model.ReaderOpenMode
import org.amanahquran.app.core.model.ReaderAnchor
import org.amanahquran.app.core.model.AutoScrollPace
import org.amanahquran.app.core.model.ReaderContentMode
import org.amanahquran.app.core.model.ReaderZoomLevel

data class SurahListItem(
    val surahNumber: Int,
    val arabicName: String,
    val simpleName: String,
    val ayahCount: Int,
    val revelationType: String?,
)

data class ReaderAyahUiModel(
    val ayahKey: String,
    val surahNumber: Int,
    val ayahNumber: Int,
    val juzNumber: Int,
    val pageNumber: Int,
    val displayText: String,
    val scriptType: ScriptType,
    val surahNameArabic: String = "",
    val surahNameSimple: String = "",
    val surahAyahCount: Int? = null,
    val isSelected: Boolean = false,
    val isBookmarked: Boolean = false,
)

data class ReaderUiState(
    val isLoading: Boolean = true,
    val selectedScript: ScriptType = ScriptType.INDOPAK,
    val arabicFontSizeSp: Float = 24f,
    val elderModeEnabled: Boolean = false,
    val bookModeEnabled: Boolean = false,
    val openMode: ReaderOpenMode = ReaderOpenMode.Surah(1),
    val anchor: ReaderAnchor = ReaderAnchor.SurahStart(1),
    val modeTitle: String = "Surah 1",
    val surahNumber: Int = 1,
    val surahName: String = "",
    val pageSurahName: String = "",
    val surahNameArabic: String = "",
    val juzNumber: Int = 1,
    val ayahs: List<ReaderAyahUiModel> = emptyList(),
    val readerBlocks: List<ReaderStructuralItem> = emptyList(),
    val readerOpenStartedAtMs: Long? = null,
    val selectedAyahKey: String? = null,
    val anchorScrollIndex: Int? = null,
    val anchorScrollRequestId: Long = 0,
    val isPageBookmarked: Boolean = false,
    val errorMessage: String? = null,
    val translationEnabled: Boolean = false,
    val translationFontSizeSp: Float = 18f,
    val translations: Map<String, String> = emptyMap(),
    val arabicLineSpacingMultiplier: Float = 1.88f,
    val readerHorizontalPaddingDp: Float = 16f,
    val zoomLevel: ReaderZoomLevel = ReaderZoomLevel.default,
    val autoScrollPace: AutoScrollPace = AutoScrollPace.default,
    val firstZoomHintShown: Boolean = false,
    val pinchToResizeEnabled: Boolean = true,
    val contentMode: ReaderContentMode = ReaderContentMode.default,
    val translationZoomLevel: ReaderZoomLevel = ReaderZoomLevel.default,
    val linkedZoomEnabled: Boolean = true,
)

data class SurahListUiState(
    val isLoading: Boolean = true,
    val surahs: List<SurahListItem> = emptyList(),
    val errorMessage: String? = null,
)
