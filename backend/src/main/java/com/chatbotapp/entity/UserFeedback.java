package com.chatbotapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing user feedback for conversation satisfaction
 * Stores rating, comments, and conversation context
 */
@Entity
@Table(name = "user_feedback")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFeedback {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String sessionId;
    
    @Column(nullable = false, length = 50)
    private String userId;
    
    @Column(nullable = false)
    private Integer rating; // 1-5 stars
    
    @Column(length = 1000)
    private String comment;
    
    @Column(nullable = false, length = 50)
    private String conversationTopic; // e.g., "payment_inquiry", "balance_check"
    
    @Column(nullable = false)
    private Boolean wasHelpful;
    
    @Column(nullable = false)
    private LocalDateTime submittedAt;
    
    @Column(nullable = false)
    private LocalDateTime conversationStartedAt;
    
    @Column(nullable = false)
    private LocalDateTime conversationEndedAt;
    
    @Column(nullable = false)
    private Integer messageCount; // Total messages in conversation
    
    @Column(length = 20)
    private String deviceType; // "mobile", "desktop", "tablet"
    
    @PrePersist
    protected void onCreate() {
        if (submittedAt == null) {
            submittedAt = LocalDateTime.now();
        }
        if (wasHelpful == null) {
            wasHelpful = rating != null && rating >= 4;
        }
    }
}
