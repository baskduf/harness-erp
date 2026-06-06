# 0002 Task Outcome Reviewer Evidence Gap

## Date Observed

2026-06-06

## Failure Type

Failed harness check.

## Goal

The harness update should adopt the starter kit's stricter task outcome
evidence gate while keeping existing included outcome records valid and
traceable.

## What Happened Or Was Tried

After updating `scripts/check_effectiveness_plan.py` with the starter kit's
required evidence fields, the first verification attempt ran:

```bash
python scripts/check_harness.py
```

Maven tests passed, but `scripts/check_effectiveness_plan.py` rejected every
included task outcome that still had `reviewer: unknown`.

## Why It Failed

The older local evidence schema allowed `reviewer: unknown`. The updated gate
treats `unknown` as a placeholder for included task outcomes, so the existing
records needed an explicit reviewer value for the known Codex evidence review
performed in this repository.

## Current Replacement

Included task outcome records now set:

```yaml
reviewer: Codex in Codex desktop
```

The task outcome template still uses `unknown` because it is not included in
effectiveness or comparable product-task counts.

## Detection Or Prevention Check

Run the normal harness gate before finishing evidence or harness-maintenance
work:

```bash
python scripts/check_harness.py
```

This gate includes `scripts/check_effectiveness_plan.py`, which now rejects
included task outcomes that leave required evidence fields as placeholders.

## Agent Guidance

For included task outcomes, do not leave `reviewer` as `unknown` when the
evidence review agent is known. Use `unknown` only for values that are actually
unknown and are not required by the active evidence gate.
