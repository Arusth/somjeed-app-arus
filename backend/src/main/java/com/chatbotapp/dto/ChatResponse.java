package com.chatbotapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ChatResponse DTO for chat message responses
 * Contains message content and timestamp
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    
    /**
     * Bot response message
     */
    private String message;
    
    /**
     * Response timestamp
     */
    private LocalDateTime timestamp;
}
