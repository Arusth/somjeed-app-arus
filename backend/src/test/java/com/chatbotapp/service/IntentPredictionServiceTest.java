package com.chatbotapp.service;

import com.chatbotapp.dto.IntentPrediction;
import com.chatbotapp.dto.UserContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Unit tests for IntentPredictionService
 * Tests the three main prediction scenarios and intent ranking logic
 */
@ExtendWith(MockitoExtension.class)
class IntentPredictionServiceTest {

    @Mock
    private UserDataService userDataService;

    @InjectMocks
    private IntentPredictionService intentPredictionService;

    @Test
    @DisplayName("Should predict payment intent for overdue user")
    void shouldPredictPaymentIntentForOverdueUser() {
        // Arrange
        UserContext overdueUser = createOverdueUserContext();
        when(userDataService.getUserContext("user_overdue")).thenReturn(overdueUser);

        // Act
        List<IntentPrediction> predictions = intentPredictionService.predictIntents("user_overdue");

        // Assert
        assertThat(predictions).isNotEmpty();
        IntentPrediction topPrediction = predictions.get(0);
        assertThat(topPrediction.getIntentId()).isEqualTo("OVERDUE_PAYMENT");
        assertThat(topPrediction.getPriority()).isEqualTo("HIGH");
        assertThat(topPrediction.getConfidence()).isEqualTo(0.95);
        assertThat(topPrediction.getCategory()).isEqualTo("PAYMENT");
        assertThat(topPrediction.getPredictedIntent()).isEqualTo("Get Payment Amount");
    }

    @Test
    @DisplayName("Should predict credit balance intent for recent payment user")
    void shouldPredictCreditBalanceIntentForRecentPaymentUser() {
        // Arrange
        UserContext recentPaymentUser = createRecentPaymentUserContext();
        when(userDataService.getUserContext("user_recent_payment")).thenReturn(recentPaymentUser);

        // Act
        List<IntentPrediction> predictions = intentPredictionService.predictIntents("user_recent_payment");

        // Assert
        assertThat(predictions).isNotEmpty();
        IntentPrediction topPrediction = predictions.get(0);
        assertThat(topPrediction.getIntentId()).isEqualTo("RECENT_PAYMENT");
        assertThat(topPrediction.getPriority()).isEqualTo("MEDIUM");
        assertThat(topPrediction.getConfidence()).isEqualTo(0.85);
        assertThat(topPrediction.getCategory()).isEqualTo("BALANCE");
        assertThat(topPrediction.getPredictedIntent()).isEqualTo("Get Updated Credit Balance");
    }

    @Test
    @DisplayName("Should predict duplicate transaction intent for user with similar transactions")
    void shouldPredictDuplicateTransactionIntentForUserWithSimilarTransactions() {
        // Arrange
        UserContext duplicateTransactionUser = createDuplicateTransactionUserContext();
        when(userDataService.getUserContext("user_duplicate_transactions")).thenReturn(duplicateTransactionUser);

        // Act
        List<IntentPrediction> predictions = intentPredictionService.predictIntents("user_duplicate_transactions");

        // Assert
        assertThat(predictions).isNotEmpty();
        IntentPrediction topPrediction = predictions.get(0);
        assertThat(topPrediction.getIntentId()).isEqualTo("DUPLICATE_TRANSACTION");
        assertThat(topPrediction.getPriority()).isEqualTo("MEDIUM");
        assertThat(topPrediction.getConfidence()).isEqualTo(0.80);
        assertThat(topPrediction.getCategory()).isEqualTo("TRANSACTION");
        assertThat(topPrediction.getPredictedIntent()).isEqualTo("Check for Duplicate/Cancel Transaction");
    }

    @Test
    @DisplayName("Should return empty predictions for normal user")
    void shouldReturnEmptyPredictionsForNormalUser() {
        // Arrange
        UserContext normalUser = createNormalUserContext();
        when(userDataService.getUserContext("user_normal")).thenReturn(normalUser);

        // Act
        List<IntentPrediction> predictions = intentPredictionService.predictIntents("user_normal");

        // Assert
        assertThat(predictions).isEmpty();
    }

    @Test
    @DisplayName("Should use random scenario when userId is null")
    void shouldUseRandomScenarioWhenUserIdIsNull() {
        // Arrange
        UserContext randomUser = createOverdueUserContext();
        when(userDataService.getRandomUserScenario()).thenReturn(randomUser);

        // Act
        List<IntentPrediction> predictions = intentPredictionService.predictIntents(null);

        // Assert
        assertThat(predictions).isNotEmpty();
        IntentPrediction topPrediction = predictions.get(0);
        assertThat(topPrediction.getIntentId()).isEqualTo("OVERDUE_PAYMENT");
    }

    @Test
    @DisplayName("Should prioritize overdue payment over other intents")
    void shouldPrioritizeOverduePaymentOverOtherIntents() {
        // Arrange - User with both overdue payment and recent payment confirmation
        UserContext complexUser = UserContext.builder()
                .userId("complex_user")
                .outstandingBalance(new BigDecimal("1200.00"))
                .dueDate(LocalDate.now().minusDays(5)) // Overdue
                .lastPaymentConfirmation(LocalDateTime.now()) // Recent payment today
                .accountStatus("OVERDUE")
                .availableCredit(new BigDecimal("3800.00"))
                .creditLimit(new BigDecimal("5000.00"))
                .recentTransactions(Collections.emptyList())
                .build();

        when(userDataService.getUserContext("complex_user")).thenReturn(complexUser);

        // Act
        List<IntentPrediction> predictions = intentPredictionService.predictIntents("complex_user");

        // Assert
        assertThat(predictions).hasSize(2);
        // First prediction should be overdue payment (HIGH priority)
        IntentPrediction firstPrediction = predictions.get(0);
        assertThat(firstPrediction.getIntentId()).isEqualTo("OVERDUE_PAYMENT");
        assertThat(firstPrediction.getPriority()).isEqualTo("HIGH");
        
        // Second prediction should be credit balance (MEDIUM priority)
        IntentPrediction secondPrediction = predictions.get(1);
        assertThat(secondPrediction.getIntentId()).isEqualTo("RECENT_PAYMENT");
        assertThat(secondPrediction.getPriority()).isEqualTo("MEDIUM");
    }

