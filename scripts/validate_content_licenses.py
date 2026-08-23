#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
import os
from dataclasses import dataclass
from datetime import datetime, timezone
from pathlib import Path

from content_pipeline_common import CONTENT_PIPELINE, PROJECTDATA, ROOT, load_json, sha256_file


APP_ASSETS = ROOT / "apps" / "android" / "app" / "src" / "main" / "assets"
APP_FONT_DIR = ROOT / "apps" / "android" / "app" / "src" / "main" / "res" / "font"
GENERATED_PROJECTDATA = CONTENT_PIPELINE / "06_generated_projectdata"


@dataclass
class Finding:
    asset_path: str
    asset_kind: str
    profile: str
    scope: str
    status: str
    reason: str
    source_name: str | None = None
    license_status: str | None = None
    checksum_sha256: str | None = None


def now_utc() -> str:
    return datetime.now(timezone.utc).replace(microsecond=0).isoformat()


def registry_by_path() -> dict[str, dict]:
    registry = load_json(CONTENT_PIPELINE / "01_license_review" / "source_registry.json")
    mapping: dict[str, dict] = {}
    for row in registry:
        local_path = (row.get("local_path") or "").replace("\\", "/")
        mapping[local_path] = row
        mapping[Path(local_path).name] = row
        mapping[row.get("asset_id", "")] = row
    return mapping


def font_manifest_by_name() -> dict[str, dict]:
    manifest_path = PROJECTDATA / "font_manifest.json"
    if not manifest_path.exists():
        return {}
    mapping: dict[str, dict] = {}
    for row in load_json(manifest_path):
        mapping[row["fileName"]] = row
        mapping[Path(row["filePath"]).name] = row
    return mapping


def generated_assets_by_name() -> dict[str, dict]:
    summary_path = GENERATED_PROJECTDATA / "validation_summary.json"
    if not summary_path.exists():
        return {}
    summary = load_json(summary_path)
    mapping: dict[str, dict] = {}
    for row in summary.get("generated_assets", []):
        mapping[row["asset_name"]] = row
    return mapping


def scan_app_assets() -> list[Path]:
    files = []
    if APP_ASSETS.exists():
        files.extend(p for p in APP_ASSETS.rglob("*") if p.is_file() and p.name != ".gitkeep")
    if APP_FONT_DIR.exists():
        files.extend(p for p in APP_FONT_DIR.rglob("*") if p.is_file() and p.name != ".gitkeep")
    return sorted(set(files))


def classify_packaged_asset(path: Path) -> tuple[str, str]:
    rel = path.relative_to(ROOT).as_posix()
    if rel.startswith("apps/android/app/src/main/res/font/"):
        return "FONT", rel
    if rel.startswith("apps/android/app/src/main/assets/database/"):
        return "DATABASE", rel
    if rel.startswith("apps/android/app/src/main/assets/trust/"):
        return "TRUST_JSON", rel
    if rel.startswith("apps/android/app/src/main/assets/content/translations/"):
        return "TRANSLATION", rel
    if rel.startswith("apps/android/app/src/main/assets/"):
        return "ASSET", rel
    return "OTHER", rel


def scan_packaged_inventory() -> list[dict]:
    inventory = []
    for path in scan_app_assets():
        kind, rel = classify_packaged_asset(path)
        inventory.append(
            {
                "path": rel,
                "kind": kind,
                "basename": path.name,
                "checksum_sha256": sha256_file(path),
            }
        )
    return inventory


