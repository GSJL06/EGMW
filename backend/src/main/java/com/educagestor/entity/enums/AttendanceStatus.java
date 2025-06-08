package com.educagestor.entity.enums;

/**
 * Enumeration for attendance status
 * 
 * Defines the different attendance states a student can have
 * for a particular class session.
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
public enum AttendanceStatus {
    /**
     * Student was present for the class
     */
    PRESENT("Present"),
    
    /**
     * Student was absent from the class
     */
    ABSENT("Absent"),
    
    /**
     * Student arrived late to the class
     */
    LATE("Late"),
    
    /**
     * Student was absent but with a valid excuse
     */
    EXCUSED("Excused");

    private final String displayName;

    /**
     * Constructor for AttendanceStatus enum
     * 
     * @param displayName human-readable name for the status
     */
    AttendanceStatus(String displayName) {
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
     * Get AttendanceStatus from string value
     * 
     * @param value string representation of the status
     * @return AttendanceStatus enum value
     * @throws IllegalArgumentException if value is not valid
     */
    public static AttendanceStatus fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Status value cannot be null");
        }
        
        try {
            return AttendanceStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid attendance status: " + value);
        }
    }

    /**
     * Check if this status counts as present
     * 
     * @return true if status is PRESENT or LATE
     */
    public boolean countsAsPresent() {
        return this == PRESENT || this == LATE;
    }

    /**
     * Check if this status counts as absent
     * 
     * @return true if status is ABSENT
     */
    public boolean countsAsAbsent() {
        return this == ABSENT;
    }

    /**
     * Check if this status is excused
     * 
     * @return true if status is EXCUSED
     */
    public boolean isExcused() {
        return this == EXCUSED;
    }

    /**
     * Get the CSS class for UI styling
     * 
     * @return CSS class name for styling
     */
    public String getCssClass() {
        return switch (this) {
            case PRESENT -> "text-green-600";
            case ABSENT -> "text-red-600";
            case LATE -> "text-yellow-600";
            case EXCUSED -> "text-blue-600";
        };
    }

    /**
     * Get the icon for UI representation
     * 
     * @return icon name
     */
    public String getIcon() {
        return switch (this) {
            case PRESENT -> "check-circle";
            case ABSENT -> "x-circle";
            case LATE -> "clock";
            case EXCUSED -> "info-circle";
        };
    }
}
