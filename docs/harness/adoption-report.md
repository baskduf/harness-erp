# Harness Adoption Report

## Target Repository Observed

- Repository: `harness-erp`
- Setup date: 2026-06-06
- Stack and framework: Java 21, Spring Boot 4.0.6, Maven wrapper, H2, Spring Web,
  Spring Data JPA, Validation, Spring Boot Test
- Package manager and commands: Maven wrapper, `./mvnw test`,
  `python scripts/check_harness.py`
- Local server, fixture, seed data, emulator, or device dependencies: no server
  dependency for tests; H2 in-memory database is configured for local/test use
- Existing docs or agent instructions: none before setup
- CI or verification path: local harness gate and GitHub Actions harness
  verification workflow
- Monorepo or special layout: no

## Files Added Or Changed

- `AGENTS.md`: project purpose, commands, source boundaries, generated-file
  rules, local config rules, completion checks, and security-policy caveat.
- `.harness/source.json`: harness-starter-kit source tracking.
- `docs/`: harness adoption report, Harness Doctor setup baseline, coding
  conventions, domain glossary, decision records, failure memory, effectiveness
  report, and task outcome records.
- `scripts/`: `check_harness.py`, `check_docs_drift.py`,
  `check_structure.py`, `check_effectiveness_plan.py`, and
  `check_failure_memory.py`.
- `.github/workflows/harness-verification.yml`: CI workflow that runs the local
  harness gate on pull requests and pushes to `main`.
- `src/`: minimal Spring Boot ERP MVP and staged ERP-001 through ERP-005
  product-task implementations.

## Existing Structures Reused

- The generated Maven wrapper, Spring Boot source layout, and Spring Boot test
  conventions were reused instead of adding a second build path.
- No pre-existing repository docs or CI existed before setup.

## Checks Run

```bash
python /Users/wb/Desktop/harness-starter-kit/scripts/harness_doctor.py --target .
```

Result: pass after a temporary out-of-repository `python` wrapper mapped the
staged command spelling to `/usr/bin/python3`; score 54/100, harness health
evidence only.

```bash
python scripts/check_harness.py
```

First setup verification result: fail. The generated Spring Initializr POM used
`spring-boot-starter-parent:4.0.6.RELEASE`, which Maven Central did not resolve.

Final setup verification result: pass after correcting the Spring Boot Maven
coordinate to `4.0.6`. Maven test summary: 11 tests run, 0 failures, 0 errors,
0 skipped.

## Kit Source

- Harness-starter-kit path: `/Users/wb/Desktop/harness-starter-kit`
- Harness-starter-kit remote: `https://github.com/harnessworks/harness-starter-kit.git`
- Harness-starter-kit commit: `de0737abf3808ecbd7eae50fcc7fab119594bd0d`
- Source tracking file: `.harness/source.json`
- Applied profile: `spring`
- Read-only reference material: yes

## Selected Technical Context

- Java version: `openjdk version "21.0.8" 2025-07-15`
- Spring Boot version: `4.0.6`
- Maven wrapper version: `3.3.4`
- Maven distribution: `Apache Maven 3.9.16`
- Dependency set:
  - `spring-boot-h2console`
  - `spring-boot-starter-data-jpa`
  - `spring-boot-starter-validation`
  - `spring-boot-starter-webmvc`
  - `h2`
  - `spring-boot-starter-data-jpa-test`
  - `spring-boot-starter-validation-test`
  - `spring-boot-starter-webmvc-test`

## Harness-Starter-Kit Files Read

- `/Users/wb/Desktop/harness-starter-kit/README.md`
- `/Users/wb/Desktop/harness-starter-kit/templates/profiles/spring/README.md`
- `/Users/wb/Desktop/harness-starter-kit/templates/profiles/spring/check_harness.py`
- `/Users/wb/Desktop/harness-starter-kit/templates/profiles/spring/gitignore.harness.txt`
- `/Users/wb/Desktop/harness-starter-kit/docs/templates/task-outcome.yaml`
- `/Users/wb/Desktop/harness-starter-kit/docs/templates/effectiveness-report.md`
- `/Users/wb/Desktop/harness-starter-kit/docs/templates/adoption-report.md`
- `/Users/wb/Desktop/harness-starter-kit/commands/harness-doctor.md`
- `/Users/wb/Desktop/harness-starter-kit/commands/harness-update.md`
- `/Users/wb/Desktop/harness-starter-kit/scripts/check_effectiveness_plan.py`
- `/Users/wb/Desktop/harness-starter-kit/scripts/check_failure_memory.py`

## Profile Absorption

- Spring profile snippets adopted:
  - Maven wrapper test command as the normal build check.
  - `scripts/check_harness.py` as a local completion gate.
  - GitHub Actions workflow that runs `python scripts/check_harness.py` with
    Java 21.
  - Generated build output and local config ignore rules.
