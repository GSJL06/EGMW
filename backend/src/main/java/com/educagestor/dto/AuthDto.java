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

/**
 * DTOs for authentication and authorization
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
public class AuthDto {

    /**
     * DTO for login requests
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginRequest {

        @NotBlank(message = "Username or email is required")
        private String usernameOrEmail;

        @NotBlank(message = "Password is required")
        private String password;
    }

    /**
     * DTO for registration requests
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RegisterRequest {

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

        @NotBlank(message = "Confirm password is required")
        private String confirmPassword;

        @NotNull(message = "Role is required")
        private UserRole role;
    }

    /**
     * DTO for authentication responses
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AuthResponse {

        private String accessToken;
        private String refreshToken;
        private String tokenType = "Bearer";
        private Long expiresIn;
        private UserInfo user;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class UserInfo {
            private Long id;
            private String username;
            private String email;
            private UserRole role;
            private Boolean active;
        }
    }

    /**
     * DTO for token refresh requests
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefreshTokenRequest {

        @NotBlank(message = "Refresh token is required")
        private String refreshToken;
    }

    /**
     * DTO for password reset requests
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ForgotPasswordRequest {

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        private String email;
    }

    /**
     * DTO for password reset confirmation
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResetPasswordRequest {

        @NotBlank(message = "Reset token is required")
        private String resetToken;

        @NotBlank(message = "New password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        private String newPassword;

        @NotBlank(message = "Confirm password is required")
        private String confirmPassword;
    }

    /**
     * DTO for password change requests (authenticated users)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChangePasswordRequest {

        @NotBlank(message = "Current password is required")
        private String currentPassword;

        @NotBlank(message = "New password is required")
        @Size(min = 8, message = "New password must be at least 8 characters")
        private String newPassword;

        @NotBlank(message = "Confirm password is required")
        private String confirmPassword;
    }

    /**
     * DTO for logout requests
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LogoutRequest {

        private String refreshToken;
    }

    /**
     * DTO for JWT token validation
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TokenValidationResponse {

        private boolean valid;
        private String username;
        private UserRole role;
        private Long expiresAt;
        private String message;
    }

    /**
     * DTO for user profile information
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserProfile {

        private Long id;
        private String username;
        private String email;
        private UserRole role;
        private Boolean active;
        private String displayName;

        // Additional profile information based on role
        private Long studentId;
        private Long teacherId;
        private String studentCode;
        private String employeeCode;
        private String fullName;
        private String institutionName;
    }

    /**
     * DTO for updating user profile
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateProfileRequest {

        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        private String username;

        @Email(message = "Email must be valid")
        @Size(max = 255, message = "Email must not exceed 255 characters")
        private String email;
    }
}
