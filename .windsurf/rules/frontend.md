---
trigger: always_on
---

# Frontend Development Rules
## Next.js 15 + TypeScript Standards

---

## 🎯 General Code Style Rules

### File Size and Organization
- **Keep files under 200 lines of code** - split larger files into smaller focused modules
- **Single Responsibility Principle** - each file should have one clear purpose
- **Consistent naming conventions**:
  - Files: `kebab-case` for components, `camelCase` for utilities
  - Directories: `kebab-case` for consistency
  - Components: `PascalCase`
  - Functions/Variables: `camelCase`

### Code Quality Principles
- **Follow DRY principle** and separate concerns consistently
- **Create reusable utilities** where applicable
- **Write meaningful JSDoc comments**, avoid temporary/TODO comments
- **Prefer composition over inheritance**
- **Extract complex logic** into dedicated service modules

---

## 🔧 TypeScript Best Practices

### Type Safety Requirements
- **Use TypeScript strictly**: avoid `any`, minimize `unknown`
- **Enable strict mode** in `tsconfig.json`:
  ```json
  {
    "compilerOptions": {
      "strict": true,
      "noImplicitAny": true,
      "noImplicitReturns": true,
      "noImplicitThis": true,
      "noUnusedLocals": true,
      "noUnusedParameters": true
    }
  }
  ```

### Interface and Type Definitions
- **Define clear interfaces** for all component props to ensure type safety
- **Use proper TypeScript exports and imports**
- **Create type definitions** for API responses and requests
- **Leverage union types** and discriminated unions where appropriate

### Path Aliases and Imports
- **Use path aliases (`@/*`)** for consistent import paths
- **Organize imports** in the following order:
  1. External libraries
  2. Internal modules (using `@/`)
  3. Relative imports
  4. Type-only imports (use `import type`)

Example:
```typescript
import React from 'react';
import axios from 'axios';

import { chatService } from '@/services/chatService';
import { MessageBubble } from '@/components/MessageBubble';

import { validateMessage } from '../utils/validation';

import type { ChatMessage, ChatResponse } from '@/types/chat';
```

---

## 🏗️ Component Architecture Rules

### Component Patterns
- **Follow Single Responsibility Principle** - one clear purpose per component
- **Separate Smart vs Dumb components**:
  - **Smart (Container)**: Handle business logic, state management, API calls
  - **Dumb (Presentational)**: Pure UI components with props and callbacks
- **Extract business logic** into custom hooks when components become complex
- **Break complex components** into smaller focused pieces

### Component Structure Template
```typescript
// components/MessageBubble/MessageBubble.tsx
import type { FC } from 'react';
import type { ChatMessage } from '@/types/chat';

interface MessageBubbleProps {
  message: ChatMessage;
  isUser: boolean;
  onRetry?: () => void;
}

/**
 * MessageBubble component displays individual chat messages
 * Handles both user and bot messages with appropriate styling
 */
export const MessageBubble: FC<MessageBubbleProps> = ({
  message,
  isUser,
  onRetry
}) => {
  // Component implementation
};
```

### Custom Hooks Guidelines
- **Extract stateful logic** into custom hooks
- **Prefix with 'use'** following React conventions
- **Return objects** instead of arrays for better destructuring
- **Include proper TypeScript types** for parameters and return values

Example:
```typescript
// hooks/useChat.ts
import { useState, useCallback } from 'react';
import type { ChatMessage, ChatResponse } from '@/types/chat';

interface UseChatReturn {
  messages: ChatMessage[];
  isLoading: boolean;
  sendMessage: (content: string) => Promise<void>;
  clearMessages: () => void;
}

export const useChat = (): UseChatReturn => {
  // Hook implementation
};
```

---

## 📁 Frontend Directory Structure

```
frontend/
├── src/
│   ├── app/                    # Next.js app router
│   │   ├── globals.css
│   │   ├── layout.tsx
│   │   └── page.tsx
│   ├── components/             # Reusable components
│   │   ├── ui/                # Basic UI components
│   │   ├── chat/              # Chat-specific components
│   │   └── layout/            # Layout components
│   ├── services/              # API services
│   │   ├── chatService.ts
│   │   └── apiClient.ts
│   ├── hooks/                 # Custom hooks
│   │   ├── useChat.ts
│   │   └── useLocalStorage.ts
│   ├── types/                 # TypeScript definitions
│   │   ├── chat.ts
│   │   └── api.ts
│   ├── utils/                 # Utility functions
│   │   ├── validation.ts
│   │   └── formatting.ts
│   └── constants/             # Application constants
│       └── api.ts
├── public/                    # Static assets
├── __tests__/                 # Test files
└── docs/                      # Component documentation
```

---

## 🔄 Service Layer Guidelines

### API Services
- **Organize business logic** in `services/` directory
- **Handle API operations** in dedicated service modules
- **Use proper error handling** with typed error responses
- **Implement retry logic** for failed requests

Example:
```typescript
// services/chatService.ts
import axios from 'axios';
import type { ChatRequest, ChatResponse } from '@/types/chat';

class ChatService {
  private readonly baseURL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

  async sendMessage(request: ChatRequest): Promise<ChatResponse> {
    try {
      const response = await axios.post<ChatResponse>(
        `${this.baseURL}/api/chat/message`,
        request
      );
      return response.data;
    } catch (error) {
      throw new Error('Failed to send message');
    }
  }
}

export const chatService = new ChatService();
```

---

## 🧪 Testing Standards

