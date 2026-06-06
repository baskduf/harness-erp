# 0002 Role-Based Access Policy Representation

## Status

Accepted

## Context

ERP-005 requires a role-based access policy as documented behavior with a
minimal code-level policy representation. ERP-006 requires that policy to be
enforced by public mutating service entrypoints. ERP-009 extends the employee
master-data policy to employee updates. ERP-010 adds Spring Security
request-level authorization for mutating HTTP endpoints.

## Decision

Add a simple `Role` enum and `AccessPolicy` service that answers whether a role
may perform these operations:

- `ADMIN` may create employees.
- `ADMIN` may update employees.
- `EMPLOYEE` may create purchase requests.
- `MANAGER` may approve or reject purchase requests.

The policy is covered by focused unit tests.

ERP-006 wires the policy into the service layer:

- Employee creation service calls require an explicit `ADMIN` caller role.
- Employee update service calls require an explicit `ADMIN` caller role.
- Purchase request creation service calls require an explicit `EMPLOYEE`
  caller role.
- Approval and rejection service calls require an explicit `MANAGER` caller
  role.

Controllers pass a caller-supplied `X-ERP-Role` header into the service layer so
the service entrypoints always receive explicit role input.

ERP-010 wires the same role matrix into Spring Security:

- `RoleHeaderAuthenticationFilter` maps a valid `X-ERP-Role` header to
  `ROLE_ADMIN`, `ROLE_EMPLOYEE`, or `ROLE_MANAGER`.
- `SecurityConfig` requires `ADMIN` for employee create/update, `EMPLOYEE` for
  purchase request creation, and `MANAGER` for approval decisions.
- Static resources and read endpoints remain public.
- Service-layer checks remain in place as defense in depth.

## Runtime Security Boundary

The application now has tested Spring Security request-level authorization for
mutating endpoints. The `X-ERP-Role` header is still a local benchmark role
input. It is not production-grade user identity, password login, SSO, or
per-user authorization.

## Consequences

- The access policy is explicit, testable, and stable for future integration.
- Mutating service operations cannot be called publicly without explicit role
  input for the documented policy checks.
- Mutating HTTP endpoints are blocked by Spring Security before controller
  handling when the role header is missing or insufficient.
- Future identity work can replace the role-header input with real
  authentication without changing the documented role semantics.
