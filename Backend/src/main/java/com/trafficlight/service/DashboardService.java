package com.trafficlight.service;

import com.trafficlight.entity.Intersection;
import com.trafficlight.entity.Intersection.IntersectionStatus;
import com.trafficlight.entity.Intersection.IntersectionType;
import com.trafficlight.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * HAFTA 6 - Dashboard Service
 * Business logic for dashboard data aggregation and statistics
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final IntersectionRepository intersectionRepository;
    private final IntersectionMetricRepository metricRepository;
    private final IntersectionConfigRepository configRepository;
    private final IntersectionPhaseRepository phaseRepository;

    /**
     * Get overall dashboard summary
     */
    public Map<String, Object> getDashboardSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        // Total counts
        long totalIntersections = intersectionRepository.count();
        long totalMetrics = metricRepository.count();
        long totalConfigs = configRepository.count();
        long totalPhases = phaseRepository.count();
        
        // Status breakdown
        long activeIntersections = intersectionRepository.countByStatus(IntersectionStatus.ACTIVE);
        long maintenanceIntersections = intersectionRepository.countByStatus(IntersectionStatus.MAINTENANCE);
        long inactiveIntersections = intersectionRepository.countByStatus(IntersectionStatus.INACTIVE);
        
        // Type breakdown
        long trafficLights = intersectionRepository.countByIntersectionType(IntersectionType.TRAFFIC_LIGHT);
        long roundabouts = intersectionRepository.countByIntersectionType(IntersectionType.ROUNDABOUT);
        long crossroads = intersectionRepository.countByIntersectionType(IntersectionType.CROSSROAD);
        
        summary.put("totalIntersections", totalIntersections);
        summary.put("totalMetrics", totalMetrics);
        summary.put("totalConfigs", totalConfigs);
        summary.put("totalPhases", totalPhases);
        
        summary.put("activeIntersections", activeIntersections);
        summary.put("maintenanceIntersections", maintenanceIntersections);
        summary.put("inactiveIntersections", inactiveIntersections);
        
        summary.put("trafficLights", trafficLights);
        summary.put("roundabouts", roundabouts);
        summary.put("crossroads", crossroads);
        
        // Calculate percentages
        if (totalIntersections > 0) {
            summary.put("activePercentage", (double) activeIntersections / totalIntersections * 100);
            summary.put("maintenancePercentage", (double) maintenanceIntersections / totalIntersections * 100);
        }
        
        return summary;
    }

    /**
     * Get city statistics
     */
    public Map<String, Object> getCityStatistics() {
        Map<String, Object> cityStats = new HashMap<>();
        
        List<Object[]> cityData = intersectionRepository.getCityStatistics();
        List<Map<String, Object>> cities = new ArrayList<>();
        
        for (Object[] row : cityData) {
            Map<String, Object> cityInfo = new HashMap<>();
            cityInfo.put("city", row[0]);
            cityInfo.put("count", row[1]);
            
            String city = (String) row[0];
            cityInfo.put("activeCount", intersectionRepository.countByCityAndStatus(city, IntersectionStatus.ACTIVE));
            cityInfo.put("maintenanceCount", intersectionRepository.countByCityAndStatus(city, IntersectionStatus.MAINTENANCE));
            
            cities.add(cityInfo);
        }
        
        cityStats.put("cities", cities);
        cityStats.put("totalCities", cities.size());
        
        return cityStats;
    }

    /**
     * Get performance metrics
     */
    public Map<String, Object> getPerformanceMetrics(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> performance = new HashMap<>();
        
        List<Intersection> intersections = intersectionRepository.findAll();
        
        // Aggregate metrics across all intersections
        double totalAvgWaitTime = 0;
        long totalVehicles = 0;
        double totalAvgThroughput = 0;
        int count = 0;
        
        for (Intersection intersection : intersections) {
            Double avgWaitTime = metricRepository.getAverageWaitTime(
                intersection.getId(), startDate, endDate);
            Long vehicleCount = metricRepository.getTotalVehicleCount(
                intersection.getId(), startDate, endDate);
            Double avgThroughput = metricRepository.getAverageThroughput(
                intersection.getId(), startDate, endDate);
            
            if (avgWaitTime != null) {
                totalAvgWaitTime += avgWaitTime;
                count++;
            }
            if (vehicleCount != null) {
                totalVehicles += vehicleCount;
            }
            if (avgThroughput != null) {
                totalAvgThroughput += avgThroughput;
            }
        }
        
        performance.put("averageWaitTime", count > 0 ? totalAvgWaitTime / count : 0);
        performance.put("totalVehicles", totalVehicles);
        performance.put("averageThroughput", count > 0 ? totalAvgThroughput / count : 0);
        performance.put("intersectionsAnalyzed", count);
        performance.put("dateRange", Map.of("start", startDate, "end", endDate));
        
        return performance;
    }

    /**
     * Get system alerts
     */
    public Map<String, Object> getSystemAlerts() {
        Map<String, Object> alerts = new HashMap<>();
        
        List<Map<String, Object>> alertList = new ArrayList<>();
        
        // Check for intersections requiring maintenance
        LocalDate today = LocalDate.now();
        LocalDate oneWeekFromNow = today.plusDays(7);
        List<Intersection> maintenanceRequired = intersectionRepository.findRequiringMaintenance(oneWeekFromNow);
        
        if (!maintenanceRequired.isEmpty()) {
            Map<String, Object> alert = new HashMap<>();
            alert.put("type", "MAINTENANCE_REQUIRED");
            alert.put("severity", "WARNING");
            alert.put("message", maintenanceRequired.size() + " intersections require maintenance soon");
            alert.put("count", maintenanceRequired.size());
            alert.put("details", maintenanceRequired.stream()
                .map(i -> Map.of(
                    "id", i.getId(),
                    "name", i.getName(),
                    "nextMaintenanceDate", i.getNextMaintenanceDate()
                ))
                .collect(Collectors.toList()));
            alertList.add(alert);
        }
        
        // Check for intersections in maintenance status
        List<Intersection> inMaintenance = intersectionRepository.findInMaintenance();
        if (!inMaintenance.isEmpty()) {
            Map<String, Object> alert = new HashMap<>();
            alert.put("type", "IN_MAINTENANCE");
            alert.put("severity", "INFO");
            alert.put("message", inMaintenance.size() + " intersections currently in maintenance");
            alert.put("count", inMaintenance.size());
            alertList.add(alert);
        }
        
        // Check for low data quality metrics
        List<com.trafficlight.entity.IntersectionMetric> lowQuality = metricRepository.findMetricsWithLowQuality(0.70);
        if (!lowQuality.isEmpty()) {
            Map<String, Object> alert = new HashMap<>();
            alert.put("type", "LOW_DATA_QUALITY");
            alert.put("severity", "WARNING");
            alert.put("message", lowQuality.size() + " metrics with low data quality");
            alert.put("count", lowQuality.size());
            alertList.add(alert);
        }
        
        alerts.put("alerts", alertList);
        alerts.put("totalAlerts", alertList.size());
        alerts.put("hasWarnings", alertList.stream().anyMatch(a -> "WARNING".equals(a.get("severity"))));
        
        return alerts;
    }

    /**
     * Get traffic trends
     */
    public Map<String, Object> getTrafficTrends(String city, int days) {
        Map<String, Object> trends = new HashMap<>();
        
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);
        
        List<Intersection> intersections;
        if (city != null && !city.isEmpty()) {
            intersections = intersectionRepository.findByCity(city);
        } else {
            intersections = intersectionRepository.findAll();
        }
        
        // Calculate daily trends
        List<Map<String, Object>> dailyTrends = new ArrayList<>();
        
        for (int i = 0; i < days; i++) {
            LocalDate date = startDate.plusDays(i);
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date);
            
            long totalVehicles = 0;
            double totalWaitTime = 0;
            int count = 0;
            
            for (Intersection intersection : intersections) {
                Long vehicles = metricRepository.getTotalVehicleCount(intersection.getId(), date, date);
                Double waitTime = metricRepository.getAverageWaitTime(intersection.getId(), date, date);
                
                if (vehicles != null) totalVehicles += vehicles;
                if (waitTime != null) {
                    totalWaitTime += waitTime;
                    count++;
                }
            }
            
            dayData.put("totalVehicles", totalVehicles);
            dayData.put("averageWaitTime", count > 0 ? totalWaitTime / count : 0);
            
            dailyTrends.add(dayData);
        }
        
        trends.put("dailyTrends", dailyTrends);
        trends.put("city", city != null ? city : "All Cities");
        trends.put("days", days);
        trends.put("startDate", startDate);
        trends.put("endDate", endDate);
        
        return trends;
    }

    /**
     * Get status distribution
     */
    public Map<String, Object> getStatusDistribution() {
        Map<String, Object> distribution = new HashMap<>();
        
        List<Map<String, Object>> statusData = new ArrayList<>();
        
        for (IntersectionStatus status : IntersectionStatus.values()) {
            long count = intersectionRepository.countByStatus(status);
            Map<String, Object> statusInfo = new HashMap<>();
            statusInfo.put("status", status.name());
            statusInfo.put("count", count);
            statusData.add(statusInfo);
        }
        
        distribution.put("distribution", statusData);
        distribution.put("total", intersectionRepository.count());
        
        return distribution;
    }

    /**
     * Get type distribution
     */
    public Map<String, Object> getTypeDistribution() {
        Map<String, Object> distribution = new HashMap<>();
        
        List<Map<String, Object>> typeData = new ArrayList<>();
        
        for (IntersectionType type : IntersectionType.values()) {
            long count = intersectionRepository.countByIntersectionType(type);
            Map<String, Object> typeInfo = new HashMap<>();
            typeInfo.put("type", type.name());
            typeInfo.put("count", count);
            typeData.add(typeInfo);
        }
        
        distribution.put("distribution", typeData);
        distribution.put("total", intersectionRepository.count());
        
        return distribution;
    }

    /**
     * Get top performing intersections
     */
    public Map<String, Object> getTopPerformingIntersections(int limit, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new HashMap<>();
        
        List<Intersection> allIntersections = intersectionRepository.findAll();
        List<Map<String, Object>> performanceList = new ArrayList<>();
        
        for (Intersection intersection : allIntersections) {
            Double avgWaitTime = metricRepository.getAverageWaitTime(intersection.getId(), startDate, endDate);
            Double avgThroughput = metricRepository.getAverageThroughput(intersection.getId(), startDate, endDate);
            
            if (avgWaitTime != null && avgThroughput != null) {
                Map<String, Object> perfData = new HashMap<>();
                perfData.put("id", intersection.getId());
                perfData.put("name", intersection.getName());
                perfData.put("city", intersection.getCity());
                perfData.put("averageWaitTime", avgWaitTime);
                perfData.put("averageThroughput", avgThroughput);
                
                // Calculate performance score (lower wait time and higher throughput is better)
                double score = (avgThroughput / (avgWaitTime + 1)) * 100;
                perfData.put("performanceScore", score);
                
                performanceList.add(perfData);
            }
        }
        
        // Sort by performance score descending
        performanceList.sort((a, b) -> 
            Double.compare((Double) b.get("performanceScore"), (Double) a.get("performanceScore")));
        
        // Limit results
        List<Map<String, Object>> topPerforming = performanceList.stream()
            .limit(limit)
            .collect(Collectors.toList());
        
        result.put("topPerforming", topPerforming);
        result.put("limit", limit);
        result.put("dateRange", Map.of("start", startDate, "end", endDate));
        
        return result;
    }

    /**
     * Get incident statistics
     */
    public Map<String, Object> getIncidentStatistics(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> incidents = new HashMap<>();
        
        List<Intersection> intersections = intersectionRepository.findAll();
        
        int totalAccidents = 0;
        int totalRedLightViolations = 0;
        int totalYellowLightViolations = 0;
        int totalPedestrianViolations = 0;
        
        List<Map<String, Object>> intersectionIncidents = new ArrayList<>();
        
        for (Intersection intersection : intersections) {
            var accidents = metricRepository.findMetricsWithAccidents(intersection.getId(), startDate, endDate);
            var violations = metricRepository.findMetricsWithViolations(intersection.getId(), startDate, endDate);
            
            int accidentCount = accidents.stream().mapToInt(m -> m.getAccidentsCount()).sum();
            int redViolations = violations.stream().mapToInt(m -> m.getRedLightViolations()).sum();
            int yellowViolations = violations.stream().mapToInt(m -> m.getYellowLightViolations()).sum();
            int pedViolations = violations.stream().mapToInt(m -> m.getPedestrianViolations()).sum();
            
            totalAccidents += accidentCount;
            totalRedLightViolations += redViolations;
            totalYellowLightViolations += yellowViolations;
            totalPedestrianViolations += pedViolations;
            
            if (accidentCount > 0 || redViolations > 0 || yellowViolations > 0 || pedViolations > 0) {
                Map<String, Object> incidentData = new HashMap<>();
                incidentData.put("intersectionId", intersection.getId());
                incidentData.put("intersectionName", intersection.getName());
                incidentData.put("city", intersection.getCity());
                incidentData.put("accidents", accidentCount);
                incidentData.put("redLightViolations", redViolations);
                incidentData.put("yellowLightViolations", yellowViolations);
                incidentData.put("pedestrianViolations", pedViolations);
                incidentData.put("totalIncidents", accidentCount + redViolations + yellowViolations + pedViolations);
                
                intersectionIncidents.add(incidentData);
            }
        }
        
        // Sort by total incidents descending
        intersectionIncidents.sort((a, b) -> 
            Integer.compare((Integer) b.get("totalIncidents"), (Integer) a.get("totalIncidents")));
        
        incidents.put("totalAccidents", totalAccidents);
        incidents.put("totalRedLightViolations", totalRedLightViolations);
        incidents.put("totalYellowLightViolations", totalYellowLightViolations);
        incidents.put("totalPedestrianViolations", totalPedestrianViolations);
        incidents.put("totalViolations", totalRedLightViolations + totalYellowLightViolations + totalPedestrianViolations);
        incidents.put("intersectionIncidents", intersectionIncidents);
        incidents.put("dateRange", Map.of("start", startDate, "end", endDate));
        
        return incidents;
    }
}
