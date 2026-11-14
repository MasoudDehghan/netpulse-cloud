package io.netpulse.cloud.domain.model;

import java.util.List;

public record DeviceFilter(
        DeviceVendor vendor,
        DeviceStatus status,
        String region,
        int page,
        int size
) {}
