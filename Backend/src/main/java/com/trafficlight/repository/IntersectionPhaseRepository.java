package com.trafficlight.repository;

import com.trafficlight.entity.IntersectionPhase;
import com.trafficlight.entity.IntersectionPhase.PhaseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * HAFTA 2 - Repository Pattern
 * IntersectionPhaseRepository - Phase management and sequencing
 * 
 * getPhases(intersectionId) support
 */
@Repository
public interface IntersectionPhaseRepository extends JpaRepository<IntersectionPhase, Long> {

    // ==========================================
    // Basic Phase Queries - getPhases(intersectionId)
    // ==========================================
    
    /**
     * Get all phases for a specific intersection
     * @param intersectionId Intersection ID
     * @return List of phases
     */
    List<IntersectionPhase> findByIntersectionId(Long intersectionId);

    /**
     * Get all phases for intersection ordered by sequence
     * @param intersectionId Intersection ID
     * @return List of phases in sequence order
     */
    @Query("SELECT p FROM IntersectionPhase p WHERE " +
           "p.intersection.id = :intersectionId " +
           "ORDER BY p.sequenceOrder ASC")
    List<IntersectionPhase> findByIntersectionIdOrderBySequenceOrder(@Param("intersectionId") Long intersectionId);

    /**
     * Get active phases for intersection
     * @param intersectionId Intersection ID
     * @return List of active phases
     */
    @Query("SELECT p FROM IntersectionPhase p WHERE " +
           "p.intersection.id = :intersectionId AND p.isActive = true " +
           "ORDER BY p.sequenceOrder ASC")
    List<IntersectionPhase> findActivePhasesByIntersectionId(@Param("intersectionId") Long intersectionId);

    /**
     * Find phase by intersection ID and phase number
     * @param intersectionId Intersection ID
     * @param phaseNumber Phase number
     * @return Optional phase
     */
    Optional<IntersectionPhase> findByIntersectionIdAndPhaseNumber(Long intersectionId, Integer phaseNumber);

    // ==========================================
    // Phase Type Queries
    // ==========================================
    
    /**
     * Find phases by type
     * @param intersectionId Intersection ID
     * @param phaseType Phase type
     * @return List of phases
     */
    List<IntersectionPhase> findByIntersectionIdAndPhaseType(Long intersectionId, PhaseType phaseType);

    /**
     * Find pedestrian phases for intersection
     * @param intersectionId Intersection ID
     * @return List of pedestrian phases
     */
    @Query("SELECT p FROM IntersectionPhase p WHERE " +
           "p.intersection.id = :intersectionId AND p.phaseType = 'PEDESTRIAN' " +
           "ORDER BY p.sequenceOrder ASC")
    List<IntersectionPhase> findPedestrianPhases(@Param("intersectionId") Long intersectionId);

    /**
     * Find vehicle phases for intersection
     * @param intersectionId Intersection ID
     * @return List of vehicle phases
     */
    @Query("SELECT p FROM IntersectionPhase p WHERE " +
           "p.intersection.id = :intersectionId AND p.phaseType = 'VEHICLE' " +
           "ORDER BY p.sequenceOrder ASC")
    List<IntersectionPhase> findVehiclePhases(@Param("intersectionId") Long intersectionId);

    // ==========================================
    // Phase Priority and Sequence Queries
    // ==========================================
    
    /**
     * Find phases by priority level
     * @param intersectionId Intersection ID
     * @param priorityLevel Priority level
     * @return List of phases
     */
    List<IntersectionPhase> findByIntersectionIdAndPriorityLevel(Long intersectionId, Integer priorityLevel);

    /**
     * Find high priority phases (priority >= 7)
     * @param intersectionId Intersection ID
     * @return List of high priority phases
     */
    @Query("SELECT p FROM IntersectionPhase p WHERE " +
           "p.intersection.id = :intersectionId AND p.priorityLevel >= 7 " +
           "ORDER BY p.priorityLevel DESC, p.sequenceOrder ASC")
    List<IntersectionPhase> findHighPriorityPhases(@Param("intersectionId") Long intersectionId);

