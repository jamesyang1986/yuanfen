<template>
  <div class="auth-container">
    <el-card style="width: 400px">
      <h2>登录</h2>
      <el-form :model="form" ref="formRef">
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width:100%" @click="submit">登录</el-button>
        </el-form-item>
      </el-form>
      <div style="text-align:center">
        没有账号？<router-link to="/register">去注册</router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../api/auth'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()
const form = ref({ username: '', password: '' })

async function submit() {
  try {
    const res = await login(form.value.username, form.value.password)
    const { accessToken, refreshToken } = res.data.data
    auth.login(accessToken, refreshToken)
    router.push('/profile')
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '登录失败')
  }
}
</script>

<style scoped>
.auth-container { display: flex; justify-content: center; align-items: center; height: 100vh; }
</style>
