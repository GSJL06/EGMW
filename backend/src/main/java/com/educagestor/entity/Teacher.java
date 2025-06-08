package com.educagestor.entity;

import com.educagestor.entity.enums.Gender;
import com.educagestor.entity.enums.TeacherStatus;
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
 * Teacher entity representing teachers in the educational system
 * 
 * This entity contains all teacher information including personal details,
 * contact information, specialization, and employment status. It maintains
 * relationships with User, Institution, Course, and EducationalResource entities.
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
@Entity
@Table(name = "teachers", indexes = {
    @Index(name = "idx_teachers_user_id", columnList = "user_id"),
    @Index(name = "idx_teachers_institution_id", columnList = "institution_id"),
    @Index(name = "idx_teachers_employee_code", columnList = "employee_code"),
    @Index(name = "idx_teachers_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Teacher extends BaseEntity {

    /**
     * Associated user account for authentication
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    /**
     * Institution where the teacher works
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", referencedColumnName = "id")
    private Institution institution;

    /**
     * Unique employee identification code
     */
    @NotBlank(message = "Employee code is required")
    @Size(max = 20, message = "Employee code must not exceed 20 characters")
    @Column(name = "employee_code", nullable = false, unique = true, length = 20)
    private String employeeCode;

    /**
     * Teacher's first name
     */
    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    /**
     * Teacher's last name
     */
    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    /**
     * Teacher's date of birth
     */
    @Past(message = "Date of birth must be in the past")
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    /**
     * Teacher's gender
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private Gender gender;

    /**
     * Teacher's address
     */
    @Size(max = 500, message = "Address must not exceed 500 characters")
    @Column(name = "address", length = 500)
    private String address;

    /**
     * Teacher's phone number
     */
    @Size(max = 20, message = "Phone must not exceed 20 characters")
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * Teacher's area of specialization
     */
    @Size(max = 255, message = "Specialization must not exceed 255 characters")
    @Column(name = "specialization")
    private String specialization;

    /**
     * Date when the teacher was hired
     */
    @NotNull(message = "Hire date is required")
    @Column(name = "hire_date", nullable = false)
    @Builder.Default
    private LocalDate hireDate = LocalDate.now();

    /**
     * Current employment status of the teacher
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private TeacherStatus status = TeacherStatus.ACTIVE;

    /**
     * List of courses taught by this teacher
     */
    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Course> courses = new ArrayList<>();

    /**
     * List of educational resources created by this teacher
     */
    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<EducationalResource> educationalResources = new ArrayList<>();

    /**
     * Get the teacher's full name
     * 
     * @return concatenated first and last name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Get the teacher's age based on date of birth
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
     * Get years of experience based on hire date
     * 
     * @return years of experience
     */
    public int getYearsOfExperience() {
        return LocalDate.now().getYear() - hireDate.getYear();
    }

    /**
     * Check if the teacher is currently active
     * 
     * @return true if status is ACTIVE
     */
    public boolean isActive() {
        return status == TeacherStatus.ACTIVE;
    }

    /**
     * Check if the teacher can teach courses
     * 
     * @return true if teacher can teach
     */
    public boolean canTeachCourses() {
        return status.canTeachCourses();
    }

    /**
     * Add a course to this teacher
     * 
     * @param course the course to add
     */
    public void addCourse(Course course) {
        courses.add(course);
        course.setTeacher(this);
    }

    /**
     * Remove a course from this teacher
     * 
     * @param course the course to remove
     */
    public void removeCourse(Course course) {
        courses.remove(course);
        course.setTeacher(null);
    }

    /**
     * Add an educational resource to this teacher
     * 
     * @param resource the resource to add
     */
    public void addEducationalResource(EducationalResource resource) {
        educationalResources.add(resource);
        resource.setTeacher(this);
    }

    /**
     * Remove an educational resource from this teacher
     * 
     * @param resource the resource to remove
     */
    public void removeEducationalResource(EducationalResource resource) {
        educationalResources.remove(resource);
        resource.setTeacher(null);
    }

    /**
     * Get the number of courses taught by this teacher
     * 
     * @return number of courses
     */
    public int getCourseCount() {
        return courses.size();
    }

    /**
     * Get active courses only
     * 
     * @return list of active courses
     */
    public List<Course> getActiveCourses() {
        return courses.stream()
                .filter(course -> course.getStatus().isActive())
                .toList();
    }

    /**
     * Get the number of educational resources created by this teacher
     * 
     * @return number of resources
     */
    public int getEducationalResourceCount() {
        return educationalResources.size();
    }
}
