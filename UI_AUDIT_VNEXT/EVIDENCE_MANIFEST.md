# Evidence Manifest — UI Refinement (R1–R5)

What was verified for this round and how. There was no emulator in the build environment (no KVM), so there are **no new device screenshots or videos**. The checks below run on the JVM (Robolectric + Compose UI test), as part of `./gradlew :app:testDebugUnitTest`.

| Area | Evidence | Kind |
|---|---|---|
| R1 Linked text size in the reader sheet | `ReaderSettingsSheetLogicTest` (linked/unlinked/no-translation cases) and `ReaderSettingsSheetUiTest.inElderMode_linkToggleRevealsSeparateTranslationSlider` | Unit + Compose UI |
| R1 Sheet at 200% system font scale | `ReaderSettingsSheetUiTest.atDoubleFontScale_everyControlIsReachable_andBlackThemeSelectable` (360×640dp, font scale 2.0) | Compose UI |
| R1 Chrome hide/show on scroll | `ReaderSettingsSheetLogicTest.chromeVisibility_*` | Unit |
| R1 Sheet labels match Settings | `ReaderSettingsSheetLogicTest.sheetLabels_matchSettingsWording` | Unit |
| R2 Jump to ayah (1:1, 2:286, 114:6, invalid refs) | `AyahReferenceParserTest`, `ReaderMvpViewModelTest.jumpToReference_*` (real bundled content DB) | Unit + ViewModel |
| R2 One-time ayah tap hint persists | `ReaderSettingsRepositoryTest.ayahTapHintDefaultsOffAndPersists` | Unit |
| R3 Search/Bookmarks directly under Continue Reading | `HomeSectionOrderTest` | Unit |
| R3 Reading position separate from bookmarks | `BookmarksViewModelTest.readingPositionIsShownSeparatelyFromSavedBookmarks` | ViewModel (real DB) |
| R4 Urdu Nastaliq line height ≥1.4× at every size, more than English | `TranslationLineHeightTest` (14–40sp) | Unit |
| R5 Black (OLED) theme: true #000000, text contrast ≥7:1 (WCAG AAA) | `ThemeModeTest` | Unit |
| R5 Black theme persists and survives backup/restore | `ReaderSettingsRepositoryTest.optionalBlackThemePersists`, `UserBackupCodecTest.blackThemeRoundTripsThroughBackup` | Unit |
| R5 Settings preview shows verified DB text (not hard-coded) | `TypographyPreviewCardUiTest` | Compose UI (real DB) |
| Search keyboard action only opens a single result | `SearchImeActionTest` | Unit |

## Still to capture on a device (owner / QA)
1. Reader: hide chrome by scrolling, reveal by tapping or scrolling up; confirm the text does not jump.
2. Reader at 200% system font scale and in Elder Mode with TalkBack on: toolbar is read first and never auto-hides.
3. Home on a 1080×1920 phone at 100% font scale: Search and Bookmarks visible without scrolling.
4. Black (OLED) theme across Home, reader, Settings and the reader sheet.
5. Urdu translation at maximum size: no clipped Nastaliq glyphs.

Store screenshots under `UI_AUDIT_VNEXT/SCREENSHOTS/` with the step number in the file name.
