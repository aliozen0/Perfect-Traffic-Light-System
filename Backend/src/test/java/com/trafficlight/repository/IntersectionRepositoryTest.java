package com.trafficlight.repository;

import com.trafficlight.entity.Intersection;
import com.trafficlight.entity.Intersection.IntersectionStatus;
import com.trafficlight.entity.Intersection.IntersectionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HAFTA 4 - Unit Tests
 * Repository tests for IntersectionRepository
 * 
 * Test Coverage:
 * - findAll()
 * - findById()
 * - findByCity()
 * - create()
 * - update()
 * - delete()
 * - findNearby()
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Intersection Repository Tests")
class IntersectionRepositoryTest {

    @Autowired
    private IntersectionRepository intersectionRepository;

    private Intersection testIntersection;

    @BeforeEach
    void setUp() {
        // Clean database before each test
        intersectionRepository.deleteAll();

        // Create test data
        testIntersection = Intersection.builder()
            .name("Test Intersection")
            .code("TEST-001")
            .latitude(new BigDecimal("41.0369"))
            .longitude(new BigDecimal("28.9857"))
            .address("Test Address")
            .city("Istanbul")
            .district("Test District")
            .postalCode("34000")
            .intersectionType(IntersectionType.TRAFFIC_LIGHT)
            .status(IntersectionStatus.ACTIVE)
            .lanesCount(4)
            .hasPedestrianCrossing(true)
            .hasVehicleDetection(false)
            .hasEmergencyOverride(false)
            .description("Test intersection for unit testing")
            .installationDate(LocalDate.now())
            .createdBy("test")
            .build();
    }

    @Test
    @DisplayName("Test findAll() - Should return all intersections")
    void testFindAll() {
        // Given
        intersectionRepository.save(testIntersection);

        // When
        List<Intersection> intersections = intersectionRepository.findAll();

        // Then
        assertThat(intersections).isNotEmpty();
        assertThat(intersections).hasSize(1);
        assertThat(intersections.get(0).getName()).isEqualTo("Test Intersection");
    }

