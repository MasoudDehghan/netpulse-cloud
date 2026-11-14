package io.netpulse.cloud.domain.model;

public record FleetSummary(
        long totalDevices,
        long healthyCount,
        long degradedCount,
        long unreachableCount,
        long maintenanceCount
) {}