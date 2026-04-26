<template>
  <el-menu mode="horizontal" :router="true" :default-active="$route.path" v-if="auth.isLoggedIn">
    <el-menu-item index="/square">广场</el-menu-item>
    <el-menu-item index="/profile">我的资料</el-menu-item>
  </el-menu>
</template>

<script setup>
import { useAuthStore } from '../stores/auth'
const auth = useAuthStore()
</script>
