'use client'

import Link from 'next/link'
import Image from 'next/image'
import Header from '@/components/Header'
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/Card'

export default function AboutPage() {
  return (
    <main className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100">
      <Header />
      
      {/* Hero Section */}
      <section className="py-16 px-4">
        <div className="container mx-auto max-w-6xl">
          <div className="text-center mb-16">
            <h1 className="text-5xl font-bold text-gray-800 mb-6">
              Somjeed ChatBot Project
            </h1>
            <p className="text-xl text-gray-600 max-w-3xl mx-auto leading-relaxed">
              An intelligent credit card customer support chatbot built with modern technologies, 
              featuring contextual greetings, intent prediction, and comprehensive conversation management.
            </p>
          </div>
        </div>
      </section>

      {/* System Overview */}
      <section className="py-12 px-4 bg-white">
        <div className="container mx-auto max-w-6xl">
          <h2 className="text-3xl font-bold text-center text-gray-800 mb-12">
            System Architecture Overview
          </h2>
          
          <div className="grid md:grid-cols-2 gap-8 mb-12">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center">
                  <span className="text-2xl mr-3">🎯</span>
                  What is Somjeed?
                </CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-gray-600 leading-relaxed">
                  Somjeed is an intelligent chatbot designed specifically for credit card customer support. 
                  It provides personalized, context-aware assistance with payment inquiries, transaction disputes, 
                  card management, and account security - all through natural language conversations.
                </p>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle className="flex items-center">
                  <span className="text-2xl mr-3">⚡</span>
                  Key Capabilities
                </CardTitle>
              </CardHeader>
              <CardContent>
                <ul className="text-gray-600 space-y-2">
                  <li>• Time & weather-aware contextual greetings</li>
                  <li>• Proactive intent prediction based on user context</li>
                  <li>• 8 specialized credit card support intents</li>
                  <li>• Real-time conversation management</li>
                  <li>• Feedback collection and satisfaction tracking</li>
                </ul>
              </CardContent>
            </Card>
          </div>

          {/* High-Level Architecture Diagram */}
          <div className="mb-12">
            <Card>
              <CardHeader>
                <CardTitle className="text-center">
                  <span className="text-2xl mr-3">🏗️</span>
                  High-Level System Architecture
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="flex justify-center">
                  <Image 
                    src="/architecture-diagram.png" 
                    alt="Somjeed ChatBot Architecture - Frontend Layer (Next.js 15 + TypeScript) connecting to Backend Layer (Spring Boot 3.2.0 + Java 17) through RESTful API Communication"
                    width={800}
                    height={600}
                    className="max-w-full h-auto rounded-lg shadow-lg border border-gray-200"
                    style={{ maxHeight: '600px' }}
                  />
                </div>
                <p className="text-center text-sm text-gray-500 mt-4">
                  Complete system architecture showing Frontend Layer, API Communication, and Backend Layer with data flow
                </p>
              </CardContent>
            </Card>
          </div>
        </div>
      </section>

      {/* System Flow */}
      <section className="py-12 px-4 bg-gradient-to-r from-blue-50 to-indigo-50">
        <div className="container mx-auto max-w-6xl">
          <h2 className="text-3xl font-bold text-center text-gray-800 mb-12">
            System Flow & User Journey
          </h2>
          
          <div className="grid md:grid-cols-4 gap-6">
            <div className="text-center">
              <div className="w-16 h-16 bg-blue-500 rounded-full flex items-center justify-center mx-auto mb-4">
                <span className="text-white font-bold text-xl">1</span>
              </div>
              <h3 className="font-semibold text-gray-800 mb-2">Contextual Greeting</h3>
              <p className="text-sm text-gray-600">
                Time-based greeting with weather awareness and personalized welcome message
              </p>
            </div>
            
            <div className="text-center">
              <div className="w-16 h-16 bg-indigo-500 rounded-full flex items-center justify-center mx-auto mb-4">
                <span className="text-white font-bold text-xl">2</span>
              </div>
              <h3 className="font-semibold text-gray-800 mb-2">Intent Prediction</h3>
              <p className="text-sm text-gray-600">
                Proactive suggestions based on payment history, transactions, and account status
              </p>
            </div>
            
            <div className="text-center">
              <div className="w-16 h-16 bg-purple-500 rounded-full flex items-center justify-center mx-auto mb-4">
                <span className="text-white font-bold text-xl">3</span>
              </div>
              <h3 className="font-semibold text-gray-800 mb-2">Intent Recognition</h3>
              <p className="text-sm text-gray-600">
                Natural language processing to understand user queries and classify intents
              </p>
            </div>
            
            <div className="text-center">
              <div className="w-16 h-16 bg-pink-500 rounded-full flex items-center justify-center mx-auto mb-4">
                <span className="text-white font-bold text-xl">4</span>
              </div>
              <h3 className="font-semibold text-gray-800 mb-2">Feedback & Closure</h3>
              <p className="text-sm text-gray-600">
                Silence detection, conversation closure, and satisfaction feedback collection
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Core Features */}
      <section className="py-12 px-4 bg-white text-gray-600">
        <div className="container mx-auto max-w-6xl">
          <h2 className="text-3xl font-bold text-center text-gray-800 mb-12">
            Core Features Integration
          </h2>
          
          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center">
                  <span className="text-2xl mr-3">🌤️</span>
                  Contextual Greetings
                </CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-gray-600 mb-4">
                  Dynamic greetings that adapt to time of day and weather conditions.
                </p>
                <div className="bg-gray-50 p-3 rounded-lg text-sm">
                  <strong>Integration:</strong> WeatherService + GreetingService → Personalized welcome experience
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle className="flex items-center">
                  <span className="text-2xl mr-3">🎯</span>
                  Intent Prediction
                </CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-gray-600 mb-4">
                  Proactive suggestions based on user context and payment history.
                </p>
                <div className="bg-gray-50 p-3 rounded-lg text-sm">
                  <strong>Integration:</strong> UserDataService + IntentPredictionService → Smart recommendations
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle className="flex items-center">
                  <span className="text-2xl mr-3">🧠</span>
                  Intent Recognition
                </CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-gray-600 mb-4">
                  8 specialized intents for credit card support with 85-95% accuracy.
                </p>
                <div className="bg-gray-50 p-3 rounded-lg text-sm">
                  <strong>Integration:</strong> IntentRecognitionService + IntentResponseService → Contextual responses
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle className="flex items-center">
                  <span className="text-2xl mr-3">⏰</span>
                  Silence Tracking
                </CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-gray-600 mb-4">
                  Monitors user inactivity with 3-stage conversation closure approach.
                </p>
                <div className="bg-gray-50 p-3 rounded-lg text-sm">
                  <strong>Integration:</strong> ConversationClosureService + Frontend hooks → Natural conversation flow
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle className="flex items-center">
                  <span className="text-2xl mr-3">⭐</span>
                  Feedback System
                </CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-gray-600 mb-4">
                  5-star rating system with analytics for service improvement.
                </p>
                <div className="bg-gray-50 p-3 rounded-lg text-sm">
                  <strong>Integration:</strong> FeedbackService + StarRating component → User satisfaction tracking
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle className="flex items-center">
                  <span className="text-2xl mr-3">🔄</span>
                  Real-time Updates
                </CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-gray-600 mb-4">
                  Live conversation updates with optimistic UI and error handling.
                </p>
                <div className="bg-gray-50 p-3 rounded-lg text-sm">
                  <strong>Integration:</strong> React hooks + API services → Seamless user experience
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </section>

      {/* Technical Architecture */}
      <section className="py-12 px-4 bg-gradient-to-r from-gray-50 to-blue-50">
        <div className="container mx-auto max-w-6xl">
          <h2 className="text-3xl font-bold text-center text-gray-800 mb-12">
            Technical Architecture & Tech Stack
          </h2>
          
          <div className="grid md:grid-cols-2 gap-8">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center">
                  <span className="text-2xl mr-3">⚛️</span>
                  Frontend Stack
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  <div>
                    <h4 className="font-semibold text-gray-800">Next.js 15 + TypeScript</h4>
                    <p className="text-sm text-gray-600">Modern React framework with App Router, Server Actions, and type safety</p>
                  </div>
                  <div>
                    <h4 className="font-semibold text-gray-800">Tailwind CSS</h4>
                    <p className="text-sm text-gray-600">Utility-first CSS framework for rapid UI development</p>
                  </div>
                  <div>
                    <h4 className="font-semibold text-gray-800">React Testing Library + Jest</h4>
                    <p className="text-sm text-gray-600">Comprehensive testing with 85%+ coverage requirement</p>
                  </div>
                  <div>
                    <h4 className="font-semibold text-gray-800">Custom Hooks Architecture</h4>
                    <p className="text-sm text-gray-600">Reusable logic with useChat, useGreeting, useSilenceTracker</p>
                  </div>
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle className="flex items-center">
                  <span className="text-2xl mr-3">☕</span>
                  Backend Stack
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  <div>
                    <h4 className="font-semibold text-gray-800">Spring Boot 3.2.0 + Java 17</h4>
                    <p className="text-sm text-gray-600">Enterprise-grade framework with modern Java features</p>
                  </div>
                  <div>
                    <h4 className="font-semibold text-gray-800">JPA/Hibernate + H2 Database</h4>
                    <p className="text-sm text-gray-600">ORM with in-memory database for development</p>
                  </div>
                  <div>
                    <h4 className="font-semibold text-gray-800">JUnit 5 + Mockito</h4>
                    <p className="text-sm text-gray-600">Comprehensive testing with 90%+ service coverage requirement</p>
                  </div>
                  <div>
                    <h4 className="font-semibold text-gray-800">Layered Architecture</h4>
                    <p className="text-sm text-gray-600">Controller → Service → Repository pattern with DTOs</p>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </section>

      {/* Development Standards */}
      <section className="py-12 px-4 bg-white">
        <div className="container mx-auto max-w-6xl">
          <h2 className="text-3xl font-bold text-center text-gray-800 mb-12">
            Development Standards & Quality Metrics
          </h2>
          
          <div className="grid md:grid-cols-3 gap-8">
            <Card>
              <CardHeader>
                <CardTitle className="text-center">
                  <div className="text-3xl font-bold text-green-600 mb-2">90%+</div>
                  Test Coverage
                </CardTitle>
              </CardHeader>
              <CardContent className="text-center">
                <p className="text-gray-600">
                  Comprehensive unit and integration testing with strict coverage requirements
                </p>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle className="text-center">
                  <div className="text-3xl font-bold text-blue-600 mb-2">&lt;200ms</div>
                  API Response
                </CardTitle>
              </CardHeader>
              <CardContent className="text-center">
                <p className="text-gray-600">
                  Fast API responses for real-time chat experience and intent recognition
                </p>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle className="text-center">
                  <div className="text-3xl font-bold text-purple-600 mb-2">100%</div>
                  TypeScript
                </CardTitle>
              </CardHeader>
              <CardContent className="text-center">
                <p className="text-gray-600">
                  Full type safety with strict TypeScript configuration and no any types
                </p>
              </CardContent>
            </Card>
          </div>
        </div>
      </section>

      {/* Intent Recognition Details */}
      <section className="py-12 px-4 bg-gradient-to-r from-indigo-50 to-purple-50">
        <div className="container mx-auto max-w-6xl">
          <h2 className="text-3xl font-bold text-center text-gray-800 mb-12">
            Intent Recognition System
          </h2>
          
          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6">
            {[
              { icon: '💳', title: 'Payment Inquiry', confidence: '90%', desc: 'Balance, due dates, payment amounts' },
              { icon: '🚨', title: 'Transaction Dispute', confidence: '95%', desc: 'Fraud reports, unauthorized charges' },
              { icon: '🔒', title: 'Card Management', confidence: '92%', desc: 'Block, replace, activate cards' },
              { icon: '📊', title: 'Credit Limit', confidence: '88%', desc: 'Limit increases, available credit' },
              { icon: '🛡️', title: 'Account Security', confidence: '96%', desc: 'Fraud alerts, security concerns' },
              { icon: '📄', title: 'Statement Inquiry', confidence: '85%', desc: 'Transaction history, statements' },
              { icon: '🎁', title: 'Reward Points', confidence: '83%', desc: 'Points balance, redemption' },
              { icon: '🔧', title: 'Technical Support', confidence: '80%', desc: 'App issues, login problems' }
            ].map((intent, index) => (
              <Card key={index}>
                <CardHeader>
                  <CardTitle className="text-center">
                    <div className="text-3xl mb-2">{intent.icon}</div>
                    <div className="text-sm font-semibold">{intent.title}</div>
                  </CardTitle>
                </CardHeader>
                <CardContent className="text-center">
                  <div className="text-lg font-bold text-green-600 mb-2">{intent.confidence}</div>
                  <p className="text-xs text-gray-600">{intent.desc}</p>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Quality Assurance */}
      <section className="py-12 px-4 bg-white">
        <div className="container mx-auto max-w-6xl">
          <h2 className="text-3xl font-bold text-center text-gray-800 mb-12">
            Quality Assurance & Security
          </h2>
          
          <div className="grid md:grid-cols-2 gap-8">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center">
                  <span className="text-2xl mr-3">🔐</span>
                  Security Features
                </CardTitle>
              </CardHeader>
              <CardContent>
                <ul className="space-y-2 text-gray-600">
                  <li>• Input validation and sanitization</li>
                  <li>• CSRF protection with Server Actions</li>
                  <li>• Secure API endpoints with proper error handling</li>
                  <li>• No sensitive data exposure in client-side code</li>
                  <li>• Rate limiting for form submissions</li>
                </ul>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle className="flex items-center">
                  <span className="text-2xl mr-3">⚡</span>
                  Performance Optimization
                </CardTitle>
              </CardHeader>
              <CardContent>
                <ul className="space-y-2 text-gray-600">
                  <li>• Code splitting with dynamic imports</li>
                  <li>• Optimized images with Next.js Image component</li>
                  <li>• Lazy loading for components</li>
                  <li>• Connection pooling and database optimization</li>
                  <li>• Bundle size optimization (&lt;250KB)</li>
                </ul>
              </CardContent>
            </Card>
          </div>
        </div>
      </section>

      {/* Call to Action */}
      <section className="py-16 px-4 bg-gradient-to-r from-blue-600 to-indigo-600 text-white">
        <div className="container mx-auto max-w-4xl text-center">
          <h2 className="text-3xl font-bold mb-6">
            Experience Somjeed in Action
          </h2>
          <p className="text-xl mb-8 opacity-90">
            Try our intelligent chatbot and see how it handles credit card customer support with contextual awareness and proactive assistance.
          </p>
          <div className="flex justify-center">
            <Link 
              href="/" 
              className="bg-white text-blue-600 px-8 py-3 rounded-lg font-semibold hover:bg-gray-100 transition-colors"
            >
              Start Chatting
            </Link>
          </div>
        </div>
      </section>
    </main>
  )
}
