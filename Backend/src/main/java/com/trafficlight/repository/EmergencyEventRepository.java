package com.trafficlight.repository;

import com.trafficlight.entity.EmergencyEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmergencyEventRepository extends JpaRepository<EmergencyEvent, Long> {

    List<EmergencyEvent> findByEmergencyVehicleIdOrderByCreatedAtDesc(Long emergencyVehicleId);

    List<EmergencyEvent> findByIntersectionIdOrderByCreatedAtDesc(Long intersectionId);

    List<EmergencyEvent> findByEventType(EmergencyEvent.EventType eventType);

    @Query("SELECT e FROM EmergencyEvent e WHERE e.createdAt BETWEEN :start AND :end ORDER BY e.createdAt DESC")
    List<EmergencyEvent> findByDateRange(LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(e) FROM EmergencyEvent e WHERE e.eventType = 'EMERGENCY_DETECTED' AND e.createdAt >= :since")
    Long countEmergenciesSince(LocalDateTime since);
}