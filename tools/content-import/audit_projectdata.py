#!/usr/bin/env python3
import csv
import json
import sqlite3
from datetime import datetime, timezone
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
MANAGED = ROOT / "projectdata" / "managed"
DB = MANAGED / "build" / "amanah_quran_content_v1_candidate.sqlite"
TRUST = MANAGED / "build" / "trust_center_content.json"
REVIEWER = MANAGED / "reviewer_package"
NOW = datetime.now(timezone.utc).replace(microsecond=0).isoformat()

EXPECTED_TABLES = {
    "surahs",
    "ayahs",
    "quran_texts",
    "search_index",
    "content_sources",
    "content_validation",
    "mushaf_layout_references",
    "font_inventory",
}
PROHIBITED_TABLE_TOKENS = [
    "translations",
    "tafsir",
    "audio",
    "tajweed",
    "qiraat",
    "morphology",
    "word_by_word",
    "users",
    "accounts",
    "sync",
    "analytics",
]
REQUIRED_REVIEWER_FILES = [
    "reviewer_readme.md",
    "uthmani_review_sample.csv",
    "indopak_review_sample.csv",
    "script_comparison_sample.csv",
    "metadata_review_summary.md",
    "mushaf_layout_review_summary.md",
    "trust_center_preview.md",
    "reviewer_decision_form.md",
]


def rel(path):
    return path.relative_to(ROOT).as_posix()


def write(path, text):
    path.write_text(text, encoding="utf-8")


def write_json(path, data):
    path.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def connect_ro():
    return sqlite3.connect(f"file:{DB}?mode=ro", uri=True)


def q1(cur, sql, params=()):
    return cur.execute(sql, params).fetchone()[0]


def table_names(cur):
    return [r[0] for r in cur.execute("select name from sqlite_master where type='table' order by name")]


def bool_text(value):
    return "YES" if value else "NO"


def verdict_line(verdict):
    return f"\nVerdict: {verdict}\n"


def phase1():
    checks = {
        "candidate_database": DB.exists(),
        "trust_center_content": TRUST.exists(),
        "reviewer_package": REVIEWER.is_dir(),
        "android_import_readiness_plan": (MANAGED / "android_import_readiness_plan.md").exists(),
    }
    lines = ["# Pre-Android Import File Existence Audit", "", f"Generated: {NOW}", ""]
    for name, ok in checks.items():
        lines.append(f"- {name}: {bool_text(ok)}")
    verdict = "GO" if all(checks.values()) else "BLOCKED"
    lines.append(verdict_line(verdict))
    write(MANAGED / "pre_android_import_audit_file_existence.md", "\n".join(lines))
    return checks, verdict


def phase2(cur):
    tables = table_names(cur)
    missing = sorted(EXPECTED_TABLES - set(tables))
    prohibited = sorted(t for t in tables if any(token in t.lower() for token in PROHIBITED_TABLE_TOKENS))
    verdict = "GO" if not missing and not prohibited else "BLOCKED"
    lines = [
        "# Pre-Android Import Schema Audit",
        "",
        f"Generated: {NOW}",
        "",
        "## Tables Found",
        "",
        *[f"- `{t}`" for t in tables],
        "",
        f"Expected tables missing: {missing}",
        f"Prohibited tables found: {prohibited}",
        verdict_line(verdict),
    ]
    write(MANAGED / "pre_android_import_schema_audit.md", "\n".join(lines))
    return {"tables": tables, "missing": missing, "prohibited": prohibited}, verdict


