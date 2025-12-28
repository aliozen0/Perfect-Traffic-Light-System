package com.trafficlight.repository;

import com.trafficlight.entity.TrafficSensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrafficSensorRepository extends JpaRepository<TrafficSensor, Long> {

    Optional<TrafficSensor> findBySensorId(String sensorId);

    List<TrafficSensor> findByIntersectionIdAndActiveTrueOrderByRecordedAtDesc(Long intersectionId);

    List<TrafficSensor> findByIntersectionIdAndDirectionAndActiveTrueOrderByRecordedAtDesc(
            Long intersectionId, TrafficSensor.Direction direction);

    @Query("SELECT s FROM TrafficSensor s WHERE s.intersectionId = :intersectionId " +
           "AND s.active = true AND s.recordedAt >= :since ORDER BY s.recordedAt DESC")
    List<TrafficSensor> findRecentReadings(Long intersectionId, LocalDateTime since);

    @Query("SELECT AVG(s.vehicleCount) FROM TrafficSensor s " +
           "WHERE s.intersectionId = :intersectionId AND s.recordedAt >= :since")
    Double getAverageVehicleCount(Long intersectionId, LocalDateTime since);

    @Query("SELECT s.direction, AVG(s.vehicleCount) FROM TrafficSensor s " +
           "WHERE s.intersectionId = :intersectionId AND s.recordedAt >= :since " +
           "GROUP BY s.direction")
    List<Object[]> getAverageByDirection(Long intersectionId, LocalDateTime since);
}