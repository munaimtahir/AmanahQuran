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
