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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for ChatController
 * Tests REST endpoints for chat functionality and greetings
 */
@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;

    @MockBean
    private GreetingService greetingService;

    @MockBean
    private IntentPredictionService intentPredictionService;

    @MockBean
    private IntentRecognitionService intentRecognitionService;

    @MockBean
    private UserDataService userDataService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should return chat response when sending message")
    void shouldReturnChatResponseWhenSendingMessage() throws Exception {
        // Arrange
        ChatRequest request = new ChatRequest("Hello");
        ChatResponse response = ChatResponse.builder()
            .message("Good morning, on a sunshine day!")
            .timestamp(LocalDateTime.now())
            .build();
        
        when(chatService.processMessage(any(ChatRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/chat/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Good morning, on a sunshine day!"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Should return bad request for empty message")
    void shouldReturnBadRequestForEmptyMessage() throws Exception {
        // Arrange
        ChatRequest request = new ChatRequest("");

        // Act & Assert
        mockMvc.perform(post("/api/chat/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return bad request for null message")
    void shouldReturnBadRequestForNullMessage() throws Exception {
        // Arrange
        ChatRequest request = new ChatRequest(null);

        // Act & Assert
        mockMvc.perform(post("/api/chat/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return greeting response when getting greeting")
    void shouldReturnGreetingResponseWhenGettingGreeting() throws Exception {
        // Arrange
        GreetingResponse greeting = GreetingResponse.builder()
            .message("Good morning, on a sunshine day!")
            .timeOfDay("morning")
            .weatherCondition("sunny")
            .timestamp(LocalDateTime.now())
            .build();
        
        when(greetingService.generateGreeting(anyString())).thenReturn(greeting);
        when(greetingService.generateGreeting((String) null)).thenReturn(greeting);

        // Act & Assert
        mockMvc.perform(get("/api/chat/greeting"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Good morning, on a sunshine day!"))
                .andExpect(jsonPath("$.timeOfDay").value("morning"))
                .andExpect(jsonPath("$.weatherCondition").value("sunny"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Should return OK for health check")
    void shouldReturnOkForHealthCheck() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/chat/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Chat service is running"));
    }

    @Test
    @DisplayName("Should handle CORS for frontend requests")
    void shouldHandleCorsForFrontendRequests() throws Exception {
        // Arrange
        ChatRequest request = new ChatRequest("Hello");
        ChatResponse response = ChatResponse.builder()
            .message("Echo: Hello")
            .timestamp(LocalDateTime.now())
            .build();
        
        when(chatService.processMessage(any(ChatRequest.class))).thenReturn(response);

        // Act & Assert - Test multiple origins
        mockMvc.perform(post("/api/chat/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk());
                
        mockMvc.perform(post("/api/chat/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Origin", "http://127.0.0.1:54155"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return intent predictions when getting intents")
    void shouldReturnIntentPredictionsWhenGettingIntents() throws Exception {
        // Arrange
        IntentPrediction prediction = IntentPrediction.builder()
            .intentId("PAYMENT_INQUIRY")
            .category("PAYMENT")
            .predictedIntent("Payment and Balance Inquiry")
            .confidence(0.95)
            .priority("HIGH")
            .suggestedActions(new String[]{"Check Balance", "Make Payment"})
            .build();
        
        when(intentPredictionService.predictIntents(anyString())).thenReturn(Arrays.asList(prediction));

        // Act & Assert
        mockMvc.perform(get("/api/chat/intents")
                .param("userId", "testUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].intentId").value("PAYMENT_INQUIRY"))
                .andExpect(jsonPath("$[0].confidence").value(0.95))
                .andExpect(jsonPath("$[0].priority").value("HIGH"));
    }

    @Test
    @DisplayName("Should return top intent when getting top intent")
    void shouldReturnTopIntentWhenGettingTopIntent() throws Exception {
        // Arrange
        IntentPrediction topIntent = IntentPrediction.builder()
            .intentId("PAYMENT_INQUIRY")
            .category("PAYMENT")
            .predictedIntent("Payment and Balance Inquiry")
            .confidence(0.95)
            .priority("HIGH")
            .suggestedActions(new String[]{"Check Balance"})
            .build();
        
        when(intentPredictionService.getTopPriorityIntent(anyString())).thenReturn(topIntent);

        // Act & Assert
        mockMvc.perform(get("/api/chat/intents/top")
                .param("userId", "testUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.intentId").value("PAYMENT_INQUIRY"))
                .andExpect(jsonPath("$.confidence").value(0.95));
    }

    @Test
    @DisplayName("Should return no content when no top intent available")
    void shouldReturnNoContentWhenNoTopIntentAvailable() throws Exception {
        // Arrange
        when(intentPredictionService.getTopPriorityIntent(anyString())).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/chat/intents/top")
                .param("userId", "testUser"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should classify intent when getting classification")
    void shouldClassifyIntentWhenGettingClassification() throws Exception {
        // Arrange
        UserIntent userIntent = UserIntent.builder()
            .intentId("PAYMENT_INQUIRY")
            .category("PAYMENT")
            .intentName("Payment and Balance Inquiry")
            .confidence(0.9)
            .entities(Collections.emptyList())
            .timestamp(LocalDateTime.now())
            .build();
        
        when(intentRecognitionService.classifyIntent(anyString(), any())).thenReturn(userIntent);
        when(userDataService.getRandomUserScenario()).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/chat/classify")
                .param("message", "What is my balance?"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.intentId").value("PAYMENT_INQUIRY"))
                .andExpect(jsonPath("$.category").value("PAYMENT"))
                .andExpect(jsonPath("$.confidence").value(0.9));
    }
}
