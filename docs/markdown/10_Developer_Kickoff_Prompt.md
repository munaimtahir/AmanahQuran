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
