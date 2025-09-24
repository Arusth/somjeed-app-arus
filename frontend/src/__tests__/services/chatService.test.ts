import axios from 'axios';
import { chatService } from '@/services/chatService';
import type { ChatResponse, GreetingResponse } from '@/types/chat';

// Mock axios
jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

describe('ChatService', () => {
  const mockChatResponse: ChatResponse = {
    message: 'Good morning, on a sunshine day!',
    timestamp: '2024-01-01T09:00:00Z',
  };

  const mockGreetingResponse: GreetingResponse = {
    message: 'Good morning, on a sunshine day!',
    timeOfDay: 'morning',
    weatherCondition: 'sunny',
    timestamp: '2024-01-01T09:00:00Z',
  };

  beforeEach(() => {
    jest.clearAllMocks();
    // Mock axios.create to return a mock instance
    const mockAxiosInstance = {
      post: jest.fn(),
      get: jest.fn(),
    };
    mockedAxios.create.mockReturnValue(mockAxiosInstance as unknown as jest.Mocked<typeof axios>);
  });

  describe('sendMessage', () => {
    test('should send message successfully', async () => {
      const mockAxiosInstance = mockedAxios.create();
      mockAxiosInstance.post = jest.fn().mockResolvedValue({ data: mockChatResponse });

      const result = await chatService.sendMessage('Hello');

      expect(mockAxiosInstance.post).toHaveBeenCalledWith('/message', { message: 'Hello' });
      expect(result).toEqual(mockChatResponse);
    });

    test('should handle send message error', async () => {
      const mockAxiosInstance = mockedAxios.create();
      mockAxiosInstance.post = jest.fn().mockRejectedValue(new Error('Network error'));

      await expect(chatService.sendMessage('Hello')).rejects.toThrow(
        'Failed to send message. Please try again.'
      );

      expect(mockAxiosInstance.post).toHaveBeenCalledWith('/message', { message: 'Hello' });
    });

    test('should send message with proper request format', async () => {
      const mockAxiosInstance = mockedAxios.create();
      mockAxiosInstance.post = jest.fn().mockResolvedValue({ data: mockChatResponse });

      await chatService.sendMessage('Test message');

      expect(mockAxiosInstance.post).toHaveBeenCalledWith('/message', {
        message: 'Test message'
      });
    });
  });

  describe('getGreeting', () => {
    test('should get greeting successfully', async () => {
      const mockAxiosInstance = mockedAxios.create();
      mockAxiosInstance.get = jest.fn().mockResolvedValue({ data: mockGreetingResponse });

      const result = await chatService.getGreeting();

      expect(mockAxiosInstance.get).toHaveBeenCalledWith('/greeting');
      expect(result).toEqual(mockGreetingResponse);
    });

    test('should handle get greeting error', async () => {
      const mockAxiosInstance = mockedAxios.create();
      mockAxiosInstance.get = jest.fn().mockRejectedValue(new Error('Network error'));

      await expect(chatService.getGreeting()).rejects.toThrow(
        'Failed to get greeting. Please try again.'
      );

      expect(mockAxiosInstance.get).toHaveBeenCalledWith('/greeting');
    });
  });

  describe('checkHealth', () => {
    test('should check health successfully', async () => {
      const healthResponse = 'Chat service is running';
      const mockAxiosInstance = mockedAxios.create();
      mockAxiosInstance.get = jest.fn().mockResolvedValue({ data: healthResponse });

      const result = await chatService.checkHealth();

      expect(mockAxiosInstance.get).toHaveBeenCalledWith('/health');
      expect(result).toBe(healthResponse);
    });

    test('should handle health check error', async () => {
      const mockAxiosInstance = mockedAxios.create();
      mockAxiosInstance.get = jest.fn().mockRejectedValue(new Error('Service unavailable'));

      await expect(chatService.checkHealth()).rejects.toThrow(
        'Failed to check service health'
      );

      expect(mockAxiosInstance.get).toHaveBeenCalledWith('/health');
    });
  });

  describe('axios configuration', () => {
    test('should create axios instance with correct configuration', () => {
      expect(mockedAxios.create).toHaveBeenCalledWith({
        baseURL: 'http://localhost:8080/api/chat',
        headers: {
          'Content-Type': 'application/json',
        },
        timeout: 10000,
      });
    });

    test('should use environment variable for API URL if available', () => {
      const originalEnv = process.env.NEXT_PUBLIC_API_URL;
      process.env.NEXT_PUBLIC_API_URL = 'https://api.example.com';

      // Re-import to get new instance with updated env
      jest.resetModules();
      require('@/services/chatService');

      expect(mockedAxios.create).toHaveBeenCalledWith(
        expect.objectContaining({
          baseURL: 'https://api.example.com/api/chat',
        })
      );

      // Restore original env
      process.env.NEXT_PUBLIC_API_URL = originalEnv;
    });
  });

  describe('error handling', () => {
    test('should log errors to console', async () => {
      const consoleSpy = jest.spyOn(console, 'error').mockImplementation();
      const mockAxiosInstance = mockedAxios.create();
      const networkError = new Error('Network error');
      mockAxiosInstance.post = jest.fn().mockRejectedValue(networkError);

      await expect(chatService.sendMessage('Hello')).rejects.toThrow();

      expect(consoleSpy).toHaveBeenCalledWith('Error sending message:', networkError);
      
      consoleSpy.mockRestore();
    });

    test('should handle different error types', async () => {
      const mockAxiosInstance = mockedAxios.create();
      
      // Test with axios error
      const axiosError = {
        response: { status: 500, data: 'Internal server error' },
        message: 'Request failed',
      };
      mockAxiosInstance.post = jest.fn().mockRejectedValue(axiosError);

      await expect(chatService.sendMessage('Hello')).rejects.toThrow(
        'Failed to send message. Please try again.'
      );
    });
  });

  describe('timeout handling', () => {
    test('should handle timeout errors', async () => {
      const mockAxiosInstance = mockedAxios.create();
      const timeoutError = new Error('timeout of 10000ms exceeded');
      mockAxiosInstance.get = jest.fn().mockRejectedValue(timeoutError);

      await expect(chatService.getGreeting()).rejects.toThrow(
        'Failed to get greeting. Please try again.'
      );
    });
  });
});
