package com.chatbotapp.service;

import com.chatbotapp.dto.ChatRequest;
import com.chatbotapp.dto.ChatResponse;
import com.chatbotapp.dto.GreetingResponse;
import com.chatbotapp.dto.UserContext;
import com.chatbotapp.dto.UserIntent;
import com.chatbotapp.entity.ChatMessage;
import com.chatbotapp.repository.ChatMessageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ChatService
 * Tests message processing and greeting integration
 */
@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private GreetingService greetingService;

    @Mock
    private UserDataService userDataService;

    @Mock
    private IntentRecognitionService intentRecognitionService;

    @Mock
    private IntentResponseService intentResponseService;

    @Mock
    private ConversationContextService conversationContextService;

    @InjectMocks
    private ChatService chatService;

    @Test
    @DisplayName("Should process regular message and return echo response")
    void shouldProcessRegularMessageAndReturnEchoResponse() {
        // Arrange
        String userMessage = "How can you help me?";
        ChatRequest request = new ChatRequest(userMessage);
        
        // Mock dependencies
        when(greetingService.isGreetingMessage(userMessage)).thenReturn(false);
        when(conversationContextService.getContext(anyString())).thenReturn(null);
        when(userDataService.getRandomUserScenario()).thenReturn(createMockUserContext());
        when(intentRecognitionService.classifyIntent(anyString(), any(UserContext.class)))
            .thenReturn(createMockUserIntent("GENERAL_INQUIRY", 0.3)); // Low confidence for default response
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(new ChatMessage());

        // Act
        ChatResponse response = chatService.processMessage(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).contains("I'm sorry, I didn't quite understand that");
        assertThat(response.getTimestamp()).isNotNull();
        
        // Verify that save was called twice (user message + bot response)
        verify(chatMessageRepository, times(2)).save(any(ChatMessage.class));
        verify(greetingService).isGreetingMessage(userMessage);
        verify(conversationContextService).clearContext(anyString());
    }

    @Test
    @DisplayName("Should process greeting message and return contextual greeting")
    void shouldProcessGreetingMessageAndReturnContextualGreeting() {
        // Arrange
        String userMessage = "Hello";
        ChatRequest request = new ChatRequest(userMessage);
        
        GreetingResponse mockGreeting = GreetingResponse.builder()
            .message("Good morning, on a sunshine day!")
            .timeOfDay("morning")
            .weatherCondition("sunny")
            .timestamp(LocalDateTime.now())
            .build();
        
        when(greetingService.isGreetingMessage(userMessage)).thenReturn(true);
        when(greetingService.generateGreeting()).thenReturn(mockGreeting);
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(new ChatMessage());

        // Act
        ChatResponse response = chatService.processMessage(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("Good morning, on a sunshine day!");
        assertThat(response.getTimestamp()).isNotNull();
        
        // Verify interactions
        verify(chatMessageRepository, times(2)).save(any(ChatMessage.class));
        verify(greetingService).isGreetingMessage(userMessage);
        verify(greetingService).generateGreeting();
        verify(conversationContextService).clearContext(anyString());
    }

    @Test
    @DisplayName("Should throw exception for empty message")
    void shouldThrowExceptionForEmptyMessage() {
        // Arrange
        ChatRequest request = new ChatRequest("");

        // Act & Assert
        assertThatThrownBy(() -> chatService.processMessage(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Message cannot be empty");
        
        verify(chatMessageRepository, never()).save(any(ChatMessage.class));
    }

    @Test
    @DisplayName("Should throw exception for null message")
    void shouldThrowExceptionForNullMessage() {
        // Arrange
        ChatRequest request = new ChatRequest(null);

        // Act & Assert
        assertThatThrownBy(() -> chatService.processMessage(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Message cannot be empty");
        
        verify(chatMessageRepository, never()).save(any(ChatMessage.class));
    }

    @Test
    @DisplayName("Should throw exception for message too long")
    void shouldThrowExceptionForMessageTooLong() {
        // Arrange
        String longMessage = "a".repeat(1001);
        ChatRequest request = new ChatRequest(longMessage);

        // Act & Assert
        assertThatThrownBy(() -> chatService.processMessage(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Message too long");
        
        verify(chatMessageRepository, never()).save(any(ChatMessage.class));
    }

    @Test
    @DisplayName("Should handle whitespace-only message as empty")
    void shouldHandleWhitespaceOnlyMessageAsEmpty() {
        // Arrange
        ChatRequest request = new ChatRequest("   ");

        // Act & Assert
        assertThatThrownBy(() -> chatService.processMessage(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Message cannot be empty");
        
        verify(chatMessageRepository, never()).save(any(ChatMessage.class));
    }

    @Test
    @DisplayName("Should process maximum length message successfully")
    void shouldProcessMaximumLengthMessageSuccessfully() {
        // Arrange
        String maxMessage = "a".repeat(1000);
        ChatRequest request = new ChatRequest(maxMessage);
        
        // Mock dependencies
        when(greetingService.isGreetingMessage(anyString())).thenReturn(false);
        when(conversationContextService.getContext(anyString())).thenReturn(null);
        when(userDataService.getRandomUserScenario()).thenReturn(createMockUserContext());
        when(intentRecognitionService.classifyIntent(anyString(), any(UserContext.class)))
            .thenReturn(createMockUserIntent("GENERAL_INQUIRY", 0.3)); // Low confidence for default response
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(new ChatMessage());

        // Act
        ChatResponse response = chatService.processMessage(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).contains("I'm sorry, I didn't quite understand that");
        
        verify(chatMessageRepository, times(2)).save(any(ChatMessage.class));
        verify(conversationContextService).clearContext(anyString());
    }

    /**
     * Helper method to create mock UserContext
     */
    private UserContext createMockUserContext() {
        UserContext userContext = new UserContext();
        userContext.setUserId("test_user");
        userContext.setOutstandingBalance(new BigDecimal("1000.00"));
        userContext.setCreditLimit(new BigDecimal("5000.00"));
        userContext.setAvailableCredit(new BigDecimal("4000.00"));
        userContext.setAccountStatus("ACTIVE");
        return userContext;
    }

    /**
     * Helper method to create mock UserIntent
     */
    private UserIntent createMockUserIntent(String intentId, double confidence) {
        UserIntent intent = new UserIntent();
        intent.setIntentId(intentId);
        intent.setConfidence(confidence);
        intent.setCategory("GENERAL");
        intent.setIntentName("Test Intent");
        intent.setTimestamp(LocalDateTime.now());
        return intent;
    }
}
