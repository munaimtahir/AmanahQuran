# UI Inventory — Application Screens

This inventory catalogs all visual surfaces discovered in **Amanah Quran (v2.2.0, Build 11)** during the systematic emulator audit.

---

### S001 — Splash & Launch Transition
- **Screen ID**: S001
- **Screen name**: Minimal Launch Splash
- **Entry point**: App icon tap / OS cold launch
- **Primary purpose**: Provide instantaneous visual feedback while Compose UI initializes.
- **Main UI components**: System window background, theme background color.
- **Navigation options**: Automatic transition to Home.
- **Interactive elements**: None (transitory).
- **Scrollable**: No
- **Arabic text present**: No
- **English/Urdu/translation text present**: No
- **Audio controls present**: None
- **Special states**: Transitory launch state (~200ms).
- **Screenshot filenames**: `SCREENSHOTS/01_launch/S001_launch_splash.png`
- **Workflow video**: `VIDEOS/workflow_01_first_launch/first_launch.mp4`
- **Notes**: Zero advertisement or sponsored splash; immediate transition.

---

### S002 — Home Screen (Sacred Hub)
- **Screen ID**: S002
- **Screen name**: HomeScreen
- **Entry point**: App launch / Back from child screens
- **Primary purpose**: Primary landing dashboard providing reading resumption, daily inspiration, browsing shortcuts, and privacy assurances.
- **Main UI components**: Top App Bar ("Amanah Quran", "Ad-Free · Forever · Offline", Trust Center icon, Settings icon), "Start Reading" hero card ("Open Mushaf Page"), Reading Streak banner, Daily Ayah card, "Browse the Quran" grid (Surah, Juz, Page indices), Quick Navigation (Search, Bookmarks), Reading Activity card, Quran Content Sources summary card.
- **Navigation options**: Surah Index, Juz Index, Page Index, Mushaf Page, Search, Bookmarks, Reading Streak, Daily Ayah History, Reading Activity, Trust Center, Settings.
- **Interactive elements**: All cards, indices, buttons, and app bar actions are touch targets.
- **Scrollable**: Yes (vertical)
- **Arabic text present**: Yes (Daily Ayah sacred verse text rendered in configured script).
- **English/Urdu/translation text present**: English UI labels and verse reference.
- **Audio controls present**: None (V1 scope compliant).
- **Special states**: First launch (no last-read banner), populated (shows resume card after reading).
- **Screenshot filenames**: `SCREENSHOTS/01_launch/S002_first_launch_loaded.png`, `SCREENSHOTS/03_home/S003_home_initial.png`, `SCREENSHOTS/03_home/S004_home_scrolled.png`, `SCREENSHOTS/03_home/S005_home_dark_mode.png`, `SCREENSHOTS/14_misc/S140_fontscale_130_home.png`, `SCREENSHOTS/14_misc/S145_landscape_home.png`
- **Workflow video**: `VIDEOS/workflow_01_first_launch/first_launch.mp4`
- **Notes**: High visual hierarchy clarity; warm parchment tone in Light theme.

---

### S003 — Surah Index Screen
- **Screen ID**: S003
- **Screen name**: SurahListScreen
- **Entry point**: Home -> "Surah Index" card
- **Primary purpose**: Enable rapid discovery and selection of any of the 114 Surahs.
- **Main UI components**: TopAppBar with back arrow and title, lazy column of Surah items (surah number badge, transliterated name, revelation type [Meccan/Medinan], ayah count, and calligraphic Arabic name).
- **Navigation options**: Tap any Surah to open Surah Reader; Back button to return to Home.
- **Interactive elements**: Back button, list item cards.
- **Scrollable**: Yes (vertical lazy column)
- **Arabic text present**: Yes (calligraphic Surah Arabic names).
- **English/Urdu/translation text present**: English names, verse counts, revelation classification.
- **Audio controls present**: None
- **Special states**: Initial top view, scrolled state.
- **Screenshot filenames**: `SCREENSHOTS/04_quran_navigation/S010_surah_list_top.png`, `SCREENSHOTS/04_quran_navigation/S011_surah_list_scrolled.png`, `SCREENSHOTS/04_quran_navigation/S014_surah_list_dark_mode.png`, `SCREENSHOTS/14_misc/S141_fontscale_130_surah_list.png`
- **Workflow video**: `VIDEOS/workflow_02_core_reading/read_surah.mp4`
- **Notes**: Clean card separation and high-contrast typography.

