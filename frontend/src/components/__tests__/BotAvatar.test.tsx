import { render, screen } from '@testing-library/react';
import BotAvatar from '../BotAvatar';
import type { BotProfile } from '@/types/chat';

describe('BotAvatar', () => {
  const mockBotProfile: BotProfile = {
    name: 'Test Bot',
    avatar: '🤖',
    color: '#22c55e'
  };

  test('should render bot avatar with correct styling', () => {
    render(<BotAvatar profile={mockBotProfile} />);
    
    const avatar = screen.getByText('🤖');
    expect(avatar).toBeInTheDocument();
    expect(avatar).toHaveStyle({ backgroundColor: '#22c55e' });
  });

  test('should render bot name when showName is true', () => {
    render(<BotAvatar profile={mockBotProfile} showName={true} />);
    
    expect(screen.getByText('Test Bot')).toBeInTheDocument();
  });

  test('should not render bot name when showName is false', () => {
    render(<BotAvatar profile={mockBotProfile} showName={false} />);
    
    expect(screen.queryByText('Test Bot')).not.toBeInTheDocument();
  });

  test('should apply correct size classes', () => {
    const { rerender } = render(<BotAvatar profile={mockBotProfile} size="sm" />);
    let avatar = screen.getByText('🤖');
    expect(avatar).toHaveClass('w-6');
    expect(avatar).toHaveClass('h-6');

    rerender(<BotAvatar profile={mockBotProfile} size="lg" />);
    avatar = screen.getByText('🤖');
    expect(avatar).toHaveClass('w-10');
    expect(avatar).toHaveClass('h-10');
  });

  test('should apply custom className', () => {
    render(<BotAvatar profile={mockBotProfile} className="custom-class" />);
    
    const container = screen.getByText('🤖').closest('div')?.parentElement;
    expect(container).toHaveClass('custom-class');
  });

  test('should have fade-in animation class', () => {
    render(<BotAvatar profile={mockBotProfile} />);
    
    const container = screen.getByText('🤖').closest('div')?.parentElement;
    expect(container).toHaveClass('animate-fade-in');
  });
});
