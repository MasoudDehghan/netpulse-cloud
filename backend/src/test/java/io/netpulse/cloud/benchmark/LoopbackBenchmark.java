package io.netpulse.cloud.benchmark;

import com.sun.net.httpserver.HttpServer;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;

/** Finite loopback experiment, not a production capacity test or a JMH benchmark. */
public final class LoopbackBenchmark {
    private LoopbackBenchmark() { }
    enum Mode { FIXED, VIRTUAL, WEBCLIENT }
    record Result(Mode mode, int requests, long successes, long failures, double seconds, double p95Ms) { }

    public static void main(String[] args) throws Exception {
        int requests = Integer.getInteger("bench.requests", 5_000);
        int concurrency = Integer.getInteger("bench.concurrency", 200);
        int workers = Integer.getInteger("bench.workers", 32);
        int delayMs = Integer.getInteger("bench.delayMs", 20);
        if (requests < 1 || requests > 100_000 || concurrency < 1 || concurrency > 5_000
                || workers < 1 || workers > concurrency || delayMs < 0 || delayMs > 10_000) {
            throw new IllegalArgumentException("requests 1..100000, concurrency 1..5000, workers 1..concurrency, delay 0..10000 ms");
        }
        var serverExecutor = Executors.newVirtualThreadPerTaskExecutor();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 512);
        server.setExecutor(serverExecutor);
        server.createContext("/sample", exchange -> {
            try {
                Thread.sleep(delayMs);
                byte[] body = "ok".getBytes(java.nio.charset.StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, body.length);
                try (var output = exchange.getResponseBody()) { output.write(body); }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally { exchange.close(); }
        });
        server.start();
        URI uri = URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/sample");
        System.out.printf("Loopback HTTP: requests=%d concurrencyCap=%d fixedWorkers=%d artificialDelayMs=%d%n",
                requests, concurrency, workers, delayMs);
        try {
            // Small warm-up for each implementation. Not statistically robust.
            for (Mode mode : Mode.values()) run(mode, uri, Math.min(20, requests), concurrency, workers);
            boolean failed = false;
            for (Mode mode : Mode.values()) {
                Result r = run(mode, uri, requests, concurrency, workers);
                System.out.printf("%-9s success=%d failure=%d elapsed=%.3fs throughput=%.1f/s active-p95=%.1fms%n",
                        r.mode(), r.successes(), r.failures(), r.seconds(), r.successes() / r.seconds(), r.p95Ms());
                failed |= r.failures() != 0 || r.successes() != requests;
            }
            if (failed) throw new IllegalStateException("One or more requests failed; results are not comparable");
        } finally {
            server.stop(0);
            serverExecutor.shutdownNow();
        }
    }

    static Result run(Mode mode, URI uri, int requests, int concurrency, int workers) throws Exception {
        var timings = new ConcurrentLinkedQueue<Long>();
        var failures = new LongAdder();

        long start = System.nanoTime();
        if (mode == Mode.WEBCLIENT) {
            ConnectionProvider pool = ConnectionProvider.builder("bench-" + System.nanoTime())
                    .maxConnections(concurrency).pendingAcquireMaxCount(concurrency * 2)
                    .lifo().maxIdleTime(Duration.ofSeconds(1))
                    .pendingAcquireTimeout(Duration.ofSeconds(30)).build();
            try {
                WebClient client = WebClient.builder().clientConnector(
                        new ReactorClientHttpConnector(HttpClient.create(pool).disableRetry(true))).build();
                Flux.range(0, requests).flatMap(i -> Mono.defer(() -> {
                    long begun = System.nanoTime();
                    return client.get().uri(uri).retrieve().bodyToMono(String.class)
                            .switchIfEmpty(Mono.error(new IllegalStateException("empty body")))
                            .timeout(Duration.ofSeconds(30))
                            .doOnNext(body -> {
                                if (!"ok".equals(body)) throw new IllegalStateException("unexpected body: " + body);
                                timings.add(System.nanoTime() - begun);
                            })
                            .onErrorResume(e -> { failures.increment(); return Mono.empty(); });
                }), concurrency).blockLast(Duration.ofMinutes(5));
            } finally { pool.disposeLater().block(Duration.ofSeconds(10)); }
        } else {
            java.net.http.HttpClient client = java.net.http.HttpClient.newBuilder()
                    .version(java.net.http.HttpClient.Version.HTTP_1_1).connectTimeout(Duration.ofSeconds(10)).build();
            HttpRequest request = HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(30)).GET().build();
            Semaphore permits = new Semaphore(concurrency);
            ExecutorService executor = mode == Mode.FIXED ? Executors.newFixedThreadPool(workers)
                    : Executors.newVirtualThreadPerTaskExecutor();
            try {

                var futures = IntStream.range(0, requests).mapToObj(i -> executor.submit(() -> {
                    boolean acquired = false;
                    try {
                        permits.acquire();
                        acquired = true;
                        long begun = System.nanoTime();
                        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
                        if (response.statusCode() != 200 || !"ok".equals(response.body()))
                            throw new IllegalStateException("HTTP " + response.statusCode());
                        timings.add(System.nanoTime() - begun);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        failures.increment();
                    } catch (Exception e) { failures.increment(); }
                    finally { if (acquired) permits.release(); }
                })).toList();
                for (Future<?> future : futures) future.get();
            } finally {
                executor.shutdownNow();
                client.close();
            }
        }
        double seconds = (System.nanoTime() - start) / 1_000_000_000.0;
        long[] sorted = timings.stream().mapToLong(Long::longValue).sorted().toArray();
        double p95 = sorted.length == 0 ? Double.NaN : sorted[(int) Math.ceil(sorted.length * .95) - 1] / 1_000_000.0;
        return new Result(mode, requests, sorted.length, failures.sum(), seconds, p95);
    }
}
