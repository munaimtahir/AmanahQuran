# Play Store Readiness Notes

## Suggested Category

- Books & Reference is the safest default.

## Data Safety Notes

- No account.
- No ads.
- No analytics.
- No tracking.
- Offline reading only.
- Local bookmarks and settings only.
- No user data collection or sharing.

## Permissions Notes

- No dangerous permissions are requested in the current manifest.
- Do not add permissions unless a concrete V1 feature requires them.

## Content Review Notes

- Manual Quran text review must be complete before submission.
- Font/license review must be complete before submission.
- Trust Center wording must stay conservative and truthful.

## Target SDK Compliance

- **Target SDK**: Android 16 (API Level 36)
- **Compile SDK**: Android 16 (API Level 36)
- **Min SDK**: Android 7.0 (API Level 24)
- **Release Version**: `2.1.1`
- **Release Version Code**: `9`

## Privacy Policy Notes

- A privacy policy will still be needed for store submission, even if it states that no data is collected.

## Release Readiness Summary

- Application updated to target API level 36 (Android 16) in compliance with Google Play Policy.
- Upgraded build configuration to Android Gradle Plugin (AGP) version `9.0.0` / Gradle `9.1.0`.
- Enabled R8 minification and resource shrinking for peak execution performance.
- Configured `ndk.debugSymbolLevel = "FULL"` to package complete native debug symbol tables for Google Play Console error reporting.
- Version `2.1.1` (versionCode `9`) is configured in `apps/android/app/build.gradle.kts` and verified via release bundle build (`app-release.aab`).


