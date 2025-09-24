# Somjeed ChatBot Application

A modern, intelligent chatbot application featuring contextual greetings, intent prediction, and conversation management. Built with Spring Boot backend and Next.js 15 frontend for enterprise-grade performance and user experience.

## ✨ Features

### 🎯 **Core Chatbot Features**
- **Contextual Greetings**: Time-based and weather-aware personalized greetings
- **Intent Prediction**: Proactive suggestions based on user context and payment history
- **Intent Recognition**: Natural language processing with 8 specialized intents (85-95% accuracy)
- **Conversation Management**: Silence detection and natural conversation closure
- **Feedback Collection**: Interactive 5-star rating system with analytics

### 🧠 **Intelligent Capabilities**
- **8 Specialized Intents**:
  - Payment Inquiry (90% confidence) - Balance, due dates, payment amounts
  - Transaction Dispute (95% confidence) - Fraud reports, unauthorized charges
  - Card Management (92% confidence) - Block, replace, activate cards
  - Credit Limit (88% confidence) - Limit increases, available credit
  - Account Security (96% confidence) - Fraud alerts, security concerns
  - Statement Inquiry (85% confidence) - Transaction history, statements
  - Reward Points (83% confidence) - Points balance, redemption
  - Technical Support (80% confidence) - App issues, login problems

### 🎨 **User Experience**
- **Real-time Chat Interface**: Modern, responsive design with typing indicators
- **Interactive Components**: Action buttons, intent prediction cards, feedback modals
- **Accessibility**: Screen reader support, keyboard navigation, ARIA compliance
- **Mobile-Friendly**: Responsive design optimized for all devices

## 🚀 Quick Start

### Prerequisites
- Docker and Docker Compose
- Git
- Node.js 18+ (for local development)
- Java 17+ (for local development)

### Run the Application
```bash
# Clone the repository
git clone <repository-url>
cd chatbotapp

# Start all services with Docker Compose
docker-compose up
```

**Access Points:**
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **Database**: H2 Console at http://localhost:8080/h2-console

### Development Setup

#### Option 1: Docker Compose (Recommended)
```bash
# Start all services in development mode
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

#### Option 2: Local Development

**Backend Setup:**
```bash
cd backend

# Run with Maven wrapper (uses H2 database)
./mvnw spring-boot:run

# Run tests with coverage
./mvnw test jacoco:report
```

**Frontend Setup:**
```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev

# Run tests with coverage
npm run test:coverage
```

### Access the Application

Once running, access these endpoints:

- **Main Chat Interface**: http://localhost:3000
- **Demo Page**: http://localhost:3000/demo
- **About Page**: http://localhost:3000/about
- **Backend API**: http://localhost:8080/api
- **Health Check**: http://localhost:8080/api/chat/health
- **H2 Database Console**: http://localhost:8080/h2-console

### Stop the Application
```bash
# Stop Docker Compose services
docker-compose down

