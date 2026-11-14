package io.netpulse.cloud.infrastructure;

import io.netpulse.cloud.domain.model.Device;
import io.netpulse.cloud.domain.model.DeviceFilter;
import io.netpulse.cloud.domain.model.DeviceStatus;
import io.netpulse.cloud.domain.model.DeviceVendor;
import io.netpulse.cloud.domain.model.FleetSummary;
import io.netpulse.cloud.domain.model.PageResult;
import io.netpulse.cloud.infrastructure.persistence.adapter.JpaDeviceRepositoryAdapter;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@ImportAutoConfiguration(FlywayAutoConfiguration.class)
@Import(JpaDeviceRepositoryAdapter.class)
class FlywayMigrationAndJpaSliceTest {

    @Autowired
    private Flyway flyway;

    @Autowired
    private JpaDeviceRepositoryAdapter adapter;

    @Test
    @DisplayName("Flyway applies V1 & V2 & V3 SQL migrations and Hibernate ddl-auto=validate succeeds")
    void shouldApplyFlywayMigrationsAndPersistDevices() {
        assertThat(flyway.info().applied()).hasSize(3);

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
}
