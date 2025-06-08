package com.educagestor.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard error response structure
 * 
 * Provides a consistent format for error responses across the application.
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    /**
     * Timestamp when the error occurred
     */
    private LocalDateTime timestamp;

    /**
     * HTTP status code
     */
    private int status;

    /**
     * Error type or category
     */
    private String error;

    /**
     * Detailed error message
     */
    private String message;

    /**
     * Request path where the error occurred
     */
    private String path;

    /**
     * Validation errors (field-specific errors)
     */
    private Map<String, String> validationErrors;

    /**
     * Additional error details
     */
    private Map<String, Object> details;
}

/**
 * Custom exception for resource not found errors
 */
class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ResourceNotFoundException(String resourceType, String identifier) {
        super(String.format("%s not found with identifier: %s", resourceType, identifier));
    }
    
    public ResourceNotFoundException(String resourceType, String field, Object value) {
        super(String.format("%s not found with %s: %s", resourceType, field, value));
    }
}

/**
 * Custom exception for business logic errors
 */
class BusinessException extends RuntimeException {
    
    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Custom exception for duplicate resource errors
 */
class DuplicateResourceException extends BusinessException {
    
    public DuplicateResourceException(String message) {
        super(message);
    }
    
    public DuplicateResourceException(String resourceType, String field, Object value) {
        super(String.format("%s already exists with %s: %s", resourceType, field, value));
    }
}

/**
 * Custom exception for invalid operation errors
 */
class InvalidOperationException extends BusinessException {
    
    public InvalidOperationException(String message) {
        super(message);
    }
    
    public InvalidOperationException(String operation, String reason) {
        super(String.format("Cannot perform operation '%s': %s", operation, reason));
    }
}
