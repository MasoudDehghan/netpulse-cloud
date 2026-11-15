package io.netpulse.cloud.domain.model;


public record DeviceFilter(
        DeviceVendor vendor,
        DeviceStatus status,
        String region,
        int page,
        int size
) {}
