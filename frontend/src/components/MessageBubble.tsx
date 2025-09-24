'use client'

import ClientOnly from './ClientOnly'

interface MessageBubbleProps {
  message: string
  sender: 'user' | 'bot'
  timestamp: Date
}

export default function MessageBubble({ message, sender, timestamp }: MessageBubbleProps) {
  const isUser = sender === 'user'
  
  return (
    <div className={`flex ${isUser ? 'justify-end' : 'justify-start'}`}>
      <div className={`max-w-xs lg:max-w-md px-4 py-2 rounded-lg ${
        isUser 
          ? 'bg-primary-500 text-white' 
          : 'bg-gray-100 text-gray-800'
      }`}>
        <p className="text-sm">{message}</p>
        <ClientOnly fallback={<p className={`text-xs mt-1 ${isUser ? 'text-primary-100' : 'text-gray-500'}`}>--:--</p>}>
          <p className={`text-xs mt-1 ${
            isUser ? 'text-primary-100' : 'text-gray-500'
          }`}>
            {timestamp.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
          </p>
        </ClientOnly>
      </div>
    </div>
  )
}
