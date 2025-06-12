package com.educagestor.service;

import com.educagestor.dto.UserDto;
import com.educagestor.entity.User;
import com.educagestor.entity.enums.UserRole;
import com.educagestor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for User entity operations
 * 
 * Provides business logic for user management including CRUD operations,
 * authentication support, and user search functionality.
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Load user by username for Spring Security authentication
     * 
     * @param username the username to load
     * @return UserDetails for authentication
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);
        
        return userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    /**
     * Create a new user
     * 
     * @param userDto the user data
     * @param password the plain text password
     * @return created user DTO
     */
    public UserDto createUser(UserDto userDto, String password) {
        log.info("Creating new user: {}", userDto.getUsername());
        
        validateUserData(userDto);
        
        User user = User.builder()
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(password))
                .role(userDto.getRole())
                .active(userDto.getActive())
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getId());
        
        return convertToDto(savedUser);
    }

    /**
     * Get user by ID
     * 
     * @param id the user ID
     * @return user DTO if found
     */
    @Transactional(readOnly = true)
    public Optional<UserDto> getUserById(Long id) {
        log.debug("Getting user by ID: {}", id);
        
        return userRepository.findById(id)
                .map(this::convertToDto);
    }

    /**
     * Get user by username
     * 
     * @param username the username
     * @return user DTO if found
     */
    @Transactional(readOnly = true)
    public Optional<UserDto> getUserByUsername(String username) {
        log.debug("Getting user by username: {}", username);
        
        return userRepository.findByUsername(username)
                .map(this::convertToDto);
    }

    /**
     * Get user by email
     * 
     * @param email the email
     * @return user DTO if found
     */
    @Transactional(readOnly = true)
    public Optional<UserDto> getUserByEmail(String email) {
        log.debug("Getting user by email: {}", email);
        
        return userRepository.findByEmail(email)
                .map(this::convertToDto);
    }

    /**
     * Get all users with pagination
     * 
     * @param pageable pagination information
     * @return page of user DTOs
     */
    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(Pageable pageable) {
        log.debug("Getting all users with pagination");
        
        return userRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    /**
     * Get users by role
     * 
     * @param role the user role
     * @param pageable pagination information
     * @return page of user DTOs
     */
    @Transactional(readOnly = true)
    public Page<UserDto> getUsersByRole(UserRole role, Pageable pageable) {
        log.debug("Getting users by role: {}", role);
        
        return userRepository.findByRole(role, pageable)
                .map(this::convertToDto);
    }

    /**
     * Get active users
     * 
     * @param pageable pagination information
     * @return page of active user DTOs
     */
    @Transactional(readOnly = true)
    public Page<UserDto> getActiveUsers(Pageable pageable) {
        log.debug("Getting active users");
        
        return userRepository.findByActiveTrue(pageable)
                .map(this::convertToDto);
    }

    /**
     * Search users by username or email
     * 
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return page of matching user DTOs
     */
    @Transactional(readOnly = true)
    public Page<UserDto> searchUsers(String searchTerm, Pageable pageable) {
        log.debug("Searching users with term: {}", searchTerm);
        
        return userRepository.searchByUsernameOrEmail(searchTerm, pageable)
                .map(this::convertToDto);
    }

    /**
     * Update user
     * 
     * @param id the user ID
     * @param userDto the updated user data
     * @return updated user DTO
     */
    public UserDto updateUser(Long id, UserDto userDto) {
        log.info("Updating user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        
        // Update fields if provided
        if (userDto.getUsername() != null) {
            validateUsernameUnique(userDto.getUsername(), id);
            user.setUsername(userDto.getUsername());
        }
        
        if (userDto.getEmail() != null) {
            validateEmailUnique(userDto.getEmail(), id);
            user.setEmail(userDto.getEmail());
        }
        
        if (userDto.getRole() != null) {
            user.setRole(userDto.getRole());
        }
        
        if (userDto.getActive() != null) {
            user.setActive(userDto.getActive());
        }
        
        User updatedUser = userRepository.save(user);
        log.info("User updated successfully: {}", updatedUser.getUsername());
        
        return convertToDto(updatedUser);
    }

    /**
     * Change user password
     * 
     * @param id the user ID
     * @param currentPassword the current password
     * @param newPassword the new password
     */
    public void changePassword(Long id, String currentPassword, String newPassword) {
        log.info("Changing password for user ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        log.info("Password changed successfully for user: {}", user.getUsername());
    }

    /**
     * Activate or deactivate user
     * 
     * @param id the user ID
     * @param active the active status
     */
    public void setUserActive(Long id, boolean active) {
        log.info("Setting user {} active status to: {}", id, active);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        
        user.setActive(active);
        userRepository.save(user);
        
        log.info("User active status updated: {}", user.getUsername());
    }

    /**
     * Delete user
     * 
     * @param id the user ID
     */
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with ID: " + id);
        }
        
        userRepository.deleteById(id);
        log.info("User deleted successfully with ID: {}", id);
    }

    /**
     * Check if username exists
     * 
     * @param username the username to check
     * @return true if username exists
     */
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Check if email exists
     * 
     * @param email the email to check
     * @return true if email exists
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Get user count by role
     *
     * @param role the user role
     * @return count of users with the role
     */
    @Transactional(readOnly = true)
    public long countByRole(UserRole role) {
        return userRepository.countByRole(role);
    }

    /**
     * Get recently created users
     *
     * @param days number of days to look back
     * @return list of recently created user DTOs
     */
    @Transactional(readOnly = true)
    public List<UserDto> getRecentUsers(int days) {
        log.debug("Getting users created in the last {} days", days);

        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        return userRepository.findRecentUsers(startDate)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    /**
     * Convert User entity to UserDto
     * 
     * @param user the user entity
     * @return user DTO
     */
    private UserDto convertToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * Validate user data
     * 
     * @param userDto the user data to validate
     */
    private void validateUserData(UserDto userDto) {
        if (existsByUsername(userDto.getUsername())) {
            throw new RuntimeException("Username already exists: " + userDto.getUsername());
        }
        
        if (existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Email already exists: " + userDto.getEmail());
        }
    }

    /**
     * Validate username uniqueness for updates
     * 
     * @param username the username to check
     * @param userId the user ID to exclude from check
     */
    private void validateUsernameUnique(String username, Long userId) {
        userRepository.findByUsername(username)
                .ifPresent(user -> {
                    if (!user.getId().equals(userId)) {
                        throw new RuntimeException("Username already exists: " + username);
                    }
                });
    }

    /**
     * Validate email uniqueness for updates
     * 
     * @param email the email to check
     * @param userId the user ID to exclude from check
     */
    private void validateEmailUnique(String email, Long userId) {
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    if (!user.getId().equals(userId)) {
                        throw new RuntimeException("Email already exists: " + email);
                    }
                });
    }
}
