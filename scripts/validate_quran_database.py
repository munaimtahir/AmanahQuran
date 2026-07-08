#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
import sqlite3
from collections import Counter
from datetime import datetime, timezone
from pathlib import Path

from content_pipeline_common import CONTENT_PIPELINE


DISPLAY_SOURCE_FOLDERS = {1, 3}
SEARCH_SOURCE_FOLDERS = {2}


def now_utc() -> str:
    return datetime.now(timezone.utc).replace(microsecond=0).isoformat()


def main() -> int:
    parser = argparse.ArgumentParser(description="Validate the Amanah Quran SQLite database.")
    parser.add_argument(
        "--database",
        type=Path,
        default=CONTENT_PIPELINE / "06_generated_projectdata" / "amanah_quran.sqlite",
    )
    parser.add_argument(
        "--trust",
        type=Path,
        default=CONTENT_PIPELINE / "06_generated_projectdata" / "trust_center_sources.json",
    )
    parser.add_argument(
        "--report",
        type=Path,
        default=CONTENT_PIPELINE / "05_validation_reports" / "database_validation.json",
    )
    args = parser.parse_args()

    conn = sqlite3.connect(args.database)
    conn.row_factory = sqlite3.Row
    cur = conn.cursor()
    failures: list[dict] = []
    categories = Counter()

    def count(sql: str, params: tuple = ()) -> int:
        row = cur.execute(sql, params).fetchone()
        return int(row[0]) if row else 0

    try:
        surah_count = count("select count(*) from surahs")
        ayah_count = count("select count(*) from ayahs")
        uthmani_rows = count("select count(*) from quran_texts where script_type='UTHMANI'")
        indopak_rows = count("select count(*) from quran_texts where script_type='INDOPAK'")
        search_rows = count("select count(*) from search_index")
        content_sources = count("select count(*) from content_sources")
        content_validation = count("select count(*) from content_validation")
        empty_uthmani = count("select count(*) from quran_texts where script_type='UTHMANI' and trim(display_text)=''")
        empty_indopak = count("select count(*) from quran_texts where script_type='INDOPAK' and trim(display_text)=''")
        duplicate_ayahs = count("select count(*) from (select ayah_key from ayahs group by ayah_key having count(*) > 1)")
        missing_ayah_key = count("select count(*) from ayahs where ayah_key is null or trim(ayah_key)=''")
        missing_page = count("select count(*) from ayahs where page_number is null")
        missing_juz = count("select count(*) from ayahs where juz_number is null")
        source_checksum_missing = count("select count(*) from content_sources where sha256 is null or trim(sha256)=''")
        source_license_missing = count("select count(*) from content_sources where license_status is null or trim(license_status)=''")
        independent_source_checksums = count(
            "select count(distinct source_folder_number) from content_sources where sha256 is not null and trim(sha256)<>'' and source_folder_number in (1,2,3)"
        )
        trust_json = json.loads(args.trust.read_text(encoding="utf-8"))
        trust_missing = [key for key in ["generated_at", "no_modification_statement", "privacy_pledge", "quran_text_sources_actually_used", "source_references", "mushaf_page_layout", "release_approval", "app_version"] if key not in trust_json or trust_json[key] in (None, "", [])]

        row_status = []
        for row in cur.execute(
            """
            select q.ayah_key, q.script_type, q.display_text, q.source_id, s.normalized_arabic
            from quran_texts q
            join search_index s on s.ayah_key = q.ayah_key
            order by q.ayah_key, q.script_type
            """
        ):
            source_id = int(row["source_id"] or 0)
            display_text = row["display_text"] or ""
            normalized_arabic = row["normalized_arabic"] or ""
            if source_id == 0:
                status = "DISPLAY_TEXT_SOURCE_MISSING"
            elif source_id in SEARCH_SOURCE_FOLDERS:
                status = "DISPLAY_TEXT_DERIVED_FROM_SEARCH_INDEX"
            elif source_id in DISPLAY_SOURCE_FOLDERS:
                status = "NORMALIZATION_NO_CHANGE_EXPECTED" if display_text == normalized_arabic else "DISPLAY_TEXT_SOURCE_OK"
            else:
                status = "DISPLAY_TEXT_SOURCE_MISSING"
            categories[status] += 1
            row_status.append(
                {
                    "ayah_key": row["ayah_key"],
                    "script_type": row["script_type"],
                    "source_id": source_id,
                    "status": status,
                    "display_text": display_text,
                    "normalized_arabic": normalized_arabic,
                }
            )

        source_lineage_missing = sum(1 for item in row_status if item["status"] == "DISPLAY_TEXT_SOURCE_MISSING")
        derived_from_search = sum(1 for item in row_status if item["status"] == "DISPLAY_TEXT_DERIVED_FROM_SEARCH_INDEX")
        if surah_count != 114:
            failures.append({"check": "surah_count", "expected": 114, "actual": surah_count})
        if ayah_count != 6236:
            failures.append({"check": "ayah_count", "expected": 6236, "actual": ayah_count})
        if duplicate_ayahs != 0:
            failures.append({"check": "duplicate_ayah_keys", "expected": 0, "actual": duplicate_ayahs})
        if missing_ayah_key != 0:
            failures.append({"check": "missing_ayah_keys", "expected": 0, "actual": missing_ayah_key})
        if empty_indopak != 0:
            failures.append({"check": "empty_indopak_display_text", "expected": 0, "actual": empty_indopak})
        if empty_uthmani != 0:
            failures.append({"check": "empty_uthmani_display_text", "expected": 0, "actual": empty_uthmani})
        if missing_page != 0:
            failures.append({"check": "missing_page_number", "expected": 0, "actual": missing_page})
        if missing_juz != 0:
            failures.append({"check": "missing_juz_number", "expected": 0, "actual": missing_juz})
        if search_rows != ayah_count:
            failures.append({"check": "search_index_count", "expected": ayah_count, "actual": search_rows})
        if trust_missing:
            failures.append({"check": "trust_center_metadata", "expected": "present", "actual": trust_missing})
        if source_checksum_missing != 0:
            failures.append({"check": "content_source_checksum_missing", "expected": 0, "actual": source_checksum_missing})
        if source_license_missing != 0:
            failures.append({"check": "content_source_license_missing", "expected": 0, "actual": source_license_missing})
        if independent_source_checksums < 3:
            failures.append({"check": "indopak_uthmani_search_source_checksums", "expected": 3, "actual": independent_source_checksums})
        if source_lineage_missing != 0:
            failures.append({"check": "display_text_source_missing", "expected": 0, "actual": source_lineage_missing})
        if derived_from_search != 0:
            failures.append({"check": "display_text_derived_from_search_index", "expected": 0, "actual": derived_from_search})
    finally:
        conn.close()

    report = {
        "generated_at": now_utc(),
        "database": str(args.database),
        "trust": str(args.trust),
        "counts": {
            "surahs": surah_count,
            "ayahs": ayah_count,
            "quran_texts_uthmani": uthmani_rows,
            "quran_texts_indopak": indopak_rows,
            "search_index": search_rows,
            "content_sources": content_sources,
            "content_validation": content_validation,
        },
        "categories": dict(categories),
        "failure_count": len(failures),
        "failures": failures,
        "result": "PASS" if not failures else "FAIL",
    }
    args.report.parent.mkdir(parents=True, exist_ok=True)
    args.report.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    if failures:
        print(json.dumps(report, ensure_ascii=False, indent=2))
        return 1
    print("PASS: Quran database validated")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
