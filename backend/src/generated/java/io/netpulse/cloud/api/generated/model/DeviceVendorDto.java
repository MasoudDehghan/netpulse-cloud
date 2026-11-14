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
 * Gets or Sets DeviceVendorDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-11-14T16:05:07.168542400+03:30[Asia/Tehran]", comments = "Generator version: 7.11.0")
public enum DeviceVendorDto {
  
  CISCO_IOS_XE("CISCO_IOS_XE"),
  
  CISCO_NX_OS("CISCO_NX_OS"),
  
  JUNIPER_JUNOS("JUNIPER_JUNOS"),
  
  IDIRECT_SAT("IDIRECT_SAT"),
  
  HNS_SAT("HNS_SAT");

  private String value;

  DeviceVendorDto(String value) {
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
  public static DeviceVendorDto fromValue(String value) {
    for (DeviceVendorDto b : DeviceVendorDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

