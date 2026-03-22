<template>
  <div class="login-container">
    <div class="login-box">
      <h2>{{ isLogin ? '用户登录' : '用户注册' }}</h2>
      
      <form @submit.prevent="handleSubmit" class="login-form">
        <div class="form-group">
          <label>用户名</label>
          <input 
            v-model="form.username" 
            type="text" 
            placeholder="请输入用户名"
            required
            minlength="3"
            maxlength="20"
          />
        </div>

        <div class="form-group">
          <label>密码</label>
          <input 
            v-model="form.password" 
            type="password" 
            placeholder="请输入密码"
            required
            minlength="6"
            maxlength="20"
          />
        </div>

        <div class="form-group" v-if="!isLogin">
          <label>确认密码</label>
          <input 
            v-model="form.confirmPassword" 
            type="password" 
            placeholder="请再次输入密码"
            required
            minlength="6"
            maxlength="20"
          />
        </div>

        <div class="error-message" v-if="errorMsg">{{ errorMsg }}</div>

        <button type="submit" class="submit-btn" :disabled="loading">
          {{ loading ? '处理中...' : (isLogin ? '登录' : '注册') }}
        </button>
      </form>

      <div class="switch-mode">
        <span>{{ isLogin ? '还没有账号？' : '已有账号？' }}</span>
        <a href="#" @click.prevent="toggleMode">
          {{ isLogin ? '立即注册' : '立即登录' }}
        </a>
      </div>

      <div class="back-link">
        <router-link to="/">返回首页</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { authApi } from '@/api/auth'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const isLogin = ref(true)
const loading = ref(false)
const errorMsg = ref('')

const form = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

const toggleMode = () => {
  isLogin.value = !isLogin.value
  errorMsg.value = ''
  form.username = ''
  form.password = ''
  form.confirmPassword = ''
}

const handleSubmit = async () => {
  errorMsg.value = ''
  
  if (!isLogin.value && form.password !== form.confirmPassword) {
    errorMsg.value = '两次输入的密码不一致'
    return
  }

  loading.value = true
  try {
    if (isLogin.value) {
      const res = await authApi.login({
        username: form.username,
        password: form.password
      })
      
      if (res.success && res.data) {
        userStore.setToken(res.data.token)
        userStore.setUser(res.data.user)
        router.push('/')
      } else {
        errorMsg.value = res.message || '登录失败'
      }
    } else {
      const res = await authApi.register({
        username: form.username,
        password: form.password,
        confirmPassword: form.confirmPassword
      })
      
      if (res.success) {
        alert('注册成功，请登录')
        isLogin.value = true
        form.password = ''
        form.confirmPassword = ''
      } else {
        errorMsg.value = res.message || '注册失败'
      }
    }
  } catch (error: any) {
    errorMsg.value = error.response?.data?.message || '网络错误，请稍后重试'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 400px;
}

h2 {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
  font-size: 24px;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 14px;
  color: #555;
  font-weight: 500;
}

.form-group input {
  padding: 12px 16px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-group input:focus {
  outline: none;
  border-color: #667eea;
}

.error-message {
  color: #e74c3c;
  font-size: 14px;
  text-align: center;
}

.submit-btn {
  padding: 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 20px rgba(102, 126, 234, 0.4);
}

.submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.switch-mode {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #666;
}

.switch-mode a {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
  margin-left: 5px;
}

.switch-mode a:hover {
  text-decoration: underline;
}

.back-link {
  text-align: center;
  margin-top: 15px;
}

.back-link a {
  color: #999;
  font-size: 14px;
  text-decoration: none;
}

.back-link a:hover {
  color: #667eea;
}
</style>
