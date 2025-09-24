package com.chatbotapp.service;

import com.chatbotapp.dto.ChatRequest;
import com.chatbotapp.dto.ChatResponse;
import com.chatbotapp.dto.GreetingResponse;
import com.chatbotapp.entity.ChatMessage;
import com.chatbotapp.repository.ChatMessageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @InjectMocks
    private ChatService chatService;

    @Test
    @DisplayName("Should process regular message and return echo response")
    void shouldProcessRegularMessageAndReturnEchoResponse() {
        // Arrange
        String userMessage = "How can you help me?";
        ChatRequest request = new ChatRequest(userMessage);
        
        when(greetingService.isGreetingMessage(userMessage)).thenReturn(false);
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(new ChatMessage());

        // Act
        ChatResponse response = chatService.processMessage(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("Echo: " + userMessage);
        assertThat(response.getTimestamp()).isNotNull();
        
        // Verify that save was called twice (user message + bot response)
        verify(chatMessageRepository, times(2)).save(any(ChatMessage.class));
        verify(greetingService).isGreetingMessage(userMessage);
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
        
        when(greetingService.isGreetingMessage(anyString())).thenReturn(false);
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(new ChatMessage());

        // Act
        ChatResponse response = chatService.processMessage(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("Echo: " + maxMessage);
        
        verify(chatMessageRepository, times(2)).save(any(ChatMessage.class));
    }
}
