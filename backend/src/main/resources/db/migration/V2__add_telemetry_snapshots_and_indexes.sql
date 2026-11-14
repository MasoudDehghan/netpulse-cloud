-- V2__add_telemetry_snapshots_and_indexes.sql
-- Stores latest & historical point-in-time device health telemetry snapshots.

CREATE TABLE telemetry_snapshots (
    id                  UUID                        NOT NULL,
    device_id           UUID                        NOT NULL,
    cpu_pct             DOUBLE PRECISION            NOT NULL,
    memory_pct          DOUBLE PRECISION            NOT NULL,
    latency_ms          DOUBLE PRECISION            NOT NULL,
    packet_loss_pct     DOUBLE PRECISION            NOT NULL,
    rf_snr_db           DOUBLE PRECISION,
    recorded_at         TIMESTAMP WITH TIME ZONE    NOT NULL,

    CONSTRAINT pk_telemetry_snapshots PRIMARY KEY (id),
    CONSTRAINT fk_telemetry_device FOREIGN KEY (device_id)
        REFERENCES devices (id) ON DELETE CASCADE
);

CREATE INDEX idx_telemetry_device_recorded_at
    ON telemetry_snapshots (device_id, recorded_at DESC);
