package org.amanahquran.app.core.navigation

import org.amanahquran.app.core.model.PageReferenceType

object AppRoute {
    const val Home = "home"
    const val QuranNavigation = "quran-navigation"
    const val SurahList = "surah-list"
    const val Reader = SurahList
    const val SurahNumberArg = "surahNumber"
    const val AyahKeyArg = "ayahKey"
    const val PageNumberArg = "pageNumber"
    const val PageReferenceTypeArg = "pageReferenceType"
    const val JuzNumberArg = "juzNumber"
    const val JuzList = "juz-list"
    const val PageList = "page-list"
    const val SurahReader = "reader/surah/{$SurahNumberArg}"
    const val SurahReaderWithAyah = "reader/surah/{$SurahNumberArg}?$AyahKeyArg={$AyahKeyArg}"
    const val PageReader = "reader/page/{$PageNumberArg}/{$PageReferenceTypeArg}"
    const val JuzReader = "reader/juz/{$JuzNumberArg}"
    const val Search = "search"
    const val Bookmarks = "bookmarks"
    const val Settings = "settings"
    const val TrustCenter = "trust-center"
    const val ContentProof = "content-proof"

    fun surahReader(surahNumber: Int, ayahKey: String? = null): String {
        return if (ayahKey.isNullOrBlank()) {
            "reader/surah/$surahNumber"
        } else {
            "reader/surah/$surahNumber?$AyahKeyArg=$ayahKey"
        }
    }

    fun pageReader(pageNumber: Int, pageReferenceType: PageReferenceType): String {
        return "reader/page/$pageNumber/${pageReferenceType.name}"
    }

    fun juzReader(juzNumber: Int): String {
        return "reader/juz/$juzNumber"
    }

    val all = listOf(
        Home,
        QuranNavigation,
        SurahList,
        JuzList,
        PageList,
        SurahReader,
        PageReader,
        JuzReader,
        Search,
        Bookmarks,
        Settings,
        TrustCenter,
        ContentProof,
    )
}
