# ADB Device Validation Summary

## Device

- Device: TECNO CH6i
- Android version: Android 13
- SDK: 33
- APK tested: `apps/android/app/build/outputs/apk/debug/app-debug.apk`
- Mode: airplane mode / offline

## Features Tested

| Area | Result | Notes |
|---|---|---|
| APK install via adb | Pass | Installed successfully |
| App launch | Pass | No crash or ANR observed |
| Surah navigation | Pass | Surah list and reader opened |
| Juz navigation | Pass | Juz list and Juz reader opened |
| Page navigation | Pass | Page list and page reader opened |
| Script switching | Pass | IndoPak and Uthmani switched successfully |
| Script persistence | Pass | Selected script remained applied |
| Last-read / Continue Reading | Pass | Updated and restored correctly |
| Ayah bookmarks | Pass | Bookmark added and shown in list |
| Page bookmarks | Pass | Page bookmark added and shown in list |
| Offline search | Pass | Query `2:255` returned result and updated continue-reading state |
| Elder Mode | Pass | Enabled and persisted |
| Light / Dark / Sepia / System themes | Pass | Themes were switched and confirmed |
| Trust Center | Pass | Opened and reviewed offline |
| Login prompt | Pass | None observed |
| Permission prompt | Pass | None observed |
| Internet dependency | Pass | No dependency observed in airplane mode |
| Crash / ANR | Pass | None observed |

## Notes

- `adb shell input text` was unreliable for queries containing spaces.
- A real space keyevent was used to complete the `juz 30` search path.
- No logcat was collected because no crash or ANR occurred.
- The page list terminal mapping observed in this build was Page 559.
- Real-device validation was completed on one physical device only.

## Final Verdict

GO FOR INTERNAL DEVICE TESTING

This is not a public release approval.
