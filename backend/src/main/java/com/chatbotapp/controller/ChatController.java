package com.chatbotapp.controller;

import com.chatbotapp.dto.ChatRequest;
import com.chatbotapp.dto.ChatResponse;
import com.chatbotapp.dto.GreetingResponse;
import com.chatbotapp.service.ChatService;
import com.chatbotapp.service.GreetingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * ChatController handles REST endpoints for chat functionality
 * Provides message processing and greeting endpoints
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final GreetingService greetingService;

    public ChatController(ChatService chatService, GreetingService greetingService) {
        this.chatService = chatService;
        this.greetingService = greetingService;
    }

    /**
     * Process chat message and return bot response
     * 
     * @param request ChatRequest containing user message
     * @return ChatResponse with bot reply
     */
    @PostMapping("/message")
    public ResponseEntity<ChatResponse> sendMessage(@Valid @RequestBody ChatRequest request) {
        ChatResponse response = chatService.processMessage(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get contextual greeting based on time and weather
     * 
     * @return GreetingResponse with personalized greeting
     */
    @GetMapping("/greeting")
    public ResponseEntity<GreetingResponse> getGreeting() {
        GreetingResponse greeting = greetingService.generateGreeting();
        return ResponseEntity.ok(greeting);
    }

    /**
     * Health check endpoint
     * 
     * @return Health status message
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Chat service is running");
    }
}
