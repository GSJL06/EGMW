package com.educagestor.entity.enums;

/**
 * Enumeration for educational resource types
 * 
 * Defines the different types of educational resources
 * that can be uploaded and shared in the system.
 * 
 * @author EducaGestor360 Team
 * @version 1.0.0
 */
public enum ResourceType {
    /**
     * Document files (PDF, DOC, DOCX, etc.)
     */
    DOCUMENT("Document"),
    
    /**
     * Video files (MP4, AVI, MOV, etc.)
     */
    VIDEO("Video"),
    
    /**
     * Audio files (MP3, WAV, etc.)
     */
    AUDIO("Audio"),
    
    /**
     * Web links and URLs
     */
    LINK("Link"),
    
    /**
     * Image files (JPG, PNG, GIF, etc.)
     */
    IMAGE("Image"),
    
    /**
     * Other types of resources
     */
    OTHER("Other");

    private final String displayName;

    /**
     * Constructor for ResourceType enum
     * 
     * @param displayName human-readable name for the resource type
     */
    ResourceType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Get the display name for the resource type
     * 
     * @return display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get ResourceType from string value
     * 
     * @param value string representation of the resource type
     * @return ResourceType enum value
     * @throws IllegalArgumentException if value is not valid
     */
    public static ResourceType fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Resource type value cannot be null");
        }
        
        try {
            return ResourceType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid resource type: " + value);
        }
    }

    /**
     * Check if this resource type requires file upload
     * 
     * @return true if resource type requires file upload
     */
    public boolean requiresFileUpload() {
        return this != LINK;
    }

    /**
     * Check if this resource type is multimedia
     * 
     * @return true if resource type is VIDEO, AUDIO, or IMAGE
     */
    public boolean isMultimedia() {
        return this == VIDEO || this == AUDIO || this == IMAGE;
    }

    /**
     * Check if this resource type is a document
     * 
     * @return true if resource type is DOCUMENT
     */
    public boolean isDocument() {
        return this == DOCUMENT;
    }

    /**
     * Get the icon class for UI representation
     * 
     * @return CSS icon class name
     */
    public String getIconClass() {
        return switch (this) {
            case DOCUMENT -> "file-text";
            case VIDEO -> "video";
            case AUDIO -> "music";
            case LINK -> "link";
            case IMAGE -> "image";
            case OTHER -> "file";
        };
    }
}
