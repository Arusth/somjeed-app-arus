package com.chatbotapp.service;

import com.chatbotapp.dto.ChatRequest;
import com.chatbotapp.dto.ChatResponse;
import com.chatbotapp.dto.GreetingResponse;
import com.chatbotapp.dto.UserContext;
import com.chatbotapp.entity.ChatMessage;
import com.chatbotapp.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * ChatService handles chat message processing and conversation logic
 * Integrates with GreetingService for contextual greetings
 */
@Service
@Transactional
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final GreetingService greetingService;
    private final UserDataService userDataService;

    public ChatService(ChatMessageRepository chatMessageRepository, GreetingService greetingService, UserDataService userDataService) {
        this.chatMessageRepository = chatMessageRepository;
        this.greetingService = greetingService;
        this.userDataService = userDataService;
    }

    /**
     * Process user message and generate appropriate bot response
     * Detects greeting messages and provides contextual responses
     * 
     * @param request ChatRequest containing user message
     * @return ChatResponse with bot reply
     */
    public ChatResponse processMessage(ChatRequest request) {
        validateMessage(request.getMessage());
        
        // Save user message
        ChatMessage userMessage = createUserMessage(request);
        chatMessageRepository.save(userMessage);

        // Generate bot response based on message content
        String botResponseText = generateResponse(request.getMessage());
        
        // Save bot response
        ChatMessage botMessage = createBotMessage(botResponseText);
        chatMessageRepository.save(botMessage);

        return ChatResponse.builder()
            .message(botResponseText)
            .timestamp(botMessage.getTimestamp())
            .build();
    }

    /**
     * Validate incoming message
     * 
     * @param message Message to validate
     */
    private void validateMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }
        if (message.length() > 1000) {
            throw new IllegalArgumentException("Message too long");
        }
    }

    /**
     * Create user message entity
     * 
     * @param request ChatRequest
     * @return ChatMessage entity
     */
    private ChatMessage createUserMessage(ChatRequest request) {
        ChatMessage userMessage = new ChatMessage();
        userMessage.setMessage(request.getMessage());
        userMessage.setSender("user");
        userMessage.setTimestamp(LocalDateTime.now());
        return userMessage;
    }

    /**
     * Create bot message entity
     * 
     * @param responseText Bot response text
     * @return ChatMessage entity
     */
    private ChatMessage createBotMessage(String responseText) {
        ChatMessage botMessage = new ChatMessage();
        botMessage.setMessage(responseText);
        botMessage.setSender("bot");
        botMessage.setTimestamp(LocalDateTime.now());
        return botMessage;
    }

    /**
     * Generate bot response based on user message
     * 
     * @param userMessage User's message
     * @return Bot response text
     */
    private String generateResponse(String userMessage) {
        // Check if this is a greeting message
        if (greetingService.isGreetingMessage(userMessage)) {
            GreetingResponse greeting = greetingService.generateGreeting();
            return greeting.getMessage();
        }
        
        // Check for intent prediction responses
        String intentResponse = handleIntentPredictionResponse(userMessage);
        if (intentResponse != null) {
            return intentResponse;
        }
        
        // Default echo response for non-greeting messages
        return "Echo: " + userMessage;
    }
    
    /**
     * Handle responses to intent predictions
     * 
     * @param userMessage User's message
     * @return Intent-specific response or null if not an intent response
     */
    private String handleIntentPredictionResponse(String userMessage) {
        String message = userMessage.toLowerCase().trim();
        
        // Handle payment amount requests
        if (message.contains("get payment amount") || 
            message.contains("check balance") || 
            message.contains("payment")) {
            
            // Get user context (using overdue user as example)
            UserContext userContext = userDataService.getUserContext("user_overdue");
            return String.format("Your current outstanding balance is %,.2f THB, and your due date was %s.",
                userContext.getOutstandingBalance(),
                userContext.getDueDate().toString());
        }
        
        // Handle credit balance requests
        if (message.contains("get updated credit balance") || 
            message.contains("check available credit") || 
            message.contains("credit balance")) {
            
            UserContext userContext = userDataService.getUserContext("user_recent_payment");
            return String.format("Your available credit is %,.2f THB out of %,.2f THB total credit limit.",
                userContext.getAvailableCredit(),
                userContext.getCreditLimit());
        }
        
        // Handle duplicate transaction requests
        if (message.contains("check for duplicate") || 
            message.contains("duplicate transaction") || 
            message.contains("cancel transaction")) {
            
            UserContext userContext = userDataService.getUserContext("user_duplicate_transactions");
            if (!userContext.getRecentTransactions().isEmpty()) {
                UserContext.TransactionData transaction = userContext.getRecentTransactions().get(0);
                return String.format("I found duplicate transactions of %,.2f THB. Transaction ID: %s. Would you like me to help you report this as a duplicate charge?",
                    transaction.getAmount(),
                    transaction.getTransactionId());
            }
        }
        
        // Handle "yes" responses - need to determine context
        if (message.equals("yes")) {
            // For now, provide a generic helpful response for "yes"
            // In a real system, you would track conversation context
            return "I understand you'd like to proceed. Could you please be more specific about what you'd like me to help you with? For example:\n" +
                   "• 'Check my balance' - to see your outstanding balance\n" +
                   "• 'Check available credit' - to see your credit limit\n" +
                   "• 'Report duplicate transaction' - to report duplicate charges";
        }
        
        // Handle "no" responses - provide helpful alternatives
        if (message.equals("no")) {
            return "No problem! I'm here to help with whatever you need. You can ask me about:\n" +
                   "• Account information and balances\n" +
                   "• Payment history and due dates\n" +
                   "• Transaction details and reports\n" +
                   "• Credit limit and available credit\n" +
                   "\nWhat would you like to know about?";
        }
        
        // Handle specific duplicate transaction responses
        if (message.contains("report duplicate") || 
            message.contains("report this") ||
            message.contains("duplicate charge")) {
            
            return "I've initiated a duplicate transaction report for you. Our fraud team will review the transactions and contact you within 2-3 business days. You'll receive an email confirmation shortly with your case reference number.";
        }
        
        return null; // Not an intent response
    }
}
