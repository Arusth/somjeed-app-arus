package com.chatbotapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ConversationContext stores the current conversation state
 * to handle follow-up responses like "yes", "no", "ok"
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationContext {
    
    /**
     * The last action/question the bot asked about
     */
    private String lastAction;
    
    /**
     * The intent ID of the last question
     */
    private String lastIntentId;
    
    /**
     * Additional context data for the last action
     */
    private String contextData;
    
    /**
     * Possible follow-up actions user can take
     */
    private List<String> expectedResponses;
    
    /**
     * When this context was created
     */
    private LocalDateTime timestamp;
    
    /**
     * User ID this context belongs to
     */
    private String userId;
    
    // Common context types
    public static final String PAYMENT_CONFIRMATION = "PAYMENT_CONFIRMATION";
    public static final String CREDIT_LIMIT_REQUEST = "CREDIT_LIMIT_REQUEST";
    public static final String CARD_BLOCK_CONFIRMATION = "CARD_BLOCK_CONFIRMATION";
    public static final String DUPLICATE_REPORT_CONFIRMATION = "DUPLICATE_REPORT_CONFIRMATION";
    public static final String STATEMENT_REQUEST = "STATEMENT_REQUEST";
    public static final String SECURITY_PHONE_CONFIRMATION = "SECURITY_PHONE_CONFIRMATION";
}
