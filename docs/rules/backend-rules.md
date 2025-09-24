# Backend Development Rules
## Spring Boot 3.2.0 + Java 17 Standards

---

## 🎯 General Code Style Rules

### File Size and Organization
- **Keep files under 200 lines of code** - split larger files into smaller focused modules
- **Single Responsibility Principle** - each file should have one clear purpose
- **Consistent naming conventions**:
  - Files: `PascalCase` for classes (e.g., `ChatService.java`)
  - Packages: `lowercase` (e.g., `com.chatbotapp.service`)
  - Classes: `PascalCase`
  - Methods/Variables: `camelCase`
  - Constants: `UPPER_SNAKE_CASE`

### Code Quality Principles
- **Follow DRY principle** and separate concerns consistently
- **Use service pattern** for business logic - keep controllers thin
- **Create reusable utilities** where applicable
- **Write meaningful Javadoc comments**, avoid temporary/TODO comments
- **Prefer composition over inheritance**
- **Extract complex logic** into dedicated service modules

---

## ☕ Java Best Practices

### Naming Conventions
- **Classes**: `PascalCase` (e.g., `ChatController`, `MessageProcessor`)
- **Methods/Variables**: `camelCase` (e.g., `processMessage`, `userInput`)
- **Constants**: `UPPER_SNAKE_CASE` (e.g., `MAX_MESSAGE_LENGTH`, `DEFAULT_TIMEOUT`)
- **Packages**: `lowercase` (e.g., `com.chatbotapp.service`, `com.chatbotapp.dto.request`)

### Code Structure Standards
- **Use final keyword** for immutable variables
- **Prefer constructor injection** over field injection
- **Use Optional** for nullable return types
- **Implement proper equals() and hashCode()** for entities
- **Use builder pattern** for complex object creation

---

## 🏗️ Spring Boot Architecture Rules

### Controller Layer
- **Keep controllers thin** - delegate business logic to services
- **Use proper HTTP status codes** and response types
- **Implement comprehensive validation** using Bean Validation
- **Handle exceptions** with `@ControllerAdvice`

Example:
```java
// controller/ChatController.java
@RestController
@RequestMapping("/api/chat")
@Validated
public class ChatController {
    
    private final ChatService chatService;
    
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }
    
    /**
     * Processes chat message and returns bot response
     * @param request validated chat request
     * @return ChatResponse with bot reply
     */
    @PostMapping("/message")
    public ResponseEntity<ChatResponse> sendMessage(
            @Valid @RequestBody ChatRequest request) {
        
        ChatResponse response = chatService.processMessage(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> status = Map.of("status", "UP");
        return ResponseEntity.ok(status);
    }
}
```

### Service Layer
- **Implement business logic** in service classes
- **Use @Transactional** for database operations
- **Handle exceptions** appropriately
- **Keep methods focused** on single responsibilities

Example:
```java
// service/ChatService.java
@Service
@Transactional
public class ChatService {
    
    private final ChatMessageRepository repository;
    private final MessageProcessor messageProcessor;
    
    public ChatService(ChatMessageRepository repository, 
                      MessageProcessor messageProcessor) {
        this.repository = repository;
        this.messageProcessor = messageProcessor;
    }
    
    /**
     * Processes user message and generates bot response
     * @param request the chat request containing user message
     * @return ChatResponse with bot reply
     * @throws ChatException if message processing fails
     */
    public ChatResponse processMessage(ChatRequest request) {
        // Validate input
        validateMessage(request.getMessage());
        
        // Save user message
        ChatMessage userMessage = createUserMessage(request);
        repository.save(userMessage);
        
        // Generate and save bot response
        String botReply = messageProcessor.generateResponse(request.getMessage());
        ChatMessage botMessage = createBotMessage(botReply);
        repository.save(botMessage);
        
        return ChatResponse.builder()
                .message(botReply)
                .timestamp(botMessage.getTimestamp())
                .build();
    }
    
    private void validateMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }
        if (message.length() > MAX_MESSAGE_LENGTH) {
            throw new IllegalArgumentException("Message too long");
        }
    }
}
```

### Repository Layer
- **Use Spring Data JPA** for standard CRUD operations
- **Create custom queries** using @Query annotation
- **Implement proper pagination** for large datasets
- **Use native queries** sparingly and only when necessary

Example:
```java
// repository/ChatMessageRepository.java
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    @Query("SELECT c FROM ChatMessage c WHERE c.sender = :sender ORDER BY c.timestamp DESC")
    Page<ChatMessage> findBySenderOrderByTimestampDesc(
            @Param("sender") String sender, 
            Pageable pageable);
    
    @Query("SELECT c FROM ChatMessage c WHERE c.timestamp >= :since")
    List<ChatMessage> findRecentMessages(@Param("since") LocalDateTime since);
    
    long countBySender(String sender);
}
```

---

## 📁 Backend Directory Structure

