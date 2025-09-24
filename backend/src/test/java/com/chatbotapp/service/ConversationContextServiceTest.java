package com.chatbotapp.service;

import com.chatbotapp.dto.ConversationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ConversationContextService
 * Tests conversation context storage, retrieval, and expiration logic
 */
class ConversationContextServiceTest {

    private ConversationContextService conversationContextService;

    @BeforeEach
    void setUp() {
        conversationContextService = new ConversationContextService();
    }

    @Test
    @DisplayName("Should store and retrieve conversation context")
    void shouldStoreAndRetrieveConversationContext() {
        // Arrange
        String userId = "test-user-123";
        String action = ConversationContext.PAYMENT_CONFIRMATION;
        String intentId = "PAYMENT_INQUIRY";
        String contextData = "payment_setup";

        // Act
        conversationContextService.setContext(userId, action, intentId, contextData);
        ConversationContext retrievedContext = conversationContextService.getContext(userId);

        // Assert
        assertThat(retrievedContext).isNotNull();
        assertThat(retrievedContext.getUserId()).isEqualTo(userId);
        assertThat(retrievedContext.getLastAction()).isEqualTo(action);
        assertThat(retrievedContext.getLastIntentId()).isEqualTo(intentId);
        assertThat(retrievedContext.getContextData()).isEqualTo(contextData);
        assertThat(retrievedContext.getExpectedResponses()).contains("yes", "no", "ok", "sure", "cancel", "maybe");
        assertThat(retrievedContext.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Should clear conversation context")
    void shouldClearConversationContext() {
        // Arrange
        String userId = "test-user-123";
        conversationContextService.setContext(userId, ConversationContext.PAYMENT_CONFIRMATION, "PAYMENT_INQUIRY", "payment_setup");

        // Act
        conversationContextService.clearContext(userId);
        ConversationContext retrievedContext = conversationContextService.getContext(userId);

        // Assert
        assertThat(retrievedContext).isNull();
    }

    @Test
    @DisplayName("Should return null for non-existent user context")
    void shouldReturnNullForNonExistentUserContext() {
        // Act
        ConversationContext context = conversationContextService.getContext("non-existent-user");

        // Assert
        assertThat(context).isNull();
    }

    @Test
    @DisplayName("Should identify simple responses correctly")
    void shouldIdentifySimpleResponsesCorrectly() {
        // Test positive cases
        assertThat(conversationContextService.isSimpleResponse("yes")).isTrue();
        assertThat(conversationContextService.isSimpleResponse("YES")).isTrue();
        assertThat(conversationContextService.isSimpleResponse("no")).isTrue();
        assertThat(conversationContextService.isSimpleResponse("No")).isTrue();
        assertThat(conversationContextService.isSimpleResponse("ok")).isTrue();
        assertThat(conversationContextService.isSimpleResponse("OK")).isTrue();
        assertThat(conversationContextService.isSimpleResponse("sure")).isTrue();
        assertThat(conversationContextService.isSimpleResponse("cancel")).isTrue();
        assertThat(conversationContextService.isSimpleResponse("maybe")).isTrue();
        assertThat(conversationContextService.isSimpleResponse("yep")).isTrue();
        assertThat(conversationContextService.isSimpleResponse("yeah")).isTrue();
        assertThat(conversationContextService.isSimpleResponse("okay")).isTrue();
        assertThat(conversationContextService.isSimpleResponse("nope")).isTrue();

        // Test negative cases
        assertThat(conversationContextService.isSimpleResponse("I want to check my balance")).isFalse();
        assertThat(conversationContextService.isSimpleResponse("What are my payment options?")).isFalse();
        assertThat(conversationContextService.isSimpleResponse("")).isFalse();
        assertThat(conversationContextService.isSimpleResponse("   ")).isFalse();
    }

    @Test
    @DisplayName("Should identify positive responses correctly")
    void shouldIdentifyPositiveResponsesCorrectly() {
        // Test positive cases
        assertThat(conversationContextService.isPositiveResponse("yes")).isTrue();
        assertThat(conversationContextService.isPositiveResponse("YES")).isTrue();
        assertThat(conversationContextService.isPositiveResponse("ok")).isTrue();
        assertThat(conversationContextService.isPositiveResponse("OK")).isTrue();
        assertThat(conversationContextService.isPositiveResponse("sure")).isTrue();
        assertThat(conversationContextService.isPositiveResponse("SURE")).isTrue();
        assertThat(conversationContextService.isPositiveResponse("yep")).isTrue();
        assertThat(conversationContextService.isPositiveResponse("yeah")).isTrue();
        assertThat(conversationContextService.isPositiveResponse("okay")).isTrue();

        // Test negative cases
        assertThat(conversationContextService.isPositiveResponse("no")).isFalse();
        assertThat(conversationContextService.isPositiveResponse("cancel")).isFalse();
        assertThat(conversationContextService.isPositiveResponse("maybe")).isFalse();
        assertThat(conversationContextService.isPositiveResponse("")).isFalse();
    }

    @Test
    @DisplayName("Should identify negative responses correctly")
    void shouldIdentifyNegativeResponsesCorrectly() {
        // Test positive cases
        assertThat(conversationContextService.isNegativeResponse("no")).isTrue();
        assertThat(conversationContextService.isNegativeResponse("NO")).isTrue();
        assertThat(conversationContextService.isNegativeResponse("cancel")).isTrue();
        assertThat(conversationContextService.isNegativeResponse("CANCEL")).isTrue();
        assertThat(conversationContextService.isNegativeResponse("nope")).isTrue();
        assertThat(conversationContextService.isNegativeResponse("not now")).isTrue();
        assertThat(conversationContextService.isNegativeResponse("later")).isTrue();
        assertThat(conversationContextService.isNegativeResponse("maybe")).isTrue();
        assertThat(conversationContextService.isNegativeResponse("maybe later")).isTrue();
        assertThat(conversationContextService.isNegativeResponse("not sure")).isTrue();

        // Test negative cases
        assertThat(conversationContextService.isNegativeResponse("yes")).isFalse();
        assertThat(conversationContextService.isNegativeResponse("ok")).isFalse();
        assertThat(conversationContextService.isNegativeResponse("sure")).isFalse();
        assertThat(conversationContextService.isNegativeResponse("")).isFalse();
    }

    @Test
    @DisplayName("Should overwrite existing context for same user")
    void shouldOverwriteExistingContextForSameUser() {
        // Arrange
        String userId = "test-user-123";
        
        // Set initial context
        conversationContextService.setContext(userId, ConversationContext.PAYMENT_CONFIRMATION, "PAYMENT_INQUIRY", "payment_setup");
        
        // Act - Set new context for same user
        conversationContextService.setContext(userId, ConversationContext.CREDIT_LIMIT_REQUEST, "CREDIT_LIMIT", "limit_increase");
        ConversationContext retrievedContext = conversationContextService.getContext(userId);

        // Assert
        assertThat(retrievedContext).isNotNull();
        assertThat(retrievedContext.getLastAction()).isEqualTo(ConversationContext.CREDIT_LIMIT_REQUEST);
        assertThat(retrievedContext.getLastIntentId()).isEqualTo("CREDIT_LIMIT");
        assertThat(retrievedContext.getContextData()).isEqualTo("limit_increase");
    }

    @Test
    @DisplayName("Should handle multiple users independently")
    void shouldHandleMultipleUsersIndependently() {
        // Arrange
        String user1 = "user-1";
        String user2 = "user-2";

        // Act
        conversationContextService.setContext(user1, ConversationContext.PAYMENT_CONFIRMATION, "PAYMENT_INQUIRY", "payment_setup");
        conversationContextService.setContext(user2, ConversationContext.CREDIT_LIMIT_REQUEST, "CREDIT_LIMIT", "limit_increase");

        ConversationContext context1 = conversationContextService.getContext(user1);
        ConversationContext context2 = conversationContextService.getContext(user2);

        // Assert
        assertThat(context1).isNotNull();
        assertThat(context1.getLastAction()).isEqualTo(ConversationContext.PAYMENT_CONFIRMATION);
        
        assertThat(context2).isNotNull();
        assertThat(context2.getLastAction()).isEqualTo(ConversationContext.CREDIT_LIMIT_REQUEST);
        
        // Contexts should be independent
        assertThat(context1.getLastAction()).isNotEqualTo(context2.getLastAction());
    }

    @Test
    @DisplayName("Should handle context clearing for specific user only")
    void shouldHandleContextClearingForSpecificUserOnly() {
        // Arrange
        String user1 = "user-1";
        String user2 = "user-2";

        conversationContextService.setContext(user1, ConversationContext.PAYMENT_CONFIRMATION, "PAYMENT_INQUIRY", "payment_setup");
        conversationContextService.setContext(user2, ConversationContext.CREDIT_LIMIT_REQUEST, "CREDIT_LIMIT", "limit_increase");

        // Act - Clear context for user1 only
        conversationContextService.clearContext(user1);

        ConversationContext context1 = conversationContextService.getContext(user1);
        ConversationContext context2 = conversationContextService.getContext(user2);

        // Assert
        assertThat(context1).isNull(); // Should be cleared
        assertThat(context2).isNotNull(); // Should still exist
        assertThat(context2.getLastAction()).isEqualTo(ConversationContext.CREDIT_LIMIT_REQUEST);
    }

    @Test
    @DisplayName("Should handle edge cases for response classification")
    void shouldHandleEdgeCasesForResponseClassification() {
        // Test whitespace handling
        assertThat(conversationContextService.isSimpleResponse("  yes  ")).isTrue();
        assertThat(conversationContextService.isPositiveResponse("  ok  ")).isTrue();
        assertThat(conversationContextService.isNegativeResponse("  no  ")).isTrue();

        // Test case insensitivity
        assertThat(conversationContextService.isSimpleResponse("YeS")).isTrue();
        assertThat(conversationContextService.isPositiveResponse("SuRe")).isTrue();
        assertThat(conversationContextService.isNegativeResponse("CaNcEl")).isTrue();

        // Test empty handling
        assertThat(conversationContextService.isSimpleResponse("")).isFalse();
        assertThat(conversationContextService.isPositiveResponse("")).isFalse();
        assertThat(conversationContextService.isNegativeResponse("")).isFalse();
    }

    @Test
    @DisplayName("Should validate context constants")
    void shouldValidateContextConstants() {
        // Test that context constants are properly defined
        assertThat(ConversationContext.PAYMENT_CONFIRMATION).isEqualTo("PAYMENT_CONFIRMATION");
        assertThat(ConversationContext.CREDIT_LIMIT_REQUEST).isEqualTo("CREDIT_LIMIT_REQUEST");
        assertThat(ConversationContext.DUPLICATE_REPORT_CONFIRMATION).isEqualTo("DUPLICATE_REPORT_CONFIRMATION");
        assertThat(ConversationContext.SECURITY_PHONE_CONFIRMATION).isEqualTo("SECURITY_PHONE_CONFIRMATION");
        assertThat(ConversationContext.FURTHER_ASSISTANCE).isEqualTo("FURTHER_ASSISTANCE");
    }
}
