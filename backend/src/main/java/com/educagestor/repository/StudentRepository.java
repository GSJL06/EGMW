package com.educagestor.repository;

import com.educagestor.entity.Student;
import com.educagestor.entity.enums.StudentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Student entity
 * 
 * Provides data access methods for student management including
 * search functionality, status filtering, and academic queries.
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    /**
     * Find student by student code
     * 
     * @param studentCode the student code to search for
     * @return Optional containing the student if found
     */
    Optional<Student> findByStudentCode(String studentCode);

    /**
     * Find student by user ID
     * 
     * @param userId the user ID to search for
     * @return Optional containing the student if found
     */
    Optional<Student> findByUserId(Long userId);

    /**
     * Check if student code exists
     * 
     * @param studentCode the student code to check
     * @return true if student code exists
     */
    boolean existsByStudentCode(String studentCode);

    /**
     * Find students by institution ID
     * 
     * @param institutionId the institution ID to filter by
     * @return list of students in the institution
     */
    List<Student> findByInstitutionId(Long institutionId);

    /**
     * Find students by institution ID with pagination
     * 
     * @param institutionId the institution ID to filter by
     * @param pageable pagination information
     * @return page of students in the institution
     */
    Page<Student> findByInstitutionId(Long institutionId, Pageable pageable);

    /**
     * Find students by status
     * 
     * @param status the student status to filter by
     * @return list of students with the specified status
     */
    List<Student> findByStatus(StudentStatus status);

    /**
     * Find students by status with pagination
     * 
     * @param status the student status to filter by
     * @param pageable pagination information
     * @return page of students with the specified status
     */
    Page<Student> findByStatus(StudentStatus status, Pageable pageable);

    /**
     * Find active students
     * 
     * @return list of active students
     */
    List<Student> findByStatusOrderByLastNameAsc(StudentStatus status);

    /**
     * Find students by institution and status
     * 
     * @param institutionId the institution ID to filter by
     * @param status the student status to filter by
     * @return list of students matching the criteria
     */
    List<Student> findByInstitutionIdAndStatus(Long institutionId, StudentStatus status);

    /**
     * Find students by institution and status with pagination
     * 
     * @param institutionId the institution ID to filter by
     * @param status the student status to filter by
     * @param pageable pagination information
     * @return page of students matching the criteria
     */
    Page<Student> findByInstitutionIdAndStatus(Long institutionId, StudentStatus status, Pageable pageable);

    /**
     * Search students by name or student code
     * 
     * @param searchTerm the term to search for
     * @param pageable pagination information
     * @return page of students matching the search criteria
     */
    @Query("SELECT s FROM Student s WHERE " +
           "LOWER(s.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.studentCode) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Student> searchByNameOrCode(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Search students by name or student code within an institution
     * 
     * @param institutionId the institution ID to filter by
     * @param searchTerm the term to search for
     * @param pageable pagination information
     * @return page of students matching the search criteria
     */
    @Query("SELECT s FROM Student s WHERE s.institution.id = :institutionId AND " +
           "(LOWER(s.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.studentCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Student> searchByNameOrCodeInInstitution(@Param("institutionId") Long institutionId, 
                                                  @Param("searchTerm") String searchTerm, 
                                                  Pageable pageable);

    /**
     * Find students enrolled in a specific course
     * 
     * @param courseId the course ID to filter by
     * @return list of students enrolled in the course
     */
    @Query("SELECT s FROM Student s JOIN s.enrollments e WHERE e.course.id = :courseId")
    List<Student> findByCourseId(@Param("courseId") Long courseId);

    /**
     * Find students enrolled in a specific course with pagination
     * 
     * @param courseId the course ID to filter by
     * @param pageable pagination information
     * @return page of students enrolled in the course
     */
    @Query("SELECT s FROM Student s JOIN s.enrollments e WHERE e.course.id = :courseId")
    Page<Student> findByCourseId(@Param("courseId") Long courseId, Pageable pageable);

    /**
     * Find students by enrollment date range
     * 
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     * @return list of students enrolled within the date range
     */
    List<Student> findByEnrollmentDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Count students by institution
     * 
     * @param institutionId the institution ID to count for
     * @return number of students in the institution
     */
    long countByInstitutionId(Long institutionId);

    /**
     * Count students by status
     * 
     * @param status the student status to count
     * @return number of students with the specified status
     */
    long countByStatus(StudentStatus status);

    /**
     * Count students by institution and status
     * 
     * @param institutionId the institution ID to filter by
     * @param status the student status to filter by
     * @return number of students matching the criteria
     */
    long countByInstitutionIdAndStatus(Long institutionId, StudentStatus status);

    /**
     * Find students with upcoming birthdays
     * 
     * @param days number of days to look ahead
     * @return list of students with birthdays in the next N days
     */
    @Query("SELECT s FROM Student s WHERE " +
           "EXTRACT(MONTH FROM s.dateOfBirth) = EXTRACT(MONTH FROM CURRENT_DATE + :days) AND " +
           "EXTRACT(DAY FROM s.dateOfBirth) = EXTRACT(DAY FROM CURRENT_DATE + :days)")
    List<Student> findStudentsWithUpcomingBirthdays(@Param("days") int days);

    /**
     * Find students by age range
     * 
     * @param minAge minimum age
     * @param maxAge maximum age
     * @return list of students within the age range
     */
    @Query("SELECT s FROM Student s WHERE " +
           "EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM s.dateOfBirth) BETWEEN :minAge AND :maxAge")
    List<Student> findByAgeRange(@Param("minAge") int minAge, @Param("maxAge") int maxAge);

    /**
     * Find recently enrolled students
     * 
     * @param days number of days to look back
     * @return list of students enrolled in the last N days
     */
    @Query("SELECT s FROM Student s WHERE s.enrollmentDate >= CURRENT_DATE - :days")
    List<Student> findRecentlyEnrolledStudents(@Param("days") int days);
}