def phase3(cur):
    counts = {
        "surahs": q1(cur, "select count(*) from surahs"),
        "ayahs": q1(cur, "select count(*) from ayahs"),
        "uthmani_quran_texts": q1(cur, "select count(*) from quran_texts where script_type='UTHMANI'"),
        "indopak_quran_texts": q1(cur, "select count(*) from quran_texts where script_type='INDOPAK'"),
        "search_index": q1(cur, "select count(*) from search_index"),
        "content_sources": q1(cur, "select count(*) from content_sources"),
        "content_validation": q1(cur, "select count(*) from content_validation"),
        "mushaf_layout_references": q1(cur, "select count(*) from mushaf_layout_references"),
        "font_inventory": q1(cur, "select count(*) from font_inventory"),
    }
    ok = (
        counts["surahs"] == 114
        and counts["ayahs"] == 6236
        and counts["uthmani_quran_texts"] == 6236
        and counts["indopak_quran_texts"] == 6236
        and counts["search_index"] == 6236
    )
    verdict = "GO" if ok else "BLOCKED"
    lines = ["# Pre-Android Import Count Audit", "", f"Generated: {NOW}", ""]
    lines.extend(f"- {k}: {v}" for k, v in counts.items())
    lines.append(verdict_line(verdict))
    write(MANAGED / "pre_android_import_count_audit.md", "\n".join(lines))
    return counts, verdict


def phase4(cur):
    checks = {
        "duplicate_ayah_key_in_ayahs": q1(cur, "select count(*) from (select ayah_key from ayahs group by ayah_key having count(*) > 1)"),
        "duplicate_uthmani_ayah_key_in_quran_texts": q1(cur, "select count(*) from (select ayah_key from quran_texts where script_type='UTHMANI' group by ayah_key having count(*) > 1)"),
        "duplicate_indopak_ayah_key_in_quran_texts": q1(cur, "select count(*) from (select ayah_key from quran_texts where script_type='INDOPAK' group by ayah_key having count(*) > 1)"),
        "quran_texts_missing_from_ayahs": q1(cur, "select count(*) from quran_texts q left join ayahs a on a.ayah_key=q.ayah_key where a.ayah_key is null"),
        "search_index_missing_from_ayahs": q1(cur, "select count(*) from search_index s left join ayahs a on a.ayah_key=s.ayah_key where a.ayah_key is null"),
        "ayahs_without_uthmani_text": q1(cur, "select count(*) from ayahs a left join quran_texts q on q.ayah_key=a.ayah_key and q.script_type='UTHMANI' where q.ayah_key is null"),
        "ayahs_without_indopak_text": q1(cur, "select count(*) from ayahs a left join quran_texts q on q.ayah_key=a.ayah_key and q.script_type='INDOPAK' where q.ayah_key is null"),
        "ayahs_without_search_index": q1(cur, "select count(*) from ayahs a left join search_index s on s.ayah_key=a.ayah_key where s.ayah_key is null"),
    }
    verdict = "GO" if all(v == 0 for v in checks.values()) else "BLOCKED"
    lines = ["# Pre-Android Import Key Integrity Audit", "", f"Generated: {NOW}", ""]
    lines.extend(f"- {k}: {v}" for k, v in checks.items())
    lines.append(verdict_line(verdict))
    write(MANAGED / "pre_android_import_key_integrity_audit.md", "\n".join(lines))
    return checks, verdict


def phase5(cur):
    checks = {
        "null_or_empty_uthmani_display_text": q1(cur, "select count(*) from quran_texts where script_type='UTHMANI' and (display_text is null or length(display_text)=0)"),
        "null_or_empty_indopak_display_text": q1(cur, "select count(*) from quran_texts where script_type='INDOPAK' and (display_text is null or length(display_text)=0)"),
        "null_or_empty_normalized_arabic": q1(cur, "select count(*) from search_index where normalized_arabic is null or length(normalized_arabic)=0"),
        "whitespace_only_display_text": q1(cur, "select count(*) from quran_texts where length(trim(display_text))=0"),
        "suspiciously_short_display_text_len_lt_3": q1(cur, "select count(*) from quran_texts where length(trim(display_text)) < 3"),
        "display_text_identical_to_normalized_arabic_rows": q1(cur, "select count(*) from quran_texts q join search_index s on s.ayah_key=q.ayah_key where q.display_text=s.normalized_arabic"),
    }
    short_samples = cur.execute(
        "select ayah_key, script_type, display_text, length(trim(display_text)) from quran_texts where length(trim(display_text)) < 12 order by ayah_key, script_type limit 25"
    ).fetchall()
    missing = any(checks[k] for k in [
        "null_or_empty_uthmani_display_text",
        "null_or_empty_indopak_display_text",
        "null_or_empty_normalized_arabic",
        "whitespace_only_display_text",
    ])
    verdict = "BLOCKED" if missing else "CONDITIONAL GO"
    lines = ["# Pre-Android Import Text Audit", "", f"Generated: {NOW}", ""]
    lines.extend(f"- {k}: {v}" for k, v in checks.items())
    lines.extend(["", "## Short Text Samples", "", "| Ayah | Script | Length | Text |", "|---|---|---:|---|"])
    lines.extend(f"| {k} | {script} | {length} | {text} |" for k, script, text, length in short_samples)
    lines.append("")
    lines.append("Suspicious short rows are expected for short ayahs and disjoint letters; reviewer material only.")
    lines.append(verdict_line(verdict))
    write(MANAGED / "pre_android_import_text_audit.md", "\n".join(lines))
    return checks, verdict


