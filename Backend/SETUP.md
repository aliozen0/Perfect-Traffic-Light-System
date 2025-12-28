# 🔧 Setup Guide - Traffic Light Management System

This guide will help you set up and run the Traffic Light Management System.

## 📋 Prerequisites

Ensure you have the following installed:

- **Java 17** or higher ([Download](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html))
- **Maven 3.8+** ([Download](https://maven.apache.org/download.cgi))
- **PostgreSQL 15+** ([Download](https://www.postgresql.org/download/))
- **Docker & Docker Compose** (Optional - for containerized setup)
- **Git** ([Download](https://git-scm.com/downloads))
- **Your favorite IDE** (IntelliJ IDEA, Eclipse, VS Code)

## 🐳 Quick Start with Docker (Recommended)

### 1. Start PostgreSQL with Docker

```bash
# Start PostgreSQL and pgAdmin
docker-compose up -d

# Verify containers are running
docker ps
```

**Services:**
- PostgreSQL: `localhost:5432`
  - Database: `trafficlight_db`
  - Username: `postgres`
  - Password: `postgres`
  
- pgAdmin: `http://localhost:5050`
  - Email: `admin@trafficlight.com`
  - Password: `admin`

### 2. Build and Run Application

```bash
# Clone repository
git clone <repository-url>
cd traffic-light-system

# Build project
mvn clean install

# Run application
mvn spring-boot:run
```

### 3. Access Application

- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

## 💻 Manual Setup (Without Docker)

### 1. Install PostgreSQL

#### Windows:
1. Download installer from [PostgreSQL website](https://www.postgresql.org/download/windows/)
2. Run installer and follow wizard
3. Remember your superuser password

#### macOS:
```bash
brew install postgresql@15
brew services start postgresql@15
```

#### Linux (Ubuntu/Debian):
```bash
sudo apt update
sudo apt install postgresql-15
sudo systemctl start postgresql
```

### 2. Create Database

```bash
# Login to PostgreSQL
psql -U postgres

# Create database
CREATE DATABASE trafficlight_db;

# Verify
\l

# Exit
\q
```

### 3. Configure Application

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/trafficlight_db
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD
```

### 4. Build and Run

```bash
# Clean and build
mvn clean install

# Run application
mvn spring-boot:run

# Or run JAR directly
java -jar target/traffic-light-system-1.0.0.jar
```

## 🧪 Verify Installation

### 1. Check Health

```bash
curl http://localhost:8080/actuator/health
```

### 2. Test API

```bash
# Get all intersections
curl http://localhost:8080/api/intersections

# Should return paginated results with sample data
```

### 3. Access Swagger UI

Open browser: http://localhost:8080/swagger-ui.html

You should see the API documentation interface.

## 📊 Database Migration

Flyway will automatically run migrations on startup.

**Migration files:**
- `V1__Create_Intersection_Schema.sql` - Creates tables, indexes, triggers
- `V2__Insert_Sample_Data.sql` - Inserts sample data

### Verify Migrations

```bash
# Connect to database
psql -U postgres -d trafficlight_db

# Check tables
\dt

# You should see:
# - intersections
# - intersection_configs
# - intersection_metrics
# - intersection_phases
# - flyway_schema_history

# Check sample data
SELECT COUNT(*) FROM intersections;
# Should return 8 intersections
```

## 🔍 Troubleshooting

### Port Already in Use

If port 8080 is already in use:

```properties
# Change in application.properties
server.port=8081
```

### Database Connection Failed

1. Verify PostgreSQL is running:
```bash
# Windows
sc query postgresql-x64-15

# Linux/macOS
sudo systemctl status postgresql
# or
brew services list | grep postgres
```

2. Check connection settings in `application.properties`

3. Test connection manually:
```bash
psql -U postgres -d trafficlight_db
```

### Flyway Migration Errors

If migrations fail:

```sql
-- Connect to database
psql -U postgres -d trafficlight_db

-- Drop schema and retry
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;

-- Restart application
```

### Maven Build Errors

```bash
# Clear Maven cache
mvn clean

# Update dependencies
mvn dependency:purge-local-repository

# Rebuild
mvn clean install -U
```

## 🧪 Running Tests

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=IntersectionRepositoryTest

# Run tests with coverage report
mvn clean test jacoco:report

# View coverage report
# Open: target/site/jacoco/index.html
```

## 🌐 Development Profiles

### Test Profile

Uses H2 in-memory database:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=test
```

### Production Profile

Create `application-prod.properties`:

```properties
spring.datasource.url=jdbc:postgresql://production-host:5432/trafficlight_db
spring.datasource.username=prod_user
spring.datasource.password=prod_password
spring.jpa.show-sql=false
logging.level.root=WARN
```

Run with production profile:

```bash
java -jar target/traffic-light-system-1.0.0.jar --spring.profiles.active=prod
```

## 📝 IDE Setup

### IntelliJ IDEA

1. **Import Project:**
   - File → Open → Select `pom.xml`
   - Import as Maven project

2. **Enable Lombok:**
   - Install Lombok plugin
   - Settings → Build → Compiler → Annotation Processors
   - Enable annotation processing

3. **Run Configuration:**
   - Add new Spring Boot configuration
   - Main class: `com.trafficlight.TrafficLightApplication`

### Eclipse

1. **Import Project:**
   - File → Import → Maven → Existing Maven Projects
   - Select project directory

2. **Install Lombok:**
   - Download lombok.jar
   - Run: `java -jar lombok.jar`
   - Select Eclipse installation

3. **Run:**
   - Right-click `TrafficLightApplication.java`
   - Run As → Spring Boot App

### VS Code

1. **Install Extensions:**
   - Spring Boot Extension Pack
   - Java Extension Pack
   - Lombok Annotations Support

2. **Open Project:**
   - File → Open Folder
   - Select project directory

3. **Run:**
   - Press F5 or use Debug panel

## 🔐 Default Credentials

### Database
- **Host:** localhost:5432
- **Database:** trafficlight_db
- **Username:** postgres
- **Password:** postgres

### pgAdmin (Docker setup)
- **URL:** http://localhost:5050
- **Email:** admin@trafficlight.com
- **Password:** admin

## 📦 Building for Production

```bash
# Build JAR
mvn clean package -DskipTests

# The JAR will be in: target/traffic-light-system-1.0.0.jar

# Run JAR
java -jar target/traffic-light-system-1.0.0.jar
```

## 🚀 Next Steps

After successful setup:

1. ✅ Explore API with Swagger UI
2. ✅ Review sample data in database
3. ✅ Run test suite
4. ✅ Try example API calls (see README.md)
5. ✅ Read API documentation

## 📞 Support

If you encounter issues:

1. Check logs: `logs/spring-boot-logger.log`
2. Review application.properties configuration
3. Verify database connection
4. Check port availability
5. Review error messages in console

## 🎉 Success!

If you see this message, setup is complete:

```
╔════════════════════════════════════════════════════════════╗
║  Traffic Light Management System - STARTED                ║
║  Version: 1.0.0                                            ║
║  Swagger UI: http://localhost:8080/swagger-ui.html        ║
║  API Docs: http://localhost:8080/api-docs                 ║
╚════════════════════════════════════════════════════════════╝
```

Happy coding! 🚦

