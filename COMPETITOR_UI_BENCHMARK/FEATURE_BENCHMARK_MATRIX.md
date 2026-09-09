# FEATURE BENCHMARK MATRIX — UX PATTERN AUDIT

> **Analysis Methodology**: Comprehensive pattern extraction across 7 mandatory Quran apps and 1 commercial control app.
> **Recommendation Legend**:
> - **ADOPT**: Pattern represents proven usability excellence directly aligned with Amanah's sacred charter.
> - **ADAPT**: Pattern is valuable but must be re-architected to fit Amanah's calm, offline, non-commercial ethos.
> - **AVOID**: Pattern conflicts with Amanah principles (e.g. ads, paywalls, anxiety gamification, super-app bloat).
> - **INVESTIGATE**: Pattern requires deeper user testing before inclusion in V1/V2 roadmap.

---

## 1. Comprehensive UX Pattern Comparison Table

| UX Interaction Pattern | Competitor Apps Using It | User Benefit | Discoverability | Visual Cost | Cognitive Cost | Amanah Alignment | Complexity | A11y Impact | Privacy Impact | Recommendation |
| :--- | :--- | :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **Entire "Continue Reading" Hero Card is Tappable** | Tarteel, Quranly, Greentech | Instant single-tap resumption to the exact last-read ayah/page without hunting through indices. | Immediate (Top of home screen) | Low (Compact elevated card) | Very Low (Zero thought required to resume) | High (Core to Amanah Sacred Reader MVP) | Low (Room single row last_read entity) | Positive (Large 48dp+ tap target, screen reader reads full verse location) | Positive (100% local DataStore/Room) | 🟢 **`ADOPT`** |
| **Compact Home Information Hierarchy (Reading-First)** | Quran for Android, Amanah Quran | Zero distraction upon launch; immediate visibility of Surah/Juz/Page entry points. | Immediate | Low | Very Low | Extremely High (Amanah Core Philosophy) | Low | Positive (Simple list/tab focus order) | Positive (Zero telemetry) | 🟢 **`ADOPT`** |
| **Super-App Widget Clutter (Prayer, Qibla, Shopping, Ads)** | Quran Majeed, Muslim Pro | Consolidates multiple utilities into a single app. | High | Extreme (Dozens of icons, cards, promotional widgets) | Extreme (Overwhelms readers, dilutes Quran focus) | Violates Amanah Scope Guardrail | High | Negative (Cluttered tab stops, noisy screen reader experience) | Negative (Location tracking, ad network IDs) | 🔴 **`AVOID`** |
| **Reader Settings Accessible via Compact Bottom Sheet** | Greentech, Tarteel, The Glorious Quran | Allows adjusting script, font size, and themes without leaving the reading flow. | High (Gear icon on reader toolbar) | Very Low (Hidden until summoned) | Low (Contextual to current reading surface) | High (Preserves reading immersion) | Medium (Compose ModalBottomSheet) | Positive (Modal accessibility announcements, live text scale preview) | Positive (Local StateFlow) | 🟢 **`ADOPT`** |
| **Direct Script Switcher (IndoPak <-> Uthmani) Inside Reader** | Amanah Quran, Greentech | Enables immediate script switching to suit reader familiarity without navigating global settings. | High (Quick chip or sheet toggle) | Low | Low | Extremely High (Amanah Core Requirement: IndoPak + Uthmani parity) | Medium (Font swap & line re-layout) | Positive (High utility for South Asian and Arab readers) | Positive (Local font rendering) | 🟢 **`ADOPT`** |
| **Translation Selector Inside Reader Chrome** | The Glorious Quran, Greentech, Quran for Android | Enables toggling between Urdu (Irfan-ul-Quran) and English (Manifest Quran) on the fly. | High (Toolbar icon / bottom sheet selector) | Low | Low | High (Amanah translation context) | Medium | Positive (Direct locale and commentary switching) | Positive (Bundled offline text) | 🟢 **`ADOPT`** |
| **Tap Ayah for Contextual Action Sheet / Menu** | Quran for Android, Greentech, Amanah Quran | Reveals Bookmark, Share, Tafsir, and Copy actions directly related to the specific verse. | Medium (Requires tap/long-press gesture discoverability) | Low (Ephemeral sheet/bar) | Low | High | Medium (Ayah bounding box tap detection) | Positive (Ayah accessible actions dialog) | Positive (Local actions) | 🟢 **`ADOPT`** |
| **Tap Verse Marker (Ayah End Symbol) for Actions** | The Glorious Quran, Aasan Tarjuma | Clear visible affordance that does not interfere with reading the sacred Arabic words. | High (Marker glyph is a distinct visual anchor) | Zero (Uses existing ayah marker) | Very Low | High | Low (Clickable trailing span/icon) | Positive (Explicit touch target 48x48dp) | Positive | 🟡 **`ADAPT`** |
| **Contextual Top Action Toolbar (Replaces Standard App Bar)** | Quran for Android | Highlights selected verse and presents clear icons for Copy, Bookmark, and Translation. | Immediate upon selection | Low | Low | High | Medium | Positive (Contextual action bar announce) | Positive | 🟢 **`ADOPT`** |
| **Page-Style Mushaf View (Madani / 15-Line Reproduction)** | Quran for Android, Quran Majeed, Greentech | Provides sacred page layout matching physical printed Quran used for Hifz. | High | Zero extra chrome | Low for experienced readers | High (Amanah Mushaf Reader) | High (Accurate page layout & line rendering) | Needs Care (Requires accessible verse-by-verse overlay) | Positive | 🟢 **`ADOPT`** |
| **Continuous Vertical Scrolling Reader (Continuous Mode)** | The Glorious Quran, Aasan Tarjuma, Greentech | Comfortable modern mobile scrolling with natural reading rhythm and inline translations. | Default in continuous mode | Low | Low | High (Amanah Ayah Reader) | Medium (LazyColumn with keys) | Excellent (Standard vertical accessibility scrolling, linear navigation) | Positive | 🟢 **`ADOPT`** |
| **Independent Arabic and Translation Font Sizing Sliders** | Aasan Tarjuma, Greentech, The Glorious Quran | Allows users to scale Arabic up for visual clarity while keeping translation compact, or vice versa. | High in reader settings | Low | Low | Extremely High (Essential for Elder Mode & bilingual balance) | Low (Compose TextUnit state) | Crucial (Prevents clipping while supporting low-vision readers) | Positive | 🟢 **`ADOPT`** |
| **Quick Surah / Juz Jump Modal (Numeric Grid / Sheet)** | Quran for Android, Tarteel, Greentech | Jump instantly to Surah X, Ayah Y without scrolling through 114 Surahs. | High (Header title is tappable) | Low | Low | High | Low (Compose Dialog / BottomSheet) | Positive (Keyboard accessible direct verse navigation) | Positive | 🟢 **`ADOPT`** |
| **Search Suggestion Chips & Recent Queries** | Greentech, Quran Majeed | Helps users quickly jump to popular Surahs (e.g. Yasin, Al-Mulk, Al-Kahf, Ayatul Kursi). | Immediate in search view | Low (Horizontal chip row) | Very Low | High (Helps non-Arabic speakers find key verses) | Low (Static chip list + recent queries DataStore) | Positive (One-tap search targets) | Positive (Local history only) | 🟢 **`ADOPT`** |
| **Bookmark Categorization (Tags / Folders / Colors)** | Quran for Android (Tags), Greentech (Collections) | Keeps daily recitation bookmarks distinct from study verses or favorites. | Medium (In bookmarks management) | Low | Medium | Moderate (Keep simple in V1, expand in V2) | Medium (Room entity relation) | Positive (Better organization for large collections) | Positive | 🟡 **`ADAPT`** |
| **Last-Read Pin Distinction (Automatic vs Manual)** | Greentech, Quran for Android | Clear difference between where the reader stopped reading versus deliberate bookmarks. | Immediate on home / bookmarks tab | Low | Low | High (Prevents accidental overwrite of saved verses) | Low (Separate DataStore keys) | Positive (Clear semantic labels: "Last Read" vs "Saved Bookmark") | Positive | 🟢 **`ADOPT`** |
| **Aggressive Gamified Streaks, Freezes, and Leaderboards** | Quranly, Tarteel | Drives daily engagement through psychological habit loops and loss aversion. | High | Moderate-High (Badges, animated flame icons, confetti) | High (Creates anxiety, guilt, and performance pressure) | Violates Amanah Calm & Sacred Reverence Principle | Medium | Negative (Noisy celebratory alerts, flashing animations) | Negative (Requires cloud sync to maintain streak credibility) | 🔴 **`AVOID`** |
| **Calm, Quiet Personal Reading History (Non-Gamified)** | Amanah Quran Proposed Pattern | Gives readers a gentle record of Quran contact without guilt, counters, or social comparison. | Subtle (Accessible in secondary view) | Very Low (Minimal clean calendar dots) | Very Low (Informational, not punitive) | Extremely High (Matches Sadaqah / Spiritual Calm) | Low (Local SQLite timestamp log) | Positive (Calm, high contrast, non-distracting) | Positive (100% on-device private log) | 🟢 **`ADOPT`** |
| **Reader Auto-Scroll with Speed Slider** | The Glorious Quran, Aasan Tarjuma, Quran Majeed, Greentech | Hands-free continuous reading during personal reflection or tilawat. | Medium (Icon on reader toolbar) | Low (Small floating speed pill) | Low-Medium | Moderate (Considered for future accessibility refinement) | Medium (Smooth coroutine scroll physics) | Mixed (Helpful for motor impairment, but can disorient elder readers if too fast) | Positive | 🔵 **`INVESTIGATE`** |
| **Offline Asset Verification & Download Status** | Quran for Android, Greentech | Informs user if assets are ready for offline use. | High | Low | Low | Amanah is 100% offline out-of-the-box (Zero post-install download) | Low | Positive (Reassurance of offline independence) | Positive | 🟡 **`ADAPT`** |
| **Progressive Disclosure in Settings (Categorized Cards)** | Quran for Android, Amanah Quran | Prevents cognitive overload by grouping related settings (Appearance, Text, Storage, About). | High | Low | Very Low | Extremely High | Low | Positive (Structured headings and clear group focus) | Positive | 🟢 **`ADOPT`** |
| **High-Contrast Night / Dark / Sepia Theming** | All Apps (Varying quality) | Reduces eye strain in low-light environments; warm sepia mimics aged paper. | High (Theme switcher in settings & reader) | Zero extra UI | Low | Extremely High (Amanah 4-theme system: Light, Dark, Sepia, System) | Low (Compose ColorScheme) | Crucial (WCAG AAA contrast ratios) | Positive | 🟢 **`ADOPT`** |
| **Dedicated Elder Accessibility Mode** | Amanah Quran Unique Innovation | Instant one-tap re-layout with 140%+ font sizing, extra line padding, 56dp touch targets, and simplified chrome. | Prominent toggle in Settings & Reader | Low toggle cost, massive layout benefit | Zero for elders | Exemplary Amanah Core Innovation | Medium (Adaptive layout tokens) | Transformative (Best-in-class elder usability) | Positive | 🟢 **`ADOPT`** |
| **Full-Screen Video Ads & Sticky Banners** | Quran Majeed, Muslim Pro | Monetization for commercial developers. | Intrusive | Disastrous (Obscures sacred text, clutters screens) | Disastrous (Interrupts spiritual connection) | STRICTLY PROHIBITED BY AMANAH CHARTER | High (Third-party ad mediation) | Severe Defect (Trap focus, unexpected audio, tiny dismiss targets) | Severe Defect (Extensive data collection) | 🔴 **`AVOID`** |
| **Subscription Paywalls ($/mo, $/yr, Lifetime)** | Tarteel, Quranly, Quran Majeed, Muslim Pro | Recurring revenue for commercial SaaS operators. | Persistent popups | Moderate-High | High (Commercial tension in worship context) | STRICTLY PROHIBITED BY AMANAH CHARTER | High (Google Play Billing API) | Negative | Negative | 🔴 **`AVOID`** |

