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
