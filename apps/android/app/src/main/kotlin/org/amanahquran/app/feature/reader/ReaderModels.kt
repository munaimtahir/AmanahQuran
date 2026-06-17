package org.amanahquran.app.feature.reader

import org.amanahquran.app.core.model.ScriptType

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
)

data class ReaderUiState(
    val isLoading: Boolean = true,
    val selectedScript: ScriptType = ScriptType.INDOPAK,
    val surahNumber: Int = 1,
    val surahName: String = "",
    val ayahs: List<ReaderAyahUiModel> = emptyList(),
    val errorMessage: String? = null,
)

data class SurahListUiState(
    val isLoading: Boolean = true,
    val surahs: List<SurahListItem> = emptyList(),
    val errorMessage: String? = null,
)
