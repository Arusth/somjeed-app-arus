'use client'

import { useState, useRef, useEffect } from 'react';
import { chatService } from '@/services/chatService';
import MessageBubble from './MessageBubble';
import MessageInput from './MessageInput';
import LoadingIndicator from './LoadingIndicator';
import GreetingMessage from './GreetingMessage';
import type { ChatMessage, BotProfile } from '@/types/chat';

interface Message {
  id: string;
  text: string;
  sender: 'user' | 'bot';
  timestamp: Date;
}

export default function ChatInterface() {
  const [messages, setMessages] = useState<Message[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [showGreeting, setShowGreeting] = useState(true);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  // Bot profile configuration
  const botProfile: BotProfile = {
    name: 'Somjeed',
    avatar: '🤖',
    color: '#22c55e'
  };

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }

  useEffect(() => {
    scrollToBottom()
  }, [messages])

  const handleSendMessage = async (text: string) => {
    if (!text.trim()) return;

    // Hide greeting when first message is sent
    if (showGreeting) {
      setShowGreeting(false);
    }

    // Add user message
    const userMessage: Message = {
      id: Date.now().toString(),
      text: text.trim(),
      sender: 'user',
      timestamp: new Date()
    };

    setMessages(prev => [...prev, userMessage]);
    setIsLoading(true);

    try {
      // Simulate a slight delay for better UX
      await new Promise(resolve => setTimeout(resolve, 500));
      
      // Send message to backend
      const response = await chatService.sendMessage(text.trim());
      
      // Add bot response with animation delay
      setTimeout(() => {
        const botMessage: Message = {
          id: (Date.now() + 1).toString(),
          text: response.message,
          sender: 'bot',
          timestamp: new Date(response.timestamp)
        };

        setMessages(prev => [...prev, botMessage]);
        setIsLoading(false);
      }, 300);
      
    } catch (error) {
      console.error('Error sending message:', error);
      
      // Add error message with delay
      setTimeout(() => {
        const errorMessage: Message = {
          id: (Date.now() + 1).toString(),
          text: 'Sorry, I encountered an error. Please try again.',
          sender: 'bot',
          timestamp: new Date()
        };

        setMessages(prev => [...prev, errorMessage]);
        setIsLoading(false);
      }, 300);
    }
  };

  return (
    <div className="bg-white rounded-lg shadow-lg overflow-hidden transition-all duration-300 ease-in-out hover:shadow-xl">
      {/* Chat Messages */}
      <div className="h-96 overflow-y-auto p-4 space-y-4 scroll-smooth custom-scrollbar scrollbar-thin scrollbar-webkit">
        {/* Show greeting message when no conversation has started */}
        {showGreeting && messages.length === 0 && (
          <GreetingMessage 
            botProfile={botProfile}
            autoFetch={true}
            className="mb-4"
          />
        )}

        {/* Show conversation messages */}
        {messages.map((message, index) => (
          <div
            key={message.id}
            style={{ animationDelay: `${index * 100}ms` }}
          >
            <MessageBubble
              message={message.text}
              sender={message.sender}
              timestamp={message.timestamp}
              botProfile={message.sender === 'bot' ? botProfile : undefined}
              isAnimated={true}
            />
          </div>
        ))}
        
        {/* Enhanced Loading Indicator */}
        {isLoading && (
          <LoadingIndicator 
            botProfile={botProfile} 
            variant="typing"
            className="animate-fade-in"
          />
        )}
        
        <div ref={messagesEndRef} />
      </div>

      {/* Message Input */}
      <div className="border-t border-gray-200 p-4 bg-gray-50 transition-colors duration-200">
        <MessageInput 
          onSendMessage={handleSendMessage} 
          disabled={isLoading} 
        />
      </div>
    </div>
  );
}
