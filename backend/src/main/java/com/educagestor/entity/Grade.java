package com.educagestor.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Grade entity representing individual grades for student assignments
 * 
 * This entity stores individual grade records for students in specific
 * courses, including assignment details, scores, and weights.
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
@Entity
@Table(name = "grades", indexes = {
    @Index(name = "idx_grades_enrollment_id", columnList = "enrollment_id"),
    @Index(name = "idx_grades_grade_date", columnList = "grade_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grade extends BaseEntity {

    /**
     * Enrollment this grade belongs to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", referencedColumnName = "id", nullable = false)
    @NotNull(message = "Enrollment is required")
    private Enrollment enrollment;

    /**
     * Name of the assignment or assessment
     */
    @NotBlank(message = "Assignment name is required")
    @Size(max = 255, message = "Assignment name must not exceed 255 characters")
    @Column(name = "assignment_name", nullable = false)
    private String assignmentName;

    /**
     * Grade received by the student
     */
    @NotNull(message = "Grade is required")
    @DecimalMin(value = "0.00", message = "Grade must be at least 0.00")
    @Column(name = "grade", nullable = false, precision = 5, scale = 2)
    private BigDecimal grade;

    /**
     * Maximum possible grade for this assignment
     */
    @NotNull(message = "Max grade is required")
    @DecimalMin(value = "0.01", message = "Max grade must be greater than 0")
    @Column(name = "max_grade", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal maxGrade = BigDecimal.valueOf(100.00);

    /**
     * Weight of this grade in the overall course grade
     */
    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.01", message = "Weight must be greater than 0")
    @DecimalMax(value = "100.00", message = "Weight must not exceed 100")
    @Column(name = "weight", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal weight = BigDecimal.valueOf(1.00);

    /**
     * Date when the grade was recorded
     */
    @NotNull(message = "Grade date is required")
    @Column(name = "grade_date", nullable = false)
    @Builder.Default
    private LocalDate gradeDate = LocalDate.now();

    /**
     * Additional comments about the grade
     */
    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    /**
     * Calculate the percentage score for this grade
     * 
     * @return percentage score (0.00 to 100.00)
     */
    public BigDecimal calculatePercentage() {
        if (maxGrade.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        return grade.multiply(BigDecimal.valueOf(100))
                .divide(maxGrade, 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Calculate the weighted score for this grade
     * 
     * @return weighted score
     */
    public BigDecimal calculateWeightedScore() {
        BigDecimal percentage = calculatePercentage();
        return percentage.multiply(weight).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Check if this is a passing grade
     * 
     * @param passingPercentage minimum percentage to pass
     * @return true if grade percentage is above passing percentage
     */
    public boolean isPassing(BigDecimal passingPercentage) {
        return calculatePercentage().compareTo(passingPercentage) >= 0;
    }

    /**
     * Get letter grade based on percentage
     * 
     * @return letter grade (A, B, C, D, F)
     */
    public String getLetterGrade() {
        BigDecimal percentage = calculatePercentage();
        
        if (percentage.compareTo(BigDecimal.valueOf(90)) >= 0) {
            return "A";
        } else if (percentage.compareTo(BigDecimal.valueOf(80)) >= 0) {
            return "B";
        } else if (percentage.compareTo(BigDecimal.valueOf(70)) >= 0) {
            return "C";
        } else if (percentage.compareTo(BigDecimal.valueOf(60)) >= 0) {
            return "D";
        } else {
            return "F";
        }
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
     * Check if the grade is above average
     * 
     * @param averageGrade the average grade to compare against
     * @return true if this grade is above average
     */
    public boolean isAboveAverage(BigDecimal averageGrade) {
        return calculatePercentage().compareTo(averageGrade) > 0;
    }

    /**
     * Get a formatted display string for the grade
     * 
     * @return formatted grade string (e.g., "85.50/100.00 (85.50%)")
     */
    public String getFormattedGrade() {
        BigDecimal percentage = calculatePercentage();
        return String.format("%.2f/%.2f (%.2f%%)", 
                grade, maxGrade, percentage);
    }

    /**
     * Validate that the grade does not exceed the maximum grade
     * 
     * @return true if grade is valid
     */
    public boolean isValidGrade() {
        return grade.compareTo(maxGrade) <= 0;
    }

    /**
     * Get the points earned out of total possible points
     * 
     * @return points earned
     */
    public BigDecimal getPointsEarned() {
        return grade;
    }

    /**
     * Get the total possible points
     * 
     * @return total possible points
     */
    public BigDecimal getTotalPossiblePoints() {
        return maxGrade;
    }
}
