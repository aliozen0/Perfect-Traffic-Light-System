package com.trafficlight.dto;

import com.trafficlight.entity.Intersection;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * HAFTA 3 - DTO Class
 * Response DTO for Intersection data
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntersectionResponse {

    private Long id;
    private String name;
    private String code;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String address;
    private String city;
    private String district;
    private String postalCode;
    private Intersection.IntersectionType intersectionType;
    private Intersection.IntersectionStatus status;
    private Integer lanesCount;
    private Boolean hasPedestrianCrossing;
    private Boolean hasVehicleDetection;
    private Boolean hasEmergencyOverride;
    private String description;
    private LocalDate installationDate;
    private LocalDate lastMaintenanceDate;
    private LocalDate nextMaintenanceDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Integer version;

    // Statistics (optional - can be populated separately)
    private Long totalMetrics;
    private Long totalPhases;
    private Long activeConfigs;

    public static IntersectionResponse fromEntity(Intersection intersection) {
        return IntersectionResponse.builder()
            .id(intersection.getId())
            .name(intersection.getName())
            .code(intersection.getCode())
            .latitude(intersection.getLatitude())
            .longitude(intersection.getLongitude())
            .address(intersection.getAddress())
            .city(intersection.getCity())
            .district(intersection.getDistrict())
            .postalCode(intersection.getPostalCode())
            .intersectionType(intersection.getIntersectionType())
            .status(intersection.getStatus())
            .lanesCount(intersection.getLanesCount())
            .hasPedestrianCrossing(intersection.getHasPedestrianCrossing())
            .hasVehicleDetection(intersection.getHasVehicleDetection())
            .hasEmergencyOverride(intersection.getHasEmergencyOverride())
            .description(intersection.getDescription())
            .installationDate(intersection.getInstallationDate())
            .lastMaintenanceDate(intersection.getLastMaintenanceDate())
            .nextMaintenanceDate(intersection.getNextMaintenanceDate())
            .createdAt(intersection.getCreatedAt())
            .updatedAt(intersection.getUpdatedAt())
            .createdBy(intersection.getCreatedBy())
            .updatedBy(intersection.getUpdatedBy())
            .version(intersection.getVersion())
            .build();
    }
}

