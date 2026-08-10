package org.amanahquran.app.feature.reader.mushaf

import org.amanahquran.app.core.model.ScriptType
import org.amanahquran.app.core.repository.MushafPageUi
import org.amanahquran.app.core.repository.MushafLineUi

data class MushafReaderUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val pageNumber: Int = 1,
    val pageCount: Int = 604,
    val scriptType: ScriptType = ScriptType.INDOPAK,
    val page: MushafPageUi? = null,
    val lines: List<MushafLineUi> = emptyList(),
    val isPageBookmarked: Boolean = false,
    val fontScale: Float = 1.0f,
    val isFullScreen: Boolean = true,
    val elderModeEnabled: Boolean = false,
    val bookModeEnabled: Boolean = false,
    val keepScreenAwakeEnabled: Boolean = false,
)
