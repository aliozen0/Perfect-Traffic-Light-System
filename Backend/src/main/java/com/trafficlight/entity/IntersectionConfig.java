package com.trafficlight.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * HAFTA 2 - Entity Class
 * IntersectionConfig Entity - Configuration parameters for intersections
 * 
 * Stores timing configurations, detection system settings, and operational modes.
 */
@Entity
@Table(name = "intersection_configs", indexes = {
    @Index(name = "idx_configs_intersection_active", columnList = "intersection_id, is_active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "intersection")
public class IntersectionConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Foreign Key
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intersection_id", nullable = false)
    private Intersection intersection;

    // Timing Configuration
    @NotNull(message = "Green light duration is required")
    @Min(value = 5, message = "Green light duration must be at least 5 seconds")
    @Max(value = 300, message = "Green light duration must not exceed 300 seconds")
    @Column(name = "green_light_duration", nullable = false)
    @Builder.Default
    private Integer greenLightDuration = 30;

    @NotNull(message = "Yellow light duration is required")
    @Min(value = 2, message = "Yellow light duration must be at least 2 seconds")
    @Max(value = 10, message = "Yellow light duration must not exceed 10 seconds")
    @Column(name = "yellow_light_duration", nullable = false)
    @Builder.Default
    private Integer yellowLightDuration = 3;

    @NotNull(message = "Red light duration is required")
    @Min(value = 5, message = "Red light duration must be at least 5 seconds")
    @Max(value = 300, message = "Red light duration must not exceed 300 seconds")
    @Column(name = "red_light_duration", nullable = false)
    @Builder.Default
    private Integer redLightDuration = 30;

    @Min(value = 1, message = "All red duration must be at least 1 second")
    @Max(value = 10, message = "All red duration must not exceed 10 seconds")
    @Column(name = "all_red_duration")
    @Builder.Default
    private Integer allRedDuration = 2;

    // Advanced Configuration
    @Min(value = 5, message = "Pedestrian crossing duration must be at least 5 seconds")
    @Max(value = 120, message = "Pedestrian crossing duration must not exceed 120 seconds")
    @Column(name = "pedestrian_crossing_duration")
    @Builder.Default
    private Integer pedestrianCrossingDuration = 15;

    @Min(value = 3, message = "Minimum green time must be at least 3 seconds")
    @Column(name = "minimum_green_time")
    @Builder.Default
    private Integer minimumGreenTime = 5;

    @Min(value = 10, message = "Maximum green time must be at least 10 seconds")
    @Column(name = "maximum_green_time")
    @Builder.Default
    private Integer maximumGreenTime = 120;

    // Detection System Config
    @Column(name = "vehicle_detection_enabled")
    @Builder.Default
    private Boolean vehicleDetectionEnabled = false;

    @Column(name = "pedestrian_button_enabled")
    @Builder.Default
    private Boolean pedestrianButtonEnabled = true;

    @Column(name = "emergency_vehicle_priority")
    @Builder.Default
    private Boolean emergencyVehiclePriority = false;

    // Adaptive Traffic Control
    @Column(name = "adaptive_timing_enabled")
    @Builder.Default
    private Boolean adaptiveTimingEnabled = false;

    @Column(name = "peak_hour_mode_enabled")
    @Builder.Default
    private Boolean peakHourModeEnabled = false;

    @Column(name = "night_mode_enabled")
    @Builder.Default
    private Boolean nightModeEnabled = false;

    // Time-based Configuration
    @Column(name = "peak_morning_start")
    private LocalTime peakMorningStart;

    @Column(name = "peak_morning_end")
    private LocalTime peakMorningEnd;

    @Column(name = "peak_evening_start")
    private LocalTime peakEveningStart;

    @Column(name = "peak_evening_end")
    private LocalTime peakEveningEnd;

    @Column(name = "night_mode_start")
    private LocalTime nightModeStart;

    @Column(name = "night_mode_end")
    private LocalTime nightModeEnd;

    // System Configuration
    @Min(value = 30, message = "Cycle length must be at least 30 seconds")
    @Max(value = 300, message = "Cycle length must not exceed 300 seconds")
    @Column(name = "cycle_length")
    @Builder.Default
    private Integer cycleLength = 90;

    @Column(name = "coordination_enabled")
    @Builder.Default
    private Boolean coordinationEnabled = false;

    @Column(name = "coordination_offset")
    @Builder.Default
    private Integer coordinationOffset = 0;

    // Metadata
    @Size(max = 20, message = "Config version must not exceed 20 characters")
    @Column(name = "config_version", length = 20)
    @Builder.Default
    private String configVersion = "1.0";

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "effective_from")
    private LocalDate effectiveFrom;

    @Column(name = "effective_until")
    private LocalDate effectiveUntil;

    // Audit Fields
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Size(max = 100, message = "Created by must not exceed 100 characters")
    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Size(max = 100, message = "Updated by must not exceed 100 characters")
    @Column(name = "updated_by", length = 100)
    private String updatedBy;
}