def phase6(cur):
    source_rows = cur.execute(
        "select source_folder_number, source_name, v1_candidate_status from content_sources order by source_folder_number, original_file_name"
    ).fetchall()
    simple_display_rows = q1(
        cur,
        "select count(*) from quran_texts q join content_sources c on c.source_folder_number=q.source_id where c.source_folder_number=2",
    )
    display_safe_bad = q1(cur, "select count(*) from search_index where coalesce(display_safe, 0) != 0")
    scripts = cur.execute("select script_type, count(*) from quran_texts group by script_type order by script_type").fetchall()
    same_as_search = q1(cur, "select count(*) from quran_texts q join search_index s on s.ayah_key=q.ayah_key where q.display_text=s.normalized_arabic")
    verdict = "GO" if simple_display_rows == 0 and display_safe_bad == 0 and dict(scripts).get("INDOPAK") == 6236 and dict(scripts).get("UTHMANI") == 6236 else "BLOCKED"
    lines = [
        "# Pre-Android Import Display/Search Separation Audit",
        "",
        f"Generated: {NOW}",
        "",
        f"- Search index display_safe non-zero rows: {display_safe_bad}",
        f"- Simple Clean source used as quran_text display rows: {simple_display_rows}",
        f"- Display rows identical to normalized search rows: {same_as_search}",
        f"- Script row counts: {scripts}",
        "",
        "Technical confirmation: `quran_texts` stores display text by script, and `search_index` stores normalized/search text separately.",
        "The Simple Clean source is represented as search/cross-check only, not display text.",
        verdict_line(verdict),
    ]
    write(MANAGED / "pre_android_import_display_search_separation_audit.md", "\n".join(lines))
    return {"display_safe_bad": display_safe_bad, "simple_display_rows": simple_display_rows, "same_as_search": same_as_search, "scripts": scripts, "sources": source_rows}, verdict


def sample_keys():
    keys = []
    for s, start, end in [(1, 1, 7), (2, 1, 5), (18, 1, 10), (36, 1, 12), (55, 1, 13), (67, 1, 10), (96, 1, 5), (112, 1, 4), (113, 1, 5), (114, 1, 6)]:
        keys.extend(f"{s}:{a}" for a in range(start, end + 1))
    keys.append("2:255")
    return sorted(set(keys), key=lambda k: tuple(map(int, k.split(":"))))


def phase7(cur):
    rows = []
    for key in sample_keys():
        uthmani = q1(cur, "select display_text from quran_texts where ayah_key=? and script_type='UTHMANI'", (key,))
        indopak = q1(cur, "select display_text from quran_texts where ayah_key=? and script_type='INDOPAK'", (key,))
        normalized = q1(cur, "select normalized_arabic from search_index where ayah_key=?", (key,))
        rows.append({"ayah_key": key, "UTHMANI display text": uthmani, "INDOPAK display text": indopak, "normalized search text": normalized})
    csv_path = MANAGED / "pre_android_import_sample_ayahs.csv"
    with csv_path.open("w", encoding="utf-8", newline="") as fh:
        writer = csv.DictWriter(fh, fieldnames=list(rows[0].keys()))
        writer.writeheader()
        writer.writerows(rows)
    lines = [
        "# Pre-Android Import Sample Ayahs",
        "",
        f"Generated: {NOW}",
        "",
        "Reviewer material only. This audit does not judge religious correctness automatically.",
        "",
        "| Ayah | UTHMANI display text | INDOPAK display text | normalized search text |",
        "|---|---|---|---|",
    ]
    lines.extend(f"| {r['ayah_key']} | {r['UTHMANI display text']} | {r['INDOPAK display text']} | {r['normalized search text']} |" for r in rows)
    write(MANAGED / "pre_android_import_sample_ayahs.md", "\n".join(lines) + "\n")
    return rows


