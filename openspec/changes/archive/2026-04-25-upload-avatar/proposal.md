## Why

用户目前只能通过 `PUT /api/users/profile` 手动填写 `avatarUrl` 字符串来设置头像，无法直接上传图片文件。引入头像上传接口，让用户能够通过文件上传完成头像设置，提升使用体验。

## What Changes

- 新增 `POST /api/users/avatar` 接口，接收 `multipart/form-data` 格式的图片文件
- 服务端校验文件类型（仅允许 JPEG/PNG/GIF/WebP）及文件大小（上限 5 MB）
- 上传成功后将文件持久化到本地存储，并将生成的访问 URL 自动写入当前用户的 `avatarUrl` 字段
- 返回新的头像访问 URL

## Capabilities

### New Capabilities
- `avatar-upload`: 用户头像文件上传接口，包含文件校验、存储、URL 生成及用户资料更新

### Modified Capabilities
- `user-profile`: 新增通过文件上传方式更新 `avatarUrl` 的场景

## Impact

- **新增接口**：`POST /api/users/avatar`（需 JWT 认证）
- **新增文件存储**：本地磁盘目录（如 `uploads/avatars/`），通过静态资源路由对外暴露
- **修改**：`UserService`、`UserController` 新增头像上传方法
- **依赖**：Spring Web `MultipartFile`；无需引入新第三方库
