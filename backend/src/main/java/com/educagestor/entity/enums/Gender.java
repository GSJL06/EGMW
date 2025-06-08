package com.educagestor.entity.enums;

/**
 * Enumeration for gender options
 * 
 * Defines the available gender options for students and teachers
 * in the system.
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
public enum Gender {
    /**
     * Male gender
     */
    MALE("Male"),
    
    /**
     * Female gender
     */
    FEMALE("Female"),
    
    /**
     * Other gender option
     */
    OTHER("Other");

    private final String displayName;

    /**
     * Constructor for Gender enum
     * 
     * @param displayName human-readable name for the gender
     */
    Gender(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Get the display name for the gender
     * 
     * @return display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get Gender from string value
     * 
     * @param value string representation of the gender
     * @return Gender enum value
     * @throws IllegalArgumentException if value is not valid
     */
    public static Gender fromString(String value) {
        if (value == null) {
            return null;
        }
        
        try {
            return Gender.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid gender: " + value);
        }
    }
}
