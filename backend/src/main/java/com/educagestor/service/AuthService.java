package com.educagestor.service;

import com.educagestor.dto.AuthDto;
import com.educagestor.entity.User;
import com.educagestor.entity.enums.UserRole;
import com.educagestor.repository.UserRepository;
import com.educagestor.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for authentication and authorization operations
 * 
 * Handles user login, registration, token management, and password operations.
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Authenticate user and generate tokens
     * 
     * @param loginRequest the login credentials
     * @return authentication response with tokens
     */
    public AuthDto.AuthResponse login(AuthDto.LoginRequest loginRequest) {
        log.info("Attempting login for user: {}", loginRequest.getUsernameOrEmail());
        
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Get user details
        User user = userRepository.findByUsernameOrEmail(
                loginRequest.getUsernameOrEmail(),
                loginRequest.getUsernameOrEmail()
        ).orElseThrow(() -> new RuntimeException("User not found"));
        
        // Generate tokens
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
        
        log.info("User logged in successfully: {}", user.getUsername());
        
        return AuthDto.AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpirationTime())
                .user(AuthDto.AuthResponse.UserInfo.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .active(user.getActive())
                        .build())
                .build();
    }

    /**
     * Register a new user
     * 
     * @param registerRequest the registration data
     * @return authentication response with tokens
     */
    public AuthDto.AuthResponse register(AuthDto.RegisterRequest registerRequest) {
        log.info("Attempting registration for user: {}", registerRequest.getUsername());
        
        // Validate registration data
        validateRegistrationData(registerRequest);
        
        // Create new user
        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(registerRequest.getRole())
                .active(true)
                .build();
        
        User savedUser = userRepository.save(user);
        
        // Authenticate the new user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        registerRequest.getUsername(),
                        registerRequest.getPassword()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Generate tokens
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
        
        log.info("User registered successfully: {}", savedUser.getUsername());
        
        return AuthDto.AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpirationTime())
                .user(AuthDto.AuthResponse.UserInfo.builder()
                        .id(savedUser.getId())
                        .username(savedUser.getUsername())
                        .email(savedUser.getEmail())
                        .role(savedUser.getRole())
                        .active(savedUser.getActive())
                        .build())
                .build();
    }

    /**
     * Refresh access token using refresh token
     * 
     * @param refreshTokenRequest the refresh token request
     * @return new authentication response
     */
    public AuthDto.AuthResponse refreshToken(AuthDto.RefreshTokenRequest refreshTokenRequest) {
        log.debug("Attempting token refresh");
        
        String refreshToken = refreshTokenRequest.getRefreshToken();
        
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }
        
        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!user.getActive()) {
            throw new RuntimeException("User account is deactivated");
        }
        
        // Create authentication object
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities()
        );
        
        // Generate new tokens
        String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(authentication);
        
        log.debug("Token refreshed successfully for user: {}", username);
        
        return AuthDto.AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpirationTime())
                .user(AuthDto.AuthResponse.UserInfo.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .active(user.getActive())
                        .build())
                .build();
    }

    /**
     * Change user password
     * 
     * @param changePasswordRequest the password change request
     * @param currentUser the current authenticated user
     */
    public void changePassword(AuthDto.ChangePasswordRequest changePasswordRequest, User currentUser) {
        log.info("Changing password for user: {}", currentUser.getUsername());
        
        // Validate current password
        if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), currentUser.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        // Validate new password confirmation
        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
            throw new RuntimeException("New password and confirmation do not match");
        }
        
        // Update password
        currentUser.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(currentUser);
        
        log.info("Password changed successfully for user: {}", currentUser.getUsername());
    }

    /**
     * Get current user profile
     * 
     * @param currentUser the current authenticated user
     * @return user profile information
     */
    @Transactional(readOnly = true)
    public AuthDto.UserProfile getCurrentUserProfile(User currentUser) {
        log.debug("Getting profile for user: {}", currentUser.getUsername());
        
        return AuthDto.UserProfile.builder()
                .id(currentUser.getId())
                .username(currentUser.getUsername())
                .email(currentUser.getEmail())
                .role(currentUser.getRole())
                .active(currentUser.getActive())
                .displayName(currentUser.getDisplayName())
                .build();
    }

    /**
     * Update user profile
     * 
     * @param updateRequest the profile update request
     * @param currentUser the current authenticated user
     * @return updated user profile
     */
    public AuthDto.UserProfile updateProfile(AuthDto.UpdateProfileRequest updateRequest, User currentUser) {
        log.info("Updating profile for user: {}", currentUser.getUsername());
        
        boolean updated = false;
        
        // Update username if provided and different
        if (updateRequest.getUsername() != null && !updateRequest.getUsername().equals(currentUser.getUsername())) {
            if (userRepository.existsByUsername(updateRequest.getUsername())) {
                throw new RuntimeException("Username already exists: " + updateRequest.getUsername());
            }
            currentUser.setUsername(updateRequest.getUsername());
            updated = true;
        }
        
        // Update email if provided and different
        if (updateRequest.getEmail() != null && !updateRequest.getEmail().equals(currentUser.getEmail())) {
            if (userRepository.existsByEmail(updateRequest.getEmail())) {
                throw new RuntimeException("Email already exists: " + updateRequest.getEmail());
            }
            currentUser.setEmail(updateRequest.getEmail());
            updated = true;
        }
        
        if (updated) {
            userRepository.save(currentUser);
            log.info("Profile updated successfully for user: {}", currentUser.getUsername());
        }
        
        return getCurrentUserProfile(currentUser);
    }

    /**
     * Validate registration data
     * 
     * @param registerRequest the registration request to validate
     */
    private void validateRegistrationData(AuthDto.RegisterRequest registerRequest) {
        // Check if passwords match
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new RuntimeException("Password and confirmation do not match");
        }
        
        // Check if username already exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username already exists: " + registerRequest.getUsername());
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email already exists: " + registerRequest.getEmail());
        }
    }
}