---

## 2. Detailed Pattern Recommendations & Architectural Rationale

### 1. Entire "Continue Reading" Hero Card is Tappable
- **Recommendation**: **`ADOPT`**
- **Used In**: `Tarteel, Quranly, Greentech`
- **Core User Benefit**: Instant single-tap resumption to the exact last-read ayah/page without hunting through indices.
- **Amanah Alignment**: High (Core to Amanah Sacred Reader MVP)
- **Accessibility & Privacy Impact**: Positive (Large 48dp+ tap target, screen reader reads full verse location) | Positive (100% local DataStore/Room)
- **Implementation Strategy for Amanah Quran**:
  Implement cleanly within Jetpack Compose using local Room/DataStore state. Ensure full test coverage.

### 2. Compact Home Information Hierarchy (Reading-First)
- **Recommendation**: **`ADOPT`**
- **Used In**: `Quran for Android, Amanah Quran`
- **Core User Benefit**: Zero distraction upon launch; immediate visibility of Surah/Juz/Page entry points.
- **Amanah Alignment**: Extremely High (Amanah Core Philosophy)
- **Accessibility & Privacy Impact**: Positive (Simple list/tab focus order) | Positive (Zero telemetry)
- **Implementation Strategy for Amanah Quran**:
  Implement cleanly within Jetpack Compose using local Room/DataStore state. Ensure full test coverage.

