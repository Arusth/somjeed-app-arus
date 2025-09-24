'use client'

import { useState, useRef, useEffect, useCallback } from 'react';
import { chatService } from '@/services/chatService';
import MessageBubble from './MessageBubble';
import MessageInput from './MessageInput';
import LoadingIndicator from './LoadingIndicator';
import GreetingMessage from './GreetingMessage';
import IntentPredictionBubble from './IntentPredictionBubble';
import FeedbackModal from './FeedbackModal';
import useSilenceTracker from '@/hooks/useSilenceTracker';
import type { 
  ChatMessage, 
  BotProfile, 
  IntentPrediction, 
  ConversationClosureResponse,
  FeedbackRequest 
} from '@/types/chat';

interface Message {
  id: string;
  text: string;
  sender: 'user' | 'bot';
  timestamp: Date;
}

export default function ChatInterface() {
  const [messages, setMessages] = useState<Message[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [showGreeting, setShowGreeting] = useState(true);
  const [intentPredictions, setIntentPredictions] = useState<IntentPrediction[]>([]);
  const [showIntentPredictions, setShowIntentPredictions] = useState(false);
  const [showFeedbackModal, setShowFeedbackModal] = useState(false);
  const [conversationStartedAt] = useState(new Date());
  const [sessionId, setSessionId] = useState<string>('');
  const [isClient, setIsClient] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const hasUserStartedChatting = useRef<boolean>(false);

  // Generate sessionId only on client side to avoid hydration mismatch
  useEffect(() => {
    setSessionId(`session_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`);
    setIsClient(true);
  }, []);

  // Bot profile configuration
  const botProfile: BotProfile = {
    name: 'Somjeed',
    avatar: '🤖',
    color: '#22c55e'
  };

  const scrollToBottom = useCallback(() => {
    setTimeout(() => {
      if (messagesEndRef.current) {
        messagesEndRef.current.scrollIntoView({ 
          behavior: 'smooth',
          block: 'end',
          inline: 'nearest'
        });
      }
    }, 100);
  }, []);

  // Handle silence-triggered actions (defined first to avoid circular dependency)
  const handleSilenceAction = useCallback((response: ConversationClosureResponse) => {
    const botMessage: Message = {
      id: `silence_${Date.now()}`,
      text: response.message,
      sender: 'bot',
      timestamp: new Date()
    };

    setMessages(prev => [...prev, botMessage]);
    
    if (response.shouldRequestFeedback) {
      // Show feedback modal after a short delay
      setTimeout(() => {
        setShowFeedbackModal(true);
      }, 2000);
    }
    
    scrollToBottom();
  }, [scrollToBottom]);

  // Silence tracking for conversation closure (only when client-side and sessionId is ready)
  const { silenceTracker, resetActivity, pauseTracking, resumeTracking } = useSilenceTracker({
    sessionId,
    userId: 'user_overdue', // This should come from user context
    conversationTopic: 'payment_inquiry',
    messageCount: messages.length,
    isEnabled: isClient && sessionId !== '', // Enable tracking only when client-side and sessionId is ready
    onSilenceAction: handleSilenceAction
  });

  useEffect(() => {
    scrollToBottom()
  }, [messages, showIntentPredictions, scrollToBottom])

  // Handle greeting loaded callback to show intent predictions
  const handleGreetingLoaded = (greetingData: any) => {
    if (greetingData && greetingData.intentPredictions && greetingData.intentPredictions.length > 0) {
      // Show intent predictions after a short delay for better UX
      setTimeout(() => {
        // Only show intent predictions if user hasn't started chatting yet
        if (!hasUserStartedChatting.current) {
          const highPriorityIntents = greetingData.intentPredictions
            .filter((intent: IntentPrediction) => intent.showAfterGreeting)
            .slice(0, 1); // Show only the top priority intent
          
          setIntentPredictions(highPriorityIntents);
          setShowIntentPredictions(true);
        }
      }, 1500); // 1.5 second delay after greeting
    }
  };

  const handleSendMessage = async (text: string) => {
    if (!text.trim()) return;

    // Mark that user has started chatting
    hasUserStartedChatting.current = true;

    // Reset silence tracking when user sends a message
    resetActivity();

    // Convert intent predictions to regular messages before hiding them
    if (showIntentPredictions && intentPredictions.length > 0) {
      const intentMessages: Message[] = intentPredictions.map((intent, index) => ({
        id: `intent_${Date.now()}_${index}`,
        text: intent.suggestedMessage,
        sender: 'bot' as const,
        timestamp: new Date(Date.now() - (intentPredictions.length - index) * 100) // Slightly stagger timestamps
      }));
      
      // Add intent predictions as regular messages first
      setMessages(prev => [...prev, ...intentMessages]);
      
      // Then hide the intent prediction bubbles
      setShowIntentPredictions(false);
    }

    // Add user message
    const userMessage: Message = {
      id: Date.now().toString(),
      text: text.trim(),
      sender: 'user',
      timestamp: new Date()
    };

    setMessages(prev => [...prev, userMessage]);
    setIsLoading(true);
    
    // Scroll to bottom after adding user message
    setTimeout(() => scrollToBottom(), 50);

    try {
      // Simulate a slight delay for better UX
      await new Promise(resolve => setTimeout(resolve, 500));
      
      // Send message to backend with sessionId for context tracking
      const response = await chatService.sendMessage(text.trim(), sessionId);
      
      // Add bot response with animation delay
      setTimeout(() => {
        const botMessage: Message = {
          id: (Date.now() + 1).toString(),
          text: response.message,
          sender: 'bot',
          timestamp: new Date(response.timestamp)
        };

        setMessages(prev => [...prev, botMessage]);
        setIsLoading(false);
        // Scroll to bottom after adding message
        setTimeout(() => scrollToBottom(), 100);
      }, 300);
      
    } catch (error) {
      console.error('Error sending message:', error);
      
      // Add error message with delay
      setTimeout(() => {
        const errorMessage: Message = {
          id: (Date.now() + 1).toString(),
          text: 'Sorry, I encountered an error. Please try again.',
          sender: 'bot',
          timestamp: new Date()
        };

        setMessages(prev => [...prev, errorMessage]);
        setIsLoading(false);
        // Scroll to bottom after adding error message
        setTimeout(() => scrollToBottom(), 100);
      }, 300);
    }
  };

  // Handle feedback submission
  const handleFeedbackSubmit = useCallback(async (feedback: FeedbackRequest) => {
    try {
      const response = await chatService.submitFeedback(feedback);
      
      // Add bot response thanking for feedback
      const thankYouMessage: Message = {
        id: `feedback_thanks_${Date.now()}`,
        text: response.message,
        sender: 'bot',
        timestamp: new Date()
      };
      
      setMessages(prev => [...prev, thankYouMessage]);
      setShowFeedbackModal(false);
      
      // End conversation tracking
      pauseTracking();
      
      scrollToBottom();
    } catch (error) {
      console.error('Failed to submit feedback:', error);
      // Keep modal open on error so user can try again
    }
  }, [pauseTracking, scrollToBottom]);

  // Handle feedback modal close
  const handleFeedbackModalClose = useCallback(() => {
    setShowFeedbackModal(false);
    resumeTracking(); // Resume tracking if user closes without submitting
  }, [resumeTracking]);

  return (
    <div className="bg-white rounded-lg shadow-lg overflow-hidden transition-all duration-300 ease-in-out hover:shadow-xl h-full flex flex-col">
      {/* Chat Messages */}
      <div className="flex-1 overflow-y-auto max-h-[calc(100vh-270px)] p-4 space-y-4 scroll-smooth custom-scrollbar scrollbar-thin scrollbar-webkit min-h-0">
        {/* Show greeting message */}
        {showGreeting && (
          <GreetingMessage 
            botProfile={botProfile}
            autoFetch={true}
            className="mb-4"
            userId="user_overdue"
            onGreetingLoaded={handleGreetingLoaded}
          />
        )}

        {/* Show intent predictions as separate bubbles */}
        {showIntentPredictions && intentPredictions.map((intent, index) => (
          <IntentPredictionBubble
            key={intent.intentId}
            intent={intent}
            botProfile={botProfile}
            isAnimated={true}
            className="mb-4"
          />
        ))}

        {/* Show conversation messages */}
        {messages.map((message, index) => (
          <div
            key={message.id}
            style={{ animationDelay: `${index * 100}ms` }}
          >
            <MessageBubble
              message={message.text}
              sender={message.sender}
              timestamp={message.timestamp}
              botProfile={message.sender === 'bot' ? botProfile : undefined}
              isAnimated={true}
            />
          </div>
        ))}
        
        {/* Enhanced Loading Indicator */}
        {isLoading && (
          <LoadingIndicator 
            botProfile={botProfile} 
            variant="typing"
            className="animate-fade-in"
          />
        )}
        
        <div ref={messagesEndRef} />
      </div>


      {/* Message Input */}
      <div className="flex-shrink-0 border-t border-gray-200 p-4 bg-gray-50 transition-colors duration-200">
        <MessageInput 
          onSendMessage={handleSendMessage} 
          disabled={isLoading} 
        />
      </div>

      {/* Feedback Modal */}
      {isClient && sessionId && (
        <FeedbackModal
          isOpen={showFeedbackModal}
          onClose={handleFeedbackModalClose}
          onSubmit={handleFeedbackSubmit}
          sessionId={sessionId}
          userId="user_overdue"
          conversationTopic="payment_inquiry"
          conversationStartedAt={conversationStartedAt}
          messageCount={messages.length}
          botProfile={botProfile}
        />
      )}
    </div>
  );
}
