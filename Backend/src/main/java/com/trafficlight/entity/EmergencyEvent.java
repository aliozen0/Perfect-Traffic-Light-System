package com.trafficlight.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "emergency_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "emergency_vehicle_id", nullable = false)
    private Long emergencyVehicleId;

    @Column(name = "intersection_id", nullable = false)
    private Long intersectionId;

    @Column(name = "intersection_name", length = 200)
    private String intersectionName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EventType eventType;

    @Column(length = 1000)
    private String description;

    @Column(name = "previous_phase", length = 20)
    private String previousPhase;

    @Column(name = "new_phase", length = 20)
    private String newPhase;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "affected_intersections")
    private Integer affectedIntersections;

    @Column(name = "total_wait_time")
    private Integer totalWaitTime;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "success")
    private Boolean success;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (success == null) {
            success = true;
        }
    }

    public enum EventType {
        EMERGENCY_DETECTED("🚨 Acil Araç Tespit Edildi"),
        PHASE_CHANGED("🚦 Faz Değiştirildi"),
        GREEN_LIGHT_ACTIVATED("🟢 Yeşil Işık Aktif"),
        RED_LIGHT_ACTIVATED("🔴 Kırmızı Işık Aktif"),
        EMERGENCY_CLEARED("✅ Acil Durum Sonlandı"),
        NORMAL_OPERATION_RESUMED("🔄 Normal Moda Dönüldü");

        private final String displayName;

        EventType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}