import { render, screen } from '@testing-library/react'
import MessageBubble from '../MessageBubble'

describe('MessageBubble', () => {
  const mockTimestamp = new Date('2023-01-01T12:00:00Z')
  const expectedTimeString = mockTimestamp.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })

  it('renders user message correctly', () => {
    render(
      <MessageBubble
        message="Hello, bot!"
        sender="user"
        timestamp={mockTimestamp}
      />
    )

    expect(screen.getByText('Hello, bot!')).toBeInTheDocument()
    expect(screen.getByText(expectedTimeString)).toBeInTheDocument()
  })

  it('renders bot message correctly', () => {
    render(
      <MessageBubble
        message="Hello, user!"
        sender="bot"
        timestamp={mockTimestamp}
      />
    )

    expect(screen.getByText('Hello, user!')).toBeInTheDocument()
    expect(screen.getByText(expectedTimeString)).toBeInTheDocument()
  })

  it('applies correct styling for user messages', () => {
    const { container } = render(
      <MessageBubble
        message="User message"
        sender="user"
        timestamp={mockTimestamp}
      />
    )

    const messageDiv = container.querySelector('.bg-primary-500')
    expect(messageDiv).toBeInTheDocument()
  })

  it('applies correct styling for bot messages', () => {
    const { container } = render(
      <MessageBubble
        message="Bot message"
        sender="bot"
        timestamp={mockTimestamp}
      />
    )

    const messageDiv = container.querySelector('.bg-gray-100')
    expect(messageDiv).toBeInTheDocument()
  })
})
