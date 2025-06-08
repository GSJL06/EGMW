package com.educagestor.entity.enums;

/**
 * Enumeration for enrollment status
 * 
 * Defines the different status states an enrollment can have
 * representing the student's progress in a course.
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
public enum EnrollmentStatus {
    /**
     * Student is currently enrolled and attending the course
     */
    ENROLLED("Enrolled"),
    
    /**
     * Student has successfully completed the course
     */
    COMPLETED("Completed"),
    
    /**
     * Student has dropped out of the course
     */
    DROPPED("Dropped"),
    
    /**
     * Student has failed the course
     */
    FAILED("Failed");

    private final String displayName;

    /**
     * Constructor for EnrollmentStatus enum
     * 
     * @param displayName human-readable name for the status
     */
    EnrollmentStatus(String displayName) {
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
     * Get EnrollmentStatus from string value
     * 
     * @param value string representation of the status
     * @return EnrollmentStatus enum value
     * @throws IllegalArgumentException if value is not valid
     */
    public static EnrollmentStatus fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Status value cannot be null");
        }
        
        try {
            return EnrollmentStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid enrollment status: " + value);
        }
    }

    /**
     * Check if this status represents an active enrollment
     * 
     * @return true if status is ENROLLED
     */
    public boolean isActive() {
        return this == ENROLLED;
    }

    /**
     * Check if this status represents a completed enrollment
     * 
     * @return true if status is COMPLETED
     */
    public boolean isCompleted() {
        return this == COMPLETED;
    }

    /**
     * Check if this status represents a terminated enrollment
     * 
     * @return true if status is DROPPED or FAILED
     */
    public boolean isTerminated() {
        return this == DROPPED || this == FAILED;
    }

    /**
     * Check if grades can be assigned for this enrollment status
     * 
     * @return true if status allows grading
     */
    public boolean allowsGrading() {
        return this == ENROLLED || this == COMPLETED || this == FAILED;
    }

    /**
     * Check if attendance can be recorded for this enrollment status
     * 
     * @return true if status allows attendance recording
     */
    public boolean allowsAttendance() {
        return this == ENROLLED;
    }
}