### 3. Super-App Widget Clutter (Prayer, Qibla, Shopping, Ads)
- **Recommendation**: **`AVOID`**
- **Used In**: `Quran Majeed, Muslim Pro`
- **Core User Benefit**: Consolidates multiple utilities into a single app.
- **Amanah Alignment**: Violates Amanah Scope Guardrail
- **Accessibility & Privacy Impact**: Negative (Cluttered tab stops, noisy screen reader experience) | Negative (Location tracking, ad network IDs)
- **Implementation Strategy for Amanah Quran**:
  Permanently exclude from codebase. Maintain strict architectural guardrails to prevent regression.

### 4. Reader Settings Accessible via Compact Bottom Sheet
- **Recommendation**: **`ADOPT`**
- **Used In**: `Greentech, Tarteel, The Glorious Quran`
- **Core User Benefit**: Allows adjusting script, font size, and themes without leaving the reading flow.
- **Amanah Alignment**: High (Preserves reading immersion)
- **Accessibility & Privacy Impact**: Positive (Modal accessibility announcements, live text scale preview) | Positive (Local StateFlow)
- **Implementation Strategy for Amanah Quran**:
  Implement cleanly within Jetpack Compose using local Room/DataStore state. Ensure full test coverage.

### 5. Direct Script Switcher (IndoPak <-> Uthmani) Inside Reader
- **Recommendation**: **`ADOPT`**
- **Used In**: `Amanah Quran, Greentech`
- **Core User Benefit**: Enables immediate script switching to suit reader familiarity without navigating global settings.
- **Amanah Alignment**: Extremely High (Amanah Core Requirement: IndoPak + Uthmani parity)
- **Accessibility & Privacy Impact**: Positive (High utility for South Asian and Arab readers) | Positive (Local font rendering)
- **Implementation Strategy for Amanah Quran**:
  Implement cleanly within Jetpack Compose using local Room/DataStore state. Ensure full test coverage.

