package com.educagestor.controller;

import com.educagestor.entity.enums.CourseStatus;
import com.educagestor.entity.enums.StudentStatus;
import com.educagestor.entity.enums.TeacherStatus;
import com.educagestor.entity.enums.UserRole;
import com.educagestor.service.CourseService;
import com.educagestor.service.StudentService;
import com.educagestor.service.TeacherService;
import com.educagestor.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for Dashboard operations
 * 
 * Provides endpoints for dashboard statistics and overview data.
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Dashboard", description = "Dashboard and statistics operations")
public class DashboardController {

    private final UserService userService;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final CourseService courseService;

    /**
     * Get dashboard statistics
     * 
     * @return dashboard statistics
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @Operation(summary = "Get dashboard statistics", description = "Retrieve overall system statistics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<DashboardStats> getDashboardStats() {
        log.debug("Getting dashboard statistics");
        
        try {
            DashboardStats stats = DashboardStats.builder()
                    .userStats(getUserStats())
                    .studentStats(getStudentStats())
                    .teacherStats(getTeacherStats())
                    .courseStats(getCourseStats())
                    .build();
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting dashboard statistics", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get user statistics
     * 
     * @return user statistics
     */
    @GetMapping("/stats/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user statistics", description = "Retrieve user-related statistics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User statistics retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<UserStats> getUserStatistics() {
        log.debug("Getting user statistics");
        
        UserStats stats = getUserStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * Get student statistics
     * 
     * @return student statistics
     */
    @GetMapping("/stats/students")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @Operation(summary = "Get student statistics", description = "Retrieve student-related statistics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Student statistics retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<StudentStats> getStudentStatistics() {
        log.debug("Getting student statistics");
        
        StudentStats stats = getStudentStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * Get teacher statistics
     * 
     * @return teacher statistics
     */
    @GetMapping("/stats/teachers")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get teacher statistics", description = "Retrieve teacher-related statistics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Teacher statistics retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<TeacherStats> getTeacherStatistics() {
        log.debug("Getting teacher statistics");
        
        TeacherStats stats = getTeacherStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * Get course statistics
     * 
     * @return course statistics
     */
    @GetMapping("/stats/courses")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @Operation(summary = "Get course statistics", description = "Retrieve course-related statistics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Course statistics retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<CourseStats> getCourseStatistics() {
        log.debug("Getting course statistics");
        
        CourseStats stats = getCourseStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * Build user statistics
     */
    private UserStats getUserStats() {
        return UserStats.builder()
                .totalUsers(userService.countByRole(UserRole.ADMIN) + 
                           userService.countByRole(UserRole.TEACHER) + 
                           userService.countByRole(UserRole.STUDENT))
                .adminCount(userService.countByRole(UserRole.ADMIN))
                .teacherCount(userService.countByRole(UserRole.TEACHER))
                .studentCount(userService.countByRole(UserRole.STUDENT))
                .build();
    }

    /**
     * Build student statistics
     */
    private StudentStats getStudentStats() {
        return StudentStats.builder()
                .totalStudents(studentService.countByStatus(StudentStatus.ACTIVE) +
                              studentService.countByStatus(StudentStatus.INACTIVE) +
                              studentService.countByStatus(StudentStatus.GRADUATED) +
                              studentService.countByStatus(StudentStatus.SUSPENDED))
                .activeStudents(studentService.countByStatus(StudentStatus.ACTIVE))
                .inactiveStudents(studentService.countByStatus(StudentStatus.INACTIVE))
                .graduatedStudents(studentService.countByStatus(StudentStatus.GRADUATED))
                .suspendedStudents(studentService.countByStatus(StudentStatus.SUSPENDED))
                .build();
    }

    /**
     * Build teacher statistics
     */
    private TeacherStats getTeacherStats() {
        return TeacherStats.builder()
                .totalTeachers(teacherService.countByStatus(TeacherStatus.ACTIVE) +
                              teacherService.countByStatus(TeacherStatus.INACTIVE) +
                              teacherService.countByStatus(TeacherStatus.ON_LEAVE))
                .activeTeachers(teacherService.countByStatus(TeacherStatus.ACTIVE))
                .inactiveTeachers(teacherService.countByStatus(TeacherStatus.INACTIVE))
                .onLeaveTeachers(teacherService.countByStatus(TeacherStatus.ON_LEAVE))
                .build();
    }

    /**
     * Build course statistics
     */
    private CourseStats getCourseStats() {
        return CourseStats.builder()
                .totalCourses(courseService.countByStatus(CourseStatus.ACTIVE) +
                             courseService.countByStatus(CourseStatus.INACTIVE) +
                             courseService.countByStatus(CourseStatus.COMPLETED))
                .activeCourses(courseService.countByStatus(CourseStatus.ACTIVE))
                .inactiveCourses(courseService.countByStatus(CourseStatus.INACTIVE))
                .completedCourses(courseService.countByStatus(CourseStatus.COMPLETED))
                .build();
    }

    /**
     * Dashboard statistics DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DashboardStats {
        private UserStats userStats;
        private StudentStats studentStats;
        private TeacherStats teacherStats;
        private CourseStats courseStats;
    }

    /**
     * User statistics DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserStats {
        private long totalUsers;
        private long adminCount;
        private long teacherCount;
        private long studentCount;
    }

    /**
     * Student statistics DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StudentStats {
        private long totalStudents;
        private long activeStudents;
        private long inactiveStudents;
        private long graduatedStudents;
        private long suspendedStudents;
    }

    /**
     * Teacher statistics DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TeacherStats {
        private long totalTeachers;
        private long activeTeachers;
        private long inactiveTeachers;
        private long onLeaveTeachers;
    }

    /**
     * Course statistics DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CourseStats {
        private long totalCourses;
        private long activeCourses;
        private long inactiveCourses;
        private long completedCourses;
    }
}