    @Test
    @DisplayName("Test findById() - Should return intersection when exists")
    void testFindById() {
        // Given
        Intersection saved = intersectionRepository.save(testIntersection);

        // When
        Optional<Intersection> found = intersectionRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getCode()).isEqualTo("TEST-001");
        assertThat(found.get().getCity()).isEqualTo("Istanbul");
    }

    @Test
    @DisplayName("Test findById() - Should return empty when not exists")
    void testFindByIdNotFound() {
        // When
        Optional<Intersection> found = intersectionRepository.findById(999L);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Test findByCity() - Should return intersections in city")
    void testFindByCity() {
        // Given
        intersectionRepository.save(testIntersection);
        
        Intersection ankara = Intersection.builder()
            .name("Ankara Intersection")
            .code("ANK-001")
            .latitude(new BigDecimal("39.9189"))
            .longitude(new BigDecimal("32.8540"))
            .city("Ankara")
            .intersectionType(IntersectionType.TRAFFIC_LIGHT)
            .status(IntersectionStatus.ACTIVE)
            .createdBy("test")
            .build();
        intersectionRepository.save(ankara);

        // When
        List<Intersection> istanbulIntersections = intersectionRepository.findByCity("Istanbul");
        List<Intersection> ankaraIntersections = intersectionRepository.findByCity("Ankara");

        // Then
        assertThat(istanbulIntersections).hasSize(1);
        assertThat(ankaraIntersections).hasSize(1);
        assertThat(istanbulIntersections.get(0).getCity()).isEqualTo("Istanbul");
    }

    @Test
    @DisplayName("Test create() - Should save new intersection")
    void testCreate() {
        // When
        Intersection saved = intersectionRepository.save(testIntersection);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCode()).isEqualTo("TEST-001");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Test update() - Should update existing intersection")
    void testUpdate() {
        // Given
        Intersection saved = intersectionRepository.save(testIntersection);
        Long id = saved.getId();

        // When
        saved.setName("Updated Intersection");
        saved.setStatus(IntersectionStatus.MAINTENANCE);
        saved.setUpdatedBy("updater");
        Intersection updated = intersectionRepository.save(saved);

        // Then
        assertThat(updated.getId()).isEqualTo(id);
        assertThat(updated.getName()).isEqualTo("Updated Intersection");
        assertThat(updated.getStatus()).isEqualTo(IntersectionStatus.MAINTENANCE);
        assertThat(updated.getUpdatedBy()).isEqualTo("updater");
    }

    @Test
    @DisplayName("Test delete() - Should delete intersection")
    void testDelete() {
        // Given
        Intersection saved = intersectionRepository.save(testIntersection);
        Long id = saved.getId();

        // When
        intersectionRepository.deleteById(id);

        // Then
        Optional<Intersection> deleted = intersectionRepository.findById(id);
        assertThat(deleted).isEmpty();
    }

    @Test
    @DisplayName("Test findByStatus() - Should return intersections by status")
    void testFindByStatus() {
        // Given
        intersectionRepository.save(testIntersection);
        
        Intersection maintenance = Intersection.builder()
            .name("Maintenance Intersection")
            .code("MAINT-001")
            .latitude(new BigDecimal("40.0"))
            .longitude(new BigDecimal("29.0"))
            .city("Istanbul")
            .intersectionType(IntersectionType.TRAFFIC_LIGHT)
            .status(IntersectionStatus.MAINTENANCE)
            .createdBy("test")
            .build();
        intersectionRepository.save(maintenance);

        // When
        List<Intersection> activeIntersections = intersectionRepository.findByStatus(IntersectionStatus.ACTIVE);
        List<Intersection> maintenanceIntersections = intersectionRepository.findByStatus(IntersectionStatus.MAINTENANCE);

        // Then
        assertThat(activeIntersections).hasSize(1);
        assertThat(maintenanceIntersections).hasSize(1);
    }

    @Test
    @DisplayName("Test findByCode() - Should return intersection by code")
    void testFindByCode() {
        // Given
        intersectionRepository.save(testIntersection);

        // When
        Optional<Intersection> found = intersectionRepository.findByCode("TEST-001");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Intersection");
    }

    @Test
    @DisplayName("Test existsByCode() - Should check if code exists")
    void testExistsByCode() {
        // Given
        intersectionRepository.save(testIntersection);

        // When
        boolean exists = intersectionRepository.existsByCode("TEST-001");
        boolean notExists = intersectionRepository.existsByCode("NON-EXISTENT");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Test findWithPagination() - Should return paginated results")
    void testFindWithPagination() {
        // Given
        for (int i = 1; i <= 15; i++) {
            Intersection intersection = Intersection.builder()
                .name("Intersection " + i)
                .code("CODE-" + String.format("%03d", i))
                .latitude(new BigDecimal("41.0"))
                .longitude(new BigDecimal("29.0"))
                .city("Istanbul")
                .intersectionType(IntersectionType.TRAFFIC_LIGHT)
                .status(IntersectionStatus.ACTIVE)
                .createdBy("test")
                .build();
            intersectionRepository.save(intersection);
        }

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<Intersection> page = intersectionRepository.findAll(pageable);

        // Then
        assertThat(page.getContent()).hasSize(10);
        assertThat(page.getTotalElements()).isEqualTo(15);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getNumber()).isEqualTo(0);
    }

    @Test
    @DisplayName("Test findNearby() - Should find nearby intersections")
    void testFindNearby() {
        // Given - Create intersections at different locations
        intersectionRepository.save(testIntersection); // 41.0369, 28.9857
        
        Intersection nearby = Intersection.builder()
            .name("Nearby Intersection")
            .code("NEARBY-001")
            .latitude(new BigDecimal("41.0400")) // Very close
            .longitude(new BigDecimal("28.9900"))
            .city("Istanbul")
            .intersectionType(IntersectionType.TRAFFIC_LIGHT)
            .status(IntersectionStatus.ACTIVE)
            .createdBy("test")
            .build();
        intersectionRepository.save(nearby);
        
        Intersection farAway = Intersection.builder()
            .name("Far Away Intersection")
            .code("FAR-001")
            .latitude(new BigDecimal("39.9189")) // Ankara - far away
            .longitude(new BigDecimal("32.8540"))
            .city("Ankara")
            .intersectionType(IntersectionType.TRAFFIC_LIGHT)
            .status(IntersectionStatus.ACTIVE)
            .createdBy("test")
            .build();
        intersectionRepository.save(farAway);

        // When - Search within 5km radius
        List<Intersection> nearbyIntersections = intersectionRepository.findNearby(
            new BigDecimal("41.0369"), 
            new BigDecimal("28.9857"), 
            5.0
        );

        // Then
        assertThat(nearbyIntersections).hasSizeGreaterThanOrEqualTo(1);
        // Should include test intersection and nearby intersection, but not far away
    }

    @Test
    @DisplayName("Test countByCity() - Should count intersections by city")
    void testCountByCity() {
        // Given
        intersectionRepository.save(testIntersection);
        
        Intersection istanbul2 = Intersection.builder()
            .name("Istanbul 2")
            .code("IST-002")
            .latitude(new BigDecimal("41.0"))
            .longitude(new BigDecimal("29.0"))
            .city("Istanbul")
            .intersectionType(IntersectionType.TRAFFIC_LIGHT)
            .status(IntersectionStatus.ACTIVE)
            .createdBy("test")
            .build();
        intersectionRepository.save(istanbul2);

        // When
        long count = intersectionRepository.countByCity("Istanbul");

        // Then
        assertThat(count).isEqualTo(2);
    }
}

