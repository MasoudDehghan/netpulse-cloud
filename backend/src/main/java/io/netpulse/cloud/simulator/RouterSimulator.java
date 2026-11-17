package io.netpulse.cloud.simulator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;

/** Finite local workload: each simulated router owns its state on one virtual thread. */
public final class RouterSimulator {
    private RouterSimulator() { }

    public record Config(int devices, int samplesPerDevice, long intervalMillis, long maxJitterMillis, long seed) {
        public Config {
            if (devices < 1 || devices > 10_000 || samplesPerDevice < 1 || samplesPerDevice > 1_000
                    || intervalMillis < 0 || intervalMillis > 60_000 || maxJitterMillis < 0 || maxJitterMillis > 60_000) {
                throw new IllegalArgumentException("devices 1..10000, samples 1..1000, interval/jitter 0..60000 ms");
            }
        }
    }

    public record Summary(long samples, long highCpuAlerts, long highMemoryAlerts) { }


    /** Sink must be thread-safe and fast; blocking sinks apply backpressure to each router task. */
    public static Summary run(Config config, Consumer<TelemetryEvent> sink)
            throws InterruptedException, ExecutionException {
        LongAdder samples = new LongAdder();
        LongAdder alerts = new LongAdder();
        LongAdder memoryAlerts = new LongAdder();
        var factory = Thread.ofVirtual().name("router-", 0).factory();
        var executor = Executors.newThreadPerTaskExecutor(factory);
        List<Future<?>> futures = new ArrayList<>(config.devices());
        try {
            for (int i = 0; i < config.devices(); i++) {
                final int index = i;
                futures.add(executor.submit(() -> {
                    Random random = new Random(config.seed() + index);
                    if (config.maxJitterMillis() > 0) {
                        Thread.sleep(random.nextLong(config.maxJitterMillis() + 1));
                    }
                    double cpu = 40 + random.nextDouble() * 50;
                    double memory = 35 + random.nextDouble() * 50;
                    String id = "router-%04d".formatted(index + 1);
                    for (int cycle = 0; cycle < config.samplesPerDevice(); cycle++) {
                        cpu = clamp(cpu + random.nextDouble() * 12 - 6);
                        memory = clamp(memory + random.nextDouble() * 6 - 3);
                        Instant now = Instant.now();
                        sink.accept(new TelemetryEvent.Sample(id, now, cpu, memory));
                        samples.increment();
                        if (cpu >= 85) {
                            sink.accept(new TelemetryEvent.HighCpu(id, now, cpu));
                            alerts.increment();
                        }
                        if(memory >= 75){
                            sink.accept(new TelemetryEvent.HighMemory(id, now, memory));
                            memoryAlerts.increment();
                        }
                        if (cycle + 1 < config.samplesPerDevice()) Thread.sleep(config.intervalMillis());
                    }
                    return null;
                }));
            }
            for (Future<?> future : futures) future.get();
            return new Summary(samples.sum(), alerts.sum(), memoryAlerts.sum());
        } catch (InterruptedException | ExecutionException | RuntimeException e) {
            futures.forEach(f -> f.cancel(true));
            executor.shutdownNow();
            throw e;
        } finally {
            executor.shutdown();
        }
    }

    static double clamp(double value) { return Math.max(0, Math.min(100, value)); }

    public static void main(String[] args) throws Exception {
        int devices = args.length > 0 ? Integer.parseInt(args[0]) : 1_000;
        int samples = args.length > 1 ? Integer.parseInt(args[1]) : 5;
        long interval = args.length > 2 ? Long.parseLong(args[2]) : 100;
        long jitter = args.length > 3 ? Long.parseLong(args[3]) : 250;
        if (args.length > 4) throw new IllegalArgumentException("usage: devices samples intervalMillis maxJitterMillis");
        long start = System.nanoTime();
        Summary summary = run(new Config(devices, samples, interval, jitter, 42), event -> {
            // Java 21 exhaustive pattern switch; no per-event logging or network I/O.
            switch (event) {
                case TelemetryEvent.Sample s -> { }
                case TelemetryEvent.HighCpu a -> { }
                case TelemetryEvent.HighMemory m -> {
                    System.out.println("High Memory is detected with value "+m);
                }
            }
        });
        System.out.printf("devices=%d samples=%d highCpuAlerts=%d highMemoryAlerts=%d elapsedMs=%.1f%n", devices,
                summary.samples(), summary.highCpuAlerts(), summary.highMemoryAlerts(), (System.nanoTime() - start) / 1_000_000.0);
    }
}
