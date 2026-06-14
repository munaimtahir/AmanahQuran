# Product Requirements Document

# Amanah Quran — Version 1: Sacred Reader MVP

## 1. Document Control

| Item               | Detail                                                                      |
| ------------------ | --------------------------------------------------------------------------- |
| Product            | Amanah Quran                                                                |
| Project identity   | Amanah-e-Kisa                                                               |
| Version            | V1.0 Sacred Reader MVP                                                      |
| Platform           | Android                                                                     |
| Product model      | Free, ad-free, no in-app purchases, no monetization                         |
| Data model         | Offline-first local Quran database                                          |
| Primary audience   | Pakistan / Urdu-speaking / IndoPak-script users                             |
| Secondary audience | Global Muslims preferring Uthmani script                                    |
| Release type       | Public MVP only after content verification                                  |
| Core promise       | Verified offline Quran reading without ads, tracking, login, or distraction |

---

## 2. Product Vision

Amanah Quran V1 will be a highly trustworthy, beautiful, fast, offline Android Quran reader focused on one sacred task: allowing users to read the Holy Quran with confidence, clarity, and peace.

The first version must not try to become a full Islamic super-app. It must establish trust by doing the core reader exceptionally well.

### Mission Line

> A verified, offline, ad-free Quran app built as an Amanah.

---

## 3. Product Principles

### 3.1 Non-Negotiable Principles

1. **No ads ever.**
2. **No tracking.**
3. **No monetization pressure.**
4. **No forced login.**
5. **No unnecessary permissions.**
6. **No modification of Quran display text.**
7. **Full offline Quran reading after install.**
8. **Verified Quranic content only.**
9. **Transparent Trust Center.**
10. **Respectful, distraction-free reading experience.**

### 3.2 Content Principles

1. Quran display text must remain exactly as imported from verified source data.
2. Search-normalized text must be stored separately from display text.
3. IndoPak and Uthmani scripts must be treated as separate verified text assets.
4. Every content source must have:

   * Source name
   * Source URL
   * Version
   * License status
   * Checksum
   * Import date
   * Validation status
5. Public release requires manual content review before launch.

### 3.3 UX Principles

1. Reading should feel calm, sacred, and uncluttered.
2. The app should open quickly and resume reading easily.
3. Elder users should be able to use the app without confusion.
4. Settings must not overwhelm first-time users.
5. Trust information should be visible but not intrusive.
6. No popups during recitation.

---

## 4. Version 1 Scope

### 4.1 V1 Product Theme

**Sacred Reader MVP**

Amanah Quran V1 is limited to offline Quran reading and trust-building features.

### 4.2 In Scope

1. Offline full Quran Arabic text.
2. IndoPak script.
3. Uthmani script.
4. Script switching.
5. Surah navigation.
6. Juz navigation.
7. Page navigation.
8. Ayah-level reading.
9. Last-read position.
10. Bookmarks.
11. Search.
12. Elder Mode.
13. Themes.
14. Trust Center.
15. Source Attribution.
16. No ads / no tracking / no login architecture.

### 4.3 Out of Scope for V1

The following must not be implemented in Version 1:

1. Audio recitation.
2. Urdu translation.
3. English translation.
4. Tafsir.
5. Word-by-word meaning.
6. Hifz tools.
7. Recitation correction.
8. AI features.
9. Prayer times.
10. Qibla.
11. Islamic calendar.
12. Hadith database.
13. Social feed.
14. Cloud sync.
15. User accounts.
16. Donations inside the app.
17. In-app purchases.
18. Analytics SDK.
19. Ads SDK.
20. Push notifications.

---

## 5. Target Users

## 5.1 Primary Personas

### Persona A — Daily Reader in Pakistan

**Need:** Wants a simple, ad-free Quran app with IndoPak script.
**Pain:** Many apps contain ads, interruptions, tracking, or clutter.
**V1 Solution:** Opens directly to reading, supports IndoPak script, resumes last position, works offline.

### Persona B — Elder User

**Need:** Large text, simple buttons, minimal settings.
**Pain:** Small icons, crowded UI, hard navigation.
**V1 Solution:** Elder Mode with large Arabic text, larger buttons, simplified navigation, high readability.

### Persona C — Trust-Conscious User

**Need:** Wants to know the Quran text is verified and not modified.
**Pain:** Distrusts commercial apps and unclear sources.
**V1 Solution:** Trust Center shows source, version, checksum, validation status, and no-modification statement.

### Persona D — Uthmani Reader

**Need:** Prefers Uthmani script instead of IndoPak.
**Pain:** IndoPak-first apps sometimes ignore global script needs.
**V1 Solution:** Uthmani script is included as a first-class option.

---

## 6. Success Criteria

## 6.1 Product Success

