import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Seckill',
    component: () => import('@/views/SeckillPage.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue'),
    meta: { guest: true }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/views/ProductAdmin.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 从 localStorage 获取用户状态
  const token = localStorage.getItem('token')
  const userStr = localStorage.getItem('user')
  let user = null
  if (userStr) {
    try {
      user = JSON.parse(userStr)
    } catch {
      user = null
    }
  }
  
  const isLoggedIn = !!token
  const isAdmin = user?.role === 1
  
  // 需要登录的页面
  if (to.meta.requiresAuth && !isLoggedIn) {
    next('/login')
    return
  }
  
  // 需要管理员权限的页面
  if (to.meta.requiresAdmin && !isAdmin) {
    alert('无权访问，需要管理员权限')
    next('/')
    return
  }
  
  // 游客页面（已登录用户不能访问）
  if (to.meta.guest && isLoggedIn) {
    next('/')
    return
  }
  
  next()
})

export default router
