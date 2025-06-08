package com.educagestor.controller;

import com.educagestor.dto.CourseDto;
import com.educagestor.entity.enums.CourseStatus;
import com.educagestor.service.CourseService;
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

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Course entity operations
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Courses", description = "Course management operations")
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @Operation(summary = "Create course", description = "Create a new course")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Course created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "409", description = "Course code already exists"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<CourseDto> createCourse(@Valid @RequestBody CourseDto courseDto) {
        log.info("Creating course: {}", courseDto.getCode());
        
        try {
            CourseDto createdCourse = courseService.createCourse(courseDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCourse);
        } catch (Exception e) {
            log.error("Failed to create course: {}", courseDto.getCode(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @Operation(summary = "Get course by ID", description = "Retrieve course information by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Course found"),
        @ApiResponse(responseCode = "404", description = "Course not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<CourseDto> getCourseById(
            @Parameter(description = "Course ID") @PathVariable Long id) {
        
        log.debug("Getting course by ID: {}", id);
        
        Optional<CourseDto> course = courseService.getCourseById(id);
        return course.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @Operation(summary = "Get course by code", description = "Retrieve course information by course code")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Course found"),
        @ApiResponse(responseCode = "404", description = "Course not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<CourseDto> getCourseByCode(
            @Parameter(description = "Course code") @PathVariable String code) {
        
        log.debug("Getting course by code: {}", code);
        
        Optional<CourseDto> course = courseService.getCourseByCode(code);
        return course.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @Operation(summary = "Get all courses", description = "Retrieve all courses with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Courses retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Page<CourseDto>> getAllCourses(
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.debug("Getting all courses with pagination");
        
        Page<CourseDto> courses = courseService.getAllCourses(pageable);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/institution/{institutionId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @Operation(summary = "Get courses by institution", description = "Retrieve courses by institution with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Courses retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Page<CourseDto>> getCoursesByInstitution(
            @Parameter(description = "Institution ID") @PathVariable Long institutionId,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.debug("Getting courses by institution: {}", institutionId);
        
        Page<CourseDto> courses = courseService.getCoursesByInstitution(institutionId, pageable);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @Operation(summary = "Get courses by teacher", description = "Retrieve courses by teacher with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Courses retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Page<CourseDto>> getCoursesByTeacher(
            @Parameter(description = "Teacher ID") @PathVariable Long teacherId,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.debug("Getting courses by teacher: {}", teacherId);
        
        Page<CourseDto> courses = courseService.getCoursesByTeacher(teacherId, pageable);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @Operation(summary = "Get courses by status", description = "Retrieve courses by status with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Courses retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Page<CourseDto>> getCoursesByStatus(
            @Parameter(description = "Course status") @PathVariable CourseStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.debug("Getting courses by status: {}", status);
        
        Page<CourseDto> courses = courseService.getCoursesByStatus(status, pageable);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/current")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @Operation(summary = "Get current courses", description = "Retrieve courses currently in session")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Current courses retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Page<CourseDto>> getCurrentCourses(
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.debug("Getting current courses");
        
        Page<CourseDto> courses = courseService.getCurrentCourses(pageable);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/available")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @Operation(summary = "Get available courses", description = "Retrieve courses available for enrollment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Available courses retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<List<CourseDto>> getAvailableCoursesForEnrollment() {
        log.debug("Getting available courses for enrollment");
        
        List<CourseDto> courses = courseService.getAvailableCoursesForEnrollment();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('STUDENT')")
    @Operation(summary = "Search courses", description = "Search courses by name, code, or description")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search completed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid search term"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Page<CourseDto>> searchCourses(
            @Parameter(description = "Search term") @RequestParam String searchTerm,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.debug("Searching courses with term: {}", searchTerm);
        
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Page<CourseDto> courses = courseService.searchCourses(searchTerm.trim(), pageable);
        return ResponseEntity.ok(courses);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @Operation(summary = "Update course", description = "Update course information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Course updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Course not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<CourseDto> updateCourse(
            @Parameter(description = "Course ID") @PathVariable Long id,
            @Valid @RequestBody CourseDto courseDto) {
        
        log.info("Updating course with ID: {}", id);
        
        try {
            CourseDto updatedCourse = courseService.updateCourse(id, courseDto);
            return ResponseEntity.ok(updatedCourse);
        } catch (RuntimeException e) {
            log.error("Failed to update course with ID: {}", id, e);
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete course", description = "Delete course record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Course deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Course not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Void> deleteCourse(
            @Parameter(description = "Course ID") @PathVariable Long id) {
        
        log.info("Deleting course with ID: {}", id);
        
        try {
            courseService.deleteCourse(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Failed to delete course with ID: {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/exists/code/{code}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @Operation(summary = "Check course code existence", description = "Check if course code already exists")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Check completed"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Boolean> existsByCode(
            @Parameter(description = "Course code") @PathVariable String code) {
        
        log.debug("Checking existence of course code: {}", code);
        
        boolean exists = courseService.existsByCode(code);
        return ResponseEntity.ok(exists);
    }
}
