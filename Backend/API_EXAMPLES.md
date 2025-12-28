# 🔌 API Examples - Traffic Light Management System

Complete examples for testing all API endpoints.

## 📋 Table of Contents

- [Intersection Management](#intersection-management)
- [Metric Management](#metric-management)
- [Analytics](#analytics)
- [Search and Filters](#search-and-filters)

## 🚦 Intersection Management

### Get All Intersections

```bash
# Basic request
curl -X GET "http://localhost:8080/api/intersections" \
  -H "Accept: application/json"

# With pagination
curl -X GET "http://localhost:8080/api/intersections?page=0&limit=10&sort=id&direction=asc" \
  -H "Accept: application/json"

# Filter by city
curl -X GET "http://localhost:8080/api/intersections?city=Istanbul" \
  -H "Accept: application/json"

# Filter by status
curl -X GET "http://localhost:8080/api/intersections?status=ACTIVE" \
  -H "Accept: application/json"

# Combined filters
curl -X GET "http://localhost:8080/api/intersections?city=Istanbul&status=ACTIVE&page=0&limit=5" \
  -H "Accept: application/json"
```

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": {
    "content": [
      {
        "id": 1,
        "name": "Taksim Square Intersection",
        "code": "IST-TAK-001",
        "latitude": 41.0369,
        "longitude": 28.9857,
        "city": "Istanbul",
        "intersectionType": "TRAFFIC_LIGHT",
        "status": "ACTIVE",
        "lanesCount": 6
      }
    ],
    "totalElements": 8,
    "totalPages": 2,
    "number": 0,
    "size": 5
  },
  "timestamp": "2024-12-14T10:30:00",
  "statusCode": 200
}
```

### Get Intersection by ID

```bash
curl -X GET "http://localhost:8080/api/intersections/1" \
  -H "Accept: application/json"
```

### Create New Intersection

```bash
curl -X POST "http://localhost:8080/api/intersections" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "name": "Beşiktaş Square",
    "code": "IST-BES-002",
    "latitude": 41.0428,
    "longitude": 29.0089,
    "address": "Beşiktaş Square, Beşiktaş",
    "city": "Istanbul",
    "district": "Beşiktaş",
    "postalCode": "34349",
    "intersectionType": "TRAFFIC_LIGHT",
    "status": "ACTIVE",
    "lanesCount": 6,
    "hasPedestrianCrossing": true,
    "hasVehicleDetection": true,
    "hasEmergencyOverride": true,
    "description": "Major intersection at Beşiktaş Square",
    "installationDate": "2024-01-15",
    "createdBy": "admin"
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "Resource created successfully",
  "data": {
    "id": 9,
    "name": "Beşiktaş Square",
    "code": "IST-BES-002",
    "latitude": 41.0428,
    "longitude": 29.0089,
    "city": "Istanbul",
    "status": "ACTIVE",
    "createdAt": "2024-12-14T10:35:00"
  },
  "statusCode": 201
}
```

### Update Intersection

```bash
curl -X PUT "http://localhost:8080/api/intersections/9" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "name": "Beşiktaş Square - Updated",
    "code": "IST-BES-002",
    "latitude": 41.0428,
    "longitude": 29.0089,
    "city": "Istanbul",
    "intersectionType": "TRAFFIC_LIGHT",
    "status": "MAINTENANCE",
    "lanesCount": 8,
    "description": "Under maintenance for system upgrade",
    "updatedBy": "admin"
  }'
```

### Delete Intersection

```bash
curl -X DELETE "http://localhost:8080/api/intersections/9" \
  -H "Accept: application/json"
```

**Response:**
```json
{
  "success": true,
  "message": "Intersection deleted successfully",
  "data": null,
  "timestamp": "2024-12-14T10:40:00",
  "statusCode": 200
}
```

## 📊 Metric Management

### Get Metrics for Intersection

```bash
# All metrics
curl -X GET "http://localhost:8080/api/intersections/1/metrics" \
  -H "Accept: application/json"

# With pagination
curl -X GET "http://localhost:8080/api/intersections/1/metrics?page=0&limit=10" \
  -H "Accept: application/json"

# With date range filter
curl -X GET "http://localhost:8080/api/intersections/1/metrics?startDate=2024-01-01&endDate=2024-01-31" \
  -H "Accept: application/json"

# Combined filters
curl -X GET "http://localhost:8080/api/intersections/1/metrics?startDate=2024-12-01&endDate=2024-12-14&page=0&limit=20&sort=measurementDate&direction=desc" \
  -H "Accept: application/json"
```

### Create Metric

```bash
curl -X POST "http://localhost:8080/api/intersections/1/metrics" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "measurementDate": "2024-12-14",
    "measurementHour": 8,
    "totalVehicleCount": 1450,
    "carCount": 1100,
    "truckCount": 95,
    "busCount": 50,
    "motorcycleCount": 150,
    "bicycleCount": 55,
    "pedestrianCount": 380,
    "averageWaitTime": 52.3,
    "maximumWaitTime": 135.7,
    "averageQueueLength": 9.8,
    "maximumQueueLength": 18,
    "throughput": 1450,
    "greenTimeUtilization": 89.2,
    "redLightViolations": 4,
    "yellowLightViolations": 3,
    "pedestrianViolations": 6,
    "accidentsCount": 0,
    "nearMissCount": 2,
    "emergencyVehiclePassages": 1,
    "systemUptimePercentage": 99.9,
    "malfunctionCount": 0,
    "manualOverrideCount": 0,
    "estimatedCo2Emission": 145.2,
    "estimatedFuelConsumption": 110.5,
    "dataQualityScore": 0.96,
    "notes": "Morning peak hour data"
  }'
