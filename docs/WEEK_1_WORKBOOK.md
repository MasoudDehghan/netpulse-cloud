# Week 1 Workbook — Architecture Review & Your 3 Hands-On Missions

Your **Week 1 foundation (`netpulse-cloud`)** is already scaffolded, compiled against **OpenJDK 21 + Spring Boot 3.4.3 + Angular 20**, and passing **7/7 automated tests** (`Unit + WebMvc + Flyway/JPA Slice`).

Instead of wasting hours fighting Maven plugin versions, spend your **Week 1 study block (~6–8 hours)** mastering how the 4 senior patterns in this repo work — and completing the **3 hands-on coding missions** below so every line is in your own muscle memory for Dutch technical interviews.

---

## Part 1: Master the 4 Senior Patterns Already Wired Here (Interview Cheat Sheet)

### 1. Contract-First OpenAPI (`contracts/netpulse-openapi.yaml` → `DevicesApi`)
- **How it works:** Open `backend/pom.xml` and inspect `openapi-generator-maven-plugin`. Notice `<interfaceOnly>true</interfaceOnly>` and `<useSpringBoot3>true</useSpringBoot3>`.
- Every time `./mvnw compile` runs, Maven reads `../contracts/netpulse-openapi.yaml` and generates `io.netpulse.cloud.api.generated.DevicesApi` inside `target/generated-sources/openapi`.
- Your `DeviceController` writes `public class DeviceController implements DevicesApi`.
- **Why Dutch tech leads love this:** If a developer changes a field type or status code in `netpulse-openapi.yaml`, **Java compilation breaks immediately** until the controller matches the contract — and the Angular team can run `npm run generate:api` to regenerate strongly typed TypeScript interfaces from the exact same file.

### 2. Flyway + `spring.jpa.hibernate.ddl-auto: validate`
- **How it works:** Open `backend/src/main/resources/application.yml` and `db/migration/V1__init_devices.sql` + `V2__add_telemetry_snapshots_and_indexes.sql`.
- On startup, **Flyway** checks `flyway_schema_history`, runs any unapplied `V{N}__*.sql` scripts in order, and verifies SHA/CRC checksums of past migrations. Immediately afterward, **Hibernate** starts in `validate` mode and checks every `@Entity` (`DeviceJpaEntity`, `TelemetrySnapshotJpaEntity`) column-for-column against the real SQL tables. If a column type or nullability mismatches, startup fails fast!
- **Golden Flyway Rule for Interviews:** *Never edit an already-committed `V1` or `V2` SQL file* (Flyway checksum validation will reject startup!). Always add a new `V3__...sql` migration.

### 3. Clean / Hexagonal Package Separation (`domain` vs `infrastructure`)
- Inspect `backend/src/main/java/io/netpulse/cloud/domain/model/Device.java` and `domain/port/DeviceRepositoryPort.java`.
- Notice there are **zero** `jakarta.persistence.*` or `org.springframework.*` imports in `domain/`.
- `Device` is an immutable **Java 21 `record`**, while `DeviceJpaEntity` lives strictly inside `infrastructure/persistence/entity/` with `@Version private long version;` for **optimistic locking** (preventing two concurrent threads or operators from silently overwriting each other's router updates).
- *(In Week 2, you will write a 15-line `ArchUnit` test that fails the build if anyone ever imports Spring or JPA inside `domain/`!)*

### 4. RFC 7807 `ProblemDetail` Error Responses (`GlobalExceptionHandler.java`)
- Instead of inventing ad-hoc `{ "error": "..." }` JSON objects, Spring Boot 3 natively supports **RFC 7807 (`application/problem+json`)** via `org.springframework.http.ProblemDetail`.
- Check `DeviceControllerWebMvcTest.java` to see how `409 Conflict` (`DEVICE_ALREADY_EXISTS`) and `404 Not Found` (`DEVICE_NOT_FOUND`) return structured, standard HTTP problem documents.

---

## Part 2: Your 3 Hands-On Coding Missions to Finish Week 1

Complete these three small tasks yourself (~1–1.5 hours each) before telling me you're ready for **Week 2 (`Testcontainers` + `ArchUnit`)**:

### Mission 1: Add a Contract-First Endpoint (`PATCH /api/v1/devices/{deviceId}/status`)
1. Open `contracts/netpulse-openapi.yaml` and add a `patch:` operation under `/api/v1/devices/{deviceId}/status` (`operationId: updateDeviceStatus`) accepting `{ "status": "MAINTENANCE" }` (`UpdateDeviceStatusRequest`) and returning `200 OK` (`DeviceResponse`) or `404 Not Found` (`ProblemDetailResponse`).
2. Run `./mvnw generate-sources` — watch `DeviceController` fail to compile because `DevicesApi` now demands `updateDeviceStatus(...)`!
3. Add a pure domain method `public Device withStatus(DeviceStatus newStatus, OffsetDateTime now)` on your `Device` record, orchestrate it in `DeviceInventoryService`, implement the method in `DeviceController`, and add 1 unit test + 1 `@WebMvcTest` case.

### Mission 2: Write `V3__add_alert_rules.sql` + Validate with Hibernate
1. Create `backend/src/main/resources/db/migration/V3__add_alert_rules.sql` defining an `alert_rules` table (`id UUID PRIMARY KEY`, `name VARCHAR(100) NOT NULL UNIQUE`, `metric_type VARCHAR(32) NOT NULL`, `threshold_value DOUBLE PRECISION NOT NULL`, `severity VARCHAR(16) NOT NULL`, `enabled BOOLEAN NOT NULL DEFAULT TRUE`, `created_at TIMESTAMP WITH TIME ZONE NOT NULL`).
2. Create `AlertRuleJpaEntity.java` in `infrastructure/persistence/entity/`.
3. Update `FlywayMigrationAndJpaSliceTest` (`assertThat(flyway.info().applied()).hasSize(3)`) and run `./mvnw test`. Deliberately misspell a column name in `@Column(name = "...")` once just to watch `ddl-auto: validate` catch the mismatch immediately, then fix it!

### Mission 3: Push Your Repo & Light Up Your Green GitHub CI Badge
1. Update `GitHub_Profile_README_Updated.md` onto your profile repo (`MasoudDehghan/MasoudDehghan`) and make old 2021–2023 tutorial repos private.
2. Create a new public repo **`netpulse-cloud`** on GitHub and push this workspace folder:
   ```bash
   cd netpulse-cloud
   git remote add origin git@github.com:MasoudDehghan/netpulse-cloud.git
   git push -u origin main
   ```
   *(Tip: If local Docker/Maven downloads are slow in Tehran, click **Code → Codespaces → Create codespace on main** on GitHub to code inside `.devcontainer/devcontainer.json` with gigabit cloud speeds!)*
3. Verify the **NetPulse Cloud CI** workflow turns **green** on GitHub Actions!

---

When those 3 missions are done (or if you hit any question while implementing Mission 1 or 2), come back and say **"Start Week 2"** — and we will level up to **real PostgreSQL/Redis `Testcontainers` + `ArchUnit` architectural boundary tests**!
