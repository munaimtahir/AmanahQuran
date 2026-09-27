# AMANAH UI OPPORTUNITY BACKLOG — SPRINT ROADMAP

> **Backlog Scope**: Evidence-based design proposals derived strictly from competitive audit findings and real user feedback.
> **Priority Classification**:
> - **P0**: Critical usability defect or essential competitor-proven requirement.
> - **P1**: High-value UX improvement backed by strong evidence.
> - **P2**: Useful ergonomic refinement.
> - **P3**: Experimental or future exploratory feature.
> **Note**: This sprint is READ-ONLY. No changes are to be implemented during this benchmark phase.

---

## 1. Prioritized Opportunity Summary Table

| ID | Priority | Feature Proposal | Target Surface | Competitor Evidence | Expected User Benefit | Complexity |
| :---: | :---: | :--- | :--- | :--- | :--- | :---: |
| `PROP-01` | 🔴 **`P0`** | **Independent Typography Scaling & Nastaliq Line-Height Spacing** | `Surah Reader` | Aasan Tarjuma & Greentech | Elderly and low-vision readers can read Arabic com... | `Low (Compose TextUnit state)` |
| `PROP-02` | 🟠 **`P1`** | **Elevated, Fully Tappable "Continue Reading" Hero Card with Context Chips** | `Home Screen` | Tarteel Home Dashboard & Quranly Reading Card. | Reduces time-to-reading to sub-1 second; eliminate... | `Low (UI card refactoring in Compose)` |
| `PROP-03` | 🟠 **`P1`** | **Contextual Bottom Sheet for Reader Settings & Script Toggling** | `Reader Screen` | Greentech Reader Settings Bottom Sheet. | Allows reader to customize visual comfort without ... | `Medium (Compose ModalBottomSheet integration)` |
| `PROP-04` | 🟠 **`P1`** | **Suggestion Chips & Direct Reference Jump in Offline Search** | `Search Screen` | Greentech Search Home & Quran Majeed Quick Index. | Non-Arabic speakers and new readers can instantly ... | `Low (Static list + existing navigation routing)` |
| `PROP-05` | 🟠 **`P1`** | **Clear Semantic Separation: Auto Last-Read Pin vs. Saved Bookmarks** | `Bookmarks Screen` | Quran for Android Bookmarks Tab | Zero confusion. Eliminates user anxiety about losi... | `Low (Room query separation)` |
| `PROP-06` | 🟡 **`P2`** | **Direct Numeric Reference Jump Dialog (Surah : Ayah)** | `Reader Screen Top Bar & Quran Navigation Tab.` | Quran for Android Surah/Ayah jump dialog. | Instant navigation for students and teachers.... | `Low (Compose NumberPicker/Dialog)` |
| `PROP-07` | 🟡 **`P2`** | **Pure OLED True Black Palette for Dark Theme** | `Theming system` | Tarteel Pure Black Theme. | Zero pixel glow for late-night tahajjud or bed-sid... | `Low (Color token addition in ColorScheme)` |
| `PROP-08` | 🟢 **`P3`** | **Optional Translation Visibility Selector in Reader** | `Reader Screen` | The Glorious Quran Reader Layout Selector. | Flexibility for memorization/hifz recitation witho... | `Medium` |

---

## 2. Detailed Architectural Proposals

### [PROP-01] Independent Typography Scaling & Nastaliq Line-Height Spacing (P0)
- **Problem Statement**: When users increase Arabic font size in continuous reader, Urdu Nastaliq translation can either become disproportionately huge or clip vertical diacritics due to tight line-height bounds.
- **Competitor Evidence**: Review complaints in The Glorious Quran & Aasan Tarjuma regarding Urdu clipping; direct emulator validation on quran_android where font scaling alters translation layout.
- **Competitor Reference**: `Aasan Tarjuma & Greentech (both provide separate sliders for Arabic and Translation text).`
- **User Review Sentiment**: Urdu Nastaliq readability, low-vision reading comfort.
- **Amanah Surface Affected**: `Surah Reader (`ReaderScreen.kt`), Settings (`SettingsScreen.kt`). Evidence: `UI_AUDIT/SCREENSHOTS/05_reading/001_surah_reader_alfatihah.png`.`
- **Proposed Interaction Design**: In Reader Settings Sheet, provide two linked sliders: "Arabic Script Size" (24sp - 48sp) and "Translation Text Size" (14sp - 28sp), with minimum 1.4x line-height multiplier for Urdu.
- **Expected Usability Benefit**: Elderly and low-vision readers can read Arabic comfortably without ballooning translation text to screen-filling heights.
- **Potential Risks**: Low. Purely presentation layer styling.
- **Accessibility & Elder Impact**: Transformative. WCAG 1.4.4 compliant up to 200% scaling without clipping.
- **Estimated Complexity**: `Low (Compose TextUnit state)`
- **Quran Text / Content Integrity**: Zero risk. Quran text strings remain completely immutable.
- **Recommended Quality Gate**: `Visual regression test at 100%, 150%, and 200% font scaling.`

