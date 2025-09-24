package com.chatbotapp.service;

import com.chatbotapp.dto.IntentPrediction;
import com.chatbotapp.dto.UserContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * IntentPredictionService analyzes user context to predict likely intents
 * Implements the three main prediction scenarios:
 * 1. User is past due date → Get Payment Amount
 * 2. User received payment confirmation today → Get Updated Credit Balance
 * 3. User has 2+ similar transaction amounts close in time → Check for Duplicate/Cancel Transaction
 */
@Service
public class IntentPredictionService {
    
    private final UserDataService userDataService;
    
    public IntentPredictionService(UserDataService userDataService) {
        this.userDataService = userDataService;
    }
    
    /**
     * Predict user intents based on their context
     * 
     * @param userId User identifier (if null, uses random scenario)
     * @return List of predicted intents ordered by priority
     */
    public List<IntentPrediction> predictIntents(String userId) {
        UserContext userContext = userId != null ? 
            userDataService.getUserContext(userId) : 
            userDataService.getRandomUserScenario();
            
        List<IntentPrediction> predictions = new ArrayList<>();
        
        // Check for overdue payment (Highest Priority)
        IntentPrediction overdueIntent = checkOverduePayment(userContext);
        if (overdueIntent != null) {
            predictions.add(overdueIntent);
        }
        
        // Check for recent payment confirmation (Medium Priority)
        IntentPrediction recentPaymentIntent = checkRecentPaymentConfirmation(userContext);
        if (recentPaymentIntent != null) {
            predictions.add(recentPaymentIntent);
        }
        
        // Check for duplicate transactions (Medium Priority)
        IntentPrediction duplicateTransactionIntent = checkDuplicateTransactions(userContext);
        if (duplicateTransactionIntent != null) {
            predictions.add(duplicateTransactionIntent);
        }
        
        // Sort by priority (HIGH first, then MEDIUM, then LOW)
        predictions.sort((a, b) -> {
            Map<String, Integer> priorityOrder = Map.of("HIGH", 3, "MEDIUM", 2, "LOW", 1);
            return priorityOrder.get(b.getPriority()).compareTo(priorityOrder.get(a.getPriority()));
        });
        
        return predictions;
    }
    
    /**
     * Check if user has overdue payment
     * Scenario: User is past due date → Get Payment Amount
     */
    private IntentPrediction checkOverduePayment(UserContext userContext) {
        if (userContext.getDueDate() != null && 
            userContext.getDueDate().isBefore(LocalDate.now()) &&
            userContext.getOutstandingBalance().compareTo(BigDecimal.ZERO) > 0) {
            
            long daysOverdue = ChronoUnit.DAYS.between(userContext.getDueDate(), LocalDate.now());
            
            return IntentPrediction.builder()
                .intentId("OVERDUE_PAYMENT")
                .category("PAYMENT")
                .predictedIntent("Get Payment Amount")
                .suggestedMessage(String.format(
                    "Looks like your payment is overdue by %d day%s. Would you like to check your current outstanding balance?",
                    daysOverdue, daysOverdue > 1 ? "s" : ""
                ))
                .confidence(0.95)
                .priority("HIGH")
                .triggerContext("User is past due date")
                .suggestedActions(new String[]{"Check Balance", "Make Payment", "View Due Date"})
                .timestamp(LocalDateTime.now())
                .showAfterGreeting(true)
                .build();
        }
        return null;
    }
    
    /**
     * Check if user received payment confirmation today
     * Scenario: User received payment confirmation today → Get Updated Credit Balance
     */
    private IntentPrediction checkRecentPaymentConfirmation(UserContext userContext) {
        if (userContext.getLastPaymentConfirmation() != null) {
            LocalDateTime today = LocalDateTime.now();
            LocalDateTime paymentDate = userContext.getLastPaymentConfirmation();
            
            // Check if payment was confirmed today (within last 24 hours)
            if (ChronoUnit.HOURS.between(paymentDate, today) <= 24) {
                return IntentPrediction.builder()
                    .intentId("RECENT_PAYMENT")
                    .category("BALANCE")
                    .predictedIntent("Get Updated Credit Balance")
                    .suggestedMessage("I see you made a payment recently. Would you like to check your updated available credit balance?")
                    .confidence(0.85)
                    .priority("MEDIUM")
                    .triggerContext("User received payment confirmation today")
                    .suggestedActions(new String[]{"Check Available Credit", "View Payment History", "Account Summary"})
                    .timestamp(LocalDateTime.now())
                    .showAfterGreeting(true)
                    .build();
            }
        }
        return null;
    }
    
    /**
     * Check for duplicate or similar transactions
     * Scenario: User has 2+ similar transaction amounts close in time → Check for Duplicate/Cancel Transaction
     */
    private IntentPrediction checkDuplicateTransactions(UserContext userContext) {
        if (userContext.getRecentTransactions() == null || userContext.getRecentTransactions().isEmpty()) {
            return null;
        }
        
        // Group transactions by amount within the last 2 hours
        LocalDateTime twoHoursAgo = LocalDateTime.now().minusHours(2);
        
        Map<BigDecimal, List<UserContext.TransactionData>> transactionsByAmount = 
            userContext.getRecentTransactions().stream()
                .filter(txn -> txn.getTimestamp().isAfter(twoHoursAgo))
                .filter(txn -> "PURCHASE".equals(txn.getType()))
                .collect(Collectors.groupingBy(UserContext.TransactionData::getAmount));
        
        // Check for amounts that appear 2 or more times
        for (Map.Entry<BigDecimal, List<UserContext.TransactionData>> entry : transactionsByAmount.entrySet()) {
            if (entry.getValue().size() >= 2) {
                BigDecimal amount = entry.getKey();
                List<UserContext.TransactionData> duplicates = entry.getValue();
                
                // Check if transactions are close in time (within 30 minutes of each other)
                for (int i = 0; i < duplicates.size() - 1; i++) {
                    LocalDateTime first = duplicates.get(i).getTimestamp();
                    LocalDateTime second = duplicates.get(i + 1).getTimestamp();
                    
                    if (ChronoUnit.MINUTES.between(first, second) <= 30) {
                        return IntentPrediction.builder()
                            .intentId("DUPLICATE_TRANSACTION")
                            .category("TRANSACTION")
                            .predictedIntent("Check for Duplicate/Cancel Transaction")
                            .suggestedMessage(String.format(
                                "I noticed you have similar transactions of %,.2f THB within a short time. Would you like to check if this might be a duplicate charge?",
                                amount
                            ))
                            .confidence(0.80)
                            .priority("MEDIUM")
                            .triggerContext("User has 2+ similar transaction amounts close in time")
                            .suggestedActions(new String[]{"Review Transactions", "Report Duplicate", "Cancel Transaction"})
                            .timestamp(LocalDateTime.now())
                            .showAfterGreeting(true)
                            .build();
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * Get the highest priority intent prediction for immediate display
     * 
     * @param userId User identifier
     * @return The most important intent prediction, or null if none
     */
    public IntentPrediction getTopPriorityIntent(String userId) {
        List<IntentPrediction> predictions = predictIntents(userId);
        return predictions.isEmpty() ? null : predictions.get(0);
    }
}
