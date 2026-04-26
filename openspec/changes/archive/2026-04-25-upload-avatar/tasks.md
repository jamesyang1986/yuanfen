## 1. 配置与基础设施

- [x] 1.1 在 `application.yml` 中添加 `app.upload.dir`（默认 `uploads/avatars`）和 `app.base-url` 配置项
- [x] 1.2 创建 `WebMvcConfig`，通过 `addResourceHandlers` 将 `uploads/` 目录挂载为静态资源路径 `/uploads/**`

## 2. 文件存储服务

- [x] 2.1 创建 `FileStorageService`，实现将 `MultipartFile` 写入 `<upload-dir>/<userId>/<uuid>.<ext>` 并返回访问 URL
- [x] 2.2 在 `FileStorageService` 中校验文件类型（MIME type 限 image/jpeg、image/png、image/gif、image/webp）
- [x] 2.3 在 `FileStorageService` 中校验文件大小（≤ 5 MB），超出时抛出业务异常

## 3. 业务逻辑

- [x] 3.1 在 `UserService` 中新增 `uploadAvatar(Long userId, MultipartFile file)` 方法，调用 `FileStorageService` 存储文件并更新 `users.avatar_url`
- [x] 3.2 在 `UserMapper` / `UserMapper.xml` 中新增 `updateAvatarUrl(Long userId, String avatarUrl)` SQL

## 4. 接口层

- [x] 4.1 在 `UserProfileController` 中新增 `POST /avatar` 端点，参数为 `@RequestParam MultipartFile file` 和 `@AuthenticationPrincipal Long userId`
- [x] 4.2 返回包含 `avatarUrl` 的 `ApiResponse`（可复用现有 `ApiResponse` 包装）

## 5. 错误处理

- [x] 5.1 在全局异常处理器中处理 `MaxUploadSizeExceededException`，返回 HTTP 400 及友好提示
- [x] 5.2 将文件类型/大小校验异常映射到 HTTP 400

## 6. 测试

- [x] 6.1 编写 `FileStorageService` 单元测试：合法文件存储成功、文件过大报错、类型不支持报错
- [x] 6.2 编写 `UserProfileController` 集成测试：上传成功返回 URL、未认证返回 401
