package com.trafficlight.controller;

import com.trafficlight.dto.ApiResponse;
import com.trafficlight.dto.IntersectionResponse;
import com.trafficlight.service.MapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * HAFTA 6 - Map Controller
 * Map integration endpoints for frontend map display
 * 
 * Endpoints:
 * - GET /api/map/intersections - Get all intersections for map
 * - GET /api/map/bounds - Get intersections within bounding box
 * - GET /api/map/clusters - Get clustering data for map
 * - GET /api/map/city/{city} - Get intersections for specific city
 */
@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
@Tag(name = "Map", description = "Map Integration API")
public class MapController {

    private final MapService mapService;

    /**
     * GET /api/map/intersections - Get all intersections for map display
     */
    @GetMapping("/intersections")
    @Operation(summary = "Get all intersections for map", description = "Get all intersections with location data for map display")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllIntersectionsForMap(
            @Parameter(description = "Filter by status") @RequestParam(required = false) String status) {
        
        List<Map<String, Object>> intersections = mapService.getAllIntersectionsForMap(status);
        return ResponseEntity.ok(ApiResponse.success(intersections));
    }

    /**
     * GET /api/map/bounds - Get intersections within bounding box
     * 
     * Bounding box defined by:
     * - minLat: Minimum latitude (south)
     * - maxLat: Maximum latitude (north)
     * - minLng: Minimum longitude (west)
     * - maxLng: Maximum longitude (east)
     */
    @GetMapping("/bounds")
    @Operation(summary = "Get intersections in bounds", description = "Get intersections within a geographical bounding box")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getIntersectionsInBounds(
            @Parameter(description = "Minimum latitude (south)") @RequestParam BigDecimal minLat,
            @Parameter(description = "Maximum latitude (north)") @RequestParam BigDecimal maxLat,
            @Parameter(description = "Minimum longitude (west)") @RequestParam BigDecimal minLng,
            @Parameter(description = "Maximum longitude (east)") @RequestParam BigDecimal maxLng) {
        
        List<Map<String, Object>> intersections = mapService.getIntersectionsInBounds(
            minLat, maxLat, minLng, maxLng);
        
        return ResponseEntity.ok(ApiResponse.success(intersections));
    }

    /**
     * GET /api/map/clusters - Get clustering data for map
     * 
     * Returns clustered intersection data for better map performance
     * when displaying many markers
     */
    @GetMapping("/clusters")
    @Operation(summary = "Get clustering data", description = "Get clustered intersection data for efficient map rendering")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getClusteringData(
            @Parameter(description = "Zoom level (0-20)") @RequestParam(defaultValue = "10") int zoom) {
        
        Map<String, Object> clusterData = mapService.getClusteringData(zoom);
        return ResponseEntity.ok(ApiResponse.success(clusterData));
    }

    /**
     * GET /api/map/city/{city} - Get intersections for specific city
     */
    @GetMapping("/city/{city}")
    @Operation(summary = "Get city intersections", description = "Get all intersections for a specific city")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCityIntersections(
            @Parameter(description = "City name") @PathVariable String city) {
        
        Map<String, Object> cityData = mapService.getCityIntersections(city);
        return ResponseEntity.ok(ApiResponse.success(cityData));
    }

    /**
     * GET /api/map/nearby/{id} - Get nearby intersections
     */
    @GetMapping("/nearby/{id}")
    @Operation(summary = "Get nearby intersections", description = "Get intersections near a specific intersection")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getNearbyIntersections(
            @Parameter(description = "Intersection ID") @PathVariable Long id,
            @Parameter(description = "Radius in kilometers") @RequestParam(defaultValue = "5.0") double radius) {
        
        List<Map<String, Object>> nearby = mapService.getNearbyIntersections(id, radius);
        return ResponseEntity.ok(ApiResponse.success(nearby));
    }

    /**
     * GET /api/map/route - Get intersections along a route
     */
    @GetMapping("/route")
    @Operation(summary = "Get intersections along route", description = "Get intersections along a route defined by waypoints")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getIntersectionsAlongRoute(
            @Parameter(description = "Comma-separated latitude values") @RequestParam String lats,
            @Parameter(description = "Comma-separated longitude values") @RequestParam String lngs,
            @Parameter(description = "Buffer radius in km") @RequestParam(defaultValue = "1.0") double buffer) {
        
        List<Map<String, Object>> intersections = mapService.getIntersectionsAlongRoute(lats, lngs, buffer);
        return ResponseEntity.ok(ApiResponse.success(intersections));
    }

    /**
     * GET /api/map/heatmap - Get heatmap data for traffic intensity
     */
    @GetMapping("/heatmap")
    @Operation(summary = "Get heatmap data", description = "Get traffic intensity heatmap data")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHeatmapData(
            @Parameter(description = "City filter") @RequestParam(required = false) String city,
            @Parameter(description = "Days to analyze") @RequestParam(defaultValue = "7") int days) {
        
        Map<String, Object> heatmapData = mapService.getHeatmapData(city, days);
        return ResponseEntity.ok(ApiResponse.success(heatmapData));
    }

    /**
     * GET /api/map/geojson - Get GeoJSON format data for map
     */
    @GetMapping("/geojson")
    @Operation(summary = "Get GeoJSON data", description = "Get intersection data in GeoJSON format")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getGeoJsonData(
            @Parameter(description = "City filter") @RequestParam(required = false) String city) {
        
        Map<String, Object> geoJson = mapService.getGeoJsonData(city);
        return ResponseEntity.ok(ApiResponse.success(geoJson));
    }
}
