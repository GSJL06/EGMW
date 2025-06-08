package com.educagestor.dto;

import com.educagestor.entity.enums.Gender;
import com.educagestor.entity.enums.TeacherStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Teacher entity
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherDto {

    private Long id;

    private Long userId;
    private String username;
    private String email;

    private Long institutionId;
    private String institutionName;

    @NotBlank(message = "Employee code is required")
    @Size(max = 20, message = "Employee code must not exceed 20 characters")
    private String employeeCode;

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

    @Size(max = 255, message = "Specialization must not exceed 255 characters")
    private String specialization;

    @NotNull(message = "Hire date is required")
    @Builder.Default
    private LocalDate hireDate = LocalDate.now();

    @Builder.Default
    private TeacherStatus status = TeacherStatus.ACTIVE;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Computed fields
    private String fullName;
    private Integer age;
    private int yearsOfExperience;
    private int courseCount;
    private boolean active;

    /**
     * Get the teacher's full name
     */
    public String getFullName() {
        if (fullName != null) {
            return fullName;
        }
        return firstName + " " + lastName;
    }

    /**
     * Get the teacher's age based on date of birth
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
     * Get years of experience based on hire date
     */
    public int getYearsOfExperience() {
        if (hireDate == null) {
            return 0;
        }
        return LocalDate.now().getYear() - hireDate.getYear();
    }

    /**
     * Check if the teacher is currently active
     */
    public boolean isActive() {
        return status == TeacherStatus.ACTIVE;
    }
}
