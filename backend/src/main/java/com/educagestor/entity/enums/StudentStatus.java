package com.educagestor.entity.enums;

/**
 * Enumeration for student status
 * 
 * Defines the different status states a student can have
 * in the educational system.
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
public enum StudentStatus {
    /**
     * Student is actively enrolled and attending classes
     */
    ACTIVE("Active"),
    
    /**
     * Student is temporarily inactive
     */
    INACTIVE("Inactive"),
    
    /**
     * Student has completed their studies
     */
    GRADUATED("Graduated"),
    
    /**
     * Student is temporarily suspended
     */
    SUSPENDED("Suspended");

    private final String displayName;

    /**
     * Constructor for StudentStatus enum
     * 
     * @param displayName human-readable name for the status
     */
    StudentStatus(String displayName) {
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
     * Get StudentStatus from string value
     * 
     * @param value string representation of the status
     * @return StudentStatus enum value
     * @throws IllegalArgumentException if value is not valid
     */
    public static StudentStatus fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Status value cannot be null");
        }
        
        try {
            return StudentStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid student status: " + value);
        }
    }

    /**
     * Check if this status allows the student to attend classes
     * 
     * @return true if status is ACTIVE
     */
    public boolean canAttendClasses() {
        return this == ACTIVE;
    }

    /**
     * Check if this status allows enrollment in new courses
     * 
     * @return true if status is ACTIVE
     */
    public boolean canEnrollInCourses() {
        return this == ACTIVE;
    }
}