---

### S004 — Juz (Para) Index Screen
- **Screen ID**: S004
- **Screen name**: JuzListScreen
- **Entry point**: Home -> "Juz Index" card
- **Primary purpose**: Navigate the Quran partitioned by its 30 traditional Juz (Paras).
- **Main UI components**: TopAppBar, list of 30 Juz items displaying Juz number, initial Arabic verse snippet, starting Surah, and page reference.
- **Navigation options**: Tap Juz to open Juz Reader; Back to Home.
- **Interactive elements**: Back navigation, Juz cards.
- **Scrollable**: Yes
- **Arabic text present**: Yes (Juz name and opening Quranic snippet).
- **English/Urdu/translation text present**: English title and metadata.
- **Audio controls present**: None
- **Special states**: Populated 30-item list.
- **Screenshot filenames**: `SCREENSHOTS/04_quran_navigation/S012_juz_list.png`
- **Workflow video**: `VIDEOS/workflow_02_core_reading/read_surah.mp4`
- **Notes**: Facilitates structured daily or monthly reading schedules.

---

### S005 — Page Index Screen
- **Screen ID**: S005
- **Screen name**: PageListScreen
- **Entry point**: Home -> "Page Index" card
- **Primary purpose**: Navigate Quran by standard Mushaf pages (1 to 604 for standard 15-line Uthmani, 1 to 548/550 for IndoPak).
- **Main UI components**: TopAppBar, grid/list of pages displaying page number, starting Surah, and Juz indicator.
- **Navigation options**: Tap page to open Page Reader; Back to Home.
- **Interactive elements**: Page items, back navigation.
- **Scrollable**: Yes
- **Arabic text present**: Yes
- **English/Urdu/translation text present**: Yes
- **Audio controls present**: None
- **Special states**: Standard populated list.
- **Screenshot filenames**: `SCREENSHOTS/04_quran_navigation/S013_page_list.png`
- **Workflow video**: `VIDEOS/workflow_02_core_reading/read_surah.mp4`
- **Notes**: Faithful to physical printed Mushaf page layouts.

---

### S006 — Surah Reader Screen (Core Sacred Reader)
- **Screen ID**: S006
- **Screen name**: SurahReaderScreen / QuranReaderScreen
- **Entry point**: Surah Index selection / Exact Ayah navigation / Home Continue Reading
- **Primary purpose**: Sacred offline reading of Quranic text with verified font glyphs.
- **Main UI components**: Top header with Surah name and progress, Surah header card with Bismillah, sacred Arabic verses with end-of-ayah markers (۝), bottom floating action card upon ayah selection (verse info, bookmark toggle, share text, more actions).
- **Navigation options**: Back navigation, verse selection, modal action bar.
- **Interactive elements**: Ayah tap to inspect/bookmark/share, autoscroll controls, page flip gestures.
- **Scrollable**: Yes (continuous or paged)
- **Arabic text present**: Yes (100% verified Quranic text with full tashkeel/diacritics).
- **English/Urdu/translation text present**: Minimal reference labels (`Al-Baqarah 2:255`).
- **Audio controls present**: None (prohibited in V1).
- **Special states**: Short Surah (Al-Kawthar), Long Surah (Al-Baqarah), Long Ayah (2:282), Short Ayah (55:64), IndoPak script, Uthmani script, Dark Mode, Sepia Mode.
- **Screenshot filenames**: `SCREENSHOTS/05_reading/S020_surah_reader_fatihah.png`, `SCREENSHOTS/05_reading/S021_surah_reader_baqarah_top.png`, `SCREENSHOTS/05_reading/S022_surah_reader_baqarah_scrolled.png`, `SCREENSHOTS/05_reading/S025_reader_verse_action_bar.png`, `SCREENSHOTS/05_reading/S027_surah_reader_dark_mode.png`, `SCREENSHOTS/05_reading/S028_surah_reader_sepia_mode.png`, `SCREENSHOTS/13_long_content_edge_cases/S130_short_surah_kawthar.png`, `SCREENSHOTS/13_long_content_edge_cases/S131_long_surah_baqarah.png`, `SCREENSHOTS/13_long_content_edge_cases/S132_longest_ayah_2_282.png`, `SCREENSHOTS/13_long_content_edge_cases/S133_short_ayah_55_64.png`, `SCREENSHOTS/13_long_content_edge_cases/S134_script_indopak_2_255.png`, `SCREENSHOTS/13_long_content_edge_cases/S135_script_uthmani_2_255.png`, `SCREENSHOTS/14_misc/S142_fontscale_130_reading.png`, `SCREENSHOTS/14_misc/S146_landscape_reader.png`
- **Workflow video**: `VIDEOS/workflow_02_core_reading/read_surah.mp4`
- **Notes**: Pristine typographic rendering; no tofu boxes observed across tests.

