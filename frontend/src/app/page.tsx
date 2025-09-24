'use client'

import ChatInterface from '@/components/ChatInterface'
import Header from '@/components/Header'

export default function Home() {
  return (
    <main className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100">
      <Header />
      <div className="container mx-auto px-4 py-8">
        <div className="max-w-4xl mx-auto">
          <div className="text-center mb-8">
            <h1 className="text-4xl font-bold text-gray-800 mb-4">
              Welcome to Somjeed App
            </h1>
            <p className="text-lg text-gray-600">
              Start a conversation with our intelligent chatbot 
            </p>
          </div>
          <ChatInterface />
        </div>
      </div>
    </main>
  )
}
