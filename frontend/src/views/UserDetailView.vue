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
          {{ user.city || '未知' }} · {{ user.age != null ? user.age + '岁' : '未知' }}
        </div>
        <div v-if="user.occupation" style="color:#999;font-size:13px;margin-top:4px">{{ user.occupation }}</div>
      </div>

      <el-divider v-if="user.bio || (user.partnerTags && user.partnerTags.length)" />

      <div v-if="user.bio" style="margin-bottom:12px">
        <div style="font-size:13px;color:#999;margin-bottom:4px">自我介绍</div>
        <div style="font-size:14px;line-height:1.6">{{ user.bio }}</div>
      </div>

      <div v-if="user.partnerTags && user.partnerTags.length">
        <div style="font-size:13px;color:#999;margin-bottom:8px">择偶标签</div>
        <el-tag v-for="tag in user.partnerTags" :key="tag" style="margin:0 4px 4px 0" type="success" size="small">{{ tag }}</el-tag>
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
</script>
