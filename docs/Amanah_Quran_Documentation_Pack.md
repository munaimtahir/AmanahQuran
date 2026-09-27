# Amanah Quran Documentation Pack

Version 1.0 - Sacred Reader MVP

Prepared for Android implementation.

---


\newpage


# Amanah Quran Documentation Pack

**Project identity:** Amanah-e-Kisa  
**Public app name:** Amanah Quran  
**Version focus:** V1.0 Sacred Reader MVP  
**Platform:** Android  
**Date:** 2026-06-15

This pack converts the project handoff and V1 PRD into implementation-ready documentation for the first Android release of Amanah Quran.

## Pack Contents

| File | Purpose |
|---|---|
| `01_Project_Charter.md` | Identity, mission, scope discipline, product principles |
| `02_PRD_V1_Sacred_Reader.md` | Main product requirements for Version 1 |
| `03_Android_Technical_Architecture.md` | Android stack, architecture, modules, data flow |
| `04_Data_Model_and_Content_Verification.md` | SQLite/Room schema, content import, validation gates |
| `05_UX_Accessibility_and_Reader_Spec.md` | Reader UX, Elder Mode, themes, accessibility details |
| `06_Privacy_No_Tracking_and_Compliance.md` | Privacy model, permissions, Play Store Data Safety stance |
| `07_QA_Release_Checklist.md` | Functional, offline, content, device, privacy, release tests |
| `08_Roadmap_Backlog_and_Milestones.md` | Milestones, P0/P1/P2 backlog, future phases |
| `09_Trust_Center_Template.md` | App-facing Trust Center content and metadata template |
| `10_Developer_Kickoff_Prompt.md` | Copy-paste prompt for Android development agent |
| `checklists/release_checklist.csv` | Release checklist in spreadsheet-friendly CSV format |
| `templates/content_manifest_template.json` | Template for Quran text/content source manifest |
| `templates/validation_report_template.json` | Template for build-time content validation report |
| `Amanah_Quran_Documentation_Pack.docx` | Consolidated formatted document |
| `Amanah_Quran_Documentation_Pack.pdf` | Consolidated PDF export |

## Locked Guardrails

1. No ads.
2. No tracking.
3. No in-app purchases.
4. No monetization pressure.
5. No forced login.
6. No unnecessary permissions.
7. No public release without verified Quranic content.
8. No modification of displayed Quran text.
9. Search-normalized text must remain separate from display text.
10. Trust Center must disclose sources, versions, checksums, and validation status.

## Recommended Immediate Use

1. Share the PRD and architecture docs with the development team.
2. Use the content verification document before selecting any Quran text/font source.
3. Use the QA checklist as the release gate.
4. Use the developer kickoff prompt when starting the first Android repository.


\newpage


# Project Charter
# Amanah Quran - V1 Sacred Reader MVP

## 1. Project Identity

| Item | Decision |
|---|---|
| Project identity | Amanah-e-Kisa |
| Public app name | Amanah Quran |
| Preferred domain | amanahquran.org |
| Project nature | Charity / Sadaqah Jariyah |
| Commercial model | Completely free, no ads, no in-app purchases, no monetization pressure |
| First product release | Android Sacred Reader MVP |

## 2. Mission

Amanah Quran is a verified, offline, ad-free Quran app built as an Amanah: a sacred trust to preserve, present, and make Quranic reading accessible without commercialization, tracking, or distraction.

## 3. Strategic Thesis

The Quran app market is crowded, but trust is still underserved. Amanah Quran should not compete by feature overload. It should compete through verified content, offline reliability, IndoPak friendliness, elder accessibility, respectful design, and transparent sourcing.

## 4. Primary Users

1. Muslims in Pakistan who prefer IndoPak script.
2. Urdu-speaking users globally.
3. Elders who need large text and simple navigation.
4. Daily Quran readers who want no ads or interruptions.
5. Parents and teachers who want a clean Quran app for children.
6. Users who distrust commercial religious apps due to privacy or monetization concerns.

## 5. Public Positioning

Amanah Quran should be presented as universal and inclusive for all Muslims. Amanah-e-Kisa remains the spiritual project identity, while Amanah Quran remains the public-facing app name.

## 6. Non-Negotiable Product Guardrails

1. No ads ever.
2. No ad SDK ever.
3. No analytics SDK in V1.
4. No tracking or advertising ID usage.
5. No forced login.
6. No donation popups during recitation.
7. No unnecessary permissions.
8. No unverified Quran text.
9. No modified Quran display text.
10. No sectarian or political content.
11. No cloud-dependent core reading.
12. No feature that distracts from recitation, understanding, memorization, accessibility, or trust.

## 7. V1 Scope Boundary

Version 1 is only the Sacred Reader MVP. It includes offline Quran reading, IndoPak and Uthmani scripts, navigation, last read, bookmarks, search, Elder Mode, themes, and Trust Center. It excludes audio, translations, tafsir, hifz tools, AI, prayer times, qibla, calendar, accounts, donations, analytics, ads, and notifications.

## 8. Decision Logic

A feature may enter V1 only if the answer to all questions is yes:

1. Does it directly support offline Quran reading, accessibility, or trust?
2. Can it work without login and without internet?
3. Can it be implemented without introducing tracking, monetization, or unnecessary permissions?
4. Can it be tested reliably before release?
5. Does it avoid increasing risk to Quran text authenticity?

## 9. Success Definition

V1 succeeds when a user can install the app, read the full Quran offline, switch between IndoPak and Uthmani scripts, resume reading, bookmark ayahs/pages, search offline, enable Elder Mode, change reading themes, and verify source transparency in Trust Center - all without ads, tracking, login, or monetization.


\newpage


# Product Requirements Document
# Amanah Quran - Version 1 Sacred Reader MVP

## 1. Document Control

| Item | Detail |
|---|---|
| Product | Amanah Quran |
| Project identity | Amanah-e-Kisa |
| Version | V1.0 Sacred Reader MVP |
| Platform | Android |
| Product model | Free, ad-free, no in-app purchases, no monetization |
| Data model | Offline-first local Quran database |
| Primary audience | Pakistan / Urdu-speaking / IndoPak-script users |
| Secondary audience | Global Muslims preferring Uthmani script |
| Release type | Public MVP only after content verification |
| Core promise | Verified offline Quran reading without ads, tracking, login, or distraction |

## 2. Product Vision

