import axios from 'axios'
import type { User, LoginForm, RegisterForm, LoginResult, Result } from '@/types/user'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器 - 添加token
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

// 响应拦截器 - 处理401错误
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export const authApi = {
  login: async (data: LoginForm): Promise<Result<LoginResult>> => {
    const response = await api.post('/auth/login', data)
    return response.data
  },

  register: async (data: RegisterForm): Promise<Result<User>> => {
    const response = await api.post('/auth/register', data)
    return response.data
  },

  getUserInfo: async (): Promise<Result<User>> => {
    const response = await api.get('/auth/info')
    return response.data
  }
}

export default api
