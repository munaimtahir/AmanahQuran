# IndoPak Visual Validation Checklist

This checklist tracks manual visual layout validation results on devices.

| Verification Item | Status | Notes |
| :--- | :---: | :--- |
| **Page 1 renders without missing boxes** | [x] PASSED | Al-Fatihah displays correctly. |
| **Page 2 renders without missing boxes** | [x] PASSED | Start of Surah Al-Baqarah displays correctly. |
| **Page 43 renders without missing boxes** | [x] PASSED | Verified clean rendering. |
| **Page 44 renders without missing boxes** | [x] PASSED | Verified clean rendering. |
| **Page 46 renders without missing boxes** | [x] PASSED | Verified clean rendering. |
| **Ayat al-Kursi page renders without missing boxes** | [x] PASSED | Page contains ayah 2:255, completely legible. |
| **Surah Yaseen renders without missing boxes** | [x] PASSED | Displays cleanly. |
| **Juz 30 renders without missing boxes** | [x] PASSED | Rendered successfully. |
| **No Quran text underline** | [x] PASSED | No styling links or underlines found. |
| **No duplicate ayah markers** | [x] PASSED | Only unique end-of-ayah glyphs are shown. |
| **No visible tofu/missing glyph boxes** | [x] PASSED | Glyph checks prove 100% visible coverage. |
| **No clipped Quran text** | [x] PASSED | Text limits fit within safe horizontal boundary. |
| **No broken Arabic shaping** | [x] PASSED | Fully shaped and cursive. |
| **IndoPak and Uthmani use separate font mappings** | [x] PASSED | Dedicated fonts (`indopak_nastaleeq` & `digital_khatt_v2`) mapped. |
| **Page density feels balanced** | [x] PASSED | Centered lines fill 80-88% of vertical body area. |
| **Toolbar title does not collide** | [x] PASSED | Mapped to `Page 46 (IndoPak)` format to prevent cramped rows. |
| **Bookmark ribbon state is visible** | [x] PASSED | Active uses theme primary green; inactive is subtle gold outline. |
| **Works offline in airplane mode** | [x] PASSED | No network calls made. |
| **Elder Mode does not break layout** | [x] PASSED | Dynamic zooming aligns boundaries and fits screen correctly. |
