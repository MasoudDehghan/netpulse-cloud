package io.netpulse.cloud.domain.exception;

public class DeviceAlreadyExistsException extends RuntimeException {
    private final String conflictField;
    private final String conflictValue;

    public DeviceAlreadyExistsException(String conflictField, String conflictValue) {
        super("Device with %s '%s' already exists in inventory".formatted(conflictField, conflictValue));
        this.conflictField = conflictField;
        this.conflictValue = conflictValue;
    }

    public String getConflictField() {
        return conflictField;
    }

    public String getConflictValue() {
        return conflictValue;
    }
}
