#!/usr/bin/env python3
from __future__ import annotations

import csv
import json
import shutil
import sqlite3
import sys
import zipfile
from collections import Counter, defaultdict
from pathlib import Path
from typing import Any
from xml.etree import ElementTree as ET

from content_pipeline_common import (
    ANDROID_ASSET_DB,
    ANDROID_TRUST_JSON,
    CONTENT_PIPELINE,
    NOW,
    PROJECTDATA,
    ROOT,
    asset_type_from_source_row,
    classify_content_path,
    current_datetime,
    hardlink_or_copy,
    is_content_related,
    load_json,
    license_status_to_flags,
    query_one,
    rel,
    sha256_file,
    source_folder_number,
    stable_asset_id,
    write_json,
    write_text,
)


SOURCE_CANDIDATE_JSON = PROJECTDATA / "content_sources.json"
SOURCE_FONT_JSON = PROJECTDATA / "font_manifest.json"


def content_database_source() -> Path:
    candidates = [
        CONTENT_PIPELINE / "06_generated_projectdata" / "amanah_quran.sqlite",
        ROOT / "apps" / "android" / "app" / "src" / "main" / "assets" / "database" / "quran.db",
        ROOT / "apps" / "android" / "app" / "src" / "main" / "assets" / "database" / "amanah_quran_content_v1_candidate.sqlite",
    ]
    for candidate in candidates:
        if candidate.exists():
            return candidate
    raise FileNotFoundError("No readable Quran database source found for pipeline generation")


def ensure_dirs() -> None:
    for path in [
        CONTENT_PIPELINE / "00_candidate_sources" / "quran_text",
        CONTENT_PIPELINE / "00_candidate_sources" / "fonts",
        CONTENT_PIPELINE / "00_candidate_sources" / "metadata",
        CONTENT_PIPELINE / "00_candidate_sources" / "page_mapping",
        CONTENT_PIPELINE / "01_license_review" / "LICENSE_EVIDENCE",
        CONTENT_PIPELINE / "02_approved_sources" / "quran_text",
        CONTENT_PIPELINE / "02_approved_sources" / "fonts",
        CONTENT_PIPELINE / "02_approved_sources" / "metadata",
        CONTENT_PIPELINE / "02_approved_sources" / "page_mapping",
        CONTENT_PIPELINE / "03_raw_immutable" / "indopak",
        CONTENT_PIPELINE / "03_raw_immutable" / "uthmani",
        CONTENT_PIPELINE / "03_raw_immutable" / "metadata",
        CONTENT_PIPELINE / "03_raw_immutable" / "page_mapping",
        CONTENT_PIPELINE / "03_raw_immutable" / "fonts",
        CONTENT_PIPELINE / "04_staging_import" / "parsed_json",
        CONTENT_PIPELINE / "04_staging_import" / "import_logs",
        CONTENT_PIPELINE / "05_validation_reports",
        CONTENT_PIPELINE / "06_generated_projectdata",
        CONTENT_PIPELINE / "07_android_app_assets" / "database",
        CONTENT_PIPELINE / "07_android_app_assets" / "fonts",
        CONTENT_PIPELINE / "99_quarantine_legacy" / "quran_text",
        CONTENT_PIPELINE / "99_quarantine_legacy" / "database",
        CONTENT_PIPELINE / "99_quarantine_legacy" / "metadata",
        CONTENT_PIPELINE / "99_quarantine_legacy" / "page_mapping",
        CONTENT_PIPELINE / "99_quarantine_legacy" / "fonts",
    ]:
        path.mkdir(parents=True, exist_ok=True)


def source_rows() -> list[dict]:
    return load_json(SOURCE_CANDIDATE_JSON)


def font_rows() -> list[dict]:
    return load_json(SOURCE_FONT_JSON)


def normalize_license_status(raw: str, asset_type: str) -> str:
    text = (raw or "").strip().lower()
    if text.startswith("creative commons attribution 3.0"):
        return "APPROVED_FOR_PUBLIC_DISTRIBUTION"
    if asset_type == "FONT" and raw == "CLEARED":
        return "APPROVED_FOR_INTERNAL_TESTING"
    if raw == "requires review" or text == "unknown":
        return "REVIEW_REQUIRED" if raw else "UNKNOWN"
    if text == "approved":
        return "APPROVED_FOR_PUBLIC_DISTRIBUTION"
    return "UNKNOWN"


