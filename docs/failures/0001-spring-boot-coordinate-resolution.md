# 0001 Spring Boot Coordinate Resolution

## Date Observed

2026-06-06

## Failure Type

Failed check and cross-environment mismatch.

## Goal

The setup Spring Boot Maven project should resolve dependencies and run the
normal verification gate with the generated Maven wrapper.

## What Happened Or Was Tried

Spring Initializr generated a POM with
`org.springframework.boot:spring-boot-starter-parent:4.0.6.RELEASE`. The first
setup verification attempt ran:

```bash
python scripts/check_harness.py
```

Maven could not resolve that parent POM from Maven Central.

## Why It Failed

Maven Central published the Spring Boot parent as `4.0.6` without the
`.RELEASE` suffix. The generated metadata-style version string did not match
the resolvable Maven artifact coordinate.

## Current Replacement

The POM now uses the resolvable coordinate:

```text
org.springframework.boot:spring-boot-starter-parent:4.0.6
```

The setup verification passed after this non-comparable setup correction.

## Detection Or Prevention Check

Run these checks before finishing similar setup or dependency-coordinate work:

```bash
python scripts/check_harness.py
./mvnw test
```

`python scripts/check_harness.py` includes `./mvnw test`, so either command
detects parent POM resolution failures before a stage is considered complete.

## Agent Guidance

Do not reuse Spring Initializr metadata suffixes such as `.RELEASE` as Maven
coordinates without verifying that Maven Central resolves them. Prefer the
actual published Maven coordinate and finish by running
`python scripts/check_harness.py`.
