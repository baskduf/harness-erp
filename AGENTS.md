# Agent Instructions

## Project Purpose

This repository is a minimal Spring Boot internal ERP MVP used for a staged
harness-engineering benchmark. Setup work is non-comparable; ERP-001 through
ERP-005 are the measurable product-task runs.

## Commands

- Run the normal completion gate with `python scripts/check_harness.py`.
- Run Maven tests directly with `./mvnw test` when debugging product behavior.
- Do not push to GitHub from this repository unless explicitly requested.

## Source Boundaries

- Application code lives under `src/main/java/com/example/harnesserp`.
- Tests live under `src/test/java/com/example/harnesserp`.
- Controllers delegate to services and must not import or call repositories.
- Business rules belong in the service/domain layer and must be covered by
  service tests.
- Harness evidence lives under `.harness`, `docs/harness`, and
  `docs/effectiveness`.

## Generated Files

Do not commit generated build output such as `target/`, `build/`, `out/`,
`.gradle/`, or compiled `.class` files. Do not copy or commit the local
`harness-starter-kit` directory.

## Local Configuration

Do not commit local secrets, `.env`, logs, or machine-specific
`application-local.*` files. H2 is the local and test database.

## Completion Checks

Before finishing a setup or product-task stage, run `python scripts/check_harness.py`.
For measurable ERP tasks, preserve the first verification result in the task
outcome YAML before applying any fixes, then update
`docs/effectiveness/effectiveness-report.md`.

For substantial harness-maintenance work that changes check scripts, command
workflows, source tracking, effectiveness evidence, first-pass verification
results, known failure paths, or failed CI/harness checks, record task outcome
evidence and keep it out of comparable product-task counts. For trivial
docs-only wording, typo, link-label, or formatting changes, record the skip
reason in the final report.

## Security Policy Caveat

ERP-005 defines and tests a minimal role-based access policy, but full Spring
Security and request-level runtime authorization are intentionally deferred. Do
not claim HTTP runtime security exists unless a later task implements and tests
it.
