import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  { path: '/', redirect: '/square' },
  { path: '/login', component: () => import('../views/LoginView.vue') },
  { path: '/register', component: () => import('../views/RegisterView.vue') },
  { path: '/profile', component: () => import('../views/ProfileView.vue'), meta: { requiresAuth: true } },
  { path: '/square', component: () => import('../views/SquareView.vue'), meta: { requiresAuth: true } },
  { path: '/user/:id', component: () => import('../views/UserDetailView.vue'), meta: { requiresAuth: true } },
  { path: '/wechat-callback', component: () => import('../views/WechatCallbackView.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth && !auth.isLoggedIn) return '/login'
  if ((to.path === '/login' || to.path === '/register') && auth.isLoggedIn) return '/square'
})

export default router
