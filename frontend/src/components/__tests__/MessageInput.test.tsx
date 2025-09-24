import { render, screen, act } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import MessageInput from '../MessageInput'

describe('MessageInput', () => {
  const mockOnSendMessage = jest.fn()

  beforeEach(() => {
    mockOnSendMessage.mockClear()
  })

  it('renders input and send button', () => {
    render(<MessageInput onSendMessage={mockOnSendMessage} />)

    expect(screen.getByPlaceholderText('Type your message...')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Send' })).toBeInTheDocument()
  })

  it('calls onSendMessage when form is submitted', async () => {
    const user = userEvent.setup()
    render(<MessageInput onSendMessage={mockOnSendMessage} />)

    const input = screen.getByPlaceholderText('Type your message...')
    const sendButton = screen.getByRole('button', { name: 'Send' })

    await act(async () => {
      await user.type(input, 'Hello, world!')
      await user.click(sendButton)
    })

    expect(mockOnSendMessage).toHaveBeenCalledWith('Hello, world!')
  })

  it('clears input after sending message', async () => {
    const user = userEvent.setup()
    render(<MessageInput onSendMessage={mockOnSendMessage} />)

    const input = screen.getByPlaceholderText('Type your message...') as HTMLTextAreaElement
    const sendButton = screen.getByRole('button', { name: 'Send' })

    await act(async () => {
      await user.type(input, 'Test message')
      await user.click(sendButton)
    })

    expect(input.value).toBe('')
  })

  it('does not send empty messages', async () => {
    const user = userEvent.setup()
    render(<MessageInput onSendMessage={mockOnSendMessage} />)

    const sendButton = screen.getByRole('button', { name: 'Send' })
    
    await act(async () => {
      await user.click(sendButton)
    })

    expect(mockOnSendMessage).not.toHaveBeenCalled()
  })

  it('sends message when Enter is pressed', async () => {
    const user = userEvent.setup()
    render(<MessageInput onSendMessage={mockOnSendMessage} />)

    const input = screen.getByPlaceholderText('Type your message...')
    
    await act(async () => {
      await user.type(input, 'Test message')
      await user.keyboard('{Enter}')
    })

    expect(mockOnSendMessage).toHaveBeenCalledWith('Test message')
  })

  it('disables input and button when disabled prop is true', () => {
    render(<MessageInput onSendMessage={mockOnSendMessage} disabled={true} />)

    const input = screen.getByPlaceholderText('Type your message...')
    const sendButton = screen.getByRole('button', { name: 'Sending...' })

    expect(input).toBeDisabled()
    expect(sendButton).toBeDisabled()
  })
})
