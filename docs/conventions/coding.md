# Spring Boot Coding Conventions

## Package Layout

- Use `controller`, `service`, `repository`, `domain`, and `dto` packages under
  `com.example.harnesserp`.
- Keep REST request and response shapes in `dto`.
- Keep JPA persistence objects in `domain`.

## Controller Rules

- Controllers delegate to services.
- Controllers must not import repositories or enforce core business rules.
- Request validation annotations may guard HTTP input, but service/domain logic
  must still enforce business invariants.

## Service And Domain Rules

- Services coordinate repositories and transactions.
- Purchase request status transitions are business rules and belong in the
  service/domain layer.
- Invalid business operations should raise `BusinessRuleException` or a specific
  domain exception adapted by the controller advice.

## Repository Rules

- Repositories should remain Spring Data interfaces.
- Add query methods only for behavior required by the active stage.

## Test Rules

- Business rules require service tests.
- Keep the Spring Boot context test.
- Run `python scripts/check_harness.py` before committing a stage.

## Generated And Local Files

- Do not edit or commit generated outputs from `target/`, `build/`, `out/`,
  `.gradle/`, or compiled `.class` files.
- Do not commit local secrets, logs, `.env`, or machine-specific
  `application-local.*` files.
