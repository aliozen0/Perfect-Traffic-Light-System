# 🚦 Traffic Light Management System

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Comprehensive Traffic Light Intersection Management System built with Spring Boot, Java 17, and PostgreSQL.

## 📋 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Testing](#testing)
- [Configuration](#configuration)
- [Project Timeline](#project-timeline)

## ✨ Features

### HAFTA 1 - Database Design
- ✅ Comprehensive PostgreSQL database schema
- ✅ 4 main tables: intersections, intersection_configs, intersection_metrics, intersection_phases
- ✅ Optimized indexes for performance
- ✅ Automated triggers and functions
- ✅ Flyway database migration support

### HAFTA 2 - System Architecture
- ✅ JPA Entity mappings with relationships
- ✅ Repository pattern with Spring Data JPA
- ✅ Custom queries for complex operations
- ✅ Support for pagination and filtering
- ✅ findByCity(), findByStatus(), findNearby() implementations

### HAFTA 3 - API Implementation
- ✅ RESTful API endpoints (CRUD operations)
- ✅ Intersection management endpoints
- ✅ Metric data collection endpoints
- ✅ Global exception handling middleware
- ✅ Time-range filtering support
- ✅ Pagination and sorting support
- ✅ Standardized API responses

### HAFTA 4 - Testing & Optimization
- ✅ Unit tests (80%+ coverage)
- ✅ Integration tests with MockMvc
- ✅ Repository layer tests
- ✅ Service layer tests with Mockito
- ✅ Controller tests
- ✅ HikariCP connection pooling
- ✅ Query optimization with indexes
- ✅ Performance monitoring

## 🛠️ Tech Stack

- **Backend Framework:** Spring Boot 3.2.0
- **Language:** Java 17
- **Database:** PostgreSQL 15
- **ORM:** Spring Data JPA / Hibernate
- **Migration:** Flyway
- **Connection Pooling:** HikariCP
- **Testing:** JUnit 5, Mockito, AssertJ
- **API Documentation:** SpringDoc OpenAPI 3 (Swagger)
- **Build Tool:** Maven
- **Validation:** Jakarta Bean Validation

## 📁 Project Structure

```
traffic-light-system/
├── src/
│   ├── main/
│   │   ├── java/com/trafficlight/
│   │   │   ├── TrafficLightApplication.java
│   │   │   ├── config/
│   │   │   │   ├── DatabaseConfig.java
│   │   │   │   └── OpenApiConfig.java
│   │   │   ├── entity/
│   │   │   │   ├── Intersection.java
│   │   │   │   ├── IntersectionConfig.java
│   │   │   │   ├── IntersectionMetric.java
│   │   │   │   └── IntersectionPhase.java
│   │   │   ├── repository/
│   │   │   │   ├── IntersectionRepository.java
│   │   │   │   ├── IntersectionConfigRepository.java
│   │   │   │   ├── IntersectionMetricRepository.java
│   │   │   │   └── IntersectionPhaseRepository.java
│   │   │   ├── service/
│   │   │   │   ├── IntersectionService.java
│   │   │   │   └── MetricService.java
│   │   │   ├── controller/
│   │   │   │   ├── IntersectionController.java
│   │   │   │   └── MetricController.java
│   │   │   ├── dto/
│   │   │   │   ├── IntersectionRequest.java
│   │   │   │   ├── IntersectionResponse.java
│   │   │   │   ├── MetricRequest.java
│   │   │   │   ├── MetricResponse.java
│   │   │   │   └── ApiResponse.java
│   │   │   └── exception/
│   │   │       ├── ResourceNotFoundException.java
│   │   │       ├── BadRequestException.java
│   │   │       ├── DuplicateResourceException.java
│   │   │       └── GlobalExceptionHandler.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/migration/
│   │           ├── V1__Create_Intersection_Schema.sql
│   │           └── V2__Insert_Sample_Data.sql
│   └── test/
│       └── java/com/trafficlight/
│           ├── repository/
│           │   └── IntersectionRepositoryTest.java
│           ├── service/
│           │   └── IntersectionServiceTest.java
│           └── controller/
│               └── IntersectionControllerTest.java
├── pom.xml
└── README.md
```

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- PostgreSQL 15 or higher
- Maven 3.8 or higher

### Database Setup

1. Create PostgreSQL database:
```sql
CREATE DATABASE trafficlight_db;
```

2. Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/trafficlight_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/traffic-light-system.git
cd traffic-light-system
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Running Tests

```bash
# Run all tests
mvn test

# Run tests with coverage
mvn test jacoco:report

# Run specific test class
mvn test -Dtest=IntersectionRepositoryTest
```

## 📚 API Documentation

### Swagger UI
Access the interactive API documentation at:
```
http://localhost:8080/swagger-ui.html
```

### OpenAPI JSON
```
http://localhost:8080/api-docs
```

### Main Endpoints

#### Intersections

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/intersections` | Get all intersections (with pagination) |
| GET | `/api/intersections/{id}` | Get intersection by ID |
| POST | `/api/intersections` | Create new intersection |
| PUT | `/api/intersections/{id}` | Update intersection |
| DELETE | `/api/intersections/{id}` | Delete intersection |
| GET | `/api/intersections/nearby` | Find nearby intersections |
| GET | `/api/intersections/search` | Search intersections |

#### Metrics

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/intersections/{id}/metrics` | Get metrics for intersection |
| POST | `/api/intersections/{id}/metrics` | Create new metric |
| GET | `/api/metrics/{id}` | Get metric by ID |
| DELETE | `/api/metrics/{id}` | Delete metric |
| GET | `/api/intersections/{id}/metrics/analytics` | Get analytics summary |

### Example Requests

#### Create Intersection
```bash
curl -X POST http://localhost:8080/api/intersections \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Taksim Square",
    "code": "IST-TAK-001",
    "latitude": 41.0369,
    "longitude": 28.9857,
    "city": "Istanbul",
    "intersectionType": "TRAFFIC_LIGHT",
    "status": "ACTIVE",
    "lanesCount": 6
  }'
```

#### Get Intersections with Filters
```bash
curl "http://localhost:8080/api/intersections?city=Istanbul&status=ACTIVE&page=0&limit=10"
```

#### Create Metric
```bash
curl -X POST http://localhost:8080/api/intersections/1/metrics \
  -H "Content-Type: application/json" \
  -d '{
    "measurementDate": "2024-12-14",
    "measurementHour": 8,
    "totalVehicleCount": 1250,
    "averageWaitTime": 45.5,
    "greenTimeUtilization": 87.5
  }'
```

#### Get Metrics with Date Range
```bash
curl "http://localhost:8080/api/intersections/1/metrics?startDate=2024-01-01&endDate=2024-01-31"
```

## 🗄️ Database Schema

### Tables

1. **intersections** - Main intersection information
   - Location data (latitude, longitude, address)
   - Intersection type and status
   - Features (pedestrian crossing, vehicle detection)
   - Maintenance scheduling

2. **intersection_configs** - Configuration parameters
   - Timing configurations (green, yellow, red light durations)
   - Adaptive traffic control settings
   - Peak hour and night mode configurations
   - Coordination settings

3. **intersection_metrics** - Performance metrics
   - Traffic volume data
   - Performance metrics (wait times, queue lengths)
   - Violation counts
   - System performance data
   - Environmental metrics

4. **intersection_phases** - Phase definitions
   - Phase timing and sequencing
   - Priority levels
   - Conflict management
   - Pedestrian phase configurations

### Indexes

Performance-optimized indexes for:
- Location-based queries (lat/lng)
- City and status filtering
- Date range queries on metrics
- Phase sequencing

## 🧪 Testing

### Test Coverage

- **Repository Tests**: Data access layer testing
- **Service Tests**: Business logic testing with Mockito
- **Controller Tests**: API endpoint testing with MockMvc
- **Integration Tests**: End-to-end testing

### Test Statistics

- Total Tests: 50+
- Coverage: 80%+
- Test Types: Unit, Integration, Repository

### Running Specific Tests

```bash
# Repository tests
mvn test -Dtest=IntersectionRepositoryTest

# Service tests
mvn test -Dtest=IntersectionServiceTest

# Controller tests
mvn test -Dtest=IntersectionControllerTest
```

## ⚙️ Configuration

### Application Properties

Key configuration options in `application.properties`:

```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/trafficlight_db
spring.datasource.username=postgres
spring.datasource.password=postgres

# HikariCP Connection Pool (HAFTA 4 - Optimization)
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.connection-timeout=20000

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Flyway Migration
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
```

### Environment Variables

You can override properties using environment variables:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/trafficlight_db
export DB_USERNAME=postgres
export DB_PASSWORD=your_password
```

## 📅 Project Timeline

### HAFTA 1 - Gereksinim Analizi ✅
- Database schema design
- 4-table structure
- Index optimization
- Migration scripts

### HAFTA 2 - Sistem Mimarisi ✅
- JPA Entity classes
- Repository pattern
- Custom queries
- Relationship mappings

### HAFTA 3 - API Implementation ✅
- CRUD endpoints
- Metric endpoints
- Exception handling
- Response standardization

### HAFTA 4 - Testing & Optimization ✅
- Unit tests (80%+ coverage)
- Connection pooling
- Query optimization
- Performance baseline

## 🔒 Security Considerations

- Input validation with Jakarta Bean Validation
- SQL injection prevention with JPA/Hibernate
- Error message sanitization
- Connection pooling security

## 📊 Performance Optimization

### Database
- HikariCP connection pooling
- Optimized indexes
- Query result caching
- Batch insert/update operations

### Application
- Lazy loading for relationships
- Pagination for large datasets
- DTO pattern to reduce data transfer
- Efficient query design

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Team

Traffic Light System Team - Secure Coding Project

## 📧 Contact

For questions and support, please contact: support@trafficlight.com

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- PostgreSQL community for the robust database
- All contributors and testers

---

**⭐ Star this repo if you find it useful!**

