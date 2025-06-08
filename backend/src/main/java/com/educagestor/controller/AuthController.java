package com.educagestor.controller;

import com.educagestor.dto.AuthDto;
import com.educagestor.entity.User;
import com.educagestor.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for authentication and authorization operations
 * 
 * Handles user login, registration, token refresh, password management,
 * and user profile operations.
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication and authorization operations")
public class AuthController {

    private final AuthService authService;

    /**
     * User login endpoint
     * 
     * @param loginRequest the login credentials
     * @return authentication response with tokens
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return access tokens")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<AuthDto.AuthResponse> login(@Valid @RequestBody AuthDto.LoginRequest loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.getUsernameOrEmail());
        
        try {
            AuthDto.AuthResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Login failed for user: {}", loginRequest.getUsernameOrEmail(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * User registration endpoint
     * 
     * @param registerRequest the registration data
     * @return authentication response with tokens
     */
    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register new user and return access tokens")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Registration successful"),
        @ApiResponse(responseCode = "400", description = "Invalid request data or user already exists"),
        @ApiResponse(responseCode = "409", description = "Username or email already exists")
    })
    public ResponseEntity<AuthDto.AuthResponse> register(@Valid @RequestBody AuthDto.RegisterRequest registerRequest) {
        log.info("Registration attempt for user: {}", registerRequest.getUsername());
        
        try {
            AuthDto.AuthResponse response = authService.register(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Registration failed for user: {}", registerRequest.getUsername(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Token refresh endpoint
     * 
     * @param refreshTokenRequest the refresh token
     * @return new authentication response
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Generate new access token using refresh token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token refresh successful"),
        @ApiResponse(responseCode = "401", description = "Invalid refresh token"),
        @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<AuthDto.AuthResponse> refreshToken(@Valid @RequestBody AuthDto.RefreshTokenRequest refreshTokenRequest) {
        log.debug("Token refresh attempt");
        
        try {
            AuthDto.AuthResponse response = authService.refreshToken(refreshTokenRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Token refresh failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Change password endpoint
     * 
     * @param changePasswordRequest the password change data
     * @param currentUser the current authenticated user
     * @return success response
     */
    @PostMapping("/change-password")
    @Operation(summary = "Change password", description = "Change password for authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password changed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data or current password incorrect"),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody AuthDto.ChangePasswordRequest changePasswordRequest,
            @AuthenticationPrincipal User currentUser) {
        
        log.info("Password change attempt for user: {}", currentUser.getUsername());
        
        try {
            authService.changePassword(changePasswordRequest, currentUser);
            return ResponseEntity.ok("Password changed successfully");
        } catch (Exception e) {
            log.error("Password change failed for user: {}", currentUser.getUsername(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Get current user profile
     * 
     * @param currentUser the current authenticated user
     * @return user profile information
     */
    @GetMapping("/profile")
    @Operation(summary = "Get user profile", description = "Get current user profile information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<AuthDto.UserProfile> getProfile(@AuthenticationPrincipal User currentUser) {
        log.debug("Profile request for user: {}", currentUser.getUsername());
        
        AuthDto.UserProfile profile = authService.getCurrentUserProfile(currentUser);
        return ResponseEntity.ok(profile);
    }

    /**
     * Update user profile
     * 
     * @param updateRequest the profile update data
     * @param currentUser the current authenticated user
     * @return updated user profile
     */
    @PutMapping("/profile")
    @Operation(summary = "Update user profile", description = "Update current user profile information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data or username/email already exists"),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<AuthDto.UserProfile> updateProfile(
            @Valid @RequestBody AuthDto.UpdateProfileRequest updateRequest,
            @AuthenticationPrincipal User currentUser) {
        
        log.info("Profile update attempt for user: {}", currentUser.getUsername());
        
        try {
            AuthDto.UserProfile updatedProfile = authService.updateProfile(updateRequest, currentUser);
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            log.error("Profile update failed for user: {}", currentUser.getUsername(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Logout endpoint
     * 
     * @param logoutRequest the logout data (optional refresh token)
     * @return success response
     */
    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout user and invalidate tokens")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logout successful"),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<String> logout(@RequestBody(required = false) AuthDto.LogoutRequest logoutRequest) {
        log.debug("Logout request");
        
        // In a stateless JWT implementation, logout is typically handled client-side
        // by removing the tokens. Server-side token blacklisting could be implemented here.
        
        return ResponseEntity.ok("Logout successful");
    }

    /**
     * Health check endpoint for authentication service
     * 
     * @return health status
     */
    @GetMapping("/health")
    @Operation(summary = "Authentication service health check", description = "Check if authentication service is running")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Authentication service is running");
    }
}
