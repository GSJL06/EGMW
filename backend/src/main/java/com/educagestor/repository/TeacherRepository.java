package com.educagestor.repository;

import com.educagestor.entity.Teacher;
import com.educagestor.entity.enums.TeacherStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Teacher entity
 */
@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    Optional<Teacher> findByEmployeeCode(String employeeCode);
    Optional<Teacher> findByUserId(Long userId);
    boolean existsByEmployeeCode(String employeeCode);
    
    List<Teacher> findByInstitutionId(Long institutionId);
    Page<Teacher> findByInstitutionId(Long institutionId, Pageable pageable);
    
    List<Teacher> findByStatus(TeacherStatus status);
    Page<Teacher> findByStatus(TeacherStatus status, Pageable pageable);
    
    List<Teacher> findByInstitutionIdAndStatus(Long institutionId, TeacherStatus status);
    Page<Teacher> findByInstitutionIdAndStatus(Long institutionId, TeacherStatus status, Pageable pageable);

    @Query("SELECT t FROM Teacher t WHERE " +
           "LOWER(t.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.employeeCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.specialization) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Teacher> searchByNameCodeOrSpecialization(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT t FROM Teacher t WHERE t.institution.id = :institutionId AND " +
           "(LOWER(t.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.employeeCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.specialization) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Teacher> searchInInstitution(@Param("institutionId") Long institutionId, 
                                     @Param("searchTerm") String searchTerm, 
                                     Pageable pageable);

    List<Teacher> findBySpecializationContainingIgnoreCase(String specialization);
    
    List<Teacher> findByHireDateBetween(LocalDate startDate, LocalDate endDate);
    
    long countByInstitutionId(Long institutionId);
    long countByStatus(TeacherStatus status);
    long countByInstitutionIdAndStatus(Long institutionId, TeacherStatus status);

    @Query("SELECT t FROM Teacher t WHERE t.hireDate >= :startDate")
    List<Teacher> findRecentlyHiredTeachers(@Param("startDate") LocalDate startDate);

    @Query("SELECT t FROM Teacher t LEFT JOIN t.courses c GROUP BY t ORDER BY COUNT(c) DESC")
    Page<Teacher> findTeachersOrderedByCourseCount(Pageable pageable);
}
