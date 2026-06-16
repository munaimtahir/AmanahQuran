# Content Manifest Contract

## Overview
The Content Manifest is the source of truth for all content included in an Amanah Quran release. It provides the metadata required for the Trust Center and for internal validation.

## Manifest Structure
A manifest must include:
- `manifestVersion`: Version of the manifest schema.
- `databaseVersion`: Schema version of the database.
- `generatedAt`: ISO 8601 timestamp.
- `overallChecksum`: Hash of the entire database.
- `validationStatus`: PENDING | PASSED | FAILED.
- `reviewerStatus`: NOT_REVIEWED | REVIEW_PENDING | REVIEWED.
- `sources`: A list of content sources.

## Source Metadata
Each source entry must include:
- `sourceName`
- `sourceUrl`
- `license`
- `version`
- `checksum`
- `importDate`
- `contentType`
- `scriptType` (if applicable)