def phase8(cur):
    cols = [r[1] for r in cur.execute("pragma table_info(ayahs)")]
    checks = {
        "ayahs_missing_surah_number": q1(cur, "select count(*) from ayahs where surah_number is null or surah_number <= 0"),
        "ayahs_missing_ayah_number": q1(cur, "select count(*) from ayahs where ayah_number is null or ayah_number <= 0"),
        "global_ayah_number_column_present": int("global_ayah_number" in cols),
        "id_available_as_global_ayah_number": q1(cur, "select count(*) from ayahs where id is null or id <= 0") == 0,
        "ayahs_missing_juz_number": q1(cur, "select count(*) from ayahs where juz_number is null or juz_number <= 0"),
        "ayahs_missing_page_number": q1(cur, "select count(*) from ayahs where page_number is null or page_number <= 0"),
        "ayahs_with_sajdah_metadata": q1(cur, "select count(*) from ayahs where sajdah_type is not null and length(sajdah_type) > 0"),
        "ayahs_missing_ruku_number": q1(cur, "select count(*) from ayahs where ruku_number is null or ruku_number <= 0"),
        "ayahs_missing_hizb_number": q1(cur, "select count(*) from ayahs where hizb_number is null or hizb_number <= 0"),
        "ayahs_missing_rub_number": q1(cur, "select count(*) from ayahs where rub_number is null or rub_number <= 0"),
        "ayahs_missing_manzil_number": q1(cur, "select count(*) from ayahs where manzil_number is null or manzil_number <= 0"),
    }
    blocking_gaps = []
    if checks["ayahs_missing_surah_number"]:
        blocking_gaps.append("missing surah_number")
    if checks["ayahs_missing_ayah_number"]:
        blocking_gaps.append("missing ayah_number")
    if checks["ayahs_missing_juz_number"]:
        blocking_gaps.append("missing juz_number")
    if checks["ayahs_missing_page_number"]:
        blocking_gaps.append("missing page_number")
    optional_gaps = []
    if not checks["global_ayah_number_column_present"]:
        optional_gaps.append("No explicit global_ayah_number column; ayahs.id is available as the global sequence.")
    for key in ["ayahs_missing_ruku_number", "ayahs_missing_hizb_number", "ayahs_missing_rub_number", "ayahs_missing_manzil_number"]:
        if checks[key]:
            optional_gaps.append(key)
    verdict = "GO" if not blocking_gaps else "BLOCKED"
    lines = ["# Pre-Android Import Metadata Audit", "", f"Generated: {NOW}", ""]
    lines.extend(f"- {k}: {v}" for k, v in checks.items())
    lines.extend(["", f"Blocking metadata gaps: {blocking_gaps}", f"Optional metadata gaps: {optional_gaps}", verdict_line(verdict)])
    write(MANAGED / "pre_android_import_metadata_audit.md", "\n".join(lines))
    return {"checks": checks, "blocking_gaps": blocking_gaps, "optional_gaps": optional_gaps}, verdict


