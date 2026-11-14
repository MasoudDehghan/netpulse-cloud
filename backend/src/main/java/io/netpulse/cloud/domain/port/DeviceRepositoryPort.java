package io.netpulse.cloud.domain.port;

import io.netpulse.cloud.domain.model.Device;
import io.netpulse.cloud.domain.model.DeviceFilter;
import io.netpulse.cloud.domain.model.FleetSummary;
import io.netpulse.cloud.domain.model.PageResult;

import java.util.Optional;
import java.util.UUID;

public interface DeviceRepositoryPort {

    boolean existsByHostname(String hostname);

    boolean existsByManagementIp(String managementIp);

    Device save(Device device);

    Optional<Device> findById(UUID id);

    PageResult<Device> findByFilter(DeviceFilter filter);

    FleetSummary computeFleetSummary();
}
