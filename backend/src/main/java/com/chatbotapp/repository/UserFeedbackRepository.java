package com.chatbotapp.repository;

import com.chatbotapp.entity.UserFeedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for UserFeedback entity operations
 */
@Repository
public interface UserFeedbackRepository extends JpaRepository<UserFeedback, Long> {
    
    /**
     * Find feedback by session ID
     */
    Optional<UserFeedback> findBySessionId(String sessionId);
    
    /**
     * Find all feedback for a specific user
     */
    List<UserFeedback> findByUserIdOrderBySubmittedAtDesc(String userId);
    
    /**
     * Find feedback by rating range
     */
    List<UserFeedback> findByRatingBetweenOrderBySubmittedAtDesc(Integer minRating, Integer maxRating);
    
    /**
     * Calculate average rating for a specific conversation topic
     */
    @Query("SELECT AVG(f.rating) FROM UserFeedback f WHERE f.conversationTopic = :topic")
    Double getAverageRatingByTopic(@Param("topic") String topic);
    
    /**
     * Find recent feedback (last 7 days)
     */
    @Query("SELECT f FROM UserFeedback f WHERE f.submittedAt >= :since ORDER BY f.submittedAt DESC")
    List<UserFeedback> findRecentFeedback(@Param("since") LocalDateTime since);
    
    /**
     * Count helpful vs not helpful feedback
     */
    Long countByWasHelpful(Boolean wasHelpful);
    
    /**
     * Find feedback with comments (for qualitative analysis)
     */
    @Query("SELECT f FROM UserFeedback f WHERE f.comment IS NOT NULL AND LENGTH(f.comment) > 0 ORDER BY f.submittedAt DESC")
    Page<UserFeedback> findFeedbackWithComments(Pageable pageable);
}
