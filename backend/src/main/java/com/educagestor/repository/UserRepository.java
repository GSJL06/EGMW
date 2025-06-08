package com.educagestor.repository;

import com.educagestor.entity.User;
import com.educagestor.entity.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity
 * 
 * Provides data access methods for user management including
 * authentication, role-based queries, and user search functionality.
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username
     * 
     * @param username the username to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email
     * 
     * @param email the email to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by username or email
     * 
     * @param username the username to search for
     * @param email the email to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByUsernameOrEmail(String username, String email);

    /**
     * Check if username exists
     * 
     * @param username the username to check
     * @return true if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists
     * 
     * @param email the email to check
     * @return true if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Find users by role
     * 
     * @param role the user role to filter by
     * @return list of users with the specified role
     */
    List<User> findByRole(UserRole role);

    /**
     * Find users by role with pagination
     * 
     * @param role the user role to filter by
     * @param pageable pagination information
     * @return page of users with the specified role
     */
    Page<User> findByRole(UserRole role, Pageable pageable);

    /**
     * Find active users
     * 
     * @return list of active users
     */
    List<User> findByActiveTrue();

    /**
     * Find active users with pagination
     * 
     * @param pageable pagination information
     * @return page of active users
     */
    Page<User> findByActiveTrue(Pageable pageable);

    /**
     * Find inactive users
     * 
     * @return list of inactive users
     */
    List<User> findByActiveFalse();

    /**
     * Find users by role and active status
     * 
     * @param role the user role to filter by
     * @param active the active status to filter by
     * @return list of users matching the criteria
     */
    List<User> findByRoleAndActive(UserRole role, Boolean active);

    /**
     * Find users by role and active status with pagination
     * 
     * @param role the user role to filter by
     * @param active the active status to filter by
     * @param pageable pagination information
     * @return page of users matching the criteria
     */
    Page<User> findByRoleAndActive(UserRole role, Boolean active, Pageable pageable);

    /**
     * Search users by username or email containing the search term
     * 
     * @param searchTerm the term to search for
     * @param pageable pagination information
     * @return page of users matching the search criteria
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<User> searchByUsernameOrEmail(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Search active users by username or email containing the search term
     * 
     * @param searchTerm the term to search for
     * @param pageable pagination information
     * @return page of active users matching the search criteria
     */
    @Query("SELECT u FROM User u WHERE u.active = true AND " +
           "(LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<User> searchActiveUsersByUsernameOrEmail(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Count users by role
     * 
     * @param role the user role to count
     * @return number of users with the specified role
     */
    long countByRole(UserRole role);

    /**
     * Count active users
     * 
     * @return number of active users
     */
    long countByActiveTrue();

    /**
     * Count inactive users
     * 
     * @return number of inactive users
     */
    long countByActiveFalse();

    /**
     * Find users created in the last N days
     * 
     * @param days number of days to look back
     * @return list of recently created users
     */
    @Query("SELECT u FROM User u WHERE u.createdAt >= CURRENT_DATE - :days")
    List<User> findRecentUsers(@Param("days") int days);

    /**
     * Find users by multiple roles
     * 
     * @param roles list of roles to filter by
     * @return list of users with any of the specified roles
     */
    List<User> findByRoleIn(List<UserRole> roles);

    /**
     * Find users by multiple roles with pagination
     * 
     * @param roles list of roles to filter by
     * @param pageable pagination information
     * @return page of users with any of the specified roles
     */
    Page<User> findByRoleIn(List<UserRole> roles, Pageable pageable);
}