---

### S007 — Page / Mushaf Reader Screen
- **Screen ID**: S007
- **Screen name**: MushafPageScreen / QuranReaderScreen(openMode=Page)
- **Entry point**: Home -> "Open Mushaf Page" / Page Index selection
- **Primary purpose**: Replicate physical printed Mushaf page boundaries offline.
- **Main UI components**: Page bookmark row ("Bookmark this page"), full-page justified Quran lines, Surah headers, page footer (Page number, Juz indicator).
- **Navigation options**: Back button, next/previous page swipe, bookmark toggle.
- **Interactive elements**: Bookmark this page button, page swipe.
- **Scrollable**: Yes
- **Arabic text present**: Yes (page-justified sacred text).
- **English/Urdu/translation text present**: Page and Juz numeric labels.
- **Audio controls present**: None
- **Special states**: Page 1 Mushaf view.
- **Screenshot filenames**: `SCREENSHOTS/05_reading/S024_page_reader_page1.png`, `SCREENSHOTS/05_reading/S026_mushaf_page_reader.png`
- **Workflow video**: `VIDEOS/workflow_02_core_reading/read_surah.mp4`, `VIDEOS/workflow_misc/secondary_features_and_trust.mp4`
- **Notes**: Essential for huffaz and readers accustomed to standard 15-line or 16-line print layouts.

---

### S008 — Offline Search Screen
- **Screen ID**: S008
- **Screen name**: SearchScreen
- **Entry point**: Home -> "Search" card
- **Primary purpose**: Instant offline search across Surah titles, transliterations, and canonical verse references.
- **Main UI components**: Search query input field with search icon and clear button, search hints/empty-state placeholder, result items lazy list (surah name, ayah reference, matching verse text snippet).
- **Navigation options**: Tap result row to jump directly to exact verse anchor in Reader; Back button to Home.
- **Interactive elements**: Search text box, clear icon, result list items.
- **Scrollable**: Yes
- **Arabic text present**: Yes (in search result snippets).
- **English/Urdu/translation text present**: Search input, surah transliterations, reference numbers.
- **Audio controls present**: None
- **Special states**: Initial empty prompt, no results found state, multi-result list, Dark Mode.
- **Screenshot filenames**: `SCREENSHOTS/06_search/S030_search_initial_empty.png`, `SCREENSHOTS/06_search/S031_search_no_results.png`, `SCREENSHOTS/06_search/S032_search_results_2_255.png`, `SCREENSHOTS/06_search/S033_search_open_result_reader.png`, `SCREENSHOTS/06_search/S034_search_results_yasin.png`, `SCREENSHOTS/06_search/S035_search_dark_mode.png`, `SCREENSHOTS/12_empty_error_loading_states/S120_search_empty_state.png`, `SCREENSHOTS/14_misc/S143_fontscale_130_search.png`
- **Workflow video**: `VIDEOS/workflow_03_search/search_workflow.mp4`
- **Notes**: High performance (<100ms local SQLite query); separate search-normalized index preserves display integrity.