V1 is successful if:

1. A user can install the app and read the full Quran offline.
2. A user can switch between IndoPak and Uthmani script.
3. A user can bookmark any ayah or page.
4. A user can resume from last-read position.
5. A user can search by Surah, ayah reference, juz, page, and Arabic text.
6. Elder Mode makes the app clearly easier for older users.
7. Trust Center transparently displays content source and verification data.
8. The app contains no ads, no trackers, no login, and no monetization prompts.
9. The app performs smoothly on common Android phones used in Pakistan.

## 6.2 Technical Success

1. App cold start target: under 2 seconds on mid-range Android devices.
2. Quran reader screen load target: under 500 ms after database warmup.
3. Search result target: under 1 second for Arabic text search.
4. App works offline with airplane mode enabled.
5. No runtime permissions required for core V1.
6. No analytics SDK included.
7. No ad SDK included.
8. No crash on common OEMs: Samsung, Vivo, Oppo, Xiaomi, Infinix, Tecno.
9. Database integrity checks pass before release.
10. Quran text checksum matches packaged source manifest.

---

## 7. App Structure

## 7.1 Main Navigation

Bottom navigation or simple home-based navigation:

1. **Read**
2. **Search**
3. **Bookmarks**
4. **Settings**
5. **Trust Center**

For Elder Mode, navigation should become larger and simpler:

1. **Continue Reading**
2. **Open Quran**
3. **Bookmarks**
4. **Search**
5. **Settings**

## 7.2 Home Screen

### Required Elements

1. Continue Reading card.
2. Quran navigation card.
3. Search card.
4. Bookmarks card.
5. Trust Center entry.
6. Current selected script indicator.
7. Theme-aware design.

### Home Screen Acceptance Criteria

1. User can resume reading with one tap.
2. User can access Quran list within one tap.
3. User can access Trust Center within two taps.
4. No promotional banner, donation card, ad placeholder, or social module is present.
5. Elder Mode increases button size and improves readability.

---

## 8. Core Feature Requirements

# Feature 1 — Offline Quran Reading

## 8.1 Description

The app must provide the complete Arabic Quran offline after installation.

## 8.2 Functional Requirements

1. The full Quran must be available without internet.
2. User must be able to read by:

   * Surah
   * Juz
   * Page
3. Reader must support vertical scrolling.
4. Reader must support ayah-level display.
5. Reader must show ayah numbers clearly.
6. Reader must support script switching between IndoPak and Uthmani.
7. Reader must preserve scroll position during app background/foreground changes.
8. Reader must restore the last-read position after app restart.
9. Reader must not show popups during reading.
10. Reader must not require login.

## 8.3 Reading Views

### Required V1 Reader Modes

| Reader Mode | Description                        | Required |
| ----------- | ---------------------------------- | -------- |
| Surah View  | Read full Surah in selected script | Yes      |
| Juz View    | Read Quran by Juz                  | Yes      |
| Page View   | Navigate by page metadata          | Yes      |
| Ayah View   | Ayah-level rendering inside reader | Yes      |

## 8.4 Acceptance Criteria

1. With internet disabled, the user can open any Surah.
2. With internet disabled, the user can open any Juz.
3. With internet disabled, the user can open any page.
4. Arabic text renders correctly in both scripts.
5. Ayah numbers are visible and not confused with Quran text.
6. The app does not alter Quran text for display.
7. The reader remains usable on small phones.
8. The reader remains usable on low-end phones.

---

# Feature 2 — IndoPak and Uthmani Script Support

## 9.1 Description

V1 must support both IndoPak and Uthmani Quran scripts as first-class reading options.

## 9.2 Functional Requirements

1. User can switch script from:

   * Reader toolbar
   * Settings
2. Script selection is saved locally.
3. Current script is applied across:

   * Surah view
   * Juz view
   * Page view
   * Search result previews
   * Bookmarks
4. Script switching must not change bookmark identity.
5. Bookmark identity must be based on canonical ayah key, not visual text.
6. Search should work regardless of currently selected display script.
7. Both scripts must have independent source metadata in Trust Center.

## 9.3 Data Requirements

Each ayah must have:

1. `surah_number`
2. `ayah_number`
3. `ayah_key`, e.g. `2:255`
4. `text_uthmani`
5. `text_indopak`
6. `page_number`
7. `juz_number`
8. `hizb_number`, if available
9. `ruku_number`, if available
10. `sajdah_marker`, if applicable

## 9.4 Acceptance Criteria

1. User can switch from IndoPak to Uthmani without restarting app.
2. User can switch from Uthmani to IndoPak without losing reading position.
3. Bookmarked ayahs remain bookmarked after script switch.
4. Trust Center shows separate source/checksum records for both scripts.
5. Arabic rendering passes visual QA on target devices.

