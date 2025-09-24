package com.chatbotapp.service;

import com.chatbotapp.dto.UserContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * UserDataService provides mock user data for intent prediction
 * Simulates different user scenarios for testing purposes
 */
@Service
public class UserDataService {
    
    private final Random random = new Random();
    
    // Mock user data scenarios
    private final Map<String, UserContext> mockUsers = new HashMap<>();
    
    public UserDataService() {
        initializeMockUsers();
    }
    
    /**
     * Get user context by user ID
     * Returns mock data for demonstration purposes
     * 
     * @param userId User identifier
     * @return UserContext with user-specific data
     */
    public UserContext getUserContext(String userId) {
        return mockUsers.getOrDefault(userId, getRandomUserScenario());
    }
    
    /**
     * Get a random user scenario for demonstration
     * 
     * @return UserContext with random scenario
     */
    public UserContext getRandomUserScenario() {
        String[] userIds = mockUsers.keySet().toArray(new String[0]);
        String randomUserId = userIds[random.nextInt(userIds.length)];
        return mockUsers.get(randomUserId);
    }
    
    /**
     * Initialize mock user data with different scenarios
     */
    private void initializeMockUsers() {
        // Scenario 1: User with overdue payment
        mockUsers.put("user_overdue", UserContext.builder()
            .userId("user_overdue")
            .outstandingBalance(new BigDecimal("120000.00"))
            .dueDate(LocalDate.of(2025, 9, 1)) // Past due date
            .availableCredit(new BigDecimal("80000.00"))
            .creditLimit(new BigDecimal("200000.00"))
            .accountStatus("OVERDUE")
            .lastPaymentConfirmation(LocalDateTime.of(2025, 8, 15, 10, 30))
            .recentTransactions(Arrays.asList(
                UserContext.TransactionData.builder()
                    .transactionId("TXN001")
                    .amount(new BigDecimal("50000.00"))
                    .timestamp(LocalDateTime.of(2025, 8, 25, 14, 20))
                    .type("PURCHASE")
                    .status("COMPLETED")
                    .description("Online shopping")
                    .build(),
                UserContext.TransactionData.builder()
                    .transactionId("TXN002")
                    .amount(new BigDecimal("70000.00"))
                    .timestamp(LocalDateTime.of(2025, 8, 28, 16, 45))
                    .type("PURCHASE")
                    .status("COMPLETED")
                    .description("Electronics store")
                    .build()
            ))
            .build());
        
        // Scenario 2: User with recent payment confirmation
        mockUsers.put("user_recent_payment", UserContext.builder()
            .userId("user_recent_payment")
            .outstandingBalance(new BigDecimal("25000.00"))
            .dueDate(LocalDate.of(2025, 10, 15))
            .availableCredit(new BigDecimal("175000.00"))
            .creditLimit(new BigDecimal("200000.00"))
            .accountStatus("ACTIVE")
            .lastPaymentConfirmation(LocalDateTime.now().minusHours(2)) // Today
            .recentTransactions(Arrays.asList(
                UserContext.TransactionData.builder()
                    .transactionId("TXN003")
                    .amount(new BigDecimal("95000.00"))
                    .timestamp(LocalDateTime.now().minusHours(3))
                    .type("PAYMENT")
                    .status("COMPLETED")
                    .description("Credit card payment")
                    .build(),
                UserContext.TransactionData.builder()
                    .transactionId("TXN004")
                    .amount(new BigDecimal("15000.00"))
                    .timestamp(LocalDateTime.now().minusDays(1))
                    .type("PURCHASE")
                    .status("COMPLETED")
                    .description("Restaurant")
                    .build()
            ))
            .build());
        
        // Scenario 3: User with duplicate transactions
        mockUsers.put("user_duplicate_transactions", UserContext.builder()
            .userId("user_duplicate_transactions")
            .outstandingBalance(new BigDecimal("45000.00"))
            .dueDate(LocalDate.of(2025, 10, 20))
            .availableCredit(new BigDecimal("155000.00"))
            .creditLimit(new BigDecimal("200000.00"))
            .accountStatus("ACTIVE")
            .lastPaymentConfirmation(LocalDateTime.of(2025, 9, 10, 9, 15))
            .recentTransactions(Arrays.asList(
                UserContext.TransactionData.builder()
                    .transactionId("TXN005")
                    .amount(new BigDecimal("12500.00"))
                    .timestamp(LocalDateTime.now().minusMinutes(15))
                    .type("PURCHASE")
                    .status("COMPLETED")
                    .description("Online subscription")
                    .build(),
                UserContext.TransactionData.builder()
                    .transactionId("TXN006")
                    .amount(new BigDecimal("12500.00"))
                    .timestamp(LocalDateTime.now().minusMinutes(8))
                    .type("PURCHASE")
                    .status("COMPLETED")
                    .description("Online subscription")
                    .build(),
                UserContext.TransactionData.builder()
                    .transactionId("TXN007")
                    .amount(new BigDecimal("8000.00"))
                    .timestamp(LocalDateTime.now().minusHours(1))
                    .type("PURCHASE")
                    .status("COMPLETED")
                    .description("Grocery store")
                    .build()
            ))
            .build());
        
        // Scenario 4: Normal user (no specific issues)
        mockUsers.put("user_normal", UserContext.builder()
            .userId("user_normal")
            .outstandingBalance(new BigDecimal("35000.00"))
            .dueDate(LocalDate.of(2025, 10, 25))
            .availableCredit(new BigDecimal("165000.00"))
            .creditLimit(new BigDecimal("200000.00"))
            .accountStatus("ACTIVE")
            .lastPaymentConfirmation(LocalDateTime.of(2025, 9, 5, 11, 20))
            .recentTransactions(Arrays.asList(
                UserContext.TransactionData.builder()
                    .transactionId("TXN008")
                    .amount(new BigDecimal("5000.00"))
                    .timestamp(LocalDateTime.now().minusDays(2))
                    .type("PURCHASE")
                    .status("COMPLETED")
                    .description("Coffee shop")
                    .build(),
                UserContext.TransactionData.builder()
                    .transactionId("TXN009")
                    .amount(new BigDecimal("18000.00"))
                    .timestamp(LocalDateTime.now().minusDays(5))
                    .type("PURCHASE")
                    .status("COMPLETED")
                    .description("Gas station")
                    .build()
            ))
            .build());
    }
}
