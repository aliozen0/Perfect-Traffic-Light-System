package com.trafficlight;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Traffic Light Management System - Main Application
 * 
 * A comprehensive system for managing traffic light intersections,
 * including configuration, metrics, and phase management.
 * 
 * @author Traffic Light System Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
public class TrafficLightApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrafficLightApplication.class, args);
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  Traffic Light Management System - STARTED                ║");
        System.out.println("║  Version: 1.0.0                                            ║");
        System.out.println("║  Swagger UI: http://localhost:8080/swagger-ui.html        ║");
        System.out.println("║  API Docs: http://localhost:8080/api-docs                 ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }
}

