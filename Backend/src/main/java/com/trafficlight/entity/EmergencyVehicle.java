package com.trafficlight.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "emergency_vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyVehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String vehicleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VehicleType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EmergencyStatus status;

    @Column(nullable = false)
    private Long currentIntersectionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Direction direction;

    @Column(name = "detected_at", nullable = false)
    private LocalDateTime detectedAt;

    @Column(name = "cleared_at")
    private LocalDateTime clearedAt;

    @Column(name = "priority_level")
    private Integer priorityLevel; // 1 = highest, 5 = lowest

    @Column(length = 500)
    private String notes;

    @PrePersist
    protected void onCreate() {
        detectedAt = LocalDateTime.now();
        if (priorityLevel == null) {
            priorityLevel = type == VehicleType.AMBULANCE ? 1 : 2;
        }
    }

    // Enums
    public enum VehicleType {
        AMBULANCE("🚑 Ambulans"),
        FIRE_TRUCK("🚒 İtfaiye"),
        POLICE("🚓 Polis"),
        MILITARY("🚙 Askeri Araç");

        private final String displayName;

        VehicleType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum EmergencyStatus {
        DETECTED("Tespit Edildi"),
        IN_PROGRESS("Geçiş Yapıyor"),
        CLEARED("Geçti"),
        CANCELLED("İptal Edildi");

        private final String displayName;

        EmergencyStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

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
}