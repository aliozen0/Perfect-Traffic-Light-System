package com.trafficlight.repository;

import com.trafficlight.entity.IntersectionMetric;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * HAFTA 2 - Repository Pattern
 * IntersectionMetricRepository - Metric data collection and analytics
 * 
 * HAFTA 3 - Time-range filtering support
 */
@Repository
public interface IntersectionMetricRepository extends JpaRepository<IntersectionMetric, Long> {

    // ==========================================
    // Basic Metric Queries - getMetrics(intersectionId)
    // ==========================================
    
    /**
     * Get all metrics for a specific intersection
     * @param intersectionId Intersection ID
     * @return List of metrics
     */
    List<IntersectionMetric> findByIntersectionId(Long intersectionId);

    /**
     * Get metrics for a specific intersection with pagination
     * @param intersectionId Intersection ID
     * @param pageable Pagination parameters
     * @return Page of metrics
     */
    Page<IntersectionMetric> findByIntersectionId(Long intersectionId, Pageable pageable);

    /**
     * Get metric for specific intersection, date and hour
     * @param intersectionId Intersection ID
     * @param date Measurement date
     * @param hour Measurement hour
     * @return Optional metric
     */
    Optional<IntersectionMetric> findByIntersectionIdAndMeasurementDateAndMeasurementHour(
        Long intersectionId, LocalDate date, Integer hour
    );

    // ==========================================
    // Time-Range Filtering (HAFTA 3)
    // ==========================================
    
    /**
     * Get metrics for intersection within date range
     * @param intersectionId Intersection ID
     * @param startDate Start date
     * @param endDate End date
     * @return List of metrics
     */
    @Query("SELECT m FROM IntersectionMetric m WHERE " +
           "m.intersection.id = :intersectionId AND " +
           "m.measurementDate BETWEEN :startDate AND :endDate " +
           "ORDER BY m.measurementDate DESC, m.measurementHour DESC")
    List<IntersectionMetric> findByIntersectionIdAndDateRange(
        @Param("intersectionId") Long intersectionId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Get metrics for intersection within date range with pagination
     * @param intersectionId Intersection ID
     * @param startDate Start date
     * @param endDate End date
     * @param pageable Pagination parameters
     * @return Page of metrics
     */
    @Query("SELECT m FROM IntersectionMetric m WHERE " +
           "m.intersection.id = :intersectionId AND " +
           "m.measurementDate BETWEEN :startDate AND :endDate " +
           "ORDER BY m.measurementDate DESC, m.measurementHour DESC")
    Page<IntersectionMetric> findByIntersectionIdAndDateRange(
        @Param("intersectionId") Long intersectionId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        Pageable pageable
    );

    /**
     * Get metrics for specific date
     * @param date Measurement date
     * @return List of metrics
     */
    List<IntersectionMetric> findByMeasurementDate(LocalDate date);

    /**
     * Get metrics for specific date and hour
     * @param date Measurement date
     * @param hour Measurement hour
     * @return List of metrics
     */
    List<IntersectionMetric> findByMeasurementDateAndMeasurementHour(LocalDate date, Integer hour);

    // ==========================================
    // Aggregation and Analytics Queries
    // ==========================================
    
    /**
     * Get average wait time for intersection in date range
     * @param intersectionId Intersection ID
     * @param startDate Start date
     * @param endDate End date
     * @return Average wait time
     */
    @Query("SELECT AVG(m.averageWaitTime) FROM IntersectionMetric m WHERE " +
           "m.intersection.id = :intersectionId AND " +
           "m.measurementDate BETWEEN :startDate AND :endDate")
    Double getAverageWaitTime(
        @Param("intersectionId") Long intersectionId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Get total vehicle count for intersection in date range
     * @param intersectionId Intersection ID
     * @param startDate Start date
     * @param endDate End date
     * @return Total vehicle count
     */
    @Query("SELECT SUM(m.totalVehicleCount) FROM IntersectionMetric m WHERE " +
           "m.intersection.id = :intersectionId AND " +
           "m.measurementDate BETWEEN :startDate AND :endDate")
    Long getTotalVehicleCount(
        @Param("intersectionId") Long intersectionId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Get peak hour metrics (highest traffic volume)
     * @param intersectionId Intersection ID
     * @param date Measurement date
     * @return Metric with highest traffic
     */
    @Query("SELECT m FROM IntersectionMetric m WHERE " +
           "m.intersection.id = :intersectionId AND m.measurementDate = :date " +
           "ORDER BY m.totalVehicleCount DESC LIMIT 1")
    Optional<IntersectionMetric> findPeakHourMetric(
        @Param("intersectionId") Long intersectionId,
        @Param("date") LocalDate date
    );

    /**
     * Get metrics with accidents
     * @param intersectionId Intersection ID
     * @param startDate Start date
     * @param endDate End date
     * @return List of metrics with accidents
     */
    @Query("SELECT m FROM IntersectionMetric m WHERE " +
           "m.intersection.id = :intersectionId AND " +
           "m.measurementDate BETWEEN :startDate AND :endDate AND " +
           "m.accidentsCount > 0 " +
           "ORDER BY m.accidentsCount DESC")
    List<IntersectionMetric> findMetricsWithAccidents(
        @Param("intersectionId") Long intersectionId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Get metrics with violations
     * @param intersectionId Intersection ID
     * @param startDate Start date
     * @param endDate End date
     * @return List of metrics with violations
     */
    @Query("SELECT m FROM IntersectionMetric m WHERE " +
           "m.intersection.id = :intersectionId AND " +
           "m.measurementDate BETWEEN :startDate AND :endDate AND " +
           "(m.redLightViolations > 0 OR m.yellowLightViolations > 0 OR m.pedestrianViolations > 0) " +
           "ORDER BY (m.redLightViolations + m.yellowLightViolations + m.pedestrianViolations) DESC")
    List<IntersectionMetric> findMetricsWithViolations(
        @Param("intersectionId") Long intersectionId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Get average throughput for intersection
     * @param intersectionId Intersection ID
     * @param startDate Start date
     * @param endDate End date
     * @return Average throughput
     */
    @Query("SELECT AVG(m.throughput) FROM IntersectionMetric m WHERE " +
           "m.intersection.id = :intersectionId AND " +
           "m.measurementDate BETWEEN :startDate AND :endDate")
    Double getAverageThroughput(
        @Param("intersectionId") Long intersectionId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Get metrics with low data quality
     * @param threshold Data quality threshold (0.0 to 1.0)
     * @return List of metrics with low quality
     */
    @Query("SELECT m FROM IntersectionMetric m WHERE " +
           "m.dataQualityScore < :threshold " +
           "ORDER BY m.dataQualityScore ASC, m.measurementDate DESC")
    List<IntersectionMetric> findMetricsWithLowQuality(@Param("threshold") Double threshold);

    /**
     * Delete old metrics before a specific date
     * @param date Cut-off date
     */
    @Query("DELETE FROM IntersectionMetric m WHERE m.measurementDate < :date")
    void deleteMetricsOlderThan(@Param("date") LocalDate date);

    /**
     * Count metrics for intersection
     * @param intersectionId Intersection ID
     * @return Count of metrics
     */
    long countByIntersectionId(Long intersectionId);

    /**
     * Get latest metrics for all intersections
     * @return List of latest metrics
     */
    @Query(value = """
        SELECT DISTINCT ON (m.intersection_id) m.*
        FROM intersection_metrics m
        ORDER BY m.intersection_id, m.measurement_date DESC, m.measurement_hour DESC
        """, nativeQuery = true)
    List<IntersectionMetric> findLatestMetricsForAllIntersections();
}

