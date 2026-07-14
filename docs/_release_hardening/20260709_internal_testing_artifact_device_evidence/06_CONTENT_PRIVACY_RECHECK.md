# Content and Privacy Recheck

## Quran database counts

Command:

```bash
sqlite3 apps/android/app/src/main/assets/database/quran.db "select 'surahs', count(*) from surahs union all select 'ayahs', count(*) from ayahs union all select 'quran_texts', count(*) from quran_texts union all select 'search_index', count(*) from search_index;"
```

Results:
- Surahs: 114
- Ayahs: 6236
- Quran text rows: 12472
- Search index rows: 6236

## Display/search separation

Schema check:
- `quran_texts.display_text` exists as the display field
- `search_index.normalized_arabic` exists as the search-only field

Empty-row checks:
- Empty `quran_texts.display_text` rows: 0
- Empty `search_index.normalized_arabic` rows: 0

## Permissions audit

Commands:

```bash
rg -n "android.permission|uses-permission|INTERNET|ACCESS_NETWORK_STATE|POST_NOTIFICATIONS|CAMERA|RECORD_AUDIO|READ_EXTERNAL_STORAGE|WRITE_EXTERNAL_STORAGE" apps/android/app/src/main/AndroidManifest.xml apps/android/app/build.gradle.kts apps/android/app/src/main/kotlin apps/android/app/src/main/res
```

```bash
rg -n "firebase|admob|google ads|tracking|analytics|login|auth|billing|push notification|remote config|crashlytics" apps/android/app/src/main apps/android/app/build.gradle.kts apps/android/build.gradle.kts
```

Results:
- No dangerous permissions were found.
- No ads, analytics, login, billing, or tracking SDK references were found.
- The only hits were user-facing text strings saying the app is offline / no tracking.

## Trust Center state

- Trust Center JSON stays conservative.
- Manual review remains pending.
- Public release approval remains blocked.
