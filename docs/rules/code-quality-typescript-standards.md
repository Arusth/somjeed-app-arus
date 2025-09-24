---
trigger: always_on
---

# Code Quality & Development Standards
## ChatBot Application Project Rules

## 📋 Overview
This document serves as the main index for code quality standards and development best practices for the ChatBot Application project. The rules are organized into separate documents for better maintainability and focus.

---

## 📚 Documentation Structure

### 🎨 [Frontend Rules](./frontend-rules.md)
**Next.js 15 + TypeScript Standards**

Covers comprehensive frontend development guidelines including:
- TypeScript best practices and strict mode configuration
- React component architecture (Smart vs Dumb components)
- Custom hooks guidelines and patterns
- Service layer organization for API calls
- Testing standards with Jest + React Testing Library
- ESLint and configuration management
- Performance optimization and security practices

**Key Topics:**
- Component structure and naming conventions
- Path aliases and import organization
- State management patterns
- Testing requirements (85% coverage minimum)
- Bundle optimization and performance standards

---

### ☕ [Backend Rules](./backend-rules.md)
**Spring Boot 3.2.0 + Java 17 Standards**

Covers comprehensive backend development guidelines including:
- Java naming conventions and code structure
- Spring Boot architecture patterns (Controller, Service, Repository)
- JPA entity design and database optimization
- Data Transfer Objects (DTOs) and validation
- Testing standards with JUnit 5 + Mockito
- Security best practices and exception handling
- Performance optimization and monitoring

**Key Topics:**
- Service layer architecture and transaction management
- Repository patterns and custom queries
- Configuration management and profiles
- Testing requirements (90% service coverage minimum)
- API documentation and error handling

---

## 🎯 Universal Principles

### Code Quality Standards (Both Frontend & Backend)
- **Keep files under 200 lines** - split larger files into focused modules
- **Single Responsibility Principle** - each file should have one clear purpose
- **Follow DRY principle** and separate concerns consistently
- **Write meaningful documentation** - JSDoc for TypeScript, Javadoc for Java
- **Prefer composition over inheritance**
- **Extract complex logic** into dedicated service modules

### Testing Requirements
- **Unit tests required** for all new functions/methods
- **Integration tests** for API endpoints and component interactions
- **Code coverage minimums**:
  - Frontend: 85% overall
  - Backend: 90% for services, 85% for controllers
- **Test-driven development** encouraged for complex features

### Development Workflow
- **Pre-commit hooks** for linting and formatting
- **Code review required** for all pull requests
- **Continuous integration** with automated testing
- **Documentation updates** required with code changes

---

## 🔧 Quick Reference

### Frontend Commands
```bash
npm run lint              # ESLint check
npm run type-check        # TypeScript compilation
npm run test:coverage     # Run tests with coverage
npm run build             # Production build test
```

### Backend Commands
```bash
mvn test                  # Run unit tests
mvn verify                # Run integration tests
mvn jacoco:report         # Generate coverage report
mvn spring-boot:run       # Run application locally
```

---

## 📊 Quality Gates

### Before Committing Code
- [ ] All tests pass locally
- [ ] Code coverage meets minimum requirements
- [ ] Linting passes without errors
- [ ] TypeScript compilation successful (frontend)
- [ ] Maven compilation successful (backend)

### Before Pull Request
- [ ] New features have corresponding tests
- [ ] Documentation updated for API changes
- [ ] Integration tests pass
- [ ] Performance impact assessed
- [ ] Security considerations reviewed

---

## 🚀 Getting Started

1. **Read the relevant rules document**:
   - Frontend developers: Start with [Frontend Rules](./frontend-rules.md)
   - Backend developers: Start with [Backend Rules](./backend-rules.md)
   - Full-stack developers: Review both documents

2. **Set up your development environment** according to the configuration examples in each document

3. **Configure your IDE** with the recommended linting and formatting rules

4. **Review the testing standards** and ensure your development workflow includes proper test coverage

---

## 📝 Contributing to These Rules

These rules are living documents that should evolve with the project. When proposing changes:

1. **Discuss with the team** before making significant modifications
2. **Update examples** to reflect current best practices
3. **Maintain consistency** between frontend and backend standards where applicable
4. **Test proposed changes** in real development scenarios

---

**Last Updated**: 2024-09-24  
**Version**: 1.0  
**Maintainers**: Development Team
