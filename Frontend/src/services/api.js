// API Service for Backend Integration
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

/**
 * Generic API call handler with error handling
 */
const apiCall = async (endpoint, options = {}) => {
  try {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
      ...options,
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const data = await response.json();
    return data;
  } catch (error) {
    console.error('API call failed:', error);
    throw error;
  }
};

// ==========================================
// Health & Status APIs
// ==========================================

export const getHealthStatus = async () => {
  return apiCall('/health');
};

// ==========================================
// Intersection APIs
// ==========================================

/**
 * Get all intersections with optional pagination and filtering
 */
export const getIntersections = async (params = {}) => {
  const queryString = new URLSearchParams(params).toString();
  return apiCall(`/intersections${queryString ? '?' + queryString : ''}`);
};

/**
 * Get intersection by ID
 */
export const getIntersectionById = async (id) => {
  return apiCall(`/intersections/${id}`);
};

/**
 * Get intersections by city
 */
export const getIntersectionsByCity = async (city) => {
  return apiCall(`/intersections?city=${city}`);
};

/**
 * Get nearby intersections
 */
export const getNearbyIntersections = async (lat, lng, radius = 5) => {
  return apiCall(`/intersections/nearby?lat=${lat}&lng=${lng}&radius=${radius}`);
};

/**
 * Create new intersection
 */
export const createIntersection = async (data) => {
  return apiCall('/intersections', {
    method: 'POST',
    body: JSON.stringify(data),
  });
};

/**
 * Update intersection
 */
export const updateIntersection = async (id, data) => {
  return apiCall(`/intersections/${id}`, {
    method: 'PUT',
    body: JSON.stringify(data),
  });
};

/**
 * Delete intersection
 */
export const deleteIntersection = async (id) => {
  return apiCall(`/intersections/${id}`, {
    method: 'DELETE',
  });
};

// ==========================================
// Dashboard APIs
// ==========================================

/**
 * Get dashboard summary
 */
export const getDashboardSummary = async () => {
  return apiCall('/dashboard/summary');
};

/**
 * Get city statistics
 */
export const getCityStatistics = async () => {
  return apiCall('/dashboard/city-statistics');
};

/**
 * Get status distribution
 */
export const getStatusDistribution = async () => {
  return apiCall('/dashboard/status-distribution');
};

// ==========================================
// Map APIs
// ==========================================

/**
 * Get all intersections for map display
 */
export const getMapIntersections = async () => {
  return apiCall('/map/intersections');
};

/**
 * Get intersections within bounds
 */
export const getIntersectionsInBounds = async (minLat, maxLat, minLng, maxLng) => {
  return apiCall(`/map/bounds?minLat=${minLat}&maxLat=${maxLat}&minLng=${minLng}&maxLng=${maxLng}`);
};

/**
 * Get heatmap data
 */
export const getHeatmapData = async (city, days = 7) => {
  return apiCall(`/map/heatmap?city=${city}&days=${days}`);
};

/**
 * Get GeoJSON data
 */
export const getGeoJSON = async (city) => {
  return apiCall(`/map/geojson${city ? '?city=' + city : ''}`);
};

// ==========================================
// Metrics APIs
// ==========================================

/**
 * Get metrics for intersection
 */
export const getIntersectionMetrics = async (intersectionId, params = {}) => {
  const queryString = new URLSearchParams(params).toString();
  return apiCall(`/intersections/${intersectionId}/metrics${queryString ? '?' + queryString : ''}`);
};

/**
 * Add metric for intersection
 */
export const addIntersectionMetric = async (intersectionId, data) => {
  return apiCall(`/intersections/${intersectionId}/metrics`, {
    method: 'POST',
    body: JSON.stringify(data),
  });
};

/**
 * Get analytics for intersection
 */
export const getIntersectionAnalytics = async (intersectionId, startDate, endDate) => {
  return apiCall(`/intersections/${intersectionId}/metrics/analytics?startDate=${startDate}&endDate=${endDate}`);
};

export default {
  // Health
  getHealthStatus,
  
  // Intersections
  getIntersections,
  getIntersectionById,
  getIntersectionsByCity,
  getNearbyIntersections,
  createIntersection,
  updateIntersection,
  deleteIntersection,
  
  // Dashboard
  getDashboardSummary,
  getCityStatistics,
  getStatusDistribution,
  
  // Map
  getMapIntersections,
  getIntersectionsInBounds,
  getHeatmapData,
  getGeoJSON,
  
  // Metrics
  getIntersectionMetrics,
  addIntersectionMetric,
  getIntersectionAnalytics,
};