---

# Feature 3 — Navigation

## 10.1 Description

The app must provide simple Quran navigation through Surah, Juz, and Page.

## 10.2 Surah Navigation

Required fields:

1. Surah number.
2. Arabic Surah name.
3. Transliteration or English/Urdu readable name.
4. Revelation type, if verified metadata available.
5. Ayah count.
6. Quick open button.

## 10.3 Juz Navigation

Required fields:

1. Juz number.
2. Starting Surah and ayah.
3. Ending Surah and ayah, if available.
4. Quick open button.

## 10.4 Page Navigation

Required fields:

1. Page number.
2. Starting Surah and ayah.
3. Ending Surah and ayah, if available.
4. Quick open button.

## 10.5 Acceptance Criteria

1. User can reach any Surah within three taps from home.
2. User can reach any Juz within three taps from home.
3. User can reach any page within three taps from home.
4. Navigation works offline.
5. Navigation remains readable in Elder Mode.

---

# Feature 4 — Last-Read Position

## 11.1 Description

The app must automatically save and restore the user’s last reading position.

## 11.2 Functional Requirements

1. Save last-read position when:

   * User opens an ayah.
   * User scrolls beyond a meaningful threshold.
   * App goes to background.
   * App closes.
2. Last-read must store:

   * Surah number
   * Ayah number
   * Page number
   * Juz number
   * Script selected
   * Scroll offset, where technically reliable
   * Timestamp
3. Home screen must show Continue Reading.
4. Reader must resume near the correct ayah.
5. User should not need to manually save last-read.

## 11.3 Acceptance Criteria

1. User closes app and reopens it; Continue Reading opens previous location.
2. User switches script; last-read ayah remains the same.
3. User reads offline; last-read still saves.
4. If exact scroll offset fails, app falls back to ayah-level resume.

---

# Feature 5 — Bookmarks

## 12.1 Description

The app must allow users to save important reading positions locally.

## 12.2 Bookmark Types for V1

| Bookmark Type    | Required |
| ---------------- | -------- |
| Ayah bookmark    | Yes      |
| Page bookmark    | Yes      |
| Named collection | No       |
| Notes            | No       |
| Cloud sync       | No       |

## 12.3 Functional Requirements

1. User can bookmark an ayah.
2. User can remove an ayah bookmark.
3. User can bookmark a page.
4. User can remove a page bookmark.
5. Bookmarks are stored locally.
6. Bookmarks must work offline.
7. Bookmarks must persist after app restart.
8. Bookmarks must not depend on current script.
9. Bookmarks screen must show:

   * Surah name
   * Ayah number or page number
   * Short preview in selected script
   * Date added
10. User can open bookmark directly in reader.

## 12.4 Acceptance Criteria

1. User bookmarks Ayah 2:255 and can find it in Bookmarks.
2. User switches script and bookmark still opens Ayah 2:255.
3. User removes bookmark and it disappears immediately.
4. Bookmarks persist after app restart.
5. Bookmarks work with internet disabled.

---

# Feature 6 — Search

## 13.1 Description

The app must provide simple, fast offline search.

## 13.2 Search Types

V1 search must support:

1. Surah name search.
2. Surah number search.
3. Ayah reference search, e.g. `2:255`.
4. Juz number search.
5. Page number search.
6. Arabic text search.
7. Normalized Arabic search.

## 13.3 Search Input Examples

| User Input    | Expected Result                      |
| ------------- | ------------------------------------ |
| `Yaseen`      | Surah Yaseen                         |
| `36`          | Surah 36 and/or page/juz suggestions |
| `2:255`       | Ayat al-Kursi                        |
| `Juz 30`      | Juz Amma                             |
| `Page 1`      | Page 1                               |
| Arabic phrase | Matching ayahs                       |

## 13.4 Arabic Search Rules

Display Quran text must not be changed. For search only, the app may use a separate normalized text column.

Allowed search normalization:

1. Remove tatweel.
2. Normalize common Arabic letter variants.
3. Optionally ignore harakat for search.
4. Store normalized text separately.
5. Never display normalized text as Quran text.

## 13.5 Search Result Display

Each search result should show:

1. Surah name.
2. Ayah number.
3. Ayah preview in selected script.
4. Matched phrase highlighting, if technically safe.
5. Tap to open in reader.

Highlighting must not distort Quran text. If highlighting risks rendering problems, use a non-invasive result indicator instead.

## 13.6 Acceptance Criteria

1. Search works offline.
2. Search returns Surah by name.
3. Search opens ayah reference correctly.
4. Arabic text search returns matching ayahs.
5. Search does not display normalized Quran text.
6. Search results respect selected script.
7. Search performance remains acceptable on low-end devices.

---

