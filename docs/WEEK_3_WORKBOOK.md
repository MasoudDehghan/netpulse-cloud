# Week 3 — Virtual Threads, a Router Simulator & Concurrency Trade-offs

**Time budget:** ~8–10 hours. **Prerequisites:** JDK 21 and Maven Wrapper; Docker is needed only for the Week 2 integration suite. Keep any Week 1/2 work you completed locally: this workspace does not contain your separate local edits unless you sync them first.

## What is implemented

- `backend/src/main/java/io/netpulse/cloud/simulator/RouterSimulator.java`: one finite virtual-thread task per simulated router; private CPU/memory state drifts within 0–100%; startup jitter and finite cycles; a thread-safe caller-supplied sink receives records. The default run models 1,000 routers × 5 samples, locally in memory.
- `TelemetryEvent.java`: sealed interface with record variants (`Sample`, `HighCpu`); the runner demonstrates an exhaustive Java 21 pattern-matching `switch`.
- `backend/src/test/java/io/netpulse/cloud/benchmark/LoopbackBenchmark.java`: compares fixed-pool and virtual-thread **blocking JDK HTTP clients** with **non-blocking Spring WebClient**, against the same artificial-delay loopback HTTP service. WebFlux is test-scope only.
- `docs/adr/001-virtual-threads-vs-webflux.md`: decision and limitations.

**Not implemented:** real device connections, a long-running simulator container, telemetry ingestion or persistence, chaos/remediation endpoints, Kafka, a Redis adapter, production throughput, or an application-level WebClient pipeline. The simulator and HTTP benchmark are separate experiments. Default simulator pacing is short for a demo; it isn't the earlier proposed 200 events/sec steady-state workload.

## First run (45–60 min)

From the repository root:

```bash
cd backend
./mvnw clean test
./mvnw -q exec:java \
  -Dexec.mainClass=io.netpulse.cloud.simulator.RouterSimulator \
  -Dexec.args="1000 5 100 250"
```

The four arguments are devices, samples per device, interval in milliseconds and maximum startup jitter in milliseconds. Defaults are the same. The output reports the number of samples, high-CPU alerts and elapsed wall time; the seed is fixed but actual scheduling and elapsed time vary. This does not write data to the API or database. Use `chmod +x mvnw` if needed on Linux/macOS; use `mvnw.cmd` on Windows.

## Loopback benchmark (1–2 hours)

```bash
# Prepare test-scope WebClient and the benchmark class:
./mvnw test-compile
./mvnw -q exec:java \
  -Dexec.mainClass=io.netpulse.cloud.benchmark.LoopbackBenchmark \
  -Dexec.classpathScope=test \
  -Dbench.requests=5000 -Dbench.concurrency=200 \
  -Dbench.workers=32 -Dbench.delayMs=20
```

Default workload is **5,000 total HTTP requests**, not 5,000 simultaneous connections. Virtual-thread and WebClient modes are capped at 200 in-flight operations; the fixed-pool mode has 32 workers. The server sleeps for 20 ms before replying. Compare completion count, failures, total elapsed time and throughput. `active-p95` is the 95th percentile of **successful request times after a fixed/virtual task acquires its permit or after WebClient subscription**; it excludes waiting in the fixed executor queue or virtual semaphore and may include connection acquisition. It is not end-to-end latency and the implementations use different HTTP clients. Batch time also includes setup/teardown. No application-level retries are configured (the client libraries may have their own connection behavior); any request reported as failed makes the command fail. Results vary with JVM warm-up, connection pool limits, CPU and OS scheduling.

To attempt a **5,000 in-flight cap**, explicitly set `-Dbench.concurrency=5000` and use sufficient local resources and OS file/socket limits; start smaller first. A configured cap does **not** prove that 5,000 connections were simultaneously established, and the fixed pool is still limited by `bench.workers`. If you see errors or timeouts, keep those results as failures, reduce concurrency, inspect logs and resource limits; do not compare a failed run as if it succeeded. Don't point the benchmark at a real or third-party host: the built-in server binds to loopback only.

**Record:** JDK version (`java -version`), OS/CPU, workload parameters, all success/error counts, elapsed time, active p95, and at least three runs per setup. Note that this simple test is not JMH and doesn't measure peak memory. For rigorous capacity work, run a separate load generator and service, measure end-to-end latency including admission queues, concurrency, memory, GC, network, pool pressure and failures, and use realistic I/O.

## Your missions (4–5 hours)

1. **Thread identity and stability (1.5 h):** Add a test showing every sample is received from a virtual thread, and assert *each* of several routers emits exactly the requested number of samples. Explain why one actor's state doesn't require a shared lock, but the shared event sink must be thread-safe. Run `./mvnw test`.
2. **Fault event with pattern switch (1.5–2 h):** Add a new `TelemetryEvent` record, update the sealed permits list and handle it exhaustively in the switch. Write a deterministic test for its generation, without adding a real REST endpoint or claiming production alerting. Notice the compiler error if a sealed variant is not handled.
3. **Concurrency comparison and reflection (1.5 h):** Run the loopback benchmark with 32 and 200 virtual/WebClient permits and record results and failures. Compare the fixed pool at 32 and 64 workers separately, keeping `workers <= concurrency`. Explain why per-request active p95 alone hides queueing, and why changing several parameters at once is not a controlled comparison.

## Interview rehearsal (1–2 hours)

Explain virtual threads versus platform threads; why sleeping virtual threads don't imply one OS thread per router; why virtual threads do not eliminate CPU, database, memory or downstream limits; Java 21 pinning while blocking inside `synchronized`; how `ReentrantLock` can help without eliminating the need for careful locking; what reactive demand means; and why 5,000 requests at a 200 cap are not 5,000 simultaneous connections. Read the ADR for the JDK 24 qualification.

**Exit criteria:** complete the missions, run `./mvnw clean test` and—on a Docker-enabled machine—`./mvnw clean verify`; record reproducible benchmark outcomes and failures; review the code and explain trade-offs without presenting a local synthetic measurement as a production result.
