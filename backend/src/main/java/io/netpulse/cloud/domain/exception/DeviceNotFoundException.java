package io.netpulse.cloud.domain.exception;

import java.util.UUID;

public class DeviceNotFoundException extends RuntimeException {
    private final UUID deviceId;

    public DeviceNotFoundException(UUID deviceId) {
        super("Network device with id '%s' was not found".formatted(deviceId));
        this.deviceId = deviceId;
    }

    public UUID getDeviceId() {
        return deviceId;
    }
}