---

### S009 — Bookmarks Collection Screen
- **Screen ID**: S009
- **Screen name**: BookmarksScreen
- **Entry point**: Home -> "Bookmarks" card
- **Primary purpose**: View and manage saved Quranic verses and page bookmarks.
- **Main UI components**: TopAppBar, empty state card ("No bookmarks yet") or collection list of bookmarked verses (Surah name, ayah key, timestamp, delete action).
- **Navigation options**: Tap bookmark item to open Reader anchored at that verse; Back to Home.
- **Interactive elements**: Bookmark rows, remove bookmark action, back button.
- **Scrollable**: Yes
- **Arabic text present**: Yes (when items are populated).
- **English/Urdu/translation text present**: Collection title, labels, empty state prompt.
- **Audio controls present**: None
- **Special states**: Initial empty state, populated state, item removed state.
- **Screenshot filenames**: `SCREENSHOTS/07_bookmarks_favourites/S070_bookmarks_initial_empty.png`, `SCREENSHOTS/07_bookmarks_favourites/S072_bookmarks_list_with_item.png`, `SCREENSHOTS/07_bookmarks_favourites/S073_bookmark_reopened_reader.png`, `SCREENSHOTS/07_bookmarks_favourites/S074_bookmark_removed.png`, `SCREENSHOTS/12_empty_error_loading_states/S121_bookmarks_empty_state.png`
- **Workflow video**: `VIDEOS/workflow_05_bookmark/bookmark_workflow.mp4`
- **Notes**: Canonical storage references (`surah:ayah`), completely local Room DB storage.

---

### S010 — Settings Screen
- **Screen ID**: S010
- **Screen name**: SettingsScreen
- **Entry point**: Home -> Settings icon (top bar)
- **Primary purpose**: Manage reading appearance, typography, themes, and offline backup.
- **Main UI components**: TopAppBar ("Settings"), Script section (IndoPak / Uthmani chips), Reading Mode section (Continuous / Ayah View chips), Theme section (System, Light, Dark, Sepia chips), Arabic Font Size slider, Elder Mode switch, Navigation rows (Reading Reminders, Advanced Reader Settings, Trust Center), Backup & Restore buttons.
- **Navigation options**: Sub-screens: Reading Reminders, Advanced Reader Settings, Trust Center; Back to Home.
- **Interactive elements**: Radio chips, switches, sliders, buttons, navigation rows.
- **Scrollable**: Yes (vertical column)
- **Arabic text present**: In script preview chips.
- **English/Urdu/translation text present**: All setting descriptions and section headers.
- **Audio controls present**: None
- **Special states**: Light theme, Dark theme, Sepia theme, Elder Mode active, Scrolled settings.
- **Screenshot filenames**: `SCREENSHOTS/10_settings/S100_settings_main.png`, `SCREENSHOTS/10_settings/S101_settings_script_uthmani.png`, `SCREENSHOTS/10_settings/S102_settings_dark_mode.png`, `SCREENSHOTS/10_settings/S103_settings_sepia_mode.png`, `SCREENSHOTS/10_settings/S104_settings_elder_mode_on.png`, `SCREENSHOTS/10_settings/S104_settings_scrolled.png`, `SCREENSHOTS/10_settings/S108_settings_dark_mode_full.png`, `SCREENSHOTS/14_misc/S144_fontscale_130_settings.png`
- **Workflow video**: `VIDEOS/workflow_06_settings/settings_workflow.mp4`
- **Notes**: Real-time reactivity via StateFlow and DataStore; changes take effect immediately.

---

