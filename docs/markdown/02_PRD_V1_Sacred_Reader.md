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
