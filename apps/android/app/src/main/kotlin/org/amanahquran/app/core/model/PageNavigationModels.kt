package org.amanahquran.app.core.model

data class JuzListItem(
    val juzNumber: Int,
    val startAyahKey: String,
    val endAyahKey: String?,
    val startSurahNumber: Int,
    val startSurahName: String,
    val endSurahNumber: Int?,
    val endSurahName: String?,
    val ayahCount: Int,
)

data class PageListItem(
    val pageNumber: Int,
    val pageReferenceType: PageReferenceType,
    val startAyahKey: String,
    val endAyahKey: String?,
    val startSurahNumber: Int,
    val startSurahName: String,
    val endSurahNumber: Int?,
    val endSurahName: String?,
    val ayahCount: Int,
)

enum class PageReferenceType(
    val layoutName: String,
) {
    INDOPAK("IndoPak 15-line Qudratullah"),
    UTHMANI("KFGQPC V2 1421H");

    companion object {
        fun fromLayoutName(layoutName: String): PageReferenceType? {
            return entries.firstOrNull { it.layoutName == layoutName }
        }
    }
}

sealed class ReaderOpenMode {
    data class Surah(val surahNumber: Int) : ReaderOpenMode()
    data class Page(val pageNumber: Int, val pageReferenceType: PageReferenceType) : ReaderOpenMode()
    data class Juz(val juzNumber: Int) : ReaderOpenMode()
    data class AyahTarget(val surahNumber: Int, val ayahKey: String) : ReaderOpenMode()
}

data class PageReaderUiState(
    val isLoading: Boolean = true,
    val pageNumber: Int = 1,
    val pageReferenceType: PageReferenceType = PageReferenceType.INDOPAK,
    val selectedScript: ScriptType = ScriptType.INDOPAK,
    val targetAyahKey: String? = null,
    val ayahs: List<org.amanahquran.app.feature.reader.ReaderAyahUiModel> = emptyList(),
    val errorMessage: String? = null,
)

data class JuzReaderUiState(
    val isLoading: Boolean = true,
    val juzNumber: Int = 1,
    val selectedScript: ScriptType = ScriptType.INDOPAK,
    val targetAyahKey: String? = null,
    val ayahs: List<org.amanahquran.app.feature.reader.ReaderAyahUiModel> = emptyList(),
    val errorMessage: String? = null,
)
