package com.educagestor.entity.enums;

/**
 * Enumeration for user roles in the system
 * 
 * Defines the different types of users that can access the system
 * with their respective permissions and access levels.
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
public enum UserRole {
    /**
     * Administrator role with full system access
     */
    ADMIN("Administrator"),
    
    /**
     * Teacher role with access to course management and student data
     */
    TEACHER("Teacher"),
    
    /**
     * Student role with limited access to their own data
     */
    STUDENT("Student");

    private final String displayName;

    /**
     * Constructor for UserRole enum
     * 
     * @param displayName human-readable name for the role
     */
    UserRole(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Get the display name for the role
     * 
     * @return display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get UserRole from string value
     * 
     * @param value string representation of the role
     * @return UserRole enum value
     * @throws IllegalArgumentException if value is not valid
     */
    public static UserRole fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Role value cannot be null");
        }
        
        try {
            return UserRole.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + value);
        }
    }

    /**
     * Check if this role has administrative privileges
     * 
     * @return true if role is ADMIN
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }

    /**
     * Check if this role can manage courses
     * 
     * @return true if role is ADMIN or TEACHER
     */
    public boolean canManageCourses() {
        return this == ADMIN || this == TEACHER;
    }

    /**
     * Check if this role can view all student data
     * 
     * @return true if role is ADMIN or TEACHER
     */
    public boolean canViewAllStudents() {
        return this == ADMIN || this == TEACHER;
    }
}