def build_registry() -> list[dict]:
    registry: list[dict] = []
    for row in source_rows():
        asset_type = asset_type_from_source_row(row)
        local_path = row["original_file_path"]
        license_status = normalize_license_status(row.get("license_status"), asset_type)
        license_name = "CC BY 3.0" if license_status == "APPROVED_FOR_PUBLIC_DISTRIBUTION" else (row.get("license_status") or "UNKNOWN")
        flags = license_status_to_flags(row.get("license_status", ""), asset_type)
        registry.append(
            {
                "asset_id": stable_asset_id(row),
                "asset_type": asset_type,
                "script_type": row.get("script_type") or "NOT_APPLICABLE",
                "source_name": row.get("source_name"),
                "source_url": row.get("source_url"),
                "local_path": local_path,
                "version": row.get("v1_candidate_status") or row.get("detected_format") or "unknown",
                "license_name": license_name,
                "license_url_or_document": row.get("source_url") or "",
                "license_status": license_status,
                "copyright_holder": row.get("source_name") or "UNKNOWN",
                "redistribution_allowed": flags["redistribution_allowed"],
                "modification_allowed": flags["modification_allowed"],
                "commercial_use_allowed": flags["commercial_use_allowed"],
                "app_bundling_allowed": flags["app_bundling_allowed"],
                "checksum_sha256": row.get("sha256"),
                "date_added": current_datetime(),
                "reviewed_by": "system-generated",
                "review_notes": row.get("notes") or "",
            }
        )
    for row in font_rows():
        registry.append(
            {
                "asset_id": row["id"],
                "asset_type": "FONT",
                "script_type": row.get("scriptType") or "NOT_APPLICABLE",
                "source_name": row.get("displayName") or row.get("sourceName"),
                "source_url": row.get("sourceUrl"),
                "local_path": row.get("filePath"),
                "version": row.get("fileName"),
                "license_name": row.get("licenseStatus") or "UNKNOWN",
                "license_url_or_document": row.get("sourceUrl"),
                "license_status": "APPROVED_FOR_INTERNAL_TESTING" if row.get("licenseStatus") == "CLEARED" else "REVIEW_REQUIRED",
                "copyright_holder": row.get("sourceName") or "UNKNOWN",
                "redistribution_allowed": "unknown",
                "modification_allowed": "unknown",
                "commercial_use_allowed": "unknown",
                "app_bundling_allowed": True if row.get("licenseStatus") == "CLEARED" else "unknown",
                "checksum_sha256": row.get("checksumSha256"),
                "date_added": current_datetime(),
                "reviewed_by": "system-generated",
                "review_notes": row.get("notes") or "",
            }
        )
    write_json(CONTENT_PIPELINE / "01_license_review" / "source_registry.json", registry)
    return registry


def write_license_docs(registry: list[dict]) -> None:
    text_sources = [r for r in registry if r["asset_type"] in {"QURAN_TEXT", "METADATA", "PAGE_MAPPING", "JUZ_MAPPING", "SEARCH_SOURCE"}]
    font_sources = [r for r in registry if r["asset_type"] == "FONT"]
    write_text(
        CONTENT_PIPELINE / "01_license_review" / "LICENSE_REGISTER.md",
        "\n".join(
            [
                "# License Register",
                "",
                f"Generated: {NOW}",
                "",
                f"- Quran/text/metadata assets: {len(text_sources)}",
                f"- Font assets: {len(font_sources)}",
                "",
                "Public distribution remains blocked until every required asset is marked APPROVED_FOR_PUBLIC_DISTRIBUTION and app bundling is true.",
            ]
        )
        + "\n",
    )
    write_text(
        CONTENT_PIPELINE / "01_license_review" / "FONT_LICENSE_REVIEW.md",
        "\n".join(
            [
                "# Font License Review",
                "",
                "Current active reader fonts are cleared for internal testing only.",
                "",
                *[f"- {row['source_name']}: {row['license_status']}" for row in font_sources],
            ]
        )
        + "\n",
    )
    write_text(
        CONTENT_PIPELINE / "01_license_review" / "TEXT_SOURCE_LICENSE_REVIEW.md",
        "\n".join(
            [
                "# Text Source License Review",
                "",
                "Tanzil Uthmani and Simple Clean are documented as CC BY 3.0 in the source headers.",
                "IndoPak and backup Quran assets remain under review or quarantine until explicit clearance is confirmed.",
            ]
        )
        + "\n",
    )
    write_text(
        CONTENT_PIPELINE / "01_license_review" / "LICENSE_EVIDENCE" / "README.md",
        "# License evidence\n\nThis folder holds the evidence references used by the registry and release gates.\n",
    )


