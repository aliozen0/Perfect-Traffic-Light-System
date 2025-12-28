package com.trafficlight.repository;

import com.trafficlight.entity.IntersectionConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * HAFTA 2 - Repository Pattern
 * IntersectionConfigRepository - Custom queries for configuration management
 */
@Repository
public interface IntersectionConfigRepository extends JpaRepository<IntersectionConfig, Long> {

    /**
     * Find all configurations for a specific intersection
     * @param intersectionId Intersection ID
     * @return List of configurations
     */
    List<IntersectionConfig> findByIntersectionId(Long intersectionId);

    /**
     * Find active configuration for an intersection
     * @param intersectionId Intersection ID
     * @return Optional active configuration
     */
    @Query("SELECT c FROM IntersectionConfig c WHERE " +
           "c.intersection.id = :intersectionId AND c.isActive = true " +
           "ORDER BY c.createdAt DESC")
    Optional<IntersectionConfig> findActiveByIntersectionId(@Param("intersectionId") Long intersectionId);

    /**
     * Find configurations with adaptive timing enabled
     * @return List of configurations
     */
    List<IntersectionConfig> findByAdaptiveTimingEnabledTrue();

    /**
     * Find configurations with peak hour mode enabled
     * @return List of configurations
     */
    List<IntersectionConfig> findByPeakHourModeEnabledTrue();

    /**
     * Find configurations with night mode enabled
     * @return List of configurations
     */
    List<IntersectionConfig> findByNightModeEnabledTrue();

    /**
     * Find configurations with vehicle detection enabled
     * @return List of configurations
     */
    List<IntersectionConfig> findByVehicleDetectionEnabledTrue();

    /**
     * Find configurations with emergency vehicle priority
     * @return List of configurations
     */
    List<IntersectionConfig> findByEmergencyVehiclePriorityTrue();

    /**
     * Find configurations with coordination enabled
     * @return List of configurations
     */
    List<IntersectionConfig> findByCoordinationEnabledTrue();

    /**
     * Find configurations effective on a specific date
     * @param date Effective date
     * @return List of configurations
     */
    @Query("SELECT c FROM IntersectionConfig c WHERE " +
           "c.effectiveFrom <= :date AND (c.effectiveUntil IS NULL OR c.effectiveUntil >= :date)")
    List<IntersectionConfig> findEffectiveOnDate(@Param("date") LocalDate date);

    /**
     * Find configurations by version
     * @param configVersion Configuration version
     * @return List of configurations
     */
    List<IntersectionConfig> findByConfigVersion(String configVersion);

    /**
     * Count active configurations
     * @return Count of active configurations
     */
    long countByIsActiveTrue();

    /**
     * Delete inactive configurations older than specified date
     * @param date Cut-off date
     */
    @Query("DELETE FROM IntersectionConfig c WHERE c.isActive = false AND c.updatedAt < :date")
    void deleteInactiveOlderThan(@Param("date") LocalDate date);
}

