# Repository review — 2026-11-02

## Verified

OpenJDK 17 / Spring Boot 3.4.5: seven integration tests pass against H2 in PostgreSQL compatibility mode; executable JAR packaging succeeds. Tests exercise empty listing, creation/readback through JPA, missing/invalid IDs, validation, overwrite protection and malformed JSON. Linux Maven Wrapper startup was checked separately after adding its missing properties file.

## Changes

Added the root README and request examples. Aligned the Java requirement with the code's tested Java 17 support. Creation returns 201 with Location and a JSON body. Missing records return 404 instead of an unhandled server error; invalid IDs return 400. New records reject a supplied ID and validate nonblank, bounded names/stacks.

Configured PostgreSQL creation explicitly in Compose and replaced the destructive create-drop development default with update. Connection settings now accept environment overrides. Compose uses a PostgreSQL 16 image, a persistent volume, localhost-bound port and a readiness check. Test configuration uses an isolated H2 database and a non-agent Mockito mock maker. Generated target files are removed from the current tree and ignored.

## Limits

Docker/PostgreSQL was not run in this environment. H2 tests do not prove PostgreSQL-specific behavior. Existing database volumes are not automatically migrated when environment variables change. The API implements list/read/create only; no update/delete/authentication layer is claimed. PostgreSQL defaults are for local demonstrations. This is not a dependency-security audit or production deployment review.
