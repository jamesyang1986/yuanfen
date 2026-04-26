## MODIFIED Requirements

### Requirement: 用户可更新自己的个人资料
已认证用户 SHALL 能够通过 `PUT /api/v1/users/profile` 更新自己的个人资料字段，所有字段均为可选。`avatarUrl` 字段可直接传入 URL 字符串，也可通过 `POST /api/v1/users/avatar` 文件上传接口更新。

#### Scenario: 用户更新昵称和头像 URL
- **WHEN** 已认证用户发送 `PUT /api/v1/users/profile`，请求体包含 `{"nickname": "小明", "avatarUrl": "https://example.com/avatar.jpg"}`
- **THEN** 系统返回 HTTP 200，响应体中 `nickname` 为 "小明"，`avatarUrl` 为提交的 URL，其他字段保持不变

#### Scenario: 用户更新性别字段
- **WHEN** 已认证用户发送 `PUT /api/v1/users/profile`，请求体包含 `{"gender": 1}`（1=男）
- **THEN** 系统返回 HTTP 200，响应体中 `gender` 为 1

#### Scenario: 用户提交无效的出生日期格式
- **WHEN** 已认证用户发送 `PUT /api/v1/users/profile`，请求体包含 `{"birthDate": "not-a-date"}`
- **THEN** 系统返回 HTTP 400，响应体包含字段校验错误信息

#### Scenario: 用户提交超长昵称
- **WHEN** 已认证用户发送 `PUT /api/v1/users/profile`，请求体中 `nickname` 长度超过 32 个字符
- **THEN** 系统返回 HTTP 400，响应体包含 `nickname` 字段校验错误

#### Scenario: 未登录用户尝试更新个人资料
- **WHEN** 未携带有效 JWT 的请求访问 `PUT /api/v1/users/profile`
- **THEN** 系统返回 HTTP 401 Unauthorized