### Testing Framework Setup
- **Use Jest + React Testing Library**
- **Component tests**: Test user interactions and rendering
- **Hook tests**: Test custom hook behavior
- **Service tests**: Test API service functions
- **Integration tests**: Test component integration

### Test Structure Example
```typescript
// __tests__/components/MessageBubble.test.tsx
import { render, screen } from '@testing-library/react';
import { MessageBubble } from '@/components/MessageBubble';
import type { ChatMessage } from '@/types/chat';

describe('MessageBubble', () => {
  const mockMessage: ChatMessage = {
    id: '1',
    content: 'Hello World',
    sender: 'user',
    timestamp: new Date()
  };

  test('should render user message correctly', () => {
    render(<MessageBubble message={mockMessage} isUser={true} />);
    
    expect(screen.getByText('Hello World')).toBeInTheDocument();
    expect(screen.getByTestId('user-message')).toHaveClass('user-message');
  });

  test('should handle retry functionality', () => {
    const mockRetry = jest.fn();
    render(
      <MessageBubble 
        message={mockMessage} 
        isUser={false} 
        onRetry={mockRetry} 
      />
    );
    
    // Test retry button functionality
  });
});
```

### Testing Requirements
- **Component coverage**: 85% minimum
- **Custom hooks coverage**: 90% minimum
- **Utility functions coverage**: 95% minimum
- **API services coverage**: 80% minimum

---

## 🔧 Configuration Files

### ESLint Configuration
```json
// .eslintrc.json
{
  "extends": [
    "next/core-web-vitals",
    "@typescript-eslint/recommended"
  ],
  "rules": {
    "@typescript-eslint/no-unused-vars": "error",
    "@typescript-eslint/no-explicit-any": "error",
    "@typescript-eslint/prefer-const": "error",
    "prefer-const": "error",
    "no-var": "error",
    "react-hooks/exhaustive-deps": "warn"
  }
}
```

### TypeScript Configuration
```json
// tsconfig.json
{
  "compilerOptions": {
    "target": "es5",
    "lib": ["dom", "dom.iterable", "es6"],
    "allowJs": true,
    "skipLibCheck": true,
    "strict": true,
    "noEmit": true,
    "esModuleInterop": true,
    "module": "esnext",
    "moduleResolution": "bundler",
    "resolveJsonModule": true,
    "isolatedModules": true,
    "jsx": "preserve",
    "incremental": true,
    "plugins": [
      {
        "name": "next"
      }
    ],
    "baseUrl": ".",
    "paths": {
      "@/*": ["./src/*"]
    }
  },
  "include": ["next-env.d.ts", "**/*.ts", "**/*.tsx", ".next/types/**/*.ts"],
  "exclude": ["node_modules"]
}
```

### Tailwind CSS Configuration
```javascript
// tailwind.config.js
/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './src/pages/**/*.{js,ts,jsx,tsx,mdx}',
    './src/components/**/*.{js,ts,jsx,tsx,mdx}',
    './src/app/**/*.{js,ts,jsx,tsx,mdx}',
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          50: '#eff6ff',
          500: '#3b82f6',
          600: '#2563eb',
          700: '#1d4ed8',
        }
      }
    },
  },
  plugins: [],
}
```

---

## 📊 Performance Standards

### Bundle Optimization
- **Keep JavaScript bundles under 250KB**
- **Use dynamic imports** for code splitting
- **Optimize images** with Next.js Image component
- **Implement lazy loading** for components

### Runtime Performance
- **API response handling**: Under 200ms for chat endpoints
- **Component rendering**: Avoid unnecessary re-renders
- **Memory management**: Clean up event listeners and subscriptions

---

## 🔒 Security Best Practices

### Input Validation
- **Validate all user inputs** before sending to API
- **Sanitize data** before rendering
- **Use proper escaping** for user-generated content
- **Implement client-side rate limiting**

### API Security
- **Use HTTPS** for all API calls
- **Handle authentication tokens** securely
- **Never expose sensitive data** in client-side code
- **Implement proper error boundaries**

---

## 🚀 Development Commands

### Quality Assurance Commands
```bash
# Linting and formatting
npm run lint              # ESLint check
npm run lint:fix          # Fix ESLint issues
npm run format            # Prettier formatting

# Type checking
npm run type-check        # TypeScript compilation check

# Testing
npm run test              # Run all tests
npm run test:watch        # Run tests in watch mode
npm run test:coverage     # Run tests with coverage

# Build and deployment
npm run build             # Production build
npm run start             # Start production server
npm run dev               # Development server
```

### Pre-commit Hooks
```json
// package.json
{
  "husky": {
    "hooks": {
      "pre-commit": "lint-staged"
    }
  },
  "lint-staged": {
    "*.{ts,tsx}": [
      "eslint --fix",
      "prettier --write",
      "jest --findRelatedTests"
    ]
  }
}
```

---

## 📚 Documentation Standards

### Component Documentation
```typescript
/**
 * ChatInterface component manages the main chat functionality
 * 
 * Features:
 * - Real-time message display
 * - Message input with validation
 * - Auto-scroll to latest messages
 * - Loading states during API calls
 * 
 * @example
 * ```tsx
 * <ChatInterface 
 *   onMessageSent={handleMessage}
 *   initialMessages={messages}
 * />
 * ```
 */
export const ChatInterface: FC<ChatInterfaceProps> = (props) => {
  // Implementation
};
```

### README Requirements
- **Component purpose** and functionality
- **Props interface** documentation
- **Usage examples** with code snippets
- **Testing instructions**
- **Performance considerations**

