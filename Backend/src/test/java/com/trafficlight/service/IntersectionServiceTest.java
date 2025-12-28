package com.trafficlight.service;

import com.trafficlight.dto.IntersectionRequest;
import com.trafficlight.dto.IntersectionResponse;
import com.trafficlight.entity.Intersection;
import com.trafficlight.entity.Intersection.IntersectionStatus;
import com.trafficlight.entity.Intersection.IntersectionType;
import com.trafficlight.exception.DuplicateResourceException;
import com.trafficlight.exception.ResourceNotFoundException;
import com.trafficlight.repository.IntersectionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * HAFTA 4 - Unit Tests
 * Service tests for IntersectionService using Mockito
 * 
 * Test Coverage: %80+
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Intersection Service Tests")
class IntersectionServiceTest {

    @Mock
    private IntersectionRepository intersectionRepository;

    @InjectMocks
    private IntersectionService intersectionService;

    private Intersection testIntersection;
    private IntersectionRequest testRequest;

    @BeforeEach
    void setUp() {
        testIntersection = Intersection.builder()
            .id(1L)
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
            .createdBy("test")
            .build();

        testRequest = IntersectionRequest.builder()
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
            .createdBy("test")
            .build();
    }

    @Test
    @DisplayName("Test getAllIntersections() - Should return list of intersections")
    void testGetAllIntersections() {
        // Given
        List<Intersection> intersections = Arrays.asList(testIntersection);
        when(intersectionRepository.findAll()).thenReturn(intersections);

        // When
        List<IntersectionResponse> result = intersectionService.getAllIntersections();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Intersection");
        verify(intersectionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test getIntersectionById() - Should return intersection when exists")
    void testGetIntersectionById() {
        // Given
        when(intersectionRepository.findById(1L)).thenReturn(Optional.of(testIntersection));

        // When
        IntersectionResponse result = intersectionService.getIntersectionById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Intersection");
        verify(intersectionRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Test getIntersectionById() - Should throw exception when not found")
    void testGetIntersectionByIdNotFound() {
        // Given
        when(intersectionRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> intersectionService.getIntersectionById(999L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Intersection not found");
        
        verify(intersectionRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Test createIntersection() - Should create new intersection")
    void testCreateIntersection() {
        // Given
        when(intersectionRepository.existsByCode(anyString())).thenReturn(false);
        when(intersectionRepository.save(any(Intersection.class))).thenReturn(testIntersection);

        // When
        IntersectionResponse result = intersectionService.createIntersection(testRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Intersection");
        verify(intersectionRepository, times(1)).existsByCode("TEST-001");
        verify(intersectionRepository, times(1)).save(any(Intersection.class));
    }

    @Test
    @DisplayName("Test createIntersection() - Should throw exception for duplicate code")
    void testCreateIntersectionDuplicateCode() {
        // Given
        when(intersectionRepository.existsByCode("TEST-001")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> intersectionService.createIntersection(testRequest))
            .isInstanceOf(DuplicateResourceException.class)
            .hasMessageContaining("already exists");
        
        verify(intersectionRepository, times(1)).existsByCode("TEST-001");
        verify(intersectionRepository, never()).save(any(Intersection.class));
    }

    @Test
    @DisplayName("Test updateIntersection() - Should update existing intersection")
    void testUpdateIntersection() {
        // Given
        IntersectionRequest updateRequest = IntersectionRequest.builder()
            .name("Updated Intersection")
            .code("TEST-001")
            .latitude(new BigDecimal("41.0369"))
            .longitude(new BigDecimal("28.9857"))
            .city("Istanbul")
            .intersectionType(IntersectionType.TRAFFIC_LIGHT)
            .status(IntersectionStatus.MAINTENANCE)
            .build();

        when(intersectionRepository.findById(1L)).thenReturn(Optional.of(testIntersection));
        when(intersectionRepository.save(any(Intersection.class))).thenReturn(testIntersection);

        // When
        IntersectionResponse result = intersectionService.updateIntersection(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        verify(intersectionRepository, times(1)).findById(1L);
        verify(intersectionRepository, times(1)).save(any(Intersection.class));
    }

    @Test
    @DisplayName("Test updateIntersection() - Should throw exception when not found")
    void testUpdateIntersectionNotFound() {
        // Given
        when(intersectionRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> intersectionService.updateIntersection(999L, testRequest))
            .isInstanceOf(ResourceNotFoundException.class);
        
        verify(intersectionRepository, times(1)).findById(999L);
        verify(intersectionRepository, never()).save(any(Intersection.class));
    }

    @Test
    @DisplayName("Test deleteIntersection() - Should delete intersection")
    void testDeleteIntersection() {
        // Given
        when(intersectionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(intersectionRepository).deleteById(1L);

        // When
        intersectionService.deleteIntersection(1L);

        // Then
        verify(intersectionRepository, times(1)).existsById(1L);
        verify(intersectionRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Test deleteIntersection() - Should throw exception when not found")
    void testDeleteIntersectionNotFound() {
        // Given
        when(intersectionRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> intersectionService.deleteIntersection(999L))
            .isInstanceOf(ResourceNotFoundException.class);
        
        verify(intersectionRepository, times(1)).existsById(999L);
        verify(intersectionRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Test findByFilters() - Should filter by city")
    void testFindByFiltersWithCity() {
        // Given
        List<Intersection> intersections = Arrays.asList(testIntersection);
        when(intersectionRepository.findByCity("Istanbul")).thenReturn(intersections);

        // When
        List<IntersectionResponse> result = intersectionService.findByFilters("Istanbul", null);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCity()).isEqualTo("Istanbul");
        verify(intersectionRepository, times(1)).findByCity("Istanbul");
    }

    @Test
    @DisplayName("Test findByFilters() - Should filter by status")
    void testFindByFiltersWithStatus() {
        // Given
        List<Intersection> intersections = Arrays.asList(testIntersection);
        when(intersectionRepository.findByStatus(IntersectionStatus.ACTIVE)).thenReturn(intersections);

        // When
        List<IntersectionResponse> result = intersectionService.findByFilters(null, IntersectionStatus.ACTIVE);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(IntersectionStatus.ACTIVE);
        verify(intersectionRepository, times(1)).findByStatus(IntersectionStatus.ACTIVE);
    }

    @Test
    @DisplayName("Test findByFilters() - Should filter by city and status")
    void testFindByFiltersWithCityAndStatus() {
        // Given
        List<Intersection> intersections = Arrays.asList(testIntersection);
        when(intersectionRepository.findByCityAndStatus("Istanbul", IntersectionStatus.ACTIVE))
            .thenReturn(intersections);

        // When
        List<IntersectionResponse> result = intersectionService.findByFilters("Istanbul", IntersectionStatus.ACTIVE);

        // Then
        assertThat(result).hasSize(1);
        verify(intersectionRepository, times(1))
            .findByCityAndStatus("Istanbul", IntersectionStatus.ACTIVE);
    }

    @Test
    @DisplayName("Test findNearbyIntersections() - Should find nearby intersections")
    void testFindNearbyIntersections() {
        // Given
        List<Intersection> intersections = Arrays.asList(testIntersection);
        when(intersectionRepository.findNearby(
            any(BigDecimal.class), 
            any(BigDecimal.class), 
            anyDouble()
        )).thenReturn(intersections);

        // When
        List<IntersectionResponse> result = intersectionService.findNearbyIntersections(
            new BigDecimal("41.0369"), 
            new BigDecimal("28.9857"), 
            5.0
        );

        // Then
        assertThat(result).hasSize(1);
        verify(intersectionRepository, times(1)).findNearby(
            any(BigDecimal.class), 
            any(BigDecimal.class), 
            eq(5.0)
        );
    }

    @Test
    @DisplayName("Test findByType() - Should find intersections by type")
    void testFindByType() {
        // Given
        List<Intersection> intersections = Arrays.asList(testIntersection);
        when(intersectionRepository.findByIntersectionType(IntersectionType.TRAFFIC_LIGHT))
            .thenReturn(intersections);

        // When
        List<IntersectionResponse> result = intersectionService.findByType(IntersectionType.TRAFFIC_LIGHT);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIntersectionType()).isEqualTo(IntersectionType.TRAFFIC_LIGHT);
        verify(intersectionRepository, times(1)).findByIntersectionType(IntersectionType.TRAFFIC_LIGHT);
    }
}

