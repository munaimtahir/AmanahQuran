package org.amanahquran.app.feature.reader

import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.model.ReaderOpenMode

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
    val displayText: String,
    val scriptType: ScriptType,
    val isSelected: Boolean = false,
    val isBookmarked: Boolean = false,
)

data class ReaderUiState(
    val isLoading: Boolean = true,
    val selectedScript: ScriptType = ScriptType.INDOPAK,
    val arabicFontSizeSp: Float = 24f,
    val elderModeEnabled: Boolean = false,
    val openMode: ReaderOpenMode = ReaderOpenMode.Surah(1),
    val modeTitle: String = "Surah 1",
    val surahNumber: Int = 1,
    val surahName: String = "",
    val ayahs: List<ReaderAyahUiModel> = emptyList(),
    val selectedAyahKey: String? = null,
    val isPageBookmarked: Boolean = false,
    val errorMessage: String? = null,
)

data class SurahListUiState(
    val isLoading: Boolean = true,
    val surahs: List<SurahListItem> = emptyList(),
    val errorMessage: String? = null,
)