# Feature 7 — Elder Mode

## 14.1 Description

Elder Mode is an accessibility-focused interface for users who need larger text, larger buttons, and simpler navigation.

## 14.2 Functional Requirements

When Elder Mode is enabled:

1. Arabic font size increases.
2. UI text size increases.
3. Buttons become larger.
4. Tap targets become larger.
5. Reader toolbar becomes simpler.
6. Navigation cards become larger.
7. Visual clutter is reduced.
8. Contrast improves.
9. Search input remains large and easy to tap.
10. Bookmark button remains clearly visible.

## 14.3 Elder Mode Defaults

| Element        | Standard Mode | Elder Mode     |
| -------------- | ------------- | -------------- |
| Arabic font    | User selected | Larger default |
| Buttons        | Standard      | Large          |
| Navigation     | Standard      | Simplified     |
| Reader toolbar | Full          | Simplified     |
| Search field   | Standard      | Large          |
| Line spacing   | Normal        | Increased      |
| Icons          | Normal        | Larger         |

## 14.4 Acceptance Criteria

1. Elder Mode can be enabled from Settings.
2. Elder Mode can be disabled from Settings.
3. Elder Mode persists after app restart.
4. Reader remains stable when font size is very large.
5. No important action becomes hidden on small phones.
6. Elder Mode is usable with one hand on common Android devices.

---

# Feature 8 — Themes

## 15.1 Description

The app must support comfortable reading themes.

## 15.2 Required Themes

1. Light Theme.
2. Dark Theme.
3. Sepia / Paper Theme.
4. System Default Theme.

## 15.3 Functional Requirements

1. User can change theme from Settings.
2. Theme setting persists after app restart.
3. Theme applies to:

   * Home
   * Reader
   * Search
   * Bookmarks
   * Settings
   * Trust Center
4. Theme must not reduce Quran readability.
5. Dark mode must avoid harsh contrast.
6. Sepia mode should support long reading comfort.

## 15.4 Acceptance Criteria

1. Theme changes immediately.
2. Theme persists after app restart.
3. Quran text remains readable in all themes.
4. Elder Mode works with all themes.
5. No theme uses decorative backgrounds that reduce readability.

---

# Feature 9 — Trust Center

## 16.1 Description

Trust Center is a dedicated transparency area that explains the app’s content sources, verification status, privacy stance, and no-modification pledge.

## 16.2 Required Trust Center Sections

### Section A — Quran Text Sources

For each script:

1. Script name.
2. Source name.
3. Source URL.
4. Source version.
5. License status.
6. Checksum.
7. Import date.
8. Validation status.
9. Ayah count.
10. Surah count.

### Section B — No Modification Statement

Required message:

> Amanah Quran displays Quranic text exactly as imported from verified source data. Search normalization is stored separately and is never used as display Quran text.

### Section C — Verification Status

Show:

1. Database validation result.
2. Checksum validation result.
3. Ayah count validation.
4. Surah count validation.
5. Last verification date.
6. Manual review status.

### Section D — Privacy Pledge

Required points:

1. No ads.
2. No tracking.
3. No analytics SDK.
4. No advertising ID use.
5. No forced login.
6. No selling or sharing user data.
7. Offline reading works without internet.

### Section E — App Integrity

Show:

1. App version.
2. Quran database version.
3. Content manifest version.
4. Build date, if available.
5. Open-source/license information, if applicable.

## 16.3 Trust Center Acceptance Criteria

1. Trust Center is accessible from Home and Settings.
2. User can see source details for IndoPak script.
3. User can see source details for Uthmani script.
4. User can see checksum values.
5. User can see no-modification statement.
6. User can see privacy pledge.
7. Trust Center works offline.
8. Trust Center must not be hidden behind account creation or internet access.

---

## 17. Technical Requirements

## 17.1 Recommended Android Stack

| Layer                | Recommended Choice                                     |
| -------------------- | ------------------------------------------------------ |
| Language             | Kotlin                                                 |
| UI                   | Jetpack Compose                                        |
| Architecture         | MVVM or MVI-lite                                       |
| Local database       | Room SQLite                                            |
| Settings             | DataStore Preferences                                  |
| Dependency injection | Hilt or lightweight manual DI                          |
| Background jobs      | Not required for V1                                    |
| Audio                | Not included in V1                                     |
| Networking           | Not required for V1                                    |
| Analytics            | Not allowed in V1                                      |
| Ads                  | Never allowed                                          |
| Crash reporting      | Avoid third-party SDK in V1; use local debug logs only |

## 17.2 Minimum Android Target

Recommended:

1. Minimum SDK: Android 8.0 / API 26 or lower if rendering remains stable.
2. Target SDK: Latest stable Play Store requirement at time of release.
3. Orientation: Portrait first.
4. Tablet support: Basic responsive layout, not optimized in V1.
5. Offline support: Mandatory.

