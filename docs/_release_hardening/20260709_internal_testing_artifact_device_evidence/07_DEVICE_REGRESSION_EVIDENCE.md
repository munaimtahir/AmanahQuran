# Device Regression Evidence

## Device metadata

- Device model: V2109
- Android version: 13
- API level: 33
- Resolution: 1080x2226
- Airplane mode: enabled with `adb shell cmd connectivity airplane-mode enable`

## Install command

```bash
adb uninstall org.amanahquran.app
adb install -r -g apps/android/app/build/outputs/apk/release/app-release.apk
```

## Evidence captured

Captured screenshots:
- `screenshots/relaunch.png`
- `screenshots/home_for_search540.png`
- `screenshots/search_2255_filled.png`
- `screenshots/ayah_opened.png`
- `screenshots/ayah_bookmarked.png`
- `screenshots/ayah_unbookmarked.png`
- `screenshots/bookmarks_screen2.png`
- `screenshots/settings_screen3.png`
- `screenshots/settings_elder_on.png`
- `screenshots/theme_light.png`
- `screenshots/theme_dark.png`
- `screenshots/theme_sepia2.png`
- `screenshots/search540_filled.png`
- `screenshots/page540_indopak.png`
- `screenshots/page540_indopak_switched.png`
- `screenshots/settings_trust_visible.png`
- `screenshots/home_scrolled_to_trust.png`
- `screenshots/home_after_search.png`
- `screenshots/page540_bookmark_added.png`
- `screenshots/page540_after_unbookmark_tap.png`
- `screenshots/bookmarks_after_page_add.png`
- `screenshots/bookmarks_after_remove_check.png`
- `screenshots/trust_center_attempt.png`

Observed on device:
- Home screen loaded with no login prompt.
- Home screen showed offline / source-attributed / no-ads messaging.
- Search `2:255` opened the ayah result for Al-Baqarah 2:255.
- Ayah bookmark add/remove was exercised on-device.
- Bookmarks screen showed the saved ayah entry, then returned to the reader after re-open.
- Continue Reading was visible after app restart and still pointed to Page 40, Al-Baqarah.
- Settings screen exposed script, theme, Arabic font size, Elder Mode, and Trust Center sections.
- Elder Mode toggle was enabled on-device.
- Light, Dark, and Sepia theme states were captured.
- Search `540` returned page search results for Page 540.
- Page 540 was opened and captured in both IndoPak and Uthmani.
- IndoPak and Uthmani script switching on the page 540 reader was captured.
- Page 540 bookmark add/remove was exercised on-device.
- Bookmarks screen showed the Page 540 entry after add, then the empty state after removal.
- Trust Center opened as a dedicated offline route and showed the conservative internal-test content, including the Quran source section and public-release/manual-review blocked state.

What remains incomplete:
- No controlled timing series was captured for cold start / reader open / search / script switching.

Reason:
- App-owned performance trace logs were not emitted during this pass, so the remaining timing evidence is observational only.