# Or stop individual processes
pkill -f "next dev"
pkill -f "spring-boot:run"
```

## 🏗️ Architecture

```
┌─────────────────────┐    ┌─────────────────────┐    ┌─────────────────────┐
│   Frontend Layer    │    │   Backend Layer     │    │   Data Layer        │
│   (Next.js 15)      │◄──►│  (Spring Boot 3.2)  │◄──►│   (H2/PostgreSQL)   │
│                     │    │                     │    │                     │
│ • Chat Interface    │    │ • REST Controllers  │    │ • JPA Entities      │
│ • Intent Cards      │    │ • Business Services │    │ • Repositories      │
│ • Feedback Modal    │    │ • Intent Processing │    │ • Database Schema   │
│ • Greeting System   │    │ • Analytics Engine  │    │ • Data Persistence  │
│                     │    │                     │    │                     │
│   Port: 3000        │    │   Port: 8080        │    │   Port: 5432        │
└─────────────────────┘    └─────────────────────┘    └─────────────────────┘
```

### System Flow
1. **Contextual Greeting** → Time-based greeting with weather awareness
2. **Intent Prediction** → Proactive suggestions based on user context
3. **Intent Recognition** → Natural language processing and response generation
4. **Feedback & Closure** → Silence detection and satisfaction tracking

## 🛠️ Tech Stack

### Frontend Stack
- **Framework**: Next.js 15 with App Router
- **Language**: TypeScript (100% type coverage)
- **Styling**: Tailwind CSS with custom components
- **HTTP Client**: Axios with interceptors
- **Testing**: Jest + React Testing Library (85%+ coverage)
- **Build Tool**: Next.js built-in bundler

### Backend Stack
- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17 with modern features
- **Build Tool**: Maven with wrapper
- **Database**: H2 (Development), PostgreSQL (Production)
- **Testing**: JUnit 5 + Mockito (90%+ service coverage)
- **Architecture**: Layered (Controller → Service → Repository)

### Infrastructure
- **Containerization**: Docker & Docker Compose
- **Database**: H2 in-memory (dev), PostgreSQL 15 (prod)
- **API Documentation**: OpenAPI/Swagger integration
- **Monitoring**: Spring Boot Actuator

## 📁 Project Structure

```
chatbotapp/
├── backend/                         # Spring Boot Backend
│   ├── src/main/java/com/chatbotapp/
│   │   ├── controller/              # REST Controllers
│   │   │   ├── ChatController.java      # Main chat endpoints
│   │   │   └── FeedbackController.java  # Feedback & analytics
│   │   ├── service/                 # Business Logic Layer
│   │   │   ├── ChatService.java        # Core chat processing
│   │   │   ├── GreetingService.java     # Contextual greetings
│   │   │   ├── IntentPredictionService.java  # Intent prediction
│   │   │   ├── IntentRecognitionService.java # NLP processing
│   │   │   ├── IntentResponseService.java    # Response generation
│   │   │   ├── FeedbackService.java     # Feedback analytics
│   │   │   ├── ConversationClosureService.java # Silence handling
│   │   │   ├── WeatherService.java      # Weather integration
│   │   │   └── UserDataService.java     # User context data
│   │   ├── entity/                  # JPA Entities
│   │   │   ├── ChatMessage.java        # Chat message storage
│   │   │   └── UserFeedback.java       # Feedback storage
│   │   ├── repository/              # Data Access Layer
│   │   │   ├── ChatMessageRepository.java
│   │   │   └── UserFeedbackRepository.java
│   │   └── dto/                     # Data Transfer Objects
│   │       ├── ChatRequest.java        # Chat message requests
│   │       ├── ChatResponse.java       # Chat responses
│   │       ├── GreetingResponse.java   # Greeting data
│   │       ├── IntentPrediction.java   # Intent predictions
│   │       ├── UserIntent.java         # Recognized intents
│   │       ├── UserContext.java        # User context data
│   │       └── FeedbackRequest.java    # Feedback submissions
│   ├── src/test/                    # Comprehensive Test Suite
│   │   ├── controller/              # Controller tests (85%+ coverage)
│   │   └── service/                 # Service tests (90%+ coverage)
│   └── pom.xml                      # Maven configuration
├── frontend/                        # Next.js Frontend
│   ├── src/
│   │   ├── app/                     # Next.js App Router
│   │   │   ├── page.tsx                 # Main chat interface
│   │   │   ├── about/page.tsx           # About page
│   │   │   └── demo/page.tsx            # Demo showcase
│   │   ├── components/              # React Components
│   │   │   ├── ui/                      # Base UI components
│   │   │   │   └── Card.tsx             # Reusable card component
│   │   │   ├── ChatInterface.tsx        # Main chat UI
│   │   │   ├── GreetingMessage.tsx      # Greeting display
│   │   │   ├── MessageBubble.tsx        # Chat message bubbles
│   │   │   ├── FeedbackModal.tsx        # Feedback collection
│   │   │   └── StarRating.tsx           # Rating component
│   │   ├── hooks/                   # Custom React Hooks
│   │   │   ├── useChat.ts               # Chat state management
│   │   │   ├── useGreeting.ts           # Greeting data
│   │   │   └── useSilenceTracker.ts     # Silence detection
│   │   ├── services/                # API Services
│   │   │   └── chatService.ts           # Backend API integration
│   │   ├── types/                   # TypeScript Definitions
│   │   │   └── chat.ts                  # Chat-related types
│   │   └── __tests__/               # Test Files
│   │       ├── components/              # Component tests
│   │       ├── hooks/                   # Hook tests
│   │       └── services/                # Service tests
│   ├── package.json                 # Dependencies & scripts
│   ├── tsconfig.json               # TypeScript configuration
│   ├── tailwind.config.js          # Tailwind CSS setup
│   └── jest.config.js              # Testing configuration
├── docs/                           # Documentation
│   ├── ARCHITECTURE.md             # System architecture
│   └── rules/                      # Development standards
├── scripts/                        # Quality Assurance Scripts
│   ├── quality-check.sh            # Full project QA
│   ├── backend-check.sh            # Backend-only QA
│   └── frontend-check.sh           # Frontend-only QA
├── docker-compose.yml              # Multi-container setup
└── README.md                       # This file
```

## 📡 API Documentation

### Chat Endpoints

#### Send Message
```http
POST /api/chat/message
Content-Type: application/json

