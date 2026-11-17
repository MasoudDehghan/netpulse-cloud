package io.netpulse.cloud.simulator;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import static org.assertj.core.api.Assertions.*;

class RouterSimulatorTest {
    @Test
    void emitsBoundedSamplesPerRouterAndAccurateSummary() throws Exception {
        List<TelemetryEvent> events = new CopyOnWriteArrayList<>();
        var result = RouterSimulator.run(new RouterSimulator.Config(3, 4, 0, 0, 42), events::add);
        var samples = events.stream().filter(TelemetryEvent.Sample.class::isInstance)
                .map(TelemetryEvent.Sample.class::cast).toList();
        assertThat(result.samples()).isEqualTo(12);
        assertThat(samples).hasSize(12).allSatisfy(s -> {
            assertThat(s.cpuPercent()).isBetween(0.0, 100.0);
            assertThat(s.memoryPercent()).isBetween(0.0, 100.0);
        });
        assertThat(samples.stream().map(TelemetryEvent.Sample::deviceId).distinct()).hasSize(3);
        assertThat(events.stream().filter(TelemetryEvent.HighCpu.class::isInstance).count())
                .isEqualTo(result.highCpuAlerts());
        assertThat(events.stream().filter(TelemetryEvent.HighMemory.class::isInstance).count())
                .isEqualTo(result.highMemoryAlerts());
    }

    @Test
    void rejectsUnsafeParametersAndPropagatesSinkFailures() {
        assertThatThrownBy(() -> new RouterSimulator.Config(0, 1, 0, 0, 1))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> RouterSimulator.run(new RouterSimulator.Config(1, 1, 0, 0, 1),
                e -> { throw new IllegalStateException("sink failure"); }))
                .isInstanceOf(java.util.concurrent.ExecutionException.class)
                .hasCauseInstanceOf(IllegalStateException.class);
    }

    @Test
    void eachRouterEmitsItsSamplesOnAVirtualThread() throws Exception {
        // Router ID -> number of samples received from that router.
        var samplesPerRouter = new ConcurrentHashMap<String, Integer>();

        // Record any thread that unexpectedly isn't virtual.
        var nonVirtualThreads = new ConcurrentLinkedQueue<String>();

        // Simulate 3 routers, each producing 4 samples.
        var config = new RouterSimulator.Config(
                3,   // devices
                4,   // samples per device
                0,   // interval in milliseconds
                0,   // startup jitter in milliseconds
                42   // random seed
        );

        var summary = RouterSimulator.run(config, event -> {
            // The sink also receives HighCpu events. Count only samples.
            if (event instanceof TelemetryEvent.Sample sample) {
                samplesPerRouter.merge(sample.deviceId(), 1, Integer::sum);

                Thread current = Thread.currentThread();
                if (!current.isVirtual()) {
                    nonVirtualThreads.add(current.getName());
                }
            }
        });

        // 3 routers × 4 samples = 12 total.
        assertThat(summary.samples()).isEqualTo(12);

        // Verify the individual counts—not just the overall total.
        assertThat(samplesPerRouter)
                .hasSize(3)
                .containsEntry("router-0001", 4)
                .containsEntry("router-0002", 4)
                .containsEntry("router-0003", 4);

        assertThat(nonVirtualThreads).isEmpty();
    }
}
