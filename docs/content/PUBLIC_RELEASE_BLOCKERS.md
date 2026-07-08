# Public Release Blockers

Status: public release blocked.

Current status from focused post-fix verification:

- Search result `2:255` now opens `2:255` on the physical device.
- Bookmark `2:255` now opens `2:255` on the physical device.
- Continue Reading to `2:255` opened `2:255` on the physical device.

Remaining public-release blockers:

- A full fresh post-fix physical-device regression folder is still required.
- The original Continue Reading Page 540/Uthmani scenario must be re-tested.
- Trust Center/release metadata and signed review evidence must remain conservative
  until complete.

Required before production:

- Re-test exact ayah anchor navigation in the full release suite.
- Re-test Continue Reading script-state initialization and restore timing.
- Replace placeholder/contradictory Trust Center verification metadata.
- Archive signed human/scholarly review evidence with reviewer identity, scope,
  scripts, decision, date, and evidence reference.
- Pass release content validation.
- Pass a fresh physical-device regression run with non-empty logs/screenshots for
  exact ayah anchors, Continue Reading, script switching, offline behavior, and
  Juz boundaries.