### S011 — Reading Reminder Screen
- **Screen ID**: S011
- **Screen name**: ReadingReminderScreen
- **Entry point**: Settings -> "Reading reminders" row
- **Primary purpose**: Schedule local-only daily Quran reading notification without external servers or tracking.
- **Main UI components**: TopAppBar, reminder toggle switch, time picker, frequency options.
- **Navigation options**: Back to Settings.
- **Interactive elements**: Toggle switch, time selector, back button.
- **Scrollable**: Yes
- **Arabic text present**: No
- **English/Urdu/translation text present**: Yes
- **Audio controls present**: None
- **Special states**: Disabled state, enabled state.
- **Screenshot filenames**: `SCREENSHOTS/11_secondary_features/S110_reading_reminder_screen.png`
- **Workflow video**: `VIDEOS/workflow_06_settings/settings_workflow.mp4`
- **Notes**: Requests runtime `POST_NOTIFICATIONS` permission strictly upon user opt-in, never at startup.

---

### S012 — Advanced Reader Settings Screen
- **Screen ID**: S012
- **Screen name**: AdvancedReaderSettingsScreen
- **Entry point**: Settings -> "Advanced Reader Settings"
- **Primary purpose**: Fine-tune reading gestures, autoscroll speed, and page transition behavior.
- **Main UI components**: TopAppBar, tuning sliders, toggle options, "Reset Reading Settings" navigation row.
- **Navigation options**: Sub-screen: Reset Reading Settings; Back to Settings.
- **Interactive elements**: Sliders, toggles, back navigation.
- **Scrollable**: Yes
- **Arabic text present**: No
- **English/Urdu/translation text present**: Yes
- **Audio controls present**: None
- **Special states**: Standard settings state.
- **Screenshot filenames**: `SCREENSHOTS/10_settings/S106_settings_advanced_reader.png`
- **Workflow video**: `VIDEOS/workflow_06_settings/settings_workflow.mp4`
- **Notes**: Provides deep customization for power readers and elders.

---

### S013 — Reset Reading Settings Screen
- **Screen ID**: S013
- **Screen name**: ResetReadingSettingsScreen
- **Entry point**: Advanced Reader Settings -> "Reset Reading Settings"
- **Primary purpose**: Safely reset reader typography and appearance settings back to factory defaults without erasing bookmarks.
- **Main UI components**: Explanatory warning card, "Reset to Default" button, back navigation.
- **Navigation options**: Back to Advanced Settings.
- **Interactive elements**: Reset button, cancel/back.
- **Scrollable**: No
- **Arabic text present**: No
- **English/Urdu/translation text present**: Yes
- **Audio controls present**: None
- **Special states**: Confirmation dialog state.
- **Screenshot filenames**: `SCREENSHOTS/10_settings/S107_settings_reset_reader.png`
- **Workflow video**: `VIDEOS/workflow_06_settings/settings_workflow.mp4`
- **Notes**: Distinct separation between display settings reset and user content preservation.

---

### S014 — Trust Center Screen
- **Screen ID**: S014
- **Screen name**: TrustCenterScreen
- **Entry point**: Home -> Shield/Trust icon (top bar) / Settings -> Trust Center / Home bottom card
- **Primary purpose**: Transparently document content sources, cryptographic checksums, font licenses, and no-tracking privacy guarantees.
- **Main UI components**: TopAppBar, Content Integrity card, Script Sources summary (Tanzil, King Fahd Complex, Digital Khatt), Font Manifest checksum status, Privacy & Zero-Tracking verification badge.
- **Navigation options**: Back to caller.
- **Interactive elements**: Back button, expandable source details cards.
- **Scrollable**: Yes (vertical)
- **Arabic text present**: Minimal (sample script labels).
- **English/Urdu/translation text present**: Detailed provenance and verification documentation.
- **Audio controls present**: None
- **Special states**: Top overview, scrolled verification logs.
- **Screenshot filenames**: `SCREENSHOTS/11_secondary_features/S111_trust_center_top.png`, `SCREENSHOTS/11_secondary_features/S112_trust_center_scrolled.png`
- **Workflow video**: `VIDEOS/workflow_misc/secondary_features_and_trust.mp4`
- **Notes**: Core architectural differentiator establishing Sadaqah Jariyah integrity.

