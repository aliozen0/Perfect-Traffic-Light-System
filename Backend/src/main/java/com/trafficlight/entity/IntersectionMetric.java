package com.trafficlight.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * HAFTA 2 - Entity Class
 * IntersectionMetric Entity - Performance metrics and KPIs
 * 
 * Stores traffic volume, performance metrics, efficiency data, and incidents.
 */
@Entity
@Table(name = "intersection_metrics", 
    indexes = {
        @Index(name = "idx_metrics_date_hour", columnList = "intersection_id, measurement_date, measurement_hour")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "unique_metric_period", 
            columnNames = {"intersection_id", "measurement_date", "measurement_hour"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "intersection")
public class IntersectionMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Foreign Key
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intersection_id", nullable = false)
    private Intersection intersection;

    // Time Period
    @NotNull(message = "Measurement date is required")
    @Column(name = "measurement_date", nullable = false)
    private LocalDate measurementDate;

    @Min(value = 0, message = "Measurement hour must be between 0 and 23")
    @Max(value = 23, message = "Measurement hour must be between 0 and 23")
    @Column(name = "measurement_hour")
    private Integer measurementHour;

    // Traffic Volume Metrics
    @Min(value = 0, message = "Total vehicle count cannot be negative")
    @Column(name = "total_vehicle_count")
    @Builder.Default
    private Integer totalVehicleCount = 0;

    @Min(value = 0, message = "Car count cannot be negative")
    @Column(name = "car_count")
    @Builder.Default
    private Integer carCount = 0;

    @Min(value = 0, message = "Truck count cannot be negative")
    @Column(name = "truck_count")
    @Builder.Default
    private Integer truckCount = 0;

    @Min(value = 0, message = "Bus count cannot be negative")
    @Column(name = "bus_count")
    @Builder.Default
    private Integer busCount = 0;

    @Min(value = 0, message = "Motorcycle count cannot be negative")
    @Column(name = "motorcycle_count")
    @Builder.Default
    private Integer motorcycleCount = 0;

    @Min(value = 0, message = "Bicycle count cannot be negative")
    @Column(name = "bicycle_count")
    @Builder.Default
    private Integer bicycleCount = 0;

    @Min(value = 0, message = "Pedestrian count cannot be negative")
    @Column(name = "pedestrian_count")
    @Builder.Default
    private Integer pedestrianCount = 0;

    // Performance Metrics
    @DecimalMin(value = "0.0", message = "Average wait time cannot be negative")
    @Column(name = "average_wait_time", precision = 10, scale = 2)
    private BigDecimal averageWaitTime;

    @DecimalMin(value = "0.0", message = "Maximum wait time cannot be negative")
    @Column(name = "maximum_wait_time", precision = 10, scale = 2)
    private BigDecimal maximumWaitTime;

    @DecimalMin(value = "0.0", message = "Average queue length cannot be negative")
    @Column(name = "average_queue_length", precision = 10, scale = 2)
    private BigDecimal averageQueueLength;

    @Min(value = 0, message = "Maximum queue length cannot be negative")
    @Column(name = "maximum_queue_length")
    private Integer maximumQueueLength;

    @Min(value = 0, message = "Throughput cannot be negative")
    private Integer throughput;

    // Efficiency Metrics
    @DecimalMin(value = "0.0", message = "Green time utilization cannot be negative")
    @DecimalMax(value = "100.0", message = "Green time utilization cannot exceed 100%")
    @Column(name = "green_time_utilization", precision = 5, scale = 2)
    private BigDecimal greenTimeUtilization;

    @Min(value = 0, message = "Red light violations cannot be negative")
    @Column(name = "red_light_violations")
    @Builder.Default
    private Integer redLightViolations = 0;

    @Min(value = 0, message = "Yellow light violations cannot be negative")
    @Column(name = "yellow_light_violations")
    @Builder.Default
    private Integer yellowLightViolations = 0;

    @Min(value = 0, message = "Pedestrian violations cannot be negative")
    @Column(name = "pedestrian_violations")
    @Builder.Default
    private Integer pedestrianViolations = 0;

    // Incident Metrics
    @Min(value = 0, message = "Accidents count cannot be negative")
    @Column(name = "accidents_count")
    @Builder.Default
    private Integer accidentsCount = 0;

    @Min(value = 0, message = "Near miss count cannot be negative")
    @Column(name = "near_miss_count")
    @Builder.Default
    private Integer nearMissCount = 0;

    @Min(value = 0, message = "Emergency vehicle passages cannot be negative")
    @Column(name = "emergency_vehicle_passages")
    @Builder.Default
    private Integer emergencyVehiclePassages = 0;

    // System Performance
    @DecimalMin(value = "0.0", message = "System uptime percentage cannot be negative")
    @DecimalMax(value = "100.0", message = "System uptime percentage cannot exceed 100%")
    @Column(name = "system_uptime_percentage", precision = 5, scale = 2)
    private BigDecimal systemUptimePercentage;

    @Min(value = 0, message = "Malfunction count cannot be negative")
    @Column(name = "malfunction_count")
    @Builder.Default
    private Integer malfunctionCount = 0;

    @Min(value = 0, message = "Manual override count cannot be negative")
    @Column(name = "manual_override_count")
    @Builder.Default
    private Integer manualOverrideCount = 0;

    // Environmental Metrics
    @DecimalMin(value = "0.0", message = "CO2 emission cannot be negative")
    @Column(name = "estimated_co2_emission", precision = 10, scale = 2)
    private BigDecimal estimatedCo2Emission;

    @DecimalMin(value = "0.0", message = "Fuel consumption cannot be negative")
    @Column(name = "estimated_fuel_consumption", precision = 10, scale = 2)
    private BigDecimal estimatedFuelConsumption;

    // Metadata
    @DecimalMin(value = "0.0", message = "Data quality score must be between 0 and 1")
    @DecimalMax(value = "1.0", message = "Data quality score must be between 0 and 1")
    @Column(name = "data_quality_score", precision = 3, scale = 2)
    private BigDecimal dataQualityScore;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // Audit Fields
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Size(max = 100)
    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Size(max = 100)
    @Column(name = "updated_by", length = 100)
    private String updatedBy;
}

