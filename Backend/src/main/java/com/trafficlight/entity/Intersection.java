package com.trafficlight.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * HAFTA 2 - Entity Class
 * Intersection Entity - Main table for traffic light intersections
 * 
 * Represents a traffic light intersection with location, configuration,
 * and operational status information.
 */
@Entity
@Table(name = "intersections", indexes = {
    @Index(name = "idx_intersections_city_status", columnList = "city, status"),
    @Index(name = "idx_intersections_type", columnList = "intersection_type"),
    @Index(name = "idx_intersections_lat_lng", columnList = "latitude, longitude"),
    @Index(name = "idx_intersections_created_at", columnList = "created_at"),
    @Index(name = "idx_intersections_updated_at", columnList = "updated_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"configs", "metrics", "phases"})
public class Intersection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Intersection name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Intersection code is required")
    @Size(max = 100, message = "Code must not exceed 100 characters")
    @Column(nullable = false, unique = true, length = 100)
    private String code;

    // Location Information
    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    @Column(nullable = false, precision = 10, scale = 8)
    private BigDecimal latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    @Column(nullable = false, precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(columnDefinition = "TEXT")
    private String address;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String city;

    @Size(max = 100, message = "District must not exceed 100 characters")
    @Column(length = 100)
    private String district;

    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    @Column(name = "postal_code", length = 20)
    private String postalCode;

    // Intersection Type
    @NotNull(message = "Intersection type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "intersection_type", nullable = false, length = 50)
    private IntersectionType intersectionType;

    // Status
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private IntersectionStatus status = IntersectionStatus.ACTIVE;

    // Configuration References
    @Min(value = 1, message = "Lanes count must be at least 1")
    @Max(value = 20, message = "Lanes count must not exceed 20")
    @Column(name = "lanes_count")
    @Builder.Default
    private Integer lanesCount = 4;

    @Column(name = "has_pedestrian_crossing")
    @Builder.Default
    private Boolean hasPedestrianCrossing = true;

    @Column(name = "has_vehicle_detection")
    @Builder.Default
    private Boolean hasVehicleDetection = false;

    @Column(name = "has_emergency_override")
    @Builder.Default
    private Boolean hasEmergencyOverride = false;

    // Metadata
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "installation_date")
    private LocalDate installationDate;

    @Column(name = "last_maintenance_date")
    private LocalDate lastMaintenanceDate;

    @Column(name = "next_maintenance_date")
    private LocalDate nextMaintenanceDate;

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

    @Version
    @Builder.Default
    private Integer version = 0;

    // Relationships
    @OneToMany(mappedBy = "intersection", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<IntersectionConfig> configs = new ArrayList<>();

    @OneToMany(mappedBy = "intersection", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<IntersectionMetric> metrics = new ArrayList<>();

    @OneToMany(mappedBy = "intersection", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<IntersectionPhase> phases = new ArrayList<>();

    // Helper methods for bidirectional relationships
    public void addConfig(IntersectionConfig config) {
        configs.add(config);
        config.setIntersection(this);
    }

    public void removeConfig(IntersectionConfig config) {
        configs.remove(config);
        config.setIntersection(null);
    }

    public void addMetric(IntersectionMetric metric) {
        metrics.add(metric);
        metric.setIntersection(this);
    }

    public void removeMetric(IntersectionMetric metric) {
        metrics.remove(metric);
        metric.setIntersection(null);
    }

    public void addPhase(IntersectionPhase phase) {
        phases.add(phase);
        phase.setIntersection(this);
    }

    public void removePhase(IntersectionPhase phase) {
        phases.remove(phase);
        phase.setIntersection(null);
    }

    // Enums
    public enum IntersectionType {
        TRAFFIC_LIGHT("traffic_light"),
        ROUNDABOUT("roundabout"),
        CROSSROAD("crossroad"),
        PEDESTRIAN_CROSSING("pedestrian_crossing");

        private final String value;

        IntersectionType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum IntersectionStatus {
        ACTIVE("active"),
        INACTIVE("inactive"),
        MAINTENANCE("maintenance"),
        UNDER_CONSTRUCTION("under_construction");

        private final String value;

        IntersectionStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}

