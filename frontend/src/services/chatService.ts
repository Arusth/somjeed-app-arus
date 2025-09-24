import axios from 'axios';
import type { 
  ChatRequest, 
  ChatResponse, 
  GreetingResponse, 
  FeedbackRequest, 
  FeedbackSubmissionResponse,
  ConversationClosureRequest,
  ConversationClosureResponse
} from '@/types/chat';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

/**
 * ChatService handles API communication with the backend
 * Provides methods for sending messages, getting greetings, and health checks
 */
class ChatService {
  private readonly api = axios.create({
    baseURL: `${API_BASE_URL}/api/chat`,
    headers: {
      'Content-Type': 'application/json',
    },
    timeout: 10000, // 10 second timeout
  });

  private readonly feedbackApi = axios.create({
    baseURL: `${API_BASE_URL}/api/feedback`,
    headers: {
      'Content-Type': 'application/json',
    },
    timeout: 10000,
  });

  /**
   * Send a chat message to the backend
   * 
   * @param message User message content
   * @param sessionId Optional session ID for context tracking
   * @returns Promise<ChatResponse> Bot response
   */
  async sendMessage(message: string, sessionId?: string): Promise<ChatResponse> {
    try {
      const request: ChatRequest = { 
        message,
        ...(sessionId && { sessionId })
      };
      const response = await this.api.post<ChatResponse>('/message', request);
      return response.data;
    } catch (error: unknown) {
      console.error('Error sending message:', error);
      
      // Provide more detailed error information
      const axiosError = error as { response?: { status: number; statusText: string; data: unknown }; request?: unknown };
      if (axiosError?.response) {
        // Server responded with error status
        const status = axiosError.response.status;
        const statusText = axiosError.response.statusText;
        console.error(`HTTP ${status}: ${statusText}`, axiosError.response.data);
        throw new Error(`Server error (${status}): ${statusText}`);
      } else if (axiosError?.request) {
        // Request was made but no response received
        console.error('No response received:', axiosError.request);
        throw new Error('No response from server. Please check your connection.');
      } else {
        // Something else happened
        const message = error instanceof Error ? error.message : 'An unexpected error occurred';
        console.error('Request setup error:', message);
        throw new Error(`Request failed: ${message}`);
      }
    }
  }

  /**
   * Get contextual greeting from the backend
   * 
   * @param userId Optional user ID for personalized intent predictions
   * @returns Promise<GreetingResponse> Personalized greeting
   */
  async getGreeting(userId?: string): Promise<GreetingResponse> {
    try {
      const params = userId ? { userId } : {};
      const response = await this.api.get<GreetingResponse>('/greeting', { params });
      return response.data;
    } catch (error) {
      console.error('Error getting greeting:', error);
      throw new Error('Failed to get greeting. Please try again.');
    }
  }

  /**
   * Check backend service health
   * 
   * @returns Promise<string> Health status message
   */
  async checkHealth(): Promise<string> {
    try {
      const response = await this.api.get<string>('/health');
      return response.data;
    } catch (error) {
      console.error('Error checking health:', error);
      throw new Error('Failed to check service health');
    }
  }

  /**
   * Submit user feedback
   * 
   * @param feedback FeedbackRequest object
   * @returns Promise<FeedbackSubmissionResponse> Submission result
   */
  async submitFeedback(feedback: FeedbackRequest): Promise<FeedbackSubmissionResponse> {
    try {
      const response = await this.feedbackApi.post<FeedbackSubmissionResponse>('/submit', feedback);
      return response.data;
    } catch (error: unknown) {
      console.error('Error submitting feedback:', error);
      
      const axiosError = error as { response?: { status: number; statusText: string; data?: { message?: string } }; request?: unknown };
      if (axiosError?.response?.status === 400) {
        throw new Error(axiosError.response.data?.message || 'Invalid feedback data');
      }
      
      throw new Error('Failed to submit feedback. Please try again.');
    }
  }

  /**
   * Handle user silence and get conversation closure guidance
   * 
   * @param request ConversationClosureRequest object
   * @returns Promise<ConversationClosureResponse | null> Closure guidance or null if no action needed
   */
  async handleSilence(request: ConversationClosureRequest): Promise<ConversationClosureResponse | null> {
    try {
      const response = await this.feedbackApi.post<ConversationClosureResponse>('/silence', request);
      return response.data;
    } catch (error: unknown) {
      const axiosError = error as { response?: { status: number; statusText: string; data: unknown }; request?: unknown };
      if (axiosError?.response?.status === 204) {
        // No content - no action needed yet
        return null;
      }
      
      console.error('Error handling silence:', error);
      throw new Error('Failed to process silence detection');
    }
  }

  /**
   * Reset user activity (called when user sends a message)
   * 
   * @param sessionId Session ID to reset activity for
   */
  async resetActivity(sessionId: string): Promise<void> {
    try {
      await this.feedbackApi.post(`/activity/${sessionId}`);
    } catch (error) {
      console.error('Error resetting activity:', error);
      // Don't throw error as this is not critical
    }
  }
}

// Export singleton instance
export const chatService = new ChatService();

// Export individual methods for backward compatibility
export const sendMessage = (message: string) => chatService.sendMessage(message);
export const getGreeting = () => chatService.getGreeting();
export const checkHealth = () => chatService.checkHealth();
