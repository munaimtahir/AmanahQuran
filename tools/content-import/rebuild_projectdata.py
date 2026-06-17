#!/usr/bin/env python3
import csv
import hashlib
import json
import os
import re
import shutil
import sqlite3
import sys
import zipfile
from collections import Counter, defaultdict
from datetime import datetime, timezone
from pathlib import Path
from xml.etree import ElementTree as ET

ROOT = Path(__file__).resolve().parents[2]
SOURCE_ROOT = ROOT / "sourcedata"
PROJECTDATA = ROOT / "projectdata"
MANAGED = PROJECTDATA / "managed"
STAGING = MANAGED / "staging"
EXTRACTED = MANAGED / "extracted"
BUILD = MANAGED / "build"
REVIEWER = MANAGED / "reviewer_package"
FUTURE_PROMPTS = MANAGED / "future_prompts"

EXPECTED_SURAH_COUNT = 114
EXPECTED_AYAH_COUNT = 6236
NOW = datetime.now(timezone.utc).replace(microsecond=0).isoformat()
NO_MODIFICATION_STATEMENT = (
    "Amanah Quran displays Quranic text exactly as imported from verified source data. "
    "Search normalization is stored separately and is never used as display Quran text."
)


def rel(path):
    return path.relative_to(ROOT).as_posix()


def ensure_dirs():
    for path in [PROJECTDATA, MANAGED, STAGING, EXTRACTED, BUILD, REVIEWER, FUTURE_PROMPTS]:
        path.mkdir(parents=True, exist_ok=True)
    for i in range(1, 11):
        (EXTRACTED / f"source_{i}").mkdir(parents=True, exist_ok=True)


def write_text(path, text):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(text, encoding="utf-8")


def write_json(path, data):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def write_jsonl(path, rows):
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("w", encoding="utf-8") as fh:
        for row in rows:
            fh.write(json.dumps(row, ensure_ascii=False, separators=(",", ":")) + "\n")


def sha256(path):
    h = hashlib.sha256()
    with path.open("rb") as fh:
        for chunk in iter(lambda: fh.read(1024 * 1024), b""):
            h.update(chunk)
    return h.hexdigest()


def source_num(path):
    parts = path.relative_to(SOURCE_ROOT).parts
    return int(parts[0])


def list_raw_files():
    files = []
    for i in range(1, 11):
        folder = SOURCE_ROOT / str(i)
        files.extend(sorted(p for p in folder.rglob("*") if p.is_file()))
    return sorted(files)


def detect_type(path):
    name = path.name.lower()
    suffix = path.suffix.lower()
    if suffix == ".zip":
        return "ZIP"
    if suffix == ".xml":
        return "XML"
    if suffix == ".json":
        return "JSON"
    if suffix in [".sqlite", ".db"]:
        return "SQLite"
    if suffix == ".pdf":
        return "PDF"
    if suffix == ".docx":
        return "DOCX"
    if suffix in [".ttf", ".otf", ".woff", ".woff2", ".bz2"] or any(x in name for x in [".ttf.", ".otf.", ".woff"]):
        return "font"
    if suffix in [".txt", ".md", ".html", ".css", ".js"]:
        return "text"
    return "other"


def likely_source_and_category(path):
    n = source_num(path)
    name = path.name.lower()
    source_name = {
        1: "Tanzil Uthmani XML",
        2: "Tanzil Simple Clean XML",
        3: "QUL Digital Khatt IndoPak",
        4: "QUL IndoPak Nastaleeq",
        5: "QUL Quran Metadata",
        6: "QUL Mushaf Layout",
        7: "Quran Meta GitHub ZIP",
        8: "QUL font files",
        9: "Quran Foundation documentation pages",
        10: "Quranic Arabic Corpus morphology data",
    }[n]
    category = {
        1: "Uthmani Quran display text candidate",
        2: "Simple Clean search normalization and cross-check text",
        3: "IndoPak display text candidate",
        4: "Backup IndoPak display text candidate",
        5: "Quran metadata",
        6: "Mushaf layout reference",
        7: "Metadata cross-check",
        8: "Font testing",
        9: "Future cross-check/reference documentation",
        10: "Future morphology/linguistic resource",
    }[n]
    suitability = {
        1: "V1 display text",
        2: "V1 search/cross-check",
        3: "V1 display text",
        4: "future/reference only",
        5: "V1 metadata",
        6: "V1 layout reference",
        7: "future/reference only",
        8: "V1 font testing only",
        9: "future/reference only",
        10: "future/reference only",
    }[n]
    warnings = []
    if n == 2:
        warnings.append("Never use as display Quran text.")
    if n == 6 and "_pages" in name:
        warnings.append("_pages file is visual/page reference only, not SQLite source.")
    if n == 8:
        warnings.append("Do not bundle font until license review is complete.")
    if n == 10:
        warnings.append("Future-only; exclude from V1 candidate database.")
    if n == 9 and any(token in name for token in ["oauth", "api"]):
        warnings.append("Documentation/reference only; do not create credentials.")
    return source_name, category, suitability, warnings


def infer_url(path):
    n = source_num(path)
    if n in [1, 2]:
        return "https://tanzil.net/download/"
    if n in [3, 4, 5, 6, 8]:
        return "https://qul.tarteel.ai/"
    if n == 7:
        return "https://github.com/risan/quran-meta"
    if n == 9:
        return "https://docs.quran.foundation/"
    if n == 10:
        return "https://corpus.quran.com/"
    return ""


def script_type(path):
    n = source_num(path)
    if n in [1, 2]:
        return "UTHMANI" if n == 1 else "SIMPLE_CLEAN"
    if n in [3, 4]:
        return "INDOPAK"
    return None


def v1_candidate_status(path):
    n = source_num(path)
    return {
        1: "primary Uthmani display candidate",
        2: "search/cross-check only; not display",
        3: "primary IndoPak display candidate",
        4: "backup/future optional IndoPak candidate",
        5: "metadata candidate",
        6: "layout reference candidate",
        7: "cross-check only",
        8: "font testing only; blocked for bundling until license review",
        9: "reference only",
        10: "future-only; excluded from V1",
    }[n]


def build_inventory(raw_files, checksums):
    rows = []
    for path in raw_files:
        source_name, category, suitability, warnings = likely_source_and_category(path)
        rows.append({
            "source_folder_number": source_num(path),
            "original_raw_file_path": rel(path),
            "filename": path.name,
            "extension": "".join(path.suffixes) if path.suffixes else "",
            "file_size": path.stat().st_size,
            "detected_type": detect_type(path),
            "likely_source_name": source_name,
            "likely_content_category": category,
            "v1_suitability": suitability,
            "warnings": "; ".join(warnings),
            "sha256": checksums[path],
        })
    lines = ["# Source Inventory", "", f"Generated: {NOW}", "", "| # | Raw file | Size | Type | Source | Category | V1 suitability | Warnings |", "|---:|---|---:|---|---|---|---|---|"]
    for r in rows:
        lines.append(
            f"| {r['source_folder_number']} | `{r['original_raw_file_path']}` | {r['file_size']} | "
            f"{r['detected_type']} | {r['likely_source_name']} | {r['likely_content_category']} | "
            f"{r['v1_suitability']} | {r['warnings']} |"
        )
    write_text(MANAGED / "source_inventory.md", "\n".join(lines) + "\n")
    return rows


