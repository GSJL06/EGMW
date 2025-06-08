package com.educagestor.controller;

import com.educagestor.dto.StudentDto;
import com.educagestor.entity.enums.StudentStatus;
import com.educagestor.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * REST Controller for Student entity operations
 * 
 * Provides endpoints for student management including CRUD operations,
 * search functionality, and student-specific queries.
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Students", description = "Student management operations")
public class StudentController {

    private final StudentService studentService;

    /**
     * Create a new student
     * 
     * @param studentDto the student data
     * @return created student DTO
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create student", description = "Create a new student record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Student created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "409", description = "Student code already exists"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<StudentDto> createStudent(@Valid @RequestBody StudentDto studentDto) {
        log.info("Creating student: {}", studentDto.getStudentCode());
        
        try {
            StudentDto createdStudent = studentService.createStudent(studentDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdStudent);
        } catch (Exception e) {
            log.error("Failed to create student: {}", studentDto.getStudentCode(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Get student by ID
     * 
     * @param id the student ID
     * @return student DTO if found
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @Operation(summary = "Get student by ID", description = "Retrieve student information by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Student found"),
        @ApiResponse(responseCode = "404", description = "Student not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<StudentDto> getStudentById(
            @Parameter(description = "Student ID") @PathVariable Long id) {
        
        log.debug("Getting student by ID: {}", id);
        
        Optional<StudentDto> student = studentService.getStudentById(id);
        return student.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get student by student code
     * 
     * @param studentCode the student code
     * @return student DTO if found
     */
    @GetMapping("/code/{studentCode}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @Operation(summary = "Get student by code", description = "Retrieve student information by student code")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Student found"),
        @ApiResponse(responseCode = "404", description = "Student not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<StudentDto> getStudentByCode(
            @Parameter(description = "Student code") @PathVariable String studentCode) {
        
        log.debug("Getting student by code: {}", studentCode);
        
        Optional<StudentDto> student = studentService.getStudentByCode(studentCode);
        return student.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all students with pagination
     * 
     * @param pageable pagination parameters
     * @return page of student DTOs
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @Operation(summary = "Get all students", description = "Retrieve all students with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Students retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Page<StudentDto>> getAllStudents(
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.debug("Getting all students with pagination");
        
        Page<StudentDto> students = studentService.getAllStudents(pageable);
        return ResponseEntity.ok(students);
    }

    /**
     * Get students by institution
     * 
     * @param institutionId the institution ID
     * @param pageable pagination parameters
     * @return page of student DTOs
     */
    @GetMapping("/institution/{institutionId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @Operation(summary = "Get students by institution", description = "Retrieve students by institution with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Students retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Page<StudentDto>> getStudentsByInstitution(
            @Parameter(description = "Institution ID") @PathVariable Long institutionId,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.debug("Getting students by institution: {}", institutionId);
        
        Page<StudentDto> students = studentService.getStudentsByInstitution(institutionId, pageable);
        return ResponseEntity.ok(students);
    }

    /**
     * Get students by status
     * 
     * @param status the student status
     * @param pageable pagination parameters
     * @return page of student DTOs
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @Operation(summary = "Get students by status", description = "Retrieve students by status with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Students retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Page<StudentDto>> getStudentsByStatus(
            @Parameter(description = "Student status") @PathVariable StudentStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.debug("Getting students by status: {}", status);
        
        Page<StudentDto> students = studentService.getStudentsByStatus(status, pageable);
        return ResponseEntity.ok(students);
    }

    /**
     * Search students by name or code
     * 
     * @param searchTerm the search term
     * @param pageable pagination parameters
     * @return page of matching student DTOs
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @Operation(summary = "Search students", description = "Search students by name or student code")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search completed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid search term"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Page<StudentDto>> searchStudents(
            @Parameter(description = "Search term") @RequestParam String searchTerm,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.debug("Searching students with term: {}", searchTerm);
        
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Page<StudentDto> students = studentService.searchStudents(searchTerm.trim(), pageable);
        return ResponseEntity.ok(students);
    }

    /**
     * Update student
     * 
     * @param id the student ID
     * @param studentDto the updated student data
     * @return updated student DTO
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @Operation(summary = "Update student", description = "Update student information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Student updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Student not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<StudentDto> updateStudent(
            @Parameter(description = "Student ID") @PathVariable Long id,
            @Valid @RequestBody StudentDto studentDto) {
        
        log.info("Updating student with ID: {}", id);
        
        try {
            StudentDto updatedStudent = studentService.updateStudent(id, studentDto);
            return ResponseEntity.ok(updatedStudent);
        } catch (RuntimeException e) {
            log.error("Failed to update student with ID: {}", id, e);
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Delete student
     * 
     * @param id the student ID
     * @return success response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete student", description = "Delete student record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Student deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Student not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Void> deleteStudent(
            @Parameter(description = "Student ID") @PathVariable Long id) {
        
        log.info("Deleting student with ID: {}", id);
        
        try {
            studentService.deleteStudent(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Failed to delete student with ID: {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Check if student code exists
     * 
     * @param studentCode the student code to check
     * @return true if exists
     */
    @GetMapping("/exists/code/{studentCode}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @Operation(summary = "Check student code existence", description = "Check if student code already exists")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Check completed"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Boolean> existsByStudentCode(
            @Parameter(description = "Student code") @PathVariable String studentCode) {
        
        log.debug("Checking existence of student code: {}", studentCode);
        
        boolean exists = studentService.existsByStudentCode(studentCode);
        return ResponseEntity.ok(exists);
    }

    /**
     * Get student count by institution
     * 
     * @param institutionId the institution ID
     * @return student count
     */
    @GetMapping("/count/institution/{institutionId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @Operation(summary = "Count students by institution", description = "Get student count for an institution")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Long> countByInstitution(
            @Parameter(description = "Institution ID") @PathVariable Long institutionId) {
        
        log.debug("Counting students by institution: {}", institutionId);
        
        long count = studentService.countByInstitution(institutionId);
        return ResponseEntity.ok(count);
    }

    /**
     * Get student count by status
     * 
     * @param status the student status
     * @return student count
     */
    @GetMapping("/count/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @Operation(summary = "Count students by status", description = "Get student count by status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Long> countByStatus(
            @Parameter(description = "Student status") @PathVariable StudentStatus status) {
        
        log.debug("Counting students by status: {}", status);
        
        long count = studentService.countByStatus(status);
        return ResponseEntity.ok(count);
    }
}