```

### Get Metric by ID

```bash
curl -X GET "http://localhost:8080/api/metrics/1" \
  -H "Accept: application/json"
```

### Delete Metric

```bash
curl -X DELETE "http://localhost:8080/api/metrics/1" \
  -H "Accept: application/json"
```

## 📈 Analytics

### Get Analytics Summary

```bash
curl -X GET "http://localhost:8080/api/intersections/1/metrics/analytics?startDate=2024-12-01&endDate=2024-12-14" \
  -H "Accept: application/json"
```

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": {
    "intersectionId": 1,
    "startDate": "2024-12-01",
    "endDate": "2024-12-14",
    "averageWaitTime": 48.5,
    "totalVehicleCount": 156780
  },
  "statusCode": 200
}
```

### Get Metrics with Accidents

```bash
curl -X GET "http://localhost:8080/api/intersections/1/metrics/accidents?startDate=2024-01-01&endDate=2024-12-14" \
  -H "Accept: application/json"
```

### Get Metrics with Violations

```bash
curl -X GET "http://localhost:8080/api/intersections/1/metrics/violations?startDate=2024-12-01&endDate=2024-12-14" \
  -H "Accept: application/json"
```

## 🔍 Search and Filters

### Find Nearby Intersections

```bash
# Find intersections within 5km
curl -X GET "http://localhost:8080/api/intersections/nearby?lat=41.0369&lng=28.9857&radius=5.0" \
  -H "Accept: application/json"

# Within 10km
curl -X GET "http://localhost:8080/api/intersections/nearby?lat=41.0369&lng=28.9857&radius=10.0" \
  -H "Accept: application/json"
```

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": [
    {
      "id": 1,
      "name": "Taksim Square Intersection",
      "code": "IST-TAK-001",
      "latitude": 41.0369,
      "longitude": 28.9857,
      "city": "Istanbul",
      "status": "ACTIVE"
    },
    {
      "id": 2,
      "name": "Kadıköy Pier Intersection",
      "code": "IST-KAD-002",
      "latitude": 40.9907,
      "longitude": 29.0258,
      "city": "Istanbul",
      "status": "ACTIVE"
    }
  ],
  "statusCode": 200
}
```

### Search Intersections

```bash
# Search by name
curl -X GET "http://localhost:8080/api/intersections/search?q=Taksim&page=0&limit=10" \
  -H "Accept: application/json"

# Search by code
curl -X GET "http://localhost:8080/api/intersections/search?q=IST-TAK&page=0&limit=10" \
  -H "Accept: application/json"

# Search by address
curl -X GET "http://localhost:8080/api/intersections/search?q=Square&page=0&limit=10" \
  -H "Accept: application/json"
```

### Get Intersections by Type

```bash
# Traffic lights only
curl -X GET "http://localhost:8080/api/intersections/type/TRAFFIC_LIGHT" \
  -H "Accept: application/json"

# Roundabouts only
curl -X GET "http://localhost:8080/api/intersections/type/ROUNDABOUT" \
  -H "Accept: application/json"

# Available types:
# - TRAFFIC_LIGHT
# - ROUNDABOUT
# - CROSSROAD
# - PEDESTRIAN_CROSSING
```

### Get City Statistics

```bash
curl -X GET "http://localhost:8080/api/intersections/statistics/cities" \
  -H "Accept: application/json"
```

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": {
    "Istanbul": 5,
    "Ankara": 1,
    "Izmir": 1,
    "Bursa": 1
  },
  "statusCode": 200
}
```

## 🧪 Testing with Postman

### Import Collection

1. Create new Postman Collection
2. Add requests from examples above
3. Set base URL as environment variable:
   - Variable: `BASE_URL`
   - Value: `http://localhost:8080`

### Sample Environment Variables

```json
{
  "BASE_URL": "http://localhost:8080",
  "INTERSECTION_ID": "1",
  "START_DATE": "2024-01-01",
  "END_DATE": "2024-12-31"
}
```

### Example Postman Request

```
GET {{BASE_URL}}/api/intersections/{{INTERSECTION_ID}}/metrics
  ?startDate={{START_DATE}}
  &endDate={{END_DATE}}
  &page=0
  &limit=10
```

## 🐛 Error Responses

### 404 Not Found

```json
{
  "success": false,
  "message": "Intersection not found with id: 999",
  "data": null,
  "timestamp": "2024-12-14T10:45:00",
  "path": "/api/intersections/999",
  "statusCode": 404
}
```

### 400 Bad Request

```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "name": "Name is required",
    "latitude": "Latitude must be between -90 and 90"
  },
  "timestamp": "2024-12-14T10:46:00",
  "path": "/api/intersections",
  "statusCode": 400
}
```

### 409 Conflict

```json
{
  "success": false,
  "message": "Intersection already exists with code: 'IST-TAK-001'",
  "data": null,
  "timestamp": "2024-12-14T10:47:00",
  "path": "/api/intersections",
  "statusCode": 409
}
```

### 500 Internal Server Error

```json
{
  "success": false,
  "message": "Internal server error: Database connection failed",
  "data": null,
  "timestamp": "2024-12-14T10:48:00",
  "path": "/api/intersections",
  "statusCode": 500
}
```

## 📝 Notes

- All timestamps are in ISO-8601 format
- Pagination is 0-based (first page is 0)
- Date format: YYYY-MM-DD
- Default page size: 10
- Maximum page size: 100
- All responses follow the standardized ApiResponse format

## 🔗 Resources

- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/api-docs
- Health Check: http://localhost:8080/actuator/health

For more examples, explore the Swagger UI interactive documentation!