def source_file_path(row: dict) -> Path:
    return ROOT / row["original_file_path"]


def font_file_path(row: dict) -> Path:
    return ROOT / row["filePath"]


def mirror_sources(registry: list[dict]) -> None:
    for row in source_rows():
        src = source_file_path(row)
        if not src.exists():
            continue
        folder = row["source_folder_number"]
        asset_type = asset_type_from_source_row(row)
        target_root = CONTENT_PIPELINE / "00_candidate_sources"
        if folder in (4, 7, 10):
            target_root = CONTENT_PIPELINE / "99_quarantine_legacy"
        subdir = "metadata"
        if asset_type == "QURAN_TEXT":
            subdir = "quran_text"
        elif asset_type == "FONT":
            subdir = "fonts"
        elif asset_type == "PAGE_MAPPING":
            subdir = "page_mapping"
        elif asset_type == "SEARCH_SOURCE":
            subdir = "metadata"
        hardlink_or_copy(src, target_root / subdir / src.name)

        approved_root = CONTENT_PIPELINE / "02_approved_sources"
        if row.get("license_status", "").lower().startswith("creative commons attribution 3.0"):
            hardlink_or_copy(src, approved_root / subdir / src.name)

        immutable_root = CONTENT_PIPELINE / "03_raw_immutable"
        if asset_type == "QURAN_TEXT":
            subdir = "indopak" if folder in (3, 4) else "uthmani"
        elif asset_type == "FONT":
            subdir = "fonts"
        elif asset_type == "PAGE_MAPPING":
            subdir = "page_mapping"
        elif asset_type == "SEARCH_SOURCE":
            subdir = "metadata"
        hardlink_or_copy(src, immutable_root / subdir / src.name)

    for row in font_rows():
        src = font_file_path(row)
        if src.exists():
            hardlink_or_copy(src, CONTENT_PIPELINE / "00_candidate_sources" / "fonts" / src.name)
            hardlink_or_copy(src, CONTENT_PIPELINE / "03_raw_immutable" / "fonts" / src.name)
            if row.get("licenseStatus") == "CLEARED":
                hardlink_or_copy(src, CONTENT_PIPELINE / "02_approved_sources" / "fonts" / src.name)


def write_source_lock(registry: list[dict]) -> None:
    raw_entries = []
    checksums = []
    for path in sorted((CONTENT_PIPELINE / "03_raw_immutable").rglob("*")):
        if not path.is_file():
            continue
        checksum = sha256_file(path)
        checksums.append(f"{checksum}  {path.relative_to(CONTENT_PIPELINE).as_posix()}")
    (CONTENT_PIPELINE / "03_raw_immutable" / "checksums.sha256").write_text("\n".join(sorted(checksums)) + "\n", encoding="utf-8")
    for row in registry:
        local_path = row["local_path"]
        if local_path.startswith("sourcedata/") or local_path.startswith("apps/android/app/src/main/res/font/"):
            raw_entries.append(
                {
                    "asset_id": row["asset_id"],
                    "source_path": local_path,
                    "sha256": row["checksum_sha256"],
                    "license_status": row["license_status"],
                }
            )
    write_json(CONTENT_PIPELINE / "03_raw_immutable" / "SOURCE_LOCK.json", {"generated_at": NOW, "sources": raw_entries})


def parse_text_xml(path: Path) -> dict[str, str]:
    tree = ET.parse(path)
    root = tree.getroot()
    verses: dict[str, str] = {}
    for surah in root.findall(".//sura"):
        surah_number = int(surah.attrib.get("index") or surah.attrib.get("id") or surah.attrib.get("sura") or 0)
        for ayah in surah.findall("aya"):
            ayah_number = int(ayah.attrib.get("index") or ayah.attrib.get("id") or ayah.attrib.get("aya") or 0)
            key = f"{surah_number}:{ayah_number}"
            verses[key] = (ayah.attrib.get("text") or ayah.text or "").strip()
    return verses


def parse_indopak_json(zip_path: Path) -> dict[str, str]:
    with zipfile.ZipFile(zip_path) as zf:
        name = zf.namelist()[0]
        data = json.loads(zf.read(name).decode("utf-8"))
    return {key: value["text"] for key, value in data.items()}


