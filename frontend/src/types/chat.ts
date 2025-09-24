/**
 * Chat-related type definitions for the ChatBot Application
 * Following TypeScript best practices from frontend rules
 */

export interface ChatMessage {
  id: string;
  text: string;
  sender: 'user' | 'bot';
  timestamp: Date;
}

export interface BotProfile {
  name: string;
  avatar: string;
  color: string;
}

export interface ChatResponse {
  message: string;
  timestamp: string;
}

export interface ChatRequest {
  message: string;
  sessionId?: string;
}

export interface WeatherResponse {
  condition: string;
  description: string;
  temperature: number;
  location: string;
}

export interface IntentPrediction {
  intentId: string;
  category: string;
  predictedIntent: string;
  suggestedMessage: string;
  confidence: number;
  priority: string;
  triggerContext: string;
  suggestedActions: string[];
  timestamp: string;
  showAfterGreeting: boolean;
}

export interface GreetingResponse {
  message: string;
  timeOfDay: string;
  weatherCondition: string;
  timestamp: string;
  intentPredictions?: IntentPrediction[];
  userId?: string;
}

// Animation states for smooth transitions
export type AnimationState = 'entering' | 'entered' | 'exiting' | 'exited';

// Loading states for better UX
export type LoadingState = 'idle' | 'loading' | 'success' | 'error';

// Feedback and conversation closure types
export interface FeedbackRequest {
  sessionId: string;
  userId: string;
  rating: number; // 1-5 stars
  comment?: string;
  conversationTopic: string;
  conversationStartedAt: Date;
  conversationEndedAt: Date;
  messageCount: number;
  deviceType?: string;
  totalSilenceDurationSeconds?: number;
  silenceIntervals?: number;
  conversationCompletedNaturally?: boolean;
}

export interface FeedbackSubmissionResponse {
  success: boolean;
  message: string;
  feedbackId?: number;
}

export interface ConversationClosureRequest {
  sessionId: string;
  userId: string;
  lastActivityAt: Date;
  silenceDurationSeconds: number;
  messageCount: number;
  lastUserMessage?: string;
  conversationTopic?: string;
  userNeedsResolved?: boolean;
}

export interface ConversationClosureResponse {
  message: string;
  actionType: 'CHECK_ASSISTANCE' | 'REQUEST_FEEDBACK' | 'GOODBYE';
  shouldRequestFeedback: boolean;
  shouldCloseConversation: boolean;
  nextCheckInSeconds?: number;
}

// Silence tracking for inactivity monitoring
export interface SilenceTracker {
  lastActivityAt: Date;
  silenceDurationSeconds: number;
  silenceIntervals: number;
  isUserActive: boolean;
  conversationStartedAt: Date;
}
