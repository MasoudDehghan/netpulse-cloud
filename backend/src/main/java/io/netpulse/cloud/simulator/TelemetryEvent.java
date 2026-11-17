package io.netpulse.cloud.simulator;

import java.time.Instant;

/** Local demo events, not the public OpenAPI contract or production domain model. */
public sealed interface TelemetryEvent permits TelemetryEvent.HighCpu, TelemetryEvent.HighMemory, TelemetryEvent.Sample {
    String deviceId();
    Instant recordedAt();

    record Sample(String deviceId, Instant recordedAt, double cpuPercent, double memoryPercent)
            implements TelemetryEvent { }
    record HighCpu(String deviceId, Instant recordedAt, double cpuPercent)
            implements TelemetryEvent { }
    record HighMemory(String deviceId, Instant recordedAt, double memoryPercent)
            implements TelemetryEvent { }
}
