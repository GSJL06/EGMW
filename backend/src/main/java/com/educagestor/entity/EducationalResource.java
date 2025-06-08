package com.educagestor.entity;

import com.educagestor.entity.enums.ResourceType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * EducationalResource entity representing learning materials and resources
 * 
 * This entity stores information about educational resources such as documents,
 * videos, links, and other materials associated with courses and teachers.
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
@Entity
@Table(name = "educational_resources", indexes = {
    @Index(name = "idx_educational_resources_course_id", columnList = "course_id"),
    @Index(name = "idx_educational_resources_teacher_id", columnList = "teacher_id"),
    @Index(name = "idx_educational_resources_type", columnList = "resource_type"),
    @Index(name = "idx_educational_resources_public", columnList = "is_public")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationalResource extends BaseEntity {

    /**
     * Course this resource belongs to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    private Course course;

    /**
     * Teacher who created this resource
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", referencedColumnName = "id")
    private Teacher teacher;

    /**
     * Title of the resource
     */
    @NotBlank(message = "Resource title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * Description of the resource
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Type of the resource
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false, length = 50)
    @NotNull(message = "Resource type is required")
    private ResourceType resourceType;

    /**
     * File path for uploaded resources
     */
    @Size(max = 500, message = "File path must not exceed 500 characters")
    @Column(name = "file_path", length = 500)
    private String filePath;

    /**
     * Size of the file in bytes
     */
    @Column(name = "file_size")
    private Long fileSize;

    /**
     * MIME type of the file
     */
    @Size(max = 100, message = "MIME type must not exceed 100 characters")
    @Column(name = "mime_type", length = 100)
    private String mimeType;

    /**
     * Whether the resource is publicly accessible
     */
    @Column(name = "is_public", nullable = false)
    @Builder.Default
    private Boolean isPublic = false;

    /**
     * Check if this resource requires file upload
     * 
     * @return true if resource type requires file upload
     */
    public boolean requiresFileUpload() {
        return resourceType.requiresFileUpload();
    }

    /**
     * Check if this resource is multimedia
     * 
     * @return true if resource type is multimedia
     */
    public boolean isMultimedia() {
        return resourceType.isMultimedia();
    }

    /**
     * Check if this resource is a document
     * 
     * @return true if resource type is document
     */
    public boolean isDocument() {
        return resourceType.isDocument();
    }

    /**
     * Check if this resource is a link
     * 
     * @return true if resource type is link
     */
    public boolean isLink() {
        return resourceType == ResourceType.LINK;
    }

    /**
     * Get the file extension from the file path
     * 
     * @return file extension or empty string if not available
     */
    public String getFileExtension() {
        if (filePath == null || !filePath.contains(".")) {
            return "";
        }
        return filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * Get the filename from the file path
     * 
     * @return filename or empty string if not available
     */
    public String getFileName() {
        if (filePath == null) {
            return "";
        }
        return filePath.substring(filePath.lastIndexOf("/") + 1);
    }

    /**
     * Get formatted file size for display
     * 
     * @return formatted file size (e.g., "1.5 MB")
     */
    public String getFormattedFileSize() {
        if (fileSize == null || fileSize == 0) {
            return "Unknown";
        }

        double size = fileSize.doubleValue();
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;

        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        return String.format("%.1f %s", size, units[unitIndex]);
    }

    /**
     * Get the course name for display
     * 
     * @return course name or "No Course" if not associated
     */
    public String getCourseName() {
        return course != null ? course.getName() : "No Course";
    }

    /**
     * Get the teacher name for display
     * 
     * @return teacher name or "Unknown" if not available
     */
    public String getTeacherName() {
        return teacher != null ? teacher.getFullName() : "Unknown";
    }

    /**
     * Get the icon class for UI representation
     * 
     * @return CSS icon class name
     */
    public String getIconClass() {
        return resourceType.getIconClass();
    }

    /**
     * Check if the resource can be downloaded
     * 
     * @return true if resource has a file path
     */
    public boolean canBeDownloaded() {
        return filePath != null && !filePath.trim().isEmpty();
    }

    /**
     * Check if the resource can be previewed
     * 
     * @return true if resource can be previewed (images, documents)
     */
    public boolean canBePreviewed() {
        return resourceType == ResourceType.IMAGE || 
               resourceType == ResourceType.DOCUMENT ||
               resourceType == ResourceType.LINK;
    }

    /**
     * Get the URL for the resource
     * For links, returns the file path as URL
     * For files, returns the download URL
     * 
     * @return resource URL
     */
    public String getResourceUrl() {
        if (resourceType == ResourceType.LINK) {
            return filePath;
        }
        return filePath != null ? "/api/resources/download/" + getId() : null;
    }

    /**
     * Check if the resource is accessible to a specific user
     * 
     * @param userId the user ID to check access for
     * @return true if resource is accessible
     */
    public boolean isAccessibleToUser(Long userId) {
        if (isPublic) {
            return true;
        }
        
        // Resource is accessible if user is the teacher who created it
        if (teacher != null && teacher.getUser() != null) {
            return teacher.getUser().getId().equals(userId);
        }
        
        return false;
    }

    /**
     * Validate the resource data
     * 
     * @return true if resource data is valid
     */
    public boolean isValid() {
        if (resourceType == ResourceType.LINK) {
            return filePath != null && (filePath.startsWith("http://") || filePath.startsWith("https://"));
        }
        
        if (resourceType.requiresFileUpload()) {
            return filePath != null && !filePath.trim().isEmpty();
        }
        
        return true;
    }
}
