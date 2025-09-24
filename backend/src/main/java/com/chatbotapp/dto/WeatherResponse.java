package com.chatbotapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WeatherResponse DTO for weather information
 * Contains weather condition, description, temperature, and location
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponse {
    
    /**
     * Weather condition (sunny, cloudy, rainy, stormy)
     */
    private String condition;
    
    /**
     * Human-readable weather description
     */
    private String description;
    
    /**
     * Temperature in Celsius
     */
    private Double temperature;
    
    /**
     * Location name
     */
    private String location;
}