## 17.3 Permissions

V1 should require no dangerous runtime permissions.

Avoid:

1. Location.
2. Contacts.
3. Microphone.
4. Camera.
5. Storage permission.
6. Notification permission.
7. Advertising ID.

If source links open externally, use Android browser intent instead of adding in-app network dependency.

---

## 18. Data Architecture

## 18.1 Database Strategy

Use a prepackaged Room SQLite database bundled with the app.

The database should be generated through a controlled import pipeline before release.

## 18.2 Core Tables

### `surahs`

| Field           | Type | Notes                        |
| --------------- | ---- | ---------------------------- |
| id              | Int  | 1–114                        |
| name_arabic     | Text | Verified metadata            |
| name_simple     | Text | Search/display               |
| name_urdu       | Text | Optional for V1 if available |
| revelation_type | Text | Optional verified metadata   |
| ayah_count      | Int  | Required                     |

### `ayahs`

| Field        | Type | Notes                |
| ------------ | ---- | -------------------- |
| id           | Int  | Internal primary key |
| ayah_key     | Text | e.g. `2:255`         |
| surah_number | Int  | Required             |
| ayah_number  | Int  | Required             |
| juz_number   | Int  | Required             |
| page_number  | Int  | Required             |
| hizb_number  | Int  | Optional             |
| ruku_number  | Int  | Optional             |
| sajdah_type  | Text | Optional             |

### `quran_texts`

| Field        | Type | Notes                         |
| ------------ | ---- | ----------------------------- |
| id           | Int  | Primary key                   |
| ayah_key     | Text | Foreign reference             |
| script_type  | Text | `INDOPAK` or `UTHMANI`        |
| display_text | Text | Immutable verified text       |
| source_id    | Int  | Foreign key                   |
| checksum     | Text | Optional per ayah or per pack |

### `search_index`

| Field                      | Type | Notes             |
| -------------------------- | ---- | ----------------- |
| id                         | Int  | Primary key       |
| ayah_key                   | Text | Foreign reference |
| normalized_arabic          | Text | Search only       |
| normalized_transliteration | Text | Optional          |
| surah_search_terms         | Text | For Surah lookup  |

### `bookmarks`

| Field         | Type | Notes                      |
| ------------- | ---- | -------------------------- |
| id            | Int  | Primary key                |
| bookmark_type | Text | `AYAH` or `PAGE`           |
| ayah_key      | Text | Nullable for page bookmark |
| page_number   | Int  | Nullable for ayah bookmark |
| created_at    | Long | Timestamp                  |
| updated_at    | Long | Timestamp                  |

### `last_read`

| Field         | Type | Notes                         |
| ------------- | ---- | ----------------------------- |
| id            | Int  | Single-row table or DataStore |
| ayah_key      | Text | Required                      |
| surah_number  | Int  | Required                      |
| ayah_number   | Int  | Required                      |
| juz_number    | Int  | Required                      |
| page_number   | Int  | Required                      |
| script_type   | Text | Current script                |
| scroll_offset | Int  | Optional                      |
| updated_at    | Long | Required                      |

### `content_sources`

| Field             | Type | Notes                              |
| ----------------- | ---- | ---------------------------------- |
| id                | Int  | Primary key                        |
| content_type      | Text | Quran text / metadata / font       |
| script_type       | Text | IndoPak / Uthmani where applicable |
| source_name       | Text | Required                           |
| source_url        | Text | Required                           |
| license           | Text | Required                           |
| version           | Text | Required                           |
| checksum          | Text | Required                           |
| import_date       | Text | Required                           |
| validation_status | Text | Required                           |
| reviewer_status   | Text | Required                           |

### `content_validation`

| Field           | Type    | Notes           |
| --------------- | ------- | --------------- |
| id              | Int     | Primary key     |
| validation_name | Text    | e.g. ayah count |
| expected_value  | Text    | Required        |
| actual_value    | Text    | Required        |
| passed          | Boolean | Required        |
| checked_at      | Long    | Required        |

---

## 19. Content Import and Verification Pipeline

## 19.1 Import Pipeline Stages

1. Select licensed/approved source.
2. Download source outside app development build.
3. Record source metadata.
4. Generate source checksum.
5. Import into staging database.
6. Validate Surah count = 114.
7. Validate total ayah count = 6236.
8. Validate ayah keys are complete.
9. Validate no duplicate ayah keys.
10. Validate script text not empty.
11. Generate normalized search text separately.
12. Generate final Room SQLite database.
13. Generate content manifest.
14. Run checksum verification.
15. Submit for manual content review.
16. Freeze database for release.

## 19.2 Validation Rules