Amanah Quran V1 will be a trustworthy, fast, beautiful, offline Android Quran reader focused on one sacred task: allowing users to read the Holy Quran with confidence, clarity, and peace.

The first version must not become a full Islamic super-app. It must establish trust by doing the core reader exceptionally well.

## 3. Version 1 Scope

### 3.1 In Scope

1. Full Quran Arabic text offline.
2. IndoPak script support.
3. Uthmani script support.
4. Script switching.
5. Surah navigation.
6. Juz navigation.
7. Page navigation.
8. Ayah-level reading.
9. Last-read position.
10. Ayah and page bookmarks.
11. Offline search.
12. Elder Mode.
13. Light, dark, sepia, and system themes.
14. Trust Center.
15. Source attribution.
16. No ads, no tracking, no login architecture.

### 3.2 Out of Scope

1. Audio recitation.
2. Urdu translation.
3. English translation.
4. Tafsir.
5. Word-by-word meaning.
6. Hifz tools.
7. AI features.
8. Prayer times.
9. Qibla.
10. Islamic calendar.
11. Hadith database.
12. Social feed.
13. Cloud sync.
14. User accounts.
15. Donations inside the app.
16. Analytics SDK.
17. Ads SDK.
18. Push notifications.

## 4. Core Feature Requirements

## Feature 1 - Offline Quran Reading

### Description

The app must provide the complete Arabic Quran offline after installation.

### Functional Requirements

1. Full Quran must be available without internet.
2. User can read by Surah, Juz, and page.
3. Reader supports vertical scrolling.
4. Reader supports ayah-level display.
5. Ayah numbers are visually clear and not confused with Quran text.
6. Reader supports IndoPak and Uthmani script switching.
7. Reader preserves reading position across app backgrounding.
8. Reader restores last-read position after app restart.
9. No popups appear during recitation.
10. No login is required.

### Acceptance Criteria

1. With internet disabled, user can open any Surah.
2. With internet disabled, user can open any Juz.
3. With internet disabled, user can open any page.
4. Arabic text renders correctly in both scripts.
5. The app never displays search-normalized text as Quran text.
6. Reader remains usable on small and low-end phones.

## Feature 2 - IndoPak and Uthmani Script Support

### Functional Requirements

1. User can switch script from Reader and Settings.
2. Script selection persists locally.
3. Current script applies to Reader, Search result previews, and Bookmarks.
4. Script switching never changes bookmark identity.
5. Bookmark identity is based on canonical ayah key, not visual text.
6. Both script packs have independent Trust Center metadata.

### Acceptance Criteria

1. User switches IndoPak to Uthmani without restarting.
2. User switches Uthmani to IndoPak without losing reading position.
3. Bookmarked ayahs remain bookmarked after script switch.
4. Trust Center shows source/checksum records for both scripts.

## Feature 3 - Navigation

### Required Navigation Types

1. Surah navigation.
2. Juz navigation.
3. Page navigation.

### Surah List Fields

1. Surah number.
2. Arabic Surah name.
3. Readable name.
4. Ayah count.
5. Quick open action.

### Juz List Fields

1. Juz number.
2. Starting Surah and ayah.
3. Ending Surah and ayah, if available.
4. Quick open action.

### Page List Fields

1. Page number.
2. Starting Surah and ayah.
3. Ending Surah and ayah, if available.
4. Quick open action.

### Acceptance Criteria

1. User can reach any Surah within three taps from Home.
2. User can reach any Juz within three taps from Home.
3. User can reach any page within three taps from Home.
4. Navigation works offline and remains readable in Elder Mode.

## Feature 4 - Last-Read Position

### Functional Requirements

1. Save last-read when user opens an ayah, scrolls meaningfully, backgrounds the app, or exits the reader.
2. Store Surah number, ayah number, ayah key, Juz number, page number, script, optional scroll offset, and timestamp.
3. Home screen shows Continue Reading.
4. Reader resumes near the correct ayah.
5. If scroll offset cannot be restored safely, app falls back to ayah-level resume.

### Acceptance Criteria

1. Closing and reopening the app preserves last-read position.
2. Script switching preserves the ayah identity.
3. Last-read works offline.

## Feature 5 - Bookmarks

### Functional Requirements

1. User can bookmark an ayah.
2. User can remove an ayah bookmark.
3. User can bookmark a page.
4. User can remove a page bookmark.
5. Bookmarks are stored locally.
6. Bookmarks persist after restart.
7. Bookmarks do not depend on current script.
8. Bookmark list shows Surah name, ayah/page reference, short preview, and date added.
9. User can open bookmark directly in Reader.

### Acceptance Criteria

1. User bookmarks Ayah 2:255 and can find it in Bookmarks.
2. User switches script and bookmark still opens Ayah 2:255.
3. Removing bookmark updates the list immediately.
4. Bookmarks work in airplane mode.

## Feature 6 - Search

### Required Search Types

1. Surah name search.
2. Surah number search.
3. Ayah reference search, such as 2:255.
4. Juz number search.
5. Page number search.
6. Arabic text search.
7. Normalized Arabic search.

### Arabic Search Rules

1. Display Quran text must never be changed for search.
2. Search may use a separate normalized text column.
3. Normalization may remove tatweel and optionally ignore harakat.
4. Normalized text must never be displayed as Quran text.

### Acceptance Criteria

1. Search works offline.
2. Search opens ayah reference correctly.
3. Arabic search returns matching ayahs.
4. Search results display the selected script.
5. Search result performance remains acceptable on low-end phones.

## Feature 7 - Elder Mode

### Functional Requirements

1. Arabic font size increases.
2. UI text size increases.
3. Buttons and touch targets become larger.
4. Reader toolbar becomes simpler.
5. Navigation cards become larger.
6. Contrast improves.
7. Search input remains large and easy to tap.
8. Bookmark action remains clear.

### Acceptance Criteria

1. Elder Mode can be enabled and disabled in Settings.
2. Elder Mode persists after restart.
3. Reader remains stable at large font sizes.
4. Important actions do not become hidden on small phones.

## Feature 8 - Themes

### Required Themes

1. Light.
2. Dark.
3. Sepia / Paper.
4. System default.

### Acceptance Criteria

1. Theme changes immediately.
2. Theme persists after restart.
3. Quran text remains readable in every theme.
4. Elder Mode works with every theme.

## Feature 9 - Trust Center

### Required Sections

