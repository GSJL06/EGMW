package com.educagestor.service;

import com.educagestor.dto.StudentDto;
import com.educagestor.entity.Institution;
import com.educagestor.entity.Student;
import com.educagestor.entity.User;
import com.educagestor.entity.enums.StudentStatus;
import com.educagestor.entity.enums.UserRole;
import com.educagestor.repository.InstitutionRepository;
import com.educagestor.repository.StudentRepository;
import com.educagestor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service class for Student entity operations
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final InstitutionRepository institutionRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Create a new student
     * 
     * @param studentDto the student data
     * @return created student DTO
     */
    public StudentDto createStudent(StudentDto studentDto) {
        log.info("Creating new student: {}", studentDto.getStudentCode());
        
        validateStudentData(studentDto);
        
        // Get or create user
        User user = null;
        if (studentDto.getUserId() != null) {
            user = userRepository.findById(studentDto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + studentDto.getUserId()));
        }
        
        // Get institution
        Institution institution = null;
        if (studentDto.getInstitutionId() != null) {
            institution = institutionRepository.findById(studentDto.getInstitutionId())
                    .orElseThrow(() -> new RuntimeException("Institution not found with ID: " + studentDto.getInstitutionId()));
        }
        
        Student student = Student.builder()
                .user(user)
                .institution(institution)
                .studentCode(studentDto.getStudentCode())
                .firstName(studentDto.getFirstName())
                .lastName(studentDto.getLastName())
                .dateOfBirth(studentDto.getDateOfBirth())
                .gender(studentDto.getGender())
                .address(studentDto.getAddress())
                .phone(studentDto.getPhone())
                .emergencyContact(studentDto.getEmergencyContact())
                .emergencyPhone(studentDto.getEmergencyPhone())
                .enrollmentDate(studentDto.getEnrollmentDate())
                .status(studentDto.getStatus())
                .build();
        
        Student savedStudent = studentRepository.save(student);
        log.info("Student created successfully with ID: {}", savedStudent.getId());
        
        return convertToDto(savedStudent);
    }

    /**
     * Get student by ID
     * 
     * @param id the student ID
     * @return student DTO if found
     */
    @Transactional(readOnly = true)
    public Optional<StudentDto> getStudentById(Long id) {
        log.debug("Getting student by ID: {}", id);
        
        return studentRepository.findById(id)
                .map(this::convertToDto);
    }

    /**
     * Get student by student code
     * 
     * @param studentCode the student code
     * @return student DTO if found
     */
    @Transactional(readOnly = true)
    public Optional<StudentDto> getStudentByCode(String studentCode) {
        log.debug("Getting student by code: {}", studentCode);
        
        return studentRepository.findByStudentCode(studentCode)
                .map(this::convertToDto);
    }

    /**
     * Get all students with pagination
     * 
     * @param pageable pagination information
     * @return page of student DTOs
     */
    @Transactional(readOnly = true)
    public Page<StudentDto> getAllStudents(Pageable pageable) {
        log.debug("Getting all students with pagination");
        
        return studentRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    /**
     * Get students by institution
     * 
     * @param institutionId the institution ID
     * @param pageable pagination information
     * @return page of student DTOs
     */
    @Transactional(readOnly = true)
    public Page<StudentDto> getStudentsByInstitution(Long institutionId, Pageable pageable) {
        log.debug("Getting students by institution: {}", institutionId);
        
        return studentRepository.findByInstitutionId(institutionId, pageable)
                .map(this::convertToDto);
    }

    /**
     * Get students by status
     * 
     * @param status the student status
     * @param pageable pagination information
     * @return page of student DTOs
     */
    @Transactional(readOnly = true)
    public Page<StudentDto> getStudentsByStatus(StudentStatus status, Pageable pageable) {
        log.debug("Getting students by status: {}", status);
        
        return studentRepository.findByStatus(status, pageable)
                .map(this::convertToDto);
    }

    /**
     * Search students by name or code
     * 
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return page of matching student DTOs
     */
    @Transactional(readOnly = true)
    public Page<StudentDto> searchStudents(String searchTerm, Pageable pageable) {
        log.debug("Searching students with term: {}", searchTerm);
        
        return studentRepository.searchByNameOrCode(searchTerm, pageable)
                .map(this::convertToDto);
    }

    /**
     * Update student
     * 
     * @param id the student ID
     * @param studentDto the updated student data
     * @return updated student DTO
     */
    public StudentDto updateStudent(Long id, StudentDto studentDto) {
        log.info("Updating student with ID: {}", id);
        
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + id));
        
        // Update fields if provided
        if (studentDto.getFirstName() != null) {
            student.setFirstName(studentDto.getFirstName());
        }
        
        if (studentDto.getLastName() != null) {
            student.setLastName(studentDto.getLastName());
        }
        
        if (studentDto.getDateOfBirth() != null) {
            student.setDateOfBirth(studentDto.getDateOfBirth());
        }
        
        if (studentDto.getGender() != null) {
            student.setGender(studentDto.getGender());
        }
        
        if (studentDto.getAddress() != null) {
            student.setAddress(studentDto.getAddress());
        }
        
        if (studentDto.getPhone() != null) {
            student.setPhone(studentDto.getPhone());
        }
        
        if (studentDto.getEmergencyContact() != null) {
            student.setEmergencyContact(studentDto.getEmergencyContact());
        }
        
        if (studentDto.getEmergencyPhone() != null) {
            student.setEmergencyPhone(studentDto.getEmergencyPhone());
        }
        
        if (studentDto.getStatus() != null) {
            student.setStatus(studentDto.getStatus());
        }
        
        Student updatedStudent = studentRepository.save(student);
        log.info("Student updated successfully: {}", updatedStudent.getStudentCode());
        
        return convertToDto(updatedStudent);
    }

    /**
     * Delete student
     * 
     * @param id the student ID
     */
    public void deleteStudent(Long id) {
        log.info("Deleting student with ID: {}", id);
        
        if (!studentRepository.existsById(id)) {
            throw new RuntimeException("Student not found with ID: " + id);
        }
        
        studentRepository.deleteById(id);
        log.info("Student deleted successfully with ID: {}", id);
    }

    /**
     * Check if student code exists
     * 
     * @param studentCode the student code to check
     * @return true if student code exists
     */
    @Transactional(readOnly = true)
    public boolean existsByStudentCode(String studentCode) {
        return studentRepository.existsByStudentCode(studentCode);
    }

    /**
     * Count students by institution
     * 
     * @param institutionId the institution ID
     * @return count of students
     */
    @Transactional(readOnly = true)
    public long countByInstitution(Long institutionId) {
        return studentRepository.countByInstitutionId(institutionId);
    }

    /**
     * Count students by status
     * 
     * @param status the student status
     * @return count of students
     */
    @Transactional(readOnly = true)
    public long countByStatus(StudentStatus status) {
        return studentRepository.countByStatus(status);
    }

    /**
     * Convert Student entity to StudentDto
     * 
     * @param student the student entity
     * @return student DTO
     */
    private StudentDto convertToDto(Student student) {
        StudentDto dto = StudentDto.builder()
                .id(student.getId())
                .studentCode(student.getStudentCode())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .dateOfBirth(student.getDateOfBirth())
                .gender(student.getGender())
                .address(student.getAddress())
                .phone(student.getPhone())
                .emergencyContact(student.getEmergencyContact())
                .emergencyPhone(student.getEmergencyPhone())
                .enrollmentDate(student.getEnrollmentDate())
                .status(student.getStatus())
                .createdAt(student.getCreatedAt())
                .updatedAt(student.getUpdatedAt())
                .build();
        
        // Set user information
        if (student.getUser() != null) {
            dto.setUserId(student.getUser().getId());
            dto.setUsername(student.getUser().getUsername());
            dto.setEmail(student.getUser().getEmail());
        }
        
        // Set institution information
        if (student.getInstitution() != null) {
            dto.setInstitutionId(student.getInstitution().getId());
            dto.setInstitutionName(student.getInstitution().getName());
        }
        
        // Set computed fields
        dto.setFullName(student.getFullName());
        dto.setAge(student.getAge());
        dto.setEnrollmentCount(student.getEnrollmentCount());
        dto.setActive(student.isActive());
        
        return dto;
    }

    /**
     * Validate student data
     * 
     * @param studentDto the student data to validate
     */
    private void validateStudentData(StudentDto studentDto) {
        if (existsByStudentCode(studentDto.getStudentCode())) {
            throw new RuntimeException("Student code already exists: " + studentDto.getStudentCode());
        }
    }
}
