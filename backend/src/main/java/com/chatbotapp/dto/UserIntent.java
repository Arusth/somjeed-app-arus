package com.chatbotapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * UserIntent DTO representing a classified user intent with confidence and context
 * Used for intent recognition and response generation in credit card customer support
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserIntent {
    
    /**
     * Intent identifier (e.g., PAYMENT_INQUIRY, TRANSACTION_DISPUTE)
     */
    private String intentId;
    
    /**
     * Intent category (PAYMENT, TRANSACTION, ACCOUNT, SUPPORT, SECURITY)
     */
    private String category;
    
    /**
     * Human-readable intent name
     */
    private String intentName;
    
    /**
     * Confidence score (0.0 to 1.0)
     */
    private Double confidence;
    
    /**
     * Extracted entities/parameters from user message
     */
    private List<IntentEntity> entities;
    
    /**
     * Context information that influenced intent classification
     */
    private String context;
    
    /**
     * Suggested response template
     */
    private String responseTemplate;
    
    /**
     * Follow-up questions or actions
     */
    private List<String> followUpActions;
    
    /**
     * Timestamp when intent was classified
     */
    private LocalDateTime timestamp;
    
    /**
     * IntentEntity nested class for extracted parameters
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IntentEntity {
        private String entityType; // AMOUNT, DATE, MERCHANT, TRANSACTION_ID
        private String entityValue;
        private Double confidence;
    }
}
