#!/usr/bin/env python3
"""Check deterministic project structure and harness safety rules."""

from __future__ import annotations

import sys
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]


REQUIRED_DIRECTORIES = [
    "src/main/java/com/example/harnesserp/controller",
    "src/main/java/com/example/harnesserp/service",
    "src/main/java/com/example/harnesserp/repository",
    "src/main/java/com/example/harnesserp/domain",
    "src/main/java/com/example/harnesserp/dto",
    "src/test/java/com/example/harnesserp/service",
    "docs/effectiveness/task-outcomes",
]


def fail(message: str) -> int:
    print(f"structure: {message}", file=sys.stderr)
    return 1


def main() -> int:
    for relative_path in REQUIRED_DIRECTORIES:
        if not (ROOT / relative_path).is_dir():
            return fail(f"missing required directory {relative_path}")

    if (ROOT / "harness-starter-kit").exists():
        return fail("local harness-starter-kit directory must not be in target repo")

    gitignore = (ROOT / ".gitignore").read_text(encoding="utf-8")
    for expected in [
        "target/",
        "build/",
        "out/",
        ".gradle/",
        "*.class",
        "*.log",
        ".env",
        "application-local.properties",
        "application-local.yml",
        "application-local.yaml",
        "harness-starter-kit/",
    ]:
        if expected not in gitignore:
            return fail(f".gitignore missing {expected}")

    for controller in (ROOT / "src/main/java/com/example/harnesserp/controller").glob("*.java"):
        source = controller.read_text(encoding="utf-8")
        if ".repository." in source:
            return fail(f"controller imports repository directly: {controller.name}")

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
