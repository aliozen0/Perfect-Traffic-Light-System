package com.trafficlight.service;

import com.trafficlight.dto.MetricRequest;
import com.trafficlight.dto.MetricResponse;
import com.trafficlight.entity.Intersection;
import com.trafficlight.entity.IntersectionMetric;
import com.trafficlight.exception.BadRequestException;
import com.trafficlight.exception.ResourceNotFoundException;
import com.trafficlight.repository.IntersectionMetricRepository;
import com.trafficlight.repository.IntersectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * HAFTA 3 - Service Layer
 * MetricService - Metric data collection and analytics
 * 
 * Supports time-range filtering and aggregations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MetricService {

    private final IntersectionMetricRepository metricRepository;
    private final IntersectionRepository intersectionRepository;

    // ==========================================
    // Metric Endpoints (HAFTA 3)
    // ==========================================

    /**
     * Get metrics for intersection
     * GET /intersections/:id/metrics
     */
    public List<MetricResponse> getMetricsByIntersectionId(Long intersectionId) {
        log.info("Fetching metrics for intersection: {}", intersectionId);
        
        // Verify intersection exists
        if (!intersectionRepository.existsById(intersectionId)) {
            throw new ResourceNotFoundException("Intersection", intersectionId);
        }

        return metricRepository.findByIntersectionId(intersectionId).stream()
            .map(MetricResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Get metrics with pagination
     * GET /intersections/:id/metrics?page=1&limit=10
     */
    public Page<MetricResponse> getMetricsByIntersectionId(Long intersectionId, Pageable pageable) {
        log.info("Fetching metrics for intersection: {} with pagination", intersectionId);
        
        if (!intersectionRepository.existsById(intersectionId)) {
            throw new ResourceNotFoundException("Intersection", intersectionId);
        }

        return metricRepository.findByIntersectionId(intersectionId, pageable)
            .map(MetricResponse::fromEntity);
    }

    /**
     * Get metrics with time-range filtering
     * GET /intersections/:id/metrics?startDate=2024-01-01&endDate=2024-01-31
     */
    public List<MetricResponse> getMetricsByDateRange(
            Long intersectionId, 
            LocalDate startDate, 
            LocalDate endDate) {
        
        log.info("Fetching metrics for intersection: {} from {} to {}", 
            intersectionId, startDate, endDate);
        
        if (!intersectionRepository.existsById(intersectionId)) {
            throw new ResourceNotFoundException("Intersection", intersectionId);
        }

        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date must be before or equal to end date");
        }

        return metricRepository.findByIntersectionIdAndDateRange(intersectionId, startDate, endDate)
            .stream()
            .map(MetricResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Get metrics with time-range filtering and pagination
     */
    public Page<MetricResponse> getMetricsByDateRange(
            Long intersectionId, 
            LocalDate startDate, 
            LocalDate endDate,
            Pageable pageable) {
        
        log.info("Fetching metrics for intersection: {} from {} to {} with pagination", 
            intersectionId, startDate, endDate);
        
        if (!intersectionRepository.existsById(intersectionId)) {
            throw new ResourceNotFoundException("Intersection", intersectionId);
        }

        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date must be before or equal to end date");
        }

        return metricRepository.findByIntersectionIdAndDateRange(
            intersectionId, startDate, endDate, pageable)
            .map(MetricResponse::fromEntity);
    }

    /**
     * Create new metric
     * POST /intersections/:id/metrics
     */
    @Transactional
    public MetricResponse createMetric(MetricRequest request) {
        log.info("Creating metric for intersection: {}", request.getIntersectionId());
        
        Intersection intersection = intersectionRepository.findById(request.getIntersectionId())
            .orElseThrow(() -> new ResourceNotFoundException("Intersection", request.getIntersectionId()));

        // Check for duplicate metric (same intersection, date, hour)
        if (request.getMeasurementHour() != null) {
            metricRepository.findByIntersectionIdAndMeasurementDateAndMeasurementHour(
                request.getIntersectionId(), 
                request.getMeasurementDate(), 
                request.getMeasurementHour()
            ).ifPresent(existing -> {
                throw new BadRequestException("Metric already exists for this intersection, date and hour");
            });
        }

        IntersectionMetric metric = IntersectionMetric.builder()
            .intersection(intersection)
            .measurementDate(request.getMeasurementDate())
            .measurementHour(request.getMeasurementHour())
            .totalVehicleCount(request.getTotalVehicleCount() != null ? request.getTotalVehicleCount() : 0)
            .carCount(request.getCarCount() != null ? request.getCarCount() : 0)
            .truckCount(request.getTruckCount() != null ? request.getTruckCount() : 0)
            .busCount(request.getBusCount() != null ? request.getBusCount() : 0)
            .motorcycleCount(request.getMotorcycleCount() != null ? request.getMotorcycleCount() : 0)
            .bicycleCount(request.getBicycleCount() != null ? request.getBicycleCount() : 0)
            .pedestrianCount(request.getPedestrianCount() != null ? request.getPedestrianCount() : 0)
            .averageWaitTime(request.getAverageWaitTime())
            .maximumWaitTime(request.getMaximumWaitTime())
            .averageQueueLength(request.getAverageQueueLength())
            .maximumQueueLength(request.getMaximumQueueLength())
            .throughput(request.getThroughput())
            .greenTimeUtilization(request.getGreenTimeUtilization())
            .redLightViolations(request.getRedLightViolations() != null ? request.getRedLightViolations() : 0)
            .yellowLightViolations(request.getYellowLightViolations() != null ? request.getYellowLightViolations() : 0)
            .pedestrianViolations(request.getPedestrianViolations() != null ? request.getPedestrianViolations() : 0)
            .accidentsCount(request.getAccidentsCount() != null ? request.getAccidentsCount() : 0)
            .nearMissCount(request.getNearMissCount() != null ? request.getNearMissCount() : 0)
            .emergencyVehiclePassages(request.getEmergencyVehiclePassages() != null ? request.getEmergencyVehiclePassages() : 0)
            .systemUptimePercentage(request.getSystemUptimePercentage())
            .malfunctionCount(request.getMalfunctionCount() != null ? request.getMalfunctionCount() : 0)
            .manualOverrideCount(request.getManualOverrideCount() != null ? request.getManualOverrideCount() : 0)
            .estimatedCo2Emission(request.getEstimatedCo2Emission())
            .estimatedFuelConsumption(request.getEstimatedFuelConsumption())
            .dataQualityScore(request.getDataQualityScore())
            .notes(request.getNotes())
            .build();

        IntersectionMetric saved = metricRepository.save(metric);
        log.info("Metric created successfully with id: {}", saved.getId());
        
        return MetricResponse.fromEntity(saved);
    }

    /**
     * Get metric by ID
     * GET /metrics/:id
     */
    public MetricResponse getMetricById(Long id) {
        log.info("Fetching metric with id: {}", id);
        IntersectionMetric metric = metricRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Metric", id));
        return MetricResponse.fromEntity(metric);
    }

    /**
     * Delete metric
     * DELETE /metrics/:id
     */
    @Transactional
    public void deleteMetric(Long id) {
        log.info("Deleting metric with id: {}", id);
        
        if (!metricRepository.existsById(id)) {
            throw new ResourceNotFoundException("Metric", id);
        }

        metricRepository.deleteById(id);
        log.info("Metric deleted successfully with id: {}", id);
    }

    // ==========================================
    // Analytics and Aggregations
    // ==========================================

    /**
     * Get average wait time for intersection
     */
    public Double getAverageWaitTime(Long intersectionId, LocalDate startDate, LocalDate endDate) {
        log.info("Calculating average wait time for intersection: {}", intersectionId);
        
        if (!intersectionRepository.existsById(intersectionId)) {
            throw new ResourceNotFoundException("Intersection", intersectionId);
        }

        return metricRepository.getAverageWaitTime(intersectionId, startDate, endDate);
    }

    /**
     * Get total vehicle count for intersection
     */
    public Long getTotalVehicleCount(Long intersectionId, LocalDate startDate, LocalDate endDate) {
        log.info("Calculating total vehicle count for intersection: {}", intersectionId);
        
        if (!intersectionRepository.existsById(intersectionId)) {
            throw new ResourceNotFoundException("Intersection", intersectionId);
        }

        return metricRepository.getTotalVehicleCount(intersectionId, startDate, endDate);
    }

    /**
     * Get metrics with accidents
     */
    public List<MetricResponse> getMetricsWithAccidents(
            Long intersectionId, 
            LocalDate startDate, 
            LocalDate endDate) {
        
        log.info("Fetching metrics with accidents for intersection: {}", intersectionId);
        
        if (!intersectionRepository.existsById(intersectionId)) {
            throw new ResourceNotFoundException("Intersection", intersectionId);
        }

        return metricRepository.findMetricsWithAccidents(intersectionId, startDate, endDate)
            .stream()
            .map(MetricResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Get metrics with violations
     */
    public List<MetricResponse> getMetricsWithViolations(
            Long intersectionId, 
            LocalDate startDate, 
            LocalDate endDate) {
        
        log.info("Fetching metrics with violations for intersection: {}", intersectionId);
        
        if (!intersectionRepository.existsById(intersectionId)) {
            throw new ResourceNotFoundException("Intersection", intersectionId);
        }

        return metricRepository.findMetricsWithViolations(intersectionId, startDate, endDate)
            .stream()
            .map(MetricResponse::fromEntity)
            .collect(Collectors.toList());
    }
}

