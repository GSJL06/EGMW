package com.educagestor.repository;

import com.educagestor.entity.Institution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Institution entity
 * 
 * Provides data access methods for institution management including
 * search functionality and statistical queries.
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Long> {

    /**
     * Find institution by name
     * 
     * @param name the institution name to search for
     * @return Optional containing the institution if found
     */
    Optional<Institution> findByName(String name);

    /**
     * Find institution by email
     * 
     * @param email the institution email to search for
     * @return Optional containing the institution if found
     */
    Optional<Institution> findByEmail(String email);

    /**
     * Check if institution name exists
     * 
     * @param name the institution name to check
     * @return true if name exists
     */
    boolean existsByName(String name);

    /**
     * Check if institution email exists
     * 
     * @param email the institution email to check
     * @return true if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Search institutions by name containing the search term
     * 
     * @param searchTerm the term to search for
     * @param pageable pagination information
     * @return page of institutions matching the search criteria
     */
    @Query("SELECT i FROM Institution i WHERE " +
           "LOWER(i.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Institution> searchByName(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Search institutions by name, address, or email containing the search term
     * 
     * @param searchTerm the term to search for
     * @param pageable pagination information
     * @return page of institutions matching the search criteria
     */
    @Query("SELECT i FROM Institution i WHERE " +
           "LOWER(i.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(i.address) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(i.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Institution> searchByNameAddressOrEmail(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find institutions with students count
     * 
     * @return list of institutions with their student counts
     */
    @Query("SELECT i, COUNT(s) FROM Institution i LEFT JOIN i.students s GROUP BY i")
    List<Object[]> findInstitutionsWithStudentCount();

    /**
     * Find institutions with teachers count
     * 
     * @return list of institutions with their teacher counts
     */
    @Query("SELECT i, COUNT(t) FROM Institution i LEFT JOIN i.teachers t GROUP BY i")
    List<Object[]> findInstitutionsWithTeacherCount();

    /**
     * Find institutions with courses count
     * 
     * @return list of institutions with their course counts
     */
    @Query("SELECT i, COUNT(c) FROM Institution i LEFT JOIN i.courses c GROUP BY i")
    List<Object[]> findInstitutionsWithCourseCount();

    /**
     * Find institutions ordered by student count
     * 
     * @param pageable pagination information
     * @return page of institutions ordered by student count
     */
    @Query("SELECT i FROM Institution i LEFT JOIN i.students s " +
           "GROUP BY i ORDER BY COUNT(s) DESC")
    Page<Institution> findInstitutionsOrderedByStudentCount(Pageable pageable);

    /**
     * Find institutions by phone number
     * 
     * @param phone the phone number to search for
     * @return list of institutions with the specified phone
     */
    List<Institution> findByPhone(String phone);

    /**
     * Find institutions by website
     * 
     * @param website the website to search for
     * @return Optional containing the institution if found
     */
    Optional<Institution> findByWebsite(String website);

    /**
     * Find institutions created in the last N days
     * 
     * @param days number of days to look back
     * @return list of recently created institutions
     */
    @Query("SELECT i FROM Institution i WHERE i.createdAt >= CURRENT_DATE - :days")
    List<Institution> findRecentInstitutions(@Param("days") int days);

    /**
     * Get institution statistics
     * 
     * @param institutionId the institution ID
     * @return array containing [studentCount, teacherCount, courseCount]
     */
    @Query("SELECT " +
           "(SELECT COUNT(s) FROM Student s WHERE s.institution.id = :institutionId), " +
           "(SELECT COUNT(t) FROM Teacher t WHERE t.institution.id = :institutionId), " +
           "(SELECT COUNT(c) FROM Course c WHERE c.institution.id = :institutionId)")
    Object[] getInstitutionStatistics(@Param("institutionId") Long institutionId);
}
