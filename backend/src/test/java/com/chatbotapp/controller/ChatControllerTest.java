package com.chatbotapp.controller;

import com.chatbotapp.dto.ChatRequest;
import com.chatbotapp.dto.ChatResponse;
import com.chatbotapp.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void sendMessage_ShouldReturnChatResponse() throws Exception {
        // Given
        ChatRequest request = new ChatRequest("Hello");
        ChatResponse response = new ChatResponse("Echo: Hello", LocalDateTime.now());
        
        when(chatService.processMessage(any(ChatRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/chat/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Echo: Hello"));
    }

    @Test
    void sendMessage_WithEmptyMessage_ShouldReturnBadRequest() throws Exception {
        // Given
        ChatRequest request = new ChatRequest("");

        // When & Then
        mockMvc.perform(post("/api/chat/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void health_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/chat/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Chat service is running"));
    }
}
