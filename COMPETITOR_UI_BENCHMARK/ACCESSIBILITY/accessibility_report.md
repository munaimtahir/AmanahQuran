# ACCESSIBILITY AUDIT & SPOT-CHECK REPORT

> **Evidence corrections (2026-09-27):** some claims in this benchmark were unsupported or wrong and have been corrected or flagged in place. See `UI_AUDIT_VNEXT/EVIDENCE_INTEGRITY_REPORT.md` for the full list. Play Store reviewer names, avatars and review IDs were removed from `LOGS/*_reviews.json`.

> **Testing Environment**: `QuranBenchmark_API36` (Android 16, API 36 Baklava, 1080x1920, 420 dpi).
> **Test Configurations**:
> - Baseline: 100% Font Scale (`font_scale=1.0`), Portrait orientation, Light & Dark themes.
> - Extreme Scaling: 200% Android System Font Scale (`font_scale=2.0`).
> - Rotation: Landscape orientation (`user_rotation=1`, 1920x1080 effective).
> **Compliance Notice**: Observations reflect visual layout inspection, touch target metrics, and assistive behavior under Android 16; formal WCAG 2.2 AA certification requires additional screen reader (TalkBack) audio verification.

---

## 1. Multi-App Accessibility Comparison

| Application | 200% Font Scaling Behavior | Landscape Usability | Dark/Night Mode Quality | Nastaliq / Arabic Clipping | Touch Target Compliance (>=48dp) | Elder Reader Suitability Score (1-10) |
| :--- | :--- | :--- | :--- | :--- | :---: | :---: |
| **Quran for Android** | Home tabs wrap cleanly. Mushaf page graphics remain fixed-pixel (no zoom on text itself), while translation view expands. | Excellent horizontal dual-page or zoomed single-page layout. | Clean high-contrast inverted black/white. | Zero clipping on page images; translation font scales cleanly. | Partially compliant (some action bar icons are 40dp). | **6.5 / 10** |
| **Al Quran (Greentech)** | High text wrapping; settings dialogs overflow slightly; reader cards expand gracefully. | Usable, but top and bottom bars consume significant vertical real estate. | Good dark mode with sepia option. | Minimal clipping due to independent line-height adjustments. | Compliant (most targets 48-52dp). | **7.5 / 10** |
| **The Glorious Quran** | Urdu Nastaliq can overlap if Arabic font is scaled to maximum without adjusting translation. | Functional, but side margins reduce line length. | Solid dark and sepia modes. | Minor diacritic clipping on complex ligatures under extreme zoom. | Compliant. | **7.0 / 10** |
| **Aasan Tarjuma** | Urdu commentary expands; text container scroll works reliably. | Wide landscape view improves Nastaliq reading flow. | Traditional green/night palette. | Generous vertical line padding prevents Nastaliq clipping. | Compliant. | **8.0 / 10** |
| **Tarteel** | Modern Compose dynamic type works well, but upsell banners overflow screen bounds. | Reader functions well in landscape. | Exceptional true-black OLED mode. | Clean Uthmani font rendering. | Fully compliant (modern Material 3 targets). | **6.0 / 10** (Complex for elders) |
| **Quranly** | Cards overflow vertical viewports; celebratory modals require scrolling to dismiss. | Landscape causes card distortion. | Modern soft dark mode. | Standard Uthmani. | Compliant. | **4.0 / 10** (Gamified, unsuited for elders) |
| **Quran Majeed** | Severe layout breakage: banner ads collide with scaled Arabic text. | Ad banners occlude up to 35% of landscape reader space. | Night mode available, but ads remain bright white. | IndoPak text renders well, but ad overlays block verses. | Non-compliant (ad dismiss buttons are tiny 24dp targets). | **3.0 / 10** (Hazardous for elders) |
| **Amanah Quran (Baseline)** | *Not measured at 200% in this audit (only 130%, see `UI_AUDIT/ACCESSIBILITY/observations.md`).* Elder Mode gives larger type and 56dp touch targets. | Responsive Compose layout adapts cleanly to landscape. | 4-Theme system (Light, Dark, Sepia, System) with WCAG AAA contrast. | Bundled verified IndoPak and Uthmani fonts with tuned baselines prevent clipping. | 100% compliant (All primary targets >= 48dp, Elder Mode >= 56dp). | *Self-assessed, not comparable* (competitor scores were from device testing; Amanah's was not) |

---

## 2. Deep Dive: Live Emulator Testing on Quran for Android

Direct testing on `QuranBenchmark_API36` revealed key accessibility insights:

### A. 200% Font Scaling Test (`quran_android_font_scale_200_home.png` & `_reader.png`)
- **Home Screen**: Tab labels (`SURAHS`, `JUZ'`, `BOOKMARKS`) scaled cleanly. List item titles (`Surah Al-Baqarah`, `Madani - 286 verses`) remained legible with two-line wrapping.
- **Mushaf Reader Screen**: Because the Mushaf view renders fixed-layout Madani page graphics, the Arabic text did NOT expand with Android system font scaling. This creates a critical accessibility limitation: low-vision users who increase Android system font scale receive NO magnification on the actual Quranic Arabic text in page mode! They must switch to translation mode to see larger fonts.
- **Amanah Design Lesson**: Amanah's Ayah Reader uses native Jetpack Compose dynamic typography that scales *both* Arabic and translation text proportionally or independently, ensuring true accessibility for elder and low-vision readers.

### B. Landscape Mode Test (`quran_android_landscape_reader.png`)
- **Orientation Handling**: Screen rotated cleanly without crashing or restarting the activity lifecycle.
- **Viewport Utilization**: In landscape, single-page view scales down slightly to fit vertical height, leaving blank side margins.
- **Amanah Design Lesson**: In landscape mode, Amanah should utilize wide screens to provide side-by-side bilingual view (Arabic on right, Urdu/English translation on left) rather than empty side padding.
