import { render, screen } from '@testing-library/react'
import AboutPage from '@/app/about/page'

// Mock Next.js Link component
jest.mock('next/link', () => {
  return function MockLink({ children, href, ...props }: { children: React.ReactNode; href: string; [key: string]: unknown }) {
    return <a href={href} {...props}>{children}</a>
  }
})

// Mock Header component
jest.mock('@/components/Header', () => {
  return function MockHeader() {
    return <header data-testid="header">Mocked Header</header>
  }
})

describe('AboutPage', () => {
  beforeEach(() => {
    render(<AboutPage />)
  })

  test('should render main page structure', () => {
    expect(screen.getByTestId('header')).toBeInTheDocument()
    expect(screen.getByText('Somjeed ChatBot Project')).toBeInTheDocument()
  })

  test('should render hero section with project description', () => {
    expect(screen.getByText('Somjeed ChatBot Project')).toBeInTheDocument()
    expect(screen.getByText(/An intelligent credit card customer support chatbot/)).toBeInTheDocument()
  })

  test('should render system overview section', () => {
    expect(screen.getByText('System Architecture Overview')).toBeInTheDocument()
    expect(screen.getByText('What is Somjeed?')).toBeInTheDocument()
    expect(screen.getByText('Key Capabilities')).toBeInTheDocument()
  })

  test('should render system flow section', () => {
    expect(screen.getByText('System Flow & User Journey')).toBeInTheDocument()
    expect(screen.getByText('Contextual Greeting')).toBeInTheDocument()
    expect(screen.getAllByText('Intent Prediction')).toHaveLength(2) // Appears in flow and features
    expect(screen.getAllByText('Intent Recognition')).toHaveLength(2) // Appears in flow and features
    expect(screen.getByText('Feedback & Closure')).toBeInTheDocument()
  })

  test('should render core features integration section', () => {
    expect(screen.getByText('Core Features Integration')).toBeInTheDocument()
    expect(screen.getByText('Contextual Greetings')).toBeInTheDocument()
    expect(screen.getByText('Silence Tracking')).toBeInTheDocument()
    expect(screen.getByText('Feedback System')).toBeInTheDocument()
  })

  test('should render technical architecture section', () => {
    expect(screen.getByText('Technical Architecture & Tech Stack')).toBeInTheDocument()
    expect(screen.getByText('Frontend Stack')).toBeInTheDocument()
    expect(screen.getByText('Backend Stack')).toBeInTheDocument()
    expect(screen.getByText('Next.js 15 + TypeScript')).toBeInTheDocument()
    expect(screen.getByText('Spring Boot 3.2.0 + Java 17')).toBeInTheDocument()
  })

  test('should render development standards section', () => {
    expect(screen.getByText('Development Standards & Quality Metrics')).toBeInTheDocument()
    expect(screen.getByText('90%+')).toBeInTheDocument()
    expect(screen.getByText('<200ms')).toBeInTheDocument()
    expect(screen.getByText('100%')).toBeInTheDocument()
  })

  test('should render intent recognition system section', () => {
    expect(screen.getByText('Intent Recognition System')).toBeInTheDocument()
    expect(screen.getByText('Payment Inquiry')).toBeInTheDocument()
    expect(screen.getByText('Transaction Dispute')).toBeInTheDocument()
    expect(screen.getByText('Card Management')).toBeInTheDocument()
    expect(screen.getByText('Account Security')).toBeInTheDocument()
  })

  test('should render quality assurance section', () => {
    expect(screen.getByText('Quality Assurance & Security')).toBeInTheDocument()
    expect(screen.getByText('Security Features')).toBeInTheDocument()
    expect(screen.getByText('Performance Optimization')).toBeInTheDocument()
  })

  test('should render call to action section', () => {
    expect(screen.getByText('Experience Somjeed in Action')).toBeInTheDocument()
    expect(screen.getByText('Start Chatting')).toBeInTheDocument()
    expect(screen.getByText('View Demo')).toBeInTheDocument()
  })

  test('should have proper navigation links in CTA section', () => {
    const startChattingLink = screen.getByText('Start Chatting').closest('a')
    const viewDemoLink = screen.getByText('View Demo').closest('a')
    
    expect(startChattingLink).toHaveAttribute('href', '/')
    expect(viewDemoLink).toHaveAttribute('href', '/demo')
  })

  test('should display all 8 intent recognition cards', () => {
    const intentCards = [
      'Payment Inquiry',
      'Transaction Dispute', 
      'Card Management',
      'Credit Limit',
      'Account Security',
      'Statement Inquiry',
      'Reward Points',
      'Technical Support'
    ]

    intentCards.forEach(intent => {
      expect(screen.getByText(intent)).toBeInTheDocument()
    })
  })

  test('should display confidence percentages for intents', () => {
    const confidences = ['90%', '95%', '92%', '88%', '96%', '85%', '83%', '80%']
    
    confidences.forEach(confidence => {
      expect(screen.getByText(confidence)).toBeInTheDocument()
    })
  })

  test('should display integration information for features', () => {
    expect(screen.getByText(/WeatherService \+ GreetingService/)).toBeInTheDocument()
    expect(screen.getByText(/UserDataService \+ IntentPredictionService/)).toBeInTheDocument()
    expect(screen.getByText(/IntentRecognitionService \+ IntentResponseService/)).toBeInTheDocument()
  })

  test('should have proper semantic structure', () => {
    const main = screen.getByRole('main')
    expect(main).toBeInTheDocument()
    
    // Check for multiple section headings instead of DOM structure
    expect(screen.getByText('System Architecture Overview')).toBeInTheDocument()
    expect(screen.getByText('Core Features Integration')).toBeInTheDocument()
    expect(screen.getByText('Technical Architecture & Tech Stack')).toBeInTheDocument()
    expect(screen.getByText('Development Standards & Quality Metrics')).toBeInTheDocument()
    expect(screen.getByText('Intent Recognition System')).toBeInTheDocument()
    expect(screen.getByText('Quality Assurance & Security')).toBeInTheDocument()
  })

  test('should display technology stack details', () => {
    expect(screen.getByText('Tailwind CSS')).toBeInTheDocument()
    expect(screen.getByText('React Testing Library + Jest')).toBeInTheDocument()
    expect(screen.getByText('JPA/Hibernate + H2 Database')).toBeInTheDocument()
    expect(screen.getByText('JUnit 5 + Mockito')).toBeInTheDocument()
  })
})
