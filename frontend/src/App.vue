<template>
  <div id="app">
    <nav class="nav-bar">
      <div class="nav-left">
        <router-link to="/" class="nav-link">秒杀活动</router-link>
        <router-link to="/admin" class="nav-link" v-if="isAdmin">商品管理</router-link>
      </div>
      <div class="nav-right">
        <template v-if="isLoggedIn">
          <span class="user-info">欢迎, {{ currentUser?.username }}</span>
          <button class="logout-btn" @click="handleLogout">退出登录</button>
        </template>
        <router-link v-else to="/login" class="nav-link">登录/注册</router-link>
      </div>
    </nav>
    <router-view />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { userApi } from '@/api/user'

const router = useRouter()
const isLoggedIn = ref(false)
const isAdmin = ref(false)
const currentUser = ref<any>(null)

const updateAuthStatus = () => {
  isLoggedIn.value = userApi.isLoggedIn()
  isAdmin.value = userApi.isAdmin()
  currentUser.value = userApi.getCurrentUser()
}

const handleLogout = () => {
  userApi.logout()
  updateAuthStatus()
  router.push('/')
}

onMounted(() => {
  updateAuthStatus()
})

router.beforeEach((to, from, next) => {
  updateAuthStatus()
  next()
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background: #f5f5f5;
  min-height: 100vh;
}

#app {
  min-height: 100vh;
}

.nav-bar {
  background: #fff;
  padding: 0 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.nav-left,
.nav-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.nav-link {
  display: inline-block;
  padding: 16px 20px;
  color: #333;
  text-decoration: none;
  font-weight: 500;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
}

.nav-link:hover {
  color: #1890ff;
}

.nav-link.router-link-active {
  color: #1890ff;
  border-bottom-color: #1890ff;
}

.user-info {
  color: #666;
  font-size: 14px;
  margin-right: 10px;
}

.logout-btn {
  padding: 8px 16px;
  background: #ff4d4f;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.3s;
}

.logout-btn:hover {
  background: #ff7875;
}
</style>
