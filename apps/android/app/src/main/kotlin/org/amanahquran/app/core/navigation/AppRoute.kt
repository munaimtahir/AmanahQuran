package org.amanahquran.app.core.navigation

object AppRoute {
    const val Home = "home"
    const val SurahList = "surah-list"
    const val Reader = SurahList
    const val SurahNumberArg = "surahNumber"
    const val SurahReader = "reader/surah/{$SurahNumberArg}"
    const val Search = "search"
    const val Bookmarks = "bookmarks"
    const val Settings = "settings"
    const val TrustCenter = "trust-center"
    const val ContentProof = "content-proof"

    fun surahReader(surahNumber: Int): String = "reader/surah/$surahNumber"

    val all = listOf(Home, SurahList, SurahReader, Search, Bookmarks, Settings, TrustCenter, ContentProof)
}
