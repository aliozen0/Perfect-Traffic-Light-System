package com.trafficlight.exception;

/**
 * HAFTA 3 - Exception Handling
 * Exception thrown for invalid request data (400)
 */
public class BadRequestException extends RuntimeException {
    
    public BadRequestException(String message) {
        super(message);
    }
}

