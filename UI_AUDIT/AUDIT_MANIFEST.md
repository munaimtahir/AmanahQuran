# AUDIT MANIFEST — AMANAH QURAN UI/UX EVIDENCE PACK

## Repository
- **Repository Path**: `/media/munaim/shared1/Documents/github/AmanahQuran`
- **Branch**: `main`
- **Commit SHA**: `9739d44ff70fa2574c2c6ed876f3b9a5c0c6390f`
- **Working Tree State**: Clean prior to audit execution

## Application
- **App Name**: Amanah Quran
- **Project Identity**: Amanah-e-Kisa
- **Package ID**: `org.amanahquran.app`
- **Version Name**: `2.2.0`
- **Version Code**: `11`
- **Build Variant**: `debug`
- **APK Path**: `apps/android/app/build/outputs/apk/debug/app-debug.apk`
- **APK Size**: `28,784,332 bytes` (~28 MB)
- **APK SHA256**: `1092363813e83db7634a31e4dacac17c5c7e11f5ab3c1d3de34c781bfe4cf25e`

## Emulator
- **Exact AVD Name**: `AdForge_API_36`
- **Serial**: `emulator-5556`
- **Android Version**: Android 16
- **API Level**: 36
- **Architecture**: `x86_64`
- **Device Profile**: Google Pixel 6 (`hw.device.name=pixel_6`)
- **Resolution**: `1080 x 1920` (portrait initial; landscape also validated)
- **Density**: `420` dpi
- **Allocated RAM**: `2048 MB`

## Audit Coverage
- **Number of Discovered Screens**: 21
- **Number of Screens Captured**: 21
- **Number of Screenshots Captured**: 58
- **Number of Screen Recordings Captured**: 6
- **Workflows Completed**:
  - Workflow 1: First Launch & Initial Landing (Cold launch, zero friction)
  - Workflow 2: Find & Read Surah, Juz, and Page (Al-Fatihah, Al-Baqarah, Juz 1, Page 1)
  - Workflow 3: Offline Search (Empty prompt, zero result, exact reference `2:255`, transliteration `Yasin`)
  - Workflow 5: Bookmark & Favourite Management (Toggle verse bookmark, view collection, resume from bookmark, untoggle)
  - Workflow 6: Settings & Appearance (IndoPak/Uthmani script switch, Light/Dark/Sepia themes, Elder Mode, Reading Reminders, Advanced Settings, Reset Settings)
  - Workflow 7: Trust Center & Provenance (Content licenses, font SHAs, zero-tracking verification)
  - Workflow 8: Reading Streak & Calendar (Streak counting, calendar grid)
  - Workflow 9: Daily Ayah & History (Daily verse card, history log)
  - Workflow 10: Reading Activity Dashboard (Personal reading statistics)
  - Workflow 11: Mushaf Page Reader Mode (Full page Mushaf layout)
- **Workflows Incomplete**: None.
- **Prohibited Features Guardrail Confirmation**: Audio playback, translations, tafsir, and word-by-word meanings are strictly excluded per `AGENTS.md` and `docs/ai-dev/00_AI_AGENT_MASTER_RULES.md` to protect authentic Quranic text integrity. Their absence was verified in code and documented in respective folder READMEs.
- **Inaccessible Features**: None. All discovered surfaces were exercised and captured.

## Important Limitations
1. **Physical Touch Hardware**: Tests were performed on official Android API 36 Google APIs x86_64 emulator; physical touchscreen tactile response and real-world sunlight reflectivity could not be measured on an emulator.
2. **Audio / Translation Scope**: Folders `08_audio/`, `09_tafsir_translation/`, and `workflow_04_audio/` are intentionally preserved with explanatory READMEs to document adherence to the V1 Sacred Reader MVP scope.