1. Quran Text Sources.
2. No Modification Statement.
3. Verification Status.
4. Privacy Pledge.
5. App Integrity.

### Acceptance Criteria

1. Trust Center is accessible from Home and Settings.
2. User can view source details for IndoPak and Uthmani script.
3. User can view checksums and validation status.
4. User can view no-modification statement.
5. User can view privacy pledge.
6. Trust Center works offline.

## 5. Release Definition

Amanah Quran V1 is complete when a user can install the app, open it without login, read the full Quran offline, choose IndoPak or Uthmani script, navigate by Surah/Juz/page, resume last read, bookmark ayahs and pages, search offline, enable Elder Mode, use themes, open Trust Center, and use the app without ads, tracking, monetization, or distraction.


\newpage


# Android Technical Architecture
# Amanah Quran - V1 Sacred Reader MVP

## 1. Architecture Goals

1. Offline-first Quran reading.
2. Fast startup and smooth reader scrolling.
3. Reliable Arabic rendering for IndoPak and Uthmani scripts.
4. No network dependency for core V1.
5. No analytics, ads, account, or monetization SDKs.
6. Simple enough for a small development team to maintain.
7. Structured enough to expand later into translation, audio, hifz, and optional backup.

## 2. Recommended Stack

| Layer | Decision |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose |
| Local database | Room SQLite with prepackaged database |
| Settings | Jetpack DataStore Preferences |
| Architecture | MVVM or MVI-lite |
| Navigation | Jetpack Navigation Compose |
| Dependency injection | Hilt or lightweight manual DI |
| Background jobs | Not required in V1 |
| Network | Not required in V1 core |
| Audio | Not included in V1 |
| Analytics | Not allowed in V1 |
| Ads | Never allowed |

## 3. App Modules

For V1, use a single Android app module to reduce complexity. Keep code organized internally by package boundaries.

Suggested future modularization only after the reader is stable:

1. `core-database`
2. `core-ui`
3. `feature-reader`
4. `feature-search`
5. `feature-trust`

## 4. Package Structure

```text
app/
  core/
    database/
    datastore/
    model/
    navigation/
    theme/
    typography/
    utils/
  feature/
    home/
    reader/
    navigation/
    search/
    bookmarks/
    settings/
    trust/
  content/
    manifest/
    validation/
```

## 5. Data Flow

```text
Compose UI
  -> ViewModel
    -> Repository
      -> Room DAO / DataStore
        -> Local SQLite database / preferences
```

No V1 screen should require a network call.

## 6. Core Screens

| Screen | Purpose |
|---|---|
| Home | Continue Reading, Quran navigation, Search, Bookmarks, Trust Center |
| Reader | Offline Quran reading in selected script |
| Quran Navigation | Surah, Juz, and Page lists |
| Search | Offline reference and Arabic text search |
| Bookmarks | Local ayah/page bookmarks |
| Settings | Script, theme, font size, Elder Mode |
| Trust Center | Source, checksums, validation, privacy pledge |

## 7. ViewModels

| ViewModel | Responsibility |
|---|---|
| `HomeViewModel` | Last-read summary, shortcuts, current script/theme state |
| `ReaderViewModel` | Load ayahs, script switching, bookmark state, last-read updates |
| `QuranNavigationViewModel` | Surah/Juz/page list state |
| `SearchViewModel` | Query parsing, search results, reference lookup |
| `BookmarksViewModel` | Bookmark list, add/remove/open actions |
| `SettingsViewModel` | Script, theme, font size, Elder Mode state |
| `TrustCenterViewModel` | Source metadata, validation status, privacy pledge content |

## 8. Repositories

| Repository | Data Source |
|---|---|
| `QuranRepository` | Room: surahs, ayahs, quran_texts, page/juz metadata |
| `SearchRepository` | Room: search_index and metadata |
| `BookmarkRepository` | Room: bookmarks |
| `LastReadRepository` | DataStore or Room single-row table |
| `SettingsRepository` | DataStore |
| `TrustRepository` | Room/content manifest |

## 9. Offline-First Rules

1. Reader must not depend on internet.
2. Search must not depend on internet.
3. Bookmarks must not depend on internet.
4. Last-read must not depend on internet.
5. Trust Center must not depend on internet.
6. External source links may open through browser intent but are not required for core use.
7. No network client should be initialized on app launch in V1.

## 10. Reader Rendering Strategy

1. Load only the current Surah/Juz/page range, not the full Quran.
2. Use Compose lazy lists for long ranges.
3. Keep display Quran text immutable.
4. Avoid expensive runtime Arabic normalization during rendering.
5. Store normalized search text separately.
6. Test Arabic shaping, line wrapping, ayah markers, and diacritics on target OEMs.

## 11. Script Switching Strategy

1. Use canonical ayah identity: `surah_number + ayah_number` or `ayah_key`.
2. Reader state stores ayah identity, not text position alone.
3. Switching script reloads display text while preserving ayah identity.
4. Bookmarks reference ayah/page IDs, not text strings.
5. Trust Center stores separate metadata per script.

## 12. Settings Strategy

Use DataStore for:

1. Selected script.
2. Theme.
3. Arabic font scale.
4. Elder Mode enabled/disabled.
5. First-launch message seen.

Use Room for:

1. Bookmarks.
2. Quran content.
3. Search index.
4. Trust Center source metadata.
5. Content validation status.

## 13. Build-Time Content Gate

The build pipeline should fail release builds if:

1. Quran database is missing.
2. Content manifest is missing.
3. Surah count is not 114.
4. Ayah count is not 6236.
5. Script text is missing for IndoPak or Uthmani.
6. Any required source/license/checksum field is empty.
7. Trust Center metadata is incomplete.
8. Validation report contains a failed critical check.

## 14. Dependency Guardrails

Do not include:

1. Ad SDKs.
2. Analytics SDKs.
3. Social SDKs.
4. Account/login SDKs.
5. Push notification SDKs.
6. Unnecessary crash-reporting SDKs in V1.

Allowed with caution:

1. AndroidX.
2. Compose.
3. Room.
4. DataStore.
5. Navigation Compose.
6. Hilt, if the team is comfortable.

## 15. Performance Targets

| Area | Target |
|---|---|
| Cold start | Under 2 seconds on mid-range Android phone |
| Reader load | Under 500 ms after database warmup |
| Search response | Under 1 second for common queries |
| Offline app use | 100% of V1 features except external links |
| Runtime permissions | None for core V1 |