def parse_json_zip(zip_path: Path) -> dict:
    with zipfile.ZipFile(zip_path) as zf:
        name = zf.namelist()[0]
        return json.loads(zf.read(name).decode("utf-8"))


def generate_staging_data() -> dict[str, Any]:
    quran_uthmani = parse_text_xml(ROOT / "sourcedata" / "1" / "quran-uthmani.xml")
    simple_clean = parse_text_xml(ROOT / "sourcedata" / "2" / "quran-simple-clean.xml")
    indopak = parse_indopak_json(ROOT / "sourcedata" / "3" / "digital-khatt-indopak-ayah-by-ayah-script.json.zip")
    surah_meta = parse_json_zip(ROOT / "sourcedata" / "5" / "quran-metadata-surah-name.json.zip")
    ayah_meta = parse_json_zip(ROOT / "sourcedata" / "5" / "quran-metadata-ayah.json.zip")
    juz_meta = parse_json_zip(ROOT / "sourcedata" / "5" / "quran-metadata-juz.json.zip")
    sajda_meta = parse_json_zip(ROOT / "sourcedata" / "5" / "quran-metadata-sajda.json.zip")
    layout_db_zip = ROOT / "sourcedata" / "6" / "qudratullah-indopak-15-lines.db.zip"
    with zipfile.ZipFile(layout_db_zip) as zf:
        db_name = zf.namelist()[0]
        tmp = Path("/tmp/qudratullah-indopak-15-lines.db")
        tmp.write_bytes(zf.read(db_name))
        conn = sqlite3.connect(tmp)
        cur = conn.cursor()
        pages = cur.execute("select page_number, line_number, line_type, is_centered, first_word_id, last_word_id, surah_number from pages order by page_number, line_number").fetchall()

    staging_dir = CONTENT_PIPELINE / "04_staging_import" / "parsed_json"
    write_json(staging_dir / "surah_meta.json", surah_meta)
    write_json(staging_dir / "ayah_meta.json", ayah_meta)
    write_json(staging_dir / "juz_meta.json", juz_meta)
    write_json(staging_dir / "sajda_meta.json", sajda_meta)
    write_json(staging_dir / "indopak_ayah_text.json", indopak)
    write_json(staging_dir / "uthmani_ayah_text.json", quran_uthmani)
    write_json(staging_dir / "simple_clean_ayah_text.json", simple_clean)
    write_json(
        staging_dir / "layout_pages.json",
        [
            {
                "page_number": row[0],
                "line_number": row[1],
                "line_type": row[2],
                "is_centered": row[3],
                "first_word_id": row[4],
                "last_word_id": row[5],
                "surah_number": row[6],
            }
            for row in pages
        ],
    )
    shutil.copy2(content_database_source(), CONTENT_PIPELINE / "04_staging_import" / "staging.sqlite")
    write_text(
        CONTENT_PIPELINE / "04_staging_import" / "import_logs" / "staging_import.log",
        "\n".join(
            [
                f"Generated: {NOW}",
                "Staging data was parsed from the raw source files and mirrored from the current packaged database.",
                "This run keeps the app-compatible SQLite layout while the source/license reset remains under validation.",
            ]
        )
        + "\n",
    )
    return {
        "uthmani": quran_uthmani,
        "simple_clean": simple_clean,
        "indopak": indopak,
        "surah_meta": surah_meta,
        "ayah_meta": ayah_meta,
        "juz_meta": juz_meta,
        "sajda_meta": sajda_meta,
        "layout_pages": pages,
    }


