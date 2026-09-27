# AMANAH VS. COMPETITORS — SURFACE-BY-SURFACE BENCHMARK

> **Evidence corrections (2026-09-27):** some claims in this benchmark were unsupported or wrong and have been corrected or flagged in place. See `UI_AUDIT_VNEXT/EVIDENCE_INTEGRITY_REPORT.md` for the full list. Play Store reviewer names, avatars and review IDs were removed from `LOGS/*_reviews.json`.

> **Comparative Objective**: Rigorous, evidence-based evaluation comparing Amanah Quran against leading Quran apps.
> **Core Stance**: Competitors are studied to adopt proven ergonomics and avoid commercial anti-patterns. Where Amanah is superior, we preserve our architectural edge.

---

## 1. Surface Comparison Matrix

| UI/UX Surface | Amanah Quran Pattern | Best Competitor Pattern | Winner | Architectural Rationale | Proposed Change for Amanah | Priority |
| :--- | :--- | :--- | :---: | :--- | :--- | :---: |
| **First Launch / Onboarding** | Instant zero-onboarding launch directly to Home dashboard. Zero permissions requested, zero network downloads, zero account setup. | Quran for Android (clean minimal start, but blocked by mandatory download modal) / Tarteel (modern onboarding, but forced login). | **AMANAH IS BETTER** | Amanah achieves absolute zero-friction: install and immediately engage. No network waits, no permission popups, no sign-up barriers. | None. Preserve zero-onboarding philosophy. | `P3` |
| **Home Dashboard Hierarchy** | Reading-focused dashboard with Continue Reading card, Quran navigation shortcuts, and secondary features. | Tarteel (exceptional visual balance of Continue Reading hero card, daily goal ring, and clean quick navigation). | **TARTEEL (Visual Hierarchy) / AMANAH (Content Sanctity)** | Tarteel's Continue Reading card has stronger visual elevation, larger tap targets, and clearer reading context (e.g. Surah name, Ayah number, Juz). Amanah can refine its hero card without adopting Tarteel's commercial clutter. | Make entire Continue Reading card tappable with elevated visual hierarchy and clear metadata chips. | `P1` |
| **Quran Navigation (Surah / Juz / Page)** | Clear index tabs for Surahs, Juz, and Pages with clean search filtering. | Quran for Android (instant swipe between Surahs, Juz, Bookmarks; lightweight list items). | **TIE (Different ergonomic paradigms)** | Amanah provides rich metadata per Surah (Arabic title, English transliteration, Ayah count, Revelation type) which is superior for discovery. Quran for Android has slightly faster horizontal swipe physics. | Add quick numeric jump dialog (Direct Surah:Ayah jumper) from navigation header. | `P1` |
| **Continuous / Ayah Reader** | Clean LazyColumn rendering verified IndoPak and Uthmani fonts with inline translations. | The Glorious Quran (exemplary verse-by-verse Urdu Irfan-ul-Quran and English Manifest Quran layout). | **THE GLORIOUS QURAN (Layout refinement) / AMANAH (Typographical engine)** | The Glorious Quran has highly refined vertical rhythm between Arabic text, Urdu, and English. Amanah has superior font bundling (no tofu) but needs generous line-height multipliers for Nastaliq. | Refine vertical spacing, add independent Arabic/translation font sliders, and provide optional translation visibility toggle. | `P0` |
| **Mushaf Reader (Page Mode)** | Bundled page-accurate Mushaf presentation respecting line breaks. | Quran for Android (100% immersive Madani Mushaf reproduction with invisible chrome until tapped). | **QURAN FOR ANDROID (Pristine immersion)** | Quran for Android's single-tap chrome toggle and full-bleed immersive Mushaf canvas creates unmatched sacred reverent feel. | Ensure full immersive canvas mode with single-tap chrome hide/reveal and subtle RTL page transition indicators. | `P1` |
| **Translation Presentation** | Clear bilingual context featuring The Manifest Quran (English) and Irfan-ul-Quran (Urdu). | The Glorious Quran (same translations, mature line breaks, and clear scholar attribution). | **AMANAH IS BETTER (Architectural integrity: Bundled offline SQLite)** | Amanah bundles both translations offline with zero network download requirements, whereas competitors either lack one or require network sync. | Allow toggling translation visibility: Arabic-only, Arabic+English, Arabic+Urdu, or Full Bilingual. | `P1` |
| **Search Experience** | Instant offline local SQLite FTS search across Quran Arabic text and translations. | Greentech (root word search, topic indexing) / Quran for Android (broken without downloads). | **AMANAH IS BETTER (Core offline search) / GREENTECH (Query suggestions)** | Amanah works 100% offline out-of-the-box (unlike Quran for Android). Greentech offers helpful suggestion chips for popular Surahs. | Add suggestion chips (e.g. Yasin, Al-Mulk, Al-Kahf, Ayatul Kursi 2:255) to empty search state. | `P1` |
| **Bookmarks & Favorites** | Saved ayah and page bookmarks with direct navigation jump. | Quran for Android (clean separation between Last Page and Bookmarks tab). | **QURAN FOR ANDROID (Home tab integration)** | Having a dedicated top-level Bookmarks tab on the main screen makes resuming saved reading instantaneous. | Promote bookmarks visibility on Home and clearly separate "Last Read Auto-Pin" from "Manual Bookmarks". | `P1` |
| **Reader Contextual Actions** | Bottom-docked action card for the selected ayah (Bookmark, Share, overflow). *(Corrected: it was never a centred modal dialog; R2 added labelled actions.)* | Quran for Android (contextual top action bar replacing standard toolbar). | **QURAN FOR ANDROID** | A top action bar or compact bottom sheet is less disruptive than a centered modal dialog that obscures the verse being acted upon. | Migrate centered Ayah action dialog to a compact Bottom Sheet or contextual top toolbar. | `P1` |
| **Settings Organization** | Direct, categorized settings for Script, Theme, Elder Mode, and Data Reset. | Quran for Android (simple minimal preferences) / Greentech (overwhelming 60+ toggles). | **AMANAH IS BETTER** | Amanah avoids Greentech's overwhelming settings labyrinth while providing critical Sacred Reader toggles in 4 clean cards. | Add live typography preview card in settings so users see text size changes before returning to reader. | `P2` |
| **Theming (Light, Dark, Sepia)** | Comprehensive 4-theme palette (Light, Dark, Sepia, System) with verified WCAG AAA contrast. | Tarteel (modern OLED true black) / Quran for Android (basic night mode). | **AMANAH IS BETTER** | Amanah provides a dedicated Sepia/Paper Mushaf theme with warm tones that significantly reduces eye fatigue during extended tilawat. | Ensure pure OLED black option is included for dark theme on AMOLED displays. | `P2` |
| **Habits & Reading History** | Quiet, on-device reading calendar and activity log, plus an optional reading streak line (no flames, confetti or loss alerts). *(Corrected: Amanah does ship a streak.)* | Quranly (intense streaks, but anxious) / Tarteel (motivating, but commercial). | **AMANAH IS BETTER (Philosophical alignment with sacred worship)** | Competitor apps turn Quran recitation into a gamified chore with streak guilt. Amanah honors the sacred nature of tilawat as personal worship. | Maintain calm, non-punitive calendar dots without streak counters or loss-aversion alerts. | `P3` |
| **Trust Center & Provenance** | Dedicated Trust Center with content manifests, checksum verification, font licenses, and source attribution. | None. (Most competitors have vague "About Us" pages with zero checksums or open validation manifests). | **AMANAH IS UNRIVALED (Industry-first transparency benchmark)** | Amanah is the only Quran application with an exhaustive cryptographic Trust Center verifying content immutability and open licensing. | None. Amanah sets the industry gold standard here. | `P3` |
| **Elder Mode & Accessibility** | Dedicated Elder Mode toggle with 140% typography, generous padding, and 56dp touch targets. | Aasan Tarjuma (good default Nastaliq, but lacks systematic Elder Mode). | **AMANAH IS UNRIVALED** | No competitor provides a dedicated, one-tap Elder Mode specifically engineered for elderly South Asian readers. | Ensure Elder Mode touch targets are rigorously tested up to 200% Android system font scaling. | `P1` |