## 16. Target Device Testing

Test on:

1. Samsung mid-range.
2. Vivo.
3. Oppo.
4. Xiaomi/Redmi.
5. Infinix.
6. Tecno.
7. Small-screen phone.
8. Large-screen phone.

## 17. Future-Proofing Without V1 Bloat

Design extension points for later phases but do not implement them in V1:

1. Translation packs.
2. Tafsir packs.
3. Audio packs.
4. Hifz/revision status.
5. Local export/import.
6. Optional encrypted cloud sync.

Tables for future features may be planned separately, but do not expose UI for future features in V1.


\newpage


# Data Model and Content Verification
# Amanah Quran - V1 Sacred Reader MVP

## 1. Core Principle

Quran display text is sacred content and must be treated as immutable after verified import. Search optimization, normalization, indexing, and highlighting must never modify or replace the displayed Quran text.

## 2. Data Strategy

Use a prepackaged Room SQLite database bundled with the app. The database should be produced through a controlled import pipeline and frozen for release after verification.

## 3. Required Tables

## 3.1 `surahs`

| Field | Type | Required | Notes |
|---|---|---:|---|
| `id` | Integer | Yes | 1 to 114 |
| `name_arabic` | Text | Yes | Verified Arabic name |
| `name_simple` | Text | Yes | Search/display name |
| `name_urdu` | Text | No | Optional V1 metadata |
| `revelation_type` | Text | No | Only if verified |
| `ayah_count` | Integer | Yes | Count per Surah |

## 3.2 `ayahs`

| Field | Type | Required | Notes |
|---|---|---:|---|
| `id` | Integer | Yes | Internal primary key |
| `ayah_key` | Text | Yes | Format: `surah:ayah`, e.g. `2:255` |
| `surah_number` | Integer | Yes | 1 to 114 |
| `ayah_number` | Integer | Yes | Ayah number within Surah |
| `juz_number` | Integer | Yes | 1 to 30 |
| `page_number` | Integer | Yes | Based on selected metadata source |
| `hizb_number` | Integer | No | Optional metadata |
| `ruku_number` | Integer | No | Optional metadata |
| `sajdah_type` | Text | No | Optional marker |

## 3.3 `quran_texts`

| Field | Type | Required | Notes |
|---|---|---:|---|
| `id` | Integer | Yes | Primary key |
| `ayah_key` | Text | Yes | Links to `ayahs.ayah_key` |
| `script_type` | Text | Yes | `INDOPAK` or `UTHMANI` |
| `display_text` | Text | Yes | Immutable verified text |
| `source_id` | Integer | Yes | Links to `content_sources.id` |
| `checksum` | Text | No | Optional per ayah; pack checksum required |

## 3.4 `search_index`

| Field | Type | Required | Notes |
|---|---|---:|---|
| `id` | Integer | Yes | Primary key |
| `ayah_key` | Text | Yes | Links to ayah |
| `normalized_arabic` | Text | Yes | Search only |
| `normalized_uthmani` | Text | No | Optional separate script search |
| `normalized_indopak` | Text | No | Optional separate script search |
| `surah_search_terms` | Text | No | For Surah lookup |

## 3.5 `bookmarks`

| Field | Type | Required | Notes |
|---|---|---:|---|
| `id` | Integer | Yes | Primary key |
| `bookmark_type` | Text | Yes | `AYAH` or `PAGE` |
| `ayah_key` | Text | Conditional | Required for ayah bookmark |
| `page_number` | Integer | Conditional | Required for page bookmark |
| `created_at` | Long | Yes | Timestamp |
| `updated_at` | Long | Yes | Timestamp |

## 3.6 `last_read`

| Field | Type | Required | Notes |
|---|---|---:|---|
| `id` | Integer | Yes | Single row or DataStore equivalent |
| `ayah_key` | Text | Yes | Canonical last ayah |
| `surah_number` | Integer | Yes | Redundant but useful |
| `ayah_number` | Integer | Yes | Redundant but useful |
| `juz_number` | Integer | Yes | Redundant but useful |
| `page_number` | Integer | Yes | Redundant but useful |
| `script_type` | Text | Yes | Current selected script |
| `scroll_offset` | Integer | No | Optional |
| `updated_at` | Long | Yes | Timestamp |

## 3.7 `content_sources`

| Field | Type | Required | Notes |
|---|---|---:|---|
| `id` | Integer | Yes | Primary key |
| `content_type` | Text | Yes | Quran text, metadata, font, etc. |
| `script_type` | Text | Conditional | Required for script text |
| `source_name` | Text | Yes | Public source name |
| `source_url` | Text | Yes | Public source URL |
| `license` | Text | Yes | License/reuse status |
| `version` | Text | Yes | Source version |
| `checksum` | Text | Yes | Pack checksum |
| `import_date` | Text | Yes | Date imported |
| `validation_status` | Text | Yes | Pass/fail/pending |
| `reviewer_status` | Text | Yes | Manual review state |

## 3.8 `content_validation`

| Field | Type | Required | Notes |
|---|---|---:|---|
| `id` | Integer | Yes | Primary key |
| `validation_name` | Text | Yes | e.g. `surah_count` |
| `expected_value` | Text | Yes | Expected value |
| `actual_value` | Text | Yes | Observed value |
| `passed` | Boolean | Yes | Result |
| `severity` | Text | Yes | Critical/warning/info |
| `checked_at` | Long | Yes | Timestamp |

## 4. Content Import Pipeline

1. Select source after license review.
2. Download source outside the app build.
3. Record source metadata.
4. Generate source file checksum.
5. Import into staging database.
6. Validate Surah count = 114.
7. Validate total ayah count = 6236.
8. Validate ayah keys are complete.
9. Validate no duplicate ayah keys.
10. Validate no empty display text.
11. Validate script-specific text exists for IndoPak and Uthmani.
12. Generate normalized search text separately.
13. Generate final Room SQLite database.
14. Generate content manifest JSON.
15. Run checksum verification.
16. Submit for manual content review.
17. Freeze database for release.

## 5. Required Validation Rules

