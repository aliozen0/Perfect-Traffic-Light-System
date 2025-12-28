package com.trafficlight.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "rule_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rule_id", nullable = false)
    private Long ruleId;

    @Column(name = "rule_name", length = 100)
    private String ruleName;

    @Column(name = "intersection_id", nullable = false)
    private Long intersectionId;

    @Column(name = "intersection_name", length = 200)
    private String intersectionName;

    @Column(name = "vehicle_count")
    private Integer vehicleCount;

    @Column(name = "previous_green_duration")
    private Integer previousGreenDuration;

    @Column(name = "new_green_duration")
    private Integer newGreenDuration;

    @Column(name = "adjustment")
    private Integer adjustment; // +/- saniye

    @Column(length = 1000)
    private String reason;

    @Column(name = "applied_at", nullable = false)
    private LocalDateTime appliedAt;

    @Column(nullable = false)
    private Boolean successful = true;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @PrePersist
    protected void onCreate() {
        appliedAt = LocalDateTime.now();
    }
}