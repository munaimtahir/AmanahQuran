#!/usr/bin/env python3
"""Shared helpers for the Amanah Quran content reset pipeline."""

from __future__ import annotations

import csv
import hashlib
import json
import os
import shutil
from dataclasses import dataclass
from datetime import datetime, timezone
from pathlib import Path
from typing import Any, Iterable


ROOT = Path(__file__).resolve().parents[1]
SOURCE_DATA = ROOT / "sourcedata"
PROJECTDATA = ROOT / "projectdata" / "managed"
ANDROID_ASSET_DB = ROOT / "apps" / "android" / "app" / "src" / "main" / "assets" / "database" / "quran.db"
ANDROID_TRUST_JSON = ROOT / "apps" / "android" / "app" / "src" / "main" / "assets" / "trust" / "trust_center_content.json"
CONTENT_PIPELINE = ROOT / "content-pipeline"
NOW = datetime.now(timezone.utc).replace(microsecond=0).isoformat()


def load_json(path: Path) -> Any:
    return json.loads(path.read_text(encoding="utf-8"))


def write_json(path: Path, data: Any) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def write_text(path: Path, text: str) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(text, encoding="utf-8")


def sha256_file(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as handle:
        for chunk in iter(lambda: handle.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def hardlink_or_copy(src: Path, dst: Path) -> None:
    dst.parent.mkdir(parents=True, exist_ok=True)
    if dst.exists():
        return
    try:
        os.link(src, dst)
    except OSError:
        shutil.copy2(src, dst)


def rel(path: Path) -> str:
    return path.relative_to(ROOT).as_posix()


def is_content_related(path: Path) -> bool:
    text = path.as_posix()
    return (
        text.startswith("sourcedata/")
        or text.startswith("projectdata/managed/")
        or text.startswith("apps/android/app/src/main/assets/")
        or text.startswith("apps/android/app/src/main/res/font/")
        or text.startswith("docs/_ai_quran_audit")
        or text.startswith("docs/_release_gate")
        or text.startswith("docs/_implementation")
        or text.startswith("docs/content/")
        or text.startswith("docs/legal/")
        or text.startswith("tools/content-import/")
        or text.startswith("tools/validation/")
        or text.startswith("scripts/")
    )


def classify_content_path(path: Path) -> str:
    text = path.as_posix()
    if text.startswith("apps/android/app/src/main/kotlin/") or text.startswith("apps/android/app/src/test/") or text.endswith(
        ("build.gradle.kts", "settings.gradle.kts", "gradle.properties", "proguard-rules.pro")
    ):
        return "KEEP_APP_CODE"
    if text.startswith("sourcedata/4/") or text.startswith("sourcedata/7/") or text.startswith("sourcedata/10/"):
        return "QUARANTINE_LEGACY_CONTENT"
    if text.startswith("sourcedata/"):
        return "CANDIDATE_SOURCE"
    if text.startswith("apps/android/app/src/main/assets/") or text.startswith("apps/android/app/src/main/res/font/"):
        return "GENERATED_OUTPUT"
    if text.startswith("projectdata/managed/") or text.startswith("docs/_ai_quran_audit") or text.startswith("docs/_release_gate") or text.startswith(
        "docs/_implementation"
    ) or text.startswith("docs/content/") or text.startswith("docs/legal/"):
        return "GENERATED_OUTPUT"
    if text.startswith("tools/content-import/") or text.startswith("tools/validation/") or text.startswith("scripts/"):
        return "KEEP_APP_CODE"
    return "UNKNOWN_REVIEW_REQUIRED"


def source_folder_number(path: Path) -> int | None:
    parts = path.relative_to(SOURCE_DATA).parts
    if not parts:
        return None
    try:
        return int(parts[0])
    except ValueError:
        return None


def asset_type_from_source_row(row: dict[str, Any]) -> str:
    folder = row.get("source_folder_number")
    file_name = (row.get("original_file_name") or "").lower()
    category = (row.get("content_category") or "").lower()
    if folder == 1:
        return "QURAN_TEXT"
    if folder == 2:
        return "SEARCH_SOURCE"
    if folder in (3, 4):
        return "QURAN_TEXT"
    if "layout" in category or "page" in file_name or folder == 6:
        return "PAGE_MAPPING"
    if "juz" in file_name or "hizb" in file_name:
        return "JUZ_MAPPING" if "juz" in file_name else "METADATA"
    if folder == 5:
        return "METADATA"
    if folder == 8:
        return "FONT"
    if folder == 10:
        return "SEARCH_SOURCE"
    return "UNKNOWN"


def script_type_from_source_row(row: dict[str, Any]) -> str:
    script = row.get("script_type")
    if script in ("INDOPAK", "UTHMANI"):
        return script
    folder = row.get("source_folder_number")
    if folder in (1, 2):
        return "UTHMANI" if folder == 1 else "NOT_APPLICABLE"
    if folder in (3, 4):
        return "INDOPAK"
    return "NOT_APPLICABLE"


def license_status_to_flags(license_status: str, asset_type: str) -> dict[str, Any]:
    normalized = (license_status or "").strip().lower()
    public = normalized.startswith("creative commons attribution 3.0") or normalized.startswith("cc by 3.0")
    internal_testing = normalized in {"cleared", "requires review"} or "internal testing" in normalized
    if asset_type == "FONT":
        return {
            "license_name": license_status or "UNKNOWN",
            "redistribution_allowed": "unknown",
            "modification_allowed": "unknown",
            "commercial_use_allowed": "unknown",
            "app_bundling_allowed": True if internal_testing else "unknown",
        }
    if public:
        return {
            "license_name": "CC BY 3.0",
            "redistribution_allowed": True,
            "modification_allowed": True,
            "commercial_use_allowed": True,
            "app_bundling_allowed": True,
        }
    if normalized == "unknown":
        return {
            "license_name": "UNKNOWN",
            "redistribution_allowed": "unknown",
            "modification_allowed": "unknown",
            "commercial_use_allowed": "unknown",
            "app_bundling_allowed": "unknown",
        }
    return {
        "license_name": license_status or "UNKNOWN",
        "redistribution_allowed": "unknown",
        "modification_allowed": "unknown",
        "commercial_use_allowed": "unknown",
        "app_bundling_allowed": "unknown",
    }


def stable_asset_id(row: dict[str, Any]) -> str:
    folder = row.get("source_folder_number")
    name = row.get("original_file_name") or row.get("file_name") or "asset"
    return f"{folder}:{name}".replace(" ", "_")


def current_datetime() -> str:
    return NOW


def read_source_inventory() -> list[dict[str, Any]]:
    inventory_path = PROJECTDATA / "source_inventory.md"
    if not inventory_path.exists():
        return []
    rows: list[dict[str, Any]] = []
    for line in inventory_path.read_text(encoding="utf-8").splitlines():
        if not line.startswith("| ") or line.startswith("| # "):
            continue
        parts = [part.strip() for part in line.strip("|").split("|")]
        if len(parts) < 8:
            continue
        try:
            source_number = int(parts[0])
        except ValueError:
            continue
        raw_file = parts[1].strip("`")
        rows.append(
            {
                "source_folder_number": source_number,
                "original_file_path": raw_file,
                "original_file_name": Path(raw_file).name,
            }
        )
    return rows


def load_packaged_database() -> tuple[Any, Any]:
    import sqlite3

    conn = sqlite3.connect(ANDROID_ASSET_DB)
    conn.row_factory = sqlite3.Row
    return conn, conn.cursor()


def query_one(cur, sql: str, params: Iterable[Any] = ()) -> Any:
    row = cur.execute(sql, tuple(params)).fetchone()
    return row[0] if row else None
