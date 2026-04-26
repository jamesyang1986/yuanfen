<template>
  <div class="auth-container">
    <el-card style="width: 400px">
      <h2>注册</h2>
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名（4-32位字母数字）" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码（6-64位）" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width:100%" @click="submit">注册</el-button>
        </el-form-item>
      </el-form>
      <div style="text-align:center">
        已有账号？<router-link to="/login">去登录</router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { registerByUsername } from '../api/auth'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()
const formRef = ref()
const form = ref({ username: '', password: '' })
const rules = {
  username: [{ pattern: /^[a-zA-Z0-9]{4,32}$/, message: '用户名需4-32位字母数字', trigger: 'blur' }],
  password: [{ min: 6, max: 64, message: '密码需6-64位', trigger: 'blur' }]
}

async function submit() {
  await formRef.value.validate()
  try {
    const res = await registerByUsername(form.value.username, form.value.password)
    const { accessToken, refreshToken } = res.data.data
    auth.login(accessToken, refreshToken)
    router.push('/profile?welcome=1')
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '注册失败')
  }
}
</script>

<style scoped>
.auth-container { display: flex; justify-content: center; align-items: center; height: 100vh; }
</style>
