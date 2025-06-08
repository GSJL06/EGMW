package com.educagestor.entity.enums;

/**
 * Enumeration for teacher status
 * 
 * Defines the different status states a teacher can have
 * in the educational system.
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
public enum TeacherStatus {
    /**
     * Teacher is actively working and teaching
     */
    ACTIVE("Active"),
    
    /**
     * Teacher is temporarily inactive
     */
    INACTIVE("Inactive"),
    
    /**
     * Teacher is on leave (vacation, medical, etc.)
     */
    ON_LEAVE("On Leave");

    private final String displayName;

    /**
     * Constructor for TeacherStatus enum
     * 
     * @param displayName human-readable name for the status
     */
    TeacherStatus(String displayName) {
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
     * Get TeacherStatus from string value
     * 
     * @param value string representation of the status
     * @return TeacherStatus enum value
     * @throws IllegalArgumentException if value is not valid
     */
    public static TeacherStatus fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Status value cannot be null");
        }
        
        try {
            return TeacherStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid teacher status: " + value);
        }
    }

    /**
     * Check if this status allows the teacher to teach courses
     * 
     * @return true if status is ACTIVE
     */
    public boolean canTeachCourses() {
        return this == ACTIVE;
    }

    /**
     * Check if this status allows assignment to new courses
     * 
     * @return true if status is ACTIVE
     */
    public boolean canBeAssignedToCourses() {
        return this == ACTIVE;
    }
}
