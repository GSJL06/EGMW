package com.educagestor.service;

import com.educagestor.dto.UserDto;
import com.educagestor.entity.User;
import com.educagestor.entity.enums.UserRole;
import com.educagestor.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .role(UserRole.STUDENT)
                .active(true)
                .build();
        testUser.setId(1L);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        testUserDto = UserDto.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .role(UserRole.STUDENT)
                .active(true)
                .build();
    }

    @Test
    void createUser_ShouldCreateUserSuccessfully() {
        // Given
        String plainPassword = "password123";
        String encodedPassword = "encodedPassword";
        
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(plainPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDto result = userService.createUser(testUserDto, plainPassword);

        // Then
        assertNotNull(result);
        assertEquals(testUserDto.getUsername(), result.getUsername());
        assertEquals(testUserDto.getEmail(), result.getEmail());
        assertEquals(testUserDto.getRole(), result.getRole());
        
        verify(userRepository).existsByUsername(testUserDto.getUsername());
        verify(userRepository).existsByEmail(testUserDto.getEmail());
        verify(passwordEncoder).encode(plainPassword);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_ShouldThrowException_WhenUsernameExists() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> userService.createUser(testUserDto, "password123"));
        
        assertTrue(exception.getMessage().contains("Username already exists"));
        verify(userRepository).existsByUsername(testUserDto.getUsername());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailExists() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> userService.createUser(testUserDto, "password123"));
        
        assertTrue(exception.getMessage().contains("Email already exists"));
        verify(userRepository).existsByUsername(testUserDto.getUsername());
        verify(userRepository).existsByEmail(testUserDto.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        Optional<UserDto> result = userService.getUserById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser.getUsername(), result.get().getUsername());
        assertEquals(testUser.getEmail(), result.get().getEmail());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_ShouldReturnEmpty_WhenUserNotExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<UserDto> result = userService.getUserById(1L);

        // Then
        assertFalse(result.isPresent());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserByUsername_ShouldReturnUser_WhenUserExists() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When
        Optional<UserDto> result = userService.getUserByUsername("testuser");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser.getUsername(), result.get().getUsername());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void getAllUsers_ShouldReturnPageOfUsers() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(testUser));
        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // When
        Page<UserDto> result = userService.getAllUsers(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testUser.getUsername(), result.getContent().get(0).getUsername());
        verify(userRepository).findAll(pageable);
    }

    @Test
    void getUsersByRole_ShouldReturnUsersWithSpecificRole() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(testUser));
        when(userRepository.findByRole(UserRole.STUDENT, pageable)).thenReturn(userPage);

        // When
        Page<UserDto> result = userService.getUsersByRole(UserRole.STUDENT, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(UserRole.STUDENT, result.getContent().get(0).getRole());
        verify(userRepository).findByRole(UserRole.STUDENT, pageable);
    }

    @Test
    void updateUser_ShouldUpdateUserSuccessfully() {
        // Given
        UserDto updateDto = UserDto.builder()
                .username("updateduser")
                .email("updated@example.com")
                .role(UserRole.TEACHER)
                .active(false)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername("updateduser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("updated@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDto result = userService.updateUser(1L, updateDto);

        // Then
        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> userService.updateUser(1L, testUserDto));
        
        assertTrue(exception.getMessage().contains("User not found"));
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_ShouldDeleteUserSuccessfully() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> userService.deleteUser(1L));
        
        assertTrue(exception.getMessage().contains("User not found"));
        verify(userRepository).existsById(1L);
        verify(userRepository, never()).deleteById(1L);
    }

    @Test
    void existsByUsername_ShouldReturnTrue_WhenUsernameExists() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // When
        boolean result = userService.existsByUsername("testuser");

        // Then
        assertTrue(result);
        verify(userRepository).existsByUsername("testuser");
    }

    @Test
    void existsByEmail_ShouldReturnTrue_WhenEmailExists() {
        // Given
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // When
        boolean result = userService.existsByEmail("test@example.com");

        // Then
        assertTrue(result);
        verify(userRepository).existsByEmail("test@example.com");
    }

    @Test
    void changePassword_ShouldChangePasswordSuccessfully() {
        // Given
        String currentPassword = "oldPassword";
        String newPassword = "newPassword";
        String encodedNewPassword = "encodedNewPassword";
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(currentPassword, testUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.changePassword(1L, currentPassword, newPassword);

        // Then
        verify(userRepository).findById(1L);
        verify(passwordEncoder).matches(currentPassword, "encodedPassword");
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void changePassword_ShouldThrowException_WhenCurrentPasswordIncorrect() {
        // Given
        String currentPassword = "wrongPassword";
        String newPassword = "newPassword";
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(currentPassword, testUser.getPassword())).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> userService.changePassword(1L, currentPassword, newPassword));
        
        assertTrue(exception.getMessage().contains("Current password is incorrect"));
        verify(userRepository).findById(1L);
        verify(passwordEncoder).matches(currentPassword, testUser.getPassword());
        verify(userRepository, never()).save(any(User.class));
    }
}
