package com.educagestor.repository;

import com.educagestor.entity.Enrollment;
import com.educagestor.entity.enums.EnrollmentStatus;
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
 * Repository interface for Enrollment entity
 */
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
    
    List<Enrollment> findByStudentId(Long studentId);
    Page<Enrollment> findByStudentId(Long studentId, Pageable pageable);
    
    List<Enrollment> findByCourseId(Long courseId);
    Page<Enrollment> findByCourseId(Long courseId, Pageable pageable);
    
    List<Enrollment> findByStatus(EnrollmentStatus status);
    Page<Enrollment> findByStatus(EnrollmentStatus status, Pageable pageable);
    
    List<Enrollment> findByStudentIdAndStatus(Long studentId, EnrollmentStatus status);
    List<Enrollment> findByCourseIdAndStatus(Long courseId, EnrollmentStatus status);
    
    Page<Enrollment> findByStudentIdAndStatus(Long studentId, EnrollmentStatus status, Pageable pageable);
    Page<Enrollment> findByCourseIdAndStatus(Long courseId, EnrollmentStatus status, Pageable pageable);

    List<Enrollment> findByEnrollmentDateBetween(LocalDate startDate, LocalDate endDate);
    
    long countByStudentId(Long studentId);
    long countByCourseId(Long courseId);
    long countByStatus(EnrollmentStatus status);
    long countByStudentIdAndStatus(Long studentId, EnrollmentStatus status);
    long countByCourseIdAndStatus(Long courseId, EnrollmentStatus status);

    @Query("SELECT e FROM Enrollment e WHERE e.enrollmentDate >= :startDate")
    List<Enrollment> findRecentEnrollments(@Param("startDate") LocalDate startDate);

    @Query("SELECT e FROM Enrollment e WHERE e.student.institution.id = :institutionId")
    List<Enrollment> findByInstitutionId(@Param("institutionId") Long institutionId);

    @Query("SELECT e FROM Enrollment e WHERE e.student.institution.id = :institutionId")
    Page<Enrollment> findByInstitutionId(@Param("institutionId") Long institutionId, Pageable pageable);

    @Query("SELECT e FROM Enrollment e WHERE e.course.teacher.id = :teacherId")
    List<Enrollment> findByTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT e FROM Enrollment e WHERE e.course.teacher.id = :teacherId")
    Page<Enrollment> findByTeacherId(@Param("teacherId") Long teacherId, Pageable pageable);

    @Query("SELECT AVG(e.finalGrade) FROM Enrollment e WHERE e.course.id = :courseId AND e.finalGrade IS NOT NULL")
    Double findAverageGradeByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT AVG(e.finalGrade) FROM Enrollment e WHERE e.student.id = :studentId AND e.finalGrade IS NOT NULL")
    Double findAverageGradeByStudentId(@Param("studentId") Long studentId);
}
