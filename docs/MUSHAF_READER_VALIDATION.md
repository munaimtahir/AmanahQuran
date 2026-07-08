# Mushaf Page Reader Manual Validation Checklist

This checklist provides a manual testing reference to verify the visual accuracy, script correctness, and navigation behaviors of the new Mushaf Page Reader.

## 1. Reading Experience & Render Integrity

- [ ] **No Missing Glyphs:** Scroll through several pages (1-10) of both Uthmani and IndoPak scripts. Ensure no rectangular fallback boxes appear.
- [ ] **No Accidental Underlines:** Verify that Quran display text is not styled with underlines.
- [ ] **No Duplicate Ayah Markers:** Look at ayah boundaries. Ensure there is only one ayah marker system (i.e. if the text contains a marker, no app-generated badge is rendered alongside).
- [ ] **No Line Wrapping:** Verify that no individual line of the 15 Mushaf lines wraps to a new line or collides with the border margins.
- [ ] **No Clipped Text:** Ensure vertical text lines are not clipped by page borders on smaller devices.

## 2. Key Pages & Content Verification

- [ ] **Al-Fatihah:** Opening Surah Fatihah starts correctly on its first page.
- [ ] **Al-Baqarah start:** Start of Surah Al-Baqarah renders properly with Alif-Lam-Mim.
- [ ] **Ayat al-Kursi (2:255):** Open page containing 2:255. Verify content displays exactly as intended.
- [ ] **Surah Yaseen:** Verify pages contain the complete surah text.
- [ ] **Surah Ar-Rahman:** Verify formatting and readability.
- [ ] **Surah Al-Mulk:** Verify pages are correctly loaded.
- [ ] **Juz 30:** Browse pages in Juz 30. Confirm short Surahs are correctly mapped and readable.

## 3. Reader Controls & Interactivities

- [ ] **Fullscreen Toggle:** Tap the center of a Mushaf page. It should toggle the visibility of the Top Toolbar and Bottom Page Controls.
- [ ] **Marginal Taps:** Tap the left margin (Alignment.CenterStart) to turn to the next page. Tap the right margin (Alignment.CenterEnd) to turn to the previous page.
- [ ] **RTL Swipe Directions:** Swipe left (finger moves right-to-left) to increase the page number. Swipe right (finger moves left-to-right) to go back.
- [ ] **Bookmark Ribbon:** Tap the bookmark ribbon at the top-right of the page to add/remove the bookmark. Verify the state changes visually.

## 4. Navigation & Settings Integration

- [ ] **Continue Reading:** Reopening the app and tapping Continue Reading restores the exact page last read.
- [ ] **Offline Mode:** Turn on airplane mode on the device and verify that all pages, settings, and navigations continue to work without any network requests.
- [ ] **Elder Mode:** Turn on Elder Mode in Settings. Verify that page control button sizes scale up to 56.dp and page headers remain legible and spacious.
- [ ] **Theme Switching:** Switch the theme to Light, Dark, Sepia, and System. Verify text readability and colors in all modes.
