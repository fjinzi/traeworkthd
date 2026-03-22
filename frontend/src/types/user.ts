export interface User {
  id: number
  username: string
  role: number
  status: number
  createTime?: string
  updateTime?: string
}

export interface LoginForm {
  username: string
  password: string
}

export interface RegisterForm {
  username: string
  password: string
  confirmPassword: string
}

export interface LoginResult {
  token: string
  user: User
}

export interface Result<T> {
  code: number
  message: string
  data: T
}
