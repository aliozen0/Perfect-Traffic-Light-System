package com.trafficlight.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "traffic_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrafficRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String ruleName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RuleType ruleType;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private Integer priority; // 1 = highest

    // Koşullar
    @Column(name = "min_vehicle_count")
    private Integer minVehicleCount;

    @Column(name = "max_vehicle_count")
    private Integer maxVehicleCount;

    @Column(name = "time_start")
    private LocalTime timeStart;

    @Column(name = "time_end")
    private LocalTime timeEnd;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private DayType dayType;

    // Aksiyonlar
    @Column(name = "green_duration_adjustment")
    private Integer greenDurationAdjustment; // +/- saniye

    @Column(name = "base_green_duration")
    private Integer baseGreenDuration = 30; // Varsayılan 30 saniye

    @Column(name = "max_green_duration")
    private Integer maxGreenDuration = 90;

    @Column(name = "min_green_duration")
    private Integer minGreenDuration = 15;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "times_applied")
    private Long timesApplied = 0L;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (timesApplied == null) {
            timesApplied = 0L;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Enums
    public enum RuleType {
        PEAK_HOUR("⏰ Yoğun Saat"),
        HIGH_DENSITY("🚗 Yüksek Yoğunluk"),
        LOW_DENSITY("🌙 Düşük Yoğunluk"),
        NIGHT_MODE("🌃 Gece Modu"),
        WEEKEND("📅 Hafta Sonu"),
        WEATHER_BASED("🌧️ Hava Durumu"),
        CUSTOM("⚙️ Özel Kural");

        private final String displayName;

        RuleType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum DayType {
        WEEKDAY("Hafta İçi"),
        WEEKEND("Hafta Sonu"),
        ALL("Her Gün");

        private final String displayName;

        DayType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}