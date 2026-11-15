package io.netpulse.cloud.api.mapper;

import io.netpulse.cloud.api.generated.model.DevicePageResponse;
import io.netpulse.cloud.api.generated.model.DeviceResponse;
import io.netpulse.cloud.api.generated.model.DeviceStatusDto;
import io.netpulse.cloud.api.generated.model.DeviceVendorDto;
import io.netpulse.cloud.api.generated.model.FleetSummaryResponse;
import io.netpulse.cloud.domain.model.Device;
import io.netpulse.cloud.domain.model.DeviceStatus;
import io.netpulse.cloud.domain.model.DeviceVendor;
import io.netpulse.cloud.domain.model.FleetSummary;
import io.netpulse.cloud.domain.model.PageResult;
import org.springframework.stereotype.Component;

@Component
public class DeviceApiMapper {

    public DeviceVendor toDomainVendor(DeviceVendorDto dto) {
        return dto == null ? null : DeviceVendor.valueOf(dto.name());
    }

    public DeviceStatus toDomainStatus(DeviceStatusDto dto) {
        return dto == null ? null : DeviceStatus.valueOf(dto.name());
    }



    public DeviceResponse toResponse(Device device) {
        DeviceResponse response = new DeviceResponse(
                device.id(),
                device.hostname(),
                device.managementIp(),
                DeviceVendorDto.fromValue(device.vendor().name()),
                device.region(),
                device.firmwareVersion(),
                DeviceStatusDto.fromValue(device.status().name()),
                device.createdAt(),
                device.updatedAt()
        );
        response.setLastSeenAt(device.lastSeenAt());
        return response;
    }

    public DevicePageResponse toPageResponse(PageResult<Device> page) {
        return new DevicePageResponse(
                page.items().stream().map(this::toResponse).toList(),
                page.page(),
                page.size(),
                page.totalElements(),
                page.totalPages()
        );
    }

    public FleetSummaryResponse toFleetSummaryResponse(FleetSummary summary) {
        return new FleetSummaryResponse(
                summary.totalDevices(),
                summary.healthyCount(),
                summary.degradedCount(),
                summary.unreachableCount(),
                summary.maintenanceCount()
        );
    }


}
