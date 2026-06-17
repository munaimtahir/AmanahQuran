# 12 — Project Bootstrap Summary

## Purpose

This document condenses the current Amanah Quran repository state into one working reference for future AI coding sessions.

It is derived from:

- `/AGENTS.md`
- `/docs`
- `/docs/ai-dev`
- The current repository tree under `/apps/android`

Use this as the first-stop summary before making further changes.

## Current Project Status

### Identity

- Public app name: `Amanah Quran`
- Project identity: `Amanah-e-Kisa`
- V1 theme: `Sacred Reader MVP`
- Primary platform for V1: Android
- Future platform: Web placeholder only

### Current implementation state

- The Android scaffold already exists under `/apps/android`.
- The app is currently a Compose navigation shell with placeholder screens.
- No Quran database, import pipeline, bookmarks storage, search engine, or content validation engine is implemented yet.
- Existing unit tests for route registration and theme enumeration pass.
- The current repository state is aligned with the documented V1 guardrails.

### Latest verification

- Command run: `./gradlew testDebugUnitTest`
- Result: `BUILD SUCCESSFUL`
- Scope of verification: current Android unit tests only

## V1 Scope

Build only:

- Offline Quran reading
- IndoPak script support
- Uthmani script support
- Script switching
- Surah navigation
- Juz navigation
- Page navigation
- Last-read position
- Bookmarks
- Offline search
- Elder Mode
- Light / Dark / Sepia / System themes
- Trust Center
- Content source attribution and verification

Do not build:

- Ads
- Analytics SDKs
- Tracking
- Login or accounts
- Cloud sync
- Donation prompts
- In-app purchases
- Audio
- Translation
- Tafsir
- Word-by-word meaning
- Hifz tools
- AI
- Prayer times
- Qibla
- Islamic calendar
- Hadith database
- Social features
- Push notifications
- Any network-dependent core feature

## Existing Files

### Root and documentation

- `README.md`
- `AGENTS.md`
- `README_AI_DEV_MISSING_FILES.md`
- `docs/Amanah_Quran_Documentation_Pack.md`
- `docs/Amanah_Quran_Documentation_Pack.docx`
- `docs/Amanah_Quran_Documentation_Pack.pdf`
- `docs/markdown/01_Project_Charter.md`
- `docs/markdown/02_PRD_V1_Sacred_Reader.md`
- `docs/markdown/03_Android_Technical_Architecture.md`
- `docs/markdown/04_Data_Model_and_Content_Verification.md`
- `docs/markdown/05_UX_Accessibility_and_Reader_Spec.md`
- `docs/markdown/06_Privacy_No_Tracking_and_Compliance.md`
- `docs/markdown/07_QA_Release_Checklist.md`
- `docs/markdown/08_Roadmap_Backlog_and_Milestones.md`
- `docs/markdown/09_Trust_Center_Template.md`
- `docs/markdown/10_Developer_Kickoff_Prompt.md`
- `docs/source/Project_Handoff.md`
- `docs/source/V1_PRD_Input.md`
- `docs/checklists/release_checklist.csv`
- `docs/templates/content_manifest_template.json`
- `docs/templates/validation_report_template.json`

### AI-dev guidance

- `docs/ai-dev/00_AI_AGENT_MASTER_RULES.md`
- `docs/ai-dev/01_REPO_STRUCTURE.md`
- `docs/ai-dev/02_IMPLEMENTATION_PLAN.md`
- `docs/ai-dev/03_SPRINT_PROMPTS.md`
- `docs/ai-dev/04_CODE_GUARDRAILS.md`
- `docs/ai-dev/05_CONTENT_DATA_CONTRACTS.md`
- `docs/ai-dev/06_TESTING_GATES.md`
- `docs/ai-dev/07_RELEASE_GATES.md`
- `docs/ai-dev/08_CONTEXT_REFRESH_PROMPT.md`
- `docs/ai-dev/09_DO_NOT_BUILD_YET.md`
- `docs/ai-dev/10_FUTURE_WEBAPP_READY_SCAFFOLD.md`
- `docs/ai-dev/11_SCAFFOLD_DIRECTORY_PROMPT.md`
- `docs/ai-dev/README.md`

### Android app scaffold

- `apps/android/build.gradle.kts`
- `apps/android/settings.gradle.kts`
- `apps/android/gradle.properties`
- `apps/android/app/build.gradle.kts`
- `apps/android/app/src/main/AndroidManifest.xml`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/MainActivity.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/AmanahQuranApp.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/navigation/AppRoute.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/navigation/AmanahQuranNavHost.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/theme/ThemeMode.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/theme/AmanahQuranTheme.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/ui/PlaceholderScreen.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/home/HomeScreen.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/reader/ReaderScreen.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/search/SearchScreen.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/bookmarks/BookmarksScreen.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/settings/SettingsScreen.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/trust/TrustCenterScreen.kt`
- `apps/android/app/src/test/kotlin/org/amanahquran/app/core/navigation/AppRouteTest.kt`
- `apps/android/app/src/test/kotlin/org/amanahquran/app/core/theme/ThemeModeTest.kt`

### Placeholder directories already present

- `apps/android/app/src/main/assets/db/.gitkeep`
- `apps/android/app/src/main/assets/content/.gitkeep`
- `apps/android/app/src/main/assets/fonts/.gitkeep`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/database/.gitkeep`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/datastore/.gitkeep`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/model/.gitkeep`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/util/.gitkeep`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/content/manifest/.gitkeep`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/content/validation/.gitkeep`
- `apps/android/app/src/androidTest/.gitkeep`

## Proposed Files To Modify Next

These are the most likely next edit targets for the next implementation sprint:

- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/ui/PlaceholderScreen.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/navigation/AmanahQuranNavHost.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/theme/AmanahQuranTheme.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/theme/ThemeMode.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/home/HomeScreen.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/reader/ReaderScreen.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/search/SearchScreen.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/bookmarks/BookmarksScreen.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/settings/SettingsScreen.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/trust/TrustCenterScreen.kt`
- `apps/android/app/src/test/kotlin/org/amanahquran/app/core/navigation/AppRouteTest.kt`
- `apps/android/app/src/test/kotlin/org/amanahquran/app/core/theme/ThemeModeTest.kt`

Later, when content work starts:

- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/database/`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/datastore/`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/model/`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/content/manifest/`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/content/validation/`

## Tests To Run

### Already passing baseline

- `./gradlew testDebugUnitTest`

### Recommended next tests as implementation expands

- Android unit tests for route and theme behavior
- ViewModel tests for reader, search, bookmarks, settings, and trust center state
- Data validation tests for Quran content integrity
- DAO tests for Room entities once the database layer exists
- Manifest and validation report parsing tests
- UI smoke tests for the six core screens
- Offline behavior checklist in airplane mode

## Guardrails

### Content integrity

- Never modify Quran display text.
- Store search-normalized text separately.
- Never render normalized text as display text.
- Use canonical references like `surah:ayah` and page number for bookmarks and last-read state.

### Privacy

- No ads.
- No analytics SDK.
- No tracking.
- No login.
- No cloud sync.
- No data collection.
- No data sharing.
- No unnecessary permissions.
- App must work fully offline after install.

### Scope control

- Stay inside the Sacred Reader MVP.
- Do not add audio, translation, tafsir, AI, prayer times, qibla, or other out-of-scope features.
- Keep the repository Android-first.
- Keep the web app as placeholder-only until explicitly approved.

## Operational Notes

- The current scaffold is intentionally minimal and clean.
- The main near-term work is content and data infrastructure, not feature expansion.
- Any future change that affects architecture, data contracts, or release rules should update the relevant docs.

