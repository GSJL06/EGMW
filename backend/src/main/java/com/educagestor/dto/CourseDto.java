package com.educagestor.dto;

import com.educagestor.entity.enums.CourseStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Course entity
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDto {

    private Long id;

    private Long institutionId;
    private String institutionName;

    private Long teacherId;
    private String teacherName;

    @NotBlank(message = "Course code is required")
    @Size(max = 20, message = "Course code must not exceed 20 characters")
    private String code;

    @NotBlank(message = "Course name is required")
    @Size(max = 255, message = "Course name must not exceed 255 characters")
    private String name;

    private String description;

    @Min(value = 1, message = "Credits must be at least 1")
    @Max(value = 10, message = "Credits must not exceed 10")
    @Builder.Default
    private Integer credits = 1;

    @Min(value = 1, message = "Max students must be at least 1")
    @Max(value = 100, message = "Max students must not exceed 100")
    @Builder.Default
    private Integer maxStudents = 30;

    @Size(max = 255, message = "Schedule must not exceed 255 characters")
    private String schedule;

    @Size(max = 100, message = "Classroom must not exceed 100 characters")
    private String classroom;

    private LocalDate startDate;
    private LocalDate endDate;

    @Builder.Default
    private CourseStatus status = CourseStatus.ACTIVE;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Computed fields
    private String fullName;
    private int enrolledStudentCount;
    private int availableSpots;
    private boolean active;
    private boolean atCapacity;
    private boolean inSession;
    private Long durationInDays;

    /**
     * Get the course's full display name
     * 
     * @return formatted course name with code
     */
    public String getFullName() {
        if (fullName != null) {
            return fullName;
        }
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
        return enrolledStudentCount >= maxStudents;
    }

    /**
     * Get available spots in the course
     * 
     * @return number of available spots
     */
    public int getAvailableSpots() {
        return maxStudents - enrolledStudentCount;
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
}

/**
 * DTO for course creation requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class CreateCourseDto {

    private Long institutionId;
    private Long teacherId;

    @NotBlank(message = "Course code is required")
    @Size(max = 20, message = "Course code must not exceed 20 characters")
    private String code;

    @NotBlank(message = "Course name is required")
    @Size(max = 255, message = "Course name must not exceed 255 characters")
    private String name;

    private String description;

    @Min(value = 1, message = "Credits must be at least 1")
    @Max(value = 10, message = "Credits must not exceed 10")
    @Builder.Default
    private Integer credits = 1;

    @Min(value = 1, message = "Max students must be at least 1")
    @Max(value = 100, message = "Max students must not exceed 100")
    @Builder.Default
    private Integer maxStudents = 30;

    @Size(max = 255, message = "Schedule must not exceed 255 characters")
    private String schedule;

    @Size(max = 100, message = "Classroom must not exceed 100 characters")
    private String classroom;

    private LocalDate startDate;
    private LocalDate endDate;

    @Builder.Default
    private CourseStatus status = CourseStatus.ACTIVE;
}

/**
 * DTO for course update requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class UpdateCourseDto {

    private Long teacherId;

    @Size(max = 255, message = "Course name must not exceed 255 characters")
    private String name;

    private String description;

    @Min(value = 1, message = "Credits must be at least 1")
    @Max(value = 10, message = "Credits must not exceed 10")
    private Integer credits;

    @Min(value = 1, message = "Max students must be at least 1")
    @Max(value = 100, message = "Max students must not exceed 100")
    private Integer maxStudents;

    @Size(max = 255, message = "Schedule must not exceed 255 characters")
    private String schedule;

    @Size(max = 100, message = "Classroom must not exceed 100 characters")
    private String classroom;

    private LocalDate startDate;
    private LocalDate endDate;

    private CourseStatus status;
}
