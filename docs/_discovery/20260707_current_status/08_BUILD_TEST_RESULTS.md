# Build and Test Results

## Git state

- Branch: `main...origin/main`
- Repo is dirty with many pre-existing user changes.
- Discovery work did not revert or rewrite unrelated changes.

## Commands run

### Debug verification

Command:

```bash
./gradlew testDebugUnitTest assembleDebug lintDebug
```

Result:

- PASS
- `BUILD SUCCESSFUL`
- Lint HTML report generated at `apps/android/app/build/reports/lint-results-debug.html`

### Release build

Command:

```bash
./gradlew assembleRelease
```

Result:

- FAIL
- Release build stops in `:app:generateContentPipeline`
- Error:

```text
python3: can't open file '/home/munaim/Documents/github/AmanahQuran/apps/scripts/generate_content_pipeline.py': [Errno 2] No such file or directory
```

## Why release failed

The Gradle task in `apps/android/app/build.gradle.kts` points at:

- `../../scripts/generate_content_pipeline.py`

From the Android project root, that resolves to the wrong place. The real script exists at:

- `scripts/generate_content_pipeline.py`

Relevant lines:

- `apps/android/app/build.gradle.kts:124-130`

## Additional verification

### SQLite content checks

Command:

```bash
sqlite3 apps/android/app/src/main/assets/database/quran.db "SELECT COUNT(*) FROM surahs; ..."
```

Result:

- Surahs: 114
- Ayahs: 6236
- Quran text rows: 12472
- Search index rows: 6236
- Content source rows: 120
- Content validation rows: 9
- Font inventory rows: 41
- Mushaf layout reference rows: 1118

### Permission search

Command:

```bash
rg -n "android.permission|uses-permission|INTERNET|ACCESS_NETWORK_STATE|POST_NOTIFICATIONS|CAMERA|RECORD_AUDIO|READ_EXTERNAL_STORAGE|WRITE_EXTERNAL_STORAGE" apps/android/app/src/main/AndroidManifest.xml apps/android/app/build.gradle.kts apps/android/app/src/main/kotlin apps/android/app/src/main/res
```

Result:

- No matches.

### SDK search

Command:

```bash
rg -n "firebase|admob|google ads|tracking|analytics|login|auth|billing|push notification|remote config|crashlytics" apps/android/app/src/main apps/android/app/build.gradle.kts apps/android/build.gradle.kts
```

Result:

- No evidence of prohibited SDKs found in the app sources or Gradle files.

## Summary

- Debug build: PASS
- Unit tests: PASS
- Lint: PASS
- Release build: FAIL because of path wiring in the content pipeline task

