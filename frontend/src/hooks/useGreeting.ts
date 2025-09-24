'use client';

import { useState, useCallback } from 'react';
import { chatService } from '@/services/chatService';
import type { GreetingResponse, LoadingState } from '@/types/chat';

interface UseGreetingReturn {
  greeting: GreetingResponse | null;
  isLoading: boolean;
  error: string | null;
  fetchGreeting: (userId?: string) => Promise<void>;
  clearError: () => void;
}

/**
 * Custom hook for managing greeting state and API calls
 * Provides loading states, error handling, and greeting data management
 * 
 * @returns UseGreetingReturn object with greeting data and methods
 */
export const useGreeting = (): UseGreetingReturn => {
  const [greeting, setGreeting] = useState<GreetingResponse | null>(null);
  const [loadingState, setLoadingState] = useState<LoadingState>('idle');
  const [error, setError] = useState<string | null>(null);

  const isLoading = loadingState === 'loading';

  /**
   * Fetch greeting from the backend API
   * Handles loading states and error management
   */
  const fetchGreeting = useCallback(async (userId?: string): Promise<void> => {
    try {
      setLoadingState('loading');
      setError(null);
      
      const greetingData = await chatService.getGreeting(userId);
      
      setGreeting(greetingData);
      setLoadingState('success');
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to fetch greeting';
      setError(errorMessage);
      setLoadingState('error');
      console.error('Error fetching greeting:', err);
    }
  }, []);

  /**
   * Clear any existing error state
   */
  const clearError = useCallback((): void => {
    setError(null);
    if (loadingState === 'error') {
      setLoadingState('idle');
    }
  }, [loadingState]);

  return {
    greeting,
    isLoading,
    error,
    fetchGreeting,
    clearError,
  };
};
