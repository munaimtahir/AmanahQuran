# UI Inventory — Design System Components

Amanah Quran implements a bespoke, dignified design system built on Jetpack Compose Material 3, tailored for sacred Islamic literature reading.

### 1. Cards & Containers
- `AmanahCard`: Standard elevated surface container with rounded corners (16dp radius), gentle border stroke (`#E0D6C8` in Light), and soft parchment background.
- `AmanahSectionCard`: Grouped card with internal vertical rhythm and integrated section semantics.
- `AmanahSectionHeader`: Distinctive category header using bold, muted typography to segment settings and browsing modes.

### 2. Selection & Control Elements
- `AmanahScriptChip`: High-accessibility rounded pill chips used for selecting Scripts (`IndoPak`, `Uthmani`), Reading Modes (`Continuous`, `Ayah`), and Themes (`System`, `Light`, `Dark`, `Sepia`). Displays gold-accented active border and tinted background when selected.
- `AmanahSlider`: Refined continuous slider for Arabic font size and autoscroll velocity tuning.
- `AmanahDivider`: Subtle horizontal separator maintaining visual structure between list items without visual noise.

### 3. Sacred Typography Components
- `QuranText / MushafLine`: Dedicated typography composable that renders verified Quranic glyphs using bundled OpenType fonts:
  - `digital_khatt_indopak.otf` (IndoPak script)
  - `digital_khatt_v2.otf` (Uthmani script)
  - `indopak_nastaleeq.ttf` (Nastaleeq fallback)
- End-of-Ayah Glyph (۝): Integrated canonical verse marker enclosing the Eastern Arabic verse numeral.

### 4. Interactive Action Surfaces
- `ReaderSelectedAyahActionCard`: Transient bottom card that elevates upon tapping any verse in the reader. Contains verse key, Bookmark toggle button (`Icons.Rounded.Bookmark` / `BookmarkBorder`), and Overflow menu (`Share as text`).
- `PageBookmarkRow`: Dedicated header row on Mushaf page views allowing one-tap whole-page bookmarking.
- `TopAppBar`: Standardized Material 3 top app bar with unified back navigation icon, title styling, and context actions.
