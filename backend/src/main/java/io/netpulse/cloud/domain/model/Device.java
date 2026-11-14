package io.netpulse.cloud.domain.model;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public record Device(
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
    public Device {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(hostname, "hostname must not be null");
        Objects.requireNonNull(managementIp, "managementIp must not be null");
        Objects.requireNonNull(vendor, "vendor must not be null");
        Objects.requireNonNull(region, "region must not be null");
        Objects.requireNonNull(firmwareVersion, "firmwareVersion must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }

    public static Device createNew(
            String hostname,
            String managementIp,
            DeviceVendor vendor,
            String region,
            String firmwareVersion,
            OffsetDateTime now
    ) {
        return new Device(
                UUID.randomUUID(),
                hostname.trim().toLowerCase(),
                managementIp.trim(),
                vendor,
                region.trim(),
                firmwareVersion.trim(),
                DeviceStatus.PROVISIONING,
                null,
                now,
                now,
                0L
        );
    }
    // --- Builder Pattern Implementation ---

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id = UUID.randomUUID();
        private String hostname;
        private String managementIp;
        private DeviceVendor vendor;
        private String region;
        private String firmwareVersion;
        private DeviceStatus status = DeviceStatus.PROVISIONING;
        private OffsetDateTime lastSeenAt = null;
        private OffsetDateTime createdAt = OffsetDateTime.now();
        private OffsetDateTime updatedAt = OffsetDateTime.now();
        private long version = 0L;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder hostname(String hostname) {
            this.hostname = hostname != null ? hostname.trim().toLowerCase() : null;
            return this;
        }

        public Builder managementIp(String managementIp) {
            this.managementIp = managementIp != null ? managementIp.trim() : null;
            return this;
        }

        public Builder vendor(DeviceVendor vendor) {
            this.vendor = vendor;
            return this;
        }

        public Builder region(String region) {
            this.region = region != null ? region.trim() : null;
            return this;
        }

        public Builder firmwareVersion(String firmwareVersion) {
            this.firmwareVersion = firmwareVersion != null ? firmwareVersion.trim() : null;
            return this;
        }

        public Builder status(DeviceStatus status) {
            this.status = status;
            return this;
        }

        public Builder lastSeenAt(OffsetDateTime lastSeenAt) {
            this.lastSeenAt = lastSeenAt;
            return this;
        }

        public Builder createdAt(OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(OffsetDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder version(long version) {
            this.version = version;
            return this;
        }

        public Device build() {
            return new Device(
                    id,
                    hostname,
                    managementIp,
                    vendor,
                    region,
                    firmwareVersion,
                    status,
                    lastSeenAt,
                    createdAt,
                    updatedAt,
                    version
            );
        }
    }

}
