package com.trafficlight.repository;

import com.trafficlight.entity.Intersection;
import com.trafficlight.entity.Intersection.IntersectionStatus;
import com.trafficlight.entity.Intersection.IntersectionType;
import com.trafficlight.entity.IntersectionMetric;
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
 * Repository tests for IntersectionMetricRepository
 * 
 * Test Coverage:
 * - findByIntersectionId()
 * - findByIntersectionIdAndDateRange()
 * - getAverageWaitTime()
 * - getTotalVehicleCount()
 * - findMetricsWithAccidents()
 * - findMetricsWithViolations()
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Intersection Metric Repository Tests")
class IntersectionMetricRepositoryTest {

    @Autowired
    private IntersectionMetricRepository metricRepository;

    @Autowired
    private IntersectionRepository intersectionRepository;

    private Intersection testIntersection;
    private IntersectionMetric testMetric;

    @BeforeEach
    void setUp() {
        // Clean database before each test
        metricRepository.deleteAll();
        intersectionRepository.deleteAll();

        // Create test intersection
        testIntersection = Intersection.builder()
            .name("Test Intersection")
            .code("TEST-001")
            .latitude(new BigDecimal("41.0369"))
            .longitude(new BigDecimal("28.9857"))
            .address("Test Address")
            .city("Istanbul")
            .district("Test District")
            .intersectionType(IntersectionType.TRAFFIC_LIGHT)
            .status(IntersectionStatus.ACTIVE)
            .lanesCount(4)
            .createdBy("test")
            .build();
        testIntersection = intersectionRepository.save(testIntersection);

        // Create test metric
        testMetric = IntersectionMetric.builder()
            .intersection(testIntersection)
            .measurementDate(LocalDate.now())
            .measurementHour(10)
            .totalVehicleCount(1000)
            .carCount(800)
            .truckCount(100)
            .busCount(50)
            .motorcycleCount(30)
            .bicycleCount(20)
            .pedestrianCount(200)
            .averageWaitTime(new BigDecimal("45.5"))
            .maximumWaitTime(new BigDecimal("120.0"))
            .averageQueueLength(new BigDecimal("5.5"))
            .maximumQueueLength(15)
            .throughput(1200)
            .greenTimeUtilization(new BigDecimal("85.5"))
            .redLightViolations(5)
            .yellowLightViolations(3)
            .pedestrianViolations(2)
            .accidentsCount(0)
            .nearMissCount(1)
            .emergencyVehiclePassages(2)
            .systemUptimePercentage(new BigDecimal("99.9"))
            .malfunctionCount(0)
            .manualOverrideCount(1)
            .estimatedCo2Emission(new BigDecimal("250.5"))
            .estimatedFuelConsumption(new BigDecimal("100.0"))
            .dataQualityScore(new BigDecimal("0.95"))
            .build();
    }

    @Test
    @DisplayName("Test findByIntersectionId() - Should return all metrics for intersection")
    void testFindByIntersectionId() {
        // Given
        metricRepository.save(testMetric);
        
        IntersectionMetric metric2 = IntersectionMetric.builder()
            .intersection(testIntersection)
            .measurementDate(LocalDate.now())
            .measurementHour(11)
            .totalVehicleCount(1100)
            .averageWaitTime(new BigDecimal("50.0"))
            .dataQualityScore(new BigDecimal("0.90"))
            .build();
        metricRepository.save(metric2);

        // When
        List<IntersectionMetric> metrics = metricRepository.findByIntersectionId(testIntersection.getId());

        // Then
        assertThat(metrics).isNotEmpty();
        assertThat(metrics).hasSize(2);
    }

    @Test
    @DisplayName("Test findByIntersectionId with pagination - Should return paginated metrics")
    void testFindByIntersectionIdWithPagination() {
        // Given
        for (int hour = 0; hour < 24; hour++) {
            IntersectionMetric metric = IntersectionMetric.builder()
                .intersection(testIntersection)
                .measurementDate(LocalDate.now())
                .measurementHour(hour)
                .totalVehicleCount(1000 + hour * 10)
                .averageWaitTime(new BigDecimal("45.5"))
                .dataQualityScore(new BigDecimal("0.95"))
                .build();
            metricRepository.save(metric);
        }

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<IntersectionMetric> metricsPage = metricRepository.findByIntersectionId(testIntersection.getId(), pageable);

        // Then
        assertThat(metricsPage.getContent()).hasSize(10);
        assertThat(metricsPage.getTotalElements()).isEqualTo(24);
        assertThat(metricsPage.getTotalPages()).isEqualTo(3);
    }