| Rule | Expected | Severity |
|---|---|---|
| Surah count | 114 | Critical |
| Ayah count | 6236 | Critical |
| Duplicate ayah keys | 0 | Critical |
| Missing IndoPak text | 0 | Critical |
| Missing Uthmani text | 0 | Critical |
| Missing page mapping | 0 | Critical |
| Missing Juz mapping | 0 | Critical |
| Missing source metadata | 0 | Critical |
| Missing license field | 0 | Critical |
| Missing checksum | 0 | Critical |
| Search index row count | Must match ayah count | Critical |

## 6. Build Gate

Release build must fail if any critical validation fails. The app should never ship with placeholder source data, missing checksums, missing license fields, incomplete Trust Center metadata, or unverified Quran display text.

## 7. Search Normalization Rules

Allowed only for search index:

1. Remove tatweel.
2. Normalize common Arabic letter variants.
3. Optionally remove/ignore harakat for search.
4. Optionally create FTS-compatible search text.

Forbidden:

1. Displaying normalized text as Quran text.
2. Replacing verified display text with normalized text.
3. Modifying Quran display text to improve search highlighting.
4. Saving user-visible ayah text from search index.

## 8. Content Manifest Requirements

Each content pack must include:

1. Pack ID.
2. Content type.
3. Script type, if applicable.
4. Source name.
5. Source URL.
6. License.
7. Source version.
8. Import date.
9. File checksum.
10. Ayah count.
11. Surah count.
12. Validation status.
13. Manual review status.
14. Reviewer name/role, if available.
15. Notes.

## 9. Manual Review Workflow

Before public release:

1. Freeze imported text.
2. Generate validation report.
3. Export representative samples from every Juz.
4. Review beginning, middle, and end of selected Surahs.
5. Review special cases: Sajdah ayahs, Bismillah handling, long ayahs, page boundaries.
6. Document reviewer status in `content_sources`.
7. Do not release if review status is pending.

## 10. Recommended Source Review Checklist

Before using any Quran text/font/source:

1. Is the source reputable?
2. Is the license compatible with free public app distribution?
3. Is redistribution inside APK allowed?
4. Is modification prohibited, restricted, or allowed only for technical formatting?
5. Does the source provide versioning?
6. Can checksums be recorded?
7. Are IndoPak and Uthmani scripts separate assets?
8. Are fonts licensed for embedding in Android apps?
9. Are page mappings tied to a known mushaf standard?
10. Can source attribution be shown inside Trust Center?


\newpage


# UX, Accessibility, and Reader Specification
# Amanah Quran - V1 Sacred Reader MVP

## 1. UX Vision

Amanah Quran should feel calm, sacred, trustworthy, and easy to use. The reading experience must be free from visual noise, popups, ads, donation prompts, social mechanics, and unnecessary settings.

## 2. Information Architecture

Primary navigation:

1. Read.
2. Search.
3. Bookmarks.
4. Settings.
5. Trust Center.

Elder Mode navigation:

1. Continue Reading.
2. Open Quran.
3. Bookmarks.
4. Search.
5. Settings.

## 3. Home Screen Requirements

Required cards/actions:

1. Continue Reading.
2. Open Quran.
3. Search.
4. Bookmarks.
5. Trust Center.
6. Current script indicator.
7. Current theme-aware layout.

Acceptance criteria:

1. Continue Reading opens previous location in one tap.
2. Quran navigation opens in one tap.
3. Trust Center is accessible within two taps.
4. No ad-like placeholder, promotional banner, or donation card is shown.
5. Elder Mode increases readability and button size.

## 4. Reader Screen Requirements

Reader top bar:

1. Back button.
2. Current Surah/Juz/page label.
3. Script switch.
4. Bookmark action.
5. Reader settings action.

Reader content:

1. Arabic display text.
2. Ayah number markers.
3. Comfortable line height.
4. Optional mini progress indicator.
5. No unrelated cards.
6. No popups during reading.
7. No bottom area reserved for ads.

## 5. Arabic Typography Requirements

1. Font must support Arabic shaping correctly.
2. Diacritics must render clearly.
3. IndoPak script must be readable and familiar for Pakistan users.
4. Uthmani script must remain visually authentic.
5. Line height must prevent collision of diacritics.
6. Font size must be adjustable.
7. Elder Mode must use larger defaults.
8. Avoid letter spacing that damages Arabic shaping.
9. Avoid decorative backgrounds behind Quran text.
10. Test on common OEMs.

## 6. Theme Requirements

Required themes:

1. System.
2. Light.
3. Dark.
4. Sepia / Paper.

Theme principles:

1. Reading comfort is more important than decorative design.
2. Dark mode should not use harsh pure-white text if it causes fatigue.
3. Sepia should support long recitation sessions.
4. Ayah numbers and controls must maintain contrast.
5. Elder Mode must work with every theme.

## 7. Elder Mode Specification

When enabled:

1. Home cards become larger.
2. Reader font becomes larger.
3. Line spacing increases.
4. Tap targets increase.
5. Navigation labels become more explicit.
6. Icon-only actions should gain labels where space allows.
7. Settings screen should simplify choices.
8. Search input becomes larger.
9. Bookmark action remains obvious.
10. Trust Center text remains readable.

Recommended Elder Mode defaults:

| Element | Elder Mode Behavior |
|---|---|
| Arabic font size | Larger default |
| UI text | Increased size |
| Buttons | Large, clear labels |
| Touch targets | Minimum 48dp, preferably larger |
| Reader controls | Fewer visible controls |
| Search field | Tall field, clear placeholder |
| Theme | High readability with system default respected |

## 8. Search UX

Search screen should support:

1. Simple text box.
2. Reference lookup examples.
3. Results grouped by type if useful: Surah, Ayah, Juz, Page.
4. Arabic ayah preview in selected script.
5. Tap result to open reader.

Avoid:

1. Complex filters in V1.
2. Cloud search.
3. Search suggestions requiring internet.
4. Highlighting that distorts Quran text.

## 9. Bookmark UX

Bookmarks screen should show:

1. Bookmark type: Ayah or Page.
2. Surah/page reference.
3. Short preview in selected script.
4. Date added.
5. Open action.
6. Remove action.

Empty state:

> No bookmarks yet. Open any ayah or page and tap Bookmark to save your place.

## 10. Trust Center UX

Trust Center should feel transparent and reassuring, not technical overload. Show summary first, then details.

Recommended structure:

1. Trust Summary.
2. Quran Text Sources.
3. Verification Status.
4. Privacy Pledge.
5. App and Database Integrity.
6. Detailed metadata.

## 11. First Launch

Do not use long onboarding. First launch may show a simple non-blocking statement:

