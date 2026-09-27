# Relevant UI Source Files

This reference maps every audited user surface to its source implementation in the repository.

| Screen / Feature | Source File Path | Key Composable / Component | Navigation Route | Design System Components |
|---|---|---|---|---|
| Application Entry | `apps/android/app/src/main/kotlin/org/amanahquran/app/MainActivity.kt` | `MainActivity` | Entry point | EdgeToEdge, Compose View |
| Navigation Host | `apps/android/app/src/main/kotlin/org/amanahquran/app/core/navigation/AmanahQuranNavHost.kt` | `AmanahQuranNavHost` | Root NavHost | Jetpack Navigation Compose |
| Home Dashboard | `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/home/HomeScreen.kt` | `HomeScreen` | `AppRoute.Home` | `AmanahCard`, `TopAppBar`, `DailyAyahCard` |
| Surah Index | `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/reader/SurahListScreen.kt` | `SurahListScreen` | `AppRoute.SurahList` | `LazyColumn`, `AmanahCard`, `TopAppBar` |
| Juz Index | `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/reader/JuzListScreen.kt` | `JuzListScreen` | `AppRoute.JuzList` | `LazyColumn`, `AmanahCard`, `TopAppBar` |
| Page Index | `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/reader/PageListScreen.kt` | `PageListScreen` | `AppRoute.PageList` | `LazyColumn`, `AmanahCard`, `TopAppBar` |
| Surah Reader | `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/reader/SurahReaderScreen.kt` | `SurahReaderScreen` | `AppRoute.SurahReader` | `QuranText`, `ReaderSelectedAyahActionCard` |
| Quran Reader (Generic) | `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/reader/QuranReaderScreen.kt` | `QuranReaderScreen` | `AppRoute.ExactAyahReader` / `PageReader` | `ContinuousReaderRenderer`, `MushafLine` |
| Mushaf Page Reader | `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/reader/mushaf/MushafPageScreen.kt` | `MushafPageScreen` | `AppRoute.MushafReader` | `PageBookmarkRow`, `MushafLine` |
| Offline Search | `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/search/SearchScreen.kt` | `SearchScreen` | `AppRoute.Search` | `TextField`, `AmanahCard`, `LazyColumn` |
| Bookmarks Collection | `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/bookmarks/BookmarksScreen.kt` | `BookmarksScreen` | `AppRoute.Bookmarks` | `LazyColumn`, `AmanahCard`, `EmptyState` |
| Settings Screen | `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/settings/SettingsScreen.kt` | `SettingsScreen` | `AppRoute.Settings` | `AmanahScriptChip`, `AmanahSlider`, `Switch` |
| Reading Reminders | `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/reminder/ReadingReminderScreen.kt` | `ReadingReminderScreen` | `AppRoute.ReadingReminder` | `TimePicker`, `Switch`, `AmanahCard` |
| Advanced Reader Settings | `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/settings/AdvancedReaderSettingsScreen.kt` | `AdvancedReaderSettingsScreen` | `AppRoute.AdvancedReaderSettings` | `AmanahSettingsRow`, `Slider` |
| Reset Reading Settings | `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/settings/ResetReadingSettingsScreen.kt` | `ResetReadingSettingsScreen` | `AppRoute.ResetReadingSettings` | `Button`, `AmanahCard` |
| Trust Center | `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/trust/TrustCenterScreen.kt` | `TrustCenterScreen` | `AppRoute.TrustCenter` | `AmanahSectionCard`, `TopAppBar` |
| Reading Streak | `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/streak/ReadingStreakScreen.kt` | `ReadingStreakScreen` | `AppRoute.ReadingStreak` | `AmanahCard`, `Button` |
| Reading Calendar | `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/calendar/ReadingCalendarScreen.kt` | `ReadingCalendarScreen` | `AppRoute.ReadingCalendar` | `CalendarGrid`, `TopAppBar` |
| Daily Ayah History | `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/daily/DailyAyahHistoryScreen.kt` | `DailyAyahHistoryScreen` | `AppRoute.DailyAyahHistory` | `LazyColumn`, `AmanahCard` |
| Reading Activity Dashboard | `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/stats/ReadingActivityDashboardScreen.kt` | `ReadingActivityDashboardScreen` | `AppRoute.ReadingActivityDashboard` | `StatsCard`, `ChartRow` |
| Content Proof | `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/contentproof/ContentProofScreen.kt` | `ContentProofScreen` | `AppRoute.ContentProof` | `VerificationCard` |