- Spring profile snippets adapted:
  - Harness check script now runs Maven tests plus deterministic docs and
    structure checks for this repository.
  - Coding conventions now describe this ERP package layout and service-layer
    business rule ownership.
- Spring profile snippets skipped:
  - Gradle guidance, because this project uses Maven.
  - Flyway/Liquibase migration guidance, because setup uses Hibernate
    `create-drop` for the H2 MVP.
- Spring profile snippets deferred:
  - Database migration policy for a persistent database.

## Verification Gate Placement

- Normal completion gate: `python scripts/check_harness.py`
- CI gate: `.github/workflows/harness-verification.yml` runs
  `python scripts/check_harness.py` on pull requests and pushes to `main`.
- Deterministic behavior checks included in the normal gate:
  - `./mvnw test`
  - `python scripts/check_docs_drift.py`
  - `python scripts/check_structure.py`
  - `python scripts/check_effectiveness_plan.py`
  - `python scripts/check_failure_memory.py`
- Focused or manual checks outside the normal gate:
  - Running the Spring Boot server manually for interactive API use.
  - Harness Doctor baseline, which is harness health evidence only.
- Reasons for focused/manual placement:
  - The normal gate is deterministic and test-focused.
  - CI runs the same deterministic gate as operational evidence that the local
    completion check can run outside the developer machine.
  - Manual server smoke checks depend on a running process and are not required
    for product-task measurement.
  - Harness Doctor does not prove agent effectiveness.
  - CI verification does not prove agent effectiveness improvement.

## Server Or Fixture Verification

- Required: no for the benchmark completion gate, because service tests and the
  Spring Boot context test cover the MVP behavior without running a long-lived
  server.
- How to run: `./mvnw spring-boot:run` from `/Users/wb/Desktop/harness-erp` if
  interactive API testing is needed.
- Verification performed: automated context and service tests through
  `python scripts/check_harness.py`.
- Not applicable: no external seed data, emulator, hardware, or persistent
  fixture dependency is required.

## External API Verification

- Required: no; the MVP uses H2 and no external provider API.
- Boundary: not applicable.
- Provider boundary fixture: not applicable.
- Endpoint parameter contract: not applicable.
- Live/mock mode: not applicable.
- Secret handling and redaction checked: no external secrets are used; local
  config and `.env` files are ignored.
- Empty or zero-result behavior: covered where relevant by service tests.
- Provider error handling: not applicable.
- Focused smoke command or fixture: `python scripts/check_harness.py` is enough
  for this local-only benchmark.

## Feature Scenario Test Note

- Broad feature work: yes; setup plus ERP-001 through ERP-005 changed the MVP
  behavior across employees, purchase requests, approvals, and policy docs.
- Build-only validation is enough: no; service tests and policy tests are part
  of the normal gate.
- Scenarios covered for broad feature work: employee create/list/search/detail,
  purchase request creation/status defaults/amount validation, approval and
  rejection transitions/comments, department persistence, and role-policy
  allow/deny combinations.
- Manual or hardware-dependent checks: none.

## Generated-File And Local-Config Rules Added

- Generated build outputs are ignored: `target/`, `build/`, `out/`, `.gradle/`,
  `*.class`
- Local-only files are ignored: `.env`, `*.log`, `application-local.*`
- The local `harness-starter-kit/` directory is ignored and must not be copied
  into this target repository.

## Effectiveness Measurement Plan

- Evaluation mode: `harnessed-only-initial-benchmark`
- Baseline available: no
- Comparable tasks to repeat or track:
  - ERP-001: Add employee search by name.
  - ERP-002: Add purchase request amount validation.
  - ERP-003: Add approval comment.
  - ERP-004: Add department field to employees.
  - ERP-005: Add role-based access policy as documented behavior.
- Primary metric: wrong-file edits, repeated known mistakes, first-pass
  verification result, drift violations, reverted files, and human rework when
  a human reviewer supplies it.
- Review window: after each measurable ERP task and again after ERP-005
  aggregate evidence is complete.
- Human rework minutes: `unknown` unless a human reviewer provides a value.
- Results location: `docs/effectiveness/effectiveness-report.md`
- Task outcome records location: `docs/effectiveness/task-outcomes/`

## Failure Memory

- Recorded: `docs/failures/0001-spring-boot-coordinate-resolution.md`
- Detection or prevention check: `python scripts/check_harness.py` and
  `./mvnw test` detect unresolved Spring Boot Maven coordinates before a stage
  is considered complete.
- Skipped: no other setup or ERP-stage failure memory records were added; the
  other task checks passed first verification and were covered by task outcome
  records.

## Documentation Updated

- `AGENTS.md`: project purpose, commands, boundaries, generated-file rules,
  local config rules, and completion checks.
- `docs/conventions/coding.md`: Spring Boot coding conventions.
- `docs/domain/glossary.md`: ERP domain terms.
- `docs/decisions/0001-initial-spring-boot-erp-architecture.md`: setup
  architecture decision.
