### Requirement: 用户可上传头像图片
已认证用户 SHALL 能够通过 `POST /api/users/avatar` 上传头像图片文件，系统存储图片后返回可访问的头像 URL。

#### Scenario: 用户成功上传头像图片
- **WHEN** 已认证用户发送 `POST /api/users/avatar`，请求体为 `multipart/form-data`，包含有效的图片文件（如 JPEG 或 PNG）
- **THEN** 系统返回 HTTP 200，响应体包含 `avatarUrl` 字段，值为图片的可访问 URL

#### Scenario: 用户上传非图片文件
- **WHEN** 已认证用户发送 `POST /api/users/avatar`，上传的文件类型不在允许范围内（非 JPEG / PNG / GIF / WEBP）
- **THEN** 系统返回 HTTP 400，响应体包含文件类型不合法的错误信息

#### Scenario: 用户上传超过大小限制的图片
- **WHEN** 已认证用户发送 `POST /api/users/avatar`，图片文件大小超过系统限制（如 5 MB）
- **THEN** 系统返回 HTTP 400，响应体包含文件过大的错误信息

#### Scenario: 请求未包含文件
- **WHEN** 已认证用户发送 `POST /api/users/avatar`，请求体中未包含任何文件
- **THEN** 系统返回 HTTP 400，响应体包含缺少文件的错误信息

#### Scenario: 未登录用户尝试上传头像
- **WHEN** 未携带有效 JWT 的请求访问 `POST /api/users/avatar`
- **THEN** 系统返回 HTTP 401 Unauthorized