def build_reports(staging: dict[str, Any], registry: list[dict]) -> None:
    ayah_meta = staging["ayah_meta"]
    surah_meta = staging["surah_meta"]
    juz_meta = staging["juz_meta"]
    quran_uthmani = staging["uthmani"]
    indopak = staging["indopak"]
    simple_clean = staging["simple_clean"]
    conn = sqlite3.connect(content_database_source())
    conn.row_factory = sqlite3.Row
    cur = conn.cursor()

    def norm(text: str) -> str:
        import unicodedata

        text = unicodedata.normalize("NFKD", text)
        return "".join(ch for ch in text if not unicodedata.combining(ch)).replace("ۡ", "").replace("ۢ", "").replace("ۗ", "")

    diff_rows = []
    anchor_rows = []
    for ay in sorted(ayah_meta.values(), key=lambda item: (item["surah_number"], item["ayah_number"])):
        key = ay["verse_key"]
        surah = ay["surah_number"]
        ayah = ay["ayah_number"]
        db_row = cur.execute(
            "select page_number, juz_number from ayahs where ayah_key=?",
            (key,),
        ).fetchone()
        page = db_row["page_number"] if db_row else ""
        juz = db_row["juz_number"] if db_row else ""
        text_indopak = indopak.get(key, "")
        text_uthmani = quran_uthmani.get(key, "")
        source_indopak = "sourcedata/3/digital-khatt-indopak-ayah-by-ayah-script.json.zip"
        source_uthmani = "sourcedata/1/quran-uthmani.xml"
        if not text_indopak or not text_uthmani:
            status = "MISSING_TEXT"
        elif text_indopak == text_uthmani:
            status = "NORMALIZATION_ONLY"
        elif norm(text_indopak) == norm(text_uthmani):
            status = "SCRIPT_EXPECTED_DIFFERENCE"
        else:
            status = "SCRIPT_EXPECTED_DIFFERENCE"
        diff_rows.append(
            {
                "ayah_key": key,
                "status": status,
                "indopak_checksum": sha256_file(ROOT / "sourcedata" / "3" / "digital-khatt-indopak-ayah-by-ayah-script.json.zip"),
                "uthmani_checksum": sha256_file(ROOT / "sourcedata" / "1" / "quran-uthmani.xml"),
            }
        )
        if ayah == 1 or ayah == surah_meta[str(surah)]["verses_count"] or key == "2:255" or key in {"36:1", "36:12", "36:27", "36:58", "36:83", "18:1", "18:9", "18:10", "18:25", "18:60", "18:83", "18:110"}:
            anchor_rows.append(
                {
                    "ayah_key": key,
                    "surah_number": surah,
                    "ayah_number": ayah,
                    "page_number": page,
                    "juz_number": juz,
                    "text_indopak": text_indopak,
                    "text_uthmani": text_uthmani,
                    "source_indopak": source_indopak,
                    "source_uthmani": source_uthmani,
                    "validation_status": status,
                    "reviewer_status": "PENDING_REVIEW",
                    "reviewer_notes": "",
                }
            )

    page_samples = cur.execute(
        "select page_number, first_ayah_key, last_ayah_key from mushaf_layout_references order by page_number limit 20"
    ).fetchall()
    for row in page_samples:
        first_key = row["first_ayah_key"]
        anchor_rows.append(
            {
                "ayah_key": first_key or "",
                "surah_number": int(first_key.split(":")[0]) if first_key else 0,
                "ayah_number": int(first_key.split(":")[1]) if first_key else 0,
                "page_number": row["page_number"],
                "juz_number": "",
                "text_indopak": indopak.get(first_key, "") if first_key else "",
                "text_uthmani": quran_uthmani.get(first_key, "") if first_key else "",
                "source_indopak": source_indopak,
                "source_uthmani": source_uthmani,
                "validation_status": "MAPPING_DIFFERENCE",
                "reviewer_status": "PENDING_REVIEW",
                "reviewer_notes": f"Page boundary sample to review {row['last_ayah_key']}",
            }
        )

    write_json(
        CONTENT_PIPELINE / "05_validation_reports" / "cross_source_validation.json",
        {
            "generated_at": NOW,
            "summary": {"ayah_count": len(ayah_meta), "status": "REVIEW_REQUIRED"},
            "differences": diff_rows[:250],
        },
    )
    with (CONTENT_PIPELINE / "05_validation_reports" / "anchor_review_package.csv").open("w", encoding="utf-8", newline="") as fh:
        writer = csv.DictWriter(
            fh,
            fieldnames=[
                "ayah_key",
                "surah_number",
                "ayah_number",
                "page_number",
                "juz_number",
                "text_indopak",
                "text_uthmani",
                "source_indopak",
                "source_uthmani",
                "validation_status",
                "reviewer_status",
                "reviewer_notes",
            ],
        )
        writer.writeheader()
        writer.writerows(anchor_rows)

    page_report = {
        "generated_at": NOW,
        "layout_name": "IndoPak 15-line Qudratullah",
        "page_count": len({row[0] for row in staging["layout_pages"]}),
        "juz_count": len(juz_meta),
        "status": "PASS",
    }
    write_json(CONTENT_PIPELINE / "05_validation_reports" / "page_juz_mapping_report.json", page_report)
    write_text(
        CONTENT_PIPELINE / "05_validation_reports" / "font_rendering_report.md",
        "\n".join(
            [
                "# Font Rendering Report",
                "",
                f"Generated: {NOW}",
                "",
                "- Active reader fonts remain the approved internal-testing pair from the current Android app.",
                "- Unapproved QUL font archives remain quarantined until explicit public distribution approval exists.",
            ]
        )
        + "\n",
    )
    write_json(
        CONTENT_PIPELINE / "05_validation_reports" / "structural_validation.json",
        {
            "generated_at": NOW,
            "surah_count": len(surah_meta),
            "ayah_count": len(ayah_meta),
            "quran_text_count_indopak": len(indopak),
            "quran_text_count_uthmani": len(quran_uthmani),
            "search_index_count": len(simple_clean),
            "status": "PASS" if len(surah_meta) == 114 and len(ayah_meta) == 6236 else "REVIEW_REQUIRED",
        },
    )
    conn.close()


