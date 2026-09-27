# V2.2 Emulator Validation Report

- Emulator: `Android_15_Test` (API 35 / Android 15, x86_64, `emulator-5554`)
- Status: Connected and Verified
- Test Runner: `./gradlew connectedDebugAndroidTest`
- Result: 6 / 6 PASS (100%)

## Instrumented Tests Run

1. `AmanahQuranUiSmokeTest.home_hasAccessibleSemantics`: PASS
2. `AmanahQuranUiSmokeTest.sacredReaderDestinations_openAndPassAccessibilityChecks`: PASS
3. `AmanahQuranUiSmokeTest.reader_opensOfflineAndPassesAccessibilityChecks`: PASS
4. `TranslationReaderUiTest.selectingEnglishTranslationShowsManifestTextAndSourceMissingPlaceholder`: PASS
5. `TranslationReaderUiTest.selectingUrduTranslationShowsIrfanTextAndSourceMissingPlaceholder`: PASS
6. `TranslationReaderUiTest.translationOffShowsNeitherManifestNorIrfanText`: PASS

## Highlights Verified Live on Emulator

- Home screen rendered with Daily Ayah card, reading streak, hero card, quick actions, reading activity, and trust center strips.
- Accessibility semantics verified clean across Home, Surah Index, Juz Index, Page Index, Search, Bookmarks, Settings, Trust Center, and offline Reader.
- Dual-translation integration (The Manifest Quran English + Irfan-ul-Quran Urdu) verified on device:
  - Settings translation selection persists and dynamically updates Reader.
  - Al-Fatihah 1:1 correctly displays the neutral `SOURCE_MISSING` placeholder ("Translation not provided in this source"), preserving translation source fidelity.
  - Al-Fatihah 1:2 correctly displays verbatim English/Urdu translations in their respective modes.
  - Translation Off mode cleanly hides all translation overlays.

Verdict: **PASS** (Emulator Validation Complete)

