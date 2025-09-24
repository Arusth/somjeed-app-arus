import { renderHook, act } from '@testing-library/react';
import { useGreeting } from '@/hooks/useGreeting';
import { chatService } from '@/services/chatService';
import type { GreetingResponse } from '@/types/chat';

// Mock the chatService
jest.mock('@/services/chatService', () => ({
  chatService: {
    getGreeting: jest.fn(),
  },
}));

const mockChatService = chatService as jest.Mocked<typeof chatService>;

describe('useGreeting', () => {
  const mockGreeting: GreetingResponse = {
    message: 'Good morning, on a sunshine day!',
    timeOfDay: 'morning',
    weatherCondition: 'sunny',
    timestamp: '2024-01-01T09:00:00Z',
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('should initialize with default state', () => {
    const { result } = renderHook(() => useGreeting());

    expect(result.current.greeting).toBeNull();
    expect(result.current.isLoading).toBe(false);
    expect(result.current.error).toBeNull();
    expect(typeof result.current.fetchGreeting).toBe('function');
    expect(typeof result.current.clearError).toBe('function');
  });

  test('should fetch greeting successfully', async () => {
    mockChatService.getGreeting.mockResolvedValueOnce(mockGreeting);

    const { result } = renderHook(() => useGreeting());

    await act(async () => {
      await result.current.fetchGreeting();
    });

    expect(result.current.greeting).toEqual(mockGreeting);
    expect(result.current.isLoading).toBe(false);
    expect(result.current.error).toBeNull();
    expect(mockChatService.getGreeting).toHaveBeenCalledTimes(1);
  });

  test('should handle loading state correctly', async () => {
    let resolvePromise: (value: GreetingResponse) => void;
    const promise = new Promise<GreetingResponse>((resolve) => {
      resolvePromise = resolve;
    });
    mockChatService.getGreeting.mockReturnValueOnce(promise);

    const { result } = renderHook(() => useGreeting());

    act(() => {
      result.current.fetchGreeting();
    });

    expect(result.current.isLoading).toBe(true);
    expect(result.current.error).toBeNull();

    await act(async () => {
      resolvePromise!(mockGreeting);
      await promise;
    });

    expect(result.current.isLoading).toBe(false);
    expect(result.current.greeting).toEqual(mockGreeting);
  });

  test('should handle API error correctly', async () => {
    const errorMessage = 'Failed to fetch greeting';
    mockChatService.getGreeting.mockRejectedValueOnce(new Error(errorMessage));

    const { result } = renderHook(() => useGreeting());

    await act(async () => {
      await result.current.fetchGreeting();
    });

    expect(result.current.greeting).toBeNull();
    expect(result.current.isLoading).toBe(false);
    expect(result.current.error).toBe(errorMessage);
  });

  test('should handle non-Error rejection', async () => {
    mockChatService.getGreeting.mockRejectedValueOnce('Network error');

    const { result } = renderHook(() => useGreeting());

    await act(async () => {
      await result.current.fetchGreeting();
    });

    expect(result.current.error).toBe('Failed to fetch greeting');
  });

  test('should clear error correctly', async () => {
    mockChatService.getGreeting.mockRejectedValueOnce(new Error('Test error'));

    const { result } = renderHook(() => useGreeting());

    await act(async () => {
      await result.current.fetchGreeting();
    });

    expect(result.current.error).toBe('Test error');

    act(() => {
      result.current.clearError();
    });

    expect(result.current.error).toBeNull();
  });

  test('should maintain greeting data after clearing error', async () => {
    // First successful fetch
    mockChatService.getGreeting.mockResolvedValueOnce(mockGreeting);

    const { result } = renderHook(() => useGreeting());

    await act(async () => {
      await result.current.fetchGreeting();
    });

    expect(result.current.greeting).toEqual(mockGreeting);

    // Then error
    mockChatService.getGreeting.mockRejectedValueOnce(new Error('Test error'));

    await act(async () => {
      await result.current.fetchGreeting();
    });

    expect(result.current.error).toBe('Test error');
    expect(result.current.greeting).toEqual(mockGreeting); // Should still have previous greeting

    act(() => {
      result.current.clearError();
    });

    expect(result.current.error).toBeNull();
    expect(result.current.greeting).toEqual(mockGreeting); // Should still have greeting
  });

  test('should handle multiple concurrent fetch calls', async () => {
    mockChatService.getGreeting.mockResolvedValue(mockGreeting);

    const { result } = renderHook(() => useGreeting());

    await act(async () => {
      // Start multiple fetch calls
      const promises = [
        result.current.fetchGreeting(),
        result.current.fetchGreeting(),
        result.current.fetchGreeting(),
      ];
      await Promise.all(promises);
    });

    expect(result.current.greeting).toEqual(mockGreeting);
    expect(result.current.isLoading).toBe(false);
    expect(result.current.error).toBeNull();
    // Should be called multiple times
    expect(mockChatService.getGreeting).toHaveBeenCalledTimes(3);
  });
});
