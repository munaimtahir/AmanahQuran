package org.amanahquran.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.amanahquran.app.core.model.PageReferenceType
import org.amanahquran.app.core.model.ReaderAnchor
import org.amanahquran.app.core.model.ReaderOpenMode
import org.amanahquran.app.core.model.lastReadAnchor
import org.amanahquran.app.core.repository.lastReadRepository
import org.amanahquran.app.feature.bookmarks.BookmarksScreen
import org.amanahquran.app.feature.contentproof.ContentProofScreen
import org.amanahquran.app.feature.home.HomeScreen
import org.amanahquran.app.feature.reader.JuzListScreen
import org.amanahquran.app.feature.reader.PageListScreen
import org.amanahquran.app.feature.reader.QuranNavigationScreen
import org.amanahquran.app.feature.reader.QuranReaderScreen
import org.amanahquran.app.feature.reader.ReaderPerfLogger
import org.amanahquran.app.feature.reader.SurahListScreen
import org.amanahquran.app.feature.reader.SurahReaderScreen
import org.amanahquran.app.feature.reader.mushaf.MushafPageScreen
import org.amanahquran.app.feature.search.SearchScreen
import org.amanahquran.app.feature.reminder.ReadingReminderScreen
import org.amanahquran.app.feature.settings.SettingsScreen
import org.amanahquran.app.feature.streak.ReadingStreakScreen
import org.amanahquran.app.feature.trust.TrustCenterScreen

