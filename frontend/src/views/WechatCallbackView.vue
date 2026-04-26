<template>
  <div style="display:flex;justify-content:center;align-items:center;height:100vh">
    <span>正在处理微信登录...</span>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

onMounted(() => {
  const { token, refresh, newUser } = route.query
  if (!token) {
    ElMessage.error('微信登录失败，请重试')
    router.replace('/login')
    return
  }
  auth.login(token, refresh)
  router.replace(newUser === 'true' ? '/profile?welcome=1' : '/square')
})
</script>
