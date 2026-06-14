# Tools

Development tools for Amanah Quran.

## Folders

- `content-import`: scripts for importing verified source content into local database artifacts.
- `validation`: scripts for validating content integrity, counts, checksums, and manifests.
- `release`: scripts/checklists for release preparation.

Rules:

- Tools may run during development.
- The Android app must not depend on network at runtime for core V1.
- Tools must preserve Quran display text exactly as verified source data.
