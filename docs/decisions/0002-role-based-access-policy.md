# 0002 Role-Based Access Policy Representation

## Status

Accepted

## Context

ERP-005 requires a role-based access policy as documented behavior with a
minimal code-level policy representation. ERP-006 requires that policy to be
enforced by public mutating service entrypoints. The tasks explicitly forbid
adding full Spring Security and require any deferral of runtime security to be
stated clearly.

## Decision

Add a simple `Role` enum and `AccessPolicy` service that answers whether a role
may perform these operations:

- `ADMIN` may create employees.
- `EMPLOYEE` may create purchase requests.
- `MANAGER` may approve or reject purchase requests.

The policy is covered by focused unit tests. It is not wired into HTTP
authentication.

ERP-006 wires the policy into the service layer:

- Employee creation service calls require an explicit `ADMIN` caller role.
- Purchase request creation service calls require an explicit `EMPLOYEE`
  caller role.
- Approval and rejection service calls require an explicit `MANAGER` caller
  role.

Controllers pass a caller-supplied `X-ERP-Role` header into the service layer so
the service entrypoints always receive explicit role input. This header is a
trusted test/API input, not authentication.

## Deferred Runtime Security

Full Spring Security, authenticated principals, and request-level authorization
are intentionally deferred. The application does not verify HTTP caller
identity in this stage.

## Consequences

- The access policy is explicit, testable, and stable for future integration.
- Mutating service operations cannot be called publicly without explicit role
  input for the documented policy checks.
- Future security work can wire this policy into authentication and
  authorization without changing the documented role semantics.
- Documentation must not claim runtime security exists until a later task adds
  and verifies it.
