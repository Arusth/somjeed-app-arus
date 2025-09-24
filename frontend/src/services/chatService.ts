import axios from 'axios';
import type { ChatRequest, ChatResponse, GreetingResponse } from '@/types/chat';

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

  /**
   * Send a chat message to the backend
   * 
   * @param message User message content
   * @returns Promise<ChatResponse> Bot response
   */
  async sendMessage(message: string): Promise<ChatResponse> {
    try {
      const request: ChatRequest = { message };
      const response = await this.api.post<ChatResponse>('/message', request);
      return response.data;
    } catch (error: any) {
      console.error('Error sending message:', error);
      
      // Provide more detailed error information
      if (error.response) {
        // Server responded with error status
        const status = error.response.status;
        const statusText = error.response.statusText;
        console.error(`HTTP ${status}: ${statusText}`, error.response.data);
        throw new Error(`Server error (${status}): ${statusText}`);
      } else if (error.request) {
        // Request was made but no response received
        console.error('No response received:', error.request);
        throw new Error('No response from server. Please check your connection.');
      } else {
        // Something else happened
        console.error('Request setup error:', error.message);
        throw new Error(`Request failed: ${error.message}`);
      }
    }
  }

  /**
   * Get contextual greeting from the backend
   * 
   * @returns Promise<GreetingResponse> Personalized greeting
   */
  async getGreeting(): Promise<GreetingResponse> {
    try {
      const response = await this.api.get<GreetingResponse>('/greeting');
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
}

// Export singleton instance
export const chatService = new ChatService();

// Export individual methods for backward compatibility
export const sendMessage = (message: string) => chatService.sendMessage(message);
export const getGreeting = () => chatService.getGreeting();
export const checkHealth = () => chatService.checkHealth();
