#!/usr/bin/env bash
set -euo pipefail

# Amanah Quran License Evidence Downloader
# Run from the root of your Amanah Quran repo:
#   bash amanah_license_evidence_downloader.sh
# Or pass repo root:
#   bash amanah_license_evidence_downloader.sh /path/to/repo

ROOT="${1:-$(pwd)}"
LEGAL_DIR="$ROOT/docs/legal"
EVIDENCE_DIR="$LEGAL_DIR/evidence"
QURAN_DIR="$EVIDENCE_DIR/quran"
FONT_DIR="$EVIDENCE_DIR/fonts"
SOURCE_PAGE_DIR="$EVIDENCE_DIR/source_pages"
SCAN_DIR="$EVIDENCE_DIR/scan"
MANIFEST="$LEGAL_DIR/CONTENT_AND_FONT_LICENSE_MANIFEST.md"
CHECKSUMS="$LEGAL_DIR/LEGAL_EVIDENCE_SHA256SUMS.txt"
LOG="$LEGAL_DIR/license_download_log.txt"

mkdir -p "$QURAN_DIR" "$FONT_DIR" "$SOURCE_PAGE_DIR" "$SCAN_DIR"
: > "$LOG"

log() {
  printf '%s\n' "$*" | tee -a "$LOG"
}

slugify() {
  printf '%s' "$1" | sed 's#https\?://##; s#[^A-Za-z0-9._-]#_#g; s#_\{2,\}#_#g' | cut -c1-180
}

download() {
  local url="$1"
  local out="$2"
  log "Downloading: $url -> $out"
  if command -v curl >/dev/null 2>&1; then
    curl -L --fail --retry 3 --retry-delay 2 "$url" -o "$out" || {
      log "FAILED: $url"
      return 1
    }
  elif command -v wget >/dev/null 2>&1; then
    wget -O "$out" "$url" || {
      log "FAILED: $url"
      return 1
    }
  else
    log "ERROR: curl or wget required."
    exit 1
  fi
}

log "Amanah Quran legal evidence collection"
log "Repo root: $ROOT"
log "Evidence dir: $EVIDENCE_DIR"
log ""

# Core QUL evidence.
download "https://raw.githubusercontent.com/TarteelAI/quranic-universal-library/main/LICENSE" \
  "$QURAN_DIR/QUL_REPOSITORY_LICENSE_MIT.txt" || true

download "https://qul.tarteel.ai/faq" \
  "$QURAN_DIR/QUL_FAQ_RESOURCE_LICENSE_NOTICE.html" || true

download "https://qul.tarteel.ai/" \
  "$QURAN_DIR/QUL_HOME_DEVELOPER_RESOURCES.html" || true

download "https://qul.tarteel.ai/resources" \
  "$QURAN_DIR/QUL_ALL_RESOURCES_PAGE.html" || true

download "https://qul.tarteel.ai/resources/quran-script" \
  "$QURAN_DIR/QUL_QURAN_SCRIPT_RESOURCE_PAGE.html" || true

download "https://qul.tarteel.ai/docs" \
  "$QURAN_DIR/QUL_DOCS_INDEX.html" || true

# Fonts we know are likely relevant. These do not replace your exact source audit.
download "https://qul.tarteel.ai/resources/font/568" \
  "$FONT_DIR/DIGITAL_KHATT_INDOPAK_QUL_FONT_PAGE.html" || true

download "https://static-cdn.tarteel.ai/qul/fonts/dk/DigitalKhattIndoPak.otf" \
  "$FONT_DIR/DigitalKhattIndoPak.otf" || true

download "https://raw.githubusercontent.com/DigitalKhatt/indopakfont/main/LICENSE" \
  "$FONT_DIR/DIGITAL_KHATT_INDOPAK_LICENSE_OFL_1_1.txt" || true

download "https://github.com/DigitalKhatt/indopakfont" \
  "$FONT_DIR/DIGITAL_KHATT_INDOPAK_GITHUB_PAGE.html" || true

# Optional Uthmani-compatible DigitalKhatt font evidence candidate.
download "https://qul.tarteel.ai/resources/font/247" \
  "$FONT_DIR/DIGITAL_KHATT_V2_QUL_FONT_PAGE.html" || true

download "https://static-cdn.tarteel.ai/qul/fonts/dk/DigitalKhattV2.otf" \
  "$FONT_DIR/DigitalKhattV2.otf" || true

# Scan likely project data folders and app assets for source URLs and files.
log ""
log "Scanning project folders for source URLs and bundled files..."