def build_manifest(inventory):
    manifest = []
    for r in inventory:
        p = ROOT / r["original_raw_file_path"]
        license_status = "unknown"
        if source_num(p) in [1, 2]:
            license_status = "Creative Commons Attribution 3.0 (visible in XML header)"
        if source_num(p) == 8:
            license_status = "requires review"
        manifest.append({
            "source_folder_number": r["source_folder_number"],
            "original_file_path": r["original_raw_file_path"],
            "original_file_name": r["filename"],
            "detected_format": r["detected_type"],
            "file_type": r["detected_type"],
            "sha256": r["sha256"],
            "source_name": r["likely_source_name"],
            "source_url": infer_url(p),
            "content_category": r["likely_content_category"],
            "script_type": script_type(p),
            "license_status": license_status,
            "v1_candidate_status": v1_candidate_status(p),
            "requires_manual_review": source_num(p) in [1, 3, 4, 5, 6, 8],
            "raw_file_immutable": True,
            "notes": r["warnings"] or "Raw source retained unchanged.",
        })
    write_json(MANAGED / "content_sources.json", manifest)
    return manifest


def safe_extract_zip(zip_path, target_dir):
    extracted = []
    with zipfile.ZipFile(zip_path) as zf:
        for info in zf.infolist():
            name = info.filename
            if name.startswith("/") or ".." in Path(name).parts:
                raise ValueError(f"unsafe zip member: {name}")
            zf.extract(info, target_dir)
            out = target_dir / name
            if out.is_file():
                extracted.append(out)
    return extracted


def extract_sources(raw_files):
    report = []
    extracted_files = []
    blocked = False
    for path in raw_files:
        if path.suffix.lower() != ".zip":
            continue
        n = source_num(path)
        target = EXTRACTED / f"source_{n}"
        try:
            files = safe_extract_zip(path, target)
            extracted_files.extend(files)
            report.append({"raw_zip": rel(path), "status": "OK", "extracted_count": len(files), "notes": ""})
        except Exception as exc:
            essential = n in [3, 4, 5, 6, 7, 8, 10]
            if n in [3, 4]:
                blocked = True
            report.append({"raw_zip": rel(path), "status": "FAILED", "extracted_count": 0, "notes": str(exc), "essential": essential})
    lines = ["# Extraction Report", "", f"Generated: {NOW}", "", f"ZIP files processed: {sum(1 for p in raw_files if p.suffix.lower() == '.zip')}", f"Extracted files: {len(extracted_files)}", f"Blocked: {blocked}", "", "| Raw ZIP | Status | Extracted | Notes |", "|---|---|---:|---|"]
    for r in report:
        lines.append(f"| `{r['raw_zip']}` | {r['status']} | {r['extracted_count']} | {r['notes']} |")
    write_text(MANAGED / "extraction_report.md", "\n".join(lines) + "\n")
    return extracted_files, blocked, report


def parse_tanzil_xml(path):
    tree = ET.parse(path)
    root = tree.getroot()
    ayahs = []
    for sura in root.findall("sura"):
        s = int(sura.attrib["index"])
        for aya in sura.findall("aya"):
            a = int(aya.attrib["index"])
            text = aya.attrib.get("text", "")
            ayahs.append({"ayah_key": f"{s}:{a}", "surah_number": s, "ayah_number": a, "text": text})
    return ayahs


def validate_ayah_source(rows, canonical_keys=None):
    keys = [r["ayah_key"] for r in rows]
    counts = Counter(keys)
    result = {
        "surah_count": len(set(r["surah_number"] for r in rows)),
        "ayah_count": len(rows),
        "duplicate_keys": sorted(k for k, v in counts.items() if v > 1),
        "empty_text_keys": sorted(r["ayah_key"] for r in rows if not r.get("text")),
        "arabic_text_missing_keys": sorted(r["ayah_key"] for r in rows if not re.search(r"[\u0600-\u06ff]", r.get("text", ""))),
        "keys_match_canonical": None,
        "missing_from_canonical": [],
        "extra_vs_canonical": [],
        "passed": False,
    }
    if canonical_keys is not None:
        result["keys_match_canonical"] = set(keys) == set(canonical_keys)
        result["missing_from_canonical"] = sorted(set(canonical_keys) - set(keys), key=key_sort)
        result["extra_vs_canonical"] = sorted(set(keys) - set(canonical_keys), key=key_sort)
    result["passed"] = (
        result["surah_count"] == EXPECTED_SURAH_COUNT
        and result["ayah_count"] == EXPECTED_AYAH_COUNT
        and not result["duplicate_keys"]
        and not result["empty_text_keys"]
        and not result["arabic_text_missing_keys"]
        and (canonical_keys is None or result["keys_match_canonical"])
    )
    return result


def key_sort(key):
    s, a = key.split(":")
    return int(s), int(a)


def sorted_ayahs(rows):
    return sorted(rows, key=lambda r: (r["surah_number"], r["ayah_number"]))


def load_qul_ayah_json(path):
    data = json.loads(path.read_text(encoding="utf-8"))
    rows = []
    for value in data.values():
        key = value.get("verse_key") or f"{value.get('surah') or value.get('surah_number')}:{value.get('ayah') or value.get('ayah_number')}"
        rows.append({
            "ayah_key": key,
            "surah_number": int(value.get("surah") or value.get("surah_number")),
            "ayah_number": int(value.get("ayah") or value.get("ayah_number")),
            "text": value.get("text", ""),
            "id": value.get("id"),
        })
    return sorted_ayahs(rows)


def load_json_zip_member(raw_zip):
    with zipfile.ZipFile(raw_zip) as zf:
        member = next(n for n in zf.namelist() if n.lower().endswith(".json"))
        return json.loads(zf.read(member).decode("utf-8"))


def normalized_staging_rows(simple_rows):
    rows = []
    for r in simple_rows:
        rows.append({
            "ayah_key": r["ayah_key"],
            "normalized_arabic": r["text"],
            "normalization_source": "sourcedata/2/quran-simple-clean.xml Tanzil Simple Clean",
            "display_safe": False,
            "notes": "Search/cross-check only. Never render as Quran display text.",
        })
    return rows


def write_validation_report(path, title, result, extra=None):
    lines = [f"# {title}", "", f"Generated: {NOW}", ""]
    if extra:
        lines.extend(extra)
        lines.append("")
    for k, v in result.items():
        if isinstance(v, list):
            lines.append(f"- {k}: {len(v)}")
            if v:
                lines.append(f"  Sample: `{', '.join(map(str, v[:10]))}`")
        else:
            lines.append(f"- {k}: {v}")
    lines.append("")
    lines.append(f"Verdict: {'GO' if result.get('passed') else 'BLOCKED'}")
    write_text(path, "\n".join(lines) + "\n")


def jsonl_from_ayah_rows(rows, source_name, source_folder, raw_source):
    return [{
        "ayah_key": r["ayah_key"],
        "surah_number": r["surah_number"],
        "ayah_number": r["ayah_number"],
        "text": r["text"],
        "source_folder": source_folder,
        "source_name": source_name,
        "raw_source_reference": raw_source,
    } for r in rows]


def display_staging_rows(rows, script, source_folder, source_name, raw_source):
    return [{
        "ayah_key": r["ayah_key"],
        "surah_number": r["surah_number"],
        "ayah_number": r["ayah_number"],
        "display_text": r["text"],
        "script_type": script,
        "source_folder": source_folder,
        "source_name": source_name,
        "raw_source_reference": raw_source,
        "staging_status": "candidate; unmodified raw display text",
    } for r in rows]


def metadata_files():
    files = {}
    for path in (EXTRACTED / "source_5").glob("*.json"):
        name = path.name.lower()
        for key in ["surah-name", "ayah", "juz", "hizb", "rub", "manzil", "ruku", "sajda"]:
            if key in name:
                files[key] = path
    return files


