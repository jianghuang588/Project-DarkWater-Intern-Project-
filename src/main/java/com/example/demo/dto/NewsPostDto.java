package com.example.demo.dto;


import com.example.demo.Enum.PostType;
import com.example.demo.Enum.PostStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for news posts and patch notes
 * Used for creating, updating, and displaying posts
 * No author information included per requirements
 */
public class NewsPostDto {

    private Long id;

    /**
     * Post title - required for all posts
     */
    @NotBlank(message = "Title is required")
    private String title;

    /**
     * Full post content - required
     */
    @NotBlank(message = "Content is required")
    private String content;

    /**
     * Short excerpt for carousel display
     */
    private String excerpt;

    /**
     * Type of post (NEWS or PATCH_NOTES)
     */
    @NotNull(message = "Post type is required")
    private PostType type;

    /**
     * Publication status for workflow management
     */
    private PostStatus status;

    /**
     * Version number for patch notes (e.g., "1.2.3")
     */
    private String versionNumber;

    /**
     * Whether this is a major release (for patch notes)
     */
    private boolean isMajorRelease;

    /**
     * When to publish this post (for scheduling)
     */
    private LocalDateTime scheduledDate;

    /**
     * When this was actually published
     */
    private LocalDateTime publishedDate;

    /**
     * Post creation timestamp
     */
    private LocalDateTime createdAt;

    /**
     * Last update timestamp
     */
    private LocalDateTime updatedAt;

    /**
     * URL to featured image
     */
    private String featuredImage;

    /**
     * Comma-separated tags for categorization
     */
    private String tags;

    /**
     * Formatted dates for display
     */
    private String formattedPublishedDate;
    private String formattedScheduledDate;

    // Constructors
    public NewsPostDto() {}

    public NewsPostDto(String title, String content, PostType type) {
        this.title = title;
        this.content = content;
        this.type = type;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getExcerpt() { return excerpt; }
    public void setExcerpt(String excerpt) { this.excerpt = excerpt; }

    public PostType getType() { return type; }
    public void setType(PostType type) { this.type = type; }

    public PostStatus getStatus() { return status; }
    public void setStatus(PostStatus status) { this.status = status; }

    public String getVersionNumber() { return versionNumber; }
    public void setVersionNumber(String versionNumber) { this.versionNumber = versionNumber; }

    public boolean isMajorRelease() { return isMajorRelease; }
    public void setMajorRelease(boolean majorRelease) { isMajorRelease = majorRelease; }

    public LocalDateTime getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDateTime scheduledDate) { this.scheduledDate = scheduledDate; }

    public LocalDateTime getPublishedDate() { return publishedDate; }
    public void setPublishedDate(LocalDateTime publishedDate) { this.publishedDate = publishedDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getFeaturedImage() { return featuredImage; }
    public void setFeaturedImage(String featuredImage) { this.featuredImage = featuredImage; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getFormattedPublishedDate() { return formattedPublishedDate; }
    public void setFormattedPublishedDate(String formattedPublishedDate) { this.formattedPublishedDate = formattedPublishedDate; }

    public String getFormattedScheduledDate() { return formattedScheduledDate; }
    public void setFormattedScheduledDate(String formattedScheduledDate) { this.formattedScheduledDate = formattedScheduledDate; }
}