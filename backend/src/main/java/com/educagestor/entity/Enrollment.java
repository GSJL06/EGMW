package com.educagestor.entity;

import com.educagestor.entity.enums.EnrollmentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Enrollment entity representing student-course relationships
 * 
 * This entity manages the enrollment of students in courses,
 * tracking their progress, final grades, and related academic data.
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
@Entity
@Table(name = "enrollments", 
       indexes = {
           @Index(name = "idx_enrollments_student_id", columnList = "student_id"),
           @Index(name = "idx_enrollments_course_id", columnList = "course_id"),
           @Index(name = "idx_enrollments_status", columnList = "status")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_student_course", columnNames = {"student_id", "course_id"})
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment extends BaseEntity {

    /**
     * Student enrolled in the course
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "id", nullable = false)
    @NotNull(message = "Student is required")
    private Student student;

    /**
     * Course the student is enrolled in
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", referencedColumnName = "id", nullable = false)
    @NotNull(message = "Course is required")
    private Course course;

    /**
     * Date when the student enrolled in the course
     */
    @NotNull(message = "Enrollment date is required")
    @Column(name = "enrollment_date", nullable = false)
    @Builder.Default
    private LocalDate enrollmentDate = LocalDate.now();

    /**
     * Current status of the enrollment
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private EnrollmentStatus status = EnrollmentStatus.ENROLLED;

    /**
     * Final grade for the course (0.00 to 100.00)
     */
    @DecimalMin(value = "0.00", message = "Final grade must be at least 0.00")
    @DecimalMax(value = "100.00", message = "Final grade must not exceed 100.00")
    @Column(name = "final_grade", precision = 5, scale = 2)
    private BigDecimal finalGrade;

    /**
     * List of individual grades for this enrollment
     */
    @OneToMany(mappedBy = "enrollment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Grade> grades = new ArrayList<>();

    /**
     * List of attendance records for this enrollment
     */
    @OneToMany(mappedBy = "enrollment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Attendance> attendanceRecords = new ArrayList<>();

    /**
     * Check if the enrollment is currently active
     * 
     * @return true if status is ENROLLED
     */
    public boolean isActive() {
        return status == EnrollmentStatus.ENROLLED;
    }

    /**
     * Check if the enrollment is completed
     * 
     * @return true if status is COMPLETED
     */
    public boolean isCompleted() {
        return status == EnrollmentStatus.COMPLETED;
    }

    /**
     * Check if grades can be assigned to this enrollment
     * 
     * @return true if enrollment allows grading
     */
    public boolean allowsGrading() {
        return status.allowsGrading();
    }

    /**
     * Check if attendance can be recorded for this enrollment
     * 
     * @return true if enrollment allows attendance
     */
    public boolean allowsAttendance() {
        return status.allowsAttendance();
    }

    /**
     * Add a grade to this enrollment
     * 
     * @param grade the grade to add
     */
    public void addGrade(Grade grade) {
        grades.add(grade);
        grade.setEnrollment(this);
    }

    /**
     * Remove a grade from this enrollment
     * 
     * @param grade the grade to remove
     */
    public void removeGrade(Grade grade) {
        grades.remove(grade);
        grade.setEnrollment(null);
    }

    /**
     * Add an attendance record to this enrollment
     * 
     * @param attendance the attendance record to add
     */
    public void addAttendanceRecord(Attendance attendance) {
        attendanceRecords.add(attendance);
        attendance.setEnrollment(this);
    }

    /**
     * Remove an attendance record from this enrollment
     * 
     * @param attendance the attendance record to remove
     */
    public void removeAttendanceRecord(Attendance attendance) {
        attendanceRecords.remove(attendance);
        attendance.setEnrollment(null);
    }

    /**
     * Calculate the average grade for this enrollment
     * 
     * @return average grade or null if no grades
     */
    public BigDecimal calculateAverageGrade() {
        if (grades.isEmpty()) {
            return null;
        }

        BigDecimal totalWeightedGrade = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;

        for (Grade grade : grades) {
            BigDecimal weightedGrade = grade.getGrade()
                    .multiply(grade.getWeight())
                    .divide(grade.getMaxGrade(), 2, BigDecimal.ROUND_HALF_UP);
            
            totalWeightedGrade = totalWeightedGrade.add(weightedGrade);
            totalWeight = totalWeight.add(grade.getWeight());
        }

        if (totalWeight.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        return totalWeightedGrade.divide(totalWeight, 2, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * Calculate attendance percentage for this enrollment
     * 
     * @return attendance percentage (0.00 to 100.00)
     */
    public BigDecimal calculateAttendancePercentage() {
        if (attendanceRecords.isEmpty()) {
            return BigDecimal.ZERO;
        }

        long presentCount = attendanceRecords.stream()
                .filter(attendance -> attendance.getStatus().countsAsPresent())
                .count();

        return BigDecimal.valueOf(presentCount)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(attendanceRecords.size()), 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Get the number of grades for this enrollment
     * 
     * @return number of grades
     */
    public int getGradeCount() {
        return grades.size();
    }

    /**
     * Get the number of attendance records for this enrollment
     * 
     * @return number of attendance records
     */
    public int getAttendanceRecordCount() {
        return attendanceRecords.size();
    }

    /**
     * Get the student's full name for display
     * 
     * @return student's full name
     */
    public String getStudentFullName() {
        return student != null ? student.getFullName() : "";
    }

    /**
     * Get the course's full name for display
     * 
     * @return course's full name
     */
    public String getCourseFullName() {
        return course != null ? course.getFullName() : "";
    }

    /**
     * Check if the student has passed the course
     * 
     * @param passingGrade the minimum grade to pass
     * @return true if final grade is above passing grade
     */
    public boolean hasPassed(BigDecimal passingGrade) {
        if (finalGrade == null) {
            BigDecimal averageGrade = calculateAverageGrade();
            return averageGrade != null && averageGrade.compareTo(passingGrade) >= 0;
        }
        return finalGrade.compareTo(passingGrade) >= 0;
    }
}
