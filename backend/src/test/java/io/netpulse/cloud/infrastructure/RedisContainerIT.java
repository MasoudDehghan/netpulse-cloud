package io.netpulse.cloud.infrastructure;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/** Infrastructure smoke test only: no application Redis integration exists yet. */
@Testcontainers
class RedisContainerIT {
    @Container
    static final GenericContainer<?> redis = new GenericContainer<>("redis:7.4-alpine").withExposedPorts(6379);

    @Test
    void respondsToPingAndRoundTripsAValue() throws Exception {
        try (Socket socket = new Socket(redis.getHost(), redis.getMappedPort(6379))) {
            socket.setSoTimeout(5000);
            OutputStream out = socket.getOutputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            out.write("*1\r\n$4\r\nPING\r\n".getBytes(StandardCharsets.UTF_8));
            out.flush();
            assertThat(in.readLine()).isEqualTo("+PONG");
            out.write("*3\r\n$3\r\nSET\r\n$6\r\nrouter\r\n$7\r\nhealthy\r\n".getBytes(StandardCharsets.UTF_8));
            out.flush();
            assertThat(in.readLine()).isEqualTo("+OK");
            out.write("*2\r\n$3\r\nGET\r\n$6\r\nrouter\r\n".getBytes(StandardCharsets.UTF_8));
            out.flush();
            assertThat(in.readLine()).isEqualTo("$7");
            assertThat(in.readLine()).isEqualTo("healthy");
        }
    }
}
