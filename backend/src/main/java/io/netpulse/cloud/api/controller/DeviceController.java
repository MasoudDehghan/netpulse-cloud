package io.netpulse.cloud.api.controller;

import io.netpulse.cloud.api.generated.DevicesApi;
import io.netpulse.cloud.api.generated.model.*;
import io.netpulse.cloud.api.mapper.DeviceApiMapper;
import io.netpulse.cloud.application.service.DeviceInventoryService;
import io.netpulse.cloud.domain.model.Device;
import io.netpulse.cloud.domain.model.DeviceFilter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class DeviceController implements DevicesApi {

    private final DeviceInventoryService inventoryService;
    private final DeviceApiMapper mapper;

    public DeviceController(DeviceInventoryService inventoryService, DeviceApiMapper mapper) {
        this.inventoryService = inventoryService;
        this.mapper = mapper;
    }

    @Override
    public ResponseEntity<DeviceResponse> registerDevice(RegisterDeviceRequest request) {
        Device created = inventoryService.registerDevice(
                request.getHostname(),
                request.getManagementIp(),
                mapper.toDomainVendor(request.getVendor()),
                request.getRegion(),
                request.getFirmwareVersion()
        );
        URI location = URI.create("/api/v1/devices/" + created.id());
        return ResponseEntity.created(location).body(mapper.toResponse(created));
    }

    @Override
    public ResponseEntity<DeviceResponse> getDeviceById(UUID deviceId) {
        Device device = inventoryService.getDeviceById(deviceId);
        return ResponseEntity.ok(mapper.toResponse(device));
    }

    @Override
    public ResponseEntity<DevicePageResponse> listDevices(
            DeviceVendorDto vendor,
            DeviceStatusDto status,
            String region,
            Integer page,
            Integer size
    ) {
        DeviceFilter filter = new DeviceFilter(
                mapper.toDomainVendor(vendor),
                mapper.toDomainStatus(status),
                region,
                page != null ? page : 0,
                size != null ? size : 20
        );
        return ResponseEntity.ok(mapper.toPageResponse(inventoryService.listDevices(filter)));
    }

    @Override
        public ResponseEntity<FleetSummaryResponse> getFleetSummary() {
        return ResponseEntity.ok(mapper.toFleetSummaryResponse(inventoryService.getFleetSummary()));
    }

    @Override
    public ResponseEntity<DeviceResponse> updateDeviceStatus(UUID deviceId, UpdateDeviceStatusRequest updateDeviceStatusRequest) {
        Device device = inventoryService.updateDeviceStatueById(deviceId, updateDeviceStatusRequest);
        return ResponseEntity.ok(mapper.toResponse(device));
    }
}