> Amanah Quran is free, offline, ad-free, and built without tracking. Quran text sources and verification details are available in the Trust Center.

Do not ask for:

1. Login.
2. Permissions.
3. Donations.
4. Notifications.
5. Personal information.

## 12. Accessibility Requirements

1. Support Android font scaling where safe.
2. Provide content descriptions for major actions.
3. Use text labels where icons may be unclear.
4. Maintain contrast in all themes.
5. Keep touch targets accessible.
6. Support predictable back navigation.
7. Do not rely only on color for state.
8. Avoid motion-heavy transitions.
9. Test TalkBack for basic screen navigation.
10. Avoid tiny Arabic controls or low-contrast ayah markers.

## 13. Small-Screen Requirements

On small phones:

1. Reader controls must not overlap.
2. Font size controls must remain usable.
3. Elder Mode must not hide critical actions.
4. Long Surah names should truncate gracefully.
5. Bookmark and script switch actions should remain reachable.

## 14. UX Anti-Patterns to Avoid

1. Home screen overloaded with future features.
2. Donation prompt on launch.
3. Popup during recitation.
4. Social sharing feed.
5. Streaks or badges that trivialize Quran reading.
6. Ads-like empty spaces.
7. Too many first-run choices.
8. Mixing Study/Hifz/Masjid features into V1 navigation.


\newpage


# Privacy, No-Tracking, and Compliance
# Amanah Quran - V1 Sacred Reader MVP

## 1. Privacy Position

Amanah Quran V1 should be designed so the user can read the Quran offline without creating an account, granting dangerous permissions, being tracked, or sharing any personal data.

## 2. Product Privacy Commitments

1. No ads.
2. No ad SDK.
3. No tracking.
4. No analytics SDK in V1.
5. No advertising ID usage.
6. No forced login.
7. No account creation.
8. No personal data collection.
9. No cloud sync in V1.
10. No donation prompts during reading.
11. No unnecessary notifications.
12. No selling or sharing user data.

## 3. Permissions Policy

V1 should require no dangerous runtime permissions.

Do not request:

1. Location.
2. Contacts.
3. Camera.
4. Microphone.
5. Phone.
6. SMS.
7. Storage access.
8. Notifications.
9. Nearby devices.
10. Advertising ID.

## 4. Local Data Stored

The app may store locally:

1. Last-read position.
2. Bookmarks.
3. Theme preference.
4. Script preference.
5. Arabic font size preference.
6. Elder Mode preference.
7. First-launch message seen state.

This data must remain on the device in V1.

## 5. Network Policy

Core V1 features must work without network:

1. Reader.
2. Navigation.
3. Search.
4. Bookmarks.
5. Last-read.
6. Settings.
7. Trust Center.

External links in Trust Center may open in the device browser, but this must not be required to use the app.

## 6. Play Store Data Safety Target

V1 should be engineered so the Play Store Data Safety form can honestly declare:

1. No data collected.
2. No data shared.
3. No location collected.
4. No personal information collected.
5. No financial information collected.
6. No contacts collected.
7. No app activity analytics collected.
8. No advertising data collected.

## 7. SDK Audit Checklist

Before release, confirm the APK/AAB does not contain:

1. Google AdMob.
2. Meta/Facebook SDK.
3. Firebase Analytics.
4. Any third-party analytics SDK.
5. Any attribution SDK.
6. Any social login SDK.
7. Any push notification SDK.
8. Any tracking or fingerprinting library.

Allowed dependencies should be limited to Android app infrastructure such as Compose, Room, DataStore, Navigation, and other non-tracking AndroidX components.

## 8. Privacy Policy Draft Points

The public privacy policy should state:

1. Amanah Quran does not show ads.
2. Amanah Quran does not collect personal data in V1.
3. Amanah Quran does not require login.
4. Amanah Quran does not use analytics or advertising identifiers in V1.
5. Bookmarks and reading position are stored locally on the user's device.
6. Offline Quran reading works without internet.
7. External source links, if opened, are handled by the user's browser.

## 9. Release Gate

Do not release if:

1. Any analytics SDK is present.
2. Any ad SDK is present.
3. Any unnecessary permission is present.
4. Any core feature requires internet.
5. Any user data is transmitted from the app.
6. Any privacy statement cannot be truthfully supported by the build.


\newpage


# QA and Release Checklist
# Amanah Quran - V1 Sacred Reader MVP

## 1. Release Philosophy

Amanah Quran should not be released because the code compiles. It should be released only when the Quran content, offline behavior, reader stability, privacy model, and Trust Center are verified.

## 2. Functional QA

| ID | Test | Expected Result | Status |
|---|---|---|---|
| F-001 | First app launch | App opens without login, permissions, ads, or donation prompt | Pending |
| F-002 | Open Surah Al-Fatihah | Surah opens offline in selected script | Pending |
| F-003 | Open Surah Al-Baqarah | Long Surah scrolls smoothly | Pending |
| F-004 | Open Surah Yaseen | Surah opens correctly | Pending |
| F-005 | Open Juz 30 | Juz opens correctly | Pending |
| F-006 | Open page 1 | Page opens correctly | Pending |
| F-007 | Switch IndoPak to Uthmani | Text changes, position remains stable | Pending |
| F-008 | Switch Uthmani to IndoPak | Text changes, position remains stable | Pending |
| F-009 | Bookmark ayah | Bookmark appears in list | Pending |
| F-010 | Remove bookmark | Bookmark disappears | Pending |
| F-011 | Bookmark page | Page bookmark appears | Pending |
| F-012 | Resume last-read | Opens previous location | Pending |
| F-013 | Search Surah name | Correct Surah appears | Pending |
| F-014 | Search ayah reference | Correct ayah opens | Pending |
| F-015 | Search Arabic text | Matching ayahs appear | Pending |
| F-016 | Enable Elder Mode | Text/buttons become larger | Pending |
| F-017 | Disable Elder Mode | UI returns to standard mode | Pending |
| F-018 | Change theme | Theme changes and persists | Pending |
| F-019 | Open Trust Center | Source/verification/privacy data visible | Pending |
| F-020 | Airplane mode full use | Core V1 features work offline | Pending |

## 3. Content QA

