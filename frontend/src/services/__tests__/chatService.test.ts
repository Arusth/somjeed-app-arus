import axios from 'axios'
import { sendMessage, checkHealth } from '../chatService'

// Mock axios
jest.mock('axios')
const mockedAxios = axios as jest.Mocked<typeof axios>

describe('chatService', () => {
  beforeEach(() => {
    jest.clearAllMocks()
  })

  describe('sendMessage', () => {
    it('should send message and return response', async () => {
      const mockResponse = {
        data: {
          message: 'Echo: Hello',
          timestamp: '2023-01-01T12:00:00Z'
        }
      }

      mockedAxios.create.mockReturnValue({
        post: jest.fn().mockResolvedValue(mockResponse),
        get: jest.fn()
      } as any)

      const result = await sendMessage('Hello')

      expect(result).toEqual({
        message: 'Echo: Hello',
        timestamp: '2023-01-01T12:00:00Z'
      })
    })

    it('should throw error when request fails', async () => {
      mockedAxios.create.mockReturnValue({
        post: jest.fn().mockRejectedValue(new Error('Network error')),
        get: jest.fn()
      } as any)

      await expect(sendMessage('Hello')).rejects.toThrow('Failed to send message')
    })
  })

  describe('checkHealth', () => {
    it('should return health status', async () => {
      const mockResponse = {
        data: 'Chat service is running'
      }

      mockedAxios.create.mockReturnValue({
        post: jest.fn(),
        get: jest.fn().mockResolvedValue(mockResponse)
      } as any)

      const result = await checkHealth()

      expect(result).toBe('Chat service is running')
    })

    it('should throw error when health check fails', async () => {
      mockedAxios.create.mockReturnValue({
        post: jest.fn(),
        get: jest.fn().mockRejectedValue(new Error('Service unavailable'))
      } as any)

      await expect(checkHealth()).rejects.toThrow('Failed to check service health')
    })
  })
})
