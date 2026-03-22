import { createRouter, createWebHistory } from 'vue-router'
import { userApi } from '@/api/user'

const routes = [
  {
    path: '/',
    name: 'Seckill',
    component: () => import('@/views/SeckillPage.vue')
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/views/ProductAdmin.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginPage.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const isLoggedIn = userApi.isLoggedIn()
  const isAdmin = userApi.isAdmin()

  if (to.meta.requiresAuth && !isLoggedIn) {
    next('/login')
  } else if (to.meta.requiresAdmin && !isAdmin) {
    next('/login')
  } else if (to.path === '/login' && isLoggedIn) {
    next('/')
  } else {
    next()
  }
})

export default router