    @Test
    @DisplayName("Test findByIntersectionIdAndDateRange() - Should return metrics in date range")
    void testFindByIntersectionIdAndDateRange() {
        // Given
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate tomorrow = today.plusDays(1);
        
        // Metric for yesterday
        IntersectionMetric metric1 = IntersectionMetric.builder()
            .intersection(testIntersection)
            .measurementDate(yesterday)
            .measurementHour(10)
            .totalVehicleCount(900)
            .averageWaitTime(new BigDecimal("40.0"))
            .dataQualityScore(new BigDecimal("0.90"))
            .build();
        metricRepository.save(metric1);
        
        // Metric for today
        metricRepository.save(testMetric);
        
        // Metric for tomorrow
        IntersectionMetric metric3 = IntersectionMetric.builder()
            .intersection(testIntersection)
            .measurementDate(tomorrow)
            .measurementHour(10)
            .totalVehicleCount(1100)
            .averageWaitTime(new BigDecimal("50.0"))
            .dataQualityScore(new BigDecimal("0.95"))
            .build();
        metricRepository.save(metric3);

        // When
        List<IntersectionMetric> metrics = metricRepository.findByIntersectionIdAndDateRange(
            testIntersection.getId(), 
            yesterday, 
            today
        );

        // Then
        assertThat(metrics).hasSize(2); // Should include yesterday and today, not tomorrow
        assertThat(metrics.get(0).getMeasurementDate()).isIn(yesterday, today);
    }

    @Test
    @DisplayName("Test getAverageWaitTime() - Should calculate average wait time")
    void testGetAverageWaitTime() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(2);
        LocalDate endDate = LocalDate.now();
        
        // Create metrics with different wait times
        IntersectionMetric metric1 = IntersectionMetric.builder()
            .intersection(testIntersection)
            .measurementDate(startDate)
            .measurementHour(10)
            .averageWaitTime(new BigDecimal("40.0"))
            .dataQualityScore(new BigDecimal("0.95"))
            .build();
        metricRepository.save(metric1);
        
        IntersectionMetric metric2 = IntersectionMetric.builder()
            .intersection(testIntersection)
            .measurementDate(startDate.plusDays(1))
            .measurementHour(10)
            .averageWaitTime(new BigDecimal("50.0"))
            .dataQualityScore(new BigDecimal("0.95"))
            .build();
        metricRepository.save(metric2);
        
        IntersectionMetric metric3 = IntersectionMetric.builder()
            .intersection(testIntersection)
            .measurementDate(endDate)
            .measurementHour(10)
            .averageWaitTime(new BigDecimal("60.0"))
            .dataQualityScore(new BigDecimal("0.95"))
            .build();
        metricRepository.save(metric3);

        // When
        Double avgWaitTime = metricRepository.getAverageWaitTime(testIntersection.getId(), startDate, endDate);

