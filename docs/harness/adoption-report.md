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
- CI or verification path: local harness gate only
- Monorepo or special layout: no

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
- Harness-starter-kit remote: `https://github.com/baskduf/harness-starter-kit.git`
- Harness-starter-kit commit: `f06600e2baaadcc0930573409c850c11a3168ace`
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

## Profile Absorption

- Spring profile snippets adopted:
  - Maven wrapper test command as the normal build check.
  - `scripts/check_harness.py` as a local completion gate.
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
  - CI integration.
  - Database migration policy for a persistent database.

## Verification Gate Placement

- Normal completion gate: `python scripts/check_harness.py`
- Deterministic behavior checks included in the normal gate:
  - `./mvnw test`
  - `python scripts/check_docs_drift.py`
  - `python scripts/check_structure.py`
- Focused or manual checks outside the normal gate:
  - Running the Spring Boot server manually for interactive API use.
  - Harness Doctor baseline, which is harness health evidence only.
- Reasons for focused/manual placement:
  - The normal gate is deterministic and test-focused.
  - Manual server smoke checks depend on a running process and are not required
    for product-task measurement.
  - Harness Doctor does not prove agent effectiveness.

## Generated-File And Local-Config Rules Added

- Generated build outputs are ignored: `target/`, `build/`, `out/`, `.gradle/`,
  `*.class`
- Local-only files are ignored: `.env`, `*.log`, `application-local.*`
- The local `harness-starter-kit/` directory is ignored and must not be copied
  into this target repository.

## Effectiveness Measurement Plan

- Evaluation mode: `harnessed-only-initial-benchmark`
- Baseline available: no
- Comparable product-task benchmark runs:
  - ERP-001: Add employee search by name.
  - ERP-002: Add purchase request amount validation.
  - ERP-003: Add approval comment.
  - ERP-004: Add department field to employees.
  - ERP-005: Add role-based access policy as documented behavior.
- Primary metric: wrong-file edits, repeated known mistakes, first-pass
  verification result, drift violations, reverted files, and human rework when
  a human reviewer supplies it.
- Human rework minutes: `unknown` unless a human reviewer provides a value.
- Results location: `docs/effectiveness/effectiveness-report.md`
- Task outcome records location: `docs/effectiveness/task-outcomes/`

## Documentation Updated

- `AGENTS.md`: project purpose, commands, boundaries, generated-file rules,
  local config rules, and completion checks.
- `docs/conventions/coding.md`: Spring Boot coding conventions.
- `docs/domain/glossary.md`: ERP domain terms.
- `docs/decisions/0001-initial-spring-boot-erp-architecture.md`: setup
  architecture decision.
- `docs/failures/README.md`: when to record failure memory.
- `docs/effectiveness/effectiveness-report.md`: setup and future benchmark plan.

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

- Decide whether to add CI after the benchmark run.
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
