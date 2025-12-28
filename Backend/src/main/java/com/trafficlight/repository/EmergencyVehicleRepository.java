package com.trafficlight.repository;

import com.trafficlight.entity.EmergencyVehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmergencyVehicleRepository extends JpaRepository<EmergencyVehicle, Long> {

    List<EmergencyVehicle> findByStatus(EmergencyVehicle.EmergencyStatus status);

    Optional<EmergencyVehicle> findByVehicleId(String vehicleId);

    List<EmergencyVehicle> findByCurrentIntersectionId(Long intersectionId);

    @Query("SELECT e FROM EmergencyVehicle e WHERE e.status IN ('DETECTED', 'IN_PROGRESS') ORDER BY e.priorityLevel ASC, e.detectedAt ASC")
    List<EmergencyVehicle> findActiveEmergencies();

    @Query("SELECT e FROM EmergencyVehicle e WHERE e.detectedAt BETWEEN :start AND :end")
    List<EmergencyVehicle> findByDateRange(LocalDateTime start, LocalDateTime end);

    Long countByStatusAndDetectedAtAfter(EmergencyVehicle.EmergencyStatus status, LocalDateTime after);
}