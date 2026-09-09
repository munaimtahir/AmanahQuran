# Raw Observations

This document records formal observations noted during the UI/UX audit of **Amanah Quran (v2.2.0)**.

---

Observation ID: O001
Severity: Informational
Area: First Launch & Privacy
Description: Clean application launch with zero onboarding friction, zero user login screens, zero network requests, and zero advertising SDK initialization.
Evidence: `SCREENSHOTS/01_launch/S001_launch_splash.png`, `SCREENSHOTS/01_launch/S002_first_launch_loaded.png`, `VIDEOS/workflow_01_first_launch/first_launch.mp4`
Confidence: High
Potential user impact: Exceptional initial trust; respects user privacy and religious sanctity immediately upon install.

---

Observation ID: O002
Severity: Informational
Area: Quranic Font Integrity
Description: Dual script support (IndoPak and Uthmani) renders verified OpenType glyphs without tofu boxes, missing characters, or overlapping diacritical marks.
Evidence: `SCREENSHOTS/13_long_content_edge_cases/S134_script_indopak_2_255.png`, `SCREENSHOTS/13_long_content_edge_cases/S135_script_uthmani_2_255.png`
Confidence: High
Potential user impact: Flawless recitation and reading experience matching traditional printed Mushafs.

---

Observation ID: O003
Severity: Minor
Area: Reader Navigation
Description: Switching scripts between IndoPak and Uthmani cannot be done directly from within the Reader screen; it requires navigating back to Settings.
Evidence: `SCREENSHOTS/05_reading/S021_surah_reader_baqarah_top.png`, `SCREENSHOTS/10_settings/S101_settings_script_uthmani.png`
Confidence: High
Potential user impact: Minor friction for users comparing scripts or transitioning between regional recitation traditions.

---

Observation ID: O004
Severity: Minor
Area: Verse Action Discoverability
Description: Tapping an individual verse reveals the `ReaderSelectedAyahActionCard` (for bookmarking and sharing), but there is no visual affordance or cue indicating that verses are tappable.
Evidence: `SCREENSHOTS/05_reading/S025_reader_verse_action_bar.png`
Confidence: High
Potential user impact: Some users may not discover how to bookmark individual verses without trial and error.

---

Observation ID: O005
Severity: Informational
Area: Offline Search Performance
Description: Search queries across canonical references (e.g. `2:255`) and Surah transliterations return instantaneous results (<50ms) using a separate normalized index that protects display text integrity.
Evidence: `SCREENSHOTS/06_search/S032_search_results_2_255.png`, `SCREENSHOTS/06_search/S033_search_open_result_reader.png`, `VIDEOS/workflow_03_search/search_workflow.mp4`
Confidence: High
Potential user impact: Swift, frictionless navigation to desired verses without internet reliance.

---

Observation ID: O006
Severity: Informational
Area: Theme Consistency & Dark Mode
Description: Full palette support for Light, Dark, Sepia, and System themes. Theme switching immediately re-renders all surfaces without glitches or unreadable contrast.
Evidence: `SCREENSHOTS/03_home/S005_home_dark_mode.png`, `SCREENSHOTS/05_reading/S027_surah_reader_dark_mode.png`, `SCREENSHOTS/05_reading/S028_surah_reader_sepia_mode.png`, `SCREENSHOTS/10_settings/S102_settings_dark_mode.png`
Confidence: High
Potential user impact: High comfort for prolonged night reading and daylight reading alike.

---

Observation ID: O007
Severity: Minor
Area: Home Screen Vertical Stacking
Description: The Home screen hosts multiple cards (Daily Ayah, Browse Indices, Search, Bookmarks, Reading Streak, Activity, Trust Center), pushing Search and Bookmarks below the fold on standard portrait viewports.
Evidence: `SCREENSHOTS/03_home/S003_home_initial.png`, `SCREENSHOTS/03_home/S004_home_scrolled.png`
Confidence: High
Potential user impact: Requires extra swipe gesture to access bookmarks and search from the landing screen.

---

Observation ID: O008
Severity: Informational
Area: Accessibility & Elder Mode
Description: Built-in Elder Mode expands touch targets, card padding, and typography, significantly improving usability for elderly readers or users with motor limitations.
Evidence: `SCREENSHOTS/10_settings/S104_settings_elder_mode_on.png`
Confidence: High
Potential user impact: Greatly broadens accessibility across diverse demographic groups.

---

Observation ID: O009
Severity: Informational
Area: Font Scaling Resilience
Description: Testing at 130% Android system font scale demonstrated responsive card growth and zero label clipping or overlapping in headers.
Evidence: `SCREENSHOTS/14_misc/S140_fontscale_130_home.png`, `SCREENSHOTS/14_misc/S142_fontscale_130_reading.png`
Confidence: High
Potential user impact: Users with system-wide accessibility enlargement settings can read comfortably.

---

Observation ID: O010
Severity: Informational
Area: Landscape Orientation
Description: Landscape orientation rotates gracefully, maintaining readability and adjusting line layout widths appropriately.
Evidence: `SCREENSHOTS/14_misc/S145_landscape_home.png`, `SCREENSHOTS/14_misc/S146_landscape_reader.png`
Confidence: High
Potential user impact: Tablet and car-mount / stand users can read comfortably in horizontal mode.

---

Observation ID: O011
Severity: Informational
Area: Trust Center & Provenance
Description: The dedicated Trust Center provides full transparency into content licenses, font source SHAs, and zero-telemetry architecture, setting a high standard for authentic Islamic software.
Evidence: `SCREENSHOTS/11_secondary_features/S111_trust_center_top.png`, `SCREENSHOTS/11_secondary_features/S112_trust_center_scrolled.png`
Confidence: High
Potential user impact: Total peace of mind regarding religious textual accuracy and absence of spyware.
