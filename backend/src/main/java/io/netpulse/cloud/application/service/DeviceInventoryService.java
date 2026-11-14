package io.netpulse.cloud.application.service;

import io.netpulse.cloud.api.generated.model.DeviceStatusDto;
import io.netpulse.cloud.api.generated.model.UpdateDeviceStatusRequest;
import io.netpulse.cloud.domain.exception.DeviceAlreadyExistsException;
import io.netpulse.cloud.domain.exception.DeviceNotFoundException;
import io.netpulse.cloud.domain.model.*;
import io.netpulse.cloud.domain.port.DeviceRepositoryPort;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class DeviceInventoryService {

    private final DeviceRepositoryPort deviceRepository;

    public DeviceInventoryService(DeviceRepositoryPort deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Transactional
    public Device registerDevice(
            String hostname,
            String managementIp,
            DeviceVendor vendor,
            String region,
            String firmwareVersion
    ) {
        String normalizedHost = hostname.trim().toLowerCase();
        String normalizedIp = managementIp.trim();

        if (deviceRepository.existsByHostname(normalizedHost)) {
            throw new DeviceAlreadyExistsException("hostname", normalizedHost);
        }
        if (deviceRepository.existsByManagementIp(normalizedIp)) {
            throw new DeviceAlreadyExistsException("managementIp", normalizedIp);
        }

        Device candidate = Device.createNew(
                normalizedHost,
                normalizedIp,
                vendor,
                region,
                firmwareVersion,
                OffsetDateTime.now()
        );
        return deviceRepository.save(candidate);
    }

    @Transactional(readOnly = true)
    public Device getDeviceById(UUID deviceId) {
        return deviceRepository.findById(deviceId)
                .orElseThrow(() -> new DeviceNotFoundException(deviceId));
    }

    @Transactional(readOnly = true)
    public PageResult<Device> listDevices(DeviceFilter filter) {
        return deviceRepository.findByFilter(filter);
    }

    @Transactional(readOnly = true)
    public FleetSummary getFleetSummary() {
        return deviceRepository.computeFleetSummary();
    }

    @Transactional
    public Device updateDeviceStatueById(UUID deviceId, UpdateDeviceStatusRequest statusRequest) throws DeviceNotFoundException{
        Device device = deviceRepository.findById(deviceId).orElseThrow(() -> new DeviceNotFoundException(deviceId));

        DeviceStatusDto statusDto = statusRequest.getStatus();
        device = Device.builder()
                .id(deviceId)
                .hostname(device.hostname())
                .managementIp(device.managementIp())
                .region(device.region())
                .vendor(device.vendor())
                .firmwareVersion(device.firmwareVersion())
                .createdAt(device.createdAt())
                .lastSeenAt(device.lastSeenAt())
                .version(device.version())
                .status(toModel(statusDto))
                .updatedAt(OffsetDateTime.now())
                .build();
        deviceRepository.save(device);
        return device;
    }
    public static DeviceStatus toModel(DeviceStatusDto statusDto) {
        if (statusDto == null) return null;
        return DeviceStatus.valueOf(statusDto.name());
    }

}
