## 1. 后端 — 数据层

- [x] 1.1 在 `UserMapper.java` 新增 `listUsers(int offset, int size)` 方法，返回 `List<User>`，按 `last_login_at` DESC
- [x] 1.2 在 `UserMapper.java` 新增 `countUsers()` 方法，返回用户总数
- [x] 1.3 在 `UserMapper.java` 新增 `findById(Long id)` 方法（若已有则复用）
- [x] 1.4 在 `UserMapper.xml` 添加对应 SQL：listUsers（LIMIT/OFFSET 分页）、countUsers、findById

## 2. 后端 — DTO 与 Service

- [x] 2.1 创建 `dto/UserPublicDTO.java`（字段：id、nickname、avatarUrl、city、birthDate）
- [x] 2.2 创建 `dto/PageResult.java`（泛型：total、page、size、items）
- [x] 2.3 在 `UserService.java` 新增 `listUsers(int page, int size)` 方法，返回 `PageResult<UserPublicDTO>`
- [x] 2.4 在 `UserService.java` 新增 `getUserById(Long id)` 方法，返回 `UserPublicDTO`，不存在时抛 404 异常

## 3. 后端 — Controller

- [x] 3.1 创建 `controller/UserSquareController.java`，添加 `GET /api/v1/users` 接口（参数：page=0, size=20）
- [x] 3.2 在同一 Controller 添加 `GET /api/v1/users/{id}` 接口
- [x] 3.3 在 `SecurityConfig` 确认以上两个路径需要认证（默认已受保护，检查确认即可）

## 4. 前端 — API 层

- [x] 4.1 在 `src/api/user.js` 新增 `listUsers(page, size)` 函数，调用 `GET /api/v1/users`
- [x] 4.2 在 `src/api/user.js` 新增 `getUserById(id)` 函数，调用 `GET /api/v1/users/{id}`

## 5. 前端 — 导航栏

- [x] 5.1 创建 `src/components/AppNav.vue`，包含"广场"（`/square`）和"我的资料"（`/profile`）两个导航链接，已登录时显示
- [x] 5.2 在 `App.vue` 引入并渲染 `AppNav`

## 6. 前端 — 路由

- [x] 6.1 在 `src/router/index.js` 新增 `/square` 路由（`SquareView`，requiresAuth: true）
- [x] 6.2 在 `src/router/index.js` 新增 `/user/:id` 路由（`UserDetailView`，requiresAuth: true）

## 7. 前端 — 用户广场页

- [x] 7.1 创建 `src/views/SquareView.vue`，进入时调用 `listUsers(0, 20)` 加载首页数据
- [x] 7.2 以卡片网格（`el-row` + `el-col`）展示用户列表，每张卡片含圆形头像、昵称、城市、年龄
- [x] 7.3 年龄计算：用当前年份减去 birthDate 年份；birthDate 为空时显示"未知"；城市为空时显示"未知"
- [x] 7.4 卡片可点击，跳转到 `/user/:id`
- [x] 7.5 添加 `el-pagination` 分页组件，切换页码时重新请求数据

## 8. 前端 — 用户详情页

- [x] 8.1 创建 `src/views/UserDetailView.vue`，进入时读取路由参数 `id`，调用 `getUserById(id)`
- [x] 8.2 展示头像（圆形）、昵称、城市、年龄
- [x] 8.3 API 返回 404 时显示"用户不存在"提示
- [x] 8.4 添加返回按钮，点击后返回 `/square`