### 6. Translation Selector Inside Reader Chrome
- **Recommendation**: **`ADOPT`**
- **Used In**: `The Glorious Quran, Greentech, Quran for Android`
- **Core User Benefit**: Enables toggling between Urdu (Irfan-ul-Quran) and English (Manifest Quran) on the fly.
- **Amanah Alignment**: High (Amanah translation context)
- **Accessibility & Privacy Impact**: Positive (Direct locale and commentary switching) | Positive (Bundled offline text)
- **Implementation Strategy for Amanah Quran**:
  Implement cleanly within Jetpack Compose using local Room/DataStore state. Ensure full test coverage.

### 7. Tap Ayah for Contextual Action Sheet / Menu
- **Recommendation**: **`ADOPT`**
- **Used In**: `Quran for Android, Greentech, Amanah Quran`
- **Core User Benefit**: Reveals Bookmark, Share, Tafsir, and Copy actions directly related to the specific verse.
- **Amanah Alignment**: High
- **Accessibility & Privacy Impact**: Positive (Ayah accessible actions dialog) | Positive (Local actions)
- **Implementation Strategy for Amanah Quran**:
  Implement cleanly within Jetpack Compose using local Room/DataStore state. Ensure full test coverage.

### 8. Tap Verse Marker (Ayah End Symbol) for Actions
- **Recommendation**: **`ADAPT`**
- **Used In**: `The Glorious Quran, Aasan Tarjuma`
- **Core User Benefit**: Clear visible affordance that does not interfere with reading the sacred Arabic words.
- **Amanah Alignment**: High
- **Accessibility & Privacy Impact**: Positive (Explicit touch target 48x48dp) | Positive
- **Implementation Strategy for Amanah Quran**:
  Strip out commercial, online, or complex aspects; retain only the calm, sacred, local utility.

