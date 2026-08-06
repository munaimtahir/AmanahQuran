# Amanah Quran — Release Ledger

## Release Entry: Version 5 (1.0.5)

- **App Name**: Amanah Quran
- **Project Identity**: Amanah-e-Kisa
- **Release Version**: `1.0.5`
- **Version Code**: `5`
- **Release Date**: July 23, 2026
- **Target Platform**: Android

---

## Release Entry: Amanah Quran V2.0 (in development)

- **Release Version**: `2.0.0` / versionCode `6` (AAB rebuilt 2026-08-05 after the Page Mode/Trust Center fixes below, SHA-256 `244912ea6316551a18b680d414b483769f8023c274d7eabe9a49925a0930002b`; not yet uploaded or published — upload/publish remains a human decision)
- **Scope**: Offline Urdu Junagarhi translation from QuranEnc, translation-aware reader, bookmark collections, migration-safe local backup codec, and Trust Center pack metadata.
- **Translation source**: QuranEnc Urdu Muhammad Junagarhi, CSV `v1.1.3-csv.1`.
- **Source checksum**: `027cd258d87285bdb8afbffa60fd141c450a1d029b14c16501355ab24481fec4`.
- **Automated pack validation**: PASS (6,236 canonical mappings).
- **Human review**: Recorded internally (see `docs/_public_release_approval/` and `docs/legal/`); reviewer identity is kept out of user-facing Trust Center text by design.
- **Licence/republication terms**: Recorded from QuranEnc; retain final legal evidence for release audit.
- **Audio**: Deferred to V3.0 pending an approved reciter source; no audio SDK or UI is included in V2.0.
- **Current verdict**: `V2.0 RELEASE CANDIDATE — ENGINEERING GATES PASS, AAB BUILT, AWAITING HUMAN UPLOAD DECISION`. See `docs/_implementation/V2_IMPLEMENTATION_STATE.md` for full evidence and known non-blocking gaps.

The implementation state and gate evidence are maintained in [`docs/_implementation/V2_IMPLEMENTATION_STATE.md`](../_implementation/V2_IMPLEMENTATION_STATE.md).

---

### Technical Specification & Release Verification

1. **Android Gradle Plugin (AGP) Upgrade**
   - **AGP Version**: Upgraded to `9.0.0` in `apps/android/build.gradle.kts`.
   - **Status**: Verified compatible with Kotlin 1.9.24 and KSP.

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
   - **Resolution**: Fixes Play Console missing symbol table warning by embedding full native debug symbols into the Android App Bundle (AAB) / release output zip.

5. **Signing & Content Integrity**
   - **Signing Config**: Configured with production release keystore (`release-keystore.jks`).
   - **Content Validation**: Automated gate `validateReleaseContent` verifies `quran.db` and `trust_center_content.json` integrity.
   - **Privacy Compliance**: Zero ads, zero tracking, zero telemetry, zero network dependencies, 100% offline functional.

---

### Release Ledger History

| Version Code | Version Name | Release Date | Target SDK | AGP Version | R8 Minification | Native Debug Symbols | Build Track | Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **5** | **1.0.5** | **2026-07-23** | **API 36 (Android 16)** | **9.0.0** | **Enabled (R8 + Resource Shrinking)** | **FULL (Symbol Table Included)** | **Public Release** | **SIGNED & APPROVED** |
| 4 | 1.0.4 | 2026-06-22 | API 34 | 8.6.0 | Enabled | SYMBOL_TABLE | Internal | Superseded |
