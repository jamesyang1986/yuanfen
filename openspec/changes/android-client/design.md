## Context

后端已提供完整的 REST API（Spring Boot 2.7，JWT 认证），Swagger 文档在 `http://localhost:8080/docs`。API 端点：
- 认证：`POST /api/v1/auth/register/username`、`POST /api/v1/auth/login`、`POST /api/v1/auth/refresh`、`POST /api/v1/auth/logout`
- 资料：`GET/PUT /api/v1/users/profile`
- 头像：`POST /api/v1/users/avatar`（multipart/form-data）

Android 工程独立于后端，存放在 `android/` 目录，原生 Java，不引入 Kotlin 或 Compose。

## Goals / Non-Goals

**Goals:**
- 单模块 Android 工程，minSdk 24，targetSdk 34
- Retrofit2 + OkHttp3 网络层，Gson 序列化
- SharedPreferences 持久化 accessToken / refreshToken
- 4 个功能页面：注册、登录、个人资料、头像上传
- Glide + CircleImageView 展示头像
- 自动注入 Bearer Token 的 OkHttp Interceptor

**Non-Goals:**
- 不实现手机/邮箱注册（仅用户名注册）
- 不实现 Token 自动刷新（401 时跳回登录页）
- 不实现下拉刷新、分页、推送通知
- 不做 UI 精细设计，使用 Material Components 基础组件

## Decisions

### 1. Retrofit2 + OkHttp3，不用 Volley
**决定**：Retrofit2 声明式接口 + OkHttp3 拦截器。  
**理由**：类型安全、代码量少、拦截器统一处理 Token；Volley 封装粗糙，不适合 JWT 认证场景。

### 2. SharedPreferences 存储 Token（不用加密存储）
**决定**：原型阶段用明文 SharedPreferences，key 为 `access_token` / `refresh_token`。  
**理由**：EncryptedSharedPreferences 需要 AndroidX Security，增加复杂度；原型优先跑通流程。  
**后续**：生产阶段替换为 EncryptedSharedPreferences。

### 3. Activity-based 导航（不用 Fragment/Navigation Component）
**决定**：每个页面一个 Activity，Intent 跳转。  
**理由**：结构最简单，无需理解 Navigation Graph，适合快速原型。

### 4. 工程目录 `android/` 独立于后端
**决定**：在项目根目录下新建 `android/` 作为 Android 工程根，与后端 `src/` 平级。  
**理由**：前后端分离，Android Studio 可单独打开。

## Risks / Trade-offs

- **本地调试网络**：Android 模拟器访问宿主机需用 `10.0.2.2`，真机需配置 IP → 通过 `NetworkConfig.java` 统一管理 BASE_URL
- **HTTP 明文**：Android 9+ 默认禁止 HTTP → 在 `network_security_config.xml` 中声明 `localhost` / `10.0.2.2` 为可信域
- **图片选取权限**：Android 13+ 使用 `READ_MEDIA_IMAGES`，低版本用 `READ_EXTERNAL_STORAGE` → 分版本声明权限
