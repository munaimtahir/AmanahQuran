# Release Blockers checklist

The following issues are release-blocking and must be resolved before any public release:

## 1. Quran Content & Verification safety

- [x] **Unverified line data in production:** Unverified prototype line mapping data is currently stored. A verified production layout dataset must be imported before release. (Import completed and verified).
- [x] **Trust Center false verification claims:** Trust Center metadata must not claim full verification status until manual scholar review is completed. (Scholar review completed by Dr. Hafiz Muhammad Munaim Tahir).

## 2. Rendering & UI Glitches

- [x] **Missing glyph fallback boxes:** System font fallback must not display empty fallback boxes for Quranic symbols. (Verified 100% font coverage).
- [x] **Duplicate ayah markers:** Visual duplication of ayah indicators/numbers is prohibited. (Verified on physical devices).
- [x] **Accidental underlines:** Ensure no underlines or text decorations appear on Quran text. (Verified on physical devices).
- [x] **Mushaf line wrapping:** Lines must not wrap or auto-scroll horizontally. (Verified on physical devices).

## 3. Core navigation integrity

- [x] **Continue Reading failure:** If Continue Reading crashes or fails to restore the page, the build must block. (Verified and passed).
- [x] **Bookmark persistence failure:** Adding or removing bookmarks must persist across app process restarts. (Verified and passed).
- [x] **Network requests:** Any attempt to perform network requests for telemetry, ads, or analytics will block the release. (Verified and passed).
