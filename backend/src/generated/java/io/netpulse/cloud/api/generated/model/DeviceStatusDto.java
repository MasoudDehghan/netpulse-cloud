package io.netpulse.cloud.api.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets DeviceStatusDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-11-15T14:48:07.460863600+03:30[Asia/Tehran]", comments = "Generator version: 7.11.0")
public enum DeviceStatusDto {
  
  PROVISIONING("PROVISIONING"),
  
  HEALTHY("HEALTHY"),
  
  DEGRADED("DEGRADED"),
  
  UNREACHABLE("UNREACHABLE"),
  
  MAINTENANCE("MAINTENANCE");

  private String value;

  DeviceStatusDto(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static DeviceStatusDto fromValue(String value) {
    for (DeviceStatusDto b : DeviceStatusDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

