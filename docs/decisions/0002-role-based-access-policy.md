# 0002 Role-Based Access Policy Representation

## Status

Accepted

## Context

ERP-005 requires a role-based access policy as documented behavior with a
minimal code-level policy representation. The task explicitly forbids adding
full Spring Security and requires any deferral of runtime security to be stated
clearly.

## Decision

Add a simple `Role` enum and `AccessPolicy` service that answers whether a role
may perform these operations:

- `ADMIN` may create employees.
- `EMPLOYEE` may create purchase requests.
- `MANAGER` may approve or reject purchase requests.

The policy is covered by focused unit tests. It is not wired into HTTP
authentication or request handling.

## Deferred Runtime Security

Full Spring Security, authenticated principals, request-level authorization,
and endpoint enforcement are intentionally deferred. The application does not
provide runtime access control for HTTP requests in this stage.

## Consequences

- The access policy is explicit, testable, and stable for future integration.
- Future security work can wire this policy into authentication and
  authorization without changing the documented role semantics.
- Documentation must not claim runtime security exists until a later task adds
  and verifies it.