| ID | Test | Expected Result | Severity | Status |
|---|---|---|---|---|
| C-001 | Surah count | 114 | Critical | Pending |
| C-002 | Ayah count | 6236 | Critical | Pending |
| C-003 | Duplicate ayah keys | 0 | Critical | Pending |
| C-004 | Missing IndoPak text | 0 | Critical | Pending |
| C-005 | Missing Uthmani text | 0 | Critical | Pending |
| C-006 | Missing page mapping | 0 | Critical | Pending |
| C-007 | Missing Juz mapping | 0 | Critical | Pending |
| C-008 | Search index row count | Matches ayah count | Critical | Pending |
| C-009 | Content manifest | Present and complete | Critical | Pending |
| C-010 | Checksums | Match source manifest | Critical | Pending |
| C-011 | Trust Center metadata | Complete for both scripts | Critical | Pending |
| C-012 | Manual review | Completed before public release | Critical | Pending |

## 4. Privacy QA

| ID | Test | Expected Result | Status |
|---|---|---|---|
| P-001 | Runtime permissions | No dangerous permissions requested | Pending |
| P-002 | Ad SDK scan | No ad SDK present | Pending |
| P-003 | Analytics SDK scan | No analytics SDK present | Pending |
| P-004 | Advertising ID usage | Not used | Pending |
| P-005 | Login/account flow | Not present | Pending |
| P-006 | Network dependency | Core V1 features work without internet | Pending |
| P-007 | Data transmission | No user data transmitted | Pending |
| P-008 | Play Store Data Safety | Can declare no data collected/shared | Pending |

## 5. Device QA

Test minimum:

1. Samsung mid-range phone.
2. Vivo phone.
3. Oppo phone.
4. Xiaomi/Redmi phone.
5. Infinix phone.
6. Tecno phone.
7. Small-screen Android phone.
8. Large-screen Android phone.

For each device:

1. Install fresh build.
2. Launch app.
3. Open reader.
4. Switch scripts.
5. Search Arabic text.
6. Bookmark and remove bookmark.
7. Enable Elder Mode.
8. Test dark and sepia themes.
9. Kill and reopen app to test last-read.
10. Use app in airplane mode.

## 6. Accessibility QA

1. Elder Mode usable on small screens.
2. Tap targets large enough.
3. Text readable in all themes.
4. No icon-only critical actions without labels where clarity is needed.
5. TalkBack can navigate core screens.
6. Android font scaling does not break critical screens.
7. Back navigation predictable.
8. No essential state depends only on color.

## 7. Release Blockers

Do not release if any of the following are present:

1. Quran content validation failure.
2. Missing or unverified script pack.
3. Missing Trust Center metadata.
4. Unclear source license.
5. Any ad SDK.
6. Any analytics SDK.
7. Any forced login.
8. Any unnecessary permission.
9. Any crash in reader flow.
10. Search displaying normalized Quran text as display text.
11. Bookmarks or last-read failing offline.
12. Elder Mode hiding critical controls.

## 8. Final Release Sign-Off

| Area | Owner | Status | Notes |
|---|---|---|---|
| Product scope | Product lead | Pending |  |
| Quran text verification | Content reviewer | Pending |  |
| Android functionality | Developer | Pending |  |
| Privacy review | Product/technical reviewer | Pending |  |
| Device QA | QA tester | Pending |  |
| Play Store readiness | Release owner | Pending |  |


\newpage


# Roadmap, Backlog, and Milestones
# Amanah Quran - V1 Sacred Reader MVP

## 1. Roadmap Principle

Build small, polished, trustworthy releases. Do not add features simply because other Quran apps have them. Every feature must serve recitation, understanding, memorization, accessibility, or trust.

## 2. Phase 1 - Sacred Reader MVP

Goal: Build the most trustworthy and smooth offline Quran reader.

Scope:

1. Offline Quran text.
2. IndoPak and Uthmani scripts.
3. Surah/Juz/page navigation.
4. Last-read.
5. Bookmarks.
6. Search.
7. Elder Mode.
8. Themes.
9. Trust Center.
10. Source attribution.
11. No ads/no tracking/no login.

## 3. V1 Milestones

### Milestone 1 - Project Foundation

Deliverables:

1. Android project setup.
2. Kotlin + Compose setup.
3. Theme system.
4. Navigation shell.
5. DataStore settings.
6. Room database integration.

Exit criteria:

1. App launches.
2. Home, Reader, Search, Bookmarks, Settings, and Trust Center routes exist.
3. No internet, ad, or analytics dependency included.

### Milestone 2 - Quran Database and Import

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

### Milestone 3 - Reader MVP

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

### Milestone 4 - Bookmarks

Deliverables:

1. Ayah bookmark.
2. Page bookmark.
3. Bookmarks list.
4. Bookmark open/remove actions.

Exit criteria:

1. Bookmarks persist locally.
2. Bookmarks work offline.
3. Bookmarks survive script switching.

### Milestone 5 - Search

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

### Milestone 6 - Elder Mode and Themes

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

### Milestone 7 - Trust Center

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

### Milestone 8 - Release Hardening

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

## 4. Developer Backlog

## P0 - Must Build

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

## P1 - Important Polish

1. Smooth reader scrolling.
2. Better page progress indicator.
3. Search result highlighting if safe.
4. Quick Surah chips for common Surahs.
5. Better Arabic typography tuning.
6. Better empty states.
7. Better first-launch defaults.
8. Better small-screen layout.

## P2 - Not for V1 Unless Time Allows

1. Urdu interface.
2. Share ayah text.
3. Share ayah image.
4. Export bookmarks.
5. Import bookmarks.
6. Open-source license screen.
7. Advanced accessibility testing.

## 5. Future Phases

### Phase 2 - Understanding Layer

1. Urdu UI.
2. Urdu translation.
3. English translation.
4. Translation toggle.
5. Tafsir packs only after license review.
6. Word-by-word meaning.
7. Notes.
8. Tags.
9. Reading plans.
10. Export/import notes and bookmarks.

### Phase 3 - Hifz and Revision Layer

1. Ayah repeat.
2. Range repeat.
3. Page repeat.
4. Hide/reveal ayah.
5. Memorization status.
6. Weak ayah list.
7. Mistake log.
8. Sabaq/sabqi/manzil tracking.
9. Spaced repetition scheduler.
10. Teacher checklist mode.

### Phase 4 - Masjid and Family Layer

1. Masjid Mode.
2. Silent Mode.
3. Brightness lock.
4. Keep screen awake.
5. Child-safe mode.
6. Family device usability.
7. Large button navigation.

