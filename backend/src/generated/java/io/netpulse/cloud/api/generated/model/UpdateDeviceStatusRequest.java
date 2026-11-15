package io.netpulse.cloud.api.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.netpulse.cloud.api.generated.model.DeviceStatusDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UpdateDeviceStatusRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-11-15T14:48:07.460863600+03:30[Asia/Tehran]", comments = "Generator version: 7.11.0")
public class UpdateDeviceStatusRequest {

  private DeviceStatusDto status;

  public UpdateDeviceStatusRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UpdateDeviceStatusRequest(DeviceStatusDto status) {
    this.status = status;
  }

  public UpdateDeviceStatusRequest status(DeviceStatusDto status) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateDeviceStatusRequest updateDeviceStatusRequest = (UpdateDeviceStatusRequest) o;
    return Objects.equals(this.status, updateDeviceStatusRequest.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateDeviceStatusRequest {\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
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

