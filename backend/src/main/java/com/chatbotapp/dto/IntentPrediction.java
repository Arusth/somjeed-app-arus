package com.chatbotapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * IntentPrediction DTO for predicted user intents and suggested actions
 * Based on user context analysis and behavioral patterns
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntentPrediction {
    
    /**
     * Predicted intent identifier
     */
    private String intentId;
    
    /**
     * Intent category (PAYMENT, BALANCE, TRANSACTION, SUPPORT)
     */
    private String category;
    
    /**
     * Predicted intent description
     */
    private String predictedIntent;
    
    /**
     * Suggested response message to user
     */
    private String suggestedMessage;
    
    /**
     * Confidence score (0.0 to 1.0)
     */
    private Double confidence;
    
    /**
     * Priority level (HIGH, MEDIUM, LOW)
     */
    private String priority;
    
    /**
     * Context that triggered this prediction
     */
    private String triggerContext;
    
    /**
     * Suggested action buttons/options
     */
    private String[] suggestedActions;
    
    /**
     * Timestamp when prediction was made
     */
    private LocalDateTime timestamp;
    
    /**
     * Whether this prediction should be shown immediately after greeting
     */
    private Boolean showAfterGreeting;
}
