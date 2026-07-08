# Phase 6 — Launch Crash Fix Report

## Summary
The latest installed Android build crashed immediately on launch with:

- `java.lang.IllegalStateException: CompositionLocal LocalLifecycleOwner not present`

The startup path was using lifecycle-aware state collection in Compose before the lifecycle owner was available in the composition tree on this device/build.

## Files Changed

- `/home/munaim/Documents/github/AmanahQuran/apps/android/app/src/main/kotlin/org/amanahquran/app/MainActivity.kt`
- `/home/munaim/Documents/github/AmanahQuran/apps/android/app/src/main/kotlin/org/amanahquran/app/feature/home/HomeScreen.kt`
- `/home/munaim/Documents/github/AmanahQuran/apps/android/app/src/main/kotlin/org/amanahquran/app/feature/settings/SettingsScreen.kt`
- `/home/munaim/Documents/github/AmanahQuran/apps/android/app/src/main/kotlin/org/amanahquran/app/feature/trust/TrustCenterScreen.kt`
- `/home/munaim/Documents/github/AmanahQuran/apps/android/app/src/main/kotlin/org/amanahquran/app/feature/bookmarks/BookmarksScreen.kt`
- `/home/munaim/Documents/github/AmanahQuran/apps/android/app/src/main/kotlin/org/amanahquran/app/feature/reader/SurahListScreen.kt`
- `/home/munaim/Documents/github/AmanahQuran/apps/android/app/src/main/kotlin/org/amanahquran/app/feature/reader/JuzListScreen.kt`
- `/home/munaim/Documents/github/AmanahQuran/apps/android/app/src/main/kotlin/org/amanahquran/app/feature/search/SearchScreen.kt`
- `/home/munaim/Documents/github/AmanahQuran/apps/android/app/src/main/kotlin/org/amanahquran/app/feature/reader/SurahReaderScreen.kt`

## Fix Applied

- Replaced lifecycle-aware Compose state collection with plain `collectAsState()` in the launch path and the first-level screens.
- Kept app behavior unchanged otherwise.
- No Quran display text was modified.

## Tests Run

- `./gradlew test :app:assembleDebug --no-daemon`
- `adb install -r apps/android/app/build/outputs/apk/debug/app-debug.apk`
- `adb shell monkey -p org.amanahquran.app -c android.intent.category.LAUNCHER 1`
- `adb shell pidof org.amanahquran.app`
- `adb logcat -d -v time`

## Results

- Gradle unit tests: pass
- Debug APK build: pass
- Device install: pass
- Post-fix launch: process remained running on device
- No new `AndroidRuntime` fatal exception observed after relaunch

## Known Issues

- The device log still contains unrelated vendor/app noise from background apps and system services.
- No additional UI interaction beyond launch was needed to confirm this crash fix.

## Scope Guardrail Confirmation

- No ads added
- No analytics added
- No login added
- No tracking added
- No network dependency added
- Quran display text not modified
- V1 Android-only scope preserved
