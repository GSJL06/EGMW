package com.educagestor.repository;

import com.educagestor.entity.Grade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Grade entity
 */
@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    List<Grade> findByEnrollmentId(Long enrollmentId);
    Page<Grade> findByEnrollmentId(Long enrollmentId, Pageable pageable);
    
    List<Grade> findByEnrollmentIdOrderByGradeDateDesc(Long enrollmentId);
    
    @Query("SELECT g FROM Grade g WHERE g.enrollment.student.id = :studentId")
    List<Grade> findByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT g FROM Grade g WHERE g.enrollment.student.id = :studentId")
    Page<Grade> findByStudentId(@Param("studentId") Long studentId, Pageable pageable);
    
    @Query("SELECT g FROM Grade g WHERE g.enrollment.course.id = :courseId")
    List<Grade> findByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT g FROM Grade g WHERE g.enrollment.course.id = :courseId")
    Page<Grade> findByCourseId(@Param("courseId") Long courseId, Pageable pageable);

    List<Grade> findByGradeDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<Grade> findByAssignmentNameContainingIgnoreCase(String assignmentName);

    @Query("SELECT AVG(g.grade) FROM Grade g WHERE g.enrollment.id = :enrollmentId")
    Double findAverageGradeByEnrollmentId(@Param("enrollmentId") Long enrollmentId);
    
    @Query("SELECT AVG(g.grade) FROM Grade g WHERE g.enrollment.course.id = :courseId")
    Double findAverageGradeByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT AVG(g.grade) FROM Grade g WHERE g.enrollment.student.id = :studentId")
    Double findAverageGradeByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT MAX(g.grade) FROM Grade g WHERE g.enrollment.course.id = :courseId")
    BigDecimal findHighestGradeByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT MIN(g.grade) FROM Grade g WHERE g.enrollment.course.id = :courseId")
    BigDecimal findLowestGradeByCourseId(@Param("courseId") Long courseId);

    long countByEnrollmentId(Long enrollmentId);
    
    @Query("SELECT COUNT(g) FROM Grade g WHERE g.enrollment.student.id = :studentId")
    long countByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT COUNT(g) FROM Grade g WHERE g.enrollment.course.id = :courseId")
    long countByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT g FROM Grade g WHERE g.gradeDate >= :startDate")
    List<Grade> findRecentGrades(@Param("startDate") LocalDate startDate);

    @Query("SELECT g FROM Grade g WHERE g.enrollment.course.teacher.id = :teacherId")
    List<Grade> findByTeacherId(@Param("teacherId") Long teacherId);
    
    @Query("SELECT g FROM Grade g WHERE g.enrollment.course.teacher.id = :teacherId")
    Page<Grade> findByTeacherId(@Param("teacherId") Long teacherId, Pageable pageable);
}
