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
- Spring Security
- Jackson
- Jakarta Validation
- H2 in-memory database

## Functional Scope

The MVP covers a small internal purchasing workflow:

- Employee master data can be listed, searched by name, created, viewed, and
  updated with a required department.
- Purchase requests can be created for employees, validated for positive
  amounts, viewed, and filtered by employee id, status, or both.
- Submitted purchase requests can be approved or rejected with optional
  persisted comments.
- Approval history can be read per purchase request in deterministic creation
  order.
- Role policy is enforced at request level by Spring Security and again at
  service mutating entrypoints.

## Run Checks

Use the local harness gate before finishing changes:

```bash
python scripts/check_harness.py
```

The harness gate runs Maven tests, documentation drift checks, project
structure checks, effectiveness evidence checks, and failure-memory checks.

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
or seed data are required. The static ERP workspace is served from
`http://localhost:8080/`. The local H2 console is enabled for development at
`http://localhost:8080/h2-console` with JDBC URL
`jdbc:h2:mem:harness_erp`, user `sa`, and a blank password.

## Static UI

The root page serves a legacy internal ERP workspace with these modules:

- Employee Management: list, search, detail load, create, and update employees.
- Purchase Requests: list, filter, detail load, create requests, and lookup
  employees.
- Approval Queue: list submitted requests and approve or reject them with an
  optional comment.
- Approval History: load persisted decision history for a purchase request.
- Role Policy Reference: show the role values expected by mutating API calls.

Use the role selector in the toolbar to send the `X-ERP-Role` header from the
UI. Spring Security authorizes mutating endpoints before controller handling,
and service-level policy denials or business errors are shown in the status
bar.

## API Overview

Mutating endpoints accept an `X-ERP-Role` header. Spring Security maps that
local benchmark role input to request authorities and rejects disallowed
mutating requests before controller handling. The service layer keeps the same
role policy checks as defense in depth. This is not production-grade user
identity, password login, or SSO.

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
ERP-010 are tracked separately as `harness-erp-follow-up-benchmark`. MAINT-001
through MAINT-004 are non-comparable maintenance and are excluded from
comparable product-task counts. FE-001 through FE-005 are tracked separately as
`harness-erp-frontend-follow-up`.

## Design Convention

If a UI is added, use the legacy internal ERP style specified in
`docs/conventions/legacy-erp-design.md`. The design convention is presentation
only; runtime endpoint authorization is handled by Spring Security with the
`X-ERP-Role` header.

The Employee Management tab can list, search, create, and update employees.
Use role `ADMIN` for employee create/update calls; non-admin role policy
denials are shown in the status bar.

The Purchase Requests tab can list, filter, and create purchase requests. Use
role `EMPLOYEE` for purchase request creation; non-employee role policy denials
are shown in the status bar.

The Approval Queue tab lists submitted purchase requests and supports approve
or reject actions with optional comments. Use role `MANAGER` for approval
actions; approval history is loaded from the persisted history endpoint.

## Notes For Contributors

- Application code lives under `src/main/java/com/example/harnesserp`.
- Tests live under `src/test/java/com/example/harnesserp`.
- Controllers delegate to services and must not call repositories.
- Business rules belong in the service or domain layer and should be covered by
  service tests.
- Do not commit generated build output, local secrets, logs, or
  `application-local.*` files.
