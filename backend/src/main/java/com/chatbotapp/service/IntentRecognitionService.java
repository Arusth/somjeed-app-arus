package com.chatbotapp.service;

import com.chatbotapp.dto.UserContext;
import com.chatbotapp.dto.UserIntent;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * IntentRecognitionService classifies user messages into predefined intents
 * for credit card customer support using rule-based keyword matching
 * 
 * Supports 8 main intent categories:
 * 1. PAYMENT_INQUIRY - Balance, due dates, payment amounts
 * 2. TRANSACTION_DISPUTE - Unauthorized or incorrect charges
 * 3. CARD_MANAGEMENT - Block, replace, activate cards
 * 4. CREDIT_LIMIT - Increase requests, limit inquiries
 * 5. ACCOUNT_SECURITY - Fraud alerts, suspicious activity
 * 6. STATEMENT_INQUIRY - Statement requests, transaction history
 * 7. REWARD_POINTS - Points balance, redemption
 * 8. TECHNICAL_SUPPORT - App issues, login problems
 */
@Service
public class IntentRecognitionService {
    
    private final UserDataService userDataService;
    
    public IntentRecognitionService(UserDataService userDataService) {
        this.userDataService = userDataService;
    }
    
    /**
     * Classify user message into intent with confidence score
     * 
     * @param userMessage User's message
     * @param userContext User's account context (optional)
     * @return UserIntent with classification and confidence
     */
    public UserIntent classifyIntent(String userMessage, UserContext userContext) {
        String message = userMessage.toLowerCase().trim();
        
        // Enhance user context if not provided or incomplete
        UserContext enhancedContext = enhanceUserContext(userContext);
        
        // Try each intent classifier in order of specificity (most specific first)
        UserIntent intent = classifyRewardPoints(message, enhancedContext);
        if (intent != null) return intent;
        
        intent = classifyTransactionDispute(message, enhancedContext);
        if (intent != null) return intent;
        
        intent = classifyCardManagement(message, enhancedContext);
        if (intent != null) return intent;
        
        intent = classifyCreditLimit(message, enhancedContext);
        if (intent != null) return intent;
        
        intent = classifyAccountSecurity(message, enhancedContext);
        if (intent != null) return intent;
        
        intent = classifyStatementInquiry(message, enhancedContext);
        if (intent != null) return intent;
        
        intent = classifyPaymentInquiry(message, enhancedContext);
        if (intent != null) return intent;
        
        intent = classifyTechnicalSupport(message, enhancedContext);
        if (intent != null) return intent;
        
        // Default fallback intent
        return createFallbackIntent(message);
    }
    
    /**
     * Enhance user context using UserDataService if context is missing or incomplete
     * 
     * @param userContext Existing user context (may be null or incomplete)
     * @return Enhanced UserContext with additional data from UserDataService
     */
    private UserContext enhanceUserContext(UserContext userContext) {
        if (userContext == null) {
            // No context provided, get a random scenario for demonstration
            return userDataService.getRandomUserScenario();
        }
        
        if (userContext.getUserId() != null) {
            // Try to get more complete context using the user ID
            UserContext fullContext = userDataService.getUserContext(userContext.getUserId());
            if (fullContext != null) {
                return fullContext;
            }
        }
        
        // Return original context if no enhancement is possible
        return userContext;
    }
    
    /**
     * Intent 1: PAYMENT_INQUIRY - Balance, due dates, payment amounts
     */
    private UserIntent classifyPaymentInquiry(String message, UserContext userContext) {
        String[] keywords = {"payment", "due date", "amount due", "outstanding balance", 
                           "minimum payment", "pay", "owe", "bill", "account balance", 
                           "check balance", "my balance", "current balance"};
        
        if (containsKeywords(message, keywords)) {
            List<UserIntent.IntentEntity> entities = extractPaymentEntities(message);
            
            // Enhance confidence and context based on user data
            double confidence = 0.9;
            String contextInfo = "General payment inquiry";
            String responseTemplate = "I can help you with your payment information. Let me check your account details.";
            
            if (userContext != null) {
                // Increase confidence if user has payment-related context
                if ("OVERDUE".equals(userContext.getAccountStatus())) {
                    confidence = 0.95;
                    contextInfo = "User account is overdue - urgent payment needed";
                    responseTemplate = "I see your account is overdue. Let me help you with your payment immediately.";
                } else if (userContext.getOutstandingBalance() != null && 
                          userContext.getOutstandingBalance().compareTo(BigDecimal.ZERO) > 0) {
                    confidence = 0.92;
                    contextInfo = "User has outstanding balance of " + userContext.getOutstandingBalance();
                    responseTemplate = "I can help you with your current balance and payment options.";
                }
                
                // Add balance entity if available
                if (userContext.getOutstandingBalance() != null) {
                    entities.add(UserIntent.IntentEntity.builder()
                        .entityType("CURRENT_BALANCE")
                        .entityValue(userContext.getOutstandingBalance().toString())
                        .confidence(1.0)
                        .build());
                }
            }
            
            return UserIntent.builder()
                .intentId("PAYMENT_INQUIRY")
                .category("PAYMENT")
                .intentName("Payment and Balance Inquiry")
                .confidence(confidence)
                .entities(entities)
                .context(contextInfo)
                .responseTemplate(responseTemplate)
                .followUpActions(Arrays.asList("Show current balance", "Show due date", "Payment options"))
                .timestamp(LocalDateTime.now())
                .build();
        }
        return null;
    }
    