### 9. Contextual Top Action Toolbar (Replaces Standard App Bar)
- **Recommendation**: **`ADOPT`**
- **Used In**: `Quran for Android`
- **Core User Benefit**: Highlights selected verse and presents clear icons for Copy, Bookmark, and Translation.
- **Amanah Alignment**: High
- **Accessibility & Privacy Impact**: Positive (Contextual action bar announce) | Positive
- **Implementation Strategy for Amanah Quran**:
  Implement cleanly within Jetpack Compose using local Room/DataStore state. Ensure full test coverage.

### 10. Page-Style Mushaf View (Madani / 15-Line Reproduction)
- **Recommendation**: **`ADOPT`**
- **Used In**: `Quran for Android, Quran Majeed, Greentech`
- **Core User Benefit**: Provides sacred page layout matching physical printed Quran used for Hifz.
- **Amanah Alignment**: High (Amanah Mushaf Reader)
- **Accessibility & Privacy Impact**: Needs Care (Requires accessible verse-by-verse overlay) | Positive
- **Implementation Strategy for Amanah Quran**:
  Implement cleanly within Jetpack Compose using local Room/DataStore state. Ensure full test coverage.

### 11. Continuous Vertical Scrolling Reader (Continuous Mode)
- **Recommendation**: **`ADOPT`**
- **Used In**: `The Glorious Quran, Aasan Tarjuma, Greentech`
- **Core User Benefit**: Comfortable modern mobile scrolling with natural reading rhythm and inline translations.
- **Amanah Alignment**: High (Amanah Ayah Reader)
- **Accessibility & Privacy Impact**: Excellent (Standard vertical accessibility scrolling, linear navigation) | Positive
- **Implementation Strategy for Amanah Quran**:
  Implement cleanly within Jetpack Compose using local Room/DataStore state. Ensure full test coverage.

### 12. Independent Arabic and Translation Font Sizing Sliders
- **Recommendation**: **`ADOPT`**
- **Used In**: `Aasan Tarjuma, Greentech, The Glorious Quran`
- **Core User Benefit**: Allows users to scale Arabic up for visual clarity while keeping translation compact, or vice versa.
- **Amanah Alignment**: Extremely High (Essential for Elder Mode & bilingual balance)
- **Accessibility & Privacy Impact**: Crucial (Prevents clipping while supporting low-vision readers) | Positive
- **Implementation Strategy for Amanah Quran**:
  Implement cleanly within Jetpack Compose using local Room/DataStore state. Ensure full test coverage.

