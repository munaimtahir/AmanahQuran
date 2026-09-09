# Interaction & Usability Issues Observed

This document details interaction observations and potential usability friction points identified during the emulator audit.

### 1. In-Reader Script Switching Requires Visiting Settings
- **Observation**: When reading a Surah or Page, there is no direct toggle inside the Reader to switch between IndoPak and Uthmani scripts.
- **Friction**: The user must exit back to Home, open Settings, switch the script chip, and re-open the reader.
- **Potential Improvement**: Adding a discreet script switch button (or in the reader top bar / overflow menu) would improve cross-script study.
- **Evidence**: `S021_surah_reader_baqarah_top.png`, `S101_settings_script_uthmani.png`

### 2. Ayah Action Card Tap Discoverability
- **Observation**: To bookmark or share an ayah, the user must tap the verse text to invoke `ReaderSelectedAyahActionCard`.
- **Friction**: First-time users receive no visual hint that individual Quranic verses are tappable surfaces.
- **Potential Improvement**: A subtle onboarding tooltip or long-press hint upon first reader entry would enhance discoverability.
- **Evidence**: `S025_reader_verse_action_bar.png`

### 3. Home Screen Vertical Density
- **Observation**: The Home screen contains several informative cards (Start Reading hero, Streak, Daily Ayah, Browse grid, Navigation cards, Reading Activity, Trust Center).
- **Friction**: On smaller devices or higher display densities, essential items like Search and Bookmarks require scrolling below the fold.
- **Potential Improvement**: Consider a slightly tighter hero header or configurable card order.
- **Evidence**: `S003_home_initial.png`, `S004_home_scrolled.png`

### 4. Elder Mode Accessibility in Deep Settings
- **Observation**: While Elder Mode substantially expands touch targets and card sizes, deeply nested menus (Advanced Reader Settings) still retain smaller sub-option rows.
- **Evidence**: `S104_settings_elder_mode_on.png`, `S106_settings_advanced_reader.png`
