package com.example.demo.user;

import com.example.demo.Enum.PostType;
import com.example.demo.Enum.PostStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "news_posts")
public class NewsPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Post details
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "excerpt")
    private String excerpt; // Short summary for carousel

    // Post type (NEWS or PATCH_NOTES)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostType type;

    // Status for drafts/scheduling
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostStatus status = PostStatus.DRAFT;

    // Version info for patch notes
    @Column(name = "version_number")
    private String versionNumber; // e.g., "1.2.3"

    @Column(name = "is_major_release")
    private boolean isMajorRelease = false;

    // Scheduling - NO AUTHOR INFO per requirements
    @Column(name = "scheduled_date")
    private LocalDateTime scheduledDate;

    @Column(name = "published_date")
    private LocalDateTime publishedDate;

    // Timestamps
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Featured image URL
    @Column(name = "featured_image")
    private String featuredImage;

    // Tags for categorization
    @Column(name = "tags")
    private String tags; // Comma-separated

    // Auto-set timestamps
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public NewsPost() {}

    public NewsPost(String title, String content, PostType type) {
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
}