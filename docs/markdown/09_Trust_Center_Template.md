# Trust Center Template
# Amanah Quran - V1 Sacred Reader MVP

## 1. Trust Center Purpose

Trust Center gives users transparent access to Quran text sources, verification status, checksums, app integrity information, and the privacy pledge. It should be available offline and should not require login.

## 2. Trust Summary

Suggested app-facing summary:

> Amanah Quran is built to provide verified offline Quran reading without ads, tracking, login, or monetization. Quran text sources, versions, checksums, and validation details are shown here for transparency.

## 3. No Modification Statement

Required app-facing statement:

> Amanah Quran displays Quranic text exactly as imported from verified source data. Search normalization is stored separately and is never used as display Quran text.

## 4. Privacy Pledge

Required app-facing bullets:

1. No ads.
2. No tracking.
3. No analytics SDK in Version 1.
4. No advertising ID use.
5. No forced login.
6. No selling or sharing user data.
7. Offline reading works without internet.
8. Bookmarks and last-read position remain stored locally on your device.

## 5. Quran Text Source Card

Use one card per script pack.

| Field | Value |
|---|---|
| Script | IndoPak / Uthmani |
| Source name | To be completed |
| Source URL | To be completed |
| Source version | To be completed |
| License | To be completed |
| Import date | To be completed |
| Pack checksum | To be completed |
| Ayah count | 6236 |
| Surah count | 114 |
| Validation status | Pending / Passed / Failed |
| Manual review status | Pending / Reviewed / Approved |

## 6. Verification Status Card

| Check | Expected | Actual | Status |
|---|---|---|---|
| Surah count | 114 | To be completed | Pending |
| Ayah count | 6236 | To be completed | Pending |
| Duplicate ayah keys | 0 | To be completed | Pending |
| Missing IndoPak text | 0 | To be completed | Pending |
| Missing Uthmani text | 0 | To be completed | Pending |
| Missing page mapping | 0 | To be completed | Pending |
| Missing Juz mapping | 0 | To be completed | Pending |
| Search index count | 6236 | To be completed | Pending |
| Checksum verification | Match | To be completed | Pending |

## 7. App Integrity Card

| Field | Value |
|---|---|
| App version | 1.0.0 |
| Quran database version | To be completed |
| Content manifest version | To be completed |
| Build date | To be completed |
| Release channel | Internal / Closed / Public |
| Source code/license page | To be completed if applicable |

## 8. Source Attribution Text Template

> Quran text source: [SOURCE NAME], version [VERSION]. Used under [LICENSE]. Pack checksum: [CHECKSUM]. Imported on [DATE]. Validation status: [STATUS].

## 9. Manual Review Text Template

> Manual content review status: [PENDING / REVIEWED / APPROVED]. Reviewer notes: [NOTES]. This release should not be published publicly until manual review is approved.

## 10. Trust Center Release Gate

Trust Center is complete only when:

1. Both IndoPak and Uthmani source cards are filled.
2. All required license fields are complete.
3. Checksums are shown.
4. Validation status is passed.
5. Manual review status is not pending for public release.
6. Privacy pledge is shown.
7. No-modification statement is shown.
8. App/database/content manifest versions are shown.
