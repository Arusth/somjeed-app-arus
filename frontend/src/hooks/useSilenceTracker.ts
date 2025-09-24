'use client';

import { useState, useEffect, useRef, useCallback } from 'react';
import { chatService } from '@/services/chatService';
import type { 
  SilenceTracker, 
  ConversationClosureRequest, 
  ConversationClosureResponse 
} from '@/types/chat';

interface UseSilenceTrackerProps {
  sessionId: string;
  userId: string;
  conversationTopic?: string;
  messageCount: number;
  isEnabled?: boolean;
  onSilenceAction?: (response: ConversationClosureResponse) => void;
}

interface UseSilenceTrackerReturn {
  silenceTracker: SilenceTracker;
  resetActivity: () => void;
  pauseTracking: () => void;
  resumeTracking: () => void;
  getSilenceStatus: () => string;
}

/**
 * Hook for tracking user silence and triggering conversation closure actions
 * Monitors inactivity periods and communicates with backend for guidance
 */
export const useSilenceTracker = ({
  sessionId,
  userId,
  conversationTopic = 'general',
  messageCount,
  isEnabled = true,
  onSilenceAction
}: UseSilenceTrackerProps): UseSilenceTrackerReturn => {
  
  const [silenceTracker, setSilenceTracker] = useState<SilenceTracker>(() => ({
    lastActivityAt: new Date(),
    silenceDurationSeconds: 0,
    silenceIntervals: 0,
    isUserActive: true,
    conversationStartedAt: new Date()
  }));

  const [isPaused, setIsPaused] = useState<boolean>(!isEnabled);
  const intervalRef = useRef<NodeJS.Timeout | null>(null);
  const lastCheckRef = useRef<number>(0);

  // Handle isEnabled changes without resetting the tracker
  useEffect(() => {
    setIsPaused(!isEnabled);
  }, [isEnabled]);


  /**
   * Reset user activity when they interact with the chat
   */
  const resetActivity = useCallback(() => {
    const now = new Date();
    setSilenceTracker(prev => ({
      ...prev,
      lastActivityAt: now,
      silenceDurationSeconds: 0,
      isUserActive: true
    }));

    // Reset the last check reference so silence detection can trigger again
    lastCheckRef.current = 0;

    // Notify backend about user activity
    chatService.resetActivity(sessionId).catch(error => {
      console.warn('Failed to reset activity on backend:', error);
    });
  }, [sessionId]);

  /**
   * Pause silence tracking
   */
  const pauseTracking = useCallback(() => {
    setIsPaused(true);
    if (intervalRef.current) {
      clearInterval(intervalRef.current);
      intervalRef.current = null;
    }
  }, []);

  /**
   * Resume silence tracking
   */
  const resumeTracking = useCallback(() => {
    setIsPaused(false);
    resetActivity();
  }, [resetActivity]);

  /**
   * Get current silence status for debugging
   */
  const getSilenceStatus = useCallback(() => {
    const { silenceDurationSeconds, isUserActive, silenceIntervals } = silenceTracker;
    return `Silent for ${silenceDurationSeconds}s, Active: ${isUserActive}, Intervals: ${silenceIntervals}`;
  }, [silenceTracker]);

  /**
   * Handle silence detection and backend communication
   */
  const handleSilenceCheck = useCallback(async (currentSilenceDuration: number) => {
    // Only check at specific intervals to avoid spam
    const checkIntervals = [10, 20, 50]; // seconds
    const shouldCheck = checkIntervals.some(interval => 
      currentSilenceDuration >= interval && lastCheckRef.current < interval
    );

    if (!shouldCheck) return;

    lastCheckRef.current = currentSilenceDuration;

    try {
      const request: ConversationClosureRequest = {
        sessionId,
        userId,
        lastActivityAt: silenceTracker.lastActivityAt,
        silenceDurationSeconds: currentSilenceDuration,
        messageCount,
        conversationTopic,
        userNeedsResolved: messageCount > 2 // Assume needs resolved if they've chatted
      };

      const response = await chatService.handleSilence(request);
      
      if (response && onSilenceAction) {
        onSilenceAction(response);
        
        // Increment silence intervals counter
        setSilenceTracker(prev => ({
          ...prev,
          silenceIntervals: prev.silenceIntervals + 1
        }));
      }
    } catch (error) {
      console.error('Error handling silence check:', error);
    }
  }, [sessionId, userId, conversationTopic, messageCount, onSilenceAction]);

  /**
   * Main silence tracking effect
   */
  useEffect(() => {
    if (isPaused || !isEnabled) {
      return;
    }

    intervalRef.current = setInterval(() => {
      setSilenceTracker(prev => {
        const now = new Date();
        const silenceDuration = Math.floor((now.getTime() - prev.lastActivityAt.getTime()) / 1000);
        
        const updatedTracker = {
          ...prev,
          silenceDurationSeconds: silenceDuration,
          isUserActive: silenceDuration < 5 // Consider active if silent for less than 5 seconds
        };

        // Trigger silence check if needed
        if (silenceDuration >= 10) {
          handleSilenceCheck(silenceDuration);
        }

        return updatedTracker;
      });
    }, 1000); // Check every second

    return () => {
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
        intervalRef.current = null;
      }
    };
  }, [isPaused, isEnabled, handleSilenceCheck]);

  /**
   * Reset check reference when activity resets
   */
  useEffect(() => {
    if (silenceTracker.silenceDurationSeconds === 0) {
      lastCheckRef.current = 0;
    }
  }, [silenceTracker.silenceDurationSeconds]);

  /**
   * Cleanup on unmount
   */
  useEffect(() => {
    return () => {
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
      }
    };
  }, []);

  return {
    silenceTracker,
    resetActivity,
    pauseTracking,
    resumeTracking,
    getSilenceStatus
  };
};

export default useSilenceTracker;
