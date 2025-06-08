package com.educagestor.repository;

import com.educagestor.entity.Attendance;
import com.educagestor.entity.enums.AttendanceStatus;
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
 * Repository interface for Attendance entity
 */
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByEnrollmentIdAndAttendanceDate(Long enrollmentId, LocalDate attendanceDate);
    boolean existsByEnrollmentIdAndAttendanceDate(Long enrollmentId, LocalDate attendanceDate);
    
    List<Attendance> findByEnrollmentId(Long enrollmentId);
    Page<Attendance> findByEnrollmentId(Long enrollmentId, Pageable pageable);
    
    List<Attendance> findByEnrollmentIdOrderByAttendanceDateDesc(Long enrollmentId);
    
    @Query("SELECT a FROM Attendance a WHERE a.enrollment.student.id = :studentId")
    List<Attendance> findByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT a FROM Attendance a WHERE a.enrollment.student.id = :studentId")
    Page<Attendance> findByStudentId(@Param("studentId") Long studentId, Pageable pageable);
    
    @Query("SELECT a FROM Attendance a WHERE a.enrollment.course.id = :courseId")
    List<Attendance> findByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT a FROM Attendance a WHERE a.enrollment.course.id = :courseId")
    Page<Attendance> findByCourseId(@Param("courseId") Long courseId, Pageable pageable);

    List<Attendance> findByAttendanceDate(LocalDate attendanceDate);
    List<Attendance> findByAttendanceDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<Attendance> findByStatus(AttendanceStatus status);
    Page<Attendance> findByStatus(AttendanceStatus status, Pageable pageable);
    
    @Query("SELECT a FROM Attendance a WHERE a.enrollment.student.id = :studentId AND a.status = :status")
    List<Attendance> findByStudentIdAndStatus(@Param("studentId") Long studentId, @Param("status") AttendanceStatus status);
    
    @Query("SELECT a FROM Attendance a WHERE a.enrollment.course.id = :courseId AND a.status = :status")
    List<Attendance> findByCourseIdAndStatus(@Param("courseId") Long courseId, @Param("status") AttendanceStatus status);

    @Query("SELECT a FROM Attendance a WHERE a.enrollment.course.id = :courseId AND a.attendanceDate = :date")
    List<Attendance> findByCourseIdAndDate(@Param("courseId") Long courseId, @Param("date") LocalDate date);

    long countByEnrollmentId(Long enrollmentId);
    long countByEnrollmentIdAndStatus(Long enrollmentId, AttendanceStatus status);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.enrollment.student.id = :studentId")
    long countByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.enrollment.student.id = :studentId AND a.status = :status")
    long countByStudentIdAndStatus(@Param("studentId") Long studentId, @Param("status") AttendanceStatus status);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.enrollment.course.id = :courseId")
    long countByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.enrollment.course.id = :courseId AND a.status = :status")
    long countByCourseIdAndStatus(@Param("courseId") Long courseId, @Param("status") AttendanceStatus status);

    @Query("SELECT (COUNT(a) * 100.0 / (SELECT COUNT(a2) FROM Attendance a2 WHERE a2.enrollment.id = :enrollmentId)) " +
           "FROM Attendance a WHERE a.enrollment.id = :enrollmentId AND a.status IN ('PRESENT', 'LATE')")
    Double calculateAttendancePercentageByEnrollmentId(@Param("enrollmentId") Long enrollmentId);

    @Query("SELECT a FROM Attendance a WHERE a.attendanceDate = CURRENT_DATE")
    List<Attendance> findTodayAttendance();
    
    @Query("SELECT a FROM Attendance a WHERE a.enrollment.course.teacher.id = :teacherId")
    List<Attendance> findByTeacherId(@Param("teacherId") Long teacherId);
    
    @Query("SELECT a FROM Attendance a WHERE a.enrollment.course.teacher.id = :teacherId")
    Page<Attendance> findByTeacherId(@Param("teacherId") Long teacherId, Pageable pageable);
}
