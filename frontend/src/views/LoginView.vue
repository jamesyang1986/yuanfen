<template>
  <div class="auth-container">
    <el-card style="width: 400px">
      <h2 style="margin-top:0">登录</h2>
      <el-tabs v-model="activeTab">
        <!-- 用户名登录 Tab -->
        <el-tab-pane label="用户名登录" name="username">
          <el-form :model="usernameForm">
            <el-form-item>
              <el-input v-model="usernameForm.username" placeholder="用户名" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="usernameForm.password" type="password" placeholder="密码" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" style="width:100%" @click="submitUsername">登录</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 手机号登录 Tab -->
        <el-tab-pane label="手机号登录" name="phone">
          <el-form :model="phoneForm">
            <el-form-item>
              <el-input v-model="phoneForm.phone" placeholder="手机号" maxlength="11" />
            </el-form-item>
            <el-form-item>
              <div style="display:flex;gap:8px">
                <el-input v-model="phoneForm.code" placeholder="验证码" maxlength="6" style="flex:1" />
                <el-button
                  :disabled="countdown > 0"
                  style="width:110px;flex-shrink:0"
                  @click="sendCode"
                >
                  {{ countdown > 0 ? `${countdown}s 后重发` : '发送验证码' }}
                </el-button>
              </div>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" style="width:100%" @click="submitPhone">登录</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>

      <div style="text-align:center;margin-top:8px">
        没有账号？<router-link to="/register">去注册</router-link>
      </div>

      <el-divider>其他登录方式</el-divider>
      <div style="text-align:center">
        <el-button style="width:100%" @click="loginWithWechat">微信登录</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login, loginByPhone, sendSmsCode, getWechatAuthUrl } from '../api/auth'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()

const activeTab = ref('username')
const usernameForm = ref({ username: '', password: '' })
const phoneForm = ref({ phone: '', code: '' })

const countdown = ref(0)
let timer = null

async function submitUsername() {
  try {
    const res = await login(usernameForm.value.username, usernameForm.value.password)
    const { accessToken, refreshToken } = res.data.data
    auth.login(accessToken, refreshToken)
    router.push('/square')
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '登录失败')
  }
}

async function sendCode() {
  if (!phoneForm.value.phone) {
    ElMessage.warning('请输入手机号')
    return
  }
  try {
    await sendSmsCode(phoneForm.value.phone)
    ElMessage.success('验证码已发送（测试环境固定为 123456）')
    countdown.value = 30
    timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timer)
        timer = null
      }
    }, 1000)
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '发送失败')
  }
}

async function submitPhone() {
  try {
    const res = await loginByPhone(phoneForm.value.phone, phoneForm.value.code)
    const { accessToken, refreshToken, newUser } = res.data.data
    auth.login(accessToken, refreshToken)
    router.push(newUser ? '/profile?welcome=1' : '/square')
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '登录失败')
  }
}

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

async function loginWithWechat() {
  try {
    const res = await getWechatAuthUrl()
    window.location.href = res.data.data.url
  } catch (e) {
    ElMessage.error('获取微信授权链接失败')
  }
}
</script>

<style scoped>
.auth-container { display: flex; justify-content: center; align-items: center; height: 100vh; }
</style>
