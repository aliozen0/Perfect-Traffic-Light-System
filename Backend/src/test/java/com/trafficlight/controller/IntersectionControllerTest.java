package com.trafficlight.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trafficlight.dto.IntersectionRequest;
import com.trafficlight.dto.IntersectionResponse;
import com.trafficlight.entity.Intersection.IntersectionStatus;
import com.trafficlight.entity.Intersection.IntersectionType;
import com.trafficlight.service.IntersectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * HAFTA 4 - Integration Tests
 * Controller tests using MockMvc
 */
@WebMvcTest(IntersectionController.class)
@DisplayName("Intersection Controller Tests")
class IntersectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IntersectionService intersectionService;

    private IntersectionResponse testResponse;
    private IntersectionRequest testRequest;

    @BeforeEach
    void setUp() {
        testResponse = IntersectionResponse.builder()
            .id(1L)
            .name("Test Intersection")
            .code("TEST-001")
            .latitude(new BigDecimal("41.0369"))
            .longitude(new BigDecimal("28.9857"))
            .city("Istanbul")
            .intersectionType(IntersectionType.TRAFFIC_LIGHT)
            .status(IntersectionStatus.ACTIVE)
            .lanesCount(4)
            .build();

        testRequest = IntersectionRequest.builder()
            .name("Test Intersection")
            .code("TEST-001")
            .latitude(new BigDecimal("41.0369"))
            .longitude(new BigDecimal("28.9857"))
            .city("Istanbul")
            .intersectionType(IntersectionType.TRAFFIC_LIGHT)
            .status(IntersectionStatus.ACTIVE)
            .lanesCount(4)
            .build();
    }

    @Test
    @DisplayName("GET /api/intersections - Should return paginated intersections")
    void testGetAllIntersections() throws Exception {
        // Given
        List<IntersectionResponse> intersections = Arrays.asList(testResponse);
        Page<IntersectionResponse> page = new PageImpl<>(intersections);
        when(intersectionService.findByFilters(any(), any(), any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/intersections")
                .param("page", "0")
                .param("limit", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content", hasSize(1)))
            .andExpect(jsonPath("$.data.content[0].name").value("Test Intersection"));

        verify(intersectionService, times(1)).findByFilters(any(), any(), any());
    }

    @Test
    @DisplayName("GET /api/intersections/:id - Should return intersection by ID")
    void testGetIntersectionById() throws Exception {
        // Given
        when(intersectionService.getIntersectionById(1L)).thenReturn(testResponse);

        // When & Then
        mockMvc.perform(get("/api/intersections/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.name").value("Test Intersection"))
            .andExpect(jsonPath("$.data.code").value("TEST-001"));

        verify(intersectionService, times(1)).getIntersectionById(1L);
    }

    @Test
    @DisplayName("POST /api/intersections - Should create new intersection")
    void testCreateIntersection() throws Exception {
        // Given
        when(intersectionService.createIntersection(any(IntersectionRequest.class)))
            .thenReturn(testResponse);

        // When & Then
        mockMvc.perform(post("/api/intersections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.name").value("Test Intersection"))
            .andExpect(jsonPath("$.statusCode").value(201));

        verify(intersectionService, times(1)).createIntersection(any(IntersectionRequest.class));
    }

    @Test
    @DisplayName("POST /api/intersections - Should return 400 for invalid request")
    void testCreateIntersectionInvalidRequest() throws Exception {
        // Given - Invalid request without required fields
        IntersectionRequest invalidRequest = IntersectionRequest.builder().build();

        // When & Then
        mockMvc.perform(post("/api/intersections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false));

        verify(intersectionService, never()).createIntersection(any(IntersectionRequest.class));
    }

    @Test
    @DisplayName("PUT /api/intersections/:id - Should update intersection")
    void testUpdateIntersection() throws Exception {
        // Given
        when(intersectionService.updateIntersection(anyLong(), any(IntersectionRequest.class)))
            .thenReturn(testResponse);

        // When & Then
        mockMvc.perform(put("/api/intersections/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(1));

        verify(intersectionService, times(1)).updateIntersection(eq(1L), any(IntersectionRequest.class));
    }

    @Test
    @DisplayName("DELETE /api/intersections/:id - Should delete intersection")
    void testDeleteIntersection() throws Exception {
        // Given
        doNothing().when(intersectionService).deleteIntersection(1L);

        // When & Then
        mockMvc.perform(delete("/api/intersections/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Intersection deleted successfully"));

        verify(intersectionService, times(1)).deleteIntersection(1L);
    }

    @Test
    @DisplayName("GET /api/intersections/nearby - Should find nearby intersections")
    void testFindNearbyIntersections() throws Exception {
        // Given
        List<IntersectionResponse> nearby = Arrays.asList(testResponse);
        when(intersectionService.findNearbyIntersections(any(), any(), anyDouble()))
            .thenReturn(nearby);

        // When & Then
        mockMvc.perform(get("/api/intersections/nearby")
                .param("lat", "41.0369")
                .param("lng", "28.9857")
                .param("radius", "5.0"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data", hasSize(1)));

        verify(intersectionService, times(1)).findNearbyIntersections(any(), any(), eq(5.0));
    }

    @Test
    @DisplayName("GET /api/intersections/search - Should search intersections")
    void testSearchIntersections() throws Exception {
        // Given
        List<IntersectionResponse> results = Arrays.asList(testResponse);
        Page<IntersectionResponse> page = new PageImpl<>(results);
        when(intersectionService.searchIntersections(anyString(), any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/intersections/search")
                .param("q", "Test")
                .param("page", "0")
                .param("limit", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content", hasSize(1)));

        verify(intersectionService, times(1)).searchIntersections(eq("Test"), any());
    }
}

