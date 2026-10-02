# UI/UX Implementation & Testing Plan

> **Project**: Amanah Quran (Amanah-e-Kisa)
> **Document Version**: 1.0
> **Date**: 2026-10-03
> **Status**: Planning Phase (No Execution)

---

## Table of Contents

1. [Executive Summary](#executive-summary)
2. [Completed UI/UX Features](#completed-ux-features)
3. [Pending Feature: Bookmark Collections](#pending-feature-bookmark-collections)
4. [Android Emulator Testing Plan](#android-emulator-testing-plan)
5. [Test Execution Procedures](#test-execution-procedures)
6. [Test Reporting & Acceptance Criteria](#test-reporting--acceptance-criteria)

---

## Executive Summary

This document outlines:
1. **11 completed UI/UX features** requiring emulator testing on the `quran` emulator
2. **1 pending feature** (Bookmark Collections) requiring architecture planning
3. **Comprehensive testing plan** including emulator factory reset procedures

All testing will be performed on the dedicated Android emulator named `quran` with factory reset between test suites to ensure clean state.

---

## Completed UX Features

### Phase 1: Quick Wins

#### 1.1 Verse Tappability Visual Cue
**Files Modified:**
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/reader/ContinuousReaderRenderer.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/reader/SurahReaderScreen.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/reader/TranslationRendering.kt`

**Implementation:**
- Added `MutableInteractionSource` and `collectIsPressedAsState` to track press state
- Subtle background highlight on verse press (8% alpha of active control color)
- Removed for stability (simple press feedback sufficient)

**Test Scenarios:**
- [ ] Tap verse in continuous reader - verify visual feedback
- [ ] Tap verse in ayah-by-ahah mode - verify visual feedback
- [ ] Tap translation text - verify visual feedback
- [ ] Verify no visual feedback on non-interactive elements
- [ ] Verify press state resets correctly after tap

**Acceptance Criteria:**
- Verse areas show subtle background change on press
- No performance degradation on large Surahs (e.g., Al-Baqarah 286 verses)
- Visual feedback respects theme colors (light/dark/sepia/black)

---

#### 1.3 Search Empty State Enhancement
**Files Modified:**
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/search/SearchScreen.kt`

**Implementation:**
- Expanded popular passages from 6 to 10 suggestions
- Added "Search Tips" section with usage examples
- Suggestions include: Yasin, Al-Kahf, Al-Mulk, Ar-Rahman, 2:255, 2:286, 67:1, 112:1, Juz 30, Page 1

**Test Scenarios:**
- [ ] Open search screen - verify popular passages displayed
- [ ] Tap suggestion chip - verify correct search executes
- [ ] Verify all 10 chips are visible and tappable
- [ ] Verify search tips text is readable in all themes
- [ ] Test numeric reference routing (e.g., "2:255")
- [ ] Verify chips work in both light and dark themes

**Acceptance Criteria:**
- 10 suggestion chips displayed in empty state
- Search tips provide clear usage examples
- Chips resolve to correct Quran locations
- Layout adapts to Elder Mode (fewer chips per row)

---

### Phase 2: Reader Polish

#### 2.3 Reading Progress Visualization
**Files Modified:**
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/home/HomeViewModel.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/home/HomeScreen.kt`

**Implementation:**
- Added `progress: Float` field to `HomeContinueReadingUiModel`
- Calculates progress as `ayahNumber / totalAyahsInSurah`
- Added `LinearProgressIndicator` in Continue Reading card
- Shows percentage text (e.g., "45% of Surah complete")

**Test Scenarios:**
- [ ] Open app with last-read position - verify progress bar appears
- [ ] Progress bar shows correct percentage
- [ ] Progress bar updates when reading new ayahs
- [ ] Progress bar hidden for first-time users (0% progress)
- [ ] Verify progress bar colors match theme
- [ ] Test with short Surahs (e.g., Al-Fatiha 7 verses)
- [ ] Test with long Surahs (e.g., Al-Baqarah 286 verses)

**Acceptance Criteria:**
- Progress accurately reflects ayah position in Surah
- Progress bar only shows when progress > 0%
- Visual design consistent with card styling
- No performance impact on home screen rendering

---

#### 1.2 Ayah Action Bar Enhancement
**Files Modified:**
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/reader/SurahReaderScreen.kt`

**Implementation:**
- Wrapped `ReaderSelectedAyahActionCard` in `AnimatedVisibility`
- Added fade-in and slide-in animations
- Added haptic feedback on bookmark toggle

**Test Scenarios:**
- [ ] Tap verse - verify action bar animates in smoothly
- [ ] Dismiss action bar - verify animates out smoothly
- [ ] Bookmark verse - verify haptic feedback
- [ ] Test animation speed (should be 200-300ms)
- [ ] Verify animation respects Elder Mode (may need slower)

**Acceptance Criteria:**
- Action bar appears/disappears with smooth animation
- Haptic feedback on bookmark action
- Animation doesn't interfere with reading flow
- No jarring or distracting transitions

---

#### 2.1 Mushaf Page Navigation
**Files Modified:**
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/reader/mushaf/MushafPageScreen.kt`

**Implementation:**
- Kept existing swipe navigation (HorizontalPager/VerticalPager)
- Simplified pinch-to-zoom (removed complex gestures for stability)
- Left/right margin tap zones for page navigation

**Test Scenarios:**
- [ ] Swipe left/right - verify page navigation
- [ ] Tap left margin - verify next page
- [ ] Tap right margin - verify previous page
- [ ] Test in book mode (horizontal swipe)
- [ ] Test in vertical mode (vertical swipe)
- [ ] Verify page number updates correctly
- [ ] Test at first and last pages (no out-of-bounds)

**Acceptance Criteria:**
- Swipe navigation works smoothly
- Margin tap zones provide alternative navigation
- No gesture conflicts with fullscreen toggle
- Page state persists correctly

---

#### 2.2 Auto-Scroll Progress Indicator
**Files Modified:**
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/reader/ReaderAutoScrollController.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/reader/SurahReaderScreen.kt`

**Implementation:**
- Added `progress: Float` property to `AutoScrollController`
- Calculates progress as `firstVisibleIndex / totalItemsCount`
- Visual progress ring around auto-scroll button (normal mode)
- Linear progress bar below button (Elder Mode)

**Test Scenarios:**
- [ ] Start auto-scroll - verify progress ring appears
- [ ] Scroll through Surah - verify progress updates
- [ ] Pause auto-scroll - verify progress ring persists
- [ ] Test in Elder Mode - verify linear bar appears
- [ ] Verify progress resets correctly on new Surah
- [ ] Test progress at start (0%) and end (100%)

**Acceptance Criteria:**
- Progress accurately reflects scroll position
- Visual indicator updates smoothly
- Ring animation doesn't interfere with performance
- Elder Mode shows linear bar instead of ring

---

### Phase 3: Polish & Delight

#### 3.3 Typography Preview Enhancement
**Files Modified:**
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/settings/TypographyPreviewCard.kt`
- `apps/android/app/src/test/kotlin/org/amanahquran/app/ui/TypographyPreviewCardUiTest.kt`

**Implementation:**
- Changed from single ayah (1:2) to multiple ayahs (Al-Fatiha 1:1-4)
- Added script comparison toggle switch
- Shows both IndoPak and Uthmani when comparison enabled
- Added `PREVIEW_AYAH_KEYS` constant for flexibility

**Test Scenarios:**
- [ ] Open Settings - verify preview shows Al-Fatiha 1:1-4
- [ ] Change script - verify preview updates
- [ ] Toggle comparison - verify both scripts shown
- [ ] Change font size - verify preview updates in real-time
- [ ] Change line spacing - verify preview updates
- [ ] Verify preview shows translations when enabled

**Acceptance Criteria:**
- Preview displays 4 ayahs of Al-Fatiha
- Script comparison toggle works correctly
- Preview updates immediately on setting changes
- No performance degradation in Settings screen

---

#### 3.1 Transition Animations
**Files Modified:**
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/reader/ReaderSettingsBottomSheet.kt`

**Implementation:**
- Added `AnimatedContent` wrapper to bottom sheet content
- Added slide-in/slide-out animations on settings change
- Animation duration: 200ms with SizeTransform

**Test Scenarios:**
- [ ] Open reader settings - verify sheet slides up smoothly
- [ ] Change script - verify content animates
- [ ] Change theme - verify content animates
- [ ] Change translation - verify content animates
- [ ] Verify animation doesn't cause layout jumps
- [ ] Test in Elder Mode (slower animations preferred)

**Acceptance Criteria:**
- Settings sheet opens/closes with smooth animation
- Content transitions animate cleanly
- No visual glitches during animations
- Animation speed feels natural

---

### Phase 4: Accessibility

#### 4.1 High-Contrast Mode
**Files Modified:**
- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/theme/ThemeMode.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/theme/AmanahColors.kt`
- `apps/android/app/src/main/kotlin/org/amanahquran/app/core/theme/AmanahQuranTheme.kt`

**Implementation:**
- Added `HIGH_CONTRAST` to `ThemeMode` enum
- Created dedicated color palette with WCAG AAA compliant colors
- Pure black background (#000000) with bright green (#00FF00) and gold (#FFD700) accents
- Added `isHighContrast` property for detection

**Test Scenarios:**
- [ ] Add HIGH_CONTRAST to theme chips in Settings
- [ ] Select high-contrast mode - verify theme applies
- [ ] Verify background is pure black
- [ ] Verify text is high-contrast white/green
- [ ] Verify WCAG AAA contrast ratios
- [ ] Test in reader - verify text is readable
- [ ] Test in home screen - verify all elements visible
- [ ] Verify Elder Mode still works with high-contrast

**Acceptance Criteria:**
- High-contrast theme available in Settings
- All text meets WCAG AAA contrast (≥7:1)
- Pure black background for OLED battery savings
- Accents provide clear visual hierarchy
- No functionality broken in high-contrast mode

---

#### 4.2 TalkBack Semantics
**Files Modified:**
- `apps/android/app/src/main/kotlin/org/amanahquran/app/feature/home/HomeScreen.kt`

**Implementation:**
- Added `heading()` semantic to Continue Reading card
- Added `contentDescription` with full context
- Partial implementation (Continue Reading card only)

**Test Scenarios:**
- [ ] Enable TalkBack on emulator
- [ ] Navigate to home screen - verify heading announced
- [ ] Navigate to Continue Reading - verify full description announced
- [ ] Verify description includes Surah name and position
- [ ] Test in Elder Mode - verify descriptions appropriate

**Acceptance Criteria:**
- Continue Reading card announced as heading
- Full context provided in content description
- Screen reader can navigate card efficiently
- Descriptions are clear and concise

---

## Pending Feature: Bookmark Collections

### Overview
Add ability to organize bookmarks into collections/folders and add notes for study purposes.

### Architecture Plan

#### Database Schema Changes

**New Tables:**

```sql
-- Bookmark Collections Table
CREATE TABLE bookmark_collections (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    description TEXT,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0
);

-- Bookmark Notes Table
CREATE TABLE bookmark_notes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    bookmark_id INTEGER NOT NULL,
    note TEXT NOT NULL,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL,
    FOREIGN KEY (bookmark_id) REFERENCES bookmarks(id) ON DELETE CASCADE
);

-- Update Bookmarks Table
ALTER TABLE bookmarks ADD COLUMN collection_id INTEGER;
ALTER TABLE bookmarks ADD COLUMN FOREIGN KEY (collection_id) REFERENCES bookmark_collections(id) ON DELETE SET NULL;
```

#### Repository Layer Updates

**New Files:**
- `BookmarkCollectionDao.kt` - Database access for collections
- `BookmarkNoteDao.kt` - Database access for notes
- `BookmarkCollectionRepository.kt` - Business logic for collections
- `BookmarkNoteRepository.kt` - Business logic for notes

**Modified Files:**
- `BookmarkDao.kt` - Add collection_id queries
- `BookmarkRepository.kt` - Add collection and note methods

#### UI Components

**New Screens:**
- `BookmarkCollectionsScreen.kt` - List and manage collections
- `BookmarkCollectionDetailScreen.kt` - View bookmarks in a collection
- `BookmarkNoteDialog.kt` - Add/edit note for a bookmark

**Modified Screens:**
- `BookmarksScreen.kt` - Add collection sections, folder organization
- `ReaderSelectedAyahActionCard.kt` - Add "Add to Collection" option

#### State Management

**New ViewModels:**
- `BookmarkCollectionsViewModel.kt`
- `BookmarkCollectionDetailViewModel.kt`

**Modified ViewModels:**
- `BookmarksViewModel.kt` - Add collection filtering

### Implementation Steps

#### Step 1: Database Migration
1. Create database migration class `BookmarkMigrationV3`
2. Add new tables and columns
3. Migrate existing bookmarks to "Default" collection
4. Add version bump in `AmanahContentDatabase`

#### Step 2: Repository Layer
1. Implement DAOs for collections and notes
2. Implement repository classes
3. Add collection and note methods to existing repositories
4. Write unit tests for repositories

#### Step 3: UI Implementation
1. Create collection list screen
2. Create collection detail screen
3. Create note dialog component
4. Update bookmarks screen to show collections
5. Add "Add to Collection" to ayah action bar
6. Implement drag-and-drop for reordering (if time permits)

#### Step 4: Integration
1. Wire up ViewModels with repositories
2. Add navigation routes
3. Update BackupService to include collections and notes
4. Test end-to-end workflows

### Testing Plan for Bookmark Collections

#### Unit Tests
- [ ] `BookmarkCollectionDaoTest` - CRUD operations
- [ ] `BookmarkNoteDaoTest` - CRUD operations
- [ ] `BookmarkCollectionRepositoryTest` - Business logic
- [ ] `BookmarkNoteRepositoryTest` - Business logic
- [ ] Migration test - Verify existing bookmarks preserved

#### UI Tests
- [ ] Create collection - Verify appears in list
- [ ] Rename collection - Verify name updates
- [ ] Delete collection - Verify bookmarks moved to Default
- [ ] Add bookmark to collection - Verify bookmark appears in collection
- [ ] Remove bookmark from collection - Verify moved to Default
- [ ] Add note to bookmark - Verify note persists
- [ ] Edit note - Verify note updates
- [ ] Delete note - Verify note removed
- [ ] Backup/restore - Verify collections and notes included

#### Manual Emulator Tests
- [ ] Create multiple collections
- [ ] Organize bookmarks by Surah/Juz
- [ ] Add study notes to specific ayahs
- [ ] Test collection switching in reader
- [ ] Verify backup includes collections
- [ ] Verify restore restores collections correctly
- [ ] Test with 100+ bookmarks
- [ ] Test in Elder Mode
- [ ] Test in all themes

### Acceptance Criteria
- Users can create, rename, delete collections
- Bookmarks can be assigned to collections
- Notes can be added to bookmarks
- Collections persist across app restarts
- Backup/restore includes collections and notes
- Existing bookmarks migrated to "Default" collection
- UI remains performant with many collections
- Collections feature works offline

### Risks & Mitigations

**Risk 1: Data Loss During Migration**
- Mitigation: Backup existing bookmarks before migration
- Rollback plan if migration fails

**Risk 2: Performance with Many Collections**
- Mitigation: Add indexes on collection_id
- Lazy load collection contents

**Risk 3: UI Complexity**
- Mitigation: Keep UI simple (list-based)
- Defer advanced features (drag-and-drop) to later sprint

---

## Android Emulator Testing Plan

### Emulator Configuration

**Emulator Name:** `quran`
**Purpose:** Dedicated testing for Amanah Quran app
**Target API:** Android 16 (API 36) or latest stable
**RAM:** Minimum 4GB recommended
**Storage:** Minimum 8GB

### Pre-Test Setup

#### 1. Factory Reset Emulator
```bash
# Stop emulator if running
adb -s emulator-5554 emu kill

# Wipe data (factory reset)
adb -s emulator-5554 emu avd root wipe-data

# Reboot emulator
adb -s emulator-5554 emu reboot
```

#### 2. Verify Clean State
```bash
# Check app is not installed
adb -s emulator-5554 shell pm list packages | grep amanahquran

# Should return empty
```

#### 3. Install Latest Build
```bash
# Install debug APK
adb -s emulator-5554 install -r app/build/outputs/apk/debug/app-debug.apk

# Verify installation
adb -s emulator-5554 shell pm list packages | grep amanahquran
```

#### 4. Grant Permissions (if needed)
```bash
# Grant notification permission (for reminders)
adb -s emulator-5554 shell appops set org.amanahquran.app POST_NOTIFICATIONS allow

# Grant storage permission (for backup/restore)
adb -s emulator-5554 shell appops set org.amanahquran.app READ_EXTERNAL_STORAGE allow
adb -s emulator-5554 shell appops set org.amanahquran.app WRITE_EXTERNAL_STORAGE allow
```

### Test Suite Organization

Test suites will be executed in order, with factory reset between major suites:

#### Suite 1: Home Screen & Navigation
**Factory Reset:** Yes
**Features Tested:**
- Continue Reading card with progress bar
- Quick actions grid
- Streak line
- Daily Ayah card
- Navigation between screens

**Test Steps:**
1. Launch app - verify home screen loads
2. Verify Continue Reading card shows progress if last-read exists
3. Tap Continue Reading - verify opens to correct position
4. Test all quick action tiles (Surah, Juz, Page, Search, Bookmarks)
5. Navigate through all main screens
6. Verify Elder Mode toggle
7. Verify theme switching (Light, Dark, Sepia, Black, High-Contrast)

**Expected Duration:** 15 minutes

---

#### Suite 2: Reader Experience
**Factory Reset:** Yes
**Features Tested:**
- Continuous reader with press feedback
- Ayah-by-ayah mode with press feedback
- Translation press feedback
- Ayah action bar animations
- Auto-scroll with progress indicator
- Reader settings bottom sheet animations
- Script switching
- Theme switching in reader

**Test Steps:**
1. Open Surah Al-Fatiha - verify reader loads
2. Tap various verses - verify press feedback
3. Select ayah - verify action bar animates in
4. Bookmark ayah - verify haptic feedback
5. Try different scripts (IndoPak, Uthmani)
6. Try different themes
7. Open reader settings - verify animation
8. Change font size - verify live preview
9. Test auto-scroll - verify progress ring/bar
10. Test in Elder Mode

**Expected Duration:** 20 minutes

---

#### Suite 3: Search Experience
**Factory Reset:** Yes
**Features Tested:**
- Search empty state with popular passages
- Search tips display
- Suggestion chips functionality
- Numeric reference routing

**Test Steps:**
1. Open search screen
2. Verify 10 suggestion chips displayed
3. Tap various chips - verify correct navigation
4. Test numeric references (2:255, 36:1)
5. Verify search tips are readable
6. Test search with Arabic text
7. Test search with Urdu/English translations
8. Verify all themes

**Expected Duration:** 10 minutes

---

#### Suite 4: Settings & Typography
**Factory Reset:** Yes
**Features Tested:**
- Typography preview with multiple ayahs
- Script comparison toggle
- High-contrast mode
- All theme modes
- Elder Mode
- Font size and spacing adjustments

**Test Steps:**
1. Open Settings
2. Verify typography preview shows Al-Fatiha 1:1-4
3. Toggle script comparison - verify both scripts shown
4. Change script - verify preview updates
5. Change font size - verify preview updates
6. Change line spacing - verify preview updates
7. Enable High-Contrast mode - verify theme applies
8. Test all theme modes
9. Enable Elder Mode - verify touch targets enlarge
10. Test reset to defaults

**Expected Duration:** 15 minutes

---

#### Suite 5: Mushaf Page Mode
**Factory Reset:** Yes
**Features Tested:**
- Swipe navigation
- Margin tap navigation
- Page controls
- Bookmarks

**Test Steps:**
1. Open Mushaf page mode
2. Test horizontal swipe (book mode)
3. Test vertical swipe (page mode)
4. Test left/right margin taps
5. Navigate to various pages
6. Test page bookmark toggle
7. Test zoom controls
8. Test fullscreen toggle
9. Verify all themes

**Expected Duration:** 10 minutes

---

#### Suite 6: Accessibility
**Factory Reset:** Yes
**Features Tested:**
- High-contrast mode across all screens
- TalkBack semantics (Continue Reading card)
- Elder Mode
- Font scaling (130% system scale)
- Touch target sizes

**Test Steps:**
1. Enable High-Contrast mode
2. Navigate all screens - verify readable
3. Enable TalkBack
4. Navigate home - verify heading announced
5. Navigate to Continue Reading - verify description announced
6. Enable Elder Mode
7. Enable 130% font scale in system settings
8. Verify no text clipping
9. Verify touch targets are minimum 48dp (56dp in Elder Mode)
10. Test contrast ratios visually

**Expected Duration:** 20 minutes

---

#### Suite 7: Edge Cases & Performance
**Factory Reset:** No (reuse state from previous suite)
**Features Tested:**
- Long Surahs (Al-Baqarah 286 verses)
- Short Surahs (Al-Fatiha 7 verses)
- Longest ayah (2:282)
- Rapid theme switching
- Rapid script switching
- Memory usage during scrolling
- Auto-scroll to end of Surah

**Test Steps:**
1. Open Al-Baqarah - verify smooth scrolling
2. Open Al-Fatah - verify rendering
3. Navigate to 2:282 - verify longest ayah displays
4. Rapidly switch between all themes (10x) - verify no crashes
5. Rapidly switch between scripts (10x) - verify no crashes
6. Start auto-scroll to end of Surah - verify completes
7. Monitor memory usage during scrolling
8. Test with 100+ bookmarks
9. Test backup/restore functionality

**Expected Duration:** 15 minutes

---

### Test Execution Procedures

#### Before Each Test Suite
1. **Factory Reset** (except Suite 7)
2. **Install latest build**
3. **Grant permissions**
4. **Launch app**
5. **Clear app data** (for clean slate):
   ```bash
   adb -s emulator-5554 shell pm clear org.amanahquran.app
   ```

#### During Testing
1. **Take screenshots** of each feature tested
2. **Log bugs** with reproduction steps
3. **Record performance** observations
4. **Test in all themes** (Light, Dark, Sepia, Black, High-Contrast)
5. **Test in Elder Mode** for each feature

#### After Each Test Suite
1. **Collect logs**:
   ```bash
   adb -s emulator-5554 logcat -d > test_suite_X_logs.txt
   ```
2. **Archive screenshots**
3. **Document test results**
4. **Factory reset** before next suite (except Suite 7)

---

## Test Reporting & Acceptance Criteria

### Test Report Template

```markdown
## Test Suite: [Suite Name]
**Date:** [Date]
**Emulator:** quran
**Build Version:** [versionCode versionName]
**Tester:** [Name]

### Summary
- Total Tests: [Number]
- Passed: [Number]
- Failed: [Number]
- Blocked: [Number]

### Results
| Feature | Status | Notes |
|---------|--------|-------|
| [Feature 1] | Pass/Fail | [Notes] |
| [Feature 2] | Pass/Fail | [Notes] |
| ... | ... | ... |

### Issues Found
1. [Issue 1] - [Severity] - [Description]
2. [Issue 2] - [Severity] - [Description]

### Screenshots
[Links to screenshots folder]
```

### Acceptance Criteria for All Features

#### Functional Requirements
- [ ] Feature works as specified in implementation plan
- [ ] No crashes or ANRs during testing
- [ ] Feature works in all supported themes
- [ ] Feature works in Elder Mode
- [ ] Feature works offline (no network required)

#### Performance Requirements
- [ ] Smooth 60fps scrolling in reader
- [ ] <250ms cold start time
- <100ms search execution
- [ ] No UI lag during theme/script switching
- [ ] Memory usage remains reasonable (<500MB during scrolling)

#### Accessibility Requirements
- [ ] Touch targets ≥48dp (56dp in Elder Mode)
- [ ] WCAG AAA contrast ratios in all themes
- [ ] Screen reader announcements are clear
- [ ] No content lost at 130% font scale
- [ ] High-contrast mode fully functional

#### Stability Requirements
- [ ] No crashes across all test suites
- [ ] No ANRs across all test suites
- [ ] No data loss during testing
- [ ] Backup/restore works correctly
- [ ] Settings persist across app restarts

#### Design Requirements
- [ ] Visuals match implementation plan
- [] Animations are smooth and natural
- [ ] No visual glitches or layout jumps
- [ ] Color schemes applied consistently
- [] Typography renders correctly (no clipping, no missing glyphs)

---

## Timeline Estimation

### Total Testing Time
- **Setup (factory reset + install):** 15 minutes per suite
- **Test execution:** 105 minutes total (7 suites × 15 min avg)
- **Reporting:** 30 minutes per suite
- **Total:** ~12 hours of testing

### Recommended Schedule
- **Day 1:** Suites 1-3 (Home, Reader, Search) - 4 hours
- **Day 2:** Suites 4-5 (Settings, Mushaf) - 3 hours
- **Day 3:** Suites 6-7 (Accessibility, Edge Cases) - 5 hours

---

## Success Metrics

### Overall Success Criteria
- **95% of tests pass** (majority of features working)
- **No P0 bugs** (critical crashes, data loss)
- **No regression** in existing functionality
- **All accessibility criteria met**

### Feature-Specific Success Metrics
- **Verse tappability:** Press feedback visible on all interactive text
- **Search suggestions:** 10 chips working, tips displayed
- **Reading progress:** Accurate percentage calculation
- **Action bar animations:** Smooth 200-300ms animations
- **Auto-scroll progress:** Visual indicator updates correctly
- **Typography preview:** 4 ayahs displayed, comparison works
- **Transition animations:** Smooth settings transitions
- **High-contrast mode:** WCAG AAA compliant, fully functional
- **TalkBack semantics:** Continue Reading card properly announced

---

## Risks & Contingencies

### Known Risks
1. **Emulator performance** - older machines may struggle with API 36
2. **Test automation** - manual testing is time-intensive
3. **Feature interactions** - combinations of features may have edge cases

### Contingencies
1. **Latest build** must include all implemented features
2. **Emulator configuration** must meet minimum specs
3. **ADB tools** must be working correctly
4. **Factory reset** must complete successfully

### Mitigation Strategies
1. Use API 34 if API 36 emulator is unstable
2. Prioritize P0 and P1 test cases if time is limited
3. Document all assumptions made during testing
4. Have rollback plan if emulator issues arise

---

## Appendix

### Emulator ADB Commands Reference

```bash
# List connected devices
adb devices

# Install APK
adb -s <device> install -r <apk-path>

# Uninstall app
adb -s <device> uninstall <package-name>

# Clear app data
adb -s <device> shell pm clear <package-name>

# Grant permissions
adb -s <device> shell appops set <package-name> <permission> allow

# Take screenshot
adb -s <device> shell screencap -p /sdcard/screenshot.png
adb -s <device> pull /sdcard/screenshot.png

# Capture logcat
adb -s <device> logcat -d > logs.txt

# Reboot emulator
adb -s <device> reboot

# Factory reset
adb -s <device> emu avd root wipe-data
```

### Screenshot Organization

Create folder structure:
```
/screenshots/
  suite_01_home_navigation/
  suite_02_reader_experience/
  suite_03_search_experience/
  suite_04_settings_typography/
  suite_05_mushaf_page_mode/
  suite_06_accessibility/
  suite_07_edge_cases_performance/
```

### Bug Severity Classification

- **P0 - Critical:** Crash, data loss, security issue
- **P1 - High:** Major feature broken, significant UX degradation
- **P2 - Medium:** Minor feature broken, visual glitch
- **P3 - Low:** Cosmetic issue, nice-to-have improvement

---

## Next Steps After Testing

1. **Review test results** with team
2. **Prioritize bug fixes** based on severity
3. **Create follow-up plan** for any failed tests
4. **Decide on bookmark collections** implementation based on testing outcomes
5. **Document lessons learned** for future sprints

---

**End of Plan**
