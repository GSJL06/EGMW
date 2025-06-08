package com.educagestor.service;

import com.educagestor.dto.CourseDto;
import com.educagestor.entity.Course;
import com.educagestor.entity.Institution;
import com.educagestor.entity.Teacher;
import com.educagestor.entity.enums.CourseStatus;
import com.educagestor.repository.CourseRepository;
import com.educagestor.repository.InstitutionRepository;
import com.educagestor.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for Course entity operations
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final InstitutionRepository institutionRepository;
    private final TeacherRepository teacherRepository;

    /**
     * Create a new course
     */
    public CourseDto createCourse(CourseDto courseDto) {
        log.info("Creating new course: {}", courseDto.getCode());
        
        validateCourseData(courseDto);
        
        Institution institution = null;
        if (courseDto.getInstitutionId() != null) {
            institution = institutionRepository.findById(courseDto.getInstitutionId())
                    .orElseThrow(() -> new RuntimeException("Institution not found with ID: " + courseDto.getInstitutionId()));
        }
        
        Teacher teacher = null;
        if (courseDto.getTeacherId() != null) {
            teacher = teacherRepository.findById(courseDto.getTeacherId())
                    .orElseThrow(() -> new RuntimeException("Teacher not found with ID: " + courseDto.getTeacherId()));
        }
        
        Course course = Course.builder()
                .institution(institution)
                .teacher(teacher)
                .code(courseDto.getCode())
                .name(courseDto.getName())
                .description(courseDto.getDescription())
                .credits(courseDto.getCredits())
                .maxStudents(courseDto.getMaxStudents())
                .schedule(courseDto.getSchedule())
                .classroom(courseDto.getClassroom())
                .startDate(courseDto.getStartDate())
                .endDate(courseDto.getEndDate())
                .status(courseDto.getStatus())
                .build();
        
        Course savedCourse = courseRepository.save(course);
        log.info("Course created successfully with ID: {}", savedCourse.getId());
        
        return convertToDto(savedCourse);
    }

    /**
     * Get course by ID
     */
    @Transactional(readOnly = true)
    public Optional<CourseDto> getCourseById(Long id) {
        log.debug("Getting course by ID: {}", id);
        return courseRepository.findById(id).map(this::convertToDto);
    }

    /**
     * Get course by code
     */
    @Transactional(readOnly = true)
    public Optional<CourseDto> getCourseByCode(String code) {
        log.debug("Getting course by code: {}", code);
        return courseRepository.findByCode(code).map(this::convertToDto);
    }

    /**
     * Get all courses with pagination
     */
    @Transactional(readOnly = true)
    public Page<CourseDto> getAllCourses(Pageable pageable) {
        log.debug("Getting all courses with pagination");
        return courseRepository.findAll(pageable).map(this::convertToDto);
    }

    /**
     * Get courses by institution
     */
    @Transactional(readOnly = true)
    public Page<CourseDto> getCoursesByInstitution(Long institutionId, Pageable pageable) {
        log.debug("Getting courses by institution: {}", institutionId);
        return courseRepository.findByInstitutionId(institutionId, pageable).map(this::convertToDto);
    }

    /**
     * Get courses by teacher
     */
    @Transactional(readOnly = true)
    public Page<CourseDto> getCoursesByTeacher(Long teacherId, Pageable pageable) {
        log.debug("Getting courses by teacher: {}", teacherId);
        return courseRepository.findByTeacherId(teacherId, pageable).map(this::convertToDto);
    }

    /**
     * Get courses by status
     */
    @Transactional(readOnly = true)
    public Page<CourseDto> getCoursesByStatus(CourseStatus status, Pageable pageable) {
        log.debug("Getting courses by status: {}", status);
        return courseRepository.findByStatus(status, pageable).map(this::convertToDto);
    }

    /**
     * Get current courses
     */
    @Transactional(readOnly = true)
    public Page<CourseDto> getCurrentCourses(Pageable pageable) {
        log.debug("Getting current courses");
        return courseRepository.findCurrentCourses(pageable).map(this::convertToDto);
    }

    /**
     * Get available courses for enrollment
     */
    @Transactional(readOnly = true)
    public List<CourseDto> getAvailableCoursesForEnrollment() {
        log.debug("Getting available courses for enrollment");
        return courseRepository.findAvailableCoursesForEnrollment()
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    /**
     * Search courses
     */
    @Transactional(readOnly = true)
    public Page<CourseDto> searchCourses(String searchTerm, Pageable pageable) {
        log.debug("Searching courses with term: {}", searchTerm);
        return courseRepository.searchByNameCodeOrDescription(searchTerm, pageable).map(this::convertToDto);
    }

    /**
     * Update course
     */
    public CourseDto updateCourse(Long id, CourseDto courseDto) {
        log.info("Updating course with ID: {}", id);
        
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + id));
        
        if (courseDto.getTeacherId() != null) {
            Teacher teacher = teacherRepository.findById(courseDto.getTeacherId())
                    .orElseThrow(() -> new RuntimeException("Teacher not found with ID: " + courseDto.getTeacherId()));
            course.setTeacher(teacher);
        }
        
        if (courseDto.getName() != null) {
            course.setName(courseDto.getName());
        }
        if (courseDto.getDescription() != null) {
            course.setDescription(courseDto.getDescription());
        }
        if (courseDto.getCredits() != null) {
            course.setCredits(courseDto.getCredits());
        }
        if (courseDto.getMaxStudents() != null) {
            course.setMaxStudents(courseDto.getMaxStudents());
        }
        if (courseDto.getSchedule() != null) {
            course.setSchedule(courseDto.getSchedule());
        }
        if (courseDto.getClassroom() != null) {
            course.setClassroom(courseDto.getClassroom());
        }
        if (courseDto.getStartDate() != null) {
            course.setStartDate(courseDto.getStartDate());
        }
        if (courseDto.getEndDate() != null) {
            course.setEndDate(courseDto.getEndDate());
        }
        if (courseDto.getStatus() != null) {
            course.setStatus(courseDto.getStatus());
        }
        
        Course updatedCourse = courseRepository.save(course);
        log.info("Course updated successfully: {}", updatedCourse.getCode());
        
        return convertToDto(updatedCourse);
    }

    /**
     * Delete course
     */
    public void deleteCourse(Long id) {
        log.info("Deleting course with ID: {}", id);
        
        if (!courseRepository.existsById(id)) {
            throw new RuntimeException("Course not found with ID: " + id);
        }
        
        courseRepository.deleteById(id);
        log.info("Course deleted successfully with ID: {}", id);
    }

    /**
     * Check if course code exists
     */
    @Transactional(readOnly = true)
    public boolean existsByCode(String code) {
        return courseRepository.existsByCode(code);
    }

    /**
     * Count courses by institution
     */
    @Transactional(readOnly = true)
    public long countByInstitution(Long institutionId) {
        return courseRepository.countByInstitutionId(institutionId);
    }

    /**
     * Count courses by teacher
     */
    @Transactional(readOnly = true)
    public long countByTeacher(Long teacherId) {
        return courseRepository.countByTeacherId(teacherId);
    }

    /**
     * Count courses by status
     */
    @Transactional(readOnly = true)
    public long countByStatus(CourseStatus status) {
        return courseRepository.countByStatus(status);
    }

    /**
     * Convert Course entity to CourseDto
     */
    private CourseDto convertToDto(Course course) {
        CourseDto dto = CourseDto.builder()
                .id(course.getId())
                .code(course.getCode())
                .name(course.getName())
                .description(course.getDescription())
                .credits(course.getCredits())
                .maxStudents(course.getMaxStudents())
                .schedule(course.getSchedule())
                .classroom(course.getClassroom())
                .startDate(course.getStartDate())
                .endDate(course.getEndDate())
                .status(course.getStatus())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
        
        if (course.getInstitution() != null) {
            dto.setInstitutionId(course.getInstitution().getId());
            dto.setInstitutionName(course.getInstitution().getName());
        }
        
        if (course.getTeacher() != null) {
            dto.setTeacherId(course.getTeacher().getId());
            dto.setTeacherName(course.getTeacher().getFullName());
        }
        
        dto.setFullName(course.getFullName());
        dto.setEnrolledStudentCount(course.getEnrolledStudentCount());
        dto.setAvailableSpots(course.getAvailableSpots());
        dto.setActive(course.isActive());
        dto.setAtCapacity(course.isAtCapacity());
        dto.setInSession(course.isInSession());
        dto.setDurationInDays(course.getDurationInDays());
        
        return dto;
    }

    /**
     * Validate course data
     */
    private void validateCourseData(CourseDto courseDto) {
        if (existsByCode(courseDto.getCode())) {
            throw new RuntimeException("Course code already exists: " + courseDto.getCode());
        }
    }
}