### [PROP-02] Elevated, Fully Tappable "Continue Reading" Hero Card with Context Chips (P1)
- **Problem Statement**: Home screen resumption card has smaller tap target area and could provide richer context on exactly what verse is queued.
- **Competitor Evidence**: Tarteel and Quranly feature prominent hero cards that make resumption frictionless (<1 tap from cold launch to active verse).
- **Competitor Reference**: `Tarteel Home Dashboard & Quranly Reading Card.`
- **User Review Sentiment**: Frictionless reading resumption, lost last-read confusion.
- **Amanah Surface Affected**: `Home Screen (`HomeScreen.kt`). Evidence: `UI_AUDIT/SCREENSHOTS/03_home/001_home_screen.png`.`
- **Proposed Interaction Design**: Expand Continue Reading into a full-width hero card with elevated surface, showing Surah name, Ayah reference (e.g. "Surah Al-Baqarah • Ayah 255"), Juz badge, and a prominent "Resume" icon.
- **Expected Usability Benefit**: Reduces time-to-reading to sub-1 second; eliminates ambiguity over what will open.
- **Potential Risks**: Low.
- **Accessibility & Elder Impact**: Positive. Large 64dp+ tap target, explicit accessibility announcement.
- **Estimated Complexity**: `Low (UI card refactoring in Compose)`
- **Quran Text / Content Integrity**: Zero risk (Canonical reference ID used).
- **Recommended Quality Gate**: `Unit test verifying single-tap navigation to exact verse ID.`

### [PROP-03] Contextual Bottom Sheet for Reader Settings & Script Toggling (P1)
- **Problem Statement**: Adjusting script (IndoPak vs Uthmani), theme, or font size currently requires navigating away from reader or opening separate full dialogs.
- **Competitor Evidence**: Greentech and Tarteel utilize lightweight bottom sheets that adjust reader styling in real-time behind a translucent scrim.
- **Competitor Reference**: `Greentech Reader Settings Bottom Sheet.`
- **User Review Sentiment**: Reader focus, ease of theme/script switching during tilawat.
- **Amanah Surface Affected**: `Reader Screen (`ReaderScreen.kt`). Evidence: `UI_AUDIT/SCREENSHOTS/05_reading/`.`
- **Proposed Interaction Design**: Tapping Gear icon in reader top bar opens a compact `ModalBottomSheet` containing: Script chips (IndoPak / Uthmani), Theme chips (Light, Sepia, Dark, OLED), and Font sliders with immediate live background preview.
- **Expected Usability Benefit**: Allows reader to customize visual comfort without losing their place on the page.
- **Potential Risks**: Low.
- **Accessibility & Elder Impact**: Positive (Modal accessibility semantics, easy thumb reachability).
- **Estimated Complexity**: `Medium (Compose ModalBottomSheet integration)`
- **Quran Text / Content Integrity**: Zero risk.
- **Recommended Quality Gate**: `Compose UI test verifying state updates propagate instantly.`

### [PROP-04] Suggestion Chips & Direct Reference Jump in Offline Search (P1)
- **Problem Statement**: Blank search screen provides no guidance for users who do not know exact Arabic spelling or transliteration rules.
- **Competitor Evidence**: Quran for Android search is empty; Greentech offers quick suggestion chips (e.g. Yasin, Al-Mulk, Al-Kahf, Ayatul Kursi) that drive 60% of search traffic.
- **Competitor Reference**: `Greentech Search Home & Quran Majeed Quick Index.`
- **User Review Sentiment**: Search usability, finding popular chapters easily.
- **Amanah Surface Affected**: `Search Screen (`SearchScreen.kt`). Evidence: `UI_AUDIT/SCREENSHOTS/06_search/001_offline_search_empty.png`.`
- **Proposed Interaction Design**: In the empty search state, display a "Popular Passages" chip row (`Yasin`, `Al-Mulk`, `Al-Kahf`, `Ar-Rahman`, `Ayat al-Kursi 2:255`), plus a numeric reference input (`Surah : Ayah`).
- **Expected Usability Benefit**: Non-Arabic speakers and new readers can instantly reach spiritually significant passages with one tap.
- **Potential Risks**: Low.
- **Accessibility & Elder Impact**: Positive. High-contrast, easily focused suggestion chips.
- **Estimated Complexity**: `Low (Static list + existing navigation routing)`
- **Quran Text / Content Integrity**: Zero risk.
- **Recommended Quality Gate**: `Search test verifying chip taps resolve directly to canonical verse.`