    /**
     * Intent 2: TRANSACTION_DISPUTE - Unauthorized or incorrect charges
     */
    private UserIntent classifyTransactionDispute(String message, UserContext userContext) {
        String[] keywords = {"dispute", "unauthorized", "fraud", "wrong charge", "didn't make", 
                           "unknown transaction", "suspicious", "report", "charge back"};
        
        if (containsKeywords(message, keywords)) {
            List<UserIntent.IntentEntity> entities = extractTransactionEntities(message);
            
            // Enhance confidence and context based on user transaction history
            double confidence = 0.95;
            String contextInfo = "Potential fraudulent activity";
            String responseTemplate = "I understand you want to dispute a transaction. Let me help you with that immediately.";
            
            if (userContext != null && userContext.getRecentTransactions() != null) {
                // Check for duplicate transactions (potential dispute scenario)
                boolean hasDuplicateTransactions = checkForDuplicateTransactions(userContext);
                if (hasDuplicateTransactions) {
                    confidence = 0.98;
                    contextInfo = "User has duplicate transactions - likely dispute case";
                    responseTemplate = "I see you have similar transactions. Let me help you identify and dispute any unauthorized charges.";
                }
                
                // Add recent transaction entities for context
                userContext.getRecentTransactions().stream()
                    .limit(3) // Include up to 3 recent transactions
                    .forEach(transaction -> {
                        entities.add(UserIntent.IntentEntity.builder()
                            .entityType("RECENT_TRANSACTION")
                            .entityValue(transaction.getDescription() + " - " + transaction.getAmount())
                            .confidence(0.9)
                            .build());
                    });
            }
            
            return UserIntent.builder()
                .intentId("TRANSACTION_DISPUTE")
                .category("TRANSACTION")
                .intentName("Transaction Dispute")
                .confidence(confidence)
                .entities(entities)
                .context(contextInfo)
                .responseTemplate(responseTemplate)
                .followUpActions(Arrays.asList("Identify transaction", "Block card if needed", "File dispute"))
                .timestamp(LocalDateTime.now())
                .build();
        }
        return null;
    }
    
    /**
     * Intent 3: CARD_MANAGEMENT - Block, replace, activate cards
     */
    private UserIntent classifyCardManagement(String message, UserContext userContext) {
        String[] keywords = {"block card", "lost card", "stolen", "replace card", "new card", 
                           "activate", "deactivate", "cancel card", "card not working"};
        
        if (containsKeywords(message, keywords)) {
            String action = determineCardAction(message);
            
            return UserIntent.builder()
                .intentId("CARD_MANAGEMENT")
                .category("ACCOUNT")
                .intentName("Card Management")
                .confidence(0.92)
                .entities(Arrays.asList(
                    UserIntent.IntentEntity.builder()
                        .entityType("ACTION")
                        .entityValue(action)
                        .confidence(0.9)
                        .build()
                ))
                .context("Card security or replacement needed")
                .responseTemplate("I'll help you with your card " + action + ". For security, I need to verify your identity first.")
                .followUpActions(Arrays.asList("Verify identity", "Process card action", "Provide timeline"))
                .timestamp(LocalDateTime.now())
                .build();
        }
        return null;
    }
    
    /**
     * Intent 4: CREDIT_LIMIT - Increase requests, limit inquiries
     */
    private UserIntent classifyCreditLimit(String message, UserContext userContext) {
        String[] keywords = {"credit limit", "increase limit", "raise limit", "available credit", 
                           "credit line", "spending limit", "limit increase"};
        
        if (containsKeywords(message, keywords)) {
            List<UserIntent.IntentEntity> entities = extractAmountEntities(message);
            
            return UserIntent.builder()
                .intentId("CREDIT_LIMIT")
                .category("ACCOUNT")
                .intentName("Credit Limit Inquiry")
                .confidence(0.88)
                .entities(entities)
                .context("Credit limit modification request")
                .responseTemplate("I can help you with your credit limit. Let me review your account eligibility.")
                .followUpActions(Arrays.asList("Check eligibility", "Show current limit", "Process request"))
                .timestamp(LocalDateTime.now())
                .build();
        }
        return null;
    }
    
