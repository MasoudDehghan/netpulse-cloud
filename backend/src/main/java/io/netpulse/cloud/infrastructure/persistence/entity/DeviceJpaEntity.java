package io.netpulse.cloud.infrastructure.persistence.entity;

import io.netpulse.cloud.domain.model.DeviceStatus;
import io.netpulse.cloud.domain.model.DeviceVendor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "devices")
public class DeviceJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "hostname", nullable = false, length = 128, unique = true)
    private String hostname;

    @Column(name = "management_ip", nullable = false, length = 45, unique = true)
    private String managementIp;

    @Enumerated(EnumType.STRING)
    @Column(name = "vendor", nullable = false, length = 32)
    private DeviceVendor vendor;

    @Column(name = "region", nullable = false, length = 64)
    private String region;

    @Column(name = "firmware_version", nullable = false, length = 64)
    private String firmwareVersion;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private DeviceStatus status;

    @Column(name = "last_seen_at")
    private OffsetDateTime lastSeenAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    protected DeviceJpaEntity() {
        // Required by JPA
    }

    public DeviceJpaEntity(
            UUID id,
            String hostname,
            String managementIp,
            DeviceVendor vendor,
            String region,
            String firmwareVersion,
            DeviceStatus status,
            OffsetDateTime lastSeenAt,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt,
            long version
    ) {
        this.id = id;
        this.hostname = hostname;
        this.managementIp = managementIp;
        this.vendor = vendor;
        this.region = region;
        this.firmwareVersion = firmwareVersion;
        this.status = status;
        this.lastSeenAt = lastSeenAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public UUID getId() { return id; }
    public String getHostname() { return hostname; }
    public String getManagementIp() { return managementIp; }
    public DeviceVendor getVendor() { return vendor; }
    public String getRegion() { return region; }
    public String getFirmwareVersion() { return firmwareVersion; }
    public DeviceStatus getStatus() { return status; }
    public OffsetDateTime getLastSeenAt() { return lastSeenAt; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public long getVersion() { return version; }
}
