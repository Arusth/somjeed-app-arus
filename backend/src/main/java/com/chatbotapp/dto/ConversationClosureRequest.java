package com.chatbotapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Request DTO for initiating conversation closure due to inactivity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationClosureRequest {
    
    @NotBlank(message = "Session ID is required")
    private String sessionId;
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotNull(message = "Last activity time is required")
    private LocalDateTime lastActivityAt;
    
    @NotNull(message = "Silence duration is required")
    private Long silenceDurationSeconds;
    
    @NotNull(message = "Message count is required")
    private Integer messageCount;
    
    private String lastUserMessage;
    private String conversationTopic;
    private Boolean userNeedsResolved; // Did the user get what they needed?
}