    /**
     * Intent 5: ACCOUNT_SECURITY - Fraud alerts, suspicious activity
     */
    private UserIntent classifyAccountSecurity(String message, UserContext userContext) {
        String[] keywords = {"fraud alert", "security", "suspicious activity", "hacked", 
                           "compromised", "unusual activity", "security breach", "protect account"};
        
        if (containsKeywords(message, keywords)) {
            return UserIntent.builder()
                .intentId("ACCOUNT_SECURITY")
                .category("SECURITY")
                .intentName("Account Security Concern")
                .confidence(0.96)
                .entities(new ArrayList<>())
                .context("Security threat detected")
                .responseTemplate("I take security very seriously. Let me immediately secure your account and investigate.")
                .followUpActions(Arrays.asList("Secure account", "Review recent activity", "Update security"))
                .timestamp(LocalDateTime.now())
                .build();
        }
        return null;
    }
    
    /**
     * Intent 6: STATEMENT_INQUIRY - Statement requests, transaction history
     */
    private UserIntent classifyStatementInquiry(String message, UserContext userContext) {
        String[] keywords = {"statement", "transaction history", "monthly statement", "download", 
                           "transactions", "activity", "history", "past purchases"};
        
        if (containsKeywords(message, keywords)) {
            List<UserIntent.IntentEntity> entities = extractDateEntities(message);
            
            return UserIntent.builder()
                .intentId("STATEMENT_INQUIRY")
                .category("ACCOUNT")
                .intentName("Statement and History Request")
                .confidence(0.85)
                .entities(entities)
                .context("Statement or transaction history needed")
                .responseTemplate("I can provide your statement and transaction history. What time period do you need?")
                .followUpActions(Arrays.asList("Specify date range", "Generate statement", "Send via email"))
                .timestamp(LocalDateTime.now())
                .build();
        }
        return null;
    }
    
    /**
     * Intent 7: REWARD_POINTS - Points balance, redemption
     */
    private UserIntent classifyRewardPoints(String message, UserContext userContext) {
        String[] keywords = {"points", "rewards", "cashback", "redeem", "points balance", 
                           "reward program", "miles", "loyalty"};
        
        if (containsKeywords(message, keywords)) {
            return UserIntent.builder()
                .intentId("REWARD_POINTS")
                .category("REWARDS")
                .intentName("Reward Points Inquiry")
                .confidence(0.83)
                .entities(new ArrayList<>())
                .context("Rewards program inquiry")
                .responseTemplate("I can help you with your reward points and redemption options.")
                .followUpActions(Arrays.asList("Show points balance", "Redemption options", "Points history"))
                .timestamp(LocalDateTime.now())
                .build();
        }
        return null;
    }
    
    /**
     * Intent 8: TECHNICAL_SUPPORT - App issues, login problems
     */
    private UserIntent classifyTechnicalSupport(String message, UserContext userContext) {
        String[] keywords = {"app not working", "login", "password", "technical issue", "website", 
                           "mobile app", "can't access", "error", "bug", "system down"};
        
        if (containsKeywords(message, keywords)) {
            return UserIntent.builder()
                .intentId("TECHNICAL_SUPPORT")
                .category("SUPPORT")
                .intentName("Technical Support")
                .confidence(0.80)
                .entities(new ArrayList<>())
                .context("Technical assistance needed")
                .responseTemplate("I'll help you resolve this technical issue. Can you describe what's happening?")
                .followUpActions(Arrays.asList("Troubleshoot issue", "Reset credentials", "Escalate if needed"))
                .timestamp(LocalDateTime.now())
                .build();
        }
        return null;
    }
    
