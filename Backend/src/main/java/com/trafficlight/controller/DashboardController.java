package com.trafficlight.controller;

import com.trafficlight.dto.ApiResponse;
import com.trafficlight.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * HAFTA 6 - Dashboard Controller
 * Frontend integration endpoints for dashboard data
 * 
 * Endpoints:
 * - GET /api/dashboard/summary - Overall statistics summary
 * - GET /api/dashboard/city-stats - City-based statistics
 * - GET /api/dashboard/performance - Performance metrics
 * - GET /api/dashboard/alerts - System alerts and warnings
 * - GET /api/dashboard/trends - Traffic trends over time
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard Data API for Frontend Integration")
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * GET /api/dashboard/summary - Overall statistics summary
     */
    @GetMapping("/summary")
    @Operation(summary = "Get dashboard summary", description = "Get overall system statistics and key metrics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSummary() {
        Map<String, Object> summary = dashboardService.getDashboardSummary();
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    /**
     * GET /api/dashboard/city-stats - City-based statistics
     */
    @GetMapping("/city-stats")
    @Operation(summary = "Get city statistics", description = "Get intersection statistics grouped by city")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCityStats() {
        Map<String, Object> cityStats = dashboardService.getCityStatistics();
        return ResponseEntity.ok(ApiResponse.success(cityStats));
    }

    /**
     * GET /api/dashboard/performance - Performance metrics
     */
    @GetMapping("/performance")
    @Operation(summary = "Get performance metrics", description = "Get system-wide performance metrics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPerformanceMetrics(
            @Parameter(description = "Start date (YYYY-MM-DD)") 
                @RequestParam(required = false) 
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)") 
                @RequestParam(required = false) 
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        // Default to last 7 days if not specified
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(7);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        Map<String, Object> performance = dashboardService.getPerformanceMetrics(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(performance));
    }

    /**
     * GET /api/dashboard/alerts - System alerts and warnings
     */
    @GetMapping("/alerts")
    @Operation(summary = "Get system alerts", description = "Get active alerts and warnings")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAlerts() {
        Map<String, Object> alerts = dashboardService.getSystemAlerts();
        return ResponseEntity.ok(ApiResponse.success(alerts));
    }

    /**
     * GET /api/dashboard/trends - Traffic trends over time
     */
    @GetMapping("/trends")
    @Operation(summary = "Get traffic trends", description = "Get traffic trends and patterns over time")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTrends(
            @Parameter(description = "City filter") 
                @RequestParam(required = false) String city,
            @Parameter(description = "Days to look back") 
                @RequestParam(defaultValue = "30") int days) {
        
        Map<String, Object> trends = dashboardService.getTrafficTrends(city, days);
        return ResponseEntity.ok(ApiResponse.success(trends));
    }

    /**
     * GET /api/dashboard/status-distribution - Intersection status distribution
     */
    @GetMapping("/status-distribution")
    @Operation(summary = "Get status distribution", description = "Get distribution of intersection statuses")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatusDistribution() {
        Map<String, Object> statusDist = dashboardService.getStatusDistribution();
        return ResponseEntity.ok(ApiResponse.success(statusDist));
    }

    /**
     * GET /api/dashboard/type-distribution - Intersection type distribution
     */
    @GetMapping("/type-distribution")
    @Operation(summary = "Get type distribution", description = "Get distribution of intersection types")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTypeDistribution() {
        Map<String, Object> typeDist = dashboardService.getTypeDistribution();
        return ResponseEntity.ok(ApiResponse.success(typeDist));
    }

    /**
     * GET /api/dashboard/top-performing - Top performing intersections
     */
    @GetMapping("/top-performing")
    @Operation(summary = "Get top performing intersections", description = "Get best performing intersections by metrics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTopPerforming(
            @Parameter(description = "Number of results") 
                @RequestParam(defaultValue = "10") int limit,
            @Parameter(description = "Start date") 
                @RequestParam(required = false) 
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") 
                @RequestParam(required = false) 
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate == null) startDate = LocalDate.now().minusDays(7);
        if (endDate == null) endDate = LocalDate.now();
        
        Map<String, Object> topPerforming = dashboardService.getTopPerformingIntersections(limit, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(topPerforming));
    }

    /**
     * GET /api/dashboard/incidents - Incident statistics
     */
    @GetMapping("/incidents")
    @Operation(summary = "Get incident statistics", description = "Get accident and violation statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getIncidentStats(
            @Parameter(description = "Start date") 
                @RequestParam(required = false) 
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") 
                @RequestParam(required = false) 
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate == null) startDate = LocalDate.now().minusDays(30);
        if (endDate == null) endDate = LocalDate.now();
        
        Map<String, Object> incidents = dashboardService.getIncidentStatistics(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(incidents));
    }
}
