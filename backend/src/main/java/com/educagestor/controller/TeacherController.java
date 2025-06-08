package com.educagestor.controller;

import com.educagestor.dto.TeacherDto;
import com.educagestor.entity.enums.TeacherStatus;
import com.educagestor.service.TeacherService;
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
 * REST Controller for Teacher entity operations
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Teachers", description = "Teacher management operations")
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create teacher", description = "Create a new teacher record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Teacher created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "409", description = "Employee code already exists"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<TeacherDto> createTeacher(@Valid @RequestBody TeacherDto teacherDto) {
        log.info("Creating teacher: {}", teacherDto.getEmployeeCode());
        
        try {
            TeacherDto createdTeacher = teacherService.createTeacher(teacherDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTeacher);
        } catch (Exception e) {
            log.error("Failed to create teacher: {}", teacherDto.getEmployeeCode(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get teacher by ID", description = "Retrieve teacher information by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Teacher found"),
        @ApiResponse(responseCode = "404", description = "Teacher not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<TeacherDto> getTeacherById(
            @Parameter(description = "Teacher ID") @PathVariable Long id) {
        
        log.debug("Getting teacher by ID: {}", id);
        
        Optional<TeacherDto> teacher = teacherService.getTeacherById(id);
        return teacher.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{employeeCode}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get teacher by employee code", description = "Retrieve teacher information by employee code")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Teacher found"),
        @ApiResponse(responseCode = "404", description = "Teacher not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<TeacherDto> getTeacherByEmployeeCode(
            @Parameter(description = "Employee code") @PathVariable String employeeCode) {
        
        log.debug("Getting teacher by employee code: {}", employeeCode);
        
        Optional<TeacherDto> teacher = teacherService.getTeacherByEmployeeCode(employeeCode);
        return teacher.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all teachers", description = "Retrieve all teachers with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Teachers retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Page<TeacherDto>> getAllTeachers(
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.debug("Getting all teachers with pagination");
        
        Page<TeacherDto> teachers = teacherService.getAllTeachers(pageable);
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/institution/{institutionId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get teachers by institution", description = "Retrieve teachers by institution with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Teachers retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Page<TeacherDto>> getTeachersByInstitution(
            @Parameter(description = "Institution ID") @PathVariable Long institutionId,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.debug("Getting teachers by institution: {}", institutionId);
        
        Page<TeacherDto> teachers = teacherService.getTeachersByInstitution(institutionId, pageable);
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get teachers by status", description = "Retrieve teachers by status with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Teachers retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Page<TeacherDto>> getTeachersByStatus(
            @Parameter(description = "Teacher status") @PathVariable TeacherStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.debug("Getting teachers by status: {}", status);
        
        Page<TeacherDto> teachers = teacherService.getTeachersByStatus(status, pageable);
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Search teachers", description = "Search teachers by name, code, or specialization")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search completed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid search term"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Page<TeacherDto>> searchTeachers(
            @Parameter(description = "Search term") @RequestParam String searchTerm,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.debug("Searching teachers with term: {}", searchTerm);
        
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Page<TeacherDto> teachers = teacherService.searchTeachers(searchTerm.trim(), pageable);
        return ResponseEntity.ok(teachers);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update teacher", description = "Update teacher information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Teacher updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Teacher not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<TeacherDto> updateTeacher(
            @Parameter(description = "Teacher ID") @PathVariable Long id,
            @Valid @RequestBody TeacherDto teacherDto) {
        
        log.info("Updating teacher with ID: {}", id);
        
        try {
            TeacherDto updatedTeacher = teacherService.updateTeacher(id, teacherDto);
            return ResponseEntity.ok(updatedTeacher);
        } catch (RuntimeException e) {
            log.error("Failed to update teacher with ID: {}", id, e);
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete teacher", description = "Delete teacher record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Teacher deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Teacher not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Void> deleteTeacher(
            @Parameter(description = "Teacher ID") @PathVariable Long id) {
        
        log.info("Deleting teacher with ID: {}", id);
        
        try {
            teacherService.deleteTeacher(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Failed to delete teacher with ID: {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/exists/code/{employeeCode}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Check employee code existence", description = "Check if employee code already exists")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Check completed"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Boolean> existsByEmployeeCode(
            @Parameter(description = "Employee code") @PathVariable String employeeCode) {
        
        log.debug("Checking existence of employee code: {}", employeeCode);
        
        boolean exists = teacherService.existsByEmployeeCode(employeeCode);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/count/institution/{institutionId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Count teachers by institution", description = "Get teacher count for an institution")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Long> countByInstitution(
            @Parameter(description = "Institution ID") @PathVariable Long institutionId) {
        
        log.debug("Counting teachers by institution: {}", institutionId);
        
        long count = teacherService.countByInstitution(institutionId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Count teachers by status", description = "Get teacher count by status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Long> countByStatus(
            @Parameter(description = "Teacher status") @PathVariable TeacherStatus status) {
        
        log.debug("Counting teachers by status: {}", status);
        
        long count = teacherService.countByStatus(status);
        return ResponseEntity.ok(count);
    }
}
