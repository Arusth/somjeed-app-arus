package com.chatbotapp.service;

import com.chatbotapp.dto.GreetingResponse;
import com.chatbotapp.dto.WeatherResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * GreetingService handles contextual greetings based on time and weather
 * Provides time-based greetings with weather-aware messages
 */
@Service
public class GreetingService {
    
    private final WeatherService weatherService;
    
    public GreetingService(WeatherService weatherService) {
        this.weatherService = weatherService;
    }
    
    /**
     * Generate contextual greeting based on current time and weather
     * 
     * @return GreetingResponse with personalized greeting message
     */
    public GreetingResponse generateGreeting() {
        LocalDateTime now = LocalDateTime.now();
        String timeOfDay = getTimeOfDay(now.toLocalTime());
        WeatherResponse weather = weatherService.getCurrentWeather();
        String greetingMessage = buildGreetingMessage(timeOfDay, weather);
        
        return GreetingResponse.builder()
            .message(greetingMessage)
            .timeOfDay(timeOfDay)
            .weatherCondition(weather.getCondition())
            .timestamp(now)
            .build();
    }
    
    /**
     * Generate greeting for specific time and weather (for testing)
     * 
     * @param time LocalTime for greeting
     * @param weatherCondition Weather condition
     * @return GreetingResponse with specified parameters
     */
    public GreetingResponse generateGreeting(LocalTime time, String weatherCondition) {
        String timeOfDay = getTimeOfDay(time);
        WeatherResponse weather = weatherService.getWeatherByCondition(weatherCondition);
        String greetingMessage = buildGreetingMessage(timeOfDay, weather);
        
        return GreetingResponse.builder()
            .message(greetingMessage)
            .timeOfDay(timeOfDay)
            .weatherCondition(weather.getCondition())
            .timestamp(LocalDateTime.now())
            .build();
    }
    
    /**
     * Determine time of day based on hour
     * 05:00 – 11:59 ➔ "morning"
     * 12:00 – 16:59 ➔ "afternoon" 
     * 17:00+ ➔ "evening"
     * 
     * @param time LocalTime to evaluate
     * @return String representing time of day
     */
    private String getTimeOfDay(LocalTime time) {
        int hour = time.getHour();
        
        if (hour >= 5 && hour < 12) {
            return "morning";
        } else if (hour >= 12 && hour < 17) {
            return "afternoon";
        } else {
            return "evening";
        }
    }
    
    /**
     * Build greeting message combining time of day and weather
     * 
     * @param timeOfDay Time period (morning/afternoon/evening)
     * @param weather Current weather information
     * @return Complete greeting message
     */
    private String buildGreetingMessage(String timeOfDay, WeatherResponse weather) {
        String baseGreeting = getBaseGreeting(timeOfDay);
        String weatherMessage = getWeatherMessage(weather.getCondition());
        
        return baseGreeting + ", " + weatherMessage;
    }
    
    /**
     * Get base greeting for time of day
     * 
     * @param timeOfDay Time period
     * @return Base greeting string
     */
    private String getBaseGreeting(String timeOfDay) {
        Map<String, String> greetings = new HashMap<>();
        greetings.put("morning", "Good morning");
        greetings.put("afternoon", "Good afternoon");
        greetings.put("evening", "Good evening");
        
        return greetings.getOrDefault(timeOfDay, "Hello");
    }
    
    /**
     * Get weather-specific message
     * 
     * @param condition Weather condition
     * @return Weather-aware message
     */
    private String getWeatherMessage(String condition) {
        Map<String, String> weatherMessages = new HashMap<>();
        weatherMessages.put("sunny", "on a sunshine day!");
        weatherMessages.put("cloudy", "a bit cloudy but I'm here to help!");
        weatherMessages.put("rainy", "stay dry out there!");
        weatherMessages.put("stormy", "let me help make your stormy day better.");
        
        return weatherMessages.getOrDefault(condition, "I'm here to help you today!");
    }
    
    /**
     * Check if a message is a greeting trigger
     * 
     * @param message User message to check
     * @return true if message contains greeting keywords
     */
    public boolean isGreetingMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return false;
        }
        
        String lowerMessage = message.toLowerCase().trim();
        return lowerMessage.matches("^(hi|hello|hey|good morning|good afternoon|good evening|greetings?).*");
    }
}
