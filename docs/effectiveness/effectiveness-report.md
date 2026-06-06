# Harness Effectiveness Report

## Target

- Repository: `harness-erp`
- Stack and framework: Java 21, Spring Boot 4.0.6, Maven wrapper, H2
- Evaluation date or window: starts 2026-06-06
- Agent or model: Codex in Codex desktop
- Evaluation mode: `harnessed-only-initial-benchmark`

## Harness Source

- Kit path: `/Users/wb/Desktop/harness-starter-kit`
- Kit remote: `https://github.com/baskduf/harness-starter-kit.git`
- Kit commit: `f06600e2baaadcc0930573409c850c11a3168ace`
- Applied profile: `spring`
- Source tracking: `.harness/source.json`

This source information is kit adoption traceability only. It is not proof of
agent effectiveness.

## Task Set

| Task ID | Scenario | Expected boundary | Common failure |
| --- | --- | --- | --- |
| ERP-001 | Add employee search by name | Employee controller, service, repository, DTOs, tests, task outcome, effectiveness report | Query logic added without service test |
| ERP-002 | Add purchase request amount validation | Purchase request DTO/entity if needed, service, controller if validation response is needed, tests, task outcome, effectiveness report | Validation exists only at controller boundary |
| ERP-003 | Add approval comment | Approval DTOs/service behavior, purchase request entity if stored there, response DTOs, approval/transition tests, optional glossary, task outcome, effectiveness report | Comment is returned but not persisted |
| ERP-004 | Add department field to employees | Employee entity, DTOs, service, controller if mapping changes, repository if needed, tests, optional glossary, task outcome, effectiveness report | Field missing from list/search response |
| ERP-005 | Add role-based access policy as documented behavior | Decision record, role type, access-policy class/service, policy tests, optional glossary/AGENTS, task outcome, effectiveness report | Security behavior claimed without tests or explicit deferral |

## Results

No comparable product-task runs have been completed yet.

| Metric | Baseline | Harnessed | Delta |
| --- | --- | --- | --- |
| Wrong-file edits | unknown | unknown | unknown |
| Repeated mistakes | unknown | unknown | unknown |
| First-pass verification success | unknown | unknown | unknown |
| Drift violations detected | unknown | unknown | unknown |
| Human rework minutes | unknown | unknown | unknown |
| Reverted files | unknown | unknown | unknown |

## Non-Comparable Setup Runs

| Run | Reason excluded | Use in metrics |
| --- | --- | --- |
| setup-2026-06-06 | Initial ERP MVP, harness adoption, source tracking, and setup verification | Excluded from comparable product-task count |

## Run Log

| Condition | Task ID | Run | Verification result | Notes |
| --- | --- | ---: | --- | --- |
| harnessed-only | setup | 1 | pass after non-comparable setup fix | Spring Boot coordinate corrected from generated `4.0.6.RELEASE` to resolvable `4.0.6`; setup is excluded from comparable product-task count |

## Changed-Files Consistency

| Task ID | Expected boundary | Actual changed files | Wrong-file edit result |
| --- | --- | --- | --- |
| setup | Setup files and initial ERP MVP | Initial repository contents | Not comparable |

## Source Records

- Task outcome records reviewed: none yet
- Repository refs compared: none yet
- Prompt refs compared:
  - `/Users/wb/Desktop/prompt/00-setup-only.md`
  - `/Users/wb/Desktop/prompt/01-erp-001-employee-search.md`
  - `/Users/wb/Desktop/prompt/02-erp-002-purchase-request-amount-validation.md`
  - `/Users/wb/Desktop/prompt/03-erp-003-approval-comment.md`
  - `/Users/wb/Desktop/prompt/04-erp-004-employee-department-field.md`
  - `/Users/wb/Desktop/prompt/05-erp-005-role-based-access-policy.md`
- Verification commands compared: `python scripts/check_harness.py`

## Interpretation

- Observed benchmark: no comparable product-task observations yet.
- What improved: unknown; no improvement claim is supported by this initial
  harnessed-only benchmark.
- What did not improve: unknown.
- Confounders or limitations: no baseline and no completed product-task records
  yet.
- Harness changes to make next: record ERP-001 through ERP-005 task outcomes as
  they run.
- Human rework interpretation: `unknown` is distinct from `0`; use `unknown`
  unless a human reviewer provides a value.

## Follow-Up

- Next review window: after each measurable ERP task.
- Owner or reviewer: unknown.
- Related decision or failure records:
  - `docs/decisions/0001-initial-spring-boot-erp-architecture.md`
