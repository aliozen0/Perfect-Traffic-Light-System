package com.trafficlight.service;

import com.trafficlight.dto.IntersectionRequest;
import com.trafficlight.dto.IntersectionResponse;
import com.trafficlight.entity.Intersection;
import com.trafficlight.entity.Intersection.IntersectionStatus;
import com.trafficlight.entity.Intersection.IntersectionType;
import com.trafficlight.exception.BadRequestException;
import com.trafficlight.exception.DuplicateResourceException;
import com.trafficlight.exception.ResourceNotFoundException;
import com.trafficlight.repository.IntersectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * HAFTA 3 - Service Layer
 * IntersectionService - Business logic for intersection management
 * 
 * Provides CRUD operations and custom business logic
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class IntersectionService {

    private final IntersectionRepository intersectionRepository;

    // ==========================================
    // CRUD Operations (HAFTA 3)
    // ==========================================

    /**
     * Get all intersections
     * GET /intersections
     */
    public List<IntersectionResponse> getAllIntersections() {
        log.info("Fetching all intersections");
        return intersectionRepository.findAll().stream()
            .map(IntersectionResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Get intersections with pagination
     * GET /intersections?page=1&limit=10
     */
    public Page<IntersectionResponse> getAllIntersections(Pageable pageable) {
        log.info("Fetching intersections with pagination: {}", pageable);
        return intersectionRepository.findAll(pageable)
            .map(IntersectionResponse::fromEntity);
    }

    /**
     * Get specific intersection by ID
     * GET /intersections/:id
     */
    public IntersectionResponse getIntersectionById(Long id) {
        log.info("Fetching intersection with id: {}", id);
        Intersection intersection = intersectionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Intersection", id));
        return IntersectionResponse.fromEntity(intersection);
    }

    /**
     * Create new intersection
     * POST /intersections
     */
    @Transactional
    public IntersectionResponse createIntersection(IntersectionRequest request) {
        log.info("Creating new intersection with code: {}", request.getCode());
        
        // Check for duplicate code
        if (intersectionRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Intersection", "code", request.getCode());
        }

        Intersection intersection = Intersection.builder()
            .name(request.getName())
            .code(request.getCode())
            .latitude(request.getLatitude())
            .longitude(request.getLongitude())
            .address(request.getAddress())
            .city(request.getCity())
            .district(request.getDistrict())
            .postalCode(request.getPostalCode())
            .intersectionType(request.getIntersectionType())
            .status(request.getStatus() != null ? request.getStatus() : IntersectionStatus.ACTIVE)
            .lanesCount(request.getLanesCount() != null ? request.getLanesCount() : 4)
            .hasPedestrianCrossing(request.getHasPedestrianCrossing() != null ? request.getHasPedestrianCrossing() : true)
            .hasVehicleDetection(request.getHasVehicleDetection() != null ? request.getHasVehicleDetection() : false)
            .hasEmergencyOverride(request.getHasEmergencyOverride() != null ? request.getHasEmergencyOverride() : false)
            .description(request.getDescription())
            .installationDate(request.getInstallationDate())
            .lastMaintenanceDate(request.getLastMaintenanceDate())
            .nextMaintenanceDate(request.getNextMaintenanceDate())
            .createdBy(request.getCreatedBy() != null ? request.getCreatedBy() : "system")
            .build();

        Intersection saved = intersectionRepository.save(intersection);
        log.info("Intersection created successfully with id: {}", saved.getId());
        
        return IntersectionResponse.fromEntity(saved);
    }

    /**
     * Update intersection
     * PUT /intersections/:id
     */
    @Transactional
    public IntersectionResponse updateIntersection(Long id, IntersectionRequest request) {
        log.info("Updating intersection with id: {}", id);
        
        Intersection intersection = intersectionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Intersection", id));

        // Check for duplicate code (if code is being changed)
        if (!intersection.getCode().equals(request.getCode()) && 
            intersectionRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Intersection", "code", request.getCode());
        }

        // Update fields
        intersection.setName(request.getName());
        intersection.setCode(request.getCode());
        intersection.setLatitude(request.getLatitude());
        intersection.setLongitude(request.getLongitude());
        intersection.setAddress(request.getAddress());
        intersection.setCity(request.getCity());
        intersection.setDistrict(request.getDistrict());
        intersection.setPostalCode(request.getPostalCode());
        intersection.setIntersectionType(request.getIntersectionType());
        
        if (request.getStatus() != null) {
            intersection.setStatus(request.getStatus());
        }
        if (request.getLanesCount() != null) {
            intersection.setLanesCount(request.getLanesCount());
        }
        if (request.getHasPedestrianCrossing() != null) {
            intersection.setHasPedestrianCrossing(request.getHasPedestrianCrossing());
        }
        if (request.getHasVehicleDetection() != null) {
            intersection.setHasVehicleDetection(request.getHasVehicleDetection());
        }
        if (request.getHasEmergencyOverride() != null) {
            intersection.setHasEmergencyOverride(request.getHasEmergencyOverride());
        }
        
        intersection.setDescription(request.getDescription());
        intersection.setInstallationDate(request.getInstallationDate());
        intersection.setLastMaintenanceDate(request.getLastMaintenanceDate());
        intersection.setNextMaintenanceDate(request.getNextMaintenanceDate());
        intersection.setUpdatedBy(request.getUpdatedBy() != null ? request.getUpdatedBy() : "system");

        Intersection updated = intersectionRepository.save(intersection);
        log.info("Intersection updated successfully with id: {}", updated.getId());
        
        return IntersectionResponse.fromEntity(updated);
    }

    /**
     * Delete intersection
     * DELETE /intersections/:id
     */
    @Transactional
    public void deleteIntersection(Long id) {
        log.info("Deleting intersection with id: {}", id);
        
        if (!intersectionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Intersection", id);
        }

        intersectionRepository.deleteById(id);
        log.info("Intersection deleted successfully with id: {}", id);
    }

    // ==========================================
    // Filter and Pagination (HAFTA 3)
    // ==========================================

    /**
     * Filter by city and status
     * GET /intersections?city=Istanbul&status=active
     */
    public List<IntersectionResponse> findByFilters(String city, IntersectionStatus status) {
        log.info("Finding intersections by city: {}, status: {}", city, status);
        
        if (city != null && status != null) {
            return intersectionRepository.findByCityAndStatus(city, status).stream()
                .map(IntersectionResponse::fromEntity)
                .collect(Collectors.toList());
        } else if (city != null) {
            return intersectionRepository.findByCity(city).stream()
                .map(IntersectionResponse::fromEntity)
                .collect(Collectors.toList());
        } else if (status != null) {
            return intersectionRepository.findByStatus(status).stream()
                .map(IntersectionResponse::fromEntity)
                .collect(Collectors.toList());
        }
        
        return getAllIntersections();
    }

    /**
     * Filter with pagination
     * GET /intersections?city=Istanbul&status=active&page=1&limit=10
     */
    public Page<IntersectionResponse> findByFilters(
            String city, 
            IntersectionStatus status, 
            Pageable pageable) {
        
        log.info("Finding intersections by city: {}, status: {} with pagination", city, status);
        
        if (city != null && status != null) {
            return intersectionRepository.findByCityAndStatus(city, status, pageable)
                .map(IntersectionResponse::fromEntity);
        } else if (city != null) {
            return intersectionRepository.findByCity(city, pageable)
                .map(IntersectionResponse::fromEntity);
        } else if (status != null) {
            return intersectionRepository.findByStatus(status, pageable)
                .map(IntersectionResponse::fromEntity);
        }
        
        return getAllIntersections(pageable);
    }

    // ==========================================
    // Custom Business Logic
    // ==========================================

    /**
     * Find nearby intersections
     * GET /intersections/nearby?lat=41.0&lng=29.0&radius=5
     */
    public List<IntersectionResponse> findNearbyIntersections(
            BigDecimal latitude, 
            BigDecimal longitude, 
            double radiusKm) {
        
        log.info("Finding intersections near lat: {}, lng: {}, radius: {}km", 
            latitude, longitude, radiusKm);
        
        if (radiusKm <= 0) {
            throw new BadRequestException("Radius must be greater than 0");
        }
        
        return intersectionRepository.findNearby(latitude, longitude, radiusKm).stream()
            .map(IntersectionResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Find by intersection type
     */
    public List<IntersectionResponse> findByType(IntersectionType type) {
        log.info("Finding intersections by type: {}", type);
        return intersectionRepository.findByIntersectionType(type).stream()
            .map(IntersectionResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Search intersections
     * GET /intersections/search?q=Taksim
     */
    public Page<IntersectionResponse> searchIntersections(String searchTerm, Pageable pageable) {
        log.info("Searching intersections with term: {}", searchTerm);
        return intersectionRepository.searchIntersections(searchTerm, pageable)
            .map(IntersectionResponse::fromEntity);
    }

    /**
     * Get city statistics
     */
    public Map<String, Long> getCityStatistics() {
        log.info("Fetching city statistics");
        List<Object[]> stats = intersectionRepository.getCityStatistics();
        return stats.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> (Long) row[1]
            ));
    }
}