### [PROP-05] Clear Semantic Separation: Auto Last-Read Pin vs. Saved Bookmarks (P1)
- **Problem Statement**: Users risk confusing their auto-updating reading position with deliberate, permanent verse bookmarks.
- **Competitor Evidence**: Persistent review complaints in Greentech, Quran for Android, and Quran Majeed regarding overwritten bookmarks or lost reading marks.
- **Competitor Reference**: `Quran for Android Bookmarks Tab (clearly separates "Last Page" from "Page Bookmarks").`
- **User Review Sentiment**: Bookmark preservation, reading tracking clarity.
- **Amanah Surface Affected**: `Bookmarks Screen (`BookmarksScreen.kt`) and Home Screen.`
- **Proposed Interaction Design**: Explicitly separate the Bookmarks surface into two distinct sections: (1) "Reading Position (Auto-Pin)" pinned at the top with a clear "Auto-saved as you read" caption, and (2) "Saved Verses (Manual Bookmarks)" with date-added badges and swipe-to-delete.
- **Expected Usability Benefit**: Zero confusion. Eliminates user anxiety about losing their place.
- **Potential Risks**: Low.
- **Accessibility & Elder Impact**: Positive. Semantic headers for screen reader navigation.
- **Estimated Complexity**: `Low (Room query separation)`
- **Quran Text / Content Integrity**: Zero risk.
- **Recommended Quality Gate**: `Database migration test ensuring existing bookmarks are untouched.`

### [PROP-06] Direct Numeric Reference Jump Dialog (Surah : Ayah) (P2)
- **Problem Statement**: Jumping to a specific verse (e.g. 2:282) currently requires scrolling through a 286-verse list or performing a search query.
- **Competitor Evidence**: Quran for Android and Tarteel both offer quick numeric jump pickers.
- **Competitor Reference**: `Quran for Android Surah/Ayah jump dialog.`
- **User Review Sentiment**: Speed of navigation, study convenience.
- **Amanah Surface Affected**: `Reader Screen Top Bar & Quran Navigation Tab.`
- **Proposed Interaction Design**: Tapping the Surah title in the reader toolbar opens a small dialog with two numeric pickers: Surah (1-114) and Ayah (1-N). Selecting values scrolls the reader directly to the target verse.
- **Expected Usability Benefit**: Instant navigation for students and teachers.
- **Potential Risks**: Low.
- **Accessibility & Elder Impact**: Positive (Direct accessible numeric input).
- **Estimated Complexity**: `Low (Compose NumberPicker/Dialog)`
- **Quran Text / Content Integrity**: Zero risk.
- **Recommended Quality Gate**: `Navigation test with boundary values (1:1, 2:286, 114:6).`

### [PROP-07] Pure OLED True Black Palette for Dark Theme (P2)
- **Problem Statement**: Current Dark theme uses deep grey (#121212), which is good for general dark mode but does not achieve 0-pixel emission on OLED screens.
- **Competitor Evidence**: Tarteel and modern Quran apps offer an "OLED Black" option which saves battery and maximizes contrast in dark rooms.
- **Competitor Reference**: `Tarteel Pure Black Theme.`
- **User Review Sentiment**: Night reading comfort, OLED battery conservation.
- **Amanah Surface Affected**: `Theming system (`Theme.kt`).`
- **Proposed Interaction Design**: Add an optional "True Black (OLED)" variant to the Theme settings where background color is #000000 and text is soft muted white (#E0E0E0) to eliminate glare.
- **Expected Usability Benefit**: Zero pixel glow for late-night tahajjud or bed-side reading; maximum battery savings on OLED devices.
- **Potential Risks**: Low.
- **Accessibility & Elder Impact**: High contrast WCAG AAA compliant.
- **Estimated Complexity**: `Low (Color token addition in ColorScheme)`
- **Quran Text / Content Integrity**: Zero risk.
- **Recommended Quality Gate**: `Contrast ratio verification test.`

### [PROP-08] Optional Translation Visibility Selector in Reader (P3)
- **Problem Statement**: Continuous reader always renders translations inline, which can clutter the screen for readers who only want Arabic Tilawat.
- **Competitor Evidence**: The Glorious Quran and Aasan Tarjuma both support "Arabic Only", "Translation Only", and "Bilingual" display modes.
- **Competitor Reference**: `The Glorious Quran Reader Layout Selector.`
- **User Review Sentiment**: Distraction-free recitation vs deep study.
- **Amanah Surface Affected**: `Reader Screen (`ReaderScreen.kt`).`
- **Proposed Interaction Design**: Provide a 3-way toggle in Reader Settings: [Arabic Only | Arabic + English | Arabic + Urdu | Full Bilingual].
- **Expected Usability Benefit**: Flexibility for memorization/hifz recitation without distraction.
- **Potential Risks**: Medium (Requires dynamic recomposition of LazyColumn items).
- **Accessibility & Elder Impact**: Positive (Reduces cognitive load for Arabic-fluent readers).
- **Estimated Complexity**: `Medium`
- **Quran Text / Content Integrity**: Zero risk.
- **Recommended Quality Gate**: `Compose benchmark test verifying 60fps scrolling.`
