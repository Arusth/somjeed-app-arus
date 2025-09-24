package com.chatbotapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * GreetingResponse DTO for contextual greetings
 * Contains full greeting message, time of day, weather condition, and timestamp
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GreetingResponse {
    
    /**
     * Complete greeting message with weather context
     */
    private String message;
    
    /**
     * Time of day (morning, afternoon, evening)
     */
    private String timeOfDay;
    
    /**
     * Current weather condition
     */
    private String weatherCondition;
    
    /**
     * Timestamp when greeting was generated
     */
    private LocalDateTime timestamp;
}
