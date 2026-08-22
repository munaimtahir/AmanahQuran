#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
from dataclasses import dataclass
from datetime import datetime, timezone
from pathlib import Path

from content_pipeline_common import CONTENT_PIPELINE, PROJECTDATA, ROOT, load_json, sha256_file


APP_ASSETS = ROOT / "apps" / "android" / "app" / "src" / "main" / "assets"
APP_FONT_DIR = ROOT / "apps" / "android" / "app" / "src" / "main" / "res" / "font"
GENERATED = CONTENT_PIPELINE / "06_generated_projectdata"


@dataclass
class PackageAsset:
    path: str
    kind: str
    checksum_sha256: str
    source_match: str | None
    license_status: str | None
    notes: str


def now_utc() -> str:
    return datetime.now(timezone.utc).replace(microsecond=0).isoformat()


def scan_files(root: Path) -> list[Path]:
    return sorted(p for p in root.rglob("*") if p.is_file() and p.name != ".gitkeep")


def source_registry() -> dict[str, dict]:
    registry = {}
    for row in load_json(CONTENT_PIPELINE / "01_license_review" / "source_registry.json"):
        registry[row.get("local_path", "")] = row
        registry[Path(row.get("local_path", "")).name] = row
        registry[row.get("asset_id", "")] = row
        registry[row.get("original_file_name", "")] = row
    return registry


def font_manifest() -> dict[str, dict]:
    mapping = {}
    for row in load_json(PROJECTDATA / "font_manifest.json"):
        mapping[row["fileName"]] = row
    return mapping


def generated_assets() -> dict[str, dict]:
    summary = load_json(GENERATED / "validation_summary.json")
    mapping = {}
    for row in summary.get("generated_assets", []):
        mapping[row["asset_name"]] = row
    return mapping


def classify(path: Path) -> str:
    rel = path.relative_to(ROOT).as_posix()
    if rel.startswith("apps/android/app/src/main/assets/database/"):
        return "DATABASE"
    if rel.startswith("apps/android/app/src/main/assets/trust/"):
        return "TRUST_JSON"
    if rel.startswith("apps/android/app/src/main/assets/content/translations/"):
        return "TRANSLATION"
    if rel.startswith("apps/android/app/src/main/assets/"):
        return "ASSET"
    if rel.startswith("apps/android/app/src/main/res/font/"):
        return "FONT"
    return "OTHER"


def scan_inventory() -> list[PackageAsset]:
    registry = source_registry()
    fonts = font_manifest()
    generated = generated_assets()
    inventory: list[PackageAsset] = []

    for root in [APP_ASSETS, APP_FONT_DIR]:
        if not root.exists():
            continue
        for path in scan_files(root):
            kind = classify(path)
            rel = path.relative_to(ROOT).as_posix()
            checksum = sha256_file(path)
            source_match = None
            license_status = None
            notes = ""
            if kind == "FONT":
                font = fonts.get(path.name)
                if font:
                    source_match = font.get("sourceName")
                    license_status = font.get("licenseStatus")
                    notes = "packaged font"
            elif kind == "DATABASE" and path.name == "quran.db":
                source_match = "generated_projectdata/amanah_quran.sqlite"
                license_status = generated.get("amanah_quran.sqlite", {}).get("validation_status")
                notes = "generated internal-testing database"
            elif kind == "TRUST_JSON" and path.name == "trust_center_content.json":
                source_match = "generated_projectdata/trust_center_sources.json"
                license_status = generated.get("trust_center_sources.json", {}).get("validation_status")
                notes = "generated Trust Center JSON"
            elif kind == "TRANSLATION" and path.name in {
                "translation_content.db",
                "translation_content_manifest.json",
            }:
                source_match = "docs/legal/DUAL_TRANSLATION_LICENSE_CLEARANCE_DECISION.md"
                license_status = generated.get(path.name, {}).get("validation_status")
                notes = "packaged Manifest English + Irfan-ul-Quran Urdu translations"
            else:
                reg = (
                    registry.get(f"sourcedata/{path.name}")
                    or registry.get(f"apps/android/app/src/main/assets/database/{path.name}")
                    or registry.get(f"apps/android/app/src/main/assets/trust/{path.name}")
                    or registry.get(path.name)
                )
                if reg:
                    source_match = reg.get("source_name")
                    license_status = reg.get("license_status")
                elif path.name in {"quran.db", "trust_center_content.json"}:
                    source_match = "generated_projectdata"
                    license_status = "INTERNAL_TESTING_ONLY"
            inventory.append(PackageAsset(rel, kind, checksum, source_match, license_status, notes))
    return inventory


