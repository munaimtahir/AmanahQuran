# Amanah Quran — Active Reader Font License/Source Evidence Download Summary

## Purpose

This folder stores source pages, font downloads, and license/terms evidence for the active Quran reader fonts.

Final active reader fonts to verify:
1. IndoPak display: `indopak_nastaleeq.ttf`
2. Uthmani display: `digital_khatt_v2.otf`

## Evidence Downloaded

### IndoPak Nastaleeq

- QUL Indopak Nastaleeq font page:
  - source_pages/QUL_INDOPAK_NASTALEEQ_FONT_PAGE.html
- Downloaded TTF:
  - fonts/Indopak_Nastaleeq_Hanafi_With_Waqf_Lazmi.ttf

### KFGQPC Nastaleeq candidate

- QUL KFGQPC Nastaleeq page:
  - source_pages/QUL_KFGQPC_NASTALEEQ_FONT_PAGE.html
- Downloaded TTF:
  - fonts/KFGQPCNastaleeq-Regular.ttf

### Uthmani Hafs2 / QPC V2 candidate

- QUL QPC V2 page:
  - source_pages/QUL_QPC_V2_FONT_PAGE.html
- Downloaded TTF:
  - fonts/QPC_V2_Hafs.ttf

### DigitalKhatt V2 final active Uthmani font

- QUL DigitalKhatt V2 page:
  - source_pages/QUL_DIGITAL_KHATT_V2_FONT_PAGE.html
- Downloaded OTF:
  - fonts/DigitalKhattV2.otf

### General QUL evidence

- QUL FAQ/resource notice
- QUL all fonts page
- QUL repository MIT license

### Restrictive KFGQPC evidence

- KFGQPC Uthmanic Script Hafs license notice from ScanCode
- Third-party KFGQPC Hafs Uthmanic Script license page

## Important Decision Rule

Do not mark any active font CLEARED unless:

1. the exact bundled Android font file checksum matches a downloaded evidence font file, and
2. the matching source/license evidence allows bundling/distribution inside an Android APK.

If the active font is a KFGQPC/Quran Complex font and the only available license says permission is required for reproduction/modification, keep it BLOCKING until written permission is obtained.

## Next Required Step

Compare:
- ACTIVE_READER_FONT_LICENSE_EVIDENCE_SHA256SUMS.txt
- CURRENT_ANDROID_FONT_SHA256SUMS.txt

Then update:
- docs/legal/CONTENT_AND_FONT_LICENSE_MANIFEST.md
- docs/legal/FINAL_LICENSE_CLEARANCE_DECISION.md
