'use client';

import type { FC } from 'react';
import { useEffect } from 'react';
import { useGreeting } from '@/hooks/useGreeting';
import BotAvatar from './BotAvatar';
import type { BotProfile } from '@/types/chat';

interface GreetingMessageProps {
  botProfile?: BotProfile;
  onGreetingLoaded?: (message: string) => void;
  autoFetch?: boolean;
  className?: string;
}

/**
 * GreetingMessage component displays contextual greetings from Somjeed
 * Features time-based and weather-aware messages with loading and error states
 */
export const GreetingMessage: FC<GreetingMessageProps> = ({
  botProfile,
  onGreetingLoaded,
  autoFetch = true,
  className = ''
}) => {
  const { greeting, isLoading, error, fetchGreeting, clearError } = useGreeting();

  // Default bot profile for Somjeed
  const defaultBotProfile: BotProfile = {
    name: 'Somjeed',
    avatar: '🤖',
    color: '#22c55e'
  };

  const currentBotProfile = botProfile || defaultBotProfile;

  // Auto-fetch greeting on component mount
  useEffect(() => {
    if (autoFetch) {
      fetchGreeting();
    }
  }, [autoFetch, fetchGreeting]);

  // Notify parent when greeting is loaded
  useEffect(() => {
    if (greeting && onGreetingLoaded) {
      onGreetingLoaded(greeting.message);
    }
  }, [greeting, onGreetingLoaded]);

  // Loading state
  if (isLoading) {
    return (
      <div className={`flex items-start space-x-3 animate-pulse ${className}`}>
        <div className="flex-shrink-0 mt-1">
          <BotAvatar profile={currentBotProfile} size="sm" />
        </div>
        <div className="bg-gray-50 border border-gray-200 px-4 py-3 rounded-lg shadow-sm max-w-xs lg:max-w-md">
          <div className="flex items-center space-x-2 mb-1">
            <span className="text-xs font-medium text-gray-600">
              {currentBotProfile.name}
            </span>
          </div>
          <div className="space-y-2">
            <div className="h-4 bg-gray-200 rounded animate-pulse"></div>
            <div className="h-4 bg-gray-200 rounded animate-pulse w-3/4"></div>
          </div>
          <p className="text-xs text-gray-500 mt-2">
            Preparing your greeting...
          </p>
        </div>
      </div>
    );
  }

  // Error state
  if (error) {
    return (
      <div className={`flex items-start space-x-3 ${className}`}>
        <div className="flex-shrink-0 mt-1">
          <BotAvatar profile={currentBotProfile} size="sm" />
        </div>
        <div className="bg-red-50 border border-red-200 px-4 py-3 rounded-lg shadow-sm max-w-xs lg:max-w-md">
          <div className="flex items-center space-x-2 mb-1">
            <span className="text-xs font-medium text-red-600">
              {currentBotProfile.name}
            </span>
          </div>
          <p className="text-sm text-red-700 leading-relaxed">
            Sorry, I couldn't prepare a greeting right now. 
          </p>
          <div className="flex items-center space-x-2 mt-2">
            <button
              onClick={fetchGreeting}
              className="text-xs text-red-600 hover:text-red-800 underline transition-colors"
            >
              Try again
            </button>
            <button
              onClick={clearError}
              className="text-xs text-red-500 hover:text-red-700 transition-colors"
            >
              Dismiss
            </button>
          </div>
        </div>
      </div>
    );
  }

  // Success state with greeting
  if (greeting) {
    return (
      <div className={`flex items-start space-x-3 animate-slide-in-left ${className}`}>
        <div className="flex-shrink-0 mt-1">
          <BotAvatar profile={currentBotProfile} size="sm" />
        </div>
        <div className="bg-gray-50 border border-gray-200 px-4 py-3 rounded-lg shadow-sm max-w-xs lg:max-w-md hover:shadow-md transition-all duration-200 ease-in-out transform hover:scale-[1.02]">
          <div className="flex items-center space-x-2 mb-1">
            <span className="text-xs font-medium text-gray-600">
              {currentBotProfile.name}
            </span>
            <span className="text-xs text-gray-400">
              • {greeting.timeOfDay}
            </span>
          </div>
          
          <p className="text-sm text-gray-800 leading-relaxed break-words">
            {greeting.message}
          </p>
          
          <div className="flex items-center justify-between mt-2">
            <div className="flex items-center space-x-1">
              <span className="text-xs text-gray-500">
                {getWeatherEmoji(greeting.weatherCondition)}
              </span>
              <span className="text-xs text-gray-500 capitalize">
                {greeting.weatherCondition}
              </span>
            </div>
            <button
              onClick={fetchGreeting}
              className="text-xs text-gray-400 hover:text-gray-600 transition-colors"
              title="Get new greeting"
            >
              🔄
            </button>
          </div>
        </div>
      </div>
    );
  }

  // Default state (no greeting, not loading, no error)
  return (
    <div className={`flex items-start space-x-3 ${className}`}>
      <div className="flex-shrink-0 mt-1">
        <BotAvatar profile={currentBotProfile} size="sm" />
      </div>
      <div className="bg-gray-50 border border-gray-200 px-4 py-3 rounded-lg shadow-sm max-w-xs lg:max-w-md">
        <div className="flex items-center space-x-2 mb-1">
          <span className="text-xs font-medium text-gray-600">
            {currentBotProfile.name}
          </span>
        </div>
        <p className="text-sm text-gray-600 leading-relaxed">
          Hello! I'm ready to help you today.
        </p>
        <button
          onClick={fetchGreeting}
          className="text-xs text-blue-600 hover:text-blue-800 underline mt-2 transition-colors"
        >
          Get personalized greeting
        </button>
      </div>
    </div>
  );
};

/**
 * Get weather emoji based on condition
 * 
 * @param condition Weather condition string
 * @returns Emoji representing the weather
 */
function getWeatherEmoji(condition: string): string {
  const weatherEmojis: Record<string, string> = {
    sunny: '☀️',
    cloudy: '☁️',
    rainy: '🌧️',
    stormy: '⛈️'
  };
  
  return weatherEmojis[condition.toLowerCase()] || '🌤️';
}

export default GreetingMessage;
