package com.chatbotapp.service;

import com.chatbotapp.dto.WeatherResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for WeatherService
 * Tests mocked weather data functionality
 */
@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @InjectMocks
    private WeatherService weatherService;

    @Test
    @DisplayName("Should return weather data when getting current weather")
    void shouldReturnWeatherDataWhenGettingCurrentWeather() {
        // Act
        WeatherResponse weather = weatherService.getCurrentWeather();

        // Assert
        assertThat(weather).isNotNull();
        assertThat(weather.getCondition()).isIn("sunny", "cloudy", "rainy", "stormy");
        assertThat(weather.getDescription()).isNotBlank();
        assertThat(weather.getTemperature()).isNotNull();
        assertThat(weather.getLocation()).isEqualTo("Bangkok");
    }

    @Test
    @DisplayName("Should return sunny weather when requesting sunny condition")
    void shouldReturnSunnyWeatherWhenRequestingSunnyCondition() {
        // Act
        WeatherResponse weather = weatherService.getWeatherByCondition("sunny");

        // Assert
        assertThat(weather).isNotNull();
        assertThat(weather.getCondition()).isEqualTo("sunny");
        assertThat(weather.getDescription()).isEqualTo("Clear skies with bright sunshine");
        assertThat(weather.getTemperature()).isEqualTo(28.5);
        assertThat(weather.getLocation()).isEqualTo("Bangkok");
    }

    @Test
    @DisplayName("Should return cloudy weather when requesting cloudy condition")
    void shouldReturnCloudyWeatherWhenRequestingCloudyCondition() {
        // Act
        WeatherResponse weather = weatherService.getWeatherByCondition("cloudy");

        // Assert
        assertThat(weather).isNotNull();
        assertThat(weather.getCondition()).isEqualTo("cloudy");
        assertThat(weather.getDescription()).isEqualTo("Partly cloudy with overcast skies");
        assertThat(weather.getTemperature()).isEqualTo(25.0);
        assertThat(weather.getLocation()).isEqualTo("Bangkok");
    }

    @Test
    @DisplayName("Should return rainy weather when requesting rainy condition")
    void shouldReturnRainyWeatherWhenRequestingRainyCondition() {
        // Act
        WeatherResponse weather = weatherService.getWeatherByCondition("rainy");

        // Assert
        assertThat(weather).isNotNull();
        assertThat(weather.getCondition()).isEqualTo("rainy");
        assertThat(weather.getDescription()).isEqualTo("Light rain with occasional showers");
        assertThat(weather.getTemperature()).isEqualTo(22.3);
        assertThat(weather.getLocation()).isEqualTo("Bangkok");
    }

    @Test
    @DisplayName("Should return stormy weather when requesting stormy condition")
    void shouldReturnStormyWeatherWhenRequestingStormyCondition() {
        // Act
        WeatherResponse weather = weatherService.getWeatherByCondition("stormy");

        // Assert
        assertThat(weather).isNotNull();
        assertThat(weather.getCondition()).isEqualTo("stormy");
        assertThat(weather.getDescription()).isEqualTo("Heavy rain with thunderstorms");
        assertThat(weather.getTemperature()).isEqualTo(20.8);
        assertThat(weather.getLocation()).isEqualTo("Bangkok");
    }

    @Test
    @DisplayName("Should return default sunny weather when requesting unknown condition")
    void shouldReturnDefaultSunnyWeatherWhenRequestingUnknownCondition() {
        // Act
        WeatherResponse weather = weatherService.getWeatherByCondition("unknown");

        // Assert
        assertThat(weather).isNotNull();
        assertThat(weather.getCondition()).isEqualTo("sunny");
        assertThat(weather.getDescription()).isEqualTo("Clear skies with bright sunshine");
        assertThat(weather.getTemperature()).isEqualTo(28.5);
        assertThat(weather.getLocation()).isEqualTo("Bangkok");
    }

    @Test
    @DisplayName("Should handle case insensitive condition requests")
    void shouldHandleCaseInsensitiveConditionRequests() {
        // Act
        WeatherResponse weather1 = weatherService.getWeatherByCondition("SUNNY");
        WeatherResponse weather2 = weatherService.getWeatherByCondition("Cloudy");
        WeatherResponse weather3 = weatherService.getWeatherByCondition("rAiNy");

        // Assert
        assertThat(weather1.getCondition()).isEqualTo("sunny");
        assertThat(weather2.getCondition()).isEqualTo("cloudy");
        assertThat(weather3.getCondition()).isEqualTo("rainy");
    }
}
