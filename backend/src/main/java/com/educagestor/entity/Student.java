package com.educagestor.entity;

import com.educagestor.entity.enums.Gender;
import com.educagestor.entity.enums.StudentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Student entity representing students in the educational system
 * 
 * This entity contains all student information including personal details,
 * contact information, and academic status. It maintains relationships
 * with User, Institution, and Enrollment entities.
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
@Entity
@Table(name = "students", indexes = {
    @Index(name = "idx_students_user_id", columnList = "user_id"),
    @Index(name = "idx_students_institution_id", columnList = "institution_id"),
    @Index(name = "idx_students_student_code", columnList = "student_code"),
    @Index(name = "idx_students_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student extends BaseEntity {

    /**
     * Associated user account for authentication
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    /**
     * Institution where the student is enrolled
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", referencedColumnName = "id")
    private Institution institution;

    /**
     * Unique student identification code
     */
    @NotBlank(message = "Student code is required")
    @Size(max = 20, message = "Student code must not exceed 20 characters")
    @Column(name = "student_code", nullable = false, unique = true, length = 20)
    private String studentCode;

    /**
     * Student's first name
     */
    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    /**
     * Student's last name
     */
    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    /**
     * Student's date of birth
     */
    @Past(message = "Date of birth must be in the past")
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    /**
     * Student's gender
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private Gender gender;

    /**
     * Student's address
     */
    @Size(max = 500, message = "Address must not exceed 500 characters")
    @Column(name = "address", length = 500)
    private String address;

    /**
     * Student's phone number
     */
    @Size(max = 20, message = "Phone must not exceed 20 characters")
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * Emergency contact name
     */
    @Size(max = 255, message = "Emergency contact must not exceed 255 characters")
    @Column(name = "emergency_contact")
    private String emergencyContact;

    /**
     * Emergency contact phone number
     */
    @Size(max = 20, message = "Emergency phone must not exceed 20 characters")
    @Column(name = "emergency_phone", length = 20)
    private String emergencyPhone;

    /**
     * Date when the student enrolled
     */
    @NotNull(message = "Enrollment date is required")
    @Column(name = "enrollment_date", nullable = false)
    @Builder.Default
    private LocalDate enrollmentDate = LocalDate.now();

    /**
     * Current status of the student
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private StudentStatus status = StudentStatus.ACTIVE;

    /**
     * List of course enrollments for this student
     */
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Enrollment> enrollments = new ArrayList<>();

    /**
     * Get the student's full name
     * 
     * @return concatenated first and last name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Get the student's age based on date of birth
     * 
     * @return age in years, or null if date of birth is not set
     */
    public Integer getAge() {
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

    /**
     * Check if the student can enroll in courses
     * 
     * @return true if student can enroll
     */
    public boolean canEnrollInCourses() {
        return status.canEnrollInCourses();
    }

    /**
     * Add an enrollment to this student
     * 
     * @param enrollment the enrollment to add
     */
    public void addEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
        enrollment.setStudent(this);
    }

    /**
     * Remove an enrollment from this student
     * 
     * @param enrollment the enrollment to remove
     */
    public void removeEnrollment(Enrollment enrollment) {
        enrollments.remove(enrollment);
        enrollment.setStudent(null);
    }

    /**
     * Get the number of courses the student is enrolled in
     * 
     * @return number of enrollments
     */
    public int getEnrollmentCount() {
        return enrollments.size();
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
}