    /**
     * Find protected phases
     * @param intersectionId Intersection ID
     * @return List of protected phases
     */
    List<IntersectionPhase> findByIntersectionIdAndIsProtectedTrue(Long intersectionId);

    /**
     * Find permissive phases
     * @param intersectionId Intersection ID
     * @return List of permissive phases
     */
    List<IntersectionPhase> findByIntersectionIdAndIsPermissiveTrue(Long intersectionId);

    /**
     * Find skippable phases
     * @param intersectionId Intersection ID
     * @return List of skippable phases
     */
    List<IntersectionPhase> findByIntersectionIdAndCanSkipTrue(Long intersectionId);

    // ==========================================
    // Phase Timing Queries
    // ==========================================
    
    /**
     * Get next phase in sequence
     * @param intersectionId Intersection ID
     * @param currentSequenceOrder Current sequence order
     * @return Optional next phase
     */
    @Query("SELECT p FROM IntersectionPhase p WHERE " +
           "p.intersection.id = :intersectionId AND " +
           "p.sequenceOrder > :currentSequenceOrder AND " +
           "p.isActive = true " +
           "ORDER BY p.sequenceOrder ASC LIMIT 1")
    Optional<IntersectionPhase> findNextPhase(
        @Param("intersectionId") Long intersectionId,
        @Param("currentSequenceOrder") Integer currentSequenceOrder
    );

    /**
     * Get first phase in sequence
     * @param intersectionId Intersection ID
     * @return Optional first phase
     */
    @Query("SELECT p FROM IntersectionPhase p WHERE " +
           "p.intersection.id = :intersectionId AND p.isActive = true " +
           "ORDER BY p.sequenceOrder ASC LIMIT 1")
    Optional<IntersectionPhase> findFirstPhase(@Param("intersectionId") Long intersectionId);

    /**
     * Calculate total cycle time for intersection
     * @param intersectionId Intersection ID
     * @return Total cycle time in seconds
     */
    @Query("SELECT SUM(p.defaultDuration) FROM IntersectionPhase p WHERE " +
           "p.intersection.id = :intersectionId AND p.isActive = true")
    Integer calculateTotalCycleTime(@Param("intersectionId") Long intersectionId);

    // ==========================================
    // Conflict Management Queries
    // ==========================================
    
    /**
     * Check if two phases conflict
     * @param phase1Id Phase 1 ID
     * @param phase2Number Phase 2 number
     * @return True if phases conflict
     */
    @Query(value = """
        SELECT CASE WHEN :phase2Number = ANY(p.conflicting_phases) THEN true ELSE false END
        FROM intersection_phases p
        WHERE p.id = :phase1Id
        """, nativeQuery = true)
    Boolean checkPhaseConflict(@Param("phase1Id") Long phase1Id, @Param("phase2Number") Integer phase2Number);

    /**
     * Check if two phases are compatible
     * @param phase1Id Phase 1 ID
     * @param phase2Number Phase 2 number
     * @return True if phases are compatible
     */
    @Query(value = """
        SELECT CASE WHEN :phase2Number = ANY(p.compatible_phases) THEN true ELSE false END
        FROM intersection_phases p
        WHERE p.id = :phase1Id
        """, nativeQuery = true)
    Boolean checkPhaseCompatibility(@Param("phase1Id") Long phase1Id, @Param("phase2Number") Integer phase2Number);

    // ==========================================
    // Statistics
    // ==========================================
    
    /**
     * Count phases for intersection
     * @param intersectionId Intersection ID
     * @return Count of phases
     */
    long countByIntersectionId(Long intersectionId);

    /**
     * Count active phases for intersection
     * @param intersectionId Intersection ID
     * @return Count of active phases
     */
    long countByIntersectionIdAndIsActiveTrue(Long intersectionId);

    /**
     * Check if phase number exists for intersection
     * @param intersectionId Intersection ID
     * @param phaseNumber Phase number
     * @return True if exists
     */
    boolean existsByIntersectionIdAndPhaseNumber(Long intersectionId, Integer phaseNumber);
}

