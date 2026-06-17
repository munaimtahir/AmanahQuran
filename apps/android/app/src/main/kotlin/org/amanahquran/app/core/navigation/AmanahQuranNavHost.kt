package org.amanahquran.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.amanahquran.app.feature.bookmarks.BookmarksScreen
import org.amanahquran.app.feature.contentproof.ContentProofScreen
import org.amanahquran.app.feature.home.HomeScreen
import org.amanahquran.app.feature.reader.SurahListScreen
import org.amanahquran.app.feature.reader.SurahReaderScreen
import org.amanahquran.app.feature.search.SearchScreen
import org.amanahquran.app.feature.settings.SettingsScreen
import org.amanahquran.app.feature.trust.TrustCenterScreen

@Composable
fun AmanahQuranNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoute.Home,
        modifier = modifier,
    ) {
        composable(AppRoute.Home) {
            HomeScreen(
                onOpenReader = { navController.navigate(AppRoute.SurahList) },
                onOpenSearch = { navController.navigate(AppRoute.Search) },
                onOpenBookmarks = { navController.navigate(AppRoute.Bookmarks) },
                onOpenSettings = { navController.navigate(AppRoute.Settings) },
                onOpenTrustCenter = { navController.navigate(AppRoute.TrustCenter) },
            )
        }
        composable(AppRoute.SurahList) {
            SurahListScreen(
                onOpenSurah = { surahNumber -> navController.navigate(AppRoute.surahReader(surahNumber)) },
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(
            route = AppRoute.SurahReader,
            arguments = listOf(navArgument(AppRoute.SurahNumberArg) { type = NavType.IntType }),
        ) { backStackEntry ->
            val surahNumber = backStackEntry.arguments?.getInt(AppRoute.SurahNumberArg) ?: 1
            SurahReaderScreen(
                surahNumber = surahNumber,
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(AppRoute.Search) {
            SearchScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(AppRoute.Bookmarks) {
            BookmarksScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(AppRoute.Settings) {
            SettingsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(AppRoute.TrustCenter) {
            TrustCenterScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(AppRoute.ContentProof) {
            ContentProofScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
