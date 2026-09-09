# Visual Consistency Observations

This report reviews the visual harmony and UI cohesion of **Amanah Quran (v2.2.0)**.

### 1. Spacing & Grid Alignment
- **Spacing Token Consistency**: The application consistently utilizes standard `AmanahSpacing` tokens (`xs=4dp`, `sm=8dp`, `md=16dp`, `lg=24dp`, `xl=32dp`).
- **Margins & Gutters**: Screen horizontal gutters are uniform (16dp in default mode, 24dp in Elder Mode) across Home, Surah List, Bookmarks, and Settings.
- **Card Margins**: All cards adhere to the same horizontal inset, creating a neat vertical column.

### 2. Card Radii & Elevation
- **Corner Radii**: Rounded corners on `AmanahCard`, `AmanahSectionCard`, and list cards consistently measure 16dp.
- **Elevation**: Flat to minimal elevation (1dp to 2dp) with subtle borders (`#E0D6C8`) is applied uniformly across the app, avoiding jarring shadows or conflicting elevations.

### 3. Iconography & Symbol Styles
- **Style**: Standardized on Google Material Rounded icons (`Icons.Rounded.*` and `Icons.AutoMirrored.Rounded.*`).
- **Tints**: Icons dynamically respect `MaterialTheme.colorScheme.onSurfaceVariant` or `AmanahGoldMuted` when active, ensuring no mismatched hardcoded colors.

### 4. Typography Hierarchy
- **Arabic Text**: Styled with dignified OpenType fonts with proportional line heights to prevent diacritic clipping.
- **Latin Headings**: Material 3 `titleLarge` and `titleMedium` establish clear hierarchy without clashing with Arabic calligraphy.
- **Secondary Metadata**: Subdued tones (`onSurfaceVariant`) ensure verse numbers and revelation classifications remain secondary to the sacred text.

### 5. Color Palette & Theming
- **Light**: Warm parchment background reminiscent of high-grade paper Mushafs.
- **Dark**: Deep slate/charcoal background preventing OLED eye strain.
- **Sepia**: Warm ambient amber tone designed for reading in soft light.
- **No Visual Glitches**: When switching themes, all cards, borders, texts, and icons invert cleanly in a single frame.
