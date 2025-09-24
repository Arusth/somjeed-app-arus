'use client'

import type { FC } from 'react';
import ClientOnly from './ClientOnly';
import BotAvatar from './BotAvatar';
import type { BotProfile } from '@/types/chat';

interface MessageBubbleProps {
  message: string;
  sender: 'user' | 'bot';
  timestamp: Date;
  botProfile?: BotProfile;
  isAnimated?: boolean;
}

/**
 * MessageBubble component displays individual chat messages
 * Features bot avatars, smooth animations, and responsive design
 */
export const MessageBubble: FC<MessageBubbleProps> = ({ 
  message, 
  sender, 
  timestamp, 
  botProfile,
  isAnimated = true 
}) => {
  const isUser = sender === 'user';
  
  // Default bot profile if none provided
  const defaultBotProfile: BotProfile = {
    name: 'Somjeed',
    avatar: '🤖',
    color: '#22c55e'
  };

  const currentBotProfile = botProfile || defaultBotProfile;

  return (
    <div className={`
      flex ${isUser ? 'justify-end' : 'justify-start'} 
      ${isAnimated ? (isUser ? 'animate-slide-in-right' : 'animate-slide-in-left') : ''}
      transition-all duration-300 ease-in-out
    `}>
      <div className={`flex items-start space-x-3 max-w-xs lg:max-w-md ${isUser ? 'flex-row-reverse space-x-reverse' : ''}`}>
        {/* Bot Avatar (only for bot messages) */}
        {!isUser && (
          <div className="flex-shrink-0 mt-1">
            <BotAvatar profile={currentBotProfile} size="sm" />
          </div>
        )}
        
        {/* Message Content */}
        <div className={`
          px-4 py-3 rounded-lg shadow-sm border transition-all duration-200 ease-in-out
          hover:shadow-md transform hover:scale-[1.02]
          ${isUser 
            ? 'bg-primary-500 text-white border-primary-600' 
            : 'bg-gray-50 text-gray-800 border-gray-200'
          }
        `}>
          {/* Bot Name (only for bot messages) */}
          {!isUser && (
            <div className="flex items-center space-x-2 mb-1">
              <span className="text-xs font-medium text-gray-600">
                {currentBotProfile.name}
              </span>
            </div>
          )}
          
          {/* Message Text */}
          <div className="text-sm leading-relaxed break-words whitespace-pre-line">
            {message}
          </div>
          
          {/* Timestamp */}
          <ClientOnly fallback={
            <p className={`text-xs mt-2 ${isUser ? 'text-primary-100' : 'text-gray-500'}`}>
              --:--
            </p>
          }>
            <p className={`text-xs mt-2 transition-colors duration-200 ${
              isUser ? 'text-primary-100' : 'text-gray-500'
            }`}>
              {timestamp.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
            </p>
          </ClientOnly>
        </div>
      </div>
    </div>
  );
};

export default MessageBubble;
