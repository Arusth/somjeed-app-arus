package com.chatbotapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for conversation closure actions
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationClosureResponse {
    
    private String message; // The message Somjeed should send
    private String actionType; // "CHECK_ASSISTANCE", "REQUEST_FEEDBACK", "GOODBYE"
    private Boolean shouldRequestFeedback;
    private Boolean shouldCloseConversation;
    private Long nextCheckInSeconds; // When to check again if no response
    
    // Pre-defined messages for different closure stages
    public static ConversationClosureResponse checkAssistance() {
        return ConversationClosureResponse.builder()
                .message("Do you need any further assistance?")
                .actionType("CHECK_ASSISTANCE")
                .shouldRequestFeedback(false)
                .shouldCloseConversation(false)
                .nextCheckInSeconds(10L) // Check again in 10 seconds
                .build();
    }
    
    public static ConversationClosureResponse requestFeedback() {
        return ConversationClosureResponse.builder()
                .message("Thanks for chatting with me today. Before you go, could you rate your experience?")
                .actionType("REQUEST_FEEDBACK")
                .shouldRequestFeedback(true)
                .shouldCloseConversation(false)
                .nextCheckInSeconds(30L) // Give 30 seconds for feedback
                .build();
    }
    
    public static ConversationClosureResponse goodbye() {
        return ConversationClosureResponse.builder()
                .message("Thank you for using our service today. Have a great day!")
                .actionType("GOODBYE")
                .shouldRequestFeedback(false)
                .shouldCloseConversation(true)
                .nextCheckInSeconds(null)
                .build();
    }
}
