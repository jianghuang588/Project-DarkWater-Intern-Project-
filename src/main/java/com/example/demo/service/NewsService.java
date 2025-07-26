package com.example.demo.service;


import com.example.demo.Enum.PostType;
import com.example.demo.Enum.PostStatus;
import com.example.demo.dto.NewsPostDto;
import com.example.demo.repository.NewsPostRepository;
import com.example.demo.user.NewsPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewsService {

    @Autowired
    private NewsPostRepository newsPostRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm a");

    // Create new post (draft by default)
    @Transactional
    public NewsPostDto createPost(NewsPostDto dto) {
        NewsPost post = new NewsPost();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setExcerpt(dto.getExcerpt());
        post.setType(dto.getType());
        post.setStatus(PostStatus.DRAFT); // Always start as draft
        post.setVersionNumber(dto.getVersionNumber());
        post.setMajorRelease(dto.isMajorRelease());
        post.setScheduledDate(dto.getScheduledDate());
        post.setFeaturedImage(dto.getFeaturedImage());
        post.setTags(dto.getTags());

        NewsPost savedPost = newsPostRepository.save(post);
        return convertToDto(savedPost);
    }

    // Update existing post
    @Transactional
    public NewsPostDto updatePost(Long id, NewsPostDto dto) {
        NewsPost post = newsPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setExcerpt(dto.getExcerpt());
        post.setVersionNumber(dto.getVersionNumber());
        post.setMajorRelease(dto.isMajorRelease());
        post.setScheduledDate(dto.getScheduledDate());
        post.setFeaturedImage(dto.getFeaturedImage());
        post.setTags(dto.getTags());

        NewsPost updatedPost = newsPostRepository.save(post);
        return convertToDto(updatedPost);
    }

    // Schedule post for publication
    @Transactional
    public NewsPostDto schedulePost(Long id, LocalDateTime scheduledDate) {
        NewsPost post = newsPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setScheduledDate(scheduledDate);
        post.setStatus(PostStatus.SCHEDULED);

        NewsPost updatedPost = newsPostRepository.save(post);
        return convertToDto(updatedPost);
    }

    // Publish post immediately
    @Transactional
    public NewsPostDto publishPost(Long id) {
        NewsPost post = newsPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setStatus(PostStatus.PUBLISHED);
        post.setPublishedDate(LocalDateTime.now());

        NewsPost updatedPost = newsPostRepository.save(post);
        return convertToDto(updatedPost);
    }

    // Get recent news for carousel (5 most recent)
    public List<NewsPostDto> getRecentNewsForCarousel() {
        Pageable pageable = PageRequest.of(0, 5);
        List<NewsPost> posts = newsPostRepository.findTop5RecentNews(pageable);
        return posts.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // Get latest major patch note
    public NewsPostDto getLatestMajorPatchNote() {
        return newsPostRepository.findLatestMajorPatchNote()
                .map(this::convertToDto)
                .orElse(null);
    }

    // Get all posts by status (for admin)
    public Page<NewsPostDto> getPostsByStatus(PostStatus status, Pageable pageable) {
        return newsPostRepository.findByStatusOrderByCreatedAtDesc(status, pageable)
                .map(this::convertToDto);
    }

    // Get published posts by type
    public Page<NewsPostDto> getPublishedPostsByType(PostType type, Pageable pageable) {
        return newsPostRepository.findByTypeAndStatusOrderByPublishedDateDesc(type, PostStatus.PUBLISHED, pageable)
                .map(this::convertToDto);
    }

    // Process scheduled posts (called by scheduler)
    @Transactional
    public void publishScheduledPosts() {
        List<NewsPost> scheduledPosts = newsPostRepository.findScheduledPostsReadyToPublish(LocalDateTime.now());

        for (NewsPost post : scheduledPosts) {
            post.setStatus(PostStatus.PUBLISHED);
            post.setPublishedDate(LocalDateTime.now());
            newsPostRepository.save(post);
        }
    }

    // Delete post
    @Transactional
    public void deletePost(Long id) {
        newsPostRepository.deleteById(id);
    }

    // Get single post
    public NewsPostDto getPost(Long id) {
        NewsPost post = newsPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return convertToDto(post);
    }

    // Convert entity to DTO
    private NewsPostDto convertToDto(NewsPost post) {
        NewsPostDto dto = new NewsPostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setExcerpt(post.getExcerpt());
        dto.setType(post.getType());
        dto.setStatus(post.getStatus());
        dto.setVersionNumber(post.getVersionNumber());
        dto.setMajorRelease(post.isMajorRelease());
        dto.setScheduledDate(post.getScheduledDate());
        dto.setPublishedDate(post.getPublishedDate());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setFeaturedImage(post.getFeaturedImage());
        dto.setTags(post.getTags());

        // Format dates for display
        if (post.getPublishedDate() != null) {
            dto.setFormattedPublishedDate(post.getPublishedDate().format(formatter));
        }
        if (post.getScheduledDate() != null) {
            dto.setFormattedScheduledDate(post.getScheduledDate().format(formatter));
        }

        return dto;
    }
}
