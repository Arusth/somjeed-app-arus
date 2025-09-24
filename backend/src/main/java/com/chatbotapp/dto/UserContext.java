package com.chatbotapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * UserContext DTO containing user-specific data for intent prediction
 * Includes payment history, transaction data, and account information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserContext {
    
    /**
     * User identifier
     */
    private String userId;
    
    /**
     * Current outstanding balance
     */
    private BigDecimal outstandingBalance;
    
    /**
     * Payment due date
     */
    private LocalDate dueDate;
    
    /**
     * Available credit balance
     */
    private BigDecimal availableCredit;
    
    /**
     * Total credit limit
     */
    private BigDecimal creditLimit;
    
    /**
     * Recent transactions (last 30 days)
     */
    private List<TransactionData> recentTransactions;
    
    /**
     * Last payment confirmation date
     */
    private LocalDateTime lastPaymentConfirmation;
    
    /**
     * Account status (ACTIVE, OVERDUE, SUSPENDED)
     */
    private String accountStatus;
    
    /**
     * TransactionData nested class
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionData {
        private String transactionId;
        private BigDecimal amount;
        private LocalDateTime timestamp;
        private String type; // PAYMENT, PURCHASE, REFUND
        private String status; // PENDING, COMPLETED, FAILED
        private String description;
    }
}