```
backend/src/main/java/com/chatbotapp/
├── controller/                # REST controllers
│   ├── ChatController.java
│   └── HealthController.java
├── service/                   # Business logic services
│   ├── ChatService.java
│   ├── MessageProcessor.java
│   └── ValidationService.java
├── repository/                # Data access layer
│   ├── ChatMessageRepository.java
│   └── UserSessionRepository.java
├── entity/                    # JPA entities
│   ├── ChatMessage.java
│   └── UserSession.java
├── dto/                       # Data transfer objects
│   ├── request/
│   │   ├── ChatRequest.java
│   │   └── ValidationRequest.java
│   └── response/
│       ├── ChatResponse.java
│       └── ErrorResponse.java
├── config/                    # Configuration classes
│   ├── WebConfig.java
│   ├── DatabaseConfig.java
│   └── SecurityConfig.java
├── exception/                 # Custom exceptions
│   ├── ChatException.java
│   ├── ValidationException.java
│   └── GlobalExceptionHandler.java
└── util/                      # Utility classes
    ├── MessageUtils.java
    └── DateTimeUtils.java
```

---

## 📝 Data Transfer Objects (DTOs)

### Request DTOs
```java
// dto/request/ChatRequest.java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    
    @NotBlank(message = "Message cannot be empty")
    @Size(max = 1000, message = "Message cannot exceed 1000 characters")
    private String message;
    
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Invalid session ID format")
    private String sessionId;
    
    @Valid
    private MessageMetadata metadata;
}
```

### Response DTOs
```java
// dto/response/ChatResponse.java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    
    private String message;
    private LocalDateTime timestamp;
    private String messageId;
    private ResponseMetadata metadata;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
```

---

## 🗄️ Entity Design

### JPA Entity Example
```java
// entity/ChatMessage.java
@Entity
@Table(name = "chat_messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ChatMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 1000)
    private String message;
    
    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private MessageSender sender;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Column(name = "session_id")
    private String sessionId;
    
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}

// enums/MessageSender.java
public enum MessageSender {
    USER, BOT
}
```

---

## 🧪 Testing Standards

### Unit Testing Framework
- **Use JUnit 5** for unit tests
- **Use Mockito** for mocking dependencies
- **Use TestContainers** for integration tests
- **Use @SpringBootTest** for full integration tests

### Service Layer Testing
```java
// service/ChatServiceTest.java
@ExtendWith(MockitoExtension.class)
class ChatServiceTest {
    
    @Mock
    private ChatMessageRepository repository;
    
    @Mock
    private MessageProcessor messageProcessor;
    
    @InjectMocks
    private ChatService chatService;
    
    @Test
    @DisplayName("Should save user message and return bot response")
    void shouldProcessMessageSuccessfully() {
        // Arrange
        ChatRequest request = ChatRequest.builder()
                .message("Hello")
                .sessionId("test-session")
                .build();
        
        when(messageProcessor.generateResponse("Hello"))
                .thenReturn("Echo: Hello");
        when(repository.save(any(ChatMessage.class)))
                .thenReturn(createMockMessage());
        
        // Act
        ChatResponse response = chatService.processMessage(request);
        
        // Assert
        assertThat(response.getMessage()).startsWith("Echo: ");
        verify(repository, times(2)).save(any(ChatMessage.class));
    }
    
    @Test
    @DisplayName("Should throw exception for empty message")
    void shouldThrowExceptionForEmptyMessage() {
        // Arrange
        ChatRequest request = ChatRequest.builder()
                .message("")
                .build();
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
                () -> chatService.processMessage(request));
    }
}
```

### Controller Layer Testing
```java
// controller/ChatControllerTest.java
@WebMvcTest(ChatController.class)
class ChatControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ChatService chatService;
    
    @Test
    @DisplayName("Should return chat response for valid request")
    void shouldReturnChatResponse() throws Exception {
        // Arrange
        ChatResponse mockResponse = ChatResponse.builder()
                .message("Hello there!")
                .timestamp(LocalDateTime.now())
                .build();
        
        when(chatService.processMessage(any(ChatRequest.class)))
                .thenReturn(mockResponse);
        
        // Act & Assert
        mockMvc.perform(post("/api/chat/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "message": "Hello",
                        "sessionId": "test-session"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello there!"));
    }
}
```

### Repository Testing
```java
// repository/ChatMessageRepositoryTest.java
@DataJpaTest
class ChatMessageRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private ChatMessageRepository repository;
    
    @Test
    @DisplayName("Should find messages by sender")
    void shouldFindMessagesBySender() {
        // Arrange
        ChatMessage userMessage = ChatMessage.builder()
                .message("Hello")
                .sender(MessageSender.USER)
                .timestamp(LocalDateTime.now())
                .build();
        
        entityManager.persistAndFlush(userMessage);
        
        // Act
        Page<ChatMessage> result = repository.findBySenderOrderByTimestampDesc(
                MessageSender.USER, PageRequest.of(0, 10));
        
        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getMessage()).isEqualTo("Hello");
    }
}
```

