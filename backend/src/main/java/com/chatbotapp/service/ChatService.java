package com.chatbotapp.service;

import com.chatbotapp.dto.ChatRequest;
import com.chatbotapp.dto.ChatResponse;
import com.chatbotapp.entity.ChatMessage;
import com.chatbotapp.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    public ChatResponse processMessage(ChatRequest request) {
        // Save user message
        ChatMessage userMessage = new ChatMessage();
        userMessage.setMessage(request.getMessage());
        userMessage.setSender("user");
        userMessage.setTimestamp(LocalDateTime.now());
        chatMessageRepository.save(userMessage);

        // Generate bot response (simple echo for now)
        String botResponseText = "Echo: " + request.getMessage();
        
        // Save bot response
        ChatMessage botMessage = new ChatMessage();
        botMessage.setMessage(botResponseText);
        botMessage.setSender("bot");
        botMessage.setTimestamp(LocalDateTime.now());
        chatMessageRepository.save(botMessage);

        return new ChatResponse(botResponseText, LocalDateTime.now());
    }
}
