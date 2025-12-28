package com.trafficlight.dto;

import com.trafficlight.entity.Intersection;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * HAFTA 3 - DTO Class
 * Request DTO for creating and updating Intersections
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntersectionRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @NotBlank(message = "Code is required")
    @Size(max = 100, message = "Code must not exceed 100 characters")
    private String code;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private BigDecimal latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private BigDecimal longitude;

    private String address;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Size(max = 100, message = "District must not exceed 100 characters")
    private String district;

    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    private String postalCode;

    @NotNull(message = "Intersection type is required")
    private Intersection.IntersectionType intersectionType;

    private Intersection.IntersectionStatus status;

    @Min(value = 1, message = "Lanes count must be at least 1")
    @Max(value = 20, message = "Lanes count must not exceed 20")
    private Integer lanesCount;

    private Boolean hasPedestrianCrossing;
    private Boolean hasVehicleDetection;
    private Boolean hasEmergencyOverride;

    private String description;
    private LocalDate installationDate;
    private LocalDate lastMaintenanceDate;
    private LocalDate nextMaintenanceDate;

    private String createdBy;
    private String updatedBy;
}

