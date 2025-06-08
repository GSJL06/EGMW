package com.educagestor.service;

import com.educagestor.dto.TeacherDto;
import com.educagestor.entity.Institution;
import com.educagestor.entity.Teacher;
import com.educagestor.entity.User;
import com.educagestor.entity.enums.TeacherStatus;
import com.educagestor.repository.InstitutionRepository;
import com.educagestor.repository.TeacherRepository;
import com.educagestor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service class for Teacher entity operations
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final InstitutionRepository institutionRepository;

    /**
     * Create a new teacher
     */
    public TeacherDto createTeacher(TeacherDto teacherDto) {
        log.info("Creating new teacher: {}", teacherDto.getEmployeeCode());
        
        validateTeacherData(teacherDto);
        
        User user = null;
        if (teacherDto.getUserId() != null) {
            user = userRepository.findById(teacherDto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + teacherDto.getUserId()));
        }
        
        Institution institution = null;
        if (teacherDto.getInstitutionId() != null) {
            institution = institutionRepository.findById(teacherDto.getInstitutionId())
                    .orElseThrow(() -> new RuntimeException("Institution not found with ID: " + teacherDto.getInstitutionId()));
        }
        
        Teacher teacher = Teacher.builder()
                .user(user)
                .institution(institution)
                .employeeCode(teacherDto.getEmployeeCode())
                .firstName(teacherDto.getFirstName())
                .lastName(teacherDto.getLastName())
                .dateOfBirth(teacherDto.getDateOfBirth())
                .gender(teacherDto.getGender())
                .address(teacherDto.getAddress())
                .phone(teacherDto.getPhone())
                .specialization(teacherDto.getSpecialization())
                .hireDate(teacherDto.getHireDate())
                .status(teacherDto.getStatus())
                .build();
        
        Teacher savedTeacher = teacherRepository.save(teacher);
        log.info("Teacher created successfully with ID: {}", savedTeacher.getId());
        
        return convertToDto(savedTeacher);
    }

    /**
     * Get teacher by ID
     */
    @Transactional(readOnly = true)
    public Optional<TeacherDto> getTeacherById(Long id) {
        log.debug("Getting teacher by ID: {}", id);
        return teacherRepository.findById(id).map(this::convertToDto);
    }

    /**
     * Get teacher by employee code
     */
    @Transactional(readOnly = true)
    public Optional<TeacherDto> getTeacherByEmployeeCode(String employeeCode) {
        log.debug("Getting teacher by employee code: {}", employeeCode);
        return teacherRepository.findByEmployeeCode(employeeCode).map(this::convertToDto);
    }

    /**
     * Get all teachers with pagination
     */
    @Transactional(readOnly = true)
    public Page<TeacherDto> getAllTeachers(Pageable pageable) {
        log.debug("Getting all teachers with pagination");
        return teacherRepository.findAll(pageable).map(this::convertToDto);
    }

    /**
     * Get teachers by institution
     */
    @Transactional(readOnly = true)
    public Page<TeacherDto> getTeachersByInstitution(Long institutionId, Pageable pageable) {
        log.debug("Getting teachers by institution: {}", institutionId);
        return teacherRepository.findByInstitutionId(institutionId, pageable).map(this::convertToDto);
    }

    /**
     * Get teachers by status
     */
    @Transactional(readOnly = true)
    public Page<TeacherDto> getTeachersByStatus(TeacherStatus status, Pageable pageable) {
        log.debug("Getting teachers by status: {}", status);
        return teacherRepository.findByStatus(status, pageable).map(this::convertToDto);
    }

    /**
     * Search teachers
     */
    @Transactional(readOnly = true)
    public Page<TeacherDto> searchTeachers(String searchTerm, Pageable pageable) {
        log.debug("Searching teachers with term: {}", searchTerm);
        return teacherRepository.searchByNameCodeOrSpecialization(searchTerm, pageable).map(this::convertToDto);
    }

    /**
     * Update teacher
     */
    public TeacherDto updateTeacher(Long id, TeacherDto teacherDto) {
        log.info("Updating teacher with ID: {}", id);
        
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found with ID: " + id));
        
        if (teacherDto.getFirstName() != null) {
            teacher.setFirstName(teacherDto.getFirstName());
        }
        if (teacherDto.getLastName() != null) {
            teacher.setLastName(teacherDto.getLastName());
        }
        if (teacherDto.getDateOfBirth() != null) {
            teacher.setDateOfBirth(teacherDto.getDateOfBirth());
        }
        if (teacherDto.getGender() != null) {
            teacher.setGender(teacherDto.getGender());
        }
        if (teacherDto.getAddress() != null) {
            teacher.setAddress(teacherDto.getAddress());
        }
        if (teacherDto.getPhone() != null) {
            teacher.setPhone(teacherDto.getPhone());
        }
        if (teacherDto.getSpecialization() != null) {
            teacher.setSpecialization(teacherDto.getSpecialization());
        }
        if (teacherDto.getStatus() != null) {
            teacher.setStatus(teacherDto.getStatus());
        }
        
        Teacher updatedTeacher = teacherRepository.save(teacher);
        log.info("Teacher updated successfully: {}", updatedTeacher.getEmployeeCode());
        
        return convertToDto(updatedTeacher);
    }

    /**
     * Delete teacher
     */
    public void deleteTeacher(Long id) {
        log.info("Deleting teacher with ID: {}", id);
        
        if (!teacherRepository.existsById(id)) {
            throw new RuntimeException("Teacher not found with ID: " + id);
        }
        
        teacherRepository.deleteById(id);
        log.info("Teacher deleted successfully with ID: {}", id);
    }

    /**
     * Check if employee code exists
     */
    @Transactional(readOnly = true)
    public boolean existsByEmployeeCode(String employeeCode) {
        return teacherRepository.existsByEmployeeCode(employeeCode);
    }

    /**
     * Count teachers by institution
     */
    @Transactional(readOnly = true)
    public long countByInstitution(Long institutionId) {
        return teacherRepository.countByInstitutionId(institutionId);
    }

    /**
     * Count teachers by status
     */
    @Transactional(readOnly = true)
    public long countByStatus(TeacherStatus status) {
        return teacherRepository.countByStatus(status);
    }

    /**
     * Convert Teacher entity to TeacherDto
     */
    private TeacherDto convertToDto(Teacher teacher) {
        TeacherDto dto = TeacherDto.builder()
                .id(teacher.getId())
                .employeeCode(teacher.getEmployeeCode())
                .firstName(teacher.getFirstName())
                .lastName(teacher.getLastName())
                .dateOfBirth(teacher.getDateOfBirth())
                .gender(teacher.getGender())
                .address(teacher.getAddress())
                .phone(teacher.getPhone())
                .specialization(teacher.getSpecialization())
                .hireDate(teacher.getHireDate())
                .status(teacher.getStatus())
                .createdAt(teacher.getCreatedAt())
                .updatedAt(teacher.getUpdatedAt())
                .build();
        
        if (teacher.getUser() != null) {
            dto.setUserId(teacher.getUser().getId());
            dto.setUsername(teacher.getUser().getUsername());
            dto.setEmail(teacher.getUser().getEmail());
        }
        
        if (teacher.getInstitution() != null) {
            dto.setInstitutionId(teacher.getInstitution().getId());
            dto.setInstitutionName(teacher.getInstitution().getName());
        }
        
        dto.setFullName(teacher.getFullName());
        dto.setAge(teacher.getAge());
        dto.setYearsOfExperience(teacher.getYearsOfExperience());
        dto.setCourseCount(teacher.getCourseCount());
        dto.setActive(teacher.isActive());
        
        return dto;
    }

    /**
     * Validate teacher data
     */
    private void validateTeacherData(TeacherDto teacherDto) {
        if (existsByEmployeeCode(teacherDto.getEmployeeCode())) {
            throw new RuntimeException("Employee code already exists: " + teacherDto.getEmployeeCode());
        }
    }
}
