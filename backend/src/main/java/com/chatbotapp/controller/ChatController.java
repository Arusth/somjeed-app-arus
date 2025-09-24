package com.chatbotapp.controller;

import com.chatbotapp.dto.ChatRequest;
import com.chatbotapp.dto.ChatResponse;
import com.chatbotapp.dto.GreetingResponse;
import com.chatbotapp.dto.IntentPrediction;
import com.chatbotapp.dto.UserIntent;
import com.chatbotapp.service.ChatService;
import com.chatbotapp.service.GreetingService;
import com.chatbotapp.service.IntentPredictionService;
import com.chatbotapp.service.IntentRecognitionService;
import com.chatbotapp.service.UserDataService;
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
    private final IntentPredictionService intentPredictionService;
    private final IntentRecognitionService intentRecognitionService;
    private final UserDataService userDataService;

    public ChatController(ChatService chatService, 
                         GreetingService greetingService, 
                         IntentPredictionService intentPredictionService,
                         IntentRecognitionService intentRecognitionService,
                         UserDataService userDataService) {
        this.chatService = chatService;
        this.greetingService = greetingService;
        this.intentPredictionService = intentPredictionService;
        this.intentRecognitionService = intentRecognitionService;
        this.userDataService = userDataService;
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
     * @param userId Optional user ID for personalized intent predictions
     * @return GreetingResponse with personalized greeting and intent predictions
     */
    @GetMapping("/greeting")
    public ResponseEntity<GreetingResponse> getGreeting(@RequestParam(required = false) String userId) {
        GreetingResponse greeting = greetingService.generateGreeting(userId);
        return ResponseEntity.ok(greeting);
    }
    
    /**
     * Get intent predictions for a specific user
     * 
     * @param userId User ID for intent prediction (optional, uses random scenario if null)
     * @return List of predicted intents
     */
    @GetMapping("/intents")
    public ResponseEntity<java.util.List<IntentPrediction>> getIntentPredictions(@RequestParam(required = false) String userId) {
        java.util.List<IntentPrediction> predictions = intentPredictionService.predictIntents(userId);
        return ResponseEntity.ok(predictions);
    }
    
    /**
     * Get the top priority intent prediction for immediate display
     * 
     * @param userId User ID for intent prediction (optional)
     * @return Top priority intent prediction
     */
    @GetMapping("/intents/top")
    public ResponseEntity<IntentPrediction> getTopIntent(@RequestParam(required = false) String userId) {
        IntentPrediction topIntent = intentPredictionService.getTopPriorityIntent(userId);
        if (topIntent != null) {
            return ResponseEntity.ok(topIntent);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    /**
     * Classify user message intent for testing purposes
     * 
     * @param message User message to classify
     * @return Classified intent with confidence and entities
     */
    @GetMapping("/classify")
    public ResponseEntity<UserIntent> classifyIntent(@RequestParam String message) {
        UserIntent intent = intentRecognitionService.classifyIntent(message, userDataService.getRandomUserScenario());
        return ResponseEntity.ok(intent);
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
