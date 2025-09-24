package com.chatbotapp.service;

import com.chatbotapp.dto.ConversationContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * ConversationContextService manages conversation state for handling follow-up responses
 * Stores context in memory (in production, this would use Redis or database)
 */
@Service
public class ConversationContextService {
    
    // In-memory storage for conversation contexts (use Redis in production)
    private final Map<String, ConversationContext> contextStore = new ConcurrentHashMap<>();
    
    // Context expires after 5 minutes of inactivity
    private static final long CONTEXT_EXPIRY_MINUTES = 5;
    
    /**
     * Store conversation context for a user
     */
    public void setContext(String userId, String action, String intentId, String contextData) {
        ConversationContext context = ConversationContext.builder()
            .userId(userId)
            .lastAction(action)
            .lastIntentId(intentId)
            .contextData(contextData)
            .expectedResponses(Arrays.asList("yes", "no", "ok", "sure", "cancel", "maybe"))
            .timestamp(LocalDateTime.now())
            .build();
            
        contextStore.put(userId, context);
    }
    
    /**
     * Get conversation context for a user
     */
    public ConversationContext getContext(String userId) {
        ConversationContext context = contextStore.get(userId);
        
        // Check if context has expired
        if (context != null && isContextExpired(context)) {
            contextStore.remove(userId);
            return null;
        }
        
        return context;
    }
    
    /**
     * Clear conversation context for a user
     */
    public void clearContext(String userId) {
        contextStore.remove(userId);
    }
    
    /**
     * Check if user response is a simple confirmation/denial
     */
    public boolean isSimpleResponse(String message) {
        String msg = message.toLowerCase().trim();
        return Arrays.asList("yes", "no", "ok", "okay", "sure", "nope", "yep", "yeah", 
                           "cancel", "maybe", "not now", "later", "maybe later", "not sure").contains(msg);
    }
    
    /**
     * Check if user response is positive
     */
    public boolean isPositiveResponse(String message) {
        String msg = message.toLowerCase().trim();
        return Arrays.asList("yes", "ok", "okay", "sure", "yep", "yeah").contains(msg);
    }
    
    /**
     * Check if user response is negative
     */
    public boolean isNegativeResponse(String message) {
        String msg = message.toLowerCase().trim();
        return Arrays.asList("no", "nope", "cancel", "not now", "later", "maybe later", "not sure", "maybe").contains(msg);
    }
    
    /**
     * Check if context has expired
     */
    private boolean isContextExpired(ConversationContext context) {
        return context.getTimestamp().isBefore(LocalDateTime.now().minusMinutes(CONTEXT_EXPIRY_MINUTES));
    }
    
    /**
     * Update context timestamp to extend expiry
     */
    public void refreshContext(String userId) {
        ConversationContext context = contextStore.get(userId);
        if (context != null) {
            context.setTimestamp(LocalDateTime.now());
        }
    }
}
