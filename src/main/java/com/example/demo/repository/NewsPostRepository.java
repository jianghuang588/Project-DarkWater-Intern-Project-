package com.example.demo.repository;


import com.example.demo.Enum.PostType;
import com.example.demo.Enum.PostStatus;
import com.example.demo.user.NewsPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NewsPostRepository extends JpaRepository<NewsPost, Long> {

    // Get published posts by type
    Page<NewsPost> findByTypeAndStatusOrderByPublishedDateDesc(PostType type, PostStatus status, Pageable pageable);

    // Get recent news for carousel (5 most recent)
    @Query("SELECT n FROM NewsPost n WHERE n.type = 'NEWS' AND n.status = 'PUBLISHED' ORDER BY n.publishedDate DESC")
    List<NewsPost> findTop5RecentNews(Pageable pageable);

    // Get most recent major patch note
    @Query("SELECT n FROM NewsPost n WHERE n.type = 'PATCH_NOTES' AND n.status = 'PUBLISHED' AND n.isMajorRelease = true ORDER BY n.publishedDate DESC")
    Optional<NewsPost> findLatestMajorPatchNote();

    // Get scheduled posts that need to be published
    @Query("SELECT n FROM NewsPost n WHERE n.status = 'SCHEDULED' AND n.scheduledDate <= :now")
    List<NewsPost> findScheduledPostsReadyToPublish(LocalDateTime now);

    // Get all posts by status (for admin management)
    Page<NewsPost> findByStatusOrderByCreatedAtDesc(PostStatus status, Pageable pageable);

    // Search posts by title or content
    @Query("SELECT n FROM NewsPost n WHERE (LOWER(n.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(n.content) LIKE LOWER(CONCAT('%', :query, '%'))) AND n.status = 'PUBLISHED'")
    Page<NewsPost> searchPublishedPosts(String query, Pageable pageable);
}