TARGET_DIRS=()
for d in \
  "$ROOT/sourcedata" "$ROOT/sourceData" "$ROOT/source_data" "$ROOT/source-data" \
  "$ROOT/projectdata" "$ROOT/projectData" "$ROOT/project_data" "$ROOT/project-data" \
  "$ROOT/data" "$ROOT/app/src/main/assets" "$ROOT/apps/android/app/src/main/assets" \
  "$ROOT/app/src/main/res" "$ROOT/apps/android/app/src/main/res"; do
  [ -d "$d" ] && TARGET_DIRS+=("$d")
done

{
  echo "# Detected scan targets"
  printf '%s\n' "${TARGET_DIRS[@]:-NO_TARGET_DIRS_FOUND}"
} > "$SCAN_DIR/SCAN_TARGETS.txt"

if [ "${#TARGET_DIRS[@]}" -gt 0 ]; then
  # Extract URLs from text-like files only. Ignore binary read errors.
  grep -RhoE 'https?://[^ )"'"'<>]+' "${TARGET_DIRS[@]}" 2>/dev/null \
    | sed 's/[.,;)]$//' \
    | sort -u > "$SCAN_DIR/DETECTED_SOURCE_URLS.txt" || true

  find "${TARGET_DIRS[@]}" -type f \( \
    -iname '*.ttf' -o -iname '*.otf' -o -iname '*.db' -o -iname '*.sqlite' -o -iname '*.json' \
    -o -iname '*.csv' -o -iname '*.xml' -o -iname '*.txt' -o -iname '*.md' \
  \) -print | sort > "$SCAN_DIR/DETECTED_CONTENT_AND_FONT_FILES.txt" || true
else
  : > "$SCAN_DIR/DETECTED_SOURCE_URLS.txt"
  : > "$SCAN_DIR/DETECTED_CONTENT_AND_FONT_FILES.txt"
fi

# Download detected source pages as evidence, without assuming they are licenses.
if [ -s "$SCAN_DIR/DETECTED_SOURCE_URLS.txt" ]; then
  while IFS= read -r url; do
    name="$(slugify "$url")"
    download "$url" "$SOURCE_PAGE_DIR/${name}.html" || true
  done < "$SCAN_DIR/DETECTED_SOURCE_URLS.txt"
fi

# Detect exact currently bundled font names that still require clearance.
{
  echo "# Detected font files requiring explicit source/license clearance"
  if [ -s "$SCAN_DIR/DETECTED_CONTENT_AND_FONT_FILES.txt" ]; then
    grep -Ei '\.(ttf|otf)$' "$SCAN_DIR/DETECTED_CONTENT_AND_FONT_FILES.txt" || true
  fi
} > "$SCAN_DIR/DETECTED_FONT_FILES.txt"

# Generate checksums for evidence and detected project files.
log "Generating checksums..."
{
  echo "# SHA-256 checksums generated on $(date -Iseconds)"
  echo ""
  if command -v sha256sum >/dev/null 2>&1; then
    find "$EVIDENCE_DIR" -type f -print0 | sort -z | xargs -0 sha256sum 2>/dev/null || true
    if [ -s "$SCAN_DIR/DETECTED_CONTENT_AND_FONT_FILES.txt" ]; then
      while IFS= read -r f; do
        [ -f "$f" ] && sha256sum "$f" || true
      done < "$SCAN_DIR/DETECTED_CONTENT_AND_FONT_FILES.txt"
    fi
  elif command -v shasum >/dev/null 2>&1; then
    find "$EVIDENCE_DIR" -type f -print0 | sort -z | xargs -0 shasum -a 256 2>/dev/null || true
    if [ -s "$SCAN_DIR/DETECTED_CONTENT_AND_FONT_FILES.txt" ]; then
      while IFS= read -r f; do
        [ -f "$f" ] && shasum -a 256 "$f" || true
      done < "$SCAN_DIR/DETECTED_CONTENT_AND_FONT_FILES.txt"
    fi
  else
    echo "No sha256sum/shasum found. Install coreutils or use macOS shasum."
  fi
} > "$CHECKSUMS"

# Create manifest template with resolved known evidence and pending exact-resource review.
cat > "$MANIFEST" <<'MD'
# Amanah Quran — Content and Font License Evidence Manifest

## Purpose

This folder records legal/source evidence for Quran text, Quran metadata, fonts, and other bundled resources used in the Amanah Quran APK.

Important: this manifest is evidence documentation, not a scholar/content approval. Quran text still requires manual content review and release sign-off.

---

## Evidence Files Collected

### QUL / Quranic Universal Library

