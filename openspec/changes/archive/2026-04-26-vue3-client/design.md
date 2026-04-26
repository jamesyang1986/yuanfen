## Context

后端已提供完整的 REST API（Spring Boot 2.7 + JWT），包括注册、登录、Token 刷新、退出登录、用户资料 CRUD、头像上传。前端需要对接这套 API，以 Vue 3 + Vite 实现 Web 端功能。后端运行在 `http://localhost:8080`，开发期前端运行在 `http://localhost:5173`，需要解决跨域问题。

## Goals / Non-Goals

**Goals:**
- 使用 Vue 3 Composition API + `<script setup>` 语法构建前端
- 使用 Vue Router 4 管理页面路由，并实现登录守卫
- 使用 Pinia 管理全局认证状态（Token、用户信息）
- 使用 Axios 封装 HTTP 客户端，自动注入 Bearer Token，统一处理 401 自动跳转登录
- 使用 Element Plus 作为 UI 组件库，快速实现表单和交互
- 支持头像图片预览后上传

**Non-Goals:**
- 不实现短信验证码、邮箱注册等其他注册方式
- 不实现国际化（i18n）
- 不实现服务端渲染（SSR）
- 不配置生产环境 CI/CD 部署

## Decisions

### D1: Vite 作为构建工具
选 Vite 而非 Vue CLI / Webpack：Vite 启动速度快，HMR 体验好，是 Vue 3 生态官方推荐工具链。

### D2: Pinia 管理认证状态
选 Pinia 而非 Vuex：Pinia 是 Vue 3 官方推荐的状态管理库，API 更简洁，支持 Composition API。Token 存储在 `localStorage`，持久化跨页面刷新。

### D3: Axios 封装统一请求层
创建 `src/api/http.js` Axios 实例，配置 `baseURL`、请求拦截（注入 Token）、响应拦截（401 自动跳登录、统一错误提示）。各模块 API 函数按职责分文件（`auth.js`、`user.js`）。

### D4: 代理解决跨域
Vite 开发服务器配置 `server.proxy`，将 `/api` 请求代理到 `http://localhost:8080`，避免开发期跨域问题；同时后端可后续配置 CORS 用于生产。

### D5: Element Plus 组件库
选 Element Plus 而非 Ant Design Vue / Naive UI：Element Plus 文档丰富，表单验证集成完善，头像上传可用 `el-upload` 组件。

## Risks / Trade-offs

- [Token 安全] localStorage 存储 Token 存在 XSS 风险 → 当前为 MVP，后续可改用 httpOnly Cookie
- [后端 CORS] 生产部署时需要后端正确配置 CORS → 开发期用 Vite proxy 规避，生产环境需额外配置
- [头像上传大小] 超 5MB 文件前端不做 size 校验会直接报服务端 400 → 在上传前做客户端文件大小校验
