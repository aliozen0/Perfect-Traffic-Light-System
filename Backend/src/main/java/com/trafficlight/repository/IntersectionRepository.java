package com.trafficlight.repository;

import com.trafficlight.entity.Intersection;
import com.trafficlight.entity.Intersection.IntersectionStatus;
import com.trafficlight.entity.Intersection.IntersectionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * HAFTA 2 - Repository Pattern
 * IntersectionRepository - Custom queries for Intersection entity
 * 
 * Provides CRUD operations and custom query methods for intersection management.
 */
@Repository
public interface IntersectionRepository extends JpaRepository<Intersection, Long> {

    // ==========================================
    // Basic CRUD Operations (Inherited from JpaRepository)
    // ==========================================
    // - save(Intersection) - Create/Update
    // - findById(Long) - Read by ID
    // - findAll() - Read all
    // - findAll(Pageable) - Read with pagination
    // - deleteById(Long) - Delete by ID
    // - count() - Count all

    // ==========================================
    // Custom Query Methods - findByCity
    // ==========================================
    
    /**
     * Find all intersections in a specific city
     * @param city City name
     * @return List of intersections
     */
    List<Intersection> findByCity(String city);

    /**
     * Find intersections in a city with pagination
     * @param city City name
     * @param pageable Pagination parameters
     * @return Page of intersections
     */
    Page<Intersection> findByCity(String city, Pageable pageable);

    /**
     * Find intersections in a city and district
     * @param city City name
     * @param district District name
     * @return List of intersections
     */
    List<Intersection> findByCityAndDistrict(String city, String district);

    // ==========================================
    // Custom Query Methods - findByStatus
    // ==========================================
    
    /**
     * Find all intersections by status
     * @param status Intersection status
     * @return List of intersections
     */
    List<Intersection> findByStatus(IntersectionStatus status);

    /**
     * Find intersections by status with pagination
     * @param status Intersection status
     * @param pageable Pagination parameters
     * @return Page of intersections
     */
    Page<Intersection> findByStatus(IntersectionStatus status, Pageable pageable);

    /**
     * Find intersections by city and status
     * @param city City name
     * @param status Intersection status
     * @return List of intersections
     */
    List<Intersection> findByCityAndStatus(String city, IntersectionStatus status);

    /**
     * Find intersections by city and status with pagination
     * @param city City name
     * @param status Intersection status
     * @param pageable Pagination parameters
     * @return Page of intersections
     */
    Page<Intersection> findByCityAndStatus(String city, IntersectionStatus status, Pageable pageable);

    // ==========================================
    // Custom Query Methods - findByType
    // ==========================================
    
    /**
     * Find all intersections by type
     * @param type Intersection type
     * @return List of intersections
     */
    List<Intersection> findByIntersectionType(IntersectionType type);

    /**
     * Find intersections by type with pagination
     * @param type Intersection type
     * @param pageable Pagination parameters
     * @return Page of intersections
     */
    Page<Intersection> findByIntersectionType(IntersectionType type, Pageable pageable);

    // ==========================================
    // Custom Query Methods - findByCode
    // ==========================================
    
    /**
     * Find intersection by unique code
     * @param code Intersection code
     * @return Optional intersection
     */
    Optional<Intersection> findByCode(String code);

    /**
     * Check if intersection code exists
     * @param code Intersection code
     * @return true if exists
     */
    boolean existsByCode(String code);

    // ==========================================
    // Custom Query Methods - findNearby (HAFTA 4 Optimization)
    // ==========================================
    
    /**
     * Find intersections nearby a location (within radius in kilometers)
     * Using Haversine formula for distance calculation
     * 
     * @param latitude Latitude of center point
     * @param longitude Longitude of center point
     * @param radiusKm Radius in kilometers
     * @return List of nearby intersections
     */
    @Query(value = """
        SELECT i.* FROM intersections i
        WHERE (
            6371 * acos(
                cos(radians(:latitude)) 
                * cos(radians(CAST(i.latitude AS DOUBLE PRECISION))) 
                * cos(radians(CAST(i.longitude AS DOUBLE PRECISION)) - radians(:longitude)) 
                + sin(radians(:latitude)) 
                * sin(radians(CAST(i.latitude AS DOUBLE PRECISION)))
            )
        ) <= :radiusKm
        ORDER BY (
            6371 * acos(
                cos(radians(:latitude)) 
                * cos(radians(CAST(i.latitude AS DOUBLE PRECISION))) 
                * cos(radians(CAST(i.longitude AS DOUBLE PRECISION)) - radians(:longitude)) 
                + sin(radians(:latitude)) 
                * sin(radians(CAST(i.latitude AS DOUBLE PRECISION)))
            )
        ) ASC
        """, nativeQuery = true)
    List<Intersection> findNearby(
        @Param("latitude") BigDecimal latitude, 
        @Param("longitude") BigDecimal longitude, 
        @Param("radiusKm") double radiusKm
    );

