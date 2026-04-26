<template>
  <div style="max-width:1000px;margin:24px auto;padding:0 16px">
    <h2>用户广场</h2>

    <el-row :gutter="16">
      <el-col v-for="user in users" :key="user.id" :xs="12" :sm="8" :md="6" style="margin-bottom:16px">
        <el-card class="user-card" @click="$router.push(`/user/${user.id}`)">
          <div style="text-align:center">
            <el-avatar :size="72" :src="user.avatarUrl || undefined">
              {{ user.nickname ? user.nickname[0] : '?' }}
            </el-avatar>
            <div class="nickname">{{ user.nickname || '未设置昵称' }}</div>
            <div class="meta">{{ user.city || '未知' }} · {{ calcAge(user.birthDate) }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-empty v-if="!loading && users.length === 0" description="暂无用户" />

    <div style="text-align:center;margin-top:16px">
      <el-pagination
        v-if="total > pageSize"
        background
        layout="prev, pager, next"
        :total="total"
        :page-size="pageSize"
        :current-page="currentPage + 1"
        @current-change="onPageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { listUsers } from '../api/user'

const users = ref([])
const total = ref(0)
const currentPage = ref(0)
const pageSize = 20
const loading = ref(false)

onMounted(() => load(0))

async function load(page) {
  loading.value = true
  try {
    const res = await listUsers(page, pageSize)
    users.value = res.data.data.items
    total.value = res.data.data.total
    currentPage.value = page
  } catch {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

function onPageChange(page) {
  load(page - 1)
}

function calcAge(birthDate) {
  if (!birthDate) return '未知'
  const birthYear = parseInt(birthDate.substring(0, 4))
  const age = new Date().getFullYear() - birthYear
  return `${age}岁`
}
</script>

<style scoped>
.user-card { cursor: pointer; transition: box-shadow 0.2s; }
.user-card:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.12); }
.nickname { font-weight: 600; margin-top: 8px; font-size: 15px; }
.meta { color: #999; font-size: 13px; margin-top: 4px; }
</style>