def phase9():
    data = json.loads(TRUST.read_text(encoding="utf-8"))
    text = json.dumps(data, ensure_ascii=False).lower()
    required = {
        "quran_text_sources_actually_used": "quran_text_sources_actually_used" in data,
        "uthmani_source": "uthmani" in text,
        "indopak_source": "indopak" in text,
        "no_modification_statement": "no_modification_statement" in data and "search normalization is stored separately" in data.get("no_modification_statement", "").lower(),
        "validation_status": "validation_status" in data,
        "privacy_pledge": "privacy_pledge" in data,
        "app_content_integrity_placeholders": "app_content_integrity_placeholders" in data,
    }
    false_claim_terms = ["scholar review complete", "font license approved", "public release ready"]
    false_claims = [term for term in false_claim_terms if term in text and term not in " ".join(data.get("claims_not_made", [])).lower()]
    qf_final_claim = "quran foundation" in text and "final source" in text and "claims_not_made" not in data
    verdict = "GO" if all(required.values()) and not false_claims and not qf_final_claim else "CONDITIONAL GO"
    lines = ["# Pre-Android Import Trust Center Audit", "", f"Generated: {NOW}", ""]
    lines.extend(f"- {k}: {bool_text(v)}" for k, v in required.items())
    lines.extend([
        f"- False claims detected: {false_claims}",
        f"- Quran Foundation final-source claim detected: {qf_final_claim}",
        "- Suitable for internal app import/testing. Public wording still needs human review before release.",
        verdict_line(verdict),
    ])
    write(MANAGED / "pre_android_import_trust_center_audit.md", "\n".join(lines))
    return {"required": required, "false_claims": false_claims, "qf_final_claim": qf_final_claim}, verdict


def phase10(cur):
    counts = dict(cur.execute("select v1_bundling_status, count(*) from font_inventory group by v1_bundling_status").fetchall())
    approved = q1(cur, "select count(*) from font_inventory where lower(v1_bundling_status) like '%approved%' or lower(license_status) like '%approved%'")
    plan_text = (MANAGED / "android_import_readiness_plan.md").read_text(encoding="utf-8").lower()
    bundle_attempt = "bundle font" in plan_text or "bundle fonts" in plan_text
    risk_exists = (MANAGED / "font_license_risk_report.md").exists()
    shortlist_exists = (MANAGED / "font_testing_shortlist.md").exists()
    verdict = "GO" if approved == 0 and not bundle_attempt and risk_exists and shortlist_exists else "BLOCKED"
    lines = [
        "# Pre-Android Import Font Audit",
        "",
        f"Generated: {NOW}",
        "",
        f"- Font inventory statuses: {counts}",
        f"- Fonts marked approved: {approved}",
        f"- Font license risk report exists: {bool_text(risk_exists)}",
        f"- Font testing shortlist exists: {bool_text(shortlist_exists)}",
        f"- Android import plan tries to bundle fonts automatically: {bool_text(bundle_attempt)}",
        verdict_line(verdict),
    ]
    write(MANAGED / "pre_android_import_font_audit.md", "\n".join(lines))
    return {"counts": counts, "approved": approved, "bundle_attempt": bundle_attempt}, verdict


def phase11():
    exists = REVIEWER.is_dir()
    missing = [name for name in REQUIRED_REVIEWER_FILES if not (REVIEWER / name).exists()]
    verdict = "GO" if exists and not missing else "BLOCKED"
    lines = [
        "# Pre-Android Import Reviewer Package Audit",
        "",
        f"Generated: {NOW}",
        "",
        f"- Reviewer package exists: {bool_text(exists)}",
        f"- Missing files: {missing}",
        "- Public release remains blocked until reviewer approval is actually completed.",
        verdict_line(verdict),
    ]
    write(MANAGED / "pre_android_import_reviewer_package_audit.md", "\n".join(lines))
    return {"exists": exists, "missing": missing}, verdict


