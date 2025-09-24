package com.chatbotapp.service;

import com.chatbotapp.dto.ChatRequest;
import com.chatbotapp.dto.ChatResponse;
import com.chatbotapp.dto.GreetingResponse;
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

    public ChatService(ChatMessageRepository chatMessageRepository, GreetingService greetingService) {
        this.chatMessageRepository = chatMessageRepository;
        this.greetingService = greetingService;
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
        
        // Default echo response for non-greeting messages
        return "Echo: " + userMessage;
    }
}
