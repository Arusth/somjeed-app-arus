# ChatBot Application

A modern full-stack chatbot application built with Spring Boot backend and Next.js 15 frontend, designed for easy deployment with Docker Compose.

## 🚀 Quick Start

### Prerequisites
- Docker and Docker Compose
- Git

### One-Command Setup
```bash
git clone <repository-url>
cd chatbotapp
docker-compose up
```

The application will be available at:
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **Database**: PostgreSQL on port 5432

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │    Backend      │    │   Database      │
│   (Next.js 15)  │◄──►│  (Spring Boot)  │◄──►│  (PostgreSQL)   │
│   Port: 3000    │    │   Port: 8080    │    │   Port: 5432    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🛠️ Technology Stack

### Backend
- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Build Tool**: Maven
- **Database**: PostgreSQL (Production), H2 (Development)
- **Testing**: JUnit 5, Mockito, Spring Boot Test

### Frontend
- **Framework**: Next.js 15
- **Language**: TypeScript
- **Styling**: Tailwind CSS
- **HTTP Client**: Axios
- **Testing**: Jest, React Testing Library

### Infrastructure
- **Containerization**: Docker & Docker Compose
- **Database**: PostgreSQL 15

## 📁 Project Structure

```
chatbotapp/
├── backend/                    # Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/chatbotapp/
│   │   │   │   ├── controller/     # REST controllers
│   │   │   │   ├── service/        # Business logic
│   │   │   │   ├── entity/         # JPA entities
│   │   │   │   ├── repository/     # Data access layer
│   │   │   │   └── dto/            # Data transfer objects
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── application-docker.yml
│   │   └── test/                   # Unit tests
│   ├── pom.xml                     # Maven configuration
│   ├── Dockerfile
│   └── mvnw                        # Maven wrapper
├── frontend/                   # Next.js frontend
│   ├── src/
│   │   ├── app/                    # Next.js app directory
│   │   ├── components/             # React components
│   │   └── services/               # API services
│   ├── package.json
│   ├── tsconfig.json
│   ├── tailwind.config.js
│   ├── jest.config.js
│   └── Dockerfile
├── docs/
│   └── ARCHITECTURE.md             # Detailed architecture documentation
├── docker-compose.yml              # Multi-container setup
└── README.md
```

## 🚀 Development Setup

### Option 1: Docker Compose (Recommended)
```bash
# Clone the repository
git clone <repository-url>
cd chatbotapp

# Start all services
docker-compose up

# Or run in background
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### Option 2: Local Development

#### Backend Setup
```bash
cd backend

# Run with Maven wrapper (uses H2 database)
./mvnw spring-boot:run

# Or with installed Maven
mvn spring-boot:run

# Run tests
./mvnw test

# Generate test coverage report
./mvnw test jacoco:report
```

#### Frontend Setup
```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev

# Run tests
npm test

# Run tests with coverage
npm run test:coverage

# Build for production
npm run build
```

## 🧪 Testing

### Backend Testing
```bash
cd backend

# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=ChatControllerTest

# Generate coverage report
./mvnw test jacoco:report
# Report available at: target/site/jacoco/index.html
```

### Frontend Testing
```bash
cd frontend

# Run all tests
npm test

# Run tests in watch mode
npm run test:watch

# Generate coverage report
npm run test:coverage
# Report available at: coverage/lcov-report/index.html
```

## 📡 API Documentation

### Chat Endpoints

#### Send Message
```http
POST /api/chat/message
Content-Type: application/json

{
  "message": "Hello, bot!"
}
```

**Response:**
```json
{
  "message": "Echo: Hello, bot!",
  "timestamp": "2023-01-01T12:00:00Z"
}
```

#### Health Check
```http
GET /api/chat/health
```

**Response:**
```
Chat service is running
```

#### Spring Boot Actuator
```http
GET /actuator/health
```

## 🐳 Docker Commands

### Build Images
```bash
# Build backend image
docker build -t chatbot-backend ./backend