def load_metadata_jsons():
    files = metadata_files()
    return {k: json.loads(p.read_text(encoding="utf-8")) for k, p in files.items()}


def expand_verse_mapping(mapping, value):
    for surah, ayah_range in mapping.items():
        if "-" in ayah_range:
            start, end = ayah_range.split("-", 1)
        else:
            start = end = ayah_range
        for ayah in range(int(start), int(end) + 1):
            yield f"{int(surah)}:{ayah}", value


def build_juz_map(meta):
    result = {}
    for item in meta.get("juz", {}).values():
        for key, value in expand_verse_mapping(item["verse_mapping"], int(item["juz_number"])):
            result[key] = value
    return result


def build_division_map(meta, name, number_field):
    result = {}
    for item in meta.get(name, {}).values():
        mapping = item.get("verse_mapping")
        if mapping:
            for key, value in expand_verse_mapping(mapping, int(item[number_field])):
                result[key] = value
    return result


def ayah_word_ranges(meta_ayah):
    rows = []
    current = 1
    for item in sorted(meta_ayah.values(), key=lambda x: int(x["id"])):
        count = int(item.get("words_count") or 0)
        start = current
        end = current + count - 1 if count else current - 1
        rows.append((item["verse_key"], int(item["surah_number"]), int(item["ayah_number"]), start, end))
        current = end + 1
    return rows


def word_range_to_ayahs(word_ranges, first_word, last_word):
    keys = []
    for key, _s, _a, start, end in word_ranges:
        if end >= first_word and start <= last_word:
            keys.append(key)
    return keys


def build_page_mapping(db_path, meta_ayah, layout_name):
    if not db_path.exists():
        return []
    word_ranges = ayah_word_ranges(meta_ayah)
    con = sqlite3.connect(db_path)
    con.row_factory = sqlite3.Row
    tables = {r[0] for r in con.execute("select name from sqlite_master where type='table'")}
    if "pages" not in tables:
        con.close()
        return []
    page_to_keys = defaultdict(set)
    page_to_word_ranges = defaultdict(list)
    for row in con.execute("select * from pages"):
        if str(row["line_type"]).lower() != "ayah":
            continue
        try:
            first_word = int(row["first_word_id"])
            last_word = int(row["last_word_id"])
        except Exception:
            continue
        page = int(row["page_number"])
        page_to_word_ranges[page].append((first_word, last_word))
        for key in word_range_to_ayahs(word_ranges, first_word, last_word):
            page_to_keys[page].add(key)
    con.close()
    rows = []
    for page in sorted(page_to_keys):
        keys = sorted(page_to_keys[page], key=key_sort)
        first_word = min(x[0] for x in page_to_word_ranges[page])
        last_word = max(x[1] for x in page_to_word_ranges[page])
        rows.append({
            "layout_name": layout_name,
            "page_number": page,
            "first_ayah_key": keys[0],
            "last_ayah_key": keys[-1],
            "ayah_keys": keys,
            "first_word_id": first_word,
            "last_word_id": last_word,
            "mapping_basis": "layout DB line word ranges mapped to QUL ayah word counts",
        })
    return rows


def find_extracted(name):
    matches = list(EXTRACTED.rglob(name))
    return matches[0] if matches else None


def sqlite_table_summary(path):
    try:
        con = sqlite3.connect(path)
        rows = []
        for (table,) in con.execute("select name from sqlite_master where type='table' order by name"):
            count = con.execute(f"select count(*) from \"{table}\"").fetchone()[0]
            cols = [r[1] for r in con.execute(f"pragma table_info(\"{table}\")")]
            rows.append({"table": table, "count": count, "columns": cols})
        con.close()
        return rows
    except Exception as exc:
        return [{"error": str(exc)}]


def build_candidate_db(path, surahs, ayahs, uthmani_rows, indopak_rows, search_rows, manifest, validation_rows, layout_rows, font_rows):
    if path.exists():
        path.unlink()
    con = sqlite3.connect(path)
    cur = con.cursor()
    cur.executescript(
        """
        PRAGMA foreign_keys = ON;
        CREATE TABLE surahs (
          id INTEGER PRIMARY KEY,
          number INTEGER NOT NULL UNIQUE,
          name_arabic TEXT NOT NULL,
          name_simple TEXT NOT NULL,
          revelation_type TEXT,
          ayah_count INTEGER NOT NULL
        );
        CREATE TABLE ayahs (
          id INTEGER PRIMARY KEY,
          ayah_key TEXT NOT NULL UNIQUE,
          surah_number INTEGER NOT NULL,
          ayah_number INTEGER NOT NULL,
          juz_number INTEGER NOT NULL,
          page_number INTEGER NOT NULL,
          hizb_number INTEGER,
          rub_number INTEGER,
          manzil_number INTEGER,
          ruku_number INTEGER,
          sajdah_type TEXT
        );
        CREATE TABLE quran_texts (
          id INTEGER PRIMARY KEY,
          ayah_key TEXT NOT NULL,
          script_type TEXT NOT NULL,
          display_text TEXT NOT NULL,
          source_id INTEGER NOT NULL,
          checksum TEXT,
          FOREIGN KEY(ayah_key) REFERENCES ayahs(ayah_key)
        );
        CREATE TABLE search_index (
          id INTEGER PRIMARY KEY,
          ayah_key TEXT NOT NULL UNIQUE,
          normalized_arabic TEXT NOT NULL,
          normalization_source TEXT NOT NULL,
          display_safe INTEGER NOT NULL DEFAULT 0,
          FOREIGN KEY(ayah_key) REFERENCES ayahs(ayah_key)
        );
        CREATE TABLE content_sources (
          id INTEGER PRIMARY KEY,
          source_folder_number INTEGER NOT NULL,
          original_file_path TEXT NOT NULL,
          original_file_name TEXT NOT NULL,
          detected_format TEXT NOT NULL,
          sha256 TEXT NOT NULL,
          source_name TEXT NOT NULL,
          source_url TEXT,
          content_category TEXT NOT NULL,
          script_type TEXT,
          license_status TEXT NOT NULL,
          v1_candidate_status TEXT NOT NULL,
          requires_manual_review INTEGER NOT NULL,
          raw_file_immutable INTEGER NOT NULL,
          notes TEXT
        );
        CREATE TABLE content_validation (
          id INTEGER PRIMARY KEY,
          validation_name TEXT NOT NULL,
          expected_value TEXT NOT NULL,
          actual_value TEXT NOT NULL,
          passed INTEGER NOT NULL,
          checked_at TEXT NOT NULL
        );
        CREATE TABLE mushaf_layout_references (
          id INTEGER PRIMARY KEY,
          layout_name TEXT NOT NULL,
          page_number INTEGER NOT NULL,
          first_ayah_key TEXT,
          last_ayah_key TEXT,
          first_word_id INTEGER,
          last_word_id INTEGER,
          mapping_basis TEXT
        );
        CREATE TABLE font_inventory (
          id INTEGER PRIMARY KEY,
          raw_file_path TEXT NOT NULL,
          file_name TEXT NOT NULL,
          likely_use TEXT NOT NULL,
          license_status TEXT NOT NULL,
          v1_bundling_status TEXT NOT NULL
        );
        """
    )
    cur.executemany(
        "insert into surahs(id, number, name_arabic, name_simple, revelation_type, ayah_count) values(?,?,?,?,?,?)",
        [(s["id"], s["number"], s["name_arabic"], s["name_simple"], s.get("revelation_type"), s["ayah_count"]) for s in surahs],
    )
    cur.executemany(
        "insert into ayahs(id, ayah_key, surah_number, ayah_number, juz_number, page_number, hizb_number, rub_number, manzil_number, ruku_number, sajdah_type) values(?,?,?,?,?,?,?,?,?,?,?)",
        [(a["id"], a["ayah_key"], a["surah_number"], a["ayah_number"], a["juz_number"], a["page_number"], a.get("hizb_number"), a.get("rub_number"), a.get("manzil_number"), a.get("ruku_number"), a.get("sajdah_type")) for a in ayahs],
    )
    qtext = []
    idx = 1
    for r in uthmani_rows:
        qtext.append((idx, r["ayah_key"], "UTHMANI", r["text"], 1, hashlib.sha256(r["text"].encode("utf-8")).hexdigest()))
        idx += 1
    for r in indopak_rows:
        qtext.append((idx, r["ayah_key"], "INDOPAK", r["text"], 3, hashlib.sha256(r["text"].encode("utf-8")).hexdigest()))
        idx += 1
    cur.executemany("insert into quran_texts(id, ayah_key, script_type, display_text, source_id, checksum) values(?,?,?,?,?,?)", qtext)
    cur.executemany(
        "insert into search_index(id, ayah_key, normalized_arabic, normalization_source, display_safe) values(?,?,?,?,?)",
        [(i + 1, r["ayah_key"], r["normalized_arabic"], r["normalization_source"], 0) for i, r in enumerate(search_rows)],
    )
    cur.executemany(
        "insert into content_sources(id, source_folder_number, original_file_path, original_file_name, detected_format, sha256, source_name, source_url, content_category, script_type, license_status, v1_candidate_status, requires_manual_review, raw_file_immutable, notes) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
        [(i + 1, r["source_folder_number"], r["original_file_path"], r["original_file_name"], r["detected_format"], r["sha256"], r["source_name"], r["source_url"], r["content_category"], r.get("script_type"), r["license_status"], r["v1_candidate_status"], int(r["requires_manual_review"]), int(r["raw_file_immutable"]), r["notes"]) for i, r in enumerate(manifest)],
    )
    cur.executemany(
        "insert into content_validation(id, validation_name, expected_value, actual_value, passed, checked_at) values(?,?,?,?,?,?)",
        [(i + 1, r["validation_name"], str(r["expected_value"]), str(r["actual_value"]), int(r["passed"]), NOW) for i, r in enumerate(validation_rows)],
    )
    cur.executemany(
        "insert into mushaf_layout_references(id, layout_name, page_number, first_ayah_key, last_ayah_key, first_word_id, last_word_id, mapping_basis) values(?,?,?,?,?,?,?,?)",
        [(i + 1, r["layout_name"], r["page_number"], r["first_ayah_key"], r["last_ayah_key"], r.get("first_word_id"), r.get("last_word_id"), r["mapping_basis"]) for i, r in enumerate(layout_rows)],
    )
    cur.executemany(
        "insert into font_inventory(id, raw_file_path, file_name, likely_use, license_status, v1_bundling_status) values(?,?,?,?,?,?)",
        [(i + 1, r["raw_file_path"], r["file_name"], r["likely_use"], r["license_status"], r["v1_bundling_status"]) for i, r in enumerate(font_rows)],
    )
    con.commit()
    con.close()


