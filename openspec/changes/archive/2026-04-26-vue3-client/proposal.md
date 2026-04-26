## Why

项目已有完整的后端 REST API（注册、登录、用户资料、头像上传），但缺少 Web 端入口，导致功能无法在浏览器中使用。使用 Vue 3 构建前端客户端，与现有 API 对接，提供完整的 Web 用户体验。

## What Changes

- 新建独立 `frontend/` 目录，包含完整 Vue 3 + Vite 工程
- 实现用户注册页（用户名/密码方式）
- 实现用户登录页，登录成功后保存 JWT Token
- 实现个人资料页（昵称、性别、出生年月、城市、地址查看与编辑）
- 实现头像上传（选取本地图片，预览并上传至服务端）
- 实现退出登录（清除 Token，跳回登录页）
- 封装 Axios HTTP 客户端，自动携带 Bearer Token，统一处理 401 跳转

## Capabilities

### New Capabilities

- `vue3-auth`: Vue 3 注册 / 登录 / 退出登录页面及路由守卫
- `vue3-user-profile`: Vue 3 个人资料查看与编辑页面（含头像上传）

### Modified Capabilities

（无现有规格变更）

## Impact

- 新增 `frontend/` 目录（Vite + Vue 3 工程），不影响后端代码
- 依赖：Vue 3、Vue Router 4、Pinia、Axios、Element Plus（UI 组件库）
- 后端 API：依赖现有 `/api/v1/auth/*` 和 `/api/v1/users/*` 端点
- 需要后端启用 CORS（允许前端开发服务器 `http://localhost:5173`）