def main() -> int:
    parser = argparse.ArgumentParser(description="Scan Android packaged content assets.")
    parser.add_argument("--profile", choices=["internal", "public"], default="public")
    parser.add_argument("--scope", choices=["packaged", "all"], default="packaged")
    parser.add_argument(
        "--report",
        type=Path,
        default=CONTENT_PIPELINE / "05_validation_reports" / "packaged_content_scan.json",
    )
    args = parser.parse_args()

    artifact_label = "INTERNAL TESTING ONLY - NOT PUBLIC RELEASE APPROVED" if args.profile == "internal" else "PUBLIC RELEASE TRACK"

    inventory = scan_inventory()
    blockers = []
    warnings = []

    for item in inventory:
        name = Path(item.path).name
        if item.kind == "DATABASE" and name == "amanah_quran_content_v1_candidate.sqlite":
            blockers.append({**item.__dict__, "reason": "legacy_candidate_database_packaged"})
        elif item.kind == "DATABASE" and name == "quran.db":
            if args.profile == "public" and item.license_status != "APPROVED":
                blockers.append({**item.__dict__, "reason": "generated_database_not_public_release_approved"})
            else:
                warnings.append({**item.__dict__, "reason": "internal_testing_database"})
        elif item.kind == "TRUST_JSON" and name == "trust_center_content.json":
            if args.profile == "public" and item.license_status != "APPROVED":
                blockers.append({**item.__dict__, "reason": "generated_trust_json_not_public_release_approved"})
            else:
                warnings.append({**item.__dict__, "reason": "internal_testing_trust_json"})
        elif item.kind == "FONT":
            if args.profile == "public" and item.license_status != "CLEARED":
                blockers.append({**item.__dict__, "reason": "font_not_public_release_approved"})
            else:
                warnings.append({**item.__dict__, "reason": "internal_testing_font"})
        elif item.kind == "TRANSLATION" and name in {
            "translation_content.db",
            "translation_content_manifest.json",
        }:
            if args.profile == "public" and item.license_status != "APPROVED":
                blockers.append({**item.__dict__, "reason": "translation_not_public_release_approved"})
            else:
                warnings.append({**item.__dict__, "reason": "internal_testing_translation"})
        elif name.endswith((".zip", ".xml", ".csv", ".bz2", ".pdf", ".docx", ".html", ".md")):
            blockers.append({**item.__dict__, "reason": "raw_source_or_archive_packaged"})
        elif item.source_match is None:
            blockers.append({**item.__dict__, "reason": "packaged_asset_missing_from_source_registry"})
        elif not item.checksum_sha256:
            blockers.append({**item.__dict__, "reason": "packaged_asset_missing_checksum"})
        elif not item.license_status:
            blockers.append({**item.__dict__, "reason": "packaged_asset_missing_license_status"})
        elif args.profile == "public" and item.kind not in {"DATABASE", "TRUST_JSON", "FONT"}:
            blockers.append({**item.__dict__, "reason": "packaged_asset_not_publicly_approved"})

    report = {
        "generated_at": now_utc(),
        "profile": args.profile,
        "artifact_label": artifact_label,
        "scope": args.scope,
        "packaged_count": len(inventory),
        "blocker_count": len(blockers),
        "warning_count": len(warnings),
        "packaged_assets": [item.__dict__ for item in inventory],
        "blockers": blockers,
        "warnings": warnings,
        "result": "FAIL" if blockers and args.profile == "public" else "PASS",
    }
    args.report.parent.mkdir(parents=True, exist_ok=True)
    args.report.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")

    print(f"INFO: Amanah release profile = {args.profile}")
    print(f"INFO: Amanah artifact label = {artifact_label}")
    print(json.dumps(report, ensure_ascii=False, indent=2))
    if args.profile == "public" and blockers:
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
