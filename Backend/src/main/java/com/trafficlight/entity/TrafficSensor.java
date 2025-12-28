package com.trafficlight.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "traffic_sensors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrafficSensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sensor_id", nullable = false, unique = true, length = 50)
    private String sensorId;

    @Column(name = "intersection_id", nullable = false)
    private Long intersectionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Direction direction;

    @Column(name = "vehicle_count", nullable = false)
    private Integer vehicleCount;

    @Column(name = "average_speed")
    private Double averageSpeed; // km/h

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DensityLevel densityLevel;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @Column(nullable = false)
    private Boolean active = true;

    @PrePersist
    protected void onCreate() {
        recordedAt = LocalDateTime.now();
        if (densityLevel == null) {
            densityLevel = calculateDensityLevel(vehicleCount);
        }
    }

    // Helper method
    private DensityLevel calculateDensityLevel(Integer count) {
        if (count == null) return DensityLevel.UNKNOWN;
        if (count < 10) return DensityLevel.LOW;
        if (count < 30) return DensityLevel.MEDIUM;
        if (count < 50) return DensityLevel.HIGH;
        return DensityLevel.CRITICAL;
    }

    // Enums
    public enum Direction {
        NORTH("Kuzey"),
        SOUTH("Güney"),
        EAST("Doğu"),
        WEST("Batı");

        private final String displayName;

        Direction(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum DensityLevel {
        LOW("🟢 Düşük", "0-9 araç"),
        MEDIUM("🟡 Orta", "10-29 araç"),
        HIGH("🟠 Yüksek", "30-49 araç"),
        CRITICAL("🔴 Kritik", "50+ araç"),
        UNKNOWN("❓ Bilinmiyor", "Veri yok");

        private final String displayName;
        private final String range;

        DensityLevel(String displayName, String range) {
            this.displayName = displayName;
            this.range = range;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getRange() {
            return range;
        }
    }
}