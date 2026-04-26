<template>
  <div style="max-width:600px;margin:40px auto;padding:0 16px">
    <el-alert
      v-if="isNewUser"
      title="欢迎加入缘分！完善资料后即可进入用户广场"
      type="success"
      show-icon
      :closable="false"
      style="margin-bottom:16px"
    />
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
        <el-form-item label="年龄">
          <span style="color:#666">{{ form.age != null ? form.age + ' 岁' : (form.birthDate ? '' : '请先填写出生年月') }}</span>
        </el-form-item>
        <el-form-item label="城市">
          <el-input v-model="form.city" maxlength="64" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" maxlength="256" />
        </el-form-item>
        <el-form-item label="职业">
          <el-input v-model="form.occupation" maxlength="64" placeholder="如：软件工程师" />
        </el-form-item>
        <el-form-item label="自我介绍">
          <el-input
            v-model="form.bio"
            type="textarea"
            :rows="4"
            maxlength="500"
            show-word-limit
            placeholder="介绍一下自己吧..."
          />
        </el-form-item>
        <el-form-item label="微信号">
          <el-input v-model="form.wechatId" maxlength="64" placeholder="填写后可展示给其他用户" />
        </el-form-item>
        <el-form-item label="QQ 号">
          <el-input v-model="form.qqNumber" maxlength="20" placeholder="填写后可展示给其他用户" />
        </el-form-item>
        <el-form-item label="择偶标签">
          <div>
            <el-checkbox-group v-model="form.partnerTags" :max="5">
              <el-checkbox v-for="tag in PARTNER_TAGS" :key="tag" :label="tag" style="margin-bottom:6px">{{ tag }}</el-checkbox>
            </el-checkbox-group>
            <div style="font-size:12px;color:#999;margin-top:4px">最多选 5 个</div>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="save">{{ isNewUser ? '保存并进入广场' : '保存' }}</el-button>
          <el-button v-if="isNewUser" style="margin-left:12px" @click="router.push('/square')">跳过</el-button>
          <el-button v-else style="margin-left:12px" @click="doLogout">退出登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getProfile, updateProfile, uploadAvatar } from '../api/user'
import { logout } from '../api/auth'
import { useAuthStore } from '../stores/auth'
import { calcAge } from '../utils/age'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()
const isNewUser = computed(() => route.query.welcome === '1')

const PARTNER_TAGS = ['三观相合', '爱好运动', '喜欢旅行', '宅家宅男/女', '爱宠物', '独立自主', '家庭观念强', '热爱美食', '文艺范儿', '幽默风趣']

const genderMap = { 0: '未知', 1: '男', 2: '女', 3: '其他' }
const form = ref({ nickname: '', gender: null, birthDate: null, city: '', address: '', avatarUrl: '', occupation: '', bio: '', partnerTags: [], age: null, wechatId: '', qqNumber: '' })

// 当 birthDate 变化时实时更新年龄预览
watch(() => form.value.birthDate, (val) => {
  form.value.age = calcAge(val)
})

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
      avatarUrl: p.avatarUrl || '',
      occupation: p.occupation || '',
      bio: p.bio || '',
      partnerTags: p.partnerTags || [],
      age: p.age ?? null,
      wechatId: p.wechatId || '',
      qqNumber: p.qqNumber || ''
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
  if (form.value.avatarUrl) fields.avatarUrl = form.value.avatarUrl
  if (form.value.occupation) fields.occupation = form.value.occupation
  if (form.value.bio) fields.bio = form.value.bio
  fields.partnerTags = form.value.partnerTags
  if (form.value.wechatId) fields.wechatId = form.value.wechatId
  if (form.value.qqNumber) fields.qqNumber = form.value.qqNumber
  try {
    await updateProfile(fields)
    ElMessage.success('保存成功')
    if (isNewUser.value) {
      router.push('/square')
    } else {
      await loadProfile()
    }
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
