#!/usr/bin/env python3
"""Check deterministic harness documentation requirements."""

from __future__ import annotations

import json
import sys
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]


REQUIRED_FILES = [
    ".harness/source.json",
    "AGENTS.md",
    "docs/harness/adoption-report.md",
    "docs/harness/harness-doctor-setup.md",
    "docs/conventions/coding.md",
    "docs/domain/glossary.md",
    "docs/decisions/0001-initial-spring-boot-erp-architecture.md",
    "docs/failures/README.md",
    "docs/effectiveness/effectiveness-report.md",
    "docs/effectiveness/task-outcomes/task-outcome-template.yaml",
    "scripts/check_effectiveness_plan.py",
    "scripts/check_failure_memory.py",
]


def fail(message: str) -> int:
    print(f"docs drift: {message}", file=sys.stderr)
    return 1


def main() -> int:
    for relative_path in REQUIRED_FILES:
        if not (ROOT / relative_path).exists():
            return fail(f"missing required file {relative_path}")

    source = json.loads((ROOT / ".harness/source.json").read_text(encoding="utf-8"))
    expected_source = {
        "kit_path": "/Users/wb/Desktop/harness-starter-kit",
        "kit_url": "https://github.com/harnessworks/harness-starter-kit.git",
        "kit_remote": "https://github.com/harnessworks/harness-starter-kit.git",
        "kit_commit": "de0737abf3808ecbd7eae50fcc7fab119594bd0d",
        "applied_profile": "spring",
        "setup_prompt": "/Users/wb/Desktop/prompt/00-setup-only.md",
        "source_tracking_ref": ".harness/source.json",
    }
    for key, expected_value in expected_source.items():
        if source.get(key) != expected_value:
            return fail(f".harness/source.json has unexpected {key}")

    if source.get("kit_url") != source.get("kit_remote"):
        return fail(".harness/source.json kit_url and kit_remote must match")

    effectiveness = (ROOT / "docs/effectiveness/effectiveness-report.md").read_text(
        encoding="utf-8"
    )
    required_effectiveness_text = [
        "harnessed-only-initial-benchmark",
        ".harness/source.json",
        "ERP-001",
        "ERP-002",
        "ERP-003",
        "ERP-004",
        "ERP-005",
        "MAINT-004",
        "de0737abf3808ecbd7eae50fcc7fab119594bd0d",
        "unknown",
    ]
    for expected_text in required_effectiveness_text:
        if expected_text not in effectiveness:
            return fail(f"effectiveness report missing {expected_text}")

    adoption = (ROOT / "docs/harness/adoption-report.md").read_text(
        encoding="utf-8"
    )
    required_adoption_text = [
        "python scripts/check_effectiveness_plan.py",
        "python scripts/check_failure_memory.py",
        "387dbfabda3d63975494bdabfc812ddf64100919",
        "de0737abf3808ecbd7eae50fcc7fab119594bd0d",
        "/harness update",
    ]
    for expected_text in required_adoption_text:
        if expected_text not in adoption:
            return fail(f"adoption report missing {expected_text}")

    template = (
        ROOT / "docs/effectiveness/task-outcomes/task-outcome-template.yaml"
    ).read_text(encoding="utf-8")
    required_template_text = [
        "harness_source:",
        "kit_url:",
        "kit_commit:",
        "source_tracking_ref: .harness/source.json",
        "include_in_effectiveness_report:",
        "include_in_comparable_product_task_count:",
    ]
    for expected_text in required_template_text:
        if expected_text not in template:
            return fail(f"task outcome template missing {expected_text}")

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
