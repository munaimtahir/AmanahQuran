#!/usr/bin/env python3
"""Deterministic Android-side import of the frozen dual-translation handoff.

Consumes ONLY the approved final integration bundle produced by the separate
translation-builder repository (copied into this repository at
``manifest-quran-builder/manifest-quran-builder/``):

    manifest-quran-builder/manifest-quran-builder/release/final/amanah-integration/

This script never touches source-native or staging material, never rewrites
translation text, and never invents text for SOURCE_MISSING canonical ayahs
(currently only 1:1, the Bismillah, for both packs). It fails loudly rather
than silently continuing if the handoff's checksums, canonical coverage, or
approval status do not match what a release build requires.

Output: one Room-compatible SQLite asset
(``apps/android/app/src/main/assets/content/translations/translation_content.db``)
containing both TAHIR_QADRI_MANIFEST_EN and TAHIR_QADRI_IRFAN_UR, plus a
human-readable manifest/report used by the Trust Center and CI.
"""

from __future__ import annotations

import argparse
import hashlib
import json
import re
import sqlite3
from datetime import datetime, timezone
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
HANDOFF_DIR = ROOT / "manifest-quran-builder" / "manifest-quran-builder" / "release" / "final" / "amanah-integration"
DEFAULT_OUTPUT = ROOT / "apps" / "android" / "app" / "src" / "main" / "assets" / "content" / "translations" / "translation_content.db"
DEFAULT_MANIFEST_OUT = ROOT / "apps" / "android" / "app" / "src" / "main" / "assets" / "content" / "translations" / "translation_content_manifest.json"
DEFAULT_REPORT_OUT = ROOT / "content-pipeline" / "05_validation_reports" / "dual_translation_validation.json"

AYAH_KEY_RE = re.compile(r"^(\d+):(\d+)$")

TRANSLATIONS = {
    "TAHIR_QADRI_MANIFEST_EN": {
        "sqlite": "manifest_en_canonical.sqlite",
        "language_code": "en",
        "language_name": "English",
        "display_name": "The Manifest Quran",
        "direction": "LTR",
        "source_domain": "manifestquran.com",
    },
    "TAHIR_QADRI_IRFAN_UR": {
        "sqlite": "irfan_ur_canonical.sqlite",
        "language_code": "ur",
        "language_name": "Urdu",
        "display_name": "Irfan-ul-Quran",
        "direction": "RTL",
        "source_domain": "irfan-ul-quran.com",
    },
}
TRANSLATOR_NAME = "Dr Muhammad Tahir-ul-Qadri"

# Room's compiled identity hash for this exact schema (org.amanahquran.app.content.translation.
# TranslationDatabase, version 1) -- regenerate via `:app:kspDebugKotlin` and copy from
# apps/android/app/schemas/.../1.json if the Kotlin entities in
# apps/android/app/src/main/kotlin/org/amanahquran/app/content/translation/ ever change.
ROOM_IDENTITY_HASH = "425e9a5584d08d0666d65ae0244eb89f"

