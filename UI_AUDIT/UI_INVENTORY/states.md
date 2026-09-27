# UI Inventory — Application States

This document evaluates the presence and handling of diverse UI states across Amanah Quran.

### 1. Initial State (Clean First Launch)
- **Observed**: Zero onboarding wizards, zero forced logins, zero permission prompts.
- **Home Screen**: Renders immediately with Daily Ayah, Reading Streak banner at 0 days, and Browse indices. Last-Read card is omitted until the user completes their first reading session.
- **Evidence**: `SCREENSHOTS/01_launch/S002_first_launch_loaded.png`

### 2. Populated States
- **Surah Index**: 114 Surahs fully populated with title, verse count, and Arabic calligraphic name.
- **Juz Index**: 30 Juz fully indexed.
- **Search Results**: Multi-result lists populate instantaneously upon typing queries such as `2:255` or `Yasin`.
- **Evidence**: `SCREENSHOTS/04_quran_navigation/S010_surah_list_top.png`, `SCREENSHOTS/06_search/S032_search_results_2_255.png`

### 3. Empty States
- **Search (No Input)**: Clean prompt inviting the user to search by Surah name, verse reference (`surah:ayah`), or page number.
- **Search (No Results)**: Graceful zero-result state explaining that no matching verses were found.
- **Bookmarks (Empty)**: Clean card explaining that no verses or pages have been bookmarked yet, with guidance on how to bookmark from the reader.
- **Reading Activity (Zero State)**: Displays clean initial zero counts for fresh installs.
- **Evidence**: `SCREENSHOTS/12_empty_error_loading_states/S120_search_empty_state.png`, `SCREENSHOTS/12_empty_error_loading_states/S121_bookmarks_empty_state.png`

### 4. Active / Interacted States
- **Ayah Selection**: Tapping a verse triggers `ReaderSelectedAyahActionCard` with interactive bookmarking and sharing.
- **Theme Selection**: Immediate reactive repaint across Light, Dark, and Sepia modes without screen reload.
- **Evidence**: `SCREENSHOTS/05_reading/S025_reader_verse_action_bar.png`, `SCREENSHOTS/10_settings/S102_settings_dark_mode.png`

### 5. Loading & Error States
- **Database Loading**: Pre-packaged SQLite database in assets results in sub-10ms query times; traditional loading spinners are unnecessary and avoided.
- **Error Handling**: Missing routes or corrupt anchor intents fall back safely to default Al-Fatihah or Home without crashing.
