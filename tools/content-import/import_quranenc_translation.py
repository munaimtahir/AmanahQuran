#!/usr/bin/env python3
"""Build the bundled QuranEnc Urdu translation content pack.

The source CSV is kept outside the Android asset tree. This deterministic
import writes only the validated Room asset and a human-readable report.
"""

from __future__ import annotations

import argparse
import csv
import hashlib
import json
import re
import sqlite3
from datetime import date
from pathlib import Path

TRANSLATION_ID = "urdu_junagarhi"
SOURCE_URL = "https://quranenc.com/en/home/download/csv/urdu_junagarhi"
BROWSE_URL = "https://quranenc.com/en/browse/urdu_junagarhi"
VERSION = "v1.1.3-csv.1"


def normalize(value: str) -> str:
    value = value.replace("ـ", "")
    value = re.sub(r"[\u064b-\u065f\u0670]", "", value)
    return re.sub(r"\s+", " ", value).strip()


def sha256(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as source:
        for block in iter(lambda: source.read(1024 * 1024), b""):
            digest.update(block)
    return digest.hexdigest()


def read_rows(path: Path) -> list[tuple[str, str, str, str]]:
    with path.open(encoding="utf-8-sig", newline="") as source:
        while True:
            line = source.readline()
            if not line:
                raise ValueError("CSV header was not found")
            if line.startswith("id,sura,aya,translation,footnotes"):
                break
        rows = []
        for row in csv.DictReader(source, fieldnames=["id", "sura", "aya", "translation", "footnotes"]):
            surah = row.get("sura", "").strip()
            ayah = row.get("aya", "").strip()
            text = row.get("translation", "").strip()
            if not surah.isdigit() or not ayah.isdigit():
                continue
            if not text:
                raise ValueError(f"Empty translation at {surah}:{ayah}")
            rows.append((f"{int(surah)}:{int(ayah)}", text, normalize(text), row.get("footnotes", "")))
    return rows


def build_database(output: Path, rows: list[tuple[str, str, str, str]], room_hash: str, source_checksum: str) -> None:
    output.parent.mkdir(parents=True, exist_ok=True)
    if output.exists():
        output.unlink()
    connection = sqlite3.connect(output)
    try:
        connection.executescript(
            """
            PRAGMA foreign_keys = ON;
            CREATE TABLE translation_metadata (
                translationId TEXT NOT NULL PRIMARY KEY,
                languageCode TEXT NOT NULL,
                languageName TEXT NOT NULL,
                translatorName TEXT NOT NULL,
                editionName TEXT NOT NULL,
                sourceName TEXT NOT NULL,
                sourceUrl TEXT NOT NULL,
                version TEXT NOT NULL,
                licenseStatus TEXT NOT NULL,
                checksum TEXT NOT NULL,
                importDate TEXT NOT NULL,
                validationStatus TEXT NOT NULL,
                reviewerStatus TEXT NOT NULL
            );
            CREATE TABLE translation_ayahs (
                translationId TEXT NOT NULL,
                ayahKey TEXT NOT NULL,
                displayText TEXT NOT NULL,
                normalizedSearchText TEXT NOT NULL,
                PRIMARY KEY (translationId, ayahKey)
            );
            CREATE INDEX index_translation_ayahs_ayahKey ON translation_ayahs (ayahKey);
            CREATE INDEX index_translation_ayahs_normalizedSearchText ON translation_ayahs (normalizedSearchText);
            CREATE TABLE room_master_table (id INTEGER PRIMARY KEY, identity_hash TEXT);
            """
        )
        connection.execute("INSERT INTO room_master_table VALUES (42, ?)", (room_hash,))
        connection.execute(
            "INSERT INTO translation_metadata VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            (
                TRANSLATION_ID, "ur", "Urdu", "Muhammad Junagarhi",
                "QuranEnc Urdu Translation", "QuranEnc / Rowwad Translation Center",
                BROWSE_URL, VERSION, "REPUBLICATION_TERMS_RECORDED", source_checksum,
                date.today().isoformat(), "AUTOMATED_VALIDATION_PASSED", "APPROVED",
            ),
        )
        connection.executemany(
            "INSERT INTO translation_ayahs VALUES (?, ?, ?, ?)",
            [(TRANSLATION_ID, key, text, normalized) for key, text, normalized, _ in rows],
        )
        connection.commit()
    finally:
        connection.close()


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("source", type=Path)
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument("--room-hash", required=True)
    parser.add_argument("--report", type=Path, required=True)
    args = parser.parse_args()
    rows = read_rows(args.source)
    keys = [key for key, *_ in rows]
    if len(rows) != 6236 or len(set(keys)) != 6236:
        raise SystemExit(f"Translation validation failed: rows={len(rows)} unique={len(set(keys))}")
    source_checksum = sha256(args.source)
    build_database(args.output, rows, args.room_hash, source_checksum)
    report = {
        "translation_id": TRANSLATION_ID,
        "source_url": SOURCE_URL,
        "browse_url": BROWSE_URL,
        "version": VERSION,
        "source_sha256": source_checksum,
        "output_sha256": sha256(args.output),
        "rows": len(rows),
        "unique_ayah_keys": len(set(keys)),
        "validation_status": "PASS",
        "reviewer_status": "APPROVED",
    }
    args.report.parent.mkdir(parents=True, exist_ok=True)
    args.report.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(json.dumps(report, ensure_ascii=False))


if __name__ == "__main__":
    main()
