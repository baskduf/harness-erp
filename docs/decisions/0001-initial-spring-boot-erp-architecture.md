# 0001 Initial Spring Boot ERP Architecture

## Status

Accepted

## Context

The setup stage needs a minimal runnable internal ERP MVP and a durable harness
evidence structure. The setup prompt requires Spring Boot, Maven wrapper, H2,
Spring Web, Spring Data JPA, Validation, and Spring Boot Test. It also requires
setup to remain non-comparable and to avoid Spring Security.

## Decision

Use a conventional single-module Spring Boot application with:

- Java 21.
- Maven wrapper.
- Spring Boot 4.0.6.
- H2 in-memory database.
- JPA entities in `domain`.
- Spring Data repositories in `repository`.
- Transactional services in `service`.
- REST controllers in `controller`.
- Request/response records in `dto`.

Controllers do not access repositories directly. Business rules for purchase
request status transitions are enforced by service/domain code and covered by
service tests.

## Consequences

- The MVP is small enough for staged benchmark changes.
- Hibernate `create-drop` is acceptable for the local H2 MVP.
- Persistent database migrations, CI, and Spring Security are deferred until a
  task explicitly requires them.
