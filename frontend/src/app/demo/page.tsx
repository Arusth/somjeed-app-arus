'use client';

import { useState } from 'react';
import GreetingMessage from '@/components/GreetingMessage';
import { useGreeting } from '@/hooks/useGreeting';

/**
 * Demo page showcasing the Somjeed greeting feature
 * Interactive demonstration with refresh capability and technical documentation
 */
export default function DemoPage() {
  const [greetingCount, setGreetingCount] = useState(0);
  const { greeting, isLoading, error, fetchGreeting } = useGreeting();

  const handleGreetingLoaded = (message: string) => {
    console.log('Greeting loaded:', message);
  };

  const handleRefreshGreeting = async () => {
    await fetchGreeting();
    setGreetingCount(prev => prev + 1);
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 py-8 px-4">
      <div className="max-w-4xl mx-auto">
        {/* Header */}
        <div className="text-center mb-8">
          <h1 className="text-4xl font-bold text-gray-900 mb-2">
            Somjeed Greeting Demo
          </h1>
          <p className="text-lg text-gray-600">
            Contextual greetings with time-based and weather-aware responses
          </p>
        </div>

        {/* Main Demo Section */}
        <div className="bg-white rounded-xl shadow-lg p-6 mb-8">
          <h2 className="text-2xl font-semibold text-gray-800 mb-4">
            Interactive Greeting Demo
          </h2>
          
          <div className="bg-gray-50 rounded-lg p-6 mb-4">
            <GreetingMessage
              onGreetingLoaded={handleGreetingLoaded}
              autoFetch={true}
            />
          </div>

          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-4">
              <button
                onClick={handleRefreshGreeting}
                disabled={isLoading}
                className="bg-blue-600 hover:bg-blue-700 disabled:bg-blue-400 text-white px-4 py-2 rounded-lg transition-colors duration-200 flex items-center space-x-2"
              >
                <span>🔄</span>
                <span>{isLoading ? 'Loading...' : 'Get New Greeting'}</span>
              </button>
              
              <div className="text-sm text-gray-600">
                Greetings fetched: <span className="font-semibold">{greetingCount}</span>
              </div>
            </div>

            {error && (
              <div className="text-sm text-red-600 bg-red-50 px-3 py-1 rounded">
                Error: {error}
              </div>
            )}
          </div>
        </div>

        {/* Feature Overview */}
        <div className="grid md:grid-cols-2 gap-6 mb-8">
          <div className="bg-white rounded-xl shadow-lg p-6">
            <h3 className="text-xl font-semibold text-gray-800 mb-3">
              🕐 Time-Based Logic
            </h3>
            <ul className="space-y-2 text-gray-600">
              <li><strong>Morning:</strong> 05:00 - 11:59</li>
              <li><strong>Afternoon:</strong> 12:00 - 16:59</li>
              <li><strong>Evening:</strong> 17:00+</li>
            </ul>
          </div>

          <div className="bg-white rounded-xl shadow-lg p-6">
            <h3 className="text-xl font-semibold text-gray-800 mb-3">
              🌤️ Weather Conditions
            </h3>
            <ul className="space-y-2 text-gray-600">
              <li><strong>☀️ Sunny:</strong> "on a sunshine day!"</li>
              <li><strong>☁️ Cloudy:</strong> "a bit cloudy but I'm here to help!"</li>
              <li><strong>🌧️ Rainy:</strong> "stay dry out there!"</li>
              <li><strong>⛈️ Stormy:</strong> "let me help make your stormy day better."</li>
            </ul>
          </div>
        </div>

        {/* Current Greeting Details */}
        {greeting && (
          <div className="bg-white rounded-xl shadow-lg p-6 mb-8">
            <h3 className="text-xl font-semibold text-gray-800 mb-4">
              Current Greeting Details
            </h3>
            <div className="grid md:grid-cols-2 gap-4">
              <div className="space-y-3">
                <div>
                  <label className="text-sm font-medium text-gray-500">Message</label>
                  <p className="text-gray-800">{greeting.message}</p>
                </div>
                <div>
                  <label className="text-sm font-medium text-gray-500">Time of Day</label>
                  <p className="text-gray-800 capitalize">{greeting.timeOfDay}</p>
                </div>
              </div>
              <div className="space-y-3">
                <div>
                  <label className="text-sm font-medium text-gray-500">Weather Condition</label>
                  <p className="text-gray-800 capitalize">{greeting.weatherCondition}</p>
                </div>
                <div>
                  <label className="text-sm font-medium text-gray-500">Timestamp</label>
                  <p className="text-gray-800">{new Date(greeting.timestamp).toLocaleString()}</p>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Technical Information */}
        <div className="bg-white rounded-xl shadow-lg p-6">
          <h3 className="text-xl font-semibold text-gray-800 mb-4">
            Technical Implementation
          </h3>
          <div className="grid md:grid-cols-2 gap-6">
            <div>
              <h4 className="font-semibold text-gray-700 mb-2">Backend (Spring Boot 3.2.0)</h4>
              <ul className="text-sm text-gray-600 space-y-1">
                <li>• WeatherService: Mocked weather data</li>
                <li>• GreetingService: Time-based logic</li>
                <li>• ChatService: Greeting integration</li>
                <li>• REST API: /api/chat/greeting</li>
              </ul>
            </div>
            <div>
              <h4 className="font-semibold text-gray-700 mb-2">Frontend (Next.js 15 + TypeScript)</h4>
              <ul className="text-sm text-gray-600 space-y-1">
                <li>• GreetingMessage component</li>
                <li>• useGreeting custom hook</li>
                <li>• Type-safe API integration</li>
                <li>• Loading & error states</li>
              </ul>
            </div>
          </div>
        </div>

        {/* Navigation */}
        <div className="text-center mt-8">
          <a
            href="/"
            className="inline-flex items-center space-x-2 text-blue-600 hover:text-blue-800 transition-colors"
          >
            <span>←</span>
            <span>Back to Chat</span>
          </a>
        </div>
      </div>
    </div>
  );
}
