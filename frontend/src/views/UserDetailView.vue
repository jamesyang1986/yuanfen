<template>
  <div style="max-width:480px;margin:40px auto;padding:0 16px">
    <el-button @click="$router.push('/square')" style="margin-bottom:16px">← 返回广场</el-button>

    <el-card v-if="user">
      <div style="text-align:center;padding:16px 0">
        <el-avatar :size="100" :src="user.avatarUrl || undefined">
          {{ user.nickname ? user.nickname[0] : '?' }}
        </el-avatar>
        <h2 style="margin:12px 0 4px">{{ user.nickname || '未设置昵称' }}</h2>
        <div style="color:#666;font-size:14px">
          {{ user.city || '未知' }} · {{ calcAge(user.birthDate) }}
        </div>
      </div>
    </el-card>

    <el-result v-else-if="notFound" icon="warning" title="用户不存在" sub-title="该用户不存在或已注销">
      <template #extra>
        <el-button @click="$router.push('/square')">返回广场</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getUserById } from '../api/user'

const route = useRoute()
const user = ref(null)
const notFound = ref(false)

onMounted(async () => {
  try {
    const res = await getUserById(route.params.id)
    user.value = res.data.data
  } catch (e) {
    if (e.response?.status === 404) notFound.value = true
  }
})

function calcAge(birthDate) {
  if (!birthDate) return '未知'
  const birthYear = parseInt(birthDate.substring(0, 4))
  return `${new Date().getFullYear() - birthYear}岁`
}
</script>
