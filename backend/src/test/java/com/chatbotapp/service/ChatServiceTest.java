package com.chatbotapp.service;

import com.chatbotapp.dto.ChatRequest;
import com.chatbotapp.dto.ChatResponse;
import com.chatbotapp.entity.ChatMessage;
import com.chatbotapp.repository.ChatMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @InjectMocks
    private ChatService chatService;

    @BeforeEach
    void setUp() {
        // Setup any common test data here
    }

    @Test
    void processMessage_ShouldSaveUserMessageAndReturnBotResponse() {
        // Given
        String userMessage = "Hello, bot!";
        ChatRequest request = new ChatRequest(userMessage);
        
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(new ChatMessage());

        // When
        ChatResponse response = chatService.processMessage(request);

        // Then
        assertNotNull(response);
        assertEquals("Echo: " + userMessage, response.getMessage());
        assertNotNull(response.getTimestamp());
        
        // Verify that save was called twice (user message + bot response)
        verify(chatMessageRepository, times(2)).save(any(ChatMessage.class));
    }

    @Test
    void processMessage_ShouldHandleNullMessage() {
        // Given
        ChatRequest request = new ChatRequest(null);
        
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(new ChatMessage());

        // When
        ChatResponse response = chatService.processMessage(request);

        // Then
        assertNotNull(response);
        assertEquals("Echo: null", response.getMessage());
    }
}