        // Then
        assertThat(avgWaitTime).isNotNull();
        assertThat(avgWaitTime).isEqualTo(50.0); // (40 + 50 + 60) / 3 = 50
    }

    @Test
    @DisplayName("Test getTotalVehicleCount() - Should sum total vehicle count")
    void testGetTotalVehicleCount() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now();
        
        IntersectionMetric metric1 = IntersectionMetric.builder()
            .intersection(testIntersection)
            .measurementDate(startDate)
            .measurementHour(10)
            .totalVehicleCount(1000)
            .averageWaitTime(new BigDecimal("45.0"))
            .dataQualityScore(new BigDecimal("0.95"))
            .build();
        metricRepository.save(metric1);
        
        IntersectionMetric metric2 = IntersectionMetric.builder()
            .intersection(testIntersection)
            .measurementDate(endDate)
            .measurementHour(10)
            .totalVehicleCount(1500)
            .averageWaitTime(new BigDecimal("50.0"))
            .dataQualityScore(new BigDecimal("0.95"))
            .build();
        metricRepository.save(metric2);

        // When
        Long totalVehicles = metricRepository.getTotalVehicleCount(testIntersection.getId(), startDate, endDate);

        // Then
        assertThat(totalVehicles).isNotNull();
        assertThat(totalVehicles).isEqualTo(2500); // 1000 + 1500
    }

    @Test
    @DisplayName("Test findMetricsWithAccidents() - Should return metrics with accidents")
    void testFindMetricsWithAccidents() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now();
        
        // Metric with no accidents
        metricRepository.save(testMetric);
        
        // Metric with accidents
        IntersectionMetric metricWithAccident = IntersectionMetric.builder()
            .intersection(testIntersection)
            .measurementDate(LocalDate.now())
            .measurementHour(14)
            .totalVehicleCount(1200)
            .averageWaitTime(new BigDecimal("55.0"))
            .accidentsCount(2)
            .dataQualityScore(new BigDecimal("0.95"))
            .build();
        metricRepository.save(metricWithAccident);

        // When
        List<IntersectionMetric> metricsWithAccidents = metricRepository.findMetricsWithAccidents(
            testIntersection.getId(), 
            startDate, 
            endDate
        );

        // Then
        assertThat(metricsWithAccidents).hasSize(1);
        assertThat(metricsWithAccidents.get(0).getAccidentsCount()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Test findMetricsWithViolations() - Should return metrics with violations")
    void testFindMetricsWithViolations() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now();
        
        // Metric with violations (testMetric already has violations)
        metricRepository.save(testMetric);
        
        // Metric with no violations
        IntersectionMetric metricNoViolations = IntersectionMetric.builder()
            .intersection(testIntersection)
            .measurementDate(LocalDate.now())
            .measurementHour(6)
            .totalVehicleCount(500)
            .averageWaitTime(new BigDecimal("30.0"))
            .redLightViolations(0)
            .yellowLightViolations(0)
            .pedestrianViolations(0)
            .dataQualityScore(new BigDecimal("0.95"))
            .build();
        metricRepository.save(metricNoViolations);

        // When
        List<IntersectionMetric> metricsWithViolations = metricRepository.findMetricsWithViolations(
            testIntersection.getId(), 
            startDate, 
            endDate
        );

        // Then
        assertThat(metricsWithViolations).hasSize(1);
        assertThat(metricsWithViolations.get(0).getRedLightViolations() + 
                   metricsWithViolations.get(0).getYellowLightViolations() + 
                   metricsWithViolations.get(0).getPedestrianViolations()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Test getAverageThroughput() - Should calculate average throughput")
    void testGetAverageThroughput() {
        // Given
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now();
        
        IntersectionMetric metric1 = IntersectionMetric.builder()
            .intersection(testIntersection)
            .measurementDate(startDate)
            .measurementHour(8)
            .throughput(1000)
            .averageWaitTime(new BigDecimal("40.0"))
            .dataQualityScore(new BigDecimal("0.95"))
            .build();
        metricRepository.save(metric1);
        
        IntersectionMetric metric2 = IntersectionMetric.builder()
            .intersection(testIntersection)
            .measurementDate(startDate)
            .measurementHour(9)
            .throughput(1200)
            .averageWaitTime(new BigDecimal("45.0"))
            .dataQualityScore(new BigDecimal("0.95"))
            .build();
        metricRepository.save(metric2);

        // When
        Double avgThroughput = metricRepository.getAverageThroughput(testIntersection.getId(), startDate, endDate);

        // Then
        assertThat(avgThroughput).isNotNull();
        assertThat(avgThroughput).isEqualTo(1100.0); // (1000 + 1200) / 2
    }

    @Test
    @DisplayName("Test findMetricsWithLowQuality() - Should find metrics with low data quality")
    void testFindMetricsWithLowQuality() {
        // Given
        IntersectionMetric lowQuality = IntersectionMetric.builder()
            .intersection(testIntersection)
            .measurementDate(LocalDate.now())
            .measurementHour(12)
            .totalVehicleCount(800)
            .averageWaitTime(new BigDecimal("40.0"))
            .dataQualityScore(new BigDecimal("0.65"))
            .build();
        metricRepository.save(lowQuality);
        
        // High quality metric (testMetric has 0.95)
        metricRepository.save(testMetric);

        // When
        List<IntersectionMetric> lowQualityMetrics = metricRepository.findMetricsWithLowQuality(0.80);

        // Then
        assertThat(lowQualityMetrics).hasSize(1);
        assertThat(lowQualityMetrics.get(0).getDataQualityScore()).isLessThan(new BigDecimal("0.80"));
    }

    @Test
    @DisplayName("Test countByIntersectionId() - Should count metrics for intersection")
    void testCountByIntersectionId() {
        // Given
        metricRepository.save(testMetric);
        
        IntersectionMetric metric2 = IntersectionMetric.builder()
            .intersection(testIntersection)
            .measurementDate(LocalDate.now())
            .measurementHour(11)
            .totalVehicleCount(1100)
            .averageWaitTime(new BigDecimal("50.0"))
            .dataQualityScore(new BigDecimal("0.90"))
            .build();
        metricRepository.save(metric2);

        // When
        long count = metricRepository.countByIntersectionId(testIntersection.getId());

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Test findByMeasurementDateAndMeasurementHour() - Should find metrics by date and hour")
    void testFindByMeasurementDateAndMeasurementHour() {
        // Given
        metricRepository.save(testMetric);
        
        IntersectionMetric metric2 = IntersectionMetric.builder()
            .intersection(testIntersection)
            .measurementDate(LocalDate.now())
            .measurementHour(10) // Same hour as testMetric
            .totalVehicleCount(1100)
            .averageWaitTime(new BigDecimal("50.0"))
            .dataQualityScore(new BigDecimal("0.90"))
            .build();
        metricRepository.save(metric2);

        // When
        List<IntersectionMetric> metrics = metricRepository.findByMeasurementDateAndMeasurementHour(
            LocalDate.now(), 10
        );

        // Then
        assertThat(metrics).hasSize(2);
        assertThat(metrics).allMatch(m -> m.getMeasurementHour() == 10);
    }
}
