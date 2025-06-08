package com.educagestor.entity;

import com.educagestor.entity.enums.AttendanceStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

/**
 * Attendance entity representing student attendance records
 * 
 * This entity tracks daily attendance for students in their enrolled courses,
 * including attendance status and any related comments.
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
@Entity
@Table(name = "attendance", 
       indexes = {
           @Index(name = "idx_attendance_enrollment_id", columnList = "enrollment_id"),
           @Index(name = "idx_attendance_date", columnList = "attendance_date"),
           @Index(name = "idx_attendance_status", columnList = "status")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_enrollment_date", columnNames = {"enrollment_id", "attendance_date"})
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance extends BaseEntity {

    /**
     * Enrollment this attendance record belongs to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", referencedColumnName = "id", nullable = false)
    @NotNull(message = "Enrollment is required")
    private Enrollment enrollment;

    /**
     * Date of the attendance record
     */
    @NotNull(message = "Attendance date is required")
    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    /**
     * Attendance status for this date
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @NotNull(message = "Attendance status is required")
    private AttendanceStatus status;

    /**
     * Additional comments about the attendance
     */
    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    /**
     * Check if the student was present
     * 
     * @return true if status counts as present
     */
    public boolean isPresent() {
        return status.countsAsPresent();
    }

    /**
     * Check if the student was absent
     * 
     * @return true if status counts as absent
     */
    public boolean isAbsent() {
        return status.countsAsAbsent();
    }

    /**
     * Check if the absence was excused
     * 
     * @return true if status is excused
     */
    public boolean isExcused() {
        return status.isExcused();
    }

    /**
     * Check if the student was late
     * 
     * @return true if status is late
     */
    public boolean isLate() {
        return status == AttendanceStatus.LATE;
    }

    /**
     * Get the student's full name for display
     * 
     * @return student's full name
     */
    public String getStudentFullName() {
        return enrollment != null && enrollment.getStudent() != null 
                ? enrollment.getStudent().getFullName() 
                : "";
    }

    /**
     * Get the course's full name for display
     * 
     * @return course's full name
     */
    public String getCourseFullName() {
        return enrollment != null && enrollment.getCourse() != null 
                ? enrollment.getCourse().getFullName() 
                : "";
    }

    /**
     * Get the student code for display
     * 
     * @return student code
     */
    public String getStudentCode() {
        return enrollment != null && enrollment.getStudent() != null 
                ? enrollment.getStudent().getStudentCode() 
                : "";
    }

    /**
     * Get the course code for display
     * 
     * @return course code
     */
    public String getCourseCode() {
        return enrollment != null && enrollment.getCourse() != null 
                ? enrollment.getCourse().getCode() 
                : "";
    }

    /**
     * Check if this attendance record is for today
     * 
     * @return true if attendance date is today
     */
    public boolean isToday() {
        return attendanceDate.equals(LocalDate.now());
    }

    /**
     * Check if this attendance record is in the past
     * 
     * @return true if attendance date is before today
     */
    public boolean isPast() {
        return attendanceDate.isBefore(LocalDate.now());
    }

    /**
     * Check if this attendance record is in the future
     * 
     * @return true if attendance date is after today
     */
    public boolean isFuture() {
        return attendanceDate.isAfter(LocalDate.now());
    }

    /**
     * Get a formatted display string for the attendance
     * 
     * @return formatted attendance string
     */
    public String getFormattedAttendance() {
        return String.format("%s - %s (%s)", 
                attendanceDate.toString(), 
                status.getDisplayName(),
                getStudentFullName());
    }

    /**
     * Get CSS class for styling based on status
     * 
     * @return CSS class name
     */
    public String getStatusCssClass() {
        return status.getCssClass();
    }

    /**
     * Get icon for UI representation based on status
     * 
     * @return icon name
     */
    public String getStatusIcon() {
        return status.getIcon();
    }

    /**
     * Check if the attendance can be modified
     * 
     * @return true if attendance can be modified (not too far in the past)
     */
    public boolean canBeModified() {
        // Allow modification for records up to 7 days old
        return attendanceDate.isAfter(LocalDate.now().minusDays(7));
    }

    /**
     * Get the day of week for the attendance date
     * 
     * @return day of week
     */
    public String getDayOfWeek() {
        return attendanceDate.getDayOfWeek().toString();
    }

    /**
     * Check if this is a weekend attendance record
     * 
     * @return true if attendance date is Saturday or Sunday
     */
    public boolean isWeekend() {
        return attendanceDate.getDayOfWeek().getValue() >= 6;
    }
}
