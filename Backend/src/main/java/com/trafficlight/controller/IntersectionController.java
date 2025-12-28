package com.trafficlight.controller;

import com.trafficlight.dto.ApiResponse;
import com.trafficlight.dto.IntersectionRequest;
import com.trafficlight.dto.IntersectionResponse;
import com.trafficlight.entity.Intersection.IntersectionStatus;
import com.trafficlight.entity.Intersection.IntersectionType;
import com.trafficlight.service.IntersectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * HAFTA 3 - REST Controller
 * IntersectionController - CRUD endpoints for intersections
 * 
 * Endpoints:
 * - GET /api/intersections - List all intersections
 * - GET /api/intersections/:id - Get specific intersection
 * - POST /api/intersections - Create new intersection
 * - PUT /api/intersections/:id - Update intersection
 * - DELETE /api/intersections/:id - Delete intersection
 * 
 * Filter and Pagination support
 */
@RestController
@RequestMapping("/api/intersections")
@RequiredArgsConstructor
@Tag(name = "Intersections", description = "Traffic Light Intersection Management API")
public class IntersectionController {

    private final IntersectionService intersectionService;

    // ==========================================
    // CRUD Endpoints (HAFTA 3)
    // ==========================================

    /**
     * GET /api/intersections - List all intersections
     * Supports filtering and pagination
     * 
     * Query params:
     * - city: Filter by city
     * - status: Filter by status
     * - page: Page number (default: 0)
     * - limit: Page size (default: 10)
     * - sort: Sort field (default: id)
     */
    @GetMapping
    @Operation(summary = "Get all intersections", description = "Retrieve all traffic light intersections with optional filtering and pagination")
    public ResponseEntity<ApiResponse<Page<IntersectionResponse>>> getAllIntersections(
            @Parameter(description = "Filter by city") @RequestParam(required = false) String city,
            @Parameter(description = "Filter by status") @RequestParam(required = false) IntersectionStatus status,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int limit,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "id") String sort,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String direction) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, limit, Sort.by(sortDirection, sort));
        
        Page<IntersectionResponse> intersections = intersectionService.findByFilters(city, status, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(intersections));
    }

    /**
     * GET /api/intersections/:id - Get specific intersection
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get intersection by ID", description = "Retrieve a specific intersection by its ID")
    public ResponseEntity<ApiResponse<IntersectionResponse>> getIntersectionById(
            @Parameter(description = "Intersection ID") @PathVariable Long id) {
        
        IntersectionResponse intersection = intersectionService.getIntersectionById(id);
        return ResponseEntity.ok(ApiResponse.success(intersection));
    }

    /**
     * POST /api/intersections - Create new intersection
     */
    @PostMapping
    @Operation(summary = "Create intersection", description = "Create a new traffic light intersection")
    public ResponseEntity<ApiResponse<IntersectionResponse>> createIntersection(
            @Valid @RequestBody IntersectionRequest request) {
        
        IntersectionResponse created = intersectionService.createIntersection(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.created(created));
    }

    /**
     * PUT /api/intersections/:id - Update intersection
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update intersection", description = "Update an existing intersection")
    public ResponseEntity<ApiResponse<IntersectionResponse>> updateIntersection(
            @Parameter(description = "Intersection ID") @PathVariable Long id,
            @Valid @RequestBody IntersectionRequest request) {
        
        IntersectionResponse updated = intersectionService.updateIntersection(id, request);
        return ResponseEntity.ok(ApiResponse.success("Intersection updated successfully", updated));
    }

    /**
     * DELETE /api/intersections/:id - Delete intersection
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete intersection", description = "Delete an intersection by ID")
    public ResponseEntity<ApiResponse<Void>> deleteIntersection(
            @Parameter(description = "Intersection ID") @PathVariable Long id) {
        
        intersectionService.deleteIntersection(id);
        return ResponseEntity.ok(ApiResponse.success("Intersection deleted successfully", null));
    }

    // ==========================================
    // Additional Endpoints
    // ==========================================

    /**
     * GET /api/intersections/nearby - Find nearby intersections
     * Query params: lat, lng, radius
     */
    @GetMapping("/nearby")
    @Operation(summary = "Find nearby intersections", description = "Find intersections within a specified radius")
    public ResponseEntity<ApiResponse<List<IntersectionResponse>>> findNearbyIntersections(
            @Parameter(description = "Latitude") @RequestParam BigDecimal lat,
            @Parameter(description = "Longitude") @RequestParam BigDecimal lng,
            @Parameter(description = "Radius in kilometers") @RequestParam(defaultValue = "5.0") double radius) {
        
        List<IntersectionResponse> nearby = intersectionService.findNearbyIntersections(lat, lng, radius);
        return ResponseEntity.ok(ApiResponse.success(nearby));
    }

    /**
     * GET /api/intersections/search - Search intersections
     * Query params: q (search term), page, limit
     */
    @GetMapping("/search")
    @Operation(summary = "Search intersections", description = "Search intersections by name, code or address")
    public ResponseEntity<ApiResponse<Page<IntersectionResponse>>> searchIntersections(
            @Parameter(description = "Search term") @RequestParam String q,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int limit) {
        
        Pageable pageable = PageRequest.of(page, limit);
        Page<IntersectionResponse> results = intersectionService.searchIntersections(q, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    /**
     * GET /api/intersections/type/:type - Get intersections by type
     */
    @GetMapping("/type/{type}")
    @Operation(summary = "Get intersections by type", description = "Retrieve intersections filtered by type")
    public ResponseEntity<ApiResponse<List<IntersectionResponse>>> getIntersectionsByType(
            @Parameter(description = "Intersection type") @PathVariable IntersectionType type) {
        
        List<IntersectionResponse> intersections = intersectionService.findByType(type);
        return ResponseEntity.ok(ApiResponse.success(intersections));
    }

    /**
     * GET /api/intersections/statistics/cities - Get city statistics
     */
    @GetMapping("/statistics/cities")
    @Operation(summary = "Get city statistics", description = "Get intersection count statistics by city")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getCityStatistics() {
        Map<String, Long> statistics = intersectionService.getCityStatistics();
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }
}