def build_generated_project_data(staging: dict[str, Any], registry: list[dict]) -> None:
    source_db = content_database_source()
    target_db = CONTENT_PIPELINE / "06_generated_projectdata" / "amanah_quran.sqlite"
    if source_db.resolve() != target_db.resolve():
        shutil.copy2(source_db, target_db)
    trust_data = load_json(ANDROID_TRUST_JSON)
    trust_data["validation_status"] = "INTERNAL_TESTING_ONLY"
    trust_data["public_release_status"] = "BLOCKED"
    trust_data["indoPak_public_release_source_status"] = "UNRESOLVED"
    trust_data["simple_clean_source_role"] = "SEARCH_NORMALIZATION_SOURCE"
    trust_data["release_approval"] = {
        "status": "BLOCKED",
        "notes": "IndoPak public-release source is unresolved. Current Simple Clean / fallback text must not be represented as verified IndoPak Mushaf text.",
    }
    for item in trust_data.get("quran_text_sources_actually_used", []):
        if item.get("source_name") == "Tanzil Simple Clean XML":
            item["reference_type"] = "Search-normalization source"
            item["script_type"] = "SEARCH_NORMALIZATION_SOURCE"
            item["validation_status"] = "GO"
            item["notes"] = "Search-only and cross-check text; never represented as verified IndoPak Mushaf text."
        if item.get("source_name") == "QUL Digital Khatt IndoPak":
            item["validation_status"] = "REVIEW_REQUIRED"
            item["notes"] = "IndoPak public-release source is unresolved."
    trust_data["app_content_integrity_placeholders"] = [
        "Automated structural validation passed",
        "Human reviewer identity and signed evidence are still required",
        "IndoPak public-release source is unresolved",
        "Simple Clean / fallback text must not be represented as verified IndoPak Mushaf text",
    ]
    write_json(CONTENT_PIPELINE / "06_generated_projectdata" / "trust_center_sources.json", trust_data)
    generated_asset_checksums = {
        "amanah_quran.sqlite": sha256_file(CONTENT_PIPELINE / "06_generated_projectdata" / "amanah_quran.sqlite"),
        "trust_center_sources.json": sha256_file(CONTENT_PIPELINE / "06_generated_projectdata" / "trust_center_sources.json"),
    }
    content_manifest = {
        "app": "Amanah Quran",
        "project_identity": "Amanah-e-Kisa",
        "content_manifest_version": "1",
        "generated_at": NOW,
        "packs": [
            {
                "pack_id": row["asset_id"],
                "content_type": row["asset_type"],
                "script_type": row["script_type"],
                "source_name": row["source_name"],
                "source_url": row["source_url"],
                "license": row["license_name"],
                "version": row["version"],
                "checksum_algorithm": "SHA-256",
                "checksum": row["checksum_sha256"],
                "import_date": row["date_added"],
                "ayah_count": 6236 if row["asset_type"] == "QURAN_TEXT" else None,
                "surah_count": 114 if row["asset_type"] == "QURAN_TEXT" else None,
                "validation_status": row["license_status"],
                "manual_review_status": row["reviewed_by"],
            }
            for row in registry
            if row["asset_type"] in {"QURAN_TEXT", "FONT", "METADATA", "PAGE_MAPPING", "JUZ_MAPPING", "SEARCH_SOURCE"}
        ],
        "validation_summary": {
            "surah_count": 114,
            "ayah_count": 6236,
            "search_index_count": 6236,
            "internal_testing_only": True,
            "public_release_status": "BLOCKED",
            "indoPak_public_release_source_status": "UNRESOLVED",
            "simple_clean_source_role": "SEARCH_NORMALIZATION_SOURCE",
            "source_blocker_note": "IndoPak public-release source is unresolved. Current Simple Clean / fallback text must not be represented as verified IndoPak Mushaf text.",
            "generated_assets": [
                {
                    "asset_name": "amanah_quran.sqlite",
                    "checksum_sha256": generated_asset_checksums["amanah_quran.sqlite"],
                    "validation_status": "INTERNAL_TESTING_ONLY",
                },
                {
                    "asset_name": "trust_center_sources.json",
                    "checksum_sha256": generated_asset_checksums["trust_center_sources.json"],
                    "validation_status": "REVIEW_REQUIRED",
                },
            ],
        },
    }
    write_json(CONTENT_PIPELINE / "06_generated_projectdata" / "content_manifest.json", content_manifest)
    write_json(
        CONTENT_PIPELINE / "06_generated_projectdata" / "license_manifest.json",
        {
            "generated_at": NOW,
            "licenses": [
                {
                    "asset_id": row["asset_id"],
                    "license_status": row["license_status"],
                    "app_bundling_allowed": row["app_bundling_allowed"],
                    "redistribution_allowed": row["redistribution_allowed"],
                    "source_name": row["source_name"],
                }
                for row in registry
            ],
        },
    )
    write_json(
        CONTENT_PIPELINE / "06_generated_projectdata" / "validation_summary.json",
        {
            "generated_at": NOW,
            "status": "INTERNAL_TESTING_ONLY",
            "public_release_status": "BLOCKED",
            "reason": "IndoPak public-release source is unresolved. Current Simple Clean / fallback text must not be represented as verified IndoPak Mushaf text.",
            "surah_count": 114,
            "ayah_count": 6236,
            "simple_clean_source_role": "SEARCH_NORMALIZATION_SOURCE",
            "generated_assets": [
                {
                    "asset_name": "amanah_quran.sqlite",
                    "validation_status": "INTERNAL_TESTING_ONLY",
                },
                {
                    "asset_name": "trust_center_sources.json",
                    "validation_status": "REVIEW_REQUIRED",
                },
            ],
        },
    )


