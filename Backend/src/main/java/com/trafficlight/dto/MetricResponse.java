package com.trafficlight.dto;

import com.trafficlight.entity.IntersectionMetric;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * HAFTA 3 - DTO Class
 * Response DTO for Metric data
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetricResponse {

    private Long id;
    private Long intersectionId;
    private String intersectionName;
    private String intersectionCode;
    private LocalDate measurementDate;
    private Integer measurementHour;
    
    // Traffic Volume
    private Integer totalVehicleCount;
    private Integer carCount;
    private Integer truckCount;
    private Integer busCount;
    private Integer motorcycleCount;
    private Integer bicycleCount;
    private Integer pedestrianCount;
    
    // Performance Metrics
    private BigDecimal averageWaitTime;
    private BigDecimal maximumWaitTime;
    private BigDecimal averageQueueLength;
    private Integer maximumQueueLength;
    private Integer throughput;
    
    // Efficiency Metrics
    private BigDecimal greenTimeUtilization;
    private Integer redLightViolations;
    private Integer yellowLightViolations;
    private Integer pedestrianViolations;
    
    // Incident Metrics
    private Integer accidentsCount;
    private Integer nearMissCount;
    private Integer emergencyVehiclePassages;
    
    // System Performance
    private BigDecimal systemUptimePercentage;
    private Integer malfunctionCount;
    private Integer manualOverrideCount;
    
    // Environmental
    private BigDecimal estimatedCo2Emission;
    private BigDecimal estimatedFuelConsumption;
    
    // Metadata
    private BigDecimal dataQualityScore;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MetricResponse fromEntity(IntersectionMetric metric) {
        return MetricResponse.builder()
            .id(metric.getId())
            .intersectionId(metric.getIntersection().getId())
            .intersectionName(metric.getIntersection().getName())
            .intersectionCode(metric.getIntersection().getCode())
            .measurementDate(metric.getMeasurementDate())
            .measurementHour(metric.getMeasurementHour())
            .totalVehicleCount(metric.getTotalVehicleCount())
            .carCount(metric.getCarCount())
            .truckCount(metric.getTruckCount())
            .busCount(metric.getBusCount())
            .motorcycleCount(metric.getMotorcycleCount())
            .bicycleCount(metric.getBicycleCount())
            .pedestrianCount(metric.getPedestrianCount())
            .averageWaitTime(metric.getAverageWaitTime())
            .maximumWaitTime(metric.getMaximumWaitTime())
            .averageQueueLength(metric.getAverageQueueLength())
            .maximumQueueLength(metric.getMaximumQueueLength())
            .throughput(metric.getThroughput())
            .greenTimeUtilization(metric.getGreenTimeUtilization())
            .redLightViolations(metric.getRedLightViolations())
            .yellowLightViolations(metric.getYellowLightViolations())
            .pedestrianViolations(metric.getPedestrianViolations())
            .accidentsCount(metric.getAccidentsCount())
            .nearMissCount(metric.getNearMissCount())
            .emergencyVehiclePassages(metric.getEmergencyVehiclePassages())
            .systemUptimePercentage(metric.getSystemUptimePercentage())
            .malfunctionCount(metric.getMalfunctionCount())
            .manualOverrideCount(metric.getManualOverrideCount())
            .estimatedCo2Emission(metric.getEstimatedCo2Emission())
            .estimatedFuelConsumption(metric.getEstimatedFuelConsumption())
            .dataQualityScore(metric.getDataQualityScore())
            .notes(metric.getNotes())
            .createdAt(metric.getCreatedAt())
            .updatedAt(metric.getUpdatedAt())
            .build();
    }
}

