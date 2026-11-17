# NetPulse Cloud

[![NetPulse Cloud CI](https://github.com/MasoudDehghan/netpulse-cloud/actions/workflows/ci.yml/badge.svg)](https://github.com/MasoudDehghan/netpulse-cloud/actions/workflows/ci.yml)
![Java 21](https://img.shields.io/badge/Java-21_LTS_(Virtual_Threads)-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot 3.4](https://img.shields.io/badge/Spring_Boot-3.4-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Angular 20](https://img.shields.io/badge/Angular-20_(Signals_%2B_OnPush)-DD0031?style=flat-square&logo=angular&logoColor=white)
![OpenAPI 3](https://img.shields.io/badge/Contract--First-OpenAPI_3.0-6BA539?style=flat-square&logo=openapiinitiative&logoColor=white)
![Flyway](https://img.shields.io/badge/DB_Migrations-Flyway-CC0200?style=flat-square&logo=flyway&logoColor=white)

Cloud-native **Network Device Inventory, Real-Time Telemetry & Automated Remediation Platform** built with **Java 21**, **Spring Boot 3.4**, **PostgreSQL (Flyway)**, and **Angular 20 (Standalone + Signals)**.

---

## System Architecture Overview

The diagram below is the **planned target architecture**, not a claim that every service is implemented today. See the roadmap below for current progress.

```mermaid
flowchart LR
    Sim["Device Simulator\n(1,000 Virtual-Thread Routers)"] -->|High-Rate Telemetry| Ing["Telemetry Ingestor\n(Java 21 · Spring Boot 3.4)"]
    Ing -->|Partitioned by deviceId| Kafka[("Apache Kafka\n(KRaft)")]
    Kafka --> Core["Inventory & Alert Service\n(Hexagonal · JPA · Outbox)"]
    Kafka --> Rem["Remediation Worker\n(Idempotent Consumer + DLT)"]
    Core <--> PG[("PostgreSQL 16\n(Flyway DDL)")]
    Core <--> Redis[("Redis 7")]
    Core -->|Live Alerts (SSE) & OpenAPI REST| UI["Operator Console\n(Angular 20 · Signals · OnPush)"]
    UI <-->|OAuth2 / OIDC PKCE| KC["Keycloak IAM"]
```

---

## Key Engineering Design Choices

- **Contract-First REST (`contracts/netpulse-openapi.yaml`):** A single OpenAPI 3.0.3 specification generates both the Spring Boot 3 Jakarta API interfaces (`interfaceOnly=true`) during `mvn generate-sources` and typed Angular TypeScript clients (`npm run generate:api`), eliminating frontend/backend contract drift.
- **Zero Auto-DDL (`spring.jpa.hibernate.ddl-auto: validate`):** All schema changes are version-controlled SQL scripts managed by **Flyway** (`db/migration/V1__...sql`, `V2__...sql`) with explicit composite indexes and `@Version` optimistic locking.
- **Framework-Free Domain Core (`io.netpulse.cloud.domain`):** Domain records, enums, exceptions, and repository ports contain **zero** `org.springframework.*` or `jakarta.persistence.*` dependencies (Clean / Hexagonal isolation).
- **RFC 7807 Error Contract (`ProblemDetail`):** All domain conflicts (`409`), missing resources (`404`), and validation errors (`400`) return standardized `application/problem+json` payloads with machine-readable `errorCode` properties.
- **Java 21 Virtual Threads & Kubernetes Probes:** Configured out of the box with `spring.threads.virtual.enabled=true`, graceful shutdown, and Actuator `/actuator/health/liveness` & `/readiness` probes.

---

## Repository Structure

```text
netpulse-cloud/
├── contracts/
│   └── netpulse-openapi.yaml       # Single source-of-truth OpenAPI 3.0.3 specification
├── backend/                        # Java 21 + Spring Boot 3.4 + Flyway + JPA Hexagonal backend
├── frontend/                       # Angular 20 Standalone + Signals + OnPush operator UI
├── docker-compose.yml              # Local PostgreSQL 16 + Redis 7 with healthchecks
├── .devcontainer/                  # One-click GitHub Codespaces environment
└── .github/workflows/ci.yml        # Automated Backend & Frontend CI verification
```

---

## Quick Start (Local Development)

### 1. Start PostgreSQL 16 & Redis 7
```bash
docker compose up -d
```

### 2. Run Backend (`http://localhost:8080`)
```bash
cd backend
./mvnw spring-boot:run
```

### 3. Run Angular 20 Console (`http://localhost:4200`)
```bash
cd frontend
npm install
npm start
```

### 4. Run Automated Tests
```bash
cd backend
./mvnw clean test    # unit, WebMvc, ArchUnit; Docker not required
./mvnw clean verify  # includes PostgreSQL and Redis Testcontainers integration tests
```

The full `verify` needs a running Docker daemon and access to `postgres:16-alpine` and `redis:7.4-alpine`; it fails if Docker is unavailable. The Redis test is a container smoke test, not an application integration. See [Week 2 workbook](docs/WEEK_2_WORKBOOK.md) for exercises and test details.

### 5. Run the Week 3 local simulator
```bash
cd backend
./mvnw -q compile exec:java -Dexec.mainClass=io.netpulse.cloud.simulator.RouterSimulator
```
The default run uses 1,000 finite virtual-thread router tasks and emits in-memory samples only; it does not connect to devices or the backend. For configuration and the separate HTTP concurrency experiment, see the [Week 3 workbook](docs/WEEK_3_WORKBOOK.md) and [ADR 001](docs/adr/001-virtual-threads-vs-webflux.md).

---

## Incremental Architecture Roadmap

- [x] **Milestone 1:** Contract-First OpenAPI 3, Hexagonal Domain Isolation, Flyway SQL Migrations (`V1`/`V2`), RFC 7807 `ProblemDetail`, Angular 20 Signal Store & GitHub Actions CI
- [ ] **Milestone 2 (in progress):** PostgreSQL Testcontainers persistence tests, Redis container smoke test and ArchUnit guardrails; full Docker-backed verification and exercises still required
- [ ] **Milestone 3 (in progress):** Finite in-memory virtual-thread router simulator and local HTTP concurrency experiment; telemetry transport and Keycloak OAuth2/OIDC remain planned
- [ ] **Milestone 4:** Apache Kafka Telemetry Pipeline + Transactional Outbox + Idempotent Consumers
- [ ] **Milestone 5:** Playwright E2E Browser Suite + OpenTelemetry / Prometheus / Grafana + Kubernetes Helm Chart
