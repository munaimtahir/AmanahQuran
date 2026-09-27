# Amanah Quran — UI/UX Evidence Collection Audit

Welcome to the **Amanah Quran (v2.2.0)** UI/UX Evidence Pack.

This directory contains a complete visual and diagnostic evidence archive compiled by running the latest build of the application on the locally installed `AdForge_API_36` Android emulator.

The evidence is structured to enable independent reviewers to thoroughly evaluate visual hierarchy, typographical rendering, interaction patterns, accessibility, theming, and edge-case behavior without access to a running emulator.

---

## Recommended Review Order
For a comprehensive and structured external review, proceed in this order:

1. [`AUDIT_MANIFEST.md`](AUDIT_MANIFEST.md) — Exact build, environment, and coverage summary.
2. [`UI_INVENTORY/screens.md`](UI_INVENTORY/screens.md) — Exhaustive catalog of all 21 discovered screens.
3. [`UI_INVENTORY/navigation_map.md`](UI_INVENTORY/navigation_map.md) — Mermaid diagram and routing patterns.
4. [`WORKFLOWS/workflow_inventory.md`](WORKFLOWS/workflow_inventory.md) — Major workflow matrix and video references.
5. [`FINDINGS/evidence_index.md`](FINDINGS/evidence_index.md) — Complete cross-referenced table of all 58 screenshots and 6 recordings.
6. [`SCREENSHOTS/`](SCREENSHOTS/) — Visual captures organized into 14 thematic folders.
7. [`VIDEOS/`](VIDEOS/) — Concise MP4 screen recordings of key workflows.
8. [`FINDINGS/raw_observations.md`](FINDINGS/raw_observations.md) — Structured usability observations (O001–O011).
9. [`ACCESSIBILITY/observations.md`](ACCESSIBILITY/observations.md) — Touch targets, contrast, 130% font scale, and RTL analysis.
10. [`SOURCE_CONTEXT/relevant_ui_files.md`](SOURCE_CONTEXT/relevant_ui_files.md) — Code-level source mapping for engineering handoff.

---

## Directory Structure
```text
UI_AUDIT/
├── README.md                           # External reviewer guide (this document)
├── AUDIT_MANIFEST.md                   # Full audit manifest and scope confirmation
├── ENVIRONMENT.md                      # Host, build, and device configuration details
├── BUILD/                              # Build summary, aapt badging info, and build logs
├── DEVICE/                             # Emulator props, normalized display settings, reset logs
├── SCREENSHOTS/                         # 58 full-resolution PNG captures
│   ├── 01_launch/                      # Splash and initial loaded transitions
│   ├── 02_onboarding/                  # Architectural note (zero-onboarding design)
│   ├── 03_home/                        # Home screen default, scrolled, and Dark theme
│   ├── 04_quran_navigation/            # Surah index, Juz index, Page index
│   ├── 05_reading/                     # Surah reader, Mushaf page, ayah action cards, themes
│   ├── 06_search/                      # Offline search: empty, no-results, multi-results, dark
│   ├── 07_bookmarks_favourites/        # Bookmark empty, item added, jump to verse, removed
│   ├── 08_audio/                       # V1 scope explanation (audio excluded)
│   ├── 09_tafsir_translation/          # V1 scope explanation (sacred reader focus)
│   ├── 10_settings/                    # Script selection, theme chips, Elder Mode, resets
│   ├── 11_secondary_features/          # Trust Center, Streak, Calendar, Daily Ayah History
│   ├── 12_empty_error_loading_states/  # Empty search, empty bookmarks
│   ├── 13_long_content_edge_cases/     # Short/long Surahs, longest ayah (2:282), IndoPak vs Uthmani
│   └── 14_misc/                        # Accessibility 130% font scaling, landscape rotation
├── VIDEOS/                             # 6 MP4 workflow screen recordings
│   ├── workflow_01_first_launch/       # Cold start to landing
│   ├── workflow_02_core_reading/       # Surah, Juz, Page discovery and reading
│   ├── workflow_03_search/             # Query execution, no-results, exact verse routing
│   ├── workflow_04_audio/              # Scope note
│   ├── workflow_05_bookmark/           # Verse bookmark toggle and persistence
│   ├── workflow_06_settings/           # Script, theme, Elder Mode, and reading settings
│   └── workflow_misc/                  # Trust Center, Streak, Calendar, History
├── WORKFLOWS/                          # Inventory, observations, and interaction issues
├── UI_INVENTORY/                       # Screen catalog, navigation map, components, states
├── LOGS/                               # Concise app logcat, crash verification, perf logs
├── ACCESSIBILITY/                      # Touch targets, contrast ratios, font scale, RTL
├── FINDINGS/                           # Raw observations, visual consistency, evidence index
└── SOURCE_CONTEXT/                     # UI source mappings and architectural notes
```

---

## Key Findings Snapshot
- **Textual & Visual Integrity**: 100% verified Quranic glyphs rendered via bundled IndoPak and Uthmani fonts. Zero missing glyphs (tofu) or overlapping diacritics observed.
- **Privacy & Offline Independence**: 100% offline functionality. Zero internet permissions requested, zero tracking or advertising SDKs.
- **Fast Performance**: Sub-250ms warm launch, <100ms offline search execution, steady 60fps scrolling through 286-verse Surah Al-Baqarah.
- **Zero Stability Defects**: 0 crashes, 0 ANRs, 0 layout exceptions recorded across the entire audit session.
