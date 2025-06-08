package com.educagestor.entity;

import com.educagestor.entity.enums.CourseStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Course entity representing courses in the educational system
 * 
 * This entity contains all course information including details,
 * schedule, capacity, and relationships with other entities.
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
@Entity
@Table(name = "courses", indexes = {
    @Index(name = "idx_courses_institution_id", columnList = "institution_id"),
    @Index(name = "idx_courses_teacher_id", columnList = "teacher_id"),
    @Index(name = "idx_courses_code", columnList = "code"),
    @Index(name = "idx_courses_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course extends BaseEntity {

    /**
     * Institution offering this course
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", referencedColumnName = "id")
    private Institution institution;

    /**
     * Teacher assigned to this course
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", referencedColumnName = "id")
    private Teacher teacher;

    /**
     * Unique course code
     */
    @NotBlank(message = "Course code is required")
    @Size(max = 20, message = "Course code must not exceed 20 characters")
    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    /**
     * Course name
     */
    @NotBlank(message = "Course name is required")
    @Size(max = 255, message = "Course name must not exceed 255 characters")
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Course description
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Number of credits for this course
     */
    @Min(value = 1, message = "Credits must be at least 1")
    @Max(value = 10, message = "Credits must not exceed 10")
    @Column(name = "credits", nullable = false)
    @Builder.Default
    private Integer credits = 1;

    /**
     * Maximum number of students that can enroll
     */
    @Min(value = 1, message = "Max students must be at least 1")
    @Max(value = 100, message = "Max students must not exceed 100")
    @Column(name = "max_students", nullable = false)
    @Builder.Default
    private Integer maxStudents = 30;

    /**
     * Course schedule information
     */
    @Size(max = 255, message = "Schedule must not exceed 255 characters")
    @Column(name = "schedule")
    private String schedule;

    /**
     * Classroom where the course is held
     */
    @Size(max = 100, message = "Classroom must not exceed 100 characters")
    @Column(name = "classroom", length = 100)
    private String classroom;

    /**
     * Course start date
     */
    @Column(name = "start_date")
    private LocalDate startDate;

    /**
     * Course end date
     */
    @Column(name = "end_date")
    private LocalDate endDate;

    /**
     * Current status of the course
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private CourseStatus status = CourseStatus.ACTIVE;

    /**
     * List of student enrollments for this course
     */
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Enrollment> enrollments = new ArrayList<>();

    /**
     * List of educational resources for this course
     */
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<EducationalResource> educationalResources = new ArrayList<>();

    /**
     * Get the course's full display name
     * 
     * @return formatted course name with code
     */
    public String getFullName() {
        return code + " - " + name;
    }

    /**
     * Check if the course is currently active
     * 
     * @return true if status is ACTIVE
     */
    public boolean isActive() {
        return status == CourseStatus.ACTIVE;
    }

    /**
     * Check if the course allows new enrollments
     * 
     * @return true if course allows enrollments
     */
    public boolean allowsEnrollments() {
        return status.allowsEnrollments() && !isAtCapacity();
    }

    /**
     * Check if the course is at maximum capacity
     * 
     * @return true if enrolled students equals max students
     */
    public boolean isAtCapacity() {
        return getEnrolledStudentCount() >= maxStudents;
    }

    /**
     * Get the number of currently enrolled students
     * 
     * @return number of enrolled students
     */
    public int getEnrolledStudentCount() {
        return (int) enrollments.stream()
                .filter(enrollment -> enrollment.getStatus().isActive())
                .count();
    }

    /**
     * Get available spots in the course
     * 
     * @return number of available spots
     */
    public int getAvailableSpots() {
        return maxStudents - getEnrolledStudentCount();
    }

    /**
     * Check if the course is currently in session
     * 
     * @return true if current date is between start and end dates
     */
    public boolean isInSession() {
        if (startDate == null || endDate == null) {
            return false;
        }
        LocalDate now = LocalDate.now();
        return !now.isBefore(startDate) && !now.isAfter(endDate);
    }

    /**
     * Add an enrollment to this course
     * 
     * @param enrollment the enrollment to add
     */
    public void addEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
        enrollment.setCourse(this);
    }

    /**
     * Remove an enrollment from this course
     * 
     * @param enrollment the enrollment to remove
     */
    public void removeEnrollment(Enrollment enrollment) {
        enrollments.remove(enrollment);
        enrollment.setCourse(null);
    }

    /**
     * Add an educational resource to this course
     * 
     * @param resource the resource to add
     */
    public void addEducationalResource(EducationalResource resource) {
        educationalResources.add(resource);
        resource.setCourse(this);
    }

    /**
     * Remove an educational resource from this course
     * 
     * @param resource the resource to remove
     */
    public void removeEducationalResource(EducationalResource resource) {
        educationalResources.remove(resource);
        resource.setCourse(null);
    }

    /**
     * Get active enrollments only
     * 
     * @return list of active enrollments
     */
    public List<Enrollment> getActiveEnrollments() {
        return enrollments.stream()
                .filter(enrollment -> enrollment.getStatus().isActive())
                .toList();
    }

    /**
     * Get the number of educational resources for this course
     * 
     * @return number of resources
     */
    public int getEducationalResourceCount() {
        return educationalResources.size();
    }

    /**
     * Calculate course duration in days
     * 
     * @return duration in days, or null if dates are not set
     */
    public Long getDurationInDays() {
        if (startDate == null || endDate == null) {
            return null;
        }
        return startDate.until(endDate).getDays();
    }
}
