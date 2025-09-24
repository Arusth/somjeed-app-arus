import { render, screen } from '@testing-library/react';
import LoadingIndicator from '../LoadingIndicator';
import type { BotProfile } from '@/types/chat';

describe('LoadingIndicator', () => {
  const mockBotProfile: BotProfile = {
    name: 'Test Bot',
    avatar: '🤖',
    color: '#22c55e'
  };

  test('should render loading indicator with bot avatar', () => {
    render(<LoadingIndicator botProfile={mockBotProfile} />);
    
    expect(screen.getByText('🤖')).toBeInTheDocument();
    expect(screen.getByText('typing...')).toBeInTheDocument();
  });

  test('should render correct loading text for different variants', () => {
    const { rerender } = render(
      <LoadingIndicator botProfile={mockBotProfile} variant="typing" />
    );
    expect(screen.getByText('typing...')).toBeInTheDocument();

    rerender(<LoadingIndicator botProfile={mockBotProfile} variant="thinking" />);
    expect(screen.getByText('thinking...')).toBeInTheDocument();

    rerender(<LoadingIndicator botProfile={mockBotProfile} variant="processing" />);
    expect(screen.getByText('processing...')).toBeInTheDocument();
  });

  test('should render animated dots', () => {
    render(<LoadingIndicator botProfile={mockBotProfile} />);
    
    const dots = screen.getAllByRole('generic').filter(el => 
      el.className.includes('w-2 h-2 bg-gray-400 rounded-full')
    );
    expect(dots).toHaveLength(3);
  });

  test('should apply slide-in-left animation', () => {
    const { container } = render(<LoadingIndicator botProfile={mockBotProfile} />);
    
    const outerDiv = container.firstChild;
    expect(outerDiv).toHaveClass('animate-slide-in-left');
  });

  test('should apply custom className', () => {
    const { container } = render(
      <LoadingIndicator 
        botProfile={mockBotProfile} 
        className="custom-loading-class" 
      />
    );
    
    const outerDiv = container.firstChild;
    expect(outerDiv).toHaveClass('custom-loading-class');
  });

  test('should have correct animation classes for different variants', () => {
    const { rerender } = render(
      <LoadingIndicator botProfile={mockBotProfile} variant="typing" />
    );
    let dots = screen.getAllByRole('generic').filter(el => 
      el.className.includes('animate-typing')
    );
    expect(dots.length).toBeGreaterThan(0);

    rerender(<LoadingIndicator botProfile={mockBotProfile} variant="thinking" />);
    dots = screen.getAllByRole('generic').filter(el => 
      el.className.includes('animate-pulse-soft')
    );
    expect(dots.length).toBeGreaterThan(0);
  });
});
