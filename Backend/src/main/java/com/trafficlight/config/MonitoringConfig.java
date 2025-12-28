package com.trafficlight.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * HAFTA 5 - Monitoring Configuration
 * 
 * Features:
 * - Query performance monitoring (slow query alerts >1000ms)
 * - Connection pool monitoring
 * - Metrics dashboard support
 * - Health check configuration
 * - Performance metrics collection
 */
@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class MonitoringConfig {

    private final DataSource dataSource;
    private final MeterRegistry meterRegistry;
    
    private static final long SLOW_QUERY_THRESHOLD_MS = 1000; // 1 second

    /**
     * Enable @Timed annotation for method execution monitoring
     */
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    /**
     * Custom database health indicator
     */
    @Bean
    public HealthIndicator databaseHealthIndicator() {
        return () -> {
            try (Connection connection = dataSource.getConnection()) {
                long startTime = System.currentTimeMillis();
                boolean isValid = connection.isValid(5);
                long responseTime = System.currentTimeMillis() - startTime;
                
                // Record response time metric
                recordDatabaseResponseTime(responseTime);
                
                if (isValid) {
                    return org.springframework.boot.actuate.health.Health.up()
                        .withDetail("database", "PostgreSQL")
                        .withDetail("responseTime", responseTime + "ms")
                        .withDetail("status", "Connected")
                        .withDetail("timestamp", getCurrentTimestamp())
                        .build();
                } else {
                    return org.springframework.boot.actuate.health.Health.down()
                        .withDetail("database", "PostgreSQL")
                        .withDetail("status", "Connection invalid")
                        .withDetail("timestamp", getCurrentTimestamp())
                        .build();
                }
            } catch (Exception e) {
                log.error("Database health check failed", e);
                return org.springframework.boot.actuate.health.Health.down()
                    .withDetail("database", "PostgreSQL")
                    .withDetail("error", e.getMessage())
                    .withDetail("timestamp", getCurrentTimestamp())
                    .build();
            }
        };
    }

    /**
     * Scheduled task: Monitor database connection pool
     * Runs every 30 seconds
     */
    @Scheduled(fixedRate = 30000) // 30 seconds
    public void monitorConnectionPool() {
        try {
            try (Connection connection = dataSource.getConnection()) {
                long startTime = System.currentTimeMillis();
                connection.isValid(5);
                long responseTime = System.currentTimeMillis() - startTime;
                
                // Record metrics
                recordDatabaseResponseTime(responseTime);
                
                // Check for slow response
                if (responseTime > SLOW_QUERY_THRESHOLD_MS) {
                    log.warn("SLOW DATABASE RESPONSE: Connection validation took {}ms (threshold: {}ms)", 
                        responseTime, SLOW_QUERY_THRESHOLD_MS);
                }
                
                // Log connection pool status
                if (log.isDebugEnabled()) {
                    log.debug("Database connection pool check: OK ({}ms)", responseTime);
                }
            }
        } catch (Exception e) {
            log.error("Connection pool monitoring failed", e);
            recordDatabaseError();
        }
    }

    /**
     * Scheduled task: Monitor system metrics
     * Runs every 1 minute
     */
    @Scheduled(fixedRate = 60000) // 1 minute
    public void monitorSystemMetrics() {
        try {
            Runtime runtime = Runtime.getRuntime();
            
            // Memory metrics
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            long maxMemory = runtime.maxMemory();
            
            double memoryUsagePercent = (double) usedMemory / maxMemory * 100;
            
            // Record memory metrics
            meterRegistry.gauge("system.memory.total", totalMemory);
            meterRegistry.gauge("system.memory.free", freeMemory);
            meterRegistry.gauge("system.memory.used", usedMemory);
            meterRegistry.gauge("system.memory.max", maxMemory);
            meterRegistry.gauge("system.memory.usage.percent", memoryUsagePercent);
            
            // Log warnings for high memory usage
            if (memoryUsagePercent > 80) {
                log.warn("HIGH MEMORY USAGE: {:.2f}% of max memory ({}MB / {}MB)", 
                    memoryUsagePercent, 
                    usedMemory / (1024 * 1024), 
                    maxMemory / (1024 * 1024));
            }
            
            if (log.isDebugEnabled()) {
                log.debug("System metrics - Memory usage: {:.2f}% ({} MB / {} MB)", 
                    memoryUsagePercent,
                    usedMemory / (1024 * 1024),
                    maxMemory / (1024 * 1024));
            }
            
        } catch (Exception e) {
            log.error("System metrics monitoring failed", e);
        }
    }

    /**
     * Scheduled task: Log performance summary
     * Runs every 5 minutes
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void logPerformanceSummary() {
        try {
            Timer dbResponseTimer = meterRegistry.find("database.response.time").timer();
            
            if (dbResponseTimer != null) {
                long count = dbResponseTimer.count();
                double mean = dbResponseTimer.mean(TimeUnit.MILLISECONDS);
                double max = dbResponseTimer.max(TimeUnit.MILLISECONDS);
                
                log.info("=== PERFORMANCE SUMMARY (Last 5 minutes) ===");
                log.info("Database Response - Count: {}, Avg: {:.2f}ms, Max: {:.2f}ms", 
                    count, mean, max);
                
                // Check for performance issues
                if (mean > SLOW_QUERY_THRESHOLD_MS / 2) {
                    log.warn("PERFORMANCE WARNING: Average database response time is high: {:.2f}ms", mean);
                }
            }
            
            // Log application uptime
            long uptimeMillis = java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime();
            long hours = TimeUnit.MILLISECONDS.toHours(uptimeMillis);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(uptimeMillis) % 60;
            
            log.info("Application Uptime: {} hours, {} minutes", hours, minutes);
            log.info("==========================================");
            
        } catch (Exception e) {
            log.error("Performance summary logging failed", e);
        }
    }

    /**
     * Record database response time metric
     */
    private void recordDatabaseResponseTime(long responseTimeMs) {
        Timer.builder("database.response.time")
            .description("Database connection response time")
            .tag("type", "connection_validation")
            .register(meterRegistry)
            .record(responseTimeMs, TimeUnit.MILLISECONDS);
        
        // Alert on slow queries
        if (responseTimeMs > SLOW_QUERY_THRESHOLD_MS) {
            meterRegistry.counter("database.slow.queries", 
                "threshold", String.valueOf(SLOW_QUERY_THRESHOLD_MS))
                .increment();
        }
    }

    /**
     * Record database error metric
     */
    private void recordDatabaseError() {
        meterRegistry.counter("database.errors", 
            "type", "connection_failure")
            .increment();
    }

    /**
     * Get current timestamp
     */
    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    /**
     * Initialize custom metrics
     */
    @PostConstruct
    public void configureCustomMetrics() {
        // Register custom gauges
        meterRegistry.gauge("application.status", 1); // 1 = UP, 0 = DOWN
        
        log.info("Monitoring configuration initialized");
        log.info("- Slow query threshold: {}ms", SLOW_QUERY_THRESHOLD_MS);
        log.info("- Connection pool monitoring: Every 30 seconds");
        log.info("- System metrics monitoring: Every 1 minute");
        log.info("- Performance summary: Every 5 minutes");
    }
}
