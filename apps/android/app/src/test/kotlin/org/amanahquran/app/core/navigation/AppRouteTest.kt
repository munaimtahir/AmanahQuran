package org.amanahquran.app.core.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class AppRouteTest {
    @Test
    fun allRoutes_coverTheExpectedScreens() {
        assertEquals(
            listOf(
                AppRoute.Home,
                AppRoute.Reader,
                AppRoute.Search,
                AppRoute.Bookmarks,
                AppRoute.Settings,
                AppRoute.TrustCenter,
            ),
            AppRoute.all,
        )
    }
}

