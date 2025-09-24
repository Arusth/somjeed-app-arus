import { render, screen, fireEvent } from '@testing-library/react';
import { GreetingMessage } from '@/components/GreetingMessage';
import { useGreeting } from '@/hooks/useGreeting';
import type { GreetingResponse, BotProfile } from '@/types/chat';

// Mock the useGreeting hook
jest.mock('@/hooks/useGreeting');

const mockUseGreeting = useGreeting as jest.MockedFunction<typeof useGreeting>;

describe('GreetingMessage', () => {
  const mockGreeting: GreetingResponse = {
    message: 'Good morning, on a sunshine day!',
    timeOfDay: 'morning',
    weatherCondition: 'sunny',
    timestamp: '2024-01-01T09:00:00Z',
  };

  const mockBotProfile: BotProfile = {
    name: 'TestBot',
    avatar: '🤖',
    color: '#ff0000',
  };

  const defaultHookReturn = {
    greeting: null,
    isLoading: false,
    error: null,
    fetchGreeting: jest.fn(),
    clearError: jest.fn(),
  };

  beforeEach(() => {
    jest.clearAllMocks();
    mockUseGreeting.mockReturnValue(defaultHookReturn);
  });

  test('should render loading state correctly', () => {
    mockUseGreeting.mockReturnValue({
      ...defaultHookReturn,
      isLoading: true,
    });

    render(<GreetingMessage />);

    expect(screen.getByText('Somjeed')).toBeInTheDocument();
    expect(screen.getByText('Preparing your greeting...')).toBeInTheDocument();
    expect(screen.getByRole('generic')).toHaveClass('animate-pulse');
  });

  test('should render error state correctly', () => {
    const mockFetchGreeting = jest.fn();
    const mockClearError = jest.fn();

    mockUseGreeting.mockReturnValue({
      ...defaultHookReturn,
      error: 'Failed to fetch greeting',
      fetchGreeting: mockFetchGreeting,
      clearError: mockClearError,
    });

    render(<GreetingMessage />);

    expect(screen.getByText('Somjeed')).toBeInTheDocument();
    expect(screen.getByText(/Sorry, I couldn't prepare a greeting right now/)).toBeInTheDocument();
    
    const tryAgainButton = screen.getByText('Try again');
    const dismissButton = screen.getByText('Dismiss');

    fireEvent.click(tryAgainButton);
    expect(mockFetchGreeting).toHaveBeenCalledTimes(1);

    fireEvent.click(dismissButton);
    expect(mockClearError).toHaveBeenCalledTimes(1);
  });

  test('should render greeting successfully', () => {
    mockUseGreeting.mockReturnValue({
      ...defaultHookReturn,
      greeting: mockGreeting,
    });

    render(<GreetingMessage />);

    expect(screen.getByText('Somjeed')).toBeInTheDocument();
    expect(screen.getByText('Good morning, on a sunshine day!')).toBeInTheDocument();
    expect(screen.getByText('• morning')).toBeInTheDocument();
    expect(screen.getByText('sunny')).toBeInTheDocument();
    expect(screen.getByText('☀️')).toBeInTheDocument(); // Sunny emoji
  });

  test('should use custom bot profile when provided', () => {
    mockUseGreeting.mockReturnValue({
      ...defaultHookReturn,
      greeting: mockGreeting,
    });

    render(<GreetingMessage botProfile={mockBotProfile} />);

    expect(screen.getByText('TestBot')).toBeInTheDocument();
    expect(screen.queryByText('Somjeed')).not.toBeInTheDocument();
  });

  test('should call onGreetingLoaded when greeting is loaded', () => {
    const mockOnGreetingLoaded = jest.fn();

    mockUseGreeting.mockReturnValue({
      ...defaultHookReturn,
      greeting: mockGreeting,
    });

    render(<GreetingMessage onGreetingLoaded={mockOnGreetingLoaded} />);

    expect(mockOnGreetingLoaded).toHaveBeenCalledWith('Good morning, on a sunshine day!');
  });

  test('should auto-fetch greeting on mount by default', () => {
    const mockFetchGreeting = jest.fn();

    mockUseGreeting.mockReturnValue({
      ...defaultHookReturn,
      fetchGreeting: mockFetchGreeting,
    });

    render(<GreetingMessage />);

    expect(mockFetchGreeting).toHaveBeenCalledTimes(1);
  });

  test('should not auto-fetch when autoFetch is false', () => {
    const mockFetchGreeting = jest.fn();

    mockUseGreeting.mockReturnValue({
      ...defaultHookReturn,
      fetchGreeting: mockFetchGreeting,
    });

    render(<GreetingMessage autoFetch={false} />);

    expect(mockFetchGreeting).not.toHaveBeenCalled();
  });

  test('should render default state when no greeting and not loading', () => {
    mockUseGreeting.mockReturnValue(defaultHookReturn);

    render(<GreetingMessage autoFetch={false} />);

    expect(screen.getByText('Hello! I\'m ready to help you today.')).toBeInTheDocument();
    expect(screen.getByText('Get personalized greeting')).toBeInTheDocument();
  });

  test('should handle refresh button click in greeting state', () => {
    const mockFetchGreeting = jest.fn();

    mockUseGreeting.mockReturnValue({
      ...defaultHookReturn,
      greeting: mockGreeting,
      fetchGreeting: mockFetchGreeting,
    });

    render(<GreetingMessage />);

    const refreshButton = screen.getByTitle('Get new greeting');
    fireEvent.click(refreshButton);

    expect(mockFetchGreeting).toHaveBeenCalledTimes(2); // Once on mount, once on click
  });

  test('should display correct weather emojis for different conditions', () => {
    const testCases = [
      { condition: 'sunny', emoji: '☀️' },
      { condition: 'cloudy', emoji: '☁️' },
      { condition: 'rainy', emoji: '🌧️' },
      { condition: 'stormy', emoji: '⛈️' },
      { condition: 'unknown', emoji: '🌤️' },
    ];

    testCases.forEach(({ condition, emoji }) => {
      const greetingWithCondition = {
        ...mockGreeting,
        weatherCondition: condition,
      };

      mockUseGreeting.mockReturnValue({
        ...defaultHookReturn,
        greeting: greetingWithCondition,
      });

      const { unmount } = render(<GreetingMessage />);
      
      expect(screen.getByText(emoji)).toBeInTheDocument();
      
      unmount();
    });
  });

  test('should apply custom className', () => {
    const customClass = 'custom-greeting-class';

    render(<GreetingMessage className={customClass} />);

    const greetingContainer = screen.getByText('Somjeed').closest('div')?.parentElement?.parentElement;
    expect(greetingContainer).toHaveClass(customClass);
  });

  test('should handle case-insensitive weather conditions', () => {
    const greetingWithUpperCase = {
      ...mockGreeting,
      weatherCondition: 'SUNNY',
    };

    mockUseGreeting.mockReturnValue({
      ...defaultHookReturn,
      greeting: greetingWithUpperCase,
    });

    render(<GreetingMessage />);

    expect(screen.getByText('☀️')).toBeInTheDocument();
    expect(screen.getByText('SUNNY')).toBeInTheDocument();
  });
});