### Phase 5 - Advanced Study and Optional AI

Only after core app stability:

1. Offline voice search.
2. On-device recitation matching.
3. Optional AI hifz assistant.
4. Tajweed feedback.
5. Optional encrypted cloud backup.

AI must not be part of V1.


\newpage


# Trust Center Template
# Amanah Quran - V1 Sacred Reader MVP

## 1. Trust Center Purpose

Trust Center gives users transparent access to Quran text sources, verification status, checksums, app integrity information, and the privacy pledge. It should be available offline and should not require login.

## 2. Trust Summary

Suggested app-facing summary:

> Amanah Quran is built to provide verified offline Quran reading without ads, tracking, login, or monetization. Quran text sources, versions, checksums, and validation details are shown here for transparency.

## 3. No Modification Statement

Required app-facing statement:

> Amanah Quran displays Quranic text exactly as imported from verified source data. Search normalization is stored separately and is never used as display Quran text.

## 4. Privacy Pledge

Required app-facing bullets:

1. No ads.
2. No tracking.
3. No analytics SDK in Version 1.
4. No advertising ID use.
5. No forced login.
6. No selling or sharing user data.
7. Offline reading works without internet.
8. Bookmarks and last-read position remain stored locally on your device.

## 5. Quran Text Source Card

Use one card per script pack.

| Field | Value |
|---|---|
| Script | IndoPak / Uthmani |
| Source name | To be completed |
| Source URL | To be completed |
| Source version | To be completed |
| License | To be completed |
| Import date | To be completed |
| Pack checksum | To be completed |
| Ayah count | 6236 |
| Surah count | 114 |
| Validation status | Pending / Passed / Failed |
| Manual review status | Pending / Reviewed / Approved |

## 6. Verification Status Card

| Check | Expected | Actual | Status |
|---|---|---|---|
| Surah count | 114 | To be completed | Pending |
| Ayah count | 6236 | To be completed | Pending |
| Duplicate ayah keys | 0 | To be completed | Pending |
| Missing IndoPak text | 0 | To be completed | Pending |
| Missing Uthmani text | 0 | To be completed | Pending |
| Missing page mapping | 0 | To be completed | Pending |
| Missing Juz mapping | 0 | To be completed | Pending |
| Search index count | 6236 | To be completed | Pending |
| Checksum verification | Match | To be completed | Pending |

## 7. App Integrity Card

| Field | Value |
|---|---|
| App version | 1.0.0 |
| Quran database version | To be completed |
| Content manifest version | To be completed |
| Build date | To be completed |
| Release channel | Internal / Closed / Public |
| Source code/license page | To be completed if applicable |

## 8. Source Attribution Text Template

> Quran text source: [SOURCE NAME], version [VERSION]. Used under [LICENSE]. Pack checksum: [CHECKSUM]. Imported on [DATE]. Validation status: [STATUS].

## 9. Manual Review Text Template

> Manual content review status: [PENDING / REVIEWED / APPROVED]. Reviewer notes: [NOTES]. This release should not be published publicly until manual review is approved.

## 10. Trust Center Release Gate

Trust Center is complete only when:

1. Both IndoPak and Uthmani source cards are filled.
2. All required license fields are complete.
3. Checksums are shown.
4. Validation status is passed.
5. Manual review status is not pending for public release.
6. Privacy pledge is shown.
7. No-modification statement is shown.
8. App/database/content manifest versions are shown.


\newpage


# Developer Kickoff Prompt
# Amanah Quran - V1 Sacred Reader MVP

Use this prompt to start implementation with an Android development agent.

```text
You are building Amanah Quran V1.0, the Sacred Reader MVP, for Android.

Project identity: Amanah-e-Kisa.
Public app name: Amanah Quran.
Nature: charity / Sadaqah Jariyah project.
Commercial model: completely free, no ads, no in-app purchases, no monetization pressure.

Build only the Sacred Reader MVP. Do not add future features unless explicitly requested.

Core V1 scope:
1. Full Quran Arabic text offline.
2. IndoPak script support.
3. Uthmani script support.
4. Script switching.
5. Surah navigation.
6. Juz navigation.
7. Page navigation.
8. Ayah-level reader.
9. Last-read position.
10. Ayah and page bookmarks.
11. Offline search by Surah, ayah reference, Juz, page, and Arabic text.
12. Elder Mode with large text and large buttons.
13. Themes: system, light, dark, sepia/paper.
14. Trust Center with source, version, license, checksum, validation status, no-modification statement, and privacy pledge.

Hard guardrails:
1. No ads ever.
2. No ad SDK.
3. No analytics SDK in V1.
4. No tracking.
5. No advertising ID usage.
6. No login.
7. No account system.
8. No donations inside the reading experience.
9. No unnecessary permissions.
10. No network dependency for core V1 features.
11. No modification of Quran display text.
12. Search-normalized text must be stored separately and never displayed as Quran text.
13. No public release without content verification and manual review.

Recommended Android stack:
1. Kotlin.
2. Jetpack Compose.
3. Room SQLite with prepackaged database.
4. DataStore Preferences for settings.
5. Navigation Compose.
6. MVVM or MVI-lite.

Required screens:
1. Home.
2. Reader.
3. Quran Navigation: Surah/Juz/Page.
4. Search.
5. Bookmarks.
6. Settings.
7. Trust Center.

Required data model:
1. surahs.
2. ayahs.
3. quran_texts.
4. search_index.
5. bookmarks.
6. last_read.
7. content_sources.
8. content_validation.

Content validation release gate:
1. Surah count must be 114.
2. Ayah count must be 6236.
3. No duplicate ayah keys.
4. No missing IndoPak text.
5. No missing Uthmani text.
6. Search index must match ayah count.
7. Source metadata must include source name, URL, license, version, checksum, import date, validation status.
8. Trust Center must display source and verification details.

Implementation approach:
1. Create project scaffold and navigation shell.
2. Add Room schema and prepackaged database support.
3. Build Reader with script switching.
4. Add last-read persistence.
5. Add bookmarks.
6. Add offline search.
7. Add Elder Mode and themes.
8. Add Trust Center.
9. Add content validation checks.
10. Run offline, privacy, device, and release QA.

Deliver only V1. Do not implement audio, translations, tafsir, hifz, AI, prayer times, qibla, Islamic calendar, accounts, cloud sync, or notifications in this release.
```
