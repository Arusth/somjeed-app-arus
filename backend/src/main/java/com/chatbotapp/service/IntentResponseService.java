package com.chatbotapp.service;

import com.chatbotapp.dto.UserContext;
import com.chatbotapp.dto.UserIntent;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * IntentResponseService generates contextual responses based on classified user intents
 * Provides specific responses and actions for each of the 8 main intent categories
 */
@Service
public class IntentResponseService {
    
    private final UserDataService userDataService;
    private final Random random = new Random();
    
    public IntentResponseService(UserDataService userDataService) {
        this.userDataService = userDataService;
    }
    
    /**
     * Generate response based on classified intent and user context
     * 
     * @param intent Classified user intent
     * @param userContext User's account context
     * @return Contextual response message
     */
    public String generateResponse(UserIntent intent, UserContext userContext) {
        switch (intent.getIntentId()) {
            case "PAYMENT_INQUIRY":
                return handlePaymentInquiry(intent, userContext);
            case "TRANSACTION_DISPUTE":
                return handleTransactionDispute(intent, userContext);
            case "CARD_MANAGEMENT":
                return handleCardManagement(intent, userContext);
            case "CREDIT_LIMIT":
                return handleCreditLimit(intent, userContext);
            case "ACCOUNT_SECURITY":
                return handleAccountSecurity(intent, userContext);
            case "STATEMENT_INQUIRY":
                return handleStatementInquiry(intent, userContext);
            case "REWARD_POINTS":
                return handleRewardPoints(intent, userContext);
            case "TECHNICAL_SUPPORT":
                return handleTechnicalSupport(intent, userContext);
            case "UNRECOGNIZED_INQUIRY":
                return handleUnrecognizedInquiry(intent);
            case "GENERAL_INQUIRY":
                return handleGeneralInquiry(intent);
            default:
                return handleUnrecognizedInquiry(intent);
        }
    }
    
    /**
     * Handle payment and balance inquiries
     */
    private String handlePaymentInquiry(UserIntent intent, UserContext userContext) {
        if (userContext == null) {
            userContext = userDataService.getUserContext("user_overdue");
        }
        
        BigDecimal balance = userContext.getOutstandingBalance();
        LocalDate dueDate = userContext.getDueDate();
        
        if (balance.compareTo(BigDecimal.ZERO) > 0) {
            if (dueDate.isBefore(LocalDate.now())) {
                return String.format(
                    "Your current outstanding balance is %,.2f THB, and it was due on %s. " +
                    "To avoid additional late fees, I recommend making a payment as soon as possible. " +
                    "Would you like me to help you set up a payment?",
                    balance, dueDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
                );
            } else {
                return String.format(
                    "Your current outstanding balance is %,.2f THB, due on %s. " +
                    "The minimum payment required is %,.2f THB. Would you like to make a payment now?",
                    balance, dueDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")),
                    balance.multiply(new BigDecimal("0.05")) // 5% minimum payment
                );
            }
        } else {
            return "Great news! Your account has a zero balance. Your next statement will be generated on " +
                   LocalDate.now().plusDays(15).format(DateTimeFormatter.ofPattern("MMMM d, yyyy")) + ".";
        }
    }
    
    /**
     * Handle transaction disputes
     */
    private String handleTransactionDispute(UserIntent intent, UserContext userContext) {
        if (userContext == null) {
            userContext = userDataService.getUserContext("user_duplicate_transactions");
        }
        
        String merchant = extractMerchantFromIntent(intent);
        String amount = extractAmountFromIntent(intent);
        
        StringBuilder response = new StringBuilder();
        response.append("I understand you want to dispute a transaction. This is a serious matter and I'll help you immediately. ");
        
        if (merchant != null && amount != null) {
            response.append(String.format("I see you're referring to a charge from %s for %s THB. ", merchant, amount));
        }
        
        response.append("For your security, I'm temporarily blocking your card to prevent any additional unauthorized charges. ");
        response.append("I've initiated a dispute case (Reference: DSP-").append(System.currentTimeMillis() % 100000).append("). ");
        response.append("You'll receive a provisional credit within 2 business days while we investigate. ");
        response.append("A new card will be expedited to you within 24 hours. Is there anything else I can help secure?");
        
        return response.toString();
    }
    
