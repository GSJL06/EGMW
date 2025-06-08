package com.educagestor.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Institution entity representing educational institutions
 * 
 * This entity represents educational institutions that can have
 * multiple students, teachers, and courses associated with them.
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
@Entity
@Table(name = "institutions", indexes = {
    @Index(name = "idx_institutions_name", columnList = "name"),
    @Index(name = "idx_institutions_email", columnList = "email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Institution extends BaseEntity {

    /**
     * Name of the institution
     */
    @NotBlank(message = "Institution name is required")
    @Size(max = 255, message = "Institution name must not exceed 255 characters")
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Physical address of the institution
     */
    @Size(max = 500, message = "Address must not exceed 500 characters")
    @Column(name = "address", length = 500)
    private String address;

    /**
     * Phone number of the institution
     */
    @Size(max = 20, message = "Phone must not exceed 20 characters")
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * Email address of the institution
     */
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Column(name = "email")
    private String email;

    /**
     * Website URL of the institution
     */
    @Size(max = 255, message = "Website must not exceed 255 characters")
    @Column(name = "website")
    private String website;

    /**
     * List of students associated with this institution
     */
    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Student> students = new ArrayList<>();

    /**
     * List of teachers associated with this institution
     */
    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Teacher> teachers = new ArrayList<>();

    /**
     * List of courses offered by this institution
     */
    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Course> courses = new ArrayList<>();

    /**
     * Add a student to this institution
     * 
     * @param student the student to add
     */
    public void addStudent(Student student) {
        students.add(student);
        student.setInstitution(this);
    }

    /**
     * Remove a student from this institution
     * 
     * @param student the student to remove
     */
    public void removeStudent(Student student) {
        students.remove(student);
        student.setInstitution(null);
    }

    /**
     * Add a teacher to this institution
     * 
     * @param teacher the teacher to add
     */
    public void addTeacher(Teacher teacher) {
        teachers.add(teacher);
        teacher.setInstitution(this);
    }

    /**
     * Remove a teacher from this institution
     * 
     * @param teacher the teacher to remove
     */
    public void removeTeacher(Teacher teacher) {
        teachers.remove(teacher);
        teacher.setInstitution(null);
    }

    /**
     * Add a course to this institution
     * 
     * @param course the course to add
     */
    public void addCourse(Course course) {
        courses.add(course);
        course.setInstitution(this);
    }

    /**
     * Remove a course from this institution
     * 
     * @param course the course to remove
     */
    public void removeCourse(Course course) {
        courses.remove(course);
        course.setInstitution(null);
    }

    /**
     * Get the total number of students in this institution
     * 
     * @return number of students
     */
    public int getStudentCount() {
        return students.size();
    }

    /**
     * Get the total number of teachers in this institution
     * 
     * @return number of teachers
     */
    public int getTeacherCount() {
        return teachers.size();
    }

    /**
     * Get the total number of courses in this institution
     * 
     * @return number of courses
     */
    public int getCourseCount() {
        return courses.size();
    }
}