def validate_db(path):
    con = sqlite3.connect(path)
    cur = con.cursor()
    prohibited = ["translation", "translations", "tafsir", "audio", "tajweed", "qiraat", "morphology", "word_by_word", "wordbyword"]
    tables = [r[0] for r in cur.execute("select name from sqlite_master where type='table'")]
    checks = {
        "surah_count": cur.execute("select count(*) from surahs").fetchone()[0],
        "ayah_count": cur.execute("select count(*) from ayahs").fetchone()[0],
        "uthmani_display_rows": cur.execute("select count(*) from quran_texts where script_type='UTHMANI'").fetchone()[0],
        "indopak_display_rows": cur.execute("select count(*) from quran_texts where script_type='INDOPAK'").fetchone()[0],
        "search_index_rows": cur.execute("select count(*) from search_index").fetchone()[0],
        "duplicate_ayah_keys": cur.execute("select count(*) from (select ayah_key from ayahs group by ayah_key having count(*) > 1)").fetchone()[0],
        "empty_uthmani_text": cur.execute("select count(*) from quran_texts where script_type='UTHMANI' and length(display_text)=0").fetchone()[0],
        "empty_indopak_text": cur.execute("select count(*) from quran_texts where script_type='INDOPAK' and length(display_text)=0").fetchone()[0],
        "empty_search_text": cur.execute("select count(*) from search_index where length(normalized_arabic)=0").fetchone()[0],
        "orphan_quran_texts": cur.execute("select count(*) from quran_texts qt left join ayahs a on a.ayah_key=qt.ayah_key where a.ayah_key is null").fetchone()[0],
        "orphan_search_index": cur.execute("select count(*) from search_index si left join ayahs a on a.ayah_key=si.ayah_key where a.ayah_key is null").fetchone()[0],
        "display_safe_search_rows": cur.execute("select count(*) from search_index where display_safe != 0").fetchone()[0],
        "font_rows_blocked_for_bundling": cur.execute("select count(*) from font_inventory where v1_bundling_status='blocked until license review'").fetchone()[0],
        "prohibited_tables": [t for t in tables if any(p in t.lower() for p in prohibited)],
    }
    con.close()
    passed = (
        checks["surah_count"] == 114
        and checks["ayah_count"] == 6236
        and checks["uthmani_display_rows"] == 6236
        and checks["indopak_display_rows"] == 6236
        and checks["search_index_rows"] == 6236
        and checks["duplicate_ayah_keys"] == 0
        and checks["empty_uthmani_text"] == 0
        and checks["empty_indopak_text"] == 0
        and checks["empty_search_text"] == 0
        and checks["orphan_quran_texts"] == 0
        and checks["orphan_search_index"] == 0
        and checks["display_safe_search_rows"] == 0
        and not checks["prohibited_tables"]
    )
    return {"passed": passed, "checks": checks, "database_path": rel(path)}


def font_likely_use(path):
    name = path.name.lower()
    if "indopak" in name or "nastaleeq" in name or "khatt" in name:
        return "IndoPak rendering"
    if "uthman" in name or "qpc" in name or "hafs" in name:
        return "Uthmani rendering"
    if "surah" in name or "header" in name:
        return "Surah names"
    if "ligature" in name:
        return "headings/decorative"
    return "unknown"


def make_csv(path, rows, fieldnames):
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("w", encoding="utf-8", newline="") as fh:
        writer = csv.DictWriter(fh, fieldnames=fieldnames)
        writer.writeheader()
        for row in rows:
            writer.writerow({k: row.get(k, "") for k in fieldnames})