@Composable
fun AmanahQuranNavHost(
    modifier: Modifier = Modifier,
    pendingDeepLink: DeepLinkRequest? = null,
    onDeepLinkConsumed: () -> Unit = {},
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(pendingDeepLink) {
        if (pendingDeepLink is DeepLinkRequest.ContinueReading) {
            val lastRead = lastReadRepository(context).getLastRead().first()
            val anchor = lastRead?.let { lastReadAnchor(it.ayahKey, it.pageNumber, it.scriptType) }
            if (anchor != null) {
                navController.navigate(AppRoute.reader(anchor)) { launchSingleTop = true }
            }
            onDeepLinkConsumed()
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppRoute.Home,
        modifier = modifier,
    ) {
        composable(AppRoute.Home) {
            HomeScreen(
                onContinueReading = { continueReading ->
                    ReaderPerfLogger.log(
                        "continue_click",
                        detail = "ayah=${continueReading.ayahKey} page=${continueReading.pageNumber}",
                    )
                    ReaderPerfLogger.log("read_last_read_start", detail = "cached_home_state=true")
                    ReaderPerfLogger.log("read_last_read_end", detail = "ayah=${continueReading.ayahKey}")
                    ReaderPerfLogger.log("read_script_setting_start", detail = "cached_home_state=true")
                    ReaderPerfLogger.log("read_script_setting_end")
                    ReaderPerfLogger.log("route_start", detail = "anchor=ExactAyah(${continueReading.ayahKey})")
                    navController.navigate(AppRoute.reader(ReaderAnchor.ExactAyah(continueReading.ayahKey)))
                },
                onOpenMushafReader = { pageNumber, scriptType ->
                    val pageReferenceType = when (scriptType) {
                        org.amanahquran.app.core.model.ScriptType.INDOPAK -> PageReferenceType.INDOPAK
                        org.amanahquran.app.core.model.ScriptType.UTHMANI -> PageReferenceType.UTHMANI
                    }
                    ReaderPerfLogger.log("nav_click", detail = "home->reader page=$pageNumber script=${scriptType.name}")
                    navController.navigate(
                        AppRoute.reader(ReaderAnchor.PageStart(pageNumber, pageReferenceType))
                    )
                },
                onOpenSurahList = {
                    ReaderPerfLogger.log("nav_click", detail = "home->surah_list")
                    navController.navigate(AppRoute.SurahList)
                },
                onOpenJuzList = {
                    ReaderPerfLogger.log("nav_click", detail = "home->juz_list")
                    navController.navigate(AppRoute.JuzList)
                },
                onOpenPageList = {
                    ReaderPerfLogger.log("nav_click", detail = "home->page_list")
                    navController.navigate(AppRoute.PageList)
                },
                onOpenSearch = {
                    ReaderPerfLogger.log("nav_click", detail = "home->search")
                    navController.navigate(AppRoute.Search)
                },
                onOpenBookmarks = {
                    ReaderPerfLogger.log("nav_click", detail = "home->bookmarks")
                    navController.navigate(AppRoute.Bookmarks)
                },
                onOpenSettings = {
                    ReaderPerfLogger.log("nav_click", detail = "home->settings")
                    navController.navigate(AppRoute.Settings)
                },
                onOpenTrustCenter = {
                    ReaderPerfLogger.log("nav_click", detail = "home->trust_center")
                    navController.navigate(AppRoute.TrustCenter)
                },
                onOpenReadingStreak = {
                    ReaderPerfLogger.log("nav_click", detail = "home->reading_streak")
                    navController.navigate(AppRoute.ReadingStreak)
                },
            )
        }
        composable(AppRoute.ReadingStreak) {
            ReadingStreakScreen(onNavigateBack = { navController.popBackStack() })
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
                onOpenSurah = { surahNumber ->
                    ReaderPerfLogger.log("nav_click", detail = "surah_list->reader surah=$surahNumber")
                    navController.navigate(AppRoute.surahReader(surahNumber))
                },
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(AppRoute.JuzList) {
            JuzListScreen(
                onNavigateBack = { navController.popBackStack() },
                onOpenJuz = { juzNumber ->
                    ReaderPerfLogger.log("nav_click", detail = "juz_list->reader juz=$juzNumber")
                    navController.navigate(AppRoute.juzReader(juzNumber))
                },
            )
        }
        composable(AppRoute.PageList) {
            PageListScreen(
                onNavigateBack = { navController.popBackStack() },
                onOpenPage = { pageNumber, pageReferenceType ->
                    ReaderPerfLogger.log("nav_click", detail = "page_list->reader page=$pageNumber script=${pageReferenceType.name}")
                    navController.navigate(AppRoute.pageReader(pageNumber, pageReferenceType))
                },
            )
        }
        composable(
            route = AppRoute.MushafReader,
            arguments = listOf(navArgument(AppRoute.PageNumberArg) { type = NavType.IntType }),
        ) { backStackEntry ->
            val pageNumber = backStackEntry.arguments?.getInt(AppRoute.PageNumberArg) ?: 1
            MushafPageScreen(
                initialPageNumber = pageNumber,
                onBack = { navController.popBackStack() },
                onOpenSettings = { navController.navigate(AppRoute.Settings) }
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
            route = AppRoute.ExactAyahReader,
            arguments = listOf(navArgument(AppRoute.AyahKeyArg) { type = NavType.StringType }),
        ) { backStackEntry ->
            val ayahKey = backStackEntry.arguments?.getString(AppRoute.AyahKeyArg).orEmpty()
            QuranReaderScreen(
                anchor = ReaderAnchor.ExactAyah(ayahKey),
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable(AppRoute.Search) {
            SearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onOpenResult = { result ->
                    scope.launch {
                        result.toReaderAnchor()?.let { anchor ->
                            ReaderPerfLogger.log("nav_click", detail = "search->reader anchor=$anchor")
                            navController.navigate(AppRoute.reader(anchor))
                        }
                    }
                },
            )
        }
        composable(AppRoute.Bookmarks) {
            BookmarksScreen(
                onNavigateBack = { navController.popBackStack() },
                onOpenBookmark = { item ->
                    scope.launch {
                        item.record.toReaderAnchor()?.let { anchor ->
                            ReaderPerfLogger.log("nav_click", detail = "bookmark->reader anchor=$anchor")
                            navController.navigate(AppRoute.reader(anchor))
                        }
                    }
                },
            )
        }
        composable(AppRoute.Settings) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onOpenTrustCenter = { navController.navigate(AppRoute.TrustCenter) },
                onOpenReadingReminder = { navController.navigate(AppRoute.ReadingReminder) },
            )
        }
        composable(AppRoute.ReadingReminder) {
            ReadingReminderScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(AppRoute.TrustCenter) {
            TrustCenterScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(AppRoute.ContentProof) {
            ContentProofScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
