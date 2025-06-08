package com.educagestor.dto;

import com.educagestor.entity.enums.Gender;
import com.educagestor.entity.enums.StudentStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Student entity
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDto {

    private Long id;

    private Long userId;
    private String username;
    private String email;

    private Long institutionId;
    private String institutionName;

    @NotBlank(message = "Student code is required")
    @Size(max = 20, message = "Student code must not exceed 20 characters")
    private String studentCode;

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private Gender gender;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;

    @Size(max = 255, message = "Emergency contact must not exceed 255 characters")
    private String emergencyContact;

    @Size(max = 20, message = "Emergency phone must not exceed 20 characters")
    private String emergencyPhone;

    @NotNull(message = "Enrollment date is required")
    @Builder.Default
    private LocalDate enrollmentDate = LocalDate.now();

    @Builder.Default
    private StudentStatus status = StudentStatus.ACTIVE;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Computed fields
    private String fullName;
    private Integer age;
    private int enrollmentCount;
    private boolean active;

    /**
     * Get the student's full name
     * 
     * @return concatenated first and last name
     */
    public String getFullName() {
        if (fullName != null) {
            return fullName;
        }
        return firstName + " " + lastName;
    }

    /**
     * Get the student's age based on date of birth
     * 
     * @return age in years, or null if date of birth is not set
     */
    public Integer getAge() {
        if (age != null) {
            return age;
        }
        if (dateOfBirth == null) {
            return null;
        }
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }

    /**
     * Check if the student is currently active
     * 
     * @return true if status is ACTIVE
     */
    public boolean isActive() {
        return status == StudentStatus.ACTIVE;
    }
}

/**
 * DTO for student creation requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class CreateStudentDto {

    private Long userId;
    private Long institutionId;

    @NotBlank(message = "Student code is required")
    @Size(max = 20, message = "Student code must not exceed 20 characters")
    private String studentCode;

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private Gender gender;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;

    @Size(max = 255, message = "Emergency contact must not exceed 255 characters")
    private String emergencyContact;

    @Size(max = 20, message = "Emergency phone must not exceed 20 characters")
    private String emergencyPhone;

    @Builder.Default
    private LocalDate enrollmentDate = LocalDate.now();

    @Builder.Default
    private StudentStatus status = StudentStatus.ACTIVE;

    // User creation fields (if creating user along with student)
    private String username;
    private String email;
    private String password;
}

/**
 * DTO for student update requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class UpdateStudentDto {

    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private Gender gender;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;

    @Size(max = 255, message = "Emergency contact must not exceed 255 characters")
    private String emergencyContact;

    @Size(max = 20, message = "Emergency phone must not exceed 20 characters")
    private String emergencyPhone;

    private StudentStatus status;
}
