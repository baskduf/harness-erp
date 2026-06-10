#!/usr/bin/env python3
"""Run local harness checks for this Spring Boot ERP project."""

from __future__ import annotations

import os
import shutil
import subprocess
import sys
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]


def run(command: list[str]) -> None:
    subprocess.run(command, cwd=ROOT, check=True)


def maven_command() -> list[str]:
    wrapper = ROOT / ("mvnw.cmd" if os.name == "nt" else "mvnw")
    if wrapper.exists():
        return [str(wrapper), "test"]

    executable = shutil.which("mvn")
    if executable:
        return [executable, "test"]

    raise SystemExit("Maven wrapper or mvn executable was not found.")


def main() -> int:
    run(maven_command())
    run([sys.executable, "scripts/check_docs_drift.py"])
    run([sys.executable, "scripts/check_structure.py"])
    run([sys.executable, "scripts/check_effectiveness_plan.py"])
    run([sys.executable, "scripts/check_failure_memory.py"])
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
