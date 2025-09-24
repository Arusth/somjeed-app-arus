package com.chatbotapp.service;

import com.chatbotapp.dto.WeatherResponse;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * WeatherService provides mocked weather data
 * Simulates weather API with 4 conditions: sunny, cloudy, rainy, stormy
 */
@Service
public class WeatherService {
    
    private final Random random = new Random();
    
    private final List<WeatherResponse> mockWeatherData = Arrays.asList(
        WeatherResponse.builder()
            .condition("sunny")
            .description("Clear skies with bright sunshine")
            .temperature(28.5)
            .location("Bangkok")
            .build(),
        WeatherResponse.builder()
            .condition("cloudy")
            .description("Partly cloudy with overcast skies")
            .temperature(25.0)
            .location("Bangkok")
            .build(),
        WeatherResponse.builder()
            .condition("rainy")
            .description("Light rain with occasional showers")
            .temperature(22.3)
            .location("Bangkok")
            .build(),
        WeatherResponse.builder()
            .condition("stormy")
            .description("Heavy rain with thunderstorms")
            .temperature(20.8)
            .location("Bangkok")
            .build()
    );
    
    /**
     * Get current weather information (mocked)
     * Returns random weather condition for demonstration
     * 
     * @return WeatherResponse with current weather data
     */
    public WeatherResponse getCurrentWeather() {
        int randomIndex = random.nextInt(mockWeatherData.size());
        return mockWeatherData.get(randomIndex);
    }
    
    /**
     * Get weather by condition for testing purposes
     * 
     * @param condition Weather condition (sunny, cloudy, rainy, stormy)
     * @return WeatherResponse for the specified condition
     */
    public WeatherResponse getWeatherByCondition(String condition) {
        return mockWeatherData.stream()
            .filter(weather -> weather.getCondition().equalsIgnoreCase(condition))
            .findFirst()
            .orElse(mockWeatherData.get(0)); // Default to sunny if not found
    }
}
