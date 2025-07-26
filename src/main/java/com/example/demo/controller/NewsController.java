package com.example.demo.controller;

import com.example.demo.Enum.PostType;
import com.example.demo.Enum.PostStatus;
import com.example.demo.dto.NewsPostDto;
import com.example.demo.service.NewsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for news posts and patch notes management
 * Handles backend operations for PR team to create, schedule, and publish content
 * No author information is stored per requirements
 */
@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Autowired
    private NewsService newsService;


    @GetMapping("/debug-auth")
    public ResponseEntity<String> debugAuth(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String sessionId = request.getSession(false) != null ? request.getSession().getId() : "No session";

        StringBuilder debug = new StringBuilder();
        debug.append("Session ID: ").append(sessionId).append("\n");
        debug.append("Authentication: ").append(auth != null ? auth.toString() : "null").append("\n");
        debug.append("Principal: ").append(auth != null ? auth.getName() : "null").append("\n");
        debug.append("Authorities: ").append(auth != null ? auth.getAuthorities() : "null").append("\n");
        debug.append("Is Authenticated: ").append(auth != null ? auth.isAuthenticated() : "false").append("\n");

        return ResponseEntity.ok(debug.toString());
    }


    /**
     * Create new post (draft by default)
     * @param dto - post details
     * @return Created post
     */
    @PostMapping
    public ResponseEntity<NewsPostDto> createPost(@Valid @RequestBody NewsPostDto dto) {
        NewsPostDto createdPost = newsService.createPost(dto);
        return ResponseEntity.ok(createdPost);
    }

    /**
     * Update existing post
     * @param id - post ID
     * @param dto - updated post details
     * @return Updated post
     */
    @PutMapping("/{id}")
    public ResponseEntity<NewsPostDto> updatePost(@PathVariable Long id, @Valid @RequestBody NewsPostDto dto) {
        NewsPostDto updatedPost = newsService.updatePost(id, dto);
        return ResponseEntity.ok(updatedPost);
    }

    /**
     * Schedule post for future publication
     * @param id - post ID
     * @param scheduledDate - when to publish
     * @return Scheduled post
     */
    @PostMapping("/{id}/schedule")
    public ResponseEntity<NewsPostDto> schedulePost(@PathVariable Long id, @RequestParam LocalDateTime scheduledDate) {
        NewsPostDto scheduledPost = newsService.schedulePost(id, scheduledDate);
        return ResponseEntity.ok(scheduledPost);
    }

    /**
     * Publish post immediately
     * @param id - post ID
     * @return Published post
     */
    @PostMapping("/{id}/publish")
    public ResponseEntity<NewsPostDto> publishPost(@PathVariable Long id) {
        NewsPostDto publishedPost = newsService.publishPost(id);
        return ResponseEntity.ok(publishedPost);
    }

    /**
     * Get recent news for carousel (public endpoint)
     * @return List of 5 most recent news posts
     */
    @GetMapping("/carousel")
    public ResponseEntity<List<NewsPostDto>> getCarouselNews() {
        List<NewsPostDto> recentNews = newsService.getRecentNewsForCarousel();
        return ResponseEntity.ok(recentNews);
    }

    /**
     * Get latest major patch note (public endpoint)
     * @return Most recent major patch note
     */
    @GetMapping("/latest-patch")
    public ResponseEntity<NewsPostDto> getLatestPatchNote() {
        NewsPostDto latestPatch = newsService.getLatestMajorPatchNote();
        return ResponseEntity.ok(latestPatch);
    }

    /**
     * Get all posts by status (admin endpoint)
     * @param status - post status to filter by
     * @param pageable - pagination parameters
     * @return Page of posts
     */
    @GetMapping("/admin/status/{status}")
    public ResponseEntity<Page<NewsPostDto>> getPostsByStatus(@PathVariable PostStatus status, Pageable pageable) {
        Page<NewsPostDto> posts = newsService.getPostsByStatus(status, pageable);
        return ResponseEntity.ok(posts);
    }

    /**
     * Get published posts by type (public endpoint)
     * @param type - NEWS or PATCH_NOTES
     * @param pageable - pagination parameters
     * @return Page of published posts
     */
    @GetMapping("/published/{type}")
    public ResponseEntity<Page<NewsPostDto>> getPublishedPostsByType(@PathVariable PostType type, Pageable pageable) {
        Page<NewsPostDto> posts = newsService.getPublishedPostsByType(type, pageable);
        return ResponseEntity.ok(posts);
    }

    /**
     * Get single post by ID
     * @param id - post ID
     * @return Post details
     */
    @GetMapping("/{id}")
    public ResponseEntity<NewsPostDto> getPost(@PathVariable Long id) {
        NewsPostDto post = newsService.getPost(id);
        return ResponseEntity.ok(post);
    }

    /**
     * Delete post (admin only)
     * @param id - post ID
     * @return No content response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        newsService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Trigger scheduled post publishing (internal endpoint)
     * This would typically be called by a scheduler
     * @return Success message
     */
    @PostMapping("/admin/publish-scheduled")
    public ResponseEntity<String> publishScheduledPosts() {
        newsService.publishScheduledPosts();
        return ResponseEntity.ok("Scheduled posts processed");
    }
}