### 13. Quick Surah / Juz Jump Modal (Numeric Grid / Sheet)
- **Recommendation**: **`ADOPT`**
- **Used In**: `Quran for Android, Tarteel, Greentech`
- **Core User Benefit**: Jump instantly to Surah X, Ayah Y without scrolling through 114 Surahs.
- **Amanah Alignment**: High
- **Accessibility & Privacy Impact**: Positive (Keyboard accessible direct verse navigation) | Positive
- **Implementation Strategy for Amanah Quran**:
  Implement cleanly within Jetpack Compose using local Room/DataStore state. Ensure full test coverage.

### 14. Search Suggestion Chips & Recent Queries
- **Recommendation**: **`ADOPT`**
- **Used In**: `Greentech, Quran Majeed`
- **Core User Benefit**: Helps users quickly jump to popular Surahs (e.g. Yasin, Al-Mulk, Al-Kahf, Ayatul Kursi).
- **Amanah Alignment**: High (Helps non-Arabic speakers find key verses)
- **Accessibility & Privacy Impact**: Positive (One-tap search targets) | Positive (Local history only)
- **Implementation Strategy for Amanah Quran**:
  Implement cleanly within Jetpack Compose using local Room/DataStore state. Ensure full test coverage.

### 15. Bookmark Categorization (Tags / Folders / Colors)
- **Recommendation**: **`ADAPT`**
- **Used In**: `Quran for Android (Tags), Greentech (Collections)`
- **Core User Benefit**: Keeps daily recitation bookmarks distinct from study verses or favorites.
- **Amanah Alignment**: Moderate (Keep simple in V1, expand in V2)
- **Accessibility & Privacy Impact**: Positive (Better organization for large collections) | Positive
- **Implementation Strategy for Amanah Quran**:
  Strip out commercial, online, or complex aspects; retain only the calm, sacred, local utility.

### 16. Last-Read Pin Distinction (Automatic vs Manual)
- **Recommendation**: **`ADOPT`**
- **Used In**: `Greentech, Quran for Android`
- **Core User Benefit**: Clear difference between where the reader stopped reading versus deliberate bookmarks.
- **Amanah Alignment**: High (Prevents accidental overwrite of saved verses)
- **Accessibility & Privacy Impact**: Positive (Clear semantic labels: "Last Read" vs "Saved Bookmark") | Positive
- **Implementation Strategy for Amanah Quran**:
  Implement cleanly within Jetpack Compose using local Room/DataStore state. Ensure full test coverage.

### 17. Aggressive Gamified Streaks, Freezes, and Leaderboards
- **Recommendation**: **`AVOID`**
- **Used In**: `Quranly, Tarteel`
- **Core User Benefit**: Drives daily engagement through psychological habit loops and loss aversion.
- **Amanah Alignment**: Violates Amanah Calm & Sacred Reverence Principle
- **Accessibility & Privacy Impact**: Negative (Noisy celebratory alerts, flashing animations) | Negative (Requires cloud sync to maintain streak credibility)
- **Implementation Strategy for Amanah Quran**:
  Permanently exclude from codebase. Maintain strict architectural guardrails to prevent regression.

### 18. Calm, Quiet Personal Reading History (Non-Gamified)
- **Recommendation**: **`ADOPT`**
- **Used In**: `Amanah Quran Proposed Pattern`
- **Core User Benefit**: Gives readers a gentle record of Quran contact without guilt, counters, or social comparison.
- **Amanah Alignment**: Extremely High (Matches Sadaqah / Spiritual Calm)
- **Accessibility & Privacy Impact**: Positive (Calm, high contrast, non-distracting) | Positive (100% on-device private log)
- **Implementation Strategy for Amanah Quran**:
  Implement cleanly within Jetpack Compose using local Room/DataStore state. Ensure full test coverage.