- `docs/failures/README.md`: when to record failure memory.
- `docs/failures/0001-spring-boot-coordinate-resolution.md`: Spring Boot
  coordinate resolution failure and prevention check.
- `docs/effectiveness/effectiveness-report.md`: setup and future benchmark plan.
- Behavior or integration decisions considered: Spring Boot architecture, H2
  persistence, purchase request status transitions, approval comment
  normalization, employee department modeling, and role-policy deferral of
  runtime HTTP security.
- Decision memory result: ADRs added for initial Spring Boot architecture and
  role-based access policy; failure memory added for Maven coordinate
  resolution.
- Not updated: README was not created because the harness evidence and
  `AGENTS.md` contain the required stage instructions for this benchmark.

## Drift Checks Added

- Baseline doc or structure hygiene checks: `scripts/check_docs_drift.py` and
  `scripts/check_structure.py`.
- Effectiveness evidence consistency check:
  `scripts/check_effectiveness_plan.py`.
- Failure-memory evidence check: `scripts/check_failure_memory.py`.
- Encoding or localization hygiene checks: ASCII-only convention documented;
  no separate encoding checker was needed for this Java MVP.
- Target-specific architecture checks: `scripts/check_structure.py` checks
  required package directories, ignored generated files, and controller/repository
  boundary.
- CI drift checks: `.github/workflows/harness-verification.yml` runs the same
  harness gate as the local completion check. This is operational evidence only
  and is not proof of agent-effectiveness improvement.

## CI Verification Maintenance 2026-06-06

- Run id: `MAINT-001`
- Workflow: `.github/workflows/harness-verification.yml`
- Triggers: pull requests and pushes to `main`
- Runtime: GitHub-hosted Ubuntu runner with Java 21 and Python 3.x
- Command: `python scripts/check_harness.py`
- Secrets: none required
- Evidence interpretation: CI is operational evidence that the harness gate is
  runnable in automation. It is not a comparable product-task run and does not
  demonstrate agent-effectiveness improvement.

## Harness Update 2026-06-06

- Command: `/harness update`
- Previous kit commit: `f06600e2baaadcc0930573409c850c11a3168ace`
- Updated kit commit: `387dbfabda3d63975494bdabfc812ddf64100919`
- Applied update: added `scripts/check_effectiveness_plan.py` from the updated
  kit and wired it into `python scripts/check_harness.py`.
- Source tracking: `.harness/source.json` now records `updated_at` and
  `update_command`.
- Skipped update candidates: dogfood checklist, validation examples, and kit
  README changes were kept as reference material because they are starter-kit
  governance content rather than target ERP behavior.
- Comparison boundary: this is non-comparable harness maintenance and is not
  evidence of agent-effectiveness improvement.

## Harness Update 2026-06-10

- Command: `/harness update`
- Previous kit commit: `416409d6ab611e23ba73355d539198025fef5ad5`
- Updated kit commit: `de0737abf3808ecbd7eae50fcc7fab119594bd0d`
- Applied update: canonicalized source tracking to
  `https://github.com/harnessworks/harness-starter-kit.git`, adopted the
  latest command-reference validation in `scripts/check_effectiveness_plan.py`,
  added `scripts/check_failure_memory.py`, and wired the failure-memory checker
  into `python scripts/check_harness.py`.
- Source tracking: `.harness/source.json` now records the canonical kit remote
  and the 2026-06-10 update date.
- Skipped update candidates: Harness Doctor v2 diagnostics, Rust and Go
  profiles, kit README/site/translation changes, and generic templates were
  kept as reference material because this target keeps Doctor outside the
  normal gate and remains a Spring Boot Maven ERP benchmark.
- Comparison boundary: this is non-comparable harness maintenance and is not
  evidence of agent-effectiveness improvement.

## Assumptions

- Java 21 is acceptable because it is available locally.
- Spring Boot 4.0.6 is acceptable because Spring Initializr reported it as the
  current default during setup. The generated POM used the metadata suffix
  `4.0.6.RELEASE`, which was corrected to the resolvable Maven Central
  coordinate `4.0.6` during non-comparable setup verification.
- The initial ERP MVP may expose REST endpoints without Spring Security because
  setup explicitly excludes Spring Security.
- `human_rework_minutes` is `unknown` until a human reviewer supplies a value.

## Remaining Manual Steps

- Monitor the GitHub Actions workflow after the next push or pull request.
- Decide whether to add persistent database migrations after the MVP stops using
  only H2 `create-drop`.
- Review Harness Doctor output as health evidence only; do not use it as proof
  of effectiveness improvement.
- A temporary Codex-session `python` wrapper was created outside the repository
  to map the staged `python` command spelling to `/usr/bin/python3`.

## Notes For Future Agents

- Do not repair setup inside measurable ERP stages. If setup evidence is missing,
  stop and report the precondition failure.
- Preserve first-pass verification evidence in each ERP task outcome record
  before fixing any failure.