### Testing Requirements
- **Service classes**: 90% coverage minimum
- **Controller classes**: 85% coverage minimum
- **Repository classes**: 80% coverage minimum
- **Utility classes**: 95% coverage minimum

---

## ⚙️ Configuration Management

### Application Properties
```yaml
# application.yml
spring:
  application:
    name: chatbot-backend
  
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: password
  
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: false
    properties:
      hibernate:
        format_sql: true
  
  h2:
    console:
      enabled: true

server:
  port: 8080
  servlet:
    context-path: /

logging:
  level:
    com.chatbotapp: DEBUG
    org.springframework.web: INFO
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
```

### Docker Configuration
```yaml
# application-docker.yml
spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/chatbotdb
    username: ${DB_USERNAME:chatbot}
    password: ${DB_PASSWORD:password}
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    database-platform: org.hibernate.dialect.PostgreSQLDialect

logging:
  level:
    com.chatbotapp: INFO
```

---

## 🔒 Security Best Practices

### Input Validation
- **Use Bean Validation** annotations (@Valid, @NotNull, @Size)
- **Validate all inputs** at controller level
- **Sanitize data** before database operations
- **Use parameterized queries** to prevent SQL injection

### Exception Handling
```java
// exception/GlobalExceptionHandler.java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .message("Validation failed")
                .details(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(ChatException.class)
    public ResponseEntity<ErrorResponse> handleChatException(ChatException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .message("Chat processing failed")
                .details("Please try again later")
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

### Logging Security
- **Never log sensitive data** (passwords, tokens, personal information)
- **Log security-related events** (authentication failures, access attempts)
- **Use structured logging** with appropriate log levels
- **Implement audit trails** for critical operations

---

## 📊 Performance Standards

### Database Optimization
- **Use connection pooling** (HikariCP is default in Spring Boot)
- **Implement proper indexing** for frequently queried columns
- **Avoid N+1 queries** with proper fetch strategies
- **Use pagination** for large result sets

### API Performance
- **Response time**: Under 200ms for chat endpoints
- **Database queries**: Optimize slow queries (>100ms)
- **Memory usage**: Monitor heap usage and garbage collection
- **Connection management**: Proper resource cleanup

---

## 🚀 Development Commands

### Maven Commands
```bash
# Compilation and testing
mvn clean compile          # Clean and compile
mvn test                   # Run unit tests
mvn verify                 # Run integration tests
mvn package               # Create JAR file

# Code quality
mvn jacoco:report         # Generate test coverage report
mvn spotbugs:check        # Static code analysis
mvn checkstyle:check      # Code style validation

# Development
mvn spring-boot:run       # Run application locally
mvn spring-boot:run -Dspring-boot.run.profiles=docker  # Run with Docker profile
```

### Testing Commands
```bash
# Run specific test class
mvn test -Dtest=ChatServiceTest

# Run tests with specific profile
mvn test -Dspring.profiles.active=test

# Run integration tests only
mvn test -Dtest=*IT

# Generate coverage report
mvn clean test jacoco:report
```

---

## 📚 Documentation Standards

### Javadoc Requirements
```java
/**
 * Service class for handling chat message operations
 * 
 * This service provides functionality for:
 * - Processing user messages
 * - Generating bot responses
 * - Persisting chat history
 * 
 * @author Development Team
 * @version 1.0
 * @since 2024-01-01
 */
@Service
public class ChatService {
    
    /**
     * Processes a chat message and generates appropriate response
     * 
     * @param request the chat request containing user message and metadata
     * @return ChatResponse containing bot reply and timestamp
     * @throws ChatException if message processing fails
     * @throws IllegalArgumentException if request is invalid
     */
    public ChatResponse processMessage(ChatRequest request) {
        // Implementation
    }
}
```

### API Documentation
- **Use OpenAPI/Swagger** for REST API documentation
- **Document all endpoints** with examples
- **Include error responses** and status codes
- **Provide request/response schemas**

---

## 🔍 Code Quality Tools

### Static Analysis
```xml
<!-- pom.xml - SpotBugs Plugin -->
<plugin>
    <groupId>com.github.spotbugs</groupId>
    <artifactId>spotbugs-maven-plugin</artifactId>
    <version>4.7.3.0</version>
    <configuration>
        <effort>Max</effort>
        <threshold>Low</threshold>
    </configuration>
</plugin>

<!-- Checkstyle Plugin -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.2.0</version>
    <configuration>
        <configLocation>checkstyle.xml</configLocation>
    </configuration>
</plugin>
```

### Code Coverage
```xml
<!-- JaCoCo Plugin Configuration -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.8</version>
    <configuration>
        <rules>
            <rule>
                <element>CLASS</element>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.80</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</plugin>
```

---

This document serves as the comprehensive guide for backend development standards in the ChatBot Application project using Spring Boot 3.2.0 and Java 17.
