package io.netpulse.cloud.infrastructure;

import io.netpulse.cloud.domain.model.Device;
import io.netpulse.cloud.domain.model.DeviceFilter;
import io.netpulse.cloud.domain.model.DeviceStatus;
import io.netpulse.cloud.domain.model.DeviceVendor;
import io.netpulse.cloud.domain.model.FleetSummary;
import io.netpulse.cloud.domain.model.PageResult;
import io.netpulse.cloud.infrastructure.persistence.adapter.JpaDeviceRepositoryAdapter;
import org.flywaydb.core.Flyway;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.TransactionDefinition;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.jdbc.core.JdbcTemplate;
import io.netpulse.cloud.infrastructure.persistence.repository.SpringDataDeviceRepository;
import io.netpulse.cloud.infrastructure.persistence.entity.DeviceJpaEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.junit.jupiter.api.BeforeEach;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@ActiveProfiles("test")
@ImportAutoConfiguration(FlywayAutoConfiguration.class)
@Import(JpaDeviceRepositoryAdapter.class)
class PostgresPersistenceIT {
    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired SpringDataDeviceRepository repository;
    @Autowired JdbcTemplate jdbc;
    @Autowired PlatformTransactionManager transactionManager;

    @BeforeEach
    void cleanDevices() {
        jdbc.update("DELETE FROM telemetry_snapshots");
        jdbc.update("DELETE FROM devices");
    }

    @Autowired
    private Flyway flyway;

    @Autowired
    private JpaDeviceRepositoryAdapter adapter;

    @Test
    @DisplayName("Flyway applies V1 & V2 SQL migrations and Hibernate ddl-auto=validate succeeds")
    void shouldApplyFlywayMigrationsAndPersistDevices() {
        assertThat(flyway.info().applied()).extracting(info -> info.getVersion().getVersion())
                .contains("1", "2", "3");
        assertThat(jdbc.queryForObject("select current_setting('server_version')", String.class)).startsWith("16.");

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        Device amsRouter = new Device(
                UUID.randomUUID(),
                "core-rtr-ams-01.nl",
                "10.10.1.1",
                DeviceVendor.CISCO_IOS_XE,
                "NL-North-Holland",
                "17.12.03",
                DeviceStatus.HEALTHY,
                now,
                now,
                now,
                0L
        );
        Device einRouter = new Device(
                UUID.randomUUID(),
                "dist-sw-ein-01.nl",
                "10.20.1.1",
                DeviceVendor.JUNIPER_JUNOS,
                "NL-North-Brabant",
                "23.4R1",
                DeviceStatus.DEGRADED,
                now,
                now,
                now,
                0L
        );

        adapter.save(amsRouter);
        adapter.save(einRouter);

        PageResult<Device> ciscoOnly = adapter.findByFilter(
                new DeviceFilter(DeviceVendor.CISCO_IOS_XE, null, "nl-north-holland", 0, 10)
        );
        assertThat(ciscoOnly.totalElements()).isEqualTo(1);
        assertThat(ciscoOnly.items().getFirst().hostname()).isEqualTo("core-rtr-ams-01.nl");

        FleetSummary summary = adapter.computeFleetSummary();
        assertThat(summary.totalDevices()).isEqualTo(2);
        assertThat(summary.healthyCount()).isEqualTo(1);
        assertThat(summary.degradedCount()).isEqualTo(1);
    }

    @Test
    void rollsBackChangesWhenTransactionFails() {
        UUID id = UUID.randomUUID();
        TransactionTemplate tx = new TransactionTemplate(transactionManager);
        assertThatThrownBy(() -> tx.executeWithoutResult(status -> {
            repository.saveAndFlush(entity(id, "rollback-router", "10.0.0.10", 0));
            throw new IllegalStateException("simulate failure");
        })).isInstanceOf(IllegalStateException.class);
        assertThat(repository.findById(id)).isEmpty();
    }

    @Test
    void rejectsStaleVersionAndPreservesFirstCommittedUpdate() {
        UUID id = UUID.randomUUID();
        repository.saveAndFlush(entity(id, "original-router", "10.0.0.11", 0));
        long initialVersion = repository.findById(id).orElseThrow().getVersion();
        repository.saveAndFlush(entity(id, "winner-router", "10.0.0.11", initialVersion));
        assertThat(repository.findById(id).orElseThrow().getVersion()).isEqualTo(initialVersion + 1);
        assertThatThrownBy(() -> repository.saveAndFlush(entity(id, "stale-router", "10.0.0.11", initialVersion)))
                .isInstanceOf(ObjectOptimisticLockingFailureException.class);
        assertThat(repository.findById(id).orElseThrow().getHostname()).isEqualTo("winner-router");

        long nextVersion = repository.findById(id).orElseThrow().getVersion();
        repository.saveAndFlush(entity(id, "winner-router", "10.0.0.12", nextVersion));
        assertThat(repository.findById(id).orElseThrow().getVersion()).isEqualTo(nextVersion + 1);
        assertThatThrownBy(() -> repository.saveAndFlush(entity(id, "winner-router", "10.0.0.13", nextVersion)))
                .isInstanceOf(ObjectOptimisticLockingFailureException.class);
        assertThat(repository.findById(id).orElseThrow().getManagementIp()).isEqualTo("10.0.0.12");
    }

    @Test
    void registerNewRouterAndConstraintCheck(){
        UUID id = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        Device iDirectRouter = new Device(
                id,
                "test.nl",
                "10.90.1.1",
                DeviceVendor.IDIRECT_SAT,
                "NL-North-Brabant",
                "1.0",
                DeviceStatus.HEALTHY,
                now,
                now,
                now,
                0L
        );

        adapter.save(iDirectRouter);

        assertThat(repository.findById(id)).isPresent();

        UUID nextID = UUID.randomUUID();

        TransactionTemplate tx = new TransactionTemplate(transactionManager);
        assertThatThrownBy(() -> tx.executeWithoutResult(status -> {
            repository.saveAndFlush(entity(nextID, "test.nl", "10.100.1.1", 0));
            throw new DataIntegrityViolationException("Invalid Data");
        })).isInstanceOf(DataIntegrityViolationException.class);
        assertThat(repository.findById(nextID)).isEmpty();

        TransactionTemplate verifyTx = new TransactionTemplate(transactionManager);
        verifyTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        verifyTx.setReadOnly(true);
        verifyTx.executeWithoutResult(status -> {
            assertThat(repository.count()).isEqualTo(1);
            assertThat(repository.findById(nextID)).isEmpty();
            assertThat(repository.findById(id).orElseThrow().getHostname())
                    .isEqualTo("test.nl");
        });
    }

    private static DeviceJpaEntity entity(UUID id, String hostname, String ip, long version) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        return new DeviceJpaEntity(id, hostname, ip, DeviceVendor.CISCO_IOS_XE, "NL",
                "17.1", DeviceStatus.HEALTHY, now, now, now, version);
    }
}
