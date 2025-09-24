'use client'

import ChatInterface from '@/components/ChatInterface'
import Header from '@/components/Header'

export default function Home() {
  return (
    <main className="h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex flex-col">
      <Header />
      <div className="flex-1 container mx-auto px-4 py-4 flex flex-col">
        <div className="max-w-4xl mx-auto w-full flex flex-col h-full">
          <div className="text-center mb-4 flex-shrink-0">
            <h1 className="text-3xl font-bold text-gray-800 mb-2">
              Welcome to Somjeed App
            </h1>
            <p className="text-base text-gray-600">
              Start a conversation with our intelligent chatbot 
            </p>
          </div>
          <div className="flex-1">
            <ChatInterface />
          </div>
        </div>
      </div>
    </main>
  )
}