    /**
     * Find nearby intersections with specific status
     * @param latitude Latitude of center point
     * @param longitude Longitude of center point
     * @param radiusKm Radius in kilometers
     * @param status Intersection status
     * @return List of nearby intersections
     */
    @Query(value = """
        SELECT i.* FROM intersections i
        WHERE i.status = :status
        AND (
            6371 * acos(
                cos(radians(:latitude)) 
                * cos(radians(CAST(i.latitude AS DOUBLE PRECISION))) 
                * cos(radians(CAST(i.longitude AS DOUBLE PRECISION)) - radians(:longitude)) 
                + sin(radians(:latitude)) 
                * sin(radians(CAST(i.latitude AS DOUBLE PRECISION)))
            )
        ) <= :radiusKm
        ORDER BY (
            6371 * acos(
                cos(radians(:latitude)) 
                * cos(radians(CAST(i.latitude AS DOUBLE PRECISION))) 
                * cos(radians(CAST(i.longitude AS DOUBLE PRECISION)) - radians(:longitude)) 
                + sin(radians(:latitude)) 
                * sin(radians(CAST(i.latitude AS DOUBLE PRECISION)))
            )
        ) ASC
        """, nativeQuery = true)
    List<Intersection> findNearbyByStatus(
        @Param("latitude") BigDecimal latitude, 
        @Param("longitude") BigDecimal longitude, 
        @Param("radiusKm") double radiusKm,
        @Param("status") String status
    );

    // ==========================================
    // Custom Query Methods - Maintenance Queries
    // ==========================================
    
    /**
     * Find intersections requiring maintenance (next maintenance date is near or passed)
     * @param currentDate Current date
     * @param daysAhead Days to look ahead
     * @return List of intersections requiring maintenance
     */
    @Query("SELECT i FROM Intersection i WHERE " +
           "i.nextMaintenanceDate <= :checkDate AND i.status = 'ACTIVE'")
    List<Intersection> findRequiringMaintenance(
        @Param("checkDate") LocalDate checkDate
    );

    /**
     * Find intersections in maintenance status
     * @return List of intersections in maintenance
     */
    @Query("SELECT i FROM Intersection i WHERE i.status = 'MAINTENANCE'")
    List<Intersection> findInMaintenance();

    // ==========================================
    // Custom Query Methods - Statistics
    // ==========================================
    
    /**
     * Count intersections by city
     * @param city City name
     * @return Count of intersections
     */
    long countByCity(String city);

    /**
     * Count intersections by status
     * @param status Intersection status
     * @return Count of intersections
     */
    long countByStatus(IntersectionStatus status);

    /**
     * Count intersections by type
     * @param type Intersection type
     * @return Count of intersections
     */
    long countByIntersectionType(IntersectionType type);

    /**
     * Count intersections by city and status
     * @param city City name
     * @param status Intersection status
     * @return Count of intersections
     */
    long countByCityAndStatus(String city, IntersectionStatus status);

    /**
     * Get city statistics
     * @return List of city statistics
     */
    @Query("SELECT i.city, COUNT(i) as count FROM Intersection i GROUP BY i.city ORDER BY count DESC")
    List<Object[]> getCityStatistics();

    // ==========================================
    // Custom Query Methods - Search
    // ==========================================
    
    /**
     * Search intersections by name or code
     * @param searchTerm Search term
     * @param pageable Pagination parameters
     * @return Page of intersections
     */
    @Query("SELECT i FROM Intersection i WHERE " +
           "LOWER(i.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(i.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(i.address) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Intersection> searchIntersections(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find intersections with specific features
     * @param hasVehicleDetection Has vehicle detection
     * @param hasPedestrianCrossing Has pedestrian crossing
     * @param hasEmergencyOverride Has emergency override
     * @return List of intersections
     */
    @Query("SELECT i FROM Intersection i WHERE " +
           "(:hasVehicleDetection IS NULL OR i.hasVehicleDetection = :hasVehicleDetection) AND " +
           "(:hasPedestrianCrossing IS NULL OR i.hasPedestrianCrossing = :hasPedestrianCrossing) AND " +
           "(:hasEmergencyOverride IS NULL OR i.hasEmergencyOverride = :hasEmergencyOverride)")
    List<Intersection> findByFeatures(
        @Param("hasVehicleDetection") Boolean hasVehicleDetection,
        @Param("hasPedestrianCrossing") Boolean hasPedestrianCrossing,
        @Param("hasEmergencyOverride") Boolean hasEmergencyOverride
    );
}

