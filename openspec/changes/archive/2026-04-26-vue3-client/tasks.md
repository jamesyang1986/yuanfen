## 1. 工程初始化

- [x] 1.1 在项目根目录创建 `frontend/` Vue 3 + Vite 工程（包名 `yuanfen-web`，使用 `npm create vite`）
- [x] 1.2 安装依赖：vue-router@4、pinia、axios、element-plus
- [x] 1.3 配置 `vite.config.js`：设置 `server.proxy` 将 `/api` 代理到 `http://localhost:8080`
- [x] 1.4 在 `main.js` 中注册 Element Plus、Vue Router、Pinia

## 2. 网络层

- [x] 2.1 创建 `src/api/http.js`：Axios 实例，baseURL=/api，请求拦截注入 Bearer Token，响应拦截处理 401 跳登录
- [x] 2.2 创建 `src/api/auth.js`：封装 `registerByUsername`、`login`、`logout`、`refresh` 函数
- [x] 2.3 创建 `src/api/user.js`：封装 `getProfile`、`updateProfile`、`uploadAvatar` 函数

## 3. 状态管理

- [x] 3.1 创建 `src/stores/auth.js`（Pinia store）：存储 accessToken / refreshToken / user，持久化到 localStorage
- [x] 3.2 在 store 中实现 `login`、`logout`、`setTokens` actions

## 4. 路由配置

- [x] 4.1 创建 `src/router/index.js`：配置 `/login`、`/register`、`/profile` 路由
- [x] 4.2 添加全局路由守卫：未登录访问 `/profile` 跳转 `/login`；已登录访问 `/login` 跳转 `/profile`

## 5. 注册页面

- [x] 5.1 创建 `src/views/RegisterView.vue`：用户名、密码表单，Element Plus 表单校验（用户名4-32位字母数字，密码6-64位）
- [x] 5.2 调用 `auth.registerByUsername`，成功后保存 Token 并跳转 `/profile`，失败显示错误提示

## 6. 登录页面

- [x] 6.1 创建 `src/views/LoginView.vue`：用户名、密码表单
- [x] 6.2 调用 `auth.login`（loginType=USERNAME_PASSWORD），成功后保存 Token 跳转 `/profile`，失败显示错误提示

## 7. 个人资料页面

- [x] 7.1 创建 `src/views/ProfileView.vue`：进入时调用 `getProfile`，展示昵称、性别、出生年月、城市、地址
- [x] 7.2 性别字段使用 `el-select`（未知/男/女/其他，值 0/1/2/3）
- [x] 7.3 出生年月使用 `el-date-picker`（type=month，格式化为 YYYY-MM）
- [x] 7.4 点击保存调用 `updateProfile`，成功后提示"保存成功"并刷新数据

## 8. 头像上传

- [x] 8.1 在 ProfileView 中添加头像展示区域（圆形，无头像时显示占位图）
- [x] 8.2 使用 `el-upload` 实现头像选取，`before-upload` 钩子校验文件类型（JPEG/PNG/GIF/WebP）和大小（≤5MB）
- [x] 8.3 调用 `uploadAvatar`，成功后用返回的 avatarUrl 更新头像展示

## 9. 退出登录

- [x] 9.1 在 ProfileView 添加退出登录按钮，调用 `auth.logout`，清除 store 和 localStorage Token，跳转 `/login`