- QUL repository license: `docs/legal/evidence/quran/QUL_REPOSITORY_LICENSE_MIT.txt`
- QUL FAQ resource-license notice: `docs/legal/evidence/quran/QUL_FAQ_RESOURCE_LICENSE_NOTICE.html`
- QUL home/developer resources page: `docs/legal/evidence/quran/QUL_HOME_DEVELOPER_RESOURCES.html`
- QUL all resources page: `docs/legal/evidence/quran/QUL_ALL_RESOURCES_PAGE.html`
- QUL Quran script resource page: `docs/legal/evidence/quran/QUL_QURAN_SCRIPT_RESOURCE_PAGE.html`
- QUL docs index: `docs/legal/evidence/quran/QUL_DOCS_INDEX.html`

### DigitalKhatt IndoPak Font

- QUL font page: `docs/legal/evidence/fonts/DIGITAL_KHATT_INDOPAK_QUL_FONT_PAGE.html`
- Font file evidence: `docs/legal/evidence/fonts/DigitalKhattIndoPak.otf`
- GitHub page evidence: `docs/legal/evidence/fonts/DIGITAL_KHATT_INDOPAK_GITHUB_PAGE.html`
- License file: `docs/legal/evidence/fonts/DIGITAL_KHATT_INDOPAK_LICENSE_OFL_1_1.txt`

### Optional Uthmani-Compatible Candidate

- QUL DigitalKhatt V2 page: `docs/legal/evidence/fonts/DIGITAL_KHATT_V2_QUL_FONT_PAGE.html`
- Font file evidence: `docs/legal/evidence/fonts/DigitalKhattV2.otf`

---

## Detected Project Evidence

- Scan targets: `docs/legal/evidence/scan/SCAN_TARGETS.txt`
- Detected URLs: `docs/legal/evidence/scan/DETECTED_SOURCE_URLS.txt`
- Downloaded detected source pages: `docs/legal/evidence/source_pages/`
- Detected bundled data/font files: `docs/legal/evidence/scan/DETECTED_CONTENT_AND_FONT_FILES.txt`
- Detected font files requiring explicit clearance: `docs/legal/evidence/scan/DETECTED_FONT_FILES.txt`
- SHA-256 checksums: `docs/legal/LEGAL_EVIDENCE_SHA256SUMS.txt`

---

## Clearance Table

| Resource | Source | License Evidence | Status | Notes |
|---|---|---|---|---|
| QUL repository/source pages | QUL / TarteelAI | MIT license + FAQ/resource pages saved | Evidence collected | Resource-level terms still need exact matching to used files |
| IndoPak Quran text | QUL resource, exact file to be confirmed from sourceData/projectData | QUL license/resource pages saved | Pending exact-file mapping | Fill exact resource name, export format, download date, checksum |
| Uthmani Quran text | QUL resource, exact file to be confirmed from sourceData/projectData | QUL license/resource pages saved | Pending exact-file mapping | Fill exact resource name, export format, download date, checksum |
| Quran page/juz metadata | QUL or project source, exact file to be confirmed | Source pages/checksums saved if URL detected | Pending exact-file mapping | Page mapping must match selected Mushaf/script |
| DigitalKhattIndoPak.otf | QUL + DigitalKhatt GitHub | OFL-1.1 license saved | Likely clear, verify exact bundled file checksum | Confirm app uses this exact file or replace bundled font with this copy |
| uthmanic_hafs_v22.ttf | Existing bundled app font if present | Not automatically cleared by QUL/DigitalKhatt IndoPak license | Pending unless exact official license/source is found | Do not mark clear without official source/license evidence |
| DigitalKhattV2.otf | QUL font resource | QUL page saved; license still should be verified at resource/source level | Candidate replacement | Test rendering before switching |

---

## Required Manual Completion

For each Quran text or metadata file actually imported into the app database, add:

- Exact source name
- Exact QUL/resource URL
- Export format: JSON / SQLite / CSV / other
- Download date
- App import date
- Version or resource ID
- License/attribution requirement
- SHA-256 checksum
- Validation result: 114 Surahs / 6236 ayahs / no duplicates / no empty display text
- Manual Quran review status
- Reviewer name/sign-off date

---

## Release Rule

Public release is blocked until every bundled Quran text, metadata file, and font has:

1. source URL,
2. license evidence,
3. checksum,
4. app import mapping,
5. validation status,
6. manual review status.
MD

log ""
log "Done. Evidence folder: $EVIDENCE_DIR"
log "Manifest: $MANIFEST"
log "Checksums: $CHECKSUMS"
log "Log: $LOG"
