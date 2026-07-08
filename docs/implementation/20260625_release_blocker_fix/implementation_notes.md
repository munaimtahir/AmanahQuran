# Release Blocker Fix Implementation Notes

## Scope

This sprint changes navigation, reader presentation state, local search
metadata, Trust Center release gating, settings layout, and Android window
integration. Quran display text and canonical numbering are unchanged.

## P0 Fixes

### Exact Ayah Navigation

- Added canonical `ReaderAnchor`.
- Added a distinct `reader/ayah/{ayahKey}` route.
- Added DAO/repository ayah-reference resolution.
- Exact ayah anchors load the containing Surah and compute the structural
  `LazyColumn` index.
- The reader scrolls to and highlights the target once content is ready.
- Search and bookmark destinations use pure canonical anchor mappers.

### Continue Reading

- Continue Reading routes to `ExactAyah(lastRead.ayahKey)` through the standard
  reader.
- Home page opening also uses the standard page reader.
- The prototype Mushaf line-generation path is no longer used by these V1
  entry points.
- Debug timing logs cover continue click, cached last-read/settings resolution,
  route start, reader load, DB query, block build, first composition, and
  anchor visibility.

### Trust Center

- Contradictory hard-coded approval metadata was removed.
- Debug/internal builds display blocked internal-test status.
- The IndoPak source is labeled as Quran display text rather than search
  normalization.
- `validateReleaseContent` fails when checksum, verification, manual review,
  source metadata, or approval requirements are incomplete.
- Release packaging tasks depend on that gate.

## P1/P2 Fixes

- Added common Surah aliases including Yaseen/Yasin/Ya-Sin.
- Added an in-reader IndoPak/Uthmani switch that preserves canonical state.
- Theme controls wrap in Elder Mode, keeping Sepia reachable.
- Enabled AndroidX edge-to-edge with transparent system bar styles.
- Removed the unused Mushaf footer parameter and unused test destructuring.

## Release Safety

`validateReleaseContent` is expected to fail for the current internal build
because prototype layout verification and signed manual review evidence are
still pending. That failure is intentional and prevents a false production
claim.
