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

### 4. Run Automated Test Suite
```bash
cd backend
./mvnw clean verify
```

---

## Incremental Architecture Roadmap

- [x] **Milestone 1:** Contract-First OpenAPI 3, Hexagonal Domain Isolation, Flyway SQL Migrations (`V1`/`V2`), RFC 7807 `ProblemDetail`, Angular 20 Signal Store & GitHub Actions CI
- [ ] **Milestone 2:** `Testcontainers` (PostgreSQL & Redis) Integration Suite + `ArchUnit` Layer Guardrails
- [ ] **Milestone 3:** 1,000-Router Java 21 Virtual Thread Telemetry Simulator + Keycloak OAuth2/OIDC (PKCE)
- [ ] **Milestone 4:** Apache Kafka Telemetry Pipeline + Transactional Outbox + Idempotent Consumers
- [ ] **Milestone 5:** Playwright E2E Browser Suite + OpenTelemetry / Prometheus / Grafana + Kubernetes Helm Chart