### 19. Reader Auto-Scroll with Speed Slider
- **Recommendation**: **`INVESTIGATE`**
- **Used In**: `The Glorious Quran, Aasan Tarjuma, Quran Majeed, Greentech`
- **Core User Benefit**: Hands-free continuous reading during personal reflection or tilawat.
- **Amanah Alignment**: Moderate (Considered for future accessibility refinement)
- **Accessibility & Privacy Impact**: Mixed (Helpful for motor impairment, but can disorient elder readers if too fast) | Positive
- **Implementation Strategy for Amanah Quran**:
  Conduct targeted usability testing with elder and non-native readers before committing to roadmap.

### 20. Offline Asset Verification & Download Status
- **Recommendation**: **`ADAPT`**
- **Used In**: `Quran for Android, Greentech`
- **Core User Benefit**: Informs user if assets are ready for offline use.
- **Amanah Alignment**: Amanah is 100% offline out-of-the-box (Zero post-install download)
- **Accessibility & Privacy Impact**: Positive (Reassurance of offline independence) | Positive
- **Implementation Strategy for Amanah Quran**:
  Strip out commercial, online, or complex aspects; retain only the calm, sacred, local utility.

### 21. Progressive Disclosure in Settings (Categorized Cards)
- **Recommendation**: **`ADOPT`**
- **Used In**: `Quran for Android, Amanah Quran`
- **Core User Benefit**: Prevents cognitive overload by grouping related settings (Appearance, Text, Storage, About).
- **Amanah Alignment**: Extremely High
- **Accessibility & Privacy Impact**: Positive (Structured headings and clear group focus) | Positive
- **Implementation Strategy for Amanah Quran**:
  Implement cleanly within Jetpack Compose using local Room/DataStore state. Ensure full test coverage.

### 22. High-Contrast Night / Dark / Sepia Theming
- **Recommendation**: **`ADOPT`**
- **Used In**: `All Apps (Varying quality)`
- **Core User Benefit**: Reduces eye strain in low-light environments; warm sepia mimics aged paper.
- **Amanah Alignment**: Extremely High (Amanah 4-theme system: Light, Dark, Sepia, System)
- **Accessibility & Privacy Impact**: Crucial (WCAG AAA contrast ratios) | Positive
- **Implementation Strategy for Amanah Quran**:
  Implement cleanly within Jetpack Compose using local Room/DataStore state. Ensure full test coverage.

### 23. Dedicated Elder Accessibility Mode
- **Recommendation**: **`ADOPT`**
- **Used In**: `Amanah Quran Unique Innovation`
- **Core User Benefit**: Instant one-tap re-layout with 140%+ font sizing, extra line padding, 56dp touch targets, and simplified chrome.
- **Amanah Alignment**: Exemplary Amanah Core Innovation
- **Accessibility & Privacy Impact**: Transformative (Best-in-class elder usability) | Positive
- **Implementation Strategy for Amanah Quran**:
  Implement cleanly within Jetpack Compose using local Room/DataStore state. Ensure full test coverage.

### 24. Full-Screen Video Ads & Sticky Banners
- **Recommendation**: **`AVOID`**
- **Used In**: `Quran Majeed, Muslim Pro`
- **Core User Benefit**: Monetization for commercial developers.
- **Amanah Alignment**: STRICTLY PROHIBITED BY AMANAH CHARTER
- **Accessibility & Privacy Impact**: Severe Defect (Trap focus, unexpected audio, tiny dismiss targets) | Severe Defect (Extensive data collection)
- **Implementation Strategy for Amanah Quran**:
  Permanently exclude from codebase. Maintain strict architectural guardrails to prevent regression.

### 25. Subscription Paywalls ($/mo, $/yr, Lifetime)
- **Recommendation**: **`AVOID`**
- **Used In**: `Tarteel, Quranly, Quran Majeed, Muslim Pro`
- **Core User Benefit**: Recurring revenue for commercial SaaS operators.
- **Amanah Alignment**: STRICTLY PROHIBITED BY AMANAH CHARTER
- **Accessibility & Privacy Impact**: Negative | Negative
- **Implementation Strategy for Amanah Quran**:
  Permanently exclude from codebase. Maintain strict architectural guardrails to prevent regression.