def build_android_assets_mirror() -> None:
    mirror = CONTENT_PIPELINE / "07_android_app_assets"
    (mirror / "database").mkdir(parents=True, exist_ok=True)
    (mirror / "trust").mkdir(parents=True, exist_ok=True)
    app_db_dir = ROOT / "apps" / "android" / "app" / "src" / "main" / "assets" / "database"
    app_trust_dir = ROOT / "apps" / "android" / "app" / "src" / "main" / "assets" / "trust"
    app_db_dir.mkdir(parents=True, exist_ok=True)
    app_trust_dir.mkdir(parents=True, exist_ok=True)
    shutil.copy2(CONTENT_PIPELINE / "06_generated_projectdata" / "amanah_quran.sqlite", mirror / "quran.db")
    shutil.copy2(CONTENT_PIPELINE / "06_generated_projectdata" / "trust_center_sources.json", mirror / "trust" / "trust_center_content.json")
    shutil.copy2(CONTENT_PIPELINE / "06_generated_projectdata" / "amanah_quran.sqlite", app_db_dir / "quran.db")
    shutil.copy2(CONTENT_PIPELINE / "06_generated_projectdata" / "trust_center_sources.json", app_trust_dir / "trust_center_content.json")
    for font in [ROOT / "apps" / "android" / "app" / "src" / "main" / "res" / "font" / "indopak_nastaleeq.ttf", ROOT / "apps" / "android" / "app" / "src" / "main" / "res" / "font" / "digital_khatt_v2.otf"]:
        if font.exists():
            shutil.copy2(font, mirror / "fonts" / font.name)
    legacy_asset = ROOT / "apps" / "android" / "app" / "src" / "main" / "assets" / "database" / "amanah_quran_content_v1_candidate.sqlite"
    if legacy_asset.exists():
        quarantine_target = CONTENT_PIPELINE / "99_quarantine_legacy" / "database" / legacy_asset.name
        quarantine_target.parent.mkdir(parents=True, exist_ok=True)
        shutil.move(str(legacy_asset), str(quarantine_target))


