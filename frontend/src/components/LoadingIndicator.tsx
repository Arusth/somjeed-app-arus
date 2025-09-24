'use client'

import type { FC } from 'react';
import BotAvatar from './BotAvatar';
import type { BotProfile } from '@/types/chat';

interface LoadingIndicatorProps {
  botProfile: BotProfile;
  variant?: 'typing' | 'thinking' | 'processing';
  className?: string;
}

/**
 * LoadingIndicator component shows animated loading states for bot responses
 * Features multiple animation variants and smooth transitions
 */
export const LoadingIndicator: FC<LoadingIndicatorProps> = ({ 
  botProfile, 
  variant = 'typing',
  className = '' 
}) => {
  const getLoadingText = () => {
    switch (variant) {
      case 'thinking': return 'thinking...';
      case 'processing': return 'processing...';
      default: return 'typing...';
    }
  };

  const getAnimationClass = () => {
    switch (variant) {
      case 'thinking': return 'animate-pulse-soft';
      case 'processing': return 'animate-bounce-gentle';
      default: return 'animate-typing';
    }
  };

  return (
    <div className={`flex justify-start animate-slide-in-left ${className}`}>
      <div className="flex items-start space-x-3 max-w-xs lg:max-w-md">
        {/* Bot Avatar */}
        <div className="flex-shrink-0 mt-1">
          <BotAvatar profile={botProfile} size="sm" />
        </div>
        
        {/* Loading Message */}
        <div className="bg-gray-100 rounded-lg px-4 py-3 shadow-sm border border-gray-200">
          <div className="flex items-center space-x-2">
            {/* Animated Dots */}
            <div className="flex space-x-1">
              <div 
                className={`w-2 h-2 bg-gray-400 rounded-full ${getAnimationClass()}`}
                style={{ animationDelay: '0ms' }}
              />
              <div 
                className={`w-2 h-2 bg-gray-400 rounded-full ${getAnimationClass()}`}
                style={{ animationDelay: '150ms' }}
              />
              <div 
                className={`w-2 h-2 bg-gray-400 rounded-full ${getAnimationClass()}`}
                style={{ animationDelay: '300ms' }}
              />
            </div>
            
            {/* Loading Text */}
            <span className="text-xs text-gray-500 animate-pulse">
              {getLoadingText()}
            </span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LoadingIndicator;
