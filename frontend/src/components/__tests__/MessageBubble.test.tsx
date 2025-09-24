import { render, screen } from '@testing-library/react';
import MessageBubble from '../MessageBubble';
import type { BotProfile } from '@/types/chat';

describe('MessageBubble', () => {
  const mockTimestamp = new Date('2023-01-01T12:00:00Z');
  const expectedTimeString = mockTimestamp.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  
  const mockBotProfile: BotProfile = {
    name: 'Test Bot',
    avatar: '🤖',
    color: '#22c55e'
  };

  it('renders user message correctly', () => {
    render(
      <MessageBubble
        message="Hello, bot!"
        sender="user"
        timestamp={mockTimestamp}
      />
    );

    expect(screen.getByText('Hello, bot!')).toBeInTheDocument();
    expect(screen.getByText(expectedTimeString)).toBeInTheDocument();
  });

  it('renders bot message correctly with avatar and name', () => {
    render(
      <MessageBubble
        message="Hello, user!"
        sender="bot"
        timestamp={mockTimestamp}
        botProfile={mockBotProfile}
      />
    );

    expect(screen.getByText('Hello, user!')).toBeInTheDocument();
    expect(screen.getByText(expectedTimeString)).toBeInTheDocument();
    expect(screen.getByText('🤖')).toBeInTheDocument();
    expect(screen.getByText('Test Bot')).toBeInTheDocument();
  });

  it('uses default bot profile when none provided for bot messages', () => {
    render(
      <MessageBubble
        message="Bot message"
        sender="bot"
        timestamp={mockTimestamp}
      />
    );

    expect(screen.getByText('🤖')).toBeInTheDocument();
    expect(screen.getByText('Somjeed')).toBeInTheDocument();
  });

  it('applies correct styling for user messages', () => {
    const { container } = render(
      <MessageBubble
        message="User message"
        sender="user"
        timestamp={mockTimestamp}
      />
    );

    const messageDiv = container.querySelector('.bg-primary-500');
    expect(messageDiv).toBeInTheDocument();
  });

  it('applies correct styling for bot messages', () => {
    const { container } = render(
      <MessageBubble
        message="Bot message"
        sender="bot"
        timestamp={mockTimestamp}
        botProfile={mockBotProfile}
      />
    );

    const messageDiv = container.querySelector('.bg-gray-50');
    expect(messageDiv).toBeInTheDocument();
  });

  it('does not show bot avatar for user messages', () => {
    render(
      <MessageBubble
        message="User message"
        sender="user"
        timestamp={mockTimestamp}
        botProfile={mockBotProfile}
      />
    );

    expect(screen.queryByText('🤖')).not.toBeInTheDocument();
    expect(screen.queryByText('Test Bot')).not.toBeInTheDocument();
  });

  it('applies animation classes when isAnimated is true', () => {
    const { container } = render(
      <MessageBubble
        message="Test message"
        sender="user"
        timestamp={mockTimestamp}
        isAnimated={true}
      />
    );

    const messageContainer = container.firstChild;
    expect(messageContainer).toHaveClass('animate-slide-in-right');
  });

  it('does not apply animation classes when isAnimated is false', () => {
    const { container } = render(
      <MessageBubble
        message="Test message"
        sender="user"
        timestamp={mockTimestamp}
        isAnimated={false}
      />
    );

    const messageContainer = container.firstChild;
    expect(messageContainer).not.toHaveClass('animate-slide-in-right');
  });

  it('applies different animation directions for user vs bot messages', () => {
    const { container: userContainer } = render(
      <MessageBubble
        message="User message"
        sender="user"
        timestamp={mockTimestamp}
        isAnimated={true}
      />
    );

    const { container: botContainer } = render(
      <MessageBubble
        message="Bot message"
        sender="bot"
        timestamp={mockTimestamp}
        isAnimated={true}
      />
    );

    expect(userContainer.firstChild).toHaveClass('animate-slide-in-right');
    expect(botContainer.firstChild).toHaveClass('animate-slide-in-left');
  });
});
