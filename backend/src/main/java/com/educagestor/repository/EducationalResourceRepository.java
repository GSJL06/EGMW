package com.educagestor.repository;

import com.educagestor.entity.EducationalResource;
import com.educagestor.entity.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for EducationalResource entity
 */
@Repository
public interface EducationalResourceRepository extends JpaRepository<EducationalResource, Long> {

    List<EducationalResource> findByCourseId(Long courseId);
    Page<EducationalResource> findByCourseId(Long courseId, Pageable pageable);
    
    List<EducationalResource> findByTeacherId(Long teacherId);
    Page<EducationalResource> findByTeacherId(Long teacherId, Pageable pageable);
    
    List<EducationalResource> findByResourceType(ResourceType resourceType);
    Page<EducationalResource> findByResourceType(ResourceType resourceType, Pageable pageable);
    
    List<EducationalResource> findByIsPublicTrue();
    Page<EducationalResource> findByIsPublicTrue(Pageable pageable);
    
    List<EducationalResource> findByIsPublicFalse();
    Page<EducationalResource> findByIsPublicFalse(Pageable pageable);
    
    List<EducationalResource> findByCourseIdAndResourceType(Long courseId, ResourceType resourceType);
    List<EducationalResource> findByTeacherIdAndResourceType(Long teacherId, ResourceType resourceType);
    
    List<EducationalResource> findByCourseIdAndIsPublicTrue(Long courseId);
    List<EducationalResource> findByTeacherIdAndIsPublicTrue(Long teacherId);

    @Query("SELECT r FROM EducationalResource r WHERE " +
           "LOWER(r.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<EducationalResource> searchByTitleOrDescription(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT r FROM EducationalResource r WHERE r.course.id = :courseId AND " +
           "(LOWER(r.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<EducationalResource> searchInCourse(@Param("courseId") Long courseId, 
                                           @Param("searchTerm") String searchTerm, 
                                           Pageable pageable);

    @Query("SELECT r FROM EducationalResource r WHERE r.teacher.id = :teacherId AND " +
           "(LOWER(r.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<EducationalResource> searchByTeacher(@Param("teacherId") Long teacherId, 
                                            @Param("searchTerm") String searchTerm, 
                                            Pageable pageable);

    long countByCourseId(Long courseId);
    long countByTeacherId(Long teacherId);
    long countByResourceType(ResourceType resourceType);
    long countByIsPublicTrue();
    long countByIsPublicFalse();
    long countByCourseIdAndResourceType(Long courseId, ResourceType resourceType);

    @Query("SELECT r FROM EducationalResource r WHERE r.createdAt >= :startDate")
    List<EducationalResource> findRecentResources(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT r FROM EducationalResource r WHERE r.course.institution.id = :institutionId")
    List<EducationalResource> findByInstitutionId(@Param("institutionId") Long institutionId);
    
    @Query("SELECT r FROM EducationalResource r WHERE r.course.institution.id = :institutionId")
    Page<EducationalResource> findByInstitutionId(@Param("institutionId") Long institutionId, Pageable pageable);

    @Query("SELECT r FROM EducationalResource r WHERE r.isPublic = true AND " +
           "(LOWER(r.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<EducationalResource> searchPublicResources(@Param("searchTerm") String searchTerm, Pageable pageable);

    List<EducationalResource> findByTitleContainingIgnoreCase(String title);
    
    @Query("SELECT r FROM EducationalResource r ORDER BY r.createdAt DESC")
    Page<EducationalResource> findAllOrderByCreatedAtDesc(Pageable pageable);
    
    @Query("SELECT r FROM EducationalResource r WHERE r.course.id = :courseId ORDER BY r.createdAt DESC")
    Page<EducationalResource> findByCourseIdOrderByCreatedAtDesc(@Param("courseId") Long courseId, Pageable pageable);
}
