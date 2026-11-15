package io.netpulse.cloud.api.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.netpulse.cloud.api.generated.model.DeviceVendorDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RegisterDeviceRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-11-15T14:48:07.460863600+03:30[Asia/Tehran]", comments = "Generator version: 7.11.0")
public class RegisterDeviceRequest {

  private String hostname;

  private String managementIp;

  private DeviceVendorDto vendor;

  private String region;

  private String firmwareVersion;

  public RegisterDeviceRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public RegisterDeviceRequest(String hostname, String managementIp, DeviceVendorDto vendor, String region, String firmwareVersion) {
    this.hostname = hostname;
    this.managementIp = managementIp;
    this.vendor = vendor;
    this.region = region;
    this.firmwareVersion = firmwareVersion;
  }

  public RegisterDeviceRequest hostname(String hostname) {
    this.hostname = hostname;
    return this;
  }

  /**
   * Get hostname
   * @return hostname
   */
  @NotNull @Pattern(regexp = "^[a-zA-Z0-9.-]+$") @Size(min = 3, max = 128) 
  @JsonProperty("hostname")
  public String getHostname() {
    return hostname;
  }

  public void setHostname(String hostname) {
    this.hostname = hostname;
  }

  public RegisterDeviceRequest managementIp(String managementIp) {
    this.managementIp = managementIp;
    return this;
  }

  /**
   * Get managementIp
   * @return managementIp
   */
  @NotNull @Pattern(regexp = "^[0-9a-fA-F.:]+$") @Size(min = 7, max = 45) 
  @JsonProperty("managementIp")
  public String getManagementIp() {
    return managementIp;
  }

  public void setManagementIp(String managementIp) {
    this.managementIp = managementIp;
  }

  public RegisterDeviceRequest vendor(DeviceVendorDto vendor) {
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

  public RegisterDeviceRequest region(String region) {
    this.region = region;
    return this;
  }

  /**
   * Get region
   * @return region
   */
  @NotNull @Size(min = 2, max = 64) 
  @JsonProperty("region")
  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public RegisterDeviceRequest firmwareVersion(String firmwareVersion) {
    this.firmwareVersion = firmwareVersion;
    return this;
  }

  /**
   * Get firmwareVersion
   * @return firmwareVersion
   */
  @NotNull @Size(min = 1, max = 64) 
  @JsonProperty("firmwareVersion")
  public String getFirmwareVersion() {
    return firmwareVersion;
  }

  public void setFirmwareVersion(String firmwareVersion) {
    this.firmwareVersion = firmwareVersion;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RegisterDeviceRequest registerDeviceRequest = (RegisterDeviceRequest) o;
    return Objects.equals(this.hostname, registerDeviceRequest.hostname) &&
        Objects.equals(this.managementIp, registerDeviceRequest.managementIp) &&
        Objects.equals(this.vendor, registerDeviceRequest.vendor) &&
        Objects.equals(this.region, registerDeviceRequest.region) &&
        Objects.equals(this.firmwareVersion, registerDeviceRequest.firmwareVersion);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hostname, managementIp, vendor, region, firmwareVersion);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RegisterDeviceRequest {\n");
    sb.append("    hostname: ").append(toIndentedString(hostname)).append("\n");
    sb.append("    managementIp: ").append(toIndentedString(managementIp)).append("\n");
    sb.append("    vendor: ").append(toIndentedString(vendor)).append("\n");
    sb.append("    region: ").append(toIndentedString(region)).append("\n");
    sb.append("    firmwareVersion: ").append(toIndentedString(firmwareVersion)).append("\n");
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

