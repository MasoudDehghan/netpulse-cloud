package io.netpulse.cloud.benchmark;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.assertj.core.api.Assertions.assertThat;

class LoopbackBenchmarkTest {
    @Test
    void allClientsCompleteTheSameSmallLoopbackWorkload() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 20);
        server.createContext("/sample", exchange -> {
            byte[] bytes = new byte[] { 'o', 'k' };
            exchange.sendResponseHeaders(200, bytes.length);
            try (var output = exchange.getResponseBody()) { output.write(bytes); }
            finally { exchange.close(); }
        });
        server.start();
        try {
            URI uri = URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/sample");
            for (var mode : LoopbackBenchmark.Mode.values()) {
                var result = LoopbackBenchmark.run(mode, uri, 8, 4, 2);
                assertThat(result.successes()).isEqualTo(8);
                assertThat(result.failures()).isZero();
            }
        } finally { server.stop(0); }
    }

}
