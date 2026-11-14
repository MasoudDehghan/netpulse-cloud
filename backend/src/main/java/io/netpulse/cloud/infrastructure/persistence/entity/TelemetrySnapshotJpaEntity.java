package io.netpulse.cloud.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "telemetry_snapshots")
public class TelemetrySnapshotJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "device_id", nullable = false)
    private DeviceJpaEntity device;

    @Column(name = "cpu_pct", nullable = false)
    private double cpuPct;

    @Column(name = "memory_pct", nullable = false)
    private double memoryPct;

    @Column(name = "latency_ms", nullable = false)
    private double latencyMs;

    @Column(name = "packet_loss_pct", nullable = false)
    private double packetLossPct;

    @Column(name = "rf_snr_db")
    private Double rfSnrDb;

    @Column(name = "recorded_at", nullable = false, updatable = false)
    private OffsetDateTime recordedAt;

    protected TelemetrySnapshotJpaEntity() {
        // Required by JPA
    }
}