def evaluate_findings(profile: str, scope: str) -> tuple[list[Finding], dict]:
    registry = registry_by_path()
    fonts = font_manifest_by_name()
    generated = generated_assets_by_name()
    findings: list[Finding] = []

    if scope == "all":
        for row in load_json(CONTENT_PIPELINE / "01_license_review" / "source_registry.json"):
            findings.append(
                Finding(
                    asset_path=row.get("local_path") or row.get("asset_id") or "",
                    asset_kind=row.get("asset_type") or "UNKNOWN",
                    profile=profile,
                    scope=scope,
                    status=row.get("license_status") or "UNKNOWN",
                    reason="registry_audit",
                    source_name=row.get("source_name"),
                    license_status=row.get("license_status"),
                    checksum_sha256=row.get("checksum_sha256"),
                )
            )
        return findings, {"result": "PASS", "note": "audit_only"}

    packaged = scan_packaged_inventory()
    blockers = 0
    warnings = 0
    for item in packaged:
        path = item["path"]
        name = item["basename"]
        kind = item["kind"]
        checksum = item["checksum_sha256"]

        if kind == "FONT":
            font = fonts.get(name)
            if not font:
                findings.append(Finding(path, kind, profile, scope, "BLOCKER", "font_missing_from_manifest", checksum_sha256=checksum))
                blockers += 1
                continue
            license_status = (font.get("licenseStatus") or "").strip()
            if profile == "public" and license_status != "CLEARED":
                findings.append(
                    Finding(
                        path,
                        kind,
                        profile,
                        scope,
                        "BLOCKER",
                        "font_not_approved_for_public_distribution",
                        source_name=font.get("sourceName"),
                        license_status=license_status,
                        checksum_sha256=font.get("checksumSha256"),
                    )
                )
                blockers += 1
            else:
                findings.append(
                    Finding(
                        path,
                        kind,
                        profile,
                        scope,
                        "OK",
                        "internal_testing_only_font" if license_status == "CLEARED" else "font_packaged",
                        source_name=font.get("sourceName"),
                        license_status=license_status or "UNKNOWN",
                        checksum_sha256=font.get("checksumSha256"),
                    )
                )
                warnings += 1
            continue

        if kind == "DATABASE" and name == "quran.db":
            generated_asset = generated.get("amanah_quran.sqlite", {})
            status = generated_asset.get("validation_status", "INTERNAL_TESTING_ONLY")
            if profile == "public" and status != "APPROVED":
                findings.append(
                    Finding(path, kind, profile, scope, "BLOCKER", "generated_database_not_public_release_approved", license_status=status, checksum_sha256=checksum)
                )
                blockers += 1
            else:
                findings.append(
                    Finding(path, kind, profile, scope, "OK", "internal_testing_database", license_status=status, checksum_sha256=checksum)
                )
                warnings += 1
            continue

        if kind == "TRUST_JSON" and name in {"trust_center_content.json", "font_manifest.json"}:
            if name == "font_manifest.json":
                status = "APPROVED"
            else:
                generated_asset = generated.get("trust_center_sources.json", {})
                status = generated_asset.get("validation_status", "REVIEW_REQUIRED")
            if profile == "public" and status != "APPROVED":
                findings.append(
                    Finding(path, kind, profile, scope, "BLOCKER", "generated_trust_json_not_public_release_approved", license_status=status, checksum_sha256=checksum)
                )
                blockers += 1
            else:
                findings.append(Finding(path, kind, profile, scope, "OK", "internal_testing_trust_json", license_status=status, checksum_sha256=checksum))
                warnings += 1
            continue

        if kind == "TRANSLATION" and name in {
            "translation_content.db",
            "translation_content_manifest.json",
        }:
            generated_asset = generated.get(name, {})
            status = generated_asset.get("validation_status", "REVIEW_REQUIRED")
            if profile == "public" and status != "APPROVED":
                findings.append(
                    Finding(path, kind, profile, scope, "BLOCKER", "translation_not_public_release_approved", source_name="The Manifest Quran (EN) + Irfan-ul-Quran (UR)", license_status=status, checksum_sha256=checksum)
                )
                blockers += 1
            else:
                findings.append(Finding(path, kind, profile, scope, "OK", "internal_testing_translation", source_name="The Manifest Quran (EN) + Irfan-ul-Quran (UR)", license_status=status, checksum_sha256=checksum))
                warnings += 1
            continue

        # Any other packaged asset under app assets is not allowed for public release.
        registry_row = (
            registry.get(f"apps/android/app/src/main/assets/{name}")
            or registry.get(f"apps/android/app/src/main/assets/database/{name}")
            or registry.get(f"apps/android/app/src/main/assets/trust/{name}")
            or registry.get(f"database/{name}")
            or registry.get(f"trust/{name}")
            or registry.get(name)
        )
        if name.endswith((".zip", ".xml", ".csv", ".bz2", ".pdf", ".docx", ".html", ".md")):
            findings.append(
                Finding(
                    path,
                    kind,
                    profile,
                    scope,
                    "BLOCKER" if profile == "public" else "WARN",
                    "source_archive_or_raw_source_packaged",
                    source_name=(registry_row or {}).get("source_name"),
                    license_status=(registry_row or {}).get("license_status"),
                    checksum_sha256=checksum,
                )
            )
            if profile == "public":
                blockers += 1
            else:
                warnings += 1
            continue

        if registry_row is None:
            findings.append(
                Finding(
                    path,
                    kind,
                    profile,
                    scope,
                    "BLOCKER" if profile == "public" else "WARN",
                    "packaged_asset_missing_from_source_registry",
                    checksum_sha256=checksum,
                )
            )
            if profile == "public":
                blockers += 1
            else:
                warnings += 1
            continue

        if not registry_row.get("license_status"):
            findings.append(
                Finding(
                    path,
                    kind,
                    profile,
                    scope,
                    "BLOCKER" if profile == "public" else "WARN",
                    "packaged_asset_missing_license_status",
                    source_name=registry_row.get("source_name"),
                    checksum_sha256=checksum,
                )
            )
            if profile == "public":
                blockers += 1
            else:
                warnings += 1
            continue

        if not registry_row.get("checksum_sha256"):
            findings.append(
                Finding(
                    path,
                    kind,
                    profile,
                    scope,
                    "BLOCKER" if profile == "public" else "WARN",
                    "packaged_asset_missing_checksum",
                    source_name=registry_row.get("source_name"),
                    license_status=registry_row.get("license_status"),
                    checksum_sha256=checksum,
                )
            )
            if profile == "public":
                blockers += 1
            else:
                warnings += 1
            continue

        findings.append(
            Finding(
                path,
                kind,
                profile,
                scope,
                "WARN" if profile == "internal" else "OK",
                "packaged_asset_audited",
                source_name=registry_row.get("source_name"),
                license_status=registry_row.get("license_status"),
                checksum_sha256=checksum,
            )
        )
        if profile == "internal":
            warnings += 1

    # Candidate/quarantine assets are only audited in scope=all; here we only care about packaged assets.
    return findings, {"blockers": blockers, "warnings": warnings, "packaged_count": len(packaged)}


