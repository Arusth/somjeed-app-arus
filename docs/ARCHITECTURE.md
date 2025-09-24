# ChatBot Application - High Level Architecture

## Overview
This is a modern full-stack chatbot application built with Spring Boot backend and Next.js 15 frontend, designed to be containerized and easily deployable.

## System Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│                 │    │                 │    │                 │
│   Frontend      │    │    Backend      │    │   Database      │
│   (Next.js 15)  │◄──►│  (Spring Boot)  │◄──►│  (PostgreSQL)   │
│   Port: 3000    │    │   Port: 8080    │    │   Port: 5432    │
│                 │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Components and Their Relationships

### 1. Frontend Layer (Next.js 15)
- **Technology**: Next.js 15 with TypeScript, Tailwind CSS
- **Port**: 3000
- **Responsibilities**:
  - User interface for chat interactions
  - Real-time message display
  - API communication with backend
  - Responsive design for multiple devices

**Key Components**:
- `ChatInterface`: Main chat component managing message flow
- `MessageBubble`: Individual message display component
- `MessageInput`: User input component with validation
- `Header`: Application navigation and branding
- `chatService`: API service layer for backend communication

### 2. Backend Layer (Spring Boot)
- **Technology**: Spring Boot 3.2.0 with Java 17, Maven
- **Port**: 8080
- **Responsibilities**:
  - REST API endpoints for chat functionality
  - Business logic processing
  - Database operations
  - Message persistence
  - Health monitoring

**Key Components**:
- `ChatController`: REST endpoints for chat operations
- `ChatService`: Business logic for message processing
- `ChatMessage`: Entity model for database persistence
- `ChatMessageRepository`: Data access layer
- `DTOs`: Data transfer objects for API communication

**API Endpoints**:
- `POST /api/chat/message`: Send and receive chat messages
- `GET /api/chat/health`: Health check endpoint
- `GET /actuator/health`: Spring Boot actuator health endpoint

### 3. Database Layer (PostgreSQL)
- **Technology**: PostgreSQL 15
- **Port**: 5432
- **Responsibilities**:
  - Persistent storage for chat messages
  - User session management
  - Data integrity and consistency

**Schema**:
```sql
CREATE TABLE chat_messages (
    id BIGSERIAL PRIMARY KEY,
    message VARCHAR(1000) NOT NULL,
    sender VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP NOT NULL
);
```

## Data Flow

### Message Sending Flow
1. User types message in `MessageInput` component
2. Frontend validates input and sends POST request to `/api/chat/message`
3. `ChatController` receives request and delegates to `ChatService`
4. `ChatService` processes message and saves to database via `ChatMessageRepository`
5. Service generates bot response and saves it to database
6. Response is returned to frontend as `ChatResponse` DTO
7. Frontend updates UI with new messages

### Component Communication
```
User Input → MessageInput → ChatInterface → chatService → Backend API
                                ↓
Frontend State ← MessageBubble ← ChatInterface ← API Response ← Backend
```

## Technology Stack

### Frontend
- **Framework**: Next.js 15 (React 18)
- **Language**: TypeScript
- **Styling**: Tailwind CSS
- **HTTP Client**: Axios
- **Testing**: Jest, React Testing Library

### Backend
- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Build Tool**: Maven
- **Database**: PostgreSQL (Production), H2 (Development)
- **Testing**: JUnit 5, Mockito, Spring Boot Test

### Infrastructure
- **Containerization**: Docker & Docker Compose
- **Database**: PostgreSQL 15
- **Reverse Proxy**: Built-in Next.js rewrites for development

## Security Considerations

### Current Implementation
- Input validation on both frontend and backend
- CORS configuration for cross-origin requests
- SQL injection prevention through JPA/Hibernate

### Future Enhancements
- Authentication and authorization
- Rate limiting
- Input sanitization
- HTTPS enforcement
- API key management

## Scalability Considerations

### Current Architecture
- Stateless backend design
- Database connection pooling
- Container-based deployment

### Future Improvements
- Load balancing
- Database replication
- Caching layer (Redis)
- Message queuing for async processing
- Microservices architecture

## Deployment Architecture

### Development Environment
```
┌─────────────────┐
│   Developer     │
│   Machine       │
├─────────────────┤
│ Frontend:3000   │
│ Backend:8080    │
│ Database:H2     │
└─────────────────┘
```

### Production Environment (Docker Compose)
```
┌─────────────────────────────────────────┐
│            Docker Host                  │
├─────────────────────────────────────────┤
│  ┌─────────────┐ ┌─────────────┐       │
│  │ Frontend    │ │ Backend     │       │
│  │ Container   │ │ Container   │       │
│  │ :3000       │ │ :8080       │       │
│  └─────────────┘ └─────────────┘       │
│         │              │               │
│         └──────────────┼───────────────┤
│                        │               │
│  ┌─────────────────────▼─────────────┐ │
│  │     PostgreSQL Container         │ │
│  │           :5432                  │ │
│  └─────────────────────────────────┘ │
└─────────────────────────────────────────┘
```

## Monitoring and Health Checks

### Health Endpoints
- Frontend: Built-in Next.js health monitoring
- Backend: Spring Boot Actuator (`/actuator/health`)
- Database: PostgreSQL health checks in Docker Compose

### Logging
- Frontend: Console logging and error boundaries
- Backend: Structured logging with different levels
- Database: PostgreSQL logs

## Testing Strategy

### Unit Testing
- **Frontend**: Jest + React Testing Library
- **Backend**: JUnit 5 + Mockito + Spring Boot Test

### Integration Testing
- API endpoint testing
- Database integration tests
- Container health checks

### Test Coverage
- Code coverage reporting with JaCoCo (Backend)
- Jest coverage reports (Frontend)

## Development Workflow

1. **Local Development**: Use H2 database for quick setup
2. **Integration Testing**: Use Docker Compose for full stack testing
3. **Production Deployment**: Docker Compose with PostgreSQL

## Configuration Management

### Environment-Specific Configurations
- **Development**: `application.yml` (H2 database)
- **Docker**: `application-docker.yml` (PostgreSQL)
- **Frontend**: Environment variables for API URLs

### Key Configuration Points
- Database connection strings
- API base URLs
- CORS origins
- Logging levels
- Health check intervals
