package com.chatbotapp.service;

import com.chatbotapp.dto.ChatRequest;
import com.chatbotapp.dto.ChatResponse;
import com.chatbotapp.dto.ConversationContext;
import com.chatbotapp.dto.GreetingResponse;
import com.chatbotapp.dto.UserContext;
import com.chatbotapp.dto.UserIntent;
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
    private final IntentRecognitionService intentRecognitionService;
    private final IntentResponseService intentResponseService;
    private final ConversationContextService conversationContextService;

    public ChatService(ChatMessageRepository chatMessageRepository, 
                      GreetingService greetingService, 
                      UserDataService userDataService,
                      IntentRecognitionService intentRecognitionService,
                      IntentResponseService intentResponseService,
                      ConversationContextService conversationContextService) {
        this.chatMessageRepository = chatMessageRepository;
        this.greetingService = greetingService;
        this.userDataService = userDataService;
        this.intentRecognitionService = intentRecognitionService;
        this.intentResponseService = intentResponseService;
        this.conversationContextService = conversationContextService;
    }

    /**
     * Process chat message and generate appropriate response
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
        // Use sessionId as userId if available for context tracking
        String userId = request.getSessionId() != null ? request.getSessionId() : "default_user";
        String botResponseText = generateResponse(request.getMessage(), userId);
        
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
     * Generate bot response based on user message using intent recognition and conversation context
     * 
     * @param userMessage User's message
     * @param userId User ID for session-based context tracking
     * @return Bot response text
     */
    private String generateResponse(String userMessage, String userId) {
        
        // Check if this is a greeting message
        if (greetingService.isGreetingMessage(userMessage)) {
            conversationContextService.clearContext(userId); // Clear context on new greeting
            GreetingResponse greeting = greetingService.generateGreeting();
            return greeting.getMessage();
        }
        
        // Check for conversation context (follow-up responses)
        ConversationContext context = conversationContextService.getContext(userId);
        if (context != null && conversationContextService.isSimpleResponse(userMessage)) {
            String contextResponse = handleContextualResponse(userMessage, context, userId);
            if (contextResponse != null) {
                return contextResponse;
            }
        }
        
        // Check for simple intent prediction responses (backward compatibility)
        String intentResponse = handleIntentPredictionResponse(userMessage);
        if (intentResponse != null) {
            return intentResponse;
        }
        
        // Use advanced intent recognition for complex queries
        UserContext userContext = userDataService.getRandomUserScenario();
        UserIntent recognizedIntent = intentRecognitionService.classifyIntent(userMessage, userContext);
        
        // Generate response based on recognized intent and set context if needed
        if (recognizedIntent.getConfidence() > 0.4) {
            String response = intentResponseService.generateResponse(recognizedIntent, userContext);
            
            // Set conversation context for follow-up questions
            setConversationContext(userId, recognizedIntent, userContext);
            
            return response;
        }
        
        // Default helpful response for unrecognized messages
        conversationContextService.clearContext(userId); // Clear context for unrecognized messages
        return "I'm sorry, I didn't quite understand that. Let me help you with what I can do!\n\n" +
               "I can assist you with:\n" +
               "• Account & Payments: Check balance, due dates, payment options\n" +
               "• Security: Report fraud, block cards, dispute transactions\n" +
               "• Statements: Transaction history, monthly statements\n" +
               "• Credit: Limit increases, available credit\n" +
               "• Rewards: Points balance, redemption options\n" +
               "• Support: Technical issues, account access\n\n" +
               "Could you please rephrase your question or let me know which of these areas you'd like help with?";
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
        
        // Handle "yes" responses - assume user wants payment information (most common intent)
        if (message.equals("yes")) {
            // Default to payment inquiry for "yes" responses (most common case)
            UserContext userContext = userDataService.getUserContext("user_overdue");
            return String.format("Your current outstanding balance is %,.0f THB, and your due date was %s.",
                userContext.getOutstandingBalance(),
                userContext.getDueDate().format(java.time.format.DateTimeFormatter.ofPattern("d MMMM yyyy")));
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
    
    /**
     * Handle contextual responses based on conversation context
     */
    private String handleContextualResponse(String userMessage, ConversationContext context, String userId) {
        String action = context.getLastAction();
        boolean isPositive = conversationContextService.isPositiveResponse(userMessage);
        boolean isNegative = conversationContextService.isNegativeResponse(userMessage);
        
        // Clear context after handling
        conversationContextService.clearContext(userId);
        
        switch (action) {
            case ConversationContext.PAYMENT_CONFIRMATION:
                if (isPositive) {
                    return "Great! I'll help you set up a payment. You can make payments through:\n" +
                           "• Online banking\n" +
                           "• Mobile app\n" +
                           "• Phone banking\n" +
                           "• Visit any branch\n\n" +
                           "Would you like me to guide you through the online payment process?";
                } else if (isNegative) {
                    return "No problem! Your payment is not due immediately. I'm here if you need help with anything else. " +
                           "You can always make a payment later through our app or website.";
                }
                break;
                
            case ConversationContext.CREDIT_LIMIT_REQUEST:
                if (isPositive) {
                    return "Perfect! I've submitted your credit limit increase request for review. " +
                           "You'll receive an email confirmation shortly with your reference number. " +
                           "Our team will review your application within 2-3 business days and contact you with the decision. " +
                           "Is there anything else I can help you with?";
                } else if (isNegative) {
                    return "No worries! You can always request a credit limit increase later when you're ready. " +
                           "Your current limit and available credit remain the same. " +
                           "Is there anything else I can help you with today?";
                }
                break;
                
            case ConversationContext.DUPLICATE_REPORT_CONFIRMATION:
                if (isPositive) {
                    return "I've successfully submitted your duplicate transaction report. " +
                           "Reference number: DUP-" + (System.currentTimeMillis() % 100000) + ". " +
                           "Our fraud team will investigate and contact you within 2-3 business days. " +
                           "You'll receive an email confirmation shortly with all the details.";
                } else if (isNegative) {
                    return "Understood. I won't file a duplicate transaction report at this time. " +
                           "If you change your mind or notice any other suspicious activity, " +
                           "please don't hesitate to contact us immediately.";
                }
                break;
                
            case ConversationContext.SECURITY_PHONE_CONFIRMATION:
                if (isPositive) {
                    return "Thank you for confirming. Our fraud team will contact you at your registered number within 30 minutes. " +
                           "Please answer the call to verify the recent activity on your account. " +
                           "If you don't receive a call within 30 minutes, please contact us directly.";
                } else if (isNegative) {
                    return "I understand your phone number may have changed. For security purposes, " +
                           "please visit any of our branches with valid ID to update your contact information. " +
                           "In the meantime, your account remains secured.";
                }
                break;
                
            case ConversationContext.FURTHER_ASSISTANCE:
                if (isPositive) {
                    return "I'm here to help with whatever you need. You can ask me about:\n\n" +
                           "• Account & Payments: Check balance, due dates, payment options\n" +
                           "• Security: Report fraud, block cards, dispute transactions\n" +
                           "• Statements: Transaction history, monthly statements\n" +
                           "• Credit: Limit increases, available credit\n" +
                           "• Rewards: Points balance, redemption options\n" +
                           "• Support: Technical issues, account access\n\n" +
                           "What would you like to know about?";
                } else if (isNegative) {
                    return "Perfect! It looks like you have everything you need. " +
                           "Thank you for using our service today. " +
                           "Feel free to reach out anytime if you have questions. Have a great day!";
                }
                break;
        }
        
        // Fallback for unhandled context
        return "I'm not sure how to handle that response in this context. " +
               "Could you please be more specific about what you'd like me to help you with?";
    }
    
    /**
     * Set conversation context based on the intent that requires follow-up
     */
    private void setConversationContext(String userId, UserIntent intent, UserContext userContext) {
        switch (intent.getIntentId()) {
            case "PAYMENT_INQUIRY":
                // Set context if the response asks about making a payment
                conversationContextService.setContext(userId, 
                    ConversationContext.PAYMENT_CONFIRMATION, 
                    intent.getIntentId(), 
                    "payment_setup");
                break;
                
            case "CREDIT_LIMIT":
                // Set context if the response asks about submitting a request
                conversationContextService.setContext(userId, 
                    ConversationContext.CREDIT_LIMIT_REQUEST, 
                    intent.getIntentId(), 
                    "limit_increase");
                break;
                
            case "TRANSACTION_DISPUTE":
                // Set context if the response asks about reporting duplicate
                conversationContextService.setContext(userId, 
                    ConversationContext.DUPLICATE_REPORT_CONFIRMATION, 
                    intent.getIntentId(), 
                    "dispute_report");
                break;
                
            case "ACCOUNT_SECURITY":
                // Set context if the response asks about phone number
                conversationContextService.setContext(userId, 
                    ConversationContext.SECURITY_PHONE_CONFIRMATION, 
                    intent.getIntentId(), 
                    "phone_verification");
                break;
                
            // Other intents don't require follow-up context
            default:
                // No context needed
                break;
        }
    }
}
