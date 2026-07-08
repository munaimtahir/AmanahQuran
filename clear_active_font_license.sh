#!/usr/bin/env bash
set -Eeuo pipefail

REPO_ROOT="${1:-$(pwd)}"
cd "$REPO_ROOT"

OUT_DIR="docs/legal/evidence/fonts/active_font_clearance"
REPORT="$OUT_DIR/ACTIVE_FONT_LICENSE_CLEARANCE_REPORT.md"
MANIFEST="docs/legal/CONTENT_AND_FONT_LICENSE_MANIFEST.md"

mkdir -p "$OUT_DIR"

echo "Amanah Quran active font license clearance"
echo "Repo root: $REPO_ROOT"
echo "Output: $OUT_DIR"
echo

timestamp="$(date -Iseconds)"

APP_INDOPAK="apps/android/app/src/main/res/font/indopak_nastaleeq.ttf"
APP_UTHMANI="apps/android/app/src/main/res/font/uthmanic_hafs_v22.ttf"
APP_DIGITAL="apps/android/app/src/main/res/font/digital_khatt_indopak.otf"

SRC_KFGQPC_NASTALEEQ="sourcedata/8/extracted/KFGQPCNastaleeq-Regular.ttf"
SRC_UTHMANIC_HAFS="sourcedata/8/extracted/UthmanicHafs_V22.ttf"
SRC_DIGITAL_INDOPAK="sourcedata/8/extracted/DigitalKhattIndoPak.otf"
SRC_DIGITAL_V2="sourcedata/8/extracted/DigitalKhattV2.otf"

EVIDENCE_DIGITAL_LICENSE="docs/legal/evidence/fonts/DIGITAL_KHATT_INDOPAK_LICENSE_OFL_1_1.txt"
EVIDENCE_DIGITAL_FONT="docs/legal/evidence/fonts/DigitalKhattIndoPak.otf"
EVIDENCE_DIGITAL_V2="docs/legal/evidence/fonts/DigitalKhattV2.otf"

hash_file() {
  local f="$1"
  if [[ -f "$f" ]]; then
    sha256sum "$f" | awk '{print $1}'
  else
    echo "MISSING"
  fi
}

file_size() {
  local f="$1"
  if [[ -f "$f" ]]; then
    stat -c '%s bytes' "$f"
  else
    echo "MISSING"
  fi
}

compare_files() {
  local a="$1"
  local b="$2"
  local ha hb
  ha="$(hash_file "$a")"
  hb="$(hash_file "$b")"

  if [[ "$ha" == "MISSING" || "$hb" == "MISSING" ]]; then
    echo "MISSING"
  elif [[ "$ha" == "$hb" ]]; then
    echo "MATCH"
  else
    echo "DIFFERENT"
  fi
}

download_page() {
  local url="$1"
  local name="$2"
  local dest="$OUT_DIR/$name"

  echo "Trying: $url"
  if curl -L --fail --connect-timeout 15 --max-time 60 "$url" -o "$dest"; then
    echo "Downloaded: $dest"
  else
    echo "FAILED: $url" | tee -a "$OUT_DIR/download_failures.txt"
    rm -f "$dest"
  fi
}

echo "Generating hashes..."
{
  echo "# Amanah Quran — Active Font License Clearance Report"
  echo
  echo "Generated: $timestamp"
  echo
  echo "## Purpose"
  echo
  echo "This report focuses on active/bundled Android font files. It does not clear Quran text content and does not replace scholar/manual Quran review."
  echo
  echo "## Active/Bundled Font Files"
  echo
  echo "| Role | File | Exists | Size | SHA-256 |"
  echo "|---|---|---:|---:|---|"

  for entry in \
    "Active IndoPak reader font|$APP_INDOPAK" \
    "Active/Bundled Uthmani font|$APP_UTHMANI" \
    "Bundled DigitalKhatt IndoPak fallback/reference|$APP_DIGITAL" \
    "Source candidate KFGQPC Nastaleeq|$SRC_KFGQPC_NASTALEEQ" \
    "Source candidate Uthmanic Hafs|$SRC_UTHMANIC_HAFS" \
    "Source DigitalKhatt IndoPak|$SRC_DIGITAL_INDOPAK" \
    "Source DigitalKhatt V2|$SRC_DIGITAL_V2" \
    "Evidence DigitalKhatt IndoPak font|$EVIDENCE_DIGITAL_FONT" \
    "Evidence DigitalKhatt V2 font|$EVIDENCE_DIGITAL_V2"
  do
    role="${entry%%|*}"
    file="${entry#*|}"
    exists="NO"
    [[ -f "$file" ]] && exists="YES"
    echo "| $role | \`$file\` | $exists | $(file_size "$file") | \`$(hash_file "$file")\` |"
  done

  echo
  echo "## Checksum Comparisons"
  echo
  echo "| Comparison | Result | Meaning |"
  echo "|---|---|---|"
  echo "| app indopak_nastaleeq.ttf vs sourcedata KFGQPCNastaleeq-Regular.ttf | $(compare_files "$APP_INDOPAK" "$SRC_KFGQPC_NASTALEEQ") | If different, do not assume they are the same licensed file. |"
  echo "| app uthmanic_hafs_v22.ttf vs sourcedata UthmanicHafs_V22.ttf | $(compare_files "$APP_UTHMANI" "$SRC_UTHMANIC_HAFS") | If match, sourceData copy is same file; still needs license evidence. |"
  echo "| app digital_khatt_indopak.otf vs evidence DigitalKhattIndoPak.otf | $(compare_files "$APP_DIGITAL" "$EVIDENCE_DIGITAL_FONT") | If match, OFL evidence likely applies to this file. |"
  echo "| sourcedata DigitalKhattIndoPak.otf vs evidence DigitalKhattIndoPak.otf | $(compare_files "$SRC_DIGITAL_INDOPAK" "$EVIDENCE_DIGITAL_FONT") | Confirms source/evidence identity. |"
  echo
  echo "## Preliminary Clearance Status"
  echo
  echo "| Font | Status | Action |"
  echo "|---|---|---|"
  echo "| \`indopak_nastaleeq.ttf\` | BLOCKING/PENDING | Active IndoPak font. Must locate exact official source and license allowing Android app bundling/distribution. |"
  echo "| \`uthmanic_hafs_v22.ttf\` | BLOCKING/PENDING | Must locate exact official source and license allowing Android app bundling/distribution. |"
  echo "| \`digital_khatt_indopak.otf\` | CLEARED ONLY IF USED | Evidence collected separately, but it does not clear the active Nastaleeq font. |"
  echo "| \`DigitalKhattV2.otf\` | CANDIDATE ONLY | May be used as replacement only after license check and rendering QA. |"
  echo
} > "$REPORT"

