package io.netpulse.cloud.application;

import io.netpulse.cloud.application.service.DeviceInventoryService;
import io.netpulse.cloud.domain.exception.DeviceAlreadyExistsException;
import io.netpulse.cloud.domain.exception.DeviceNotFoundException;
import io.netpulse.cloud.domain.model.Device;
import io.netpulse.cloud.domain.model.DeviceStatus;
import io.netpulse.cloud.domain.model.DeviceVendor;
import io.netpulse.cloud.domain.port.DeviceRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeviceInventoryServiceTest {

    @Mock
    private DeviceRepositoryPort repositoryPort;

    private DeviceInventoryService service;

    @BeforeEach
    void setUp() {
        service = new DeviceInventoryService(repositoryPort);
    }

    @Test
    @DisplayName("registerDevice normalizes hostname and persists new PROVISIONING device")
    void shouldRegisterNewDevice() {
        when(repositoryPort.existsByHostname("core-ams-01.nl.internal")).thenReturn(false);
        when(repositoryPort.existsByManagementIp("10.44.12.10")).thenReturn(false);
        when(repositoryPort.save(any(Device.class))).thenAnswer(inv -> inv.getArgument(0));

        Device result = service.registerDevice(
                "  CORE-AMS-01.NL.INTERNAL  ",
                " 10.44.12.10 ",
                DeviceVendor.CISCO_IOS_XE,
                "NL-North-Holland",
                "17.12.03"
        );

        assertThat(result.hostname()).isEqualTo("core-ams-01.nl.internal");
        assertThat(result.managementIp()).isEqualTo("10.44.12.10");
        assertThat(result.status()).isEqualTo(DeviceStatus.PROVISIONING);
        verify(repositoryPort).save(any(Device.class));
    }

    @Test
    @DisplayName("registerDevice rejects duplicate hostname before hitting save")
    void shouldRejectDuplicateHostname() {
        when(repositoryPort.existsByHostname("edge-utr-02.nl.internal")).thenReturn(true);

        assertThatThrownBy(() -> service.registerDevice(
                "edge-utr-02.nl.internal",
                "10.44.20.2",
                DeviceVendor.JUNIPER_JUNOS,
                "NL-Utrecht",
                "23.4R1"
        ))
                .isInstanceOf(DeviceAlreadyExistsException.class)
                .hasMessageContaining("hostname");

        verify(repositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("getDeviceById throws DeviceNotFoundException when UUID is absent")
    void shouldThrowWhenDeviceMissing() {
        UUID missingId = UUID.randomUUID();
        when(repositoryPort.findById(missingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getDeviceById(missingId))
                .isInstanceOf(DeviceNotFoundException.class);
    }
}