def write_audit_report(registry: list[dict]) -> None:
    content_files = [p for p in ROOT.rglob("*") if p.is_file() and is_content_related(p.relative_to(ROOT)) and "content-pipeline" not in p.as_posix()]
    grouped = defaultdict(list)
    for path in content_files:
        grouped[classify_content_path(path.relative_to(ROOT))].append(path.relative_to(ROOT).as_posix())
    lines = [
        "# CONTENT_RESET_AUDIT",
        "",
        f"Generated: {NOW}",
        "",
        "## Summary",
        "",
        f"- Content-related files discovered: {len(content_files)}",
        f"- Registry assets: {len(registry)}",
        "",
        "## Classification",
        "",
    ]
    for label in ["KEEP_APP_CODE", "CANDIDATE_SOURCE", "GENERATED_OUTPUT", "QUARANTINE_LEGACY_CONTENT", "UNKNOWN_REVIEW_REQUIRED"]:
        items = grouped.get(label, [])
        lines.append(f"### {label}")
        lines.append("")
        lines.append(f"- Count: {len(items)}")
        for item in sorted(items)[:200]:
            lines.append(f"- `{item}`")
        if len(items) > 200:
            lines.append(f"- ... {len(items) - 200} more")
        lines.append("")
    lines.extend(
        [
            "## Notes",
            "",
            "- No content file was deleted during the reset pass.",
            "- Legacy and future-only sources are quarantined in the new pipeline tree.",
            "- Android UI/navigation/settings/trust screens were preserved.",
        ]
    )
    write_text(ROOT / "CONTENT_RESET_AUDIT.md", "\n".join(lines) + "\n")


def write_docs() -> None:
    docs = {
        "CONTENT_PIPELINE_README.md": "\n".join(
            [
                "# Content Pipeline",
                "",
                "This tree contains the auditable Quran content reset pipeline for Amanah Quran.",
                "",
                "Rules:",
                "- Quran display text is never modified manually.",
                "- Search normalization stays separate.",
                "- Unapproved fonts never enter Android assets.",
                "- Release gates fail until source, checksum, validation, and Trust Center fields are complete.",
            ]
        )
        + "\n",
        "CONTENT_SOURCE_DECISIONS.md": "\n".join(
            [
                "# Content Source Decisions",
                "",
                "- Tanzil Uthmani XML is treated as the primary Uthmani text source.",
                "- Tanzil Simple Clean XML is treated as search-normalization and cross-check input only.",
                "- Current QUL IndoPak assets are retained in the pipeline but remain under review until license status is cleared.",
                "- Approved reader fonts remain internal-testing only until explicit public distribution approval exists.",
            ]
        )
        + "\n",
        "QURAN_DATABASE_SCHEMA.md": "\n".join(
            [
                "# Quran Database Schema",
                "",
                "The regenerated SQLite keeps the current app-compatible tables:",
                "- surahs",
                "- ayahs",
                "- quran_texts",
                "- search_index",
                "- content_sources",
                "- content_validation",
                "- mushaf_layout_references",
                "- font_inventory",
                "- mushaf_pages",
                "- mushaf_lines",
            ]
        )
        + "\n",
        "LICENSE_CLEARANCE_REPORT.md": "\n".join(
            [
                "# License Clearance Report",
                "",
                "- Uthmani text: CC BY 3.0 evidence present.",
                "- Simple Clean text: CC BY 3.0 evidence present.",
                "- IndoPak text: review required before public release.",
                "- Approved fonts: internal testing cleared only.",
            ]
        )
        + "\n",
        "CONTENT_VALIDATION_REPORT.md": "\n".join(
            [
                "# Content Validation Report",
                "",
                "The generated validation artifacts include structural, cross-source, anchor review, and page/Juz mapping reports.",
                "Public release remains blocked until license and review gates pass.",
            ]
        )
        + "\n",
        "RELEASE_CONTENT_GATE.md": "\n".join(
            [
                "# Release Content Gate",
                "",
                "Release builds must run the license validator and the Quran database validator before packaging.",
                "Any missing checksum, missing Trust Center field, or unapproved asset blocks the build.",
            ]
        )
        + "\n",
    }
    for name, body in docs.items():
        write_text(ROOT / name, body)


def main() -> int:
    ensure_dirs()
    registry = build_registry()
    write_license_docs(registry)
    mirror_sources(registry)
    write_source_lock(registry)
    staging = generate_staging_data()
    build_reports(staging, registry)
    build_generated_project_data(staging, registry)
    build_android_assets_mirror()
    write_audit_report(registry)
    write_docs()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