    @Test
    @DisplayName("Should handle user with no context gracefully")
    void shouldHandleUserWithNoContextGracefully() {
        // Arrange
        when(userDataService.getUserContext("nonexistent_user")).thenReturn(null);

        // Act & Assert - Service should handle null context
        assertThatThrownBy(() -> intentPredictionService.predictIntents("nonexistent_user"))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should detect duplicate transactions with different amounts")
    void shouldDetectDuplicateTransactionsWithDifferentAmounts() {
        // Arrange
        UserContext userWithVariedDuplicates = UserContext.builder()
                .userId("varied_duplicates")
                .outstandingBalance(new BigDecimal("500.00"))
                .dueDate(LocalDate.now().plusDays(10))
                .accountStatus("ACTIVE")
                .recentTransactions(Arrays.asList(
                        UserContext.TransactionData.builder()
                                .transactionId("TXN001")
                                .amount(new BigDecimal("150.00"))
                                .timestamp(LocalDateTime.now().minusMinutes(60))
                                .type("PURCHASE")
                                .status("COMPLETED")
                                .build(),
                        UserContext.TransactionData.builder()
                                .transactionId("TXN002")
                                .amount(new BigDecimal("150.00"))
                                .timestamp(LocalDateTime.now().minusMinutes(45))
                                .type("PURCHASE")
                                .status("COMPLETED")
                                .build(),
                        UserContext.TransactionData.builder()
                                .transactionId("TXN003")
                                .amount(new BigDecimal("75.00"))
                                .timestamp(LocalDateTime.now().minusMinutes(30))
                                .type("PURCHASE")
                                .status("COMPLETED")
                                .build()
                ))
                .build();

        when(userDataService.getUserContext("varied_duplicates")).thenReturn(userWithVariedDuplicates);

        // Act
        List<IntentPrediction> predictions = intentPredictionService.predictIntents("varied_duplicates");

        // Assert
        assertThat(predictions).hasSize(1);
        IntentPrediction prediction = predictions.get(0);
        assertThat(prediction.getIntentId()).isEqualTo("DUPLICATE_TRANSACTION");
        assertThat(prediction.getPredictedIntent()).isEqualTo("Check for Duplicate/Cancel Transaction");
    }

    // Helper methods to create test data
    private UserContext createOverdueUserContext() {
        return UserContext.builder()
                .userId("user_overdue")
                .outstandingBalance(new BigDecimal("1200.00"))
                .dueDate(LocalDate.now().minusDays(23))
                .accountStatus("OVERDUE")
                .availableCredit(new BigDecimal("3800.00"))
                .creditLimit(new BigDecimal("5000.00"))
                .recentTransactions(Collections.emptyList())
                .build();
    }

    private UserContext createRecentPaymentUserContext() {
        return UserContext.builder()
                .userId("user_recent_payment")
                .outstandingBalance(new BigDecimal("0.00"))
                .dueDate(LocalDate.now().plusDays(25))
                .lastPaymentConfirmation(LocalDateTime.now()) // Today
                .accountStatus("ACTIVE")
                .availableCredit(new BigDecimal("4500.00"))
                .creditLimit(new BigDecimal("5000.00"))
                .recentTransactions(Collections.emptyList())
                .build();
    }

    private UserContext createDuplicateTransactionUserContext() {
        return UserContext.builder()
                .userId("user_duplicate_transactions")
                .outstandingBalance(new BigDecimal("800.00"))
                .dueDate(LocalDate.now().plusDays(15))
                .accountStatus("ACTIVE")
                .recentTransactions(Arrays.asList(
                        UserContext.TransactionData.builder()
                                .transactionId("TXN001")
                                .amount(new BigDecimal("299.99"))
                                .timestamp(LocalDateTime.now().minusMinutes(45))
                                .type("PURCHASE")
                                .status("COMPLETED")
                                .build(),
                        UserContext.TransactionData.builder()
                                .transactionId("TXN002")
                                .amount(new BigDecimal("299.99"))
                                .timestamp(LocalDateTime.now().minusMinutes(30))
                                .type("PURCHASE")
                                .status("COMPLETED")
                                .build()
                ))
                .build();
    }

    private UserContext createNormalUserContext() {
        return UserContext.builder()
                .userId("user_normal")
                .outstandingBalance(new BigDecimal("450.00"))
                .dueDate(LocalDate.now().plusDays(20))
                .accountStatus("ACTIVE")
                .availableCredit(new BigDecimal("4550.00"))
                .creditLimit(new BigDecimal("5000.00"))
                .recentTransactions(Arrays.asList(
                        UserContext.TransactionData.builder()
                                .transactionId("TXN001")
                                .amount(new BigDecimal("50.00"))
                                .timestamp(LocalDateTime.now().minusDays(2))
                                .type("PURCHASE")
                                .status("COMPLETED")
                                .build()
                ))
                .build();
    }
}
