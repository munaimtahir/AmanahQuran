package org.amanahquran.app.core.navigation

import org.junit.Assert.assertEquals
import org.junit.Test
import org.amanahquran.app.core.model.ReaderAnchor

class AppRouteTest {
    @Test
    fun allRoutes_coverTheExpectedScreens() {
        assertEquals(
            listOf(
                AppRoute.Home,
                AppRoute.QuranNavigation,
                AppRoute.SurahList,
                AppRoute.JuzList,
                AppRoute.PageList,
                AppRoute.SurahReader,
                AppRoute.ExactAyahReader,
                AppRoute.PageReader,
                AppRoute.JuzReader,
                AppRoute.MushafReader,
                AppRoute.Search,
                AppRoute.Bookmarks,
                AppRoute.Settings,
                AppRoute.TrustCenter,
                AppRoute.ContentProof,
                AppRoute.ReadingStreak,
                AppRoute.ReadingReminder,
                AppRoute.ReadingActivityDashboard,
                AppRoute.ReadingCalendar,
                AppRoute.AdvancedReaderSettings,
                AppRoute.ResetReadingSettings,
            ),
            AppRoute.all,
        )
    }

    @Test
    fun surahReader_buildsConcreteRoute() {
        assertEquals("reader/surah/2", AppRoute.surahReader(2))
    }

    @Test
    fun exactAyahReader_buildsDistinctCanonicalRoute() {
        assertEquals("reader/ayah/2%3A255", AppRoute.reader(ReaderAnchor.ExactAyah("2:255")))
    }
}
