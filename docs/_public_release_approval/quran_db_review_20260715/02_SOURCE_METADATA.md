# Source Metadata Evidence Proof

**Date**: 2026-07-15
**Manifest Reference**: `apps/android/app/src/main/assets/trust/trust_center_content.json`

This document details the original sources of all text content and assets packaged in the MVP release. This metadata is verified against the database and the source registry.

## Verified Content Sources

### 1. Quranic Arabic Text - Uthmani Script
- **Role**: Quran display text for Uthmani script view.
- **Source Name**: Tanzil Uthmani XML
- **Raw File Path**: `sourcedata/1/quran-uthmani.xml`
- **Source URL**: [https://tanzil.net/download/](https://tanzil.net/download/)
- **License**: Creative Commons Attribution 3.0
- **License Details**: [https://tanzil.net/docs/text_license](https://tanzil.net/docs/text_license)
- **Import Verification**: Stored as exact Quran display text; absolutely no runtime modification is performed.

### 2. Quranic Arabic Text - IndoPak Script
- **Role**: Quran display text for IndoPak script view.
- **Source Name**: QUL Digital Khatt IndoPak
- **Raw File Path**: `sourcedata/3/digital-khatt-indopak-ayah-by-ayah-script.json.zip`
- **Source URL**: [https://qul.tarteel.ai/resources/quran-script/59](https://qul.tarteel.ai/resources/quran-script/59)
- **License**: Reviewed upstream font project
- **License Details**: [https://github.com/DigitalKhatt/indopakfont/blob/main/LICENSE](https://github.com/DigitalKhatt/indopakfont/blob/main/LICENSE)
- **Import Verification**: Map matches verified upstream source. Note that IndoPak public-release source attribution must be confirmed by manual reviewer.

### 3. Search-Normalization Source
- **Role**: Arabic normalization and indexing for offline search (Simple Clean text).
- **Source Name**: Tanzil Simple Clean XML
- **Raw File Path**: `sourcedata/2/quran-simple-clean.xml`
- **Source URL**: [https://tanzil.net/download/](https://tanzil.net/download/)
- **License**: Creative Commons Attribution 3.0
- **License Details**: [https://tanzil.net/docs/text_license](https://tanzil.net/docs/text_license)
- **Import Verification**: Stored in a separate table (`search_index`) and never represented or rendered as display Quran text.

## Font Asset References

- **DigitalKhatt IndoPak Font (`digital_khatt_indopak.otf`)**:
  - **Source URL**: [https://github.com/DigitalKhatt/indopakfont](https://github.com/DigitalKhatt/indopakfont)
  - **License**: SIL Open Font License 1.1 (OFL-1.1)
  - **License Details**: [https://github.com/DigitalKhatt/indopakfont/blob/main/LICENSE](https://github.com/DigitalKhatt/indopakfont/blob/main/LICENSE)
  
- **DigitalKhatt V2 Font (`digital_khatt_v2.otf`)**:
  - **Source URL**: [https://github.com/DigitalKhatt/indopakfont](https://github.com/DigitalKhatt/indopakfont)
  - **License**: SIL Open Font License 1.1 (OFL-1.1)

- **IndoPak Nastaleeq Font (`indopak_nastaleeq.ttf`)**:
  - **Source Name**: QUL IndoPak Nastaleeq Font
  - **Source URL**: [https://qul.tarteel.ai/](https://qul.tarteel.ai/)
  - **License**: SIL Open Font License 1.1 (OFL-1.1)

---
**Status**: verified by automated source registry cross-checks.
