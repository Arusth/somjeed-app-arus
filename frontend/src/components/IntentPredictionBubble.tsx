'use client';

import type { FC } from 'react';
import BotAvatar from './BotAvatar';
import type { BotProfile, IntentPrediction } from '@/types/chat';

interface IntentPredictionBubbleProps {
  intent: IntentPrediction;
  botProfile?: BotProfile;
  isAnimated?: boolean;
  className?: string;
}

/**
 * IntentPredictionBubble component displays a predicted intent as a separate message bubble
 * Features priority-based styling, action buttons, and confidence display
 */
export const IntentPredictionBubble: FC<IntentPredictionBubbleProps> = ({
  intent,
  botProfile,
  isAnimated = true,
  className = ''
}) => {
  // Default bot profile for Somjeed
  const defaultBotProfile: BotProfile = {
    name: 'Somjeed',
    avatar: '🤖',
    color: '#22c55e'
  };

  const currentBotProfile = botProfile || defaultBotProfile;

  const getPriorityColor = (priority: string) => {
    switch (priority.toLowerCase()) {
      case 'high': return 'border-red-200 bg-red-50 text-red-800';
      case 'medium': return 'border-yellow-200 bg-yellow-50 text-yellow-800';
      case 'low': return 'border-blue-200 bg-blue-50 text-blue-800';
      default: return 'border-gray-200 bg-gray-50 text-gray-800';
    }
  };

  const getCategoryEmoji = (category: string) => {
    switch (category.toLowerCase()) {
      case 'payment': return '💳';
      case 'balance': return '💰';
      case 'transaction': return '🔄';
      default: return '📋';
    }
  };

  return (
    <div className={`
      flex justify-start 
      ${isAnimated ? 'animate-slide-in-left' : ''}
      transition-all duration-300 ease-in-out
      ${className}
    `}>
      <div className="flex items-start space-x-3 max-w-xs lg:max-w-md">
        {/* Bot Avatar */}
        <div className="flex-shrink-0 mt-1">
          <BotAvatar profile={currentBotProfile} size="sm" />
        </div>
        
        {/* Intent Prediction Content */}
        <div className={`
          border rounded-lg p-4 shadow-sm transition-all duration-200 ease-in-out
          hover:shadow-md transform hover:scale-[1.02]
          ${getPriorityColor(intent.priority)}
        `}>
          {/* Bot Name */}
          <div className="flex items-center space-x-2 mb-2">
            <span className="text-xs font-medium opacity-75">
              {currentBotProfile.name}
            </span>
            <span className="text-lg">{getCategoryEmoji(intent.category)}</span>
          </div>
          
          {/* Intent Message */}
          <p className="text-sm font-medium leading-relaxed break-words mb-3">
            {intent.suggestedMessage}
          </p>
          
          {/* Priority and Confidence */}
          <div className="flex items-center justify-between text-xs opacity-75">
            <span className="font-medium">
              {intent.priority} Priority
            </span>
            <span>
              {Math.round(intent.confidence * 100)}% confidence
            </span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default IntentPredictionBubble;
