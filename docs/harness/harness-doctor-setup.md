# Harness Doctor Setup Baseline

## Scope

This is setup-time harness health evidence only. It is not proof that harness
engineering improved agent effectiveness.

## Command

```bash
python /Users/wb/Desktop/harness-starter-kit/scripts/harness_doctor.py --target .
```

## Environment Note

The first attempt failed because this machine did not provide a `python`
executable. A temporary wrapper was created outside the repository in the
Codex-session path to execute `/usr/bin/python3` when the staged command uses
`python`.

## Result

- Exit code: 0
- Score: 54/100
- Grade: D (baseline)

## Output

```text
Harness Doctor Report

Score: 54/100 (baseline evidence scan)
Grade: D (baseline)

Verdict:
Mostly ad-hoc baseline evidence. This scan checks durable files and text patterns; review content quality before treating it as final.

Breakdown:
- Agent Instructions: 16/20
- Feedback Loops: 2/20
- Durable Memory: 20/20
- Structural Safety: 12/20
- Adoption Clarity: 4/20

Evidence:
- Agent instruction file exists: AGENTS.md
- Project overview is clear: README or agent instructions describe the project
- Exact build/test/lint commands exist: README or agent instructions include command-like text
- Forbidden actions are documented: Agent instructions include forbidden actions
- Security/safety notes exist: Agent instructions include safety or security notes
- Pre-commit or local validation script exists: Pre-commit config, check script, or validation command found
- docs/decisions exists: docs/decisions contains files
- docs/failures exists: docs/failures contains files
- docs/conventions exists: docs/conventions contains files
- docs/domain exists: docs/domain contains files
- At least one real decision or failure record exists: Non-template decision or failure record found
- Structure check script exists: Structure check script found
- Docs drift check exists: Docs drift check script found
- Generated file protection exists: Generated file protection appears in docs, checks, or ignore rules
- Before/after example exists: Before/after or adoption examples found

Missing Or Weak Baseline Items:
- Agent Instructions: Architecture boundaries are documented (4 pts)
- Feedback Loops: Test command exists (4 pts)
- Feedback Loops: Lint command exists (4 pts)
- Feedback Loops: Typecheck command exists (3 pts)
- CI workflow exists (5 pts)
- Validation instructions are documented (2 pts)
- Structural Safety: Forbidden path checks exist (3 pts)
- Structural Safety: Architecture/dependency boundary checks exist (3 pts)
- CI runs at least one structural check (2 pts)
- Adoption Clarity: README explains harness purpose (4 pts)
- Quickstart exists (4 pts)
- Adoption report template exists (3 pts)
- Profiles/examples exist (3 pts)
- Known limitations are documented (2 pts)

Note:
This script does not modify files, does not replace agent judgment, and does not score agent effectiveness, task outcomes, or governance maturity.
```
