package com.chatbotapp.service;

import com.chatbotapp.dto.GreetingResponse;
import com.chatbotapp.dto.IntentPrediction;
import com.chatbotapp.dto.WeatherResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for GreetingService
 * Tests time-based greeting logic and weather integration
 */
@ExtendWith(MockitoExtension.class)
class GreetingServiceTest {

    @Mock
    private WeatherService weatherService;

    @Mock
    private IntentPredictionService intentPredictionService;

    @InjectMocks
    private GreetingService greetingService;

    @Test
    @DisplayName("Should generate morning greeting with sunny weather")
    void shouldGenerateMorningGreetingWithSunnyWeather() {
        // Arrange
        LocalTime morningTime = LocalTime.of(9, 0);
        WeatherResponse sunnyWeather = WeatherResponse.builder()
            .condition("sunny")
            .description("Clear skies")
            .temperature(28.5)
            .location("Bangkok")
            .build();
        
        when(weatherService.getWeatherByCondition("sunny")).thenReturn(sunnyWeather);

        // Act
        GreetingResponse greeting = greetingService.generateGreeting(morningTime, "sunny");

        // Assert
        assertThat(greeting).isNotNull();
        assertThat(greeting.getMessage()).isEqualTo("Good morning, on a sunshine day!");
        assertThat(greeting.getTimeOfDay()).isEqualTo("morning");
        assertThat(greeting.getWeatherCondition()).isEqualTo("sunny");
        assertThat(greeting.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Should generate afternoon greeting with cloudy weather")
    void shouldGenerateAfternoonGreetingWithCloudyWeather() {
        // Arrange
        LocalTime afternoonTime = LocalTime.of(14, 30);
        WeatherResponse cloudyWeather = WeatherResponse.builder()
            .condition("cloudy")
            .description("Overcast skies")
            .temperature(25.0)
            .location("Bangkok")
            .build();
        
        when(weatherService.getWeatherByCondition("cloudy")).thenReturn(cloudyWeather);

        // Act
        GreetingResponse greeting = greetingService.generateGreeting(afternoonTime, "cloudy");

        // Assert
        assertThat(greeting).isNotNull();
        assertThat(greeting.getMessage()).isEqualTo("Good afternoon, a bit cloudy but I'm here to help!");
        assertThat(greeting.getTimeOfDay()).isEqualTo("afternoon");
        assertThat(greeting.getWeatherCondition()).isEqualTo("cloudy");
        assertThat(greeting.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Should generate evening greeting with rainy weather")
    void shouldGenerateEveningGreetingWithRainyWeather() {
        // Arrange
        LocalTime eveningTime = LocalTime.of(19, 0);
        WeatherResponse rainyWeather = WeatherResponse.builder()
            .condition("rainy")
            .description("Light rain")
            .temperature(22.3)
            .location("Bangkok")
            .build();
        
        when(weatherService.getWeatherByCondition("rainy")).thenReturn(rainyWeather);

        // Act
        GreetingResponse greeting = greetingService.generateGreeting(eveningTime, "rainy");

        // Assert
        assertThat(greeting).isNotNull();
        assertThat(greeting.getMessage()).isEqualTo("Good evening, stay dry out there!");
        assertThat(greeting.getTimeOfDay()).isEqualTo("evening");
        assertThat(greeting.getWeatherCondition()).isEqualTo("rainy");
        assertThat(greeting.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Should generate greeting with stormy weather")
    void shouldGenerateGreetingWithStormyWeather() {
        // Arrange
        LocalTime morningTime = LocalTime.of(8, 0);
        WeatherResponse stormyWeather = WeatherResponse.builder()
            .condition("stormy")
            .description("Heavy rain with thunderstorms")
            .temperature(20.8)
            .location("Bangkok")
            .build();
        
        when(weatherService.getWeatherByCondition("stormy")).thenReturn(stormyWeather);

        // Act
        GreetingResponse greeting = greetingService.generateGreeting(morningTime, "stormy");

        // Assert
        assertThat(greeting).isNotNull();
        assertThat(greeting.getMessage()).isEqualTo("Good morning, let me help make your stormy day better.");
        assertThat(greeting.getTimeOfDay()).isEqualTo("morning");
        assertThat(greeting.getWeatherCondition()).isEqualTo("stormy");
    }

    @Test
    @DisplayName("Should generate current greeting with random weather")
    void shouldGenerateCurrentGreetingWithRandomWeather() {
        // Arrange
        WeatherResponse mockWeather = WeatherResponse.builder()
            .condition("sunny")
            .description("Clear skies")
            .temperature(28.5)
            .location("Bangkok")
            .build();
        
        when(weatherService.getCurrentWeather()).thenReturn(mockWeather);
        when(intentPredictionService.predictIntents(null)).thenReturn(Collections.emptyList());

        // Act
        GreetingResponse greeting = greetingService.generateGreeting();

        // Assert
        assertThat(greeting).isNotNull();
        assertThat(greeting.getMessage()).isNotBlank();
        assertThat(greeting.getTimeOfDay()).isIn("morning", "afternoon", "evening");
        assertThat(greeting.getWeatherCondition()).isEqualTo("sunny");
        assertThat(greeting.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Should correctly identify greeting messages")
    void shouldCorrectlyIdentifyGreetingMessages() {
        // Test positive cases
        assertThat(greetingService.isGreetingMessage("hello")).isTrue();
        assertThat(greetingService.isGreetingMessage("Hi there")).isTrue();
        assertThat(greetingService.isGreetingMessage("Hey")).isTrue();
        assertThat(greetingService.isGreetingMessage("Good morning")).isTrue();
        assertThat(greetingService.isGreetingMessage("Good afternoon")).isTrue();
        assertThat(greetingService.isGreetingMessage("Good evening")).isTrue();
        assertThat(greetingService.isGreetingMessage("Greetings")).isTrue();
        assertThat(greetingService.isGreetingMessage("Hello, how are you?")).isTrue();

        // Test negative cases
        assertThat(greetingService.isGreetingMessage("How can I help you?")).isFalse();
        assertThat(greetingService.isGreetingMessage("What's the weather?")).isFalse();
        assertThat(greetingService.isGreetingMessage("")).isFalse();
        assertThat(greetingService.isGreetingMessage(null)).isFalse();
        assertThat(greetingService.isGreetingMessage("   ")).isFalse();
    }

    @Test
    @DisplayName("Should handle time boundaries correctly")
    void shouldHandleTimeBoundariesCorrectly() {
        // Arrange
        WeatherResponse mockWeather = WeatherResponse.builder()
            .condition("sunny")
            .description("Clear skies")
            .temperature(28.5)
            .location("Bangkok")
            .build();
        
        when(weatherService.getWeatherByCondition(anyString())).thenReturn(mockWeather);

        // Test morning boundaries
        GreetingResponse earlyMorning = greetingService.generateGreeting(LocalTime.of(5, 0), "sunny");
        GreetingResponse lateMorning = greetingService.generateGreeting(LocalTime.of(11, 59), "sunny");
        assertThat(earlyMorning.getTimeOfDay()).isEqualTo("morning");
        assertThat(lateMorning.getTimeOfDay()).isEqualTo("morning");

        // Test afternoon boundaries
        GreetingResponse earlyAfternoon = greetingService.generateGreeting(LocalTime.of(12, 0), "sunny");
        GreetingResponse lateAfternoon = greetingService.generateGreeting(LocalTime.of(16, 59), "sunny");
        assertThat(earlyAfternoon.getTimeOfDay()).isEqualTo("afternoon");
        assertThat(lateAfternoon.getTimeOfDay()).isEqualTo("afternoon");

        // Test evening boundaries
        GreetingResponse earlyEvening = greetingService.generateGreeting(LocalTime.of(17, 0), "sunny");
        GreetingResponse lateEvening = greetingService.generateGreeting(LocalTime.of(23, 59), "sunny");
        assertThat(earlyEvening.getTimeOfDay()).isEqualTo("evening");
        assertThat(lateEvening.getTimeOfDay()).isEqualTo("evening");

        // Test midnight to early morning (should be evening)
        GreetingResponse midnight = greetingService.generateGreeting(LocalTime.of(0, 0), "sunny");
        GreetingResponse beforeMorning = greetingService.generateGreeting(LocalTime.of(4, 59), "sunny");
        assertThat(midnight.getTimeOfDay()).isEqualTo("evening");
        assertThat(beforeMorning.getTimeOfDay()).isEqualTo("evening");
    }
}
