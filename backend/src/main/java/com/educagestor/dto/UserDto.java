package com.educagestor.dto;

import com.educagestor.entity.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for User entity
 * 
 * Used for transferring user data between layers without exposing
 * sensitive information like passwords.
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @NotNull(message = "Role is required")
    private UserRole role;

    @Builder.Default
    private Boolean active = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Get display name for the user
     * 
     * @return username as display name
     */
    public String getDisplayName() {
        return username;
    }

    /**
     * Check if user has a specific role
     * 
     * @param userRole the role to check
     * @return true if user has the role
     */
    public boolean hasRole(UserRole userRole) {
        return this.role == userRole;
    }

    /**
     * Check if user is an admin
     * 
     * @return true if user is admin
     */
    public boolean isAdmin() {
        return hasRole(UserRole.ADMIN);
    }

    /**
     * Check if user is a teacher
     * 
     * @return true if user is teacher
     */
    public boolean isTeacher() {
        return hasRole(UserRole.TEACHER);
    }

    /**
     * Check if user is a student
     * 
     * @return true if user is student
     */
    public boolean isStudent() {
        return hasRole(UserRole.STUDENT);
    }
}

/**
 * DTO for user creation requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class CreateUserDto {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotNull(message = "Role is required")
    private UserRole role;

    @Builder.Default
    private Boolean active = true;
}

/**
 * DTO for user update requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class UpdateUserDto {

    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    private UserRole role;
    private Boolean active;
}

/**
 * DTO for password change requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class ChangePasswordDto {

    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "New password must be at least 8 characters")
    private String newPassword;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}
