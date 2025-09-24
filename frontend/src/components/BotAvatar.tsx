'use client'

import type { FC } from 'react';
import type { BotProfile } from '@/types/chat';

interface BotAvatarProps {
  profile: BotProfile;
  size?: 'sm' | 'md' | 'lg';
  showName?: boolean;
  className?: string;
}

/**
 * BotAvatar component displays bot profile with icon and optional name
 * Features smooth animations and responsive design
 */
export const BotAvatar: FC<BotAvatarProps> = ({ 
  profile, 
  size = 'md', 
  showName = false,
  className = '' 
}) => {
  const sizeClasses = {
    sm: 'w-6 h-6 text-xs',
    md: 'w-8 h-8 text-sm',
    lg: 'w-10 h-10 text-base'
  };

  const textSizeClasses = {
    sm: 'text-xs',
    md: 'text-sm',
    lg: 'text-base'
  };

  return (
    <div className={`flex items-center space-x-2 animate-fade-in ${className}`}>
      {/* Bot Avatar */}
      <div 
        className={`
          ${sizeClasses[size]} 
          rounded-full 
          flex items-center justify-center 
          font-semibold text-white
          shadow-md
          transition-all duration-300 ease-in-out
          hover:scale-110 hover:shadow-lg
        `}
        style={{ backgroundColor: profile.color }}
      >
        {profile.avatar}
      </div>
      
      {/* Bot Name */}
      {showName && (
        <span className={`
          font-medium text-gray-600 
          ${textSizeClasses[size]}
          animate-slide-in-left
          transition-colors duration-200
        `}>
          {profile.name}
        </span>
      )}
    </div>
  );
};

export default BotAvatar;
