package com.trafficlight.controller;

import com.trafficlight.dto.ApiResponse;
import com.trafficlight.dto.MetricRequest;
import com.trafficlight.dto.MetricResponse;
import com.trafficlight.service.MetricService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HAFTA 3 - REST Controller
 * MetricController - Metric data collection endpoints
 * 
 * Endpoints:
 * - GET /api/intersections/:id/metrics - Get metrics for intersection
 * - POST /api/intersections/:id/metrics - Create new metric
 * - GET /api/metrics/:id - Get specific metric
 * - DELETE /api/metrics/:id - Delete metric
 * 
 * Time-range filtering support: ?startDate=2024-01-01&endDate=2024-01-31
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Metrics", description = "Traffic Metrics and Analytics API")
public class MetricController {

    private final MetricService metricService;

    // ==========================================
    // Metric Endpoints (HAFTA 3)
    // ==========================================

    /**
     * GET /api/intersections/:id/metrics - Get metrics for intersection
     * Supports time-range filtering and pagination
     * 
     * Query params:
     * - startDate: Filter from date (YYYY-MM-DD)
     * - endDate: Filter to date (YYYY-MM-DD)
     * - page: Page number (default: 0)
     * - limit: Page size (default: 10)
     */
    @GetMapping("/intersections/{intersectionId}/metrics")
    @Operation(summary = "Get metrics for intersection", description = "Retrieve all metrics for a specific intersection with optional date filtering")
    public ResponseEntity<ApiResponse<Page<MetricResponse>>> getMetricsForIntersection(
            @Parameter(description = "Intersection ID") @PathVariable Long intersectionId,
            @Parameter(description = "Start date (YYYY-MM-DD)") 
                @RequestParam(required = false) 
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)") 
                @RequestParam(required = false) 
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int limit,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "measurementDate") String sort,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, limit, Sort.by(sortDirection, sort));
        
        Page<MetricResponse> metrics;
        
        // Time-range filtering
        if (startDate != null && endDate != null) {
            metrics = metricService.getMetricsByDateRange(intersectionId, startDate, endDate, pageable);
        } else {
            metrics = metricService.getMetricsByIntersectionId(intersectionId, pageable);
        }
        
        return ResponseEntity.ok(ApiResponse.success(metrics));
    }

    /**
     * POST /api/intersections/:id/metrics - Create new metric
     */
    @PostMapping("/intersections/{intersectionId}/metrics")
    @Operation(summary = "Create metric", description = "Add a new metric entry for an intersection")
    public ResponseEntity<ApiResponse<MetricResponse>> createMetric(
            @Parameter(description = "Intersection ID") @PathVariable Long intersectionId,
            @Valid @RequestBody MetricRequest request) {
        
        // Set intersection ID from path
        request.setIntersectionId(intersectionId);
        
        MetricResponse created = metricService.createMetric(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.created(created));
    }

    /**
     * GET /api/metrics/:id - Get specific metric
     */
    @GetMapping("/metrics/{id}")
    @Operation(summary = "Get metric by ID", description = "Retrieve a specific metric by its ID")
    public ResponseEntity<ApiResponse<MetricResponse>> getMetricById(
            @Parameter(description = "Metric ID") @PathVariable Long id) {
        
        MetricResponse metric = metricService.getMetricById(id);
        return ResponseEntity.ok(ApiResponse.success(metric));
    }

    /**
     * DELETE /api/metrics/:id - Delete metric
     */
    @DeleteMapping("/metrics/{id}")
    @Operation(summary = "Delete metric", description = "Delete a metric entry by ID")
    public ResponseEntity<ApiResponse<Void>> deleteMetric(
            @Parameter(description = "Metric ID") @PathVariable Long id) {
        
        metricService.deleteMetric(id);
        return ResponseEntity.ok(ApiResponse.success("Metric deleted successfully", null));
    }

    // ==========================================
    // Analytics Endpoints
    // ==========================================

    /**
     * GET /api/intersections/:id/metrics/analytics - Get analytics summary
     */
    @GetMapping("/intersections/{intersectionId}/metrics/analytics")
    @Operation(summary = "Get analytics summary", description = "Get aggregated analytics for an intersection")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAnalyticsSummary(
            @Parameter(description = "Intersection ID") @PathVariable Long intersectionId,
            @Parameter(description = "Start date") 
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") 
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Double avgWaitTime = metricService.getAverageWaitTime(intersectionId, startDate, endDate);
        Long totalVehicles = metricService.getTotalVehicleCount(intersectionId, startDate, endDate);
        
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("intersectionId", intersectionId);
        analytics.put("startDate", startDate);
        analytics.put("endDate", endDate);
        analytics.put("averageWaitTime", avgWaitTime);
        analytics.put("totalVehicleCount", totalVehicles);
        
        return ResponseEntity.ok(ApiResponse.success(analytics));
    }

    /**
     * GET /api/intersections/:id/metrics/accidents - Get metrics with accidents
     */
    @GetMapping("/intersections/{intersectionId}/metrics/accidents")
    @Operation(summary = "Get metrics with accidents", description = "Retrieve metrics that recorded accidents")
    public ResponseEntity<ApiResponse<List<MetricResponse>>> getMetricsWithAccidents(
            @Parameter(description = "Intersection ID") @PathVariable Long intersectionId,
            @Parameter(description = "Start date") 
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") 
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<MetricResponse> metrics = metricService.getMetricsWithAccidents(intersectionId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(metrics));
    }

    /**
     * GET /api/intersections/:id/metrics/violations - Get metrics with violations
     */
    @GetMapping("/intersections/{intersectionId}/metrics/violations")
    @Operation(summary = "Get metrics with violations", description = "Retrieve metrics that recorded traffic violations")
    public ResponseEntity<ApiResponse<List<MetricResponse>>> getMetricsWithViolations(
            @Parameter(description = "Intersection ID") @PathVariable Long intersectionId,
            @Parameter(description = "Start date") 
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") 
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<MetricResponse> metrics = metricService.getMetricsWithViolations(intersectionId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(metrics));
    }
}

