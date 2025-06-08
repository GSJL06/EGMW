package com.educagestor.repository;

import com.educagestor.entity.Course;
import com.educagestor.entity.enums.CourseStatus;
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
 * Repository interface for Course entity
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByCode(String code);
    boolean existsByCode(String code);
    
    List<Course> findByInstitutionId(Long institutionId);
    Page<Course> findByInstitutionId(Long institutionId, Pageable pageable);
    
    List<Course> findByTeacherId(Long teacherId);
    Page<Course> findByTeacherId(Long teacherId, Pageable pageable);
    
    List<Course> findByStatus(CourseStatus status);
    Page<Course> findByStatus(CourseStatus status, Pageable pageable);
    
    List<Course> findByInstitutionIdAndStatus(Long institutionId, CourseStatus status);
    Page<Course> findByInstitutionIdAndStatus(Long institutionId, CourseStatus status, Pageable pageable);

    @Query("SELECT c FROM Course c WHERE " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Course> searchByNameCodeOrDescription(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT c FROM Course c WHERE c.institution.id = :institutionId AND " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Course> searchInInstitution(@Param("institutionId") Long institutionId, 
                                    @Param("searchTerm") String searchTerm, 
                                    Pageable pageable);

    List<Course> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
    List<Course> findByEndDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT c FROM Course c WHERE c.startDate <= CURRENT_DATE AND c.endDate >= CURRENT_DATE")
    List<Course> findCurrentCourses();
    
    @Query("SELECT c FROM Course c WHERE c.startDate <= CURRENT_DATE AND c.endDate >= CURRENT_DATE")
    Page<Course> findCurrentCourses(Pageable pageable);

    long countByInstitutionId(Long institutionId);
    long countByTeacherId(Long teacherId);
    long countByStatus(CourseStatus status);
    long countByInstitutionIdAndStatus(Long institutionId, CourseStatus status);

    @Query("SELECT c FROM Course c LEFT JOIN c.enrollments e GROUP BY c ORDER BY COUNT(e) DESC")
    Page<Course> findCoursesOrderedByEnrollmentCount(Pageable pageable);

    @Query("SELECT c FROM Course c WHERE " +
           "c.status = 'ACTIVE' AND " +
           "(SELECT COUNT(e) FROM Enrollment e WHERE e.course = c AND e.status = 'ENROLLED') < c.maxStudents")
    List<Course> findAvailableCoursesForEnrollment();

    @Query("SELECT c FROM Course c WHERE c.teacher.id = :teacherId AND c.status = 'ACTIVE'")
    List<Course> findActiveCoursesForTeacher(@Param("teacherId") Long teacherId);
}
