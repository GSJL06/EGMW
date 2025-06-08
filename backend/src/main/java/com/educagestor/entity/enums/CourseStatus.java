package com.educagestor.entity.enums;

/**
 * Enumeration for course status
 * 
 * Defines the different status states a course can have
 * throughout its lifecycle.
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
public enum CourseStatus {
    /**
     * Course is currently active and accepting enrollments
     */
    ACTIVE("Active"),
    
    /**
     * Course is temporarily inactive
     */
    INACTIVE("Inactive"),
    
    /**
     * Course has been completed
     */
    COMPLETED("Completed");

    private final String displayName;

    /**
     * Constructor for CourseStatus enum
     * 
     * @param displayName human-readable name for the status
     */
    CourseStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Get the display name for the status
     * 
     * @return display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get CourseStatus from string value
     * 
     * @param value string representation of the status
     * @return CourseStatus enum value
     * @throws IllegalArgumentException if value is not valid
     */
    public static CourseStatus fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Status value cannot be null");
        }
        
        try {
            return CourseStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid course status: " + value);
        }
    }

    /**
     * Check if this status represents an active course
     * 
     * @return true if status is ACTIVE
     */
    public boolean isActive() {
        return this == ACTIVE;
    }

    /**
     * Check if this status allows new enrollments
     * 
     * @return true if status allows enrollments
     */
    public boolean allowsEnrollments() {
        return this == ACTIVE;
    }

    /**
     * Check if this status allows teaching activities
     * 
     * @return true if status allows teaching
     */
    public boolean allowsTeaching() {
        return this == ACTIVE;
    }

    /**
     * Check if this status represents a completed course
     * 
     * @return true if status is COMPLETED
     */
    public boolean isCompleted() {
        return this == COMPLETED;
    }
}
