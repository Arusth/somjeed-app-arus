---
trigger: always_on
---

# Windsurf Development Rules

## 📋 กฏการพัฒนาทั่วไป

### 1. Unit Testing Requirements (บังคับ)
- **ทุกฟังก์ชัน/เมธอดใหม่ต้องมี unit test**
- **ทุกการแก้ไขโค้ดต้องมีการอัปเดต test ที่เกี่ยวข้อง**
- **Code coverage ต้องไม่ต่ำกว่า 80%**
- **ต้องมีการทดสอบทั้ง positive และ negative cases**

### 2. Test Structure Guidelines
- ใช้ AAA pattern (Arrange, Act, Assert)
- Test method names ต้องอธิบายสิ่งที่ทดสอบอย่างชัดเจน
- แต่ละ test ต้องทดสอบเพียงสิ่งเดียว
- ใช้ test data builders หรือ fixtures สำหรับ complex objects

## 🎯 Backend (Java/Spring Boot) Testing Rules

### Unit Testing Requirements:
- ใช้ JUnit 5 และ Mockito
- Service layer tests ต้อง mock dependencies
- Repository tests ใช้ @DataJpaTest
- Controller tests ใช้ @WebMvcTest
- Integration tests ใช้ @SpringBootTest

### Test Coverage Requirements:
- Service classes: 90% coverage
- Controller classes: 85% coverage
- Repository classes: 80% coverage
- Utility classes: 95% coverage

### Example Test Structure:
```java
@ExtendWith(MockitoExtension.class)
class ChatServiceTest {
    
    @Mock
    private ChatMessageRepository repository;
    
    @InjectMocks
    private ChatService chatService;
    
    @Test
    @DisplayName("Should save user message and return bot response")
    void shouldProcessMessageSuccessfully() {
        // Arrange
        ChatRequest request = new ChatRequest("Hello");
        
        // Act
        ChatResponse response = chatService.processMessage(request);
        
        // Assert
        assertThat(response.getMessage()).startsWith("Echo: ");
        verify(repository, times(2)).save(any(ChatMessage.class));
    }
}
```

### Required Test Types:
1. **Unit Tests**: แต่ละ method ใน service/component
2. **Integration Tests**: API endpoints พร้อม database
3. **Repository Tests**: JPA queries และ custom methods
4. **Validation Tests**: DTO validation และ business rules

## 🎨 Frontend (React/TypeScript) Testing Rules

### Unit Testing Requirements:
- ใช้ Jest และ React Testing Library
- Component tests ต้องทดสอบ user interactions
- Hook tests สำหรับ custom hooks
- Utility function tests

### Test Coverage Requirements:
- Components: 85% coverage
- Custom hooks: 90% coverage
- Utility functions: 95% coverage
- API services: 80% coverage

### Example Test Structure:
```typescript
import { render, screen, fireEvent } from '@testing-library/react';
import { ChatComponent } from './ChatComponent';

describe('ChatComponent', () => {
  test('should send message when form is submitted', async () => {
    // Arrange
    const mockOnSend = jest.fn();
    render(<ChatComponent onSend={mockOnSend} />);
    
    // Act
    const input = screen.getByPlaceholderText('Type your message...');
    const button = screen.getByRole('button', { name: /send/i });
    
    fireEvent.change(input, { target: { value: 'Hello' } });
    fireEvent.click(button);
    
    // Assert
    expect(mockOnSend).toHaveBeenCalledWith('Hello');
  });
});
```

### Required Test Types:
1. **Component Tests**: Rendering, props, user interactions
2. **Hook Tests**: Custom hooks behavior
3. **Integration Tests**: API calls และ state management
4. **Accessibility Tests**: Screen reader compatibility

## 🔧 Testing Tools และ Commands

### Backend Testing:
```bash
# Run all tests
mvn test

# Run tests with coverage
mvn test jacoco:report

# Run specific test class
mvn test -Dtest=ChatServiceTest

# Run integration tests
mvn test -Dtest=*IT
```

### Frontend Testing:
```bash
# Run all tests
npm test

# Run tests with coverage
npm run test:coverage

# Run tests in watch mode
npm run test:watch

# Run specific test file
npm test ChatComponent.test.tsx
```

## 📊 Quality Gates

### Before Code Commit:
- [ ] All tests pass
- [ ] Code coverage meets minimum requirements
- [ ] No test files are skipped or ignored
- [ ] Integration tests pass

### Before Pull Request:
- [ ] New features have corresponding tests
- [ ] Modified code has updated tests
- [ ] Test coverage report is included
- [ ] All test suites pass in CI/CD

## 🚫 Testing Anti-Patterns (ห้ามทำ)

1. **ห้าม skip tests** โดยไม่มีเหตุผลที่ชัดเจน
2. **ห้าม commit code** ที่ทำให้ existing tests fail
3. **ห้าม mock ทุกอย่าง** ในการทำ integration tests
4. **ห้าม hard-code test data** ที่อาจเปลี่ยนแปลงได้
5. **ห้าม ignore test coverage warnings**

## 📝 Test Documentation

### Test Case Documentation:
- ใช้ descriptive test names
- เพิ่ม comments สำหรับ complex test scenarios
- Document test data setup และ teardown
- อธิบาย business logic ที่ซับซ้อน

### Example Documentation:
```java
/**
 * Tests the chat message processing workflow
 * Scenario: User sends a message, system saves it and generates bot response
 * Expected: Both user and bot messages are saved to database
 */
@Test
@DisplayName("Should process chat message and save both user and bot messages")
void shouldProcessChatMessageWorkflow() {
    // Test implementation
}
```

## 🎯 Continuous Improvement

### Monthly Review:
- ทบทวน test coverage metrics
- ปรับปรุง slow-running tests
- อัปเดต testing tools และ libraries
- แชร์ best practices ระหว่างทีม

### Performance Monitoring:
- ติดตาม test execution time
- Optimize slow integration tests
- Monitor flaky tests และแก้ไข
- Keep test suite maintainable

---

**หมายเหตุ**: กฏเหล่านี้เป็นข้อกำหนดบังคับสำหรับการพัฒนาทั้งหมด การไม่ปฏิบัติตามจะทำให้ code review ไม่ผ่าน
