package com.trafficlight.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * HAFTA 3 - DTO Class
 * Request DTO for creating metrics
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetricRequest {

    @NotNull(message = "Intersection ID is required")
    private Long intersectionId;

    @NotNull(message = "Measurement date is required")
    private LocalDate measurementDate;

    @Min(value = 0, message = "Measurement hour must be between 0 and 23")
    @Max(value = 23, message = "Measurement hour must be between 0 and 23")
    private Integer measurementHour;

    // Traffic Volume
    @Min(value = 0, message = "Total vehicle count cannot be negative")
    private Integer totalVehicleCount;

    @Min(value = 0)
    private Integer carCount;

    @Min(value = 0)
    private Integer truckCount;

    @Min(value = 0)
    private Integer busCount;

    @Min(value = 0)
    private Integer motorcycleCount;

    @Min(value = 0)
    private Integer bicycleCount;

    @Min(value = 0)
    private Integer pedestrianCount;

    // Performance Metrics
    @DecimalMin(value = "0.0")
    private BigDecimal averageWaitTime;

    @DecimalMin(value = "0.0")
    private BigDecimal maximumWaitTime;

    @DecimalMin(value = "0.0")
    private BigDecimal averageQueueLength;

    @Min(value = 0)
    private Integer maximumQueueLength;

    @Min(value = 0)
    private Integer throughput;

    // Efficiency Metrics
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private BigDecimal greenTimeUtilization;

    @Min(value = 0)
    private Integer redLightViolations;

    @Min(value = 0)
    private Integer yellowLightViolations;

    @Min(value = 0)
    private Integer pedestrianViolations;

    // Incident Metrics
    @Min(value = 0)
    private Integer accidentsCount;

    @Min(value = 0)
    private Integer nearMissCount;

    @Min(value = 0)
    private Integer emergencyVehiclePassages;

    // System Performance
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private BigDecimal systemUptimePercentage;

    @Min(value = 0)
    private Integer malfunctionCount;

    @Min(value = 0)
    private Integer manualOverrideCount;

    // Environmental
    @DecimalMin(value = "0.0")
    private BigDecimal estimatedCo2Emission;

    @DecimalMin(value = "0.0")
    private BigDecimal estimatedFuelConsumption;

    // Metadata
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "1.0")
    private BigDecimal dataQualityScore;

    private String notes;
}

