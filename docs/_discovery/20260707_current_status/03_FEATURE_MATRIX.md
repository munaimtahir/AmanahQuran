# Feature Matrix

| Feature | Expected V1 behavior | Evidence found | Current status | Gaps / bugs | Release risk |
|---|---|---|---|---|---|
| Offline reading | Open and read Quran without network | Packaged `quran.db`, Room DAOs, offline UI | WORKING | Fresh physical-device regression still needed | Medium |
| Surah reader | Open a surah and read ayah-by-ayah | `SurahListScreen`, `SurahReaderScreen`, `QuranReaderScreen` | WORKING | Needs full-device rerun after release fixes | Medium |
| Juz reader | Open a juz and read local content | `JuzListScreen`, `QuranReaderScreen` | WORKING | Boundary evidence needs refresh | Medium |
| Page reader | Open page-based mushaf content | `PageListScreen`, `QuranReaderScreen`, `MushafPageScreen` | PARTIAL | Page 540 / continue-reading evidence still sensitive | High |
| IndoPak script | Render IndoPak Arabic | `QuranFonts.IndoPak`, `indopak_nastaleeq.ttf` | WORKING | Coverage warnings remain for rare code points | High |
| Uthmani script | Render Uthmani Arabic | `QuranFonts.Uthmani`, `digital_khatt_v2.otf` | WORKING | Coverage warnings remain for rare code points | High |
| Script switching | Switch between IndoPak and Uthmani locally | `ReaderSettingsRepository`, settings screen, reader reload logic | PARTIAL | Historical stale-state bug required retest | Medium |
| Last-read position | Save and restore last position locally | `LastReadRepository`, home continue-reading card | PARTIAL | Restore path and timing still need fresh device proof | High |
| Ayah bookmarks | Bookmark canonical ayah key | `BookmarkRepository`, bookmarks screen, exact anchor mapping | PARTIAL | Needs fresh exact-anchor regression proof | High |
| Page bookmarks | Bookmark canonical page number | `BookmarkRepository`, page bookmark toggle | WORKING | Needs fresh device rerun | Medium |
| Search by Surah | Find surah by name/number/alias | `SearchRepository.search`, alias map, surah DAO | PARTIAL | Alias and exact behavior still need device verification | Medium |
| Search by ayah reference | Find `2:255` style references | `parseAyahReference()` in search repo | WORKING | Needs device rerun in current build | Medium |
| Search by Arabic text | Search normalized Arabic offline | `search_index.normalized_arabic`, separate display lookup | WORKING | Coverage depends on source data and device validation | Medium |
| Elder Mode | Larger text and simpler UI | `ReaderSettingsRepository`, theme typography scaling | WORKING | Some settings controls can be harder to reach | Low |
| Themes | System, Light, Dark, Sepia | `ThemeMode`, settings screen, custom color schemes | WORKING | None obvious | Low |
| Trust Center | Explain sources, checksums, validation, privacy | `TrustCenterScreen`, `TrustCenterRepository`, local JSON | PARTIAL | Public-release wording still blocked | High |
| Source attribution | Show source/licensing/checksum metadata | Trust Center JSON + DB metadata tables | PARTIAL | IndoPak public-release source unresolved in asset | High |
| No login | No accounts or auth flows | No auth SDKs or manifest permissions found | WORKING | None observed | Low |
| No ads | No ads or ad SDKs | No AdMob/Firebase ads evidence | WORKING | None observed | Low |
| No tracking | No analytics/tracking SDKs | No analytics/crashlytics/remote-config evidence | WORKING | None observed | Low |
| No unnecessary permissions | No dangerous permissions | Manifest has no `uses-permission` entries | WORKING | None observed | Low |