echo "Searching project for font/source/license clues..."

SEARCH_OUT="$OUT_DIR/font_source_reference_search.txt"
LICENSE_FILES_OUT="$OUT_DIR/possible_license_files.txt"

{
  echo "# Font/source reference search"
  echo "Generated: $timestamp"
  echo

  for term in \
    "indopak_nastaleeq" \
    "KFGQPCNastaleeq" \
    "KFGQPC" \
    "Nastaleeq" \
    "uthmanic_hafs" \
    "UthmanicHafs" \
    "UthmaniHafs" \
    "qurancomplex" \
    "fonts.qurancomplex" \
    "Quran Complex" \
    "King Fahd" \
    "DigitalKhatt"
  do
    echo
    echo "## Search term: $term"
    grep -RIn --exclude-dir=.git --exclude-dir=build --exclude-dir=.gradle --exclude-dir=docs/legal/evidence/source_pages "$term" \
      sourcedata projectdata apps/android docs 2>/dev/null || true
  done
} > "$SEARCH_OUT"

{
  echo "# Possible local license/readme/terms files"
  echo "Generated: $timestamp"
  echo
  find sourcedata projectdata apps/android docs \
    -type f \
    \( -iname '*license*' -o -iname '*licence*' -o -iname '*readme*' -o -iname '*terms*' -o -iname '*copyright*' -o -iname '*attribution*' \) \
    2>/dev/null | sort
} > "$LICENSE_FILES_OUT"

echo "Attempting to save candidate official/source pages..."
: > "$OUT_DIR/download_failures.txt"

download_page "https://fonts.qurancomplex.gov.sa/" "qurancomplex_fonts_home.html"
download_page "https://qurancomplex.gov.sa/" "qurancomplex_home.html"
download_page "https://github.com/DigitalKhatt/indopakfont" "digitalkhatt_indopak_github.html"
download_page "https://raw.githubusercontent.com/DigitalKhatt/indopakfont/main/LICENSE" "digitalkhatt_indopak_license.txt"

echo "Appending active-font correction to manifest if needed..."

if [[ -f "$MANIFEST" ]]; then
  if ! grep -q "Active Font Correction" "$MANIFEST"; then
    cat >> "$MANIFEST" <<'EOF'

---

## Active Font Correction

The active IndoPak reader font is `apps/android/app/src/main/res/font/indopak_nastaleeq.ttf`, not `digital_khatt_indopak.otf`.

Therefore:

- `digital_khatt_indopak.otf` evidence remains useful as fallback/reference only.
- `indopak_nastaleeq.ttf` is a P0 license-clearance item before public release.
- `indopak_nastaleeq.ttf` must not be marked CLEARED until exact official source and license evidence are attached.
- `uthmanic_hafs_v22.ttf` also remains pending unless exact official source and license evidence are attached.
- If either font cannot be cleared, remove or replace it with a clearly licensed font and repeat rendering QA.

EOF
  fi
else
  echo "WARNING: Manifest not found at $MANIFEST"
fi

echo "Generating active-font checksums file..."
find \
  apps/android/app/src/main/res/font \
  sourcedata/8/extracted \
  docs/legal/evidence/fonts \
  "$OUT_DIR" \
  -type f 2>/dev/null | sort | xargs -r sha256sum > "$OUT_DIR/active_font_clearance_sha256sums.txt"

echo
echo "Done."
echo "Report: $REPORT"
echo "Search results: $SEARCH_OUT"
echo "Possible license files: $LICENSE_FILES_OUT"
echo "Failures: $OUT_DIR/download_failures.txt"
echo
echo "Open the report:"
echo "  xdg-open \"$REPORT\""
