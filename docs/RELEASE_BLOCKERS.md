# Release Blockers checklist

The following issues are release-blocking and must be resolved before any public release:

## 1. Quran Content & Verification safety

- [ ] **Unverified line data in production:** Unverified prototype line mapping data is currently stored. A verified production layout dataset must be imported before release.
- [ ] **Trust Center false verification claims:** Trust Center metadata must not claim full verification status until manual scholar review is completed.

## 2. Rendering & UI Glitches

- [ ] **Missing glyph fallback boxes:** System font fallback must not display empty fallback boxes for Quranic symbols.
- [ ] **Duplicate ayah markers:** Visual duplication of ayah indicators/numbers is prohibited.
- [ ] **Accidental underlines:** Ensure no underlines or text decorations appear on Quran text.
- [ ] **Mushaf line wrapping:** Lines must not wrap or auto-scroll horizontally.

## 3. Core navigation integrity

- [ ] **Continue Reading failure:** If Continue Reading crashes or fails to restore the page, the build must block.
- [ ] **Bookmark persistence failure:** Adding or removing bookmarks must persist across app process restarts.
- [ ] **Network requests:** Any attempt to perform network requests for telemetry, ads, or analytics will block the release.