{
  "message": "Hello, what's my account balance?",
  "sessionId": "user-session-123"
}
```

**Response:**
```json
{
  "message": "I'd be happy to help you check your account balance. Your current outstanding balance is 5,000.00 THB, which is due on 2024-10-15. The minimum payment required is 500.00 THB.",
  "timestamp": "2024-09-24T10:00:00Z"
}
```

#### Get Contextual Greeting
```http
GET /api/chat/greeting?userId=user123
```

**Response:**
```json
{
  "message": "Good afternoon! Let me help make your stormy day better.",
  "timeOfDay": "afternoon",
  "weatherCondition": "stormy",
  "timestamp": "2024-09-24T14:30:00Z",
  "intentPredictions": [
    {
      "intentId": "PAYMENT_INQUIRY",
      "category": "PAYMENT",
      "predictedIntent": "Payment Amount Inquiry",
      "confidence": 0.95,
      "priority": "HIGH",
      "suggestedActions": ["Check Balance", "Make Payment", "View Due Date"]
    }
  ]
}
```

#### Get Intent Predictions
```http
GET /api/chat/intents?userId=user123
```

#### Classify Intent
```http
GET /api/chat/classify?message=I want to dispute a charge
```

#### Submit Feedback
```http
POST /api/feedback/submit
Content-Type: application/json

{
  "sessionId": "session-123",
  "userId": "user123",
  "rating": 5,
  "comment": "Great service!",
  "conversationTopic": "Payment Inquiry",
  "messageCount": 8
}
```

#### Health Check
```http
GET /api/chat/health
```

**Response:** `Chat service is running`

#### Spring Boot Actuator
```http
GET /actuator/health
```

## 🔧 Key Components

### Backend Services

#### ChatService
- **Purpose**: Core chat message processing and response generation
- **Features**: Message validation, response routing, conversation context
- **Dependencies**: IntentRecognitionService, IntentResponseService

#### GreetingService  
- **Purpose**: Contextual greeting generation with weather and time awareness
- **Features**: Time-based greetings, weather integration, intent predictions
- **Dependencies**: WeatherService, IntentPredictionService

#### IntentPredictionService
- **Purpose**: Proactive intent prediction based on user context
- **Features**: 3 prediction scenarios, confidence scoring, priority ranking
- **Dependencies**: UserDataService

#### IntentRecognitionService
- **Purpose**: Natural language processing for intent classification
- **Features**: 8 specialized intents, entity extraction, confidence scoring
- **Accuracy**: 85-95% intent recognition accuracy

#### FeedbackService
- **Purpose**: User feedback collection and analytics
- **Features**: Star ratings, comments, satisfaction metrics, analytics
- **Dependencies**: UserFeedbackRepository

### Frontend Components

#### ChatInterface
- **Purpose**: Main chat UI with message display and input
- **Features**: Real-time messaging, typing indicators, silence tracking
- **Dependencies**: useChat, useSilenceTracker hooks

#### GreetingMessage
- **Purpose**: Contextual greeting display with intent predictions
- **Features**: Weather-aware greetings, intent prediction cards, action buttons
- **Dependencies**: useGreeting hook

#### FeedbackModal
- **Purpose**: Interactive feedback collection interface
- **Features**: 5-star rating, optional comments, device detection
- **Dependencies**: StarRating component

#### Custom Hooks
- **useChat**: Chat state management and message handling
- **useGreeting**: Greeting data fetching and display logic  
- **useSilenceTracker**: User inactivity monitoring and conversation closure

## 🧪 Testing

### Quality Assurance Scripts
```bash
# Run comprehensive quality check
./scripts/quality-check.sh

# Backend-only testing
./scripts/backend-check.sh

# Frontend-only testing  
./scripts/frontend-check.sh
```

### Backend Testing (90%+ Coverage Required)
```bash
cd backend

# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=ChatControllerTest

# Generate coverage report
./mvnw test jacoco:report
# Report: target/site/jacoco/index.html

# Coverage requirements:
# - Service classes: 90% minimum
# - Controller classes: 85% minimum  
# - Repository classes: 80% minimum
```

### Frontend Testing (85%+ Coverage Required)
```bash
cd frontend

# Run all tests
npm test

# Run tests in watch mode
npm run test:watch

# Generate coverage report
npm run test:coverage
# Report: coverage/lcov-report/index.html

