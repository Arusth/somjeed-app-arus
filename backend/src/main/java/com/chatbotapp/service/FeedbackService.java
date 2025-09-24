package com.chatbotapp.service;

import com.chatbotapp.dto.FeedbackRequest;
import com.chatbotapp.entity.UserFeedback;
import com.chatbotapp.repository.UserFeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for handling user feedback collection and storage
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FeedbackService {
    
    private final UserFeedbackRepository feedbackRepository;
    private final ConversationClosureService conversationClosureService;
    
    /**
     * Submit user feedback and store in database
     */
    public UserFeedback submitFeedback(FeedbackRequest request) {
        log.info("Submitting feedback for session: {}, rating: {}", 
                request.getSessionId(), request.getRating());
        
        // Check if feedback already exists for this session
        Optional<UserFeedback> existingFeedback = feedbackRepository.findBySessionId(request.getSessionId());
        if (existingFeedback.isPresent()) {
            log.warn("Feedback already exists for session: {}", request.getSessionId());
            throw new IllegalStateException("Feedback has already been submitted for this session");
        }
        
        // Create feedback entity
        UserFeedback feedback = UserFeedback.builder()
                .sessionId(request.getSessionId())
                .userId(request.getUserId())
                .rating(request.getRating())
                .comment(request.getComment())
                .conversationTopic(request.getConversationTopic())
                .conversationStartedAt(request.getConversationStartedAt())
                .conversationEndedAt(request.getConversationEndedAt())
                .messageCount(request.getMessageCount())
                .deviceType(request.getDeviceType())
                .wasHelpful(request.getRating() >= 4) // 4-5 stars considered helpful
                .build();
        
        // Save feedback
        UserFeedback savedFeedback = feedbackRepository.save(feedback);
        
        // Mark conversation as completed
        conversationClosureService.markConversationCompleted(request.getSessionId());
        
        log.info("Feedback saved successfully with ID: {}", savedFeedback.getId());
        return savedFeedback;
    }
    
    /**
     * Get feedback statistics for analysis
     */
    public FeedbackStats getFeedbackStats() {
        List<UserFeedback> allFeedback = feedbackRepository.findAll();
        
        if (allFeedback.isEmpty()) {
            return FeedbackStats.empty();
        }
        
        double averageRating = allFeedback.stream()
                .mapToInt(UserFeedback::getRating)
                .average()
                .orElse(0.0);
        
        long helpfulCount = feedbackRepository.countByWasHelpful(true);
        long notHelpfulCount = feedbackRepository.countByWasHelpful(false);
        
        return FeedbackStats.builder()
                .totalFeedback(allFeedback.size())
                .averageRating(averageRating)
                .helpfulCount(helpfulCount)
                .notHelpfulCount(notHelpfulCount)
                .satisfactionRate((double) helpfulCount / allFeedback.size() * 100)
                .build();
    }
    
    /**
     * Get recent feedback for monitoring
     */
    public List<UserFeedback> getRecentFeedback(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return feedbackRepository.findRecentFeedback(since);
    }
    
    /**
     * Get feedback by user for history
     */
    public List<UserFeedback> getUserFeedbackHistory(String userId) {
        return feedbackRepository.findByUserIdOrderBySubmittedAtDesc(userId);
    }
    
    /**
     * Generate feedback response message based on rating
     */
    public String generateFeedbackResponseMessage(int rating) {
        return switch (rating) {
            case 5 -> "Thank you so much! I'm thrilled I could help you today. 🌟";
            case 4 -> "Thank you for the positive feedback! I'm glad I could assist you. 😊";
            case 3 -> "Thank you for your feedback. I'll keep working to improve our service.";
            case 2 -> "Thank you for your feedback. I'm sorry the experience wasn't better. We'll work on improving.";
            case 1 -> "I'm sorry to hear about your experience. Your feedback helps us improve our service.";
            default -> "Thank you for your feedback!";
        };
    }
    
    /**
     * Inner class for feedback statistics
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class FeedbackStats {
        private int totalFeedback;
        private double averageRating;
        private long helpfulCount;
        private long notHelpfulCount;
        private double satisfactionRate;
        
        public static FeedbackStats empty() {
            return FeedbackStats.builder()
                    .totalFeedback(0)
                    .averageRating(0.0)
                    .helpfulCount(0)
                    .notHelpfulCount(0)
                    .satisfactionRate(0.0)
                    .build();
        }
    }
}
