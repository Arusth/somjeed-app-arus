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

export interface GreetingResponse {
  message: string;
  timeOfDay: string;
  weatherCondition: string;
  timestamp: string;
}

// Animation states for smooth transitions
export type AnimationState = 'entering' | 'entered' | 'exiting' | 'exited';

// Loading states for better UX
export type LoadingState = 'idle' | 'loading' | 'success' | 'error';