Required validation checks:

1. Exactly 114 Surahs.
2. Exactly 6236 ayahs.
3. Every ayah has valid Surah number.
4. Every ayah has valid ayah number.
5. Every ayah has display text for selected script.
6. No display text is generated from normalized text.
7. IndoPak and Uthmani packs have independent checksums.
8. Search index count matches ayah count.
9. Page and Juz mapping exists for every ayah.
10. Trust Center metadata exists for every content pack.

## 19.3 Build Gate

A release build must fail if:

1. Content manifest is missing.
2. Quran database checksum fails.
3. Ayah count is not 6236.
4. Surah count is not 114.
5. Any required Trust Center metadata is missing.
6. Any source license field is empty.
7. Any Quran text display field is empty.
8. Any script pack is unverified.

---

## 20. Privacy and Safety Requirements

## 20.1 Privacy Requirements

1. No user login.
2. No account creation.
3. No analytics SDK.
4. No ad SDK.
5. No advertising ID.
6. No device fingerprinting.
7. No location collection.
8. No contact access.
9. No microphone access.
10. No camera access.
11. No background tracking.
12. No server calls for core reader functionality.

## 20.2 Local Data

The app may store locally:

1. Last-read position.
2. Bookmarks.
3. Theme setting.
4. Script setting.
5. Elder Mode setting.

The app must not transmit this data anywhere in V1.

## 20.3 Play Store Data Safety

V1 should be designed so the Data Safety declaration can honestly state:

1. No data collected.
2. No data shared.
3. No location collected.
4. No personal information collected.
5. No analytics data collected.
6. No advertising data collected.

---

## 21. UI Requirements

## 21.1 Visual Direction

The visual identity should communicate:

1. Amanah.
2. Calmness.
3. Trust.
4. Respect.
5. Readability.
6. Simplicity.

Avoid:

1. Overdecorated religious UI.
2. Heavy animations.
3. Distracting gradients.
4. Popups.
5. Gamified streaks.
6. Excessive icons.
7. Banner-like areas that resemble ads.

## 21.2 Reader Screen

Required components:

1. Top app bar with:

   * Back button
   * Current Surah / Juz / Page label
   * Script switch
   * Bookmark action
   * More/settings action
2. Quran text area.
3. Ayah number markers.
4. Optional mini progress indicator.
5. No bottom ad space.
6. No unrelated recommendations.

## 21.3 Reader Typography

Requirements:

1. Arabic font must be highly readable.
2. Font must support selected script properly.
3. Diacritics must render clearly.
4. Line height must be comfortable.
5. User must be able to increase/decrease font size.
6. Elder Mode must override with larger defaults.
7. Avoid excessive letter spacing that breaks Arabic rendering.

## 21.4 Settings Screen

Required settings:

1. Script:

   * IndoPak
   * Uthmani
2. Theme:

   * System
   * Light
   * Dark
   * Sepia
3. Arabic font size.
4. Elder Mode toggle.
5. Trust Center link.
6. Privacy pledge link/about section.

Avoid in V1:

1. Account settings.
2. Sync settings.
3. Donation settings.
4. Notification settings.
5. Audio settings.
6. AI settings.

---

## 22. Accessibility Requirements

1. Elder Mode must be available.
2. Touch targets should be large enough for older users.
3. App must support Android font scaling where safe.
4. Important controls must have content descriptions.
5. Contrast must be acceptable in all themes.
6. Reader should support long reading without visual fatigue.
7. Buttons must not rely only on icons.
8. Search should be accessible and easy to focus.
9. Back navigation must be predictable.
10. The app should work with TalkBack for basic navigation, though Quran recitation by screen reader may require further testing.

---

## 23. Performance Requirements

## 23.1 Startup

1. App should open quickly.
2. No network initialization on launch.
3. No analytics initialization.
4. No ad initialization.
5. Home or reader should appear without unnecessary splash delay.

## 23.2 Reader Performance

1. Reader should use lazy rendering for long Surahs.
2. Avoid loading the entire Quran into memory.
3. Cache current reading range.
4. Avoid expensive text transformations during rendering.
5. Use precomputed normalized search text.

## 23.3 Search Performance

1. Use SQLite FTS where suitable.
2. Keep search index local.
3. Debounce search input.
4. Limit initial results.
5. Provide “show more” behavior if needed.

---

## 24. Android Implementation Notes

## 24.1 Suggested Package Structure

```text
app/
  core/
    database/
    datastore/
    model/
    navigation/
    theme/
    utils/
  feature/
    home/
    reader/
    search/
    bookmarks/
    settings/
    trust/
  content/
    import_manifest/
    validation/
```

## 24.2 Suggested Modules

For MVP simplicity, start with a single Android app module.

Optional later modularization:

1. `core-database`
2. `core-ui`
3. `feature-reader`
4. `feature-search`
5. `feature-trust`

## 24.3 Architecture Pattern

Recommended:

1. Compose UI.
2. ViewModels.
3. Repository layer.
4. Room DAOs.
5. DataStore for preferences.
6. Immutable UI state classes.
7. One-way UI state flow.

## 24.4 Key ViewModels

1. `HomeViewModel`
2. `ReaderViewModel`
3. `SearchViewModel`
4. `BookmarksViewModel`
5. `SettingsViewModel`
6. `TrustCenterViewModel`

## 24.5 Key Repositories

1. `QuranRepository`
2. `BookmarkRepository`
3. `LastReadRepository`
4. `SearchRepository`
5. `SettingsRepository`
6. `TrustRepository`

---

## 25. Quality Assurance Plan

## 25.1 Functional Testing

Test cases:

1. Open app first time.
2. Open Surah Al-Fatihah.
3. Open Surah Al-Baqarah.
4. Open Surah Yaseen.
5. Open Juz 30.
6. Open page 1.
7. Switch IndoPak to Uthmani.
8. Switch Uthmani to IndoPak.
9. Bookmark ayah.
10. Remove bookmark.
11. Bookmark page.
12. Resume last-read.
13. Search Surah name.
14. Search ayah reference.
15. Search Arabic text.
16. Enable Elder Mode.
17. Disable Elder Mode.
18. Switch themes.
19. Open Trust Center.
20. Use app in airplane mode.

## 25.2 Content Testing

Required tests:

1. Surah count = 114.
2. Ayah count = 6236.
3. All ayah keys present.
4. No duplicate ayah keys.
5. No empty IndoPak text.
6. No empty Uthmani text.
7. Juz mapping valid.
8. Page mapping valid.
9. Search index count matches ayah count.
10. Trust Center content source data exists.

## 25.3 Device Testing

Minimum physical device testing:

1. Samsung mid-range.
2. Vivo phone.
3. Oppo phone.
4. Xiaomi/Redmi phone.
5. Infinix or Tecno phone.
6. Small-screen Android device.
7. Large-screen Android device.

## 25.4 Offline Testing

1. Install app.
2. Enable airplane mode.
3. Open every core screen.
4. Read Quran.
5. Search.
6. Bookmark.
7. Resume last-read.
8. Open Trust Center.
9. Change settings.

All must work offline.

---

## 26. Release Criteria

Amanah Quran V1 can be released only when all of the following are true:

1. Full Quran available offline.
2. IndoPak script verified.
3. Uthmani script verified.
4. Source metadata completed.
5. Checksums completed.
6. Trust Center completed.
7. No ads SDK present.
8. No analytics SDK present.
9. No login system present.
10. No unnecessary permissions present.
11. Search works offline.
12. Bookmarks work offline.
13. Last-read works offline.
14. Elder Mode tested.
15. Themes tested.
16. Quran text validation passes.
17. Manual content review completed.
18. App tested on target Android devices.
19. Play Store Data Safety form can truthfully declare no data collection.
20. No monetization feature exists in the build.

---

## 27. Risks and Mitigations

| Risk                                         |   Impact | Mitigation                                        |
| -------------------------------------------- | -------: | ------------------------------------------------- |
| Incorrect Quran text import                  | Critical | Source checksum, validation script, manual review |
| Arabic rendering issues                      |     High | Test fonts/scripts on target devices              |
| IndoPak font licensing issue                 |     High | License review before bundling                    |
| Search accidentally displays normalized text |     High | Separate display and search tables                |
| App becomes bloated                          |   Medium | Strict V1 scope control                           |
| Elder Mode breaks layout                     |   Medium | Small-screen testing                              |
| Page mapping differs by mushaf               |   Medium | Clearly document source metadata                  |
| Trust Center incomplete                      |     High | Release gate requiring metadata                   |
| Third-party SDK privacy concern              |     High | Avoid analytics/crash/ad SDKs in V1               |
| Slow search on low-end phone                 |   Medium | Precomputed index and result limits               |

---

## 28. MVP Development Milestones

## Milestone 1 — Project Foundation

Deliverables:

1. Android project setup.
2. Kotlin + Compose setup.
3. Theme system.
4. Navigation shell.
5. DataStore settings.
6. Room database integration.

Exit criteria:

1. App launches.
2. Home, Reader, Search, Bookmarks, Settings, Trust Center routes exist.
3. No internet or analytics dependency included.

---

## Milestone 2 — Quran Database and Import

Deliverables:

1. Quran source selection.
2. IndoPak script import.
3. Uthmani script import.
4. Metadata import.
5. Search normalization.
6. Content manifest.
7. Validation script.

Exit criteria:

1. 114 Surahs present.
2. 6236 ayahs present.
3. Both scripts available.
4. Checksums generated.
5. Database packaged with app.

---

## Milestone 3 — Reader MVP

Deliverables:

1. Surah reader.
2. Juz reader.
3. Page reader.
4. Script switch.
5. Font size setting.
6. Last-read tracking.

Exit criteria:

1. User can read offline.
2. User can switch scripts.
3. User can resume reading.
4. Reader works on small screens.

---

## Milestone 4 — Bookmarks

Deliverables:

1. Ayah bookmark.
2. Page bookmark.
3. Bookmarks list.
4. Bookmark open/remove actions.

Exit criteria:

1. Bookmarks persist locally.
2. Bookmarks work offline.
3. Bookmarks survive script switching.

---

## Milestone 5 — Search

Deliverables:

1. Surah search.
2. Ayah reference search.
3. Juz search.
4. Page search.
5. Arabic text search.
6. Search result opening.

Exit criteria:

1. Search works offline.
2. Search returns correct references.
3. Search does not alter displayed Quran text.

---

## Milestone 6 — Elder Mode and Themes

Deliverables:

1. Elder Mode toggle.
2. Larger typography.
3. Larger controls.
4. Simplified reader controls.
5. Light theme.
6. Dark theme.
7. Sepia theme.
8. System theme.

Exit criteria:

1. Elder Mode improves usability.
2. Themes persist.
3. Reader remains readable in all themes.

---

## Milestone 7 — Trust Center

Deliverables:

1. Source details screen.
2. Checksum display.
3. No-modification statement.
4. Privacy pledge.
5. App/content version display.
6. Validation status display.

Exit criteria:

1. Trust Center works offline.
2. All content sources visible.
3. Privacy pledge visible.
4. No required metadata missing.

---

## Milestone 8 — Release Hardening

Deliverables:

1. Device testing.
2. Offline testing.
3. Content verification.
4. Privacy review.
5. Play Store readiness.
6. Final build.

Exit criteria:

1. No critical bugs.
2. No unverified content.
3. No unwanted permissions.
4. No ads/tracking/login.
5. Release checklist complete.

---

## 29. Developer Backlog

## P0 — Must Build

1. Android project scaffold.
2. Compose navigation.
3. Room prepackaged database.
4. Quran data models.
5. Quran reader screen.
6. IndoPak/Uthmani script switch.
7. Surah navigation.
8. Juz navigation.
9. Page navigation.
10. Last-read save/restore.
11. Ayah bookmark.
12. Page bookmark.
13. Bookmarks screen.
14. Offline search.
15. Elder Mode.
16. Light/dark/sepia themes.
17. Trust Center.
18. Content validation.
19. No-permission privacy review.

## P1 — Important Polish

1. Smooth reader scrolling.
2. Better page progress indicator.
3. Search result highlighting if safe.
4. Quick Surah chips for common Surahs.
5. Better Arabic typography tuning.
6. Better empty states.
7. Better first-launch defaults.
8. Better small-screen layout.

## P2 — Not for V1 Unless Time Allows

1. Urdu interface.
2. Share ayah text.
3. Share ayah image.
4. Export bookmarks.
5. Import bookmarks.
6. Open-source license screen.
7. Advanced accessibility testing.

---

## 30. First-Launch Experience

Recommended V1 approach:

1. No long onboarding.
2. No account prompt.
3. No donation prompt.
4. No permission prompt.
5. Open to Home with Continue Reading placeholder.
6. Default script: IndoPak.
7. User can switch script easily.
8. System theme selected by default.
9. Trust Center visible but not forced.

First launch message may be simple:

> Amanah Quran is free, offline, ad-free, and built without tracking. Quran text sources and verification details are available in the Trust Center.

This message should appear only once and should not block reading unnecessarily.

---

## 31. Final V1 Definition

Amanah Quran V1 is complete when a user can:

1. Install the app.
2. Open it without login.
3. Read the full Quran offline.
4. Choose IndoPak or Uthmani script.
5. Navigate by Surah, Juz, or page.
6. Resume last-read position.
7. Bookmark ayahs and pages.
8. Search Quran text and references offline.
9. Enable Elder Mode.
10. Use light, dark, and sepia themes.
11. Open Trust Center and verify source transparency.
12. Use the app without ads, tracking, monetization, or distraction.

---

## 32. Build Philosophy

Version 1 should be intentionally small, polished, and trustworthy.

Do not add features simply because other Quran apps have them.

The first release should earn user confidence through:

1. Authentic content.
2. Offline reliability.
3. Respectful design.
4. Excellent readability.
5. Transparent verification.
6. No commercial pressure.
7. No privacy compromise.

The product should feel like an Amanah before it feels like an app.
