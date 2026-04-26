## ADDED Requirements

### Requirement: 用户可上传头像图片
已认证用户 SHALL 能够通过 `POST /api/v1/users/avatar` 以 `multipart/form-data` 格式上传头像图片，系统保存文件并返回可访问的头像 URL。

#### Scenario: 上传合法图片成功
- **WHEN** 已认证用户发送 `POST /api/v1/users/avatar`，请求包含有效的 JPEG/PNG/GIF/WebP 图片文件（≤ 5 MB），字段名为 `file`
- **THEN** 系统返回 HTTP 200，响应体包含 `{ "avatarUrl": "<新头像的完整 HTTP URL>" }`，且该 URL 可直接访问到上传的图片

#### Scenario: 上传后用户资料中头像 URL 已更新
- **WHEN** 用户成功上传头像后调用 `GET /api/v1/users/profile`
- **THEN** 响应中 `avatarUrl` 字段为本次上传返回的新 URL

#### Scenario: 上传文件超过 5 MB
- **WHEN** 已认证用户上传大小超过 5 MB 的文件
- **THEN** 系统返回 HTTP 400，响应体包含错误信息提示文件过大

#### Scenario: 上传不支持的文件类型
- **WHEN** 已认证用户上传非 JPEG/PNG/GIF/WebP 格式的文件（如 PDF、BMP）
- **THEN** 系统返回 HTTP 400，响应体包含错误信息提示文件类型不支持

#### Scenario: 未认证用户上传头像
- **WHEN** 未携带有效 JWT 的请求访问 `POST /api/v1/users/avatar`
- **THEN** 系统返回 HTTP 401 Unauthorized
