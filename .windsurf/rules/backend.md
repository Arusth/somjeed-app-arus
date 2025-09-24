---
trigger: always_on
---

# Backend Rules - Spring Boot 3.2.0 + Java 17

## 🎯 Code Style

### Naming & Organization
- Files under 200 lines, single responsibility
- Classes: `PascalCase`, Methods: `camelCase`, Constants: `UPPER_SNAKE_CASE`
- Packages: `lowercase` (e.g., `com.chatbotapp.service`)
- Constructor injection preferred, use `Optional` for nullables

---

## 🏗️ Architecture

### Controller
```java
@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatService;
    
    @PostMapping("/message")
    public ResponseEntity<ChatResponse> sendMessage(@Valid @RequestBody ChatRequest request) {
        return ResponseEntity.ok(chatService.processMessage(request));
    }
}
```

### Service
```java
@Service
@Transactional
public class ChatService {
    private final ChatMessageRepository repository;
    
    public ChatResponse processMessage(ChatRequest request) {
        validateMessage(request.getMessage());
        ChatMessage userMessage = createUserMessage(request);
        repository.save(userMessage);
        
        String botReply = generateResponse(request.getMessage());
        ChatMessage botMessage = createBotMessage(botReply);
        repository.save(botMessage);
        
        return ChatResponse.builder()
                .message(botReply)
                .timestamp(botMessage.getTimestamp())
                .build();
    }
}
```

### Repository
```java
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query("SELECT c FROM ChatMessage c WHERE c.sender = :sender ORDER BY c.timestamp DESC")
    Page<ChatMessage> findBySenderOrderByTimestampDesc(@Param("sender") String sender, Pageable pageable);
}
```

---

## 📁 Structure
```
backend/src/main/java/com/chatbotapp/
├── controller/    # REST endpoints
├── service/       # Business logic
├── repository/    # Data access
├── entity/        # JPA entities
├── dto/           # Request/Response objects
├── config/        # Configuration
└── exception/     # Error handling
```

---

## 📝 DTOs & Entities

### DTO
```java
@Data
@Builder
public class ChatRequest {
    @NotBlank(message = "Message required")
    @Size(max = 1000)
    private String message;
    
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$")
    private String sessionId;
}
```

### Entity
```java
@Entity
@Table(name = "chat_messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 1000)
    private String message;
    
    @Enumerated(EnumType.STRING)
    private MessageSender sender;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @PrePersist
    protected void onCreate() { timestamp = LocalDateTime.now(); }
}
```

---

## 🧪 Testing

### Testing Examples
```java
// Service Test
@ExtendWith(MockitoExtension.class)
class ChatServiceTest {
    @Mock private ChatMessageRepository repository;
    @InjectMocks private ChatService chatService;
    
    @Test
    void shouldProcessMessage() {
        ChatRequest request = ChatRequest.builder().message("Hello").build();
        ChatResponse response = chatService.processMessage(request);
        assertThat(response.getMessage()).isNotNull();
        verify(repository, times(2)).save(any(ChatMessage.class));
    }
}

// Controller Test
@WebMvcTest(ChatController.class)
class ChatControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private ChatService chatService;
    
    @Test
    void shouldReturnResponse() throws Exception {
        mockMvc.perform(post("/api/chat/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"message\":\"Hello\"}"))
                .andExpected(status().isOk());
    }
}
```

**Coverage**: Service 90%, Controller 85%, Repository 80%

---

## ⚙️ Configuration

```yaml
spring:
  application:
    name: chatbot-backend
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password: password
  jpa:
    hibernate:
      ddl-auto: create-drop
server:
  port: 8080
logging:
  level:
    com.chatbotapp: DEBUG
```

---

## 🔒 Security & Error Handling

### Exception Handler
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.builder()
                        .message("Validation failed")
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}
```

**Security**: Use Bean Validation, validate inputs, parameterized queries, no sensitive logging

---

## 🚀 Commands & Performance

```bash
mvn test                 # Unit tests
mvn verify              # Integration tests
mvn jacoco:report       # Coverage
mvn spring-boot:run     # Run locally
```

**Performance**: API <200ms, optimize queries >100ms, connection pooling, indexing, pagination
