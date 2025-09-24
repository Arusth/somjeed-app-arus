package com.chatbotapp.service;

import com.chatbotapp.dto.ConversationClosureRequest;
import com.chatbotapp.dto.ConversationClosureResponse;
import com.chatbotapp.dto.ConversationContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for handling conversation closure due to user inactivity/silence
 * Tracks silence periods and guides conversations toward natural closure
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationClosureService {
    
    private final ConversationContextService conversationContextService;
    
    // Track conversation states per session
    private final ConcurrentHashMap<String, ConversationState> conversationStates = new ConcurrentHashMap<>();
    
    private static class ConversationState {
        LocalDateTime lastActivity;
        int silenceCheckCount = 0;
        boolean assistanceOffered = false;
        boolean feedbackRequested = false;
        boolean conversationClosed = false;
        String lastUserMessage;
        
        ConversationState(LocalDateTime lastActivity) {
            this.lastActivity = lastActivity;
        }
    }
    
    /**
     * Handle user silence and determine appropriate response
     */
    public ConversationClosureResponse handleUserSilence(ConversationClosureRequest request) {
        log.debug("Handling user silence for session: {}, duration: {}s", 
                request.getSessionId(), request.getSilenceDurationSeconds());
        
        ConversationState state = conversationStates.computeIfAbsent(
            request.getSessionId(), 
            k -> new ConversationState(request.getLastActivityAt())
        );
        
        // Update state
        state.lastActivity = request.getLastActivityAt();
        state.lastUserMessage = request.getLastUserMessage();
        state.silenceCheckCount++;
        
        // Determine response based on silence duration and conversation context
        return determineClosureAction(request, state);
    }
    
    /**
     * Determine what action to take based on silence duration and context
     */
    private ConversationClosureResponse determineClosureAction(
            ConversationClosureRequest request, 
            ConversationState state) {
        
        long silenceDuration = request.getSilenceDurationSeconds();
        
        // Log context for debugging
        log.debug("Determining closure action for session: {}, silence: {}s, last message: '{}'", 
                request.getSessionId(), silenceDuration, state.lastUserMessage);
        
        // First silence period (10+ seconds) - Check if they need more help
        if (silenceDuration >= 10 && !state.assistanceOffered) {
            state.assistanceOffered = true;
            log.info("Offering assistance check for session: {} after user said: '{}'", 
                    request.getSessionId(), state.lastUserMessage);
            return ConversationClosureResponse.checkAssistance();
        }
        
        // Second silence period (20+ seconds total) - Request feedback
        if (silenceDuration >= 20 && state.assistanceOffered && !state.feedbackRequested) {
            state.feedbackRequested = true;
            log.info("Requesting feedback for session: {} (last message: '{}')", 
                    request.getSessionId(), state.lastUserMessage);
            return ConversationClosureResponse.requestFeedback();
        }
        
        // Final silence period (50+ seconds total) - Say goodbye
        if (silenceDuration >= 50 && state.feedbackRequested && !state.conversationClosed) {
            state.conversationClosed = true;
            log.info("Closing conversation for session: {} (conversation ended after: '{}')", 
                    request.getSessionId(), state.lastUserMessage);
            return ConversationClosureResponse.goodbye();
        }
        
        // Still within acceptable silence range
        return null;
    }
    
    /**
     * Reset conversation state when user becomes active again
     */
    public void resetUserActivity(String sessionId) {
        ConversationState state = conversationStates.get(sessionId);
        if (state != null) {
            state.lastActivity = LocalDateTime.now();
            state.silenceCheckCount = 0;
            // Reset assistance offered flag so user can get help again if they go silent
            state.assistanceOffered = false;
            // Keep feedbackRequested and conversationClosed to avoid repeating those
            log.debug("Reset activity for session: {}", sessionId);
        }
    }
    
    /**
     * Mark conversation as completed (user provided feedback or explicitly ended)
     */
    public void markConversationCompleted(String sessionId) {
        ConversationState state = conversationStates.get(sessionId);
        if (state != null) {
            state.conversationClosed = true;
            log.info("Conversation marked as completed for session: {}", sessionId);
        }
    }
    
    /**
     * Clean up old conversation states (call periodically)
     */
    public void cleanupOldSessions() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(2);
        conversationStates.entrySet().removeIf(entry -> 
            entry.getValue().lastActivity.isBefore(cutoff));
        log.debug("Cleaned up old conversation states");
    }
    
    /**
     * Get current conversation state for debugging
     */
    public String getConversationStatus(String sessionId) {
        ConversationState state = conversationStates.get(sessionId);
        if (state == null) {
            return "No active conversation";
        }
        
        return String.format("Last activity: %s, Checks: %d, Assistance offered: %b, Feedback requested: %b, Closed: %b",
            state.lastActivity, state.silenceCheckCount, state.assistanceOffered, 
            state.feedbackRequested, state.conversationClosed);
    }
    
    /**
     * Set conversation context for "further assistance" question
     */
    public void setFurtherAssistanceContext(String sessionId) {
        // Use sessionId as userId for simplicity (in production, map sessionId to userId)
        String userId = sessionId;
        
        conversationContextService.setContext(
            userId,
            ConversationContext.FURTHER_ASSISTANCE,
            "SILENCE_CHECK",
            "further_assistance_inquiry"
        );
        
        log.debug("Set FURTHER_ASSISTANCE context for session: {}", sessionId);
    }
}