CREATE_STATEMENTS = [
    "CREATE TABLE IF NOT EXISTS `translation_metadata` (`translationId` TEXT NOT NULL, `languageCode` TEXT NOT NULL, `languageName` TEXT NOT NULL, `displayName` TEXT NOT NULL, `translatorName` TEXT NOT NULL, `sourceName` TEXT NOT NULL, `sourceUrl` TEXT NOT NULL, `direction` TEXT NOT NULL, `contentVersion` TEXT NOT NULL, `permissionStatus` TEXT NOT NULL, `attributionText` TEXT NOT NULL, `availableCount` INTEGER NOT NULL, `sourceMissingCount` INTEGER NOT NULL, `footnoteCount` INTEGER NOT NULL, `checksum` TEXT NOT NULL, `importDate` TEXT NOT NULL, PRIMARY KEY(`translationId`))",
    "CREATE TABLE IF NOT EXISTS `translation_ayahs` (`translationId` TEXT NOT NULL, `ayahKey` TEXT NOT NULL, `surahNumber` INTEGER NOT NULL, `ayahNumber` INTEGER NOT NULL, `displayText` TEXT, `availabilityStatus` TEXT NOT NULL, `normalizedSearchText` TEXT, PRIMARY KEY(`translationId`, `ayahKey`))",
    "CREATE INDEX IF NOT EXISTS `index_translation_ayahs_ayahKey` ON `translation_ayahs` (`ayahKey`)",
    "CREATE INDEX IF NOT EXISTS `index_translation_ayahs_normalizedSearchText` ON `translation_ayahs` (`normalizedSearchText`)",
    "CREATE TABLE IF NOT EXISTS `translation_footnotes` (`translationId` TEXT NOT NULL, `ayahKey` TEXT NOT NULL, `footnoteIndex` INTEGER NOT NULL, `marker` TEXT NOT NULL, `footnoteText` TEXT NOT NULL, PRIMARY KEY(`translationId`, `ayahKey`, `footnoteIndex`))",
    "CREATE INDEX IF NOT EXISTS `index_translation_footnotes_translationId_ayahKey` ON `translation_footnotes` (`translationId`, `ayahKey`)",
    "CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)",
]


class ImportError_(SystemExit):
    pass


def fail(message: str) -> "ImportError_":
    return ImportError_(f"BLOCKED: {message}")


