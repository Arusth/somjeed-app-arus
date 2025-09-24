package com.chatbotapp.service;

import com.chatbotapp.dto.FeedbackRequest;
import com.chatbotapp.entity.UserFeedback;
import com.chatbotapp.repository.UserFeedbackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for FeedbackService
 * Tests feedback submission, storage, and analytics
 */
@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

    @Mock
    private UserFeedbackRepository feedbackRepository;

    @Mock
    private ConversationClosureService conversationClosureService;

    @InjectMocks
    private FeedbackService feedbackService;

    private FeedbackRequest mockFeedbackRequest;

    @BeforeEach
    void setUp() {
        mockFeedbackRequest = FeedbackRequest.builder()
            .sessionId("test-session-123")
            .userId("test-user")
            .rating(5)
            .comment("Great service!")
            .conversationTopic("Payment Inquiry")
            .conversationStartedAt(LocalDateTime.now().minusMinutes(10))
            .conversationEndedAt(LocalDateTime.now())
            .messageCount(8)
            .deviceType("Desktop")
            .build();
    }

    @Test
    @DisplayName("Should submit feedback successfully")
    void shouldSubmitFeedbackSuccessfully() {
        // Arrange
        UserFeedback savedFeedback = UserFeedback.builder()
            .id(1L)
            .sessionId("test-session-123")
            .userId("test-user")
            .rating(5)
            .comment("Great service!")
            .conversationTopic("Payment Inquiry")
            .messageCount(8)
            .deviceType("Desktop")
            .wasHelpful(true)
            .submittedAt(LocalDateTime.now())
            .conversationStartedAt(LocalDateTime.now().minusMinutes(10))
            .conversationEndedAt(LocalDateTime.now())
            .build();

        when(feedbackRepository.findBySessionId("test-session-123")).thenReturn(Optional.empty());
        when(feedbackRepository.save(any(UserFeedback.class))).thenReturn(savedFeedback);

        // Act
        UserFeedback result = feedbackService.submitFeedback(mockFeedbackRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getRating()).isEqualTo(5);

        ArgumentCaptor<UserFeedback> feedbackCaptor = ArgumentCaptor.forClass(UserFeedback.class);
        verify(feedbackRepository).save(feedbackCaptor.capture());
        verify(conversationClosureService).markConversationCompleted("test-session-123");

        UserFeedback capturedFeedback = feedbackCaptor.getValue();
        assertThat(capturedFeedback.getRating()).isEqualTo(5);
        assertThat(capturedFeedback.getComment()).isEqualTo("Great service!");
        assertThat(capturedFeedback.getSessionId()).isEqualTo("test-session-123");
        assertThat(capturedFeedback.getConversationTopic()).isEqualTo("Payment Inquiry");
        assertThat(capturedFeedback.getMessageCount()).isEqualTo(8);
        assertThat(capturedFeedback.getDeviceType()).isEqualTo("Desktop");
    }

    @Test
    @DisplayName("Should throw exception when feedback already exists for session")
    void shouldThrowExceptionWhenFeedbackAlreadyExistsForSession() {
        // Arrange
        UserFeedback existingFeedback = UserFeedback.builder()
            .id(1L)
            .sessionId("test-session-123")
            .rating(4)
            .build();

        when(feedbackRepository.findBySessionId("test-session-123")).thenReturn(Optional.of(existingFeedback));

        // Act & Assert
        assertThatThrownBy(() -> feedbackService.submitFeedback(mockFeedbackRequest))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Feedback has already been submitted for this session");

        verify(feedbackRepository, never()).save(any(UserFeedback.class));
        verify(conversationClosureService, never()).markConversationCompleted(any());
    }

    @Test
    @DisplayName("Should get feedback stats successfully")
    void shouldGetFeedbackStatsSuccessfully() {
        // Arrange
        List<UserFeedback> mockFeedbacks = Arrays.asList(
            UserFeedback.builder().rating(5).wasHelpful(true).build(),
            UserFeedback.builder().rating(4).wasHelpful(true).build(),
            UserFeedback.builder().rating(3).wasHelpful(false).build(),
            UserFeedback.builder().rating(2).wasHelpful(false).build()
        );

        when(feedbackRepository.findAll()).thenReturn(mockFeedbacks);
        when(feedbackRepository.countByWasHelpful(true)).thenReturn(2L);
        when(feedbackRepository.countByWasHelpful(false)).thenReturn(2L);

        // Act
        FeedbackService.FeedbackStats stats = feedbackService.getFeedbackStats();

        // Assert
        assertThat(stats).isNotNull();
        assertThat(stats.getTotalFeedback()).isEqualTo(4);
        assertThat(stats.getAverageRating()).isEqualTo(3.5);
        assertThat(stats.getHelpfulCount()).isEqualTo(2L);
        assertThat(stats.getNotHelpfulCount()).isEqualTo(2L);
        assertThat(stats.getSatisfactionRate()).isEqualTo(50.0);
    }

    @Test
    @DisplayName("Should return empty stats when no feedback exists")
    void shouldReturnEmptyStatsWhenNoFeedbackExists() {
        // Arrange
        when(feedbackRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        FeedbackService.FeedbackStats stats = feedbackService.getFeedbackStats();

        // Assert
        assertThat(stats).isNotNull();
        assertThat(stats.getTotalFeedback()).isEqualTo(0);
        assertThat(stats.getAverageRating()).isEqualTo(0.0);
        assertThat(stats.getHelpfulCount()).isEqualTo(0);
        assertThat(stats.getNotHelpfulCount()).isEqualTo(0);
        assertThat(stats.getSatisfactionRate()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should get recent feedback correctly")
    void shouldGetRecentFeedbackCorrectly() {
        // Arrange
        List<UserFeedback> mockRecentFeedback = Arrays.asList(
            UserFeedback.builder()
                .rating(5)
                .comment("Excellent!")
                .submittedAt(LocalDateTime.now().minusHours(1))
                .build(),
            UserFeedback.builder()
                .rating(4)
                .comment("Good service")
                .submittedAt(LocalDateTime.now().minusHours(2))
                .build()
        );

        when(feedbackRepository.findRecentFeedback(any(LocalDateTime.class))).thenReturn(mockRecentFeedback);

        // Act
        List<UserFeedback> recentFeedback = feedbackService.getRecentFeedback(7);

        // Assert
        assertThat(recentFeedback).hasSize(2);
        assertThat(recentFeedback.get(0).getRating()).isEqualTo(5);
        assertThat(recentFeedback.get(0).getComment()).isEqualTo("Excellent!");
        assertThat(recentFeedback.get(1).getRating()).isEqualTo(4);
        assertThat(recentFeedback.get(1).getComment()).isEqualTo("Good service");
        verify(feedbackRepository).findRecentFeedback(any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should generate appropriate feedback response messages")
    void shouldGenerateAppropriateFeedbackResponseMessages() {
        // Act & Assert
        assertThat(feedbackService.generateFeedbackResponseMessage(5))
            .contains("Thank you so much!")
            .contains("🌟");

        assertThat(feedbackService.generateFeedbackResponseMessage(4))
            .contains("Thank you for the positive feedback!")
            .contains("😊");

        assertThat(feedbackService.generateFeedbackResponseMessage(3))
            .contains("Thank you for your feedback")
            .contains("keep working to improve");

        assertThat(feedbackService.generateFeedbackResponseMessage(2))
            .contains("sorry the experience wasn't better")
            .contains("work on improving");

        assertThat(feedbackService.generateFeedbackResponseMessage(1))
            .contains("sorry to hear about your experience")
            .contains("helps us improve");

        assertThat(feedbackService.generateFeedbackResponseMessage(0))
            .isEqualTo("Thank you for your feedback!");
    }

}
