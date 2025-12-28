package com.trafficlight.service;

import com.trafficlight.entity.Intersection;
import com.trafficlight.entity.Intersection.IntersectionStatus;
import com.trafficlight.entity.IntersectionMetric;
import com.trafficlight.exception.ResourceNotFoundException;
import com.trafficlight.repository.IntersectionMetricRepository;
import com.trafficlight.repository.IntersectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * HAFTA 6 - Map Service
 * Business logic for map-related operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MapService {

    private final IntersectionRepository intersectionRepository;
    private final IntersectionMetricRepository metricRepository;

    /**
     * Get all intersections for map display
     */
    public List<Map<String, Object>> getAllIntersectionsForMap(String status) {
        List<Intersection> intersections;
        
        if (status != null && !status.isEmpty()) {
            try {
                IntersectionStatus statusEnum = IntersectionStatus.valueOf(status.toUpperCase());
                intersections = intersectionRepository.findByStatus(statusEnum);
            } catch (IllegalArgumentException e) {
                intersections = intersectionRepository.findAll();
            }
        } else {
            intersections = intersectionRepository.findAll();
        }
        
        return intersections.stream()
            .map(this::convertToMapData)
            .collect(Collectors.toList());
    }

    /**
     * Get intersections within bounding box
     */
    public List<Map<String, Object>> getIntersectionsInBounds(
            BigDecimal minLat, BigDecimal maxLat, BigDecimal minLng, BigDecimal maxLng) {
        
        List<Intersection> allIntersections = intersectionRepository.findAll();
        
        return allIntersections.stream()
            .filter(i -> {
                BigDecimal lat = i.getLatitude();
                BigDecimal lng = i.getLongitude();
                return lat.compareTo(minLat) >= 0 && lat.compareTo(maxLat) <= 0 &&
                       lng.compareTo(minLng) >= 0 && lng.compareTo(maxLng) <= 0;
            })
            .map(this::convertToMapData)
            .collect(Collectors.toList());
    }

    /**
     * Get clustering data for map
     */
    public Map<String, Object> getClusteringData(int zoom) {
        Map<String, Object> clusterData = new HashMap<>();
        
        List<Intersection> allIntersections = intersectionRepository.findAll();
        
        // Simple clustering based on zoom level
        // Higher zoom = more detail, lower zoom = more clustering
        double clusterRadius = calculateClusterRadius(zoom);
        
        List<Map<String, Object>> clusters = createClusters(allIntersections, clusterRadius);
        
        clusterData.put("clusters", clusters);
        clusterData.put("zoom", zoom);
        clusterData.put("clusterRadius", clusterRadius);
        clusterData.put("totalIntersections", allIntersections.size());
        
        return clusterData;
    }

    /**
     * Get intersections for specific city
     */
    public Map<String, Object> getCityIntersections(String city) {
        List<Intersection> cityIntersections = intersectionRepository.findByCity(city);
        
        Map<String, Object> cityData = new HashMap<>();
        cityData.put("city", city);
        cityData.put("totalIntersections", cityIntersections.size());
        cityData.put("intersections", cityIntersections.stream()
            .map(this::convertToMapData)
            .collect(Collectors.toList()));
        
        // Calculate city bounds
        if (!cityIntersections.isEmpty()) {
            BigDecimal minLat = cityIntersections.stream()
                .map(Intersection::getLatitude)
                .min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
            BigDecimal maxLat = cityIntersections.stream()
                .map(Intersection::getLatitude)
                .max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
            BigDecimal minLng = cityIntersections.stream()
                .map(Intersection::getLongitude)
                .min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
            BigDecimal maxLng = cityIntersections.stream()
                .map(Intersection::getLongitude)
                .max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
            
            Map<String, Object> bounds = new HashMap<>();
            bounds.put("minLat", minLat);
            bounds.put("maxLat", maxLat);
            bounds.put("minLng", minLng);
            bounds.put("maxLng", maxLng);
            
            // Calculate center
            BigDecimal centerLat = minLat.add(maxLat).divide(new BigDecimal("2"));
            BigDecimal centerLng = minLng.add(maxLng).divide(new BigDecimal("2"));
            
            cityData.put("bounds", bounds);
            cityData.put("center", Map.of("lat", centerLat, "lng", centerLng));
        }
        
        return cityData;
    }

    /**
     * Get nearby intersections
     */
    public List<Map<String, Object>> getNearbyIntersections(Long id, double radius) {
        Intersection intersection = intersectionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Intersection not found with id: " + id));
        
        List<Intersection> nearby = intersectionRepository.findNearby(
            intersection.getLatitude(), 
            intersection.getLongitude(), 
            radius
        );
        
        // Remove the intersection itself from results
        return nearby.stream()
            .filter(i -> !i.getId().equals(id))
            .map(this::convertToMapData)
            .collect(Collectors.toList());
    }

    /**
     * Get intersections along a route
     */
    public List<Map<String, Object>> getIntersectionsAlongRoute(String latsStr, String lngsStr, double buffer) {
        String[] latArray = latsStr.split(",");
        String[] lngArray = lngsStr.split(",");
        
        if (latArray.length != lngArray.length) {
            throw new IllegalArgumentException("Latitude and longitude arrays must have the same length");
        }
        
        Set<Intersection> routeIntersections = new HashSet<>();
        
        // For each waypoint, find intersections within buffer radius
        for (int i = 0; i < latArray.length; i++) {
            BigDecimal lat = new BigDecimal(latArray[i].trim());
            BigDecimal lng = new BigDecimal(lngArray[i].trim());
            
            List<Intersection> nearby = intersectionRepository.findNearby(lat, lng, buffer);
            routeIntersections.addAll(nearby);
        }
        
        return routeIntersections.stream()
            .map(this::convertToMapData)
            .collect(Collectors.toList());
    }

    /**
     * Get heatmap data for traffic intensity
     */
    public Map<String, Object> getHeatmapData(String city, int days) {
        Map<String, Object> heatmapData = new HashMap<>();
        
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);
        
        List<Intersection> intersections;
        if (city != null && !city.isEmpty()) {
            intersections = intersectionRepository.findByCity(city);
        } else {
            intersections = intersectionRepository.findAll();
        }
        
        List<Map<String, Object>> heatmapPoints = new ArrayList<>();
        
        for (Intersection intersection : intersections) {
            Long totalVehicles = metricRepository.getTotalVehicleCount(
                intersection.getId(), startDate, endDate);
            
            if (totalVehicles != null && totalVehicles > 0) {
                Map<String, Object> point = new HashMap<>();
                point.put("lat", intersection.getLatitude());
                point.put("lng", intersection.getLongitude());
                point.put("intensity", totalVehicles);
                point.put("intersectionId", intersection.getId());
                point.put("intersectionName", intersection.getName());
                
                heatmapPoints.add(point);
            }
        }
        
        heatmapData.put("points", heatmapPoints);
        heatmapData.put("city", city != null ? city : "All Cities");
        heatmapData.put("days", days);
        heatmapData.put("dateRange", Map.of("start", startDate, "end", endDate));
        
        return heatmapData;
    }

    /**
     * Get GeoJSON format data
     */
    public Map<String, Object> getGeoJsonData(String city) {
        List<Intersection> intersections;
        if (city != null && !city.isEmpty()) {
            intersections = intersectionRepository.findByCity(city);
        } else {
            intersections = intersectionRepository.findAll();
        }
        
        Map<String, Object> geoJson = new HashMap<>();
        geoJson.put("type", "FeatureCollection");
        
        List<Map<String, Object>> features = intersections.stream()
            .map(this::convertToGeoJsonFeature)
            .collect(Collectors.toList());
        
        geoJson.put("features", features);
        
        return geoJson;
    }

    /**
     * Convert intersection to map data
     */
    private Map<String, Object> convertToMapData(Intersection intersection) {
        Map<String, Object> mapData = new HashMap<>();
        
        mapData.put("id", intersection.getId());
        mapData.put("name", intersection.getName());
        mapData.put("code", intersection.getCode());
        mapData.put("lat", intersection.getLatitude());
        mapData.put("lng", intersection.getLongitude());
        mapData.put("city", intersection.getCity());
        mapData.put("district", intersection.getDistrict());
        mapData.put("address", intersection.getAddress());
        mapData.put("type", intersection.getIntersectionType().name());
        mapData.put("status", intersection.getStatus().name());
        
        // Add icon based on type and status
        mapData.put("icon", determineIcon(intersection));
        mapData.put("color", determineColor(intersection));
        
        return mapData;
    }

    /**
     * Convert intersection to GeoJSON feature
     */
    private Map<String, Object> convertToGeoJsonFeature(Intersection intersection) {
        Map<String, Object> feature = new HashMap<>();
        feature.put("type", "Feature");
        
        // Geometry
        Map<String, Object> geometry = new HashMap<>();
        geometry.put("type", "Point");
        geometry.put("coordinates", Arrays.asList(
            intersection.getLongitude(), 
            intersection.getLatitude()
        ));
        feature.put("geometry", geometry);
        
        // Properties
        Map<String, Object> properties = new HashMap<>();
        properties.put("id", intersection.getId());
        properties.put("name", intersection.getName());
        properties.put("code", intersection.getCode());
        properties.put("city", intersection.getCity());
        properties.put("district", intersection.getDistrict());
        properties.put("type", intersection.getIntersectionType().name());
        properties.put("status", intersection.getStatus().name());
        feature.put("properties", properties);
        
        return feature;
    }

    /**
     * Calculate cluster radius based on zoom level
     */
    private double calculateClusterRadius(int zoom) {
        // Simple clustering algorithm
        // Higher zoom = smaller radius (more detail)
        if (zoom >= 15) return 0.5;  // 500m
        if (zoom >= 12) return 1.0;  // 1km
        if (zoom >= 10) return 2.0;  // 2km
        if (zoom >= 8) return 5.0;   // 5km
        return 10.0;  // 10km
    }

    /**
     * Create clusters from intersections
     */
    private List<Map<String, Object>> createClusters(List<Intersection> intersections, double radius) {
        List<Map<String, Object>> clusters = new ArrayList<>();
        Set<Long> processed = new HashSet<>();
        
        for (Intersection intersection : intersections) {
            if (processed.contains(intersection.getId())) {
                continue;
            }
            
            // Find nearby intersections to cluster
            List<Intersection> nearby = intersectionRepository.findNearby(
                intersection.getLatitude(), 
                intersection.getLongitude(), 
                radius
            );
            
            // Create cluster
            Map<String, Object> cluster = new HashMap<>();
            cluster.put("lat", intersection.getLatitude());
            cluster.put("lng", intersection.getLongitude());
            cluster.put("count", nearby.size());
            
            if (nearby.size() == 1) {
                // Single intersection, not a cluster
                cluster.put("type", "single");
                cluster.put("intersection", convertToMapData(intersection));
            } else {
                // Multiple intersections, create cluster
                cluster.put("type", "cluster");
                cluster.put("intersections", nearby.stream()
                    .map(this::convertToMapData)
                    .collect(Collectors.toList()));
            }
            
            clusters.add(cluster);
            
            // Mark all nearby intersections as processed
            nearby.forEach(i -> processed.add(i.getId()));
        }
        
        return clusters;
    }

    /**
     * Determine icon based on intersection type
     */
    private String determineIcon(Intersection intersection) {
        return switch (intersection.getIntersectionType()) {
            case TRAFFIC_LIGHT -> "traffic-light";
            case ROUNDABOUT -> "roundabout";
            case CROSSROAD -> "crossroad";
            case PEDESTRIAN_CROSSING -> "pedestrian";
        };
    }

    /**
     * Determine color based on intersection status
     */
    private String determineColor(Intersection intersection) {
        return switch (intersection.getStatus()) {
            case ACTIVE -> "green";
            case MAINTENANCE -> "yellow";
            case INACTIVE -> "red";
            case UNDER_CONSTRUCTION -> "orange";
        };
    }
}
