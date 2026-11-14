package io.netpulse.cloud.api;

import io.netpulse.cloud.api.controller.DeviceController;
import io.netpulse.cloud.api.error.GlobalExceptionHandler;
import io.netpulse.cloud.api.generated.model.DeviceStatusDto;
import io.netpulse.cloud.api.generated.model.UpdateDeviceStatusRequest;
import io.netpulse.cloud.api.mapper.DeviceApiMapper;
import io.netpulse.cloud.application.service.DeviceInventoryService;
import io.netpulse.cloud.domain.exception.DeviceAlreadyExistsException;
import io.netpulse.cloud.domain.exception.DeviceNotFoundException;
import io.netpulse.cloud.domain.model.Device;
import io.netpulse.cloud.domain.model.DeviceStatus;
import io.netpulse.cloud.domain.model.DeviceVendor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DeviceController.class)
@Import({DeviceApiMapper.class, GlobalExceptionHandler.class})
class DeviceControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DeviceInventoryService inventoryService;

    @Test
    @DisplayName("POST /api/v1/devices returns 201 Created with Location header matching OpenAPI contract")
    void shouldReturn201OnValidRegistration() throws Exception {
        UUID id = UUID.fromString("11111111-2222-3333-4444-555555555555");
        OffsetDateTime now = OffsetDateTime.parse("2026-11-08T12:00:00Z");
        Device saved = new Device(
                id,
                "core-rtr-ams-01.nl",
                "10.44.12.10",
                DeviceVendor.CISCO_IOS_XE,
                "NL-North-Holland",
                "17.12.03",
                DeviceStatus.PROVISIONING,
                null,
                now,
                now,
                0L
        );

        when(inventoryService.registerDevice(any(), any(), any(), any(), any())).thenReturn(saved);

        mockMvc.perform(post("/api/v1/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "hostname": "core-rtr-ams-01.nl",
                                  "managementIp": "10.44.12.10",
                                  "vendor": "CISCO_IOS_XE",
                                  "region": "NL-North-Holland",
                                  "firmwareVersion": "17.12.03"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/devices/" + id))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.status").value("PROVISIONING"))
                .andExpect(jsonPath("$.vendor").value("CISCO_IOS_XE"));
    }

    @Test
    @DisplayName("POST /api/v1/devices returns RFC 7807 409 Conflict ProblemDetail on duplicate hostname")
    void shouldReturn409ProblemDetailOnConflict() throws Exception {
        when(inventoryService.registerDevice(any(), any(), any(), any(), any()))
                .thenThrow(new DeviceAlreadyExistsException("hostname", "core-rtr-ams-01.nl"));

        mockMvc.perform(post("/api/v1/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "hostname": "core-rtr-ams-01.nl",
                                  "managementIp": "10.44.12.10",
                                  "vendor": "CISCO_IOS_XE",
                                  "region": "NL-North-Holland",
                                  "firmwareVersion": "17.12.03"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Duplicate Network Device"))
                .andExpect(jsonPath("$.errorCode").value("DEVICE_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.conflictField").value("hostname"));
    }

    @Test
    @DisplayName("GET /api/v1/devices/{id} returns RFC 7807 404 ProblemDetail when device does not exist")
    void shouldReturn404ProblemDetailWhenMissing() throws Exception {
        UUID missing = UUID.fromString("99999999-9999-9999-9999-999999999999");
        when(inventoryService.getDeviceById(missing)).thenThrow(new DeviceNotFoundException(missing));

        mockMvc.perform(get("/api/v1/devices/{id}", missing))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("DEVICE_NOT_FOUND"));
    }

    @Test
    @DisplayName("PATCH /api/v1/devices/{id}/status returns 200 Updated")
    void updateDeviceStatusOnUpdate() throws Exception {
        UUID id = UUID.fromString("11111111-2222-3333-4444-555555555555");
        OffsetDateTime now = OffsetDateTime.parse("2026-11-08T12:00:00Z");
        Device updated = new Device(
                id,
                "core-rtr-ams-01.nl",
                "10.44.12.10",
                DeviceVendor.CISCO_IOS_XE,
                "NL-North-Holland",
                "17.12.03",
                DeviceStatus.MAINTENANCE,
                null,
                now,
                now,
                1L
        );

        when(inventoryService.updateDeviceStatueById(
                eq(id),
                argThat(req -> req.getStatus() == DeviceStatusDto.MAINTENANCE)))
                .thenReturn(updated);

        mockMvc.perform(patch("/api/v1/devices/{id}/status", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            { "status": "MAINTENANCE" }
                            """))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.status").value("MAINTENANCE"))
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());
    }
}
