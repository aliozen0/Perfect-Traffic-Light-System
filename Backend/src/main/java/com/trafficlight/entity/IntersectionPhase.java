package com.trafficlight.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * HAFTA 2 - Entity Class
 * IntersectionPhase Entity - Traffic light phase definitions
 * 
 * Stores phase configurations, timing, priorities, and conflict management.
 */
@Entity
@Table(name = "intersection_phases",
    indexes = {
        @Index(name = "idx_phases_sequence", columnList = "intersection_id, sequence_order")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "unique_phase_number", 
            columnNames = {"intersection_id", "phase_number"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"intersection", "nextPhase"})
public class IntersectionPhase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Foreign Key
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intersection_id", nullable = false)
    private Intersection intersection;

    // Phase Information
    @NotNull(message = "Phase number is required")
    @Min(value = 1, message = "Phase number must be at least 1")
    @Column(name = "phase_number", nullable = false)
    private Integer phaseNumber;

    @NotBlank(message = "Phase name is required")
    @Size(max = 100, message = "Phase name must not exceed 100 characters")
    @Column(name = "phase_name", nullable = false, length = 100)
    private String phaseName;

    @NotNull(message = "Phase type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "phase_type", nullable = false, length = 50)
    private PhaseType phaseType;

    // Direction Configuration
    @Column(name = "allowed_directions", columnDefinition = "TEXT[]")
    private String[] allowedDirections;

    @Size(max = 50, message = "Movement type must not exceed 50 characters")
    @Column(name = "movement_type", length = 50)
    private String movementType;

    // Timing
    @NotNull(message = "Minimum duration is required")
    @Min(value = 3, message = "Minimum duration must be at least 3 seconds")
    @Column(name = "min_duration", nullable = false)
    @Builder.Default
    private Integer minDuration = 5;

    @NotNull(message = "Maximum duration is required")
    @Min(value = 5, message = "Maximum duration must be at least 5 seconds")
    @Max(value = 300, message = "Maximum duration must not exceed 300 seconds")
    @Column(name = "max_duration", nullable = false)
    @Builder.Default
    private Integer maxDuration = 120;

    @NotNull(message = "Default duration is required")
    @Min(value = 5, message = "Default duration must be at least 5 seconds")
    @Column(name = "default_duration", nullable = false)
    @Builder.Default
    private Integer defaultDuration = 30;

    @Min(value = 0, message = "Extension time cannot be negative")
    @Column(name = "extension_time")
    @Builder.Default
    private Integer extensionTime = 3;

    // Phase Priority
    @Min(value = 1, message = "Priority level must be between 1 and 10")
    @Max(value = 10, message = "Priority level must be between 1 and 10")
    @Column(name = "priority_level")
    @Builder.Default
    private Integer priorityLevel = 1;

    @Column(name = "is_protected")
    @Builder.Default
    private Boolean isProtected = false;

    @Column(name = "is_permissive")
    @Builder.Default
    private Boolean isPermissive = false;

    // Pedestrian Phase Specific
    @Column(name = "has_pedestrian_signal")
    @Builder.Default
    private Boolean hasPedestrianSignal = false;

    @Min(value = 0, message = "Pedestrian clearance time cannot be negative")
    @Column(name = "pedestrian_clearance_time")
    @Builder.Default
    private Integer pedestrianClearanceTime = 0;

    @Column(name = "accessible_pedestrian_signal")
    @Builder.Default
    private Boolean accessiblePedestrianSignal = false;

    // Sequence Configuration
    @NotNull(message = "Sequence order is required")
    @Min(value = 1, message = "Sequence order must be at least 1")
    @Column(name = "sequence_order", nullable = false)
    private Integer sequenceOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_phase_id")
    private IntersectionPhase nextPhase;

    @Column(name = "can_skip")
    @Builder.Default
    private Boolean canSkip = false;

    // Conflict Management
    @Column(name = "conflicting_phases", columnDefinition = "INTEGER[]")
    private Integer[] conflictingPhases;

    @Column(name = "compatible_phases", columnDefinition = "INTEGER[]")
    private Integer[] compatiblePhases;

    // Metadata
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    // Audit Fields
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Size(max = 100)
    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Size(max = 100)
    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    // Enums
    public enum PhaseType {
        VEHICLE("vehicle"),
        PEDESTRIAN("pedestrian"),
        MIXED("mixed"),
        TURNING("turning"),
        PROTECTED("protected");

        private final String value;

        PhaseType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}

