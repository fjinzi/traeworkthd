<template>
  <div class="auth-container">
    <div class="auth-card">
      <div class="auth-tabs">
        <button 
          class="tab-btn" 
          :class="{ active: activeTab === 'login' }"
          @click="activeTab = 'login'"
        >
          登录
        </button>
        <button 
          class="tab-btn" 
          :class="{ active: activeTab === 'register' }"
          @click="activeTab = 'register'"
        >
          注册
        </button>
      </div>

      <form v-if="activeTab === 'login'" @submit.prevent="handleLogin" class="auth-form">
        <div class="form-group">
          <label>用户名</label>
          <input 
            v-model="loginForm.username" 
            type="text" 
            placeholder="请输入用户名"
            required
          />
        </div>
        <div class="form-group">
          <label>密码</label>
          <input 
            v-model="loginForm.password" 
            type="password" 
            placeholder="请输入密码"
            required
          />
        </div>
        <button type="submit" class="submit-btn" :disabled="loading">
          {{ loading ? '登录中...' : '登 录' }}
        </button>
      </form>

      <form v-else @submit.prevent="handleRegister" class="auth-form">
        <div class="form-group">
          <label>用户名</label>
          <input 
            v-model="registerForm.username" 
            type="text" 
            placeholder="请输入用户名(4-16位字母数字下划线)"
            required
          />
        </div>
        <div class="form-group">
          <label>密码</label>
          <input 
            v-model="registerForm.password" 
            type="password" 
            placeholder="请输入密码(6-20位字母数字下划线)"
            required
          />
        </div>
        <div class="form-group">
          <label>确认密码</label>
          <input 
            v-model="registerForm.confirmPassword" 
            type="password" 
            placeholder="请再次输入密码"
            required
          />
        </div>
        <div class="form-group">
          <label>邮箱(可选)</label>
          <input 
            v-model="registerForm.email" 
            type="email" 
            placeholder="请输入邮箱"
          />
        </div>
        <div class="form-group">
          <label>手机号(可选)</label>
          <input 
            v-model="registerForm.phone" 
            type="tel" 
            placeholder="请输入手机号"
          />
        </div>
        <button type="submit" class="submit-btn" :disabled="loading">
          {{ loading ? '注册中...' : '注 册' }}
        </button>
      </form>

      <div v-if="message.show" :class="['message', message.type]">
        {{ message.text }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { userApi } from '@/api/user'
import type { UserLogin, UserRegister } from '@/types/user'

const router = useRouter()

const activeTab = ref<'login' | 'register'>('login')
const loading = ref(false)

const loginForm = reactive<UserLogin>({
  username: '',
  password: ''
})

const registerForm = reactive<UserRegister & { confirmPassword: string }>({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  phone: ''
})

const message = reactive({
  show: false,
  type: 'success',
  text: ''
})

const showMessage = (text: string, type: 'success' | 'error' = 'success') => {
  message.text = text
  message.type = type
  message.show = true
  setTimeout(() => {
    message.show = false
  }, 3000)
}

const handleLogin = async () => {
  if (!loginForm.username || !loginForm.password) {
    showMessage('请输入用户名和密码', 'error')
    return
  }

  loading.value = true
  try {
    const result = await userApi.login(loginForm)
    if (result.success) {
      localStorage.setItem('token', result.data.token)
      localStorage.setItem('user', JSON.stringify(result.data))
      showMessage('登录成功，正在跳转...')
      setTimeout(() => {
        router.push('/')
      }, 1000)
    } else {
      showMessage(result.message || '登录失败', 'error')
    }
  } catch (e) {
    showMessage('登录失败，请稍后重试', 'error')
  } finally {
    loading.value = false
  }
}

const handleRegister = async () => {
  if (!registerForm.username || !registerForm.password) {
    showMessage('请输入用户名和密码', 'error')
    return
  }

  if (registerForm.password !== registerForm.confirmPassword) {
    showMessage('两次输入的密码不一致', 'error')
    return
  }

  const usernameRegex = /^[a-zA-Z0-9_]{4,16}$/
  if (!usernameRegex.test(registerForm.username)) {
    showMessage('用户名必须是4-16位字母、数字或下划线', 'error')
    return
  }

  const passwordRegex = /^[a-zA-Z0-9_]{6,20}$/
  if (!passwordRegex.test(registerForm.password)) {
    showMessage('密码必须是6-20位字母、数字或下划线', 'error')
    return
  }

  loading.value = true
  try {
    const result = await userApi.register({
      username: registerForm.username,
      password: registerForm.password,
      email: registerForm.email,
      phone: registerForm.phone
    })
    if (result.success) {
      showMessage('注册成功，请登录')
      activeTab.value = 'login'
      loginForm.username = registerForm.username
      loginForm.password = ''
    } else {
      showMessage(result.message || '注册失败', 'error')
    }
  } catch (e) {
    showMessage('注册失败，请稍后重试', 'error')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.auth-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 420px;
  padding: 30px;
}

.auth-tabs {
  display: flex;
  margin-bottom: 30px;
  border-bottom: 2px solid #eee;
}

.tab-btn {
  flex: 1;
  padding: 12px;
  border: none;
  background: none;
  font-size: 16px;
  color: #666;
  cursor: pointer;
  position: relative;
  transition: color 0.3s;
}

.tab-btn.active {
  color: #667eea;
  font-weight: bold;
}

.tab-btn.active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  right: 0;
  height: 2px;
  background: #667eea;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-group label {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.form-group input {
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.3s, box-shadow 0.3s;
}

.form-group input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.submit-btn {
  padding: 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.message {
  margin-top: 20px;
  padding: 12px;
  border-radius: 6px;
  text-align: center;
  font-size: 14px;
}

.message.success {
  background: #f6ffed;
  border: 1px solid #b7eb8f;
  color: #52c41a;
}

.message.error {
  background: #fff1f0;
  border: 1px solid #ffa39e;
  color: #f5222d;
}
</style>
