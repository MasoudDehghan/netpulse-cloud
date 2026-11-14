package io.netpulse.cloud.api.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.netpulse.cloud.api.generated.model.DeviceStatusDto;
import io.netpulse.cloud.api.generated.model.DeviceVendorDto;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * DeviceResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-11-14T16:05:07.168542400+03:30[Asia/Tehran]", comments = "Generator version: 7.11.0")
public class DeviceResponse {

  private UUID id;

  private String hostname;

  private String managementIp;

  private DeviceVendorDto vendor;

  private String region;

  private String firmwareVersion;

  private DeviceStatusDto status;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime lastSeenAt = null;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime updatedAt;

  public DeviceResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public DeviceResponse(UUID id, String hostname, String managementIp, DeviceVendorDto vendor, String region, String firmwareVersion, DeviceStatusDto status, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
    this.id = id;
    this.hostname = hostname;
    this.managementIp = managementIp;
    this.vendor = vendor;
    this.region = region;
    this.firmwareVersion = firmwareVersion;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public DeviceResponse id(UUID id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  @NotNull @Valid 
  @JsonProperty("id")
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public DeviceResponse hostname(String hostname) {
    this.hostname = hostname;
    return this;
  }

  /**
   * Get hostname
   * @return hostname
   */
  @NotNull 
  @JsonProperty("hostname")
  public String getHostname() {
    return hostname;
  }

  public void setHostname(String hostname) {
    this.hostname = hostname;
  }

  public DeviceResponse managementIp(String managementIp) {
    this.managementIp = managementIp;
    return this;
  }

  /**
   * Get managementIp
   * @return managementIp
   */
  @NotNull 
  @JsonProperty("managementIp")
  public String getManagementIp() {
    return managementIp;
  }

  public void setManagementIp(String managementIp) {
    this.managementIp = managementIp;
  }

  public DeviceResponse vendor(DeviceVendorDto vendor) {
    this.vendor = vendor;
    return this;
  }

  /**
   * Get vendor
   * @return vendor
   */
  @NotNull @Valid 
  @JsonProperty("vendor")
  public DeviceVendorDto getVendor() {
    return vendor;
  }

  public void setVendor(DeviceVendorDto vendor) {
    this.vendor = vendor;
  }

  public DeviceResponse region(String region) {
    this.region = region;
    return this;
  }

  /**
   * Get region
   * @return region
   */
  @NotNull 
  @JsonProperty("region")
  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public DeviceResponse firmwareVersion(String firmwareVersion) {
    this.firmwareVersion = firmwareVersion;
    return this;
  }

  /**
   * Get firmwareVersion
   * @return firmwareVersion
   */
  @NotNull 
  @JsonProperty("firmwareVersion")
  public String getFirmwareVersion() {
    return firmwareVersion;
  }

  public void setFirmwareVersion(String firmwareVersion) {
    this.firmwareVersion = firmwareVersion;
  }

  public DeviceResponse status(DeviceStatusDto status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  @NotNull @Valid 
  @JsonProperty("status")
  public DeviceStatusDto getStatus() {
    return status;
  }

  public void setStatus(DeviceStatusDto status) {
    this.status = status;
  }

  public DeviceResponse lastSeenAt(OffsetDateTime lastSeenAt) {
    this.lastSeenAt = lastSeenAt;
    return this;
  }

  /**
   * Get lastSeenAt
   * @return lastSeenAt
   */
  @Valid 
  @JsonProperty("lastSeenAt")
  public OffsetDateTime getLastSeenAt() {
    return lastSeenAt;
  }

  public void setLastSeenAt(OffsetDateTime lastSeenAt) {
    this.lastSeenAt = lastSeenAt;
  }

  public DeviceResponse createdAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Get createdAt
   * @return createdAt
   */
  @NotNull @Valid 
  @JsonProperty("createdAt")
  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public DeviceResponse updatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  /**
   * Get updatedAt
   * @return updatedAt
   */
  @NotNull @Valid 
  @JsonProperty("updatedAt")
  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DeviceResponse deviceResponse = (DeviceResponse) o;
    return Objects.equals(this.id, deviceResponse.id) &&
        Objects.equals(this.hostname, deviceResponse.hostname) &&
        Objects.equals(this.managementIp, deviceResponse.managementIp) &&
        Objects.equals(this.vendor, deviceResponse.vendor) &&
        Objects.equals(this.region, deviceResponse.region) &&
        Objects.equals(this.firmwareVersion, deviceResponse.firmwareVersion) &&
        Objects.equals(this.status, deviceResponse.status) &&
        Objects.equals(this.lastSeenAt, deviceResponse.lastSeenAt) &&
        Objects.equals(this.createdAt, deviceResponse.createdAt) &&
        Objects.equals(this.updatedAt, deviceResponse.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, hostname, managementIp, vendor, region, firmwareVersion, status, lastSeenAt, createdAt, updatedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DeviceResponse {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    hostname: ").append(toIndentedString(hostname)).append("\n");
    sb.append("    managementIp: ").append(toIndentedString(managementIp)).append("\n");
    sb.append("    vendor: ").append(toIndentedString(vendor)).append("\n");
    sb.append("    region: ").append(toIndentedString(region)).append("\n");
    sb.append("    firmwareVersion: ").append(toIndentedString(firmwareVersion)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    lastSeenAt: ").append(toIndentedString(lastSeenAt)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

