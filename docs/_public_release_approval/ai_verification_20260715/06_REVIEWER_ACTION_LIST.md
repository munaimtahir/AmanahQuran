# Scholar Reviewer Action List

**Date**: 2026-07-15
**Database Reference**: `apps/android/app/src/main/assets/database/quran.db`

This action list guides the human scholar/reviewer through the steps needed to approve the database for public release.

## Action Checklist for Reviewer

### 1. Database Integrity Verification
- [ ] Inspect the AI verification report files under `docs/_public_release_approval/ai_verification_20260715/`.
- [ ] Confirm that all structural tests, diff validations, and font audits have passed.

### 2. Manual Mushaf Visual Review
- [ ] Install the candidate build on a target device.
- [ ] Review Surah Al-Fatihah, Surah Al-Baqarah opening, and the Juz boundaries as shown in screenshots.
- [ ] Validate IndoPak and Uthmani script rendering. Verify that no glyph formatting errors are visible.
- [ ] Verify that offline features (navigation, script switching, offline search, settings) function without network connections.

### 3. Signing the Approval Template
- [ ] Open the sign-off template at [08_REVIEWER_SIGN_OFF_TEMPLATE.md](file:///home/munaim/Documents/github/AmanahQuran/docs/_public_release_approval/quran_db_review_20260715/08_REVIEWER_SIGN_OFF_TEMPLATE.md).
- [ ] Fill in reviewer details, institutions, and signature fields.
- [ ] Commit the signed file to the repository.

### 4. Updating gates and manifests
- [ ] Once the signed review is committed, update `trust_center_content.json` to mark the database and font reviews as `GO` / `PASSED`.
- [ ] Update release gate documents to remove launch blockers.

---
**Status**: **PENDING REVIEWER ACTIONS**
