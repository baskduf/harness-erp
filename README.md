# Harness ERP

Harness ERP is a minimal Spring Boot internal ERP MVP used for staged
harness-engineering benchmark work. It intentionally keeps the product small so
task boundaries, verification, and evidence records stay easy to inspect.

## Stack

- Java 21
- Spring Boot 4.0.6
- Maven wrapper
- Spring Web MVC
- Spring Data JPA
- Jakarta Validation
- H2 in-memory database

## Run Checks

Use the local harness gate before finishing changes:

```bash
python scripts/check_harness.py
```

For product-behavior debugging, Maven tests can also be run directly:

```bash
./mvnw test
```

The GitHub Actions workflow in `.github/workflows/harness-verification.yml`
runs the same local harness gate on pull requests and pushes to `main`.

## Run The App

```bash
./mvnw spring-boot:run
```

The app uses H2 for local and test persistence. No external services, secrets,
or seed data are required.

## API Overview

Mutating endpoints accept a trusted `X-ERP-Role` header so the service layer can
enforce the documented role policy. This header is not authentication, and full
Spring Security is intentionally deferred.

| Method | Path | Purpose | Role |
| --- | --- | --- | --- |
| `POST` | `/employees` | Create an employee | `ADMIN` |
| `PUT` | `/employees/{employeeId}` | Update employee name and department | `ADMIN` |
| `GET` | `/employees` | List employees or search with `?name=` | none |
| `GET` | `/employees/{employeeId}` | Get employee detail | none |
| `POST` | `/purchase-requests` | Create a purchase request | `EMPLOYEE` |
| `GET` | `/purchase-requests` | List purchase requests, optionally filtered by `employeeId` and `status` | none |
| `GET` | `/purchase-requests/{purchaseRequestId}` | Get purchase request detail | none |
| `POST` | `/purchase-requests/{purchaseRequestId}/approve` | Approve a submitted purchase request | `MANAGER` |
| `POST` | `/purchase-requests/{purchaseRequestId}/reject` | Reject a submitted purchase request | `MANAGER` |
| `GET` | `/purchase-requests/{purchaseRequestId}/approvals` | List approval history | none |

## Harness Evidence

Harness source tracking and task evidence are kept in:

- `.harness/source.json`
- `docs/harness/adoption-report.md`
- `docs/effectiveness/effectiveness-report.md`
- `docs/effectiveness/task-outcomes/`

ERP-001 through ERP-005 are the initial comparable benchmark. ERP-006 through
ERP-009 are tracked separately as `harness-erp-follow-up-benchmark`. MAINT-001
is non-comparable maintenance and is excluded from comparable product-task
counts.

## Design Convention

If a UI is added, use the legacy internal ERP style specified in
`docs/conventions/legacy-erp-design.md`. The design convention does not add
HTTP runtime security; `X-ERP-Role` remains a trusted role input for
service-layer policy checks, and Spring Security is deferred.

## Notes For Contributors

- Application code lives under `src/main/java/com/example/harnesserp`.
- Tests live under `src/test/java/com/example/harnesserp`.
- Controllers delegate to services and must not call repositories.
- Business rules belong in the service or domain layer and should be covered by
  service tests.
- Do not commit generated build output, local secrets, logs, or
  `application-local.*` files.