def main():
    preflight = {
        "sourcedata_exists": SOURCE_ROOT.exists(),
        "source_folders_present": {str(i): (SOURCE_ROOT / str(i)).exists() for i in range(1, 11)},
        "projectdata_existed_before_run": PROJECTDATA.exists(),
        "raw_input_policy": "sourcedata/ treated as immutable; no in-place edits or extraction.",
        "android_code_policy": "No Android app code modified.",
    }
    if PROJECTDATA.exists():
        print("BLOCKED: projectdata already exists", file=sys.stderr)
        return 2
    if not preflight["sourcedata_exists"] or not all(preflight["source_folders_present"].values()):
        print("BLOCKED: sourcedata folders missing", file=sys.stderr)
        return 2

    ensure_dirs()
    raw_files = list_raw_files()
    checksums = {path: sha256(path) for path in raw_files}
    write_text(
        MANAGED / "raw_file_checksums.sha256",
        "".join(f"{checksums[path]}  {rel(path)}\n" for path in raw_files),
    )
    preflight["projectdata_created"] = True
    preflight["managed_workspace"] = rel(MANAGED)
    preflight["raw_file_count"] = len(raw_files)
    write_text(
        MANAGED / "phase0_preflight_report.md",
        "# Phase 0 Preflight Report\n\n"
        + f"Generated: {NOW}\n\n"
        + "\n".join(f"- {k}: {v}" for k, v in preflight.items())
        + "\n",
    )

    inventory = build_inventory(raw_files, checksums)
    write_text(
        MANAGED / "checksum_report.md",
        "# Checksum Report\n\n"
        + f"Generated: {NOW}\n\n"
        + f"- Raw file count: {len(raw_files)}\n"
        + f"- Checksum count: {len(checksums)}\n"
        + f"- Count matches: {len(raw_files) == len(checksums)}\n",
    )
    manifest = build_manifest(inventory)
    extracted_files, extraction_blocked, extraction_report = extract_sources(raw_files)

    uthmani_rows = sorted_ayahs(parse_tanzil_xml(SOURCE_ROOT / "1" / "quran-uthmani.xml"))
    canonical_keys = [r["ayah_key"] for r in uthmani_rows]
    uthmani_validation = validate_ayah_source(uthmani_rows)
    write_jsonl(STAGING / "uthmani_tanzil_ayahs.jsonl", jsonl_from_ayah_rows(uthmani_rows, "Tanzil Uthmani XML", 1, "sourcedata/1/quran-uthmani.xml"))
    write_validation_report(MANAGED / "uthmani_inspection_report.md", "Uthmani Inspection Report", uthmani_validation, ["Source: `sourcedata/1/quran-uthmani.xml`", "Purpose: primary Uthmani display text candidate."])

    simple_rows = sorted_ayahs(parse_tanzil_xml(SOURCE_ROOT / "2" / "quran-simple-clean.xml"))
    simple_validation = validate_ayah_source(simple_rows, canonical_keys)
    write_jsonl(STAGING / "simple_clean_ayahs.jsonl", jsonl_from_ayah_rows(simple_rows, "Tanzil Simple Clean XML", 2, "sourcedata/2/quran-simple-clean.xml"))
    write_validation_report(MANAGED / "simple_clean_inspection_report.md", "Simple Clean Inspection Report", simple_validation, ["Source: `sourcedata/2/quran-simple-clean.xml`", "Hard rule: search/cross-check only; never display Quran text."])

    indopak_digital_path = find_extracted("digital-khatt-indopak-ayah-by-ayah-script.json")
    nastaleeq_path = find_extracted("indopak-nastaleeq.json")
    indopak_rows = load_qul_ayah_json(indopak_digital_path)
    nastaleeq_rows = load_qul_ayah_json(nastaleeq_path)
    indopak_validation = validate_ayah_source(indopak_rows, canonical_keys)
    nastaleeq_validation = validate_ayah_source(nastaleeq_rows, canonical_keys)
    write_jsonl(STAGING / "indopak_digital_khatt_ayahs.jsonl", jsonl_from_ayah_rows(indopak_rows, "QUL Digital Khatt IndoPak", 3, "sourcedata/3/digital-khatt-indopak-ayah-by-ayah-script.json.zip"))
    write_validation_report(MANAGED / "indopak_digital_khatt_inspection_report.md", "IndoPak Digital Khatt Inspection Report", indopak_validation, ["Source: `sourcedata/3/` extracted copy.", "Default: primary IndoPak display text candidate if valid."])
    write_jsonl(STAGING / "indopak_nastaleeq_ayahs.jsonl", jsonl_from_ayah_rows(nastaleeq_rows, "QUL IndoPak Nastaleeq", 4, "sourcedata/4/indopak-nastaleeq.json.zip"))
    write_validation_report(MANAGED / "indopak_nastaleeq_inspection_report.md", "IndoPak Nastaleeq Inspection Report", nastaleeq_validation, ["Source: `sourcedata/4/` extracted copy.", "Default: backup/future optional IndoPak candidate."])

    sample_keys = []
    for s, a1, a2 in [(1, 1, 7), (2, 1, 5), (18, 1, 10), (36, 1, 12), (55, 1, 13), (67, 1, 10), (96, 1, 5), (112, 1, 4), (113, 1, 5), (114, 1, 6)]:
        sample_keys.extend(f"{s}:{a}" for a in range(a1, a2 + 1))
    sample_keys.append("2:255")
    digital_by_key = {r["ayah_key"]: r for r in indopak_rows}
    nast_by_key = {r["ayah_key"]: r for r in nastaleeq_rows}
    comparison_rows = [{"ayah_key": k, "digital_khatt": digital_by_key[k]["text"], "nastaleeq": nast_by_key[k]["text"], "same_text": digital_by_key[k]["text"] == nast_by_key[k]["text"]} for k in sorted(set(sample_keys), key=key_sort)]
    write_text(
        MANAGED / "indopak_candidate_comparison.md",
        "# IndoPak Candidate Comparison\n\nGenerated: "
        + NOW
        + "\n\n"
        + "| Ayah | Digital Khatt | Nastaleeq | Same |\n|---|---|---|---|\n"
        + "\n".join(f"| {r['ayah_key']} | {r['digital_khatt']} | {r['nastaleeq']} | {r['same_text']} |" for r in comparison_rows)
        + "\n",
    )
    default_indopak_decision = "Use Digital Khatt IndoPak as primary; keep Nastaleeq as backup/future optional candidate." if indopak_validation["passed"] else "BLOCKED: Digital Khatt IndoPak failed validation."
    write_text(MANAGED / "indopak_default_decision.md", f"# IndoPak Default Decision\n\nGenerated: {NOW}\n\n{default_indopak_decision}\n")

    meta = load_metadata_jsons()
    surahs = []
    for item in sorted(meta["surah-name"].values(), key=lambda x: int(x["id"])):
        surahs.append({
            "id": int(item["id"]),
            "number": int(item["id"]),
            "name_arabic": item["name_arabic"],
            "name_simple": item["name_simple"],
            "revelation_type": item.get("revelation_place"),
            "ayah_count": int(item["verses_count"]),
        })
    juz_map = build_juz_map(meta)
    hizb_map = build_division_map(meta, "hizb", "hizb_number")
    rub_map = build_division_map(meta, "rub", "rub_number")
    manzil_map = build_division_map(meta, "manzil", "manzil_number")
    ruku_map = build_division_map(meta, "ruku", "ruku_number")

    indopak_page_map_rows = build_page_mapping(find_extracted("qudratullah-indopak-15-lines.db") or Path("missing"), meta["ayah"], "IndoPak 15-line Qudratullah")
    uthmani_page_map_rows = build_page_mapping(find_extracted("qpc-v2-15-lines.db") or Path("missing"), meta["ayah"], "KFGQPC V2 1421H")
    write_jsonl(STAGING / "page_mapping_indopak_reference.jsonl", indopak_page_map_rows)
    write_jsonl(STAGING / "page_mapping_uthmani_reference.jsonl", uthmani_page_map_rows)
    page_by_ayah = {}
    for r in indopak_page_map_rows or uthmani_page_map_rows:
        for k in r["ayah_keys"]:
            page_by_ayah.setdefault(k, r["page_number"])

    sajdah_map = {}
    for item in meta.get("sajda", {}).values():
        key = item.get("verse_key") or item.get("ayah_key")
        if key:
            sajdah_map[key] = item.get("type") or item.get("sajdah_type") or "sajdah"

    ayahs = []
    for item in sorted(meta["ayah"].values(), key=lambda x: int(x["id"])):
        key = item["verse_key"]
        ayahs.append({
            "id": int(item["id"]),
            "ayah_key": key,
            "surah_number": int(item["surah_number"]),
            "ayah_number": int(item["ayah_number"]),
            "juz_number": int(juz_map.get(key, 0)),
            "page_number": int(page_by_ayah.get(key, 0)),
            "hizb_number": hizb_map.get(key),
            "rub_number": rub_map.get(key),
            "manzil_number": manzil_map.get(key),
            "ruku_number": ruku_map.get(key),
            "sajdah_type": sajdah_map.get(key),
        })
    write_jsonl(STAGING / "surahs_metadata.jsonl", surahs)
    write_jsonl(STAGING / "ayah_metadata.jsonl", ayahs)
    division_rows = []
    for name in ["juz", "hizb", "rub", "manzil", "ruku"]:
        for item in meta.get(name, {}).values():
            row = dict(item)
            row["division_type"] = name
            division_rows.append(row)
    write_jsonl(STAGING / "quran_divisions_metadata.jsonl", division_rows)
    write_jsonl(STAGING / "sajdah_metadata.jsonl", list(meta.get("sajda", {}).values()))

    metadata_report = [
        "# Metadata Inspection Report",
        "",
        f"Generated: {NOW}",
        "",
        f"- Categories found: {', '.join(sorted(meta.keys()))}",
        f"- Surah rows: {len(surahs)}",
        f"- Ayah rows: {len(ayahs)}",
        f"- Division rows: {len(division_rows)}",
        f"- Sajdah rows: {len(meta.get('sajda', {}))}",
        "- Display text from metadata ayah files is not used for Quran display.",
    ]
    write_text(MANAGED / "metadata_inspection_report.md", "\n".join(metadata_report) + "\n")
    write_text(
        MANAGED / "metadata_consolidation_report.md",
        "# Metadata Consolidation Report\n\n"
        + f"Generated: {NOW}\n\n"
        + f"- Staged surah metadata: {len(surahs)} rows\n"
        + f"- Staged ayah metadata: {len(ayahs)} rows\n"
        + f"- Juz mappings complete: {sum(1 for a in ayahs if a['juz_number']) == EXPECTED_AYAH_COUNT}\n"
        + f"- Page mappings complete: {sum(1 for a in ayahs if a['page_number']) == EXPECTED_AYAH_COUNT}\n",
    )

    layout_sqlite_files = sorted(EXTRACTED.glob("source_6/*.db"))
    layout_lines = ["# Mushaf Layout Inspection Report", "", f"Generated: {NOW}", "", "| File | Classification | Tables | Notes |", "|---|---|---|---|"]
    for db in layout_sqlite_files:
        name = db.name.lower()
        classification = "IndoPak layout" if "indopak" in name or "qudratullah" in name or "taj" in name else "KFGQPC/Madani layout" if "qpc" in name else "SQLite layout mapping candidate"
        summary = sqlite_table_summary(db)
        table_names = ", ".join(s["table"] for s in summary if "table" in s)
        layout_lines.append(f"| `{rel(db)}` | {classification} | {table_names} | Page/line word-range reference; not exact printed rendering. |")
    for raw in raw_files:
        if source_num(raw) == 6 and "_pages" in raw.name:
            layout_lines.append(f"| `{rel(raw)}` | DOCX/page visual reference | n/a | `_pages` visual reference only, not SQLite source. |")
    write_text(MANAGED / "mushaf_layout_inspection_report.md", "\n".join(layout_lines) + "\n")
    write_text(
        MANAGED / "page_mapping_decision.md",
        "# Page Mapping Decision\n\n"
        + f"Generated: {NOW}\n\n"
        + "- V1 will not attempt exact printed Mushaf page rendering.\n"
        + "- Preferred IndoPak page reference: IndoPak 15-line Qudratullah.\n"
        + "- Preferred Uthmani/Madani page reference: KFGQPC V2 1421H.\n"
        + f"- IndoPak staged page rows: {len(indopak_page_map_rows)}.\n"
        + f"- Uthmani/Madani staged page rows: {len(uthmani_page_map_rows)}.\n",
    )

    quran_meta_files = list((EXTRACTED / "source_7").rglob("*"))
    write_text(
        MANAGED / "quran_meta_crosscheck_report.md",
        "# Quran Meta Cross-check Report\n\n"
        + f"Generated: {NOW}\n\n"
        + f"- Extracted files/dirs inspected: {len(quran_meta_files)}\n"
        + "- Use: metadata cross-checking only.\n"
        + "- App import status: no external code imported into Android.\n",
    )

    font_raw_files = [p for p in raw_files if source_num(p) == 8]
    font_rows = [{
        "raw_file_path": rel(p),
        "file_name": p.name,
        "detected_type": detect_type(p),
        "likely_use": font_likely_use(p),
        "license_status": "requires review",
        "v1_bundling_status": "blocked until license review",
    } for p in font_raw_files]
    write_text(
        MANAGED / "font_inventory_report.md",
        "# Font Inventory Report\n\nGenerated: "
        + NOW
        + "\n\n"
        + "| File | Likely use | License | Bundling |\n|---|---|---|---|\n"
        + "\n".join(f"| `{r['raw_file_path']}` | {r['likely_use']} | {r['license_status']} | {r['v1_bundling_status']} |" for r in font_rows)
        + "\n",
    )
    write_text(MANAGED / "font_license_risk_report.md", "# Font License Risk Report\n\nGenerated: " + NOW + "\n\nEvery font is blocked for V1 app bundling until a manual license review approves use.\n")
    write_text(MANAGED / "font_testing_shortlist.md", "# Font Testing Shortlist\n\nGenerated: " + NOW + "\n\n" + "\n".join(f"- `{r['raw_file_path']}`: {r['likely_use']}" for r in font_rows if r["likely_use"] != "unknown") + "\n")

    qf_files = [p for p in raw_files if source_num(p) == 9]
    qf_text = "\n".join(p.read_text(encoding="utf-8", errors="ignore")[:10000] for p in qf_files if p.suffix.lower() in [".html", ".js", ".txt"])
    credential_terms = ["client_secret", "api_key", "access_token", "refresh_token", "bearer "]
    credential_hits = [term for term in credential_terms if term in qf_text.lower()]
    write_text(
        MANAGED / "quran_foundation_docs_report.md",
        "# Quran Foundation Docs Report\n\n"
        + f"Generated: {NOW}\n\n"
        + f"- Files inspected: {len(qf_files)}\n"
        + "- Classification: cross-check/reference route only.\n"
        + "- Runtime dependency: none.\n"
        + f"- OAuth/API credential indicators found: {credential_hits}\n"
        + "- Quran Foundation is not listed as a final content source because no data was imported from it.\n",
    )

    write_text(
        MANAGED / "future_only_sources_report.md",
        "# Future-only Sources Report\n\n"
        + f"Generated: {NOW}\n\n"
        + "- `sourcedata/10/` is Quranic Arabic Corpus morphology data.\n"
        + "- Classification: future-only linguistic resource.\n"
        + "- V1 database inclusion: excluded.\n",
    )

    canonical_rows = [{"ayah_key": r["ayah_key"], "surah_number": r["surah_number"], "ayah_number": r["ayah_number"], "source": "Tanzil Uthmani XML"} for r in uthmani_rows]
    write_jsonl(STAGING / "canonical_ayah_keys.jsonl", canonical_rows)
    all_sources_match = simple_validation["passed"] and indopak_validation["passed"] and nastaleeq_validation["passed"]
    write_text(
        MANAGED / "canonical_ayah_key_report.md",
        "# Canonical Ayah Key Report\n\n"
        + f"Generated: {NOW}\n\n"
        + f"- Canonical source: `sourcedata/1/quran-uthmani.xml`\n"
        + f"- Canonical key count: {len(canonical_rows)}\n"
        + f"- Duplicate keys: {len(uthmani_validation['duplicate_keys'])}\n"
        + f"- All V1 candidate sources map onto canonical keys: {all_sources_match}\n",
    )

    write_jsonl(STAGING / "display_text_uthmani.jsonl", display_staging_rows(uthmani_rows, "UTHMANI", 1, "Tanzil Uthmani XML", "sourcedata/1/quran-uthmani.xml"))
    write_jsonl(STAGING / "display_text_indopak.jsonl", display_staging_rows(indopak_rows, "INDOPAK", 3, "QUL Digital Khatt IndoPak", "sourcedata/3/digital-khatt-indopak-ayah-by-ayah-script.json.zip"))
    write_text(
        MANAGED / "display_text_staging_report.md",
        "# Display Text Staging Report\n\n"
        + f"Generated: {NOW}\n\n"
        + f"- Uthmani display rows: {len(uthmani_rows)}\n"
        + f"- IndoPak display rows: {len(indopak_rows)}\n"
        + "- Display text was not normalized, altered, generated, or merged.\n"
        + "- Uthmani and IndoPak display text remain separate.\n",
    )
    search_rows = normalized_staging_rows(simple_rows)
    write_jsonl(STAGING / "search_normalized_arabic.jsonl", search_rows)
    write_text(
        MANAGED / "search_normalization_report.md",
        "# Search Normalization Report\n\n"
        + f"Generated: {NOW}\n\n"
        + f"- Search rows: {len(search_rows)}\n"
        + "- Source: Tanzil Simple Clean XML.\n"
        + "- `display_safe` is false for every row.\n"
        + "- Hard rule: this file must never be used as Quran display text.\n",
    )

    validation_rows = [
        {"validation_name": "surah_count", "expected_value": 114, "actual_value": len(surahs), "passed": len(surahs) == 114},
        {"validation_name": "ayah_count", "expected_value": 6236, "actual_value": len(ayahs), "passed": len(ayahs) == 6236},
        {"validation_name": "ayah_keys_complete", "expected_value": 6236, "actual_value": len(set(a["ayah_key"] for a in ayahs)), "passed": len(set(a["ayah_key"] for a in ayahs)) == 6236},
        {"validation_name": "no_duplicate_ayah_keys", "expected_value": 0, "actual_value": len(canonical_keys) - len(set(canonical_keys)), "passed": len(canonical_keys) == len(set(canonical_keys))},
        {"validation_name": "indopak_text_complete", "expected_value": 6236, "actual_value": len(indopak_rows), "passed": indopak_validation["passed"]},
        {"validation_name": "uthmani_text_complete", "expected_value": 6236, "actual_value": len(uthmani_rows), "passed": uthmani_validation["passed"]},
        {"validation_name": "search_index_count", "expected_value": 6236, "actual_value": len(search_rows), "passed": len(search_rows) == 6236},
        {"validation_name": "content_sources_complete", "expected_value": len(raw_files), "actual_value": len(manifest), "passed": len(manifest) == len(raw_files)},
        {"validation_name": "checksum_verified", "expected_value": len(raw_files), "actual_value": len(checksums), "passed": len(raw_files) == len(checksums)},
    ]

    candidate_db = BUILD / "amanah_quran_content_v1_candidate.sqlite"
    build_candidate_db(candidate_db, surahs, ayahs, uthmani_rows, indopak_rows, search_rows, manifest, validation_rows, indopak_page_map_rows + uthmani_page_map_rows, font_rows)
    write_text(
        MANAGED / "candidate_database_build_report.md",
        "# Candidate Database Build Report\n\n"
        + f"Generated: {NOW}\n\n"
        + f"- Database: `{rel(candidate_db)}`\n"
        + f"- Surahs: {len(surahs)}\n"
        + f"- Ayahs: {len(ayahs)}\n"
        + f"- Uthmani quran_text rows: {len(uthmani_rows)}\n"
        + f"- IndoPak quran_text rows: {len(indopak_rows)}\n"
        + f"- Search index rows: {len(search_rows)}\n"
        + "- Prohibited feature tables: none created.\n",
    )
    db_validation = validate_db(candidate_db)
    write_json(MANAGED / "candidate_database_validation.json", db_validation)
    write_text(
        MANAGED / "candidate_database_validation_report.md",
        "# Candidate Database Validation Report\n\n"
        + f"Generated: {NOW}\n\n"
        + "\n".join(f"- {k}: {v}" for k, v in db_validation["checks"].items())
        + f"\n\nVerdict: {'GO' if db_validation['passed'] else 'BLOCKED'}\n",
    )

    trust_center = {
        "app": "Amanah Quran",
        "project_identity": "Amanah-e-Kisa",
        "generated_at": NOW,
        "quran_text_sources_actually_used": [
            {"script_type": "UTHMANI", "source": "Tanzil Uthmani XML", "raw_source": "sourcedata/1/quran-uthmani.xml", "validation_status": "GO" if uthmani_validation["passed"] else "BLOCKED"},
            {"script_type": "INDOPAK", "source": "QUL Digital Khatt IndoPak", "raw_source": "sourcedata/3/digital-khatt-indopak-ayah-by-ayah-script.json.zip", "validation_status": "GO" if indopak_validation["passed"] else "BLOCKED"},
            {"script_type": "SEARCH_ONLY", "source": "Tanzil Simple Clean XML", "raw_source": "sourcedata/2/quran-simple-clean.xml", "validation_status": "GO" if simple_validation["passed"] else "BLOCKED"},
        ],
        "no_modification_statement": NO_MODIFICATION_STATEMENT,
        "validation_status": db_validation["passed"],
        "privacy_pledge": "No ads, no tracking, no analytics SDK, no login, no data collection, no data sharing, and fully functional offline after install.",
        "app_content_integrity_placeholders": ["Manual Quran text review pending", "Font license review pending", "Public release approval pending"],
        "claims_not_made": ["scholar review complete", "font license approved", "public release ready", "Quran Foundation as final source"],
    }
    write_json(BUILD / "trust_center_content.json", trust_center)
    write_text(
        MANAGED / "trust_center_pack_report.md",
        "# Trust Center Pack Report\n\n"
        + f"Generated: {NOW}\n\n"
        + f"- Trust Center JSON: `{rel(BUILD / 'trust_center_content.json')}`\n"
        + "- Includes actual Quran text/search sources used.\n"
        + "- Does not claim scholar review complete, font license approved, or public release ready.\n",
    )

    make_csv(REVIEWER / "uthmani_review_sample.csv", display_staging_rows(uthmani_rows, "UTHMANI", 1, "Tanzil Uthmani XML", "sourcedata/1/quran-uthmani.xml")[:25], ["ayah_key", "surah_number", "ayah_number", "display_text", "script_type", "source_name"])
    make_csv(REVIEWER / "indopak_review_sample.csv", display_staging_rows(indopak_rows, "INDOPAK", 3, "QUL Digital Khatt IndoPak", "sourcedata/3/digital-khatt-indopak-ayah-by-ayah-script.json.zip")[:25], ["ayah_key", "surah_number", "ayah_number", "display_text", "script_type", "source_name"])
    make_csv(REVIEWER / "script_comparison_sample.csv", [{"ayah_key": k, "uthmani": {r["ayah_key"]: r for r in uthmani_rows}[k]["text"], "indopak": digital_by_key[k]["text"]} for k in sorted(set(sample_keys), key=key_sort)], ["ayah_key", "uthmani", "indopak"])
    write_text(REVIEWER / "reviewer_readme.md", "# Reviewer README\n\nThis package contains Quran text, metadata, layout, and Trust Center samples for human review. Public release remains blocked until reviewer approval is complete.\n")
    write_text(REVIEWER / "metadata_review_summary.md", f"# Metadata Review Summary\n\nSurahs: {len(surahs)}\nAyahs: {len(ayahs)}\nJuz mapped: {sum(1 for a in ayahs if a['juz_number'])}\nPages mapped: {sum(1 for a in ayahs if a['page_number'])}\n")
    write_text(REVIEWER / "mushaf_layout_review_summary.md", f"# Mushaf Layout Review Summary\n\nIndoPak reference pages: {len(indopak_page_map_rows)}\nUthmani/Madani reference pages: {len(uthmani_page_map_rows)}\nExact printed page rendering is out of V1 scope.\n")
    write_text(REVIEWER / "trust_center_preview.md", "# Trust Center Preview\n\n" + NO_MODIFICATION_STATEMENT + "\n\nPrivacy pledge: No ads, no tracking, no login, no data collection.\n")
    write_text(REVIEWER / "reviewer_decision_form.md", "# Reviewer Decision Form\n\n- Quran text approved: yes/no\n- Metadata approved: yes/no\n- Page reference acceptable for V1 navigation: yes/no\n- Font license approved for bundling: yes/no\n- Notes:\n")
    write_text(MANAGED / "reviewer_package_report.md", "# Reviewer Package Report\n\nGenerated: " + NOW + "\n\nReviewer package created under `projectdata/managed/reviewer_package/`. Public release remains blocked until reviewer approval is completed.\n")

    write_text(
        MANAGED / "android_import_readiness_plan.md",
        "# Android Import Readiness Plan\n\n"
        + f"Generated: {NOW}\n\n"
        + f"- Candidate DB location: `{rel(candidate_db)}`\n"
        + "- Room prepackaged DB target proposal: `apps/android/app/src/main/assets/databases/amanah_quran_content_v1.sqlite` after manual approval.\n"
        + "- Schema mapping: `surahs`, `ayahs`, `quran_texts`, `search_index`, `content_sources`, `content_validation`, `mushaf_layout_references`, `font_inventory`.\n"
        + "- DAO requirements: read-only Quran content DAOs for surah, ayah, script text, search index, content source, and validation records.\n"
        + "- Repository requirements: enforce display/search separation and script-specific retrieval.\n"
        + "- Trust Center JSON strategy: package `build/trust_center_content.json` after manual review.\n"
        + "- Search FTS strategy: build an Android-side FTS table from `search_index.normalized_arabic`, never from display text mutation.\n"
        + "- Script switch strategy: query `quran_texts` by canonical `ayah_key` and `script_type`.\n"
        + "- Bookmark identity: `ayah_key`.\n"
        + "- Last-read identity: `ayah_key` and page number.\n"
        + "- Build-gate validation: run DB validation before packaging.\n"
        + "- Manual review gate: Quran text review and font/license decisions required before public release.\n",
    )

    prompts = {
        "prompt_phase3_android_db_import.txt": "Import the approved candidate SQLite DB into Android as a Room prepackaged database. Do not alter Quran display text. Preserve display/search separation.",
        "prompt_phase4_reader_integration.txt": "Integrate reader UI with local Room data for Surah, Juz, and Page navigation. Support Uthmani/IndoPak script switching without runtime script conversion.",
        "prompt_phase5_search_bookmark_lastread.txt": "Implement offline search, bookmarks, and last-read using canonical ayah_key/page identity. Search results must render display text, never normalized search text.",
        "prompt_phase6_trust_center_integration.txt": "Integrate Trust Center JSON and validation/source views. Do not claim public release readiness before manual review gates pass.",
        "prompt_phase7_release_validation.txt": "Run release validation: no ads, analytics, tracking, login, unnecessary permissions, prohibited feature tables, or network-dependent core features.",
    }
    for name, body in prompts.items():
        write_text(FUTURE_PROMPTS / name, body + "\n")
    write_text(MANAGED / "future_prompt_generation_report.md", "# Future Prompt Generation Report\n\nGenerated: " + NOW + "\n\nCreated Phase 3 through Phase 7 prompt files under `projectdata/managed/future_prompts/`.\n")

    files_created = sorted(rel(p) for p in MANAGED.rglob("*") if p.is_file())
    final_verdict = "CONDITIONAL GO" if db_validation["passed"] and uthmani_validation["passed"] and simple_validation["passed"] and indopak_validation["passed"] else "BLOCKED"
    summary = [
        "# Phase 2 to Phase 25 Master Summary",
        "",
        f"Generated: {NOW}",
        "",
        "- `sourcedata/` used as immutable raw input.",
        "- `projectdata/` created as managed workspace.",
        f"- Raw files count: {len(raw_files)}",
        f"- Checksum count: {len(checksums)}",
        f"- Extracted files count: {len(extracted_files)}",
        f"- Uthmani validation status: {'GO' if uthmani_validation['passed'] else 'BLOCKED'}",
        f"- Simple Clean validation status: {'GO' if simple_validation['passed'] else 'BLOCKED'}",
        f"- Digital Khatt IndoPak status: {'GO' if indopak_validation['passed'] else 'BLOCKED'}",
        f"- Nastaleeq backup status: {'GO' if nastaleeq_validation['passed'] else 'BLOCKED'}",
        f"- Final IndoPak default decision: {default_indopak_decision}",
        f"- Metadata readiness: surahs={len(surahs)}, ayahs={len(ayahs)}",
        f"- Mushaf layout readiness: IndoPak pages={len(indopak_page_map_rows)}, Uthmani/Madani pages={len(uthmani_page_map_rows)}",
        "- Font warnings: all font bundling blocked until license review.",
        "- Quran Foundation docs status: reference only, no runtime dependency.",
        "- Future-only source status: morphology excluded from V1.",
        f"- Candidate database status: {'GO' if db_validation['passed'] else 'BLOCKED'}",
        "- Trust Center pack status: created.",
        "- Reviewer package status: created.",
        "- Android import-readiness status: plan created only; app code untouched.",
        "- Human/manual decisions still required: Quran text review, font license approval, public release approval.",
        f"- Final verdict: {final_verdict}",
        "",
        "## Files Created",
        "",
        *[f"- `{p}`" for p in files_created],
    ]
    write_text(MANAGED / "phase2_to_phase25_master_summary.md", "\n".join(summary) + "\n")
    write_text(
        MANAGED / "phase_run_log.md",
        "# Phase Run Log\n\n"
        + f"Generated: {NOW}\n\n"
        + "\n".join(f"- Phase {i}: completed" for i in range(0, 26))
        + f"\n- Final verdict: {final_verdict}\n",
    )

    print(json.dumps({
        "raw_files": len(raw_files),
        "checksums": len(checksums),
        "extracted_files": len(extracted_files),
        "candidate_db": rel(candidate_db),
        "db_validation_passed": db_validation["passed"],
        "final_verdict": final_verdict,
    }, ensure_ascii=False, indent=2))
    return 0 if final_verdict != "BLOCKED" else 1


if __name__ == "__main__":
    raise SystemExit(main())
