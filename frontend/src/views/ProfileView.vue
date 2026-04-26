<template>
  <div style="max-width:600px;margin:40px auto;padding:0 16px">
    <el-card>
      <h2>个人资料</h2>

      <!-- 头像 -->
      <div style="text-align:center;margin-bottom:24px">
        <el-upload
          :show-file-list="false"
          :before-upload="beforeUpload"
          :http-request="doUpload"
          accept="image/jpeg,image/png,image/gif,image/webp"
        >
          <el-avatar :size="100" :src="form.avatarUrl || undefined" style="cursor:pointer">
            <span>点击上传</span>
          </el-avatar>
        </el-upload>
        <div style="font-size:12px;color:#999;margin-top:4px">点击头像更换</div>
      </div>

      <el-form :model="form" label-width="80px">
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" maxlength="32" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="form.gender" style="width:100%">
            <el-option v-for="(label, val) in genderMap" :key="val" :label="label" :value="Number(val)" />
          </el-select>
        </el-form-item>
        <el-form-item label="出生年月">
          <el-date-picker
            v-model="form.birthDate"
            type="month"
            value-format="YYYY-MM"
            style="width:100%"
          />
        </el-form-item>
        <el-form-item label="城市">
          <el-input v-model="form.city" maxlength="64" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" maxlength="256" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="save">保存</el-button>
          <el-button style="margin-left:12px" @click="doLogout">退出登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getProfile, updateProfile, uploadAvatar } from '../api/user'
import { logout } from '../api/auth'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()

const genderMap = { 0: '未知', 1: '男', 2: '女', 3: '其他' }
const form = ref({ nickname: '', gender: null, birthDate: null, city: '', address: '', avatarUrl: '' })

onMounted(loadProfile)

async function loadProfile() {
  try {
    const res = await getProfile()
    const p = res.data.data
    form.value = {
      nickname: p.nickname || '',
      gender: p.gender ?? null,
      birthDate: p.birthDate || null,
      city: p.city || '',
      address: p.address || '',
      avatarUrl: p.avatarUrl || ''
    }
  } catch {
    ElMessage.error('加载资料失败')
  }
}

async function save() {
  const fields = {}
  if (form.value.nickname) fields.nickname = form.value.nickname
  if (form.value.city) fields.city = form.value.city
  if (form.value.address) fields.address = form.value.address
  if (form.value.birthDate) fields.birthDate = form.value.birthDate
  if (form.value.gender !== null) fields.gender = form.value.gender
  try {
    await updateProfile(fields)
    ElMessage.success('保存成功')
    await loadProfile()
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '保存失败')
  }
}

function beforeUpload(file) {
  const allowed = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
  if (!allowed.includes(file.type)) {
    ElMessage.error('仅支持 JPEG/PNG/GIF/WebP 格式')
    return false
  }
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('文件不能超过 5MB')
    return false
  }
  return true
}

async function doUpload({ file }) {
  try {
    const res = await uploadAvatar(file)
    form.value.avatarUrl = res.data.data.avatarUrl
    ElMessage.success('头像上传成功')
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '上传失败')
  }
}

async function doLogout() {
  try { await logout(auth.refreshToken) } catch { /* ignore */ }
  auth.logout()
  router.push('/login')
}
</script>