# Build frontend image
docker build -t chatbot-frontend ./frontend
```

### Run Individual Containers
```bash
# Run PostgreSQL
docker run -d --name postgres \
  -e POSTGRES_DB=chatbotdb \
  -e POSTGRES_USER=chatbot \
  -e POSTGRES_PASSWORD=chatbot123 \
  -p 5432:5432 \
  postgres:15-alpine

# Run backend (after PostgreSQL is running)
docker run -d --name backend \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/chatbotdb \
  -p 8080:8080 \
  --link postgres:postgres \
  chatbot-backend

# Run frontend
docker run -d --name frontend \
  -e NEXT_PUBLIC_API_URL=http://localhost:8080 \
  -p 3000:3000 \
  chatbot-frontend
```

## 🔧 Configuration

### Environment Variables

#### Backend
- `SPRING_PROFILES_ACTIVE`: Active Spring profile (default: `default`)
- `SPRING_DATASOURCE_URL`: Database URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password

#### Frontend
- `NEXT_PUBLIC_API_URL`: Backend API URL (default: `http://localhost:8080`)

### Database Configuration

#### Development (H2)
- **URL**: `jdbc:h2:mem:testdb`
- **Console**: http://localhost:8080/h2-console
- **Username**: `sa`
- **Password**: `password`

#### Production (PostgreSQL)
- **Host**: `postgres` (in Docker) or `localhost`
- **Port**: `5432`
- **Database**: `chatbotdb`
- **Username**: `chatbot`
- **Password**: `chatbot123`

## 🚀 Deployment

### Production Deployment with Docker Compose
```bash
# Clone repository on production server
git clone <repository-url>
cd chatbotapp

# Start services in production mode
docker-compose up -d

# Monitor logs
docker-compose logs -f

# Update application
git pull
docker-compose build
docker-compose up -d
```

### Health Monitoring
```bash
# Check service status
docker-compose ps

# Check backend health
curl http://localhost:8080/actuator/health

# Check chat service health
curl http://localhost:8080/api/chat/health

# View application logs
docker-compose logs backend
docker-compose logs frontend
```

## 🔍 Troubleshooting

### Common Issues

#### Port Already in Use
```bash
# Check what's using the port
lsof -i :3000  # Frontend
lsof -i :8080  # Backend
lsof -i :5432  # Database

# Kill process using port
kill -9 <PID>
```

#### Database Connection Issues
```bash
# Check PostgreSQL container status
docker-compose ps postgres

# View PostgreSQL logs
docker-compose logs postgres

# Connect to PostgreSQL directly
docker-compose exec postgres psql -U chatbot -d chatbotdb
```

#### Frontend Build Issues
```bash
# Clear Next.js cache
cd frontend
rm -rf .next
npm run build
```

#### Backend Build Issues
```bash
# Clean Maven cache
cd backend
./mvnw clean
./mvnw compile
```

### Docker Issues
```bash
# Remove all containers and volumes
docker-compose down -v

# Rebuild all images
docker-compose build --no-cache

# View detailed logs
docker-compose logs --tail=100 -f
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature-name`
3. Make changes and add tests
4. Run tests: `npm test` (frontend) and `./mvnw test` (backend)
5. Commit changes: `git commit -am 'Add feature'`
6. Push to branch: `git push origin feature-name`
7. Submit a pull request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

If you encounter any issues or have questions:

1. Check the [Architecture Documentation](docs/ARCHITECTURE.md)
2. Review the troubleshooting section above
3. Check existing issues in the repository
4. Create a new issue with detailed information

## 🔮 Future Enhancements

- [ ] User authentication and authorization
- [ ] Real-time messaging with WebSockets
- [ ] Message history and search
- [ ] File upload support
- [ ] Multiple chat rooms
- [ ] AI/ML integration for smarter responses
- [ ] Mobile app development
- [ ] Kubernetes deployment manifests
- [ ] CI/CD pipeline setup
- [ ] Performance monitoring and analytics
