import axios from 'axios'
import type { UserLogin, UserRegister, AuthResponse, Result, User } from '@/types/user'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API Error:', error)
    return Promise.reject(error)
  }
)

export const userApi = {
  login: async (data: UserLogin): Promise<AuthResponse> => {
    const response = await api.post('/user/login', data)
    return response.data
  },

  register: async (data: UserRegister): Promise<AuthResponse> => {
    const response = await api.post('/user/register', data)
    return response.data
  },

  getUserInfo: async (): Promise<Result<User>> => {
    const response = await api.get('/user/info')
    return response.data
  },

  logout: () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  },

  isLoggedIn: () => {
    return !!localStorage.getItem('token')
  },

  getCurrentUser: () => {
    const userStr = localStorage.getItem('user')
    if (userStr) {
      try {
        return JSON.parse(userStr)
      } catch {
        return null
      }
    }
    return null
  },

  isAdmin: () => {
    const userStr = localStorage.getItem('user')
    if (userStr) {
      try {
        const user = JSON.parse(userStr)
        return user.role === 1
      } catch {
        return false
      }
    }
    return false
  }
}
