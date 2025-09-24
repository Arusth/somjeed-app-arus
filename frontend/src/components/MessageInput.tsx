'use client'

import { useState, KeyboardEvent, useRef, useEffect } from 'react';
import type { FC } from 'react';

interface MessageInputProps {
  onSendMessage: (message: string) => void;
  disabled?: boolean;
}

/**
 * MessageInput component with enhanced animations and UX
 * Features auto-resize, smooth transitions, and loading states
 */
export const MessageInput: FC<MessageInputProps> = ({ 
  onSendMessage, 
  disabled = false 
}) => {
  const [message, setMessage] = useState('');
  const [isFocused, setIsFocused] = useState(false);
  const textareaRef = useRef<HTMLTextAreaElement>(null);

  // Auto-resize textarea
  useEffect(() => {
    if (textareaRef.current) {
      // Reset height to auto to get the correct scrollHeight
      textareaRef.current.style.height = 'auto';
      
      // Calculate the new height, but don't exceed maxHeight
      const scrollHeight = textareaRef.current.scrollHeight;
      const maxHeight = 128; // 32 * 4 (max-h-32 in Tailwind)
      const newHeight = Math.min(scrollHeight, maxHeight);
      
      textareaRef.current.style.height = `${newHeight}px`;
      
      // Only show scrollbar if content exceeds maxHeight
      if (scrollHeight > maxHeight) {
        textareaRef.current.style.overflowY = 'auto';
        textareaRef.current.classList.remove('scrollbar-hide');
        textareaRef.current.classList.add('custom-scrollbar');
      } else {
        textareaRef.current.style.overflowY = 'hidden';
        textareaRef.current.classList.add('scrollbar-hide');
        textareaRef.current.classList.remove('custom-scrollbar');
      }
    }
  }, [message]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (message.trim() && !disabled) {
      onSendMessage(message);
      setMessage('');
    }
  };

  const handleKeyPress = (e: KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSubmit(e);
    }
  };

  const handleFocus = () => setIsFocused(true);
  const handleBlur = () => setIsFocused(false);

  return (
    <form onSubmit={handleSubmit} className="flex items-center space-x-3">
      {/* Message Input */}
      <div className="flex-1 relative">
        <textarea
          ref={textareaRef}
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          onKeyPress={handleKeyPress}
          onFocus={handleFocus}
          onBlur={handleBlur}
          placeholder="Type your message..."
          disabled={disabled}
          rows={1}
          className={`
            w-full text-gray-800 resize-none border rounded-lg px-4 py-3
            transition-all duration-200 ease-in-out
            focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent
            disabled:bg-gray-100 disabled:cursor-not-allowed
            scrollbar-hide
            ${isFocused 
              ? 'border-primary-300 shadow-md transform scale-[1.01]' 
              : 'border-gray-300 hover:border-gray-400'
            }
            ${disabled ? 'opacity-50' : ''}
            max-h-32 min-h-[48px]
          `}
          style={{ 
            transition: 'all 0.2s ease-in-out',
            maxHeight: '128px'
          }}
        />
        
        {/* Character count indicator */}
        {message.length > 100 && (
          <div className="absolute bottom-1 right-2 text-xs text-gray-400 animate-fade-in">
            {message.length}/1000
          </div>
        )}
      </div>

      {/* Send Button */}
      <button
        type="submit"
        disabled={!message.trim() || disabled}
        className={`
          px-6 py-3 rounded-lg font-medium h-12 flex items-center justify-center
          transition-all duration-200 ease-in-out
          focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2
          transform hover:scale-105 active:scale-95
          ${!message.trim() || disabled
            ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
            : 'bg-primary-500 text-white hover:bg-primary-600 shadow-md hover:shadow-lg'
          }
        `}
      >
        {disabled ? (
          <div className="flex items-center space-x-2">
            <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
            <span>Sending...</span>
          </div>
        ) : (
          <span className="flex items-center space-x-1">
            <span>Send</span>
            <svg 
              className="w-4 h-4 transform transition-transform duration-200 group-hover:translate-x-1" 
              fill="none" 
              stroke="currentColor" 
              viewBox="0 0 24 24"
            >
              <path 
                strokeLinecap="round" 
                strokeLinejoin="round" 
                strokeWidth={2} 
                d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" 
              />
            </svg>
          </span>
        )}
      </button>
    </form>
  );
};

export default MessageInput;
