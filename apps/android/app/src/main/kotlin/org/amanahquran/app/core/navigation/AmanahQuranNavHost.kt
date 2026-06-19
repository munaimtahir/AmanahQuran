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
import org.amanahquran.app.core.model.PageReferenceType
import org.amanahquran.app.core.model.ReaderOpenMode
import org.amanahquran.app.feature.reader.SurahListScreen
import org.amanahquran.app.feature.reader.QuranNavigationScreen
import org.amanahquran.app.feature.reader.JuzListScreen
import org.amanahquran.app.feature.reader.PageListScreen
import org.amanahquran.app.feature.reader.SurahReaderScreen
import org.amanahquran.app.feature.reader.QuranReaderScreen
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
                onOpenReader = { navController.navigate(AppRoute.QuranNavigation) },
                onContinueReading = { item ->
                    navController.navigate(AppRoute.surahReader(item.surahNumber, item.ayahKey))
                },
                onOpenSearch = { navController.navigate(AppRoute.Search) },
                onOpenBookmarks = { navController.navigate(AppRoute.Bookmarks) },
                onOpenSettings = { navController.navigate(AppRoute.Settings) },
                onOpenTrustCenter = { navController.navigate(AppRoute.TrustCenter) },
            )
        }
        composable(AppRoute.QuranNavigation) {
            QuranNavigationScreen(
                onNavigateBack = { navController.popBackStack() },
                onOpenSurahList = { navController.navigate(AppRoute.SurahList) },
                onOpenJuzList = { navController.navigate(AppRoute.JuzList) },
                onOpenPageList = { navController.navigate(AppRoute.PageList) },
            )
        }
        composable(AppRoute.SurahList) {
            SurahListScreen(
                onOpenSurah = { surahNumber -> navController.navigate(AppRoute.surahReader(surahNumber)) },
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(AppRoute.JuzList) {
            JuzListScreen(
                onNavigateBack = { navController.popBackStack() },
                onOpenJuz = { juzNumber -> navController.navigate(AppRoute.juzReader(juzNumber)) },
            )
        }
        composable(AppRoute.PageList) {
            PageListScreen(
                onNavigateBack = { navController.popBackStack() },
                onOpenPage = { pageNumber, pageReferenceType -> navController.navigate(AppRoute.pageReader(pageNumber, pageReferenceType)) },
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
        composable(
            route = AppRoute.PageReader,
            arguments = listOf(
                navArgument(AppRoute.PageNumberArg) { type = NavType.IntType },
                navArgument(AppRoute.PageReferenceTypeArg) { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val pageNumber = backStackEntry.arguments?.getInt(AppRoute.PageNumberArg) ?: 1
            val pageReferenceType = runCatching {
                PageReferenceType.valueOf(backStackEntry.arguments?.getString(AppRoute.PageReferenceTypeArg).orEmpty())
            }.getOrDefault(PageReferenceType.INDOPAK)
            QuranReaderScreen(
                openMode = ReaderOpenMode.Page(pageNumber, pageReferenceType),
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(
            route = AppRoute.JuzReader,
            arguments = listOf(
                navArgument(AppRoute.JuzNumberArg) { type = NavType.IntType },
            ),
        ) { backStackEntry ->
            val juzNumber = backStackEntry.arguments?.getInt(AppRoute.JuzNumberArg) ?: 1
            QuranReaderScreen(
                openMode = ReaderOpenMode.Juz(juzNumber),
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(
            route = AppRoute.SurahReaderWithAyah,
            arguments = listOf(
                navArgument(AppRoute.SurahNumberArg) { type = NavType.IntType },
                navArgument(AppRoute.AyahKeyArg) {
                    type = NavType.StringType
                    defaultValue = ""
                },
            ),
        ) { backStackEntry ->
            val surahNumber = backStackEntry.arguments?.getInt(AppRoute.SurahNumberArg) ?: 1
            val ayahKey = backStackEntry.arguments?.getString(AppRoute.AyahKeyArg)?.takeIf { it.isNotBlank() }
            SurahReaderScreen(
                surahNumber = surahNumber,
                initialAyahKey = ayahKey,
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(AppRoute.Search) {
            SearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onOpenResult = { result ->
                    when (result.resultType) {
                        org.amanahquran.app.core.repository.SearchResultType.PAGE -> {
                            val pageNumber = result.pageNumber
                            if (pageNumber != null) {
                                val pageReferenceType = result.pageReferenceType ?: PageReferenceType.INDOPAK
                                navController.navigate(AppRoute.pageReader(pageNumber, pageReferenceType))
                            }
                        }
                        org.amanahquran.app.core.repository.SearchResultType.JUZ -> {
                            result.juzNumber?.let { juzNumber ->
                                navController.navigate(AppRoute.juzReader(juzNumber))
                            }
                        }
                        org.amanahquran.app.core.repository.SearchResultType.SURAH -> {
                            result.surahNumber?.let { surahNumber ->
                                navController.navigate(AppRoute.surahReader(surahNumber))
                            }
                        }
                        org.amanahquran.app.core.repository.SearchResultType.AYAH -> {
                            val surahNumber = result.surahNumber
                            if (surahNumber != null) {
                                navController.navigate(AppRoute.surahReader(surahNumber, result.ayahKey))
                            }
                        }
                    }
                },
            )
        }
        composable(AppRoute.Bookmarks) {
            BookmarksScreen(
                onNavigateBack = { navController.popBackStack() },
                onOpenBookmark = { item ->
                    when (item.record.bookmarkType) {
                        org.amanahquran.app.core.model.BookmarkType.AYAH -> {
                            navController.navigate(AppRoute.surahReader(item.record.surahNumber ?: 1, item.record.ayahKey))
                        }
                        org.amanahquran.app.core.model.BookmarkType.PAGE -> {
                            navController.navigate(
                                AppRoute.pageReader(
                                    item.record.pageNumber ?: 1,
                                    item.record.pageReferenceType ?: PageReferenceType.INDOPAK,
                                ),
                            )
                        }
                    }
                },
            )
        }
        composable(AppRoute.Settings) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onOpenTrustCenter = { navController.navigate(AppRoute.TrustCenter) },
            )
        }
        composable(AppRoute.TrustCenter) {
            TrustCenterScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(AppRoute.ContentProof) {
            ContentProofScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