---

### S015 — Reading Streak Screen
- **Screen ID**: S015
- **Screen name**: ReadingStreakScreen
- **Entry point**: Home -> Reading Streak banner ("Start your reading streak today")
- **Primary purpose**: Motivate consistent daily reading habits through local-only streak tracking.
- **Main UI components**: Current streak count hero display, milestones progress, motivational encouragement, "Open Reading Calendar" action button.
- **Navigation options**: Back to Home, Open Calendar.
- **Interactive elements**: Open Calendar button, back arrow.
- **Scrollable**: Yes
- **Arabic text present**: Minimal
- **English/Urdu/translation text present**: Yes
- **Audio controls present**: None
- **Special states**: 0-day initial streak state.
- **Screenshot filenames**: `SCREENSHOTS/11_secondary_features/S113_reading_streak_screen.png`
- **Workflow video**: `VIDEOS/workflow_misc/secondary_features_and_trust.mp4`
- **Notes**: 100% on-device calculation; zero analytics or social broadcast.

---

### S016 — Reading Calendar Screen
- **Screen ID**: S016
- **Screen name**: ReadingCalendarScreen
- **Entry point**: Reading Streak -> "Open Reading Calendar"
- **Primary purpose**: Month-by-month calendar view of reading history.
- **Main UI components**: TopAppBar, monthly calendar grid, date markers indicating reading completion, monthly statistics.
- **Navigation options**: Back to Reading Streak.
- **Interactive elements**: Month navigation arrows, date cells, back arrow.
- **Scrollable**: Yes
- **Arabic text present**: No
- **English/Urdu/translation text present**: Month names, day headers, metrics.
- **Audio controls present**: None
- **Special states**: Current month default.
- **Screenshot filenames**: `SCREENSHOTS/11_secondary_features/S114_reading_calendar_screen.png`
- **Workflow video**: `VIDEOS/workflow_misc/secondary_features_and_trust.mp4`
- **Notes**: Clean visual grid with no external calendar sync requirements.

---

### S017 — Daily Ayah History Screen
- **Screen ID**: S017
- **Screen name**: DailyAyahHistoryScreen
- **Entry point**: Home -> Daily Ayah card "History" button
- **Primary purpose**: Review previous daily featured ayahs.
- **Main UI components**: TopAppBar, chronological list of past daily ayahs (date, canonical reference, selection reason [e.g. `reviewed_random`]).
- **Navigation options**: Tap ayah card to open exact verse in Reader; Back to Home.
- **Interactive elements**: Ayah list items, back navigation.
- **Scrollable**: Yes
- **Arabic text present**: Yes
- **English/Urdu/translation text present**: Reference keys and dates.
- **Audio controls present**: None
- **Special states**: Current session history list.
- **Screenshot filenames**: `SCREENSHOTS/11_secondary_features/S115_daily_ayah_history.png`
- **Workflow video**: `VIDEOS/workflow_misc/secondary_features_and_trust.mp4`
- **Notes**: Allows revisiting previous reflections without losing context.

---

### S018 — Reading Activity Dashboard
- **Screen ID**: S018
- **Screen name**: ReadingActivityDashboardScreen
- **Entry point**: Home -> "Reading Activity" card
- **Primary purpose**: Display comprehensive personal reading statistics (verses read, pages covered, active reading time).
- **Main UI components**: TopAppBar, total stats cards, weekly reading chart, Juz completion progress.
- **Navigation options**: Back to Home.
- **Interactive elements**: Back navigation.
- **Scrollable**: Yes
- **Arabic text present**: No
- **English/Urdu/translation text present**: Yes
- **Audio controls present**: None
- **Special states**: Clean zero-state on fresh installation.
- **Screenshot filenames**: `SCREENSHOTS/11_secondary_features/S116_reading_activity_dashboard.png`
- **Workflow video**: `VIDEOS/workflow_misc/secondary_features_and_trust.mp4`
- **Notes**: Private, local metrics designed for self-betterment.