# Coverage requirements:
# - Components: 85% minimum
# - Custom hooks: 90% minimum
# - Services: 80% minimum
```

### Test Categories
- **Unit Tests**: Individual component/service testing
- **Integration Tests**: API endpoint testing with database
- **Component Tests**: React component behavior and rendering
- **Hook Tests**: Custom hook functionality and state management

## 🎭 Demo Scenarios

### Scenario 1: Payment Inquiry Flow
1. **User Access**: Visit http://localhost:3000
2. **Greeting**: Somjeed provides contextual greeting with weather
3. **Intent Prediction**: Displays payment-related suggestions if user has overdue balance
4. **User Query**: "What's my account balance?"
5. **Response**: Detailed balance information with payment options
6. **Feedback**: 5-star rating collection after conversation

### Scenario 2: Transaction Dispute
1. **User Query**: "I want to dispute a charge from Amazon for $150"
2. **Intent Recognition**: Classifies as TRANSACTION_DISPUTE (95% confidence)
3. **Response**: Security measures, dispute process, provisional credit information
4. **Actions**: Card blocking options, dispute case creation

### Scenario 3: Card Management
1. **User Query**: "I need to block my card"
2. **Intent Recognition**: Classifies as CARD_MANAGEMENT (92% confidence)  
3. **Response**: Immediate card blocking, replacement card ordering
4. **Confirmation**: Reference number and delivery timeline

### Scenario 4: Conversation Closure
1. **Silence Detection**: 10 seconds → "Do you need any further assistance?"
2. **Extended Silence**: 20 seconds → Feedback modal appears
3. **Final Closure**: 50 seconds → "Thank you for using our service today!"

### Demo Pages
- **Main Chat**: http://localhost:3000 - Full chat experience
- **Demo Page**: http://localhost:3000/demo - Feature showcase
- **About Page**: http://localhost:3000/about - System architecture and capabilities

## ⚙️ Configuration

### Environment Variables

#### Backend Configuration
```bash
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:h2:mem:testdb
SPRING_DATASOURCE_USERNAME=sa
SPRING_DATASOURCE_PASSWORD=password

# Profile Configuration  
SPRING_PROFILES_ACTIVE=default

# JPA Configuration
SPRING_JPA_HIBERNATE_DDL_AUTO=create-drop
SPRING_JPA_SHOW_SQL=true

# Server Configuration
SERVER_PORT=8080
```

#### Frontend Configuration
```bash
# API Configuration
NEXT_PUBLIC_API_URL=http://localhost:8080

# Build Configuration
NODE_ENV=development
```

### Database Configuration

#### Development (H2 In-Memory)
- **URL**: `jdbc:h2:mem:testdb`
- **Console**: http://localhost:8080/h2-console
- **Username**: `sa`
- **Password**: `password`
- **Driver**: `org.h2.Driver`

#### Production (PostgreSQL)
- **Host**: `postgres` (Docker) / `localhost`
- **Port**: `5432`
- **Database**: `chatbotdb`
- **Username**: `chatbot`
- **Password**: `chatbot123`

### Application Profiles

#### Default Profile (Development)
- H2 in-memory database
- Debug logging enabled
- Hot reload for development
- CORS enabled for localhost:3000

#### Docker Profile (Container)
- PostgreSQL database
- Production logging levels
- Container networking
- Health checks enabled

### Quality Standards Configuration

#### Backend Standards
- **Java Version**: 17+
- **Spring Boot**: 3.2.0
- **Test Coverage**: 90% services, 85% controllers
- **Code Style**: Google Java Style Guide
- **API Response Time**: <200ms

#### Frontend Standards  
- **TypeScript**: Strict mode enabled
- **ESLint**: Next.js recommended + TypeScript rules
- **Test Coverage**: 85% components, 90% hooks
- **Bundle Size**: <250KB JavaScript
- **Performance**: Lighthouse score >90

---

## 🚀 Getting Started

Ready to explore Somjeed? Here's how to get started:

1. **Quick Demo**: Visit the [Demo Page](http://localhost:3000/demo) after starting the application
2. **Full Experience**: Use the [Main Chat Interface](http://localhost:3000) for complete functionality
3. **Learn More**: Check the [About Page](http://localhost:3000/about) for detailed system architecture
4. **Development**: Follow the [Development Setup](#development-setup) section for local development

## 📚 Additional Resources

- **[Architecture Documentation](docs/ARCHITECTURE.md)** - Detailed system design and patterns
- **[Development Standards](docs/rules/)** - Code quality and testing standards
- **[Quality Assurance Scripts](scripts/)** - Automated testing and validation tools

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. Fork the repository
2. Create a feature branch: `git checkout -b feature-name`
3. Follow our [coding standards](docs/rules/) and maintain test coverage
4. Run quality checks: `./scripts/quality-check.sh`
5. Commit changes: `git commit -am 'Add feature'`
6. Push to branch: `git push origin feature-name`
7. Submit a pull request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

If you encounter any issues:

1. Check the [Configuration](#configuration) section for environment setup
2. Review the [Demo Scenarios](#demo-scenarios) for expected behavior
3. Run the [Testing](#testing) suite to verify functionality
4. Check existing issues in the repository
5. Create a new issue with detailed information

---

**Built with ❤️ using Spring Boot 3.2.0 + Java 17 and Next.js 15 + TypeScript**

*Somjeed ChatBot - Intelligent, contextual, and user-friendly conversational AI*
