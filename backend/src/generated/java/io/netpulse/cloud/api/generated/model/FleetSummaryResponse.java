package io.netpulse.cloud.api.generated.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * FleetSummaryResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-11-17T11:44:21.816905300+03:30[Asia/Tehran]", comments = "Generator version: 7.11.0")
public class FleetSummaryResponse {

  private Long totalDevices;

  private Long healthyCount;

  private Long degradedCount;

  private Long unreachableCount;

  private Long maintenanceCount;

  public FleetSummaryResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public FleetSummaryResponse(Long totalDevices, Long healthyCount, Long degradedCount, Long unreachableCount, Long maintenanceCount) {
    this.totalDevices = totalDevices;
    this.healthyCount = healthyCount;
    this.degradedCount = degradedCount;
    this.unreachableCount = unreachableCount;
    this.maintenanceCount = maintenanceCount;
  }

  public FleetSummaryResponse totalDevices(Long totalDevices) {
    this.totalDevices = totalDevices;
    return this;
  }

  /**
   * Get totalDevices
   * @return totalDevices
   */
  @NotNull 
  @JsonProperty("totalDevices")
  public Long getTotalDevices() {
    return totalDevices;
  }

  public void setTotalDevices(Long totalDevices) {
    this.totalDevices = totalDevices;
  }

  public FleetSummaryResponse healthyCount(Long healthyCount) {
    this.healthyCount = healthyCount;
    return this;
  }

  /**
   * Get healthyCount
   * @return healthyCount
   */
  @NotNull 
  @JsonProperty("healthyCount")
  public Long getHealthyCount() {
    return healthyCount;
  }

  public void setHealthyCount(Long healthyCount) {
    this.healthyCount = healthyCount;
  }

  public FleetSummaryResponse degradedCount(Long degradedCount) {
    this.degradedCount = degradedCount;
    return this;
  }

  /**
   * Get degradedCount
   * @return degradedCount
   */
  @NotNull 
  @JsonProperty("degradedCount")
  public Long getDegradedCount() {
    return degradedCount;
  }

  public void setDegradedCount(Long degradedCount) {
    this.degradedCount = degradedCount;
  }

  public FleetSummaryResponse unreachableCount(Long unreachableCount) {
    this.unreachableCount = unreachableCount;
    return this;
  }

  /**
   * Get unreachableCount
   * @return unreachableCount
   */
  @NotNull 
  @JsonProperty("unreachableCount")
  public Long getUnreachableCount() {
    return unreachableCount;
  }

  public void setUnreachableCount(Long unreachableCount) {
    this.unreachableCount = unreachableCount;
  }

  public FleetSummaryResponse maintenanceCount(Long maintenanceCount) {
    this.maintenanceCount = maintenanceCount;
    return this;
  }

  /**
   * Get maintenanceCount
   * @return maintenanceCount
   */
  @NotNull 
  @JsonProperty("maintenanceCount")
  public Long getMaintenanceCount() {
    return maintenanceCount;
  }

  public void setMaintenanceCount(Long maintenanceCount) {
    this.maintenanceCount = maintenanceCount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FleetSummaryResponse fleetSummaryResponse = (FleetSummaryResponse) o;
    return Objects.equals(this.totalDevices, fleetSummaryResponse.totalDevices) &&
        Objects.equals(this.healthyCount, fleetSummaryResponse.healthyCount) &&
        Objects.equals(this.degradedCount, fleetSummaryResponse.degradedCount) &&
        Objects.equals(this.unreachableCount, fleetSummaryResponse.unreachableCount) &&
        Objects.equals(this.maintenanceCount, fleetSummaryResponse.maintenanceCount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(totalDevices, healthyCount, degradedCount, unreachableCount, maintenanceCount);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FleetSummaryResponse {\n");
    sb.append("    totalDevices: ").append(toIndentedString(totalDevices)).append("\n");
    sb.append("    healthyCount: ").append(toIndentedString(healthyCount)).append("\n");
    sb.append("    degradedCount: ").append(toIndentedString(degradedCount)).append("\n");
    sb.append("    unreachableCount: ").append(toIndentedString(unreachableCount)).append("\n");
    sb.append("    maintenanceCount: ").append(toIndentedString(maintenanceCount)).append("\n");
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