def main() -> int:
    parser = argparse.ArgumentParser(description="Validate content licenses and packaged asset status.")
    parser.add_argument("--profile", choices=["internal", "public"], default="public")
    parser.add_argument("--scope", choices=["packaged", "all"], default="packaged")
    parser.add_argument(
        "--report",
        type=Path,
        default=CONTENT_PIPELINE / "05_validation_reports" / "license_validation.json",
    )
    args = parser.parse_args()

    findings, meta = evaluate_findings(args.profile, args.scope)
    blockers = sum(1 for finding in findings if finding.status == "BLOCKER")
    result = "PASS"
    if args.scope == "packaged" and args.profile == "public" and blockers:
        result = "FAIL"
    elif args.scope == "packaged" and args.profile == "internal" and blockers:
        result = "FAIL"
    elif args.scope == "all":
        result = "PASS"

    report = {
        "generated_at": now_utc(),
        "profile": args.profile,
        "scope": args.scope,
        "result": result,
        "blocker_count": blockers,
        "findings": [finding.__dict__ for finding in findings],
        "meta": meta,
    }
    args.report.parent.mkdir(parents=True, exist_ok=True)
    args.report.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")

    if args.scope == "all":
        print(json.dumps(report, ensure_ascii=False, indent=2))
        return 0
    if args.profile == "public" and blockers:
        print(json.dumps(report, ensure_ascii=False, indent=2))
        return 1
    if args.profile == "internal" and blockers:
        print(json.dumps(report, ensure_ascii=False, indent=2))
        return 1
    print(json.dumps(report, ensure_ascii=False, indent=2))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