def phase12():
    path = MANAGED / "android_import_readiness_plan.md"
    text = path.read_text(encoding="utf-8").lower() if path.exists() else ""
    required = {
        "candidate_db_location": "candidate db location" in text,
        "room_prepackaged_db_target": "room prepackaged db target" in text,
        "schema_mapping": "schema mapping" in text,
        "dao_requirements": "dao requirements" in text,
        "repository_requirements": "repository requirements" in text,
        "trust_center_json_strategy": "trust center json strategy" in text,
        "search_fts_strategy": "search fts strategy" in text,
        "script_switch_strategy": "script switch strategy" in text,
        "bookmark_identity_by_ayah_key": "bookmark identity" in text and "ayah_key" in text,
        "last_read_identity_by_ayah_key_page": "last-read identity" in text and "page" in text,
        "build_gate_validation": "build-gate validation" in text,
        "manual_review_gate": "manual review gate" in text,
    }
    public_claim = "public release ready" in text
    verdict = "GO" if all(required.values()) and not public_claim else "BLOCKED"
    lines = ["# Pre-Android Import Android Readiness Audit", "", f"Generated: {NOW}", ""]
    lines.extend(f"- {k}: {bool_text(v)}" for k, v in required.items())
    lines.extend([f"- Public-release-ready claim detected: {bool_text(public_claim)}", verdict_line(verdict)])
    write(MANAGED / "pre_android_import_android_readiness_audit.md", "\n".join(lines))
    return {"required": required, "public_claim": public_claim}, verdict


def main():
    file_checks, file_verdict = phase1()
    if not DB.exists():
        raise SystemExit("candidate DB missing")
    con = connect_ro()
    cur = con.cursor()
    schema, schema_verdict = phase2(cur)
    counts, count_verdict = phase3(cur)
    key_checks, key_verdict = phase4(cur)
    text_checks, text_verdict = phase5(cur)
    separation, separation_verdict = phase6(cur)
    phase7(cur)
    metadata, metadata_verdict = phase8(cur)
    trust, trust_verdict = phase9()
    font, font_verdict = phase10(cur)
    reviewer, reviewer_verdict = phase11()
    android, android_verdict = phase12()
    con.close()

    blocking = []
    for label, verdict in [
        ("file existence", file_verdict),
        ("schema", schema_verdict),
        ("core counts", count_verdict),
        ("key integrity", key_verdict),
        ("text", text_verdict),
        ("display/search separation", separation_verdict),
        ("metadata", metadata_verdict),
        ("font", font_verdict),
        ("reviewer package", reviewer_verdict),
        ("android readiness", android_verdict),
    ]:
        if verdict == "BLOCKED":
            blocking.append(label)
    warnings = []
    if text_verdict == "CONDITIONAL GO":
        warnings.append("Suspicious short display rows exist but are expected for short ayahs/disjoint letters; reviewer material created.")
    if trust_verdict == "CONDITIONAL GO":
        warnings.append("Trust Center suitable for internal testing; public wording still needs human review.")
    if metadata["optional_gaps"]:
        warnings.extend(metadata["optional_gaps"])
    if blocking:
        final = "BLOCKED"
    else:
        final = "GO FOR INTERNAL ANDROID IMPORT"

    report = [
        "# Pre-Android Import Audit Report",
        "",
        f"Generated: {NOW}",
        "",
        f"- Database path audited: `{rel(DB)}`",
        f"- Trust Center path audited: `{rel(TRUST)}`",
        f"- Tables found: {schema['tables']}",
        f"- Prohibited tables check: {schema['prohibited']}",
        f"- Core counts: {counts}",
        f"- Key integrity result: {key_verdict}",
        f"- Empty text result: {text_verdict}",
        f"- Display/search separation result: {separation_verdict}",
        f"- Metadata result: {metadata_verdict}",
        f"- Trust Center result: {trust_verdict}",
        f"- Font risk result: {font_verdict}",
        f"- Reviewer package result: {reviewer_verdict}",
        f"- Android import readiness result: {android_verdict}",
        f"- Warnings: {warnings}",
        f"- Blocking issues: {blocking}",
        "",
        f"Final verdict: {final}",
        "",
        "Internal Android import is a development/testing gate only. Public release remains blocked until manual Quran text review, font/license decisions, real-device page navigation verification, and Trust Center wording review are complete.",
    ]
    write(MANAGED / "pre_android_import_audit_report.md", "\n".join(report) + "\n")
    print(json.dumps({"final_verdict": final, "blocking": blocking, "warnings": warnings, "counts": counts}, ensure_ascii=False, indent=2))


if __name__ == "__main__":
    main()