def sha256_file(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as fh:
        for block in iter(lambda: fh.read(1024 * 1024), b""):
            digest.update(block)
    return digest.hexdigest()


def verify_handoff_checksums(handoff_dir: Path) -> None:
    manifest_path = handoff_dir / "checksums" / "checksum_manifest.json"
    if not manifest_path.is_file():
        raise fail(f"missing checksum manifest at {manifest_path}")
    manifest = json.loads(manifest_path.read_text(encoding="utf-8"))
    for entry in manifest["files"]:
        target = handoff_dir / entry["path"]
        if not target.is_file():
            raise fail(f"handoff file missing: {entry['path']}")
        actual_size = target.stat().st_size
        if actual_size != entry["byte_size"]:
            raise fail(f"size mismatch for {entry['path']}: expected {entry['byte_size']}, got {actual_size}")
        actual_hash = sha256_file(target)
        if actual_hash != entry["sha256"]:
            raise fail(f"checksum mismatch for {entry['path']}: expected {entry['sha256']}, got {actual_hash}")


def verify_integration_manifest(handoff_dir: Path) -> dict:
    manifest = json.loads((handoff_dir / "integration_manifest.json").read_text(encoding="utf-8"))
    if manifest.get("AMANAH_INTEGRATION_READY") is not True:
        raise fail("AMANAH_INTEGRATION_READY is not TRUE in integration_manifest.json")
    if manifest.get("MANIFEST_MAPPING_STATUS") != "APPROVED" or manifest.get("MANIFEST_PERMISSION_STATUS") != "APPROVED":
        raise fail("Manifest English mapping/permission status is not APPROVED")
    if manifest.get("IRFAN_UR_MAPPING_STATUS") != "APPROVED" or manifest.get("IRFAN_UR_PERMISSION_STATUS") != "APPROVED":
        raise fail("Irfan-ul-Quran Urdu mapping/permission status is not APPROVED")
    ids = {t["translation_id"] for t in manifest["translations"]}
    if ids != set(TRANSLATIONS):
        raise fail(f"unexpected translation id set in integration_manifest.json: {sorted(ids)}")
    return manifest


def read_translation(handoff_dir: Path, translation_id: str, expected: dict) -> dict:
    sqlite_path = handoff_dir / "translations" / expected["sqlite"]
    con = sqlite3.connect(f"file:{sqlite_path}?mode=ro", uri=True)
    con.row_factory = sqlite3.Row
    try:
        cur = con.cursor()
        cur.execute("SELECT * FROM translations_canonical")
        translated_rows = [dict(row) for row in cur.fetchall()]
        cur.execute("SELECT * FROM mapping_pending")
        pending_rows = [dict(row) for row in cur.fetchall()]
    finally:
        con.close()

    seen_ids = {row["translation_id"] for row in translated_rows}
    if seen_ids != {translation_id}:
        raise fail(f"cross-contamination or wrong translation_id in {expected['sqlite']}: found {seen_ids}")

    for row in pending_rows:
        if row["human_review_required"] not in (0, False):
            raise fail(
                f"{translation_id} has an unresolved pending mapping at {row['canonical_ayah_key']} "
                "(human_review_required=1) -- release blocked until a human decision is recorded "
                "upstream in the translation-builder repository",
            )
        if row["mapping_status"] != "SOURCE_MISSING":
            raise fail(
                f"{translation_id} has a non-SOURCE_MISSING row in mapping_pending at "
                f"{row['canonical_ayah_key']} ({row['mapping_status']}) -- refusing to import an "
                "un-finalized mapping",
            )

    translated_keys = {row["canonical_ayah_key"] for row in translated_rows}
    pending_keys = {row["canonical_ayah_key"] for row in pending_rows}
    if len(translated_keys) != len(translated_rows):
        raise fail(f"{translation_id} has duplicate canonical keys in translations_canonical")
    if translated_keys & pending_keys:
        raise fail(f"{translation_id} has keys present in both translations_canonical and mapping_pending")

    all_keys = translated_keys | pending_keys
    if len(all_keys) != 6236:
        raise fail(f"{translation_id} canonical coverage is {len(all_keys)}, expected 6236")
    if len(translated_rows) != 6235:
        raise fail(f"{translation_id} translated count is {len(translated_rows)}, expected 6235")
    if len(pending_rows) != 1:
        raise fail(f"{translation_id} SOURCE_MISSING count is {len(pending_rows)}, expected 1")

    for key in all_keys:
        match = AYAH_KEY_RE.match(key)
        if not match:
            raise fail(f"{translation_id} has a malformed canonical ayah key: {key!r}")
        surah = int(match.group(1))
        if not (1 <= surah <= 114):
            raise fail(f"{translation_id} has an out-of-corpus surah number in key {key!r}")

    footnote_count = 0
    for row in translated_rows:
        if not row["display_text"]:
            raise fail(f"{translation_id}:{row['canonical_ayah_key']} is TRANSLATED but display_text is empty")
        footnotes = json.loads(row["footnotes_json"] or "[]")
        footnote_count += len(footnotes)

    return {
        "translated_rows": translated_rows,
        "pending_rows": pending_rows,
        "footnote_count": footnote_count,
    }


def normalize_for_search(value: str) -> str:
    value = value.replace("ـ", "")
    value = re.sub(r"[ً-ٰٟ]", "", value)
    return re.sub(r"\s+", " ", value).strip()


def build_database(output: Path, translations: dict[str, dict], integration_manifest: dict) -> None:
    output.parent.mkdir(parents=True, exist_ok=True)
    if output.exists():
        output.unlink()
    connection = sqlite3.connect(output)
    try:
        for statement in CREATE_STATEMENTS:
            connection.execute(statement)
        connection.execute(
            "INSERT INTO room_master_table VALUES (42, ?)",
            (ROOM_IDENTITY_HASH,),
        )

        manifest_by_id = {t["translation_id"]: t for t in integration_manifest["translations"]}
        import_date = datetime.now(timezone.utc).date().isoformat()

        for translation_id, expected in TRANSLATIONS.items():
            data = translations[translation_id]
            manifest_entry = manifest_by_id[translation_id]

            for row in data["translated_rows"]:
                key = row["canonical_ayah_key"]
                surah_str, ayah_str = key.split(":")
                display_text = row["display_text"]
                connection.execute(
                    "INSERT INTO translation_ayahs VALUES (?, ?, ?, ?, ?, ?, ?)",
                    (
                        translation_id,
                        key,
                        int(surah_str),
                        int(ayah_str),
                        display_text,
                        "TRANSLATED",
                        normalize_for_search(display_text),
                    ),
                )
                footnotes = json.loads(row["footnotes_json"] or "[]")
                for index, footnote in enumerate(footnotes):
                    connection.execute(
                        "INSERT INTO translation_footnotes VALUES (?, ?, ?, ?, ?)",
                        (translation_id, key, index, footnote.get("marker", ""), footnote["text"]),
                    )

            # SOURCE_MISSING rows (currently only 1:1) get an explicit row with displayText=NULL --
            # the runtime must be able to distinguish "no translation exists in the authorized
            # source" from an absent/corrupt database row (see mega-sprint section 6), so absence
            # from this table is never used as the SOURCE_MISSING signal.
            for row in data["pending_rows"]:
                key = row["canonical_ayah_key"]
                surah_str, ayah_str = key.split(":")
                connection.execute(
                    "INSERT INTO translation_ayahs VALUES (?, ?, ?, ?, ?, ?, ?)",
                    (translation_id, key, int(surah_str), int(ayah_str), None, "SOURCE_MISSING", None),
                )

            connection.execute(
                "INSERT INTO translation_metadata VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                (
                    translation_id,
                    expected["language_code"],
                    expected["language_name"],
                    expected["display_name"],
                    TRANSLATOR_NAME,
                    expected["display_name"],
                    f"https://{expected['source_domain']}",
                    expected["direction"],
                    manifest_entry["content_version"],
                    "APPROVED",
                    TRANSLATOR_NAME,
                    len(data["translated_rows"]),
                    len(data["pending_rows"]),
                    data["footnote_count"],
                    manifest_entry["confirmed_content_sha256"],
                    import_date,
                ),
            )
        connection.commit()
    finally:
        connection.close()


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--handoff-dir", type=Path, default=HANDOFF_DIR)
    parser.add_argument("--output", type=Path, default=DEFAULT_OUTPUT)
    parser.add_argument("--manifest-output", type=Path, default=DEFAULT_MANIFEST_OUT)
    parser.add_argument("--report-output", type=Path, default=DEFAULT_REPORT_OUT)
    args = parser.parse_args()

    if not args.handoff_dir.is_dir():
        raise fail(f"handoff directory not found: {args.handoff_dir}")

    verify_handoff_checksums(args.handoff_dir)
    integration_manifest = verify_integration_manifest(args.handoff_dir)

    translations = {}
    for translation_id, expected in TRANSLATIONS.items():
        translations[translation_id] = read_translation(args.handoff_dir, translation_id, expected)

    build_database(args.output, translations, integration_manifest)

    output_sha256 = sha256_file(args.output)
    report = {
        "generated_at": datetime.now(timezone.utc).isoformat(),
        "handoff_dir": str(args.handoff_dir.relative_to(ROOT)),
        "output_sha256": output_sha256,
        "output_byte_size": args.output.stat().st_size,
        "room_identity_hash": ROOM_IDENTITY_HASH,
        "translations": {
            translation_id: {
                "translated_count": len(data["translated_rows"]),
                "source_missing_count": len(data["pending_rows"]),
                "footnote_count": data["footnote_count"],
            }
            for translation_id, data in translations.items()
        },
        "validation_status": "PASS",
        "reviewer_status": "APPROVED",
    }

    args.manifest_output.parent.mkdir(parents=True, exist_ok=True)
    args.manifest_output.write_text(
        json.dumps({"pack_sha256": output_sha256, **report}, ensure_ascii=False, indent=2) + "\n",
        encoding="utf-8",
    )
    args.report_output.parent.mkdir(parents=True, exist_ok=True)
    args.report_output.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(json.dumps(report, ensure_ascii=False))


if __name__ == "__main__":
    main()
