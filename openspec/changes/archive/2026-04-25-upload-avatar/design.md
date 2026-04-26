## Context

项目已实现用户个人资料接口（`GET/PUT /api/v1/users/profile`），其中 `avatarUrl` 字段目前通过 `PUT /profile` 由客户端直接传入 URL 字符串。项目使用 Spring Boot + MyBatis，JWT 做身份认证，尚未引入对象存储服务（OSS）。

## Goals / Non-Goals

**Goals:**
- 提供 `POST /api/v1/users/avatar` 接口，接收 `multipart/form-data` 图片文件
- 校验文件类型（JPEG/PNG/GIF/WebP）及大小（≤ 5 MB）
- 将文件写入本地磁盘 `uploads/avatars/<userId>/` 目录，以 UUID 命名防冲突
- 通过 Spring MVC 静态资源映射将目录对外暴露，生成可访问的 HTTP URL
- 上传成功后自动将 URL 写入 `users.avatar_url` 字段并返回

**Non-Goals:**
- 不集成云对象存储（OSS/S3）——本期使用本地磁盘，后续可替换
- 不做图片压缩、缩放或格式转换
- 不删除旧头像文件（旧文件保留，仅覆盖数据库记录）

## Decisions

### 1. 本地磁盘存储，不引入 OSS
**决定**：使用 `java.nio.file` 写入本地目录，通过 `WebMvcConfigurer.addResourceHandlers` 挂载静态路径。  
**理由**：项目当前规模小，引入 OSS 会增加配置复杂度和外部依赖。本地存储接口与 OSS 的差异只在 `FileStorageService` 实现层，后续替换成本低。  
**备选**：阿里云 OSS / AWS S3——留待流量增长后迁移。

### 2. 文件路径：`uploads/avatars/<userId>/<uuid>.<ext>`
**决定**：按 userId 分目录，文件名使用 UUID 避免冲突。  
**理由**：同一用户可能多次上传，UUID 命名保证旧文件不被覆盖（避免缓存问题），目录按用户隔离便于管理。

### 3. 接口路径 `POST /api/v1/users/avatar`（独立于 `/profile`）
**决定**：单独暴露上传接口，而非在 `PUT /profile` 中支持文件上传。  
**理由**：文件上传与字段更新是不同操作语义（`multipart` vs `application/json`），分离后职责清晰，`PUT /profile` 保持 JSON 接口不变。

## Risks / Trade-offs

- **磁盘空间**：旧头像不删除，长期运行会积累文件 → 后续可加定时清理任务
- **单机限制**：本地存储无法在多实例部署中共享 → 多实例时需替换为共享存储或 OSS
- **URL 变更**：若服务域名或端口变化，已存储的 `avatarUrl` 需批量更新 → 可通过配置 `app.base-url` 统一管理前缀
