package com.chatbotapp.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Request DTO for submitting user feedback
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRequest {
    
    @NotBlank(message = "Session ID is required")
    @Size(max = 100, message = "Session ID must not exceed 100 characters")
    private String sessionId;
    
    @NotBlank(message = "User ID is required")
    @Size(max = 50, message = "User ID must not exceed 50 characters")
    private String userId;
    
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer rating;
    
    @Size(max = 1000, message = "Comment must not exceed 1000 characters")
    private String comment;
    
    @NotBlank(message = "Conversation topic is required")
    @Size(max = 50, message = "Conversation topic must not exceed 50 characters")
    private String conversationTopic;
    
    @NotNull(message = "Conversation start time is required")
    private LocalDateTime conversationStartedAt;
    
    @NotNull(message = "Conversation end time is required")
    private LocalDateTime conversationEndedAt;
    
    @NotNull(message = "Message count is required")
    @Min(value = 1, message = "Message count must be at least 1")
    private Integer messageCount;
    
    @Size(max = 20, message = "Device type must not exceed 20 characters")
    private String deviceType;
    
    // Additional context for silence tracking
    private Long totalSilenceDurationSeconds;
    private Integer silenceIntervals;
    private Boolean conversationCompletedNaturally;
}