    /**
     * Helper method to check if message contains any of the keywords
     */
    private boolean containsKeywords(String message, String[] keywords) {
        for (String keyword : keywords) {
            if (message.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Extract payment-related entities (amounts, dates)
     */
    private List<UserIntent.IntentEntity> extractPaymentEntities(String message) {
        List<UserIntent.IntentEntity> entities = new ArrayList<>();
        
        // Extract amounts (e.g., "1000", "1,000.00")
        Pattern amountPattern = Pattern.compile("\\b\\d{1,3}(?:,\\d{3})*(?:\\.\\d{2})?\\b");
        Matcher amountMatcher = amountPattern.matcher(message);
        if (amountMatcher.find()) {
            entities.add(UserIntent.IntentEntity.builder()
                .entityType("AMOUNT")
                .entityValue(amountMatcher.group())
                .confidence(0.9)
                .build());
        }
        
        return entities;
    }
    
    /**
     * Extract transaction-related entities
     */
    private List<UserIntent.IntentEntity> extractTransactionEntities(String message) {
        List<UserIntent.IntentEntity> entities = new ArrayList<>();
        
        // Extract merchant names (simple approach - words after "at" or "from")
        Pattern merchantPattern = Pattern.compile("(?:at|from)\\s+([A-Za-z\\s]+)");
        Matcher merchantMatcher = merchantPattern.matcher(message);
        if (merchantMatcher.find()) {
            entities.add(UserIntent.IntentEntity.builder()
                .entityType("MERCHANT")
                .entityValue(merchantMatcher.group(1).trim())
                .confidence(0.8)
                .build());
        }
        
        return entities;
    }
    
    /**
     * Extract amount entities for credit limit requests
     */
    private List<UserIntent.IntentEntity> extractAmountEntities(String message) {
        List<UserIntent.IntentEntity> entities = new ArrayList<>();
        
        Pattern amountPattern = Pattern.compile("\\b\\d{1,3}(?:,\\d{3})*\\b");
        Matcher amountMatcher = amountPattern.matcher(message);
        if (amountMatcher.find()) {
            entities.add(UserIntent.IntentEntity.builder()
                .entityType("REQUESTED_AMOUNT")
                .entityValue(amountMatcher.group())
                .confidence(0.85)
                .build());
        }
        
        return entities;
    }
    
    /**
     * Extract date entities for statement requests
     */
    private List<UserIntent.IntentEntity> extractDateEntities(String message) {
        List<UserIntent.IntentEntity> entities = new ArrayList<>();
        
        // Simple date extraction (month names)
        String[] months = {"january", "february", "march", "april", "may", "june",
                          "july", "august", "september", "october", "november", "december"};
        
        for (String month : months) {
            if (message.contains(month)) {
                entities.add(UserIntent.IntentEntity.builder()
                    .entityType("MONTH")
                    .entityValue(month)
                    .confidence(0.9)
                    .build());
                break;
            }
        }
        
        return entities;
    }
    
    /**
     * Determine specific card action from message
     */
    private String determineCardAction(String message) {
        if (message.contains("block") || message.contains("lost") || message.contains("stolen")) {
            return "block";
        } else if (message.contains("replace") || message.contains("new card")) {
            return "replace";
        } else if (message.contains("activate")) {
            return "activate";
        } else if (message.contains("cancel")) {
            return "cancel";
        }
        return "manage";
    }
    
    /**
     * Create fallback intent for unrecognized messages
     */
    private UserIntent createFallbackIntent(String message) {
        return UserIntent.builder()
            .intentId("UNRECOGNIZED_INQUIRY")
            .category("SUPPORT")
            .intentName("Unrecognized Inquiry")
            .confidence(0.3)
            .entities(new ArrayList<>())
            .context("User message not understood")
            .responseTemplate("I'm sorry, I didn't understand that. Let me show you what I can help with.")
            .followUpActions(Arrays.asList("Show available services", "Ask for clarification"))
            .timestamp(LocalDateTime.now())
            .build();
    }
    
    /**
     * Check if user has duplicate transactions that might indicate a dispute scenario
     * 
     * @param userContext User context with transaction history
     * @return true if duplicate transactions are found
     */
    private boolean checkForDuplicateTransactions(UserContext userContext) {
        if (userContext.getRecentTransactions() == null || userContext.getRecentTransactions().size() < 2) {
            return false;
        }
        
        // Check for transactions with same amount and description within a short time window
        for (int i = 0; i < userContext.getRecentTransactions().size(); i++) {
            for (int j = i + 1; j < userContext.getRecentTransactions().size(); j++) {
                UserContext.TransactionData tx1 = userContext.getRecentTransactions().get(i);
                UserContext.TransactionData tx2 = userContext.getRecentTransactions().get(j);
                
                // Check if transactions have same amount and similar description
                if (tx1.getAmount().equals(tx2.getAmount()) && 
                    tx1.getDescription().equalsIgnoreCase(tx2.getDescription())) {
                    
                    // Check if they occurred within 30 minutes of each other
                    long timeDifferenceMinutes = Math.abs(
                        java.time.Duration.between(tx1.getTimestamp(), tx2.getTimestamp()).toMinutes()
                    );
                    
                    if (timeDifferenceMinutes <= 30) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
}
