package com.chatbotapp.controller;

import com.chatbotapp.dto.ConversationClosureRequest;
import com.chatbotapp.dto.ConversationClosureResponse;
import com.chatbotapp.dto.FeedbackRequest;
import com.chatbotapp.entity.UserFeedback;
import com.chatbotapp.service.ConversationClosureService;
import com.chatbotapp.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for handling feedback and conversation closure
 */
@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class FeedbackController {
    
    private final FeedbackService feedbackService;
    private final ConversationClosureService conversationClosureService;
    
    /**
     * Submit user feedback
     */
    @PostMapping("/submit")
    public ResponseEntity<FeedbackSubmissionResponse> submitFeedback(@Valid @RequestBody FeedbackRequest request) {
        log.info("Received feedback submission for session: {}", request.getSessionId());
        
        try {
            UserFeedback savedFeedback = feedbackService.submitFeedback(request);
            String responseMessage = feedbackService.generateFeedbackResponseMessage(request.getRating());
            
            FeedbackSubmissionResponse response = FeedbackSubmissionResponse.builder()
                    .success(true)
                    .message(responseMessage)
                    .feedbackId(savedFeedback.getId())
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalStateException e) {
            log.warn("Duplicate feedback submission attempt: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(FeedbackSubmissionResponse.builder()
                            .success(false)
                            .message("Feedback has already been submitted for this session")
                            .build());
        } catch (Exception e) {
            log.error("Error submitting feedback: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(FeedbackSubmissionResponse.builder()
                            .success(false)
                            .message("An error occurred while submitting feedback")
                            .build());
        }
    }
    
    /**
     * Handle user silence and get conversation closure guidance
     */
    @PostMapping("/silence")
    public ResponseEntity<ConversationClosureResponse> handleSilence(@Valid @RequestBody ConversationClosureRequest request) {
        log.debug("Handling silence for session: {}, duration: {}s", 
                request.getSessionId(), request.getSilenceDurationSeconds());
        
        ConversationClosureResponse response = conversationClosureService.handleUserSilence(request);
        
        if (response == null) {
            // No action needed yet
            return ResponseEntity.noContent().build();
        }
        
        // Set conversation context if this is a "further assistance" question
        if ("CHECK_ASSISTANCE".equals(response.getActionType())) {
            conversationClosureService.setFurtherAssistanceContext(request.getSessionId());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Reset user activity (called when user sends a message)
     */
    @PostMapping("/activity/{sessionId}")
    public ResponseEntity<Void> resetActivity(@PathVariable String sessionId) {
        conversationClosureService.resetUserActivity(sessionId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Get feedback statistics (for admin/analytics)
     */
    @GetMapping("/stats")
    public ResponseEntity<FeedbackService.FeedbackStats> getFeedbackStats() {
        FeedbackService.FeedbackStats stats = feedbackService.getFeedbackStats();
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Get recent feedback (for monitoring)
     */
    @GetMapping("/recent")
    public ResponseEntity<List<UserFeedback>> getRecentFeedback(@RequestParam(defaultValue = "7") int days) {
        List<UserFeedback> recentFeedback = feedbackService.getRecentFeedback(days);
        return ResponseEntity.ok(recentFeedback);
    }
    
    /**
     * Get user feedback history
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserFeedback>> getUserFeedback(@PathVariable String userId) {
        List<UserFeedback> userFeedback = feedbackService.getUserFeedbackHistory(userId);
        return ResponseEntity.ok(userFeedback);
    }
    
    /**
     * Get conversation status (for debugging)
     */
    @GetMapping("/status/{sessionId}")
    public ResponseEntity<String> getConversationStatus(@PathVariable String sessionId) {
        String status = conversationClosureService.getConversationStatus(sessionId);
        return ResponseEntity.ok(status);
    }
    
    /**
     * Response DTO for feedback submission
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class FeedbackSubmissionResponse {
        private boolean success;
        private String message;
        private Long feedbackId;
    }
}
