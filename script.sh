#!/usr/bin/env bash
set -Eeuo pipefail

REPO_ROOT="${1:-$(pwd)}"
cd "$REPO_ROOT"

OUT="docs/legal/evidence/fonts/active_reader_font_licenses"
mkdir -p "$OUT/source_pages" "$OUT/fonts" "$OUT/licenses"

log="$OUT/download_log.txt"
: > "$log"

download() {
  local url="$1"
  local dest="$2"

  echo "Downloading: $url -> $dest" | tee -a "$log"
  if curl -L --fail --connect-timeout 20 --max-time 90 "$url" -o "$dest"; then
    echo "OK: $dest" | tee -a "$log"
  else
    echo "FAILED: $url" | tee -a "$log"
    rm -f "$dest"
  fi
}

echo "Amanah Quran active reader font license/source download"
echo "Repo: $REPO_ROOT"
echo "Output: $OUT"
echo

# -----------------------------
# IndoPak Nastaleeq active font
# -----------------------------

download \
  "https://qul.tarteel.ai/resources/font/242" \
  "$OUT/source_pages/QUL_INDOPAK_NASTALEEQ_FONT_PAGE.html"

download \
  "https://static-cdn.tarteel.ai/qul/fonts/nastaleeq/Hanafi/normal-v4.2.2/with-waqf-lazmi/font.ttf" \
  "$OUT/fonts/Indopak_Nastaleeq_Hanafi_With_Waqf_Lazmi.ttf"

# Also collect KFGQPC Nastaleeq because your sourceData previously had KFGQPCNastaleeq-Regular.ttf
download \
  "https://qul.tarteel.ai/resources/font/462" \
  "$OUT/source_pages/QUL_KFGQPC_NASTALEEQ_FONT_PAGE.html"

download \
  "https://static-cdn.tarteel.ai/qul/fonts/nastaleeq/KFGQPCNastaleeq-Regular.ttf" \
  "$OUT/fonts/KFGQPCNastaleeq-Regular.ttf"

# -----------------------------
# Uthmani Hafs2 / V2 candidates
# -----------------------------

# QPC V2 / Hafs / glyph-based page-by-page font
download \
  "https://qul.tarteel.ai/resources/font/249" \
  "$OUT/source_pages/QUL_QPC_V2_FONT_PAGE.html"

download \
  "https://static-cdn.tarteel.ai/qul/fonts/quran_fonts/v2/ttf.ttf" \
  "$OUT/fonts/QPC_V2_Hafs.ttf"

# DigitalKhatt V2 Uthmani-compatible candidate
download \
  "https://qul.tarteel.ai/resources/font/247" \
  "$OUT/source_pages/QUL_DIGITAL_KHATT_V2_FONT_PAGE.html"

download \
  "https://static-cdn.tarteel.ai/qul/fonts/dk/DigitalKhattV2.otf" \
  "$OUT/fonts/DigitalKhattV2.otf"

# -----------------------------
# General QUL evidence
# -----------------------------

download \
  "https://qul.tarteel.ai/faq" \
  "$OUT/source_pages/QUL_FAQ_RESOURCE_LICENSE_NOTICE.html"

download \
  "https://qul.tarteel.ai/resources/font" \
  "$OUT/source_pages/QUL_ALL_QURAN_FONTS_PAGE.html"

download \
  "https://raw.githubusercontent.com/TarteelAI/quranic-universal-library/main/LICENSE" \
  "$OUT/licenses/QUL_REPOSITORY_LICENSE_MIT.txt"

# -----------------------------
# Restrictive KFGQPC evidence
# -----------------------------
# This is NOT clearance. It is evidence that some KFGQPC fonts may need explicit permission.

download \
  "https://scancode-licensedb.aboutcode.org/kfgqpc-uthmanic-script-hafs.html" \
  "$OUT/licenses/KFGQPC_UTHMANIC_SCRIPT_HAFS_LICENSE_NOTICE_SCANCODE.html"

download \
  "https://online-fonts.com/fonts/kfgqpc-hafs-uthmanic-script" \
  "$OUT/source_pages/KFGQPC_HAFS_UTHMANIC_SCRIPT_THIRD_PARTY_LICENSE_NOTICE.html"

# Official Quran Complex sites sometimes fail due network/TLS, but try saving them.
download \
  "https://fonts.qurancomplex.gov.sa/" \
  "$OUT/source_pages/QURAN_COMPLEX_FONTS_HOME.html"

download \
  "https://qurancomplex.gov.sa/" \
  "$OUT/source_pages/QURAN_COMPLEX_HOME.html"

# -----------------------------
# Compare with actual app font folder
# -----------------------------

echo
echo "Generating checksums..."
find "$OUT" -type f -exec sha256sum {} \; | sort > "$OUT/ACTIVE_READER_FONT_LICENSE_EVIDENCE_SHA256SUMS.txt"

echo
echo "Current Android font files:"
find apps/android/app/src/main/res/font -maxdepth 1 -type f -exec sha256sum {} \; | sort \
  > "$OUT/CURRENT_ANDROID_FONT_SHA256SUMS.txt" || true

cat > "$OUT/ACTIVE_READER_FONT_LICENSE_DOWNLOAD_SUMMARY.md" <<'EOF'
# Amanah Quran — Active Reader Font License/Source Evidence Download Summary

## Purpose

This folder stores source pages, font downloads, and license/terms evidence for the active Quran reader fonts.

Manual active reader fonts to verify:
1. IndoPak display: IndoPak Nastaleeq
2. Uthmani display: Uthmani Hafs2 / V2

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

### DigitalKhatt V2 Uthmani-compatible candidate

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
EOF

echo
echo "Done."
echo "Output folder: $OUT"
echo "Summary: $OUT/ACTIVE_READER_FONT_LICENSE_DOWNLOAD_SUMMARY.md"
echo "Evidence checksums: $OUT/ACTIVE_READER_FONT_LICENSE_EVIDENCE_SHA256SUMS.txt"
echo "Android font checksums: $OUT/CURRENT_ANDROID_FONT_SHA256SUMS.txt"


