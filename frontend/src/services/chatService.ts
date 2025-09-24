import axios from 'axios'

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080'

export interface ChatRequest {
  message: string
}

export interface ChatResponse {
  message: string
  timestamp: string
}

const chatApi = axios.create({
  baseURL: `${API_BASE_URL}/api/chat`,
  headers: {
    'Content-Type': 'application/json',
  },
})

export const sendMessage = async (message: string): Promise<ChatResponse> => {
  try {
    const response = await chatApi.post<ChatResponse>('/message', { message })
    return response.data
  } catch (error) {
    console.error('Error sending message:', error)
    throw new Error('Failed to send message')
  }
}

export const checkHealth = async (): Promise<string> => {
  try {
    const response = await chatApi.get<string>('/health')
    return response.data
  } catch (error) {
    console.error('Error checking health:', error)
    throw new Error('Failed to check service health')
  }
}
