package org.amanahquran.app.core.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

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
                AppRoute.PageReader,
                AppRoute.JuzReader,
                AppRoute.Search,
                AppRoute.Bookmarks,
                AppRoute.Settings,
                AppRoute.TrustCenter,
                AppRoute.ContentProof,
            ),
            AppRoute.all,
        )
    }

    @Test
    fun surahReader_buildsConcreteRoute() {
        assertEquals("reader/surah/2", AppRoute.surahReader(2))
    }
}