    /**
     * Handle card management requests
     */
    private String handleCardManagement(UserIntent intent, UserContext userContext) {
        String action = extractActionFromIntent(intent);
        
        switch (action) {
            case "block":
                return "I've immediately blocked your card for security. No new transactions can be processed. " +
                       "A replacement card will be expedited to your registered address and should arrive within 1-2 business days. " +
                       "For urgent needs, you can visit any of our branches for a temporary card. " +
                       "Reference number: BLK-" + (System.currentTimeMillis() % 100000);
                       
            case "replace":
                return "I'll order a replacement card for you right away. Your current card will remain active until you receive " +
                       "and activate the new one. The new card will arrive at your registered address within 3-5 business days. " +
                       "Would you like to update your address or expedite delivery for an additional fee?";
                       
            case "activate":
                return "I can help you activate your new card. For security, I'll need to verify the last 4 digits of your new card " +
                       "and the 3-digit security code. Once activated, your old card will be automatically deactivated. " +
                       "Please have your new card ready.";
                       
            case "cancel":
                return "I understand you want to cancel your card. Before I proceed, I want to make sure this is what you need. " +
                       "Canceling will close your account permanently. If you're concerned about security, I can block the card instead. " +
                       "If you're sure about canceling, I'll need to transfer you to our retention specialist.";
                       
            default:
                return "I can help you with various card services including blocking, replacement, activation, or cancellation. " +
                       "What specific action would you like me to take with your card?";
        }
    }
    
    /**
     * Handle credit limit requests
     */
    private String handleCreditLimit(UserIntent intent, UserContext userContext) {
        if (userContext == null) {
            userContext = userDataService.getUserContext("user_normal");
        }
        
        BigDecimal currentLimit = userContext.getCreditLimit();
        BigDecimal availableCredit = userContext.getAvailableCredit();
        String requestedAmount = extractAmountFromIntent(intent);
        
        StringBuilder response = new StringBuilder();
        response.append(String.format("Your current credit limit is %,.2f THB with %,.2f THB available. ", 
                                    currentLimit, availableCredit));
        
        if (requestedAmount != null) {
            response.append(String.format("I see you're interested in increasing your limit to %s THB. ", requestedAmount));
        }
        
        response.append("Based on your account history and payment behavior, you may be eligible for a credit limit increase. ");
        response.append("I can submit a request for review, which typically takes 2-3 business days. ");
        response.append("The decision is based on your credit score, income, and payment history. ");
        response.append("Would you like me to submit this request for you?");
        
        return response.toString();
    }
    
    /**
     * Handle account security concerns
     */
    private String handleAccountSecurity(UserIntent intent, UserContext userContext) {
        return "🚨 SECURITY ALERT: I'm taking immediate action to protect your account. " +
               "I've temporarily locked your card and flagged your account for security review. " +
               "Our fraud team will contact you within 30 minutes at your registered phone number. " +
               "In the meantime, please do NOT share any account information with anyone. " +
               "If you receive any suspicious calls claiming to be from us, hang up and call our official number. " +
               "Your security reference number is: SEC-" + (System.currentTimeMillis() % 100000) + ". " +
               "Is your registered phone number still current?";
    }
    
    /**
     * Handle statement and transaction history requests
     */
    private String handleStatementInquiry(UserIntent intent, UserContext userContext) {
        String month = extractMonthFromIntent(intent);
        
        StringBuilder response = new StringBuilder();
        response.append("I can provide your statement and transaction history. ");
        
        if (month != null) {
            response.append(String.format("I see you're looking for %s's statement. ", month));
        }
        
        response.append("I can generate statements for the past 24 months.\n\n");
        response.append("Would you like me to:\n");
        response.append("• Email your latest statement\n");
        response.append("• Show recent transactions (last 30 days)\n");
        response.append("• Generate a custom date range report\n");
        response.append("• Set up automatic monthly statement delivery\n\n");
        response.append("Which option would be most helpful?");
        
        return response.toString();
    }
    
