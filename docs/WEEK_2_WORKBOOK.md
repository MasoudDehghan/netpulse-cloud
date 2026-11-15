# Week 2 — Real Persistence Tests & Architecture Guardrails

**Budget:** 8–10 hours. **Goal:** explain and demonstrate real-database integration testing and dependency boundaries; don't add a skill to your CV until you have practiced it and can explain it.

> This workspace contains the original Week 1 baseline: it does not currently include the status PATCH or V3 alert-rules mission. If you completed those locally, **merge your work into this repository first** and resolve any test conflicts; don't overwrite your local work. The PostgreSQL test checks that V1 and V2 exist without assuming there cannot be later migrations.

## 1. Run the tests (45 minutes)

Prerequisites: JDK 21, network access to Maven Central and Docker Hub (or properly configured mirrors), Docker Engine/Desktop running and accessible to your user. From the repository root:

```bash
cd backend
./mvnw clean test      # unit, WebMvc, ArchUnit; no Docker required
./mvnw clean verify    # also executes *IT PostgreSQL and Redis tests; Docker required
```

On Windows use `mvnw.cmd`. If `./mvnw` lacks execute permission on Linux/macOS, run `chmod +x mvnw`. Failsafe runs integration tests in `verify`, not `test`; **a passing `test` alone is not a passing full suite**. Integration tests fail rather than silently skip when Docker is unavailable. CI already runs `clean verify` on GitHub's Ubuntu runner, where Docker must be accessible. Examine `backend/target/surefire-reports/` and `backend/target/failsafe-reports/` for details.

`PostgresPersistenceIT` uses PostgreSQL 16 in an ephemeral container, `@ServiceConnection` for connection details, `@AutoConfigureTestDatabase(replace = NONE)` so Spring doesn't substitute H2, real Flyway SQL migrations, and `ddl-auto=validate`. The original H2 test and H2 dependency were replaced. Read the query, rollback, and stale-version tests. A test container's data is discarded after the test; it is independent of your Compose database.

`RedisContainerIT` is an independent Redis protocol smoke test (PING and SET/GET). **It does not test an application Redis adapter**; the application doesn't have one yet. Redis isn't wired through `@ServiceConnection` because there is currently no Redis client or application Redis configuration.

## 2. Understand the boundaries (1.5 hours)

Open `ArchitectureTest.java`. The five rules prohibit: Spring in domain, JPA in domain, application depending on API/infrastructure, controllers depending on persistence or domain repository ports, and infrastructure depending on API. They check Java dependencies, not runtime call graphs or every possible architectural rule. `@AnalyzeClasses` excludes test classes; generated API classes are scanned as application code but are not themselves placed in the controller package.

**Exercise:** temporarily add an unused `jakarta.persistence.Entity` import *and annotate* a domain class with `@Entity`; run `./mvnw -Dtest=ArchitectureTest test`, observe the domain JPA rule fail, then revert the change. An unused import alone is not compiled into a class dependency, so it is not a reliable test. Don't commit the deliberate violation.

## 3. Your three coding missions (5–6 hours)

1. **PostgreSQL constraint and transaction (2 h):** Add a `PostgresPersistenceIT` test that saves a router and then attempts a duplicate hostname with a different ID and management IP. Use an explicit transaction, force a flush, assert the appropriate Spring data-integrity exception and verify from a separate transaction that only the original row remains. Explain why a pre-insert `exists` check alone cannot protect against concurrent registrations.
2. **Verify optimistic locking (1.5–2 h):** Run the existing stale-version test. Extend it to update a second field and assert both the winner's data and the incremented version remain after the losing update. Explain the SQL `WHERE id = ? AND version = ?` idea and what a client should do after a conflict. Avoid claiming these sequential stale writes prove load/concurrency behavior.
3. **Add an ArchUnit rule (1.5–2 h):** Prohibit the API layer from depending directly on `..infrastructure.persistence..`, including mapper and error packages. Make the rule fail with a temporary dependency, then remove it. Run both test commands and push your changes to your own branch/repository when ready.

## 4. Explain it like a senior engineer (1–2 hours)

Practice aloud: Why real Postgres rather than relying only on H2? What does `@ServiceConnection` provide? How do you test rollback outside an enclosing test transaction? What do migrations and `ddl-auto=validate` each check, and what can't Hibernate validate? How does optimistic locking detect a lost update? What can't a static ArchUnit rule enforce? Why isn't the Redis smoke test evidence that the application uses Redis?

**Exit criteria:** Full `./mvnw clean verify` passes on a Docker-enabled machine with zero skipped tests; complete the missions, review the generated test reports, and verify your CI run. Share your diffs or logs for review before adding these skills to your CV.
