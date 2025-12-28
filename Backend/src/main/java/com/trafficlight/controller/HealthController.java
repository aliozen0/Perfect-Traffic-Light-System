package com.trafficlight.controller;

import com.trafficlight.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * HAFTA 5 - Health Check Endpoint
 * Database and system health monitoring
 * 
 * Endpoints:
 * - GET /api/health - Basic health check
 * - GET /api/health/database - Database connection health
 * - GET /api/health/detailed - Detailed health metrics
 */
@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
@Tag(name = "Health", description = "System Health Check API")
public class HealthController {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    /**
     * Basic health check
     */
    @GetMapping
    @Operation(summary = "Basic health check", description = "Check if the API is running")
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        Map<String, Object> healthData = new HashMap<>();
        healthData.put("status", "UP");
        healthData.put("service", "Traffic Light Management System");
        healthData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        healthData.put("version", "1.0.0");
        
        return ResponseEntity.ok(ApiResponse.success("System is healthy", healthData));
    }

    /**
     * Database health check
     */
    @GetMapping("/database")
    @Operation(summary = "Database health check", description = "Check database connection and status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> databaseHealth() {
        Map<String, Object> dbHealth = new HashMap<>();
        
        try {
            // Test database connection
            try (Connection connection = dataSource.getConnection()) {
                boolean isValid = connection.isValid(5); // 5 seconds timeout
                
                dbHealth.put("status", isValid ? "UP" : "DOWN");
                dbHealth.put("database", connection.getMetaData().getDatabaseProductName());
                dbHealth.put("databaseVersion", connection.getMetaData().getDatabaseProductVersion());
                dbHealth.put("driver", connection.getMetaData().getDriverName());
                dbHealth.put("driverVersion", connection.getMetaData().getDriverVersion());
                dbHealth.put("url", connection.getMetaData().getURL());
                dbHealth.put("readOnly", connection.isReadOnly());
                dbHealth.put("autoCommit", connection.getAutoCommit());
                
                // Test query execution
                long queryStartTime = System.currentTimeMillis();
                Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
                long queryDuration = System.currentTimeMillis() - queryStartTime;
                
                dbHealth.put("testQueryResult", result);
                dbHealth.put("testQueryDuration", queryDuration + "ms");
                
                // Get table counts
                dbHealth.put("tableStats", getTableStatistics());
                
                // Connection pool info (if using HikariCP)
                dbHealth.put("connectionPool", getConnectionPoolInfo());
                
                dbHealth.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
            
            return ResponseEntity.ok(ApiResponse.success("Database is healthy", dbHealth));
            
        } catch (Exception e) {
            dbHealth.put("status", "DOWN");
            dbHealth.put("error", e.getMessage());
            dbHealth.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            return ResponseEntity.status(503)
                .body(ApiResponse.error("Database health check failed", dbHealth));
        }
    }

    /**
     * Detailed health check with metrics
     */
    @GetMapping("/detailed")
    @Operation(summary = "Detailed health check", description = "Get detailed system and database health metrics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> detailedHealth() {
        Map<String, Object> detailedHealth = new HashMap<>();
        
        try {
            // System info
            Map<String, Object> systemInfo = new HashMap<>();
            systemInfo.put("javaVersion", System.getProperty("java.version"));
            systemInfo.put("javaVendor", System.getProperty("java.vendor"));
            systemInfo.put("osName", System.getProperty("os.name"));
            systemInfo.put("osVersion", System.getProperty("os.version"));
            systemInfo.put("osArchitecture", System.getProperty("os.arch"));
            
            // Memory info
            Runtime runtime = Runtime.getRuntime();
            Map<String, Object> memoryInfo = new HashMap<>();
            memoryInfo.put("totalMemory", formatBytes(runtime.totalMemory()));
            memoryInfo.put("freeMemory", formatBytes(runtime.freeMemory()));
            memoryInfo.put("usedMemory", formatBytes(runtime.totalMemory() - runtime.freeMemory()));
            memoryInfo.put("maxMemory", formatBytes(runtime.maxMemory()));
            memoryInfo.put("availableProcessors", runtime.availableProcessors());
            
            // Database info
            Map<String, Object> dbInfo = new HashMap<>();
            try (Connection connection = dataSource.getConnection()) {
                dbInfo.put("status", connection.isValid(5) ? "UP" : "DOWN");
                dbInfo.put("database", connection.getMetaData().getDatabaseProductName());
                dbInfo.put("version", connection.getMetaData().getDatabaseProductVersion());
                
                // Table statistics
                dbInfo.put("tables", getTableStatistics());
                
                // Connection pool
                dbInfo.put("connectionPool", getConnectionPoolInfo());
                
                // Active connections
                dbInfo.put("activeConnections", getActiveConnectionsCount());
                
            } catch (Exception e) {
                dbInfo.put("status", "DOWN");
                dbInfo.put("error", e.getMessage());
            }
            
            // Application info
            Map<String, Object> appInfo = new HashMap<>();
            appInfo.put("name", "Traffic Light Management System");
            appInfo.put("version", "1.0.0");
            appInfo.put("uptime", getUptime());
            appInfo.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            // Combine all info
            detailedHealth.put("status", "UP");
            detailedHealth.put("system", systemInfo);
            detailedHealth.put("memory", memoryInfo);
            detailedHealth.put("database", dbInfo);
            detailedHealth.put("application", appInfo);
            
            return ResponseEntity.ok(ApiResponse.success("System health check completed", detailedHealth));
            
        } catch (Exception e) {
            detailedHealth.put("status", "DOWN");
            detailedHealth.put("error", e.getMessage());
            
            return ResponseEntity.status(503)
                .body(ApiResponse.error("Health check failed", detailedHealth));
        }
    }


    /**
     * Get table statistics
     */
    private Map<String, Long> getTableStatistics() {
        Map<String, Long> tableStats = new HashMap<>();
        
        try {
            tableStats.put("intersections", jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM intersections", Long.class));
            tableStats.put("intersection_configs", jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM intersection_configs", Long.class));
            tableStats.put("intersection_metrics", jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM intersection_metrics", Long.class));
            tableStats.put("intersection_phases", jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM intersection_phases", Long.class));
        } catch (Exception e) {
            tableStats.put("error", -1L);
        }
        
        return tableStats;
    }

    /**
     * Get connection pool information
     */
    private Map<String, Object> getConnectionPoolInfo() {
        Map<String, Object> poolInfo = new HashMap<>();
        
        try {
            // Try to get HikariCP specific info
            if (dataSource.getClass().getName().contains("HikariDataSource")) {
                poolInfo.put("type", "HikariCP");
                poolInfo.put("status", "Active");
                // Note: More detailed pool stats would require HikariDataSource specific imports
            } else {
                poolInfo.put("type", dataSource.getClass().getSimpleName());
                poolInfo.put("status", "Active");
            }
        } catch (Exception e) {
            poolInfo.put("error", e.getMessage());
        }
        
        return poolInfo;
    }

    /**
     * Get active connections count
     */
    private int getActiveConnectionsCount() {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM pg_stat_activity WHERE state = 'active'", 
                Integer.class
            );
            return count != null ? count : 0;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Format bytes to human-readable format
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.2f %sB", bytes / Math.pow(1024, exp), pre);
    }

    /**
     * Get application uptime
     */
    private String getUptime() {
        long uptimeMillis = java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime();
        long seconds = uptimeMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        return String.format("%d days, %d hours, %d minutes", 
            days, hours % 24, minutes % 60);
    }
}
