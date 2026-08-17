# Amanah Quran — Release Ledger

## Release Entry: Version 9 (2.1.1)

- **App Name**: Amanah Quran
- **Project Identity**: Amanah-e-Kisa
- **Release Version**: `2.1.1`
- **Version Code**: `9`
- **Release Date**: August 17, 2026
- **Target Platform**: Android
- **Release Bundle (AAB)**: `apps/android/app/build/outputs/bundle/release/app-release.aab`
- **Release Bundle SHA-256**: `0a1537111a3c16115ecd402e587860b49ca32308bd87b86e12145c9779bbb88e`
- **Scope**:
  - Continuous Reader presentation mode (Continuous View & Ayah View) with legacy migration.
  - Dynamic in-memory Active Reading Position deriving Surah, Juz, and Page header sync continuously on scroll.
  - Surah 3 English display name corrected to `Al Imran` across metadata, database, search index, and tests.
  - Clean Juz boundary architecture forcing new line breaks on Juz transitions.
  - Auto-scroll UX with intuitive horizontal speed slider.
  - 100% offline functionality, zero ads, zero tracking, zero SDK telemetries.
- **Verification Gates**:
  - Unit Tests: PASS (250/250 tests passed).
  - Connected Android Emulator Tests: PASS (3/3 on Android 8.0.0 / API 26).
  - Quran Database Validation: PASS (6,236 Ayahs, 114 Surahs canonical integrity).
  - Asset Scanner & License Clearance: PASS (0 blockers).
  - Production Release Signing: PASS (Signed with production keystore, R8 minification, full NDK symbols).

---

## Release Entry: Version 8 (2.1.0) — Uploaded to Play Store

- **App Name**: Amanah Quran
- **Project Identity**: Amanah-e-Kisa
- **Release Version**: `2.1.0`
- **Version Code**: `8`
- **Release Date**: August 17, 2026
- **Target Platform**: Android
- **Status**: Uploaded to Google Play Store Console.
- **Release Bundle SHA-256**: `e0f9a9a6f8bc4401a2d9a561f9957888ae1041cef567145fa3d400000189e628`

---

## Release Entry: Version 5 (1.0.5)

- **App Name**: Amanah Quran
- **Project Identity**: Amanah-e-Kisa
- **Release Version**: `1.0.5`
- **Version Code**: `5`
- **Release Date**: July 23, 2026
- **Target Platform**: Android

---

## Release Entry: Amanah Quran V2.0

- **Release Version**: `2.0.0` / versionCode `6` (Built 2026-08-05)
- **Scope**: Offline Urdu Junagarhi translation from QuranEnc, translation-aware reader, bookmark collections, migration-safe local backup codec, and Trust Center pack metadata.

---

### Technical Specification & Release Verification

1. **Android Gradle Plugin (AGP)**
   - **AGP Version**: `9.0.0` / Gradle `9.1.0`.
   - **Status**: Verified compatible with Kotlin and Compose compiler plugins.

2. **Performance Optimization (R8 Engine)**
   - **Code Optimization & Minification**: `isMinifyEnabled = true` enabled in release build type.
   - **Resource Shrinking**: `isShrinkResources = true` enabled in release build type.
   - **Status**: R8 optimization active for maximum runtime performance and minimal APK/AAB footprint.

3. **Android 16 API Level 36 Target**
   - **Compile SDK**: `compileSdk = 36` (Android 16)
   - **Target SDK**: `targetSdk = 36` (Android 16)
   - **Min SDK**: `minSdk = 24` (Android 7.0)
   - **Status**: Compliant with latest Google Play target API level requirements.

4. **Play Console Native Debug Symbols Table**
   - **NDK Debug Symbol Level**: Configured to `debugSymbolLevel = "FULL"`.
   - **Resolution**: Embeds full native debug symbols into the release bundle zip for Play Console crash deobfuscation.

5. **Signing & Content Integrity**
   - **Signing Config**: Configured with production release keystore (`production-keystore.jks`).
   - **Content Validation**: Automated gate `validateReleaseContent` verifies `quran.db` and `trust_center_content.json` integrity.
   - **Privacy Compliance**: Zero ads, zero tracking, zero telemetry, zero network dependencies, 100% offline functional.

---

### Release Ledger History

| Version Code | Version Name | Release Date | Target SDK | AGP Version | R8 Minification | Native Debug Symbols | Build Track | Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **9** | **2.1.1** | **2026-08-17** | **API 36 (Android 16)** | **9.0.0** | **Enabled (R8 + Resource Shrinking)** | **FULL (Symbol Table Included)** | **Public Release** | **SIGNED & APPROVED** |
| 8 | 2.1.0 | 2026-08-17 | API 36 (Android 16) | 9.0.0 | Enabled (R8 + Resource Shrinking) | FULL (Symbol Table Included) | Public Release | Uploaded to Play Store |
| 6 | 2.0.0 | 2026-08-05 | API 36 (Android 16) | 9.0.0 | Enabled (R8 + Resource Shrinking) | FULL (Symbol Table Included) | Internal | Superseded |
| 5 | 1.0.5 | 2026-07-23 | API 36 (Android 16) | 9.0.0 | Enabled (R8 + Resource Shrinking) | FULL (Symbol Table Included) | Public Release | Superseded |
| 4 | 1.0.4 | 2026-06-22 | API 34 | 8.6.0 | Enabled | SYMBOL_TABLE | Internal | Superseded |

