export interface User {
  id: number
  username: string
  email?: string
  phone?: string
  role: number
  status: number
  createTime?: string
}

export interface UserLogin {
  username: string
  password: string
}

export interface UserRegister {
  username: string
  password: string
  email?: string
  phone?: string
}

export interface AuthResponse {
  success: boolean
  message: string
  data: {
    id: number
    username: string
    role: number
    token: string
  }
}

export interface Result<T> {
  success: boolean
  message: string
  data: T
}