    /**
     * Handle reward points inquiries
     */
    private String handleRewardPoints(UserIntent intent, UserContext userContext) {
        // Mock reward points data
        int pointsBalance = 15750 + random.nextInt(10000);
        double cashValue = pointsBalance * 0.01; // 1 point = 0.01 THB
        
        return String.format(
            "Your current reward points balance is %,d points (worth approximately %.2f THB).\n\n" +
            "Here are your redemption options:\n" +
            "• Cash Back: Redeem points for statement credit\n" +
            "• Shopping: Use points at partner merchants\n" +
            "• Travel: Convert to airline miles or hotel points\n" +
            "• Gifts: Redeem from our rewards catalog\n\n" +
            "You've earned %d points this month from your purchases. " +
            "Would you like to redeem points or learn about earning more rewards?",
            pointsBalance, cashValue, 450 + random.nextInt(200)
        );
    }
    
    /**
     * Handle technical support requests
     */
    private String handleTechnicalSupport(UserIntent intent, UserContext userContext) {
        return "I'm sorry you're experiencing technical difficulties. Let me help you troubleshoot:\n\n" +
               "Quick fixes to try:\n" +
               "• Clear your browser cache or restart the mobile app\n" +
               "• Check your internet connection\n" +
               "• Try using a different browser or device\n" +
               "• Update the app to the latest version\n\n" +
               "If these don't work, I can:\n" +
               "• Reset your login credentials\n" +
               "• Guide you through step-by-step troubleshooting\n" +
               "• Connect you with our technical support team\n\n" +
               "What specific issue are you experiencing?";
    }
    
    /**
     * Handle unrecognized inquiries when bot doesn't understand
     */
    private String handleUnrecognizedInquiry(UserIntent intent) {
        return "I'm sorry, I didn't quite understand that. Let me help you with what I can do!\n\n" +
               "I can assist you with:\n" +
               "• Account & Payments: Check balance, due dates, payment options\n" +
               "• Security: Report fraud, block cards, dispute transactions\n" +
               "• Statements: Transaction history, monthly statements\n" +
               "• Credit: Limit increases, available credit\n" +
               "• Rewards: Points balance, redemption options\n" +
               "• Support: Technical issues, account access\n\n" +
               "Could you please rephrase your question or let me know which of these areas you'd like help with?";
    }
    
    /**
     * Handle general inquiries and fallback cases
     */
    private String handleGeneralInquiry(UserIntent intent) {
        return "I'm here to help with all your credit card needs! I can assist you with:\n\n" +
               "• Account & Payments: Balance, due dates, payment options\n" +
               "• Security: Report fraud, block cards, dispute transactions\n" +
               "• Statements: Transaction history, monthly statements\n" +
               "• Credit: Limit increases, available credit\n" +
               "• Rewards: Points balance, redemption options\n" +
               "• Support: Technical issues, account access\n\n" +
               "What would you like help with today?";
    }
    
    // Helper methods to extract entities from intents
    private String extractMerchantFromIntent(UserIntent intent) {
        return intent.getEntities().stream()
            .filter(e -> "MERCHANT".equals(e.getEntityType()))
            .map(UserIntent.IntentEntity::getEntityValue)
            .findFirst()
            .orElse(null);
    }
    
    private String extractAmountFromIntent(UserIntent intent) {
        return intent.getEntities().stream()
            .filter(e -> "AMOUNT".equals(e.getEntityType()) || "REQUESTED_AMOUNT".equals(e.getEntityType()))
            .map(UserIntent.IntentEntity::getEntityValue)
            .findFirst()
            .orElse(null);
    }
    
    private String extractActionFromIntent(UserIntent intent) {
        return intent.getEntities().stream()
            .filter(e -> "ACTION".equals(e.getEntityType()))
            .map(UserIntent.IntentEntity::getEntityValue)
            .findFirst()
            .orElse("manage");
    }
    
    private String extractMonthFromIntent(UserIntent intent) {
        return intent.getEntities().stream()
            .filter(e -> "MONTH".equals(e.getEntityType()))
            .map(UserIntent.IntentEntity::getEntityValue)
            .findFirst()
            .orElse(null);
    }
}
