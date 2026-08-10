package org.amanahquran.app.core.navigation

import org.amanahquran.app.core.model.PageReferenceType
import org.amanahquran.app.core.model.ReaderAnchor

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
    const val ExactAyahReader = "reader/ayah/{$AyahKeyArg}"
    const val PageReader = "reader/page/{$PageNumberArg}/{$PageReferenceTypeArg}"
    const val JuzReader = "reader/juz/{$JuzNumberArg}"
    const val MushafReader = "reader/mushaf/{$PageNumberArg}"
    const val Search = "search"
    const val Bookmarks = "bookmarks"
    const val Settings = "settings"
    const val TrustCenter = "trust-center"
    const val ContentProof = "content-proof"
    const val ReadingStreak = "reading-streak"
    const val ReadingReminder = "reading-reminder"
    const val ReadingActivityDashboard = "reading-activity-dashboard"
    const val ReadingCalendar = "reading-calendar"

    fun surahReader(surahNumber: Int): String = "reader/surah/$surahNumber"

    fun exactAyahReader(ayahKey: String): String = "reader/ayah/${ayahKey.replace(":", "%3A")}"

    fun pageReader(pageNumber: Int, pageReferenceType: PageReferenceType): String {
        return "reader/page/$pageNumber/${pageReferenceType.name}"
    }

    fun juzReader(juzNumber: Int): String {
        return "reader/juz/$juzNumber"
    }

    fun mushafReader(pageNumber: Int): String {
        return "reader/mushaf/$pageNumber"
    }

    fun reader(anchor: ReaderAnchor): String = when (anchor) {
        is ReaderAnchor.SurahStart -> surahReader(anchor.surahNumber)
        is ReaderAnchor.ExactAyah -> exactAyahReader(anchor.ayahKey)
        is ReaderAnchor.PageStart -> pageReader(anchor.pageNumber, anchor.pageReferenceType)
        is ReaderAnchor.JuzStart -> juzReader(anchor.juzNumber)
    }

    val all = listOf(
        Home,
        QuranNavigation,
        SurahList,
        JuzList,
        PageList,
        SurahReader,
        ExactAyahReader,
        PageReader,
        JuzReader,
        MushafReader,
        Search,
        Bookmarks,
        Settings,
        TrustCenter,
        ContentProof,
        ReadingStreak,
        ReadingReminder,
        ReadingActivityDashboard,
        ReadingCalendar,
    )
}