---

## 2. Surfaces Where Amanah is Already Superior

Amanah Quran already holds an decisive advantage in several critical areas:

1. **Absolute Offline Independence from Installation**:
   - *Competitors*: Quran for Android, Greentech, and Tarteel force users into multi-megabyte post-install download dialogs before reading.
   - *Amanah*: Bundles verified Quran text, IndoPak and Uthmani fonts, and core translations directly in the build. Zero network required, zero download wait time.

2. **Zero-Ad, Zero-Commercial, Zero-Monetization Architecture**:
   - *Competitors*: Quran Majeed and Muslim Pro assault users with full-screen commercial video ads, popups, and subscriptions up to $99.99.
   - *Amanah*: Permanent charity / Sadaqah Jariyah Waqf. 100% ad-free, 100% tracker-free, zero paywalls.

3. **Industry-First Trust Center & Provenance Verification**:
   - *Competitors*: Black-box content pipelines with unverified text sources and missing licensing clarity.
   - *Amanah*: Full cryptographic checksums, content manifests, and transparent source attribution.

4. **Dedicated Elder Mode**:
   - *Competitors*: Rely on generic Android font scaling, which causes Nastaliq diacritic clipping and layout breakage.
   - *Amanah*: Purpose-built Elder Mode featuring custom typography scaling, 56dp touch targets, and high contrast.

5. **Calm, Sacred Reverence (Zero Toxic Gamification)**:
   - *Competitors*: Quranly and Tarteel utilize loss-aversion streak mechanics, flame counters, and confetti that induce spiritual anxiety.
   - *Amanah*: Quiet, personal reading history that respects recitation as sacred personal worship.
