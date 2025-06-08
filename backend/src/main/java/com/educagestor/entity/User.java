package com.educagestor.entity;

import com.educagestor.entity.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * User entity representing system users
 * 
 * This entity implements UserDetails for Spring Security integration
 * and represents all users in the system including admins, teachers, and students.
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_username", columnList = "username"),
    @Index(name = "idx_users_email", columnList = "email"),
    @Index(name = "idx_users_role", columnList = "role")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity implements UserDetails {

    /**
     * Unique username for the user
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    /**
     * User's email address
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * Encrypted password
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * User's role in the system
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private UserRole role;

    /**
     * Whether the user account is active
     */
    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    // UserDetails implementation methods

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return active;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return active;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

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
